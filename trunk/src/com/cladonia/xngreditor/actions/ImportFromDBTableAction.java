/*
 * $Id: ImportFromDBTableAction.java,v 1.11 2004/10/27 10:43:53 tcurley Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.sql.Connection;

import javax.swing.AbstractAction;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ImportFromDBTableDialog;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.NonXMLDocumentChooserDialog;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xml.ExchangerDocument;

/**
 * An action that can be used to import non XML from a Database Table.
 *
 * @version	$Revision: 1.11 $, $Date: 2004/10/27 10:43:53 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ImportFromDBTableAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private NonXMLDocumentChooserDialog chooser = null;
    private ImportFromDBTableDialog dialog = null;
    private ConfigurationProperties props = null;
    
    /**
     * The constructor for the action which allows importing 
     * of database tables.
     *
     * @param parent the parent frame.
     */
    public ImportFromDBTableAction( ExchangerEditor parent, ConfigurationProperties props) {
        super( "From Database Table ...");
        
        this.parent = parent;
        this.props = props;
        
        putValue( MNEMONIC_KEY, new Integer( 'D'));
        putValue( SHORT_DESCRIPTION, "Database Table ...");
    }
    
    /**
     * The method which is called when the action is invoked
     * @param event the action event.
     */
    public void actionPerformed( ActionEvent event) {
        if ( dialog == null) {
            dialog = new ImportFromDBTableDialog( parent);
            
        }
        if ( chooser == null) {
            chooser = new NonXMLDocumentChooserDialog( parent, "Import",NonXMLDocumentChooserDialog.TYPE_DATABASE,props);
        }
        chooser.setTitle("Import","From Database Table","Connect to the database to import from");
        
        ExchangerView view = parent.getView();
        
        if ( view != null) {
            view.updateModel();
        }
        
        ExchangerDocument document = parent.getDocument();
        
        
        chooser.show(NonXMLDocumentChooserDialog.TYPE_DATABASE);
        Connection con = chooser.getCon();
        
        
        if ( !chooser.isCancelled()) {
            
            
            try {
                dialog.show(con,false,chooser.getDriver(), 
                        chooser.getUrlConnection(), chooser.getUsername(), 
                        chooser.getPassword());
                if ( !dialog.isCancelled()) {
                    parent.setWait( true);
                    parent.setStatus( "Importing From Database Table ...");
                    
                    // Run in Thread!!!
                    Runnable runner = new Runnable() {
                        public void run()  {
                            try {
                                
                                //ExchangerDocument newDocument = new ExchangerDocument( createXMLFile(dialog));
                                ExchangerDocument newDocument = new ExchangerDocument( dialog.getImportedXML() );
                                if(newDocument!=null) {
                                    parent.open( newDocument, null);
                                }
                            } catch ( Exception e) {
                                // This should never happen, just report and continue
                                MessageHandler.showError( parent, "Cannot Import From Database Table","Import From Database Table Error");
                            } finally {
                                parent.setStatus( "Done");
                                parent.setWait( false);
                            }
                        }
                    };
                    
                    // Create and start the thread ...
                    Thread thread = new Thread( runner);
                    thread.start();
//                  }
                }
                
                
                
            } catch ( Exception x) {
                MessageHandler.showError( "Could not create the Document:\n"+chooser.getInputLocation(), "Document Error");
            }
        }
    }
       
}
