/*
 * $Id: XmlTree.java,v 1.3 2005/08/29 08:30:50 gmcgoldrick Exp $
 *
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.mozilla.org/MPL/ 
 *
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is eXchaNGeR Skeleton code. (org.xngr.skeleton.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd.. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */
package com.cladonia.xml.viewer;

import java.util.Enumeration;
import java.util.EventListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.bounce.QTree;

import com.cladonia.xml.XElement;

/**
 * The explorer of documents in the system.
 *
 * @version	$Revision: 1.3 $, $Date: 2005/08/29 08:30:50 $
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
