/*
 * $Id: AttributeNode.java,v 1.1 2004/03/25 18:39:54 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import javax.swing.ImageIcon;

import org.bounce.image.ImageUtilities;

import com.cladonia.schema.SchemaAttribute;

/**
 * The default node for schema attributes.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:54 $
 * @author Dogsbay
 */
public class AttributeNode extends SchemaNode {
	private static final String PROHIBITED_ICON = "com/cladonia/schema/viewer/icons/AttributeProhibitedIcon.gif";
	private static final String REQUIRED_ICON = "com/cladonia/schema/viewer/icons/AttributeRequiredIcon.gif";
	private static final String OPTIONAL_ICON = "com/cladonia/schema/viewer/icons/AttributeOptionalIcon.gif";
	private SchemaAttribute attribute = null;

	/**
	 * The constructor for a attribute node.
	 *
	 * @param attribute the schema attribute for the node.
	 */	
	public AttributeNode( SchemaAttribute attribute) {
		this.attribute = attribute;
	}
	
	/**
	 * Gets the schema attribute for this node.
	 *
	 * @return the attribute.
	 */	
	public SchemaAttribute getAttribute() {
		return attribute;
	}

	/**
	 * Gets the name for this node.
	 *
	 * @return the name.
	 */	
	public String getName() {
		return attribute.getName();
	}

	/**
	 * Gets the description for this node.
	 * This description is used for the tooltip text.
	 *
	 * @return the description.
	 */	
	public String getDescription() {
		return attribute.getName();
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
		String use = attribute.getUse();
		
		if ( use == SchemaAttribute.USE_REQUIRED) {
			return getIcon( REQUIRED_ICON);
		} else if ( use == SchemaAttribute.USE_PROHIBITED) {
			return getIcon( PROHIBITED_ICON);
		} else {
			return getIcon( OPTIONAL_ICON);
		}
	}
} 
