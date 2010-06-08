/*
 * $Id: ToolsConvertNodeAction.java,v 1.11 2004/10/28 07:46:20 tcurley Exp $ 
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
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.tree.FlyweightCDATA;
import org.dom4j.tree.FlyweightComment;
import org.dom4j.tree.FlyweightProcessingInstruction;
import org.dom4j.tree.FlyweightText;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.ToolsConvertNodeDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to add one of various types of node by xpath.
 * 
 * @version $Revision: 1.11 $, $Date: 2004/10/28 07:46:20 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsConvertNodeAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private ToolsConvertNodeDialog dialog = null;
    private Editor editor = null;
    private ConfigurationProperties props;
    
    /**
     * The constructor for the action which allows to add one of various types
     * of node by xpath.
     * 
     * @param parent
     *            the parent frame.
     */
    public ToolsConvertNodeAction(ExchangerEditor parent, Editor editor,
            ConfigurationProperties props) {
        
        super("Convert Nodes ...");
        this.parent = parent;
        this.props = props;
        //this.properties = props;
        putValue(MNEMONIC_KEY, new Integer('C'));
        putValue(SHORT_DESCRIPTION, "Convert Nodes ...");
    }
    
    /**
     * Sets the current view.
     * 
     * @param view
     *            the current view.
     */
    public void setView(Object view) {
        
        if (view instanceof Editor) {
            editor = (Editor) view;
        }
        else {
            editor = null;
        }
        setDocument(parent.getDocument());
    }
    
    /**
     * set the current document
     * 
     * @param doc
     */
    public void setDocument(ExchangerDocument doc) {
        
        if (doc != null && doc.isXML()) {
            setEnabled(editor != null);
        }
        else {
            setEnabled(false);
        }
    }
    
    /**
     * The implementation of the validate action, called after a user action.
     * 
     * @param event
     *            the action event.
     */
    public void actionPerformed(ActionEvent event) {
        
        if (dialog == null) {
            dialog = new ToolsConvertNodeDialog(parent, props);
        }
        //called to make sure that the model is up to date to
        //prevent any problems found when undo-ing etc.
        parent.getView().updateModel();
        //get the document
        final ExchangerDocument document = parent.getDocument();
        if (document.isError()) {
            MessageHandler.showError(parent,
                    "Please make sure the document is well-formed.",
            "Parser Error");
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
        
        if (!dialog.isCancelled()) {
            parent.setWait(true);
            parent.setStatus("Converting Nodes ...");
            // Run in Thread!!!
            Runnable runner = new Runnable() {
                
                public void run() {
                    
                    try {
                        ExchangerDocument tempDoc = new ExchangerDocument(
                                document.getText());
                        String newString = convertNode(tempDoc, dialog.xpathPanel
                                .getXpathPredicate(),
                                (String) dialog.nodeTypeCombo.getSelectedItem());
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
                    }
                    catch (Exception e) {
                        // This should never happen, just report and continue
                        MessageHandler.showError(parent,"Cannot Convert Nodes","Tools Convert Node Error");
                    }
                    finally {
                        parent.setStatus("Done");
                        parent.setWait(false);
                    }
                }
            };
            // Create and start the thread ...
            Thread thread = new Thread(runner);
            thread.start();
            //        }
        }
    }
    
    /**
     * add one of various types of node to the xpath - selected nodes
     * 
     * @param document
     * @param xpathPredicate
     * @param nodeType
     * @return
     */
    public String convertNode(ExchangerDocument document, String xpathPredicate,
            String nodeType) {
        
        Vector nodeList = document.search(xpathPredicate);
        Vector attributeList = new Vector();
        String warning = "";
        if (nodeList.size() > 0) {
            try {
                for (int cnt = 0; cnt < nodeList.size(); ++cnt) {
                    Node node = (Node) nodeList.get(cnt);
                    if (node instanceof Element) {
                        XElement e = (XElement) node;
                        Element parentE = e.getParent();
                        if ((e.hasChildElements()) || (e.attributeCount() > 0)) {
                            if ((e.hasChildElements())) {
                                MessageHandler.showError(parent,"Cannont convert since node has child elements ","Tools Convert Node Error");
                                return (null);
                            }
                            else if (e.attributeCount() > 0) {
                                MessageHandler.showError("Cannont convert since node has attributes","Tools Convert Node Error");
                                return (null);
                            }
                            cnt = nodeList.size();
                            
                        }
                        else {
                            if (nodeType.equalsIgnoreCase("Element Node")) {
                                MessageHandler.showError(parent,"Node is already an Element","Tools Convert Node Error");
                                //end loop
                                cnt = nodeList.size();
                                return(null);
                            }
                            else if (nodeType.equalsIgnoreCase("Attribute Node")) {
                                //check if it has child elements
                                QName qname = e.getQName();
                                //resolve the namespace string
                                parentE
                                .add(new XAttribute(qname, e.getValue()));
                                parentE.remove(e);
                            }
                            else if (nodeType.equalsIgnoreCase("Text Node")) {
                                FlyweightText newNode = new FlyweightText(e
                                        .getText());
                                parentE.add(newNode);
                                parentE.remove(e);
                            }
                            else if (nodeType
                                    .equalsIgnoreCase("CDATA Section Node")) {
                                FlyweightCDATA newNode = new FlyweightCDATA(e
                                        .getText());
                                parentE.add(newNode);
                                parentE.remove(e);
                            }
                            else if (nodeType
                                    .equalsIgnoreCase("Processing Instruction Node")) {
                                FlyweightProcessingInstruction newNode = new FlyweightProcessingInstruction(
                                        e.getText(), "");
                                parentE.add(newNode);
                                parentE.remove(e);
                            }
                            else if (nodeType.equalsIgnoreCase("Comment Node")) {
                                FlyweightComment newNode = new FlyweightComment(
                                        e.getText());
                                parentE.add(newNode);
                                parentE.remove(e);
                            }
                        }
                    }
                    else if (node instanceof Attribute) {
                        XAttribute e = (XAttribute) node;
                        Element parentE = e.getParent();
                        if (nodeType.equalsIgnoreCase("Element Node")) {
                            QName qname = e.getQName();
                            //resolve the namespace string
                            XElement newE = new XElement(qname);
                            parentE.add(newE);
                            newE.setValue(e.getValue());
                            parentE.remove(e);
                        }
                        else if (nodeType.equalsIgnoreCase("Attribute Node")) {
                            MessageHandler.showError(parent,"Node is already an Attribute","Tools Convert Node Error");
                            //end loop
                            cnt = nodeList.size();
                            return(null);
                        }
                        else if (nodeType.equalsIgnoreCase("Text Node")) {
                            FlyweightText newNode = new FlyweightText(e
                                    .getText());
                            parentE.add(newNode);
                            parentE.remove(e);
                        }
                        else if (nodeType
                                .equalsIgnoreCase("CDATA Section Node")) {
                            FlyweightCDATA newNode = new FlyweightCDATA(e
                                    .getText());
                            parentE.add(newNode);
                            parentE.remove(e);
                        }
                        else if (nodeType
                                .equalsIgnoreCase("Processing Instruction Node")) {
                            FlyweightProcessingInstruction newNode = new FlyweightProcessingInstruction(
                                    e.getText(), "");
                            parentE.add(newNode);
                            parentE.remove(e);
                        }
                        else if (nodeType.equalsIgnoreCase("Comment Node")) {
                            FlyweightComment newNode = new FlyweightComment(e
                                    .getText());
                            parentE.add(newNode);
                            parentE.remove(e);
                        }
                    }
                    else {
                        //can only handle elements
                        MessageHandler.showError(parent,"Can only Convert Nodes to elements\n+"
                                + "XPath: " + xpathPredicate
                                + "refers to a"
                                + node.getNodeTypeName(),
                        "Tools Convert Node Error");
                        //end for loop
                        cnt = nodeList.size();
                    }
                }
            }
            catch (NullPointerException e) {
                MessageHandler
                .showError(parent, "XPath: " + xpathPredicate
                        + "\nCannot be resolved",
                "Tools Convert Node Error");
                return (null);
            }
            catch (Exception e) {
                MessageHandler.showError(parent, "Error Adding Node",
                "Tools Convert Node Error");
                return (null);
            }
            document.update();
        }
        else {
            MessageHandler.showError(parent, "No nodes could be found for:\n"
                    + xpathPredicate, "Tools Convert Node Error");
            return (null);
        }
        return (document.getText());
    }
}
