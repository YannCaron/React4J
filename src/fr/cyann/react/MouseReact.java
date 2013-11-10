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
 * @author Yann Caron
 * @version v0.1
 */
public class MouseReact<T> extends AbstractReact<T> {

	// const
	private static final Toolkit TK = Toolkit.getDefaultToolkit();

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
	private static MouseReact<Integer> createListener(final Predicate1<MouseEvent> filter, final Function1<MouseEvent, Integer> map, long eventMask) {
		final MouseReact<Integer> react = new MouseReact<Integer>();

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
	private static MouseReact<Boolean> createButtonListener(final int buttonNumber) {
		final MouseReact<Boolean> react = new MouseReact<Boolean>();

		AWTEventListener listener = new AWTEventListener() {

			@Override
			public void eventDispatched(AWTEvent e) {
				if (e instanceof MouseEvent) {
					MouseEvent ev = (MouseEvent) e;

					if (react.isRunning() && ev.getID() == MouseEvent.MOUSE_PRESSED && ev.getButton() == buttonNumber) {
						react.emit(true);
					}
					if (react.isRunning() && ev.getID() == MouseEvent.MOUSE_RELEASED && ev.getButton() == buttonNumber) {
						react.emit(false);
					}
				}
			}
		};

		TK.addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK);

		return react;

	}

	public static MouseReact<Boolean> onButton1() {
		return createButtonListener(1);
	}

	public static MouseReact<Boolean> onButton2() {
		return createButtonListener(2);
	}

	public static MouseReact<Boolean> onButton3() {
		return createButtonListener(3);
	}

	/**
	 * React that emit event when any mouse is moved, value is the x position of the cursor relative to the application.
	 * @return The corresponding mouse react.
	 */
	public static MouseReact<Integer> onMoveX() {
		return createListener(new Predicate1<MouseEvent>() {

			@Override
			public boolean invoke(MouseEvent event) {
				return true;
			}
		}, new Function1<MouseEvent, Integer>() {

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
	public static MouseReact<Integer> onMoveY() {
		return createListener(new Predicate1<MouseEvent>() {

			@Override
			public boolean invoke(MouseEvent event) {
				return true;
			}
		}, new Function1<MouseEvent, Integer>() {

			@Override
			public Integer invoke(MouseEvent event) {
				return event.getY();
			}
		}, AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}
}
