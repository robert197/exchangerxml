/*
 * $Id: SimpleSchemaType.java,v 1.2 2005/08/25 10:46:39 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import java.util.Enumeration;
import java.util.Vector;

import org.exolab.castor.xml.schema.Facet;
import org.exolab.castor.xml.schema.SimpleType;
import org.exolab.castor.xml.schema.XMLType;

/**
 * The simple schema type.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/08/25 10:46:39 $
 * @author Dogsbay
 */
public class SimpleSchemaType extends SchemaType {

	public static final String FINAL_ALL 			= "#all";
	public static final String FINAL_LIST	 		= "list";
	public static final String FINAL_UNION 			= "union";
	public static final String FINAL_RESTRICTION	= "restriction";

	/**
	 * The constructor for the simple schema type.
	 *
	 * @param type the simple schema type for the attribute or element.
	 */
	public SimpleSchemaType( SimpleType type) {
		super( type);
		
		XMLType baseType = type.getBaseType();

		if ( baseType != null) {
			setBase( new SimpleSchemaType( (SimpleType)baseType));
		}
	}
	
	/**
	 * Returns the final field for this type.<br/>
	 * Possible values are: <br/>
	 * FINAL_ALL, FINAL_LIST, FINAL_UNION, FINAL_RESTRICTION!
	 *
	 * @return the final field.
	 */
	public String getFinal() {
		String fin = null;
		String result = FINAL_ALL;
		
		fin = ((SimpleType)getType()).getFinal();
		
		if ( fin.equals( SimpleType.FINAL_RESTRICTION)) {
			result = FINAL_RESTRICTION;
		} else if ( fin.equals( SimpleType.FINAL_LIST)) {
			result = FINAL_LIST;
		} else if ( fin.equals( SimpleType.FINAL_UNION)) {
			result = FINAL_UNION;
		}
		
		return result;
	}
	
	public Vector getEnumerations() {
		Vector result = null;
		Enumeration enumeration = ((SimpleType)getType()).getFacets( Facet.ENUMERATION);
		
		if ( enumeration != null) {
			result = new Vector();
			
			while( enumeration.hasMoreElements()) {
				Facet e = (Facet)enumeration.nextElement();
				result.add( e.getValue());
			}
		}

		return result;
	}

	public String getPattern() {
		Facet patternFacet= ((SimpleType)getType()).getFacet( Facet.PATTERN);
		if ( patternFacet == null) {
			return null;
		}

		return patternFacet.getValue();
	}
} 
