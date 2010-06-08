/*
 * $Id: CollapseAllAction.java,v 1.1 2004/03/25 18:51:00 edankert Exp $
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
package com.cladonia.xml.viewer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import com.cladonia.xml.viewer.Viewer;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to collapse all nodes in the viewer.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:51:00 $
 * @author Dogsbay
 */
 public class CollapseAllAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private Viewer viewer = null;

 	/**
	 * The constructor for the action to collapse all nodes 
	 * in the viewer.
	 *
	 * @param editor the XML viewer
	 */
 	public CollapseAllAction( Viewer viewer) {
		super( "Collapse All");
		
		if (DEBUG) System.out.println( "CollapseAllAction( "+viewer+")");
		
		putValue( MNEMONIC_KEY, new Integer( 'C'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/viewer/icons/CollapseAll.gif"));
		putValue( SHORT_DESCRIPTION, "Collapses All Nodes");

		this.viewer = viewer;
 	}
 	
	/**
	 * The implementation of the collapse all action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "CollapseAllAction.actionPerformed( "+event+")");

		viewer.collapseAll();
 	}
}
