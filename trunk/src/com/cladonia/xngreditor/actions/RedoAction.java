/*
 * $Id: RedoAction.java,v 1.9 2005/04/25 08:28:14 tcurley Exp $
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
 * An action that can be used to save a XML Document.
 *
 * @version	$Revision: 1.9 $, $Date: 2005/04/25 08:28:14 $
 * @author Dogsbay
 */
 public class RedoAction extends AbstractAction implements ChangeListener {
 	private static final boolean DEBUG = false;

 	private ChangeManager handler	= null;
 	private ExchangerEditor parent 		= null;
	
 	/**
	 * The constructor for the action which allows addition of 
	 * documents to the application.
	 */
 	public RedoAction( ExchangerEditor parent) {
 		super( "Redo");

		putValue( MNEMONIC_KEY, new Integer( 'R'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_Y, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Redo16.gif"));
		putValue( SHORT_DESCRIPTION, "Redo");
		
		this.parent = parent;
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the redo action.
	 *
	 * @param e the action event.
	 */
	public void setChangeManager( ChangeManager undo) {
		if (DEBUG) System.out.println( "RedoAction.setChangeManager( "+undo+")");
		if ( handler != null) {
			handler.removeChangeListener( this);
		}
		
		handler = undo;

		if ( handler != null) {
			handler.addChangeListener( this);
			setEnabled( handler.canRedo());
			
		} else {
			setEnabled( false);
		}
		
	}

	public void stateChanged( ChangeEvent event) {
		if (DEBUG) System.out.println( "RedoAction.stateChanged( "+event+") ["+handler.canRedo()+"]");
		setEnabled( handler.canRedo());
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

		if ( handler.isDesignerRedo()) {
			if ( !(current instanceof Designer)) {
				try {
					parent.switchToDesigner();
				} catch ( Exception x) {
					// should not happen
					x.printStackTrace();
				}
			}
			
			handler.redo();
			// view.getDesigner().redo();
		} else if( handler.isPluginRedo()) {
		    
		    if ( !(current instanceof PluginViewPanel)) {
				try {
					parent.switchToPluginView((PluginViewPanel)current);
				} catch ( Exception x) {
					// should not happen
					x.printStackTrace();
				}
			}
			
			handler.redo();
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
			
			handler.redo();

			editor.updateCaretManual( false);
			editor.resetBookmarks();
			editor.revalidate();
			editor.repaint();
		}
		
		parent.getCurrent().setFocus();
 	}
 }
