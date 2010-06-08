/*
 * $Id: ToolsSortNodeAction.java,v 1.14 2004/10/27 15:38:46 tcurley Exp $ 
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
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.ToolsSortNodeDialog;

/**
 * An action that can be used to Sort Node By XPath
 * 
 * @version $Revision: 1.14 $, $Date: 2004/10/27 15:38:46 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsSortNodeAction extends AbstractAction {

    private static final boolean DEBUG = false;
    private ExchangerEditor parent = null;
    private Editor editor = null;
    private ConfigurationProperties props;
    private ToolsSortNodeDialog dialog = null;
    private boolean USE_SORTED_ID = true;
    private boolean USE_ORIGINAL_ID = false;
    private int originalCounter = 0;

    /**
     * The constructor for the action which allows Sort Node By XPath
     * 
     * @param parent
     *            the parent frame.
     */
    public ToolsSortNodeAction(ExchangerEditor parent, Editor editor,
            ConfigurationProperties props) {

        super("Sort Nodes ...");
        this.parent = parent;
        this.props = props;
        //this.properties = props;
        putValue(MNEMONIC_KEY, new Integer('S'));
        putValue(SHORT_DESCRIPTION, "Sort Nodes ...");
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
            dialog = new ToolsSortNodeDialog(parent, props);
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
        //create temporary document
        String currentXPath = null;
        Node node = (Node)document.getLastNode( parent.getView().getEditor().getCursorPosition(), true);

        if ( props.isUniqueXPath()) {
            currentXPath = node.getUniquePath();
        } else {
            currentXPath = node.getPath();
        }
        dialog.show(currentXPath);
        if (!dialog.isCancelled()) {
            //get the new vector of namespaces
            //Vector newNamespaces = dialog.getNamespaces();
            parent.setWait(true);
            parent.setStatus("Sorting Nodes ...");
            // Run in Thread!!!
            Runnable runner = new Runnable() {

                public void run() {

                    try {
                        dialog.mappingPanel.save();
                        ExchangerDocument tempDoc = new ExchangerDocument(
                                document.getText());
                        String newDocument = sortByXPath(tempDoc, dialog
                                .getXpathPredicate(), dialog
                                .getSortXpathPredicate(), dialog.ascendingRadio
                                .isSelected());
                        if (newDocument != null) {
                            if (dialog.toNewDocumentRadio.isSelected()) {
                                //user has selected to create the result as a
                                // new document
                                parent.open(new ExchangerDocument(newDocument),
                                        null);
                            }
                            else {
                                parent.getView().getEditor().setText(
                                        newDocument);
                                SwingUtilities.invokeLater(new Runnable() {

                                    public void run() {

                                        parent.switchToEditor();
                                        parent.getView().updateModel();
                                    }
                                });
                            }
                        }
                    }
                    catch (Exception e) {
                        // This should never happen, just report and continue
                        MessageHandler.showError(parent, "Cannot Sort Nodes","Tools Sort Node Error");
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
            //          }
        }
    }

    private String sortByXPath(ExchangerDocument document,
            String xpathPredicate, String sortXPathPredicate,
            boolean isAscending) throws Exception {

        //List nodeList = document.getDocument().selectNodes(xpathPredicate);
        Vector nodeList = document.search( xpathPredicate );
        
        try {
            Vector nodes = new Vector();
            if (nodeList.size() != 0) {
                this.originalCounter = 0;
                treeWalk(document,nodeList,nodes);
                
                //              get the sorted list
                DocumentHelper.sort(nodeList, sortXPathPredicate, false);
                //now update nodeinfo's with the sorted order
                                
                for (int ocnt = 0; ocnt <nodeList.size(); ++ocnt ) {
                    
                    //for each node in the sorted list,
                    //find it in the nodeInfo list and set the sorted index
                    Node node = (Node) nodeList.get(ocnt);
                    
                    for (int cnt = 0; cnt < nodes.size(); ++cnt) {
                        NodeInfo ni = (NodeInfo) nodes.get(cnt);
                        if (node == ni.getNode()) {
                            if(isAscending) {
                                ni.setSortedId(ocnt);
                            }
                            else {
                                int id = (nodeList.size()-1)-ocnt;
                                ni.setSortedId(id);
                            }
                            nodes.setElementAt(ni, cnt);
                        }
                    }
                }
                // now need to remove all duplicates of this element from the
                // document
                
                //check for children of parents in the list
                if(checkForChildrenOfParents(nodes)==false) {
                    removeOccurences(xpathPredicate, document);
                    nodes = sortIndexes(nodes, isAscending);
                
                
                
                    insertNodes(document, nodes, isAscending);
                }
                else {
                    //error children and parents mixed
                    MessageHandler.showError(parent,
                            "Cannot Sort: XPath: " + xpathPredicate + " refers to both children and parents",
                            "Tools Sort Node Error");
                    return(null);
                }
            }
            else {
                MessageHandler.showError(parent,
                        "XPath: " + xpathPredicate + " and "
                                + sortXPathPredicate + "\nCannot be resolved",
                        "Tools Sort Node Error");
                //e.printStackTrace();
                return (null);
            }
        }
        catch (NullPointerException e) {
            MessageHandler.showError(parent, "XPath: " + xpathPredicate
                    + "\nCannot be resolved", "Tools Sort Node Error");
            //e.printStackTrace();
            return (null);
        }
        catch (Exception e) {
            MessageHandler.showError(parent, "Cannot Sort Nodes",
                    "Tools Sort Node Error");
            //e.printStackTrace();
            return (null);
        }
        document.update();
        return document.getText();
    }
    
    private boolean checkForChildrenOfParents(Vector nodes) throws Exception {
        
        for(int cnt=0;cnt<nodes.size();++cnt) {
            NodeInfo ni = (NodeInfo)nodes.get(cnt);
            Node n = ni.getNode();
            
            if(n instanceof Element) {
                //System.out.println(((Element)n).attribute(0).getText());
            
	            for(int icnt=0;icnt<nodes.size();++icnt) {
	                NodeInfo ni2 = (NodeInfo)nodes.get(icnt);
	                Element parent = ni2.getParent();
	                
	                if((Element)n==parent) {
	                    //error children and parent in same list
	                    return(true);
	                }
	                
	            }
            }
        }
        
        return(false);
        
    }
    
    private Vector treeWalk(ExchangerDocument document, List nodeList, Vector nodes) throws Exception {
        return(treeWalk( document.getRoot() ,nodeList, nodes));
    }
    
    private Vector treeWalk(Element element, List nodeList, Vector nodes) throws Exception {
       
        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
            Node node = element.node(i);
            
            //search to see if this node is in the nodelist
            for(int cnt=0;cnt<nodeList.size();++cnt) {
                if(node==(Node)nodeList.get(cnt)) {
                    
                    //make sure it doesn't already exist in the nodes vector
                    boolean wasFound = false;
                    for(int icnt=0;icnt<nodes.size();++icnt) {
                        if(node==((NodeInfo)nodes.get(icnt)).getNode()) {
                            wasFound=true;
                        }
                    }
                    if(!wasFound) {
	                    //set this to the next id
	                    Element parent = node.getParent();
	                    int index = parent.indexOf(node);
	                    NodeInfo ni = new NodeInfo(originalCounter, parent, node, index);
	                    nodes.add(ni);
	                    originalCounter++;
                    }
                }
            }
            
            if ( node instanceof Element ) {
                treeWalk( (Element) node ,nodeList, nodes);
            }
            
        }
        
        return(nodes);
    }

    private void removeOccurences(String xpath, ExchangerDocument document)
            throws Exception {

        //select all these nodes by xpath and remove them
        //List nodeList = document.getDocument().selectNodes(xpath);
        List nodeList = document.search(xpath);
        //System.out.println("Removing: "+xpath +": "+nodeList.size());
        int size = nodeList.size();
        if (size > 0) {
            int cnt = 0;
            Node oldNode = (Node) nodeList.get(cnt);
            while (!nodeList.isEmpty()) {
                oldNode = (Node) nodeList.get(cnt);
                oldNode.getParent().remove(oldNode);
                nodeList.remove(cnt);
                //cnt++;
            }
        }
        else {
            //MessageHandler.showError(parent, "No nodes found",
            //"Tools Sort Node Error");
            throw new NullPointerException("no nodes found");
        }
    }

    private void insertNodes(ExchangerDocument document, Vector nodes,
            boolean isAscending) throws Exception {
        
        for (int cnt = 0; cnt < nodes.size(); ++cnt) {
            NodeInfo ni = getById(cnt,nodes,this.USE_SORTED_ID);
                   
            insert(ni.getSortedIndex(), ni.getNode(), ni.getSortedParent());
        }
    }

    private Vector sortIndexes(Vector nodes, boolean isAscending)
            throws Exception {

        Vector tempNodes = new Vector();
        
        for (int cnt = 0; cnt < nodes.size(); ++cnt) {
            //nodes are in order by id's, now need to reorder in document
            //for each node,
            NodeInfo ni = getById(cnt, nodes, USE_SORTED_ID);
            //now get the information from the node whose original id was this
            // sorted id
            NodeInfo oldNi = getById(cnt, nodes, USE_ORIGINAL_ID);
            //copy the index and parent from the old nodeinfo to the new
            // nodeinfo
            
            ni.setSortedIndex(oldNi.getIndex());
            ni.setSortedParent(oldNi.getParent());
            tempNodes.add(ni);
        }
        return (tempNodes);
    }

    private NodeInfo getById(int id, Vector nodes, boolean useSorted) throws Exception {

        for (int cnt = 0; cnt < nodes.size(); ++cnt) {
            NodeInfo ni = (NodeInfo) nodes.get(cnt);
            if (useSorted) {
                if (ni.getSortedId() == id) {
                    return (ni);
                }
            }
            else {
                if (ni.getId() == id) {
                    return (ni);
                }
            }
        }
        return (null);
    }

    private Vector removeFirstOccFromVector(int min, Vector temp) throws Exception {

        for (int cnt = 0; cnt < temp.size(); ++cnt) {
            NodeInfo ni = (NodeInfo) temp.get(cnt);
            if (ni.getIndex() == min) {
                //remove it
                temp.remove(cnt);
            }
        }
        return (temp);
    }

    private void insert(int index, Node e, Element parent) throws Exception {

        List list = parent.content();
        try {
            int nodeCount = 0;
            int elementCount = 0;
            //          List list = elements();
            while (index > nodeCount) {
                Object node = list.get(nodeCount);
                /*
                 * if ( node instanceof XElement) { elementCount++;
                 */
                nodeCount++;
            }
            if (nodeCount < list.size()) {
                list.add(nodeCount, e);
            }
            else {
                list.add(e);
            }
        }
        catch (Exception e1) {
            //if exception is thrown, just add at the end of the document
            //need to find out if the it should add at the very end or are
            // there elements before that
            list.add(e);
        }
    }

    private class NodeInfo {

        private int id;
        private Element parent;
        private Node node;
        private int index;
        private int sortedId;
        private int sortedIndex;
        private Element sortedParent;

        public NodeInfo(int id, Element p, Node n, int i) {

            this.id = id;
            this.parent = p;
            this.node = n;
            this.index = i;
            this.sortedIndex = i;
        }

        public String toString() {

            return ("PARENT: " + parent.getName() + "\n" + node.asXML()
                    + "\nINDEX: " + index);
        }

        public String xpathToParent() {

            return (parent.getPath());
        }

        public String xpathToSortedParent() {

            return (sortedParent.getPath());
        }

        /**
         * @return Returns the index.
         */
        public int getIndex() {

            return index;
        }

        /**
         * @param index
         *            The index to set.
         */
        public void setIndex(int index) {

            this.index = index;
        }

        /**
         * @return Returns the node.
         */
        public Node getNode() {

            return node;
        }

        /**
         * @param node
         *            The node to set.
         */
        public void setNode(Node node) {

            this.node = node;
        }

        /**
         * @return Returns the parent.
         */
        public Element getParent() {

            return parent;
        }

        /**
         * @param parent
         *            The parent to set.
         */
        public void setParent(Element parent) {

            this.parent = parent;
        }

        /**
         * @return Returns the sortedIndex.
         */
        public int getSortedIndex() {

            return sortedIndex;
        }

        /**
         * @param sortedIndex
         *            The sortedIndex to set.
         */
        public void setSortedIndex(int sortedIndex) {

            this.sortedIndex = sortedIndex;
        }

        /**
         * @return Returns the sortedParent.
         */
        public Element getSortedParent() {

            return sortedParent;
        }

        /**
         * @param sortedParent
         *            The sortedParent to set.
         */
        public void setSortedParent(Element sortedParent) {

            this.sortedParent = sortedParent;
        }

        /**
         * @return Returns the id.
         */
        public int getId() {

            return id;
        }

        /**
         * @param id
         *            The id to set.
         */
        public void setId(int id) {

            this.id = id;
        }

        /**
         * @return Returns the sortedId.
         */
        public int getSortedId() {

            return sortedId;
        }

        /**
         * @param sortedId
         *            The sortedId to set.
         */
        public void setSortedId(int sortedId) {

            this.sortedId = sortedId;
        }
    }
}