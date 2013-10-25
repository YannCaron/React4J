/*
 * Copyright (C) 2013 Yann Caron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Less General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Less General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package fr.cyann.react;

import fr.cyann.functional.Procedure1;
import fr.cyann.base.Package;
import fr.cyann.functional.Function2;
import fr.cyann.functional.Predicate2;

/**
 * Var class. Describe a variable that changes in time.
 * @author caronyn
 */
public class Var<V> extends Signal<V> {

	// attribute
	/**
	Value decorated by this generic type.
	 */
	protected V value;

	// constructor
	/**
	Default constructor.
	@param value the initial value.
	 */
	public Var(V value) {
		super();
		this.value = value;
	}

	/**
	Constructor for ReactManager only (avoid stack overflow with the counter react).
	@param value the initial value.
	@param count is ReactManaget count this.
	 */
	@Package
	Var(V value, boolean count) {
		super(count);
		this.value = value;
	}

	/**
	Var constructor for chained signals.
	@param value the initial value.
	@param parent chain with the parent.
	 */
	public Var(V value, Signal parent) {
		super(parent);
		this.value = value;
	}

	/**
	Emit a signal
	@param value the signal value.
	 */
	@Override
	public void emit(V value) {
		this.value = value;
		super.emit(value);
	}

	/**
	Emit a finish signal.
	@param value the signal value.
	 */
	@Override
	public void emitFinish(V value) {
		this.value = value;
		super.emitFinish(value);
	}

	// method
	public V getValue() {
		return value;
	}

	/**
	Value mutator.
	@param value the value to set.
	 */
	public synchronized void setValue(V value) {
		this.value = value;
		super.emit(value);
	}
	/*
	@Override
	public final <R> Var<R> map(final Function1<R, V> function) {
	return super.map(function).toVar(function.invoke(getValue()));
	}*/

	/**
	Merge two signal together. If any signal emit, the resulting signal will
	emit. Consider this operation like an <b>and</b> boolean operation.
	
	@param <X> Type of the resulting var.
	@param <W> Type of the merged var.
	@param merge the var to merge with.
	@param mapfold the map fold transformation function.<br>It's goal is to merge the two values together and returned in the desired type.
	@return the new merged signal.
	 */
	public final <X, W> Var<X> merge(final Var<W> merge, final Function2<X, V, W> mapfold) {
		links.add(merge);
		final Var<X> signal = new Var(mapfold.invoke(getValue(), merge.getValue()), this);

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emit(mapfold.invoke(value, merge.getValue()));
			}
		});
		merge.subscribe(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				signal.emit(mapfold.invoke(getValue(), value));
			}
		});
		this.subscribeFinish(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emitFinish(mapfold.invoke(value, merge.getValue()));
			}
		});
		merge.subscribeFinish(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				signal.emit(mapfold.invoke(getValue(), value));
			}
		});

		return signal;
	}

	/**
	 * Filter the event according a criteria on the value and it's previous.<br>
	 * <b>Finish signal</b> is not filtered.
	 *
	 * @param filter predicate to filter. If result is true, event pass else it
	 * is blocked.
	 * @return the new filtered signal.
	 */
	public final Signal<V> filterFold(final Predicate2<V, V> filter) {
		return super.filterFold(getValue(), filter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Signal<V> fold(final Function2<V, V, V> function) {
		return fold(getValue(), function, getValue(), function);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Signal<V> fold(final Function2<V, V, V> fEmit, final Function2<V, V, V> fFinish) {
		return fold(getValue(), fEmit, getValue(), fFinish);
	}

	/**
	Synchrinize two callback together.
	@param <V> the callback type.
	 */
	private static abstract class SyncProcedure1<V> implements Procedure1<V> {

		private SyncProcedure1 with;
		private boolean invoked = false;

		public void setWith(SyncProcedure1 with) {
			this.with = with;
		}

		@Override
		public synchronized void invoke(V value) {
			if (with.invoked) {
				invokeF(value);
				invoked = false;
				with.invoked = false;
			} else {
				this.invoked = true;
			}
		}

		public abstract void invokeF(V value);
	}

	/**
	Synchronize signal together. The both signals should be emited before
	resulting signal will emit.<br>
	Consider this operation like an <b>or</b> boolean operation.
	
	@param <X> Type of the resulting var.
	@param <W> Type of the merged var.
	@param sync the var to merge with.
	@param mapfold the map fold transformation function.<br>It's goal is to merge the two values together and returned in the desired type.
	@return the new merged signal.
	 */
	public final <X, W> Var<X> sync(final Var<W> sync, final Function2<X, V, W> mapfold) {
		links.add(sync);
		final Var<X> signal = new Var(mapfold.invoke(getValue(), sync.getValue()), this);

		SyncProcedure1<V> p1 = new SyncProcedure1<V>() {

			@Override
			public void invokeF(V value) {
				signal.emit(mapfold.invoke(value, sync.getValue()));
			}
		};
		SyncProcedure1<W> p2 = new SyncProcedure1<W>() {

			@Override
			public void invokeF(W value) {
				signal.emit(mapfold.invoke(getValue(), value));
			}
		};
		p1.setWith(p2);
		p2.setWith(p1);

		this.subscribe(p1);
		sync.subscribe(p2);

		SyncProcedure1<V> pf1 = new SyncProcedure1<V>() {

			@Override
			public void invokeF(V value) {
				signal.emitFinish(mapfold.invoke(value, sync.getValue()));
			}
		};
		SyncProcedure1<W> pf2 = new SyncProcedure1<W>() {

			@Override
			public void invokeF(W value) {
				signal.emitFinish(mapfold.invoke(getValue(), value));
			}
		};
		pf1.setWith(pf2);
		pf2.setWith(pf1);

		this.subscribeFinish(pf1);
		sync.subscribeFinish(pf2);

		return signal;
	}

	/**
	Gets a textual representation of the object.
	@return the textual representation.
	 */
	@Override
	public String toString() {
		return "Var{" + "value=" + this.value + '}';
	}
}
