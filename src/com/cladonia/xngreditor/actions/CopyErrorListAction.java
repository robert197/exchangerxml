/*
 * $Id: CopyErrorListAction.java,v 1.1 2005/04/12 15:45:17 tcurley Exp $
 *
 * CopyErrorListright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListModel;

import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ErrorPane;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to Copy the error list information 
 * in a XML Document.
 *
 * @version	$Revision: 1.1 $, $Date: 2005/04/12 15:45:17 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
 public class CopyErrorListAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ExchangerEditor parent = null;
	
 	/**
	 * The constructor for the CopyErrorList action.
	 *
	 * @param editor the editor to CopyErrorList information from.
	 */
 	public CopyErrorListAction( ExchangerEditor parent) {
 		super( "Copy To Clipboard");

		putValue( MNEMONIC_KEY, new Integer( 'C'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Copy16.gif"));
		putValue( SHORT_DESCRIPTION, "Copy To Clipboard");
		
		this.parent = parent;
		
		setEnabled( false);
 	}
 	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		setEnabled( view instanceof Editor);
	}

	/**
	 * The implementation of the CopyErrorList action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		try {
            ErrorPane errors = parent.getOutputPanel().getErrorPane();
            JList list = errors.getList();
            ListModel model = (ListModel)list.getModel();
            StringBuffer buffer = new StringBuffer("");
            for(int cnt=0;cnt<model.getSize();++cnt) {
                
                buffer.append(model.getElementAt(cnt)+"\n");
            }
            String str = buffer.toString();
            StringSelection ss = new StringSelection(str);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        }
        catch (Exception e1) {
            // catch HeadlessException
            //e1.printStackTrace();
            MessageHandler.showError(parent, "Error copying contents to clipboard", "Copy To Clipboard Error");
        }
	    
 	}
 }
