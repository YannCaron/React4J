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

import fr.cyann.functional.Procedure1;
import junit.framework.TestCase;

/**
 *
 * @author Yann Caron
 */
public class CommonReactTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestTools.initResults();
	}

	/**
	 * Test of newRange method, of class CommonReact.
	 */
	public void testNewRange1() throws InterruptedException {
		CommonReact.newRange(80, 75, -1, TimeReact.every(100)).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer arg1) {
				System.out.println("range " + arg1);
				TestTools.results.add(arg1);
			}
		});

		assertEquals(0, TestTools.results.size());
		Thread.currentThread().sleep(1010);
		assertEquals(10, TestTools.results.size());
		assertEquals(80, TestTools.results.get(0));
		assertEquals(79, TestTools.results.get(1));
		assertEquals(78, TestTools.results.get(2));
		assertEquals(77, TestTools.results.get(3));
		assertEquals(75, TestTools.results.get(5));
		assertEquals(80, TestTools.results.get(6));
		assertEquals(79, TestTools.results.get(7));
	}

	/**
	 * Test of newRange method, of class CommonReact.
	 */
	public void testNewRange2() throws InterruptedException {
		CommonReact.newRange(75, 80, 1, TimeReact.every(100)).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer arg1) {
				System.out.println("range " + arg1);
				TestTools.results.add(arg1);
			}
		});

		assertEquals(0, TestTools.results.size());
		Thread.currentThread().sleep(1010);
		assertEquals(10, TestTools.results.size());
		assertEquals(75, TestTools.results.get(0));
		assertEquals(76, TestTools.results.get(1));
		assertEquals(77, TestTools.results.get(2));
		assertEquals(78, TestTools.results.get(3));
		assertEquals(80, TestTools.results.get(5));
		assertEquals(75, TestTools.results.get(6));
		assertEquals(76, TestTools.results.get(7));
	}

	/**
	 * Test of newRange method, of class CommonReact.
	 */
	public void testNewRandom() throws InterruptedException {
		CommonReact.newRandom(74, 80, TimeReact.every(20)).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer arg1) {
				System.out.println("range " + arg1);
				TestTools.results.add(arg1);
			}
		});

		assertEquals(0, TestTools.results.size());
		Thread.currentThread().sleep(1010);
		assertEquals(50, TestTools.results.size());

		TestTools.assertWithTolerence(77, ((Integer) TestTools.results.get(0)).longValue(), 3);
		TestTools.assertWithTolerence(77, ((Integer) TestTools.results.get(1)).longValue(), 3);
		TestTools.assertWithTolerence(77, ((Integer) TestTools.results.get(2)).longValue(), 3);
		TestTools.assertWithTolerence(77, ((Integer) TestTools.results.get(3)).longValue(), 3);
		TestTools.assertWithTolerence(77, ((Integer) TestTools.results.get(4)).longValue(), 3);
		TestTools.assertWithTolerence(77, ((Integer) TestTools.results.get(5)).longValue(), 3);
		TestTools.assertWithTolerence(77, ((Integer) TestTools.results.get(6)).longValue(), 3);
		TestTools.assertWithTolerence(77, ((Integer) TestTools.results.get(7)).longValue(), 3);
		TestTools.assertWithTolerence(77, ((Integer) TestTools.results.get(8)).longValue(), 3);
		TestTools.assertWithTolerence(77, ((Integer) TestTools.results.get(9)).longValue(), 3);
	}
}
