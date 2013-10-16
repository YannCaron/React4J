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

/**
 * The MouseEvent class.
 * Creation date: 13 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class MouseEvent implements Event {

	public enum ButtonAction {

		PRESS, RELEASE;
	}

	public enum Wheel {

		UP, DOWN;
	}
	
	// attributes
	private int button;
	private ButtonAction action;
	private int x, y;
	private Wheel wheel;

	@Package void setEvent(java.awt.event.MouseEvent ev) {
		button = ev.getButton();
		if (ev.getID() == java.awt.event.MouseEvent.MOUSE_PRESSED) {
			action = ButtonAction.PRESS;
		} else {
			action = ButtonAction.RELEASE;
		}
	}
	
	// methods
	@Override
	public String toString() {
		return "MouseEvent{" + "button=" + button + ", action=" + action + ", x=" + x + ", y=" + y + ", wheel=" + wheel + '}';
	}

}
