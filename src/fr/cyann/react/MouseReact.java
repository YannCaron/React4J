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

/**
 * The MouseReact class.
 * Creation date: 13 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class MouseReact extends Signal<MouseEvent> {

	protected final MouseEvent event;

	@Override
	public MouseEvent getValue() {
		return event;
	}

	private MouseReact() {
		event = new MouseEvent();
	}
	private Thread thread;

	public static MouseReact once(final long timeout) {
		final MouseReact react = new MouseReact();

		react.thread = new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(timeout);
					react.emit(react.event);
				} catch (InterruptedException ex) {
					// do nothing
				}

			}
		};

		react.thread.start();

		return react;
	}
}
