/*
 * $Id: XMLUtilities.java,v 1.28 2005/09/05 13:58:15 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml;

import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Map;
import javax.xml.transform.TransformerException;


import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.dtd.XMLDTDLoader;
import org.apache.xerces.impl.io.UCSReader;
import org.apache.xerces.parsers.XMLGrammarPreparser;
import org.apache.xerces.util.EncodingMap;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.parsers.XIncludeParserConfiguration;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xml.resolver.Catalog;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.utils.XMLUtils;



import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Attribute;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;



import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.properties.TextPreferences;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

import com.thaiopensource.relaxng.SchemaFactory;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.auto.AutoSchemaReader;
import com.thaiopensource.validate.prop.rng.RngProperty;
import com.thaiopensource.validate.rng.CompactSchemaReader;
import com.thaiopensource.xml.sax.Sax2XMLReaderCreator;
import com.thaiopensource.xml.sax.XMLReaderCreator;

/**
 * Utilities for reading and writing of XML documents.
 *
 * @version	$Revision: 1.28 $, $Date: 2005/09/05 13:58:15 $
 * @author Dogsbay
 */
public class XMLUtilities {
	private static final boolean DEBUG = false;
	
	private static final int CHECK_ID_IDREF = 01;
	private static final int COMPACT_SYNTAX = 02;
	private static final int FEASIBLE = 04;

	// standard java...	
	private static final String UTF_8		= "UTF-8";
	private static final String UTF_16		= "UTF-16";
	private static final String UTF_16BE	= "UTF-16BE";
	private static final String UTF_16LE	= "UTF-16LE";
	private static final String EBCDIC		= "EBCDIC-CP-US";

	private static final String UCS_4BE		= "UCS-4BE";
	private static final String UCS_4LE		= "UCS-4LE";
	private static final String UNKNOWN		= null;

	private static SAXReader validatingReader = null;
	private static SAXReader nonValidatingReader = null;
	private static SAXReader nonValidatingExternalDTDReader = null;
	
	private static boolean loadDTDGrammar = false;
	private static boolean resolveEntities = true;

	/** Filters property identifier. */
    protected static final String FILTERS = 
    	"http://cyberneko.org/html/properties/filters";
    
    private static final String SYSTEM = "SYSTEM";
	private static final String PUBLIC = "PUBLIC";
	private static final String INTERNAL = "INTERNAL";	
	
	private static final String SCHEMAINSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String SCHEMALOCATION = "schemaLocation";
	private static final String NOSCHEMALOCATION = "noNamespaceSchemaLocation";
	
	/**
	 * Returns the XML reader, true when validating and false for 
	 * not validating.
	 *
	 * @param validate true when the XML reader should validate.
	 *
	 * @return the reader.
	 */
	public static SAXReader getReader( boolean validate) {
		SAXReader result = null;

		if ( validate) {
			if ( validatingReader == null) {
				validatingReader = createReader( true, false);
			}
			
			result = validatingReader;
		} else if ( loadDTDGrammar) {
			if ( nonValidatingExternalDTDReader == null) {
				nonValidatingExternalDTDReader = createReader( false, true);
			}

			result = nonValidatingExternalDTDReader;
		} else {
			if ( nonValidatingReader == null) {
				nonValidatingReader = createReader( false, false);
			}

			result = nonValidatingReader;
		}
		
		return result;
	}

	public static SAXReader createReader( boolean validate) {
		return createReader( validate, false);
	}

	/**
	 * Creates a new SAXReader.
	 *
	 * @param validate when true the reader validates the input.
	 *
	 * @return the reader.
	 */
	public static SAXReader createReader( boolean validate, boolean loadExternalDTD) {
		SAXReader reader = new SAXReader( XDocumentFactory.getInstance(), validate);
		
		reader.setStripWhitespaceText( false);
		reader.setMergeAdjacentText( true);
//		reader.setMergeAdjacentText( true);
		
		if ( !validate) {
			reader.setIncludeExternalDTDDeclarations( false);
			reader.setIncludeInternalDTDDeclarations( true);
		
			try {
				if ( loadExternalDTD) {
					reader.setFeature( "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", true);
//					System.out.println( "http://apache.org/xml/features/nonvalidating/load-external-dtd = "+reader.getXMLReader().getFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd"));
					reader.setEntityResolver( getCatalogResolver());
				} else {
					reader.setFeature( "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
					reader.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				reader.getXMLReader().setFeature( "http://apache.org/xml/features/validation/schema", true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return reader;
	}
	
	/**
	 * Creates a new SAXReader.
	 *
	 * @param validate when true the reader validates the input.
	 *
	 * @return the reader.
	 */
	public static SAXReader createReader( boolean validate, boolean loadExternalDTD, boolean stripWhiteSpace) {
		SAXReader reader = new SAXReader( XDocumentFactory.getInstance(), validate);
		
		reader.setStripWhitespaceText( stripWhiteSpace);
		reader.setMergeAdjacentText( true);
//		reader.setMergeAdjacentText( true);
		
		if ( !validate) {
			reader.setIncludeExternalDTDDeclarations( false);
			reader.setIncludeInternalDTDDeclarations( true);
		
			try {
				if ( loadExternalDTD) {
					reader.setFeature( "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", true);
//					System.out.println( "http://apache.org/xml/features/nonvalidating/load-external-dtd = "+reader.getXMLReader().getFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd"));
					reader.setEntityResolver( getCatalogResolver());
				} else {
					reader.setFeature( "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
					reader.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				reader.getXMLReader().setFeature( "http://apache.org/xml/features/validation/schema", true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return reader;
	}

	/**
	 * Reads the document for this URL.
	 *
	 * @param url the URL of the document.
	 *
	 * @return the text for the document.
	 */
	public static synchronized String getText( URL url) throws IOException {
		return getText( url, new XMLDeclaration());
	}
	
	public static void setLoadDTDGrammar( boolean load) {
		loadDTDGrammar = load;
	}

//	public static void setResolveEntities( boolean resolve) {
//		resolveEntities = resolve;
//	}

	/**
	 * Reads the URL stream into a String using the encoding provided.
	 *
	 * @param url the URL of the document.
	 * @param encoding the encoding for the stream.
	 *
	 * @return the text for the document.
	 */
//	public static synchronized String getText( URL url, String encoding) throws IOException {
//		String text = null;
//		
//		InputStream urlStream = url.openStream();
//		InputStreamReader reader = new InputStreamReader( urlStream , encoding);
//
//		int i = -1;
//
//		while ( (i = reader.read()) != -1) 
//		{
//			text += (char)i;
//		}
//		reader.close();
//		
//		return text;
//	}

	/**
	 * Reads the document for this URL and tries to find the encoding.
	 *
	 * @param url the URL of the document.
	 *
	 * @return the text for the document.
	 */
	public static synchronized String getText( URL url, XMLDeclaration decl) throws IOException {
		if (DEBUG) System.out.println( "XMLUtilities.getText( "+url+", "+decl+")");

		String result = null;
		
		BufferedInputStream stream = new BufferedInputStream( URLUtilities.open( url));

		Object[] objects = preParse( stream);
		
		String encoding = (String)objects[1];

		Reader reader = (BufferedReader)objects[0];
		
		decl.setEncoding( encoding);
		
		CharArrayWriter writer = new CharArrayWriter();
		
		int ch = reader.read();

		while ( ch != -1) {
			//if ( !Character.isDefined( (char)ch)) {
			if ( !Character.isDefined( ch)) {
				throw new IOException( "File contains illegal Characters");
			}

			writer.write( ch);
			
			ch = reader.read();
		}
		
		return writer.toString();
	}

	public static synchronized XDocument parseRemote( URL url) throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "XMLUtilities.parseRemote( "+url+")");

		XDocument document = null;
	
		try { 
			SAXReader reader = getReader( false);

			URLConnection connection = (URLConnection)url.openConnection();
			connection.setDefaultUseCaches( false);
			connection.setUseCaches( false);
			connection.connect();
			InputStream stream = connection.getInputStream();
			
			XMLReader xmlReader = replaceAmp( url);

			document = (XDocument)reader.read( xmlReader, url.toString());

			document.setEncoding( xmlReader.getEncoding());
			document.setVersion( xmlReader.getVersion());

			stream.close();
		} catch( DocumentException e) {
			Exception x = (Exception)e.getNestedException();
			
			if ( x instanceof SAXParseException) {
				SAXParseException spe = (SAXParseException)x;
				Exception ex = spe.getException();
				
				if ( ex instanceof IOException) {
					throw (IOException)ex;
				} else {
					throw (SAXParseException)x;
				}
			} else if ( x instanceof IOException) {
				throw (IOException)x;
			}
		}

		return document;
	}

	/**
	 * Parses the document for this URL.
	 *
	 * @param url the URL of the document.
	 *
	 * @return the Dom4J document.
	 */
//	public static synchronized XDocument parse( URL url) throws IOException, SAXParseException {
//		if (DEBUG) System.out.println( "XMLUtilities.parse( "+url+")");
//		
//		return parse( getReader( false), replaceAmp( url), url.toString());
//	}

	/**
	 * Parses the document for this reader.
	 *
	 * @param reader the reader with all the information.
	 * @param systemId the systemId of the document.
	 *
	 * @return the Dom4J document.
	 */
//	public static synchronized XDocument parse( ErrorHandler handler, BufferedReader reader, String systemId) throws IOException, SAXParseException {
//		if (DEBUG) System.out.println( "XMLUtilities.parse( "+reader+", "+systemId+")");
//		SAXReader saxReader = getReader( false);
//		saxReader.setErrorHandler( handler);
//
//		return parse( saxReader, replaceAmp( reader), systemId);
//	}

	/**
	 * Parses the document for this reader.
	 *
	 * @param reader the reader with all the information.
	 * @param systemId the systemId of the document.
	 *
	 * @return the Dom4J document.
	 */
	public static synchronized XDocument parse( BufferedReader reader, int length, String systemId, String grammarLocation, boolean stripWhiteSpace) throws IOException, SAXParseException{
		if (DEBUG) System.out.println( "XMLUtilities.parse( "+reader+", "+systemId+", "+grammarLocation+")");

		
		SAXReader saxReader = createReader( false, loadDTDGrammar, stripWhiteSpace);
		saxReader.setEntityResolver( new DummyEntityResolver( grammarLocation));
		
		if ( resolveEntities) {
			String encoding;

			try {
				reader.mark( length);
				encoding = getXMLDeclaration( reader).getEncoding();
			} finally {
				reader.reset();
			}
	
			reader.mark( length+1);
//			// parse without substituting the entities
			parse( saxReader, createReader( reader, encoding), systemId);
			reader.reset();
		}

		return parse( saxReader, replaceAmp( reader), systemId);
	}
	
	/**
	 * Parses the document for this reader.
	 *
	 * @param reader the reader with all the information.
	 * @param systemId the systemId of the document.
	 *
	 * @return the Dom4J document.
	 */
	public static synchronized XDocument parse( BufferedReader reader, int length, String systemId, String grammarLocation) throws IOException, SAXParseException{
		if (DEBUG) System.out.println( "XMLUtilities.parse( "+reader+", "+systemId+", "+grammarLocation+")");

		
		SAXReader saxReader = createReader( false, loadDTDGrammar);
		saxReader.setEntityResolver( new DummyEntityResolver( grammarLocation));
		
		if ( resolveEntities) {
			String encoding;

			try {
				reader.mark( length);
				encoding = getXMLDeclaration( reader).getEncoding();
			} finally {
				reader.reset();
			}
	
			reader.mark( length+1);
//			// parse without substituting the entities
			parse( saxReader, createReader( reader, encoding), systemId);
			reader.reset();
		}

		return parse( saxReader, replaceAmp( reader), systemId);
	}

	public static synchronized XDocument parse( BufferedReader reader, int length, String systemId) throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "XMLUtilities.parse( "+reader+", "+systemId+")");
		
		SAXReader saxReader = createReader( false, loadDTDGrammar);

		if ( resolveEntities) {
			String encoding;
	
			try {
				reader.mark( length+1);
				encoding = getXMLDeclaration( reader).getEncoding();
			} finally {
				reader.reset();
			}
			
//			 parse without substituting the entities
			reader.mark( length+1);
			parse( saxReader, createReader( reader, encoding), systemId);
			reader.reset();

		}

		return parse( saxReader, replaceAmp( reader), systemId);
	}
	
	public static synchronized XDocument parse( BufferedReader reader, int length, String systemId, boolean stripWhiteSpace) throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "XMLUtilities.parse( "+reader+", "+systemId+")");
		
		SAXReader saxReader = createReader( false, loadDTDGrammar, stripWhiteSpace);

		if ( resolveEntities) {
			String encoding;
	
			try {
				reader.mark( length+1);
				encoding = getXMLDeclaration( reader).getEncoding();
			} finally {
				reader.reset();
			}
		}

		return parse( saxReader, replaceAmp( reader), systemId);
	}

	/**
	 * Parses the document for this reader.
	 *
	 * @param reader the reader with all the information.
	 * @param systemId the systemId of the document.
	 *
	 * @return the Dom4J document.
	 */
	public static synchronized XDocument parse( SAXReader sax, BufferedReader reader, String systemId) throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "XMLUtilities.parse( "+reader+", "+systemId+")");

		return parse( sax, replaceAmp( reader), systemId);
	}

	// Does the actual parsing of the document.
	private static synchronized XDocument parse( SAXReader reader, XMLReader xmlReader, String systemId) throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "XMLUtilities.parse( "+xmlReader+", "+systemId+")");

		XDocument document = null;
		
		try { 
			document = (XDocument)reader.read( xmlReader, systemId);

			document.setEncoding( xmlReader.getEncoding());
			document.setVersion( xmlReader.getVersion());

		} catch( DocumentException e) {
			Exception x = (Exception)e.getNestedException();
			
			if ( x instanceof SAXParseException) {
				SAXParseException spe = (SAXParseException)x;
				Exception ex = spe.getException();
				
				if ( ex instanceof IOException) {
					throw (IOException)ex;
				} else {
					throw (SAXParseException)x;
				}
			} else if ( x instanceof IOException) {
				throw (IOException)x;
			}
		}

		return document;
	}

	/**
	 * Parses the document for this URL, does not substitute &amp; 
	 * and does resolve all XIncludes.
	 *
	 * @param url the URL of the document.
	 *
	 * @return the Dom4J document.
	 */
	public static synchronized XDocument parseWithoutSubstitution( URL url) throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "XMLUtilities.parseWithoutSubstitution( "+url+")");
		XDocument document = null;
		InputStream stream = URLUtilities.open( url);
		XMLReader reader = new XMLReader( stream);

		// create parser that resolves XIncludes.
		SAXParser parser = new SAXParser( new XIncludeParserConfiguration());
		SAXReader saxer = new SAXReader( parser);

		// create parser that does not resolves XIncludes.
		// SAXReader saxer = createReader( false);
		saxer.setStripWhitespaceText( true);
		saxer.setMergeAdjacentText( true);
		
		try {
			document = parse( saxer, reader, url.toString());
		} finally {
			stream.close();
			reader.close();
		}
		
		return document;
	}

	/**
	 * Validates the document for this URL.
	 *
	 * @param url the URL of the document.
	 *
	 * @return the Dom4J document.
	 */
	public static synchronized void validate( URL url) throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "XMLUtilities.validate( "+url+")");

		try { 
			SAXReader reader = getReader( true);
			reader.read( url);

		} catch( DocumentException e) {
			Exception x = (Exception)e.getNestedException();
			
			if ( x instanceof SAXParseException) {
				SAXParseException spe = (SAXParseException)x;
				Exception ex = spe.getException();
				
				if ( ex instanceof IOException) {
					throw (IOException)ex;
				} else {
					throw (SAXParseException)x;
				}
			} else if ( x instanceof IOException) {
				throw (IOException)x;
			}
		}
	}

	/**
	 * Validates the document for this URL.
	 *
	 * @param url the URL of the document.
	 *
	 * @return the Dom4J document.
	 */
	public static synchronized XDocument validate( ErrorHandler handler, BufferedReader isReader, String systemId) throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "DocumentUtilities.validate( "+isReader+", "+systemId+")");

		XDocument document = null;
		
		try { 
			SAXReader reader = createReader( true, false);
			reader.setEntityResolver( getCatalogResolver());
			reader.setErrorHandler( handler);
			String encoding = null;
	
			try {
				isReader.mark( 1024);
				encoding = getXMLDeclaration( isReader).getEncoding();
//			} catch ( NotXMLException e) {
//				e.printStackTrace();
//				throw( e);
			} finally {
				isReader.reset();
			}
	
			XMLReader xmlReader = createReader( isReader, encoding);

			document = (XDocument)reader.read( xmlReader, systemId);

			document.setEncoding( xmlReader.getEncoding());
			document.setVersion( xmlReader.getVersion());

		} catch( DocumentException e) {
			Exception x = (Exception)e.getNestedException();
			
			if ( x instanceof SAXParseException) {
				SAXParseException spe = (SAXParseException)x;
				Exception ex = spe.getException();
				
				if ( ex instanceof IOException) {
					throw (IOException)ex;
				} else {
					throw (SAXParseException)x;
				}
			} else if ( x instanceof IOException) {
				throw (IOException)x;
			}
		}

		return document;
	}

	public static synchronized void validate( ErrorHandler handler, BufferedReader isReader, String systemId, String encoding, int type, String grammarLocation) throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "XMLUtilities.validate( "+isReader+", "+systemId+", "+encoding+", "+type+", "+grammarLocation+")");
	
		if ( type == XMLGrammar.TYPE_RNG || type == XMLGrammar.TYPE_RNC || type == XMLGrammar.TYPE_NRL) {
			ValidationDriver driver = null;
			String encode = encoding;
			
			if ( type == XMLGrammar.TYPE_RNC) {
				driver = new ValidationDriver( makePropertyMap( null, handler, CHECK_ID_IDREF, false), CompactSchemaReader.getInstance());
			} else if ( type == XMLGrammar.TYPE_NRL) {
				driver = new ValidationDriver( makePropertyMap( null, handler, CHECK_ID_IDREF, true), new AutoSchemaReader());
			} else {
				driver = new ValidationDriver( makePropertyMap( null, handler, CHECK_ID_IDREF, false));
			}
			
			try {
				isReader.mark( 1024);
				encode = getXMLDeclaration( isReader).getEncoding();
//			} catch ( NotXMLException e) {
//				encode = encoding;
//				e.printStackTrace();
			} finally {
				isReader.reset();
			}
	
			InputSource source = new InputSource( isReader);
			source.setEncoding( encode);
			
			try {
				URL url = null;

				if ( systemId != null) {
					try {
						url = new URL( systemId);
					} catch ( MalformedURLException e) { 
						// does not matter really, the base url is only null ...
						url = null;
					}
				}
				
				URL schemaURL = null;
				
				if ( url != null) {
					schemaURL = new URL( url, grammarLocation);
				} else {
					schemaURL = new URL( grammarLocation);
				}
				
				driver.loadSchema( new InputSource( schemaURL.toString()));
				driver.validate( source);
			} catch( SAXException x) {
				x.printStackTrace();
				if ( x instanceof SAXParseException) {
					SAXParseException spe = (SAXParseException)x;
					Exception ex = spe.getException();
					
					if ( ex instanceof IOException) {
						throw (IOException)ex;
					} else {
						throw (SAXParseException)x;
					}
				}
			} catch ( IOException e) {
				e.printStackTrace();
				throw e;
			}
			
			
		} else { // type == XMLGrammar.TYPE_DTD || type == XMLGrammar.TYPE_XSD

			try { 
				SAXReader reader = createReader( true, false);
				reader.setErrorHandler( handler);
				String encode = encoding;
		
				try {
					isReader.mark( 1024);
					encode = getXMLDeclaration( isReader).getEncoding();
//				} catch ( NotXMLException e) {
//					encode = encoding;
//					e.printStackTrace();
				} finally {
					isReader.reset();
				}
		
				XMLReader xmlReader = createReader( isReader, encode);
				
				try {
					if ( type == XMLGrammar.TYPE_DTD) {
					    reader.setFeature( "http://apache.org/xml/features/validation/schema", false);

						reader.setEntityResolver( new DummyEntityResolver( grammarLocation));
					} else { // type == XMLGrammar.TYPE_XSD
						URL url = null;
		
						if ( systemId != null) {
							try {
								url = new URL( systemId);
							} catch ( MalformedURLException e) { 
								// does not matter really, the base url is only null ...
								url = null;
							}
						}
						
						URL schemaURL = null;
						
						if ( url != null) {
							schemaURL = new URL( url, grammarLocation);
						} else {
							schemaURL = new URL( grammarLocation);
						}

						reader.setFeature( "http://apache.org/xml/features/validation/schema", true);

						reader.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
						reader.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", new InputSource( schemaURL.toString()));
					}
				} catch ( Exception e) {
					e.printStackTrace();
				}

	//			try {
	//				System.out.println( "http://apache.org/xml/features/validation/schema = "+reader.getXMLReader().getFeature( "http://apache.org/xml/features/validation/schema"));
	//			} catch ( Exception e) {
	//				e.printStackTrace();
	//			}

				reader.read( xmlReader, systemId);

			} catch( DocumentException e) {
				Exception x = (Exception)e.getNestedException();
//				x.printStackTrace();
				
				if ( x instanceof SAXParseException) {
					SAXParseException spe = (SAXParseException)x;
					Exception ex = spe.getException();
					
					if ( ex instanceof IOException) {
						throw (IOException)ex;
					} else {
						throw (SAXParseException)x;
					}
				} else if ( x instanceof IOException) {
					throw (IOException)x;
				}
			}
		}
	}
	
	public static String resolve( String publicId, String systemId) {
		CatalogResolver resolver = getCatalogResolver();

		String entity = resolver.getResolvedEntity( publicId, systemId);
		if ( entity != null && entity.startsWith( "file:") && !entity.startsWith( "file:/")) {
			URL url = null;
			
			entity = entity.substring( 5);

			try {
				url = XngrURLUtilities.getURLFromFile(new File( entity));
				entity = url.toString();
			} catch ( MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		if ( entity != null) {
//			System.out.println( "XMLUtilities.resolve( "+publicId+", "+systemId+") ["+entity+"]");
			return entity;
		} else {
//			System.out.println( "XMLUtilities.resolve( "+publicId+", "+systemId+") ["+systemId+"]");
			return systemId;
		}
	}
	
	public static void validateSchema( XMLErrorHandler handler, BufferedReader isReader, String systemId, String encoding) throws IOException, SAXParseException {
	
	    String encode = encoding;

	    try {
	    	isReader.mark( 1024);
	    	encode = getXMLDeclaration( isReader).getEncoding();
//	    } catch ( NotXMLException e) {
//	    	encode = encoding;
//	    	e.printStackTrace();
	    } finally {
	    	isReader.reset();
	    }

	    XMLGrammarPreparser preparser = new XMLGrammarPreparser();
		preparser.setErrorHandler( handler);

        preparser.registerPreparser( XMLGrammarDescription.XML_SCHEMA, null);
	    preparser.setFeature( "http://xml.org/sax/features/namespaces", true);
	    preparser.setFeature( "http://xml.org/sax/features/validation", true);

	    preparser.setFeature( "http://apache.org/xml/features/validation/schema", true);
	    preparser.setFeature( "http://apache.org/xml/features/validation/schema-full-checking", true);
		
	    try {
			preparser.preparseGrammar( XMLGrammarDescription.XML_SCHEMA, new XMLInputSource( null, systemId, null, isReader, encode));
	    } catch ( XNIException e) {
	    	Exception x = e.getException();
	    	
	    	if ( x instanceof SAXParseException) {
	    		SAXParseException spe = (SAXParseException)x;
	    		Exception ex = spe.getException();
	    			
	    		if ( ex instanceof IOException) {
	    			throw (IOException)ex;
	    		} else {
	    			throw (SAXParseException)x;
	    		}
	    	} else if ( x instanceof IOException) {
	    		throw (IOException)x;
	    	} else {
	    		e.printStackTrace();
	    	}
	    }
	}

	public static void validateDTD( XMLErrorHandler handler, BufferedReader isReader, String systemId, String encoding, ConfigurationProperties properties) throws IOException, SAXParseException {
	
	    String encode = encoding;

	    try {
	    	isReader.mark( 1024);
	    	encode = getXMLDeclaration( isReader).getEncoding();
//	    } catch ( NotXMLException e) {
//	    	encode = encoding;
//		   	e.printStackTrace();
	    } finally {
	    	isReader.reset();
	    }
		
		XMLGrammarPreparser preparser = new XMLGrammarPreparser();

		preparser.setErrorHandler( handler);

		preparser.registerPreparser( "XML-DTD-DEF", new DefaultDTDLoader(properties));

	    preparser.setFeature( "http://xml.org/sax/features/namespaces", true);
	    preparser.setFeature( "http://xml.org/sax/features/validation", true);
		preparser.setFeature( "http://apache.org/xml/features/validation/warn-on-duplicate-attdef", true);
		preparser.setFeature( "http://apache.org/xml/features/continue-after-fatal-error", true);
		
	    try {
			preparser.preparseGrammar( "XML-DTD-DEF", new XMLInputSource( null, systemId, null, isReader, encode));
	    } catch ( XNIException e) {
	    	Exception x = e.getException();
	    	
	    	if ( x instanceof SAXParseException) {
	    		SAXParseException spe = (SAXParseException)x;
	    		Exception ex = spe.getException();
	    			
	    		if ( ex instanceof IOException) {
	    			throw (IOException)ex;
	    		} else {
	    			throw (SAXParseException)x;
	    		}
	    	} else if ( x instanceof IOException) {
	    		throw (IOException)x;
	    	} else {
	    		e.printStackTrace();
	    	}
	    }
	}

	public static void validateSchema( URL url) throws IOException, SAXParseException {
	
	    XMLGrammarPreparser preparser = new XMLGrammarPreparser();

	    preparser.registerPreparser( XMLGrammarDescription.XML_SCHEMA, null);
	    preparser.setFeature( "http://xml.org/sax/features/namespaces", true);
	    preparser.setFeature( "http://xml.org/sax/features/validation", true);
		preparser.setErrorHandler( new DefaultSchemaErrorHandler());
		
	    try {
			preparser.preparseGrammar( XMLGrammarDescription.XML_SCHEMA, new XMLInputSource( null, url.toString(), null));
	    } catch ( XNIException e) {
	    	Exception x = e.getException();
	    	
	    	if ( x instanceof SAXParseException) {
	    		SAXParseException spe = (SAXParseException)x;
	    		Exception ex = spe.getException();
	    			
	    		if ( ex instanceof IOException) {
	    			throw (IOException)ex;
	    		} else {
	    			throw (SAXParseException)x;
	    		}
	    	} else if ( x instanceof IOException) {
	    		throw (IOException)x;
	    	} else {
	    		e.printStackTrace();
	    	}
	    }
	}

	public static void validateRelaxNG( XMLErrorHandler handler, BufferedReader isReader, String systemId, String encoding) throws IOException, SAXParseException {
	
		// There is no need to find out the encoding...
		// However; James Clark does try to find out the encoding and complains 
		// about not finding BOM for UTF-16...
		String encode = null;
		
    	try {
    		isReader.mark( 1024);
    		encode = getXMLDeclaration( isReader).getEncoding();
//    	} catch ( NotXMLException e) {
//    		encode = encoding;
//    		e.printStackTrace();
    	} finally {
    		isReader.reset();
    	}
		
    	InputSource source = new InputSource( getInputStream( isReader, encode));
    	source.setEncoding( encode);
		source.setSystemId( systemId);
    	
    	try {
			SchemaFactory factory = new SchemaFactory();
			factory.setCompactSyntax( false);
			factory.setXMLReaderCreator( new Sax2XMLReaderCreator());
			factory.setErrorHandler( handler);
			
			factory.createSchema( source);

    	} catch ( IncorrectSchemaException e) {
			e.printStackTrace();
    	} catch( SAXException x) {
    		x.printStackTrace();
    		if ( x instanceof SAXParseException) {
    			SAXParseException spe = (SAXParseException)x;
    			Exception ex = spe.getException();
    			
    			if ( ex instanceof IOException) {
    				throw (IOException)ex;
    			} else {
    				throw (SAXParseException)x;
    			}
    		}
    	} catch ( IOException e) {
    		e.printStackTrace();
    		throw e;
    	}
	}

	/**
	 * Writes the document to the location specified by the URL,
	 * using the default XML writer!
	 *
	 * @param document the dom4j document.
	 * @param url the URL of the document.
	 */
	public static synchronized void write( XDocument document, URL url) throws IOException {
		if (DEBUG) System.out.println( "XMLUtilities.write( "+document+", "+url+")");

		File documentFile = null;
		
		documentFile = new File(url.getFile());
			
		if(documentFile != null) {
			FileOutputStream out = new FileOutputStream(documentFile);	
		

			//		XMLWriter writer = new XMLWriter( out, format);
			XMLWriter writer = new XMLWriter( out, new OutputFormat( TextPreferences.getTabString(), true, document.getEncoding()));
			writer.write( document);
			writer.flush();
	
			out.flush();
			out.close();
			
		}
		else {
			throw new IOException(url.toExternalForm() +" (The system cannot find the path specified)");
		}
	}
	
	/**
	 * Writes the document to the location specified by the URL.
	 *
	 * @param document the dom4j document.
	 * @param url the URL of the document.
	 * @param format the document output format.
	 */
	public static synchronized void write( XDocument document, URL url, ExchangerOutputFormat format) throws IOException {
		if (DEBUG) System.out.println( "XMLUtilities.write( "+document+", "+url+", "+format+") ["+format.getEncoding()+"]");

		FileOutputStream out = new FileOutputStream( url.getFile());

//		XMLWriter writer = new XMLWriter( out, format);
		ExchangerXMLWriter writer = new ExchangerXMLWriter( out, format);
		writer.write( document);
		writer.flush();

		out.flush();
		out.close();
	}

	public static synchronized String write( XDocument document, ExchangerOutputFormat format) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

//		XMLWriter writer = new XMLWriter( out, format);
		ExchangerXMLWriter writer = new ExchangerXMLWriter( out, format);
		writer.write( document);
		writer.flush();
		
		return out.toString( mapXMLEncodingToJava( format.getEncoding()));
	}

	public static synchronized String write( XDocument document, ExchangerOutputFormat format, boolean escapeEntities) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

//		XMLWriter writer = new XMLWriter( out, format);
		ExchangerXMLWriter writer = new ExchangerXMLWriter( out, format);
		writer.setEscapeEntities( escapeEntities);

		writer.write( document);
		writer.flush();
		
		return out.toString( mapXMLEncodingToJava( format.getEncoding()));
	}

	public static XMLReader replaceAmp( URL url) throws IOException, NotXMLException {
		InputStream stream = URLUtilities.open( url);
		Object[] objects = preParseXML( new BufferedInputStream( stream));
		
		BufferedReader reader = (BufferedReader)objects[0];
		String encoding = (String)objects[1];

		XMLReader xmlReader = null;
		
//		try {
			xmlReader = replaceAmp( reader, encoding);
//		} catch (Exception e) {
//		 	e.printStackTrace();
//		}

		return xmlReader;
	}

	private static XMLReader replaceAmp( BufferedReader reader) throws IOException, NotXMLException {
		String encoding = null;

		try {
			reader.mark( 1024);
			encoding = getXMLDeclaration( reader).getEncoding();
//		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			reader.reset();
		}

		return replaceAmp( reader, encoding);
	}

	private static XMLReader replaceAmp( BufferedReader reader, String encoding) throws IOException {
		boolean DEBUG = false;
		boolean comment = false;
		boolean entity = false; // internal entity declaration
		boolean cdata = false;
		boolean pi = false;

		CharArrayWriter writer = new CharArrayWriter();

//		XMLDeclaration declaration = null;
		
//		try {		
//			declaration = getXMLDeclaration( reader);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		int ch = reader.read();

		while ( ch != -1) {
			int peek = -1;
			
			//if ( !Character.isDefined( (char)ch)) {
			if ( !Character.isDefined( ch)) {
				throw new IOException( "File contains illegal Characters");
			}

			if ( cdata) {
				if ( ch == ']') {
					writer.write( ch);
					if (DEBUG) System.out.print( (char)ch);
					peek = reader.read();
					
					if ( peek == ']') {
						writer.write( peek);
						if (DEBUG) System.out.print( (char)peek);
						peek = reader.read();
						
						if ( peek == '>') {
							writer.write( peek);
							if (DEBUG) System.out.print( (char)peek);
							peek = -1;
							cdata = false;
						}
					}
				} else {
					writer.write( ch);
					if (DEBUG) System.out.print( (char)ch);
				}
			} else if ( comment) {
				if ( ch == '-') {
					writer.write( ch);
					if (DEBUG) System.out.print( (char)ch);
					peek = reader.read();
					
					if ( peek == '-') {
						writer.write( peek);
						if (DEBUG) System.out.print( (char)peek);
						peek = reader.read();
						
						if ( peek == '>') {
							writer.write( peek);
							if (DEBUG) System.out.print( (char)peek);
							peek = -1;
							comment = false;
						}
					}
				} else {
					writer.write( ch);
					if (DEBUG) System.out.print( (char)ch);
				}
			
			} else if ( pi) {
				if ( ch == '?') {
					writer.write( ch);
					if (DEBUG) System.out.print( (char)ch);
					peek = reader.read();

					if ( peek == '>') {
						writer.write( peek);
						if (DEBUG) System.out.print( (char)peek);
						peek = -1;
						pi = false;
					}
				} else {
					writer.write( ch);
					if (DEBUG) System.out.print( (char)ch);
				}

			} else if ( entity) {
				if ( ch == '>') {
					writer.write( ch);
					if (DEBUG) System.out.print( (char)ch);
					entity = false;
				} else {
					writer.write( ch);
					if (DEBUG) System.out.print( (char)ch);
				}

			} else { // ( !comment && !cdata && !pi)
				if ( ch == '<') {
					writer.write( ch);
					if (DEBUG) System.out.print( (char)ch);
					peek = reader.read();
					if ( peek == '!') {
						writer.write( peek);
						if (DEBUG) System.out.print( (char)peek);
						peek = reader.read();
						// --
						if ( peek == '-') {
							writer.write( peek);
							if (DEBUG) System.out.print( (char)peek);
							peek = reader.read();
							if ( peek == '-') {
								writer.write( peek);
								if (DEBUG) System.out.print( (char)peek);
								peek = -1;
								comment = true;
							}
						// <![CDATA[
						} else if ( peek == '[') {
							writer.write( peek);
							if (DEBUG) System.out.print( (char)peek);
							peek = reader.read();
							if ( peek == 'C') {
								writer.write( peek);
								if (DEBUG) System.out.print( (char)peek);
								peek = reader.read();
								if ( peek == 'D') {
									writer.write( peek);
									if (DEBUG) System.out.print( (char)peek);
									peek = reader.read();
									if ( peek == 'A') {
										writer.write( peek);
										if (DEBUG) System.out.print( (char)peek);
										peek = reader.read();
										if ( peek == 'T') {
											writer.write( peek);
											if (DEBUG) System.out.print( (char)peek);
											peek = reader.read();
											if ( peek == 'A') {
												writer.write( peek);
												if (DEBUG) System.out.print( (char)peek);
												peek = reader.read();
												if ( peek == '[') {
													writer.write( peek);
													if (DEBUG) System.out.print( (char)peek);
													peek = -1;
													cdata = true;
												}
											}
										}
									}
								}
							}
						} else {
//							entity = true;
						}
					} else if ( peek == '?') {
						writer.write( peek);
						if (DEBUG) System.out.print( (char)peek);
						peek = -1;
						pi = true;
					} 
				} else if ( ch == '&') { // entity start
					writer.write( ch);
					readEntity( reader, writer);
//					writer.write( 'a');
//					writer.write( 'm');
//					writer.write( 'p');
//					writer.write( ';');

					if (DEBUG) System.out.print( "&amp;");
				} else {
//					if ( ch > 0xFF) {
//						System.out.println( "\\u"+Integer.toHexString(ch));
//					}
					writer.write( ch);
					if (DEBUG) System.out.print( (char)ch);
				}
			}
			
			if ( peek != -1) {
				ch = peek;
			} else {
				ch = reader.read();
			}
		}
		
		XMLReader xmlReader = null;
		
		if ( encoding == null) {
			encoding = UTF_8;
		}
		
//		if ( encoding != null) {
//		byte[] bytes = writer.toString().getBytes( encoding);
		byte[] bytes = writer.toString().getBytes( mapXMLEncodingToJava( encoding));
		xmlReader = new XMLReader( new ByteArrayInputStream( bytes), encoding, null);
//		} else {
//			byte[] bytes = writer.toString().getBytes();
//			xmlReader = new XMLReader( new ByteArrayInputStream( bytes));
//		}

		return xmlReader;
	}

	private static void readEntity( Reader reader, CharArrayWriter writer) throws IOException {
		char[] buffer = new char[128]; // this should do
		
		int ch = reader.read();
		int counter = 0;

		buffer[ counter] = (char)ch;
		
		if ( ch == '#') {
			counter++;
			
			ch = reader.read();
			
			if ( ch == 'x') {
				buffer[ counter] = (char)ch;
	
				counter++;
				ch = reader.read();

				while ( ch != -1 && counter < buffer.length) {
					buffer[ counter] = (char)ch;
	
					if ( (ch >= '0' && ch <= '9') ||
                    	 (ch >= 'a' && ch <= 'f') ||
                    	 (ch >= 'A' && ch <= 'F')) {
						counter++;
						ch = reader.read();
					} else if ( ch == ';') {
						// entity end
						writer.write( 'a');
						writer.write( 'm');
						writer.write( 'p');
						writer.write( ';');
						break;
                    } else {
                    	break;
                    }
				}
				
			} else {
				while ( ch != -1 && counter < buffer.length) {
					buffer[ counter] = (char)ch;
	
					if ( ch >= '0' && ch <= '9') {
						counter++;
						ch = reader.read();
					} else if ( ch == ';') {
						// entity end
						writer.write( 'a');
						writer.write( 'm');
						writer.write( 'p');
						writer.write( ';');
						break;
                    } else {
                    	break;
                    }
				}
			}
		} else if ( XMLChar.isNameStart( ch)) {
			counter++;
			ch = reader.read();

			while ( ch != -1 && counter < buffer.length) {
				buffer[ counter] = (char)ch;
	
				if ( ch == ';') {
					// entity end
					writer.write( 'a');
					writer.write( 'm');
					writer.write( 'p');
					writer.write( ';');
					break;
				} else if ( !XMLChar.isName( (char)ch)) {
					break; // not an entity.
				} else {
					counter++;
					ch = reader.read();
				}
			}
		}
		
		for ( int i = 0; i <= counter; i++) {
			//if ( !Character.isDefined( (char)buffer[i])) {
			if ( !Character.isDefined( buffer[i])) {
				throw new IOException( "File contains illegal Characters");
			}

			writer.write( buffer[i]);
		}
	}

	public static String mapXMLEncodingToJava( String encoding) {
		String result = EncodingMap.getIANA2JavaMapping( encoding.toUpperCase());
	
		if ( result == null) {
			result = encoding;
		}
		
//		System.out.println( "mapXMLEncodingToJava( "+encoding+") ["+result+"]");
		return result;
	}

	/**
	 * pre-parse the document, so the encoding is know...
	 *
	 * @param stream the InputStream for the document.
	 */
	public static BufferedReader createEncodedReader( BufferedInputStream stream) throws IOException {
		Object[] object = preParse( stream);
		return (BufferedReader)object[0];
	}

	public static String getEncoding( URL url) throws IOException {
		Object[] objects = preParse( new BufferedInputStream( URLUtilities.open( url)));
		String encoding = (String)objects[1];
//		System.out.println( "XMLUtilities.getEncoding() ["+encoding+"]");
		
		if ( encoding == null) {
			encoding = "UTF-8";
		}
		
		return encoding;
	}

	/**
	 * pre-parse the document, so the encoding is know...
	 *
	 * @param stream the InputStream for the document.
	 */
	public static Object[] preParse( BufferedInputStream stream) throws IOException {
		BufferedReader reader = null;

		stream.mark( 1024);
		String encoding = getStreamEncoding( stream);
		stream.reset(); 
		
		reader = createReader( stream, encoding);
		
		try {		
			stream.mark( stream.available());
			encoding = getXMLDeclaration( reader).getEncoding();
		} catch ( NotXMLException e) {
//			e.printStackTrace();
		} finally {
			stream.reset();
			reader = createReader( stream, encoding);
		}

		return new Object[] { reader, encoding };
	}

	/**
	 * pre-parse the XML document, so the encoding is know, throws 
	 * a NotXMLException when the is not a XML Document...
	 *
	 * @param stream the InputStream for the document.
	 */
	public static Object[] preParseXML( BufferedInputStream stream) throws IOException, NotXMLException {
		BufferedReader reader = null;

		stream.mark( 1024);
		String encoding = getStreamEncoding( stream);
		stream.reset(); 
		
		reader = createReader( stream, encoding);
		

		try {		
			stream.mark( stream.available());
			encoding = getXMLDeclaration( reader).getEncoding();
//		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			stream.reset();
			reader = createReader( stream, encoding);
		}

		return new Object[] { reader, encoding };
	}

	public static XMLDeclaration getXMLDeclaration( BufferedReader reader) throws IOException, NotXMLException {
		boolean DEBUG = false;

		String  version = null;
		String  encoding = null;
		String  standalone = null;
		
//		reader.mark( 100);

		int ch = reader.read();
		if (DEBUG) System.out.println( "\""+(char)ch+"\"");
		
		// Check to see if this is XML 
		if ( ((char)ch) != '<') {
			// this might be the byte order mark, try again:
			if (DEBUG) System.out.println( "\""+(char)ch+"\"");

			ch = XMLParserUtilities.skipWhitespace( reader);

			if ( ((char)ch) != '<') {
				if (DEBUG) System.out.println( "\""+(char)ch+"\"");
				throw new NotXMLException();
			}
		}
		
		if ( XMLParserUtilities.hasString( reader, "<?xml", (char)ch)) {
			ch = XMLParserUtilities.skipWhitespace( reader);

			if ( XMLParserUtilities.hasString( reader, "version", (char)ch)) {
				ch = XMLParserUtilities.skipWhitespace( reader);
				if (DEBUG) System.out.print( (char)ch);
				
				if ( ((char)ch) == '=') {
					ch = XMLParserUtilities.skipWhitespace( reader);
					
					version = XMLParserUtilities.parseString( reader, (char)ch);
					
					ch = XMLParserUtilities.skipWhitespace( reader);
					
					if ( XMLParserUtilities.hasString( reader, "encoding", (char)ch)) {
						ch = XMLParserUtilities.skipWhitespace( reader);
						
						if ( ((char)ch) == '=') {
			
							ch = XMLParserUtilities.skipWhitespace( reader);
							encoding = XMLParserUtilities.parseString( reader, (char)ch);
						}
					}
				}
			}
		}
		
		if (DEBUG) System.out.println( "version=\""+version+"\" encoding=\""+encoding+"\"");

		return new XMLDeclaration( version, encoding);
	}
	
	public static String getStreamEncoding( InputStream stream) throws IOException {
		String encoding = null;
		boolean DEBUG =false;
		
		if (DEBUG) {
			SortedMap map = Charset.availableCharsets();
			Object[] keys = map.keySet().toArray();
			
			for ( int i = 0; i < keys.length; i++) {
				System.out.println("Key = "+keys[i]+" Value = "+map.get( keys[i]));
			}
		}
		
		int ch = stream.read();
		if (DEBUG) System.out.print( "["+ch+"]");

		// UCS-4 Big Endian (1234)
		if ( ch == 0x00) {
			ch = stream.read();
			if (DEBUG) System.out.print( "["+ch+"]");
			if ( ch == 0x00) {
				ch = stream.read();
				if (DEBUG) System.out.print( "["+ch+"]");
				if ( ch == 0xFE) {
					ch = stream.read();
					if (DEBUG) System.out.print( "["+ch+"]");
					if ( ch == 0xFF) {
						encoding = UCS_4BE;
					}
				} else if ( ch == 0xFF) {
					ch = stream.read();
					if (DEBUG) System.out.print( "["+ch+"]");
					if ( ch == 0xFE) {
						encoding = UNKNOWN;
					}
				} else if ( ch == 0x00) {
					ch = stream.read();
					if (DEBUG) System.out.print( "["+ch+"]");
					if ( ch == 0x3C) {
						encoding = UCS_4BE;
					}
				} else if ( ch == 0x3C) {
					ch = stream.read();
					if (DEBUG) System.out.print( "["+ch+"]");
					if ( ch == 0x00) {
						encoding = UNKNOWN;
					}
				}
			} else if ( ch == 0x3C) {
				ch = stream.read();
				if (DEBUG) System.out.print( "["+ch+"]");
				if ( ch == 0x00) {
					ch = stream.read();
					if (DEBUG) System.out.print( "["+ch+"]");
					if ( ch == 0x00) {
						encoding = UNKNOWN;
					} else if ( ch == 0x3F) {
						encoding = UTF_16BE;
					}
				}
			}
		} else if ( ch == 0x3C) {
			ch = stream.read();
			if (DEBUG) System.out.print( "["+ch+"]");
			if ( ch == 0x00) {
				ch = stream.read();
				if (DEBUG) System.out.print( "["+ch+"]");
				if ( ch == 0x00) {
					ch = stream.read();
					if (DEBUG) System.out.print( "["+ch+"]");
					if ( ch == 0x00) {
						encoding = UCS_4LE;
					}
				} else if ( ch == 0x3F) {
					ch = stream.read();
					if (DEBUG) System.out.print( "["+ch+"]");
					if ( ch == 0x00) {
						encoding = UTF_16LE;
					}
				}
			} else if ( ch == 0x3F) {
				ch = stream.read();
				if (DEBUG) System.out.print( "["+ch+"]");
				if ( ch == 0x78) {
					ch = stream.read();
					if (DEBUG) System.out.print( "["+ch+"]");
					if ( ch == 0x6D) {
						encoding = UTF_8;
					}
				}
			}
		} else if ( ch == 0xFF) {
			ch = stream.read();
			if (DEBUG) System.out.print( "["+ch+"]");
			if ( ch == 0xFE) {
				ch = stream.read();
				encoding = UTF_16LE;
				if (DEBUG) System.out.print( "["+ch+"]");
				if ( ch == 0x00) {
					ch = stream.read();
					if (DEBUG) System.out.print( "["+ch+"]");
					if ( ch == 0x00) {
						encoding = UCS_4LE;
					}
				} 
			}
		} else if ( ch == 0xFE) {
			ch = stream.read();
			if (DEBUG) System.out.print( "["+ch+"]");
			if ( ch == 0xFF) {
				ch = stream.read();

				encoding = UTF_16BE;
				if (DEBUG) System.out.print( "["+ch+"]");
				if ( ch == 0x00) {
					ch = stream.read();
					if (DEBUG) System.out.print( "["+ch+"]");
					if ( ch == 0x00) {
						encoding = UNKNOWN;
					}
				}
			}
		} else if ( ch == 0xEF) {
			ch = stream.read();
			if (DEBUG) System.out.print( "["+ch+"]");
			if ( ch == 0xBB) {
				ch = stream.read();
				if (DEBUG) System.out.print( "["+ch+"]");
				if ( ch == 0xBF) {
//					System.out.println( "Found UTF-8 byte order mark.");
					// strip utf-8 byte order mark
					stream.mark( 1024);
					encoding = UTF_8;
				}
			}
		}  else if ( ch == 0x4C) {
			ch = stream.read();
			if (DEBUG) System.out.print( "["+ch+"]");
			if ( ch == 0x6F) {
				ch = stream.read();
				if (DEBUG) System.out.print( "["+ch+"]");
				if ( ch == 0xA7) {
					ch = stream.read();
					if (DEBUG) System.out.print( "["+ch+"]");
					if ( ch == 0x94) {
						encoding = EBCDIC;
					}
				}
			}
		}

		if (DEBUG) System.out.println( "getStreamEncoding() ["+encoding+"]");
		return encoding;
	}
	
	private static BufferedReader createReader( InputStream stream, String encoding) throws UnsupportedEncodingException {
		if (DEBUG) System.out.println( "getReader( "+stream+", "+encoding+")");

		BufferedReader reader = null;
		
		if ( encoding != null) {
			if ( encoding == UCS_4LE) {
				reader = new BufferedReader( new UCSReader( stream, UCSReader.UCS4LE));
			} else if ( encoding == UCS_4BE) {
				reader = new BufferedReader( new UCSReader( stream, UCSReader.UCS4BE));
			} else {
				reader = new BufferedReader( new InputStreamReader( stream, mapXMLEncodingToJava( encoding)));
			}
		} else { // default
			reader = new BufferedReader( new InputStreamReader( stream, UTF_8));
		}
		
		return reader;
	}

	private static XMLReader createReader( BufferedReader reader, String encoding) throws IOException, UnsupportedEncodingException {
		if (DEBUG) System.out.println( "createReader( "+reader+", "+encoding+")");

		ByteArrayInputStream stream = null;
		CharArrayWriter writer = new CharArrayWriter();
		
		char[] buffer = new char[4096];
		int len = 0;
		
		while ( (len = reader.read( buffer)) != -1) {
			writer.write( buffer, 0, len);
		}

		XMLReader xmlReader = null;
		
		if ( encoding == null) {
			encoding = mapXMLEncodingToJava( UTF_8);
		}
		
		byte[] bytes = writer.toString().getBytes( mapXMLEncodingToJava( encoding));
		xmlReader = new XMLReader( new ByteArrayInputStream( bytes), encoding, null);
		

		return xmlReader;
	}

	private static InputStream getInputStream( BufferedReader reader, String encoding) throws IOException, UnsupportedEncodingException {
		if (DEBUG) System.out.println( "getInputStream( "+reader+", "+encoding+")");

		CharArrayWriter writer = new CharArrayWriter();
		
		char[] buffer = new char[4096];
		int len = 0;
		
		while ( (len = reader.read( buffer)) != -1) {
			writer.write( buffer, 0, len);
		}

		byte[] bytes = null;

		if ( encoding != null) {
			bytes = writer.toString().getBytes( mapXMLEncodingToJava( encoding));
		} else {
			bytes = writer.toString().getBytes( mapXMLEncodingToJava( UTF_8));
		}
		
		return new ByteArrayInputStream( bytes);
	}

	public static class XMLReader extends InputStreamReader {
		private String encoding = null;
		private String version = null;
		int length = -1;

		public XMLReader( ByteArrayInputStream stream, String encoding, String version) throws UnsupportedEncodingException {
			super( stream, mapXMLEncodingToJava( encoding));
			
			length = stream.available();
			this.encoding = encoding;
			this.version = version;
		}
		
		public XMLReader( InputStream stream) throws UnsupportedEncodingException {
			super( stream, UTF_8);
		}

		/**
		 * Gets the encoding for the document.
		 *
		 * @return the document encoding.
		 */
		public String getEncoding() {
			return encoding;
		}
	
		/**
		 * Gets the xml version of the document.
		 *
		 * @return the document encoding.
		 */
		public String getVersion() {
			return version;
		}
	}

	public static class XMLDeclaration {
		String encoding = UTF_8;
		String version = null;
		
		public XMLDeclaration() {
		}

		public XMLDeclaration( String version, String encoding) {
			this.version = version;
			
			setEncoding( encoding);
		}

		/**
		 * Gets the encoding for the document.
		 *
		 * @return the document encoding.
		 */
		public String getEncoding() {
			return encoding;
		}

		/**
		 * Sets the encoding for the document. 
		 *
		 * @param encoding the document encoding.
		 */
		public void setEncoding( String encoding) {
			if ( encoding != null && encoding.trim().length() > 0 ) {
				this.encoding = encoding;
			}
		}

		/**
		 * Gets the xml version of the document.
		 *
		 * @return the document encoding.
		 */
		public String getVersion() {
			return version;
		}

		/**
		 * Sets the xml version for the document.
		 *
		 * @param version the document xml version.
		 */
		public void setVersion( String version) {
			this.version = version;
		}	
	}
	
//	public static class DummyEntityResolver implements EntityResolver {
//		private String systemId = null;
//		private EntityResolver parent = null;
//		
//		public DummyEntityResolver( EntityResolver parent, String systemId) {
//			this.systemId = systemId;
//			this.parent = parent;
//		}
//		public InputSource resolveEntity( String publicId, String ignoredId) throws SAXException, IOException {
//			System.out.println( "DummyEntityResolver.resolveEntity( "+publicId+", "+ignoredId+")" );
//			return null;
//		}
//	}

	public static class DummyEntityResolver implements EntityResolver {
		private String systemId = null;
		boolean ignore = true;
		
		public DummyEntityResolver( String systemId) {
			this.systemId = systemId;
			ignore = true;
		}

		public InputSource resolveEntity( String publicId, String ignoredId) throws SAXException, IOException {
//			System.out.println( "DummyEntityResolver.resolveEntity( "+publicId+", "+ignoredId+")" );
			InputSource result = null;

			if ( ignore) { // Only ignore the first one!
				if ( systemId != null) {
					result = new InputSource( systemId);
				} else {
					result = new InputSource( new StringReader( ""));
				}

				ignore = false;
			} else {
				result = new InputSource( ignoredId);
			}
			
			return result;
		}
	}

	public static class NoErrorEntityResolver implements EntityResolver {
		public NoErrorEntityResolver() {
		}

		public InputSource resolveEntity( String publicId, String systemId) throws SAXException, IOException {
//			System.out.println( "resolveEntity( "+publicId+", "+systemId+")");

			try { 
				systemId = resolve( publicId, systemId);

				URL url = URLUtilities.toURL( systemId);
				InputSource source = new InputSource( systemId);
				source.setByteStream( url.openStream());
				
				return source;
			} catch ( Exception e) {
//				e.printStackTrace();
				return new InputSource( new StringReader( ""));
			}
		}
	}

	public static class NotXMLException extends SAXParseException {
		public NotXMLException() {
			super( "The markup in the document preceding the root element must be well-formed.", null);
		}
	}
	
//	public static class XMLTestGrammarPool extends XMLGrammarPoolImpl { 
//		private Grammar grammar = null;
//
//		public void putGrammar( Grammar grammar) {
//			this.grammar = grammar;
//		}
//
//		public Grammar retrieveGrammar( XMLGrammarDescription description) {
//			System.out.println( "XMLTestGrammarPool.retrieveGrammar( "+description+")");
//			return grammar;
//		}
//	
//	}

	private static class DefaultRelaxErrorHandler implements ErrorHandler {

	    public void warning( SAXParseException exception) throws SAXException {
            throw exception;
	    } 

	    public void error( SAXParseException exception) throws SAXException {
	        throw exception;
	    } 

	    public void fatalError( SAXParseException exception) throws SAXException {
	        throw exception;
	    } 
	}

	private static class DefaultSchemaErrorHandler implements org.apache.xerces.xni.parser.XMLErrorHandler {

	    public void warning(String domain, String key, XMLParseException exception) throws XNIException {
            throw new XNIException( createSAXParseException( exception));
	    } 

	    public void error(String domain, String key, XMLParseException exception) throws XNIException {
			throw new XNIException( createSAXParseException( exception));
	    }

	    public void fatalError(String domain, String key, XMLParseException exception) throws XNIException {
		    throw new XNIException( createSAXParseException( exception));
	    }

	    /** Creates a SAXParseException from an XMLParseException. */
	    protected static SAXParseException createSAXParseException(XMLParseException exception) {
	        return new SAXParseException(exception.getMessage(),
	                                     exception.getPublicId(),
	                                     exception.getExpandedSystemId(),
	                                     exception.getLineNumber(),
	                                     exception.getColumnNumber(),
	                                     exception.getException());
	    } // createSAXParseException(XMLParseException):SAXParseException

	    /** Creates an XMLParseException from a SAXParseException. */
	    protected static XMLParseException createXMLParseException(SAXParseException exception) {
	        final String fPublicId = exception.getPublicId();
	        final String fExpandedSystemId = exception.getSystemId();
	        final int fLineNumber = exception.getLineNumber();
	        final int fColumnNumber = exception.getColumnNumber();
	        XMLLocator location = new XMLLocator() {
	            public void setPublicId(String id) {}
	            public String getPublicId() { return fPublicId; }
	            public void setExpandedSystemId( String id) {}
	            public String getExpandedSystemId() { return fExpandedSystemId; }
	            public void setBaseSystemId(String id) {}
	            public String getBaseSystemId() { return null; }
	            public void setLiteralSystemId(String id) {}
	            public String getLiteralSystemId() { return null; }
	            public int getColumnNumber() { return fColumnNumber; }
	            public void setColumnNumber(int col) {}
	            public int getLineNumber() { return fLineNumber; }
	            public void setLineNumber(int line) {}
	            public String getEncoding() { return null; }
	            
	            //TODO GMCG
	            public String getXMLVersion() {return null;}
	            public int getCharacterOffset() {return 0;}
	        };
	        return new XMLParseException(location, exception.getMessage(),
	                                     exception.getException());
	    } // createXMLParseException(SAXParseException):XMLParseException

	    /** Creates an XNIException from a SAXException. 
	        NOTE:  care should be taken *not* to call this with a SAXParseException; this will
	        lose information!!! */
	    protected static XNIException createXNIException(SAXException exception) {
	        return new XNIException(exception.getMessage(),
	                                     exception.getException());
	    } // createXNIException(SAXException):XMLParseException
	}
	
	private static PropertyMap makePropertyMap(XMLReaderCreator xrc, ErrorHandler eh, int flags, boolean attributes) {
		PropertyMapBuilder builder = new PropertyMapBuilder();

		if (xrc == null) {
			xrc = new Sax2XMLReaderCreator();
		}

		ValidateProperty.XML_READER_CREATOR.put(builder, xrc);

		if (eh != null) {
			ValidateProperty.ERROR_HANDLER.put(builder, eh);
		} 

		if ((flags & CHECK_ID_IDREF) != 0) {
			RngProperty.CHECK_ID_IDREF.add( builder);
		}

		if ((flags & FEASIBLE) != 0) {
			RngProperty.FEASIBLE.add( builder);
		}
		
		/*if ( attributes) {
			NrlProperty.ATTRIBUTES_SCHEMA.add( builder);
		}*/
		
		return builder.toPropertyMap();	
		
	}
	
	private static class DefaultEntityManager extends XMLEntityManager {
		public void setFeature( String feature, boolean state) throws XMLConfigurationException {
		    // xerces features
	        if ( feature.equals( WARN_ON_DUPLICATE_ENTITYDEF)) {
				System.out.println( "Setting Feature "+WARN_ON_DUPLICATE_ENTITYDEF+" to "+state);
	            fWarnDuplicateEntityDef = state;
	        } else {
				super.setFeature( feature, state);
	        }
	
		} // setFeature(String,boolean)
		
		public boolean isWarnDuplicateEntityDef() {
			return fWarnDuplicateEntityDef;
		}
	}

	private static class DefaultDTDLoader extends XMLDTDLoader {
		public DefaultDTDLoader(ConfigurationProperties properties) {
			super();
			
			fEntityResolver = new DefaultEntityManager();
		    fEntityManager = (XMLEntityManager)fEntityResolver;
			
			((DefaultEntityManager)fEntityResolver).setFeature( "http://apache.org/xml/features/warn-on-duplicate-entitydef", properties.getXercesProperties().isWarnOnDuplicateEntityDef() );

			fEntityManager.setProperty( ERROR_REPORTER, fErrorReporter);

			fDTDScanner = new XMLDTDScannerImpl( fSymbolTable, fErrorReporter, fEntityManager);
			fDTDScanner.setDTDHandler(this);
			fDTDScanner.setDTDContentModelHandler(this);

			reset();
		}
	}
	
	public static CatalogResolver getCatalogResolver() {
		String property = System.getProperty("javax.xml.parsers.SAXParserFactory");
		if(property == null) {
			System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
		}
		else if(!property.equals("org.apache.xerces.jaxp.SAXParserFactoryImpl")){
			System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
		}
		
		CatalogResolver resolver = new CatalogResolver();

		try {
			Catalog catalog = resolver.getCatalog();
			catalog.getCatalogManager().setUseStaticCatalog( false);

			String prefer = System.getProperty( "xml.catalog.prefer");
			if ( prefer != null && prefer.equals( "system")) {
				catalog.getCatalogManager().setPreferPublic( false);
			} else {
				catalog.getCatalogManager().setPreferPublic( true);
			}

//			Vector files = catalog.getCatalogManager().getCatalogFiles();
//			for ( int i = 0; i < files.size(); i++) {
//				System.out.println( files.elementAt(i));
//			}

			catalog.getCatalogManager().setCatalogFiles( System.getProperty( "xml.catalog.files"));
			resolver.getCatalog().loadSystemCatalogs();

			
			
			
			// Having set all values, create a new resolver...
			resolver = new CatalogResolver();

//			files = catalog.getCatalogManager().getCatalogFiles();
//			for ( int i = 0; i < files.size(); i++) {
//				System.out.println( files.elementAt(i));
//			}
		} catch ( Exception e) {
			e.printStackTrace();
		}
		
		return resolver;
	}
	
	
	/**
	 * Resolves all XIncludes
	 *
	 * @param text The XML which may contain XIncludes
	 * @param encoding The encoding
	 * @param encoding The url of the file which can be used as a base identifier
	 * 
	 * @return the XML with any XIncludes resolved
	 */
	public static String resolveXIncludes(String text,String encoding,URL url, ExchangerOutputFormat format)
		throws DocumentException,IOException
	{
		// set the paser to use XInclude configuration
		SAXParser parser = new SAXParser(new XIncludeParserConfiguration());
		
		// create a dom4j SaxReader
		SAXReader reader = new SAXReader(parser);
		
		// if no encoding given then use UTF-8
		if (encoding == null)
		{
			encoding = "UTF-8";
		}
		
		// get the bytes from the input text
		ByteArrayInputStream stream = new ByteArrayInputStream( text.getBytes( mapXMLEncodingToJava( encoding)));
		InputStreamReader isReader = new InputStreamReader( stream, mapXMLEncodingToJava( encoding));

		InputSource source = new InputSource(isReader);

		if (url != null)
		{
			source.setSystemId(url.toString());
		}
		
		// using sax read the stream into a dom4j dom
		XDocument doc = (XDocument)reader.read(source);
		
		// write the new dom
		return write(doc, format, true);
	}
	
 	public static String format( String text, String systemId, String encoding, 
 			String indent, boolean newLines, boolean padText, int lineLength, 
			boolean trim, boolean preserveMixed, ExchangerOutputFormat format) throws IOException, SAXParseException {

 		ByteArrayInputStream stream = new ByteArrayInputStream( text.getBytes( mapXMLEncodingToJava( encoding)));
		InputStreamReader reader = new InputStreamReader( stream, mapXMLEncodingToJava( encoding));

		SAXReader sax = createReader( false, false);
		sax.setStripWhitespaceText( false);
		sax.setMergeAdjacentText( false);

		XDocument doc = XMLUtilities.parse( sax, new BufferedReader( reader), systemId);

		ByteArrayOutputStream out = new ByteArrayOutputStream();

//		OutputFormat format = new OutputFormat();
//		format.setEncoding( doc.getEncoding());

		XMLFormatter formatter = new XMLFormatter( out, format);
		
		format.setIndent( indent);
		format.setNewlines( newLines);
		format.setPadText( padText);
		formatter.setMaxLineLength( lineLength);
		formatter.setTrimText( trim);
		formatter.setPreserveMixedContent( preserveMixed);

		formatter.write( doc);
		formatter.flush();
		
		text = out.toString( mapXMLEncodingToJava( encoding));

 		stream = new ByteArrayInputStream( text.getBytes( mapXMLEncodingToJava( encoding)));
		reader = new InputStreamReader( stream, mapXMLEncodingToJava( encoding));

		sax = createReader( false, false);
		sax.setStripWhitespaceText( false);
		sax.setMergeAdjacentText( false);

		doc = XMLUtilities.parse( sax, new BufferedReader( reader), systemId);

		out = new ByteArrayOutputStream();
		ExchangerOutputFormat format2 = new ExchangerOutputFormat( "", false, encoding);

		if ( !format.isSuppressDeclaration()) {
			format2.setStandalone( format.getStandalone());
			format2.setOmitStandalone( format.isOmitStandalone());
			
			format2.setVersion( format.getVersion());
			format2.setOmitEncoding( format.isOmitEncoding());
			format2.setSuppressDeclaration( false);
		} else {
			format2.setSuppressDeclaration( true);
		}

		ExchangerXMLWriter writer = new ExchangerXMLWriter( out, format2);
		writer.write( doc);
		writer.flush();
		
		return out.toString( mapXMLEncodingToJava( encoding));
 	}
 	
 	public static String serialise( Document doc, String encoding) {
 		String result = null;
 		
 		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter( stream, mapXMLEncodingToJava( encoding));
			org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat( doc);
			format.setEncoding( encoding);
			format.setPreserveSpace( true);
			format.setPreserveEmptyAttributes( true);
			XMLSerializer output = new XMLSerializer( format);
			output.setNamespaces( true);
			output.setOutputCharStream( writer);
			output.serialize( doc);
			result = stream.toString( mapXMLEncodingToJava( encoding));
		} catch ( IOException e) {
			e.printStackTrace();
		}
 		
 		return result;
 	}
 	
 	
 	/**
	 * Updates the XML Declaration
	 *
	 * @param document The Exchanger document
	 * @param version The XML version
	 * @param encoding The encoding 
	 * @param standalone The values of standalone
	 * 
	 * @return String the text of the XML with the updated XML declaration
	 */
 	public static String updateXMLDeclaration(ExchangerDocument document,String version,String encoding,String standalone)
	{
 		String text = document.getText();
 		int updated = 0;
 		
 		String updatedText;
 		int start = text.indexOf("<?xml");
 		if (start == -1)
 		{
 			// no XML declaration
 			updatedText = text;
 			updated = 1;
 		}
 		else
 		{
 			// has an XML declaration which we need to replace
 			updatedText = removeXMLDeclaration(text);
 		}
 		
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml ");
		buf.append("version=\"");
		buf.append(version);
		buf.append("\" ");
		buf.append("encoding=\"");
		buf.append(encoding);
		buf.append("\"");
		if (!standalone.equals("NONE"))
		{
			buf.append(" ");
			buf.append("standalone=\"");
			buf.append(standalone);
			buf.append("\"");
		}
		buf.append("?>");
		if (updated == 1)
		{
			buf.append("\n");
		}
		buf.append(updatedText);
		
		return buf.toString();
			
	}
 	
 	/**
	 * Removes the XML Declaration
	 *
	 * @param text The XML whose declaration is to be removed
	 * 
	 * @return String the text of the XML with declaration removed
	 */
 	public static String removeXMLDeclaration(String text)
	{
 		int start = text.indexOf("<?xml");
 		if (start == -1)
 		{
 			return text;
 		}
 		
 		int end = text.indexOf("?>");
 		
 		return text.substring(end+2);
 	}
 	
 	
	/**
	 * Sets the XML DOCTYPE
	 *
	 * @param document The Exchanger document
	 * @param name The DOCTYPE name (should be root element's name)
	 * @param type The type (SYSTEM,PUBLIC or INTERNAL)
	 * @param publicID The publicID 
	 * @param systemID The systemID
	 * 
	 */
 	public static void setXMLDoctype(ExchangerDocument document,String name,String type,String publicID,String systemID)
	{
 		XDocument xdoc = document.getDocument();
 		DocumentType dt = xdoc.getDocType();
 		
 		if (dt != null)
 		{
 			// update existing Doctype
 			dt.setElementName(name);
 			if (type.equals(INTERNAL))
 			{
 				dt.setPublicID(null);
 				dt.setSystemID(null);
 			}
 			else if (type.equals(SYSTEM))
 			{
 				dt.setPublicID(null);
 				dt.setSystemID(systemID);
 			}
 			else
 			{
 				dt.setPublicID(publicID);
 				dt.setSystemID(systemID);
 				
 					
 			}
 		}
 		else
 		{
 			// add new doctype
 			if (type.equals(INTERNAL))
 			{
 				xdoc.addDocType(name,null,null);
 			}
 			else if (type.equals(SYSTEM))
 			{
 				xdoc.addDocType(name,null,systemID);
 			}
 			else
 			{	
 				xdoc.addDocType(name,publicID,systemID);
 			}
 			
 		}
 	}
 	
 	/**
	 * Sets the Schema Location attribute on the root element
	 *
	 * @param document The Exchanger document
	 * @param schemaType The schema type (either schemaLocation or noNamespaceSchemaLocation)
	 * @param namespace The namespace
	 * @param schemaURL The URL or the schema
	 * 
	 */
 	public static void setSchemaLocation(ExchangerDocument document,String schemaType,String namespace,String schemaURL)
	{
 		schemaURL = URLUtilities.encodeURL(schemaURL);
 			
 		XDocument xdoc = document.getDocument();
 		Element root = xdoc.getRootElement();
 		
 		if (schemaType.equals(SCHEMALOCATION))
 		{
 			Attribute attrNoNS =  root.attribute(NOSCHEMALOCATION);
 			if (attrNoNS != null)
 			{
 				root.remove(attrNoNS);
 			}
 			
 			
 			// need to set both namspace and url
 			Attribute attr = root.attribute(SCHEMALOCATION);
 			if (attr == null)
 			{ 				
 				// does the schema instance already exist
 				Namespace ns = root.getNamespaceForURI(SCHEMAINSTANCE);
 				if (ns != null)
 				{
 					String schemaInstancePrefix = ns.getPrefix();
 					StringBuffer name = new StringBuffer();
 					name.append(schemaInstancePrefix);
 					name.append(":");
 					name.append(SCHEMALOCATION);
 					
 					StringBuffer value = new StringBuffer();
 					value.append(namespace);
 					value.append(" ");
 					value.append(schemaURL);
 					
 					root.addAttribute(name.toString(),value.toString());
 				}
 				else
 				{
 					root.addNamespace("xsi",SCHEMAINSTANCE);
 					
 					StringBuffer name = new StringBuffer();
 					name.append("xsi");
 					name.append(":");
 					name.append(SCHEMALOCATION);
					
 					StringBuffer value = new StringBuffer();
 					value.append(namespace);
 					value.append(" ");
 					value.append(schemaURL);
 					
 					root.addAttribute(name.toString(),value.toString());
 				}
 			}
 			else
 			{
 				String attrValue = attr.getValue();
				
				// break up all the namespace and url pairs
				ArrayList stringValues = new ArrayList();
				
				StringTokenizer st = new StringTokenizer(attrValue);
			     while (st.hasMoreTokens()) 
			     {
			     	stringValues.add(st.nextToken());
			     }
 				
 				// update existing attribute, Note: it may have multiple attribute pairs
 				StringBuffer value = new StringBuffer();
				value.append(namespace);
				value.append(" ");
				value.append(schemaURL);
 				
			    //need to start at the third value (i.e we only replace the first namespace-url pair)
			    for (int i=2;i<stringValues.size();i++)
			    {
			    	value.append(" ");
			    	value.append((String)stringValues.get(i));
			    }
			     
 				attr.setValue(value.toString());
 			}
 		}
 		else
 		{
 			// is of type "no schema location"
 			Attribute attrSchema =  root.attribute(SCHEMALOCATION);
 			if (attrSchema != null)
 			{
 				root.remove(attrSchema);
 			}
 			
 			// just need to set the url
 			Attribute attr = root.attribute(NOSCHEMALOCATION);
 			if (attr == null)
 			{ 				
 				// does the schema instance already exist
 				Namespace ns = root.getNamespaceForURI(SCHEMAINSTANCE);
 				if (ns != null)
 				{
 					String schemaInstancePrefix = ns.getPrefix();
 					StringBuffer name = new StringBuffer();
 					name.append(schemaInstancePrefix);
 					name.append(":");
 					name.append(NOSCHEMALOCATION);
 	
 					root.addAttribute(name.toString(),schemaURL);
 				}
 				else
 				{
 					root.addNamespace("xsi",SCHEMAINSTANCE);
 					
 					StringBuffer name = new StringBuffer();
 					name.append("xsi");
 					name.append(":");
 					name.append(NOSCHEMALOCATION);
					
 					root.addAttribute(name.toString(),schemaURL);
 				}
 			}
 			else
 			{
 				// update existing attribute
 				attr.setValue(schemaURL);
 			}
 		}
 	}
 	
 	public static String canonicalize(ExchangerDocument document,String name,String xpathPred,
 			ConfigurationProperties props) throws DocumentException,CanonicalizationException, 
			InvalidCanonicalizerException, TransformerException, Exception

	{
 		// apache api needs to be initialized before being used
	      org.apache.xml.security.Init.init();
	      
	      
 		
 		if ((xpathPred != null) && (!xpathPred.equals("")))
 		{
 			String xpath = "(//. | //@* | //namespace::*)["+xpathPred+"]";
 			Map mappings = props.getPrefixNamespaceMappings();
 			
 			// get the w3c doc
 			Document w3cDoc =  document.getW3CDocument();
 			
 			// creats a context element that contains all the prefix-namespaces mappings
 			
 			org.w3c.dom.Element ele = null;
 			if (mappings.size() > 0)
 			{
 				Iterator iterator = mappings.keySet().iterator();
 				String prefix = (String)iterator.next();
 				String namespace = (String)mappings.get(prefix);
 				
 				ele = XMLUtils.createDSctx(w3cDoc,prefix,namespace);
 				
 				while (iterator.hasNext())
 				{
 					prefix = (String)iterator.next();
 					namespace = (String)mappings.get(prefix);
 					ele.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:"+prefix,namespace);
 				}
 			}
 			
 			XMLUtils.circumventBug2650(w3cDoc);
 			
 			NodeList nl = XPathAPI.selectNodeList(w3cDoc, xpath, ele);
 		
 			Canonicalizer c14n = Canonicalizer.getInstance(name);
 		
 			try{
 				byte[] out = c14n.canonicalizeXPathNodeSet(nl);
 				return (new String(out,Canonicalizer.ENCODING));
 			}
 			catch(Exception e)
			{
 				// ignore as we must be able to support UTF8
 			}
 		}
 		else
 		{
 			Canonicalizer c14n = Canonicalizer.getInstance(name);
 			try{
 				Document doc = document.getW3CDocument();
 				byte[] out = c14n.canonicalizeSubtree(doc);
 				return (new String(out,Canonicalizer.ENCODING));
 			}
 			catch(Exception e)
			{
 				throw new Exception(e);
 			}
 
 		}
 		
 		return null;
 		 		
 	}
} 
