/*
 * $Id: ToolsRemoveUnusedNSAction.java,v 1.13 2004/10/29 15:01:32 tcurley Exp $ 
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
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to Move Namespace To Where First Used
 *
 * @version	$Revision: 1.13 $, $Date: 2004/10/29 15:01:32 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsRemoveUnusedNSAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private Editor editor = null;
    private ConfigurationProperties props = null;
    
    /**
     * The constructor for the action which allows Move Namespace To Where First Used
     *
     * @param parent the parent frame.
     */
    public ToolsRemoveUnusedNSAction( ExchangerEditor parent, Editor editor, ConfigurationProperties props) {
        super( "Remove Unused Namespaces");
        
        this.parent = parent;
        this.props = props;
        
        //this.properties = props;
        
        putValue( MNEMONIC_KEY, new Integer( 'U'));
        putValue( SHORT_DESCRIPTION, "Remove Unused Namespaces");
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
        parent.setStatus( "Removing Unused Namespaces ...");
        
        // Run in Thread!!!
        Runnable runner = new Runnable() {
            public void run()  {
                try {
                    //create temporary document
                    ExchangerDocument tempDoc =  new ExchangerDocument(document.getText());
                    
                    String newDocument = removeUnusedNS(tempDoc);
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
                    MessageHandler.showError( parent, "Removing Unused Namespaces","Tools Remove Unused Namespace Error");
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
    
    private String removeUnusedNS(ExchangerDocument document) throws Exception {
        
        Vector allNamespaces = document.getDeclaredNamespaces();
        
        if(allNamespaces.size()<1) {
            MessageHandler.showError(parent,"Cannot Find Any Namespace Declarations","Tools Remove Unused Namespace Error");
            return(null);
        }
        else {
	        XElement e = (XElement) document.getRoot(); 
	        Namespace elementNamespace = e.getNamespace();
	        int index = findInList(elementNamespace,allNamespaces);
	        if(index>-1) {
	            allNamespaces.remove(index);
	        }
	        
	        //sort out the attributes
	        if(e.attributeCount()>0) {
	            XAttribute[] atts = e.getAttributes();
	            for(int cnt=0;cnt<e.attributeCount();++cnt) {
	                XAttribute a = atts[cnt];
	                Namespace attributeNamespace = a.getNamespace();
	                index = findInList(attributeNamespace,allNamespaces);
	                if(index>-1) {
	                    allNamespaces.remove(index);
	                }
	            }
	        }
	        
	        treeWalk(document.getRoot(),allNamespaces);
        }
        
        if(allNamespaces.size()==0) {
            MessageHandler.showError(parent,"Cannot Find Any Unused Namespace Declarations","Tools Remove Unused Namespace Error");
            return(null);
        }
        else {
            boolean deleteAll = false;
            for(int cnt=0;cnt<allNamespaces.size();++cnt) {
                
                
//              if the deleteAll flag is false, then ask the user about each individual object
	            if( deleteAll == false) {
	            
		            //create the message for the user
		            String message = "Are you sure you want to delete:\n ";
		            Namespace temp = (Namespace)allNamespaces.get(cnt); 
                    if((temp.getPrefix().length()>0) && (temp.getPrefix()!=null)) {
                        message += "xmlns:"+temp.getPrefix()+"="+temp.getURI();
                    }
                    else {
                        message += "xmlns="+temp.getURI();
                    }
		            
		            //ask the question
		            int questionResult = -1;
		            if(allNamespaces.size()>1) {
                        questionResult = MessageHandler.showConfirmYesNoAll(parent,message);
                    }
                    else {
                        questionResult = MessageHandler.showConfirm(parent,message);
                    }
		            		            
		            //if the user answered All, don't do anything for now and delete them all later
		            if(questionResult==MessageHandler.CONFIRM_ALL_OPTION) {
		                if(!removeNamespaceDeclaration(document.getRoot(),(Namespace)allNamespaces.get(cnt))) {
	                        //then walk the tree
	                        treeWalk(document.getRoot(),(Namespace)allNamespaces.get(cnt));
	                    }
	                	deleteAll=true;
	                } 
	                //user choose to delete this object, remove it from the list
	                else if(questionResult==JOptionPane.YES_OPTION) {
	                    if(!removeNamespaceDeclaration(document.getRoot(),(Namespace)allNamespaces.get(cnt))) {
	                        //then walk the tree
	                        treeWalk(document.getRoot(),(Namespace)allNamespaces.get(cnt));
	                    }
					}
		            
	            } else {
	                if(!removeNamespaceDeclaration(document.getRoot(),(Namespace)allNamespaces.get(cnt))) {
                        //then walk the tree
                        treeWalk(document.getRoot(),(Namespace)allNamespaces.get(cnt));
                    }
	            }
                
                
                
                
                /*
                //System.out.println(allNamespaces.get(cnt));
                int questionResult=-1;
                if(!deleteAll) {
                    
                    String message = "Are you sure you want to delete:\n ";
                    Namespace temp = (Namespace)allNamespaces.get(cnt); 
                    if((temp.getPrefix().length()>0) && (temp.getPrefix()!=null)) {
                        message += "xmlns:"+temp.getPrefix()+"="+temp.getURI();
                    }
                    else {
                        message += "xmlns="+temp.getURI();
                    }
                    
                    questionResult = MessageHandler.showConfirmYesNoAll(parent,message);
                    
                    if(questionResult==MessageHandler.CONFIRM_ALL_OPTION)
                        deleteAll=true;
                    
                
                
	                if(questionResult==JOptionPane.YES_OPTION) {
	                    //delete these namespace declarations
	                    //first from the root
	                    if(!removeNamespaceDeclaration(document.getRoot(),(Namespace)allNamespaces.get(cnt))) {
	                        //then walk the tree
	                        treeWalk(document.getRoot(),(Namespace)allNamespaces.get(cnt));
	                    }
	                    
	                }
                }*/
            }
            
            //now need to check if all was pressed after the first dialog was shown,
            //in which case need to act on the initial entries
            /*if(deleteAll) {
                for(int cnt=0;cnt<allNamespaces.size();++cnt) {
                    if(!removeNamespaceDeclaration(document.getRoot(),(Namespace)allNamespaces.get(cnt))) {
                        //then walk the tree
                        treeWalk(document.getRoot(),(Namespace)allNamespaces.get(cnt));
                    }
                }
            }*/
            
            document.update();
            return(document.getText());
        }
        
    }
    
    private boolean removeNamespaceDeclaration(XElement root, Namespace ns) throws Exception {
        
        List rootDeclared = root.declaredNamespaces();
        for(int cnt=0;cnt<rootDeclared.size();++cnt) {
            Namespace declared = (Namespace)rootDeclared.get(cnt);
            if(declared==ns) {
                //have a match, need to delete it
                if(root.remove(ns)) {
                    return(true);
                }
            }
        }
        return(false);
    }
    
    private void treeWalk(XElement element, Namespace ns) throws Exception {
        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
            Node node = element.node(i);
            if ( node instanceof Element ) {
                removeNamespaceDeclaration((XElement)node,ns);
                treeWalk((XElement)node,ns);                
                
            }
            
        }
        
    }
    
    private void treeWalk(XElement element, Vector allNamespaces) throws Exception {
        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
            Node node = element.node(i);
            if ( node instanceof Element ) {
                XElement e = (XElement) node; 
                Namespace elementNamespace = e.getNamespace();
                int index = findInList(elementNamespace,allNamespaces);
                if(index>-1) {
                    allNamespaces.remove(index);
                }
                
                //sort out the attributes
                if(e.attributeCount()>0) {
                    XAttribute[] atts = e.getAttributes();
                    for(int cnt=0;cnt<e.attributeCount();++cnt) {
                        XAttribute a = atts[cnt];
                        Namespace attributeNamespace = a.getNamespace();
                        index = findInList(attributeNamespace,allNamespaces);
                        if(index>-1) {
                            allNamespaces.remove(index);
                        }
                    }
                }
                
                
                treeWalk( e, allNamespaces);
            }
            
        }
        
    }
    
    /**
     * Finds an attribute in a list and returns the index
     * 
     * @param att the attribute to search for
     * @param attributeList the list of attributes to search in
     * @return the index of the attribute in list, or -1 if not found.
     */
    private int findInList(Namespace ns,Vector namespaces) throws Exception {
        
        int cnt=0;
        int size = namespaces.size();
        boolean found=false;
        while((!found)&&(cnt<size)) {
            
            if(ns==((Namespace)namespaces.get(cnt))) {
                
                found=true;
            }
            else {
                cnt++;
            }
        }
        if(cnt==size)
            return(-1);
        else
            return(cnt);
    }
    
}