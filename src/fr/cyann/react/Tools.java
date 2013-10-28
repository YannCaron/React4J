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

import fr.cyann.base.Package;
import fr.cyann.functional.Procedure1;

/**
 * The Tools class.
 * Creation date: 26 oct. 2013.
 * @author Yann Caron 
 * @version v0.1
 */
public class Tools {

	private Tools() {
	} // static class

	@Package
	static abstract class CombineProcedure<V, W> implements Procedure1<V> {

		protected CombineProcedure<W, V> with;
		protected boolean invoked = false;
		;
		protected V currentValue;

		public boolean isInvoked() {
			return invoked;
		}

		public void reset() {
			invoked = false;
		}

		public void associate(CombineProcedure<W, V> with) {
			this.with = with;
		}

		public V getCurrentValue() {
			if (currentValue == null) { // lazy initialization
				currentValue = initialize();
			}
			return currentValue;
		}

		public abstract V initialize();

		public abstract void invokeF(V value, W other);
	}

	/**
	Merge two callback together.
	@param <V> the callback type.
	@param <W> the syncwith callback type.
	 */
	@Package
	static abstract class MergeProcedure1<V, W> extends CombineProcedure<V, W> {

		@Override
		public synchronized void invoke(V value) {
			currentValue = value;
			invokeF(value, with.getCurrentValue());
			invoked = true;
		}
	}

	/**
	Synchronize two callback together.
	@param <V> the callback type.
	@param <W> the syncwith callback type.
	 */
	@Package
	static abstract class SyncProcedure1<V, W> extends CombineProcedure<V, W> {

		@Override
		public synchronized void invoke(V value) {
			currentValue = value;

			if (with.isInvoked()) {
				invokeF(value, with.getCurrentValue());
				invoked = false;
				with.reset();
			} else {
				invoked = true;
			}
		}
	}

	/**
	Synchronize two callback together. This one wait after the other.
	@param <V> the callback type.
	@param <W> the syncwith callback type.
	 */
	@Package
	static abstract class BlockProcedure1<V, W> extends CombineProcedure<V, W> {

		@Override
		public synchronized void invoke(V value) {
			currentValue = value;

			if (with.isInvoked() && invoked) {
				invoked = false;
			} else {
				invoked = true;
			}
			if (with.isInvoked()) {
				with.reset();
			}
		}
	}

	@Package
	static abstract class SwitchProcedure<V> implements Procedure1 {

		private V value;
		private final Signal<V> signal;
		private Signal right;

		public void setValue(V value) {
			this.value = value;
		}

		public void setRight(Signal right) {
			this.right = right;
		}
		
		public SwitchProcedure(V value, Signal<V> signal, Signal right) {
			this.value = value;
			this.signal = signal;
			this.right = right;
		}
		
		@Override
		public void invoke(Object arg1) {
			invokeF(signal, right, value);
		}
		
		public abstract void invokeF(Signal<V> signal, Signal right, V value);
	}
}
