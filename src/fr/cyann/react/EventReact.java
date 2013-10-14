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

import fr.cyann.functor.Procedure1;
import fr.cyann.base.Package;

/**
 * The EventReact class.
 * Creation date: 14 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public abstract class EventReact<V extends Event> extends Var<V> {
			
	@Package EventReact(V value) {
		super(value);
	}

	public final Signal<Event> mergeEvent(final Signal<Event> merge) {

		final Var<Event> signal = new Var<Event>(getValue());

		Procedure1<Event> p = new Procedure1<Event>() {

			@Override
			public void invoke(Event arg1) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		};

		this.subscribe(new Procedure1<V>() {

			@Override
			public void invoke(V value) {
				signal.setValue(value);
			}
		});

		merge.subscribe(new Procedure1<Event>() {

			@Override
			public void invoke(Event value) {
				signal.setValue(value);
			}
		});

		return signal;
	}
}
