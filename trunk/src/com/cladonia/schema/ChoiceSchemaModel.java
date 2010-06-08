/*
 * $Id: ChoiceSchemaModel.java,v 1.1 2004/03/25 18:37:18 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import org.exolab.castor.xml.schema.Group;

/**
 * The Choice Schema Content Model.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:37:18 $
 * @author Dogsbay
 */
public class ChoiceSchemaModel extends SchemaModel {

	public ChoiceSchemaModel( XMLSchema schema, SchemaParticle parent, Group group) {
		super( schema, parent, group);
	}

	// Returns the string representation of the type of model.	
	public String getType() {
		return "choice";
	}
} 
