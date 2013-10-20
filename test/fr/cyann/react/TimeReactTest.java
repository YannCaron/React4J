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
package fr.cyann.react;

import fr.cyann.functor.Procedure1;
import junit.framework.TestCase;

/**
 *
 * @author Yann Caron
 */
public class TimeReactTest extends TestCase {

	public TimeReactTest(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Tools.initResults();
	}
	
	/**
	 * Test of once method, of class TimeReact.
	 */
	public void testOnce() throws InterruptedException {

		Signal<TimeEvent> s = TimeReact.once(250L).subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent event) {
				Tools.results.add(event);
			}
		});

		Thread.currentThread().sleep(150L);
		assertEquals(0, Tools.results.size());

		Thread.currentThread().sleep(150L);
		assertEquals(1, Tools.results.size());
		assertEquals(1, ((TimeEvent) Tools.results.get(0)).getIteration());
		Tools.assertMoreOrLess(250, ((TimeEvent) Tools.results.get(0)).getTimeElapsed(), 5);
		Tools.assertMoreOrLess(250, ((TimeEvent) Tools.results.get(0)).getTimeElapsedFromStart(), 5);

		s.dispose();
	}

	/**
	 * Test of every method, of class TimeReact.
	 */
	public void testEvery() throws InterruptedException {

		Signal<TimeEvent> s = TimeReact.every(250L).subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent event) {
				Tools.results.add(event);
				try {
					System.out.println("TEST EVERY");
					Thread.currentThread().sleep(100L);
				} catch (InterruptedException ex) {
					// do nothing
				}
			}
		});

		Thread.currentThread().sleep(150L);
		assertEquals(0, Tools.results.size());

		Thread.currentThread().sleep(110L);
		assertEquals(1, Tools.results.size());
		assertEquals(1, ((TimeEvent) Tools.results.get(0)).getIteration());
		Tools.assertMoreOrLess(250, ((TimeEvent) Tools.results.get(0)).getTimeElapsed(), 5);
		Tools.assertMoreOrLess(250, ((TimeEvent) Tools.results.get(0)).getTimeElapsedFromStart(), 5);

		Thread.currentThread().sleep(350L);
		assertEquals(2, Tools.results.size());
		assertEquals(2, ((TimeEvent) Tools.results.get(0)).getIteration());
		Tools.assertMoreOrLess(350, ((TimeEvent) Tools.results.get(0)).getTimeElapsed(), 5);
		Tools.assertMoreOrLess(600, ((TimeEvent) Tools.results.get(0)).getTimeElapsedFromStart(), 5);

		Thread.currentThread().sleep(350L);
		assertEquals(3, Tools.results.size());
		assertEquals(3, ((TimeEvent) Tools.results.get(0)).getIteration());
		Tools.assertMoreOrLess(350, ((TimeEvent) Tools.results.get(0)).getTimeElapsed(), 5);
		Tools.assertMoreOrLess(950, ((TimeEvent) Tools.results.get(0)).getTimeElapsedFromStart(), 5);

		s.dispose();
	}

	/**
	 * Test of framePerSecond method, of class TimeReact.
	 */
	public void testFramePerSecond() throws InterruptedException {

		System.out.println(ReactManager.getInstance().getReactCounter().getValue());
		
		Signal<TimeEvent> s = TimeReact.framePerSecond(4).subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent event) {
				Tools.results.add(event);
				try {
					System.out.println("TEST FPS");
					Thread.currentThread().sleep(100L);
				} catch (InterruptedException ex) {
					// do nothing
				}
			}
		});

		Thread.currentThread().sleep(150L);
		assertEquals(0, Tools.results.size());

		Thread.currentThread().sleep(110L);
		assertEquals(1, Tools.results.size());
		assertEquals(1, ((TimeEvent) Tools.results.get(0)).getIteration());
		Tools.assertMoreOrLess(250, ((TimeEvent) Tools.results.get(0)).getTimeElapsed(), 5);
		Tools.assertMoreOrLess(250, ((TimeEvent) Tools.results.get(0)).getTimeElapsedFromStart(), 5);

		Thread.currentThread().sleep(350L);
		assertEquals(2, Tools.results.size());
		assertEquals(2, ((TimeEvent) Tools.results.get(0)).getIteration());
		Tools.assertMoreOrLess(250, ((TimeEvent) Tools.results.get(0)).getTimeElapsed(), 5);
		Tools.assertMoreOrLess(500, ((TimeEvent) Tools.results.get(0)).getTimeElapsedFromStart(), 5);

		Thread.currentThread().sleep(350L);
		assertEquals(3, Tools.results.size());
		assertEquals(3, ((TimeEvent) Tools.results.get(0)).getIteration());
		Tools.assertMoreOrLess(250, ((TimeEvent) Tools.results.get(0)).getTimeElapsed(), 5);
		Tools.assertMoreOrLess(750, ((TimeEvent) Tools.results.get(0)).getTimeElapsedFromStart(), 5);
		
		s.dispose();
	}
}
