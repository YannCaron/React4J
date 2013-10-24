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

import fr.cyann.functional.Function1;
import fr.cyann.functional.Predicate1;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

/**
 * The MouseReact class. Creation date: 13 oct. 2013.
 * All factories of mouse reacts.
 * @author CyaNn
 * @version v0.1
 */
public class MouseReact extends EventReact<Integer> {

	// const
	private static final Toolkit TK = Toolkit.getDefaultToolkit();

	/**
	 * Predefined filter. Filter on the button 1 when mouse is pressed or released.
	 */
	public static Predicate1<Integer> BUTTON1 = new Predicate1<Integer>() {

		@Override
		public boolean invoke(Integer value) {
			return value.equals(1);
		}
	};
	/**
	 * Predefined filter. Filter on the button 2 when mouse is pressed or released.
	 */
	public static Predicate1<Integer> BUTTON2 = new Predicate1<Integer>() {

		@Override
		public boolean invoke(Integer value) {
			return value.equals(1);
		}
	};
	/**
	 * Predefined filter. Filter on the button 3 when mouse is pressed or released.
	 */
	public static Predicate1<Integer> BUTTON3 = new Predicate1<Integer>() {

		@Override
		public boolean invoke(Integer value) {
			return value.equals(1);
		}
	};

	/**
	 * Template method (GoF). Do not override it !<br>
	 * Needed to dispose all react resources, threads etc.
	 */
	@Override
	public void applyDispose() {

		AWTEventListener[] listeners = TK.getAWTEventListeners();

		for (int i = listeners.length - 1; i >= 0; i--) {
			TK.removeAWTEventListener(listeners[i]);
		}
	}

	// general factory
	private static MouseReact createListener(final Predicate1<MouseEvent> filter, final Function1<Integer, MouseEvent> map, long eventMask) {
		final MouseReact react = new MouseReact();

		AWTEventListener listener = new AWTEventListener() {

			@Override
			public void eventDispatched(AWTEvent e) {
				if (e instanceof MouseEvent) {
					MouseEvent ev = (MouseEvent) e;

					if (react.isRunning() && filter.invoke(ev)) {
						react.emit(map.invoke(ev));
					}
				}
			}
		};

		TK.addAWTEventListener(listener, eventMask);

		return react;

	}

	// factories
	/**
	 * React that emit event when any mouse button is pressed.
	 * @return The corresponding mouse react.
	 */
	public static MouseReact press() {
		return createListener(new Predicate1<MouseEvent>() {

			@Override
			public boolean invoke(MouseEvent event) {
				return event.getID() == MouseEvent.MOUSE_PRESSED;
			}
		}, new Function1<Integer, MouseEvent>() {

			@Override
			public Integer invoke(MouseEvent event) {
				return event.getButton();
			}
		}, AWTEvent.MOUSE_EVENT_MASK);
	}

	/**
	 * React that emit event when any mouse button is released.
	 * @return The corresponding mouse react.
	 */
	public static MouseReact release() {
		return createListener(new Predicate1<MouseEvent>() {

			@Override
			public boolean invoke(MouseEvent event) {
				return event.getID() == MouseEvent.MOUSE_RELEASED;
			}
		}, new Function1<Integer, MouseEvent>() {

			@Override
			public Integer invoke(MouseEvent event) {
				return event.getButton();
			}
		}, AWTEvent.MOUSE_EVENT_MASK);
	}

	/**
	 * React that emit event when any mouse button is pressed and finish event when mouse button is released.<br>
	 * It create a long signal that is framed.
	 * @return The corresponding mouse react.
	 */
	public static MouseReact hold() {
		final MouseReact react = new MouseReact() {

			@Override
			public void start() {
				super.start();
			}
		};

		AWTEventListener listener = new AWTEventListener() {

			@Override
			public void eventDispatched(AWTEvent e) {
				if (e instanceof java.awt.event.MouseEvent) {
					java.awt.event.MouseEvent ev = (java.awt.event.MouseEvent) e;

					if (react.isRunning() && ev.getID() == MouseEvent.MOUSE_PRESSED) {
						react.emit(ev.getButton());
					}

					if (react.isRunning() && ev.getID() == MouseEvent.MOUSE_RELEASED) {
						react.emitFinish(ev.getButton());
					}
				}
			}
		};

		TK.addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK);

		return react;
	}

	/**
	 * React that emit event when any mouse is moved, value is the x position of the cursor relative to the application.
	 * @return The corresponding mouse react.
	 */
	public static MouseReact positionX() {
		return createListener(new Predicate1<MouseEvent>() {

			@Override
			public boolean invoke(MouseEvent event) {
				return true;
			}
		}, new Function1<Integer, MouseEvent>() {

			@Override
			public Integer invoke(MouseEvent event) {
				return event.getX();
			}
		}, AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}

	/**
	 * React that emit event when any mouse is moved, value is the x position of the cursor relative to the application.
	 * @return The corresponding mouse react.
	 */
	public static MouseReact positionY() {
		return createListener(new Predicate1<MouseEvent>() {

			@Override
			public boolean invoke(MouseEvent event) {
				return true;
			}
		}, new Function1<Integer, MouseEvent>() {

			@Override
			public Integer invoke(MouseEvent event) {
				return event.getY();
			}
		}, AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}
}
