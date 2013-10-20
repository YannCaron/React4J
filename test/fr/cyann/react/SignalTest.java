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

import fr.cyann.base.Tuple;
import fr.cyann.functor.Function1;
import fr.cyann.functor.Predicate1;
import fr.cyann.functor.Procedure1;
import junit.framework.TestCase;

/**
 *
 * @author Yann Caron
 */
public class SignalTest extends TestCase {

	public SignalTest(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Tools.initResults();
	}

	public void testDispose() {

		Signal<MouseEvent> s = MouseReact.press(1);

		assertEquals(Integer.valueOf(1), ReactManager.getInstance().getReactCounter().getValue());

		s.dispose();

		assertEquals(Integer.valueOf(0), ReactManager.getInstance().getReactCounter().getValue());

	}

	public void testDisposeMerge() {

		Signal s = MouseReact.press(1).merge(TimeReact.every(100));

		assertEquals(Integer.valueOf(3), ReactManager.getInstance().getReactCounter().getValue());

		s.dispose();

		assertEquals(Integer.valueOf(0), ReactManager.getInstance().getReactCounter().getValue());

	}

	public void testDisposeFilter() {

		Signal s = MouseReact.press(1).filter(new Predicate1<MouseEvent>() {

			@Override
			public boolean invoke(MouseEvent arg) {
				return false;
			}
		});

		assertEquals(Integer.valueOf(2), ReactManager.getInstance().getReactCounter().getValue());

		s.dispose();

		assertEquals(Integer.valueOf(0), ReactManager.getInstance().getReactCounter().getValue());

	}

	public void testDisposeMap() {

		Signal s = MouseReact.press(1).map(new Function1<String, MouseEvent>() {

			@Override
			public String invoke(MouseEvent arg1) {
				return arg1.toString();
			}
		});

		assertEquals(Integer.valueOf(2), ReactManager.getInstance().getReactCounter().getValue());

		s.dispose();

		assertEquals(Integer.valueOf(0), ReactManager.getInstance().getReactCounter().getValue());

	}

	public void testSmooth() throws InterruptedException {

		Procedure1<Tuple<TimeEvent, TimeEvent>> p = new Procedure1<Tuple<TimeEvent, TimeEvent>>() {

			@Override
			public void invoke(Tuple<TimeEvent, TimeEvent> event) {
				Tools.results.add(event);
				System.out.println("RUN");
			}
		};
		
		Signal s = TimeReact.every(50).merge(TimeReact.every(100)).subscribe(p);

		assertEquals(0, Tools.results.size());
		Thread.currentThread().sleep(410L);
		assertEquals(12, Tools.results.size());
		
		// reset
		s.unSubscribe(p);
		s.reset();
		Tools.initResults();
		assertEquals(0, Tools.results.size());
		Thread.currentThread().sleep(175L);
		assertEquals(0, Tools.results.size());

		s.smooth(20).subscribe(p);
		
		Thread.currentThread().sleep(410);
		assertEquals(8, Tools.results.size());

		System.out.println("DISPOSED");
		s.dispose();
		Thread.currentThread().sleep(510);

	}
}
