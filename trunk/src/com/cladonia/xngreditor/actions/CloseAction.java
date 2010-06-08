/*
 * $Id: CloseAction.java,v 1.7 2004/07/21 09:09:42 knesbitt Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;

/**
 * An action that can be used to close a XML Document.
 *
 * @version	$Revision: 1.7 $, $Date: 2004/07/21 09:09:42 $
 * @author Dogsbay
 */
 public class CloseAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ExchangerEditor parent = null;
	
 	/**
	 * The constructor for the action which allows addition of 
	 * documents to the application.
	 */
 	public CloseAction( ExchangerEditor parent) {
 		super( "Close");

		putValue( MNEMONIC_KEY, new Integer( 'C'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_W, InputEvent.CTRL_MASK, false));
		putValue( SHORT_DESCRIPTION, "Close current Document.");
		
		this.parent = parent;

		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the save document action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		ExchangerView view = parent.getView();
		
		parent.close( view);
		
		view = parent.getView();
		
		if ( view != null) {
			view.getCurrentView().setFocus();
		}
		else
		{
			parent.setIntialFocus();
		}
		
		
		// make sure this runs after the gui is updated!	
		SwingUtilities.invokeLater( new Runnable() {
		    public void run() {
			    System.gc();
		    }
		});		
 	}
 }
