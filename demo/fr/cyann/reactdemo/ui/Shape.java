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

import fr.cyann.functional.Function;
import fr.cyann.functional.Function1;
import fr.cyann.functional.Function2;
import fr.cyann.react.Operation;
import fr.cyann.react.Signal;
import fr.cyann.react.Var;
import java.awt.Graphics;
import java.awt.print.Book;

/**
 * The Shape class.
 * Creation date: 27 oct. 2013.
 * @author Yann Caron
 * @version v0.1
 */
public abstract class Shape {

	protected final Var<Integer> x, y;
	private final Var<Boolean> out, outTop, outBottom, outLeft, outRight;

	public Shape() {
		x = new Var<Integer>(0);
		y = new Var<Integer>(0);
		out = new Var<Boolean>(false);
		outTop = new Var<Boolean>(false);
		outBottom = new Var<Boolean>(false);
		outLeft = new Var<Boolean>(false);
		outRight = new Var<Boolean>(false);
	}
	public void setStage(final StagePanel stage) {
		y.map(new Function1<Boolean, Integer>() {

			@Override
			public Boolean invoke(Integer arg1) {
				return arg1 < 0;
			}
		}).filterFold(new Signal.DropRepeatFilter<Boolean>()).dump(outTop);

		y.map(new Function1<Boolean, Integer>() {

			@Override
			public Boolean invoke(Integer arg1) {
				return arg1 > stage.getRHeight().getValue();
			}
		}).filterFold(new Signal.DropRepeatFilter<Boolean>()).dump(outBottom);

		x.map(new Function1<Boolean, Integer>() {

			@Override
			public Boolean invoke(Integer arg1) {
				return arg1 < 0;
			}
		}).filterFold(new Signal.DropRepeatFilter<Boolean>()).dump(outLeft);

		x.map(new Function1<Boolean, Integer>() {

			@Override
			public Boolean invoke(Integer arg1) {
				return arg1 > stage.getRWidth().getValue();
			}
		}).filterFold(new Signal.DropRepeatFilter<Boolean>()).dump(outRight);

		Operation.mergeOperation(new Function<Boolean>() {

			@Override
			public Boolean invoke() {
				return outTop.getValue() || outBottom.getValue() || outLeft.getValue() || outRight.getValue();
			}
		}, outTop, outBottom, outLeft, outRight).dump(out);

	}

	public void setX(Var<Integer> var) {
		var.dump(x);
		x.setValue(var.getValue());
	}

	public void setY(Var<Integer> var) {
		var.dump(y);
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

	public Var<Boolean> getOutBottom() {
		return outBottom;
	}

	public Var<Boolean> getOutLeft() {
		return outLeft;
	}

	public Var<Boolean> getOutRight() {
		return outRight;
	}

	public Var<Boolean> getOutTop() {
		return outTop;
	}

	public void dispose() {
		x.dispose();
		y.dispose();
		out.dispose();
		outTop.dispose();
		outBottom.dispose();
		outLeft.dispose();
		outRight.dispose();
	}

	public abstract void draw(Graphics g);
}
