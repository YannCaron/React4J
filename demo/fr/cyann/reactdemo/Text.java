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
import fr.cyann.react.Operation;
import fr.cyann.react.Var;
import static fr.cyann.reactdemo.Particules.initAnim;
import static fr.cyann.reactdemo.Particules.initCursor;
import static fr.cyann.reactdemo.Particules.initLabelsReact;
import fr.cyann.reactdemo.ui.swing.RLabel;
import fr.cyann.reactdemo.ui.swing.RTextBox;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author caronyn
 */
public class Text {

	public static void launch() {

		JFrame frame = new JFrame();
		JPanel panel = (JPanel) frame.getContentPane();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		Border empty = new EmptyBorder(10, 10, 10, 10);
		panel.setBorder(empty);

		JLabel title = new JLabel("Text demo");
		title.setFont(new Font("courier", Font.BOLD, 20));

		JLabel subtitle = new JLabel("When you enter text the reactive result is automatically updated");

		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		panel.add(title);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(subtitle);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		panel.add(getAdditionPanel());

		frame.setPreferredSize(new Dimension(800, 250));
		frame.pack();
		frame.setVisible(true);

		initLabelsReact();
		initCursor();
		initAnim();

	}

	private static JPanel getAdditionPanel() {
		JPanel addition = new JPanel();

		RTextBox tb1 = new RTextBox();
		tb1.setPreferredSize(new Dimension(100, 25));
		RTextBox tb2 = new RTextBox();
		tb2.setPreferredSize(new Dimension(100, 25));
		RLabel lb1 = new RLabel();

		Function1<String, Integer> convertToInt = new Function1<String, Integer>() {

			@Override
			public Integer invoke(String value) {
				try {
					return Integer.valueOf(value);
				} catch (Exception ex) {
					return 0;
				}
			}
		};

		Var<String> res = tb1.getRText().map(convertToInt).merge(0, 0, tb2.getRText().map(convertToInt), new Function2<Integer, Integer, String>() {

			@Override
			public String invoke(Integer a, Integer b) {
				int sum = a + b;
				return a + " + " + b + " = " + sum;
			}
		});

		lb1.setText(res);

		addition.add(new JLabel("Addition"));
		addition.add(tb1);
		addition.add(new JLabel("+"));
		addition.add(tb2);
		addition.add(lb1);

		return addition;
	}
}
