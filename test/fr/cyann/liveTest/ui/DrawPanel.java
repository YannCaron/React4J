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

import fr.cyann.functor.Procedure1;
import fr.cyann.react.TimeEvent;
import fr.cyann.react.TimeReact;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * The DrawPanel class.
 * Creation date: 17 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class DrawPanel extends JPanel {

	public final List<Circle> shapes;

	public DrawPanel() {
		shapes = new ArrayList<Circle>();

		TimeReact.framePerSecond(25).subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent arg1) {
				DrawPanel.this.repaint();
			}
		});
	}

	public boolean addShape(Circle e) {
		return shapes.add(e);
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

		g2.setColor(Color.DARK_GRAY);
		g2.fill(new Rectangle2D.Float(0, 0, this.getWidth(), this.getHeight()));

		g2.setColor(Color.LIGHT_GRAY);

		for (Circle shape : shapes) {
			shape.draw(g2);
		}

	}
}
