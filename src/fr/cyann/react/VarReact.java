/*
This file was developed by Yann Caron in october 2013.
This file is part of Java.react.

Java.react is free software: you can redistribute it and/or modify
it under the terms of the GNU Less General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Java.react is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Less General Public License for more details.

You should have received a copy of the GNU General Public License
along with Java.react.  If not, see <http://www.gnu.org/licenses/lgpl.html>.*/

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
