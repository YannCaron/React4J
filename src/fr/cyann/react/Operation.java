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

import fr.cyann.functional.Function;
import fr.cyann.functional.Function2;
import fr.cyann.functional.Procedure1;

/**
 * The SyncOperation class.<br>
 * The event is emited when each elements have been changed.
 *
 * @author caronyn
 */
public class Operation<V> extends Var<V> {

	// attribute
	private final Function<V> operation;

	// constructor
	public Operation(Function<V> operation, Function2<Var, Var, Var> fold, Var... signals) {
		super(operation.invoke());
		this.operation = operation;

		// create sync binary tree
		// list fold operation
		Var sync = null;
		for (Var signal : signals) {
			if (sync == null) {
				sync = signal;
			} else {
				sync = fold.invoke(sync, signal);
			}
		}

		if (sync != null) {
			sync.subscribe(new Procedure1<V>() {
				@Override
				public void invoke(V value) {
					Operation.this.react.emit(Operation.this.getValue());
				}
			});
		}

		links.add(sync);
	}

	public Operation(Function<V> operation, Function<Signal> fold, Signal... signals) {
		super(operation.invoke());
		this.operation = operation;

		fold.invoke().subscribe(new Procedure1<V>() {
			@Override
			public void invoke(V value) {
				Operation.this.react.emit(Operation.this.getValue());
			}
		});

	}

	public static <V> Operation<V> mergeOperation(Function<V> operation, Var... signals) {
		return new Operation<V>(operation, new Function2<Var, Var, Var>() {
			@Override
			public Var invoke(Var signal1, Var signal2) {
				return signal1.merge(signal2, new Signal.KeepLeftFold());
			}
		}, signals);
	}

	public static <V> Operation<V> syncOperation(Function<V> operation, Var... signals) {
		return new Operation<V>(operation, new Function2<Var, Var, Var>() {
			@Override
			public Var invoke(Var signal1, Var signal2) {
				return signal1.sync(signal2, new Signal.KeepLeftFold());
			}
		}, signals);
	}

	// property
	/**
	 * Execute the operation and return the result.
	 *
	 * @return the result of the operation.
	 */
	@Override
	public V getValue() {
		return operation.invoke();
	}
}
