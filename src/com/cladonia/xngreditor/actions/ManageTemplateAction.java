/*
 * $Id: ManageTemplateAction.java,v 1.1 2004/03/25 18:53:18 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.template.TemplateManagementDialog;

/**
 * An action that can be used to manage templates.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:53:18 $
 * @author Dogsbay
 */
 public class ManageTemplateAction extends AbstractAction {
 	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;
	private TemplateManagementDialog dialog = null;

 	/**
	 * The constructor for the action which manages templates.
	 *
	 * @param parent the parent frame.
	 */
 	public ManageTemplateAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Manage Templates");

		this.parent = parent;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'M'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_MASK, false));
		putValue( SHORT_DESCRIPTION, "Manage Templates");
 	}
 	
	/**
	 * The implementation of the manage template action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		if ( dialog == null) {
			dialog = new TemplateManagementDialog( parent, properties);
		}
		
		dialog.show();
	}
}
