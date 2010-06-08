/*
 * $Id: ToolsChangeNSPrefixAction.java,v 1.10 2004/10/26 10:00:34 tcurley Exp $ 
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.ToolsChangeNSPrefixDialog;


/**
 * An action that can be used to rename the namespace prefix but leave the declaration
 *
 * @version	$Revision: 1.10 $, $Date: 2004/10/26 10:00:34 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsChangeNSPrefixAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private Editor editor = null;
    private ConfigurationProperties props;
    private ToolsChangeNSPrefixDialog dialog = null;
    
    private final int ADD_NEW_NS_DECLARATIONS = 1;
    private final int RENAME_NS_PREFIXES = 2;
    
    
    /**
     * The constructor for the action which allows rename the namespace prefix
     *
     * @param parent the parent frame.
     */
    public ToolsChangeNSPrefixAction( ExchangerEditor parent, Editor editor, ConfigurationProperties props) {
        super( "Rename a Namespace Prefix ...");
        
        this.parent = parent;
        this.props = props;
        
        //this.properties = props;
        
        putValue( MNEMONIC_KEY, new Integer( 'N'));
        putValue( SHORT_DESCRIPTION, "Rename a Namespace Prefix ...");
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
            dialog = new ToolsChangeNSPrefixDialog( parent,props);
        }
        //called to make sure that the model is up to date to 
        //prevent any problems found when undo-ing etc.
        parent.getView().updateModel();
        
        //get the document
        final ExchangerDocument document = parent.getDocument();
        
        if ( document.isError()) {
            MessageHandler.showError(parent, "Please make sure the document is well-formed.", "Parser Error");
            return;
        }
        
        Vector namespaces = document.getDeclaredNamespaces();
        
        if(namespaces.size()>0) {
            //if there is more than one namespace declared in the document
            
            dialog.show(namespaces);
            
            
            if(!dialog.isCancelled()) {
                
                parent.setWait( true);
                parent.setStatus( "Changing Namespace Prefix ...");
                
                // Run in Thread!!!
                Runnable runner = new Runnable() {
                    public void run()  {
                        try {
                            
                            ExchangerDocument tempDoc =  new ExchangerDocument(document.getText());
                            //get the new vector of namespaces
                            Vector newNamespaces = dialog.getNamespaces();
                            
                            String newString = changeNSPrefix(tempDoc,newNamespaces);
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
                            MessageHandler.showError( parent, "Cannot Change Namespace Prefix", "Tools Rename a Namespace Prefix Error");
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
            
        } else {
            //no namespaces declared so show error
            MessageHandler.showError(parent,"There are no namespaces declared in this document","Tools Rename a Namespace Prefix Error");
        }
        
        
        
    }
    
    /**
     * Adds the new namespaces to an element
     * @param element the element to add them to
     * @param allNamespaces the full vector of namespaces
     * @param newNamespaces the vector of new namespaces
     * @throws Exception
     */
    private void addNewNamespaces(XElement element, Vector allNamespaces, Vector newNamespaces) throws Exception{
        //add new namespace declaration to each element that needs it
        List declared = element.declaredNamespaces();
        int declaredSize = declared.size();
        if(declaredSize>0) {
            for(int cnt=0;cnt<declaredSize;++cnt) {
                //match and add new one
                Namespace tempNs = (Namespace)declared.get(cnt);
                //find its position in the old vector
                //int match = searchForPrefix(tempNs.getPrefix(),allNamespaces);
                int match=-1;
                for(int icnt=0;icnt<allNamespaces.size();++icnt) {
                    if(tempNs==(Namespace)allNamespaces.get(icnt)){
                        match = icnt;
                    }
                    
                    
                }
                if(match!=-1) {
                    //get the appropriate new one
                    tempNs = (Namespace)newNamespaces.get(match);
                    //need to add this new namespace declaration to the element
                    element.add(tempNs);
                                        
                }
            }
        }
        
    }
    
    /**
     * Renames a namespace at the element
     * @param element the element at the namespace
     * @param allNamespaces the full vector of namespaces
     * @param newNamespaces the vector of new namespaces
     * @throws Exception
     */
    private void renameNamespaces(XElement element, Vector allNamespaces, Vector newNamespaces) throws Exception{
        
        //need to handle if the root has a namespace associated
        
        try {
            
            Namespace used = element.getNamespace();
            
            int match = -1;
            
            if(used!=null) {
                
                for(int cnt=0;cnt<allNamespaces.size();++cnt) {
                    if(used==(Namespace)allNamespaces.get(cnt)){
                        match = cnt;
                    }
                    
                }
                if(match>-1) {
                    
                    //replace the elements prefix with the corresponding one in the
                    //other vector
                    Namespace tempNs = (Namespace)newNamespaces.get(match);
                    QName qname = new QName(element.getName(),tempNs);
                    element.setQName(qname);
                    
                }
            }
            
            try {
                element.setAttributes(this.setPrefixOnAttributes(element,allNamespaces,newNamespaces));
            }
            catch (Exception e1) {
                MessageHandler.showError(parent, "Error reading the document", "Tools Rename a Namespace Prefix Error");
                
            }
            
        }catch(NullPointerException e) {
            //no namespaces in the root            
            
        }
        
    }
    
    /**
     * 
     * Renames a namespace at the root element
     * @param element the root element
     * @param allNamespaces the full vector of namespaces
     * @param newNamespaces the vector of new namespaces
     * @throws Exception
     */
    private void renameNamespacesAtRoot(XElement element, Vector allNamespaces, Vector newNamespaces) throws Exception{
        
        //need to handle if the root has a namespace associated
        try {
            Namespace used = element.getNamespace();
            int match = -1;
            if(used!=null) {
                for(int cnt=0;cnt<allNamespaces.size();++cnt) {
                    if(used==(Namespace)allNamespaces.get(cnt)){
                        match = cnt;
                    }
                }
                if(match>-1) {
                    //replace the elements prefix with the corresponding one in the
                    //other vector
                    Namespace tempNs = (Namespace)newNamespaces.get(match);
                    
                    Namespace old = (Namespace)used.clone();
                    
                    QName qname = new QName(element.getName(),tempNs);
                    element.setQName(qname);
                    element.add(old);
                }
            }
            try {
                element.setAttributes(this.setPrefixOnAttributes(element,allNamespaces,newNamespaces));
            }
            catch (Exception e1) {
                MessageHandler.showError(parent, "Error reading the document", "Tools Rename a Namespace Prefix Error");
            }
        }catch(NullPointerException e) {
            //no namespaces in the root            
            MessageHandler.showError(parent, "No namespace declared", "Tools Rename a Namespace Prefix Error");
        }
    }
    
    /**
     * Sets the prefixes on the attributes at a parent element
     * @param parent The parent element
     * @param allNamespaces the full vector of namespaces
     * @param newNamespaces the vector of new namespaces
     * @return the new list of attributes for the element
     * @throws Exception
     */
    private List setPrefixOnAttributes(XElement parent, Vector allNamespaces, Vector newNamespaces) throws Exception {
        
        int attributeCount = parent.attributeCount();
        List attributeList = parent.attributes();
        //create an array to hold all the attributes
        Attribute[] attArray = new Attribute[attributeCount];
        
        for(int cnt=0;cnt<attributeCount;++cnt) {
            //add each attribute to the array
            attArray[cnt] = (Attribute)attributeList.get(cnt);
        }
        //work on the array
        for(int cnt=0;cnt<attributeCount;++cnt) {
            //String prefix = attArray[cnt].getNamespacePrefix();
            Namespace ns = attArray[cnt].getNamespace();
            
            int match = -1;
            
            //if(!prefix.equals("")) {
            if(!ns.equals(null)) {
                //see if this prefix matches anyone in the vector
                //match = searchForPrefix(prefix, allNamespaces);
                for(int icnt=0;icnt<allNamespaces.size();++icnt) {
                    if(ns==(Namespace)allNamespaces.get(icnt)){
                        match = icnt;
                    }
                    
                    
                }
                if(match>-1) {
                    
                    //replace the elements prefix with the corresponding one in the
                    //other vector
                    Namespace tempNs = (Namespace)newNamespaces.get(match);
                    
                    //get any declared namespaces as well
                    
                    //prefix = tempNs.getPrefix();
                    String name = attArray[cnt].getName();
                    String value = attArray[cnt].getValue();
                    
                    attArray[cnt] = new XAttribute(new QName( name, tempNs), value);
                    
                    
                }
            }
        }
        
        //then remove all previous and add all the attributes back into the document
        List newAttributes = Arrays.asList(attArray);
        return(newAttributes);
    }
    
    /**
     * Change a namespace prefix
     * @param document the document to change
     * @param newNamespaces the vector of new namespaces
     * @return the document as a string
     */
    private String changeNSPrefix(ExchangerDocument document, Vector newNamespaces)  {
        
        try {
            //need to get a list of namespaces for the whole document
            Vector allNamespaces = document.getDeclaredNamespaces();
            
            XElement root = document.getRoot();
            
            //add the new declarations to the root if applicable
            this.addNewNamespaces(root,allNamespaces,newNamespaces);
            
            //walk through the tree and add new declarations if applicable
            treeWalk(root, allNamespaces, newNamespaces,ADD_NEW_NS_DECLARATIONS);
            
            //update the document with the changes
            document.update();
            
            //rename the namespace prefix for th root if applicable
            this.renameNamespacesAtRoot(root,allNamespaces,newNamespaces);
            
            //walk through the tree and rename the prefixes if applicable
            treeWalk(root, allNamespaces, newNamespaces,RENAME_NS_PREFIXES);
            
            //update the document again with the changes
            document.update();
        }
        catch (Exception e) {
            MessageHandler.showError(parent, "Error changing namespace prefix","Tools Rename a Namespace Prefix Error");
            return(null);
        }
        
        //return the text of this document
        return document.getText();
    }
    
    /**
     * Recursivly walk through the tree to rename the namespace prefixes
     * @param element the element it currently on
     * @param allNamespaces the vector of all the namespaces
     * @param newNamespaces the vector of the new namespaces
     * @param type a flag which changes this function to either add new ns declarations or rename old ones
     * @throws Exception
     */
    private void treeWalk(XElement element, Vector allNamespaces, Vector newNamespaces, int type) throws Exception{
        
        if(type==ADD_NEW_NS_DECLARATIONS) {
            //add new namespace declarations
            this.addNewNamespaces(element,allNamespaces,newNamespaces);
        }
        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
            //for every node that is a child of element
            Node node = element.node(i);
            if ( node instanceof Element ) {
                //if this is an element
                if(type==RENAME_NS_PREFIXES) {
                    //rename namespace prefix if applicable
                    this.renameNamespaces((XElement) node,allNamespaces,newNamespaces);
                }
                //continue walking the tree
                treeWalk( (XElement) node, allNamespaces, newNamespaces,type);
            }
        }
    }
}