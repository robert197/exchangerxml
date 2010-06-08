/*
 * $Id: ToolsCapitalizeAction.java,v 1.16 2004/10/27 17:03:51 tcurley Exp $ 
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
import com.cladonia.xngreditor.ToolsCapitalizeDialog;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to capitalize elements or attributes.
 *
 * @version	$Revision: 1.16 $, $Date: 2004/10/27 17:03:51 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsCapitalizeAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private ToolsCapitalizeDialog dialog = null;
    private Editor editor = null;
    private ConfigurationProperties props;
    
    /**
     * The constructor for the action which allows capitalizint of elements or attributes
     *
     * @param parent the parent frame.
     */
    public ToolsCapitalizeAction( ExchangerEditor parent, Editor editor, ConfigurationProperties props) {
        super( "Capitalize Elements and Attributes ...");
        
        this.parent = parent;
        this.props = props;
        
        putValue( MNEMONIC_KEY, new Integer( 'C'));
        putValue( SHORT_DESCRIPTION, "Capitalize Elements and Attributes ...");
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
            dialog = new ToolsCapitalizeDialog( parent,props);
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
    			
    			           if((dialog.elementsRadio.isSelected())||(dialog.attributeRadio.isSelected())||
    			                   (dialog.elementsAndAttributesRadio.isSelected())) {
	    			            String newDocument = null;
	    	                    ExchangerDocument tempDoc =  new ExchangerDocument(document.getText());
	
	    	                    boolean TRAVERSE_CHILDREN = true;
	    	                    
	    	                    if(dialog.xpathPanel.xpathBox.isSelected()) {
			                        String xpathPredicate = dialog.xpathPanel.getXpathPredicate();
			                        newDocument = ToolsCapitalizeAction.this.capitalize(tempDoc,xpathPredicate,
			                                dialog.elementsRadio.isSelected(),
			                                dialog.attributeRadio.isSelected(),
			                                dialog.elementsAndAttributesRadio.isSelected(),
			                                TRAVERSE_CHILDREN);
			                        
			                    }
			                    else {
			                        
			                        //set the string to the new capitalize document
			                        newDocument = ToolsCapitalizeAction.this.capitalize(tempDoc,
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
    	                    MessageHandler.showError( parent, "Cannot Capitalize Document","Tools Capitalize Error");
    			 		} finally {
    				 		parent.setStatus( "Done");
    				 		parent.setWait( false);
    			 		}
    	 			}
    	 		};
    	 		
    	 		// Create and start the thread ...
    	 		Thread thread = new Thread( runner);
    	 		thread.start();
//            }
        }
    }
    
    private String capitalize(ExchangerDocument document, boolean capitalizeElements, 
            boolean capitalizeAttributes, boolean capitalizeElementsAndAttributes) {
        
        try {
            XElement root = document.getRoot();
            
            if(((capitalizeAttributes)||(capitalizeElementsAndAttributes))&&(root.attributeCount()>0)) {
                root.setAttributes(this.capitalizeAttributes(root));
                
            }
            //capitalize the element
            if(((capitalizeElements)||(capitalizeElementsAndAttributes))&&(root.getName()!=null)) {
                
                String name = root.getName();
                name = capitalizeString(name);
                Namespace ns = root.getNamespace();
                
                root.setQName(new QName(name,ns));
            }
            
            //then capitalize its children 	  
            iterateTree(root,capitalizeElements,capitalizeAttributes,capitalizeElementsAndAttributes);
            
            document.update();
        }
        catch (NullPointerException e) {
            MessageHandler.showError(parent,"Error - Cannot Capitalize,\nElements or Attributes not found","Tools Capitalize Error");
            return(null);
        }
        catch (Exception e) {
            MessageHandler.showError(parent,"Error - Cannot Capitalize Document","Tools Capitalize Error");
            return(null);
        }
        
        return document.getText();
    }
    
    private String capitalize(ExchangerDocument document, String xpath, boolean capitalizeElements, 
            boolean capitalizeAttributes, boolean capitalizeElementsAndAttributes, boolean traverseChildren) {
        //used for xpath expressions
        try {
            Vector nodes = document.search( xpath);
            if(nodes.size()<1) {
                MessageHandler.showError(parent,"Error - Cannot Resolve XPath","Tools Capitalize Error");
                return(null);
            }
            for(int cnt=0;cnt<nodes.size();++cnt) {
                //for each element
                //Capitalize the attributes
                Node node = (Node)nodes.get(cnt);
                if(node instanceof Element) {
                    XElement root = (XElement)nodes.get(cnt);
                    if((capitalizeAttributes)||(capitalizeElementsAndAttributes)) {
                        
                        if(root.attributeCount()>0) {
                            root.setAttributes(this.capitalizeAttributes(root));
	                    }
	                }
	                //capitalize the element
                    if((capitalizeElements)||(capitalizeElementsAndAttributes)) {
		                if(root.getName()!=null) {
		                    
		                    String name = root.getName();
		                    name = capitalizeString(name);
		                    Namespace ns = root.getNamespace();
		                    
		                    root.setQName(new QName(name,ns));
		                }
                    }
	                if(traverseChildren) {
	                    //then capitalize its children 	       
	                    iterateTree(root,capitalizeElements,capitalizeAttributes,capitalizeElementsAndAttributes);
	                }
                }
                
                else if(((capitalizeAttributes)||(capitalizeElementsAndAttributes))&&(node instanceof Attribute)) {
                    Attribute att = (Attribute)node;
                    node.getParent().setAttributes(capitalizeAttributes((XElement)(node.getParent()),att));
                    
                }
            }
            document.update();
        }catch (NullPointerException e) {
            MessageHandler.showError(parent,"Error - Cannot Capitalize,\nElements or Attributes not found","Tools Capitalize Error");
            return(null);
        }
        catch (Exception e) {
            MessageHandler.showError(parent,"Error - Cannot Capitalize Document","Tools Capitalize Error");
            return(null);
        }
        return document.getText();
    }
    
    private void iterateTree(Element element,boolean capitalizeElements, 
            boolean capitalizeAttributes,boolean capitalizeElementsAndAttributes) throws Exception{
        
        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
            Node oldNode = element.node(i);
            if(oldNode instanceof Element) {
                
                XElement oldElement = (XElement)oldNode;
                
                if(((capitalizeAttributes)||(capitalizeElementsAndAttributes))&&(oldElement.attributeCount()>0)) {
                    oldElement.setAttributes(this.capitalizeAttributes(oldElement));
                    
                }
                
                //capitalize the element
                if(((capitalizeElements)||(capitalizeElementsAndAttributes))&&(oldElement.getName()!=null)) {
                    
                    String name = oldElement.getName();
                    name = capitalizeString(name);
                    Namespace ns = oldElement.getNamespace();
                    
                    oldElement.setQName(new QName(name,ns));
                    
                }
                iterateTree(oldElement,capitalizeElements,capitalizeAttributes,capitalizeElementsAndAttributes);
                
            }
        }
    }
    
    private List capitalizeAttributes(XElement root) throws Exception {
        
        int attributeCount = root.attributeCount();
        List attributeList = root.attributes();
        //create an array to hold all the attributes
        Attribute[] attArray = new Attribute[attributeCount];
        
        for(int cnt=0;cnt<attributeCount;++cnt) {
            //add each attribute to the array
            attArray[cnt] = (Attribute)attributeList.get(cnt);
        }
        //work on the array
        for(int cnt=0;cnt<attributeCount;++cnt) {
            String name = attArray[cnt].getName();
            name = capitalizeString( name);
            String value = attArray[cnt].getValue();
            Namespace ns = attArray[cnt].getNamespace();
            
            attArray[cnt] = new XAttribute(new QName( name, ns), value);
        }
        
        //then remove all previous and add all the attributes back into the document
        List newAttributes = Arrays.asList(attArray);
        return(newAttributes);
        
    }
    
    private List capitalizeAttributes(XElement root, Attribute att) throws Exception {
        
        int attributeCount = root.attributeCount();
        List attributeList = root.attributes();
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
                name = capitalizeString( name);
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
     * Capitalize the first letter found in a string
     * @param value
     * @return the capitalized string
     */
    private String capitalizeString(String value) throws Exception{
        
        String toReturn = null;
        toReturn = value;
        //need to find the first alpha. char and capitalize it
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
            //capitalize character
            cBuff[charCnt] = Character.toUpperCase(cBuff[charCnt]);
            toReturn = new String(cBuff);
        }
        
        return toReturn;
    }
    
    private static boolean isWhiteSpace( Node node) throws Exception{
        return node.getText().trim().length() == 0;
    }
    
    
}