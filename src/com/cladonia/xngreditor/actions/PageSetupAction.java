/*
 * $Id: PageSetupAction.java,v 1.1 2004/03/25 18:53:18 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.cladonia.xngreditor.TextPrinter;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to print the XML Document.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:53:18 $
 * @author Dogsbay
 */
 public class PageSetupAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
 	/**
	 * The constructor for the action which indents 
	 * the selected text.
	 *
	 * @param editor the XML Editor
	 */
 	public PageSetupAction() {
		super( "Page Setup");
		
		putValue( MNEMONIC_KEY, new Integer( 'u'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_P, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/PageSetup16.gif"));
		putValue( SHORT_DESCRIPTION, "Page Setup");
 	}
 	
	/**
	 * The implementation of the unindent action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "PageSetupAction.actionPerformed( "+event+")");
		
		TextPrinter printer = TextPrinter.getPrinter();
		printer.setup();
 	}
}
