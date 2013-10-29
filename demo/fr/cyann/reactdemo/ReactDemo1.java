/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
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
public class ReactDemo1 {

	private static final RLabel label1 = new RLabel();
	private static final RLabel label2 = new RLabel();
	private static final StagePanel particule = new StagePanel();
	private static final Circle cursor = new Circle(25);
	private static final Var<Integer> mouseX = MouseReact.positionX().map(new Function1<Integer, Integer>() {

		@Override
		public Integer invoke(Integer arg1) {
			return arg1 - (cursor.getSize() / 2 - 2);
		}
	}).toVar(0);
	private static final Var<Integer> mouseY = MouseReact.positionY().map(new Function1<Integer, Integer>() {

		@Override
		public Integer invoke(Integer arg1) {
			return arg1 - (cursor.getSize() / 2 + 30);
		}
	}).toVar(0);

	public static void initLabelsReact() {
		// when mouse is pressed say "pressed" otherwise say "released"
		// concatenate message with mouse position and update each time it is necessary

		// declare the mouse reactor
		Var<String> mouseAndTime = MouseReact.button1().map(new Function1<Boolean, String>() {

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
	private static Signal<Integer> tab1React;

	public static void initAnim() {

		// when mouse button is old, then create a new circle every 50 ms
		tab1React = TimeReact.every(10).edge(MouseReact.button1()).subscribe(new Procedure1<Integer>() {

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

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = frame.getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		Border empty = new EmptyBorder(10, 10, 10, 10);

		label1.setAlignmentX(Component.CENTER_ALIGNMENT);
		label2.setAlignmentX(Component.CENTER_ALIGNMENT);

		final JTabbedPane tab = new JTabbedPane(JTabbedPane.BOTTOM);

		frame.add(particule);
		frame.add(Box.createRigidArea(new Dimension(0, 10)));
		frame.add(label1);
		frame.add(Box.createRigidArea(new Dimension(0, 10)));
		frame.add(label2);
		frame.add(Box.createRigidArea(new Dimension(0, 10)));

		frame.setPreferredSize(new Dimension(1024, 800));
		frame.pack();
		frame.setVisible(true);

		tab.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (tab1React != null) {
					tab1React.dispose();
				}


				switch (tab.getSelectedIndex()) {
					case 0:
						initAnim();
						break;
				}
			}
		});

		initLabelsReact();
		initCursor();
		initAnim();

	}
}
