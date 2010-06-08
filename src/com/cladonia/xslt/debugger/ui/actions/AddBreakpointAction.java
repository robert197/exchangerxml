/*
 * $Id: AddBreakpointAction.java,v 1.2 2004/05/28 15:38:21 edankert Exp $
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
import javax.swing.KeyStroke;

import com.cladonia.xslt.debugger.ui.InputPane;

/**
 * An action that can be used to add a breakpoint.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/28 15:38:21 $
 * @author Dogsbay
 */
 public class AddBreakpointAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private InputPane inputPane = null;
	
 	/**
	 * The constructor for the action which allows addition of 
	 * breakpoints for the xslt debugger.
	 */
 	public AddBreakpointAction( InputPane inputPane) {
 		super( "Add Breakpoint");

		putValue( MNEMONIC_KEY, new Integer( 'A'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F9, 0, false));
		putValue( SHORT_DESCRIPTION, "Add a Breakpoint.");
		
		this.inputPane = inputPane;
 	}
 	
	/**
	 * The implementation of the add breakpoint action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 		inputPane.setBreakpoint( inputPane.getSelectedLine());
 	}
 }
