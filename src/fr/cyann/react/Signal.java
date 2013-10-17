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

import fr.cyann.functor.Function1;
import fr.cyann.functor.Predicate1;
import fr.cyann.functor.Procedure1;
import fr.cyann.base.Package;
import java.util.ConcurrentModificationException;

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

	public boolean isRunning() {
		return running;
	}

	public void start() {
		running = true;
	}

	public void stop() {
		running = false;
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
	 * Default constructor.
	 */
	public Signal() {
		react = new React<V>();
		finish = new React<V>();
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

	public Signal<V> subscribe(final Signal<V> signal) {
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
	 * Filter the event according a criteria.
	 *
	 * @param function predicate to filter. If result is true, event pass else it
	 * is blocked.
	 * @return the new filtered signal.
	 */
	public final Signal<V> filter(final Predicate1<V> function) {
		final Signal<V> signal = new Signal<V>() {
			@Override
			public V getValue() {
				return Signal.this.getValue();
			}
		};

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
	 * Modify the react data in value and type.
	 *
	 * @param <R> The new react data type.
	 * @param function the function to transform data.
	 * @return the new transformed react.
	 */
	public final <R> Signal<R> map(final Function1<R, V> fEmit, final Function1<R, V> fFinish) {
		final Var<R> signal = new Var<R>(fFinish.invoke(this.getValue()));

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

	public final <W> Signal<V> mergeLeft(final Signal<W> merge) {

		final Var<V> signal = new Var<V>(getValue());

		this.subscribe(new Procedure1<V>() {
			@Override
			public void invoke(V value) {
				signal.setValue(value);
			}
		});

		merge.subscribe(new Procedure1<W>() {
			@Override
			public void invoke(W value) {
				if (signal.getValue().getClass().equals(value.getClass())) {
					signal.setValue(signal.getValue());
				} else {
					Signal.this.emit(signal.getValue());
				}
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

	public final <W> Signal<V> mergeLeft(final Signal<W> merge, final Function1<V, W> converter) {

		final Var<V> signal = new Var<V>(getValue());

		this.subscribe(new Procedure1<V>() {
			@Override
			public void invoke(V value) {
				signal.setValue(value);
			}
		});

		merge.subscribe(new Procedure1<W>() {
			@Override
			public void invoke(W value) {
				signal.setValue(converter.invoke(value));
			}
		});

		return signal;
	}

	public final <W> Signal<W> mergeRight(final Signal<W> merge) {

		final Var<W> signal = new Var<W>(merge.getValue());

		this.subscribe(new Procedure1<V>() {
			@Override
			public void invoke(V value) {
				if (value.getClass().equals(signal.getValue().getClass())) {
					signal.setValue((W) value);
				} else {
					merge.emit(signal.getValue());
				}
			}
		});

		merge.subscribe(new Procedure1<W>() {
			@Override
			public void invoke(W value) {
				signal.emit(value);
			}
		});

		return signal;
	}

	public final <W> Signal<W> mergeRight(final Signal<W> merge, final Function1<W, V> converter) {

		final Var<W> signal = new Var<W>(merge.getValue());

		this.subscribe(new Procedure1<V>() {
			@Override
			public void invoke(V value) {
				signal.setValue(converter.invoke(value));
			}
		});

		merge.subscribe(new Procedure1<W>() {
			@Override
			public void invoke(W value) {
				signal.setValue(value);
			}
		});

		return signal;
	}

	public final Signal<V> retainUntil(final Signal until) {
		until.noAutoStart();
		final Var<V> signal = new Var<V>(getValue());

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

	public final <W> Signal<W> runDuring(final Signal<W> merge) {
		merge.noAutoStart();

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

		final Var<V> signal = new Var<V>(function.invoke(this.getValue()));

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
}
