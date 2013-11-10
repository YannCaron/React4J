/*
 * Copyright (C) 2013 caronyn
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
 * You should have received a copy of the GNU Less General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cyann.reactdemo.ui.swing;

import fr.cyann.functional.Procedure1;
import fr.cyann.react.Signal;
import fr.cyann.react.Var;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

/**
 *
 * @author Yann Caron
 */
public class RTextBox extends JTextField {

	private final Signal<String> textChanged;

	public RTextBox() {
		textChanged = new Signal<String>() {
		};

		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				textChanged.emit(RTextBox.super.getText());
			}

		});

	}

	public void setText(Var<String> text) {
		text.subscribe(new Procedure1<String>() {

			@Override
			public void invoke(String value) {
				RTextBox.super.setText(value);
			}
		});
	}

	public Signal<String> getRText() {
		return textChanged;
	}

}
