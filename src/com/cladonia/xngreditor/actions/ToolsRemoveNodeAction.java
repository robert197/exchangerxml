/*
 * $Id: ToolsRemoveNodeAction.java,v 1.10 2005/03/10 10:54:47 tcurley Exp $ 
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.dom4j.Node;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.ToolsRemoveNodeDialog;


/**
 * An action that can be used to Remove Node By XPath
 *
 * @version	$Revision: 1.10 $, $Date: 2005/03/10 10:54:47 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsRemoveNodeAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private Editor editor = null;
    private ConfigurationProperties props;
    private ToolsRemoveNodeDialog dialog = null;
    
    private final int YES = 0;
    private final int NO = 1;
    private final int CANCEL = 2;
    
    /**
     * The constructor for the action which allows Remove Node By XPath
     *
     * @param parent the parent frame.
     */
    public ToolsRemoveNodeAction( ExchangerEditor parent, Editor editor, ConfigurationProperties props) {
        super( "Remove Nodes ...");
        
        this.parent = parent;
        this.props = props;
        
        //this.properties = props;
        
        putValue( MNEMONIC_KEY, new Integer( 'R'));
        putValue( SHORT_DESCRIPTION, "Remove Nodes ...");
    }
    
    /**
     * Sets the current view.
     *
     * @param view the current view.
     */
    public void setView( Object view) {
        if ( view instanceof Editor) {
            editor = (Editor)view;
        } else {
            editor = null;
        }
        
        setDocument( parent.getDocument());
    }
    
    public void setDocument( ExchangerDocument doc) {
        if ( doc != null && doc.isXML()) {
            setEnabled( editor != null);
        } else {
            setEnabled( false);
        }
    }
    
    
    /**
     * The implementation of the validate action, called 
     * after a user action.
     *
     * @param event the action event.
     */
    public void actionPerformed( ActionEvent event) {
        if ( dialog == null) {
            dialog = new ToolsRemoveNodeDialog( parent,props);
        }
        //called to make sure that the model is up to date to 
        //prevent any problems found when undo-ing etc.
        parent.getView().updateModel();
        
        //get the document
        final ExchangerDocument document = parent.getDocument();
        
        if ( document.isError()) {
            MessageHandler.showError( parent,"Please make sure the document is well-formed.", "Parser Error");
            return;
        }
        
        //create temporary document
        String currentXPath = null;
        Node node = (Node)document.getLastNode( parent.getView().getEditor().getCursorPosition(), true);

        if ( props.isUniqueXPath()) {
            currentXPath = node.getUniquePath();
        } else {
            currentXPath = node.getPath();
        }
        dialog.show(currentXPath);
        
        if(!dialog.isCancelled()) {
            //get the new vector of namespaces
            //Vector newNamespaces = dialog.getNamespaces();
            parent.setWait( true);
            parent.setStatus( "Removing Nodes ...");
            
            // Run in Thread!!!
            Runnable runner = new Runnable() {
                public void run()  {
                    try {
                        ExchangerDocument tempDoc =  new ExchangerDocument(document.getText());
                        
                        String newString  = removeByXPath(tempDoc,
                                dialog.xpathPanel.getXpathPredicate());
                        if(newString!=null) {
                            
                            //need to parse the new document to make sure that it
                            //will produce well-formed xml.
                            ExchangerDocument newDocument =  new ExchangerDocument(newString);
                            boolean createDocument=true;
                            
                            if(newDocument.isError()) {
                                int questionResult = MessageHandler.showConfirm(parent,"The resulting document will not be well-formed\n"+
                                        "Do you wish to continue?");
                                
                                if(questionResult==MessageHandler.CONFIRM_NO_OPTION) {
                                    createDocument=false;
                                }
                            }
                            
                            if(createDocument) {
	                            if(dialog.toNewDocumentRadio.isSelected()) {
	                                //user has selected to create the result as a new document
	                                parent.open(newDocument, null);
	                            }
	                            else {
	                                parent.getView().getEditor().setText(newString);
	                                SwingUtilities.invokeLater(new Runnable() {
	                                    public void run() {
	                                        parent.switchToEditor();
	                                        
	                                        parent.getView().updateModel();
	                                    }
	                                });
	                            }
                            }
                                                                              
                            
                        }  
                        
                    } catch ( Exception e) {
                        // This should never happen, just report and continue
                        MessageHandler.showError( parent, "Cannot Remove Nodes","Tools Remove Node Error");
                    } finally {
                        parent.setStatus( "Done");
                        parent.setWait( false);
                    }
                }
            };
            
            // Create and start the thread ...
            Thread thread = new Thread( runner);
            thread.start();
//          }
        }
    }
    
    public String removeByXPath(ExchangerDocument document, String xpathPredicate) throws Exception {
        
        
        //List nodeList = document.getDocument().selectNodes( xpathPredicate );
        Vector nodeList = document.search( xpathPredicate );
        
        if(nodeList.size()>0) {
            try {
                
                for(int cnt=0;cnt<nodeList.size();++cnt) {
                    
                    Node node = (Node)nodeList.get(cnt);
                    node.detach();
                    
                }        	    
            }
            catch (NullPointerException e) {
                MessageHandler.showError(parent, "XPath: "+xpathPredicate+"\nCannot be resolved","Tools Remove Node Error");
                return(null);
            }
            catch (Exception e) {
                MessageHandler.showError(parent, "Error removing nodes from the document","Tools Remove Node Error");
                return(null);
            }
            
            document.update();
        }
        else {
            MessageHandler.showError(parent, "No nodes could be found for:\n"+xpathPredicate,"Tools Remove Node Error");
            return(null);
        }
        
        return document.getText();
    }
}