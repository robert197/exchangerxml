/*
 * $Id: DisableBreakpointAction.java,v 1.1 2004/03/25 18:58:13 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xslt.debugger.ui.InputPane;

/**
 * An action that can be used to disable a breakpoint.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:58:13 $
 * @author Dogsbay
 */
 public class DisableBreakpointAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private InputPane inputPane = null;
	
 	/**
	 * The constructor for the action which allows disabling of 
	 * breakpoints for the xslt debugger.
	 */
 	public DisableBreakpointAction( InputPane inputPane) {
 		super( "Disable Breakpoint");

		putValue( MNEMONIC_KEY, new Integer( 'D'));
		putValue( SHORT_DESCRIPTION, "Disable the current Breakpoint.");
		
		this.inputPane = inputPane;
 	}
 	
	/**
	 * The implementation of the disable breakpoint action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 		inputPane.setBreakpoint( inputPane.getSelectedLine());
 	}
 }
