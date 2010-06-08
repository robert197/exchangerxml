/*
 * $Id: ContinueAction.java,v 1.4 2004/05/28 15:38:21 edankert Exp $
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
 * An action that can be used to continue the debugger.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/05/28 15:38:21 $
 * @author Dogsbay
 */
 public class ContinueAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private XSLTDebuggerPane debugger = null;
 	private boolean running = false;
	
 	/**
	 * The constructor for the action which continues the debugger.
	 */
 	public ContinueAction( XSLTDebuggerPane debugger) {
 		super( "Start Debugging");
 		
 		this.debugger = debugger;

		putValue( MNEMONIC_KEY, new Integer( 'C'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/Play16.gif"));
		putValue( SHORT_DESCRIPTION, "Start debugging session.");
 	}
 	
 	public void setRunning( boolean running) {
 		this.running = running;
 		
 		if ( running) {
 			putValue( NAME, "Continue");
 			putValue( SHORT_DESCRIPTION, "Continue to next Breakpoint or end.");
 		} else {
 			putValue( NAME, "Start");
 			putValue( SHORT_DESCRIPTION, "Start debugging session.");
 		}
 	}
 	
	/**
	 * The implementation of the continue debugger action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 		if ( running) {
 			debugger.continueDebugging();
 		} else {
// 			System.out.println( "***********************");
// 			System.out.println( "**** Not running... ***");
// 			System.out.println( "***********************");
 			debugger.startDebugging();
 			debugger.continueDebugging();
 		}
 	}
 }
