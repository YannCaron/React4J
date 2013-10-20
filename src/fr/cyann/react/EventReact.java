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

/**
 * The EventReact abstract class.<br>
 * Represent the super class of all continuous react un the library.<br>
 * Should be realized in case of new continuous react creation.
 * Creation date: 14 oct. 2013.
 * @author CyaNn
 * @version v0.1
 */
public abstract class EventReact<V extends Event> extends Var<V> {

	/**
	Default constructor.
	@param value Initialize the react value.
	*/
	@Package
	EventReact(V value) {
		super(value);
	}

	/**
	Template method.<br>
	Write here the needed behaviour of the react when it is disposed.
	*/
	public abstract void applyDispose();

	/**
	Override the dispose method to add specific template method.
	*/
	@Override
	public void dispose() {
		applyDispose();
		super.dispose();
	}
}
