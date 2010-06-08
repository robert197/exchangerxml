/*
 * $Id: ContentModelNode.java,v 1.1 2004/03/25 18:39:54 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.util.Vector;

import javax.swing.ImageIcon;

import org.bounce.image.ImageUtilities;

import com.cladonia.schema.AnySchemaElement;
import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.SchemaModel;
import com.cladonia.schema.SchemaParticle;

/**
 * The default node for a schema element.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:54 $
 * @author Dogsbay
 */
public class ContentModelNode extends StructureNode {
	private static final String PROHIBITED_ICON = "com/cladonia/schema/viewer/icons/GroupProhibitedIcon.gif";
	private static final String ONE_ICON = "com/cladonia/schema/viewer/icons/GroupOneIcon.gif";
	private static final String BOUNDED_ICON = "com/cladonia/schema/viewer/icons/GroupBoundedIcon.gif";
	private static final String UNBOUNDED_ICON = "com/cladonia/schema/viewer/icons/GroupUnboundedIcon.gif";
	private static final String BOUNDED_MAX_ICON = "com/cladonia/schema/viewer/icons/GroupBoundedMaxIcon.gif";
	private static final String BOUNDED_MIN_ICON = "com/cladonia/schema/viewer/icons/GroupBoundedMinIcon.gif";
	private static final String ONE_MAX_ICON = "com/cladonia/schema/viewer/icons/GroupOneMaxIcon.gif";
	private static final String ONE_MIN_ICON = "com/cladonia/schema/viewer/icons/GroupOneMinIcon.gif";

	private SchemaModel model = null;

	/**
	 * The constructor for the group node.
	 *
	 * @param parent this nodes parent node.
	 * @param model the schema content model declaration for the node.
	 */
	public ContentModelNode( StructureNode parent, SchemaModel model) {
		super( parent, model);
		
		this.model = model;
		
		addChildren();
	}
	
	/**
	 * The model represented by this node.
	 *
	 * @return the model.
	 */
	public SchemaModel getModel() {
		return model;
	}

	/**
	 * The name for this node.
	 *
	 * @return the name for the group.
	 */
	public String getName() {
		return "["+model.getType()+"]";
	}

	/**
	 * The description for this node.
	 *
	 * @return the description for the group.
	 */
	public String getDescription() {
		return model.toString();
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
		int max = model.getMaxOccurs();
		int min = model.getMinOccurs();
		
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
		return model.toString();
	}

		
	// Builds up the tree of children.
	private void addChildren() {
		Vector children = model.getChildren();
		
		for ( int i = 0; i < children.size(); i++) {
			SchemaParticle particle = (SchemaParticle)children.elementAt(i);
			
			if ( particle instanceof SchemaModel) {
				add( new ContentModelNode( this, (SchemaModel)particle));
			} else if ( particle instanceof AnySchemaElement) {
				add( new AnyElementNode( (AnySchemaElement)particle));
			} else { // ElementNode
				add( new ElementNode( this, (SchemaElement)particle));
			}
		}
	}
} 
