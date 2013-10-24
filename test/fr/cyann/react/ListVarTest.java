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

		Var<Integer> elements = list.elements(TimeReact.every(100)).toVar(0);
		elements.subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer arg1) {
				System.out.println("Element " + arg1);
			}
		}).disposeOnFinished();

		Thread.currentThread().sleep(1000);
	}
}
