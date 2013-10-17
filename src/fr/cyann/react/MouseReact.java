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

import fr.cyann.functor.Predicate1;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

/**
 * The MouseReact class. Creation date: 13 oct. 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class MouseReact extends EventReact<fr.cyann.react.MouseEvent> {

	// const
	private static final Toolkit TK = Toolkit.getDefaultToolkit();

	// constructor
	private MouseReact() {
		super(new fr.cyann.react.MouseEvent());
	}

	// general factory
	private static MouseReact createListener(final Predicate1<MouseEvent> predicate, long eventMask) {
		final MouseReact react = new MouseReact();

		AWTEventListener listener = new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent e) {
				if (e instanceof MouseEvent) {
					MouseEvent ev = (MouseEvent) e;

					if (react.isRunning() && predicate.invoke(ev)) {
						react.value.setEvent(ev);
						react.emit(react.value);
					}
				}
			}
		};

		TK.addAWTEventListener(listener, eventMask);

		return react;

	}

	// factories
	/**
	 * Create a mouse react that emit an event each time user press a mouse button
	 * on application window
	 *
	 * @return the created mouse react instance
	 */
	private static MouseReact button(final int id, final int button) {

		return createListener(new Predicate1<MouseEvent>() {
			@Override
			public boolean invoke(MouseEvent ev) {
				return ev.getButton() == button && (ev.getID() == id);
			}
		}, AWTEvent.MOUSE_EVENT_MASK);

	}

	public static MouseReact press(int button) {
		return MouseReact.button(MouseEvent.MOUSE_PRESSED, button);
	}

	public static MouseReact release(int button) {
		return MouseReact.button(MouseEvent.MOUSE_RELEASED, button);
	}

	public static MouseReact hold(final int button) {
		final MouseReact react = new MouseReact();

		AWTEventListener listener = new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent e) {
				if (e instanceof java.awt.event.MouseEvent) {
					java.awt.event.MouseEvent ev = (java.awt.event.MouseEvent) e;

					if (react.isRunning() && ev.getButton() == button && ev.getID() == MouseEvent.MOUSE_PRESSED) {
						react.value.setEvent(ev);
						react.emit(react.value);
					}

					if (ev.getButton() == button && ev.getID() == MouseEvent.MOUSE_RELEASED) {
						react.value.setEvent(ev);
						react.emitFinish(react.value);
					}
				}
			}
		};

		TK.addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK);

		return react;
	}
}
