/*
 * $Id: ChoiceModelBox.java,v 1.1 2004/03/25 18:44:21 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer.model;

import java.util.Vector;

import com.cladonia.schema.ChoiceSchemaModel;
import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.SchemaModel;

/**
 * The box for a choice schema model.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:44:21 $
 * @author Dogsbay
 */
public class ChoiceModelBox extends ModelBox {

	/**
	 * Constructs the choice-model-box for the type supplied.
	 *
	 * @param parent the parent ModelList.
	 * @param type the type of choice model for the box.
	 * @param optional wether the model is required or optional
	 */
	public ChoiceModelBox( ModelList parent, ChoiceSchemaModel type, boolean optional) {
		super( parent, type, optional);

		if (DEBUG) System.out.println( "ChoiceModelBox( "+type+", "+optional+")");
		
		init();
	}
	
	/**
	 * Initialises the choice model.
	 */
	public void init() {
		Vector children = getType().getChildren();
		
		particles.removeAllElements();

		for ( int i = 0; i < children.size(); i++) {
			Object child = children.elementAt( i);
			
			if ( child instanceof SchemaModel) {
				ModelList list = new ModelList( this, (SchemaModel)child);
				particles.add( list);
				
				if ( !optional) {
					list.init();
				}

			} else if ( child instanceof SchemaElement) {
				ElementList list = new ElementList( this, (SchemaElement)child);
				particles.add( list);

				if ( !optional) {
					list.init();
				}
			}
		}
	}
	
	/**
	 * Sets an element in the model, will return true if a 
	 * place for the element in the model could be found.
	 *
	 * @param element the element to set in the model.
	 *
	 * @return true if a place for the element in the model could be found.
	 */
//	public boolean set( XElement element) {
//		if (DEBUG) System.out.println( "ChoiceModelBox.set( "+element+")");
//
//		// set the element in one of the sub models when a place has 
//		// been found, get rid of other submodels.
//		for ( int i = 0; i < particles.size(); i++) {
//			ParticleList particle = (ParticleList)particles.elementAt(i);
//
//			if ( particle.set( element)) {
//				particles.removeAllElements();
//				particles.addElement( particle);
//
//				return true;
//			}
//		}
//
//		return false;
//	}

	/**
	 * Checks the information in the model and updates accordingly.
	 */
	public void update() {
		if (DEBUG) System.out.println( "ChoiceModelBox.update()");

		// Check if a particle list is no longer empty, 
		// get rid of others!
		for ( int i = 0; i < particles.size(); i++) {
			ParticleList list = (ParticleList)particles.elementAt(i);
			
			list.update();

			if ( !list.isEmpty()) {
				particles.removeAllElements();
				particles.addElement( list);
				return;
			}
		}
	}
}
