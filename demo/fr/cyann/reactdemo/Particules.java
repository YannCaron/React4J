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
package fr.cyann.reactdemo;

import fr.cyann.functional.Function1;
import fr.cyann.functional.Function2;
import fr.cyann.functional.Predicate1;
import fr.cyann.functional.Procedure1;
import fr.cyann.react.MouseReact;
import fr.cyann.react.ReactManager;
import fr.cyann.react.Signal;
import fr.cyann.react.TimeReact;
import fr.cyann.react.Var;
import fr.cyann.reactdemo.ui.swing.RLabel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import fr.cyann.reactdemo.ui.Circle;
import fr.cyann.reactdemo.ui.StagePanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The ReactDemo1 main class. Creation date: 18 oct. 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class Particules {

	private static final RLabel label1 = new RLabel();
	private static final RLabel label2 = new RLabel();
	private static final StagePanel particule = new StagePanel();
	private static final Circle cursor = new Circle(25);
	private static final Var<Integer> mouseX = MouseReact.onMoveX().map(new Function1<Integer, Integer>() {

		@Override
		public Integer invoke(Integer arg1) {
			return arg1 - (cursor.getSize() / 2 - 2);
		}
	}).toVar(0);
	private static final Var<Integer> mouseY = MouseReact.onMoveY().map(new Function1<Integer, Integer>() {

		@Override
		public Integer invoke(Integer arg1) {
			return arg1 - (cursor.getSize() / 2 + 30);
		}
	}).toVar(0);
	private static Signal<Integer> tab1React;

	public static void launch() {

		JFrame frame = new JFrame();
		Container pane = frame.getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		label1.setAlignmentX(Component.CENTER_ALIGNMENT);
		label2.setAlignmentX(Component.CENTER_ALIGNMENT);

		frame.add(particule);
		frame.add(Box.createRigidArea(new Dimension(0, 10)));
		frame.add(label1);
		frame.add(Box.createRigidArea(new Dimension(0, 10)));
		frame.add(label2);
		frame.add(Box.createRigidArea(new Dimension(0, 10)));

		frame.setPreferredSize(new Dimension(1024, 800));
		frame.pack();
		frame.setVisible(true);

		initLabelsReact();
		initCursor();
		initAnim();

	}

	public static void initLabelsReact() {
		// when mouse is pressed say "pressed" otherwise say "released"
		// concatenate message with mouse position and update each time it is necessary

		// declare the mouse reactor
		Var<String> mouseAndTime = MouseReact.onButton1().map(new Function1<Boolean, String>() {

			@Override
			public String invoke(Boolean arg1) {
				if (arg1) {
					return "button pressed";
				}
				return "button released";
			}
		}).toVar("no button yet !").merge(mouseX, new Function2<String, Integer, String>() {

			@Override
			public String invoke(String arg1, Integer arg2) {
				// add mouse x position
				return arg1 + " ( x=" + arg2;
			}
		}).merge(mouseY, new Function2<String, Integer, String>() {

			@Override
			public String invoke(String arg1, Integer arg2) {
				// add mouse y position
				return arg1 + ", y=" + arg2 + ")";
			}
		});

		label1.setText(mouseAndTime);

		Var<String> counters = particule.getShapeCounter().merge(ReactManager.getInstance().getReactCounter(), new Function2<Integer, Integer, String>() {

			@Override
			public String invoke(Integer arg1, Integer arg2) {
				return "Number of circles: " + arg1 + ", Number of react: " + arg2;
			}
		});
		label2.setText(counters);
	}

	public static void initCursor() {
		particule.addShape(cursor);
		cursor.setX(mouseX);
		cursor.setY(mouseY);
	}

	public static void initAnim() {

		// when mouse button is old, then create a new circle every 50 ms
		tab1React = TimeReact.every(10).edge(MouseReact.onButton1()).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer value) {
				// create circle
				final Circle circle = new Circle(5);
				particule.addShape(circle);

				// determine random velocity and angle constant in time
				double velAngle = Math.random() * Math.PI * 2;
				double velSpeed = Math.random() * 10;
				final double velX = Math.cos(velAngle) * velSpeed;
				final double velY = Math.sin(velAngle) * velSpeed;

				final int mx = mouseX.getValue();
				final int my = mouseY.getValue();

				// create reactive counter every 25 fps
				final Var<Integer> counter = TimeReact.framePerSecond(25).fold(0, new Function2<Integer, Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1, Integer arg2) {
						return arg1 + 1;
					}
				}).map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1) {
						return arg1.intValue();
					}
				}).toVar(0);

				// creation reactive operation between counter and constants to calculate x coordonate
				final Var<Integer> x = counter.map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer value) {
						return mx + (int) (velX * value);
					}
				});

				// creation reactive operation between counter and constants to calculate y coordonate
				final Var<Integer> y = counter.map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer value) {
						return my + (int) (velY * value);
					}
				});

				// creation reactive operation between counter and constants to calculate circle size
				Signal<Integer> size = counter.map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer value) {
						double factor = Math.sin((double) value / 10 - Math.PI / 4);
						return 50 + (int) (50 * factor);
					}
				});

				size.disposeWhen(new Predicate1<Integer>() {

					@Override
					public boolean invoke(Integer value) {
						if (value == 1) {
							particule.removeShape(circle);
							x.dispose();
							y.dispose();
							return true;
						}
						return false;
					}
				});

				// set reacts to component
				circle.setX(x);
				circle.setY(y);
				circle.setSize(size);
			}
		});

	}

}
