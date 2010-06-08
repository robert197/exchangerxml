/*
 * Created on 25-Aug-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.schema.generate;

import java.util.Enumeration;
import java.util.Hashtable;

import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.ModelGroup;
//import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.Structure;
import org.exolab.castor.xml.schema.XMLType;
/**
 * @author gmcgoldrick
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SchemaUtilities {

  
  
	public static Hashtable getAttributesFromType(ComplexType ct)
	{
	  Hashtable attributes = null;
	  XMLType baseType = ct.getBaseType();

	  if (baseType != null && baseType instanceof ComplexType && !ct.isRestricted())
	  {
	    attributes = getAttributesFromType((ComplexType)baseType);
	    
	  }

		Enumeration enumeration = ct.getAttributeDecls();

		while ( enumeration.hasMoreElements()) {
			AttributeDecl attr = (AttributeDecl)enumeration.nextElement();
			
			while ( attr.isReference()) {
				attr = attr.getReference();
			}
			
			if (attributes == null)
			  attributes = new Hashtable();
			
			attributes.put( attr.getName(), attr);
		}
	
		
		

	  return attributes;
	}
	

	public static Hashtable getElementsFromGroup(Group group, Hashtable elements)
	{
	  
		if ( group.getStructureType() == Structure.MODELGROUP) {
			ModelGroup m = (ModelGroup)group;
		
			//TODO!!!

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
					while (element.isReference())
					  element = element.getReference();

					elements.put(element.getName(), element);
					break;

				case Structure.MODELGROUP:
					ModelGroup modelGroup = (ModelGroup)struct;
					getElementsFromModelGroup( modelGroup, elements);

					
					//parse( m);
					
					break;

				case Structure.GROUP:
					Group g = (Group)struct;
					
				getElementsFromGroup( g, elements);
				
					//addModel( model);
					break;
				
				case Structure.WILDCARD:
					//Wildcard wildcard = (Wildcard)struct;

					//if ( !wildcard.isAttributeWildcard()) {
					//	addWildcard( new AnySchemaElement( this, wildcard));
					//} else {
					//	System.err.println("SchemaModel.parse() [ERROR: Wildcard Attribute in Content Model!]");
					//}
					break;

				default:
					//System.err.println( "SchemaModel.parse() [ERROR: Unknown Structure Type = "+struct.getStructureType()+"]");
					break;
			}
		}
	  
	  
	  return elements;
	}
	
	public static Hashtable getElementsFromModelGroup(ModelGroup modelGroup, Hashtable elements)
	{
		while ( modelGroup != null && modelGroup.hasReference()) {
			modelGroup = modelGroup.getReference();
		}

		Enumeration enum2 = modelGroup.enumerate();
		if ( enum2.hasMoreElements()) {
			Structure struct2 = (Structure)enum2.nextElement();

			switch ( struct2.getStructureType()) {
				case Structure.GROUP:
					Group group2 = (Group)struct2;
					//group2.setMaxOccurs( maxOccurs);
					//group2.setMinOccurs( minOccurs);

				elements =  getElementsFromGroup(group2, elements);
				break;
					
				default:
					//System.err.println( "SchemaModel.parse() [ERROR] Unknown Structure Type = "+struct.getStructureType());
					break;
			}
		} else {
			//System.err.println( "Could not find Structure for "+modelGroup.getReferenceId()+" ["+element.getName()+"!");
		}
	  
	  
	  return elements;
	}
	

	public static boolean isEmptyElement(ElementDecl element)
	{
	  XMLType t = element.getType();
	  
	  if (t.isComplexType())
	  {
	    ComplexType ct = (ComplexType)t;
	    
	    if (ct.isSimpleContent() ||  SchemaUtilities.hasElements(ct))
	      return false;
	    else
	      return true;	    	    
	  }
	  else
	    return false;
	}
	
	public static boolean hasElements( ComplexType ct)
	{
	  
	    Hashtable elements = getElementsFromComplexType( ct);
	    Enumeration enumeration = null;
	    if ( (elements != null) && (enumeration = elements.elements())!= null && enumeration.hasMoreElements())
	      return true;
	    else return false;
	}

	public static Hashtable getElementsFromComplexType( ComplexType ct)
	{
	  Hashtable elements = new Hashtable();
	  XMLType baseType = ct.getBaseType();

	  if (baseType != null && baseType instanceof ComplexType && !ct.isRestricted())
	  {
	    elements = getElementsFromComplexType((ComplexType)baseType);
	    
	  }

	  Enumeration enumeration = ct.enumerate();

		while ( enumeration.hasMoreElements()) {
			Structure struct = (Structure)enumeration.nextElement();

			switch ( struct.getStructureType()) {
				case Structure.GROUP:
					Group group = (Group)struct;
					getElementsFromGroup( group, elements);
					break;

				case Structure.MODELGROUP:
					//addModel( getModel( (ModelGroup)struct));

				  	ModelGroup modelGroup = (ModelGroup)struct;
					getElementsFromModelGroup( modelGroup, elements);

					break;
			}
		}
		
		

	  return elements;
	}
	

	
}
