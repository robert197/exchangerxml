/*
 * $Id: UndoAction.java,v 1.8 2005/03/09 17:02:26 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xngreditor.ChangeManager;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.ViewPanel;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to undo change in the document.
 *
 * @version	$Revision: 1.8 $, $Date: 2005/03/09 17:02:26 $
 * @author Dogsbay
 */
 public class UndoAction extends AbstractAction implements ChangeListener {
 	private static final boolean DEBUG = false;

 	private ChangeManager handler	= null;
 	private ExchangerEditor parent 		= null;
	
 	/**
	 * The constructor for the action which allows addition of 
	 * documents to the application.
	 */
 	public UndoAction( ExchangerEditor parent) {
 		super( "Undo");

		putValue( MNEMONIC_KEY, new Integer( 'U'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_Z, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Undo16.gif"));
		putValue( SHORT_DESCRIPTION, "Undo");
		
		this.parent = parent;
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the undo action.
	 *
	 * @param e the action event.
	 */
	public void setChangeManager( ChangeManager undo) {
		if (DEBUG) System.out.println( "UndoAction.setChangeManager( "+undo+")");
		if ( handler != null) {
			handler.removeChangeListener( this);
		}
		
		handler = undo;

		if ( handler != null) {
			handler.addChangeListener( this);
			setEnabled( handler.canUndo());
			
		} else {
			setEnabled( false);
		}
		
	}

	public void stateChanged( ChangeEvent event) {
		if (DEBUG) System.out.println( "UndoAction.stateChanged( "+event+") ["+handler.canUndo()+"]");
		setEnabled( handler.canUndo());
	}

	/**
	 * The implementation of the redo action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		ExchangerView view = parent.getView();
		ViewPanel current = view.getCurrentView();
		
		if ( current instanceof Designer) {
			((Designer)current).selectionChanged();
		}

		if ( handler.isDesignerUndo()) {
			if ( !(current instanceof Designer)) {
				try {
					parent.switchToDesigner();
				} catch ( Exception x) {
					// should not happen
					x.printStackTrace();
				}
			}
			
			handler.undo();
			// view.getDesigner().redo();
		} else if( handler.isPluginUndo()) {
		    
		    if ( !(current instanceof PluginViewPanel)) {
				try {
					parent.switchToPluginView((PluginViewPanel)current);
				} catch ( Exception x) {
					// should not happen
					x.printStackTrace();
				}
			}
			
			handler.undo();
		    
		} else {
			Editor editor = parent.getView().getEditor();
			editor.setFocus();
			editor.updateCaretManual( true);
			editor.updateBookmarks();

			if ( !(current instanceof Editor)) {
				try {
					parent.switchToEditor();
				} catch ( Exception x) {
					// should not happen
					x.printStackTrace();
				}
			}
			
			handler.undo();

			editor.updateCaretManual( false);
			editor.resetBookmarks();
			editor.revalidate();
			editor.repaint();
		}
		
		parent.getCurrent().setFocus();
 	}
 }