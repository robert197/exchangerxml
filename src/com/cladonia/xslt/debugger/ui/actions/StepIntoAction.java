/*
 * $Id: StepIntoAction.java,v 1.4 2004/05/28 15:38:21 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to step with the debugger.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/05/28 15:38:21 $
 * @author Dogsbay
 */
 public class StepIntoAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private XSLTDebuggerPane debugger = null;
 	private boolean running = false;
	
 	/**
	 * The constructor for the action which steps with the debugger.
	 */
 	public StepIntoAction( XSLTDebuggerPane debugger) {
 		super( "Step Into");
 		
 		this.debugger = debugger;

		putValue( MNEMONIC_KEY, new Integer( 't'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F11, 0, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/StepInto16.gif"));
		putValue( SHORT_DESCRIPTION, "Step Into");
 	}
 	
 	public void setRunning( boolean running) {
 		this.running = running;
 	}
 	
	/**
	 * The implementation of the continue debugger action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 		if ( running) {
 			debugger.stepDebugger();
 		} else {
 			debugger.startDebugging();
 			debugger.stepDebugger();
 		}
 	}
 }
