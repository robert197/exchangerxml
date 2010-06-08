/*
 * $Id: FindAction.java,v 1.9 2005/08/23 14:28:46 tcurley Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;


import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FindDialog;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to find information in a XML Document.
 *
 * @version	$Revision: 1.9 $, $Date: 2005/08/23 14:28:46 $
 * @author Dogsbay
 */
 public class FindAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private FindDialog dialog = null;

 	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;
	
 	/**
	 * The constructor for the action which allows addition of 
	 * documents to the application.
	 */
 	public FindAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Find...");

		putValue( MNEMONIC_KEY, new Integer( 'F'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Find16.gif"));
		putValue( SHORT_DESCRIPTION, "Find");
		
		this.parent = parent;
	 	this.properties = props;
		
		setEnabled( false);
 	}
	
	private FindDialog getDialog() {
		if ( dialog == null) {
			dialog = new FindDialog( parent, properties, parent);
		}
		
		return dialog;
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
 		
 		FindDialog dialog = getDialog();
 		//now do this through an action listener in the dialog if not xpath
 		if(dialog.isXPath() == true) {
 			parent.getView().updateModel();
 		}
		
 		dialog.init( parent.getDocument().isError());
		dialog.show( parent.getView().getEditor().getSelectedText());
		
 		
		
		boolean matchCase = dialog.isCaseSensitive();
		boolean down = dialog.isSearchDirectionDown();
		boolean regExp = dialog.isRegularExpression();
		boolean matchWord = dialog.isMatchWholeWord();
		boolean wrapSearch = dialog.isWrapSearch();
		boolean isXPath = dialog.isXPath();

		properties.setMatchCase( matchCase);
		properties.setMatchWholeWord( matchWord);
		properties.setDirectionDown( down);
		properties.setBasicSearch( dialog.isBasic());
		properties.setXPath( isXPath);
		properties.setRegularExpression( regExp);
		properties.setWrapSearch( wrapSearch);

		if ( dialog.isBasic()) {
			wrapSearch = true;
			isXPath = false;
			regExp = false;
 		}

		if ( !dialog.isCancelled()) {
			String xpath = null;

			if ( isXPath) { 
				xpath = dialog.getXPath();
			}

			String search = dialog.getSearch();

			if ( search != null) {
				((Editor)parent.getCurrent()).search( xpath, search, regExp, matchCase, matchWord, down, wrapSearch);

				properties.addSearch( search);

				if ( !dialog.isBasic()) {
					properties.addXPath( xpath);
				}
			}
		}
		
		parent.getCurrent().setFocus();
 	}
 }
