/*
 * $Id: XMLSupport.java,v 1.3 2004/04/05 17:01:31 knesbitt Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.
 * Use is subject to license terms.
 */
package com.cladonia.xml.webservice.wsdl;

import java.io.StringReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;


import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.reader.SchemaReader;

import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Utility methods for creating a Castor type schema and parsing, serialising XML
 *
 * @version	$Revision: 1.3 $, $Date: 2004/04/05 17:01:31 $
 * @author Dogs bay
 */
public class XMLSupport
{
   private XMLSupport()
   {
   }

   /**
    * Utility method for serialising an Element into an XML string
    * @param ele The Element object to serialise
    *
    * @return The serialised Element
    */
   public static String serialise(Element ele) throws WSDLException
   {
      return serialise((Node)ele);
   }

	/**
    * Utility method for serialising a Node into an XML string
    * @param node The Node object to serialise
    *
    * @return The serialised Node
    */
   public static String serialise(Node node) throws WSDLException
	{
       return serialise(new DOMSource(node));
   }

   /**
    * Utility function for serialising a DOM into an XML string
    * @param source The TrAX Source object to serialise
    *
    * @return The serialised XML
    */
   public static String serialise(javax.xml.transform.Source source) 
   		throws WSDLException
	{

   	try {
   		javax.xml.transform.TransformerFactory factory =
   	    javax.xml.transform.TransformerFactory.newInstance();
         javax.xml.transform.Transformer transformer = factory.newTransformer();

       
         // use TrAX to convert DOM into serialised XML
         java.io.StringWriter writer = new java.io.StringWriter();
         javax.xml.transform.stream.StreamResult result =
         new javax.xml.transform.stream.StreamResult(writer);

         // use the transformer to serialise the XML content
         transformer.transform(source, result);

         // return the result as an XML string
         return writer.toString();
       }
       catch (javax.xml.transform.TransformerConfigurationException e) 
	   {
           // JAXP/TrAX is not configured properly for this system
           throw new WSDLException(e);
       }
       catch (javax.xml.transform.TransformerException e) 
	   {
           // Problem with the DOM?
           throw new WSDLException(e);
       }
	}
   
   public static String prettySerialise(Document doc) throws WSDLException
   {
   		try{
   	
   		//  Serialise the document
   		OutputFormat format = new OutputFormat(doc);
   		format.setLineWidth(65);
   		format.setIndenting(true);
   		format.setIndent(2);
   		StringWriter writer = new StringWriter();
   		XMLSerializer serializer = new XMLSerializer(writer, format);
   		serializer.serialize(doc);
   		
   		return writer.toString();
   		
   		}
   		catch(Exception e)
		{
   			throw new WSDLException(e);
   		}
   }


  /**
    * Utility method: Parses an XML string into a Document.
    * @param xml Serialised XML document.
    * @return Parsed document (as a DOM).
    * @throws Exception on parsing error.
    */
  public static Document parse(String xml) throws WSDLException
  {
  	try
	{
  		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  		factory.setNamespaceAware(true);
  		DocumentBuilder builder = factory.newDocumentBuilder();

  		// use inner error handler.
  		SAXErrorHandler errors = new SAXErrorHandler();
  		builder.setErrorHandler(errors);

  		// create a stream source that encapsulates our serialised message
  		StringReader reader = new StringReader(xml);
  		BufferedReader breader = new BufferedReader(reader);
  		InputSource source = new InputSource(breader);

  		// parse and store the result as our DOM reference
  		Document doc = builder.parse(source);

  		// trap any parse errors caught by the ErrorHandler methods.
  		errors.throwException();

  		return doc;
	}
  	catch (ParserConfigurationException e) {
  		// JAXP/TrAX is not configured properly for this system
  		throw new WSDLException(e);
  	}
  	catch (SAXException e) {
  		// the input document is not well-formed XML
  		throw new WSDLException(e);
  	}
  	catch (IOException e) {
  		// should never happen - we're not doing any I/O.
  		throw new WSDLException(e);
  	}
  	catch (Throwable t){
  		// should never happen, but might if classpath is wrong
  		throw new WSDLException(t);
  	}
  }

  /**
   * Converts the dom element into a castor schema.
   *
   * @param   element  Th element to be converted into a castor schema.
   *
   * @return  The castor schema corresponding to the element.
   * @throws  Exception  If the element could not be written out.
   */
  public static Schema convertElementToSchema(Element element) throws Exception
  {
     // get the string content of the element
     String content = serialise(element);

     // check for null value
     if (content != null)
     {
        // create a schema from the string content
        return readSchema(new StringReader(content));
     }

     // otherwise return null
     return null;
  }

  /**
   * It reads the given reader and returns the castor schema.
   *
   * @param   reader  The reader to read.
   *
   * @return  The castor schema created from the reader.
   *
   * @throws  Exception If the schema could not be read from the reader.
   */
  public static Schema readSchema(Reader reader) throws Exception
  {
     // create the sax input source
     InputSource inputSource = new InputSource(reader);

     // create the schema reader
     SchemaReader schemaReader = new SchemaReader(inputSource);
     schemaReader.setValidation(false);

     // read the schema from the source
     Schema schema = schemaReader.read();

     return schema;
  }
}

class SAXErrorHandler implements ErrorHandler
{

  	public final static int WARNING = 0;
  	public final static int ERROR   = 1;
  	public final static int FATAL   = 2;


  	private SAXParseException err = null;
  	private int level = FATAL;

  	/**
  	 * Creates a new SAX ErrorHandler instance.
  	 * @param level Level to trap errors at.
  	 */
  	public SAXErrorHandler(int level) {
  		this.level = level;
  	}

  	/**
  	 * Creates a new SAX ErrorHandler instance.
  	 */
  	public SAXErrorHandler() {

  	}

  	/**
  	 * Clears any error contained within this handler.
  	 */
  	public void clear() {
  		err = null;
  	}

  	/**
  	 * Throws any parse error that was reported to this handler.
  	 * The exception is always cleared before this method returns.
  	 * @throws SAXParseException contained within this handler.
  	 */
  	public void throwException() throws SAXParseException {
  		if (err != null) {
  			SAXParseException e = err;
  			err = null;
  			throw e;
  		}
  	}

  	/**
  	 * Returns any parse error that was reported to this handler.
  	 * The exception is always cleared before this method returns.
  	 * @return SAXParseException contained within this handler.
  	 */
  	public SAXParseException getException() {
  		SAXParseException e = err;
  		err = null;
  		return e;
  	}

  	/**
  	 * Implements the org.xml.sax.ErrorHandler interface.
  	 * This method is used for internal parsing but must be made public to
  	 * complete the interface. <u><i>DO NOT USE IT!</i></u>
  	 */
  	public void warning(SAXParseException e) {
  		if (level <= WARNING) err = e;
  	}

  	/**
  	 * Implements the org.xml.sax.ErrorHandler interface
  	 * This method is used for internal parsing but must be made public to
  	 * complete the interface. <u><i>DO NOT USE IT!</i></u>
  	 */
  	public void error(SAXParseException e) {
  		if (level <= ERROR) err = e;
  	}

  	/**
  	 * Implements the org.xml.sax.ErrorHandler interface
  	 * This method is used for internal parsing but must be made public to
  	 * complete the interface. <u><i>DO NOT USE IT!</i></u>
  	 */
  	public void fatalError(SAXParseException e) {
  		err = e;
  	}

}

