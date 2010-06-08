/*
 * $Id: SaveAction.java,v 1.8 2005/08/29 08:31:25 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ChangeManager;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.component.AutomaticProgressMonitor;

/**
 * An action that can be used to save a XML Document.
 *
 * @version	$Revision: 1.8 $, $Date: 2005/08/29 08:31:25 $
 * @author Dogsbay
 */
 public class SaveAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ExchangerEditor parent = null;
	private ExchangerDocument document = null;
	
 	/**
	 * The constructor for the action which allows addition of 
	 * documents to the application.
	 */
 	public SaveAction( ExchangerEditor parent) {
 		super( "Save");

		putValue( MNEMONIC_KEY, new Integer( 'S'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_S, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Save16.gif"));
		putValue( SHORT_DESCRIPTION, "Save current Document");
		
		this.parent = parent;

		setEnabled( false);
 	}
 	
	public void setDocument( ExchangerDocument document) {
		this.document = document;
		
		setEnabled( document != null);
	}

	/**
	 * The implementation of the save document action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		//try {
		//	LicenseManager licenseManager = LicenseManager.getInstance();
		//	licenseManager.isValid( com.cladonia.license.KeyGenerator.generate(2), "Exchanger XML Editor");
		//} catch (Exception x) {
		//	System.exit(0);
		//	return;
		//}

		if ( document != null) {
			if ( document.isReadOnly() || (document.getURL() == null)) {
				parent.getSaveAsAction().execute();
			} else {
				parent.setWait( true);
				parent.setStatus( "Saving ...");
	
				// Run in Thread!!!
				Runnable runner = new Runnable() {
					public void run()  {
						try {
							parent.getView().updateModel();
							ChangeManager changeManager = parent.getView().getChangeManager();
	
							AutomaticProgressMonitor monitor = new AutomaticProgressMonitor( parent, null, "Saving \""+URLUtilities.toString( document.getURL())+"\".", 250);
	
							monitor.start();
							document.save();
							monitor.stop();
							
							changeManager.markSave();
						} catch ( IOException x) {
							JOptionPane.showMessageDialog(	parent,
														    "Could not save "+document.getName()+"\n"+
															x.getMessage(),
														    "Save Error",
														    JOptionPane.ERROR_MESSAGE);
						
						} finally {
							ExchangerView view = parent.getView();
							
							if ( view != null) {
								view.getCurrentView().setFocus();
							}
	
							parent.setStatus( "Done");
							parent.setWait( false);
						}
					}
				};
				
				// Create and start the thread ...
				Thread thread = new Thread( runner);
				thread.start();
			}
		}
 	}
 }
