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
package fr.cyann.liveTest.ui;

import fr.cyann.react.Signal;
import fr.cyann.react.Var;
import java.awt.Graphics;

/**
 * The Circle class.
 * Creation date: 17 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class Circle {
	
	private final Var<Integer> x, y;

	public Circle() {
		x = new Var<Integer>(0);
		y = new Var<Integer>(0);
	}

	public void setX(Signal<Integer> sx) {
		sx.register(x);
	}

	public void setY(Signal<Integer> sy) {
		sy.register(y);
	}

	public void draw(Graphics g) {
		g.drawOval(x.getValue() - 12, y.getValue() - 12, 25, 25);
	}
	
}
