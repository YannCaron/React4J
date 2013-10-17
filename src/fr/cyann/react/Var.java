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

import fr.cyann.functor.Procedure1;

/**
 *
 * @author caronyn
 */
public class Var<V> extends Signal<V> {

	// attribute
	protected V value;

	// constructor
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
	public void emit(V value) {
		super.emit(value);
		this.value = value;
	}

	@Override
	public void emitFinish(V value) {
		super.emitFinish(value);
		this.value = value;
	}
	
	// method
	
	@Override
	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
		react.emit(value);
	}

}
