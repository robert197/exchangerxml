/*
 * $Id: SelectAllAction.java,v 1.1 2004/03/25 18:53:18 edankert Exp $
 *
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.mozilla.org/MPL/ 
 *
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is eXchaNGeR browser code. (org.xngr.browser.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd.. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xml.editor.XmlEditorPane;

/**
 * An action that can be used to select all content in the
 * Xml Editor.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:53:18 $
 * @author Dogsbay
 */
 public class SelectAllAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private XmlEditorPane editor = null;

 	/**
	 * The constructor for the action which allows for selecting all
	 * the Xml Editor content.
	 *
	 * @param editor the XML Editor
	 */
 	public SelectAllAction( XmlEditorPane editor) {
		super( "Select All");
		
		if (DEBUG) System.out.println( "SelectAllAction( "+editor+")");
		
		putValue( MNEMONIC_KEY, new Integer( 'A'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F, InputEvent.CTRL_MASK, false));

		this.editor = editor;
 	}
 	
	/**
	 * The implementation of the select all action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "SelectAllAction.actionPerformed( "+event+")");
		
		editor.selectAll();
		editor.requestFocusInWindow();
 	}
}
