/*
 * $Id: ComplexSchemaType.java,v 1.2 2005/08/25 10:46:39 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.exolab.castor.xml.schema.AnyType;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.BlockList;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ContentType;
import org.exolab.castor.xml.schema.FinalList;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.ModelGroup;
import org.exolab.castor.xml.schema.Order;
import org.exolab.castor.xml.schema.SimpleType;
import org.exolab.castor.xml.schema.Structure;
import org.exolab.castor.xml.schema.Wildcard;
import org.exolab.castor.xml.schema.XMLType;

/**
 * The schema element.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/08/25 10:46:39 $
 * @author Dogsbay
 */
public class ComplexSchemaType extends SchemaType {
	public static final String BLOCK_ALL 			= "#all";
	public static final String BLOCK_EXTENSION 		= "extension";
	public static final String BLOCK_RESTRICTION	= "restriction";

	public static final String FINAL_ALL 			= "#all";
	public static final String FINAL_EXTENSION 		= "extension";
	public static final String FINAL_RESTRICTION	= "restriction";

	private ComplexType type		= null;
	private SchemaElement element	= null;
	
	private AnySchemaAttribute wildcard	= null;
	private Vector models 				= null;
	private Hashtable attributes 		= null;

	/**
	 * The constructor for the element node.
	 *
	 * @param parent the parent element.
	 * @param element the schema element declaration for the node.
	 */
	public ComplexSchemaType( SchemaElement parent, ComplexType type) {
		super( type);
		
		this.element = parent;
		this.type = type;
		attributes = new Hashtable();
		models = new Vector();
		
		XMLType baseType = type.getBaseType();

		if ( baseType != null) {
			if ( baseType.isComplexType()) {
				setBase( new ComplexSchemaType( parent, (ComplexType)baseType));
			} else if ( baseType.isSimpleType()) {
				setBase( new SimpleSchemaType( (SimpleType)baseType));
			} else {
				setBase( new AnySchemaType( (AnyType)baseType));
//				System.err.println( "Could not identify Base Type: "+baseType);
			}
		}
		
		parse( type);
	}
	
	/**
	 * The element for this type.
	 *
	 * @return the element for this type.
	 */
	public SchemaElement getElement() {
		return element;
	}

	/**
	 * Gets the (any) attribute wildcard
	 *
	 * @return the (any) attribute wildcard.
	 */
	public AnySchemaAttribute getAnyAttribute() {
		return wildcard;
	}

	/**
	 * Gets the list of child content models
	 *
	 * @return the list of child content models.
	 */
	public Vector getModels() {
		return models;
	}

	/**
	 * Gets the list of child attributes
	 *
	 * @return the list of child attributes.
	 */
	public Vector getAttributes() {
		Vector result = new Vector();
		Enumeration enumeration = attributes.elements();

		while ( enumeration.hasMoreElements()) {
			result.addElement( enumeration.nextElement());
		}
		
		return result;
	}

	/**
	 * Returns true when the type is abstract.
	 *
	 * @return true when the type is abstract.
	 */
	public boolean isAbstract() {
		return type.isAbstract();
	}

	/**
	 * Returns true when the type is mixed.
	 *
	 * @return true when the type is mixed.
	 */
	public boolean isMixed() {
		return (type.getContentType() == ContentType.mixed);
	}

	/**
	 * true when the type has simple content.
	 *
	 * @return true when simple content.
	 */
	public boolean isSimpleContent() {
		return type.isSimpleContent();
	}

	/**
	 * Returns the block field for this type.<br/>
	 * Possible values are: <br/>
	 * BLOCK_ALL, BLOCK_EXTENSION, BLOCK_RESTRICTION!
	 *
	 * @return the block field.
	 */
	public String getBlock() {
		BlockList list = null;
		String result = BLOCK_RESTRICTION;
		
		list = type.getBlock();
		
		if ( list.hasAll()) {
			result = BLOCK_ALL;
		} else if ( list.hasExtension()) {
			result = BLOCK_EXTENSION;
		}
		
		return result;
	}

	/**
	 * Returns the final field for this type.<br/>
	 * Possible values are: <br/>
	 * FINAL_ALL, FINAL_EXTENSION, FINAL_RESTRICTION!
	 *
	 * @return the final field.
	 */
	public String getFinal() {
		FinalList list = null;
		String result = FINAL_RESTRICTION;
		
		list = type.getFinal();
		
		if ( list.hasAll()) {
			result = FINAL_ALL;
		} else if ( list.hasExtension()) {
			result = FINAL_EXTENSION;
		}
		
		return result;
	}

	private void addModel( SchemaModel model) {
		if ( model != null) { // should not happen but might be possible if no model found?
			models.addElement( model);
			element.addChild( model);
		} 
//		else {
//			System.err.println( "ERROR: Tried to add a null model");
//			(new Exception()).printStackTrace();
//		}
	}
	
	// Parses the element...
	private void parse( ComplexType type ) {
		if ( type instanceof ComplexType) {
			SchemaType b = getBase();
			
			// Add the base attributes, wildcard and models to the list...
			if ( b != null && (b instanceof ComplexSchemaType)) { // && !type.isRestricted()) {
				ComplexSchemaType base = (ComplexSchemaType)b;
				
				Vector list = base.getAttributes();
				for ( int i = 0; i < list.size(); i++) {
					SchemaAttribute a = (SchemaAttribute)list.elementAt(i);
					attributes.put( a.getName(), a);
				}
				
				AnySchemaAttribute any = base.getAnyAttribute();
					
				if ( any != null) {
					wildcard = any;
				}
// new >>>
				if ( !type.isRestricted()) {
// <<<
					list = base.getModels();
					for ( int i = 0; i < list.size(); i++) {
						addModel( (SchemaModel)list.elementAt(i));
					}
// new >>>
				}
// <<<
			}
			
			// Set the wildcard
			Wildcard any = type.getAnyAttribute();
			
			if ( any != null) {
				wildcard = new AnySchemaAttribute( element, any);
			}

			// Overwrite the base attributes with the local attibutes...
			Enumeration enumeration = type.getAttributeDecls();

			while ( enumeration.hasMoreElements()) {
				AttributeDecl attr = (AttributeDecl)enumeration.nextElement();
				
				while ( attr.isReference()) {
					attr = attr.getReference();
				}
				
				SchemaAttribute a = new SchemaAttribute( element.getBaseSchema(), element, attr);
				attributes.put( a.getName(), a);
			}
			
			enumeration = type.enumerate();

			while ( enumeration.hasMoreElements()) {
				Structure struct = (Structure)enumeration.nextElement();

				switch ( struct.getStructureType()) {
					case Structure.GROUP:
						Group group = (Group)struct;
						SchemaModel model = null;
						
						if ( group.getOrder() == Order.seq) {
							model = new SequenceSchemaModel( element.getBaseSchema(), element, group);
						} else if ( group.getOrder() == Order.choice) {
							model = new ChoiceSchemaModel( element.getBaseSchema(), element, group);
						} else {
							model = new AllSchemaModel( element.getBaseSchema(), element, group);
						}
						
						addModel( model);
						break;

					case Structure.MODELGROUP:
						addModel( getModel( (ModelGroup)struct));
						break;
					
					default:
						System.err.println( "ComplexType Unknown: "+struct.getStructureType());
						break;
				}
			}
		}
	}
	
	private SchemaModel getModel( ModelGroup modelGroup) {
		int maxOccurs = modelGroup.getMaxOccurs();
		int minOccurs = modelGroup.getMinOccurs();

//		System.err.println( "ComplexSchemaType.getModel( "+modelGroup.getName()+") ["+modelGroup.getReferenceId()+"]");

		while ( modelGroup != null && modelGroup.hasReference()) {
			modelGroup = modelGroup.getReference();
		}

		Enumeration enumeration = modelGroup.enumerate();

		if ( enumeration.hasMoreElements()) {
			Structure struct = (Structure)enumeration.nextElement();

			switch ( struct.getStructureType()) {
				case Structure.GROUP:
					Group group = (Group)struct;
					group.setMaxOccurs( maxOccurs);
					group.setMinOccurs( minOccurs);

					SchemaModel model = null;
					
					if ( group.getOrder() == Order.seq) {
						model = new SequenceSchemaModel( element.getBaseSchema(), element, group);
					} else if ( group.getOrder() == Order.choice) {
						model = new ChoiceSchemaModel( element.getBaseSchema(), element, group);
					} else {
						model = new AllSchemaModel( element.getBaseSchema(), element, group);
					}
					
					return model;

				default:
					System.err.println( "SchemaModel.parse() [ERROR] Unknown Structure Type = "+struct.getStructureType());
					return null;
			}
		} else {
			System.err.println( "Could not find Structure for "+modelGroup.getReferenceId()+" ["+element.getName()+"!");
		}

		return null;
	}
} 
