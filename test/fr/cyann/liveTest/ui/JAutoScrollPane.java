/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.liveTest.ui;

import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JScrollPane;

/**
 * The JAutoScrollPane class.
 * Creation date: 1 juin 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class JAutoScrollPane extends JScrollPane {

	public static final int THRESHOLD = 20;
	private boolean autoScroll = false;

	public JAutoScrollPane() {
		init();
	}

	public JAutoScrollPane(int vsbPolicy, int hsbPolicy) {
		super(vsbPolicy, hsbPolicy);
		init();
	}

	public JAutoScrollPane(Component view) {
		super(view);
		init();
	}

	public JAutoScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);
		init();
	}

	public void autoScroll() {
		this.autoScroll = true;
	}

	public void forceAutoScroll() {
		this.autoScroll = true;
	}

	private void init() {

		this.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int max = JAutoScrollPane.this.getVerticalScrollBar().getMaximum() - JAutoScrollPane.this.getHeight();

				if (e.getValueIsAdjusting()) {
					if (e.getValue() + THRESHOLD >= max) {
						autoScroll();
					} else {
						autoScroll = false;
					}
				}

				if (autoScroll) {
					e.getAdjustable().setValue(e.getAdjustable().getMaximum());
				}
			}
		});
	}
}
