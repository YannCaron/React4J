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
import fr.cyann.functional.Procedure1;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Yann Caron
 */
public class Dispatcher<A> {

	private List<Procedure1<A>> subscribers;
	private Procedure1<A> afterEmit;

	public Dispatcher() {
		subscribers = new ArrayList<Procedure1<A>>();
	}

	public void subscribe(Procedure1<A> subscriber) {
		this.subscribers.add(subscriber);
	}

	public void unSubscribe(Procedure1<A> subscriber) {
		this.subscribers.remove(subscriber);
	}

	public void subscribeLast(Procedure1<A> subscriber) {
		afterEmit = subscriber;
	}

	public Procedure1<A> unSubscriptLast() {
		Procedure1<A> p = afterEmit;
		afterEmit = null;
		return p;
	}

	public void clearSubscribes() {
		this.subscribers.clear();
		afterEmit = null;
	}

	@Package
	synchronized Dispatcher<A> emit(A value) {
		for (Procedure1<A> subscriber : subscribers) {
			subscriber.invoke(value);
		}
		if (afterEmit != null) {
			afterEmit.invoke(value);
		}
		return this;
	}
}
