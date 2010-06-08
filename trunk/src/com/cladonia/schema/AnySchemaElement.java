/*
 * $Id: AnySchemaElement.java,v 1.1 2004/03/25 18:37:18 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import java.util.Enumeration;

import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.Wildcard;

/**
 * The schema element Wildcard (any).
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:37:18 $
 * @author Dogsbay
 */
public class AnySchemaElement extends SchemaParticle {
	public static final String PROCESS_CONTENTS_LAX		= "lax";
	public static final String PROCESS_CONTENTS_SKIP	= "skip";
	public static final String PROCESS_CONTENTS_STRICT	= "strict";

	private Wildcard wildcard = null;

	/**
	 * The constructor for the schema element Wildcard.
	 *
	 * @param parent this nodes parent node.
	 * @param wildcard the element wildcard (any).
	 */
	public AnySchemaElement( SchemaParticle parent, Wildcard wildcard) {
		super( parent, wildcard);
		
		this.wildcard = wildcard;
	}
	
	/**
	 * The namespaces for this wildcard element.
	 *
	 * @return the namespaces for the wildcard element.
	 */
	public Enumeration getNamespaces() {
		return wildcard.getNamespaces();
	}

	/**
	 * The value for the processContents field.
	 *
	 * @return the processContents value.
	 */
	 public String getProcessContents() {
	 	String result = PROCESS_CONTENTS_STRICT;
	 	String pc = wildcard.getProcessContent();
	 	
	 	if ( pc.equals( PROCESS_CONTENTS_LAX)) {
	 		result = PROCESS_CONTENTS_LAX;
	 	} else if ( pc.equals( PROCESS_CONTENTS_SKIP)) {
	 		result = PROCESS_CONTENTS_SKIP;
	 	}
	 	
	 	return result;
	 }

	/**
	 * The id for this element.
	 *
	 * @return the id for the element.
	 */
	public String getId() {
		return null;
	}

	public String toString() {
		return super.toString()+" (E*) ";
	}

	public Schema getSchema() {
		SchemaParticle parent = getParent();
		
		while ( parent != null && !(parent instanceof SchemaElement)) {
			parent = parent.getParent();
		}
		
		if ( parent != null) {
			return ((SchemaElement)parent).getSchema();
		} else {
			return null;
		}
	}

	/*
	 * Gets the Element Wildcard.
	 */
	Wildcard getWildcard() {
		return wildcard;
	}
} 
