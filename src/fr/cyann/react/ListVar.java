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

import fr.cyann.functional.Procedure1;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * The ListVar class. Creation date: 12 oct. 2013.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class ListVar<V> extends Var<List<V>> implements List<V> {

	private class Subscriber implements Procedure1<V> {

		@Override
		public void invoke(V arg1) {
			if (isRunning()) {
				ListVar.this.react.emit(value);
			}
		}
	}
	private ListVar(List<V> value) {
		super(value);
	}

	public static <V> ListVar<V> newInstance(List<V> value) {
		return new ListVar<V>(value);
	}

	@Override
	public List<V> getValue() {
		return value;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return value.toArray(a);
	}

	@Override
	public Object[] toArray() {
		return value.toArray();
	}

	@Override
	public ListVar<V> subList(int fromIndex, int toIndex) {
		return new ListVar(value.subList(fromIndex, toIndex));
	}

	@Override
	public int size() {
		return value.size();
	}

	@Override
	public V set(int index, V element) {
		V result = value.set(index, element);
		react.emit(value);
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean result = value.retainAll(c);
		if (result) {
			react.emit(value);
		}
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = value.removeAll(c);;
		if (result) {
			react.emit(value);
		}
		return result;
	}

	@Override
	public V remove(int index) {
		V result = value.remove(index);
		react.emit(value);
		return result;
	}

	@Override
	public boolean remove(Object o) {
		boolean result = value.remove(o);
		if (result) {
			react.emit(value);
		}
		return result;
	}

	@Override
	public ListIterator<V> listIterator(int index) {
		return value.listIterator(index);
	}

	@Override
	public ListIterator<V> listIterator() {
		return value.listIterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return value.lastIndexOf(o);
	}

	@Override
	public Iterator<V> iterator() {
		return value.iterator();
	}

	@Override
	public boolean isEmpty() {
		return value.isEmpty();
	}

	@Override
	public int indexOf(Object o) {
		return value.indexOf(o);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public V get(int index) {
		return value.get(index);
	}

	@Override
	public boolean equals(Object o) {
		return value.equals(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return value.containsAll(c);
	}

	@Override
	public boolean contains(Object o) {
		return value.contains(o);
	}

	@Override
	public void clear() {
		value.clear();
		react.emit(value);
	}

	@Override
	public boolean addAll(int index, Collection<? extends V> c) {
		boolean result = value.addAll(index, c);
		if (result) {
			react.emit(value);

			for (V element : c) {
				if (element instanceof Signal) {
					((Signal) element).subscribe(new Subscriber());
				}
			}
		}
		return result;
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
		boolean result = value.addAll(c);
		if (result) {
			for (V element : c) {
				if (element instanceof Signal) {
					((Signal) element).subscribe(new Subscriber());
				}
			}
			react.emit(value);
		}
		return result;
	}

	@Override
	public void add(int index, V element) {
		value.add(index, element);
		if (element instanceof Signal) {
			((Signal) element).subscribe(new Subscriber());
		}
		react.emit(value);
	}

	@Override
	public boolean add(V e) {
		boolean result = value.add(e);
		if (result) {
			react.emit(value);
			if (e instanceof Signal) {
				((Signal) e).subscribe(new Subscriber());
			}
		}
		return result;
	}
}
