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
import fr.cyann.functional.Function1;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The ReactManager class.
 * Creation date: 20 oct. 2013.
 * @author Yann Caron
 * @version v0.1
 */
public class ReactManager {

	// attribute
	private static ReactManager singleton;
	private final AtomicInteger value;
	private final Var<Integer> counter;

	// constructor
	// hide constructor
	private ReactManager() {
		value = new AtomicInteger();
		counter = new Var<Integer>(new Integer(0), false);
	}

	/**
	Get singleton instance.
	@return the only one object instance.
	 */
	public static ReactManager getInstance() {
		if (singleton == null) {
			singleton = new ReactManager();
		}
		return singleton;
	}

	// methods
	/**
	Called when a react is created.
	 */
	@Package
	void incrementCounter() {
		int v = value.incrementAndGet();
		counter.emit(v);
	}

	/**
	Called when it is disposed.
	 */
	@Package
	void decrementCounter() {
		int v = value.decrementAndGet();
		counter.emit(v);
	}

	public Var<Integer> getReactCounter() {
		return counter;
	}
}
