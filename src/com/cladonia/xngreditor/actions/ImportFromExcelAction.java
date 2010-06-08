/*
 * $Id: ImportFromExcelAction.java,v 1.9 2004/10/27 10:43:53 tcurley Exp $
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
import com.cladonia.xngreditor.ImportFromExcelDialog;
import com.cladonia.xngreditor.ImportUtilities;
import com.cladonia.xngreditor.NonXMLDocumentChooserDialog;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.MessageHandler;
import java.net.URL;

/**
 * An action that can be used to import non XML from a excel file.
 *
 * @version	$Revision: 1.9 $, $Date: 2004/10/27 10:43:53 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ImportFromExcelAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private NonXMLDocumentChooserDialog chooser = null;
    private ImportFromExcelDialog dialog = null;
    
    /**
     * The constructor for the action which allows importing 
     * of excel Documents.
     *
     * @param parent the parent frame.
     */
    public ImportFromExcelAction( ExchangerEditor parent) {
        super( "From Excel File ...");
        
        this.parent = parent;
        //this.properties = props;
        
        putValue( MNEMONIC_KEY, new Integer( 'E'));
        putValue( SHORT_DESCRIPTION, "Excel File ...");
    }
    
    /**
     * The implementation of the validate action, called 
     * after a user action.
     *
     * @param event the action event.
     */
    public void actionPerformed( ActionEvent event) {
        if ( dialog == null) {
            dialog = new ImportFromExcelDialog( parent);
        }
        if ( chooser == null) {
            chooser = new NonXMLDocumentChooserDialog( parent, "Import",NonXMLDocumentChooserDialog.TYPE_EXCEL,null);
        }
        chooser.setTitle("Import","From Excel File","Choose the file to import from");
        ExchangerView view = parent.getView();
        
        if ( view != null) {
            view.updateModel();
        }
        
        final ExchangerDocument document = parent.getDocument();
        try {
            chooser.show(NonXMLDocumentChooserDialog.TYPE_EXCEL);
            
            
            
            if ( !chooser.isCancelled()) {
                try {
                    
                    URL url = chooser.getInputLocation();
                    
                    dialog.show(url);
                    if ( !dialog.isCancelled()) {
                        parent.setWait( true);
                        parent.setStatus( "Importing From Excel ...");
                        
                        // Run in Thread!!!
                        Runnable runner = new Runnable() {
                            public void run()  {
                                try {
                                    String xml = ImportUtilities.createXMLFile(dialog.table,dialog.docField.getText(),dialog.rowField.getText(),dialog.checkConvertChars.isSelected());
                                    ExchangerDocument newDocument = new ExchangerDocument( xml);
                                    parent.open( newDocument, null);
                                } catch ( Exception e) {
                                    // This should never happen, just report and continue
                                    MessageHandler.showError( parent, "Cannot Import Document", "Import From Excel Error");
                                } finally {
                                    parent.setStatus( "Done");
                                    parent.setWait( false);
                                }
                            }
                        };
                        
                        // Create and start the thread ...
                        Thread thread = new Thread( runner);
                        thread.start();
//                      }
                        
                        
                    }
                    
                    
                    
                } catch ( Exception x) {
                    MessageHandler.showError( "Could not create the Document:\n"+chooser.getInputLocation(), "Document Error");
                }
                
                
            }
        }catch (Exception x) {
            MessageHandler.showError( "Could not open the Document:\n"+chooser.getInputLocation(), "Document Error");
        }
    }
    
    
}
