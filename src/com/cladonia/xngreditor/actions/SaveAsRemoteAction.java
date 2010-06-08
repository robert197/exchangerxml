/*
 * $Id: SaveAsRemoteAction.java,v 1.6 2004/10/21 15:41:24 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.SaveRemoteDocumentDialog;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to save a XML Document with a different name.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/10/21 15:41:24 $
 * @author Dogsbay
 */
 public class SaveAsRemoteAction extends AbstractAction {
 	private static final boolean DEBUG = false;
 	private ExchangerEditor parent = null;
 	private ConfigurationProperties properties = null;
 	private SaveRemoteDocumentDialog dialog = null;

 	/**
	 * Constructs the save as action.
	 */
 	public SaveAsRemoteAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Save As Remote ...");
		
		this.parent = parent;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'R'));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/SaveAsRemote16.gif"));
		putValue( SHORT_DESCRIPTION, "Save As Remote Document ...");
		
		dialog = new SaveRemoteDocumentDialog( props, parent);
		
		setEnabled( false);
 	}
 	
	public void setDocument( ExchangerDocument document) {
		setEnabled( document != null);
	}

	/**
	 * The implementation of the save document action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
	 	Editor editor = parent.getView().getEditor();

	 	GrammarProperties type = parent.getGrammar();

	 	dialog.show( "ftp://");
		
		if ( !dialog.isCancelled()) {
	        URL url = dialog.getURL();
	        
			parent.getView().updateModel();

			FileUtilities.saveAsRemoteDocument( url);

			ExchangerView view = parent.getView();
			
			if ( view != null) {
				view.getCurrentView().setFocus();
			}
		}
 	}
}
