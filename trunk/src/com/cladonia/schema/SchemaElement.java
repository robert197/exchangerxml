/*
 * $Id: SchemaElement.java,v 1.4 2005/08/25 10:46:39 gmcgoldrick Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import java.util.Enumeration;
import java.util.Vector;

//import org.exolab.castor.xml.schema.Annotation;
import org.exolab.castor.xml.schema.AnyType;
import org.exolab.castor.xml.schema.BlockList;
import org.exolab.castor.xml.schema.ComplexType;
//import org.exolab.castor.xml.schema.Documentation;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.FinalList;
import org.exolab.castor.xml.schema.Form;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.SimpleType;
import org.exolab.castor.xml.schema.XMLType;

/**
 * The schema element.
 *
 * @version	$Revision: 1.4 $, $Date: 2005/08/25 10:46:39 $
 * @author Dogsbay
 */
public class SchemaElement extends SchemaParticle implements ElementInformation {
	public static final String FORM_QUALIFIED	= "qualified";
	public static final String FORM_UNQUALIFIED	= "unqualified";

	public static final String BLOCK_ALL 			= "#all";
	public static final String BLOCK_EXTENSION 		= "extension";
	public static final String BLOCK_RESTRICTION	= "restriction";
	public static final String BLOCK_SUBSTITUTION	= "substitution";

	public static final String FINAL_ALL 			= "#all";
	public static final String FINAL_EXTENSION 		= "extension";
	public static final String FINAL_RESTRICTION	= "restriction";

	private String prefix = null;
	private String universalName = null;
	private String namespace = null;

	private Vector anyElements = null;
	private Vector childElements = null;
	private Vector substitutes = null;
	private Vector substituteElements = null;
	
	private ElementDecl element = null;
	private SchemaType type		= null;
	private XMLSchema schema	= null;
	
	private boolean recursed = false;
	
	/**
	 * The constructor for the element node.
	 *
	 * @param parent this nodes parent node.
	 * @param element the schema element declaration for the node.
	 */
	public SchemaElement( XMLSchema schema, SchemaParticle parent, ElementDecl element) {
		super( parent, element);

		this.element = element;
		this.schema = schema;
		
		if ( !isReference() && !isRecursive()) {
			parse();
		}
	}
	
	/**
	 * The name for this element.
	 *
	 * @return the name for the element.
	 */
	public String getName() {
		return element.getName();
	}

	/**
	 * The namespace for this element.
	 *
	 * @return the namespace for the element.
	 */
	public String getNamespace() {
		if ( namespace == null) {
		
			Schema s = element.getSchema();
			Form f = s.getElementFormDefault();
			
			if ( isReference()) {
				ElementDecl ref = element.getReference();
				s = ref.getSchema();
				
				namespace = s.getTargetNamespace();
			} else if ( isGlobal() || (f != null && f.isQualified())) {
				namespace = s.getTargetNamespace();
			} else {
				namespace = "";
			}
		}
		
		return namespace;
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
	 * The id for this element.
	 *
	 * @return the id for the element.
	 */
	public String getId() {
		return element.getId();
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
	 * Gets the type of this element. 
	 * The type is null when the element is recursive.
	 *
	 * @return the type of the element.
	 */
	public String getType() {
		if ( type != null) {
			return type.getName();
		} else {
			return null;
		}
	}

	public SchemaType getSchemaType() {
		return type;
	}

	/**
	 * Returns true when the element is abstract.
	 *
	 * @return true when the element is abstract.
	 */
	public boolean isAbstract() {
		return element.isAbstract();
	}

	/**
	 * Returns true when the element is recursive and 
	 * has not been parsed further.
	 *
	 * @return true when the element is recursive.
	 */
	public boolean isRecursive() {
		if ( recursed || (getParent() == null) || (getParent().getAncestor( element) == null)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Returns true when the element can have no content.
	 *
	 * @return true when the element is nillable.
	 */
	public boolean isNillable() {
		return element.isNillable();
	}

	public boolean isEmpty() {
		if ( type instanceof ComplexSchemaType) {
			if ( !((ComplexSchemaType)type).isSimpleContent()) {
//				if ( getAttributes() != null || getAttributes().size() == 0) {
					return true;
//				}
			}
		}

		return false;
	}

	/**
	 * Returns the block field for this element.<br/>
	 * Possible values are: <br/>
	 * BLOCK_ALL, BLOCK_EXTENSION, BLOCK_SUBSTITUTION, BLOCK_RESTRICTION!
	 *
	 * @return the block field.
	 */
	public String getBlock() {
		BlockList list = element.getBlock();
		String result = null;
		
		if ( list != null) {
			if ( list.hasAll()) {
				result = BLOCK_ALL;
			} else if ( list.hasExtension()) {
				result = BLOCK_EXTENSION;
			} else if ( list.hasSubstitution()) {
				result = BLOCK_SUBSTITUTION;
			} else if ( list.hasRestriction()) {
				result = BLOCK_RESTRICTION;
			}
		}
		
		return result;
	}

	/**
	 * Returns the final field for this element.<br/>
	 * Possible values are: <br/>
	 * FINAL_ALL, FINAL_EXTENSION, FINAL_RESTRICTION!
	 *
	 * @return the final field.
	 */
	public String getFinal() {
		FinalList list = element.getFinal();
		String result = null;
		
		if ( list != null) {
			if ( list.hasAll()) {
				result = FINAL_ALL;
			} else if ( list.hasExtension()) {
				result = FINAL_EXTENSION;
			} else if ( list.hasRestriction()) {
				result = FINAL_RESTRICTION;
			}
		}
				
		return result;
	}

	/**
	 * The default value for this element.
	 *
	 * @return the default value for the element.
	 */
	public String getDefault() {
		return element.getDefaultValue();
	}

	/**
	 * The fixed value for this element.
	 *
	 * @return the fixed value for the element.
	 */
	public String getFixed() {
		return element.getFixedValue();
	}

	/**
	 * The form value for this element.
	 *
	 * @return the form value for the element.
	 */
	public String getForm() {
		if ( element.getForm() == Form.Qualified) {
			return FORM_QUALIFIED;
		} else {
			return FORM_UNQUALIFIED;
		}
	}

	/**
	 * returns A list of substitutes for this element.
	 * this list can contain abstract elements.
	 *
	 * @return the list of substitutes.
	 */
	public Vector getSubstitutes() {
		if ( substitutes == null) {
			substitutes = schema.getSubstitutionGroup( getName());
			
			if ( substitutes == null) {
				substitutes = new Vector();
			}
		}

		return substitutes;
	}

	/**
	 * returns A list of substitutes for this element, 
	 * making sure the list does not contain any abstract elements.
	 *
	 * @return the list of substitutes.
	 */
	public Vector getSubstituteElements() {
//		System.out.println( ">>> SchemaElement.getSubstituteElements() ["+getName()+"]");
		if ( substituteElements == null) {
			substituteElements = new Vector();
			Vector substitutes = getSubstitutes();
			
			for ( int i = 0; i < substitutes.size(); i++) {
				SchemaElement substitute = (SchemaElement)substitutes.elementAt(i);
	//			System.out.println( "SchemaElement.getSubstituteElements() ["+getName()+"]["+substitute.getName()+"]");

				if ( substitute.isReference()) {
					substitute = schema.getGlobalElement( substitute);
//					substitute.resolveReference();
				}
				
				if ( substitute != this) { // avoid looping
					Vector subs = substitute.getSubstituteElements();
					
					if ( subs.size() > 0) {
						for ( int j = 0; j < subs.size(); j++) {
		//					System.out.println( "SchemaElement.getSubstituteElements() ["+getName()+"]["+substitute.getName()+"]["+((SchemaElement)subs.elementAt(j)).getName()+"]");
							substituteElements.addElement( subs.elementAt(j));
						}
					} else {
						substituteElements.addElement( substitute);
					}
				}
			}
		}

//		System.out.println( "<<< SchemaElement.getSubstituteElements() ["+getName()+"]");

		return substituteElements;
	}

	/**
	 * returns the substitutiongroup head element, 
	 * if this element is in a substitutiongroup.
	 *
	 * @return the substitutiongroup head or null.
	 */
	private ElementDecl getSubstitutionHead() {
		String substitutionGroup = element.getSubstitutionGroup();

		if ( substitutionGroup != null) { //	 && isAbstract()) {
			Schema s = element.getSchema();
			Enumeration enumeration = s.getGlobalElements();
		
			while( enumeration.hasMoreElements()) {
				ElementDecl e = (ElementDecl)enumeration.nextElement();
				
				if ( e.getName().equals( substitutionGroup)) {
					while ( e.isReference()) {
						e = e.getReference();
					}
					
					if ( e.isAbstract()) {
						return e;
					}
				}
			}
		}

		return null;
	}

	/**
	 * returns A list of of elements that refer to this element.
	 *
	 * @return the list of referers.
	 */
	public Vector getReferers() {
		return schema.getReferers( this);
	}

	/**
	 * Gets the elements parent element.
	 *
	 * @return the parent element.
	 */
	public SchemaElement getParentElement() {
		SchemaParticle parent = getParent();
		
		while ( parent != null && !(parent instanceof SchemaElement)) {
			parent = parent.getParent();
		}
	
		return (SchemaElement)parent;
	}

	/**
	 * Gets the elements parent element.
	 *
	 * @return the parent element.
	 */
	public Vector getChildElements() {
//		System.out.println( ">>> SchemaElement.getChildElements() ["+getName()+"]");
	
		if ( childElements == null) {
			childElements = new Vector();

			Vector models = getModels();
			
			if ( models != null) {
				for( int i = 0; i < models.size(); i++) {
					SchemaModel model = (SchemaModel)models.elementAt(i);
					Vector elements = model.getChildElements();
					
					for( int j = 0; j < elements.size(); j++) {
						SchemaElement element = (SchemaElement)elements.elementAt(j);
						
						addChildElement( element);
					}
				}
			}

		}
			
		return childElements;
	}

	private void addChildElement( SchemaElement element) {
	
		for ( int i = 0; i < childElements.size(); i++) {
			SchemaElement child = (SchemaElement)childElements.elementAt(i);
			
			if ( child.getUniversalName().equals( element.getUniversalName())) {
				return;
			}
		}
		
		childElements.addElement( element);
	}

	/**
	 * Gets the child (any) attribute wildcard, null 
	 * if not found.
	 *
	 * @return the child (any) attribute wildcard.
	 */
	public AnySchemaAttribute getAnyAttribute() {
		if ( type instanceof ComplexSchemaType) {
			return ((ComplexSchemaType)type).getAnyAttribute();
		}

		return null;
	}

	/**
	 * Gets the list of child content models,
	 * returns null when not a complex type.
	 *
	 * @return the list of child content models.
	 */
	public Vector getModels() {
		if ( type instanceof ComplexSchemaType) {
			return ((ComplexSchemaType)type).getModels();
		}

		return null;
	}

	/*
	 * true when element is a reference that has not been resolved yet.
	 *
	 * @return true when the reference has not been resolved.
	 */
	public boolean isReference() {
		return element.isReference();
	}

	/**
	 * Resolves the reference.
	 */
	public void resolveReference() {
		if ( isReference()) {
			
			while ( element.isReference()) {
				element = element.getReference();
			}

			setStructure( element);
			parse();
		}
	}

	/**
	 * Recurse this element.
	 */
	public void recurse() {
		if ( !recursed) {
			parse();
		}
	}
	
	public String getParentName() {
		SchemaElement parent = getParentElement();
		
		if ( parent != null) {
			return parent.getName();
		}
		
		return null;
	}

	/**
	 * Gets the list of child attributes
	 *
	 * @return the list of child attributes.
	 */
	public Vector getAttributes() {
		Vector attributes = null;

		if ( type instanceof ComplexSchemaType) {
			attributes = ((ComplexSchemaType)type).getAttributes();
		}

		return attributes;
	}
	
	/**
	 * Gets the annotations for this element as a String...
	 *
	 * @return the list of child attributes.
	 */
	public String getAnnotations() {
//		StringBuffer result = new StringBuffer();
//		Enumeration annotations = element.getAnnotations();
//		
//		while ( annotations.hasMoreElements()) {
//			Annotation annotation = (Annotation)annotations.nextElement();
//			Enumeration documentations = annotation.getDocumentation();
//			
//			while( documentations.hasMoreElements()) {
//				Documentation documentation = (Documentation)documentations.nextElement();
//				
//				result.append( "\n");
//				result.append( documentation.getContent());
//			}
//		}
//	
//		if ( type != null) {
//			annotations = type.getType().getAnnotations();
//			
//			while ( annotations.hasMoreElements()) {
//				Annotation annotation = (Annotation)annotations.nextElement();
//				Enumeration documentations = annotation.getDocumentation();
//				
//				while( documentations.hasMoreElements()) {
//					Documentation documentation = (Documentation)documentations.nextElement();
//					
//					result.append( "\n");
//					result.append( documentation.getContent());
//				}
//			}
//		}
//			
//
		return null;
	}

	private void parse() {
//		System.out.println("SchemaElement.parse() ["+element.getName()+"]");

		XMLType t = element.getType();
		
		if ( t == null && element.getSubstitutionGroup() != null) {
			ElementDecl es = getSubstitutionHead();
			
			if ( es != null) {
				t = es.getType();
			}
		}

		if ( t instanceof ComplexType) {
			type = new ComplexSchemaType( this, (ComplexType)t);
		} else if ( t instanceof SimpleType) {
			type = new SimpleSchemaType( (SimpleType)t);
		} else {
			type = new AnySchemaType( (AnyType)t);
		}

		recursed = true;
	}
	
	/*
	 * return the global schema.
	 */
	public Schema getSchema() {
		return element.getSchema();
	}

	/*
	 * return the global schema.
	 */
	XMLSchema getBaseSchema() {
		return schema;
	}

	public boolean isGlobal() {
		Schema s = element.getSchema();
	
		Enumeration enumeration = s.getGlobalElements();

		while( enumeration.hasMoreElements()) {
			ElementDecl e = (ElementDecl)enumeration.nextElement();
			if ( e.equals( element)) {
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * return the element represented by this node.
	 */
	public ElementDecl getElement() {
		return element;
	}

	String toString( String indent) {
		String result = indent+toString()+"\n";
		String newIndent = indent+"  ";
		
		Vector attributes = getAttributes();
		if ( attributes != null) {
			for ( int i = 0; i < attributes.size(); i++) {
				result = result+((SchemaAttribute)attributes.elementAt(i)).toString( newIndent);
			}
		}

		Vector models = getModels();
		if ( models != null) {
			for ( int i = 0; i < models.size(); i++) {
				result = result+((SchemaModel)models.elementAt(i)).toString( newIndent);
			}
		}
		
		return result;
	}
	
	public String toString() {
		return super.toString()+" (E) "+element.getName();
	}
	
	public boolean equals( Object object) {

		if ( object instanceof SchemaElement) {
			 if ( getElement( ((SchemaElement)object).getElement() ) == getElement( this.element) ) {
				return true;
			 }
		}
		
		return false;
	}
	
	private static ElementDecl getElement( ElementDecl element) {
		ElementDecl newElement = element;

		while ( newElement.isReference()) {
			newElement = newElement.getReference();
		}
		
		return newElement;
	}
	
	public Vector getAnyElements() {
		if ( anyElements == null) {
			anyElements = new Vector();

			Vector models = getModels();
			
			if ( models != null) {
				for( int i = 0; i < models.size(); i++) {
					SchemaModel model = (SchemaModel)models.elementAt(i);
					Vector elements = model.getAnyElements();
					
					for( int j = 0; j < elements.size(); j++) {
						addAnyElement( (AnySchemaElement)elements.elementAt(j));
					}
				}
			}
		}
		
		return anyElements;
	}

	private void addAnyElement( AnySchemaElement element) {
//		for ( int i = 0; i < anyElements.size(); i++) {
//			AnySchemaElement child = (AnySchemaElement)anyElements.elementAt(i);
//			
//			if ( child.getNamespaces().equals( element.getUniversalName())) {
//				return;
//			}
//		}
		
		anyElements.addElement( element);
	}
}