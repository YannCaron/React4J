/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.react.data;

/**
 * The TimeEvent class.
 * Creation date: 12 oct. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class TimeEvent implements Event {

	private int iteration;
	private final long startTime;
	private long fromStartTime;
	private long lastTime;

	public TimeEvent() {
		iteration = 0;
		startTime = System.currentTimeMillis();
		lastTime = startTime;
	}

	public void increment() {
		long time = System.currentTimeMillis(); 
		lastTime = time - lastTime;
		fromStartTime = time - startTime;
		iteration++;
	}

	public int getIteration() {
		return iteration;
	}

	public long getTimeElapsedFromStart() {
		return fromStartTime;
	}
	
	public long getTimeElapsed() {
		return lastTime;
	}
	
}
