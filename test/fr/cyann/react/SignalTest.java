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

import fr.cyann.functional.Function;
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

	 Signal<Integer> c = a.then(new Function1<Signal<Integer>, Integer>() {

	 @Override
	 public Signal<Integer> invoke(Integer arg1) {
	 return TimeReact.once(150);
	 }
	 });

	 c.subscribe(new Procedure1<Integer>() {

	 @Override
	 public void invoke(Integer value) {
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

		Var<Integer> t1 = TimeReact.every(50).toVar(0);
		Var<Integer> t2 = TimeReact.every(30).toVar(0);
		t1.subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer arg1) {
				Tools.results.add("T1");
			}
		});

		t2.subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer arg1) {
				Tools.results.add("T2");
			}
		});

		t1.merge(t2.weak().toVar(0), new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer invoke(Integer arg1, Integer arg2) {
				return arg1 + 1;
			}
		}).dispose();

		Thread.currentThread().sleep(170);
		assertNotSame(0, Tools.results.size());
		for (Object item : Tools.results) {
			assertEquals("T2", item.toString());
		}

		t1.dispose();
		t2.dispose();
	}

	public void testFoldDiscreet() throws Exception {

		Var<Integer> a = new Var<Integer>(0);
		Signal s = a.fold(0, new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer invoke(Integer value1, Integer value2) {
				return value1 + value2;
			}
		}).subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer value) {
				Tools.results.add(value);
			}
		});

		a.setValue(1);
		a.setValue(2);
		a.setValue(3);
		a.setValue(4);

		assertEquals(4, Tools.results.size());
		assertEquals(1, Tools.results.get(0));
		assertEquals(3, Tools.results.get(1));
		assertEquals(6, Tools.results.get(2));
		assertEquals(10, Tools.results.get(3));

		a.dispose();
		s.dispose();

	}

	public void testFoldContinuous() throws Exception {

		Var<Integer> a = TimeReact.every(50).fold(0, new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer invoke(Integer arg1, Integer arg2) {
				return arg1 + arg2;
			}
		}).toVar(0);

		Var<Integer> b = TimeReact.every(50).fold(0, new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer invoke(Integer arg1, Integer arg2) {
				return arg1 + 1;
			}
		}).toVar(0);

		Tools.assertWithTolerence(a.getValue(), 0, 5);
		assertEquals(b.getValue(), 0, 5);
		Thread.sleep(410);
		Tools.assertWithTolerence(a.getValue(), 400, 5);
		assertEquals(b.getValue(), 8, 5);

		a.dispose();
		b.dispose();

	}

	public void testFoldContinuousCount() throws Exception {

		Signal s = TimeReact.every(50).fold(0, Signal.SUM_FOLD).subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer value) {
				Tools.results.add(value);
			}
		});

		Thread.sleep(380);
		assertEquals(7, Tools.results.size());

		int i = 0;
		for (Object o : Tools.results) {
			int v = (Integer) o;
			assertEquals(i + 1, v);
			i++;
		}

		s.dispose();
	}

	public void testFoldAverage() throws Exception {

		Var<Integer> a = new Var<Integer>(0);
		Var<Integer> average = a.fold(new Signal.AverageFold<Integer>() {

			@Override
			public Integer convert(Float value) {
				return value.intValue();
			}
		}).toVar(0);

		average.subscribe(new Procedure1<Integer>() {

			@Override
			public void invoke(Integer arg1) {
				System.out.println("Average=" + arg1);
				Tools.results.add(arg1);
			}
		});

		a.setValue(10);
		a.setValue(20);
		a.setValue(30);
		a.setValue(40);

		assertEquals(average, average);

	}

	public void testDisposeCounter() {

		Signal<Integer> s = MouseReact.press();
		assertEquals(1, ReactManager.getInstance().getReactCounter().getValue().get());

		s.dispose();

		assertEquals(0, ReactManager.getInstance().getReactCounter().getValue().get());

	}

	public void testDisposeMerge() {

		Signal<Integer> s = MouseReact.press().toVar(0).merge(TimeReact.every(100).toVar(0), new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer invoke(Integer arg1, Integer arg2) {
				return 0;
			}
		});

		assertEquals(5, ReactManager.getInstance().getReactCounter().getValue().get());

		s.dispose();

		assertEquals(0, ReactManager.getInstance().getReactCounter().getValue().get());

	}

	public void testDisposeFilter() {

		Signal s = MouseReact.press().filter(new Predicate1<Integer>() {
			@Override
			public boolean invoke(Integer arg) {
				return false;
			}
		});

		assertEquals(2, ReactManager.getInstance().getReactCounter().getValue().get());

		s.dispose();

		assertEquals(0, ReactManager.getInstance().getReactCounter().getValue().get());

	}

	public void testDisposeMap() {

		Signal s = MouseReact.press().map(new Function1<String, Integer>() {
			@Override
			public String invoke(Integer arg1) {
				return arg1.toString();
			}
		});

		assertEquals(2, ReactManager.getInstance().getReactCounter().getValue().get());

		s.dispose();

		assertEquals(0, ReactManager.getInstance().getReactCounter().getValue().get());

	}

	public void testDisposeOnFinish() throws InterruptedException {

		Signal s = TimeReact.every(50).until(TimeReact.once(130)).subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer value) {
				System.out.println("RUN");
				Tools.results.add(value);
			}
		}).subscribeFinish(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer value) {
				System.out.println("FINISH");
				Tools.results.add(value);
			}
		}).disposeOnFinished();

		assertEquals(0, Tools.results.size());
		Thread.currentThread().sleep(200);
		assertEquals(4, Tools.results.size());

		Thread.currentThread().sleep(200);
		assertEquals(4, Tools.results.size());

		s.dispose();
	}

	public void testOperationDisposeWhen() throws InterruptedException {

		final Var<Integer> a = new Var<Integer>(0);
		final Var<Integer> b = new Var<Integer>(0);

		Operation<Integer> sum = Operation.mergeOperation(new Function<Integer>() {
			@Override
			public Integer invoke() {
				return a.getValue() + b.getValue();
			}
		}, a.weak().toVar(0), b.weak().toVar(0));

		sum.dispose();

		a.dispose();
		b.dispose();
	}
}
