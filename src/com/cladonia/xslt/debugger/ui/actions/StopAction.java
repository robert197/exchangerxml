/*
 * $Id: StopAction.java,v 1.4 2004/05/28 15:38:21 edankert Exp $
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
 * An action that can be used to stop the debugger.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/05/28 15:38:21 $
 * @author Dogsbay
 */
 public class StopAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private XSLTDebuggerPane debugger = null;
	
 	/**
	 * The constructor for the action which continues the debugger.
	 */
 	public StopAction( XSLTDebuggerPane debugger) {
 		super( "Stop");
 		
 		this.debugger = debugger;

		putValue( MNEMONIC_KEY, new Integer( 'S'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F5, InputEvent.SHIFT_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/Stop16.gif"));
		putValue( SHORT_DESCRIPTION, "Stop Debugger");
 	}
 	
	/**
	 * The implementation of the stop debugger action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		debugger.stopDebugging();
 	}
 }
