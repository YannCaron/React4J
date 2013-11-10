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
public class Game {

	private final RLabel label1 = new RLabel();
	private final RLabel label2 = new RLabel();
	private final StagePanel game = new StagePanel();
	private final Circle cursor = new Circle(25);
	private final Var<Integer> mouseX = MouseReact.onMoveX().map(new Function1<Integer, Integer>() {

		@Override
		public Integer invoke(Integer arg1) {
			return arg1 - (cursor.getSize() / 2 - 2);
		}
	}).toVar(0);
	private final Var<Integer> mouseY = MouseReact.onMoveY().map(new Function1<Integer, Integer>() {

		@Override
		public Integer invoke(Integer arg1) {
			return arg1 - (cursor.getSize() / 2 + 10);
		}
	}).toVar(0);
	private final Signal<Integer> fps = TimeReact.framePerSecond(25);
	private final Random rnd = new Random();

	public static void launch() {
		Game demo = new Game();

		demo.initLabelsReact();
		demo.createGameBehaviours();

	}

	// draw panel
	public Game() {

		JFrame frame = new JFrame();
		Container pane = frame.getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

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

	}

	// labels in the bottom of the screen
	public void initLabelsReact() {
		// when mouse is pressed say "pressed" otherwise say "released"
		// concatenate message with mouse position and update each time it is necessary

		// mouse click and position to label 1
		Var<String> mouseInfo = MouseReact.onButton1().map(new Function1<Boolean, String>() {

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
		label1.setText(mouseInfo); // dump to label1

		// counters to label 2
		Var<String> counters = game.getShapeCounter().merge(ReactManager.getInstance().getReactCounter(), new Function2<Integer, Integer, String>() {

			@Override
			public String invoke(Integer arg1, Integer arg2) {
				return "Number of circles: " + arg1 + ", Number of react: " + arg2;
			}
		});
		label2.setText(counters);
	}

	// global game behaviours
	public void createGameBehaviours() {

		// player
		addPlayer();

		// speed lines
		addBackgroundElements(25, "/img/speedLine.png", 50);

		// stars
		addBackgroundElements(50, "/img/starBig.png", 15);
		addBackgroundElements(20, "/img/starSmall.png", 5);

		// ennemies
		addEnnemies(2500, "/img/enemyShip.png", 15, true);
		addEnnemies(5000, "/img/enemyUFO.png", 25, false);

	}

	// level design
	private void addBackgroundElements(int timeout, final String fileName, final int velY) {

		// time randomly between timeout and timeout * 5
		TimeReact.randomly(timeout, timeout * 5).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer arg1) {
				// count every frame
				final Var<Integer> counter = fps.weak().fold(0, new Signal.CountFold<Integer>());

				// create sprite with image
				final Sprite sprite = new Sprite(fileName, Shape.Type.OTHER);
				final float random = rnd.nextFloat();

				// determine x position according time
				final Var<Integer> x = game.getRWidth().weak().map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1) {
						return (int) (random * arg1);
					}
				});

				// determine y position according time
				final Var<Integer> y = counter.map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer value) {
						return (velY * value);
					}
				});

				// if out of the screen
				sprite.getOutBottom().subscribe(new Procedure1<Boolean>() {

					@Override
					public void invoke(Boolean arg1) {
						if (arg1) {
							game.removeShape(sprite);
						}
					}
				});

				sprite.setX(x);
				sprite.setY(y);
				game.addShape(sprite);

			}
		});
	}

	// player
	private void addPlayer() {
		final Sprite player = new Sprite("/img/player.png", Shape.Type.PLAYER);

		// determine x position according mouse
		final Var<Integer> x = mouseX.map(new Function1<Integer, Integer>() {

			@Override
			public Integer invoke(Integer value) {
				return value - (player.getWidth() / 2);
			}
		});

		// determine y position according window dimensions
		final Var<Integer> y = game.getRHeight().map(new Function1<Integer, Integer>() {

			@Override
			public Integer invoke(Integer value) {
				return value - 100;
			}
		});

		// shoots
		Signal shoot = TimeReact.every(50).edge(MouseReact.onButton1()).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer value) {
				addPlayerShoot(x.getValue(), y.getValue());
				addPlayerShoot(x.getValue() + player.getWidth() - 10, y.getValue());
			}
		});

		player.addLink(shoot);
		player.setX(x);
		player.setY(y);

		game.addCollideShape(player);
	}

	// when he shoot
	private void addPlayerShoot(final int ix, final int iy) {

		final int velY = 50;

		// create sprite
		final Sprite fire = new Sprite("/img/laserGreen.png", Shape.Type.SHOOT_PLAYER);

		// count every frame
		Var<Integer> counter = fps.weak().fold(0, new Signal.CountFold<Integer>());

		// determine y position according window dimensions
		final Var<Integer> y = counter.map(new Function1<Integer, Integer>() {

			@Override
			public Integer invoke(Integer value) {
				return iy - (velY * value);
			}
		});

		// when it is out of the screen
		fire.getOut().subscribe(new Procedure1<Boolean>() {

			@Override
			public void invoke(Boolean arg1) {
				if (arg1) {
					game.removeShape(fire);
				}
			}
		});

		// when collision happens
		fire.getCollision().subscribe(new Procedure1<Shape>() {

			@Override
			public void invoke(Shape arg1) {
				if (arg1.getType() == Shape.Type.ENNEMY) {
					game.removeShape(arg1);
					game.addToScore(50);
					addExplosion(arg1.getX() + arg1.getHeight() / 2, arg1.getY() + arg1.getWidth() / 2, "/img/laserGreenShot.png");
				}
			}
		});

		fire.addLink(counter);
		fire.setX(new Var(ix));
		fire.setY(y);

		game.addCollideShape(fire);
	}

	// when any shoot hit something
	private void addExplosion(final int ix, final int iy, final String fileName) {

		for (int i = 0; i < 25; i++) {
			final Sprite exp = new Sprite(fileName, Shape.Type.SHOOT_ENNEMY);

			final double factor = 0.7;
			final int vel = rnd.nextInt(100);
			final double a = rnd.nextDouble() * Math.PI * 2;

			// determine X velocity according time to animate
			final Var<Integer> velX = fps.weak().fold((int) (Math.cos(a) * vel), new Function2<Integer, Integer, Integer>() {

				@Override
				public Integer invoke(Integer arg1, Integer arg2) {
					return (int) (arg1 * factor);
				}
			});

			// determine Y velocity according time to animate
			final Var<Integer> velY = fps.weak().fold((int) (Math.sin(a) * vel), new Function2<Integer, Integer, Integer>() {

				@Override
				public Integer invoke(Integer arg1, Integer arg2) {
					return (int) (arg1 * factor);
				}
			});

			// determine x position according window dimensions
			Var<Integer> x = fps.weak().fold(ix, new Function2<Integer, Integer, Integer>() {

				@Override
				public Integer invoke(Integer arg1, Integer arg2) {
					return arg1 + velX.getValue();
				}
			});

			// determine y position according window dimensions
			Var<Integer> y = fps.weak().fold(iy, new Function2<Integer, Integer, Integer>() {

				@Override
				public Integer invoke(Integer arg1, Integer arg2) {
					return arg1 + velY.getValue();
				}
			});

			// dispose after 400 ms
			Signal dispose = TimeReact.once(rnd.nextInt(400)).subscribe(new Procedure1<Integer>() {

				@Override
				public void invoke(Integer arg1) {
					game.removeShape(exp);
				}
			});

			exp.setX(x);
			exp.setY(y);

			exp.addLink(velX);
			exp.addLink(velY);
			exp.addLink(dispose);
			game.addShape(exp);
		}

	}

	// ennemy
	private void addEnnemies(int freq, final String fileName, final int vel, final boolean shoot) {
		TimeReact.randomly(freq, freq * 2).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer arg1) {
				final Sprite ennemy = new Sprite(fileName, Shape.Type.ENNEMY);

				Signal<Integer> changeVel = TimeReact.every(1000);
				final int ix = (int) (rnd.nextFloat() * (game.getRWidth().getValue() - ennemy.getWidth()));
				final int iy = (int) (rnd.nextFloat() * game.getRHeight().getValue()) / 3;

				final Var<Integer> velX = changeVel.map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1) {
						return (rnd.nextInt(vel * 2)) - vel;
					}
				}).toVar(vel);

				final Var<Integer> velY = changeVel.map(new Function1<Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1) {
						return (rnd.nextInt(vel * 2)) - vel;
					}
				}).toVar(vel);

				final Var<Integer> x = fps.weak().fold(ix, new Function2<Integer, Integer, Integer>() {

					@Override
					public Integer invoke(Integer arg1, Integer arg2) {
						return arg1 + velX.getValue();
					}
				});

				final Var<Integer> y = fps.weak().fold(iy, new Function2<Integer, Integer, Integer>() {

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

				ennemy.addLink(changeVel);
				ennemy.addLink(velX);
				ennemy.addLink(velY);
				ennemy.addLink(s1);
				ennemy.addLink(s2);

				ennemy.setX(x);
				ennemy.setY(y);

				game.addCollideShape(ennemy);
			}
		}).emit(0);
	}

	// when they shoot
	public void addEnnemyShoot(final int ix, final int iy, final int vel) {
		final Sprite shoot = new Sprite("/img/laserRed.png", Shape.Type.SHOOT_ENNEMY);

		// determine x position according window dimensions
		Var<Integer> x = new Var<Integer>(ix);
		final Var<Integer> y = fps.weak().fold(iy, new Function2<Integer, Integer, Integer>() {

			@Override
			public Integer invoke(Integer arg1, Integer arg2) {
				return arg1 + vel;
			}
		});

		// if reach bottom of the screen
		shoot.getOutBottom().subscribe(new Procedure1<Boolean>() {

			@Override
			public void invoke(Boolean arg1) {
				game.removeShape(shoot);
			}
		});

		// if collision happens
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

}
