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
import fr.cyann.functional.Predicate1;
import fr.cyann.functional.Predicate2;
import fr.cyann.functional.Tuple;

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

	// simplify casting
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Var<V> subscribe(final Signal<V> signal) {
		super.subscribe(signal);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Signal<V> unSubscribe(Procedure1<V> subscriber) {
		super.unSubscribe(subscriber);
		return this;
	}

	// <editor-fold defaultstate="collapsed" desc="high order functions">
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
		return fold(getValue(), function);
	}
	
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="signal operations">
	/**
	@see Signal#merge(java.lang.Object, java.lang.Object, fr.cyann.react.Signal, fr.cyann.functional.Function2) 
	 */
	public <W> Var<Tuple<V, W>> merge(final Var<W> merge) {
		return merge(merge, new TupleFold<V, W>());
	}

	/**
	 * @see Signal#merge(java.lang.Object, java.lang.Object, fr.cyann.react.Var, fr.cyann.functional.Function2) 
	 */
	public <X, W> Var<X> merge(final Var<W> right, final Function2<X, V, W> mapfold) {
		return super.merge(value, right.getValue(), right, mapfold);
	}

	/**
	@see Signal#sync(java.lang.Object, java.lang.Object, fr.cyann.react.Signal, fr.cyann.functional.Function2) 
	 */
	public <W> Var<Tuple<V, W>> sync(final Var<W> merge) {
		return sync(merge, new TupleFold<V, W>());
	}

	/**
	 * @see Signal#sync(java.lang.Object, java.lang.Object, fr.cyann.react.Signal, fr.cyann.functional.Function2) 
	 */
	public final <X, W> Var<X> sync(final Var<W> right, final Function2<X, V, W> mapfold) {
		return super.sync(getValue(), right.getValue(), right, mapfold);
	}

	/**
	@see Signal#then(java.lang.Object, java.lang.Object, fr.cyann.react.Signal, fr.cyann.functional.Function2) 
	 */
	public <W> Var<Tuple<V, W>> then(final Var<W> merge) {
		return then(merge, new TupleFold<V, W>());
	}

	/**
	 * @see Signal#then(java.lang.Object, java.lang.Object, fr.cyann.react.Signal, fr.cyann.functional.Function2) 
	 */
	public <W, X> Var<X> then(final Var<W> right, final Function2<X, V, W> mapfold) {
		return super.then(value, right.getValue(), right, mapfold);
	}

		/**
	@see Signal#when(java.lang.Object, java.lang.Object, fr.cyann.react.Signal, fr.cyann.functional.Function2) 
	 */
	public <W> Var<Tuple<V, W>> when(final Var<W> right) {
		return when(right, new TupleFold<V, W>());
	}

	/**
	@see Signal#when(java.lang.Object, java.lang.Object, fr.cyann.react.Signal, fr.cyann.functional.Function2) 
	 */
	public <W, X> Var<X> when(final Var<W> when, final Function2<X, V, W> mapfold) {
		return super.when(getValue(), when.getValue(), when, mapfold);
	}

	// </editor-fold>

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <W> Signal<V> disposeWhen(Signal<W> signal) {
		super.disposeWhen(signal);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Signal<V> disposeWhen(final Predicate1<V> predicate) {
		super.disposeWhen(predicate);
		return this;
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
