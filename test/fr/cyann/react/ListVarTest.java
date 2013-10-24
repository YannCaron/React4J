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
package fr.cyann.react;

import fr.cyann.functional.Function1;
import fr.cyann.functional.Procedure1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author caronyn
 */
public class ListVarTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Tools.initResults();
	}

	public void testElements() throws InterruptedException {

		List<Integer> li = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
		ListVar<Integer> list = ListVar.newInstance(li);

		Var<Integer> elements = list.elementsEvery(TimeReact.every(100)).toVar(0);
		elements.map(new Function1<String, Integer>() {

			@Override
			public String invoke(Integer value) {
				return "Element " + value;
			}
		}).subscribe(new Procedure1<String>() {
			@Override
			public void invoke(String arg1) {
				System.out.println(arg1);
				Tools.results.add(arg1);
			}
		}).disposeOnFinished();

		assertEquals(0, Tools.results.size());
		Thread.currentThread().sleep(1010);
		assertEquals(10, Tools.results.size());
		assertEquals("Element 1", Tools.results.get(0));
		assertEquals("Element 2", Tools.results.get(1));
		assertEquals("Element 3", Tools.results.get(2));
		assertEquals("Element 4", Tools.results.get(3));
		assertEquals("Element 5", Tools.results.get(4));
		assertEquals("Element 6", Tools.results.get(5));
		assertEquals("Element 7", Tools.results.get(6));
		assertEquals("Element 8", Tools.results.get(7));
		assertEquals("Element 1", Tools.results.get(8));
		assertEquals("Element 2", Tools.results.get(9));
		
	}
}
