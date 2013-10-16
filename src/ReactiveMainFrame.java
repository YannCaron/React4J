/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */

import fr.cyann.functor.Function1;
import fr.cyann.functor.Procedure1;
import fr.cyann.gui.JAutoScrollPane;
import fr.cyann.react.MouseEvent;
import fr.cyann.react.MouseReact;
import fr.cyann.react.MouseRetained;
import fr.cyann.react.TimeEvent;
import fr.cyann.react.TimeReact;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Date;
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

	private static void log(String message) {
		TEXT.append(message + "\n");
		System.out.println(message);
	}

	public static void testReact1() {
		MouseReact.press(1).mergeRight(TimeReact.every(500L)).subscribe(new Procedure1() {

			@Override
			public void invoke(Object event) {
				log(event.toString());
			}
		});
	}

	public static void testReact2() {
		MouseRetained.hold(1).runDuring(TimeReact.every(50L)).subscribe(new Procedure1<TimeEvent>() {

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
		MouseRetained.hold(1).subscribe(new Procedure1<MouseEvent>() {

			@Override
			public void invoke(MouseEvent arg1) {
				log("yes");
			}
		}).subscribeFinish(new Procedure1<MouseEvent>() {

			@Override
			public void invoke(MouseEvent arg1) {
				log("no");
			}
		});
	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		BorderLayout layout = new BorderLayout();
		frame.setLayout(layout);

		Border empty = new EmptyBorder(10, 10, 10, 10);

		JAutoScrollPane scroll = new JAutoScrollPane(TEXT);
		scroll.forceAutoScroll();
		scroll.setBorder(empty);

		frame.add(scroll);

		frame.setPreferredSize(new Dimension(800, 600));
		frame.pack();
		frame.setVisible(true);

		testReact4();
	}
}
