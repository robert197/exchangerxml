/*
 * $Id: ExpandAllAction.java,v 1.2 2005/08/26 11:03:41 tcurley Exp $
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
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to expand all nodes in a tree view.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/08/26 11:03:41 $
 * @author Dogsbay
 */
public class ExpandAllAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private XSLTDebuggerPane debugger = null;
	
 	/**
	 * The constructor for the action to collapse all nodes 
	 * in the viewer.
	 *
	 * @param editor the XML viewer
	 */
 	public ExpandAllAction( XSLTDebuggerPane debugger) {
		super( "Expand All");
		
		this.debugger = debugger;
		
		putValue( MNEMONIC_KEY, new Integer( 'x'));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ExpandAll.gif"));
		putValue( SHORT_DESCRIPTION, "Expands All Nodes");

	 	setEnabled( false);
 	}
 	
	/**
	 * The implementation of the collapse all action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "ExpandAllAction.actionPerformed( "+event+")");
 		
 		if(debugger.getSelectedPane() != null) {
 			debugger.getSelectedPane().expandAll();
 		}
 	}
}
