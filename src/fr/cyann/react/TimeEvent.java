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
import java.util.Date;

/**
 * The TimeEvent class.
 * Creation date: 12 oct. 2013.
 * @author Yann Caron 
 * @version v0.1
 */
public class TimeEvent implements Event {

	private int iteration;
	private final long startTime;
	private long fromStartTime;
	private long lastTime;
	private long fromLastTime;
	private Date date;

	public TimeEvent() {
		iteration = 0;
		long time = System.currentTimeMillis();
		startTime = time;
		lastTime = time;
		date = new Date();
	}

	@Package
	void increment() {
		long time = System.currentTimeMillis();
		fromLastTime = time - lastTime;
		lastTime = time;
		fromStartTime = time - startTime;
		iteration++;
	}

	public int getIteration() {
		return iteration;
	}

	public long getCurrentTimeElapsedFromStart() {
		return System.currentTimeMillis() - startTime;
	}

	public long getTimeElapsedFromStart() {
		return fromStartTime;
	}

	public long getCurrentTimeElapsed() {
		return System.currentTimeMillis() - lastTime;
	}

	public long getTimeElapsed() {
		return fromLastTime;
	}

	public Date getDate() {
		date.setTime(System.currentTimeMillis());
		return date;
	}

	@Override
	public String toString() {
		return "TimeEvent{" + "iteration=" + iteration + ", fromStartTime=" + fromStartTime + ", fromLastTime=" + fromLastTime + '}';
	}
}
