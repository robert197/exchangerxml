/*
 * Created on 13-Dec-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.schema.generate;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;

import com.cladonia.schema.XMLSchema;
import com.cladonia.schema.SchemaElement;
//import com.cladonia.schema.SchemaModel;


import com.cladonia.xml.XElement;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.ExchangerDocument;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.XMLType;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.Structure;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.Order;
import org.exolab.castor.xml.schema.ModelGroup;
import org.exolab.castor.xml.schema.Wildcard;

import java.util.Enumeration;
import org.exolab.castor.xml.schema.Form;
import org.exolab.castor.xml.schema.Schema;
import org.dom4j.QName;
import org.exolab.castor.xml.schema.SimpleType;
import java.util.Stack;

import java.awt.FontMetrics;

//import com.cladonia.schema.SchemaElement;
/**
 * @author gmcgoldrick
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class GraphicalSchemaGenerator
{
  XMLSchema xmlSchema = null;
  private Schema schema = null;
  Document document = null;
  Element root = null;
  Random random = new Random();
  int nested = 0;
  HashMap map = new HashMap();
  boolean optionalAttributes = false;
  boolean optionalElements = false;
  boolean generateData = true;
  Stack elementStack = new Stack();
  private static int oid = 0;
  private boolean DEBUG = false;
  private boolean DEBUG1 = true;
  String SVG_URI = "http://www.w3.org/2000/svg";

  
  Vector namespaces = null;
 
	private Hashtable globalElements = null;
	private Hashtable substitutes = null;
 
  private FontMetrics fontMetrics = null;
  
   private String rootName = "";
   private String rootNamespace = "";
   private XElement rootTreeNode = null;
   
   private String UI_PREFIX = "ui";
   private String UI_URI = "http://www.w3.org/2002/01/ui";
   
   private String UI_TREE = "Tree";
   private String UI_TREE_NODE = "TreeNode";
   private String UI_USER_OBJECT = "userObject";
   private String UI_XSD_ELEMENT = "xsdElement";
   private String UI_XSD_ATTRIBUTE = "xsdAttribute";
   private String UI_XSD_COMPLEX_TYPE = "xsdComplexType";
   private String UI_XSD_SEQUENCE = "xsdSequence";
   private String UI_XSD_CHOICE = "xsdChoice";
   private String UI_XSD_ALL = "xsdAll";
   private String UI_XSD_ANY = "xsdAny";

   private String UI_TITLE = "title";
   private String UI_NAME = "name";
   private String UI_NAMEW = "namew";
   private String UI_OID = "oid";
   private String UI_OIDREF = "oidref";
   private String UI_TYPE = "type";
  
   
   private QName qnameTree = null;
   private QName qnameTreeNode = null;
   private QName qnameXSDElement = null;
   private QName qnameXSDAttribute = null;
   private QName qnameUserObject = null;
   private QName qnameXSDComplexType = null;
   private QName qnameXSDSequence = null;
   private QName qnameXSDChoice = null;
   private QName qnameXSDAll = null;
   private QName qnameXSDAny = null;

   private QName qnameTitle = null;
   private QName qnameName = null;
   private QName qnameNamew = null;
   private QName qnameOID = null;
   private QName qnameOIDRef = null;
   private QName qnameType = null;

	private Hashtable globalElementDecls = null;
	private Hashtable globalComplexTypeDecls = null;
	private Hashtable globalSimpleTypeDecls = null;

        private Hashtable importedSchemasFirstPass = null;
       private Hashtable importedSchemas = null;

       private Hashtable includedSchemasFirstPass = null;
       private Hashtable includedSchemas = null;

  public static void main(String[] args)
  {
  }
  
  public GraphicalSchemaGenerator(FontMetrics fontMetrics)
  {
    //System.out.println("Creating GraphicalSchemaGenerator");
    this.fontMetrics = fontMetrics;
  }
 /* 
  public String generateInstanceFromSchemaURL(URL file, String root,
      boolean optionalAttributes, boolean optionalElements, boolean generateData)
  {
    
    String xml = processXSD(file, root, optionalAttributes, optionalElements,
        generateData);
    
    return xml;
    
  }
  */
  //public String processXSD(String url, String rootElement)
  //{
    
    //System.out.println("in processXSD GraphicalSchemaGenerator: " + url);
    
  //  return "<xml>test</xml>";
  //}
  
  public String processXSD(ExchangerDocument schemaDocument,
      XMLSchema schema,
      SchemaElement schemaRoot, boolean optionalAttributes,
      boolean optionalElements, boolean generateData)
  {
    
    this.xmlSchema = schema;
    this.schema = xmlSchema.getSchema();    
    //System.out.println("in processXSD GraphicalSchemaGenerator: ");
    this.optionalAttributes = optionalAttributes;
    this.optionalElements = optionalElements;
    this.generateData = generateData;
    
  
    namespaces = schemaDocument.getDeclaredNamespaces();
    
    //schema.setFontMetrics(fontMetrics);
    //schema.setPrefixes(namespaces);
    //schema.setRootName(schemaRoot.getName());
    //schema.setRootNamespace(schemaRoot.getNamespace());

    
    setFontMetrics(fontMetrics);
    setPrefixes(namespaces);
    setRootName(schemaRoot.getName());
    setRootNamespace(schemaRoot.getNamespace());

    
    /*
     * try { document = getDocument(); } catch (Exception e) {
     * System.err.println( "getDom: " + e ); }
     */
    
    
    ElementDecl elDecl = schemaRoot.getElement();
 
    ExchangerDocument document = null;
  
    QName svgqname = null;

    //<svg xmlns="http://www.w3.org/2000/svg"
    //  xmlns:xlink="http://www.w3.org/1999/xlink"
    //  xmlns:ui="http://www.w3.org/2002/01/ui" 
    //  xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
    //  onload="init()">
    
    //root.addNamespace(UI_PREFIX, UI_URI);

    Namespace svgNS  =  new Namespace("", SVG_URI);
    try
    {
      svgqname = new QName("svg", svgNS);
    }
    catch (StringIndexOutOfBoundsException e1)
    {
      //cannot parse string
      svgqname = new QName("svg");
    }
   

    root = new XElement(svgqname);
    
    try
    {
      document = new ExchangerDocument((XElement) root);
    }
    catch (Exception ex)
    {
      return "";
    }
    
    root = document.getRoot();

    root.addNamespace("", SVG_URI);
    //System.out.println("URI: " + targetNamespace);
    
    root.addNamespace("xlink", "http://www.w3.org/1999/xlink");
    root.addNamespace(UI_PREFIX, UI_URI);
    root.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
    QName qname = new QName("onload");
    XAttribute newNode = new XAttribute(qname, "init()" );
    
    root.add(newNode);

   
    QName defs = null;
    try
    {
      defs = new QName("defs", svgNS);
    }
    catch (StringIndexOutOfBoundsException e1)
    {
      //cannot parse string
      defs = new QName("defs");
    }

	XElement defsNode = new XElement(defs);
	root.add(defsNode);
	
	

    QName script = null;
    try
    {
      script = new QName("script", svgNS);
    }
    catch (StringIndexOutOfBoundsException e1)
    {
      //cannot parse string
      script = new QName("script");
    }
	
	XElement scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	
    Namespace xlinkNS  =  new Namespace("xlink", "http://www.w3.org/1999/xlink");
    QName href = null;
    try
    {
      href = new QName("href", xlinkNS );
    }
    catch (StringIndexOutOfBoundsException e1)
    {
      //cannot parse string
      href = new QName("href");
    }

	
	XAttribute attrNode = new XAttribute(href, "xsd-min.js" );    
	scriptNode.add(attrNode);
	
	
	scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	attrNode = new XAttribute(href, "Page.js" );    
	scriptNode.add(attrNode);
	
	
	scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	attrNode = new XAttribute(href, "Tree.js" );    
	scriptNode.add(attrNode);
	
	scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	attrNode = new XAttribute(href, "TreeNode.js" );    
	scriptNode.add(attrNode);
	
	scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	attrNode = new XAttribute(href, "Box.js" );    
	scriptNode.add(attrNode);
	
	scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	attrNode = new XAttribute(href, "BoundingBox.js" );    
	scriptNode.add(attrNode);
	
	scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	attrNode = new XAttribute(href, "XSDSequence.js" );    
	scriptNode.add(attrNode);

	scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	attrNode = new XAttribute(href, "XSDChoice.js" );    
	scriptNode.add(attrNode);
	
	scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	attrNode = new XAttribute(href, "XSDAll.js" );    
	scriptNode.add(attrNode);
	
	scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	attrNode = new XAttribute(href, "XSDElement.js" );    
	scriptNode.add(attrNode);
	
	scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	attrNode = new XAttribute(href, "XSDAny.js" );    
	scriptNode.add(attrNode);
	
	scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	attrNode = new XAttribute(href, "XSDComplexType.js" );    
	scriptNode.add(attrNode);
    
	scriptNode = new XElement(script);
	defsNode.add(scriptNode);
	attrNode = new XAttribute(href, "XSDSimple.js" );    
	scriptNode.add(attrNode);
	

    //<defs>
    //<script  xlink:href="xsd-min.js"/>
    //<script  xlink:href="Page.js"/>
    //<script  xlink:href="Tree.js"/>
    //<script  xlink:href="TreeNode.js"/>
    //<script  xlink:href="Box.js"/>
    //<script  xlink:href="BoundingBox.js"/>
    //<script  xlink:href="XSDSequence.js"/>
    //<script  xlink:href="XSDChoice.js"/>
    //<script  xlink:href="XSDAll.js"/>
    //<script  xlink:href="XSDElement.js"/>
    //<script  xlink:href="XSDAny.js"/>
    //<script  xlink:href="XSDComplexType.js"/>

    QName title = null;
    try
    {
      title = new QName("title", svgNS );
    }
    catch (StringIndexOutOfBoundsException e1)
    {
      //cannot parse string
      title = new QName("title");
    }
    
	XElement titleNode = new XElement(title);
	root.add(titleNode);

	titleNode.addText("SVG Schema");
	
    QName g = null;
    try
    {
      g = new QName("g", svgNS );
    }
    catch (StringIndexOutOfBoundsException e1)
    {
      //cannot parse string
      g = new QName("g");
    }
  
    root.addText("\n");

	XElement gNode = new XElement(g);
	root.add(gNode);

	QName transform = new QName("transform");

	attrNode = new XAttribute(transform, "translate(50,50)" );    
	gNode.add(attrNode);

	commonInit();

    //schema.generateIntermediateFile(gNode);
    generateIntermediateFile(gNode);
  
    document.update();
    
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + document.getText();
   
  }
  
  public String encodeURI(String url1)
  {
    String url2 = "";
    for (int i=0; i<url1.length(); i++)
    {
      if (url1.charAt(i) == ' ')
        url2 += "%20";
      else
        url2 += url1.charAt(i);
      
    }
    
    return url2;
  }
  
  
	public void commonInit()
	{
	  
	  
	   //private QName qnameTree = null;
	   //private QName qnameTreeNode = null;
	   //private QName qnameXSDElement = null;
	   //private QName qnameXSDAttribute = null;
	   //private QName qnameUserObject = null;
	   //private QName qnameXSDComplexType = null;

	
	   //private QName qnameTitle = null;
	   //private QName qnameOID = null;
	   //private QName qnameType = null;
	   //private QName qnameName = null;
	   //private QName qnameNamew = null;

	    Namespace xNS  =  new Namespace(UI_PREFIX, UI_URI);
	    try
	    {
	       qnameTree = new QName(UI_TREE, xNS);
	       qnameTreeNode = new QName(UI_TREE_NODE, xNS);
	       qnameXSDElement = new QName(UI_XSD_ELEMENT, xNS);
	       qnameXSDAttribute = new QName(UI_XSD_ATTRIBUTE, xNS);
	       qnameUserObject = new QName(UI_USER_OBJECT, xNS);
	       qnameXSDComplexType = new QName(UI_XSD_COMPLEX_TYPE, xNS);
	       qnameXSDSequence = new QName(UI_XSD_SEQUENCE, xNS);
	       qnameXSDChoice = new QName(UI_XSD_CHOICE, xNS);
	       qnameXSDAll = new QName(UI_XSD_ALL, xNS);
	       qnameXSDAny = new QName(UI_XSD_ANY, xNS);
	       
	    }
	    catch (StringIndexOutOfBoundsException e1)
	    {
	      //cannot parse string
	      qnameTree = new QName(UI_TREE);
	      qnameTreeNode = new QName(UI_TREE_NODE);
	       qnameXSDElement = new QName(UI_XSD_ELEMENT);
	       qnameXSDAttribute = new QName(UI_XSD_ATTRIBUTE);
	       qnameUserObject = new QName(UI_USER_OBJECT);
	       qnameXSDComplexType = new QName(UI_XSD_COMPLEX_TYPE);
	       qnameXSDSequence = new QName(UI_XSD_SEQUENCE);
	       qnameXSDChoice = new QName(UI_XSD_CHOICE);
	       qnameXSDAll = new QName(UI_XSD_ALL);
	       qnameXSDAny = new QName(UI_XSD_ANY);
	    }
	   
		qnameTitle = new QName(UI_TITLE);
		qnameOID = new QName(UI_OID);
		qnameOIDRef = new QName(UI_OIDREF);
		qnameName = new QName(UI_NAME);
		qnameNamew = new QName(UI_NAMEW);
		qnameType = new QName(UI_TYPE);

	  
		globalElementDecls = new Hashtable();
		globalComplexTypeDecls = new Hashtable();
		globalSimpleTypeDecls = new Hashtable();

	

        importedSchemasFirstPass = new Hashtable();
		importedSchemas = new Hashtable();

        includedSchemasFirstPass = new Hashtable();
        includedSchemas = new Hashtable();

		
		
		globalElements = new Hashtable();
		substitutes = new Hashtable();

		
		Enumeration enumeration = schema.getGlobalElements();

		while( enumeration.hasMoreElements()) {
			ElementDecl e = (ElementDecl)enumeration.nextElement();
			
			//SchemaElement element = new SchemaElement( this.xmlSchema, null, e);
			globalElements.put( getUniversalName(e), e);
			
			
		}
	}
	
	
	public void generateIntermediateFile(Element parent)
	{
	  //System.out.println("<ui:Tree xmlns:ui=\"www.exchangerxml.com\">");
	
	  XElement currNode = new XElement(qnameTree);
	  
	  parent.add(currNode);

	  
	  processSchemaFirstPass(schema);
      processElementsFirstPass(schema);

	  processSchema(schema, currNode);
	  processElements(schema, currNode);
	  
	  
	  if (rootTreeNode != null)
	    currNode.add(rootTreeNode);
	  
	  //System.out.println("</ui:Tree>"); 
	}


	public void processSchemaFirstPass(Schema curSchema)
	{
/*
		for ( int i = 0; i < nss.size(); i++) {
			Namespace ns = (Namespace)nss.get( i);
			
			if ( !prefixes.contains( ns) && ns.getURI() != null && ns.getURI().trim().length() > 0) { //&& ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
				prefixes.addElement( ns);
			}
		}
*/
	  
	    //Vector namespaces = curSchema.getDeclaredNamespaces();
	  
	  /*
	  Hashtable nsTable = curSchema.getNamespaces(); 

	  
	  Enumeration en = nsTable.elements();

	  
	  while (en.hasMoreElements())
	  {
	    
	    Namespace ns = (Namespace)en.nextElement();
	    
	    
	    
	  }*/
	  
	  
	  String namespace = curSchema.getTargetNamespace();
	  
//System.out.println("targetNamespace: " + namespace);
if (DEBUG)	
System.out.println("uri: " + curSchema.getSchemaLocation());
	  
	  //Enumeration en = ns.getLocalNamespacePrefixes(); 

	  processIncludedSchemasFirstPass(curSchema);


	  processImportedSchemasFirstPass(curSchema);
	  
	  processComplexTypesFirstPass(curSchema);

	  processSimpleTypesFirstPass(curSchema);


	}
        
        
        
        
	public void processSchema(Schema curSchema, Element root)
	{

	  processIncludedSchemas(curSchema, root);

	  processImportedSchemas(curSchema, root);
	  
	  processComplexTypes(curSchema, root);

	  processSimpleTypes(curSchema, root);


	}
	
	public void processIncludedSchemasFirstPass(Schema curSchema)
	{
	 
		Enumeration includes = curSchema.getIncludedSchemas( );
		while( includes.hasMoreElements()) 
                {
		  Schema includedSchema = (Schema)includes.nextElement();
		  
                  String location = includedSchema.getSchemaLocation();
if (DEBUG)
System.out.println("****" + location);
                  
                  Schema stored = (Schema)includedSchemasFirstPass.get(location);
                  if ( stored == null)
                  {
                    includedSchemasFirstPass.put( location, includedSchema);
                    
                    //TODO: check for cycles here
                    //System.out.println("****" + location);
                    processSchemaFirstPass(includedSchema);

                  }
                  //else
                  //  System.out.println("**** alredy processed" + location);  
                  
		}

	}
	

	
	public void processImportedSchemasFirstPass(Schema curSchema)
	{
	  	
		Enumeration imports = curSchema.getImportedSchema();
		while( imports.hasMoreElements()) 
                {
		  Schema importedSchema = (Schema)imports.nextElement();
		  
                  String location = importedSchema.getSchemaLocation();
if (DEBUG)
System.out.println("****" + location);
                  
                  Schema stored = (Schema)importedSchemasFirstPass.get(location);
                  if ( stored == null)
                  {
                    importedSchemasFirstPass.put( location, importedSchema);
                    
                    //TODO: check for cycles here
                    //System.out.println("****" + location);
                    processSchemaFirstPass(importedSchema);

                  }
                  //else
                  //  System.out.println("**** alredy processed" + location);  
                  
		}

	}
	
	public void processIncludedSchemas(Schema curSchema, Element root)
	{
		Enumeration includes = curSchema.getIncludedSchemas();
		while( includes.hasMoreElements()) 
                {
		  Schema includedSchema = (Schema)includes.nextElement();
		  
                  String location = includedSchema.getSchemaLocation();
                  //System.out.println("****" + location);
                  
                  Schema stored = (Schema)includedSchemas.get(location);
                  if ( stored == null)
                  {
                    includedSchemas.put( location, includedSchema);
                    
                    //TODO: check for cycles here
                    //System.out.println("****" + location);
                    processSchema(includedSchema, root);

                  }
                  //else
                  //  System.out.println("**** alredy processed" + location);  
                  
		}

	}
	
	
	public void processImportedSchemas(Schema curSchema, Element root)
	{
		Enumeration imports = curSchema.getImportedSchema();
		while( imports.hasMoreElements()) 
                {
		  Schema importedSchema = (Schema)imports.nextElement();
		  
                  String location = importedSchema.getSchemaLocation();
                  //System.out.println("****" + location);
                  
                  Schema stored = (Schema)importedSchemas.get(location);
                  if ( stored == null)
                  {
                    importedSchemas.put( location, importedSchema);
                    
                    //TODO: check for cycles here
                    //System.out.println("****" + location);
                    processSchema(importedSchema, root);

                  }
                  //else
                  //  System.out.println("**** alredy processed" + location);  
                  
		}

	}
	
	public void processElementsFirstPass(Schema curSchema)
	{
 		Enumeration enumEls = curSchema.getGlobalElements();
		while( enumEls.hasMoreElements()) {
			ElementDecl e = (ElementDecl)enumEls.nextElement();

			//String elementOID = XMLSchema.generateElementOID();
			String elementOID = generateElementOID();
			
			globalElementDecls.put( e, elementOID);
if (DEBUG)
{
System.out.println("EL name: " + e.getName() + "  OID: " + elementOID + " uri:" + e.getSchema().getSchemaLocation() + " baseuri: " );
System.out.println(e);
}
		}
		
       }	
	public void processElements(Schema curSchema, Element parent)
	{
	  

		Enumeration enumEls = curSchema.getGlobalElements();
		while( enumEls.hasMoreElements()) {
			ElementDecl e = (ElementDecl)enumEls.nextElement();
			
			processGlobalElement(curSchema, e, parent);

		}

	}


	public void processSimpleTypesFirstPass(Schema curSchema)
	{
		Enumeration enumST = curSchema.getSimpleTypes();
		while( enumST.hasMoreElements()) {
			SimpleType st = (SimpleType)enumST.nextElement();

			//String stOID = XMLSchema.generateSimpleTypeOID();
			String stOID = generateSimpleTypeOID();
			
			globalSimpleTypeDecls.put( st, stOID);

			//System.out.println("ST name: " + st.getName() + "  OID: " + stOID);
		}

	  
	}

        
        public void processSimpleTypes(Schema curSchema, Element parent)
	{

            
            
            
        }
	public void processComplexTypesFirstPass(Schema curSchema)
	{
		Enumeration enumCT = curSchema.getComplexTypes();
		while( enumCT.hasMoreElements()) {
		  ComplexType ct = (ComplexType)enumCT.nextElement();

			//String ctOID = XMLSchema.generateComplexTypeOID();
			String ctOID = generateComplexTypeOID();
			
			globalComplexTypeDecls.put( ct, ctOID);

			//System.out.println("CT name: " + ct.getName() + "  OID: " +ctOID);

		}
  
        }

	public void processComplexTypes(Schema curSchema, Element root)
	{
	  
	  
	  
		Enumeration enumCT = curSchema.getComplexTypes();
		while( enumCT.hasMoreElements()) {
		  ComplexType ct = (ComplexType)enumCT.nextElement();


		  processComplexType(curSchema, ct, root);
		  
		}

	}

	public Hashtable getAttributesFromType(Schema curSchema, ComplexType ct)
	{
	  Hashtable attributes = null;
	  XMLType baseType = ct.getBaseType();

	  if (baseType != null && baseType instanceof ComplexType && !ct.isRestricted())
	  {
	    attributes = getAttributesFromType(curSchema, (ComplexType)baseType);
	    
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
	

	public Hashtable getElementsFromGroup(Schema curSchema, Group group, Hashtable elements)
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
					getElementsFromModelGroup(curSchema, modelGroup, elements);

					
					//parse( m);
					
					break;

				case Structure.GROUP:
					Group g = (Group)struct;
					
				getElementsFromGroup(curSchema, g, elements);
				
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
	
	public Hashtable getElementsFromModelGroup(Schema curSchema, ModelGroup modelGroup, Hashtable elements)
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

				elements =  getElementsFromGroup(curSchema, group2, elements);
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
	
	
	public boolean hasElements(Schema curSchema, ComplexType ct)
	{
	  
	    Hashtable elements = getElementsFromComplexType(curSchema, ct);
	    Enumeration enumeration = null;
	    if ( (elements != null) && (enumeration = elements.elements())!= null && enumeration.hasMoreElements())
	      return true;
	    else return false;
	}

	public Hashtable getElementsFromComplexType(Schema curSchema, ComplexType ct)
	{
	  Hashtable elements = new Hashtable();
	  XMLType baseType = ct.getBaseType();

	  if (baseType != null && baseType instanceof ComplexType && !ct.isRestricted())
	  {
	    elements = getElementsFromComplexType(curSchema, (ComplexType)baseType);
	    
	  }

	  Enumeration enumeration = ct.enumerate();

		while ( enumeration.hasMoreElements()) {
			Structure struct = (Structure)enumeration.nextElement();

			switch ( struct.getStructureType()) {
				case Structure.GROUP:
					Group group = (Group)struct;
					getElementsFromGroup(curSchema, group, elements);
					break;

				case Structure.MODELGROUP:
					//addModel( getModel( (ModelGroup)struct));

				  	ModelGroup modelGroup = (ModelGroup)struct;
					getElementsFromModelGroup(curSchema, modelGroup, elements);

					break;
			}
		}
		
		

	  return elements;
	}
	

	
	
	
	
	
	
	public void processComplexType(Schema curSchema, ComplexType ct, Element parent)
	{
	  
if (DEBUG1)
  System.out.println("processComplexType()" +  ct.getName());
	  String ctOID = (String)globalComplexTypeDecls.get(ct);
	  
	  if (ctOID == null || ctOID.equals(""))
	    ctOID = generateComplexTypeOID();
	  
	  String ctName = ct.getName();
	  
	  if (ctName == null || ctName.equals(""))
	    ctName = "Anonymous";
	  
	  /*
	  if (ct.isTopLevel())
	  {
		  System.out.print("<ui:TreeNode ");
		  System.out.print("title=\"ComplexType\" oid=\"");
		  System.out.print(XMLSchema.generateComplexTypeOID());
		  System.out.println("\" ");
		  System.out.print("oidref=\"");
		  System.out.print(ctOID);
		  
		  System.out.print("\" />");
		  return;
	  }
	  */
	  
	  XMLType baseType = ct.getBaseType();

	  //ComplexSchemaType cst = new ComplexSchemaType( null, ct);

	  

	  
	  //System.out.print("<ui:userObject><ui:xsdComplexType name=\"");

if (DEBUG)
{  
if (ct.getName() !=null)
  System.out.println(ct.getName() + " CT");
else
  System.out.println("Anonymous CT");
}
 //System.out.print("\" type=\"");
	  //System.out.print("all");//System.out.print(order);
	  //System.out.print("\"/></ui:userObject>");

	  
	
	  if (ct.isSimpleContent() || hasElements(curSchema, ct) == false)
	  {
		  XElement currNode = new XElement(qnameTreeNode);
		  
		  XAttribute attrNode = new XAttribute(qnameTitle, "ComplexType" );    
		  currNode.add(attrNode);
		  attrNode = new XAttribute(qnameOID, ctOID);    
		  currNode.add(attrNode);
		  
		  parent.add(currNode);
		  
		  XElement userObjectNode = new XElement(qnameUserObject);
		  currNode.add(userObjectNode);

		  XElement xsdComplexTypeNode = new XElement(qnameXSDComplexType);
		  attrNode = new XAttribute(qnameName, ctName );    
		  xsdComplexTypeNode.add(attrNode);
		  
		  
		  userObjectNode.add(xsdComplexTypeNode);
		  
		  attrNode = new XAttribute(qnameType, "simple" );    
		  xsdComplexTypeNode.add(attrNode);

		  return;
	  }
	
	  if (baseType != null && baseType instanceof ComplexType && !ct.isRestricted())
	  {
		  XElement currNode = new XElement(qnameTreeNode);
		  
		  XAttribute attrNode = new XAttribute(qnameTitle, "ComplexType" );    
		  currNode.add(attrNode);
		  attrNode = new XAttribute(qnameOID, ctOID);    
		  currNode.add(attrNode);
		  
		  parent.add(currNode);
		  
		  XElement userObjectNode = new XElement(qnameUserObject);
		  currNode.add(userObjectNode);

		  XElement xsdComplexTypeNode = new XElement(qnameXSDComplexType);
		  attrNode = new XAttribute(qnameName, ctName );    
		  xsdComplexTypeNode.add(attrNode);
		  
		  
		  userObjectNode.add(xsdComplexTypeNode);
		  
		  
		  
	    processComplexType(curSchema, (ComplexType)baseType, currNode);

		  attrNode = new XAttribute(qnameType, "sequence" );    
		  xsdComplexTypeNode.add(attrNode);

	    
		  String order = null;
		  
		  Enumeration enumeration = ct.enumerate();

			while ( enumeration.hasMoreElements()) {
				Structure struct = (Structure)enumeration.nextElement();

				switch ( struct.getStructureType()) {
					case Structure.GROUP:
						Group group = (Group)struct;
						processGroupStructure(curSchema, group, currNode);
						break;

					case Structure.MODELGROUP:
						//addModel( getModel( (ModelGroup)struct));

					  	ModelGroup modelGroup = (ModelGroup)struct;
				    	processModelGroup(curSchema, modelGroup, currNode);

						break;
					
					default:
						System.err.println( "ComplexType Unknown: "+struct.getStructureType());
						break;
				}
			}
		  
	    
	    
	  }
	  else
	  {
		  String order = null;
		  
		  Enumeration enumeration = ct.enumerate();

		  if (enumeration.hasMoreElements() == false)
		    return;
		  
		  XElement currNode = new XElement(qnameTreeNode);
		  
		  XAttribute attrNode = new XAttribute(qnameTitle, "ComplexType" );    
		  currNode.add(attrNode);
		  attrNode = new XAttribute(qnameOID, ctOID);    
		  currNode.add(attrNode);
		  
		  parent.add(currNode);
		  
		  XElement userObjectNode = new XElement(qnameUserObject);
		  currNode.add(userObjectNode);

		  XElement xsdComplexTypeNode = new XElement(qnameXSDComplexType);
		  attrNode = new XAttribute(qnameName, ctName );    
		  xsdComplexTypeNode.add(attrNode);
		  
		  
		  userObjectNode.add(xsdComplexTypeNode);
		  
		  
			while ( enumeration.hasMoreElements()) {
			  
			  
				Structure struct = (Structure)enumeration.nextElement();

				Group group = null;
				
				if (struct.getStructureType() == Structure.MODELGROUP)
				{
if (DEBUG1)
System.out.println("ct: MODELGROUP");

				  	ModelGroup modelGroup = (ModelGroup)struct;
				  
					Enumeration enum2 = modelGroup.enumerate();
					if ( enum2.hasMoreElements()) {
						Structure struct2 = (Structure)enum2.nextElement();
						
						if (struct2.getStructureType() == Structure.GROUP)
						{
if (DEBUG1)
System.out.println("ct: MODELGROUP : GROUP");
						  
						  group = (Group)struct2;
						
						}
						else
						{
							System.err.println( "ComplexType Unknown: "+struct2.getStructureType());
							break;					  
						}
					}
				}
				else
				{
				  
				  group = (Group)struct;
				}
				
						
				if ( group.getOrder() == Order.seq) {
				  order = "sequence";
				  processGroup(curSchema, group, currNode);
				} else if ( group.getOrder() == Order.choice) {
				  order = "choice";
				  processGroup(curSchema, group, currNode);
				} else {
				  order="all";
				  processGroup(curSchema, group, currNode);
				}

				attrNode = new XAttribute(qnameType, order );    
				xsdComplexTypeNode.add(attrNode);
						
			}
	    
	  }
	  //if ( ((ComplexType)baseType).hasAny())
	  
	  
		
	  
	  
	    parent.addText("\n");
  
	  

	  //System.out.println("</ui:TreeNode>"); 

	  
	}

	void processGroupStructure(Schema curSchema, Group group,  Element parent)
	{	
	if ( group.getOrder() == Order.seq) {
	  
	  XElement mgNode = new XElement(qnameTreeNode);
	  
	  XAttribute attrNode = new XAttribute(qnameTitle, "Sequence" );    
	  mgNode.add(attrNode);
	  
	  parent.add(mgNode);
	  
	  XElement userObjectNode = new XElement(qnameUserObject);
	  mgNode.add(userObjectNode);
	  XElement xsdSequenceNode = new XElement(qnameXSDSequence);
	  
	  userObjectNode.add(xsdSequenceNode);

	  processGroup(curSchema, group, mgNode);
		//model = new SequenceSchemaModel( element.getBaseSchema(), element, group);
	} else if ( group.getOrder() == Order.choice) {
		//model = new ChoiceSchemaModel( element.getBaseSchema(), element, group);
	  XElement mgNode = new XElement(qnameTreeNode);
	  
	  XAttribute attrNode = new XAttribute(qnameTitle, "Choice" );    
	  mgNode.add(attrNode);
	  
	  parent.add(mgNode);
	  
	  XElement userObjectNode = new XElement(qnameUserObject);
	  mgNode.add(userObjectNode);
	  XElement xsdChoiceNode = new XElement(qnameXSDChoice);
	  
	  userObjectNode.add(xsdChoiceNode);

	  processGroup(curSchema, group, mgNode);
	} else {
		//model = new AllSchemaModel( element.getBaseSchema(), element, group);
	  XElement mgNode = new XElement(qnameTreeNode);
	  
	  XAttribute attrNode = new XAttribute(qnameTitle, "All" );    
	  mgNode.add(attrNode);

	  
	  parent.add(mgNode);

	  XElement userObjectNode = new XElement(qnameUserObject);
	  mgNode.add(userObjectNode);
	  XElement xsdAllNode = new XElement(qnameXSDAll);
	  
	  userObjectNode.add(xsdAllNode);
	  
	  
	  processGroup(curSchema, group, mgNode);
	}
	
}	
	
	
	
	
	private void processModelGroup(Schema curSchema, ModelGroup modelGroup,  Element parent) 
	{
		int maxOccurs = modelGroup.getMaxOccurs();
		int minOccurs = modelGroup.getMinOccurs();
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

				processGroupStructure(curSchema, group2, parent);
				break;
					
				default:
					//System.err.println( "SchemaModel.parse() [ERROR] Unknown Structure Type = "+struct.getStructureType());
					break;
			}
		} else {
			//System.err.println( "Could not find Structure for "+modelGroup.getReferenceId()+" ["+element.getName()+"!");
		}
	  
}
	
	private void processGroup(Schema curSchema, Group group,  Element parent) {
		int maxOccurs = -1;
		int minOccurs = -1;

		if ( group.getStructureType() == Structure.MODELGROUP) {
			ModelGroup m = (ModelGroup)group;
			maxOccurs = m.getMaxOccurs();
			minOccurs = m.getMinOccurs();
			
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
					XMLType t = element.getType();
					/*if (t != null)
					  processElement(curSchema, element, parent);
					else */ if (element.isReference())
					{
					  String elOID = (String)globalElementDecls.get(element);
			  
					  processElementRef(curSchema, element, parent);
					}
					else
					  processElement(curSchema, element, parent);
					break;

				case Structure.MODELGROUP:
					ModelGroup modelGroup = (ModelGroup)struct;
if (DEBUG)
System.out.println( "SchemaModel Model Group = "+modelGroup.getName()+" ["+modelGroup.getMinOccurs()+", "+modelGroup.getMaxOccurs()+"]");
		    		processModelGroup(curSchema, modelGroup, parent);

					
					//parse( m);
					
					break;

				case Structure.GROUP:
					Group g = (Group)struct;
					
		    		processGroupStructure(curSchema, g, parent);
				
					//addModel( model);
					break;
				
				case Structure.WILDCARD:
					Wildcard wildcard = (Wildcard)struct;

					if ( !wildcard.isAttributeWildcard()) {
					processAnyElement(  curSchema,  wildcard,  parent);
					} else {
						System.err.println("SchemaModel.parse() [ERROR: Wildcard Attribute in Content Model!]");
					}
					break;

				default:
					//System.err.println( "SchemaModel.parse() [ERROR: Unknown Structure Type = "+struct.getStructureType()+"]");
					break;
			}
		}
	}
	
	private void processAnyElement(Schema curSchema, Wildcard wildcard , Element parent) {

	  XElement treeNode = new XElement(qnameTreeNode);
	  
	  XAttribute attrNode = new XAttribute(qnameTitle, "Any" );    
	  treeNode.add(attrNode);
	  
	  parent.add(treeNode);
	  
	  XElement userObjectNode = new XElement(qnameUserObject);
	  treeNode.add(userObjectNode);
	  XElement xsdAnyNode = new XElement(qnameXSDAny);
	  
	  userObjectNode.add(xsdAnyNode);
  
	}
	
	private void processElementRef(Schema curSchema, ElementDecl element, Element parent) {

		while ( element.isReference()) {
		  element = element.getReference();
		}

	  String elOID = (String)globalElementDecls.get(element);


	  if (elOID != null && !(elOID.equals("")) )
	  {
if (DEBUG)
System.out.println("processElement: " +element.getName() + " " + elOID + " global!");

	    XElement currNode = new XElement(qnameTreeNode);
		  
		  XAttribute attrNode = new XAttribute(qnameTitle, "Element" );    
		  currNode.add(attrNode);
		  //attrNode = new XAttribute(qnameOID, XMLSchema.generateElementOID() );    
		  attrNode = new XAttribute(qnameOID, generateElementOID() );    
		  currNode.add(attrNode);
		  attrNode = new XAttribute(qnameOIDRef, elOID );    
		  currNode.add(attrNode);
	    
		  parent.add(currNode);
	    
	  }

	  
	  //System.out.print("<ui:TreeNode ");
	  //System.out.print("title=\"");
	  //if (getNamespace(element))
	  //System.out.print(element.getName());
	  //System.out.print("\" oid=\"");
	  //System.out.print(XMLSchema.generateElementOID());
	  //System.out.print(generateElementOID());
	  //System.out.print("\" ");
	  
	  //if (elOID != null && !elOID.equals("") )
	  //{
	  //  System.out.print("oidref=\"");
	  //  System.out.print(elOID);
	  //  System.out.print("\"");
	  //}
	  //else
	  //{
	  // 	//TODO problem here!
	  //}
	  
	  
	  //System.out.print("/>");


	}

	private void processGlobalElement(Schema curSchema, ElementDecl element, Element parent) 
	{
		XMLType t = element.getType();
		  Hashtable attributes = null;
		
		//if ( t == null && element.getSubstitutionGroup() != null) {
		//	ElementDecl es = getSubstitutionHead();
		//	
		//	if ( es != null) {
		//		t = es.getType();
		//	}
		//}

		  String elOID = (String)globalElementDecls.get(element);
		  //TODO Check that this isn't really global!

		  //if (isGlobal(element))
		  
		  XElement currNode = new XElement(qnameTreeNode);
		  
		  XAttribute attrNode = new XAttribute(qnameTitle, "Element" );    
		  currNode.add(attrNode);
		  //attrNode = new XAttribute(qnameOID, XMLSchema.generateElementOID() ); 
		  //String OID = generateElementOID();
		  //attrNode = new XAttribute(qnameOID, OID );    
		  attrNode = new XAttribute(qnameOID, elOID );    
		  currNode.add(attrNode);
if (DEBUG1)	
System.out.println("processGlobalElement: " + element.getName() + "  " + elOID );
		  
		  if ( element.getName().equals(rootName))
		  {
		    if ( (getNamespace(element) != null && getNamespace(element).equals(rootNamespace) ) ||
		         (getNamespace(element) == null ||getNamespace(element).equals(""))  && (rootNamespace ==null || rootNamespace.equals("") )		            
		        )		      
		    {
			    rootTreeNode = currNode;
if (DEBUG)	
System.out.println("root node");
		    }
		    else
		    {
			    parent.add(currNode);
if (DEBUG)	
System.out.println("NOT root node");
		    } 
		  }
		  else
		  {
		    parent.add(currNode);
if (DEBUG)	
System.out.println("NOT root node");
		  }
//System.out.println("new XElement(qnameUserObject)");
		  
		  XElement userObjectNode = new XElement(qnameUserObject);
		  currNode.add(userObjectNode);

		  
		  //System.out.print("<ui:TreeNode ");
		  //System.out.print("title=\"Element\" oid=\"");
		  //System.out.print(XMLSchema.generateElementOID());
		  //System.out.print("\">");
		  
		  //System.out.print("<ui:userObject>");
		  //System.out.print("<ui:xsdElement name=\"");
		  
		  String name = "";
		  String prefix = getNamespacePrefix(element);
		  if ( prefix != null && prefix.length()>0)
		  {
		    //System.out.print(prefix + ":");
		    name+= prefix;
		    name += ":";
		  }
		  //System.out.print(element.getName());
		  name += element.getName();
		  
if (DEBUG)			  
System.out.println(name);


//System.out.print("\"");

		  
		  
		  
		  
		  //System.out.print(" namew=\"");
		  //System.out.print(getStringWidth(name));
		  //System.out.print("\"");
		  
		  XElement xsdElementNode = new XElement(qnameXSDElement);
		  attrNode = new XAttribute(qnameName, name );    
		  xsdElementNode.add(attrNode);
		  attrNode = new XAttribute(qnameNamew, Integer.toString(getStringWidth(name)) );    
		  xsdElementNode.add(attrNode);
		  
		  
		  userObjectNode.add(xsdElementNode);
		  
		  
		  
		  if (t!=null && t.getName()!=null && !(t.getName().equals("")))
		  {
		    //System.out.print(" type=\"");
if (DEBUG)	
System.out.println("ct: " + t.getName());
			//System.out.print("\"");
			
			attrNode = new XAttribute(qnameType, t.getName() );    
			xsdElementNode.add(attrNode);
	
		  }
		  //System.out.print(">");

		  if ( t instanceof ComplexType) 
		  {
if (DEBUG)	
System.out.println("getAttributesFromType");

		    attributes = getAttributesFromType(curSchema, (ComplexType)t);
		    if (attributes!= null)
		    {
				Enumeration enumeration = attributes.elements();
				while ( enumeration.hasMoreElements()) {
					AttributeDecl attr = (AttributeDecl)enumeration.nextElement();
	
					  //<ui:xsdAttribute name="testattr"/>			  
					  //System.out.print("<ui:xsdAttribute name=\"");
					  //System.out.print(attr.getName());
					  //System.out.print("\"");
					  //System.out.print(" namew=\"");
					  //System.out.print(getStringWidth(attr.getName()));
					  //System.out.print("\"/>");		  

					  XElement xsdAttributeNode = new XElement(qnameXSDAttribute);
					  attrNode = new XAttribute(qnameName, attr.getName() );    
					  xsdAttributeNode.add(attrNode);
					  attrNode = new XAttribute(qnameNamew, Integer.toString(getStringWidth(attr.getName())) );    
					  xsdAttributeNode.add(attrNode);

					  xsdElementNode.add(xsdAttributeNode);

				}
		    }
		  }
		  //System.out.print("</ui:xsdElement>");		  
		  
		  //System.out.print("</ui:userObject>");
		  
		  //<ui:userObject><ui:xsdElement name="KFI" type="KFIType"/></ui:userObject><ui:TreeNode title="ct KFIType" oidref="d0e1376"/></ui:TreeNode>
		  
		  
		if ( t instanceof ComplexType) {
		  if (((ComplexType)t).isTopLevel())
		  {

		    ComplexType ct = (ComplexType)t;
		    
		    if (hasElements(curSchema, ct))
		    {

		    String ctOID = (String)globalComplexTypeDecls.get(t);

			  //System.out.print("<ui:TreeNode title=\"");
			  //System.out.print(t.getName());
			  //System.out.print("\" oidref=\"");			  
			  //System.out.print(ctOID);
			  //System.out.print("\"/>");
		    
			  XElement newNode = new XElement(qnameTreeNode);
			  
			  attrNode = new XAttribute(qnameTitle, t.getName() );    
			  newNode.add(attrNode);
			  attrNode = new XAttribute(qnameOIDRef, ctOID );    
			  newNode.add(attrNode);
			  
			  currNode.add(newNode);

		    }
		  }
		  else
		  {
if (DEBUG)
System.out.print("processComplexType( curSchema, (ComplexType)t, currNode);");
			if (hasElements(curSchema, (ComplexType)t))
			  processComplexType( curSchema, (ComplexType)t, currNode);
		  }
		} 
		else if ( t instanceof SimpleType) 
		{
		  if (((SimpleType)t).getName() == null)
		  {
		  }
		  else
		  {
		    
		  }
		} 
		else 
		{
		}  
	  
		
		
		  //System.out.println("</ui:TreeNode>");
	    parent.addText("\n");

	}
	
	
	private void processElement(Schema curSchema, ElementDecl element, Element parent) {
		XMLType t = element.getType();
		  Hashtable attributes = null;
		
		//if ( t == null && element.getSubstitutionGroup() != null) {
		//	ElementDecl es = getSubstitutionHead();
		//	
		//	if ( es != null) {
		//		t = es.getType();
		//	}
		//}

		  String elOID = (String)globalElementDecls.get(element);
		  //TODO Check that this isn't really global!


		  if (elOID != null && !(elOID.equals("")) )
		  {
if (DEBUG)
System.out.println("processElement: " +element.getName() + " " + elOID + " global!");

		    XElement currNode = new XElement(qnameTreeNode);
			  
			  XAttribute attrNode = new XAttribute(qnameTitle, "Element" );    
			  currNode.add(attrNode);
			  //attrNode = new XAttribute(qnameOID, XMLSchema.generateElementOID() );    
			  attrNode = new XAttribute(qnameOID, generateElementOID() );    
			  currNode.add(attrNode);
			  attrNode = new XAttribute(qnameOIDRef, elOID );    
			  currNode.add(attrNode);
		    
			  parent.add(currNode);
			  return;
		    
		  }
		  
		  XElement currNode = new XElement(qnameTreeNode);
		  
		  XAttribute attrNode = new XAttribute(qnameTitle, "Element" );    
		  currNode.add(attrNode);
		  //attrNode = new XAttribute(qnameOID, XMLSchema.generateElementOID() );   
		  String OID = generateElementOID();
		  attrNode = new XAttribute(qnameOID, OID );    
		  currNode.add(attrNode);
if (DEBUG)
System.out.println("processElement: " +element.getName() + " " + OID );

//System.out.println(element.getName() + "  " + rootName + "  " + rootNamespace);
		  
		  if ( element.getName().equals(rootName))
		  {
		    if ( (getNamespace(element) != null && getNamespace(element).equals(rootNamespace) ) ||
		         (getNamespace(element) == null ||getNamespace(element).equals(""))  && (rootNamespace ==null || rootNamespace.equals("") )		            
		        )		      
		    {
			    rootTreeNode = currNode;
		    }
		    else
		    {
			    parent.add(currNode);
		    } 
		  }
		  else
		    parent.add(currNode);

//System.out.println("new XElement(qnameUserObject)");
		  
		  XElement userObjectNode = new XElement(qnameUserObject);
		  currNode.add(userObjectNode);

		  
		  //System.out.print("<ui:TreeNode ");
		  //System.out.print("title=\"Element\" oid=\"");
		  //System.out.print(XMLSchema.generateElementOID());
		  //System.out.print("\">");
		  
		  //System.out.print("<ui:userObject>");
		  //System.out.print("<ui:xsdElement name=\"");
		  
		  String name = "";
		  String prefix = getNamespacePrefix(element);
		  if ( prefix != null && prefix.length()>0)
		  {
		    //System.out.print(prefix + ":");
		    name+= prefix;
		    name += ":";
		  }
		  //System.out.print(element.getName());
		  name += element.getName();
		  
		  
		  
//System.out.println(name);
		  //System.out.print("\"");

		  
		  
		  
		  
		  //System.out.print(" namew=\"");
		  //System.out.print(getStringWidth(name));
		  //System.out.print("\"");
		  
		  XElement xsdElementNode = new XElement(qnameXSDElement);
		  attrNode = new XAttribute(qnameName, name );    
		  xsdElementNode.add(attrNode);
		  attrNode = new XAttribute(qnameNamew, Integer.toString(getStringWidth(name)) );    
		  xsdElementNode.add(attrNode);
		  
		  
		  userObjectNode.add(xsdElementNode);
		  
		  
		  
		  if (t!=null && t.getName()!=null && !(t.getName().equals("")))
		  {
		    //System.out.print(" type=\"");
//System.out.println(t.getName());
			//System.out.print("\"");
			
			attrNode = new XAttribute(qnameType, t.getName() );    
			xsdElementNode.add(attrNode);
	
		  }
		  //System.out.print(">");

		  if ( t instanceof ComplexType) 
		  {

		    attributes = getAttributesFromType(curSchema, (ComplexType)t);
		    if (attributes!= null)
		    {
				Enumeration enumeration = attributes.elements();
				while ( enumeration.hasMoreElements()) {
					AttributeDecl attr = (AttributeDecl)enumeration.nextElement();
	
					  //<ui:xsdAttribute name="testattr"/>			  
					  //System.out.print("<ui:xsdAttribute name=\"");
					  //System.out.print(attr.getName());
					  //System.out.print("\"");
					  //System.out.print(" namew=\"");
					  //System.out.print(getStringWidth(attr.getName()));
					  //System.out.print("\"/>");		  

					  XElement xsdAttributeNode = new XElement(qnameXSDAttribute);
					  attrNode = new XAttribute(qnameName, attr.getName() );    
					  xsdAttributeNode.add(attrNode);
					  attrNode = new XAttribute(qnameNamew, Integer.toString(getStringWidth(attr.getName())) );    
					  xsdAttributeNode.add(attrNode);

					  xsdElementNode.add(xsdAttributeNode);

				}
		    }
		  }
		  //System.out.print("</ui:xsdElement>");		  
		  
		  //System.out.print("</ui:userObject>");
		  
		  //<ui:userObject><ui:xsdElement name="KFI" type="KFIType"/></ui:userObject><ui:TreeNode title="ct KFIType" oidref="d0e1376"/></ui:TreeNode>
		  
		  
		if ( t instanceof ComplexType) {
		  if (((ComplexType)t).isTopLevel())
		  {
		    ComplexType ct = (ComplexType)t;
		    
		    if (hasElements(curSchema, ct))
		    {
			  String ctOID = (String)globalComplexTypeDecls.get(t);

			  //System.out.print("<ui:TreeNode title=\"");
			  //System.out.print(t.getName());
			  //System.out.print("\" oidref=\"");			  
			  //System.out.print(ctOID);
			  //System.out.print("\"/>");
		    
			  XElement newNode = new XElement(qnameTreeNode);
			  
			  attrNode = new XAttribute(qnameTitle, t.getName() );    
			  newNode.add(attrNode);
			  attrNode = new XAttribute(qnameOIDRef, ctOID );    
			  newNode.add(attrNode);
			  
			  currNode.add(newNode);
		    }
			  
		  }
		  else
		  {
//System.out.print("processComplexType( curSchema, (ComplexType)t, currNode);");
		    processComplexType( curSchema, (ComplexType)t, currNode);
		  }
		} 
		else if ( t instanceof SimpleType) 
		{
		  if (((SimpleType)t).getName() == null)
		  {
		  }
		  else
		  {
		    
		  }
		} 
		else 
		{
		}  
	  
		
		
		  //System.out.println("</ui:TreeNode>");
	    parent.addText("\n");
		
	}

	
	public void setRootName( String rootName) {
	  	 if (rootName != null)
	  	   this.rootName = rootName; 
		}

	public void setRootNamespace( String rootNamespace) {
	  	 if (rootNamespace != null)
	  	   this.rootNamespace = rootNamespace; 
		}

	
	public void setFontMetrics( FontMetrics fontMetrics) {
	 this.fontMetrics = fontMetrics; 
	}

	
	public int getStringWidth(String text)
	{
	  
	  return fontMetrics.stringWidth(text);
	}

	public int getStringHeight(String text)
	{
	  
	  return fontMetrics.getHeight();
	}
	

	
	public void setPrefixes( Vector declarations) {
	  
	    for (int i = 0; i < declarations.size(); i++)
	    {

	      Namespace namespace = (Namespace) declarations.elementAt(i);
	      String prefix = namespace.getPrefix();
	      
	      if (prefix != null && prefix.trim().length() > 0 && !prefix.trim().equals("xsd") && !prefix.trim().equals("xs") &&  !prefix.trim().equals("xsi"))
	      {
	        //System.out.println("Prefix: " + prefix + "   URI: "+ namespace.getURI());
	        map.put(namespace.getURI(), prefix);
	      }
	    }
	  
	}
	
	public String getNamespacePrefix(ElementDecl element) {
	  //System.out.println("getNamespacePrefix");
	  String prefix = "";
	  
	  String uri = getNamespace(element);
	  
	  if (uri != null && uri.length()>0)
	  {
	    prefix = (String)map.get(uri);
	  }
	  
	  //System.out.println("getNamespacePrefix return: " + prefix);
	  return prefix;
	}

	public String getUniversalName(ElementDecl element) {
			StringBuffer universal = new StringBuffer();
			String namespace = getNamespace(element);
			
			if ( namespace != null && namespace.length() > 0) {
				universal.append( "{");
				universal.append( namespace);
				universal.append( "}");
			}

			universal.append( element.getName());
		
		return universal.toString();
	}
	
	public String getNamespace(ElementDecl element) {
	  
if (DEBUG)	  
System.out.println("getNamespace");
	  
	  String namespace = "";
	
			Schema s = element.getSchema();
			Form f = s.getElementFormDefault();
			
			if ( element.isReference()) {
				ElementDecl ref = element.getReference();
				s = ref.getSchema();
				
				namespace = s.getTargetNamespace();
			} else if ( isGlobal(element) || (f != null && f.isQualified())) {
				namespace = s.getTargetNamespace();
			} else {
				namespace = "";
			}
			  //System.out.println("getNamespace return: " + namespace);

if (DEBUG)	  
System.out.println("getNamespace DONE");
			
			
		return namespace;
	}

	
	public boolean isGlobal(ElementDecl element) {
	  
if (DEBUG)	  
System.out.println("isGlobal()");
	  

if (DEBUG)
{
System.out.println("EL name: " + element.getName() + "  OID: " + globalElementDecls.get(element));
System.out.println(element);
}

		Schema s = element.getSchema();
	
//System.out.println("el: " + element.getName() + " uri: " + s.getSchemaLocation());		

/*
		
		//Enumeration enumeration = s.getGlobalElements();
		Enumeration enumeration = globalElementDecls.elements();
		while( enumeration.hasMoreElements()) {
			ElementDecl e = (ElementDecl)enumeration.nextElement();
			
if (DEBUG)
System.out.println("globale el: " + e.getName());
			

			//if ( e.getName().equals( element.getName()) && getNamespace(e).equals(getNamespace(element)) ) {
			//if ( e.getName().equals( element.getName())) {
			if ( e.equals( element)) {
			  
				return true;
			}
		}
*/
		String globalOID = (String)globalElementDecls.get(element);
		if (globalOID != null && !globalOID.equals("") )
			return true;
		else						
		  return false;
	}

	
	public static int generateOID()
	{
	  oid++;
	  
	  return oid;
	
	}
	
	
	public static String generateElementOID()
	{
	  oid++;
	  
	  return "EL" + oid;
	
	}
	public static String generateComplexTypeOID()
	{
	  oid++;
	  
	  return "CT" + oid;
	
	}
	public static String generateSimpleTypeOID()
	{
	  oid++;
	  
	  return "ST" + oid;
	
	}

}
  
  
 