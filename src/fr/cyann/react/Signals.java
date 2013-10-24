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

import fr.cyann.functional.Function2;

/**
 * The Signals class.
 * Creation date: 24 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class Signals {

	private Signals() {
	} // static class

	public static final Var<Integer> newRange(final int from, final int to, final int step, final Signal every) {
		return every.fold(from - step, new Function2<Integer, Integer, Integer>() {

			@Override
			public Integer invoke(Integer arg1, Integer arg2) {
				if ((arg1 >= to && step > 0) || (arg1 <= to && step < 0)) {
					return from;
				} else {
					return arg1 + step;
				}
			}
		});
	}

	public static final Var<Integer> newCounter(final Signal every) {
		return every.fold(0, new Signal.CountFold<Integer>());
	}
}
