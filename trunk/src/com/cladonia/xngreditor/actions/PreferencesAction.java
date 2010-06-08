/*
 * $Id: PreferencesAction.java,v 1.3 2004/10/13 18:25:51 edankert Exp $
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
import com.cladonia.xngreditor.PreferencesDialog;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to show the preferences for the xngreditor app.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/10/13 18:25:51 $
 * @author Dogsbay
 */
 public class PreferencesAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ExchangerEditor parent = null;
 	private PreferencesDialog dialog = null;
	private ConfigurationProperties properties = null;
	
 	/**
	 * The constructor for the action which allows addition of 
	 * documents to the application.
	 */
 	public PreferencesAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Preferences");

		putValue( MNEMONIC_KEY, new Integer( 'e'));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Preferences16.gif"));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F4, InputEvent.ALT_MASK, false));
		putValue( SHORT_DESCRIPTION, "Preferences");
		
		this.parent = parent;
		this.properties = props;
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
		if ( dialog == null) {
			dialog = new PreferencesDialog( parent, properties);
		}

		dialog.show();
	}
 }
