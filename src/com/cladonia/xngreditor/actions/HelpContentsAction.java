/*
 * $Id: HelpContentsAction.java,v 1.2 2004/06/03 18:03:35 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import com.cladonia.xngreditor.ExchangerEditor;

/**
 * An action that can be used show the contents help.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/06/03 18:03:35 $
 * @author Dogsbay
 */
public class HelpContentsAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private ExchangerEditor parent = null;
	private HelpSet helpset = null;

 	/**
 	 * The constructor for the copy action.
 	 *
 	 * @param editor the editor to copy information from.
 	 */
 	public HelpContentsAction( ExchangerEditor parent, HelpSet set) {
 		super( "Contents");

		putValue( MNEMONIC_KEY, new Integer( 'C'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0, false));
		putValue( SHORT_DESCRIPTION, "Help Contents");
		
		this.parent = parent;
		this.helpset = set;

//	 	ClassLoader loader = this.getClass().getClassLoader();
//	 	URL url;
//	 	try {
//	 	    url = HelpSet.findHelpSet( null, "xngrplus.hs");
//	 	    debug ("findHelpSet url=" + url);
//
//	 	    if ( url == null) {
//		 		url = new URL( getCodeBase(), helpSetURL);
//	 		debug("codeBase url=" + url);
//	 	    }
//	 	    hs = new HelpSet(loader, url);
//	 	} catch (Exception ee) {
//	 	    System.out.println ("Trouble in createHelpSet;");
//	 	    ee.printStackTrace();
//	 	    return;
//	 	}
 	}
 	
	/**
	 * The implementation of the cut action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
	    HelpBroker helpbroker = helpset.createHelpBroker( "MainWindow");
		helpbroker.setDisplayed( true);
 	}
 }
