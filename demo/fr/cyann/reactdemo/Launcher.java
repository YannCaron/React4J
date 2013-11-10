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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author caronyn
 */
public class Launcher {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		new Launcher();

	}

	public Launcher() {

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception ex) {
			// no nimbus laf
		}

		JFrame frame = new JFrame();
		frame.setTitle("React4J - demos");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = (JPanel) frame.getContentPane();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		Border empty = new EmptyBorder(10, 10, 10, 10);
		panel.setBorder(empty);

		JLabel title = new JLabel("Welcome to React4J demos");
		title.setFont(new Font("courier", Font.BOLD, 20));

		JLabel subtitle = new JLabel("Please select demo in menu");

		JButton bText = new JButton("Text reactions");
		JButton bParticle = new JButton("Animated particles");
		JButton bGame = new JButton("Reactive sprite based game");

		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		bText.setAlignmentX(Component.CENTER_ALIGNMENT);
		bParticle.setAlignmentX(Component.CENTER_ALIGNMENT);
		bGame.setAlignmentX(Component.CENTER_ALIGNMENT);

		bText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Text.launch();
			}
		});

		bParticle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Particules.launch();
			}
		});

		bGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Game.launch();
			}
		});

		panel.add(title);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(subtitle);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(bText);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(bParticle);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(bGame);

		frame.pack();
		frame.setVisible(true);
	}

}
