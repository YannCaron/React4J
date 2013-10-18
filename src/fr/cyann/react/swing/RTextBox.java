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
package fr.cyann.react.swing;

import fr.cyann.functor.Procedure1;
import fr.cyann.react.Signal;
import fr.cyann.react.Var;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;

/**
 *
 * @author caronyn
 */
public class RTextBox extends JTextField {

	private final Var<String> react;
	private final Signal<String> textChanged;

	public RTextBox() {
		react = new Var<String>("");
		textChanged = new Signal<String>() {

			@Override
			public String getValue() {
				return RTextBox.super.getText();
			}
		};

		react.subscribe(new Procedure1<String>() {

			@Override
			public void invoke(String value) {
				RTextBox.super.setText(value);
			}
		});

		this.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				textChanged.emit(RTextBox.super.getText());
			}
		});
	}

	public void setText(Var<String> value) {
		value.register(react);
	}

	public Var<String> getRText() {
		return react;
	}

}
