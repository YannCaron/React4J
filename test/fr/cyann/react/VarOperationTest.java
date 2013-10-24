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
import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author Yann Caron
 */
public class VarOperationTest extends TestCase {

	public VarOperationTest(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Tools.initResults();
	}

	public void testVar() {

		Var<Integer> a = new Var<Integer>(1);
		a.subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer value) {
				Tools.results.add(value);
			}
		});

		assertEquals(0, Tools.results.size());
		assertEquals(Integer.valueOf(1), a.getValue());

		a.setValue(7);
		assertEquals(1, Tools.results.size());
		assertEquals(Integer.valueOf(7), Tools.results.get(0));

	}

	public void testSyncOperation() {

		final Var<Integer> a = new Var<Integer>(1);
		final Var<Integer> b = new Var<Integer>(1);

		Operation<Integer> sum = Operation.syncOperation(new Function<Integer>() {
			@Override
			public Integer invoke() {
				return a.getValue() + b.getValue();
			}
		}, a, b);

		sum.subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer value) {
				Tools.results.add(value);
			}
		});

		assertEquals(0, Tools.results.size());
		assertEquals(Integer.valueOf(2), sum.getValue());

		a.setValue(7);
		assertEquals(0, Tools.results.size());

		a.setValue(7);
		assertEquals(0, Tools.results.size());

		b.setValue(8);
		assertEquals(1, Tools.results.size());

		b.setValue(8);
		assertEquals(1, Tools.results.size());

		a.setValue(7);
		assertEquals(2, Tools.results.size());

	}

	public void testSyncOperationCascade() {

		final Var<Integer> a = new Var<Integer>(1);
		final Var<Integer> b = new Var<Integer>(1);
		final Var<Integer> c = new Var<Integer>(1);
		final Operation<Integer> sum1 = Operation.syncOperation(new Function<Integer>() {
			@Override
			public Integer invoke() {
				return a.getValue() + b.getValue();
			}
		}, a, b);

		Operation<Integer> sum2 = Operation.syncOperation(new Function<Integer>() {
			@Override
			public Integer invoke() {
				return sum1.getValue() + c.getValue();
			}
		}, sum1, c);

		sum2.subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer value) {
				Tools.results.add(value);
			}
		});

		assertEquals(0, Tools.results.size());
		assertEquals(Integer.valueOf(3), sum2.getValue());

		a.setValue(7);
		b.setValue(8);
		c.setValue(2);

		assertEquals(1, Tools.results.size());
		assertEquals(Integer.valueOf(17), Tools.results.get(0));

	}

	public void testMergeOperation() {

		final Var<Integer> a = new Var<Integer>(1);
		final Var<Integer> b = new Var<Integer>(1);

		Operation<Integer> sum = Operation.mergeOperation(new Function<Integer>() {
			@Override
			public Integer invoke() {
				return a.getValue() + b.getValue();
			}
		}, a, b);

		sum.subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer value) {
				Tools.results.add(value);
			}
		});

		assertEquals(0, Tools.results.size());
		assertEquals(Integer.valueOf(2), sum.getValue());

		a.setValue(7);
		assertEquals(1, Tools.results.size());

		a.setValue(7);
		assertEquals(2, Tools.results.size());

		b.setValue(8);
		assertEquals(3, Tools.results.size());

		b.setValue(8);
		assertEquals(4, Tools.results.size());

		a.setValue(7);
		assertEquals(5, Tools.results.size());

	}

	public void testMergeOperationCascade() {

		final Var<Integer> a = new Var<Integer>(1);
		final Var<Integer> b = new Var<Integer>(1);
		final Var<Integer> c = new Var<Integer>(1);
		final Operation<Integer> sum1 = Operation.mergeOperation(new Function<Integer>() {
			@Override
			public Integer invoke() {
				return a.getValue() + b.getValue();
			}
		}, a, b);

		Operation<Integer> sum2 = Operation.mergeOperation(new Function<Integer>() {
			@Override
			public Integer invoke() {
				return sum1.getValue() + c.getValue();
			}
		}, sum1, c);

		sum2.subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer value) {
				Tools.results.add(value);
			}
		});

		assertEquals(0, Tools.results.size());
		assertEquals(Integer.valueOf(3), sum2.getValue());

		a.setValue(7);
		b.setValue(8);
		c.setValue(2);

		assertEquals(3, Tools.results.size());
		assertEquals(Integer.valueOf(9), Tools.results.get(0));
		assertEquals(Integer.valueOf(16), Tools.results.get(1));
		assertEquals(Integer.valueOf(17), Tools.results.get(2));

	}

	public void testMapFilter() {
		final Var<Integer> a = new Var<Integer>(1);
		final Var<Integer> b = new Var<Integer>(2);
		final Operation<Integer> sum = Operation.mergeOperation(new Function<Integer>() {
			@Override
			public Integer invoke() {
				return a.getValue() + b.getValue();
			}
		}, a, b);

		sum.map(new Function1<String, Integer>() {
			@Override
			public String invoke(Integer value) {
				return "sum result = " + value;
			}
		}).subscribe(new Procedure1<String>() {
			@Override
			public void invoke(String value) {
				Tools.results.add(value);
			}
		});

		Operation.mergeOperation(new Function<Integer>() {
			@Override
			public Integer invoke() {
				return sum.getValue() + 1;
			}
		}, sum).filter(new Predicate1<Integer>() {
			@Override
			public boolean invoke(Integer value) {
				return (value <= 10);
			}
		}).map(new Function1<String, Integer>() {
			@Override
			public String invoke(Integer value) {
				return "increment of sum = " + value;
			}
		}).subscribe(new Procedure1<String>() {
			@Override
			public void invoke(String value) {
				Tools.results.add(value);
			}
		});

		a.setValue(5);
		b.setValue(7);

		System.out.println(Tools.results);

		//assertEquals(3, Tools.results.size());
		assertEquals("sum result = 7", Tools.results.get(0));
		assertEquals("increment of sum = 8", Tools.results.get(1));
		assertEquals("sum result = 12", Tools.results.get(2));

	}

	private void assertEquals(List list, Object... elements) {
		if (elements.length != list.size()) {
			throw new AssertionError("List should have the same number of elements expected " + elements.length + ", found " + list.size());
		}
		for (int i = 0; i < list.size(); i++) {
			assertEquals("Element [" + i + "] not equals expected " + elements[i] + ", found " + list.get(i), list.get(i), elements[i]);
		}
	}

	public void testListReact() {

		ListVar<Integer> list = ListVar.newInstance(new ArrayList<Integer>());
		list.subscribe(new Procedure1<List<Integer>>() {
			@Override
			public void invoke(List<Integer> values) {
				for (Integer value : values) {
					Tools.results.add(Integer.valueOf(value));
				}
			}
		});

		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.remove(2);
		list.set(2, 7);

		assertEquals(Tools.results, 1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 4, 1, 2, 7);

	}

	public void testListReactVar() {

		ListVar<Var<Integer>> list = ListVar.newInstance(new ArrayList<Var<Integer>>());
		list.subscribe(new Procedure1<List<Var<Integer>>>() {
			@Override
			public void invoke(List<Var<Integer>> values) {
				for (Var<Integer> value : values) {
					Tools.results.add(value.getValue());
				}
			}
		});

		final Var<Integer> a = new Var<Integer>(1);
		final Var<Integer> b = new Var<Integer>(2);
		Operation<Integer> sum = Operation.mergeOperation(new Function<Integer>() {
			@Override
			public Integer invoke() {
				return a.getValue() + b.getValue();
			}
		}, a, b);

		list.add(a);
		list.add(b);
		list.add(sum);

		a.setValue(7);

		assertEquals(Tools.results, 1, 1, 2, 1, 2, 3, 7, 2, 9, 7, 2, 9);
	}

	public void testListReactVarAddAll() {

		ListVar<Var<Integer>> list = ListVar.newInstance(new ArrayList<Var<Integer>>());
		final Var<Integer> a = new Var<Integer>(1);
		final Var<Integer> b = new Var<Integer>(2);
		Operation<Integer> sum = Operation.mergeOperation(new Function<Integer>() {
			@Override
			public Integer invoke() {
				return a.getValue() + b.getValue();
			}
		}, a, b);

		list.add(a);
		list.add(b);
		list.add(sum);

		ListVar<Var<Integer>> list2 = ListVar.newInstance(new ArrayList<Var<Integer>>());
		list2.subscribe(new Procedure1<List<Var<Integer>>>() {
			@Override
			public void invoke(List<Var<Integer>> values) {
				for (Var<Integer> value : values) {
					Tools.results.add(value.getValue());
				}
			}
		});

		list2.addAll(list);
		a.setValue(7);

		assertEquals(Tools.results, 1, 2, 3, 7, 2, 9, 7, 2, 9);
	}

	public void testSync1() {

		final Var<Integer> a = new Var<Integer>(0);
		final Var<Integer> b = new Var<Integer>(0);

		Signal<Integer> r = a.sync(b, new Signal.KeepFirstFold<Integer, Integer>() {
		});

		r.subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer value) {
				Tools.results.add(value);
				System.out.println(value);
			}
		});

		a.setValue(10);
		a.setValue(20);
		a.setValue(10);
		assertEquals(0, Tools.results.size());

		b.setValue(50);
		assertEquals(1, Tools.results.size());

		b.setValue(50);
		assertEquals(1, Tools.results.size());

		a.setValue(10);
		assertEquals(2, Tools.results.size());

	}

	public void testSync2() {

		final Var<Integer> a = new Var<Integer>(0);
		final Var<Integer> b = new Var<Integer>(0);
		final Var<Integer> c = new Var<Integer>(0);

		Signal<Integer> r = a.sync(b, new Signal.KeepFirstFold<Integer, Integer>() {
		}).sync(c, new Signal.KeepFirstFold<Integer, Integer>() {
		});

		r.subscribe(new Procedure1<Integer>() {
			@Override
			public void invoke(Integer value) {
				Tools.results.add(value);
				System.out.println(value);
			}
		});

		b.setValue(50);
		assertEquals(0, Tools.results.size());
		b.setValue(70);
		assertEquals(0, Tools.results.size());

		a.setValue(10);
		a.setValue(20);
		a.setValue(10);
		assertEquals(0, Tools.results.size());

		c.setValue(80);
		assertEquals(1, Tools.results.size());

		a.setValue(10);
		assertEquals(1, Tools.results.size());

		b.setValue(10);
		assertEquals(1, Tools.results.size());

		c.setValue(10);
		assertEquals(2, Tools.results.size());

	}
}
