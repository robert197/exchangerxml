/*
 * $Id: AnySchemaAttribute.java,v 1.1 2004/03/25 18:37:19 edankert Exp $
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
 * The schema attribute Wildcard (anyAttribute).
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:37:19 $
 * @author Dogsbay
 */
public class AnySchemaAttribute extends SchemaComponent {
	public static final String PROCESS_CONTENTS_LAX		= "lax";
	public static final String PROCESS_CONTENTS_SKIP	= "skip";
	public static final String PROCESS_CONTENTS_STRICT	= "strict";

	private Wildcard wildcard = null;

	/**
	 * The constructor for the schema attribute Wildcard.
	 *
	 * @param parent this nodes parent node.
	 * @param group the schema group declaration for the node.
	 */
	public AnySchemaAttribute( SchemaParticle parent, Wildcard wildcard) {
		super( parent, wildcard);
		
		this.wildcard = wildcard;
	}
	
	/**
	 * The namespaces for this wildcard attribute.
	 *
	 * @return the namespaces for the wildcard attribute.
	 */
	public Enumeration getNamespaces() {
		return wildcard.getNamespaces();
	}

	/**
	 * The value for the processContent field.
	 *
	 * @return the processContent value.
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
	 * The id for this wildcard attribute.
	 *
	 * @return the id for the wildcard attribute.
	 */
	public String getId() {
		return null;
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

	public String toString() {
		return "(A*)";
	}

	/*
	 * Gets the Attribute Wildcard.
	 */
	Wildcard getWildcard() {
		return wildcard;
	}
} 
