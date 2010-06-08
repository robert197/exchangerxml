/*
 * $Id: SubstituteCharactersAction.java,v 1.2 2004/10/28 16:17:07 edankert Exp $
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

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.SubstituteCharactersDialog;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to substitute the selected 
 * characters with entities.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/28 16:17:07 $
 * @author Dogsbay
 */
 public class SubstituteCharactersAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private SubstituteCharactersDialog dialog = null;
	private ConfigurationProperties properties = null;
	private Editor editor = null;
	private ExchangerEditor parent = null;

 	/**
	 * The constructor for the action which tags the selected text.
	 *
	 * @param editor the XML Editor
	 */
 	public SubstituteCharactersAction( ExchangerEditor parent, ConfigurationProperties props) {
		super( "Convert Characters to Entities...");
		
		if (DEBUG) System.out.println( "SusbtituteCharactersAction( "+parent+", "+props+")");
		
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ConvertToEntities16.gif"));
		putValue( SHORT_DESCRIPTION, "Convert Characters to Entities...");

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
		if ( view instanceof Editor) {
			editor = (Editor)view;
		} else {
			editor = null;
		}
		
		setDocument( parent.getDocument());
	}

	public void setDocument( ExchangerDocument doc) {
		if ( doc != null) {
			setEnabled( editor != null);
		} else {
			setEnabled( false);
		}
	}

	/**
	 * The implementation of the tag action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "TagAction.actionPerformed( "+event+")");
		
		if ( dialog == null) {
			dialog = new SubstituteCharactersDialog( parent, properties);
		}

		dialog.show();
		
		if ( !dialog.isCancelled()) {
			editor.substituteSelectedCharacters();
		}

		editor.setFocus();
 	}
}
