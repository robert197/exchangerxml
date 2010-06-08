/*
 * $Id: SaveAsTemplateAction.java,v 1.5 2004/10/28 16:17:07 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractAction;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ChangeManager;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.template.TemplateProperties;
import com.cladonia.xngreditor.template.TemplatePropertiesDialog;

/**
 * An action that can be used to save a XML Document as a template.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/10/28 16:17:07 $
 * @author Dogsbay
 */
public class SaveAsTemplateAction extends AbstractAction {
 	private static final boolean DEBUG = false;
 	private ExchangerEditor parent = null;
 	private ConfigurationProperties properties = null;
	private TemplatePropertiesDialog dialog = null;

 	/**
	 * Constructs the save as action.
	 */
 	public SaveAsTemplateAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Save As Template ...");
		
		this.parent = parent;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'T'));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/SaveAs16.gif"));
		putValue( SHORT_DESCRIPTION, "Save As Template ...");
		
		setEnabled( false);
 	}
 	
	public void setDocument( ExchangerDocument document) {
		if ( document != null) {
			setEnabled( true);
		} else {
			setEnabled( false);
		}
	}

	/**
	 * The implementation of the save as template action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		if ( dialog == null) {
			dialog = new TemplatePropertiesDialog( parent, true);
		}
		
		ExchangerDocument document = parent.getDocument();
		
		Vector gs = properties.getTemplateProperties();
		Vector names = new Vector();

		for ( int i = 0; i < gs.size(); i++) {
			String name = ((TemplateProperties)gs.elementAt( i)).getName();
			
			names.addElement( name);
		}

		dialog.show( document.getName(), document.getURL(), names);

	 	ExchangerView view = parent.getView();
	 	Editor editor = view.getEditor();
	 	ChangeManager changeManager = view.getChangeManager();
		
		if ( !dialog.isCancelled()) {
			URL url = dialog.getURL();
			
			if ( url.getProtocol().equals( "file")) {
				File file = new File( url.getFile());

				parent.getView().updateModel();

				FileUtilities.saveAsDocument( file);
			}
			
			TemplateProperties props = new TemplateProperties();
			
			// update list...
			props.setName( dialog.getName());
			props.setURL( dialog.getURL());

			properties.addTemplateProperties( props);
	 	}
		
 	}
 }
