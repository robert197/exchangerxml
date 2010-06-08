/*
 * $Id: ModelBox.java,v 1.1 2004/03/25 18:44:21 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer.model;

import java.util.Vector;

import com.cladonia.schema.SchemaModel;

/**
 * The box for a schema model.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:44:21 $
 * @author Dogsbay
 */
public abstract class ModelBox {
	protected static final boolean DEBUG = false;

	protected Vector particles = null;
	protected boolean optional = true;

	private SchemaModel type = null;
	private ModelList parent = null;

	/**
	 * Constructs the model-box for an model of the type supplied.
	 *
	 * @param type the type of the model that the box can contain.
	 * @param optional wether the model is required or optional
	 */
	public ModelBox( ModelList parent, SchemaModel type, boolean optional) {
		if (DEBUG) System.out.println( "ModelBox( "+type+", "+optional+")");

		this.type		= type;
		this.optional	= optional;
		this.parent		= parent;
		
		// create the different lists..
		particles = new Vector();
	}
	
	/**
	 * Returns the model type.
	 *
	 * @return the schema type of the model.
	 */
	public SchemaModel getType() {
		return type;
	}

	/**
	 * Returns the parent model list.
	 *
	 * @return the parent model list.
	 */
	public ModelList getParent() {
		return parent;
	}

	/**
	 * Sets an element in the model, will return true if a 
	 * place for the element in the model could be found.
	 *
	 * @param element the element to set in the model.
	 *
	 * @return true if a place for the element in the model could be found.
	 */
//	public abstract boolean set( XElement element);

	/**
	 * Initialises the model.
	 */
	public abstract void init();

	/**
	 * Checks the information in the model and updates accordingly.
	 */
	public abstract void update();

	/**
	 * Returns wether this model box has content that is full.
	 *
	 * @return true when any of the particle lists is full.
	 */
	public boolean hasFullContent() {
		for ( int i = 0; i < particles.size(); i++) {
			ParticleList list = (ParticleList)particles.elementAt(i);

			if ( list instanceof ModelList) {
				if ( ((ModelList)list).hasFullContent()) {
					return true;
				}
			} else if ( list.isFull()) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Sets the optional value in the model.
	 *
	 * @param optional wether this morel is required or optional.
	 */
	public void setRequired() {
		optional = false;
		
		// set the optional flag in the particles...
		for ( int i = 0; i < particles.size(); i++) {
			((ParticleList)particles.elementAt(i)).setRequired();
		}
	}

	/**
	 * Returns wether this model box has an element.
	 *
	 * @return false if there is no element in this model box.
	 */
	public boolean isEmpty() {
		for ( int i = 0; i < particles.size(); i++) {
			if ( !((ParticleList)particles.elementAt(i)).isEmpty()) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Is this model full completely?
	 *
	 * @return true if the every space in the model has been filled with elements. 
	 */
	public boolean isFull() {
		for ( int i = 0; i < particles.size(); i++) {
			if ( !((ParticleList)particles.elementAt(i)).isFull()) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Gets the list of elements.
	 *
	 * @return the list of elements.
	 */
	public Vector getElements() {
		Vector elements = new Vector();
		
		appendElements( elements);
		
		return elements;
	}
	
	/**
	 * Appends the elements in this list to the list of elements.
	 *
	 * @param list the list of elements.
	 */
	public void appendElements( Vector list) {
		if (DEBUG) System.out.println( "ModelBox.appendElements()");
		for ( int i = 0; i < particles.size(); i++) {
			((ParticleList)particles.elementAt(i)).appendElements( list);
		}
	}
	
	protected int getLastNonEmptyParticlePostion() {
		for ( int i = particles.size(); i > 0; i--) {
			if ( !((ParticleList)particles.elementAt( i - 1)).isEmpty()) {
				return i - 1;
			}
		}
		
		return 0;
	}
	
	protected void initAllParticles() {
		for ( int i = 0; i < particles.size(); i++) {
			((ParticleList)particles.elementAt(i)).init();
		}
	}
}
