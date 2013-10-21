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

import fr.cyann.functional.Tuple2;
import fr.cyann.functional.Function1;
import fr.cyann.functional.Function2;
import fr.cyann.functional.Predicate1;
import fr.cyann.functional.Procedure1;
import static junit.framework.Assert.assertEquals;
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
	/*
	public void testThen() throws InterruptedException {
	
	final Var<Integer> a = new Var<Integer>(0);
	final Var<Integer> b = new Var<Integer>(0);
	
	Signal<TimeEvent> c = a.then(new Function1<Signal<TimeEvent>, Integer>() {
	
	@Override
	public Signal<TimeEvent> invoke(Integer arg1) {
	return TimeReact.once(150);
	}
	});
	
	c.subscribe(new Procedure1<TimeEvent>() {
	
	@Override
	public void invoke(TimeEvent value) {
	System.out.println(value);
	}
	});
	
	b.setValue(1);
	
	a.setValue(10);
	a.setValue(10);
	
	b.setValue(2);
	b.setValue(2);
	
	Thread.currentThread().sleep(500);
	
	}*/

	public void testWeak() throws InterruptedException {

		Signal<TimeEvent> t1 = TimeReact.every(50);
		Signal<TimeEvent> t2 = TimeReact.every(30);

		t1.subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent arg1) {
				Tools.results.add("T1");
			}
		});

		t2.subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent arg1) {
				Tools.results.add("T2");
			}
		});

		t1.merge(t2.weak()).dispose();

		Thread.currentThread().sleep(170);
		assertNotSame(0, Tools.results.size());
		for (Object item : Tools.results) {
			assertEquals("T2", item.toString());
		}

	}

	public void testFoldDiscreet() throws Exception {

		Var<Integer> a = new Var<Integer>(0);
		a.fold(new Function2<Integer, Integer, Integer>() {

			@Override
			public Integer invoke(Integer value1, Integer value2) {
				return value1 + value2;
			}
		}).subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer value) {
				System.out.println("FOLD " + value);
			}
		});

		a.setValue(1);
		a.setValue(2);
		a.setValue(3);
		a.setValue(4);
	}

	public void testFoldContinuous() throws Exception {

		Signal<TimeEvent> s = TimeReact.every(50).fold(new Function2<TimeEvent, TimeEvent, TimeEvent>() {

			@Override
			public TimeEvent invoke(TimeEvent arg1, TimeEvent arg2) {
				System.out.println("ARG1 " + arg1);
				System.out.println("ARG2 " + arg2);
				return new TimeEvent();
			}
		});
		
		Thread.sleep(400);

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

		Procedure1<Tuple2<TimeEvent, TimeEvent>> p = new Procedure1<Tuple2<TimeEvent, TimeEvent>>() {

			@Override
			public void invoke(Tuple2<TimeEvent, TimeEvent> event) {
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

	public void testDisposeOnFinish() throws InterruptedException {

		Signal s = TimeReact.every(50).until(TimeReact.once(130)).subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent value) {
				System.out.println("RUN");
				Tools.results.add(value);
			}
		}).subscribeFinish(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent value) {
				System.out.println("FINISH");
				Tools.results.add(value);
			}
		}).disposeOnFinished();

		assertEquals(0, Tools.results.size());
		Thread.currentThread().sleep(200);
		assertEquals(4, Tools.results.size());

		Thread.currentThread().sleep(200);
		assertEquals(4, Tools.results.size());


	}
}
