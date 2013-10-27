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
package fr.cyann.reactdemo.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * The Sprite class.
 * Creation date: 27 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class Sprite extends Shape {

	private Image img;
	private int width, height;

	public Sprite(String name) {
		try {
			img = ImageIO.read(this.getClass().getResource(name));
			width = img.getWidth(null);
			height = img.getHeight(null);
		} catch (IOException ex) {
			Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(img, x.getValue(), y.getValue(), null);
	}
}
