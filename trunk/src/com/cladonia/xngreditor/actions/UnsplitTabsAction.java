/*
 * $Id: UnsplitTabsAction.java,v 1.1 2004/07/28 16:59:08 edankert Exp $
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
 * @version	$Revision: 1.1 $, $Date: 2004/07/28 16:59:08 $
 * @author Dogsbay
 */
 public class UnsplitTabsAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ExchangerEditor parent = null;
	
 	/**
	 * The constructor for the action which allows addition of 
	 * documents to the application.
	 */
 	public UnsplitTabsAction( ExchangerEditor parent) {
 		super( "Unsplit");

		putValue( MNEMONIC_KEY, new Integer( 'V'));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Unsplit16.gif"));
		putValue( SHORT_DESCRIPTION, "Unsplit the Current selectected Tab.");
		
		this.parent = parent;
 	}
 	
	/**
	 * The implementation of the save document action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		parent.unsplit();

		if ( parent.getCurrent() != null) {
 			parent.getCurrent().setFocus();
 		}
 	}
}
