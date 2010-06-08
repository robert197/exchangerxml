/*
 * $Id: TreePanel.java,v 1.1 2004/03/25 18:39:54 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.bounce.QTree;

/**
 * A panel that holds a tree that can display explorer nodes.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:54 $
 * @author Dogsbay
 */
public class TreePanel extends JPanel  { // implements Scrollable {
	private static final boolean DEBUG = false;

	private EventListenerList listeners = null;

	private QTree tree = null;
	private JScrollPane scrollPane = null;
	private DefaultTreeModel treeModel = null;

	private SchemaNode root = null;

	/**
	 * Constructs an explorer view with the ExplorerProperties supplied.
	 *
	 * @param root the root node.
	 */
	public TreePanel() {
		super( new BorderLayout());
		
		listeners = new EventListenerList();
		
//		QPanel treePanel = new QPanel( new BorderLayout());
//		treePanel.setBackground( Color.white);
//		treePanel.setBorder( new EmptyBorder( 2, 2, 2, 2));

		treeModel = new DefaultTreeModel( null);

		tree = new SchemaTree( treeModel);
//		tree.setBackground( Color.white);
		tree.setBorder( new EmptyBorder( 2, 2, 2, 2));
		
		ToolTipManager.sharedInstance().registerComponent( tree);
		tree.setEditable( false);
		tree.setShowsRootHandles( true);
		tree.putClientProperty( "JTree.lineStyle", "Angled");
		tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION);

		SchemaCellRenderer renderer = new SchemaCellRenderer();
		tree.setCellRenderer( renderer);
		tree.setRootVisible( true);
		tree.setExpandsSelectedPaths( true);

		tree.addTreeSelectionListener( new TreeSelectionListener() {
			public void valueChanged( TreeSelectionEvent event) {
				SchemaNode node = (SchemaNode) getSelectedNode();
				fireSelectionChanged( node);
			}
		});

		tree.addMouseListener ( new MouseAdapter() {
			public synchronized void mouseReleased(MouseEvent e) {
				TreePath path = tree.getPathForLocation( e.getX(), e.getY());

				if ( path != null && path.equals( tree.getSelectionPath())) {
					SchemaNode node = (SchemaNode) path.getLastPathComponent();
					
					if ( e.isPopupTrigger()) {
						firePopupTriggered( e, node);
						
					}
					if ( e.getClickCount() == 2) {
						fireDoubleClicked( e, node);
					}
				}
			}
		});
		
//		treePanel.add( tree, BorderLayout.CENTER);

		scrollPane = new JScrollPane(	tree,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		/**
		 * Work around to make sure the scroll pane shows the vertical 
		 * scrollbar for the first time when resized to a size small enough.
		 * JDK 1.3.0-C 
		 *
		 * Got work around from Bug ID: 4243631 (It should be fixed...)
		 *
		 * ED: Check with JDK1.4
		 */
		scrollPane.getViewport().addComponentListener( new ComponentAdapter() {
			public void componentResized( ComponentEvent e) {
				scrollPane.doLayout();
			}
		});
		
		add( scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Adds a node to the parent and fires an event for the 
	 * tree model.
	 *
	 * @param parent the parent node.
	 * @param node the node that needs to be added.
	 */
//	public void addNode( ExplorerNode parent, ExplorerNode node) {
//		int[] index = new int[1];
//
//		index[0] = parent.add( node);
//		treeModel.nodesWereInserted( parent, index);
//	}

	/**
	 * Removes a category from the explorer panel.
	 *
	 * @param the node for the document.
	 */
//	public void removeNode( ExplorerNode node) {
//		treeModel.removeNodeFromParent( node);
//	}

	/**
	 * Fires a structure changed event.
	 *
	 * @param the node for the document.
	 */
	public void nodeChanged( SchemaNode node) {
	
		treeModel.nodeStructureChanged( node);
	}

	/**
	 * Gets the trees root node.
	 *
	 * @return the trees root node.
	 */
	public SchemaNode getRoot() {
		return root;
	}

	/**
	 * Gets the trees root node.
	 *
	 * @return the trees root node.
	 */
	public void setRoot( SchemaNode node) {
		root = node;
		
		treeModel.setRoot( root);
	}

	/**
	 * Gets the tree component.
	 *
	 * @return the tree component.
	 */
	public JTree getTree() {
		return tree;
	}

	/**
	 * Returns the currently selected node, null if nothing 
	 * has been selected.
	 *
	 * @return a schema node.
	 */
	public SchemaNode getSelectedNode() {
		SchemaNode node = null;
		TreePath path = tree.getSelectionPath();
		
		if ( path != null) {
			node = (SchemaNode) path.getLastPathComponent();
		}	
	
		return node;
	}

	/**
	 * Sets the selected node.
	 *
	 * @param node the explorer node.
	 */
	public void setSelectedNode( SchemaNode node) {
		if ( node != null) {
			tree.setSelectionPath( new TreePath( node.getPath()));
		} else {
			tree.clearSelection();
		}
	}

	/** 
	 * Adds an Explorer listener to the list of listeners.
	 *
	 * @param the explorer listener.
	 */
	public void addTreePanelListener( TreePanelListener listener) {
		listeners.add( (Class)listener.getClass(), listener);
	}

	/** 
	 * Removes an Explorer listener from the list of listeners.
	 *
	 * @param the explorer listener.
	 */
	public void removeTreePanelListener( TreePanelListener listener) {
		listeners.remove( (Class)listener.getClass(), listener);
	}

	/**
	 * Collapses all the nodes in the tree.
	 */
	public void collapseAll() {
		tree.collapseAll();
	}

	/**
	 * Collapses all the nodes in the tree.
	 *
	 * @param node the node to collapse all nodes for.
	 */
	public void collapseNode( SchemaNode node) {
		tree.collapseNode( node);
	}

	/**
	 * Expands all the nodes in the tree.
	 */
	public void expandAll() {
		tree.expandAll();
	}

	/**
	 * Expands all the nodes in the tree from this node down.
	 *
	 * @param node the node to expand all nodes for.
	 */
	public void expandNode( SchemaNode node) {
		tree.expandNode( node);
	}

	/**
	 * Expands all the nodes in the tree to a certain level.
	 *
	 * @param node the node to expand all nodes for.
	 */
	public void expand( int level) {
		tree.expandNode( (SchemaNode)treeModel.getRoot(), level);
	}

	/** 
	 * Notifies the listeners about a popup trigger on a node.
	 *
	 * @param the mouse event.
	 * @param the node.
	 */
	protected void fireSelectionChanged( SchemaNode node) {
		// Guaranteed to return a non-null array
		Object[] list = listeners.getListenerList();
		
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = list.length-2; i >= 0; i -= 2) {
			((TreePanelListener)list[i+1]).selectionChanged( node);
		}
	}

	/** 
	 * Notifies the listeners about a popup trigger on a node.
	 *
	 * @param the mouse event.
	 * @param the node.
	 */
	protected void firePopupTriggered( MouseEvent event, SchemaNode node) {
		// Guaranteed to return a non-null array
		Object[] list = listeners.getListenerList();
		
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = list.length-2; i >= 0; i -= 2) {
			((TreePanelListener)list[i+1]).popupTriggered( event, node);
		}
	}

	/** 
	 * Notifies the listeners about a double click on a node.
	 *
	 * @param the mouse event.
	 * @param the node.
	 */
	protected void fireDoubleClicked( MouseEvent event, SchemaNode node) {
		// Guaranteed to return a non-null array
		Object[] list = listeners.getListenerList();
		
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = list.length-2; i >= 0; i -= 2) {
			((TreePanelListener)list[i+1]).doubleClicked( event, node);
		}
	}

// Implementation of Scrollable...
//	public Dimension getPreferredScrollableViewportSize() {
//		return tree.getPreferredScrollableViewportSize();
//	}
//
//	public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction) {
//		return tree.getScrollableBlockIncrement( visibleRect, orientation, direction);
//	}
//
//	public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
//		return tree.getScrollableUnitIncrement( visibleRect, orientation, direction);
//	}
//
//	public boolean getScrollableTracksViewportHeight() {
//		return tree.getScrollableTracksViewportHeight();
//	}
//
//	public boolean getScrollableTracksViewportWidth() {
//		return tree.getScrollableTracksViewportWidth();
//	}

	private class SchemaTree extends QTree {
		public SchemaTree( DefaultTreeModel model) {
			super( model);
			setRowHeight( 0);
		}

		public void setRowHeight( int height) {
	//		System.out.println( "setRowHeight( "+height+")");
			super.setRowHeight( 0);
		}
		
	}
} 
