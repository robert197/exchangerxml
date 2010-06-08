/*
 * $Id: XmlTree.java,v 1.4 2005/08/29 08:30:50 gmcgoldrick Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.xdiff;

import java.util.Enumeration;
import java.util.EventListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.bounce.QTree;
import org.dom4j.Text;

import com.cladonia.xml.XElement;

/**
 * The XDiff tree
 *
 * @version	$Revision: 1.4 $, $Date: 2005/08/29 08:30:50 $
 * @author Dogsbay
 */
public class XmlTree extends QTree {
	DefaultTreeModel model = null;
	XmlElementNode root = null;
//	XElement rootElement = null;
//	Viewer viewer = null;

	public XmlTree() {
		super();
		
//		root = new XmlElementNode( viewer, element);
		model = new DefaultTreeModel( new DefaultMutableTreeNode());
		
		setModel( model);
		putClientProperty( "JTree.lineStyle", "None");
		setEditable( false);
		setShowsRootHandles( true);
		getSelectionModel().setSelectionMode( TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		setCellRenderer( new XmlCellRenderer());
		setRootVisible( true);

//		System.out.println( "getRowHeight() ["+getRowHeight()+"]");
		setRowHeight( 0);
	}
	
	/**
	 * Sets the look and feel to the XML Tree UI look and feel.
	 * Override this method if you want to install a different UI.
	 */
	public void updateUI() {
	    setUI( XmlTreeUI.createUI( this));
	}

	public boolean isFixedRowHeight() {
		return false;
	}

	public void setRowHeight( int height) {
//		System.out.println( "setRowHeight( "+height+")");
		super.setRowHeight( 0);
	}

	public void expandAll() {
		expandNode( root);
	}
	
//	public void collapseAll() {
//		collapseNode( root);
//	}

	public void setRoot( XmlElementNode root) {
//		System.out.println( "XmlTree.setRoot( "+root+")");
		this.root = root;
		
		if ( root != null) {
			model.setRoot( root);
		} else {
			model.setRoot( new DefaultMutableTreeNode());
		}
	}

	public void update() {
		setRoot( root);

		model.nodeStructureChanged( root);
		expand( 3);
	}

	public void changed() {
		changed( root);
	}

	public void changed( XmlElementNode node) {
	
		Enumeration enumeration = node.children();
		
		model.nodeStructureChanged( node);

		while ( enumeration.hasMoreElements()) {
			changed( (XmlElementNode)enumeration.nextElement());
			
		}
	}
	
	public void nodeChanged( XmlElementNode node) 
	{
		model.nodeChanged( node);
	}
	
	public void removeNode(XmlElementNode node)
	{
		model.removeNodeFromParent(node);
	}
	
	public void insertNode(XmlElementNode node,XmlElementNode parent,int index)
	{
		model.insertNodeInto(node,parent,index);
	}

	public void expand( int level) {
		expandNode( root, level);
	}
	
	/**
	 * Selects the node for the given element.
	 *
	 * @param element the element to select the node for.
	 */
	public void setSelectedNode( XElement element, boolean end) {
		XmlElementNode node = getNode( element, end);
		
		if ( node != null) {
			TreePath path = new TreePath( model.getPathToRoot( node));
			expandPath( path);
			addSelectionPath( path);
			scrollPathToVisible( path);
		}
	}
	
	/**
	 * Selects the node for the given element.
	 *
	 * @param element the element to select the node for.
	 */
	public void setSelectedNode(Text text) {
		
		XmlElementNode node = getTextNode(text);
		
		if ( node != null) {
			TreePath path = new TreePath( model.getPathToRoot( node));
			expandPath( path);
			addSelectionPath( path);
			scrollPathToVisible( path);
		}
	}
	
	/**
	 * Selects the node for the given XmlElementNode
	 *
	 * @param node the node to select
	 */
	public void setSelectedNode( XmlElementNode node) {
		if ( node != null) {
			TreePath path = new TreePath( model.getPathToRoot( node));
			expandPath( path);
			addSelectionPath( path);
			scrollPathToVisible( path);
		}
	}

	/**
	 * Selects the node for the given element.
	 *
	 * @param element the element to select the node for.
	 */
	public XElement getSelectedElement() {
		TreePath path = getSelectionPath();

		if ( path != null) {
			XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
			
			return node.getElement();
		}
		
		return null;
	}
	
	/**
	 * Returns a node for the XElement supplied.
	 *
	 * @param element the element to get the node for.
	 *
	 * @return the element node.
	 */
	public XmlElementNode getNode( XElement element, boolean end) {
		return getNode( root, element, end);
	}
	
	private XmlElementNode getNode( XmlElementNode node, XElement element, boolean end) {
		
		if ( element.equals( node.getElement()) && node.isEndTag() == end) {
			return node;
		} else {
			Enumeration e = node.children();

			while ( e.hasMoreElements()) {
				XmlElementNode childNode = getNode( (XmlElementNode)e.nextElement(), element, end);

				if ( childNode != null) {
					return childNode;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a node for the Text node supplied.
	 *
	 * @param text the Text node to get the node for.
	 *
	 * @return the Xml element node.
	 */
	public XmlElementNode getTextNode( Text text) {
		return getNode( root, text);
	}
	
	private XmlElementNode getNode( XmlElementNode node, Text text) {
		
		if (node instanceof XmlTextNode)
		{
			XmlTextNode textNode = (XmlTextNode)node;
			if (text == textNode.getTextNode() || text == textNode.getDummyNode())
			{
				return textNode;
			}
		}
		
		 else {
			Enumeration e = node.children();

			while ( e.hasMoreElements()) 
			{
				XmlElementNode childNode = getNode( (XmlElementNode)e.nextElement(), text);
				

				if ( childNode != null) {
					return childNode;
				}
			}
		}
		
		return null;
	}
	
	protected void removeAllListeners() {
		// Guaranteed to return a non-null array
		Object[] list = listenerList.getListenerList();
		
		for ( int i = list.length-2; i >= 0; i -= 2) {
			listenerList.remove( (Class)list[i], (EventListener)list[i+1]);
		}
	}

	public void cleanup() {
		if ( root != null) {
			root.cleanup();
		}

		finalize();
	}
	
	protected void finalize() {
		removeAllListeners();
		
		model = null;
		root = null;
	}
} 
