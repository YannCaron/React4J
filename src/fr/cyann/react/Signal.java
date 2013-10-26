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

import fr.cyann.functional.Function1;
import fr.cyann.functional.Predicate1;
import fr.cyann.functional.Procedure1;
import java.util.ConcurrentModificationException;
import fr.cyann.base.Package;
import fr.cyann.functional.Function2;
import fr.cyann.functional.Predicate2;
import fr.cyann.functional.Tuple;
import java.util.ArrayList;
import java.util.List;

/**
 * The Signal class. Define the base class of all discrete and continous react.
 * Creation date: 11 oct. 2013.
 *
 * @author Yann Caron
 * @version v0.1
 */
public abstract class Signal<V> {

	protected final React<V> react;
	protected boolean running = true;
	private boolean enabled = true;
	private Signal parent;
	private boolean disposed = false;
	protected final List<Signal> links;

	// inner class
	@Package
	static class ConcretSignal<V> extends Signal<V> {

		public ConcretSignal(Signal parent) {
			super(parent);
		}
	}
	public static final Function1<String, Object> TOSTRING_MAP = new Function1<String, Object>() {

		@Override
		public String invoke(Object arg1) {
			return "" + arg1;
		}
	};

	public static class AlwaysFilter<V> implements Predicate1<V> {

		@Override
		public boolean invoke(V arg) {
			return true;
		}
	}

	public static class NeverFilter<V> implements Predicate1<V> {

		@Override
		public boolean invoke(V arg) {
			return true;
		}
	}

	public static class AlwaysFilter2<V> implements Predicate2<V, V> {

		@Override
		public boolean invoke(V arg1, V arg2) {
			return true;
		}
	}

	public static class NeverFilter2<V> implements Predicate2<V, V> {

		@Override
		public boolean invoke(V arg1, V arg2) {
			return false;
		}
	}

	public static class DropRepeatFilter<V> implements Predicate2<V, V> {

		@Override
		public boolean invoke(V arg1, V arg2) {
			return arg1 != arg2;
		}
	}

	public static class RaiseRepeatFilter<V> implements Predicate2<V, V> {

		@Override
		public boolean invoke(V arg1, V arg2) {
			return arg1 == arg2;
		}
	}

	public static class KeepLeftFold<V, W> implements Function2<V, V, W> {

		@Override
		public V invoke(V arg1, W arg2) {
			return arg1;
		}
	}

	public static class KeepRightFold<V, W> implements Function2<W, V, W> {

		@Override
		public W invoke(V arg1, W arg2) {
			return arg2;
		}
	}

	public static class TupleFold<V, W> implements Function2<Tuple<V, W>, V, W> {

		@Override
		public Tuple<V, W> invoke(V arg1, W arg2) {
			return new Tuple<V, W>(arg1, arg2);
		}
	}

	public static class CountFold<V extends Number> implements Function2<Integer, Integer, V> {

		@Override
		public Integer invoke(Integer arg1, V arg2) {
			return arg1 + 1;
		}
	}

	public static class SumFold<V extends Number> implements Function2<Float, Float, V> {

		@Override
		public Float invoke(Float arg1, V arg2) {
			return arg1.floatValue() + arg2.floatValue();
		}
	}

	public static class SumFoldInteger implements Function2<Integer, Integer, Integer> {

		@Override
		public Integer invoke(Integer arg1, Integer arg2) {
			return arg1 + arg2;
		}
	}

	public static class AverageFold<V extends Number> implements Function2<Float, Float, V> {

		private float i;

		public AverageFold() {
			this.i = 1;
		}

		@Override
		public Float invoke(Float arg1, V arg2) {
			float sum = arg1 * i;
			i++;
			return (sum + arg2.floatValue()) / i;
		}
	}
	// constructor

	@Package
	Signal(boolean count) {
		if (count) {
			ReactManager.getInstance().incrementCounter();
			disposed = false;
		}

		react = new React<V>();
		links = new ArrayList<Signal>();
	}

	/**
	 * Default constructor.
	 */
	public Signal() {
		this(true);
	}

	@Package
	Signal(Signal parent) {
		this(true);
		this.parent = parent;
	}

	// property
	@Package
	boolean isAutoStart() {
		return enabled;
	}

	@Package
	void enable() {
		this.enabled = true;
	}

	@Package
	void disable() {
		this.enabled = false;
	}

	public boolean isRunning() {
		return running;
	}

	@Package
	void setParent(Signal parent) {
		this.parent = parent;
	}

	// method
	public void start() {
		if (enabled) {
			running = true;
		}
		if (parent != null) {
			parent.start();
		}
		int size = links.size();
		for (int i = 0; i < size; i++) {
			links.get(i).start();
		}
	}

	public void stop() {
		running = false;

		if (parent != null) {
			parent.stop();
		}
		int size = links.size();
		for (int i = 0; i < size; i++) {
			links.get(i).stop();
		}
	}

	public void reset() {
		stop();
		start();
	}

	// public abstract V getValue();
	/**
	 * Command react to emit a signal.<br>
	 * Should be overrided to add automatic behaviours like emit counter time
	 * management etc.
	 */
	public void emit(V value) {
		try {
			react.emit(value);
		} catch (ConcurrentModificationException ex) {
			// avoid concurrent exception
		}
	}

	/**
	 * Register listener first order function to react. Function will be called
	 * when event is raised.
	 *
	 * @param subscriber function to be called in case of event.
	 * @return return this.
	 */
	public Signal<V> subscribe(Procedure1<V> subscriber) {
		react.subscribe(subscriber);
		start();
		return this;
	}

	@Package
	Signal<V> subscribeDiscret(Procedure1<V> subscriber) {
		react.subscribe(subscriber);
		return this;
	}

	public Signal<V> subscribe(final Signal<V> signal) {
		react.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emit(value);
			}
		});

		start();

		return this;
	}

	public Signal<V> unSubscribe(Procedure1<V> subscriber) {
		react.unSubscribe(subscriber);
		return this;
	}

	// <editor-fold defaultstate="collapsed" desc="high order functions">
	/**
	 * Filter the event according a criteria.
	 *
	 * @param function predicate to filter. If result is true, event pass else it
	 * is blocked.
	 * @return the new filtered signal.
	 */
	public final Signal<V> filter(final Predicate1<V> function) {
		final Signal<V> signal = new ConcretSignal<V>(this);

		this.subscribeDiscret(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				if (signal.isRunning() && function.invoke(value)) {
					signal.emit(value);
				}
			}
		});

		return signal;
	}

	/**
	 * Filter the event according a criteria on the value and it's previous.<br>
	 * <b>Finish signal</b> is not filtered.
	 *
	 * @param initialize the first value to compare signal value.
	 * @param filter predicate to filter. If result is true, event pass else it
	 * is blocked.
	 * @return the new filtered signal.
	 */
	public final Signal<V> filterFold(V initialize, final Predicate2<V, V> filter) {
		return filterFold(initialize, filter, initialize, new AlwaysFilter2<V>());
	}

	/**
	 * Filter the event according a criteria on the value and it's previous.
	 *
	 * @param initEmit  the first value to compare signal value.
	 * @param fEmit predicate to filter. If result is true, event pass else it
	 * is blocked.
	 * @param initFinish  the first value to compare finish signal value.
	 * @param fFinish predicate to filter finish. If result is true, event pass else it
	 * is blocked.
	 * @return the new filtered signal.
	 */
	public final Signal<V> filterFold(final V initEmit, final Predicate2<V, V> fEmit, final V initFinish, final Predicate2<V, V> fFinish) {
		final Signal<V> signal = new ConcretSignal<V>(this);

		this.subscribeDiscret(new Procedure1<V>() {

			private V previous = initEmit;

			@Override
			public void invoke(V value) {
				if (signal.isRunning() && fEmit.invoke(previous, value)) {
					signal.emit(value);
				}
				previous = value;
			}
		});

		return signal;
	}

	/**
	 * Transform the react's value according the supplied function.
	 *
	 * @param <R> The transformed react's value data type.
	 * @param function the function to transform data.
	 * @return the new transformed react.
	 */
	public <R> Signal<R> map(final Function1<R, V> function) {
		final Signal<R> signal = new ConcretSignal<R>(this);

		this.subscribeDiscret(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				if (signal.isRunning()) {
					signal.emit(function.invoke(value));
				}
			}
		});

		return signal;
	}

	/**
	 * Like map, but return value is a signal.
	 * @param <R> The transformed react's value data type.
	 * @param init the initial value
	 * @param function the function to transform data.
	 * @return the signal react.
	 */
	public <R> Signal<R> flatMap(final V init, final Function1<Signal<R>, V> function) {
		final Signal<R> signal = new ConcretSignal<R>(this);

		final Procedure1<R> p = new Procedure1<R>() {

			@Override
			public void invoke(R arg1) {
				signal.emit(arg1);
			}
		};

		this.subscribeDiscret(new Procedure1<V>() {

			Signal<R> current;

			{
				current = function.invoke(init);
				if (current != null) {
					current.subscribe(p);
				}
			}

			@Override
			public void invoke(V arg1) {
				if (current != null) {
					current.unSubscribe(p);
					//current.dispose();
				}

				current = function.invoke(arg1);

				if (current != null) {
					current.subscribe(p);
				}
			}
		});

		return signal;
	}

	public Signal<V> feedBackLoop(final Function1<Signal, V> function) {
		final Signal<V> signal = new ConcretSignal<V>(this);

		final Tools.SwitchProcedure<V> p = new Tools.SwitchProcedure<V>(null, signal, null) {

			@Override
			public void invokeF(Signal<V> signal, Signal right, V value) {
				signal.emit(value);
				if (right != null) {
					right.unSubscribe(this);
				}
			}
		};

		this.subscribeDiscret(new Procedure1<V>() {

			Signal current;

			@Override
			public void invoke(V arg1) {
				p.setValue(arg1);

				if (current != null) {
					current.unSubscribe(p);
					//current.dispose();
				}

				current = function.invoke(arg1);

				if (current != null) {
					p.setRight(current);
					current.subscribe(p);
				}
			}
		});

		return signal;
	}

	/**
	 * Fold current value with previous one.
	 * <b>Finish signal</b> is transformed with the same functor.
	 *
	 * @param function specify the action to perform between previous value and
	 * current.
	 * @return the new value.
	 */
	public Signal<V> fold(final Function2<V, V, V> function) {
		final Signal<V> signal = new ConcretSignal<V>(this);

		this.subscribeDiscret(new Procedure1<V>() {

			private V previous = null;

			@Override
			public void invoke(V value) {
				if (previous == null) {
					previous = value;
				} else {
					previous = function.invoke(previous, value);
				}
				signal.emit(previous);
			}
		});

		return signal;
	}

	/**
	 * Fold current value with previous one.
	 * <b>Finish signal</b> is transformed with the same functor.
	 *
	 * @param function specify the action to perform between previous value and
	 * current.
	 * @param initialize the first value to fold.
	 * @return the new value.
	 */
	public <W> Var<W> fold(final W initialize, final Function2<W, W, V> function) {
		final Var<W> signal = new Var<W>(initialize, this);

		this.subscribeDiscret(new Procedure1<V>() {

			private W previous = initialize;

			@Override
			public void invoke(V value) {
				previous = function.invoke(previous, value);
				signal.emit(previous);
			}
		});

		return signal;
	}

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="signal operations">
	/**
	@see Signal#merge(java.lang.Object, java.lang.Object, fr.cyann.react.Signal, fr.cyann.functional.Function2) 
	 */
	public <W> Var<Tuple<V, W>> merge(final V init, final W initMerge, final Signal<W> right) {
		return merge(init, initMerge, right, new TupleFold<V, W>());
	}

	/**
	Associative method.<br>
	Merge two signal together. If any signal emit, the resulting signal will
	emit. Consider this operation like an <b>or</b> boolean operation.
	
	@param init the initial value of signal.
	@param initMerge the initial value of merge signal.
	@param <X> Type of the resulting var.
	@param <W> Type of the merged var.
	@param right the var to merge with.
	@param mapfold the map fold transformation function.<br>It's goal is to merge the two values together and returned in the desired type.
	@return the new merged signal.
	 */
	public <X, W> Var<X> merge(final V init, final W initMerge, final Signal<W> right, final Function2<X, V, W> mapfold) {
		links.add(right);
		final Var<X> signal = new Var(mapfold.invoke(init, initMerge), this);

		Tools.MergeProcedure1<V, W> p1 = new Tools.MergeProcedure1<V, W>() {

			@Override
			public V initialize() {
				return init;
			}

			@Override
			public void invokeF(V value, W other) {
				signal.emit(mapfold.invoke(value, other));
			}
		};
		Tools.MergeProcedure1<W, V> p2 = new Tools.MergeProcedure1<W, V>() {

			@Override
			public W initialize() {
				return initMerge;
			}

			@Override
			public void invokeF(W value, V other) {
				signal.emit(mapfold.invoke(other, value));
			}
		};
		p1.associate(p2);
		p2.associate(p1);

		this.subscribe(p1);
		right.subscribe(p2);

		return signal;
	}

	/**
	@see Signal#sync(java.lang.Object, java.lang.Object, fr.cyann.react.Signal, fr.cyann.functional.Function2) 
	 */
	public <W> Var<Tuple<V, W>> sync(final V init, final W initMerge, final Signal<W> right) {
		return sync(init, initMerge, right, new TupleFold<V, W>());
	}

	/**
	Associative method.<br>
	Synchronize signal together. The both signals should be before resulting signal will be emited.<br>
	Consider this operation like an <b>and</b> boolean operation.
	
	@param init the initial value of signal.
	@param initMerge the initial value of merge signal.
	@param <X> Type of the resulting var.
	@param <W> Type of the sync var.
	@param right the var to merge with.
	@param mapfold the map fold transformation function.<br>It's goal is to merge the two values together and returned in the desired type.
	@return the new merged signal.
	 */
	public final <X, W> Var<X> sync(final V init, final W initSync, final Signal<W> right, final Function2<X, V, W> mapfold) {
		links.add(right);
		final Var<X> signal = new Var(mapfold.invoke(init, initSync), this);

		Tools.SyncProcedure1<V, W> p1 = new Tools.SyncProcedure1<V, W>() {

			@Override
			public V initialize() {
				return init;
			}

			@Override
			public void invokeF(V value, W other) {
				signal.emit(mapfold.invoke(value, other));
			}
		};
		Tools.SyncProcedure1<W, V> p2 = new Tools.SyncProcedure1<W, V>() {

			@Override
			public W initialize() {
				return initSync;
			}

			@Override
			public void invokeF(W value, V other) {
				signal.emit(mapfold.invoke(other, value));
			}
		};
		p1.associate(p2);
		p2.associate(p1);

		this.subscribe(p1);
		right.subscribe(p2);

		return signal;
	}

	/**
	@see Signal#then(java.lang.Object, java.lang.Object, fr.cyann.react.Signal, fr.cyann.functional.Function2) 
	 */
	public <W> Var<Tuple<V, W>> then(final V init, final W initMerge, final Signal<W> right) {
		return then(init, initMerge, right, new TupleFold<V, W>());
	}

	/**
	Associative method.<br>
	Synchronize signal together. The first then the second signals should be emited before resulting signal will emit.
	
	@param init the initial value of signal.
	@param initThen the initial value of then signal.
	@param <X> Type of the resulting var.
	@param <W> Type of the then var.
	@param right the var to merge with.
	@param mapfold the map fold transformation function.<br>It's goal is to merge the two values together and returned in the desired type.
	@return the new merged signal.
	 */
	public <W, X> Var<X> then(final V init, final W initThen, final Signal<W> right, final Function2<X, V, W> mapfold) {
		links.add(right);
		final Var<X> signal = new Var(mapfold.invoke(init, initThen), this);

		Tools.BlockProcedure1<V, W> p1 = new Tools.BlockProcedure1<V, W>() {

			@Override
			public V initialize() {
				return init;
			}

			@Override
			public void invokeF(V value, W other) {
				signal.emit(mapfold.invoke(value, other));
			}
		};
		Tools.SyncProcedure1<W, V> p2 = new Tools.SyncProcedure1<W, V>() {

			@Override
			public W initialize() {
				return initThen;
			}

			@Override
			public void invokeF(W value, V other) {
				signal.emit(mapfold.invoke(other, value));
			}
		};
		p1.associate(p2);
		p2.associate(p1);

		this.subscribe(p1);
		right.subscribe(p2);

		return signal;
	}

	/**
	@see Signal#when(java.lang.Object, java.lang.Object, fr.cyann.react.Signal, fr.cyann.functional.Function2) 
	 */
	public <W> Var<Tuple<V, W>> when(final V init, final W initMerge, final Signal<W> right) {
		return when(init, initMerge, right, new TupleFold<V, W>());
	}

	/**
	Associative method.<br>
	Synchronize signal together. The second then the first signals should be emited before resulting signal will emit.
	
	@param init the initial value of signal.
	@param initWhen the initial value of then signal.
	@param <X> Type of the resulting var.
	@param <W> Type of the when var.
	@param right the var to merge with.
	@param mapfold the map fold transformation function.<br>It's goal is to merge the two values together and returned in the desired type.
	@return the new merged signal.
	 */
	public <W, X> Var<X> when(final V init, final W initWhen, final Signal<W> right, final Function2<X, V, W> mapfold) {
		links.add(right);
		final Var<X> signal = new Var(mapfold.invoke(init, initWhen), this);

		Tools.SyncProcedure1<V, W> p1 = new Tools.SyncProcedure1<V, W>() {

			@Override
			public V initialize() {
				return init;
			}

			@Override
			public void invokeF(V value, W other) {
				signal.emit(mapfold.invoke(value, other));
			}
		};
		Tools.BlockProcedure1<W, V> p2 = new Tools.BlockProcedure1<W, V>() {

			@Override
			public W initialize() {
				return initWhen;
			}

			@Override
			public void invokeF(W value, V other) {
				signal.emit(mapfold.invoke(other, value));
			}
		};
		p1.associate(p2);
		p2.associate(p1);

		this.subscribe(p1);
		right.subscribe(p2);

		return signal;
	}

	// </editor-fold>
	public <W> Signal<V> disposeWhen(Signal<W> signal) {
		links.add(signal);
		signal.subscribeDiscret(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				Signal.this.dispose();
			}
		});
		return this;
	}

	public Signal<V> disposeWhen(final Predicate1<V> predicate) {
		this.subscribeDiscret(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				if (predicate.invoke(value)) {
					dispose();
				}
			}
		});
		return this;
	}

	public Signal<V> disposeAfterEmit() {
		react.subscriptLast(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				dispose();
			}
		});

		return this;
	}

	public void dispose() {
		//System.out.println("DISPOSE " + this.getClass());
		if (!disposed) {
			ReactManager.getInstance().decrementCounter();
		}
		disposed = true;

		react.clearSubscribe();

		for (Signal link : links) {
			link.dispose();
		}
		links.clear();

		if (parent != null) {
			parent.dispose();
		}
		parent = null;
	}

	/**
	 * Break link for dispose mechanism. The weak signal will not be disposed when
	 * linked signal is disposed.
	 *
	 * @return The weak signal.
	 */
	public Signal<V> weak() {
		final Signal<V> signal = new Signal<V>() {
		};
		// no parent

		this.subscribeDiscret(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emit(value);
			}
		});

		signal.start();
		return signal;
	}

	public final Var<V> toVar(V value) {
		final Var<V> signal = new Var<V>(value, this);

		this.subscribeDiscret(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emit(value);
			}
		});

		return signal;
	}
}
