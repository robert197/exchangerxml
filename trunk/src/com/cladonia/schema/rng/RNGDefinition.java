/*
 * $Id: RNGDefinition.java,v 1.1 2004/03/25 18:39:07 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.rng;


/**
 * A container for a SchemaElement and possible attributes/child elements
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:07 $
 * @author Dogsbay
 */
public class RNGDefinition extends RNGPattern {
	private String name = null;

	public RNGDefinition( String name) {
		super();

		this.name		= name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean addReference( RNGReference reference) {
		if ( reference != null) {
			for ( int i = 0; i < references.size(); i++) {
				RNGReference ref = (RNGReference)references.elementAt(i);
				
				if ( ref.getName().equals( reference.getName()) || name.equals( ref.getName())) {
					return false;
				}
			}

			references.addElement( reference);
			return true;
		} else {
			return false;
		}
	}

	public void combine( RNGDefinition def) {
		addElements( def.getElements());
		addAttributes( def.getAttributes());
		addReferences( def.getReferences());
	}
} 
