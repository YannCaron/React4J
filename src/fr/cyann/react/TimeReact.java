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

import fr.cyann.functor.Procedure1;
import java.util.ConcurrentModificationException;

/**
 * The TimeReact class. Creation date: 12 oct. 2013.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class TimeReact extends EventReact<TimeEvent> {

	private Thread thread;
	private Runnable task;

	// constructor
	private TimeReact() {
		super(new TimeEvent());
		super.running = false;
	}

	@Override
	public void start() {
		if (!isRunning()) {
			super.start();
			thread = new Thread(task);
			thread.start();
		}
	}

	@Override
	public void stop() {
		if (isRunning()) {
			super.stop();
			thread.interrupt();
		}
	}

	/**
	 * Factory to create a time based react. Emit a signal only one time after
	 * timeout.
	 *
	 * @param timeout time to wait before emitting message
	 * @return the time react
	 */
	public static TimeReact once(final long timeout) {
		final TimeReact react = new TimeReact();

		react.task = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(timeout);
					react.value.increment();
					react.emit(react.value);
				} catch (InterruptedException ex) {
					// do nothing
				} finally {
					react.stop();
				}

			}
		};

		return react;
	}

	/**
	 * Factory to create a time based react. Emit a signal whenever the time
	 * interval has elapsed.
	 *
	 * @param timeout time to wait before emitting message
	 * @return the time react
	 */
	public static TimeReact every(final long timeout) {
		final TimeReact react = new TimeReact();

		react.task = new Runnable() {
			@Override
			public void run() {
				try {

					while (react.isRunning()) {
						react.value.increment();
						react.emit(react.value);

						Thread.sleep(timeout);
					}
				} catch (InterruptedException ex) {
					// do nothing
				} finally {
					react.stop();
				}

			}
		};

		return react;
	}

	/**
	 * Factory to create a time based react. Emit a signal at regular time
	 * interval (consider the observer function elapsed time).
	 *
	 * @param timeout time to wait before emitting message
	 * @return the time react
	 */
	public static TimeReact framePerSecond(final int fps) {
		final TimeReact react = new TimeReact();
		final long timeout = 1000L / fps;

		react.task = new Runnable() {
			@Override
			public void run() {
				try {

					while (react.isRunning()) {
						long start = System.currentTimeMillis();

						react.value.increment();
						react.emit(react.value);

						long elapsed = System.currentTimeMillis() - start;

						if (timeout > elapsed) {
							Thread.sleep(timeout - elapsed);
						}

					}
				} catch (InterruptedException ex) {
					// do nothing
				} finally {
					react.stop();
				}

			}
		};

		return react;
	}
}
