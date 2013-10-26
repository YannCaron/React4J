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

import java.util.ArrayList;
import java.util.List;

/**
 * The TestTools class.
 * Creation date: 19 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public final class TestTools {

	private TestTools() {
	}

	public final static List<Object> results = new ArrayList<Object>();
	
	public final static void initResults () {
		results.clear();
	}
		
	public final static void assertWithTolerence(long time1, long time2, long tolerance) {
		if (Math.abs(time1 - time2) > tolerance) {
			String msg = "Assertion error, range expected [" + (time1 - tolerance) + "-" + (time1 + tolerance) + "], found " + time2;
			throw new AssertionError(msg);
		}
	}
	
}
