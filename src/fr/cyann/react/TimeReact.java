/*
This file was developed by Yann Caron in october 2013.
This file is part of Java.react.

Java.react is free software: you can redistribute it and/or modify
it under the terms of the GNU Less General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Java.react is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Less General Public License for more details.

You should have received a copy of the GNU General Public License
along with Java.react.  If not, see <http://www.gnu.org/licenses/lgpl.html>.*/

package fr.cyann.react;

import fr.cyann.react.data.TimeEvent;

/**
 * The TimeReact class.
 * Creation date: 12 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class TimeReact extends Signal<TimeEvent> {

	private Thread thread;
	private final TimeEvent event;

	// constructor
	private TimeReact() {
		event = new TimeEvent();
	}

	public static TimeReact once(final long timeout) {
		final TimeReact react = new TimeReact();

		react.thread = new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(timeout);
					react.event.increment();
					react.react.emit(react.event);
				} catch (InterruptedException ex) {
					// do nothing
				}

			}
		};

		react.thread.start();

		return react;
	}

	// attributes
	@Override
	public TimeEvent getValue() {
		return event;
	}
}
