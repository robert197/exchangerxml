/*
 * $Id: CollapseAllAction.java,v 1.4 2005/01/28 12:24:21 tcurley Exp $
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
import javax.swing.ImageIcon;

import com.cladonia.schema.viewer.SchemaViewer;
import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xml.viewer.Viewer;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.plugins.PluginViewPanel;


/**
 * An action that can be used to collapse all nodes in the viewer.
 *
 * @version	$Revision: 1.4 $, $Date: 2005/01/28 12:24:21 $
 * @author Dogsbay
 */
public class CollapseAllAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private Object view = null;
	
 	/**
	 * The constructor for the action to collapse all nodes 
	 * in the viewer.
	 *
	 * @param editor the XML viewer
	 */
 	public CollapseAllAction() {
		super( "Collapse All");
		
		putValue( MNEMONIC_KEY, new Integer( 'C'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/CollapseAll.gif"));
		putValue( SHORT_DESCRIPTION, "Collapses All Nodes");
		
		setEnabled( false);
 	}
 	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		this.view = view;
		
		setEnabled( this.view != null);
	}

	/**
	 * The implementation of the collapse all action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "CollapseAllAction.actionPerformed( "+event+")");
		
	 	if ( view instanceof Designer) {
		 	((Designer)view).collapseAll();
		 	((Designer)view).setFocus();
	 	} else if ( view instanceof Viewer) {
	 		((Viewer)view).collapseAll();
		 	((Viewer)view).setFocus();
	 	} else if ( view instanceof SchemaViewer) {
		 	((SchemaViewer)view).collapseAll();
		 	((SchemaViewer)view).setFocus();
	 	} else if ( view instanceof Editor) {
		 	((Editor)view).collapseAll();
		 	((Editor)view).setFocus();
	 	} else if ( view instanceof PluginViewPanel) {
		 	((PluginViewPanel)view).collapseAll();
		 	((PluginViewPanel)view).setFocus();
	 	}
 	}
}
