/*
 * $Id: ErrorPane.java,v 1.8 2005/06/23 15:14:05 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.bounce.QLabel;
import org.bounce.event.DoubleClickListener;
import org.bounce.event.PopupListener;

import com.cladonia.xml.XMLError;
import com.cladonia.xml.editor.Editor;
//import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xngreditor.ErrorPane.ErrorListModel;
import com.cladonia.xngreditor.component.GUIUtilities;
import com.cladonia.xngreditor.component.ScrollableListPanel;
import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The panel that shows parsing error information.
 *
 * @version	$Revision: 1.8 $, $Date: 2005/06/23 15:14:05 $
 * @author Dogsbay
 */
 public class SchematronErrorPane extends ErrorPane {
	 /**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	private static final boolean DEBUG = false;
	
	private ExchangerEditor xngr = null;
	
	
	public SchematronErrorPane(ExchangerEditor xngreditor) {
		super(xngreditor);
		this.xngr = xngreditor;
		//initialise errorlist and model
		errorList = new ErrorList();
	 	
	}
	
	@Override
	protected void errorSelected() {
		// TODO Auto-generated method stub
		try {
			if(super.getEditor() != null) {
				super.errorSelected();
			}
			else {
				Object item = super.getList().getSelectedValue();
		 		Object view1 = xngr.getView().getCurrentView();
		 		
		 		if((xngr.getView().getCurrentView() instanceof Editor) && (item != null) && (item instanceof XMLError)) {
		 			XMLError error = (XMLError)item;
		 			String systemId = error.getSystemId();

		 			if ( systemId != null) {
		 				String name = xngr.getDocument().getName();
		 				
		 				if ( !systemId.endsWith( name)) {
		 					// need to do this in a thread.
		 					xngr.open( URLUtilities.toURL( systemId), null, false);

		 					ExchangerView view = xngr.getView();
		 					if ( view != null) {
		 						view.getEditor().selectError( (XMLError)item);
		 						view.getEditor().setFocus();
		 					}
		 					return;
		 				}
		 			}
		 			((Editor)xngr.getView().getCurrentView()).selectError( error);
		 			((Editor)xngr.getView().getCurrentView()).setFocus();
		 		}
			}
		}catch(NullPointerException e) {
			//this usually happens when there is no document open
			Object item = super.getList().getSelectedValue();
			if(item != null) {
				XMLError error = (XMLError)item;
				try {
					
		 			String systemId = error.getSystemId();
		
		 			if ( systemId != null) {
		 				
	 					// need to do this in a thread.
		 				xngr.open( URLUtilities.toURL( systemId), null, false);
		 				
	 					ExchangerView view = xngr.getView();
	 					if ( view != null) {
	 						errorList.setDocument(xngr.getDocument());
	 						view.getEditor().addToErrors(errorList);
	 						
	 						view.getEditor().selectError( (XMLError)item);
	 						view.getEditor().setFocus();
	 					}
	 					return;
		 				
		 			}
		 			//editor.selectError( error);
					//editor.setFocus();
				} catch(Exception ex) {
					ex.printStackTrace();
					MessageHandler.showError("Cannot open file: "+error.getSystemId(), JOptionPane.MESSAGE_PROPERTY);
				}
			}
		}
	}
 	
 	
	
	
}
