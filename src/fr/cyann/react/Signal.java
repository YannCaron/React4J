/*
 * Copyright (C) 2013 CyaNn
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

import fr.cyann.functor.Function;
import fr.cyann.functor.Function1;
import fr.cyann.functor.Predicate1;
import fr.cyann.functor.Procedure1;

/**
 * The Signal interface.
 * Creation date: 11 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public abstract class Signal<V> {

	protected final React<V> react;
	public abstract V getValue();

	public Signal() {
		react = new React<V>();
	}

	public Signal<V> subscribe(Procedure1<V> subscriber) {
		react.subscribe(subscriber);
		return this;
	}

	public final Signal<V> filter(final Predicate1<V> function) {

		final Signal<V> result = new Signal<V>() {

			@Override
			public V getValue() {
				return Signal.this.getValue();
			}
		};

		this.react.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				if (function.invoke(value)) {
					result.react.emit(value);
				}
			}
		});
		return result;
	}

	public final <R> VarReact<R> map(final Function1<R, V> function) {
		VarReact<R> result = new VarReact<R>(new Function<R>() {

			@Override
			public R invoke() {
				return function.invoke(Signal.this.getValue());
			}
		}, this);
		return result;
	}
}
