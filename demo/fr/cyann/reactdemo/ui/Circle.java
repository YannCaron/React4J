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
package fr.cyann.reactdemo.ui;

import fr.cyann.react.Signal;
import fr.cyann.react.Var;
import java.awt.Color;
import java.awt.Graphics;

/**
 * The Circle class.
 * Creation date: 17 oct. 2013.
 * @author Yann Caron
 * @version v0.1
 */
public class Circle extends Shape {

	private final Var<Integer> size;
	public final Color shapeColor, fillColor;

	public Circle(int initSize) {
		size = new Var<Integer>(initSize);
		shapeColor = new Color(255, 255, 255, 200);
		fillColor = new Color(255, 255, 255, 100);
	}

	public void setSize(Signal<Integer> signal) {
		signal.dump(size);
	}

	public int getSize() {
		return size.getValue();
	}

	@Override
	public void dispose() {
		super.dispose();
		size.dispose();
	}

	@Override
	public void draw(Graphics g) {
		int s = size.getValue();
		int ms = s / 2;

		g.setColor(fillColor);
		g.fillOval(x.getValue() - ms, y.getValue(), s, s);

		g.setColor(shapeColor);
		g.drawOval(x.getValue() - ms, y.getValue(), s, s);
	}
}
