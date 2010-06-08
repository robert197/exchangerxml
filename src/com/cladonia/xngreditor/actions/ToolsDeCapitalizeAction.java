/*
 * $Id: ToolsDeCapitalizeAction.java,v 1.16 2004/10/27 17:03:51 tcurley Exp $ 
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ToolsDeCapitalizeDialog;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to DeCapitalize elements or attributes.
 *
 * @version	$Revision: 1.16 $, $Date: 2004/10/27 17:03:51 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsDeCapitalizeAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private ToolsDeCapitalizeDialog dialog = null;
    private Editor editor = null;
    private ConfigurationProperties props;
    
    /**
     * The constructor for the action which allows capitalizint of elements or attributes
     *
     * @param parent the parent frame.
     */
    public ToolsDeCapitalizeAction( ExchangerEditor parent, Editor editor, ConfigurationProperties props) {
        super( "DeCapitalize Elements and Attributes ...");
        
        this.parent = parent;
        this.props = props;
        
        //this.properties = props;
        
        putValue( MNEMONIC_KEY, new Integer( 'D'));
        putValue( SHORT_DESCRIPTION, "DeCapitalize Elements and Attributes ...");
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
            dialog = new ToolsDeCapitalizeDialog( parent,props);
        }
        
        //called to make sure that the model is up to date to 
        //prevent any problems found when undo-ing etc.
        parent.getView().updateModel();
        
        //get the document
        final ExchangerDocument document = parent.getDocument();
        
        if ( document.isError()) {
            MessageHandler.showError( parent, "Please make sure the document is well-formed.", "Parser Error");
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
            parent.setStatus( "Changing Capitals ...");
            
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
                                newDocument = deCapitalize(tempDoc,xpathPredicate,
		                                dialog.elementsRadio.isSelected(),
		                                dialog.attributeRadio.isSelected(),
		                                dialog.elementsAndAttributesRadio.isSelected(),
		                                TRAVERSE_CHILDREN);
                                
                            }
                            else {
                                
                                //set the string to the new deCapitalize document
                                newDocument = ToolsDeCapitalizeAction.this.deCapitalize(tempDoc,
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
                        MessageHandler.showError( parent, "Cannot DeCapitalize Document","Tools DeCapitalize Error");
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
    
    /**
     * Decapitalize a document based on the various flags
     * @param document the document to be decapitalized
     * @param deCapitalizeElements flag as to whether to decapitalize elements
     * @param deCapitalizeAttributes flag as to whether to decapitalize attributes
     * @param deCapitalizeElementsAndAttributes flag as to whether to decapitalize elements and attributes
     * @return the decapitalized document as a string
     */
    private String deCapitalize(ExchangerDocument document, boolean deCapitalizeElements, 
            boolean deCapitalizeAttributes, boolean deCapitalizeElementsAndAttributes) {
        
        try {
            XElement root = document.getRoot();
            
            if((deCapitalizeAttributes)||(deCapitalizeElementsAndAttributes)) {
                
                root.setAttributes(this.deCapitalizeAttributes(root));
                
            }
            //DeCapitalize the element
            if(((deCapitalizeElements)||(deCapitalizeElementsAndAttributes))&&(root.getName()!=null)) {
                
                String name = root.getName();
                name = deCapitalizeString(name);
                Namespace ns = root.getNamespace();
                
                root.setQName(new QName(name,ns));
            }
            
            //then DeCapitalize its children 	  
            iterateTree(root,deCapitalizeElements,deCapitalizeAttributes,deCapitalizeElementsAndAttributes);
            
            document.update();
        }
        catch (NullPointerException e) {
            MessageHandler.showError(parent,"Error - Cannot DeCapitalize,\nElements or Attributes not found","Tools DeCapitalize Error");
            return(null);
        }
        catch (Exception e) {
            MessageHandler.showError(parent,"Error - Cannot DeCapitalize Document","Tools DeCapitalize Error");
            return(null);
        }
        return document.getText();
    }
    
    /**
     * 
     * Decapitalize a document based on the various flags
     * @param document the document to be decapitalized
     * @param xpath the xpath filter
     * @param deCapitalizeElements flag as to whether to decapitalize elements
     * @param deCapitalizeAttributes flag as to whether to decapitalize attributes
     * @param deCapitalizeElementsAndAttributes flag as to whether to decapitalize elements and attributes
     * @param traverseChildren process sub elements flag
     * @return the decapitalized document as a string
     */
    private String deCapitalize(ExchangerDocument document, String xpath, boolean deCapitalizeElements, 
            boolean deCapitalizeAttributes, boolean deCapitalizeElementsAndAttributes, boolean traverseChildren) {
        //used for xpath expressions
        try {
            //XElement[] root = document.getElements(xpath);
            Vector nodes = document.search( xpath);
            if(nodes.size()<1) {
                MessageHandler.showError(parent,"Could Not Resolve XPath:\n"+xpath,"Tools DeCapitalize Error");
                return(null);
            }
            for(int cnt=0;cnt<nodes.size();++cnt) {
                //for each element
                //DeCapitalize the attributes
                Node node = (Node)nodes.get(cnt);
                if(node instanceof Element) {
	                XElement root = (XElement)nodes.get(cnt);
	                if((deCapitalizeAttributes)||(deCapitalizeElementsAndAttributes)) {
		                if(root.attributeCount()>0) {
		                    root.setAttributes(this.deCapitalizeAttributes(root));
		                }
	                }
	                if((deCapitalizeElements)||(deCapitalizeElementsAndAttributes)) {
		                //DeCapitalize the element
		                if(root.getName()!=null) {
		                    
		                    String name = root.getName();
		                    name = deCapitalizeString(name);
		                    Namespace ns = root.getNamespace();
		                    
		                    root.setQName(new QName(name,ns));
		                }
	                }
	                if(traverseChildren) {
	                    //then DeCapitalize its children 	       
	                    iterateTree(root,deCapitalizeElements,deCapitalizeAttributes,deCapitalizeElementsAndAttributes);
	                }
                }
                else if(((deCapitalizeAttributes)||(deCapitalizeElementsAndAttributes))&&(node instanceof Attribute)) {
                    Attribute att = (Attribute)node;
                    node.getParent().setAttributes(deCapitalizeAttributes((XElement)(node.getParent()),att));
                    
                }
            }
            document.update();
        }catch (NullPointerException e) {
            MessageHandler.showError(parent,"Error - Cannot DeCapitalize,\nElements or Attributes not found","Tools DeCapitalize Error");
            return(null);
            
        }catch (Exception e) {
            MessageHandler.showError(parent,"Error - Cannot DeCapitalize Document","Tools DeCapitalize Error");
            return(null);
        }
        return document.getText();
    }
    
    /**
     * Iterate through the tree to decapitalize elements and attributes based on the flags
     * @param element the element the method is working on
      * @param deCapitalizeElements flag as to whether to decapitalize elements
     * @param deCapitalizeAttributes flag as to whether to decapitalize attributes
     * @param deCapitalizeElementsAndAttributes flag as to whether to decapitalize elements and attributes
     * @throws Exception
     */
    private void iterateTree(Element element,boolean deCapitalizeElements, 
            boolean deCapitalizeAttributes, boolean deCapitalizeElementsAndAttributes) throws Exception{
        
        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
            Node oldNode = element.node(i);
            if(oldNode instanceof Element) {
                
                XElement oldElement = (XElement)oldNode;
                
                if((deCapitalizeAttributes)||(deCapitalizeElementsAndAttributes)) {
                    
                    oldElement.setAttributes(this.deCapitalizeAttributes(oldElement));
                    
                }
                
                //DeCapitalize the element
                if(((deCapitalizeElements)||(deCapitalizeElementsAndAttributes))&&(oldElement.getName()!=null)) {
                    
                    String name = oldElement.getName();
                    name = deCapitalizeString(name);
                    Namespace ns = oldElement.getNamespace();
                    
                    oldElement.setQName(new QName(name,ns));
                    
                }
                iterateTree(oldElement,deCapitalizeElements,deCapitalizeAttributes,deCapitalizeElementsAndAttributes);
                
            }
        }
    }
    
    /**
     * decapitalize an elements attributes
     * @param parent the element containing the attributes
     * @return the new list of attributes for that element
     * @throws Exception
     */
    private List deCapitalizeAttributes(XElement parent) throws Exception {
        
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
            name = deCapitalizeString( name);
            String value = attArray[cnt].getValue();
            Namespace ns = attArray[cnt].getNamespace();
            
            attArray[cnt] = new XAttribute(new QName( name, ns), value);
        }
        
        //then remove all previous and add all the attributes back into the document
        List newAttributes = Arrays.asList(attArray);
        return(newAttributes);
    }
    
    /**
     * 
     * decapitalize an elements attribute which matches the passed in attribute
     * @param parent the element containing the attributes
     * @param att the attribute to match
     * @return the new list of attributes for that element
     * @throws Exception
     */
    private List deCapitalizeAttributes(XElement parent, Attribute att) throws Exception {
        
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
	            name = deCapitalizeString( name);
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
     * DeCapitalize the first letter found in a string
     * @param value
     * @return the DeCapitalized string
     */
    private String deCapitalizeString(String value) throws Exception{
        
        String toReturn = null;
        toReturn = value;
        //need to find the first alpha. char and DeCapitalize it
        boolean found = false;
        int charCnt = 0;
        char[] cBuff = value.toCharArray();
        while(!found) {
            char c = cBuff[charCnt];
            //Character cChar = new Character(c);
            if(Character.isLetter(c)){
                //its a letter
                //found = true
                found = true;
            }
            else
                charCnt++;
        }
        if(found) {
            //DeCapitalize character
            cBuff[charCnt] = Character.toLowerCase(cBuff[charCnt]);
            toReturn = new String(cBuff);
        }
        
        return toReturn;
    }
    
    /**
     * check whether this is a whitespace node or not
     * @param node the node to check
     * @return boolean true if node is whitespace else false
     * @throws Exception
     */
    private static boolean isWhiteSpace( Node node) throws Exception{
        return node.getText().trim().length() == 0;
    }
    
    
}