/*
 * $Id: SynchroniseSelectionAction.java,v 1.5 2005/03/09 17:05:38 tcurley Exp $
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

import com.cladonia.xml.XElement;
import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xml.viewer.Viewer;
import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xngreditor.ExchangerEditor;

/**
 * An action that can be used to collapse all nodes in the viewer.
 *
 * @version	$Revision: 1.5 $, $Date: 2005/03/09 17:05:38 $
 * @author Dogsbay
 */
public class SynchroniseSelectionAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private Object view = null;
	private ExchangerEditor parent = null;
	
 	/**
	 * The constructor for the action to collapse all nodes 
	 * in the viewer.
	 *
	 * @param editor the XML viewer
	 */
 	public SynchroniseSelectionAction( ExchangerEditor parent) {
		super( "Synchronise Selection");
		
		this.parent = parent;
		
		putValue( MNEMONIC_KEY, new Integer( 'y'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/CollapseAll.gif"));
		putValue( SHORT_DESCRIPTION, "Synchronise the Selection");
		
		setEnabled( false);
 	}
 	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		if ( view instanceof Designer) {
			this.view = view;
		} else if ( view instanceof Viewer) {
			this.view = view;
		} else if ( view instanceof Editor) {
			this.view = view;
		} else if ( view instanceof PluginViewPanel) {
		    this.view = view;
		} else {
			this.view = null;
		}
		
		setEnabled( this.view != null);
	}

	/**
	 * The implementation of the collapse all action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
		XElement element = parent.getView().getPreviousSelectedElement();
		
		if ( element != null) {
		 	if ( view instanceof Designer) {
			 	((Designer)view).setSelectedNode( element, -1);
		 	} else if ( view instanceof Viewer) {
		 		((Viewer)view).setSelectedElement( element, false, -1);
		 	} else if ( view instanceof PluginViewPanel) {
		 	    ((PluginViewPanel)view).setSelectedElement( element);
		 	} else if ( view instanceof Editor) {
			 	if ( element.getContentStartPosition() > 0) {
			 		((Editor)view).setCursorPosition( element.getContentStartPosition());
			 	} else {
			 		((Editor)view).setCursorPosition( element.getElementEndPosition());
			 	}
		 	}
		}
 	}
}
