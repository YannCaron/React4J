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

/**
 * The Constant class.
 * Creation date: 27 oct. 2013.
 * @author Yann Caron 
 * @version v0.1
 */
public class Constant<V> extends Var<V> {

	public final Var<V> parentVar;

	public Constant(V value, Var<V> parent) {
		super(value, parent);
		this.parentVar = parent;
	}

	/**
	Return to variable e.g. varying signal.
	@return the varying signal.
	 */
	public Var<V> toVar() {
		return parentVar;
	}

	@Override
	public synchronized void setValue(V value) {
		// avoid set value
	}

	/**
	Gets a textual representation of the object.
	@return the textual representation.
	 */
	@Override
	public String toString() {
		return "Constant{" + "value=" + this.value + '}';
	}
}
