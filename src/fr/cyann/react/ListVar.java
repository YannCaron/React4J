/*
This file was developed by Yann Caron in october 2013.
This file is part of Java.react.

Java.react is free software: you can redistribute it and/or modify
it under the terms of the GNU Less General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Java.react is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Less General Public License for more details.

You should have received a copy of the GNU General Public License
along with Java.react.  If not, see <http://www.gnu.org/licenses/lgpl.html>.*/

package fr.cyann.react;

import fr.cyann.functor.Procedure1;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * The ListVar class.
 * Creation date: 12 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class ListVar<V> extends Signal<List<V>> implements List<V> {

	private class Subscriber implements Procedure1<V> {

		@Override
		public void invoke(V arg1) {
			ListVar.this.react.emit(list);
		}
	}
	// decorator design pattern
	private List<V> list;

	private ListVar(List<V> list) {
		this.list = list;
	}

	public static <V> ListVar<V> newInstance(List<V> list) {
		return new ListVar<V>(list);
	}

	@Override
	public List<V> getValue() {
		return list;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public ListVar<V> subList(int fromIndex, int toIndex) {
		return new ListVar(list.subList(fromIndex, toIndex));
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public V set(int index, V element) {
		V result = list.set(index, element);
		react.emit(list);
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean result = list.retainAll(c);
		if (result) {
			react.emit(list);
		}
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = list.removeAll(c);;
		if (result) {
			react.emit(list);
		}
		return result;
	}

	@Override
	public V remove(int index) {
		V result = list.remove(index);
		react.emit(list);
		return result;
	}

	@Override
	public boolean remove(Object o) {
		boolean result = list.remove(o);
		if (result) {
			react.emit(list);
		}
		return result;
	}

	@Override
	public ListIterator<V> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public ListIterator<V> listIterator() {
		return list.listIterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public Iterator<V> iterator() {
		return list.iterator();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int hashCode() {
		return list.hashCode();
	}

	@Override
	public V get(int index) {
		return list.get(index);
	}

	@Override
	public boolean equals(Object o) {
		return list.equals(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public void clear() {
		list.clear();
		react.emit(list);
	}

	@Override
	public boolean addAll(int index, Collection<? extends V> c) {
		boolean result = list.addAll(index, c);
		if (result) {
			react.emit(list);

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
		boolean result = list.addAll(c);
		if (result) {
			for (V element : c) {
				if (element instanceof Signal) {
					((Signal) element).subscribe(new Subscriber());
				}
			}
			react.emit(list);
		}
		return result;
	}

	@Override
	public void add(int index, V element) {
		list.add(index, element);
		if (element instanceof Signal) {
			((Signal) element).subscribe(new Subscriber());
		}
		react.emit(list);
	}

	@Override
	public boolean add(V e) {
		boolean result = list.add(e);
		if (result) {
			react.emit(list);
			if (e instanceof Signal) {
				((Signal) e).subscribe(new Subscriber());
			}
		}
		return result;
	}
}
