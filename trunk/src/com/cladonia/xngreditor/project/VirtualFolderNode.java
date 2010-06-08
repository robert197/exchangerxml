/*
 * $Id: VirtualFolderNode.java,v 1.2 2005/09/28 08:29:17 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import org.bounce.image.ImageUtilities;

import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The default node for an folder in a project.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/09/28 08:29:17 $
 * @author Dogsbay
 */
public class VirtualFolderNode extends BaseNode implements Comparator {
	private static final ImageIcon ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/project/icons/VirtualFolderIcon.gif");
	private static final ImageIcon EXPANDED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/project/icons/SelectedVirtualFolderIcon.gif");
	private VirtualFolderProperties properties = null;
	private DefaultTreeModel model = null;
	private boolean upToDate = false;
	
	
	/**
	 * The constructor for the folder node.
	 *
	 * @param properties the folder properties.
	 */
	public VirtualFolderNode( DefaultTreeModel model, VirtualFolderProperties properties, boolean isStartup) {
		super();
		this.properties = properties;
		this.model = model;
		
		if(isStartup == false) {
			update(true);
		}
		
		if(this.children != null) {
			Collections.sort(this.children, this);
		}
	}
	
	//-1 if this is less than other node
	public int compare(Object firstNode, Object secondNode) {
		
		if(firstNode instanceof VirtualFolderNode) {
			if(secondNode instanceof VirtualFolderNode) {
				//same type compare names
				return(((VirtualFolderNode)firstNode).getName().compareTo(((VirtualFolderNode)secondNode).getName()));
			}
			else {
				//other is a different type
				//folder should come first
				return(-1);
			}        
		}
		else if(secondNode instanceof VirtualFolderNode) {
			return(1);
		}
		else if((firstNode instanceof BaseNode) && (secondNode instanceof BaseNode)) {
			return((BaseNode)firstNode).compareTo(secondNode);
		}
		else {
			return(0);
		}
    }

	
	public VirtualFolderProperties getProperties() {
		return properties;
	}
	
	public void update(boolean isStartup) {
		if ( getChildCount() > 0) {
			try {
				removeAllChildren();
			}catch(ArrayIndexOutOfBoundsException e) {
				//caused by the getChildCount() returning a real live value
			}
		}
		
		this.model.nodeChanged(this);
		
		this.model.reload(this);
		
		/*Vector documents = properties.getDocumentProperties();
		for ( int i = 0; i < documents.size(); i++) {
			add( new DocumentNode( model, (DocumentProperties)documents.elementAt(i)));
		}

		Vector folders = properties.getFolderProperties();
		for ( int i = 0; i < folders.size(); i++) {
			add( new FolderNode( model, (FolderProperties)folders.elementAt(i)));
		}
		
		Vector virtualFolders = properties.getVirtualFolderProperties();
		for ( int i = 0; i < virtualFolders.size(); i++) {
			add( new VirtualFolderNode( model, (VirtualFolderProperties)virtualFolders.elementAt(i)));
		}*/
		
		try {
			File[] files = URLUtilities.toFile(this.getProperties().getURL()).listFiles();
			if(files != null) {
				for ( int i = 0; i < files.length; i++) {
					File file = files[i];
					
					if(file.isHidden() == false) {
						if ( file.isDirectory()) {
							if(file.getName().charAt(0) == '#') {
								System.err.println("error reading: "+file);
							}
							else {
								add( new VirtualFolderNode(model, new VirtualFolderProperties(file), isStartup));
							}
							//addVirtualFolderProperties( new VirtualFolderProperties( file));
						} else { // file
							add( new DocumentNode(model, new DocumentProperties(file)));
							//addDocumentProperties( new DocumentProperties( file));
						}
					}
				}
			}
			else {
				
			}
		}catch (Exception e) {
			//can be caused by a bad properties file etc
		}
		
		if(this.children != null) {
			Collections.sort(this.children, this);
		}
		
		this.setUpToDate(true);
		
		this.model.nodeChanged(this);
		
		this.model.reload(this);
	}
	
	
	public void parse() {
		Enumeration children = children();
		
		while ( children.hasMoreElements()) {
			BaseNode node = (BaseNode)children.nextElement();
			
			if ( node instanceof VirtualFolderNode) {
				((VirtualFolderNode)node).parse();
			} else if ( node instanceof DocumentNode) {
				((DocumentNode)node).parse();
			} else if ( node instanceof FolderNode) {
				((FolderNode)node).parse();
			}
		}
	}

	public void validate() {
		Enumeration children = children();
		
		while ( children.hasMoreElements()) {
			BaseNode node = (BaseNode)children.nextElement();
			
			if ( node instanceof VirtualFolderNode) {
				((VirtualFolderNode)node).validate();
			} else if ( node instanceof DocumentNode) {
				((DocumentNode)node).validate();
			} else if ( node instanceof FolderNode) {
				((FolderNode)node).validate();
			}
		}
	}

	public Vector getDocuments() {
		Vector documents = new Vector();
		
		if(upToDate == false) {
			update(true);
		}
		Enumeration children = children();
		
		
		
		while ( children.hasMoreElements()) {
			BaseNode node = (BaseNode)children.nextElement();
			
			if ( node instanceof VirtualFolderNode) {
				Vector docs = ((VirtualFolderNode)node).getDocuments();

				for ( int i = 0; i < docs.size(); i++) {
					documents.addElement( docs.elementAt(i));
				}
			} else if ( node instanceof DocumentNode) {
				documents.addElement( node);
			} else if ( node instanceof FolderNode) {
				Vector docs = ((FolderNode)node).getDocuments();

				for ( int i = 0; i < docs.size(); i++) {
					documents.addElement( docs.elementAt(i));
				}
			}
		}
		
		return documents;
	}

	public VirtualFolderNode addVirtualFolder( VirtualFolderProperties props, boolean isStartup) {
		properties.addVirtualFolderProperties( props);
		
		VirtualFolderNode node = new VirtualFolderNode( model, props, isStartup);
		
		add( node);
		
		return node;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.DefaultMutableTreeNode#getAllowsChildren()
	 */
	public boolean getAllowsChildren() {
	
		File file = URLUtilities.toFile(this.getProperties().getURL());
		if(file != null) {
			return(file.isDirectory());
		}
		else {
			return(false);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.DefaultMutableTreeNode#isLeaf()
	 */
	public boolean isLeaf() {
	
		return(!(this.allowsChildren));
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
		return (properties.getURL());
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
	public void add( VirtualFolderNode node) {
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

	
	public DefaultTreeModel getModel() {
	
		return model;
	}

	
	public void setModel(DefaultTreeModel model) {
	
		this.model = model;
	}

	
	public boolean isUpToDate() {
	
		return upToDate;
	}

	
	public void setUpToDate(boolean upToDate) {
	
		this.upToDate = upToDate;
	}

	
	
} 
