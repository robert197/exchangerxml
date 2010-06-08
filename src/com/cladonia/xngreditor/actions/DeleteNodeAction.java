/*
 * $Id: DeleteNodeAction.java,v 1.3 2004/07/21 09:09:42 knesbitt Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xml.designer.AttributeNode;
import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.designer.DesignerListener;
import com.cladonia.xml.designer.DesignerNode;
import com.cladonia.xml.designer.ElementNode;

/**
 * An action that can be used to Delete a designer node.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/07/21 09:09:42 $
 * @author Dogsbay
 */
public class DeleteNodeAction extends AbstractAction implements DesignerListener {
 	private static final boolean DEBUG = false;

 	private Designer designer = null;
 
 	/**
	 * The constructor for the copy action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public DeleteNodeAction() {
 		super( "Delete");

	 	putValue( MNEMONIC_KEY, new Integer( 'D'));
 		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Copy16.gif"));
	 	putValue( SHORT_DESCRIPTION, "Delete a Node");
 	
 		setEnabled( false);
 	}
 	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		if ( designer != null) {
			designer.removeDesignerListener( this);
		}

		if ( view instanceof Designer) {
			designer = (Designer)view;
			designer.addDesignerListener( this);
			selectionChanged( designer.getSelectedNode());
		} else {
			designer = null;
			setEnabled( false);
		}
	}
 
	public void selectionChanged( DesignerNode node) {
		boolean enable = false;
		
		if ( node != null) {
			if ( node instanceof AttributeNode) {
				AttributeNode a = (AttributeNode)node;

				if ( !a.isVirtual()) {
					putValue( NAME, "Delete Attribute");
					enable = true;
				}
			} else if ( node instanceof ElementNode) {
				ElementNode e = (ElementNode)node;

				if ( !e.isVirtual()) {
					putValue( NAME, "Delete Element");
					enable = true;
				} 
			} 
		} 
		
		setEnabled( enable);
	}
 
	/**
 	 * The implementation of the delete node action.
	 *
	 * @param e the action event.
	 */
	public void actionPerformed( ActionEvent e) {
		designer.delete();
	}
}
