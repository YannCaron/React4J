/*
 * Copyright (C) 2013 CyaNn
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
import fr.cyann.functor.Procedure1;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author caronyn
 */
public class React<A> {

	private List<Procedure1<A>> subscribers;

	public React() {
		subscribers = new ArrayList<Procedure1<A>>();
	}
	
	public React<A> subscribe(Procedure1<A> subscriber) {
		this.subscribers.add(subscriber);
		return this;
	}

	@Package React<A> emit(A value) {
		for (Procedure1<A> subscriber : subscribers) {
			subscriber.invoke(value);
		}
		return this;
	}
}
