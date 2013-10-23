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

import fr.cyann.functional.Function2;
import fr.cyann.functional.Procedure1;
import static junit.framework.Assert.assertEquals;
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

		Signal<Integer> s = TimeReact.once(250L).subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer event) {
				Tools.results.add(event);
			}
		});

		Thread.currentThread().sleep(150L);
		assertEquals(0, Tools.results.size());

		Thread.currentThread().sleep(150L);
		assertEquals(1, Tools.results.size());
		Tools.assertWithTolerence(250, ((Integer) Tools.results.get(0)), 5);

		s.dispose();
	}

	/**
	 * Test of every method, of class TimeReact.
	 */
	public void testEvery() throws InterruptedException {

		Signal<Integer> s = TimeReact.every(250L).subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer event) {
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
		Tools.assertWithTolerence(250, ((Integer) Tools.results.get(0)), 5);

		Thread.currentThread().sleep(350L);
		assertEquals(2, Tools.results.size());
		Tools.assertWithTolerence(250, ((Integer) Tools.results.get(0)), 5);

		Thread.currentThread().sleep(350L);
		assertEquals(3, Tools.results.size());
		Tools.assertWithTolerence(250, ((Integer) Tools.results.get(0)), 5);

		s.dispose();
	}

	/**
	 * Test of every method, of class TimeReact.
	 */
	public void testRandomly() throws InterruptedException {

		Signal<Integer> s = TimeReact.randomly(50, 150).subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer event) {
				Tools.results.add(event);
				System.out.println("TEST RANDOMLY" + event);
			}
		});

		Thread.currentThread().sleep(500L);

		Integer prev = null;
		for (Object obj : Tools.results) {
			Integer ev = (Integer) obj;
			assertTrue(ev >= 45);
			assertTrue(ev <= 155);
			System.out.println(ev);
		}

		s.dispose();
	}

	/**
	 * Test of framePerSecond method, of class TimeReact.
	 */
	public void testFramePerSecond() throws InterruptedException {

		Signal<Integer> s = TimeReact.framePerSecond(4).subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer arg1) {
				System.out.println(arg1);
				Tools.results.add(arg1);
			}
		});

		Var<Integer> cumul = s.fold(new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer invoke(Integer arg1, Integer arg2) {
				return arg1 + arg2;
			}
		}).toVar(0);

		assertEquals(0, Tools.results.size());
		Thread.currentThread().sleep(1010);

		assertEquals(4, Tools.results.size());
		Tools.assertWithTolerence(250, (Integer) Tools.results.get(0), 5);
		Tools.assertWithTolerence(250, (Integer) Tools.results.get(1), 5);
		Tools.assertWithTolerence(250, (Integer) Tools.results.get(2), 5);
		Tools.assertWithTolerence(250, (Integer) Tools.results.get(3), 5);

		Tools.assertWithTolerence(1000, cumul.getValue(), 5);

		s.dispose();
	}
}
