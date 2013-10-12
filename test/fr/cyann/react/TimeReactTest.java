/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.react;

import fr.cyann.functor.Procedure1;
import fr.cyann.react.data.TimeEvent;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author CyaNn
 */
public class TimeReactTest extends TestCase {

	private final List<Object> results = new ArrayList<Object>();

	public TimeReactTest(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		results.clear();
	}

	private final static void assertMoreOrLess(long time1, long time2, long tolerance) {
		if (Math.abs(time1 - time2) > tolerance) {
			String msg = "Assertion error, range expected [" + (time1 - tolerance) + "-" + (time1 + tolerance)  + "], found " + time2;
			throw new AssertionError(msg);
		}
	}
	
	/**
	 * Test of once method, of class TimeReact.
	 */
	public void testOnce() throws InterruptedException {

		TimeReact.once(250L).subscribe(new Procedure1<TimeEvent>() {

			@Override
			public void invoke(TimeEvent event) {
				results.add(event);
			}
		});

		Thread.currentThread().sleep(150L);
		assertEquals(0, results.size());

		Thread.currentThread().sleep(150L);
		assertEquals(1, results.size());
		assertEquals(1, ((TimeEvent)results.get(0)).getIteration());
		assertMoreOrLess(250, ((TimeEvent)results.get(0)).getTimeElapsed(), 1);
		assertMoreOrLess(250, ((TimeEvent)results.get(0)).getTimeElapsedFromStart(), 1);

	}
}
