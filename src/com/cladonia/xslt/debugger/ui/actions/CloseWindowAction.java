/*
 * $Id: CloseWindowAction.java,v 1.1 2004/05/23 14:46:43 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/05/23 14:46:43 $
 * @author Dogsbay
 */
public class CloseWindowAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private XSLTDebuggerPane debugger = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public CloseWindowAction( XSLTDebuggerPane debugger) {
 		super( "Close");

		this.debugger = debugger;

		putValue( MNEMONIC_KEY, new Integer( 'C'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_W, InputEvent.CTRL_MASK, false));
		putValue( SHORT_DESCRIPTION, "Close the current Window");
 	}
 	
	/**
 	 * The implementation of the open grammar action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
 		execute();
 	}
 	
 	public void execute() {
		debugger.closeSelectedPane();
 	}
}
