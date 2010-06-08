/*
 * $Id: ReloadAction.java,v 1.2 2004/08/02 09:08:56 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.MessageHandler;

/**
 * An action that can be used to reload a XML Document.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/08/02 09:08:56 $
 * @author Dogsbay
 */
public class ReloadAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ExchangerEditor parent = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public ReloadAction( ExchangerEditor parent) {
 		super( "Reload");
		
		this.parent = parent;

		putValue( MNEMONIC_KEY, new Integer( 'R'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.CTRL_MASK, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Open16.gif"));
		putValue( SHORT_DESCRIPTION, "Reload current XML Document");
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the add document action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 		execute();
 	}
 	
 	public void execute() {
		int result = JOptionPane.YES_OPTION;
		
		if ( parent.getChangeManager().isChanged()) {
	 		result = MessageHandler.showConfirm( "Are you sure you want to discard changes to \""+parent.getDocument().getName()+"\"?");
		}

 		if ( result == JOptionPane.YES_OPTION) {

	 		parent.setWait( true);
	 		parent.setStatus( "Loading ...");

	 		// Run in Thread!!!
	 		Runnable runner = new Runnable() {
	 			public void run()  {
			 		try {
			 	        parent.getView().reload();
			 		} finally {
				 		parent.setStatus( "Done");
				 		parent.setWait( false);
			 		}
	 			}
	 		};
	 		
	 		// Create and start the thread ...
	 		Thread thread = new Thread( runner);
	 		thread.start();
 		}
 	}
	
}
