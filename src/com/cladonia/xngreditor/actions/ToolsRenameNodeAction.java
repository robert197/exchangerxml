/*
 * $Id: ToolsRenameNodeAction.java,v 1.10 2004/10/27 17:03:51 tcurley Exp $ 
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
import com.cladonia.xngreditor.ToolsRenameNodeDialog;


/**
 * An action that can be used to Rename Node By XPath
 *
 * @version	$Revision: 1.10 $, $Date: 2004/10/27 17:03:51 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsRenameNodeAction extends AbstractAction {
    
    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private Editor editor = null;
    private ConfigurationProperties props;
    private ToolsRenameNodeDialog dialog = null;
    
    /**
     * The constructor for the action which allows Rename Node By XPath
     *
     * @param parent the parent frame.
     */
    public ToolsRenameNodeAction( ExchangerEditor parent, Editor editor, ConfigurationProperties props) {
        super( "Rename Nodes ...");
        
        this.parent = parent;
        this.props = props;
        
        //this.properties = props;
        
        putValue( MNEMONIC_KEY, new Integer( 'N'));
        putValue( SHORT_DESCRIPTION, "Rename Nodes ...");
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
            dialog = new ToolsRenameNodeDialog( parent,props);
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
            parent.setStatus( "Renaming Nodes ...");
            
            // Run in Thread!!!
            Runnable runner = new Runnable() {
                public void run()  {
                    try {
                        
                        ExchangerDocument tempDoc =  new ExchangerDocument(document.getText());
                        
                        String newString  = renameByXPath(tempDoc,
                                dialog.xpathPanel.getXpathPredicate(),
                                dialog.getNewName());
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
                        MessageHandler.showError( parent, "Cannot Rename Nodes","Tools Rename Node Error");
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
    
    private String renameByXPath(ExchangerDocument document, String xpathPredicate, String newName) throws Exception {
        
        //List nodeList = document.getDocument().selectNodes( xpathPredicate );
        Vector nodeList = document.search( xpathPredicate );
        
        try {
            
            for(int cnt=0;cnt<nodeList.size();++cnt) {
                Node node = (Node)nodeList.get(cnt);
//              node.setName(newName);
                
                if (node instanceof Element) {
                    
                    XElement e = (XElement) node;
                    
                    e.setQName(new QName(newName,e.getNamespace()));
                }
                else if(node instanceof Attribute) {
                    
                    XAttribute newAtt = (XAttribute)node;
                    /*int fcnt = findInList(newAtt,attributeList);
                    if(fcnt > -1) {*/
                        //if the attribute matches the one we are looking for
                        //replace it with the new qname
                        String name = newAtt.getName();
                        String oldValue = newAtt.getValue();
                        Namespace ns = newAtt.getNamespace();
                        XElement parent = (XElement) node.getParent();
                        parent.remove(newAtt);
                        parent.add(new XAttribute(new QName( newName, ns), oldValue));
                        
                        //now remove it from the attributeList to speed up the next searches
                        //attributeList.remove(fcnt);
                        
                    //}
                    //attributeList.add((XAttribute) node);
                }
                
            }
        }
        catch (NullPointerException e) {
            MessageHandler.showError(parent, "XPath: "+xpathPredicate+"\nCannot be resolved","Tools Rename Node Error");
            return(null);
        }
        catch (Exception e) {
            MessageHandler.showError(parent, "Cannot Rename Node","Tools Rename Node Error");
            return(null);
        }
        document.update();
        
        return document.getText();
    }
    
    
}