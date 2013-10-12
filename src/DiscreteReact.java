
import fr.cyann.functor.Function;
import fr.cyann.functor.Function1;
import fr.cyann.functor.Predicate1;
import fr.cyann.functor.Procedure1;
import fr.cyann.react.Var;
import fr.cyann.react.VarReact;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author caronyn
 */
public class DiscreteReact {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		final Var<Integer> a = new Var<Integer>(1);
		final Var<Integer> b = new Var<Integer>(2);
		final VarReact<Integer> sum = new VarReact<Integer>(new Function<Integer>() {

			@Override
			public Integer invoke() {
				return a.getValue() + b.getValue();
			}
		}, a, b);

		sum.map(new Function1<String, Integer> () {

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

		new VarReact<Integer>(new Function<Integer>() {

			@Override
			public Integer invoke() {
				return sum.getValue() + 1;
			}
		}, sum).filter(new Predicate1<Integer>() {

			@Override
			public boolean invoke(Integer value) {
				return (value <= 10);
			}
		}).map(new Function1<String, Integer> () {

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
