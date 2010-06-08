/*
 * $Id: ToolsLowercaseAction.java,v 1.15 2004/10/27 17:03:51 tcurley Exp $ 
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
import com.cladonia.xngreditor.ToolsLowercaseDialog;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to Lowercase elements or attributes.
 *
 * @version	$Revision: 1.15 $, $Date: 2004/10/27 17:03:51 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsLowercaseAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private ToolsLowercaseDialog dialog = null;
    private Editor editor = null;
    private ConfigurationProperties props;
    
    /**
     * The constructor for the action which allows capitalizint of elements or attributes
     *
     * @param parent the parent frame.
     */
    public ToolsLowercaseAction( ExchangerEditor parent, Editor editor, ConfigurationProperties props) {
        super( "Lowercase Elements and Attributes ...");
        
        this.parent = parent;
        this.props = props;
        
        putValue( MNEMONIC_KEY, new Integer( 'L'));
        putValue( SHORT_DESCRIPTION, "Lowercase Elements and Attributes ...");
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
            dialog = new ToolsLowercaseDialog( parent,props);
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
        
        String currentXPath = null;
        Node node = (Node)document.getLastNode( parent.getView().getEditor().getCursorPosition(), true);

        if ( props.isUniqueXPath()) {
            currentXPath = node.getUniquePath();
        } else {
            currentXPath = node.getPath();
        }
        dialog.show(currentXPath);
        
        if(!dialog.isCancelled()) {
            parent.setWait( true);
            parent.setStatus( "Changing Case ...");
            
            // Run in Thread!!!
            Runnable runner = new Runnable() {
                public void run()  {
                    try {
                        //make sure one of the options was selected
                        if((dialog.elementsRadio.isSelected())||(dialog.attributeRadio.isSelected())||
 			                   (dialog.elementsAndAttributesRadio.isSelected())) {
	    			            
                            ExchangerDocument tempDoc =  new ExchangerDocument(document.getText());
                            boolean TRAVERSE_CHILDREN = true;
                            String newDocument = null;
                            
                            if(dialog.xpathPanel.xpathBox.isSelected()) {
                                String xpathPredicate = dialog.xpathPanel.getXpathPredicate();
                                newDocument = lowercase(tempDoc,xpathPredicate,
                                        dialog.elementsRadio.isSelected(),
                                        dialog.attributeRadio.isSelected(),
                                        dialog.elementsAndAttributesRadio.isSelected(),
                                        TRAVERSE_CHILDREN);
                                
                            }
                            else {
                                
                                //set the string to the new lowercase document
                                newDocument = lowercase(tempDoc,
                                        dialog.elementsRadio.isSelected(),
                                        dialog.attributeRadio.isSelected(),
                                        dialog.elementsAndAttributesRadio.isSelected());
                            }
                            if(newDocument!=null) {
                                if(dialog.toNewDocumentRadio.isSelected()) {
                                    //user has selected to create the result as a new document
                                    parent.open( new ExchangerDocument(newDocument), null);
                                }
                                else {
                                    parent.getView().getEditor().setText(newDocument);
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
                        MessageHandler.showError( parent, "Cannot Convert To Lowercase","Tools Lowercase Error");
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
    
    private String lowercase(ExchangerDocument document, boolean lowercaseElements, 
            boolean lowercaseAttributes, boolean lowercaseElementsAndAttributes) {
        
        try {
            XElement root = document.getRoot();
            
            if((lowercaseAttributes)||(lowercaseElementsAndAttributes)) {
                
                root.setAttributes(this.lowercaseAttributes(root));
                
            }
            //Lowercase the element
            if(((lowercaseElements)||(lowercaseElementsAndAttributes))&&(root.getName()!=null)) {
                
                String name = root.getName();
                name = lowercaseString(name);
                Namespace ns = root.getNamespace();
                
                root.setQName(new QName(name,ns));
            }
            
            //then Lowercase its children 	  
            iterateTree(root,lowercaseElements,lowercaseAttributes,lowercaseElementsAndAttributes);
            
            document.update();
        }
        catch (NullPointerException e) {
            MessageHandler.showError(parent,"Error - Cannot Lowercase,\nElements or Attributes not found","Tools Lowercase Error");
            return(null);
        }
        catch (Exception e) {
            MessageHandler.showError(parent,"Error - Cannot Lowercase,\nElements or Attributes not found","Tools Lowercase Error");
            return(null);
        }
        return document.getText();
    }
    
    private String lowercase(ExchangerDocument document, String xpath, boolean lowercaseElements, 
            boolean lowercaseAttributes, boolean lowercaseElementsAndAttributes, boolean traverseChildren) {
        //used for xpath expressions
        try {
            //XElement[] root = document.getElements(xpath);
            Vector nodes = document.search( xpath);
            if(nodes.size()<1) {
                MessageHandler.showError(parent,"Could Not Resolve XPath:\n"+xpath,"Tools Lowercase Error");
                return(null);
            }
            for(int cnt=0;cnt<nodes.size();++cnt) {
                //for each element
                //lowercase the attributes
                Node node = (Node)nodes.get(cnt);
                if(node instanceof Element) {
	                XElement root = (XElement)nodes.get(cnt);
	                if((lowercaseAttributes)||(lowercaseElementsAndAttributes)) {
		                if(root.attributeCount()>0) {
		                    root.setAttributes(this.lowercaseAttributes(root));
		                }
	                }
	                if((lowercaseElements)||(lowercaseElementsAndAttributes)) {
		                //lowercase the element
		                if(root.getName()!=null) {
		                    
		                    String name = root.getName();
		                    name = lowercaseString(name);
		                    Namespace ns = root.getNamespace();
		                    
		                    root.setQName(new QName(name,ns));
		                }
	                }
	                if(traverseChildren) {
		                //then lowercase its children 	       
		                iterateTree(root,lowercaseElements,lowercaseAttributes,lowercaseElementsAndAttributes);
	                }
                }
                else if(((lowercaseAttributes)||(lowercaseElementsAndAttributes))&&(node instanceof Attribute)) {
                    Attribute att = (Attribute)node;
                    node.getParent().setAttributes(lowercaseAttributes((XElement)(node.getParent()),att));
                    
                }
            }
            document.update();
        }catch (NullPointerException e) {
            MessageHandler.showError(parent,"Error - Cannot Lowercase Document,\nElements or Attributes not found","Tools Lowercase Error");
            return(null);
        }catch (Exception e) {
            MessageHandler.showError(parent,"Error - Cannot Lowercase Document","Tools Lowercase Error");
            return(null);
        }
        return document.getText();
    }
    
    private void iterateTree(Element element,boolean lowercaseElements, 
            boolean lowercaseAttributes, boolean lowercaseElementsAndAttributes) throws Exception{
        
        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
            Node oldNode = element.node(i);
            if(oldNode instanceof Element) {
                
                XElement oldElement = (XElement)oldNode;
                
                if((lowercaseAttributes)||(lowercaseElementsAndAttributes)) {
                    
                    oldElement.setAttributes(this.lowercaseAttributes(oldElement));
                    
                }
                
                //Lowercase the element
                if(((lowercaseElements)||(lowercaseElementsAndAttributes))&&(oldElement.getName()!=null)) {
                    
                    String name = oldElement.getName();
                    name = lowercaseString(name);
                    Namespace ns = oldElement.getNamespace();
                    
                    oldElement.setQName(new QName(name,ns));
                    
                }
                iterateTree(oldElement,lowercaseElements,lowercaseAttributes,lowercaseElementsAndAttributes);
                
            }
        }
    }
    
    private List lowercaseAttributes(XElement parent) throws Exception {
        
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
            String name = attArray[cnt].getName();
            name = lowercaseString( name);
            String value = attArray[cnt].getValue();
            Namespace ns = attArray[cnt].getNamespace();
            
            attArray[cnt] = new XAttribute(new QName( name, ns), value);
        }
        
        //then remove all previous and add all the attributes back into the document
        List newAttributes = Arrays.asList(attArray);
        return(newAttributes);
    }
    
    private List lowercaseAttributes(XElement parent, Attribute att) throws Exception {
        
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
            Attribute attOld = attArray[cnt];
            if(attOld==att) {
	            String name = attArray[cnt].getName();
	            name = lowercaseString( name);
	            String value = attArray[cnt].getValue();
	            Namespace ns = attArray[cnt].getNamespace();
	            
	            attArray[cnt] = new XAttribute(new QName( name, ns), value);
            }
        }
        
        //then remove all previous and add all the attributes back into the document
        List newAttributes = Arrays.asList(attArray);
        return(newAttributes);
    }
    
    /**
     * Lowercase the first letter found in a string
     * @param value
     * @return the Lowercased string
     */
    private String lowercaseString(String value) throws Exception{
        
        return value.toLowerCase();
    }
    
    private static boolean isWhiteSpace( Node node) throws Exception{
        return node.getText().trim().length() == 0;
    }
    
    
}