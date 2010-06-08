/*
 * $Id: ImportFromSQLXMLAction.java,v 1.11 2004/11/02 14:58:04 tcurley Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;

import javax.swing.AbstractAction;

import org.xml.sax.SAXParseException;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ImportFromSQLXMLDialog;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.NonXMLDocumentChooserDialog;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.ExchangerOutputFormat;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xml.editor.EditorProperties;

/**
 * An action that can be used to import non XML from a SQL/XML query.
 *
 * @version $Revision: 1.11 $, $Date: 2004/11/02 14:58:04 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ImportFromSQLXMLAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private NonXMLDocumentChooserDialog chooser = null;
    private ImportFromSQLXMLDialog dialog = null;
    private ConfigurationProperties props;
    
    /**
     * The constructor for the action which allows importing 
     * of database tables.
     *
     * @param parent the parent frame.
     */
    public ImportFromSQLXMLAction( ExchangerEditor parent, ConfigurationProperties props) {
        super( "From SQL/XML Query ...");
        
        this.parent = parent;
        this.props = props;
        
        putValue( MNEMONIC_KEY, new Integer( 'S'));
        putValue( SHORT_DESCRIPTION, "SQL/XML Query ...");
    }
    
    /**
     * The method which is called when the action is invoked
     * @param event the action event.
     */
    public void actionPerformed( ActionEvent event) {
        if ( dialog == null) {
            dialog = new ImportFromSQLXMLDialog( parent,props);
            
        }
        if ( chooser == null) {
            chooser = new NonXMLDocumentChooserDialog( parent, "Import",NonXMLDocumentChooserDialog.TYPE_DATABASE,props);
        }
        chooser.setTitle("Import","From SQL/XML Query","Connect to the database to import from");
        
        ExchangerView view = parent.getView();
        
        if ( view != null) {
            view.updateModel();
        }
        
        final ExchangerDocument document = parent.getDocument();
        
        chooser.show(NonXMLDocumentChooserDialog.TYPE_DATABASE);
        Connection con = chooser.getCon();
        //Connection con = null;
        
        
        if ( !chooser.isCancelled()) {
            
            
            
            
            dialog.show(con,false,chooser.getDriver(), 
                    chooser.getUrlConnection(), chooser.getUsername(), 
                    chooser.getPassword());
            if ( !dialog.isCancelled()) {
                parent.setWait( true);
                parent.setStatus( "Importing SQL/XML ...");
                
                // Run in Thread!!!
                Runnable runner = new Runnable() {
                    public void run()  {
                        try {
                            String xml = dialog.getImportedXML();
                            //System.out.println(xml);
                            ExchangerDocument newDocument = new ExchangerDocument( xml);
                            try {
                                String encoding = newDocument.getEncoding();
                                String result = format( xml, encoding, null);
                                newDocument = new ExchangerDocument( result);
                            } catch (SAXParseException e1) {
                                
                                MessageHandler.showError( parent, "Error - Result XML Is Not Well-Formed", e1, "Import Error");
                            } finally {
                            
                                parent.open( newDocument, null);
                            }
                        } catch ( Exception e) {
                            // This should never happen, just report and continue
                            MessageHandler.showError( parent, "Cannot Import SQL/XML", "Import From SQL/XML Error");
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
    
    public String format( String text, String encoding, String systemId) throws IOException, SAXParseException {
        ExchangerOutputFormat format = new ExchangerOutputFormat();
        return format( text, encoding, systemId, format);
    }
    
    public String format( String text, String encoding, String systemId, ExchangerOutputFormat format) throws IOException, SAXParseException {
        EditorProperties properties = this.props.getEditorProperties();
        
        String indent = "\t";
        boolean newLines = true;
        boolean padText = false;
        boolean preserveMixed = false;
        boolean trim = false;
        int lineLength = -1;
        
        switch ( properties.getFormatType()) {
            case EditorProperties.FORMAT_CUSTOM:
                if ( !properties.isCustomIndent()) {
                    indent = "";
                }
                
            newLines = properties.isCustomNewline();
            padText = properties.isCustomPadText();
            
            if ( properties.isWrapText()) {
                lineLength = properties.getWrappingColumn();
            }
            
            trim = properties.isCustomStrip();
            preserveMixed = properties.isCustomPreserveMixedContent();
            break;
            
            case EditorProperties.FORMAT_COMPACT:
                if ( properties.isWrapText()) {
                    lineLength = properties.getWrappingColumn();
                }
                
            indent = "";
            newLines = false;
            padText = false;
            trim = true;
            preserveMixed = false;
            break;
            case EditorProperties.FORMAT_STANDARD:
                if ( properties.isWrapText()) {
                    lineLength = properties.getWrappingColumn();
                }
                
            newLines = true;
            padText = false;
            
            trim = true;
            preserveMixed = true;
            break;
        }
        
        return XMLUtilities.format( text, systemId, encoding, indent, newLines, padText, lineLength, trim, preserveMixed, format);
    }
}
