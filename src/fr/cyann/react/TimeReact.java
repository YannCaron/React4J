/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.react;

import fr.cyann.react.data.TimeEvent;

/**
 * The TimeReact class.
 * Creation date: 12 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class TimeReact extends Signal<TimeEvent> {

	private Thread thread;
	private final TimeEvent event;

	// constructor
	private TimeReact() {
		event = new TimeEvent();
	}

	public static TimeReact once(final long timeout) {
		final TimeReact react = new TimeReact();

		react.thread = new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(timeout);
					react.event.increment();
					react.react.emit(react.event);
				} catch (InterruptedException ex) {
					// do nothing
				}

			}
		};

		react.thread.start();

		return react;
	}

	// attributes
	@Override
	public TimeEvent getValue() {
		return event;
	}
}
