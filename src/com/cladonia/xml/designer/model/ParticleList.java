/*
 * $Id: ParticleList.java,v 1.1 2004/03/25 18:44:21 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer.model;

import java.util.Vector;

/**
 * The default list model for a particle.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:44:21 $
 * @author Dogsbay
 */
public abstract class ParticleList {
	private ModelBox parent = null;

	public ParticleList( ModelBox parent) {
		this.parent = parent;
	}

	public ModelBox getParent() {
		return parent;
	}
	
	// Will set the element if the model is not full and the 
	// element is of the same type...
//	public abstract boolean set( XElement element);

	public abstract boolean isFull();

	/**
	 * Checks the information in the model and updates accordingly.
	 */
	public abstract void update();

	public abstract void init();

	public abstract boolean isEmpty();
	
	/**
	 * Sets the optional value to false in the list.
	 *
	 * @param optional wether this list is required or optional.
	 */
	 public void setRequired() {
 		init();
	 }

	/**
	 * Gets the list of elements.
	 *
	 * @return the list of elements.
	 */
	public abstract Vector getElements();
	
	/**
	 * Appends the elements in this list to the list of elements.
	 *
	 * @param list the list of elements.
	 */
	public abstract void appendElements( Vector list);
	
}
