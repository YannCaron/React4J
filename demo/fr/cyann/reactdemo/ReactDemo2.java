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
import fr.cyann.functional.Procedure1;
import fr.cyann.react.Constant;
import fr.cyann.react.MouseReact;
import fr.cyann.react.ReactManager;
import fr.cyann.react.Signal;
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
import fr.cyann.reactdemo.ui.StagePanel;
import fr.cyann.reactdemo.ui.Sprite;
import java.util.Random;

/**
 * The ReactDemo1 main class. Creation date: 18 oct. 2013.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class ReactDemo2 {

	private static final RLabel label1 = new RLabel();
	private static final RLabel label2 = new RLabel();
	private static final StagePanel game = new StagePanel();
	private static final Circle cursor = new Circle(25);
	private static final Var<Integer> mouseX = MouseReact.positionX().map(new Function1<Integer, Integer>() {

		@Override
		public Integer invoke(Integer arg1) {
			return arg1 - (cursor.getSize() / 2 - 2);
		}
	}).toVar(0);
	private static final Var<Integer> mouseY = MouseReact.positionY().map(new Function1<Integer, Integer>() {

		@Override
		public Integer invoke(Integer arg1) {
			return arg1 - (cursor.getSize() / 2 + 10);
		}
	}).toVar(0);
	private static final Random rnd = new Random();

	public static void initLabelsReact() {
		// when mouse is pressed say "pressed" otherwise say "released"
		// concatenate message with mouse position and update each time it is necessary

		// declare the mouse reactor
		Var<String> mouseAndTime = MouseReact.button1().map(new Function1<String, Boolean>() {

			@Override
			public String invoke(Boolean arg1) {
				if (arg1) {
					return "button pressed";
				}
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

	private static void addBackgroundElements(int freq, final String fileName, final int velY) {

		// speed lines
		TimeReact.randomly(freq, freq * 5).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer arg1) {
				final Var<Integer> counter = TimeReact.framePerSecond(25).fold(0, new Signal.CountFold<Integer>()).toVar(0);

				final Sprite speedLine = new Sprite(fileName);
				final Var<Integer> x = game.getRWidth().weak().map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1) {
						return (int) (rnd.nextFloat() * arg1);
					}
				});
				final Var<Integer> y = counter.map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer value) {
						return (velY * value);
					}
				});

				speedLine.getOutBottom().subscribe(new Procedure1<Boolean>() {

					@Override
					public void invoke(Boolean arg1) {
						if (arg1) {
							game.removeShape(speedLine);
						}
					}
				});

				speedLine.setX(x);
				speedLine.setY(y);
				game.addShape(speedLine);

			}
		});
	}

	private static void addEnnemies(int freq, final String fileName, final int vel) {
		TimeReact.randomly(freq, freq * 2).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer arg1) {
				final Var<Integer> timer = TimeReact.framePerSecond(25).toVar(0);

				final Sprite ennemy = new Sprite(fileName);

				Signal<Integer> randomVel = TimeReact.every(1000);
				final int ix = (int) (rnd.nextFloat() * game.getRWidth().getValue());
				final int iy = (int) (rnd.nextFloat() * game.getRHeight().getValue()) / 3;
				
				final Var<Integer> velX = randomVel.toVar(vel).map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1) {
						return (rnd.nextInt(vel * 2)) - vel;
					}
				});

				final Var<Integer> velY = randomVel.toVar(vel).map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1) {
						return (rnd.nextInt(vel * 2)) - vel;
					}
				});

				final Var<Integer> x = timer.fold(ix, new Function2<Integer, Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1, Integer arg2) {
						return arg1 + velX.getValue();
					}
				});

				final Var<Integer> y = timer.fold(iy, new Function2<Integer, Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1, Integer arg2) {
						return arg1 + velY.getValue();
					}
				});


				ennemy.setX(x);
				ennemy.setY(y);

				game.addShape(ennemy);
			}
		}).emit(0);
	}

	public static void createGameBehaviours() {
		final Sprite player = new Sprite("/img/player.png");

		final Var<Integer> x = mouseX.map(new Function1<Integer, Integer>() {

			@Override
			public Integer invoke(Integer value) {
				return value - (player.getWidth() / 2);
			}
		});
		final Var<Integer> y = game.getRHeight().map(new Function1<Integer, Integer>() {

			@Override
			public Integer invoke(Integer value) {
				return value - 100;
			}
		});

		// speed lines
		addBackgroundElements(25, "/img/speedLine.png", 50);

		// stars
		addBackgroundElements(100, "/img/starBig.png", 5);
		addBackgroundElements(50, "/img/starSmall.png", 15);

		// shoots
		
		TimeReact.every(50).edge(MouseReact.button1()).subscribe(new Procedure1<Integer>() {

			final int velY = 50;

			@Override
			public void invoke(Integer value) {
				// create reactive counter every 25 fps
				final Var<Integer> counter = TimeReact.framePerSecond(25).fold(0, new Signal.CountFold<Integer>()).toVar(0);

				final Sprite fire1 = new Sprite("/img/laserGreen.png");
				final Sprite fire2 = new Sprite("/img/laserGreen.png");

				final Constant<Integer> fx = x.weak().toConstant();
				final Var<Integer> fx2 = x.weak().toConstant().map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer value) {
						return value + player.getWidth() - 10;
					}
				});

				final Var<Integer> fy = counter.map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer value) {
						return y.getValue() - (velY * value);
					}
				});

				fire1.getOut().subscribe(new Procedure1<Boolean>() {

					@Override
					public void invoke(Boolean arg1) {
						if (arg1) {
							game.removeShape(fire1);
							game.removeShape(fire2);
						}
					}
				});

				fire1.setX(fx);
				fire1.setY(fy);
				fire2.setX(fx2);
				fire2.setY(fy);

				game.addShape(fire1);
				game.addShape(fire2);
			}
		});

		// ennemies
		addEnnemies(2500, "/img/enemyShip.png", 15);

		player.setX(x);
		player.setY(y);

		game.addShape(player);

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
		createGameBehaviours();

	}
}
