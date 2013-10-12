/*
 * Copyright (C) 2013 CyaNn
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
package fr.cyann.react.data;

/**
 * The TimeEvent class.
 * Creation date: 12 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class TimeEvent implements Event {

	private int iteration;
	private final long startTime;
	private long fromStartTime;
	private long lastTime;

	public TimeEvent() {
		iteration = 0;
		startTime = System.currentTimeMillis();
		lastTime = startTime;
	}

	public void increment() {
		long time = System.currentTimeMillis(); 
		lastTime = time - lastTime;
		fromStartTime = time - startTime;
		iteration++;
	}

	public int getIteration() {
		return iteration;
	}

	public long getTimeElapsedFromStart() {
		return fromStartTime;
	}
	
	public long getTimeElapsed() {
		return lastTime;
	}
	
}
