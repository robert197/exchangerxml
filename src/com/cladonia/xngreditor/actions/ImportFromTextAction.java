/*
 * $Id: ImportFromTextAction.java,v 1.8 2004/10/27 10:43:53 tcurley Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ImportFromTextDialog;
import com.cladonia.xngreditor.ImportUtilities;
import com.cladonia.xngreditor.NonXMLDocumentChooserDialog;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.MessageHandler;
import java.net.URL;

/**
 * An action that can be used to import non XML from a text-based file.
 *
 * @version	$Revision: 1.8 $, $Date: 2004/10/27 10:43:53 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ImportFromTextAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private NonXMLDocumentChooserDialog chooser = null;
    private ImportFromTextDialog dialog = null;
    
    /**
     * The constructor for the action which allows importing 
     * of non - XML text-based Documents.
     *
     * @param parent the parent frame.
     */
    public ImportFromTextAction( ExchangerEditor parent) {
        super( "From Text File ...");
        
        this.parent = parent;
        //this.properties = props;
        
        putValue( MNEMONIC_KEY, new Integer( 'T'));
        putValue( SHORT_DESCRIPTION, "Text File ...");
    }
    
    /**
     * The implementation of the validate action, called 
     * after a user action.
     *
     * @param event the action event.
     */
    public void actionPerformed( ActionEvent event) {
        if ( dialog == null) {
            dialog = new ImportFromTextDialog( parent);
        }
        
        if ( chooser == null) {
            chooser = new NonXMLDocumentChooserDialog( parent, "Import",NonXMLDocumentChooserDialog.TYPE_TEXT,null);
        }
        chooser.setTitle("Import","From Text File","Choose the file to import from");
        
        ExchangerView view = parent.getView();
        
        if ( view != null) {
            view.updateModel();
        }
        
        ExchangerDocument document = parent.getDocument();
        
        if ( document != null) {
            chooser.show(NonXMLDocumentChooserDialog.TYPE_TEXT);
        } else{
            chooser.show(NonXMLDocumentChooserDialog.TYPE_TEXT);
        }
        
        if ( !chooser.isCancelled()) {
            
            
            URL url = chooser.getInputLocation();
            String encoding = chooser.getTextEncoding();
            
            dialog.show(url,encoding);
            if ( !dialog.isCancelled()) {
                
                parent.setWait( true);
                parent.setStatus( "Importing From Text File ...");
                
                // Run in Thread!!!
                Runnable runner = new Runnable() {
                    public void run()  {
                        try {
                            
                            
                            ExchangerDocument newDocument = new ExchangerDocument( ImportUtilities.createXMLFile(dialog.table,dialog.docField.getText(),dialog.rowField.getText(),dialog.checkConvertChars.isSelected()));
                            parent.open( newDocument, null);
                            
                            
                            
                            
                        } catch ( Exception e) {
                            // This should never happen, just report and continue
                            MessageHandler.showError( parent, "Cannot Import From Text File", "Import From Text Error");
                        } finally {
                            parent.setStatus( "Done");
                            parent.setWait( false);
                        }
                    }
                };
                
                // Create and start the thread ...
                Thread thread = new Thread( runner);
                thread.start();
//              }
            }
        }
    }
    
    
}
