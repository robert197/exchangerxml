/*
 * $Id: ReplaceAction.java,v 1.8 2004/10/21 15:41:55 edankert Exp $
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

import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ReplaceDialog;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to find information in a XML Document.
 *
 * @version	$Revision: 1.8 $, $Date: 2004/10/21 15:41:55 $
 * @author Dogsbay
 */
 public class ReplaceAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ReplaceDialog dialog = null;

 	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;
	
 	/**
	 * The constructor for the action which allows addition of 
	 * documents to the application.
	 */
 	public ReplaceAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Replace...");

		putValue( MNEMONIC_KEY, new Integer( 'e'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_H, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Replace16.gif"));
		putValue( SHORT_DESCRIPTION, "Replace");
		
		this.parent = parent;
	 	this.properties = props;
		
		setEnabled( false);
 	}
	
	private ReplaceDialog getDialog() {
		if ( dialog == null) {
			dialog = new ReplaceDialog( parent, properties);
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
		
		if ( getDialog().isVisible()) {
			getDialog().setVisible(false);
		}
	}

	/**
	 * The implementation of the find action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		ReplaceDialog dialog = getDialog();
		
		if ( parent.getView() != null) {
			parent.getView().updateModel();

			dialog.init( parent.getView().getEditor().getSelectedText(), parent.getDocument().isError());
		}

		dialog.setVisible(true);
 	}
 }
