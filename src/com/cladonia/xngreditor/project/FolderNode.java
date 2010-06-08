/*
 * $Id: FolderNode.java,v 1.2 2005/09/05 09:08:29 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import org.bounce.image.ImageUtilities;

import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.XngrProgressDialog;

/**
 * The default node for an folder in a project.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/09/05 09:08:29 $
 * @author Dogsbay
 */
public class FolderNode extends BaseNode {
	private static final ImageIcon ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/project/icons/FolderIcon.gif");
	private static final ImageIcon EXPANDED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/project/icons/SelectedFolderIcon.gif");
	private FolderProperties properties = null;
	private DefaultTreeModel model = null;
	
	/**
	 * The constructor for the folder node.
	 *
	 * @param properties the folder properties.
	 */
	public FolderNode( DefaultTreeModel model, FolderProperties properties, boolean isStartup) {
		this.properties = properties;
		this.model = model;
		
		update(isStartup);
		
	}
	
	public FolderProperties getProperties() {
		return properties;
	}
	
	public void update(boolean isStartup) {
		if ( getChildCount() > 0) {
			removeAllChildren();
		}
		
		Vector documents = properties.getDocumentProperties();
		for ( int i = 0; i < documents.size(); i++) {
			add( new DocumentNode( model, (DocumentProperties)documents.elementAt(i)));
		}

		Vector folders = properties.getFolderProperties();
		for ( int i = 0; i < folders.size(); i++) {
			add( new FolderNode( model, (FolderProperties)folders.elementAt(i), isStartup));
		}
		
		Vector virtualFolders = properties.getVirtualFolderProperties();
		for ( int i = 0; i < virtualFolders.size(); i++) {
			add( new VirtualFolderNode( model, (VirtualFolderProperties)virtualFolders.elementAt(i), isStartup));
		}
	}

	public void parse() {
		Enumeration children = children();
		
		while ( children.hasMoreElements()) {
			BaseNode node = (BaseNode)children.nextElement();
			
			if ( node instanceof FolderNode) {
				((FolderNode)node).parse();
			} else if ( node instanceof DocumentNode) {
				((DocumentNode)node).parse();
			} else if ( node instanceof VirtualFolderNode) {
				((VirtualFolderNode)node).parse();
			}
			
		}
	}

	public void validate() {
		Enumeration children = children();
		
		while ( children.hasMoreElements()) {
			BaseNode node = (BaseNode)children.nextElement();
			
			if ( node instanceof FolderNode) {
				((FolderNode)node).validate();
			} else if ( node instanceof DocumentNode) {
				((DocumentNode)node).validate();
			} else if ( node instanceof VirtualFolderNode) {
				((VirtualFolderNode)node).validate();
			}
		}
	}

	public Vector getDocuments(XngrProgressDialog progressDialog) {
		Vector documents = new Vector();
		Enumeration children = children();
		
		while ( children.hasMoreElements()) {
			BaseNode node = (BaseNode)children.nextElement();
			
			if ( node instanceof FolderNode) {
				Vector docs = ((FolderNode)node).getDocuments();

				if(progressDialog.isCancelled() == true) {
					return(null);
				}
				
				for ( int i = 0; i < docs.size(); i++) {
					documents.addElement( docs.elementAt(i));
				}
			} else if ( node instanceof DocumentNode) {
				documents.addElement( node);
				
			} else if ( node instanceof VirtualFolderNode) {
				Vector docs = ((VirtualFolderNode)node).getDocuments();

				if(progressDialog.isCancelled() == true) {
					return(null);
				}
				
				for ( int i = 0; i < docs.size(); i++) {
					documents.addElement( docs.elementAt(i));
				}
			}
		}
		
		return documents;
	}
	
	public Vector getDocuments() {
		Vector documents = new Vector();
		Enumeration children = children();
		
		while ( children.hasMoreElements()) {
			BaseNode node = (BaseNode)children.nextElement();
			
			if ( node instanceof FolderNode) {
				Vector docs = ((FolderNode)node).getDocuments();

				for ( int i = 0; i < docs.size(); i++) {
					documents.addElement( docs.elementAt(i));
				}
			} else if ( node instanceof DocumentNode) {
				documents.addElement( node);
				
			} else if ( node instanceof VirtualFolderNode) {
				Vector docs = ((VirtualFolderNode)node).getDocuments();

				for ( int i = 0; i < docs.size(); i++) {
					documents.addElement( docs.elementAt(i));
				}
			}
		}
		
		return documents;
	}

	public FolderNode addFolder( FolderProperties props, boolean isStartup) {
		properties.addFolderProperties( props);
		
		FolderNode node = new FolderNode( model, props, isStartup);
		
		add( node);
		
		return node;
	}

	/**
	 * The name for this node.
	 *
	 * @return the name for the element.
	 */
	public String getName() {
		return properties.getName();
	}

	/**
	 * Sets the name for this node.
	 *
	 * @param name the new name.
	 */	
	public void setName( String name) {
		properties.setName( name);
	}

	/**
	 * Sets the user object for the folder.
	 *
	 * @param object the new name.
	 */	
	public void setUserObject( Object object) {
		setName( (String)object);
	}

	/**
	 * The description for this node.
	 *
	 * @return the description for the element.
	 */
	public String getDescription() {
		return properties.getName();
	}

	/**
	 * Returns the icon that is shown when the node is selected.
	 *
	 * @return the selected icon.
	 */
	public Icon getSelectedIcon() {
		return ImageUtilities.createDarkerImage( ICON);
	}

	/**
	 * Returns the icon that is shown when the node is expanded and selected.
	 *
	 * @return the selected expanded icon.
	 */
	public Icon getExpandedSelectedIcon() {
		return ImageUtilities.createDarkerImage( EXPANDED_ICON);
	}


	/**
	 * Returns the icon that is shown when the node is expanded.
	 *
	 * @return the expanded icon.
	 */
	public Icon getExpandedIcon() {
		return EXPANDED_ICON;
	}

	/**
	 * The icon for this node.
	 *
	 * @return the icon for the element.
	 */
	public Icon getIcon() {
		return ICON;
	}
	
	/**
	 * Returns a string version of this node.
	 *
	 * @return the name for the element.
	 */
	public String toString() {
		return getName();
	}
	
	/** 
	 * Adds the node to the parent at a sorted location.
	 *
	 * @param the node to be added.
	 */
	public void add( FolderNode node) {
		super.add( node);
	}

	/** 
	 * Adds the node to the parent at a sorted location.
	 *
	 * @param the node to be added.
	 */
	public int add( DocumentNode node) {
		int index = 0;
		
		for ( index = 0; index < getChildCount(); index++) {
			BaseNode n = (BaseNode)getChildAt( index);
			
			if ( n instanceof DocumentNode) {
				if ( node.compareTo( n) <= 0) {
					insert( node, index);
					return index;
				}
			} else {
				insert( node, index);
				return index;
			}
		}
		
		super.add( node);
		
		return index;
	}
	
} 
