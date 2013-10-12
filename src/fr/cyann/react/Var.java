/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.react;

import fr.cyann.functor.Procedure1;

/**
 *
 * @author caronyn
 */
public class Var<V> extends Signal<V> {

	protected V value;

	public Var(V value) {
		this.value = value;
	}

	public Var(Var<V> value) {
		value.react.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V arg1) {
				react.emit(arg1);
			}
		});

	}

	@Override
	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
		react.emit(value);
	}

}
