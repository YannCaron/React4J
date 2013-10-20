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
import java.util.ConcurrentModificationException;
import fr.cyann.base.Package;
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

	/**
	Emit event after a delay. If another event is emited, delay is postponed.
	@param delay the delay to wait.
	@return the new created signal.
	*/
	public Signal<V> smooth(final long delay) {
		final Signal<V> signal = new Var<V>(getValue());

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
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

	// monadic methods
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
	 * Modify the react data in value and type.
	 *
	 * @param <R> The new react data type.
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

	public final <W> Signal<Tuple<V, W>> merge(final Signal<W> merge) {
		links.add(merge);
		final Tuple<V, W> values = new Tuple<V, W>(getValue(), merge.getValue());
		final Var<Tuple<V, W>> signal = new Var<Tuple<V, W>>(values, this);

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

		return signal;
	}

	public final Signal<V> retainUntil(final Signal until) {
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

	public final <W> Signal<W> then(final Signal<W> then) {
		then.noAutoStart();
		then.setParent(this);

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				then.start();
			}
		});

		return then;
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

	public final Var<V> initialize(V value) {
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
