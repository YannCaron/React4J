/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.gui;

import fr.cyann.functor.Procedure1;
import fr.cyann.react.Event;
import fr.cyann.react.MouseReact;
import fr.cyann.react.TimeReact;
import java.awt.BorderLayout;
import java.awt.Dimension;
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
		MouseReact.click().mergeRight(TimeReact.every(500L)).subscribe(new Procedure1() {

			@Override
			public void invoke(Object event) {
				log(event.toString());
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

		testReact1();
	}
}
