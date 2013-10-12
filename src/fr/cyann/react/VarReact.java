/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.react;

import fr.cyann.functor.Function;
import fr.cyann.functor.Procedure1;

/**
 *
 * @author caronyn
 */
public class VarReact<V> extends Signal<V> {

	private final Function<V> operation;

	public VarReact(Function<V> operation, Signal... signals) {
		this.operation = operation;

		Procedure1<V> subscriber = new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				V v = VarReact.this.operation.invoke();
				react.emit(v);
			}
		};

		for (Signal signal : signals) {
			signal.subscribe(subscriber);
		}
	}

	@Override
	public V getValue() {
		return operation.invoke();
	}
}
