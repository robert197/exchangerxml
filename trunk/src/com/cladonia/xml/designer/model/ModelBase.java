/*
 * $Id: ModelBase.java,v 1.1 2004/03/25 18:44:21 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer.model;

import java.util.Vector;

import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.SchemaModel;

/**
 * The base for the models.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:44:21 $
 * @author Dogsbay
 */
public class ModelBase {
	private SchemaElement type = null;
	private Vector lists = null;

	public ModelBase( SchemaElement type) {
		Vector models = type.getModels();
		lists = new Vector();

		if ( models != null) {
			for ( int i = 0; i < models.size(); i++) {
				ModelList list = new ModelList( null, (SchemaModel)models.elementAt(i));
				list.init();

				lists.addElement( list);
			}
		}
	}
	
	/**
	 * Updates the model structure.
	 */
	public void update() {
		for ( int i = 0; i < lists.size(); i++) {
			((ModelList)lists.elementAt(i)).update();
		}
	}
	
	/**
	 * Gets the empty and filled in element boxes.
	 *
	 * @return the element boxes.
	 */
	public Vector getElementBoxes() {
		Vector boxes = new Vector();
		
		for ( int i = 0; i < lists.size(); i++) {
			((ModelList)lists.elementAt(i)).appendElements( boxes);
		}
		
		return boxes;
	}
}
