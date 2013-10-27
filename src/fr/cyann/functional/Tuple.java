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
package fr.cyann.functional;

/**
 * The Tuple class.<br>Represents a couple of associative values.<br>
 * Creation date: 26 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class Tuple<V, W> {

	/**
	The left value.
	*/
	public final V left;
	/**
	The right value.
	*/
	public final W right;

	/**
	Constructor that set values.
	@param left the left one.
	@param right the right one.
	*/
	public Tuple(V left, W right) {
		this.left = left;
		this.right = right;
	}
	
}
