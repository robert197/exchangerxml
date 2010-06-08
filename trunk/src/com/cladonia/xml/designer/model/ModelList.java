  /*
 * $Id: ModelList.java,v 1.1 2004/03/25 18:44:21 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer.model;

import java.util.Vector;

import com.cladonia.schema.AllSchemaModel;
import com.cladonia.schema.ChoiceSchemaModel;
import com.cladonia.schema.SchemaModel;
import com.cladonia.schema.SequenceSchemaModel;

/**
 * The default list model for a model.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:44:21 $
 * @author Dogsbay
 */
public class ModelList extends ParticleList {
	private static final boolean DEBUG = false;

	private SchemaModel type;
	private Vector models;
	private boolean init = false;

	/**
	 * Constructs a list of models for the type supplied.
	 *
	 * @param type the type of the models that the list can contain.
	 */
	public ModelList( ModelBox parent, SchemaModel type) {
		super( parent);

		if (DEBUG) System.out.println( "ModelList( "+type+")");
		this.type = type;
		
		models = new Vector();
		
		if ( type instanceof SequenceSchemaModel) {
			models.addElement( new SequenceModelBox( this, (SequenceSchemaModel)type, true));
		} else if ( type instanceof ChoiceSchemaModel) {
			models.addElement( new ChoiceModelBox( this, (ChoiceSchemaModel)type, true));
		} else if ( type instanceof AllSchemaModel) {
			models.addElement( new AllModelBox( this, (AllSchemaModel)type, true));
		}
	}
	
	/**
	 * Returns the model type for the list.
	 *
	 * @return the model type of the list.
	 */
	public SchemaModel getType() {
		return type;
	}

	/**
	 * Initialises the list of elements.
	 */
	public void init() {
		if (DEBUG) System.out.println( "ModelList.init()");

		int min = type.getMinOccurs() - models.size();
		int max = type.getMaxOccurs();

		// set optional/required value on previous models
		for ( int i = 0; i < type.getMinOccurs() && i < models.size(); i++) {
			ModelBox model = (ModelBox)models.elementAt(i);
			
			if ( model.isEmpty()) {
				model.init();
			}
	
			model.setRequired();
		}

		// add all models
		// optimised for speed???
		if ( type instanceof SequenceSchemaModel) {

			if ( min > 0) {
				for ( int i = 0; i < min; i++) { // required
					models.addElement( new SequenceModelBox( this, (SequenceSchemaModel)type, false));
				}
			}
			
//			if ( !reachedMax()) { // optional
//				models.addElement( new SequenceModelBox( (SequenceSchemaModel)type, true));
//			}
		} else if ( type instanceof ChoiceSchemaModel) {
			if ( min > 0) {
				for ( int i = 0; i < min; i++) { // required
					models.addElement( new ChoiceModelBox( this, (ChoiceSchemaModel)type, false));
				}
			}
		
//			if ( !reachedMax()) { // optional
//				models.addElement( new ChoiceModelBox( (ChoiceSchemaModel)type, true));
//			}
		} else if ( type instanceof AllSchemaModel) {
			if ( min > 0) {
				for ( int i = 0; i < min; i++) { // required
					models.addElement( new AllModelBox( this, (AllSchemaModel)type, false));
				}
			}
		
//			if ( !reachedMax()) { // optional
//				models.addElement( new AllModelBox( (AllSchemaModel)type, true));
//			}
		}
		
		init = true;
	}

	/**
	 * Sets an element in a models, will return true if a 
	 * place for the element in any of the models could be found.
	 *
	 * @param element the element to set in the models.
	 *
	 * @return true if a place for the element in the models could be found.
	 */
//	public boolean set( XElement element) {
//		if (DEBUG) System.out.println( "ModelList.set( "+element+")");
//
//		boolean isFilled = false;
//		
//		if ( !isFull()) {
//			int size = models.size();
//			ModelBox previousBox = null;
//
//			for ( int i = 0; i < models.size(); i++) {
//				ModelBox currentBox = (ModelBox)models.elementAt(i);
//				
//				// found an empty box...
//				if ( currentBox.isEmpty()) {
//					if ( previousBox != null && previousBox.set( element)) {
//						isFilled = true;
//					
//					// try current box, which is empty.
//					} else if ( currentBox != previousBox) {
//						isFilled = currentBox.set( element);
//
//						if ( !init && isFilled) { // only when first element entered...
//							init();
//						}
//					}
//
//					break;
//				} 
//			
//				previousBox = currentBox;
//			}
//			
//			// no empty boxes found, try last one!
//			ModelBox lastBox = ((ModelBox)models.lastElement());
//
//			if ( !isFilled) {
//				isFilled = lastBox.set( element);
//			}
//
//			ModelBox currentBox = null;
//			ModelBox lastBox = (ModelBox)models.elementAt( size-1);
//			
//			if ( size > 1 && lastBox.isEmpty()) {
//				currentBox = (ModelBox)models.elementAt( size-2);
//			} else {
//				currentBox = lastBox;
//			}
//			
//			if ( currentBox.set( element)) {
//				if ( !init) { // only when first element entered...
//					init();
//				}
//	
//				isFilled = true;
//			
//			// try last box, this must be empty or equal to previous.
//			} else if ( currentBox != lastBox) {
//				isFilled = lastBox.set( element);
//			}
//			
//			// The list could be full now, if not add a new model...
//			if ( lastBox.hasFullContent() && !reachedMax()) {
//				if ( type instanceof SequenceSchemaModel) {
//					models.addElement( new SequenceModelBox( (SequenceSchemaModel)type, true));
//				} else if ( type instanceof ChoiceSchemaModel) {
//					models.addElement( new ChoiceModelBox( (ChoiceSchemaModel)type, true));
//				} else if ( type instanceof AllSchemaModel) {
//					models.addElement( new AllModelBox( (AllSchemaModel)type, true));
//				}
//			}
//		}
//		
//		return isFilled;
//	}
	
	/**
	 * Is this list still empty or does it have an element already?
	 *
	 * @return true if no box in the list contains a element.
	 */
	public boolean isEmpty() {
		for ( int i = 0; i < models.size(); i++) {
			if ( !((ModelBox)models.elementAt(i)).isEmpty()) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Has the last model got full content?
	 *
	 * @return true if the last box in the list has full content.
	 */
	public boolean hasFullContent() {
		return ((ModelBox)models.lastElement()).hasFullContent();
	}

	/**
	 * Is this list full completely?
	 *
	 * @return true if the last box in the list is full and the maximum 
	 *         number of models has been reached.
	 */
	public boolean isFull() {
		return reachedMax() && ((ModelBox)models.lastElement()).isFull();
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
	 * Checks the information in the model and updates accordingly.
	 */
	public void update() {
		if ( init) {
			trim();
			init();
		}

		if ( !isFull()) {
			int size = models.size();
			ModelBox previousBox = null;
			
			// see if a element has been set in any of the child models,
			// in that case init...
			for ( int i = 0; i < models.size(); i++) {
				ModelBox box = (ModelBox)models.elementAt(i);
				box.update();
				
				// found a non empty box...
				if ( !box.isEmpty() && !init) {
					init();
				} 
			}
			
			// get last model!
			ModelBox lastBox = ((ModelBox)models.lastElement());

			// The list could be full now, if not add a new model...
			if ( lastBox.hasFullContent() && !reachedMax()) {
				if ( type instanceof SequenceSchemaModel) {
					models.addElement( new SequenceModelBox( this, (SequenceSchemaModel)type, true));
				} else if ( type instanceof ChoiceSchemaModel) {
					models.addElement( new ChoiceModelBox( this, (ChoiceSchemaModel)type, true));
				} else if ( type instanceof AllSchemaModel) {
					models.addElement( new AllModelBox( this, (AllSchemaModel)type, true));
				}
			}
		}
	}

	/**
	 * Appends the elements in this list to the list of elements.
	 *
	 * @param list the list of elements.
	 */
	public void appendElements( Vector list) {
		if (DEBUG) System.out.println( "ModelList.appendElements( "+list+")");
		for ( int i = 0; i < models.size(); i++) {
			((ModelBox)models.elementAt(i)).appendElements( list);
		}
	}

	// true if the maximum number of models has been reached.
	private boolean reachedMax() {
		int max = type.getMaxOccurs();

		return (max > 0 && (models.size() == max));
	}

	// removes empty models
	private void trim() {
		Vector temp = new Vector( models);
		
		for ( int i = 0; i < temp.size(); i++) {
			ModelBox box = (ModelBox)temp.elementAt(i);

			if ( models.size() > 1) {
				if ( box.isEmpty()) {
					models.remove( box);
				}
			} else {
				
				return;
			}
		}
	}
}
