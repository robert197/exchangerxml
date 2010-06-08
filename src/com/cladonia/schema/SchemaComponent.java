/*
 * $Id: SchemaComponent.java,v 1.1 2004/03/25 18:37:18 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import org.exolab.castor.xml.schema.Structure;

/**
 * A component for the schema.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:37:18 $
 * @author Dogsbay
 */
public abstract class SchemaComponent implements SchemaObject {
	private Structure structure = null;
	private SchemaParticle parent = null;

	/**
	 * The constructor for the schema component.
	 *
	 * @param parent this components parent.
	 * @param structure the structure for this component.
	 */
	public SchemaComponent( SchemaParticle parent, Structure structure) {
		this.structure = structure;
		this.parent = parent;
	}

	/**
	 * Gets the elements parent element.
	 *
	 * @return the parent element.
	 */
	public SchemaElement getParentElement() {
		SchemaParticle parent = getParent();
		
		while ( parent != null && !(parent instanceof SchemaElement)) {
			parent = parent.getParent();
		}
	
		return (SchemaElement)parent;
	}

	/**
	 * Returns the parent node for this object.
	 *
	 * @return the parent object.
	 */
	public SchemaParticle getParent() {
		return parent;
	}

	/**
	 * The structure in this object.
	 *
	 * @return the structure object.
	 */
	Structure getStructure() {
		return structure;
	}
	
	/**
	 * Sets the structure in this object.
	 *
	 * @param structure the structure in the object.
	 */
	void setStructure( Structure structure) {
		this.structure = structure;
	}

	/**
	 * Gets the ancestor object for the schema structure.
	 *
	 * @param structure the Schema structure.
	 *
	 * @return the object, or null if not found.
	 */	
	public SchemaObject getAncestor( Structure structure) {
//		System.out.println( "SchemaComponent.getAncestor() "+this);
		
		if ( structure == this.structure) {
			return this;
		} else if ( parent != null) {
			return parent.getAncestor( structure);
		} else {
			return null;
		}
	}
	
	String toString( String indent) {
		return indent+toString()+"\n";
	}
} 
