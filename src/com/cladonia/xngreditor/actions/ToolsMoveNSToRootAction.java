/*
 * $Id: ToolsMoveNSToRootAction.java,v 1.12 2004/10/26 10:00:34 tcurley Exp $ 
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.dom4j.Namespace;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.MessageHandler;

/**
 * An action that can be used to move all namespace declarations to the root
 *
 * @version	$Revision: 1.12 $, $Date: 2004/10/26 10:00:34 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsMoveNSToRootAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private Editor editor = null;
    
    /**
     * The constructor for the action which allows all namespaces to be moved to the root
     *
     * @param parent the parent frame.
     */
    public ToolsMoveNSToRootAction( ExchangerEditor parent, Editor editor) {
        super( "Move Namespaces Declarations to Root");
        
        this.parent = parent;
        
        putValue( MNEMONIC_KEY, new Integer( 'R'));
        putValue( SHORT_DESCRIPTION, "Move Namespaces Declarations to Root");
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
        
        //called to make sure that the model is up to date to 
        //prevent any problems found when undo-ing etc.
        parent.getView().updateModel();
        
        //get the document
        final ExchangerDocument document = parent.getDocument();
        
        if ( document.isError()) {
            MessageHandler.showError( parent,"Please make sure the document is well-formed.", "Parser Error");
            return;
        }
        
        parent.setWait( true);
        parent.setStatus( "Moving Namespaces To Root ...");
        
        // Run in Thread!!!
        Runnable runner = new Runnable() {
            public void run()  {
                try {
                    //create temporary document
                    ExchangerDocument tempDoc =  new ExchangerDocument(document.getText());
                    
                    
                    String newDocument = moveNamespaces(tempDoc);
                    
                    if(newDocument!=null) {
                        parent.getView().getEditor().setText(newDocument);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                parent.switchToEditor();
                                
                                parent.getView().updateModel();
                            }
                        });
                        
                    }
                    
                } catch ( Exception e) {
                    // This should never happen, just report and continue
                    MessageHandler.showError( parent, "Cannot Move Namespaces To Root","Tools Move Namespaces To Root Error");
                } finally {
                    parent.setStatus( "Done");
                    parent.setWait( false);
                }
            }
        };
        
        // Create and start the thread ...
        Thread thread = new Thread( runner);
        thread.start();
//      }
    }
    
    
    private String moveNamespaces(ExchangerDocument document) throws Exception {
        
        try {
            XElement root = document.getRoot();
            int numberChanged = 0;
            boolean allow_one_ns_in_root = true;
            
            //get all namespaces in the root
            List rootNamespaces = root.declaredNamespaces();
            for(int cnt=0;cnt<rootNamespaces.size();++cnt) {
                //search through and see if any of these have no prefix.
                //if they all have prefixes, then allow a non-prefixed namespace
                //else dont allow any non-prefixed namespaces
                Namespace ns = (Namespace)rootNamespaces.get(cnt);
                if(ns.getPrefix().length()==0) {
                    allow_one_ns_in_root=false;
                }
            }
            
            Vector namespaces = document.getDeclaredNamespaces();
            if(namespaces.size()>0){
                for(int cnt=0;cnt<namespaces.size();++cnt) {
                    Namespace ns1 = (Namespace)namespaces.get(cnt);
                    //this will skip namespaces which do not have prefixes (e.g. Default namespaces)
                    if((allow_one_ns_in_root==true)||(ns1.getPrefix().length()>0)) {
                        root.add(ns1);
                        numberChanged++;
                        if(ns1.getPrefix().length()==0) {
                            allow_one_ns_in_root=false;
                        }
                    }
                    
                }
                if(numberChanged>0) {
                    //if the number of namespaces moved to root is greater than zero
                    
                    //check if root has 2 different namespace uris with the same prefix.
                    rootNamespaces = root.declaredNamespaces();
                    for (int cnt=0;cnt<rootNamespaces.size();++cnt) {
                        Namespace ns = (Namespace)rootNamespaces.get(cnt);
                        for(int icnt=0;icnt<rootNamespaces.size();++icnt) {
                            
                                //skip the namespace we're comparing to 
	                            Namespace ns1 = (Namespace)rootNamespaces.get(icnt);
	                        if(ns!=ns1) {
	                            if(ns.getPrefix().equals(ns1.getPrefix())) {
	                                //error
	                                MessageHandler.showError( parent, "Cannot move namespaces.\n" +
	                                		"Document contains multiple namespaces with the same prefixes.", "Tools Move Namespaces To Root Error");
	                                //else - nothing has changed, just return null
	                                return(null);
	                            }
                            }
                        }
                    }
                    
                    
                    //then update the document
                    document.update();
                }
                else {
                    MessageHandler.showError( parent, "No namespaces were moved.\n" +
                    		"Since the root already declares one non-prefixed namespace.", "Tools Move Namespaces To Root Error");
                    //else - nothing has changed, just return null
                    return(null);
                }
            }
            else {
                //there are no namespaces declared
                MessageHandler.showError( parent, "There are no namespaces declared in this document", "Tools Move Namespaces To Root Error");
                return(null);
            }
            
        }
        catch (NullPointerException e) {
            MessageHandler.showError(parent,"Error - Cannot Move Namespaces","Tools Move Namespaces To Root Error");            
            return(null);
        }
        catch (Exception e) {
            MessageHandler.showError(parent,"Error - Cannot Move Namespaces","Tools Move Namespaces To Root Error");
            return(null);
        }
        return document.getText();
    }           
}