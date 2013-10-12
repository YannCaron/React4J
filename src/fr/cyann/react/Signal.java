/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
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
