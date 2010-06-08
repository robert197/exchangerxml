/*
 * $Id: ReloadWindowAction.java,v 1.2 2004/05/31 14:44:09 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;

import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xslt.debugger.ui.InputPane;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/31 14:44:09 $
 * @author Dogsbay
 */
public class ReloadWindowAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private XSLTDebuggerPane debugger = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public ReloadWindowAction( XSLTDebuggerPane debugger) {
 		super( "Reload");

		this.debugger = debugger;

		putValue( MNEMONIC_KEY, new Integer( 'R'));
		putValue( SHORT_DESCRIPTION, "Reload the current Window");
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
 		InputPane pane = debugger.getSelectedPane();
 		
 		if ( pane != null) {
 			try {
 				pane.reload();
 			} catch ( IOException e) {
 				MessageHandler.showError( debugger.getFrame(), "Could not reload \""+pane.getName()+"\".", e, "Reload Error");
 			}
 		}
 	}
}
