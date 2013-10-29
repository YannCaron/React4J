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
import fr.cyann.reactdemo.ui.Shape;
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
		Var<String> mouseAndTime = MouseReact.button1().map(new Function1<Boolean, String>() {

			@Override
			public String invoke(Boolean arg1) {
				if (arg1) {
					return "button pressed";
				}
				return "button released";
			}
		}).toVar("no button yet !").merge(mouseX, new Function2<String, Integer, String>() {

			@Override
			public String invoke(String arg1, Integer arg2) {
				// add mouse x position
				return arg1 + " ( x=" + arg2;
			}
		}).merge(mouseY, new Function2<String, Integer, String>() {

			@Override
			public String invoke(String arg1, Integer arg2) {
				// add mouse y position
				return arg1 + ", y=" + arg2 + ")";
			}
		});

		label1.setText(mouseAndTime);

		Var<String> counters = game.getShapeCounter().merge(ReactManager.getInstance().getReactCounter(), new Function2<Integer, Integer, String>() {

			@Override
			public String invoke(Integer arg1, Integer arg2) {
				return "Number of circles: " + arg1 + ", Number of react: " + arg2;
			}
		});
		label2.setText(counters);
	}

	private static void addExplosion(final int ix, final int iy, final String fileName) {

		for (int i = 0; i < 25; i++) {
			final Var<Integer> timer = TimeReact.framePerSecond(25).toVar(0);
			final Sprite exp = new Sprite(fileName, Shape.Type.SHOOT_ENNEMY);

			final double factor = 0.7;
			final int vel = rnd.nextInt(100);
			final double a = rnd.nextDouble() * Math.PI * 2;

			final Var<Integer> velX = timer.fold((int) (Math.cos(a) * vel), new Function2<Integer, Integer, Integer>() {

				@Override
				public Integer invoke(Integer arg1, Integer arg2) {
					return (int) (arg1 * factor);
				}
			});

			final Var<Integer> velY = timer.fold((int) (Math.sin(a) * vel), new Function2<Integer, Integer, Integer>() {

				@Override
				public Integer invoke(Integer arg1, Integer arg2) {
					return (int) (arg1 * factor);
				}
			});

			Var<Integer> x = timer.fold(ix, new Function2<Integer, Integer, Integer>() {

				@Override
				public Integer invoke(Integer arg1, Integer arg2) {
					return arg1 + velX.getValue();
				}
			});

			Var<Integer> y = timer.fold(iy, new Function2<Integer, Integer, Integer>() {

				@Override
				public Integer invoke(Integer arg1, Integer arg2) {
					return arg1 + velY.getValue();
				}
			});

			Signal dispose = TimeReact.once(rnd.nextInt(400)).subscribe(new Procedure1<Integer>() {

				@Override
				public void invoke(Integer arg1) {
					game.removeShape(exp);
				}
			});

			exp.addLink(velX);
			exp.addLink(velY);
			exp.setX(x);
			exp.setY(y);
			exp.addLink(dispose);
			game.addShape(exp);
		}

	}

	private static void addBackgroundElements(int freq, final String fileName, final int velY) {

		// speed lines
		TimeReact.randomly(freq, freq * 5).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer arg1) {
				final Var<Integer> counter = TimeReact.framePerSecond(25).fold(0, new Signal.CountFold<Integer>()).toVar(0);

				final Sprite speedLine = new Sprite(fileName, Shape.Type.OTHER);
				final float random = rnd.nextFloat();

				final Var<Integer> x = game.getRWidth().weak().map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1) {
						return (int) (random * arg1);
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

	public static void addEnnemyShoot(final int ix, final int iy, final int vel) {
		final Var<Integer> timer = TimeReact.framePerSecond(25).toVar(0);

		final Sprite shoot = new Sprite("/img/laserRed.png", Shape.Type.SHOOT_ENNEMY);

		Var<Integer> x = new Var<Integer>(ix);
		final Var<Integer> y = timer.fold(iy, new Function2<Integer, Integer, Integer>() {

			@Override
			public Integer invoke(Integer arg1, Integer arg2) {
				return arg1 + vel;
			}
		});

		shoot.getOutBottom().subscribe(new Procedure1<Boolean>() {

			@Override
			public void invoke(Boolean arg1) {
				game.removeShape(shoot);
			}
		});

		shoot.getCollision().subscribe(new Procedure1<Shape>() {

			@Override
			public void invoke(Shape arg1) {
				if (arg1.getType() == Shape.Type.PLAYER) {
					game.addToScore(-150);
					addExplosion(arg1.getX() + arg1.getHeight() / 2, arg1.getY() + arg1.getWidth() / 2, "/img/laserRedShot.png");
				}
			}
		});

		shoot.setX(x);
		shoot.setY(y);

		game.addCollideShape(shoot);

	}

	private static void addEnnemies(int freq, final String fileName, final int vel, final boolean shoot) {
		TimeReact.randomly(freq, freq * 2).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer arg1) {
				final Var<Integer> timer = TimeReact.framePerSecond(25).toVar(0);

				final Sprite ennemy = new Sprite(fileName, Shape.Type.ENNEMY);

				Signal<Integer> changeVel = TimeReact.every(1000);
				final int ix = (int) (rnd.nextFloat() * (game.getRWidth().getValue() - ennemy.getWidth()));
				final int iy = (int) (rnd.nextFloat() * game.getRHeight().getValue()) / 3;

				final Var<Integer> velX = changeVel.toVar(vel).map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1) {
						return (rnd.nextInt(vel * 2)) - vel;
					}
				});

				final Var<Integer> velY = changeVel.toVar(vel).map(new Function1<Integer, Integer>() {

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

				Signal s1 = ennemy.getOutLeft().merge(ennemy.getOutRight(), new Function2<Boolean, Boolean, Boolean>() {

					@Override
					public Boolean invoke(Boolean arg1, Boolean arg2) {
						return arg1 || arg2;
					}
				}).subscribe(new Procedure1<Boolean>() {

					@Override
					public void invoke(Boolean arg1) {
						if (arg1) {
							velX.setValue(-velX.getValue());
						}
					}
				});

				Signal s2 = ennemy.getOutTop().merge(ennemy.getOutBottom(), new Function2<Boolean, Boolean, Boolean>() {

					@Override
					public Boolean invoke(Boolean arg1, Boolean arg2) {
						return arg1 || arg2;
					}
				}).subscribe(new Procedure1<Boolean>() {

					@Override
					public void invoke(Boolean arg1) {
						if (arg1) {
							velY.setValue(-velY.getValue());
						}
					}
				});

				ennemy.addLink(velX);
				ennemy.addLink(velY);
				ennemy.addLink(s1);
				ennemy.addLink(s2);

				ennemy.getCollision().subscribe(new Procedure1<Shape>() {

					@Override
					public void invoke(Shape arg1) {
						if (arg1.getType() == Shape.Type.PLAYER) {
							game.addToScore(-250);
						}
					}
				});

				if (shoot) {
					Signal shoot = TimeReact.every(500).subscribe(new Procedure1<Integer>() {

						@Override
						public void invoke(Integer arg1) {
							addEnnemyShoot(x.getValue() + ennemy.getWidth() / 2, y.getValue() + ennemy.getHeight(), 25);
						}
					});
					ennemy.addLink(shoot);
				}

				ennemy.setX(x);
				ennemy.setY(y);

				game.addCollideShape(ennemy);
			}
		}).emit(
			0);
	}

	public static void createGameBehaviours() {
		final Sprite player = new Sprite("/img/player.png", Shape.Type.PLAYER);

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
		addBackgroundElements(50, "/img/starBig.png", 15);
		addBackgroundElements(20, "/img/starSmall.png", 5);

		// shoots
		Signal shoot = TimeReact.every(50).edge(MouseReact.button1()).subscribe(new Procedure1<Integer>() {

			final int velY = 50;

			@Override
			public void invoke(Integer value) {
				// create reactive counter every 25 fps
				final Var<Integer> counter = TimeReact.framePerSecond(25).fold(0, new Signal.CountFold<Integer>()).toVar(0);

				final Sprite fire1 = new Sprite("/img/laserGreen.png", Shape.Type.SHOOT_PLAYER);
				final Sprite fire2 = new Sprite("/img/laserGreen.png", Shape.Type.SHOOT_PLAYER);

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

				fire1.getCollision().subscribe(new Procedure1<Shape>() {

					@Override
					public void invoke(Shape arg1) {
						if (arg1.getType() == Shape.Type.ENNEMY) {
							game.removeShape(arg1);
							game.addToScore(50);
							addExplosion(arg1.getX() + arg1.getHeight() / 2, arg1.getY() + arg1.getWidth() / 2, "/img/laserGreenShot.png");
						}
					}
				});

				fire2.getCollision().subscribe(new Procedure1<Shape>() {

					@Override
					public void invoke(Shape arg1) {
						if (arg1.getType() == Shape.Type.ENNEMY) {
							game.removeShape(arg1);
							game.addToScore(50);
							addExplosion(arg1.getX() + arg1.getHeight() / 2, arg1.getY() + arg1.getWidth() / 2, "/img/laserGreenShot.png");
						}
					}
				});

				fire1.setX(fx);
				fire1.setY(fy);
				fire2.setX(fx2);
				fire2.setY(fy);

				game.addCollideShape(fire1);
				game.addCollideShape(fire2);
			}
		});

		// ennemies
		addEnnemies(2500, "/img/enemyShip.png", 15, true);
		addEnnemies(5000, "/img/enemyUFO.png", 25, false);

		player.addLink(shoot);
		player.setX(x);
		player.setY(y);

		game.addCollideShape(player);

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
