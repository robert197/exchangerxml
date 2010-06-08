/*
 * $Id: SchemaParticle.java,v 1.1 2004/03/25 18:37:18 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import java.util.Vector;

import org.exolab.castor.xml.schema.Particle;
import org.exolab.castor.xml.schema.Structure;

/**
 * A node for the schema.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:37:18 $
 * @author Dogsbay
 */
public abstract class SchemaParticle extends SchemaComponent {
	private Vector children = null;
	private Particle particle = null;
	private SchemaParticle parent = null;

	/**
	 * The constructor for the structure.
	 *
	 * @param parent this components parent.
	 * @param particle the particle for this component.
	 */
	public SchemaParticle( SchemaParticle parent, Particle particle) {
		super( parent, particle);

		this.particle = particle;
		this.parent = parent;
		
		children = new Vector();
	}

	/**
	 * Adds a child to the component.
	 *
	 * @param child the child to add.
	 */
	public void addChild( SchemaParticle child) {
		children.addElement( child);
	}

	/**
	 * Gets the children for this schema component.
	 *
	 * @return the list of children.
	 */
	public Vector getChildren() {
		return children;
	}

	/**
	 * Returns the schema object for the structure.
	 *
	 * @param parent this components parent.
	 * @param particle the particle for this component.
	 *
	 * @return the id for this component.
	 */
	public SchemaComponent getObject( Structure structure) {
		if ( this.particle == structure) {
			return this;
		}

		Vector children = getChildren();

		for ( int i = 0; i < children.size(); i++) {
			SchemaComponent object = (SchemaComponent)children.elementAt( i);
			
			if ( object instanceof SchemaParticle) {
				SchemaComponent result = ((SchemaParticle)object).getObject( structure);
			
				if ( result != null) {
					return result;
				}
			} else {
				if ( object.getStructure() == structure) {
					return object;
				}
			}
		}
		
		return null;
		
	}

	/**
	 * Returns the maximum number of occurences for 
	 * this component.
	 * (-1 if the occurences are unbounded)
	 *
	 * @return the maximum number of occurences.
	 */
	public int getMaxOccurs() {
		return particle.getMaxOccurs();
	}

	/**
	 * Returns the maximum number of occurences for 
	 * this component.
	 *
	 * @return the maximum number of occurences.
	 */
	public int getMinOccurs() {
		return particle.getMinOccurs();
	}
	
	public String toString() {
		int min = getMinOccurs();
		int max = getMaxOccurs();
		
		return "["+min+".."+(max == -1 ? "*" : ""+max)+"]"; 
	}
} 
