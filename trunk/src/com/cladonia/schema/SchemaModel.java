/*
 * $Id: SchemaModel.java,v 1.2 2005/08/25 10:46:39 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import java.util.Enumeration;
import java.util.Vector;

import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.ModelGroup;
import org.exolab.castor.xml.schema.Order;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.Structure;
import org.exolab.castor.xml.schema.Wildcard;

/**
 * The default node for a schema element.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/08/25 10:46:39 $
 * @author Dogsbay
 */
public abstract class SchemaModel extends SchemaParticle {
	private Group model = null;
	private XMLSchema schema = null;

	private Vector elements		= null;
	private Vector childElements	= null;
	private Vector wildcards= null;
	private Vector models	= null;

	/**
	 * The constructor for the ContentModel.
	 *
	 * @param parent this nodes parent node.
	 * @param group the schema group declaration for the node.
	 */
	public SchemaModel( XMLSchema schema, SchemaParticle parent, Group group) {
		super( parent, group);
		
		this.model = group;
		this.schema = schema;
		
//		System.out.println("SchemaModel = "+group.getName());

		this.elements	= new Vector();
		this.wildcards	= new Vector();
		this.models		= new Vector();
		
		parse( model);
	}
	
	/**
	 * Gets the list of child elements
	 *
	 * @return the list of child elements.
	 */
	public Vector getElements() {
		return elements;
	}

	/**
	 * Gets the list of all child elements (traversing through all models)
	 *
	 * @return the list of all child elements.
	 */
	public Vector getChildElements() {
//		System.out.println( ">>> SchemaModel.getChildElements()");
	
		if ( childElements == null) {
			childElements = new Vector();
			Vector models = getModels();
			
			if ( models != null) {
				for( int i = 0; i < models.size(); i++) {
					SchemaModel model = (SchemaModel)models.elementAt(i);
					Vector elements = model.getChildElements();
					
					for( int j = 0; j < elements.size(); j++) {
						addChildElement( (SchemaElement)elements.elementAt(j));
					}
				}
			}

			Vector elements = getElements();
			
			if ( elements != null) {
				for( int i = 0; i < elements.size(); i++) {
					SchemaElement element = ((SchemaElement)elements.elementAt(i));
					String name = element.getUniversalName();
					
					if ( element.isReference()) {
						// resolve the reference to be used here but don't 
						// continue with it...
						element = schema.getGlobalElement( element);
					}
					
					Vector subs = element.getSubstituteElements();

					if ( subs.size() > 0) {
						for ( int j = 0; j < subs.size(); j++) {
							SchemaElement sub = (SchemaElement)subs.elementAt(j);
							
							addChildElement( sub);
						}
					} else {
						addChildElement( element);
					}
				}
			}
		}
	
//		System.out.println( "<<< SchemaModel.getChildElements()");

		return childElements;
	}
	
	private void addChildElement( SchemaElement element) {
//		for ( int i = 0; i < childElements.size(); i++) {
//			SchemaElement child = (SchemaElement)childElements.elementAt(i);
//			
//			if ( child.getUniversalName().equals( element.getUniversalName())) {
//				return;
//			}
//		}
		
		childElements.addElement( element);
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
	 * Gets the list of child element wildcards
	 *
	 * @return the list of child element wildcards.
	 */
	public Vector getAnyElements() {
		return wildcards;
	}

	public String getId() {
		return model.getId();
	}

	/**
	 * Returns true when the max and min occur values are 1
	 *
	 * @return true when max and min occurs is 1.
	 */
	public boolean isDefault() {
		if ( getMaxOccurs() == 1 && getMinOccurs() == 1) {
			return true;
		}
		
		return false;
	}

	/**
	 * Gets a string representation of the model type,
	 * sequence, choice or all!
	 *
	 * @return the string representation of the model type.
	 */
	public abstract String getType();

	/**
	 * Adds an element to the list of elements!
	 *
	 * @param element the to be added element.
	 */
	protected void addElement( SchemaElement element) {
		elements.addElement( element);
		addChild( element);
	}

	/**
	 * Adds a model to the list of models!
	 *
	 * @param model the to be added model.
	 */
	protected void addModel( SchemaModel model) {
		models.addElement( model);
		addChild( model);
	}

	/**
	 * Adds a wildcard to the list of wildcards!
	 *
	 * @param wildcard the to be added wildcard.
	 */
	protected void addWildcard( AnySchemaElement wildcard) {
		wildcards.addElement( wildcard);
		addChild( wildcard);
	}

// Parsing!!!!
// Parses the content model and builds up the tree of elements and attributes.
	private void parse( Group group) {
		int maxOccurs = -1;
		int minOccurs = -1;

		if ( group.getStructureType() == Structure.MODELGROUP) {
			ModelGroup m = (ModelGroup)group;
			maxOccurs = m.getMaxOccurs();
			minOccurs = m.getMinOccurs();

			while ( m != null && m.hasReference()) {
				m = m.getReference();
			}
		}
		
		Enumeration enumeration = group.enumerate();

		while ( enumeration.hasMoreElements()) {
			Structure struct = (Structure)enumeration.nextElement();

			switch ( struct.getStructureType()) {
				case Structure.ELEMENT:
					ElementDecl element = (ElementDecl)struct;
					
					addElement( new SchemaElement( schema, this, element));
					break;

				case Structure.MODELGROUP:
					ModelGroup m = (ModelGroup)struct;
//					System.out.println( "SchemaModel Model Group = "+m.getName()+" ["+m.getMinOccurs()+", "+m.getMaxOccurs()+"]");

					parse( m);
					
					break;

				case Structure.GROUP:
					Group g = (Group)struct;
					
					if ( minOccurs != -1) {
						g.setMaxOccurs( maxOccurs);
						g.setMinOccurs( minOccurs);
					} 

					SchemaModel model = null;
					
					if ( g.getOrder() == Order.seq) {
						model = new SequenceSchemaModel( schema, this, g);
					} else if ( g.getOrder() == Order.choice) {
						model = new ChoiceSchemaModel( schema, this, g);
					} else {
						model = new AllSchemaModel( schema, this, g);
					}
					
					addModel( model);
					break;
				
				case Structure.WILDCARD:
					Wildcard wildcard = (Wildcard)struct;

					if ( !wildcard.isAttributeWildcard()) {
						addWildcard( new AnySchemaElement( this, wildcard));
					} else {
						System.err.println("SchemaModel.parse() [ERROR: Wildcard Attribute in Content Model!]");
					}
					break;

				default:
					System.err.println( "SchemaModel.parse() [ERROR: Unknown Structure Type = "+struct.getStructureType()+"]");
					break;
			}
		}
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

	String toString( String indent) {
		String result = indent+toString()+"\n";
		String newIndent = indent+"  ";
		
		for ( int i = 0; i < elements.size(); i++) {
			result = result+((SchemaElement)elements.elementAt(i)).toString( newIndent);
		}

		for ( int i = 0; i < wildcards.size(); i++) {
			result = result+((AnySchemaElement)wildcards.elementAt(i)).toString( newIndent);
		}

		for ( int i = 0; i < models.size(); i++) {
			result = result+((SchemaModel)models.elementAt(i)).toString( newIndent);
		}
		
		return result;
	}
	
	public String toString() {
		return super.toString()+" ("+getType()+")";
	}
} 
