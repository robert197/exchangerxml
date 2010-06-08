/*
 * $Id: AllModelBox.java,v 1.1 2004/03/25 18:44:21 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer.model;

import java.util.Vector;

import com.cladonia.schema.AllSchemaModel;
import com.cladonia.schema.SchemaElement;

/**
 * The box for an all schema model.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:44:21 $
 * @author Dogsbay
 */
public class AllModelBox extends ModelBox {

	/**
	 * Constructs the sequence-model-box for the type supplied.
	 *
	 * @param parent the parent ModelList.
	 * @param type the type of sequence model for the box.
	 * @param optional wether the model is required or optional
	 */
	public AllModelBox( ModelList parent, AllSchemaModel type, boolean optional) {
		super( parent, type, optional);
		
		if (DEBUG) System.out.println( "AllModelBox( "+type+", "+optional+")");

		init();
	}
	
	/**
	 * Initialises the all model.
	 */
	public void init() {
		if (DEBUG) System.out.println( "AllModelBox.init()");

		Vector children = getType().getChildren();
		
		particles.removeAllElements();

		for ( int i = 0; i < children.size(); i++) {
			Object child = children.elementAt( i);
			
			if ( child instanceof SchemaElement) {
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
//		if (DEBUG) System.out.println( "AllModelBox.set( "+element+")");
//
//		Vector temps = new Vector( particles);
//
//		// set the element in one of the sub models when a place has 
//		// been found, get rid of other submodels.
//		for ( int i = 0; i < particles.size(); i++) {
//
//			ParticleList particle = (ParticleList)particles.elementAt(i);
//
//			if ( particle.set( element)) {
//				particles.removeAllElements();
//				
//				// add all filled particles
//				for ( int j = 0; j < temps.size(); j++) {
//					ParticleList part = (ParticleList)temps.elementAt(j);
//
//					if ( !part.isEmpty()) {
//						particles.addElement( part);
//					}
//				}
//
//				// add all not filled particles
//				for ( int j = 0; j < temps.size(); j++) {
//					ParticleList part = (ParticleList)temps.elementAt(j);
//
//					if ( part.isEmpty()) {
//						particles.addElement( part);
//					}
//				}
//
//				return true;
//			}
//		}
//		return false;
//	}

	/**
	 * Checks the information in the model and updates accordingly.
	 */
	public void update() {
		if (DEBUG) System.out.println( "AllModelBox.update()");

		Vector fullParticles = new Vector();
		Vector emptyParticles = new Vector();

		// set the element in one of the sub models when a place has 
		// been found, get rid of other submodels.
		for ( int i = 0; i < particles.size(); i++) {

			ParticleList list = (ParticleList)particles.elementAt(i);
			
			list.update();

			if ( list.isEmpty()) {
				emptyParticles.addElement( list);
			} else {
				fullParticles.addElement( list);
			}
		}

		particles.removeAllElements();

		for ( int i = 0; i < fullParticles.size(); i++) {
			particles.addElement( fullParticles.elementAt( i));
		}

		for ( int i = 0; i < emptyParticles.size(); i++) {
			particles.addElement( emptyParticles.elementAt( i));
		}
	}
}
