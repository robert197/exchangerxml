/*
 * $Id: SchemaViewer.java,v 1.8 2005/08/31 16:19:03 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;


import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.XMLSchema;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.ViewTreePanel;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The viewer dialog for the Schema.
 *
 * @version	$Revision: 1.8 $, $Date: 2005/08/31 16:19:03 $
 * @author Dogsbay
 */
public class SchemaViewer extends ViewTreePanel {
	private static ImageIcon icon = null;

	private static ExchangerEditor _parent = null;
	private ExchangerView view = null;
	private static SchemaDetailsDialog schemaDialog = null;

	private RootSelectionDialog rootDialog = null;

	private SchemaElement root = null;
	private XMLSchema schema = null;
	
	private SchemaViewerProperties properties = null;
	private SchemaViewerDetails schemaViewerDetails = null;
	
	private TreePanel tree = null;
	private boolean initialised = false;
	
	public SchemaViewer( ExchangerEditor parent, SchemaViewerProperties properties, ExchangerView _view) {
		super( new BorderLayout());
		
		_parent		= parent;
		this.view = _view;
		this.properties = properties;
		
//		details = new SchemaViewerDetails( parent);
		
		tree = new TreePanel();
		
		tree.addTreePanelListener( new TreePanelListener() {
			public void popupTriggered( MouseEvent event, SchemaNode node) {}
			public void doubleClicked( MouseEvent event, SchemaNode node) {}
			public void selectionChanged( SchemaNode node) {

				if ( node != null) {
					if ( node instanceof ElementNode) {
						SchemaElement element = ((ElementNode)node).getElement();
						
						if ( ((ElementNode)node).isReference()) {
							((ElementNode)node).resolveReference();
							tree.nodeChanged( node);
						} else if ( element.isRecursive()) {
							element.recurse();

							((ElementNode)node).parse();
							tree.nodeChanged( node);
						}
					} 
				}
				
				//SchemaViewer._parent.getHelper().setElement( node);
				setElementInternal(node);
			}
		}); 

		add( tree, BorderLayout.CENTER);
		
		schemaViewerDetails = new SchemaViewerDetails(parent);
		add( schemaViewerDetails, BorderLayout.EAST);
		
		tree.getTree().addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e) {
				view.setFocussed();
			}

			public void focusLost( FocusEvent e) {}
		});

		updatePreferences();
	}
	
	
	private void setElementInternal( SchemaNode node) {
		schemaViewerDetails.setNode( node);
	}
	
	public ImageIcon getIcon() {
		if ( icon == null) {
			icon = XngrImageLoader.get().getImage( "com/cladonia/schema/viewer/icons/SchemaViewerIcon.gif");
		}
		
		return icon;
	}
	
	public void updateHelper() {
		SchemaNode node = tree.getSelectedNode();
		
		//_parent.getHelper().setElement( node);
		setElementInternal(node);

//		if ( node != null) {
//			if ( node instanceof ElementNode) {
//				parent.getHelper().setElement( ((ElementNode)node).getElement());
//			} else if ( node instanceof AnyElementNode) {
//				parent.getHelper().setElement( ((AnyElementNode)node).getWildcard().getParentElement());
//			} else if ( node instanceof AttributeNode) {
//				parent.getHelper().setElement( ((AttributeNode)node).getAttribute().getParentElement());
//			} else if ( node instanceof AnyAttributeNode) {
//				parent.getHelper().setElement( ((AnyAttributeNode)node).getWildcard().getParentElement());
//			} else if ( node instanceof ContentModelNode) {
//				parent.getHelper().setElement( ((ContentModelNode)node).getModel().getParentElement());
//			}
//		}
	}

	/**
	 * Update the preferences.
	 */
	public void updatePreferences() {
		Font font = TextPreferences.getBaseFont();
		((SchemaCellRenderer)tree.getTree().getCellRenderer()).setFont( font);

//		details.updatePreferences();

	 	tree.getTree().updateUI();
	 	schemaViewerDetails.updatePreferences();
	}

//	private void setTree() {
//	}

	public boolean isInitialised() {
		return initialised;
	}

	public void initialise() {
		initialised = true;
		
		setRoot( root);
		this.schemaViewerDetails.setSchema( schema);
	}

	public void setFocus() {
		if ( !tree.hasFocus()) {
			tree.requestFocusInWindow();
		}
	}

	public void setSchema( XMLSchema schema, SchemaElement root) {
		this.schema = schema;
		this.root = root;
		
		initialised = false;
	}

	public void setRoot( SchemaElement root) {
		this.root = root;

		if ( root != null) {
			SchemaNode node = new ElementNode( null, root);
	
			tree.setRoot( node);
			tree.expand( 3); //todo set variable
			
			tree.setSelectedNode( node);
		} else {
			tree.setRoot( null);
		}
	}
	
	public SchemaElement getRoot() {
		return root;
	}

	/**
	 * Collapses all the nodes in the tree.
	 */
	public void collapseAll() {
		tree.collapseAll();
		tree.expand(1);
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

	public void setProperties() {
//		properties.setDividerLocation( split.getDividerLocation());
	}

	public RootSelectionDialog getRootSelectionDialog() {
		if ( rootDialog == null) {
			rootDialog = new RootSelectionDialog( _parent);
			rootDialog.setSchema( schema);
			rootDialog.setLocationRelativeTo( _parent);
		}

		return rootDialog;
	}

	public void cleanup() {
		removeAll();
		
		finalize();
	}
	
	protected void finalize() {
		rootDialog = null;

		root = null;
		schema = null;
	
		properties = null;
	
		tree = null;
	}	
}