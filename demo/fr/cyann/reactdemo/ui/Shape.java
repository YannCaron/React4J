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
import fr.cyann.react.Operation;
import fr.cyann.react.Signal;
import fr.cyann.react.Var;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * The Shape class. Creation date: 27 oct. 2013.
 *
 * @author Yann Caron
 * @version v0.1
 */
public abstract class Shape {

	public enum Type {

		PLAYER, ENNEMY, SHOOT_PLAYER, SHOOT_ENNEMY, OTHER;
	}

	protected final Var<Integer> x, y;
	private final Var<Boolean> out, outTop, outBottom, outLeft, outRight;
	final Signal<Shape> collision, smoothCollision;
	private final List<Signal> links;
	private final Type type;

	public abstract int getWidth();

	public abstract int getHeight();

	public Shape(Type type) {
		this.type = type;
		links = new ArrayList<Signal>();
		x = new Var<Integer>(0);
		y = new Var<Integer>(0);
		out = new Var<Boolean>(false);
		outTop = new Var<Boolean>(false);
		outBottom = new Var<Boolean>(false);
		outLeft = new Var<Boolean>(false);
		outRight = new Var<Boolean>(false);
		collision = new Signal<Shape>();
		smoothCollision = collision.filterFold(null, new Signal.DropRepeatFilter<Shape>());
	}

	public void setStage(final StagePanel stage) {
		y.map(new Function1<Integer, Boolean>() {

			@Override
			public Boolean invoke(Integer arg1) {
				return arg1 < 0;
			}
		}).filterFold(new Signal.DropRepeatFilter<Boolean>()).dump(outTop);

		y.map(new Function1<Integer, Boolean>() {

			@Override
			public Boolean invoke(Integer arg1) {
				return arg1 + getHeight() > stage.getRHeight().getValue();
			}
		}).filterFold(new Signal.DropRepeatFilter<Boolean>()).dump(outBottom);

		x.map(new Function1<Integer, Boolean>() {

			@Override
			public Boolean invoke(Integer arg1) {
				return arg1 < 0;
			}
		}).filterFold(new Signal.DropRepeatFilter<Boolean>()).dump(outLeft);

		x.map(new Function1<Integer, Boolean>() {

			@Override
			public Boolean invoke(Integer arg1) {
				return arg1 + getWidth() > stage.getRWidth().getValue();
			}
		}).filterFold(new Signal.DropRepeatFilter<Boolean>()).dump(outRight);

		Operation.mergeOperation(new Function<Boolean>() {

			@Override
			public Boolean invoke() {
				return outTop.getValue() || outBottom.getValue() || outLeft.getValue() || outRight.getValue();
			}
		}, outTop, outBottom, outLeft, outRight).dump(out);

	}

	public Type getType() {
		return type;
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

	public Signal<Shape> getCollision() {
		return smoothCollision;
	}

	public synchronized boolean addLink(Signal e) {
		return links.add(e);
	}

	public synchronized void dispose() {
		x.dispose();
		y.dispose();
		out.dispose();
		outTop.dispose();
		outBottom.dispose();
		outLeft.dispose();
		outRight.dispose();
		collision.dispose();
		smoothCollision.dispose();
		for (Signal link : links) {
			link.dispose();
		}
	}

	public abstract void draw(Graphics g);
}
