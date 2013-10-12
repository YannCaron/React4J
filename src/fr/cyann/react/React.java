/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
