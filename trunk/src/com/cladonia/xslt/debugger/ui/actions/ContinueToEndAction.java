/*
 * $Id: ContinueToEndAction.java,v 1.1 2004/05/28 15:38:21 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to continue the debugger.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/05/28 15:38:21 $
 * @author Dogsbay
 */
 public class ContinueToEndAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private XSLTDebuggerPane debugger = null;
 	private boolean running = false;
	
 	/**
	 * The constructor for the action which continues the debugger.
	 */
 	public ContinueToEndAction( XSLTDebuggerPane debugger) {
 		super( "Run to End");
 		
 		this.debugger = debugger;

		putValue( MNEMONIC_KEY, new Integer( 'E'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F5, InputEvent.SHIFT_MASK + InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/RunToEnd16.gif"));
		putValue( SHORT_DESCRIPTION, "Run to End.");
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
 			debugger.runToEnd();
 		} else {
// 			System.out.println( "***********************");
// 			System.out.println( "**** Not running... ***");
// 			System.out.println( "***********************");
 			debugger.startDebugging();
 			debugger.runToEnd();
 		}
 	}
 }
