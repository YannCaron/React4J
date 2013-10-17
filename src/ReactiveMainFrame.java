/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */

import fr.cyann.functor.Function1;
import fr.cyann.functor.Procedure1;
import fr.cyann.react.MouseEvent;
import fr.cyann.ui.JAutoScrollPane;
import fr.cyann.react.MouseReact;
import fr.cyann.react.Signal;
import fr.cyann.react.TimeEvent;
import fr.cyann.react.TimeReact;
import fr.cyann.base.Tuple;
import fr.cyann.react.Var;
import fr.cyann.react.swing.RLabel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Date;
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

	private static final JTextArea TEXT = new JTextArea();
	private static final RLabel LABEL = new RLabel();

	private static void log(String message) {
		TEXT.append(message + "\n");
		System.out.println(message);
	}

	public static void testReact1() {
		MouseReact.press(1).merge(TimeReact.every(500L)).subscribe(new Procedure1() {

			@Override
			public void invoke(Object event) {
				log(event.toString());
			}
		});
	}

	public static void testReact2() {
		MouseReact.hold(1).runDuring(TimeReact.every(50L)).subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent event) {
				log(event.toString());
			}
		});
	}

	public static void testReact3() {
		MouseReact.press(1).retainUntil(TimeReact.once(500)).runDuring(TimeReact.every(50L)).subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent event) {
				log(event.toString());
			}
		});
	}

	public static void testReact4() {
		TimeReact.every(1000).map(new Function1<Date, TimeEvent>() {

			@Override
			public Date invoke(TimeEvent arg1) {
				return new Date();
			}
		}).subscribe(new Procedure1<Date>() {

			@Override
			public void invoke(Date arg1) {
				log(arg1.toString());
			}
		});
	}

	public static void testReact5() {
		Signal<String> m = MouseReact.hold(1).map(new Function1<String, MouseEvent>() {

			@Override
			public String invoke(MouseEvent value) {
				return "Mouse button 1 pressed";
			}
		}).otherwise(new Function1<String, String>() {

			@Override
			public String invoke(String arg1) {
				return "Mouse button 1 released";
			}
		}).merge(TimeReact.every(1000)).map(new Function1<String, Tuple<String, TimeEvent>>() {

			@Override
			public String invoke(Tuple<String, TimeEvent> values) {
				System.out.println("GO");
				return values.getFirst() + " " + values.getSecond().getDate();
			}
		});

		LABEL.setText(m);
	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = frame.getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		Border empty = new EmptyBorder(10, 10, 10, 10);

		JAutoScrollPane scroll = new JAutoScrollPane(TEXT);
		scroll.forceAutoScroll();
		scroll.setBorder(empty);

		LABEL.setAlignmentX(Component.CENTER_ALIGNMENT);
		frame.add(LABEL);
		LABEL.setText("TEST");
		frame.add(scroll);

		frame.setPreferredSize(new Dimension(800, 600));
		frame.pack();
		frame.setVisible(true);

		testReact5();
	}
}
