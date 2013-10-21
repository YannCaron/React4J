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
import fr.cyann.functional.Predicate1;
import fr.cyann.functional.Procedure1;
import java.util.ConcurrentModificationException;
import fr.cyann.base.Package;
import fr.cyann.functional.Function2;
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
	protected final React<V> finish;
	protected boolean running = true;
	private boolean autoStart = true;
	private Signal parent;
	private final List<Signal> links;

	// constructor
	/**
	 * Default constructor.
	 */
	public Signal() {
		ReactManager.getInstance().incrementCounter();

		react = new React<V>();
		finish = new React<V>();
		links = new ArrayList<Signal>();
	}

	@Package
	Signal(boolean count) {
		if (count) {
			ReactManager.getInstance().incrementCounter();
		}

		react = new React<V>();
		finish = new React<V>();
		links = new ArrayList<Signal>();
	}

	@Package
	Signal(Signal parent) {
		this();
		this.parent = parent;
	}

	// property
	public boolean isRunning() {
		return running;
	}

	@Package
	void setParent(Signal parent) {
		this.parent = parent;
	}

	// method
	public void start() {
		running = true;
	}

	public void stop() {
		running = false;
	}

	public void reset() {
		stop();
		start();
	}

	protected boolean isAutoStart() {
		return autoStart;
	}

	protected void noAutoStart() {
		autoStart = false;
	}

	public abstract V getValue();

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

	public void emitFinish(V value) {
		try {
			finish.emit(value);
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
		if (isAutoStart()) {
			start();
		}
		react.subscribe(subscriber);
		return this;
	}

	public Signal<V> register(final Var<V> signal) {
		if (isAutoStart()) {
			start();
		}
		react.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emit(value);
			}
		});

		finish.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emitFinish(value);
			}
		});
		signal.value = getValue();
		return this;
	}

	/**
	 * Register listener first order function to react. Function will be called
	 * when event is raised.
	 *
	 * @param subscriber function to be called in case of event.
	 * @return return this.
	 */
	public Signal<V> subscribeFinish(Procedure1<V> subscriber) {
		finish.subscribe(subscriber);
		return this;
	}

	public Signal<V> unSubscribe(Procedure1<V> subscriber) {
		react.unSubscribe(subscriber);
		return this;
	}

	public Signal<V> unSubscribeFinish(Procedure1<V> subscriber) {
		finish.unSubscribe(subscriber);
		return this;
	}

	private static class SmoothThread<V> extends Thread {

		private final Signal<V> signal;
		private boolean isWaiting = false;
		private V value;
		private long executeAt;

		public SmoothThread(Signal<V> signal) {
			this.signal = signal;
		}

		public void postpone(long delay) {
			this.executeAt = System.currentTimeMillis() + delay;
		}

		public void setValue(V value) {
			this.value = value;
		}

		public void launch() {
			System.out.println(this.isWaiting);
			if (isWaiting) {
				synchronized (this) {
					this.notify();
				}
			}
		}

		@Override
		public void run() {

			try {

				while (!isInterrupted()) {
					synchronized (this) {
						isWaiting = true;
						Thread.currentThread().wait();
						isWaiting = false;
					}

					while (System.currentTimeMillis() < executeAt) {
						Thread.currentThread().sleep(executeAt - System.currentTimeMillis());
					}

					signal.emit(value);
				}
			} catch (InterruptedException ex) {
			}
		}
	}
	private SmoothThread<V> smoothThread = null;

	/**
	 * Emit event after a delay. If another event is emited, delay is postponed.
	 *
	 * @param delay the delay to wait.
	 * @return the new created signal.
	 */
	public Signal<V> smooth(final long delay) {
		final Signal<V> signal = new Var<V>(getValue());

		smoothThread = new SmoothThread<V>(signal);
		smoothThread.start();

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				smoothThread.setValue(value);
				smoothThread.postpone(delay);
				smoothThread.launch();
			}
		});

		this.subscribeFinish(
			new Procedure1<V>() {

				@Override
				public void invoke(V value) {
					signal.emitFinish(value);
				}
			});


		return signal;
	} // monadic methods

	/**
	 * Filter the event according a criteria.
	 *
	 * @param function predicate to filter. If result is true, event pass else it
	 * is blocked.
	 * @return the new filtered signal.
	 */
	public final Signal<V> filter(final Predicate1<V> function) {
		final Signal<V> signal = new Var<V>(Signal.this.getValue(), this);

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				if (signal.isRunning() && function.invoke(value)) {
					signal.emit(Signal.this.getValue());
				}
			}
		});

		this.unSubscribeFinish(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				if (signal.isRunning() && function.invoke(value)) {
					signal.emitFinish(Signal.this.getValue());
				}
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
	public final <R> Signal<R> map(final Function1<R, V> function) {
		return map(function, function);
	}

	/**
	 * Modify the react data in value and type for react and finish.
	 *
	 * @param <R> The new react data type.
	 * @param function the function to transform data.
	 * @return the new transformed react.
	 */
	public final <R> Signal<R> map(final Function1<R, V> fEmit, final Function1<R, V> fFinish) {
		final Var<R> signal = new Var<R>(fFinish.invoke(this.getValue()), this);

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				if (signal.isRunning()) {
					signal.emit(fEmit.invoke(Signal.this.getValue()));
				}
			}
		});

		this.subscribeFinish(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				if (signal.isRunning()) {
					signal.emitFinish(fFinish.invoke(Signal.this.getValue()));
				}
			}
		});
		return signal;
	}

	/**
	Fold current value with previous one.
	@param function specify the action to perform between previous value and current.
	@return the new value.
	*/
	public final Signal<V> fold(final Function2<V, V, V> function) {
		return fold(function, function);
	}
		
	/**
	Fold current value with previous one.
	@param fEmit specify the action to perform between previous value and current for event.
	@param fFinish specify the action to perform between previous value and current for finish event.
	@return the new value.
	*/
	public final Signal<V> fold(final Function2<V, V, V> fEmit, final Function2<V, V, V> fFinish) {
		final Signal<V> signal = new Var<V>(getValue());
		signal.setParent(this);

		this.subscribe(new Procedure1<V>() {

			private V previous = null;

			@Override
			public void invoke(V arg1) {
				if (previous == null) {
					V value = getValue();
					if (value instanceof Event) {
						previous = (V) ((Event) value).clone();
					} else {
						previous = value;
					}
				} else {
					previous = fEmit.invoke(previous, getValue());
					signal.emit(previous);
				}
			}
		});

		this.subscribeFinish(new Procedure1<V>() {

			private V previous = null;

			@Override
			public void invoke(V arg1) {
				if (previous == null) {
					V value = getValue();
					if (value instanceof Event) {
						previous = (V) ((Event) value).clone();
					} else {
						previous = value;
					}
				} else {
					previous = fFinish.invoke(previous, getValue());
					signal.emitFinish(previous);
				}
			}
		});

		return signal;
	}

	/**
	 * Merge two signal together. If any signal emit, the resulting signal will
	 * emit. Consider this operation like an <b>and</b> boolean operation.
	 *
	 * @param <W> The result signal value type.
	 * @param merge The signal to merge with.
	 * @return The merging result.
	 */
	public final <W> Signal<Tuple2<V, W>> merge(final Signal<W> merge) {
		links.add(merge);
		final Tuple2<V, W> values = new Tuple2<V, W>(getValue(), merge.getValue());
		final Var<Tuple2<V, W>> signal = new Var<Tuple2<V, W>>(values, this);

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				values.setFirst(value);
				signal.emit(values);
			}
		});

		merge.subscribe(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				values.setSecond(value);
				signal.emit(values);
			}
		});

		this.subscribeFinish(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				values.setFirst(value);
				signal.emitFinish(values);
			}
		});

		merge.subscribeFinish(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				values.setSecond(value);
				signal.emitFinish(values);
			}
		});

		return signal;
	}

	private static abstract class SyncProcedure1<W> implements Procedure1<W> {

		private SyncProcedure1 with;
		private boolean invoked = false;

		public void setWith(SyncProcedure1 with) {
			this.with = with;
		}

		@Override
		public synchronized void invoke(W value) {
			invoke(value, with.invoked);

			if (with.invoked) {
				invoked = false;
				with.invoked = false;
			} else {
				this.invoked = true;
			}
		}

		public abstract void invoke(W value, boolean sync);
	}

	/**
	 * Synchronize signal together. The both signals should be emited before
	 * resulting signal will emit.<br>
	 * Consider this operation like an <b>or</b> boolean operation.
	 *
	 * @param <W> The result signal value type.
	 * @param merge The signal to merge with.
	 * @return The synchronized result.
	 */
	public final <W> Signal<Tuple2<V, W>> sync(final Signal<W> sync) {
		links.add(sync);
		final Tuple2<V, W> values = new Tuple2<V, W>(getValue(), sync.getValue());
		final Var<Tuple2<V, W>> signal = new Var<Tuple2<V, W>>(values, this);

		SyncProcedure1<V> p1 = new SyncProcedure1<V>() {

			@Override
			public void invoke(V value, boolean sync) {
				values.setFirst(value);
				if (sync) {
					signal.emit(values);
				}
			}
		};

		SyncProcedure1<W> p2 = new SyncProcedure1<W>() {

			@Override
			public void invoke(W value, boolean sync) {
				values.setSecond(value);
				if (sync) {
					signal.emit(values);
				}
			}
		};
		p1.setWith(p2);
		p2.setWith(p1);

		this.subscribe(p1);
		sync.subscribe(p2);

		SyncProcedure1<V> pf1 = new SyncProcedure1<V>() {

			@Override
			public void invoke(V value, boolean sync) {
				values.setFirst(value);
				if (sync) {
					signal.emitFinish(values);
				}
			}
		};

		SyncProcedure1<W> pf2 = new SyncProcedure1<W>() {

			@Override
			public void invoke(W value, boolean sync) {
				values.setSecond(value);
				if (sync) {
					signal.emitFinish(values);
				}
			}
		};
		pf1.setWith(pf2);
		pf2.setWith(pf1);

		this.subscribeFinish(pf1);
		sync.subscribeFinish(pf2);

		this.subscribeFinish(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				values.setFirst(value);
				signal.emitFinish(values);
			}
		});

		sync.subscribeFinish(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				values.setSecond(value);
				signal.emitFinish(values);
			}
		});

		return signal;
	}

	/**
	 * Expects the first signal, then the second one before emit resulting.
	 * signal.<br>
	 * Difference with sync is that signals should be consecutives.
	 *
	 * @param <W> The value type of the second signal.
	 * @param then The second signal.
	 * @return The resulting signal.
	 */
	public final <W> Signal<W> then(final Signal<W> then) {

		then.noAutoStart();
		then.setParent(this);

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				then.start();
			}
		});

		this.subscribeFinish(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				then.stop();
			}
		});

		return then;
	}
	/*
	public final <W> Signal<W> then(final Function1<Signal<W>, V> function) {
	
	final Signal<W> signal = new Var<W>((W) null);
	links.add(signal);
	
	this.subscribe(new Procedure1<V>() {
	
	private boolean active = true;
	
	@Override
	public synchronized void invoke(V value) {
	if (active) {
	final Signal<W> then = function.invoke(value);
	links.add(then);
	
	then.subscribe(new Procedure1<W>() {
	
	@Override
	public void invoke(W value) {
	signal.emit(value);
	active = true;
	}
	});
	}
	active = false;
	
	}
	});
	
	return signal;
	
	}*/

	/**
	 * Expects the second signal, then the first one before emit resulting.
	 * signal.<br>
	 * Difference with sync is that signals should be consecutives.
	 *
	 * @param <W> The value type of the second signal.
	 * @param when The second signal.
	 * @return The resulting signal.
	 */
	public final <W> Signal<V> when(final Signal<W> when) {
		links.add(when);
		when.setParent(this);

		when.subscribe(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				when.start();
			}
		});

		when.subscribeFinish(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				when.stop();
			}
		});

		return this;
	}

	/**
	Break link for dispose mechanism. The weak signal will not be disposed when linked signal is disposed.
	@return The weak signal.
	 */
	public final Signal<V> weak() {
		final Signal<V> signal = new Signal<V>() {

			@Override
			public V getValue() {
				return Signal.this.getValue();
			}
		};
		// no parent

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emit(value);
			}
		});

		this.subscribeFinish(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emitFinish(value);
			}
		});

		return signal;
	}

	/**
	 * Create a long signal. Emit on the beginning and emif finish signal at the
	 * end.<br>
	 * Usefull for performing signal loop with start and stop limits.
	 *
	 * @param until the finish signal.
	 * @return The resulting signal.
	 */
	public final Signal<V> until(final Signal until) {
		links.add(until);
		until.noAutoStart();
		final Var<V> signal = new Var<V>(getValue(), this);

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.setValue(value);
				until.start();
			}
		});

		until.subscribe(new Procedure1() {

			@Override
			public void invoke(Object value) {
				signal.emitFinish(signal.getValue());
				until.stop();
			}
		});


		return signal;
	}

	public final <W> Signal<W> during(final Signal<W> merge) {
		merge.noAutoStart();
		merge.setParent(this);

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				merge.start();
			}
		});

		this.subscribeFinish(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				merge.stop();
				merge.emitFinish(merge.getValue());
			}
		});

		return merge;
	}

	public final Signal<V> otherwise(final Function1<V, V> function) {

		final Var<V> signal = new Var<V>(function.invoke(this.getValue()), this);

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emit(value);
			}
		});

		this.subscribeFinish(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emit(function.invoke(value));
			}
		});

		return signal;
	}

	public final Signal<V> emitOnFinished() {
		this.subscribeFinish(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				emit(value);
			}
		});
		return this;
	}

	public final <W> Signal<V> disposeWhen(Signal<W> signal) {
		links.add(signal);
		signal.subscribe(new Procedure1<W>() {

			@Override
			public void invoke(W value) {
				Signal.this.dispose();
			}
		});
		return this;
	}

	public final Signal<V> disposeWhen(final Predicate1<V> predicate) {
		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				if (predicate.invoke(value)) {
					Signal.this.dispose();
				}
			}
		});
		return this;
	}

	public final Signal<V> disposeOnFinished() {
		this.subscribeFinish(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				dispose();
			}
		});
		return this;
	}

	public final Var<V> toVar(V value) {
		final Var<V> signal = new Var<V>(value);

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emit(value);
			}
		});

		this.subscribeFinish(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.emitFinish(value);
			}
		});

		return signal;
	}

	public void dispose() {
		ReactManager.getInstance().decrementCounter();

		if (smoothThread != null) {
			smoothThread.interrupt();
			smoothThread = null;
		}

		for (Signal link : links) {
			link.dispose();
		}
		links.clear();

		if (parent != null) {
			parent.dispose();
		}
		parent = null;
	}
}
