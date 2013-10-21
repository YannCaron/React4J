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
 * The Function1 interface. Give the possibility to create a functor with one parameter and a return parameter.
 * Creation date: 9 oct. 2013.
 * @author Yann Caron
 * @version v0.1
 */
public interface Function2<R, A1, A2> {

	public R invoke(A1 arg1, A2 arg2);
}
