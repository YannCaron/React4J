package fr.cyann.liveTest;

/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
import fr.cyann.functor.Function1;
import fr.cyann.functor.Procedure1;
import fr.cyann.react.MouseEvent;
import fr.cyann.liveTest.ui.JAutoScrollPane;
import fr.cyann.react.MouseReact;
import fr.cyann.react.Signal;
import fr.cyann.react.TimeEvent;
import fr.cyann.react.TimeReact;
import fr.cyann.base.Tuple;
import fr.cyann.liveTest.ui.Circle;
import fr.cyann.liveTest.ui.DrawPanel;
import fr.cyann.react.Var;
import fr.cyann.react.swing.RLabel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * The ReactiveMainFrame main class. Creation date: 8 oct. 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class ReactiveMainFrame {

	private static final JTextArea logger = new JTextArea();
	private static final RLabel label1 = new RLabel();
	private static final DrawPanel game = new DrawPanel();
	private static final Circle cursor = new Circle(25);

	private static void log(String message) {
		logger.append(message + "\n");
		System.out.println(message);
	}

	public static void initLabelReact() {
		// when mouse is pressed say "pressed" otherwise say "released"
		// concatenate message with mouse position

		// declare the mouse reactor
		Signal<String> mouseAndTime = MouseReact.hold(1).map(new Function1<String, MouseEvent>() {

			@Override
			public String invoke(MouseEvent value) {
				// when mouse button is pressed
				return "button pressed";
			}
		}).otherwise(new Function1<String, String>() {

			@Override
			public String invoke(String arg1) {
				// when it is released
				return "button released";
			}
		}).merge(MouseReact.move()).map(new Function1<String, Tuple<String, MouseEvent>>() {

			@Override
			public String invoke(Tuple<String, MouseEvent> values) {
				// concatenate with time
				return values.getFirst() + " at (x= " + values.getSecond().getX() + ", y= " + values.getSecond().getY() + ")";
			}
		});

		label1.setText(mouseAndTime);
	}

	public static void initCursor() {
		game.addShape(cursor);

		cursor.setX(MouseReact.move().map(new Function1<Integer, MouseEvent>() {

			@Override
			public Integer invoke(MouseEvent value) {
				return value.getX();
			}
		}));

		cursor.setY(MouseReact.move().map(new Function1<Integer, MouseEvent>() {

			@Override
			public Integer invoke(MouseEvent value) {
				return value.getY() - 40;
			}
		}));
	}

	public static void initAnim() {

		MouseReact.hold(1).during(TimeReact.every(50)).subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent value) {
				final Circle c = new Circle(5);
				game.addShape(c);
				double velAngle = Math.random() * Math.PI * 2;
				double velSpeed = Math.random() * 10;
				final double velX = Math.cos(velAngle) * velSpeed;
				final double velY = Math.sin(velAngle) * velSpeed;

				final Signal<Integer> x = new Var<Integer>(cursor.getX()).merge(TimeReact.framePerSecond(25)).map(new Function1<Integer, Tuple<Integer, TimeEvent>>() {

					@Override
					public Integer invoke(Tuple<Integer, TimeEvent> values) {
						return values.getFirst() + (int) (velX * values.getSecond().getIteration());
					}
				});
				
				final Signal<Integer> y = new Var<Integer>(cursor.getY()).merge(TimeReact.framePerSecond(25)).map(new Function1<Integer, Tuple<Integer, TimeEvent>>() {

					@Override
					public Integer invoke(Tuple<Integer, TimeEvent> values) {
						return values.getFirst() + (int) (velY * values.getSecond().getIteration());
					}
				});

				final Signal<Integer> size = new Var<Integer>(5).merge(TimeReact.framePerSecond(25)).map(new Function1<Integer, Tuple<Integer, TimeEvent>>() {

					@Override
					public Integer invoke(Tuple<Integer, TimeEvent> values) {
						double factor = Math.sin((double) values.getSecond().getIteration() / 10 - Math.PI / 4);
						int size = 50 + (int) (50 * factor);
						if (size == 1) {
							game.removeShape(c);
							x.dispose();
							y.dispose();
						}
						return size;
					}
				});
				
				c.setX(x);
				c.setY(y);
				c.setSize(size);
			}
		});

	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = frame.getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		Border empty = new EmptyBorder(10, 10, 10, 10);

		JAutoScrollPane scroll = new JAutoScrollPane(logger);
		scroll.forceAutoScroll();
		scroll.setBorder(empty);

		label1.setAlignmentX(Component.CENTER_ALIGNMENT);
		frame.add(label1);
		frame.add(game);
		frame.add(Box.createRigidArea(new Dimension(0, 5)));
		scroll.setMaximumSize(new Dimension(2000, 550));
		frame.add(scroll);

		frame.setPreferredSize(new Dimension(1024, 800));
		frame.pack();
		frame.setVisible(true);

		initLabelReact();
		initCursor();
		initAnim();

		Signal.count.subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer value) {
				logger.setText("Number of Signal = " + value);
			}
		});
	}
}
