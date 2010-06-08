/*
 * $Id: XmlTree.java,v 1.4 2005/08/29 08:30:50 gmcgoldrick Exp $
 *
 * Copyright (C) 2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.navigator;

import java.util.Enumeration;
import java.util.EventListener;

import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.bounce.QTree;

import com.cladonia.xml.XElement;

/**
 * The explorer of documents in the system.
 *
 * @version	$Revision: 1.4 $, $Date: 2005/08/29 08:30:50 $
 * @author Dogsbay
 */
public class XmlTree extends QTree {
	DefaultTreeModel model = null;
	XmlElementNode root = null;
//	XElement rootElement = null;
//	Viewer viewer = null;

	public XmlTree( Navigator navigator) {
		super();
		
//		root = new XmlElementNode( viewer, element);
		model = new DefaultTreeModel( new DefaultMutableTreeNode());
		
		setModel( model);
		putClientProperty( "JTree.lineStyle", "Angled");
		setEditable( false);
		setToggleClickCount( 3);
		setShowsRootHandles( true);
		getSelectionModel().setSelectionMode( TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		setCellRenderer( new XmlCellRenderer( navigator));
		setRootVisible( false);
		
		ToolTipManager.sharedInstance().registerComponent( this);

		setFont( getFont().deriveFont( (float)11));
//		setRowHeight( 0);
	}
	
//	public void setRowHeight( int height) {
//		System.out.println( "setRowHeight( "+height+")");
//		super.setRowHeight( 0);
//	}

	/**
	 * Sets the look and feel to the XML Tree UI look and feel.
	 * Override this method if you want to install a different UI.
	 */
//	public void updateUI() {
//	    setUI( XmlTreeUI.createUI( this));
//	}

	public void expandAll() {
		expandNode( root);
	}
	
//	public void collapseAll() {
//		collapseNode( root);
//	}

	public void setRoot( XmlElementNode root) {
//		System.out.println( "XmlTree.setRoot( "+root+")");
		
//		if ( root != null) {
			model.setRoot( root);
			this.root = root;
//		} else {
//			model.setRoot( null);
//			this.root = null;
//		}
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

	public void expand( int level) {
		expandNode( root, level);
	}

//	private void expandNode( TreeNode node, int level) {
//		if ( level > 0) {
//			expandPath( new TreePath( model.getPathToRoot( node)));
//
//			for ( int i = 0; i < node.getChildCount(); i++) {
//				expandNode( node.getChildAt( i), level - 1);
//			}
//		}
//	}

//	private void expandNode( TreeNode node) {
//		expandPath( new TreePath( model.getPathToRoot( node)));
//
//		for ( int i = 0; i < node.getChildCount(); i++) {
//			expandNode( node.getChildAt( i));
//		}
//	}
	
//	private void collapseNode( TreeNode node) {
//		for ( int i = 0; i < node.getChildCount(); i++) {
//			collapseNode( node.getChildAt( i));
//		}
//
//		collapsePath( new TreePath( model.getPathToRoot( node)));
//	}

	/**
	 * Selects the node for the given element.
	 *
	 * @param element the element to select the node for.
	 */
	public void setSelectedNode( XElement element) {
		XmlElementNode node = getNode( element);
		
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
	public XmlElementNode getNode( XElement element) {
		return getNode( root, element);
	}
	
	private XmlElementNode getNode( XmlElementNode node, XElement element) {
		
		if ( node != null) {
			if ( element.equals( node.getElement())) {
				return node;
			} else {
				Enumeration e = node.children();

				while ( e.hasMoreElements()) {
					XmlElementNode childNode = getNode( (XmlElementNode)e.nextElement(), element);

					if ( childNode != null) {
						return childNode;
					}
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
//		if ( root != null) {
//			root.cleanup();
//		}

		finalize();
	}
	
	protected void finalize() {
		removeAllListeners();
		
		model = null;
		root = null;
	}
} 
