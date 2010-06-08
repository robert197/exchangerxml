/*
 * $Id: SplitTabsVerticallyAction.java,v 1.2 2004/11/05 11:45:09 edankert Exp $
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

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to close a XML Document.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/11/05 11:45:09 $
 * @author Dogsbay
 */
 public class SplitTabsVerticallyAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ExchangerEditor parent = null;
	
 	/**
	 * The constructor for the action which allows addition of 
	 * documents to the application.
	 */
 	public SplitTabsVerticallyAction( ExchangerEditor parent) {
 		super( "Split Vertically");

		putValue( MNEMONIC_KEY, new Integer( 'V'));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/SplitVertical16.gif"));
		putValue( SHORT_DESCRIPTION, "Split the Current Tab Vertically.");
		
		this.parent = parent;

		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the save document action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		System.out.println( "----- splitting tabs -----");

		parent.splitVertically();

//		if ( parent.getCurrent() != null) {
// 			parent.getCurrent().setFocus();
// 		}
 	}
 }
