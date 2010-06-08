/*
 * $Id: StatusFrame.java,v 1.1 2004/04/30 11:05:30 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * The desktop frame, displays the desktop services buttons and 
 * allows for adding, removing and opening services that are 
 * placed on the desktop.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/04/30 11:05:30 $
 * @author Dogsbay
 */
public abstract class StatusFrame extends JFrame{
	private static final boolean DEBUG = false;

	private WaitGlassPane waitPane = null;

	public StatusFrame() {
		// Init a Glass Pane for the wait cursor
		waitPane = new WaitGlassPane();
		setGlassPane(waitPane);

	}

	public abstract void setStatus( final String status);

	/**
	 * Sets the wait cursor on the Exchanger editor frame.
	 *
	 * @param enabled true when wait is enabled.
	 */
	public void setWait(final boolean enabled) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				waitPane.setVisible(enabled);
			}
		});
	}

	private class WaitGlassPane extends JPanel {
		public WaitGlassPane() {
			setOpaque(false);
			addKeyListener(new KeyAdapter() {
			});
			addMouseListener(new MouseAdapter() {
			});
			super.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
	}
}
