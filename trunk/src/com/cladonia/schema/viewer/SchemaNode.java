/*
 * $Id: SchemaNode.java,v 1.1 2004/03/25 18:39:54 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The base node class for the explorer tree.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:54 $
 * @author Dogsbay
 */
public abstract class SchemaNode extends DefaultMutableTreeNode {
	boolean enabled = true;
	Hashtable icons = null;
	
	public abstract String getName();
	public abstract ImageIcon getIcon();
	public abstract String getDescription();
	public abstract ImageIcon getSelectedIcon();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled( boolean enabled) {
		this.enabled = enabled;
	}
	
	/*
	 * Gets an icon for the string.
	 */
	protected ImageIcon getIcon( String path) {
		if ( icons == null) {
			icons = new Hashtable();
		}
		
		ImageIcon icon = (ImageIcon)icons.get( path);
		
		if ( icon == null) {
			icon = XngrImageLoader.get().getImage( path);
			icons.put( path, icon);
		}
		
		return icon;
	}
} 
