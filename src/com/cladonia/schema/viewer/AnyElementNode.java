/*
 * $Id: AnyElementNode.java,v 1.1 2004/03/25 18:39:54 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import javax.swing.ImageIcon;

import org.bounce.image.ImageUtilities;

import com.cladonia.schema.AnySchemaElement;

/**
 * The default node for a schema (wildcard) element.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:54 $
 * @author Dogsbay
 */
public class AnyElementNode extends SchemaNode {
	private static final String PROHIBITED_ICON = "com/cladonia/schema/viewer/icons/AnyElementProhibitedIcon.gif";
	private static final String ONE_ICON = "com/cladonia/schema/viewer/icons/AnyElementOneIcon.gif";
	private static final String BOUNDED_ICON = "com/cladonia/schema/viewer/icons/AnyElementBoundedIcon.gif";
	private static final String UNBOUNDED_ICON = "com/cladonia/schema/viewer/icons/AnyElementUnboundedIcon.gif";
	private static final String BOUNDED_MAX_ICON = "com/cladonia/schema/viewer/icons/AnyElementBoundedMaxIcon.gif";
	private static final String BOUNDED_MIN_ICON = "com/cladonia/schema/viewer/icons/AnyElementBoundedMinIcon.gif";
	private static final String ONE_MAX_ICON = "com/cladonia/schema/viewer/icons/AnyElementOneMaxIcon.gif";
	private static final String ONE_MIN_ICON = "com/cladonia/schema/viewer/icons/AnyElementOneMinIcon.gif";

	private AnySchemaElement wildcard = null;

	/**
	 * The constructor for the (wildcard) element node.
	 *
	 * @param wildcard the schema (wildcard) element declaration for the node.
	 */
	public AnyElementNode( AnySchemaElement wildcard) {
		this.wildcard = wildcard;
	}
	
	/**
	 * The (wildcard) element represented by this node.
	 *
	 * @return the (wildcard) element.
	 */
	public AnySchemaElement getWildcard() {
		return wildcard;
	}

	/**
	 * The name for this node.
	 *
	 * @return the name for the element.
	 */
	public String getName() {
		return "[any]";
	}

	/**
	 * The description for this node.
	 *
	 * @return the description for the element.
	 */
	public String getDescription() {
		return getName();
	}

	/**
	 * Returns the icon that is shown when the node is selected.
	 *
	 * @return the selected icon.
	 */
	public ImageIcon getSelectedIcon() {
		ImageIcon icon = getIcon();

		if ( icon != null) {
			icon = ImageUtilities.createDarkerImage( icon);
		}
		
		return icon;
	}

	/**
	 * The icon for this node.
	 *
	 * @return the icon for the element.
	 */
	public ImageIcon getIcon() {
		int max = wildcard.getMaxOccurs();
		int min = wildcard.getMinOccurs();
		
		if ( min == 0) {
			if ( max == 1) {
				return getIcon( ONE_MAX_ICON);
			} else if ( max > 0) {
				return getIcon( BOUNDED_MAX_ICON);
			} else if ( max == -1) {
				return getIcon( UNBOUNDED_ICON);
			} else { // max == 0;
				return getIcon( PROHIBITED_ICON);
			}
		} else {
			if ( max == -1 && min == 1) {
				return getIcon( ONE_MIN_ICON);
			} else if ( max == 1 && min == 1) {
				return getIcon( ONE_ICON);
			} else if ( max > 0) {
				return getIcon( BOUNDED_ICON);
			} else if ( max == -1) {
				return getIcon( BOUNDED_MIN_ICON);
			} else { // max == 0; cannot happen
				System.err.println( "Error: Illegal maximum value");
				return null;
			}
		}
	}
	
	/**
	 * Returns a string version of this node.
	 *
	 * @return the name for the element.
	 */
	public String toString() {
		return getName();
	}
} 
