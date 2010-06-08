/*
 * $Id: ConvertSVGAction.java,v 1.1 2004/03/25 18:53:19 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xml.svg.ConvertSVGDialog;
import com.cladonia.xngreditor.ExchangerEditor;

/**
 * Converts one grammar to another, using Trang.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:53:19 $
 * @author Dogsbay
 */
public class ConvertSVGAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ConvertSVGDialog dialog = null;
	private ExchangerEditor parent = null;

 	/**
	 * The constructor for the action which allows for
	 * converting SVG documents...
	 *
	 * @param parent the parent frame.
	 */
 	public ConvertSVGAction( ExchangerEditor parent) {
 		super( "Convert SVG");

		putValue( MNEMONIC_KEY, new Integer( 'n'));
		putValue( SHORT_DESCRIPTION, "Convert SVG");
		
		this.parent = parent;
 	}
 	
 	/**
 	 * The implementation of the open grammar action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
		if ( dialog == null) {
			dialog = new ConvertSVGDialog( parent);
		}
		
		dialog.show( parent.getDocument());
 	}
}
