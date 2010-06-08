/*
 * $Id: FindNextAction.java,v 1.7 2004/10/26 10:06:49 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to find the next information 
 * in a XML Document.
 *
 * @version	$Revision: 1.7 $, $Date: 2004/10/26 10:06:49 $
 * @author Dogsbay
 */
 public class FindNextAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ExchangerEditor parent = null;
 	private ConfigurationProperties properties = null;
	
 	/**
	 * The constructor for the action which allows addition of 
	 * documents to the application.
	 */
 	public FindNextAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Find Next");

		putValue( MNEMONIC_KEY, new Integer( 'N'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F3, 0, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/FindNext16.gif"));
		putValue( SHORT_DESCRIPTION, "Find Next");
		
	 	this.parent = parent;
	 	this.properties = props;

	 	setEnabled( false);
 	}
 	
	 /**
	  * Sets the current view.
	  *
	  * @param view the current view.
	  */
	 public void setView( Object view) {
	 	setEnabled( ( view instanceof Editor));
	 }

	 /**
	  * The implementation of the find action, called 
	  * after a user action.
	  *
	  * @param the action event.
	  */
	 public void actionPerformed( ActionEvent e) {
	 	Vector searches = properties.getSearches();
	 	Vector xpaths = properties.getXPaths();
		
		if ( searches.size() > 0) {
	 		String search = (String)searches.elementAt(0);
	 		String xpath = null;
	 		
	 		if ( properties.isXPath()) {
	 			xpath = (String)xpaths.elementAt(0);
	 		}

	 		boolean matchCase = properties.isMatchCase();
	 		boolean down = properties.isDirectionDown();
	 		boolean regExp = properties.isRegularExpression();
	 		boolean matchWord = properties.isMatchWholeWord();
	 		boolean wrapSearch = properties.isWrapSearch();

			if ( properties.isBasicSearch()) {
				xpath = null;
				wrapSearch = true;
				regExp = false;
	 		}

			if ( search != null) {
	 			((Editor)parent.getCurrent()).search( xpath, search, regExp, matchCase, matchWord, down, wrapSearch);
	 		}
		}
	 }
 }
