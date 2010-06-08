/*
 * $Id: ToolsAddNodeAction.java,v 1.11 2004/10/28 07:46:19 tcurley Exp $ 
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

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.tree.FlyweightCDATA;
import org.dom4j.tree.FlyweightComment;
import org.dom4j.tree.FlyweightProcessingInstruction;
import org.dom4j.tree.FlyweightText;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.ToolsAddNodeDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;



/**
 * An action that can be used to add one of various types of node by xpath.
 *
 * @version	$Revision: 1.11 $, $Date: 2004/10/28 07:46:19 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsAddNodeAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private ToolsAddNodeDialog dialog = null;
    private Editor editor = null;
    private ConfigurationProperties props;
    
    /**
     * The constructor for the action which allows to add one of various types of node by xpath.
     *
     * @param parent the parent frame.
     */
    public ToolsAddNodeAction( ExchangerEditor parent, Editor editor, ConfigurationProperties props) {
        super( "Add Nodes ...");
        
        this.parent = parent;
        this.props = props;
                
        putValue( MNEMONIC_KEY, new Integer( 'A'));
        putValue( SHORT_DESCRIPTION, "Add Nodes ...");
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
    
    /**
     * set the current document
     * @param doc
     */
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
            dialog = new ToolsAddNodeDialog( parent,props);
        }
        //called to make sure that the model is up to date to 
        //prevent any problems found when undo-ing etc.
        parent.getView().updateModel();
        
        //get the document
        final ExchangerDocument document = parent.getDocument();
        
        if ( document.isError()) {
            MessageHandler.showError( "Please make sure the document is well-formed.", "Parser Error");
            return;
        }
        
        String currentXPath = null;
        Node node = (Node)document.getLastNode( parent.getView().getEditor().getCursorPosition(), true);

        if ( props.isUniqueXPath()) {
            currentXPath = node.getUniquePath();
        } else {
            currentXPath = node.getPath();
        }
                
        dialog.show(document.getDeclaredNamespaces(),currentXPath);
        
        if(!dialog.isCancelled()) {
            parent.setWait( true);
            parent.setStatus( "Adding Nodes ...");
            
            // Run in Thread!!!
            Runnable runner = new Runnable() {
                public void run()  {
                    try {
                        ExchangerDocument tempDoc =  new ExchangerDocument(document.getText());
                        
                        String newString = addNode(tempDoc,dialog.xpathPanel.getXpathPredicate(),
                                (String)dialog.nodeTypeCombo.getSelectedItem(),
                                (String)dialog.namespaceCombo.getSelectedItem(),
                                dialog.prefixTextField.getText(),
                                dialog.nameTextField.getText(),
                                dialog.valueTextField.getText());
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
                        MessageHandler.showError( parent, "Error - Cannot Add Nodes", "Add Node Error");
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
     * add one of various types of node to the xpath - selected nodes
     * 
     * @param document
     * @param xpathPredicate
     * @param nodeType
     * @param namespace
     * @param name
     * @param value
     * @return
     */
    public String addNode(ExchangerDocument document, String xpathPredicate, String nodeType,
            String namespace,String prefix, String name, String value) throws Exception {
        
        Vector nodeList = document.search(xpathPredicate);
        Vector attributeList = new Vector();
        
        String warning = "";
        if(nodeList.size()>0) {
            try {
                
                for(int cnt=0;cnt<nodeList.size();++cnt) {
                    
                    Node node = (Node)nodeList.get(cnt);
                    if (node instanceof Element) {
                        XElement e = (XElement) node;
                        
                        if(nodeType.equalsIgnoreCase("Element Node")) {
                            
                            
                            
                            QName qname = null;
                            //resolve the namespace string
                            
                            if(!namespace.equalsIgnoreCase("none")) {
                                Namespace newNs;
                                try {
                                    newNs = new Namespace(prefix,namespace);
                                    qname = new QName(name,newNs);
                                }
                                catch (StringIndexOutOfBoundsException e1) {
                                    //cannot parse string
                                    MessageHandler.showError(parent,"Could not resolve Namespace:\n"+namespace,
                                    "Tools Add Node");
                                    qname = new QName(name);
                                }
                                
                            }
                            else {
                                qname = new QName(name);
                            }
                            XElement newNode = new XElement(qname);
                            e.add(newNode);
                            newNode.setValue(value);
                            
                            
                        }
                        else if(nodeType.equalsIgnoreCase("Attribute Node")) {
                            
                            QName qname = null;
                            //resolve the namespace string
                            
                            if(!namespace.equalsIgnoreCase("none")) {
                                Namespace newNs;
                                try {
                                    newNs = new Namespace(namespace.substring(0,namespace.indexOf(":")),
                                            namespace.substring(namespace.indexOf(":")+1,namespace.length()));
                                    qname = new QName(name,newNs);
                                }
                                catch (StringIndexOutOfBoundsException e1) {
                                    //cannot parse string
                                    MessageHandler.showError(parent,"Could not resolve Namespace:\n"+namespace,
                                    "Tools Add Node");
                                    qname = new QName(name);
                                }
                                
                            }
                            else {
                                qname = new QName(name);
                            }
                            XAttribute newNode = new XAttribute(qname,value);
                            
                            e.add(newNode);
                            
                        }
                        else if(nodeType.equalsIgnoreCase("Text Node")) {
                            FlyweightText newNode = new FlyweightText(value);
                            
                            e.add(newNode);
                            
                            
                        }
                        else if(nodeType.equalsIgnoreCase("CDATA Section Node")) {
                            FlyweightCDATA newNode = new FlyweightCDATA(value);
                            
                            e.add(newNode);
                            
                        }
                        else if(nodeType.equalsIgnoreCase("Processing Instruction Node")) {
                            FlyweightProcessingInstruction newNode = 
                                new FlyweightProcessingInstruction(name,value); 
                            e.add(newNode);
                        }
                        else if(nodeType.equalsIgnoreCase("Comment Node")) {
                            FlyweightComment newNode = 
                                new FlyweightComment(value); 
                            e.add(newNode);
                            
                        }
                        
                        
                        
                    }
                    else if (node instanceof Document) {
                        XDocument d = (XDocument) node;
                        
                        if(nodeType.equalsIgnoreCase("Processing Instruction Node")) {
                            FlyweightProcessingInstruction newNode = 
                                new FlyweightProcessingInstruction(name,value); 
                            d.add(newNode);
                        }
                        else if(nodeType.equalsIgnoreCase("Comment Node")) {
                            FlyweightComment newNode = 
                                new FlyweightComment(value); 
                            d.add(newNode);
                            
                        }
                        else {
                            //cant handle any others
//                          can only handle elements
                            MessageHandler.showError(parent, "Cannot add nodes to this xpath\n+" +
                                    "XPath: "+xpathPredicate + "refers to a"+node.getNodeTypeName(),"Tools Add Node");
                            //end for loop
                            cnt=nodeList.size();
                            return(null);
                        }
                        
                        
                    }
                    
                    else {
                        //can only handle elements
                        MessageHandler.showError(parent, "Cannot add nodes to this xpath\n+" +
                                "XPath: "+xpathPredicate + "refers to a"+node.getNodeTypeName(),"Tools Add Node");
                        //end for loop
                        cnt=nodeList.size();
                        return(null);
                    }
                }
                
            }catch (NullPointerException e) {
                MessageHandler.showError(parent, "XPath: "+xpathPredicate+"\nCannot be resolved","Tools Add Node");
                return(null);
            }
            catch (Exception e) {
                MessageHandler.showError(parent, "Error Adding Node","Tools Add Node");
                return(null);
            }
            
            document.update();
        }
        else {
            MessageHandler.showError(parent, "No nodes could be found for:\n"+xpathPredicate,"Tools Add Node");
            return(null);
        }
        
        return(document.getText());
    }
}
