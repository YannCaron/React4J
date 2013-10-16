/*
 * Copyright (C) 2013 caronyn
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
 * You should have received a copy of the GNU Less General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cyann.react;

import fr.cyann.functor.Procedure1;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

/**
 *
 * @author caronyn
 */
public class MouseRetained extends EventRetained<fr.cyann.react.MouseEvent> {

	// const
	private static final Toolkit TK = Toolkit.getDefaultToolkit();

	// constructor
	public MouseRetained() {
		super(new fr.cyann.react.MouseEvent());
	}

	public static MouseRetained hold(final int button) {
		final MouseRetained react = new MouseRetained();

		AWTEventListener listener = new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent e) {
				if (e instanceof java.awt.event.MouseEvent) {
					java.awt.event.MouseEvent ev = (java.awt.event.MouseEvent) e;

					if (react.isRunning() && ev.getButton() == button && ev.getID() == MouseEvent.MOUSE_PRESSED) {
						react.value.setEvent(ev);
						react.emit();
					}

					if (ev.getButton() == button && ev.getID() == MouseEvent.MOUSE_RELEASED) {
						react.value.setEvent(ev);
						react.emitFinish();
					}
				}
			}
		};

		TK.addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK);

		return react;
	}
	
}
