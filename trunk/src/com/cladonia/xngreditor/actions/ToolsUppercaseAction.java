/*
 * $Id: ToolsUppercaseAction.java,v 1.15 2004/10/27 17:03:51 tcurley Exp $ 
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

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.ToolsUppercaseDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to Uppercase elements or attributes.
 *
 * @version	$Revision: 1.15 $, $Date: 2004/10/27 17:03:51 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsUppercaseAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private ToolsUppercaseDialog dialog = null;
    private Editor editor = null;
    private ConfigurationProperties props;
    
    /**
     * The constructor for the action that can be used to Uppercase elements or attributes.
     *
     * @param parent the parent frame.
     */
    public ToolsUppercaseAction( ExchangerEditor parent, Editor editor, ConfigurationProperties props) {
        super( "Uppercase Elements and Attributes ...");
        
        this.parent = parent;
        this.props = props;
        
        putValue( MNEMONIC_KEY, new Integer( 'U'));
        putValue( SHORT_DESCRIPTION, "Uppercase Elements and Attributes ...");
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
            dialog = new ToolsUppercaseDialog( parent,props);
        }
        
        //called to make sure that the model is up to date to 
        //prevent any problems found when undo-ing etc.
        parent.getView().updateModel();
        
        final ExchangerDocument document = parent.getDocument();
        
        if ( document.isError()) {
            MessageHandler.showError( parent,"Please make sure the document is well-formed.", "Parser Error");
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
                            
                            String newDocument = null;
                            boolean TRAVERSE_CHILDREN = true;
                            
                            if(dialog.xpathPanel.xpathBox.isSelected()) {
                                String xpathPredicate = dialog.xpathPanel.getXpathPredicate();
                                newDocument = uppercase(tempDoc,xpathPredicate,
                                        dialog.elementsRadio.isSelected(),
                                        dialog.attributeRadio.isSelected(),
                                        dialog.elementsAndAttributesRadio.isSelected(),
                                        TRAVERSE_CHILDREN);
                                
                            }
                            else {
                                
                                //set the string to the new empty document
                                newDocument = uppercase(tempDoc,
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
                            }//end if
                        }//end if
                        
                        
                    } catch ( Exception e) {
                        // This should never happen, just report and continue
                        MessageHandler.showError( parent, "Cannot Convert To Uppercase", "Tools Uppercase Error");
                    } finally {
                        parent.setStatus( "Done");
                        parent.setWait( false);
                    }
                }//end run  
                
            };//end runnable
            
            // Create and start the thread ...
            Thread thread = new Thread( runner);
            thread.start();
//          }
            
        }
    }
    
    
    /**
     * Convert the complete document to uppercase with the various flags
     * @param document the document to be converted
     * @param uppercaseElements uppercase elements flag
     * @param uppercaseAttributes uppercase attributes flag
     * @param uppercaseElementsAndAttributes uppercase elements and attributes flag
     * @return the converted document as a string
     */
    private String uppercase(ExchangerDocument document, boolean uppercaseElements, 
            boolean uppercaseAttributes, boolean uppercaseElementsAndAttributes) {
        
        try {
            XElement root = document.getRoot();
            
            if((uppercaseAttributes)||(uppercaseElementsAndAttributes)) {
                
                root.setAttributes(this.uppercaseAttributes(root));
                
            }
            //Uppercase the element
            if(((uppercaseElements)||(uppercaseElementsAndAttributes))&&(root.getName()!=null)) {
                
                String name = root.getName();
                name = uppercaseString(name);
                Namespace ns = root.getNamespace();
                
                root.setQName(new QName(name,ns));
            }
            
            //then Uppercase its children 	  
            iterateTree(root,uppercaseElements,uppercaseAttributes,uppercaseElementsAndAttributes);
            
            document.update();
        }
        catch (NullPointerException e) {
            MessageHandler.showError(parent,"Error - Cannot convert to uppercase,\nElements or Attributes not found","Tools Uppercase Error");
            return(null);
        }
        catch (Exception e) {
            MessageHandler.showError(parent,"Error - Cannot convert to uppercase","Tools Uppercase Error");
            return(null);
        }
        return document.getText();
    }
    
    /**
     * Convert the document to uppercase with the various flags and an xpath filter
     * @param document the document to be converted
     * @param xpath the xpath filter to be applied
     * @param uppercaseElements uppercase elements flag
     * @param uppercaseAttributes uppercase attributes flag
     * @param uppercaseElementsAndAttributes uppercase elements and attributes flag
     * @param traverseChildren process sub elements flag
     * @return the converted document as a string
     */
    private String uppercase(ExchangerDocument document, String xpath, boolean uppercaseElements, 
            boolean uppercaseAttributes, boolean uppercaseElementsAndAttributes, boolean traverseChildren) {
        //used for xpath expressions
        try {
            //XElement[] root = document.getElements(xpath);
            Vector nodes = document.search( xpath);
            if(nodes.size()<1) {
                MessageHandler.showError(parent,"Could Not Resolve XPath:\n"+xpath,"Tools Uppercase");
                return(null);
            }
            for(int cnt=0;cnt<nodes.size();++cnt) {
                //for each element
                //uppercase the attributes
                Node node = (Node)nodes.get(cnt);
                if(node instanceof Element) {
	                XElement root = (XElement)nodes.get(cnt);
	                if((uppercaseAttributes)||(uppercaseElementsAndAttributes)) {
		                if(root.attributeCount()>0) {
		                    root.setAttributes(this.uppercaseAttributes(root));
		                }
	                }
	                if((uppercaseElements)||(uppercaseElementsAndAttributes)) {
		                //uppercase the element
		                if(root.getName()!=null) {
		                    
		                    String name = root.getName();
		                    name = uppercaseString(name);
		                    Namespace ns = root.getNamespace();
		                    
		                    root.setQName(new QName(name,ns));
		                }
	                }
	                if(traverseChildren) {
		                //then uppercase its children 	       
		                iterateTree(root,uppercaseElements,uppercaseAttributes,uppercaseElementsAndAttributes);
	                }
                }
                else if(((uppercaseAttributes)||(uppercaseElementsAndAttributes))&&(node instanceof Attribute)) {
                    Attribute att = (Attribute)node;
                    node.getParent().setAttributes(uppercaseAttributes((XElement)(node.getParent()),att));
                    
                }
            }
            document.update();
        }catch (NullPointerException e) {
            MessageHandler.showError(parent,"Error - Cannot Uppercase,\nElements or Attributes not found","Tools Uppercase Error");
            return(null);
        }catch (Exception e) {
                MessageHandler.showError(parent,"Error - Cannot convert to uppercase","Tools Uppercase Error");
                return(null);
            }
        return document.getText();
    }
    
    
    /**
     * Iterate through the tree and uppercase each node depending on the flags
     * @param element the element node
     * @param uppercaseElements uppercase elements flag
     * @param uppercaseAttributes uppercase attributes flag
     * @param uppercaseElementsAndAttributes uppercase elements and attributes flag
     * @throws Exception
     */
    private void iterateTree(Element element,boolean uppercaseElements, 
            boolean uppercaseAttributes, boolean uppercaseElementsAndAttributes) throws Exception{
        
        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
            Node oldNode = element.node(i);
            if(oldNode instanceof Element) {
                
                XElement oldElement = (XElement)oldNode;
                
                if((uppercaseAttributes)||(uppercaseElementsAndAttributes)) {
                    
                    oldElement.setAttributes(this.uppercaseAttributes(oldElement));
                    
                }
                
                //Uppercase the element
                if(((uppercaseElements)||(uppercaseElementsAndAttributes))&&(oldElement.getName()!=null)) {
                    
                    String name = oldElement.getName();
                    name = uppercaseString(name);
                    Namespace ns = oldElement.getNamespace();
                    
                    oldElement.setQName(new QName(name,ns));
                    
                }
                iterateTree(oldElement,uppercaseElements,uppercaseAttributes,uppercaseElementsAndAttributes);
                
            }
        }
    }
    
    /**
     * Uppercase all attributes of a parent element
     * @param parent The parent element
     * @return the new list of attributes
     * @throws Exception
     */
    private List uppercaseAttributes(XElement parent) throws Exception {
        
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
            name = uppercaseString( name);
            String value = attArray[cnt].getValue();
            Namespace ns = attArray[cnt].getNamespace();
            
            attArray[cnt] = new XAttribute(new QName( name, ns), value);
        }
        
        //then remove all previous and add all the attributes back into the document
        List newAttributes = Arrays.asList(attArray);
        return(newAttributes);
    }
    
    
    /**
     * Uppercase the attribute of a parent element that matches the passed parameter att
     * @param parent the parent element
     * @param att the attribute to be matched
     * @return the new list of attributes
     * @throws Exception
     */
    private List uppercaseAttributes(XElement parent, Attribute att) throws Exception {
        
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
	            name = uppercaseString( name);
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
     * Uppercase the first letter found in a string
     * @param value
     * @return the Uppercased string
     */
    private String uppercaseString(String value) throws Exception{
        
        return value.toUpperCase();
    }
    
    /**
     * Checks whether this node is whitespace or not
     * @param node the node to check
     * @return true if node is whitespace, else false
     * @throws Exception
     */
    private static boolean isWhiteSpace( Node node) throws Exception{
        return node.getText().trim().length() == 0;
    } 	
}