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
 * The Class class.
 * Creation date: 17 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class Tuple<V, W> {
	private V first;
	private W second;

	public V getFirst() {
		return first;
	}

	public W getSecond() {
		return second;
	}

	public void setFirst(V first) {
		this.first = first;
	}

	public void setSecond(W second) {
		this.second = second;
	}

	public Tuple(V first, W second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public String toString() {
		return "Tuple{" + "first=" + first + ", second=" + second + '}';
	}
	
}
