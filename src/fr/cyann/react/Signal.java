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

import fr.cyann.functor.Function1;
import fr.cyann.functor.Predicate1;
import fr.cyann.functor.Procedure1;

/**
 * The Signal class. Define the base class of all discrete and continous react.
 * Creation date: 11 oct. 2013.
 * @author Yann Caron
 * @version v0.1
 */
public abstract class Signal<V> {

	protected final React<V> react;

	public abstract V getValue();

	/**
	Command react to emit a signal.<br>
	Should be overrided to add automatic behaviours like emit counter time management etc.
	 */
	public void emit() {
		react.emit(getValue());
	}

	/**
	Default constructor.
	 */
	public Signal() {
		react = new React<V>();
	}

	/**
	Register listener first order function to react.
	Function will be called when event is raised.
	@param subscriber function to be called in case of event.
	@return return this.
	 */
	public Signal<V> subscribe(Procedure1<V> subscriber) {
		react.subscribe(subscriber);
		return this;
	}

	/**
	Filter the event according a criteria.
	@param function predicate to filter. If result is true, event pass else it is blocked.
	@return the new filtered signal.
	 */
	public final Signal<V> filter(final Predicate1<V> function) {

		final Signal<V> signal = new Signal<V>() {

			@Override
			public V getValue() {
				return Signal.this.getValue();
			}
		};

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				if (function.invoke(value)) {
					signal.emit();
				}
			}
		});
		return signal;
	}

	/**
	Modify the react data in value and type.
	@param <R> The new react data type.
	@param function the function to transform data.
	@return the new transformed react.
	 */
	public final <R> Signal<R> map(final Function1<R, V> function) {

		final Signal<R> signal = new Signal<R>() {

			@Override
			public R getValue() {
				return function.invoke(Signal.this.getValue());
			}
		};

		this.react.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emit();
			}
		});
		return signal;
	}

	public final <W> Signal<V> mergeLeft(final Signal<W> merge) {

		final Var<V> signal = new Var<V>(getValue());

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.setValue(value);
			}
		});

		merge.subscribe(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				Signal.this.emit();
			}
		});

		return signal;
	}

	public final <W> Signal<V> mergeLeft(final Signal<W> merge, final Function1<V, W> converter) {

		final Var<V> signal = new Var<V>(getValue());

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.setValue(value);
			}
		});

		merge.subscribe(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				signal.setValue(converter.invoke(value));
			}
		});

		return signal;
	}

	public final <W> Signal<W> mergeRight(final Signal<W> merge) {

		final Var<W> signal = new Var<W>(merge.getValue());

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				merge.emit();
			}
		});

		merge.subscribe(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				signal.setValue(value);
			}
		});

		return signal;
	}

	public final <W> Signal<W> mergeRight(final Signal<W> merge, final Function1<W, V> converter) {

		final Var<W> signal = new Var<W>(merge.getValue());

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.setValue(converter.invoke(value));
			}
		});

		merge.subscribe(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				signal.setValue(value);
			}
		});

		return signal;
	}
}
