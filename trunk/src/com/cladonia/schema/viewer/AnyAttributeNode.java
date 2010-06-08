/*
 * $Id: AnyAttributeNode.java,v 1.1 2004/03/25 18:39:54 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import javax.swing.ImageIcon;

import org.bounce.image.ImageUtilities;

import com.cladonia.schema.AnySchemaAttribute;

/**
 * The default node for schema (any) wildcard attributes.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:54 $
 * @author Dogsbay
 */
public class AnyAttributeNode extends SchemaNode {
	private static final String ICON = "com/cladonia/schema/viewer/icons/AnyAttributeIcon.gif";
	private AnySchemaAttribute wildcard = null;

	/**
	 * The constructor for an any-attribute node.
	 *
	 * @param attribute the schema any-attribute for the node.
	 */	
	public AnyAttributeNode( AnySchemaAttribute wildcard) {
		this.wildcard = wildcard;
	}
	
	/**
	 * Gets the schema any-attribute for this node.
	 *
	 * @return the any-attribute.
	 */	
	public AnySchemaAttribute getWildcard() {
		return wildcard;
	}

	/**
	 * Gets the name for this node.
	 *
	 * @return the name.
	 */	
	public String getName() {
		return "[any]";
	}

	/**
	 * Gets the description for this node.
	 * This description is used for the tooltip text.
	 *
	 * @return the description.
	 */	
	public String getDescription() {
		return getName();
	}

	/**
	 * Gets the selected attribute icon for this node.
	 *
	 * @return the selected attribute icon.
	 */	
	public ImageIcon getSelectedIcon() {
		ImageIcon icon = getIcon();

		if ( icon != null) {
			icon = ImageUtilities.createDarkerImage( icon);
		}
		
		return icon;
	}

	/**
	 * Gets the default attribute icon for this node.
	 *
	 * @return the attribute icon.
	 */	
	public ImageIcon getIcon() {
		return getIcon( ICON);
	}
} 
