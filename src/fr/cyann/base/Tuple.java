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
package fr.cyann.base;

/**
 * The Class class.<br>
 * An parametric entity composed of two values.
 * Creation date: 17 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class Tuple<V, W> {
	private V first;
	private W second;

	/**
	Get the first value of the tuple.
	@return the first value.
	*/
	public V getFirst() {
		return first;
	}

	/**
	Get the second value of the tuple.
	@return the second value.
	*/
	public W getSecond() {
		return second;
	}

	/**
	Set the first value of the tuple.
	@param first the value to set.
	*/
	public void setFirst(V first) {
		this.first = first;
	}

	/**
	Set the second value of the tuple.
	@param second the second value to set.
	*/
	public void setSecond(W second) {
		this.second = second;
	}

	/**
	Default constructor.
	@param first the value of the first value of the tuple.
	@param second the value of the second value of the tuple.
	*/
	public Tuple(V first, W second) {
		this.first = first;
		this.second = second;
	}

	/**
	Gets a textual representation of the object.
	@return the textual representation.
	*/
	@Override
	public String toString() {
		return "Tuple{" + "first=" + first + ", second=" + second + '}';
	}
	
}
