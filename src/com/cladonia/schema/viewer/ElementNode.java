/*
 * $Id: ElementNode.java,v 1.1 2004/03/25 18:39:54 edankert Exp $
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

import com.cladonia.schema.AnySchemaAttribute;
import com.cladonia.schema.SchemaAttribute;
import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.SchemaModel;

/**
 * The default node for a schema element.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:54 $
 * @author Dogsbay
 */
public class ElementNode extends StructureNode {
	private static final boolean DEBUG = true;

	private static final String ABSTRACT_PROHIBITED_ICON = "com/cladonia/schema/viewer/icons/AbstractElementProhibitedIcon.gif";
	private static final String ABSTRACT_ONE_ICON = "com/cladonia/schema/viewer/icons/AbstractElementOneIcon.gif";
	private static final String ABSTRACT_BOUNDED_ICON = "com/cladonia/schema/viewer/icons/AbstractElementBoundedIcon.gif";
	private static final String ABSTRACT_UNBOUNDED_ICON = "com/cladonia/schema/viewer/icons/AbstractElementUnboundedIcon.gif";
	private static final String ABSTRACT_BOUNDED_MAX_ICON = "com/cladonia/schema/viewer/icons/AbstractElementBoundedMaxIcon.gif";
	private static final String ABSTRACT_BOUNDED_MIN_ICON = "com/cladonia/schema/viewer/icons/AbstractElementBoundedMinIcon.gif";
	private static final String ABSTRACT_ONE_MAX_ICON = "com/cladonia/schema/viewer/icons/AbstractElementOneMaxIcon.gif";
	private static final String ABSTRACT_ONE_MIN_ICON = "com/cladonia/schema/viewer/icons/AbstractElementOneMinIcon.gif";

	private static final String PROHIBITED_ICON = "com/cladonia/schema/viewer/icons/ElementProhibitedIcon.gif";
	private static final String ONE_ICON = "com/cladonia/schema/viewer/icons/ElementOneIcon.gif";
	private static final String BOUNDED_ICON = "com/cladonia/schema/viewer/icons/ElementBoundedIcon.gif";
	private static final String UNBOUNDED_ICON = "com/cladonia/schema/viewer/icons/ElementUnboundedIcon.gif";
	private static final String BOUNDED_MAX_ICON = "com/cladonia/schema/viewer/icons/ElementBoundedMaxIcon.gif";
	private static final String BOUNDED_MIN_ICON = "com/cladonia/schema/viewer/icons/ElementBoundedMinIcon.gif";
	private static final String ONE_MAX_ICON = "com/cladonia/schema/viewer/icons/ElementOneMaxIcon.gif";
	private static final String ONE_MIN_ICON = "com/cladonia/schema/viewer/icons/ElementOneMinIcon.gif";

	private static final String REF_PROHIBITED_ICON = "com/cladonia/schema/viewer/icons/ElementRefProhibitedIcon.gif";
	private static final String REF_ONE_ICON = "com/cladonia/schema/viewer/icons/ElementRefOneIcon.gif";
	private static final String REF_BOUNDED_ICON = "com/cladonia/schema/viewer/icons/ElementRefBoundedIcon.gif";
	private static final String REF_UNBOUNDED_ICON = "com/cladonia/schema/viewer/icons/ElementRefUnboundedIcon.gif";
	private static final String REF_BOUNDED_MAX_ICON = "com/cladonia/schema/viewer/icons/ElementRefBoundedMaxIcon.gif";
	private static final String REF_BOUNDED_MIN_ICON = "com/cladonia/schema/viewer/icons/ElementRefBoundedMinIcon.gif";
	private static final String REF_ONE_MAX_ICON = "com/cladonia/schema/viewer/icons/ElementRefOneMaxIcon.gif";
	private static final String REF_ONE_MIN_ICON = "com/cladonia/schema/viewer/icons/ElementRefOneMinIcon.gif";

	private SchemaElement element = null;

	private boolean reference = true;

	/**
	 * The constructor for the element node.
	 *
	 * @param parent this nodes parent node.
	 * @param element the schema element declaration for the node.
	 */
	public ElementNode( StructureNode parent, SchemaElement element) {
		super( parent, element);

		this.element = element;
		
		reference = element.isReference();
		
		parse();
	}
	
	/**
	 * The element represented by this node.
	 *
	 * @return the element.
	 */
	public void resolveReference() {
		if ( element.isReference()) {
			element.resolveReference();
		}

		parse();

		reference = false;
	}

	/**
	 * The element represented by this node.
	 *
	 * @return the element.
	 */
	public boolean isReference() {
		return reference;
	}

	/**
	 * The element represented by this node.
	 *
	 * @return the element.
	 */
	public SchemaElement getElement() {
		return element;
	}

	/**
	 * The name for this node.
	 *
	 * @return the name for the element.
	 */
	public String getName() {
		return element.getName();
	}

	/**
	 * The description for this node.
	 *
	 * @return the description for the element.
	 */
	public String getDescription() {
		return element.getName();
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
		int max = element.getMaxOccurs();
		int min = element.getMinOccurs();
		
		if ( min == 0) {
			if ( max == 1) {
				if ( reference || element.isRecursive()) {
					return getIcon( REF_ONE_MAX_ICON);
				} else if ( element.isAbstract()) {
					return getIcon( ABSTRACT_ONE_MAX_ICON);
				} else {
					return getIcon( ONE_MAX_ICON);
				}
			} else if ( max > 1) {
				if ( reference || element.isRecursive()) {
					return getIcon( REF_BOUNDED_MAX_ICON);
				} else if ( element.isAbstract()) {
					return getIcon( ABSTRACT_BOUNDED_MAX_ICON);
				} else {
					return getIcon( BOUNDED_MAX_ICON);
				}
			} else if ( max == -1) {
				if ( reference || element.isRecursive()) {
					return getIcon( REF_UNBOUNDED_ICON);
				} else if ( element.isAbstract()) {
					return getIcon( ABSTRACT_UNBOUNDED_ICON);
				} else {
					return getIcon( UNBOUNDED_ICON);
				}
			} else { // max == 0;
				if ( reference || element.isRecursive()) {
					return getIcon( REF_PROHIBITED_ICON);
				} else if ( element.isAbstract()) {
					return getIcon( ABSTRACT_PROHIBITED_ICON);
				} else {
					return getIcon( PROHIBITED_ICON);
				}
			}
		} else {
			if ( max == 1 && min == 1) {
				if ( reference || element.isRecursive()) {
					return getIcon( REF_ONE_ICON);
				} else if ( element.isAbstract()) {
					return getIcon( ABSTRACT_ONE_ICON);
				} else {
					return getIcon( ONE_ICON);
				}
			} else if ( min == 1 && max == -1) {
				if ( reference || element.isRecursive()) {
					return getIcon( REF_ONE_MIN_ICON);
				} else if ( element.isAbstract()) {
					return getIcon( ABSTRACT_ONE_MIN_ICON);
				} else {
					return getIcon( ONE_MIN_ICON);
				}
			} else if ( max > 0) {
				if ( reference || element.isRecursive()) {
					return getIcon( REF_BOUNDED_ICON);
				} else if ( element.isAbstract()) {
					return getIcon( ABSTRACT_BOUNDED_ICON);
				} else {
					return getIcon( BOUNDED_ICON);
				}
			} else if ( max == -1) {
				if ( reference || element.isRecursive()) {
					return getIcon( REF_BOUNDED_MIN_ICON);
				} else if ( element.isAbstract()) {
					return getIcon( ABSTRACT_BOUNDED_MIN_ICON);
				} else {
					return getIcon( BOUNDED_MIN_ICON);
				}
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
	
	public void parse() {
		setAnyAttributeNode();
		addAttributeNodes();
		addModelNodes();
	}

	/*
	 * Sets the attribute wildcard node.
	 */
	private void setAnyAttributeNode() {
		AnySchemaAttribute wildcard = element.getAnyAttribute();

		if ( wildcard != null) {
			add( new AnyAttributeNode( wildcard));
		}
	}

	/*
	 * Add the attribute nodes.
	 */
	private void addAttributeNodes() {
		Vector attributes = element.getAttributes();
		
		if ( attributes != null) {
			for ( int i = 0; i < attributes.size(); i++) {
				add( new AttributeNode( (SchemaAttribute)attributes.elementAt(i)));
			}
		}
	}

	/*
	 * Adds the content model nodes.
	 */
	private void addModelNodes() {
		Vector models = element.getModels();
		
		if ( models != null) {
			for ( int i = 0; i < models.size(); i++) {
				add( new ContentModelNode( this, (SchemaModel)models.elementAt(i)));
			}
		}
	}
} 
