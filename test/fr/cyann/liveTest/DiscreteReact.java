package fr.cyann.liveTest;

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
import fr.cyann.functional.Function;
import fr.cyann.functional.Function1;
import fr.cyann.functional.Predicate1;
import fr.cyann.functional.Procedure1;
import fr.cyann.react.Var;
import fr.cyann.react.Operation;

public class DiscreteReact {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

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
				System.out.println(value);
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
				System.out.println(value);
			}
		});

		System.out.println(sum.getValue());
		a.setValue(5);
		b.setValue(7);

	}
}
