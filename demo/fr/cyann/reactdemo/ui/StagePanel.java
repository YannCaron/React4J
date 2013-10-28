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

import fr.cyann.functional.Procedure1;
import fr.cyann.react.TimeReact;
import fr.cyann.react.Var;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * The StagePanel class.
 * Creation date: 17 oct. 2013.
 * @author Yann Caron
 * @version v0.1
 */
public class StagePanel extends JPanel {

	private static final Color bg = new Color(94, 63, 107);

	private final List<Shape> shapes;
	private final Var<Integer> count;

	private final Var<Integer> width, height;

	public StagePanel() {
		shapes = new ArrayList<Shape>();
		count = new Var<Integer>(0);
		System.out.println(getHeight());
		width = new Var<Integer>(getWidth());
		height = new Var<Integer>(getHeight());

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				width.setValue(e.getComponent().getWidth());
				height.setValue(e.getComponent().getHeight());
			}

		});

		TimeReact.framePerSecond(25).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer arg1) {
				StagePanel.this.repaint();
			}
		});

	}

	public Var<Integer> getRHeight() {
		return height;
	}

	public Var<Integer> getRWidth() {
		return width;
	}

	public synchronized void addShape(Shape e) {
		shapes.add(e);
		e.setStage(this);
		count.setValue(shapes.size());
	}

	public synchronized void removeShape(Shape c) {
		c.dispose();
		shapes.remove(c);
		count.setValue(shapes.size());
	}

	public Var<Integer> getShapeCounter() {
		return count;
	}

	// methodes
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// for antialising geometric shapes
		g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON));
		// for antialiasing text
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		// interpolation for image
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g2.setColor(bg);
		g2.fill(new Rectangle2D.Float(0, 0, this.getWidth(), this.getHeight()));

		synchronized (this) {
			for (Shape shape : shapes) {
				shape.draw(g2);
			}
		}

	}
}
