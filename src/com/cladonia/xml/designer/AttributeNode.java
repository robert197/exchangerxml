/*
 * $Id: AttributeNode.java,v 1.1 2004/03/25 18:42:44 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer;

import javax.swing.ImageIcon;

import org.bounce.image.ImageUtilities;

import com.cladonia.schema.SchemaAttribute;
import com.cladonia.xml.XAttribute;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The default node for Designer attributes.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:42:44 $
 * @author Dogsbay
 */
public class AttributeNode extends DesignerNode {
	private static final ImageIcon ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/AttributeIcon.gif");
	private static final ImageIcon REQUIRED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/RequiredAttributeIcon.gif");
	private static final ImageIcon FOREIGN_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/ForeignAttributeIcon.gif");
	private static final ImageIcon VIRTUAL_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/VirtualAttributeIcon.gif");
	private static final ImageIcon VIRTUAL_REQUIRED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/VirtualRequiredAttributeIcon.gif");
	
	private SchemaAttribute type	= null;
	private XAttribute attribute	= null;
	private ElementNode parent		= null;

	private boolean required = false;

	/**
	 * The constructor for a virtual attribute node.
	 *
	 * @param parent the parent node.
	 * @param type the schema attribute.
	 */	
	public AttributeNode( ElementNode parent, SchemaAttribute type) {
		this( parent, type, null);
	}

	/**
	 * The constructor for a attribute node.
	 *
	 * @param parent the parent node.
	 * @param type the schema attribute.
	 * @param attribute the XML attribute implementation.
	 */	
	public AttributeNode( ElementNode parent, SchemaAttribute type, XAttribute attribute) {
		this.type = type;
		this.parent = parent;
		this.attribute = attribute;
		
		if ( type != null) {
			// AttributeNodes with USE_PROHIBITED should not be created!
			required = (type.getUse() == SchemaAttribute.USE_REQUIRED);
		} 
	}

	/**
	 * Is this attribute required or optional.
	 *
	 * @return true when this attribute is required.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Could not find a type for the attribute, the attribute does not belong 
	 * in the Schema.
	 *
	 * @return true when no type for the attribute could be found.
	 */
	public boolean isForeign() {
		return (type == null);
	}

	/**
	 * The type represented by this node.
	 *
	 * @return the type.
	 */
	public SchemaAttribute getType() {
		return type;
	}

	/**
	 * The type represented by this node.
	 *
	 * @param type the attribute type.
	 */
	public void setType( SchemaAttribute type) {
		this.type = type;

		if ( type != null) {
			// AttributeNodes with USE_PROHIBITED should not be created!
			required = (type.getUse() == SchemaAttribute.USE_REQUIRED);
		} 
	}

	/**
	 * Sets this attribute as required or optional.
	 *
	 * @param required wether the attribute is required.
	 */
//	public void setRequired( boolean required) {
//		this.required = required;
//	}

	/**
	 * Sets the attribute.
	 *
	 * @param attribute the XML attribute implementation.
	 */	
	public void setAttribute( XAttribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * Returns the attribute.
	 *
	 * @return the XML attribute implementation.
	 */	
	public XAttribute getAttribute() {
		return attribute;
	}

	/**
	 * Wether this attribute has been initialised.
	 *
	 * @return true if this attribute has not been initialised.
	 */	
	public boolean isVirtual() {
		return attribute == null;
	}

	/**
	 * Gets the name for this node.
	 *
	 * @return the name.
	 */	
	public String getName() {
		if ( type != null) {
			return type.getName();
		} else if ( attribute != null) {
			return attribute.getName();
		} else {
			return null;
		}
	}

	/**
	 * Gets the value for the attribute.
	 *
	 * @return the value.
	 */	
	public String getValue() {
		String value = null;
		
		if ( attribute != null) {
			value = attribute.getValue();
		}
	
		return value;
	}

	/**
	 * Sets the value in the attribute.
	 *
	 * @param value the value.
	 */	
	public void setValue( String value) {
		if ( attribute != null) {
			attribute.setValue( value);
		}
	}

	public void add() {
		if ( !isForeign() && isVirtual()) {
			parent.addNode( this);
		}
	}

	public void remove() {
		if ( !isVirtual()) {
			parent.removeNode( this);
		}
	}

	/**
	 * Gets the description for this node.
	 * This description is used for the tooltip text.
	 *
	 * @return the description.
	 */	
	public String getDescription() {
		String value = "";
		
		if ( attribute != null) {
			value = attribute.getValue();
		}

		return getName()+"=\""+value+"\"";
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
		if ( isVirtual()) {
			if ( isRequired()) {
				return VIRTUAL_REQUIRED_ICON;
			} else {
				return VIRTUAL_ICON;
			}
		} else {
			if ( isForeign()) {
				return FOREIGN_ICON;
			} else if ( isRequired()) {
				return REQUIRED_ICON;
			} else {
				return ICON;
			}
		}
	}

	/**
	 * The parent for this node.
	 *
	 * @return the parent for the element.
	 */
	public ElementNode getParentElementNode() {
		return parent;
	}

	public Object takeSnapShot() {
		return new SnapShot( this);
	}
	
	public void setSnapShot( Object snapShot) {
		SnapShot snap = (SnapShot)snapShot;
		
		attribute = snap.attribute;
		parent.update();
	}
	
	public String toString() {
		return getName();
	}
	
	private class SnapShot {
		public XAttribute attribute = null;
	
		public SnapShot( AttributeNode node) {
			attribute = node.attribute;
		}
	}
	
} 
