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

import java.util.Random;

/**
 * The TimeReact class. Creation date: 12 oct. 2013.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class TimeReact extends AbstractReact<Integer> {

	private Thread thread;
	private Runnable task;
	private Long lastTime;

	protected int getTimeElapsed() {
		long time = System.currentTimeMillis();
		int result = (int) (time - lastTime);
		lastTime = time;
		return result;
	}

	// constructor
	private TimeReact() {
		super.running = false;
	}

	@Override
	public synchronized void start() {
		if (!isRunning()) {
			super.start();
			thread = new Thread(task);
			thread.start();
			lastTime = System.currentTimeMillis();
		}
	}

	@Override
	public synchronized void stop() {
		if (isRunning()) {
			super.stop();
			if (thread != null) {
				thread.interrupt();
			}
		}
	}

	@Override
	public void applyDispose() {
		if (thread != null) {
			thread.interrupt();
		}
		task = null;
		thread = null;
	}

	/**
	 * Interface to run in time react.
	 */
	public interface TimeRunnable {

		public void run(TimeReact react) throws InterruptedException;
	}

	/**
	 * Create a new instance of time react. Encapsulate the runnable into thread.
	 *
	 * @param runnable the method to run in thread.
	 * @return the obtained instance of time react.
	 */
	private static TimeReact newInstance(final TimeRunnable runnable) {
		final TimeReact react = new TimeReact();

		react.task = new Runnable() {

			@Override
			public void run() {
				try {
					runnable.run(react);
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
	 * Factory to create a time based react. Emit a signal only one time after
	 * timeout.
	 *
	 * @param timeout time to wait before emitting message
	 * @return the time react
	 */
	public static TimeReact once(final long timeout) {

		return newInstance(new TimeRunnable() {

			@Override
			public void run(TimeReact react) throws InterruptedException {
				Thread.sleep(timeout);
				react.emit(react.getTimeElapsed());
			}
		});
	}

	/**
	 * Factory to create a time based react. Emit a signal whenever the time
	 * interval has elapsed.
	 *
	 * @param timeout time to wait before emitting message
	 * @return the time react
	 */
	public static TimeReact every(final long timeout) {
		return newInstance(new TimeRunnable() {

			@Override
			public void run(TimeReact react) throws InterruptedException {
				while (react.isRunning()) {
					Thread.sleep(timeout);
					react.emit(react.getTimeElapsed());
				}
			}
		});
	}

	/**
	 * Factory to create a time based react. Emit a signal whenever the time
	 * interval has elapsed.
	 *
	 * @param timeout time to wait before emitting message
	 * @return the time react
	 */
	public static TimeReact randomly(final int min, final int max) {
		return newInstance(new TimeRunnable() {

			Random rand = new Random();

			@Override
			public void run(TimeReact react) throws InterruptedException {

				while (react.isRunning()) {
					Thread.sleep(min + rand.nextInt(max - min));

					react.emit(react.getTimeElapsed());
				}
			}
		});
	}

	/**
	 * Factory to create a time based react. Emit a signal at regular time
	 * interval (consider the observer function elapsed time).
	 *
	 * @param timeout time to wait before emitting message
	 * @return the time react
	 */
	public static TimeReact framePerSecond(final int fps) {
		final long timeout = 1000L / fps;

		return newInstance(new TimeRunnable() {

			@Override
			public void run(TimeReact react) throws InterruptedException {
				long start = 0;
				long elapsed = 0;

				while (react.isRunning()) {
					if (timeout > elapsed) {
						Thread.sleep(timeout - elapsed);
					}

					start = System.currentTimeMillis();

					react.emit(react.getTimeElapsed());

					elapsed = System.currentTimeMillis() - start;
				}
			}
		});
	}
}
