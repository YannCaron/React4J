/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.reactdemo;

import fr.cyann.functional.Function;
import fr.cyann.functional.Function1;
import fr.cyann.functional.Function2;
import fr.cyann.functional.Predicate1;
import fr.cyann.functional.Procedure1;
import fr.cyann.react.MouseReact;
import fr.cyann.react.Operation;
import fr.cyann.react.ReactManager;
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
import fr.cyann.reactdemo.ui.DrawPanel;

/**
 * The ReactDemo main class. Creation date: 18 oct. 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class ReactDemo {

	private static final RLabel label1 = new RLabel();
	private static final RLabel label2 = new RLabel();
	private static final DrawPanel game = new DrawPanel();
	private static final Circle cursor = new Circle(25);
	private static final Var<Integer> mouseX = MouseReact.positionX().map(new Function1<Integer, Integer>() {

		@Override
		public Integer invoke(Integer arg1) {
			return arg1 - (cursor.getSize() / 2 - 3);
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
		Var<String> mouseAndTime = MouseReact.hold().filter(MouseReact.BUTTON1).map(new Function1<String, Integer>() {

			@Override
			public String invoke(Integer value) {
				// when mouse button is pressed
				return "button pressed";
			}
		}).otherwise(new Function1<String, String>() {

			@Override
			public String invoke(String arg1) {
				// when it is released
				return "button released";
			}
		}).toVar("no button yet !").merge(mouseX, new Function2<String, String, Integer>() {

			@Override
			public String invoke(String arg1, Integer arg2) {
				// add mouse x position
				return arg1 + " ( x=" + arg2;
			}
		}).merge(mouseY, new Function2<String, String, Integer>() {

			@Override
			public String invoke(String arg1, Integer arg2) {
				// add mouse y position
				return arg1 + ", y=" + arg2 + ")";
			}
		});

		label1.setText(mouseAndTime);

		Var<String> counters = game.getShapeCounter().merge(ReactManager.getInstance().getReactCounter(), new Function2<String, Integer, Integer>() {

			@Override
			public String invoke(Integer arg1, Integer arg2) {
				return "Number of circles: " + arg1 + ", Number of react: " + arg2;
			}
		});
		label2.setText(counters);
	}

	public static void initCursor() {
		game.addShape(cursor);
		cursor.setX(mouseX);
		cursor.setY(mouseY);
	}

	public static void initAnim() {

		// when mouse button is old, then create a new circle every 50 ms
		MouseReact.hold().filter(MouseReact.BUTTON1).during(TimeReact.every(10)).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer value) {
				// create circle
				final Circle circle = new Circle(5);
				game.addShape(circle);

				// determine random velocity and angle constant in time
				double velAngle = Math.random() * Math.PI * 2;
				double velSpeed = Math.random() * 10;
				final double velX = Math.cos(velAngle) * velSpeed;
				final double velY = Math.sin(velAngle) * velSpeed;

				// fixe mouse values
				final int mX = mouseX.getValue();
				final int mY = mouseY.getValue();

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
				final Operation<Integer> x = Operation.syncOperation(new Function<Integer>() {

					@Override
					public Integer invoke() {
						return mX + (int) (velX * counter.getValue());
					}
				}, counter);

				// creation reactive operation between counter and constants to calculate y coordonate
				final Operation<Integer> y = Operation.syncOperation(new Function<Integer>() {

					@Override
					public Integer invoke() {
						return mY + (int) (velY * counter.getValue());
					}
				}, counter);

				// creation reactive operation between counter and constants to calculate circle size
				Operation<Integer> size = Operation.syncOperation(new Function<Integer>() {

					@Override
					public Integer invoke() {
						double factor = Math.sin((double) counter.getValue() / 10 - Math.PI / 4);
						return 50 + (int) (50 * factor);
					}
				}, counter);


				size.disposeWhen(new Predicate1<Integer>() {

					@Override
					public boolean invoke(Integer value) {
						if (value == 1) {
							game.removeShape(circle);
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

		frame.add(game);
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
}
