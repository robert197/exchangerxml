/*
 * $Id: AllSchemaModel.java,v 1.1 2004/03/25 18:37:19 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import org.exolab.castor.xml.schema.Group;

/**
 * The All Schema Content Model.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:37:19 $
 * @author Dogsbay
 */
public class AllSchemaModel extends SchemaModel {

	public AllSchemaModel( XMLSchema schema, SchemaParticle parent, Group group) {
		super( schema, parent, group);
	}

	// Returns the string representation of the type of model.	
	public String getType() {
		return "all";
	}
} 
