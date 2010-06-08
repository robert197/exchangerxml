/*
 * $Id: SchemaAttribute.java,v 1.2 2004/09/23 10:50:00 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import java.util.Enumeration;
import java.util.Vector;

import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.Facet;
import org.exolab.castor.xml.schema.Form;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.SimpleType;
import org.exolab.castor.xml.schema.Union;
import org.exolab.castor.xml.schema.simpletypes.ListType;

/**
 * The schema attribute.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/09/23 10:50:00 $
 * @author Dogsbay
 */
public class SchemaAttribute extends SchemaComponent implements AttributeInformation {
	public static final String FORM_QUALIFIED	= "qualified";
	public static final String FORM_UNQUALIFIED	= "unqualified";
	
	public static final String USE_OPTIONAL		= "optional";
	public static final String USE_PROHIBITED	= "prohibited";
	public static final String USE_REQUIRED		= "required";

	private AttributeDecl attribute = null;
	private SchemaType type = null;
	private XMLSchema schema = null;
	private Vector values = null;
	private String prefix = null;
	private String universalName = null;
	private String namespace = null;

	/**
	 * The constructor for the schema attribute.
	 *
	 * @param parent this attributes parent element.
	 * @param attribute the schema attribute declaration for the node.
	 */
	public SchemaAttribute( XMLSchema schema, SchemaParticle parent, AttributeDecl attribute) {
		super( parent, attribute);

		this.attribute = attribute;
		this.schema = schema;
		
		// System.out.println("SchemaAttribute = "+attribute.getName());

		SimpleType type = attribute.getSimpleType();
		
		if ( type != null) {
			this.type = new SimpleSchemaType( type);
		} else {
			this.type = new AnySchemaType();
		}
	}
	
	/**
	 * Returns the Univeral Name {namespace}localname.
	 *
	 * @return the Universal name.
	 */
	public String getUniversalName() {
		if ( universalName == null) {
			StringBuffer universal = new StringBuffer();
			String namespace = getNamespace();
			
			if ( namespace != null && namespace.length() > 0) {
				universal.append( "{");
				universal.append( namespace);
				universal.append( "}");
			}

			universal.append( getName());
			universalName = universal.toString();
		}
		
		return universalName;
	}

	/**
	 * Sets the current prefix.
	 *
	 * @param prefix the current prefix.
	 */
	public void setPrefix( String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Gets the current prefix.
	 *
	 * @return the current prefix.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Returns the qualified Name prefix:localname.
	 *
	 * @return the Qualified name.
	 */
	public String getQualifiedName() {
		String qname = "";
		
		if ( prefix != null && prefix.trim().length() > 0) {
			qname = prefix+":";
		}
		
		return qname+getName();
	}

	/**
	 * The name for this attribute.
	 *
	 * @return the name for the attribute.
	 */
	public String getName() {
		return attribute.getName();
	}

	/**
	 * Get the attribute values.
	 *
	 * @return the attribute values.
	 */
	public Vector getValues() {
		if ( values == null) {
			values = new Vector();
			SimpleType type = attribute.getSimpleType();
			
			if ( type != null) {
				getValues( values, type);
					
				// Add true and false for Boolean values ...
				SimpleType base = type.getBuiltInBaseType();
				if ( base != null && values.size() == 0 && "boolean".equals( base.getName())) {
					if ( getFixed() != null && "true".equals( getFixed())) {
						values.addElement( new AttributeValue( "true", AttributeValue.FIXED_TYPE));
					} else if ( getDefault() != null && "true".equals( getDefault())) {
						values.addElement( new AttributeValue( "true", AttributeValue.DEFAULT_TYPE));
					} else {
						values.addElement( new AttributeValue( "true", AttributeValue.NORMAL_TYPE));
					}
	
					if ( getFixed() != null && "false".equals( getFixed())) {
						values.addElement( new AttributeValue( "false", AttributeValue.FIXED_TYPE));
					} else if ( getDefault() != null && "false".equals( getDefault())) {
						values.addElement( new AttributeValue( "false", AttributeValue.DEFAULT_TYPE));
					} else {
						values.addElement( new AttributeValue( "false", AttributeValue.NORMAL_TYPE));
					}
				}
			}
			
			if ( values.size() == 0 && getFixed() != null) {
				values.addElement( new AttributeValue( getFixed(), AttributeValue.FIXED_TYPE));
			} else if ( values.size() == 0 && getDefault() != null) {
				values.addElement( new AttributeValue( getDefault(), AttributeValue.DEFAULT_TYPE));
			}
		}
		
		return new Vector( values);
	}

	private void getValues( Vector result, SimpleType type) {
		if ( type != null) { 
//			System.out.println( "["+type.getName()+"] Derived by: "+type.getDerivationMethod());

			if ( type instanceof Union) {
				Enumeration types = ((Union)type).getMemberTypes();
				
				while( types.hasMoreElements()) {
					getValues( result, (SimpleType)types.nextElement());
				}
			} else if ( type instanceof ListType) {
				getValues( result, ((ListType)type).getItemType());
			} else if ( !"restriction".equals( type.getDerivationMethod())) {
				getValues( result, (SimpleType)type.getBaseType());
			}

			Enumeration facets = type.getLocalFacets();
			
			if ( facets != null) {
				while ( facets.hasMoreElements()) {
					Facet facet = (Facet)facets.nextElement();
					
					if ( Facet.ENUMERATION.equals( facet.getName())) {
//						System.out.println( facet.getName()+":"+facet.getValue());
						
						if ( !contains( result, facet)) {
							AttributeValue value = null;
	
							if ( getFixed() != null && facet.getValue().equals( getFixed())) {
								value = new AttributeValue( facet.getValue(), AttributeValue.FIXED_TYPE);
							} else if ( getDefault() != null && facet.getValue().equals( getDefault())) {
								value = new AttributeValue( facet.getValue(), AttributeValue.DEFAULT_TYPE);
							} else {
								value = new AttributeValue( facet.getValue(), AttributeValue.NORMAL_TYPE);
							}
	
							result.addElement( value);
						}
					}
				}
			}
		}
	}
	
	private boolean contains( Vector values, Facet facet) {
		for ( int i = 0; i < values.size(); i++) {
			AttributeValue value = (AttributeValue)values.elementAt( i);
			
			if ( facet.getValue().equals( value.getValue())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * The type for this attribute.
	 *
	 * @return the type for the attribute.
	 */
	public SchemaType getType() {
		return type;
	}

	/**
	 * The id for this attribute.
	 *
	 * @return the id for the attribute.
	 */
	public String getId() {
		return attribute.getId();
	}

	/**
	 * The default value for this attribute.
	 *
	 * @return the default value for the attribute.
	 */
	public String getDefault() {
		if ( attribute.isDefault()) {
			return attribute.getDefaultValue();
		} else {
			return null;
		}
	}

	/**
	 * The namespace for this attribute.
	 *
	 * @return the namespace for the attribute.
	 */
	public String getNamespace() {
		if ( namespace == null) {
			Schema s = attribute.getSchema();
			Form f = s.getAttributeFormDefault();
	
			if ( f != null && f.isQualified()) {
				namespace = s.getTargetNamespace();
			} else {
				namespace = "";
			}
		}
		
		return namespace;
	}

	/**
	 * The fixed value for this attribute.
	 *
	 * @return the fixed value for the attribute.
	 */
	public String getFixed() {
		if ( attribute.isFixed()) {
			return attribute.getFixedValue();
		} else {
			return null;
		}
	}

	/**
	 * The form value for this attribute.
	 *
	 * @return the form value for the attribute.
	 */
	public String getForm() {
		if ( attribute.getForm() == Form.Qualified) {
			return FORM_QUALIFIED;
		} else {
			return FORM_UNQUALIFIED;
		}
	}

	/**
	 * The use value for this attribute.
	 *
	 * @return the use value for the attribute.
	 */
	public String getUse() {
		String result = USE_OPTIONAL;
		
		if ( attribute.getUse() == AttributeDecl.USE_PROHIBITED) {
			result = USE_PROHIBITED;
		} else if ( attribute.getUse() == AttributeDecl.USE_REQUIRED){
			result = USE_REQUIRED;
		}
		
		return result;
	}
	
	public boolean isRequired() {
		return attribute.getUse() == AttributeDecl.USE_REQUIRED;
	}

	/**
	 * returns A list of of elements that refer to this attribute.
	 *
	 * @return the list of referers.
	 */
	public Vector getReferers() {
		return schema.getReferers( this);
	}

	/**
	 * returns the schema this attribute belongs to.
	 *
	 * @return the schema.
	 */
	public Schema getSchema() {
		return attribute.getSchema();
	}

	public String toString() {
		return "(A) "+attribute.getName();
	}

	/*
	 * The attribute represented by this node.
	 */
	AttributeDecl getAttribute() {
		return attribute;
	}
	
	public boolean equals( Object object) {

		if ( object instanceof SchemaAttribute) {
			 if ( ((SchemaAttribute)object).getAttribute() == this.attribute) {
				return true;
			 }
		}
		
		return false;
	}
} 
