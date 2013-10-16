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
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Yann Caron
 */
public class TimeReactTest extends TestCase {

	private final List<Object> results = new ArrayList<Object>();

	public TimeReactTest(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		results.clear();
	}

	private final static void assertMoreOrLess(long time1, long time2, long tolerance) {
		if (Math.abs(time1 - time2) > tolerance) {
			String msg = "Assertion error, range expected [" + (time1 - tolerance) + "-" + (time1 + tolerance) + "], found " + time2;
			throw new AssertionError(msg);
		}
	}

	/**
	 * Test of once method, of class TimeReact.
	 */
	public void testOnce() throws InterruptedException {

		TimeReact.once(250L).subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent event) {
				results.add(event);
			}
		});

		Thread.currentThread().sleep(150L);
		assertEquals(0, results.size());

		Thread.currentThread().sleep(150L);
		assertEquals(1, results.size());
		assertEquals(1, ((TimeEvent) results.get(0)).getIteration());
		assertMoreOrLess(250, ((TimeEvent) results.get(0)).getTimeElapsed(), 1);
		assertMoreOrLess(250, ((TimeEvent) results.get(0)).getTimeElapsedFromStart(), 1);

	}

	/**
	 * Test of every method, of class TimeReact.
	 */
	public void testEvery() throws InterruptedException {

		TimeReact.every(250L).subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent event) {
				results.add(event);
				try {
					Thread.currentThread().sleep(100L);
				} catch (InterruptedException ex) {
					// do nothing
				}
			}
		});

		Thread.currentThread().sleep(150L);
		assertEquals(0, results.size());

		Thread.currentThread().sleep(100L);
		assertEquals(1, results.size());
		assertEquals(1, ((TimeEvent) results.get(0)).getIteration());
		assertMoreOrLess(250, ((TimeEvent) results.get(0)).getTimeElapsed(), 1);
		assertMoreOrLess(250, ((TimeEvent) results.get(0)).getTimeElapsedFromStart(), 1);

		Thread.currentThread().sleep(350L);
		assertEquals(2, results.size());
		assertEquals(2, ((TimeEvent) results.get(0)).getIteration());
		assertMoreOrLess(350, ((TimeEvent) results.get(0)).getTimeElapsed(), 1);
		assertMoreOrLess(600, ((TimeEvent) results.get(0)).getTimeElapsedFromStart(), 1);

		Thread.currentThread().sleep(350L);
		assertEquals(3, results.size());
		assertEquals(3, ((TimeEvent) results.get(0)).getIteration());
		assertMoreOrLess(350, ((TimeEvent) results.get(0)).getTimeElapsed(), 1);
		assertMoreOrLess(950, ((TimeEvent) results.get(0)).getTimeElapsedFromStart(), 1);

	}

	/**
	 * Test of framePerSecond method, of class TimeReact.
	 */
	public void testFramePerSecond() throws InterruptedException {

		TimeReact.framePerSecond(4).subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent event) {
				results.add(event);
				try {
					Thread.currentThread().sleep(100L);
				} catch (InterruptedException ex) {
					// do nothing
				}
			}
		});

		Thread.currentThread().sleep(150L);
		assertEquals(0, results.size());

		Thread.currentThread().sleep(100L);
		assertEquals(1, results.size());
		assertEquals(2, ((TimeEvent) results.get(0)).getIteration());
		assertMoreOrLess(250, ((TimeEvent) results.get(0)).getTimeElapsed(), 1);
		assertMoreOrLess(250, ((TimeEvent) results.get(0)).getTimeElapsedFromStart(), 1);

		Thread.currentThread().sleep(350L);
		assertEquals(2, results.size());
		assertEquals(3, ((TimeEvent) results.get(0)).getIteration());
		assertMoreOrLess(250, ((TimeEvent) results.get(0)).getTimeElapsed(), 1);
		assertMoreOrLess(500, ((TimeEvent) results.get(0)).getTimeElapsedFromStart(), 1);

		Thread.currentThread().sleep(350L);
		assertEquals(3, results.size());
		assertEquals(4, ((TimeEvent) results.get(0)).getIteration());
		assertMoreOrLess(250, ((TimeEvent) results.get(0)).getTimeElapsed(), 1);
		assertMoreOrLess(750, ((TimeEvent) results.get(0)).getTimeElapsedFromStart(), 1);

	}
}
