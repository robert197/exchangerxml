/*
 * $Id: SaveAsAction.java,v 1.6 2004/11/04 19:20:50 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to save a XML Document with a different name.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/11/04 19:20:50 $
 * @author Dogsbay
 */
 public class SaveAsAction extends AbstractAction {
 	private static final boolean DEBUG = false;
 	private ExchangerEditor parent = null;
 	private ConfigurationProperties properties = null;

 	/**
	 * Constructs the save as action.
	 */
 	public SaveAsAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Save As ...");
		
		this.parent = parent;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'A'));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/SaveAs16.gif"));
		putValue( SHORT_DESCRIPTION, "Save current Document As...");
		
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
 		execute();
 	}
 	
 	public void execute() { 
	 	ExchangerView view = parent.getView();
		File file = FileUtilities.selectOutputFile( (File)null, null);
		
		if ( file != null) {
			view.updateModel();

			FileUtilities.saveAsDocument( file);

			if ( view != null) {
				view.getCurrentView().setFocus();
			}
	 	}
 	}
}
