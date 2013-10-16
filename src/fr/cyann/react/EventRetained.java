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

import fr.cyann.base.Package;
import fr.cyann.functor.Function1;
import fr.cyann.functor.Procedure1;

/**
 * The RetainedSignal class. Creation date: 14 oct. 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class EventRetained<V extends Event> extends EventReact<V> {

	protected final React<V> finish;

	@Package
	EventRetained(V value) {
		super(value);
		finish = new React<V>();
	}

	/**
	 * Register listener first order function to react. Function will be called
	 * when event is raised.
	 *
	 * @param subscriber function to be called in case of event.
	 * @return return this.
	 */
	public EventRetained<V> subscribeFinish(Procedure1<V> subscriber) {
		finish.subscribe(subscriber);
		return this;
	}

	public void emitFinish() {
		finish.emit(getValue());
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
			public void invoke(V arg1) {
				merge.stop();
			}
		});

		return merge;
	}

	@Override
	public EventRetained<V> subscribe(Procedure1<V> subscriber) {
		super.subscribe(subscriber);
		return this;
	}
	
	@Override
	public EventRetained<V> unSubscribe(Procedure1<V> subscriber) {
		super.unSubscribe(subscriber);
		return this;
	}

}
