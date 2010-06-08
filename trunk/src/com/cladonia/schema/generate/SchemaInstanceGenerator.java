/*
 * Created on 13-Dec-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.schema.generate;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.dom4j.Namespace;

import com.cladonia.xml.XElement;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.SchemaAttribute;
import com.cladonia.schema.SchemaModel;
import com.cladonia.schema.AnySchemaElement;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.XMLType;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.Particle;
import org.exolab.castor.xml.schema.Structure;
import java.util.Enumeration;
import org.exolab.castor.xml.schema.Form;
import org.exolab.castor.xml.schema.Schema;
import org.dom4j.QName;
import org.exolab.castor.xml.schema.SimpleType;
import java.util.Stack;
import org.exolab.castor.xml.schema.SimpleTypesFactory;
import org.exolab.castor.xml.schema.Facet;

import org.exolab.castor.xml.schema.Union;
import org.exolab.castor.xml.schema.simpletypes.ListType;
import org.exolab.castor.xml.schema.ContentType;
//import org.exolab.castor.xml.schema.SimpleContent;


//import com.cladonia.schema.SchemaElement;
/**
 * @author gmcgoldrick
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class SchemaInstanceGenerator
{
  
  Document document = null;
  Element root = null;
  Random random = new Random();
  int nested = 0;
  HashMap map = new HashMap();
  boolean optionalAttributes = false;
  boolean optionalElements = false;
  boolean generateData = true;
  Stack elementStack = new Stack();
  private boolean DEBUG = true;
  private boolean DEBUG1 = true;

  public static void main(String[] args)
  {
  }
  
  public SchemaInstanceGenerator()
  {
    //System.out.println("Creating SchemaInstanceGenerator");
    
  }
  
  public String generateInstanceFromSchemaURL(URL file, String root,
      boolean optionalAttributes, boolean optionalElements, boolean generateData)
  {
    
    String xml = processXSD(file, root, optionalAttributes, optionalElements,
        generateData);
    
    return xml;
    
  }
  
  public String processXSD(String url, String rootElement)
  {
    
    //System.out.println("in processXSD SchemaInstanceGenerator: " + url);
    
    return "<xml>test</xml>";
  }
  
  public String processXSD(ExchangerDocument schemaDocument,
      SchemaElement schemaRoot, boolean optionalAttributes,
      boolean optionalElements, boolean generateData)
  {
    
    //System.out.println("in processXSD SchemaInstanceGenerator: ");
    this.optionalAttributes = optionalAttributes;
    this.optionalElements = optionalElements;
    this.generateData = generateData;
    
    /*
     * try { document = getDocument(); } catch (Exception e) {
     * System.err.println( "getDom: " + e ); }
     */
    
    Vector namespaces = schemaDocument.getDeclaredNamespaces();
    
    ElementDecl elDecl = schemaRoot.getElement();
    
    ExchangerDocument document = null;
    root = new XElement(elDecl.getName());
    
    try
    {
      document = new ExchangerDocument((XElement) root);
    }
    catch (Exception ex)
    {
      return "";
    }
    
    root = document.getRoot();
    
    for (int i = 0; i < namespaces.size(); i++)
    {
      Namespace namespace = (Namespace) namespaces.elementAt(i);
      String prefix = namespace.getPrefix();
      
      if (prefix != null && prefix.trim().length() > 0 && !prefix.trim().equals("xsd") && !prefix.trim().equals("xs") &&  !prefix.trim().equals("xsi"))
      {
        //System.out.println("Prefix: " + prefix + "   URI: "+ namespace.getURI());

        map.put(namespace.getURI(), prefix);
      }
      else
      {
        //map.put( namespace.getURI(), prefix);
        //root.addNamespace( "", namespace.getURI());
        //System.out.println("URI: " + namespace.getURI());
      }
    }
    
    
    //xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:oasis:names:tc:ubl:Order:1.0:0.70
    //  ../schema/UBL_Library_0p70_Order.xsd"
    
    
    
    
    String targetNamespace = elDecl.getSchema().getTargetNamespace();
    if (targetNamespace != null)
    {
      map.put(targetNamespace, "");
      root.addNamespace("", targetNamespace);
      //System.out.println("URI: " + targetNamespace);
      
      root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      
      
      QName qname = null;
      //resolve the namespace string
      
      Namespace newNs;
      try
      {
        newNs = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        qname = new QName("schemaLocation", newNs);
      }
      catch (StringIndexOutOfBoundsException e1)
      {
        //cannot parse string
        //MessageHandler.showError(parent,"Could not resolve
        // Namespace:\n"+namespace,
        //"Tools Add Node");
        qname = new QName("schemaLocation");
      }
      
/*      URI uri = null;
      try
      {
         uri = URI.create(schemaDocument.getURL().toString());
 //System.out.println("uri:" + uri.toString() );
        XAttribute newNode = new XAttribute(qname, targetNamespace + " " + uri.toString() );
 
        root.add(newNode);
 
      }
      catch (Exception ex)
      {System.out.println("****exception in uri.create" + ex.getMessage() );}
 */
      
      String url1 = schemaDocument.getURL().toString();
      String url2 = encodeURI(url1);
      
      
      XAttribute newNode = new XAttribute(qname, targetNamespace + " " + url2 );
      
      root.add(newNode);
      
    }
    else
    {
      root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      
      
      QName qname = null;
      //resolve the namespace string
      
      Namespace newNs;
      try
      {
        newNs = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        qname = new QName("noNamespaceSchemaLocation", newNs);
      }
      catch (StringIndexOutOfBoundsException e1)
      {
        //cannot parse string
        //MessageHandler.showError(parent,"Could not resolve
        // Namespace:\n"+namespace,
        //"Tools Add Node");
        qname = new QName("noNamespaceSchemaLocation");
      }
      String url1 = schemaDocument.getURL().toString();
      String url2 = encodeURI(url1);
      
      
      XAttribute newNode = new XAttribute(qname,  "" + url2);
      
      
      root.add(newNode);
      
    }
    
    
    
    
    
    handleAttributes(schemaRoot, root);
    handleModels(schemaRoot, root);
    
    /*
     * XMLType xmlType = elDecl.getType(); if (xmlType != null &&
     * xmlType.isComplexType()) {
     *
     * buildComplexPart((ComplexType)xmlType, root);
     *  } else if (xmlType != null && xmlType.isSimpleType()) {
     *
     *  } else {
     *
     * return " <xml>test </xml>";
     *  }
     *
     */
    
    document.update();
    
    return document.getText();
    
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
  
  
  
  protected void handleModels(SchemaElement element, Element partElem)
  {
    if (DEBUG)
      System.out.println("handleModels: ");
    
    Vector models = element.getModels();
    
    if (models != null)
    {
      for (int i = 0; i < models.size(); i++)
      {
        SchemaModel model = (SchemaModel) models.elementAt(i);
        
        handleModelMaxMin(model, element, partElem);
        
      }
    } //else
    //System.out.println("handleModels: null");
    
  }
  
  
  protected void handleModelMaxMin(SchemaModel model, SchemaElement element,
      Element partElem)
  {
    
    if (model == null)
      return;
    
    int maxOccurs = model.getMaxOccurs();
    int minOccurs = model.getMinOccurs();
    
    if (minOccurs == 0)
    {
      if (optionalElements == false)
        return;
      else
      {
        handleModel(model, element, partElem);
        return;
      }
    }
    
    for (int j = 0; j<minOccurs; j++)
      handleModel(model, element, partElem);
    
  }
  
  protected void handleModel(SchemaModel model, SchemaElement element,
      Element partElem)
  {
    if (model == null)
      return;

    //System.out.println("handleModel: ");
    if (DEBUG)
      System.out.println("model: " + model.getType());
    
    //System.out.println("model type: " + model.getType()  + "  model: " + model.toString());
    
    Vector children = model.getChildren();
    
    if (children != null)
    {
      //System.out.println("handleModel: " + children.size());
      
      for (int j = 0; j < children.size(); j++)
      {
        //System.out.println("handleModel: process child"+ children.elementAt(j).getClass().getName());
        
        //SchemaParticle particle = (SchemaParticle)children.elementAt(j);
        Object particle = children.elementAt(j);
        
        //System.out.println("handleModel: process particle");
        
        if (particle == null)
        {
          //System.out.println("handleModel: null particle");
          return;
        }
        
        if (particle instanceof SchemaModel)
        {
          //System.out.println("handleModel: SchemaModel");
          SchemaModel sm = (SchemaModel) particle;
          
          if (sm != null)
          {
            handleModelMaxMin(sm, element, partElem);
          }
          
        }
        else if (particle instanceof AnySchemaElement)
        {
          //System.out.println("handleModel: AnySchemaElement");
          AnySchemaElement ase = (AnySchemaElement) particle;
        }
        else
        { // ElementNode
          
          //System.out.println("handleModel: SchemaElement");
          
          SchemaElement se = (SchemaElement) particle;
          if (se != null)
          {
            //System.out.println("element: " + se.getName());
            
            handleElementMaxMin(se, partElem);
            
            //handleAttributes(se, partElem);
            //handleModels(se, partElem);
          }
          
        }
        
        if (model.getType().equals("choice") )
          break;
      }
    }
  }
  
  protected void handleAttributes(SchemaElement element, Element partElem)
  {
    
    if (element == null)
      return;

    if (DEBUG)
      System.out.println("handleAttributes: ");
    
    Vector attributes = element.getAttributes();
    
    if (attributes != null)
    {
      //System.out.println("handleAttributes: " + attributes.size());
      for (int i = 0; i < attributes.size(); i++)
      {
        SchemaAttribute sa = (SchemaAttribute) attributes.elementAt(i);
        
        if (sa != null && (sa.isRequired() || this.optionalAttributes == true))
        {
          //System.out.println("handleAttributes: " + sa.getName());
          
          String name = sa.getName();
          
          Vector values = sa.getValues();
          
          String value = "attr_val";
          
          if (sa.getType() != null)
          {
            SimpleType st = (SimpleType)sa.getType().getType();
          
            value = generateSimpleTypeContent(st);
          }
          
          String namespace = sa.getNamespace();
          
          QName qname = null;
          //resolve the namespace string
          
          if (namespace != null && !namespace.equalsIgnoreCase("none"))
          {
            Namespace newNs;
            try
            {
              newNs = new Namespace(namespace.substring(0, namespace
                  .indexOf(":")), namespace.substring(
                  namespace.indexOf(":") + 1, namespace.length()));
              qname = new QName(name, newNs);
            }
            catch (StringIndexOutOfBoundsException e1)
            {
              //cannot parse string
              //MessageHandler.showError(parent,"Could not resolve
              // Namespace:\n"+namespace,
              //"Tools Add Node");
              qname = new QName(name);
            }
            
          }
          else
          {
            qname = new QName(name);
          }
          XAttribute newNode = new XAttribute(qname, value);
          
          partElem.add(newNode);
          
        }
      }
    }
    if (DEBUG)
      System.out.println("handleAttributes: done");

  }
  
  void handleElementMaxMin(SchemaElement element, Element partElem)
  {
    if (element == null)
      return;

    int maxOccurs = element.getMaxOccurs();
    int minOccurs = element.getMinOccurs();
    
    if (minOccurs == 0)
    {
      if (optionalElements == false)
        return;
      else
      {
        handleElement(element, partElem);
        return;
      }
    }
    
    for (int j = 0; j<minOccurs; j++)
      handleElement(element, partElem);
    
  }
  
  
  void handleElement(SchemaElement element, Element partElem)
  {
    if (element == null)
      return;

    if (DEBUG)
      System.out.println("handleElement: " + element.getName());
    
    
    if (element.isAbstract())
    {
      Vector subs = element.getSubstituteElements();
      
      if (subs != null && subs.size() > 0)
        handleElement((SchemaElement)subs.elementAt(0), partElem);
      
      return;
    }
    
    String universalName = element.getUniversalName();
    
    element.resolveReference();
    
    ElementDecl elementDecl = element.getElement();
    
    
    if (elementDecl == null)
      return;

    String namespace = "";
    
    Schema s = elementDecl.getSchema();
    Form f = s.getElementFormDefault();
    
    if (elementDecl.isReference())
    {
      ElementDecl ref = elementDecl.getReference();
      s = ref.getSchema();
      
      namespace = s.getTargetNamespace();
    }
    else if (isGlobal(elementDecl) || (f != null && f.isQualified()))
    {
      namespace = s.getTargetNamespace();
    }
    else
    {
      namespace = "";
    }
    
    //Namespace currNamespace = root.getNamespaceForURI(namespace);
    
    // build the element that will be added to the message
    
    QName qname = null;
    String name = elementDecl.getName();
    
    String prefix = (String) map.get(namespace);
    
    //System.out.println("name: " + elementDecl.getName() + "  namespace: " + namespace + "  prefix: " + prefix);
    
    //System.out.println(" check namespace");
    
    if (namespace != null && !namespace.equalsIgnoreCase("") && prefix != null
        && prefix != "")
    {
      
      //System.out.println(" check namespace: not null prefix not null");
      
      Namespace newNs;
      try
      {
        newNs = new Namespace(prefix, namespace);
        qname = new QName(name, newNs);
      }
      catch (StringIndexOutOfBoundsException e1)
      {
        //cannot parse string
        qname = new QName(name);
      }
      
    }
    else if (namespace != null && !namespace.equalsIgnoreCase(""))
    {
      
      //System.out.println(" check namespace: prefix null");
      
      Namespace newNs;
      try
      {
        newNs = new Namespace("", namespace);
        qname = new QName(name, newNs);
      }
      catch (StringIndexOutOfBoundsException e1)
      {
        //cannot parse string
        qname = new QName(name);
      }
      
    }
    else
    {
      //System.out.println(" check namespace:  null");
      
      qname = new QName(name);
    }
    
    //System.out.println("new XElement");
    
    XElement childElem = new XElement(qname);
    
    //System.out.println("partElem.add(childElem)");
    
    partElem.add(childElem);
    
    handleAttributes(element, childElem);
    
    boolean recursive = false;
    
    //if (!element.isRecursive())
    for (int i = elementStack.size() - 1; i >= 0; i--)
    {
      //System.out.println("i=" + i);
      String item = (String) elementStack.get(i);
      
      if (item.equals(universalName))
      {
        recursive = true;
        break;
      }
    }
    
    
    XMLType xmlType = elementDecl.getType();
    
    //if (xmlType != null && xmlType.isComplexType() && !(((ComplexType)xmlType).getContentType().getType() == 4 /*org.exolab.castor.xml.schema.ContentType.SIMPLE*/ ))
    if (xmlType != null && xmlType.isComplexType() && !((ComplexType)xmlType).isSimpleContent())
    {
      if (!recursive)
      {
      
        //if ( ((ComplexType)xmlType).getContentType() == ContentType.mixed  ||
        //     ((ComplexType)xmlType).isSimpleContent())
        //{
        //  
        //}
        //else
        if ( ((ComplexType)xmlType).getContentType() == ContentType.elemOnly && SchemaUtilities.hasElements( ((ComplexType)xmlType)))
          childElem.addText("\n");
        
        elementStack.push(universalName);
        
        handleModels(element, childElem);
        
        elementStack.pop();
      }
    }
    else  if (xmlType != null && xmlType.isComplexType())
    {
      //SimpleType st = ((ComplexType)xmlType).getContentType().getSimpleType();
 
      
      ContentType contentType = ((ComplexType)xmlType).getContentType();
      
      if (DEBUG)
        System.out.println("handleElement ComplexType Simple Content ");
      
     
      if (contentType != null)
      {
        SimpleType st = (SimpleType)contentType.getSimpleType();
        
        if (st != null)
        {
        if (DEBUG)
          System.out.println("handleElement "+ st.getName());
   	      //handleSimpleType(st, childElem);
	      String retVal = generateSimpleTypeContent(st);
	      childElem.addText(retVal);
        }
        else
        {
          if (DEBUG)
            System.out.println("handleElement ***NULL simple type ");
        
        }
      }
    }
    else
    {
      SimpleType st = (SimpleType)elementDecl.getType();
      //handleSimpleType(st, childElem);
      String retVal = generateSimpleTypeContent(st);
      childElem.addText(retVal);
    }
 
    
    if (DEBUG)
      System.out.println("handleElement:  done " + element.getName());

    
  }
  
  String  generateSimpleTypeContent(SimpleType st)
  {
    if (st == null)
      return "";
    
    String retVal = "";
    
    if (DEBUG)
      System.out.println("generateSimpleTypeContent:");
    
    if (generateData != true)
      return retVal;

    
    if (st instanceof Union)
    {
      if (DEBUG)
        System.out.println("handleSimpleType: union");
 
      Union union = (Union)st;
      Enumeration en = union.getMemberTypes();

      while (en != null && en.hasMoreElements())
      {
        SimpleType st2 = (SimpleType)en.nextElement();
        if (DEBUG)
          System.out.println("handleSimpleType: union simple type" + st2.getName());

        
        retVal = generateSimpleTypeContent( st2);
        break;
      }
      
      return retVal;
    }
    
    //TODO Handle List
    
    
    if (st instanceof ListType)
    {
      if (DEBUG)
        System.out.println("handleSimpleType: list");
 
      ListType list = (ListType)st;
      
      SimpleType st2 = list.getItemType();
      
      retVal = generateSimpleTypeContent( st2);

      return retVal;
    }
   
    Enumeration en = st.getFacets();
    
    while (en != null && en.hasMoreElements())
    {
      
      Facet facet = (Facet)en.nextElement();
      if (DEBUG)
        System.out.println("facet name: " + facet.getName() + "facet val: " + facet.getValue());

      if ( facet != null && facet.getName().equals(Facet.ENUMERATION) && !(facet.getValue().equals("")))
      {
        retVal = facet.getValue();
        
        return retVal;
        
        
      }
    }
  
    int code = -1;
    
    SimpleType base = st.getBuiltInBaseType();
    
    if (base != null)
    {
      if (DEBUG)
        System.out.println("handleSimpleType: baseType " + base.getName());

      code = base.getTypeCode();
      
      if (DEBUG)
        System.out.println(" base.getTypeCode" + code);

     }
    else   
      code = st.getTypeCode();
  
    
    switch (code)
    {
      case SimpleTypesFactory.INTEGER_TYPE :
        retVal = "29";
        break;
        
      case SimpleTypesFactory.LONG_TYPE :
        retVal = "9223372036854775801";
        break;
        
      case SimpleTypesFactory.NEGATIVE_INTEGER_TYPE :
        retVal = "-293456";
        break;
        
      case SimpleTypesFactory.NON_NEGATIVE_INTEGER_TYPE :
        retVal = "393456";
        break;
        
      case SimpleTypesFactory.POSITIVE_INTEGER_TYPE :
        retVal = "292929";
        break;
        
      case SimpleTypesFactory.NON_POSITIVE_INTEGER_TYPE :
        retVal = "-292929";
        break;
        
      case SimpleTypesFactory.INT_TYPE :
        retVal = "2147483641";
        break;
        
      case SimpleTypesFactory.SHORT_TYPE :
        retVal = "32760";
        break;
        
      case SimpleTypesFactory.BYTE_TYPE :
        retVal = "123";
        break;
        
      case SimpleTypesFactory.UNSIGNED_INT_TYPE :
        retVal = "2147483642";
        break;
        
      case SimpleTypesFactory.UNSIGNED_LONG_TYPE :
        retVal = "9223372036854775801";
        break;
        
      case SimpleTypesFactory.UNSIGNED_SHORT_TYPE :
        retVal = "32761";
        break;
        
      case SimpleTypesFactory.UNSIGNED_BYTE_TYPE :
        retVal = "124";
        break;
        
      case SimpleTypesFactory.STRING_TYPE :
        retVal = "HelloWorld";
        break;
        
      case SimpleTypesFactory.DECIMAL_TYPE :
        retVal = "7.08";
        break;
        
      case SimpleTypesFactory.BOOLEAN_TYPE :
        retVal = "true";
        break;
        
      case SimpleTypesFactory.FLOAT_TYPE :
        retVal = "12.56E";
        break;
        
      case SimpleTypesFactory.DOUBLE_TYPE :
        retVal = "12.56E";
        break;
        
      case SimpleTypesFactory.DURATION_TYPE :
        retVal = "P1Y2M3DT10H30M";
        break;
        
      case SimpleTypesFactory.DATETIME_TYPE :
        retVal = "2005-03-29T13:24:36.000";
        break;
      case SimpleTypesFactory.TIME_TYPE :
        retVal = "13:24:36.000";
        break;
      case SimpleTypesFactory.DATE_TYPE :
        retVal = "2005-03-29";
        break;
      case SimpleTypesFactory.GYEARMONTH_TYPE :
        retVal = "2005-03";
        break;
      case SimpleTypesFactory.GYEAR_TYPE :
        retVal = "2005";
        break;
      case SimpleTypesFactory.GMONTH_TYPE :
        retVal = "--03--";
        break;
      case SimpleTypesFactory.GMONTHDAY_TYPE :
        retVal = "--03-29";
        break;
      case SimpleTypesFactory.GDAY_TYPE :
        retVal = "---29";
        break;
      case SimpleTypesFactory.HEXBINARY_TYPE :
        retVal = "A2D4F623";
        break;
      case SimpleTypesFactory.BASE64BINARY_TYPE:
        retVal = "qwertyui";
        break;
      case SimpleTypesFactory.ANYURI_TYPE :
        retVal = "www.exchangerxml.com";
        break;
        
        
      case SimpleTypesFactory.NMTOKEN_TYPE :
        retVal = "nmtoken";
        break;

      case SimpleTypesFactory.NMTOKENS_TYPE :
        retVal = "nmtokens";
        break;

        
      case SimpleTypesFactory.ENTITY_TYPE :
        retVal = "entity";
        break;

      case SimpleTypesFactory.ENTITIES_TYPE :
        retVal = "entities";
        break;

        
      case SimpleTypesFactory.ID_TYPE :
        retVal = "id";
        break;

      case SimpleTypesFactory.IDREF_TYPE :
        retVal = "idref";
        break;

      case SimpleTypesFactory.IDREFS_TYPE :
        retVal = "idrefs";
        break;

    }
    

    
    return retVal;
  }
  
  void handleSimpleType(SimpleType st, Element parent)
  {
    if (DEBUG)
      System.out.println("handleSimpleType:");
    
    if (generateData != true)
      return;
    
    String retVal = "";
    
    if (st instanceof Union)
    {
      if (DEBUG)
        System.out.println("handleSimpleType: union");
 
      Union union = (Union)st;
      Enumeration en = union.getMemberTypes();

      while (en != null && en.hasMoreElements())
      {
        SimpleType st2 = (SimpleType)en.nextElement();
        if (DEBUG)
          System.out.println("handleSimpleType: union simple type" + st2.getName());

        
        handleSimpleType( st2,  parent);
        break;
      }
      
      return;
    }
 
    //TODO Handle List
    
    
    if (st instanceof ListType)
    {
      if (DEBUG)
        System.out.println("handleSimpleType: list");
 
      ListType list = (ListType)st;
      
      SimpleType st2 = list.getItemType();
      
      return;
    }
    
    Enumeration en = st.getFacets();
    
    while (en != null && en.hasMoreElements())
    {
      
      Facet facet = (Facet)en.nextElement();
      if (DEBUG)
        System.out.println("facet name: " + facet.getName() + "facet val: " + facet.getValue());

      if ( facet.getName().equals(Facet.ENUMERATION) && !(facet.getValue().equals("")))
      {
        retVal = facet.getValue();
        parent.addText(retVal);
        return;
        
        
      }
    }
    
    int code = -1;
    
    SimpleType base = st.getBuiltInBaseType();
    
    if (base != null)
    {
      if (DEBUG)
        System.out.println("handleSimpleType: baseType " + base.getName());

      code = base.getTypeCode();
      
      if (DEBUG)
        System.out.println(" base.getTypeCode" + code);

     }
    else   
      code = st.getTypeCode();
  
    
    switch (code)
    {
      case SimpleTypesFactory.INTEGER_TYPE :
        retVal = "29";
        break;
        
      case SimpleTypesFactory.LONG_TYPE :
        retVal = "9223372036854775801";
        break;
        
      case SimpleTypesFactory.NEGATIVE_INTEGER_TYPE :
        retVal = "-293456";
        break;
        
      case SimpleTypesFactory.NON_NEGATIVE_INTEGER_TYPE :
        retVal = "393456";
        break;
        
      case SimpleTypesFactory.POSITIVE_INTEGER_TYPE :
        retVal = "292929";
        break;
        
      case SimpleTypesFactory.NON_POSITIVE_INTEGER_TYPE :
        retVal = "-292929";
        break;
        
      case SimpleTypesFactory.INT_TYPE :
        retVal = "2147483641";
        break;
        
      case SimpleTypesFactory.SHORT_TYPE :
        retVal = "32760";
        break;
        
      case SimpleTypesFactory.BYTE_TYPE :
        retVal = "123";
        break;
        
      case SimpleTypesFactory.UNSIGNED_INT_TYPE :
        retVal = "2147483642";
        break;
        
      case SimpleTypesFactory.UNSIGNED_LONG_TYPE :
        retVal = "9223372036854775801";
        break;
        
      case SimpleTypesFactory.UNSIGNED_SHORT_TYPE :
        retVal = "32761";
        break;
        
      case SimpleTypesFactory.UNSIGNED_BYTE_TYPE :
        retVal = "124";
        break;
        
      case SimpleTypesFactory.STRING_TYPE :
        retVal = "HelloWorld";
        break;
        
      case SimpleTypesFactory.DECIMAL_TYPE :
        retVal = "7.08";
        break;
        
      case SimpleTypesFactory.BOOLEAN_TYPE :
        retVal = "true";
        break;
        
      case SimpleTypesFactory.FLOAT_TYPE :
        retVal = "12.56E";
        break;
        
      case SimpleTypesFactory.DOUBLE_TYPE :
        retVal = "12.56E";
        break;
        
      case SimpleTypesFactory.DURATION_TYPE :
        retVal = "P1Y2M3DT10H30M";
        break;
        
      case SimpleTypesFactory.DATETIME_TYPE :
        retVal = "2005-03-29T13:24:36.000";
        break;
      case SimpleTypesFactory.TIME_TYPE :
        retVal = "13:24:36.000";
        break;
      case SimpleTypesFactory.DATE_TYPE :
        retVal = "2005-03-29";
        break;
      case SimpleTypesFactory.GYEARMONTH_TYPE :
        retVal = "2005-03";
        break;
      case SimpleTypesFactory.GYEAR_TYPE :
        retVal = "2005";
        break;
      case SimpleTypesFactory.GMONTH_TYPE :
        retVal = "--03--";
        break;
      case SimpleTypesFactory.GMONTHDAY_TYPE :
        retVal = "--03-29";
        break;
      case SimpleTypesFactory.GDAY_TYPE :
        retVal = "---29";
        break;
      case SimpleTypesFactory.HEXBINARY_TYPE :
        retVal = "A2D4F623";
        break;
      case SimpleTypesFactory.BASE64BINARY_TYPE:
        retVal = "qwertyui";
        break;
      case SimpleTypesFactory.ANYURI_TYPE :
        retVal = "www.exchangerxml.com";
        break;
    }
    
    
    
    parent.addText(retVal);
    
  }
  
  
  protected void buildComplexPart(ComplexType complexType, Element partElem)
  {
    //System.out.println("ComplexType: " + complexType.getName());
    
    nested++;
    
    if (nested > 20) return;
    
    XMLType baseType = complexType.getBaseType();
    if (baseType != null && baseType.isComplexType())
    {
      buildComplexPart((ComplexType) baseType, partElem);
    }
    
    // find the group
    Enumeration particleEnum = complexType.enumerate();
    Group group = null;
    
    while (particleEnum.hasMoreElements())
    {
      Particle particle = (Particle) particleEnum.nextElement();
      
      if (particle instanceof Group)
      {
        group = (Group) particle;
        break;
      }
    }
    
    if (group != null)
    {
      Enumeration groupEnum = group.enumerate();
      
      while (groupEnum.hasMoreElements())
      {
        Structure item = (Structure) groupEnum.nextElement();
        
        if (item.getStructureType() == Structure.ELEMENT)
        {
          ElementDecl elementDecl = (ElementDecl) item;
          
          String namespace = "";
          
          Schema s = elementDecl.getSchema();
          Form f = s.getElementFormDefault();
          
          if (elementDecl.isReference())
          {
            ElementDecl ref = elementDecl.getReference();
            s = ref.getSchema();
            
            namespace = s.getTargetNamespace();
          }
          else if (isGlobal(elementDecl) || (f != null && f.isQualified()))
          {
            namespace = s.getTargetNamespace();
          }
          else
          {
            namespace = "";
          }
          
          //Namespace currNamespace = root.getNamespaceForURI(namespace);
          
          // build the element that will be added to the message
          
          QName qname = null;
          String name = elementDecl.getName();
          
          String prefix = (String) map.get(namespace);
          
          //System.out.println("name: " + elementDecl.getName() + "  namespace: "+ namespace + "  prefix: " + prefix);
          
          //System.out.println(" check namespace");
          
          if (namespace != null && !namespace.equalsIgnoreCase("")
          && prefix != null && prefix != "")
          {
            
            //System.out.println(" check namespace: not null prefix not null");
            
            Namespace newNs;
            try
            {
              newNs = new Namespace(prefix, namespace);
              qname = new QName(name, newNs);
            }
            catch (StringIndexOutOfBoundsException e1)
            {
              //cannot parse string
              qname = new QName(name);
            }
            
          }
          else if (namespace != null && !namespace.equalsIgnoreCase(""))
          {
            
            //System.out.println(" check namespace: prefix null");
            
            Namespace newNs;
            try
            {
              newNs = new Namespace("", namespace);
              qname = new QName(name, newNs);
            }
            catch (StringIndexOutOfBoundsException e1)
            {
              //cannot parse string
              qname = new QName(name);
            }
            
          }
          else
          {
            //System.out.println(" check namespace:  null");
            
            qname = new QName(name);
          }
          
          //System.out.println("new XElement");
          
          XElement childElem = new XElement(qname);
          
          //System.out.println("partElem.add(childElem)");
          
          partElem.add(childElem);
          
          XMLType xmlType = elementDecl.getType();
          
          if (xmlType != null && xmlType.isComplexType())
          {
            //System.out.println("recurse");
            // recurse
            buildComplexPart((ComplexType) xmlType, childElem);
          }
          else if (xmlType != null && xmlType.isSimpleType())
          {
            
            //System.out.println("simple tpye");
            
            // add some default content as just a place holder
            String defaultContent = xmlType.getName();
            if (defaultContent != null)
            {
              //System.out.println("childElem.addText(defaultContent)");
              childElem.addText(defaultContent);
            }
            
          }
          
          //partElem.appendChild(childElem);
          
        }
      }
    }
    
    nested--;
  }
  
  public boolean isGlobal(ElementDecl element)
  {
    Schema s = element.getSchema();
    
    Enumeration enumeration = s.getGlobalElements();
    
    while (enumeration.hasMoreElements())
    {
      ElementDecl e = (ElementDecl) enumeration.nextElement();
      if (e.equals(element))
      { return true; }
    }
    
    return false;
  }
  
  public String processXSD(URL url, String rootElement,
      boolean optionalAttributes, boolean optionalElements, boolean generateData)
  {
    
    XmlOptions options = new XmlOptions();
    options.setCompileDownloadUrls();
    EntityResolver resolver = new MyResolver();
    options.setEntityResolver(resolver);
    
    SchemaTypeSystem sts = null;
    try
    {
      sts = XmlBeans.compileXsd(
          new XmlObject[] { XmlObject.Factory.parse(url)}, XmlBeans
              .getBuiltinTypeSystem(), options);
    }
    catch (XmlException eX)
    {
      System.err.println(url + " not loadable: " + eX.getMessage()
      + eX.getStackTrace());
      System.err.println(eX.getError().getMessage());
    }
    catch (Exception e)
    {
      System.err.println(url + " not loadable: " + e.getMessage()
      + e.getStackTrace());
      
    }
    
    Map prefixes = new HashMap();
    prefixes.put("http://www.w3.org/XML/1998/namespace", "xml");
    prefixes.put("http://www.w3.org/2001/XMLSchema", "xs");
    
    // breadthfirst traversal of the type containment tree
    List allGlobalElements = new ArrayList();
    allGlobalElements.addAll(Arrays.asList(sts.globalElements()));
    
    try
    {
      document = getDocument();
    }
    catch (Exception e)
    {
      System.err.println("getDom: " + e);
    }
    
    if (rootElement == null || (rootElement.equals("")))
    {
      //System.out.println("no root element so create one ");
      root = document.addElement("root");
    } //else
    //System.out.println("root element:  " + rootElement);
    
    for (int i = 0; i < allGlobalElements.size(); i++)
    {
      SchemaGlobalElement sGlobalElement = (SchemaGlobalElement) allGlobalElements
          .get(i);
      
      if (rootElement != null && !(rootElement.equals("")))
      {
        if (sGlobalElement.getName().getLocalPart().equals(rootElement))
        {
          //System.out.println("found " + rootElement);
          processGlobalElement(sGlobalElement, root);
        }
      }
      else
      {
        //System.out.println("found " +
        // sGlobalElement.getName().getLocalPart());
        processGlobalElement(sGlobalElement, root);
      }
    }
    
    //System.out.println(document.asXML());
    
    return document.asXML();
    
    /*
     *
     * List allGlobalTypes = new ArrayList();
     * allGlobalTypes.addAll(Arrays.asList(sts.globalTypes()));
     *
     * for (int i = 0; i < allGlobalTypes.size(); i++) { SchemaType sType =
     * (SchemaType)allGlobalTypes.get(i); //System.out.println("type name = " +
     * sType.getName() + " isSimple=" + sType.isSimpleType()); }
     */
    
    //WindowUtilities.openInJFrame(new GraphicalSchemaViewer(), 380, 400);
  }
  
  private void processGlobalElement(SchemaGlobalElement sGlobalElement,
      Element parent)
  {
    
    try
    {
      //System.out.println("el name = " + sGlobalElement.getName() );
      Element currElement = null;
      
      if (parent == null)
        currElement = document.addElement(sGlobalElement.getName()
        .getLocalPart());
      else
        currElement = parent
            .addElement(sGlobalElement.getName().getLocalPart());
      //newNode.setValue(value);
      
      SchemaType sType = (SchemaType) sGlobalElement.getType();
      
      //System.out.println(" type name = " + sType.getName() + " isSimple=" +
      // sType.isSimpleType() );
      
      handleSchemaType(sType, currElement);
      
    }
    catch (Exception e)
    {
      System.err.println("processGlobalElement:" + e);
    }
    
  }
  
  private Document getDocument()
  {
    
    Document document = DocumentHelper.createDocument();
    
    return document;
  }
  
  private void handleSchemaParticle(SchemaParticle schemaParticle,
      Element currElement)
  {
    //System.out.println("In handleSchemaParticle");
    
    int schemaParticleType = schemaParticle.getParticleType();
    int maxOccurs = 1;
    int minOccurs = 1;
    
    switch (schemaParticleType)
    {
      case SchemaParticle.ALL:
        //System.out.println(" SchemaParticle.ALL");
        break;
        
      case SchemaParticle.CHOICE:
        //System.out.println(" SchemaParticle.CHOICE ");
        break;
        
      case SchemaParticle.SEQUENCE:
        //System.out.println(" SchemaParticle.SEQUENCE ");
        break;
        
      case SchemaParticle.ELEMENT:
        //System.out.println(" SchemaParticle.ELEMENT ");
        break;
        
      case SchemaParticle.WILDCARD:
        //System.out.println(" SchemaParticle.WILDCARD ");
        break;
        
    }
    
    switch (schemaParticleType)
    {
      case SchemaParticle.ALL:
      case SchemaParticle.CHOICE:
      case SchemaParticle.SEQUENCE:
      case SchemaParticle.ELEMENT:
      case SchemaParticle.WILDCARD:
        maxOccurs = schemaParticle.getIntMaxOccurs();
        minOccurs = schemaParticle.getIntMinOccurs();
        break;
        
    }
    
    switch (schemaParticleType)
    {
      case SchemaParticle.ALL:
      case SchemaParticle.CHOICE:
      case SchemaParticle.SEQUENCE:
        
        SchemaParticle[] children = schemaParticle.getParticleChildren();
        
        for (int i = 0; i < children.length; i++)
        {
          //System.out.println("i:" + i);
          if (i == 0 || schemaParticleType != SchemaParticle.CHOICE)
            handleSchemaParticle(children[i], currElement);
        }
        break;
        
      case SchemaParticle.ELEMENT:
        Element tempElement = currElement;
        //System.out.println("el:" + schemaParticle.getName().getLocalPart());
        currElement = tempElement.addElement(schemaParticle.getName()
        .getLocalPart());
        
        SchemaType sType = schemaParticle.getType();
        
        if (sType != null)
        {
          handleSchemaType(sType, currElement);
          
        }
        
        currElement = tempElement;
        break;
    }
    
    //System.out.println("Returning from handleSchemaParticle");
    
  }
  
  private String generateSimpleContent(SchemaType sType)
  {
    String retVal = "";
    SchemaType pType = sType.getPrimitiveType();
    //System.out.println("simple content - atomic type: " + pType.getName()
    //    + "  base type: " + sType.getBaseType().getName());
    
    XmlAnySimpleType facet = null;
    String length = null;
    String fractionDigits = null;
    String maxExclusive = null;
    String maxInclusive = null;
    String maxLength = null;
    String minExclusive = null;
    String minInclusive = null;
    String minLength = null;
    String totalDigits = null;
    
    String[] patterns = null;
    XmlAnySimpleType[] enumValues = null;
    
    facet = sType.getFacet(SchemaType.FACET_LENGTH);
    if (facet != null) length = facet.getStringValue();
    
    facet = sType.getFacet(SchemaType.FACET_FRACTION_DIGITS);
    if (facet != null) fractionDigits = facet.getStringValue();
    
    facet = sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
    if (facet != null) maxExclusive = facet.getStringValue();
    
    facet = sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
    if (facet != null) maxInclusive = facet.getStringValue();
    
    facet = sType.getFacet(SchemaType.FACET_MAX_LENGTH);
    if (facet != null) maxLength = facet.getStringValue();
    
    facet = sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
    if (facet != null) minExclusive = facet.getStringValue();
    
    facet = sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
    if (facet != null) minInclusive = facet.getStringValue();
    
    facet = sType.getFacet(SchemaType.FACET_MIN_LENGTH);
    if (facet != null) minLength = facet.getStringValue();
    
    facet = sType.getFacet(SchemaType.FACET_TOTAL_DIGITS);
    if (facet != null) totalDigits = facet.getStringValue();
    
    //sType.getFacet(SchemaType.FACET_PATTERN );
    if (sType.hasPatternFacet())
    {
      patterns = sType.getPatterns();
      
      for (int i = 0; i < patterns.length; i++)
        System.out.println(patterns[i]);
      
      //sType.matchPatternFacet("") ;
    }
    
    //sType.getFacet(SchemaType.FACET_ENUMERATION );
    
    enumValues = sType.getEnumerationValues();
    if (enumValues != null)
    {
      //for (int i = 0; i < enumValues.length; i++)
      // System.out.println(enumValues[i].getStringValue());
      
      return enumValues[random.nextInt(enumValues.length)].getStringValue();
      
    }
    
    //sType.getFacet(SchemaType.FACET_WHITE_SPACE );
    sType.getWhiteSpaceRule();
    
    int typeCode = sType.getPrimitiveType().getBuiltinTypeCode();
    
    if (sType.getBaseType().isBuiltinType())
      typeCode = sType.getBaseType().getBuiltinTypeCode();
    
    switch (typeCode)
    {
      case SchemaType.BTC_INTEGER:
        retVal = "29";
        break;
        
      case SchemaType.BTC_LONG:
        retVal = "9223372036854775801";
        break;
        
      case SchemaType.BTC_NEGATIVE_INTEGER:
        retVal = "-293456";
        break;
        
      case SchemaType.BTC_NON_NEGATIVE_INTEGER:
        retVal = "393456";
        break;
        
      case SchemaType.BTC_POSITIVE_INTEGER:
        retVal = "292929";
        break;
        
      case SchemaType.BTC_NON_POSITIVE_INTEGER:
        retVal = "-292929";
        break;
        
      case SchemaType.BTC_INT:
        retVal = "2147483641";
        break;
        
      case SchemaType.BTC_SHORT:
        retVal = "32760";
        break;
        
      case SchemaType.BTC_BYTE:
        retVal = "123";
        break;
        
      case SchemaType.BTC_UNSIGNED_INT:
        retVal = "2147483642";
        break;
        
      case SchemaType.BTC_UNSIGNED_LONG:
        retVal = "9223372036854775801";
        break;
        
      case SchemaType.BTC_UNSIGNED_SHORT:
        retVal = "32761";
        break;
        
      case SchemaType.BTC_UNSIGNED_BYTE:
        retVal = "124";
        break;
        
      case SchemaType.BTC_STRING:
        retVal = "HelloWorld";
        break;
        
      case SchemaType.BTC_DECIMAL:
        retVal = "7.08";
        break;
        
      case SchemaType.BTC_BOOLEAN:
        retVal = "true";
        break;
        
      case SchemaType.BTC_FLOAT:
        retVal = "12.56E";
        break;
        
      case SchemaType.BTC_DOUBLE:
        retVal = "12.56E";
        break;
        
      case SchemaType.BTC_DURATION:
        retVal = "";
        break;
      case SchemaType.BTC_DATE_TIME:
        retVal = "2005-03-29T13:24:36.000";
        break;
      case SchemaType.BTC_TIME:
        retVal = "13:24:36.000";
        break;
      case SchemaType.BTC_DATE:
        retVal = "2005-03-29";
        break;
      case SchemaType.BTC_G_YEAR_MONTH:
        retVal = "2005-03";
        break;
      case SchemaType.BTC_G_YEAR:
        retVal = "2005";
        break;
      case SchemaType.BTC_G_MONTH:
        retVal = "--03--";
        break;
      case SchemaType.BTC_G_MONTH_DAY:
        retVal = "--03-29";
        break;
      case SchemaType.BTC_G_DAY:
        retVal = "---29";
        break;
      case SchemaType.BTC_HEX_BINARY:
        retVal = "A2D4F623";
        break;
      case SchemaType.BTC_BASE_64_BINARY:
        retVal = "ERTDETR23SR43EGREHYTYHB";
        break;
      case SchemaType.BTC_ANY_URI:
        retVal = "www.exchangerxml.com";
        break;
        
    }
    
    return retVal;
  }
  
  private void handleSchemaType(SchemaType sType, Element currElement)
  {
    //System.out.println("In handleSchemaType");
    
    if (!sType.isSimpleType())
    {
      
      switch (sType.getContentType())
      {
        case SchemaType.ELEMENT_CONTENT:
          
          //System.out.println(" ELEMENT_CONTENT");
          break;
          
        case SchemaType.MIXED_CONTENT:
          //System.out.println(" MIXED_CONTENT");
          break;
          
        case SchemaType.EMPTY_CONTENT:
          //System.out.println(" EMPTY_CONTENT");
          break;
          
        case SchemaType.SIMPLE_CONTENT:
          
          //System.out.println(" SIMPLE_CONTENT");
          break;
      }
      
      switch (sType.getContentType())
      {
        case SchemaType.ELEMENT_CONTENT:
        case SchemaType.MIXED_CONTENT:
          
          SchemaParticle schemaParticle = sType.getContentModel();
          
          handleSchemaParticle(schemaParticle, currElement);
          
          /*
           * List allElementProperties = new ArrayList();
           * allElementProperties.addAll(Arrays.asList(sType.getElementProperties()));
           *
           * for (int j = 0; j < allElementProperties.size(); j++) {
           * SchemaProperty sProperty =
           * (SchemaProperty)allElementProperties.get(j); System.out.println("
           * prop name=" + sProperty.getName());
           *  }
           */
          break;
          
        case SchemaType.EMPTY_CONTENT:
          break;
          
        case SchemaType.SIMPLE_CONTENT:
          currElement.addText(generateSimpleContent(sType));
          break;
          
      }
      
      switch (sType.getContentType())
      {
        case SchemaType.ELEMENT_CONTENT:
        case SchemaType.MIXED_CONTENT:
        case SchemaType.EMPTY_CONTENT:
        case SchemaType.SIMPLE_CONTENT:
          List allAttributeProperties = new ArrayList();
          allAttributeProperties.addAll(Arrays.asList(sType
              .getAttributeProperties()));
          
          for (int j = 0; j < allAttributeProperties.size(); j++)
          {
            SchemaProperty sProperty = (SchemaProperty) allAttributeProperties
                .get(j);
            //System.out.println(" attr name=" + sProperty.getName());
            
            currElement.addAttribute(sProperty.getName().getLocalPart(), "");
          }
          
          break;
          
      }
    }
    else
    {
      
      int variety = sType.getSimpleVariety();
      
      switch (variety)
      {
        case SchemaType.ATOMIC:
          SchemaType pType = sType.getPrimitiveType();
          //System.out.println("atomic type: " + pType.getName());
          
          XmlAnySimpleType facet = null;
          String length = null;
          String fractionDigits = null;
          String maxExclusive = null;
          String maxInclusive = null;
          String maxLength = null;
          String minExclusive = null;
          String minInclusive = null;
          String minLength = null;
          String totalDigits = null;
          
          String[] patterns = null;
          XmlAnySimpleType[] enumValues = null;
          
          facet = sType.getFacet(SchemaType.FACET_LENGTH);
          if (facet != null) length = facet.getStringValue();
          
          facet = sType.getFacet(SchemaType.FACET_FRACTION_DIGITS);
          if (facet != null) fractionDigits = facet.getStringValue();
          
          facet = sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
          if (facet != null) maxExclusive = facet.getStringValue();
          
          facet = sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
          if (facet != null) maxInclusive = facet.getStringValue();
          
          facet = sType.getFacet(SchemaType.FACET_MAX_LENGTH);
          if (facet != null) maxLength = facet.getStringValue();
          
          facet = sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
          if (facet != null) minExclusive = facet.getStringValue();
          
          facet = sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
          if (facet != null) minInclusive = facet.getStringValue();
          
          facet = sType.getFacet(SchemaType.FACET_MIN_LENGTH);
          if (facet != null) minLength = facet.getStringValue();
          
          facet = sType.getFacet(SchemaType.FACET_TOTAL_DIGITS);
          if (facet != null) totalDigits = facet.getStringValue();
          
          //sType.getFacet(SchemaType.FACET_PATTERN );
          if (sType.hasPatternFacet())
          {
            patterns = sType.getPatterns();
            
            //for (int i = 0; i < patterns.length; i++)
            //  System.out.println(patterns[i]);
            
            //sType.matchPatternFacet("") ;
          }
          
          //sType.getFacet(SchemaType.FACET_ENUMERATION );
          
          enumValues = sType.getEnumerationValues();
          if (enumValues != null)
          {
            //for (int i = 0; i < enumValues.length; i++)
            //  System.out.println(enumValues[i].getStringValue());
            
            currElement.addText(enumValues[random.nextInt(enumValues.length)]
                .getStringValue());
            
          }
          
          //sType.getFacet(SchemaType.FACET_WHITE_SPACE );
          sType.getWhiteSpaceRule();
          
          break;
          
        case SchemaType.UNION:
          break;
          
        case SchemaType.LIST:
          break;
          
        case SchemaType.NOT_SIMPLE:
        default:
          break;
      }
      
    }
    
    //System.out.println("Returning from handleSchemaType");
  }
  
}

class MyResolver implements EntityResolver
{
  
  public InputSource resolveEntity(String publicId, String systemId)
  {
    //System.out.println("called resolveEntity publicId:" + publicId
    //    + " systemid:" + systemId);
    
    if (systemId != null)
    {
      // return a special input source
      //MyReader reader = new MyReader();
      //System.out.println("looking for " + systemId);
      return new InputSource(systemId);
    }
    else
    {
      // use the default behaviour
      return null;
    }
  }
}
