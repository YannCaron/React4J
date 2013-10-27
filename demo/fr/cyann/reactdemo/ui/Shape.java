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

import fr.cyann.functional.Function2;
import fr.cyann.functional.Procedure1;
import fr.cyann.react.Signal;
import fr.cyann.react.Var;
import java.awt.Graphics;

/**
 * The Shape class.
 * Creation date: 27 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public abstract class Shape {

	protected final Var<Integer> x, y;
	private final Var<Boolean> out;
	private Var<Boolean> tmpout;

	public Shape() {
		x = new Var<Integer>(0);
		y = new Var<Integer>(0);
		out = new Var<Boolean>(false);
	}

	public void setStage(StagePanel stage) {
		Var<Boolean> xout = x.merge(stage.getComponentWidth(), new Function2<Boolean, Integer, Integer>() {

			@Override
			public Boolean invoke(Integer arg1, Integer arg2) {
				return arg1 < 0 || arg1 > arg2;
			}
		});
		Var<Boolean> yout = y.merge(stage.getComponentHeight(), new Function2<Boolean, Integer, Integer>() {

			@Override
			public Boolean invoke(Integer arg1, Integer arg2) {
				return arg1 < 0 || arg1 > arg2;
			}
		});
		tmpout = xout.merge(yout, new Function2<Boolean, Boolean, Boolean>() {

			@Override
			public Boolean invoke(Boolean arg1, Boolean arg2) {
				return arg1 || arg2;
			}
		}).subscribe(out);
	}

	public void setX(Var<Integer> var) {
		var.subscribe(x);
		x.setValue(var.getValue());
	}

	public void setY(Var<Integer> var) {
		var.subscribe(y);
		y.setValue(var.getValue());
	}

	public int getX() {
		return x.getValue();
	}

	public int getY() {
		return y.getValue();
	}

	public Var<Boolean> getOut() {
		return out;
	}

	public void dispose() {
		x.dispose();
		y.dispose();
		out.dispose();
		if (tmpout != null) {
			tmpout.dispose();
		}
	}

	public abstract void draw(Graphics g);
}
