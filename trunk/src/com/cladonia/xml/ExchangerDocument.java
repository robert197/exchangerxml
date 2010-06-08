/*
 * $Id: ExchangerDocument.java,v 1.38 2005/05/06 16:22:18 gmcgoldrick Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.event.EventListenerList;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentException;
import org.dom4j.DocumentType;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.io.DOMReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

//import com.cladonia.xml.c14n.C14N;
import com.cladonia.xngreditor.URLUtilities;

/**
 * The default implementation of the Xml document.
 * This implementation is completely thread safe, the events
 * are always fired on the Swing event thread.
 *
 * @version	$Revision: 1.38 $, $Date: 2005/05/06 16:22:18 $
 * @author Dogsbay
 */
public class ExchangerDocument {
	private static final boolean DEBUG = false;
	
	public static final int UNKNOWN_DOCUMENT	= -1;
	public static final int XML_DOCUMENT		= 0;
	public static final int DTD_DOCUMENT		= 1;

	private EventListenerList listeners = null;
//	private XMLErrorHandler errorHandler	= null;

	private String encoding		= null;

	private String name			= "New Document";
	private File file			= null;
	private URL url				= null;

	private XDocument document		= null;
	private XDocument lastDocument	= null;
	private String text				= null;

	private int forcedType		= UNKNOWN_DOCUMENT;

	private XMLGrammar grammar = null;

	private boolean valid		= false;

	private Exception exception				= null;
//	private Exception validationException	= null;

	private Hashtable elementNames		= null;
	private Hashtable attributeNames	= null;
	private Vector namespaces		= null;

	private long modified		= 0;
	
	private boolean stripWhiteSpace = false;
	
	public static final String STANDALONE_NONE = "NONE";
	
	private boolean hasEncoding =  false;
	private boolean saving =  false;
	private String standalone =  STANDALONE_NONE;
	private String version = "1.0";

 	/**
 	 * Constructs a Document for the type.
 	 *
 	 * @param location the url of the document.
 	 */
 	public ExchangerDocument( int type) {
		listeners = new EventListenerList();

		forcedType = type;
	}

 	/**
 	 * Constructs an Xml Document from the url.
 	 *
 	 * @param location the url of the document.
 	 */
 	public ExchangerDocument( XElement root) throws IOException, SAXParseException {
 		this( null, root);
 	}

 	/**
 	 * Constructs an Xml Document from the url.
 	 *
 	 * @param location the url of the document.
 	 */
 	public ExchangerDocument( URL location) throws IOException, SAXParseException {
 		this( null, location);
 	}
 	
 	/**
 	 * Constructs an Xml Document from the url.
 	 *
 	 * @param location the url of the document.
 	 */
 	public ExchangerDocument( URL location, boolean stripWhiteSpace) throws IOException, SAXParseException {
 		this( null, location, stripWhiteSpace);
 	}

 	/**
 	 * Constructs an Xml Document from the url.
 	 *
 	 * @param location the url of the document.
 	 * @param root the root element for the document.
 	 */
 	public ExchangerDocument( URL location, XElement root) {
		this( new XDocument( root), location);
	}
 	
 	/**
 	 * Constructs an Xml Document from the url and 
 	 * dom4j document supplied.
 	 *
 	 * @param document the dom4j document.
 	 * @param location the url of the document.
 	 */
 	private ExchangerDocument( XDocument document, URL location) {
 		listeners = new EventListenerList();
//		errorHandler = new XMLErrorHandler();
 		
		this.document = document;
		this.lastDocument = document;
 		this.url = location;
 		
		if ( location != null && "file".equals( location.getProtocol())) {
	 		file = new File( location.getPath());
 			modified = file.lastModified();
		} 
		
		if ( document != null) {
			setEncoding( document.getEncoding());

			XElement root = (XElement)document.getRootElement();

//			if ( root != null) {
				// Set this as the document in the root element.
//				root.document( this);
//			}
			
			writeText();
		}
 	}

 	/**
 	 * Constructs an Xml Document from the url and 
 	 * dom4j document supplied.
 	 *
 	 * @param document the dom4j document.
 	 * @param location the url of the document.
 	 */
 	private ExchangerDocument( XDocument document, URL location, boolean stripWhiteSpace) {
 		listeners = new EventListenerList();
//		errorHandler = new XMLErrorHandler();
		this.stripWhiteSpace = stripWhiteSpace;
 		
		this.document = document;
		this.lastDocument = document;
 		this.url = location;
 		
		if ( location != null && "file".equals( location.getProtocol())) {
	 		file = new File( location.getPath());
 			modified = file.lastModified();
		} 
		
		if ( document != null) {
			setEncoding( document.getEncoding());

			XElement root = (XElement)document.getRootElement();

//			if ( root != null) {
				// Set this as the document in the root element.
//				root.document( this);
//			}
			
			writeText();
		}
 	}

 	/**
 	 * Constructs an Xml Document from the text supplied.
 	 *
 	 * @param document the dom4j document.
 	 * @param location the url of the document.
 	 */
 	public ExchangerDocument( String text) {
 		listeners = new EventListenerList();
// 		errorHandler = new XMLErrorHandler();
		
		try {
			setText( text);
		} catch ( Exception e) {
			// allow the document to be created with errors!
		}
 	}
 	
 	
 	/**
 	 * Constructs an Xml Document from the text supplied.
 	 *
 	 * @param document the dom4j document.
 	 * @param location the url of the document.
 	 */
 	public ExchangerDocument( String text, boolean stripWhiteSpace) {
 		listeners = new EventListenerList();
// 		errorHandler = new XMLErrorHandler();
 		this.stripWhiteSpace = stripWhiteSpace;
		
		try {
			setText( text);
		} catch ( Exception e) {
			// allow the document to be created with errors!
		}
 	}

 	/**
 	 * Gets an element from this document for the specified xpath.
	 * Returns Null, if the element cannot be found.
	 *
	 * @param the xpath expression to the element.
 	 *
 	 * @return the element.
 	 */	
 	public XElement getElement( String xpath) {
		XElement[] elements = getElements( xpath);
		
		if ( elements.length > 0) {
			return elements[0];
		}
		
		return null;
 	}

 	/**
 	 * Gets a list of elements from this document for the 
	 * specified xpath. Returns Null, if no elements can 
	 * be found.
 	 *
 	 * @param the xpath expression to the elements.
 	 *
 	 * @return the elements.
 	 */	
 	public XElement[] getElements( String xpath) {
 		List list = document.selectNodes( xpath);
		Vector elements = new Vector();
		Iterator iterator = list.iterator();
		
		while ( iterator.hasNext()) {
			Object object = iterator.next();
			
			if ( object instanceof XElement) {
				elements.addElement( object);
			}
		}
		
 		XElement[] result = new XElement[ elements.size()];
 		
		for ( int i = 0; i < elements.size(); i++) {
			result[i] = (XElement)elements.elementAt(i);
		}
 		
 		return result;
 	}

 	/**
 	 * Returns the element for the position in the test, 
 	 * this will return the root element if the 
 	 * position is not an element. 
 	 *
 	 * @param pos the text position.
 	 *
 	 * @return the element that is at the position.
 	 */	
 	public XElement getElement( int pos) {
 		XElement result = null;
 		
 		result = getElement( getRoot(), pos, true);
 		
 		if ( result == null) {
 			result = getRoot();
 		}
 		
 		return result;
 	}

 	public XElement getLastElement( int pos) {
 		XElement result = null;
 		
 		result = getElement( getLastRoot(), pos, true);
 		
 		if ( result == null) {
 			result = getLastRoot();
 		}
 		
 		return result;
 	}

 	public Object getLastNode( int pos, boolean current) {
 		Object result = null;
 		
 		result = getNode( getLastRoot(), pos, current);
 		
 		if ( result == null) {
 			result = getLastRoot();
 		}
 		
 		return result;
 	}

 	/**
 	 * Returns the URL for this document. 
 	 *
 	 * @return the URL for this document.
 	 */	
 	public URL getURL() {
 		try {
 			return new URL( URLUtilities.toString( url));
 		} catch ( MalformedURLException e) {
 			// do nothing;
 		}
 		
 		return null;
 	}
	
 	/**
	 * Sets the URL for this document. 
	 *
	 * @param url the URL for this document.
	 */	
	public void setURL( URL url) {
		this.url = url;
		
		if ( url != null && "file".equals( url.getProtocol())) {
			file = new File( url.getPath());
			modified = file.lastModified();
		} else {
			file = null;
			modified = 0;
		}
	}

	/**
	 * The name used when no File or URL are set...
	 *
	 * @param name the name for the document.
	 */	
	public void setName( String name) {
		this.name = name;
	}

	/**
	 * Sets the grammar type for this document.
	 *
	 * @param grammar the grammar for this document.
	 */	
	public void setGrammar( XMLGrammar grammar) {
		if (DEBUG) System.out.println( "ExchangerDocument.setGrammar( "+grammar+")");
		this.grammar = grammar;
	}

	/**
	 * Get the grammar type for this document.
	 *
	 * @return the grammar for this document.
	 */	
	public XMLGrammar getGrammar() {
		if (DEBUG) System.out.println( "ExchangerDocument.getGrammar()");
		return grammar;
	}

	/**
	 * Returns the name for this document. 
	 *
	 * @return the name for this document.
	 */	
	public String getName() {
		if ( file != null) {
			return file.getName();
		} else if ( url != null) {
			return URLUtilities.getFileName( url);
		} else {
			return name;
		}
	}

	/**
	 * Returns the root element for this document. 
	 *
	 * @return the root element.
	 */	
	public XElement getRoot() {
		if ( document != null) {
			return (XElement)document.getRootElement();
		} else {
			return null;
		}
	}

	/**
	 * Returns the root element for the last well-formed document.
	 *
	 * @return the root element.
	 */	
	public XElement getLastRoot() {
		if ( lastDocument != null) {
			return (XElement)lastDocument.getRootElement();
		} else {
			return null;
		}
	}

	/**
	 * Sets the root element for this document. 
	 *
	 * @param root the root element.
	 */	
	public void setRoot( XElement root) {
		document.setRootElement( root);

//		if ( root != null) {
			// Set this as the document in the root element.
//			root.document( this);
//		}
	}

	/**
	 * Check to see if previously loading the document
	 * resulted in an error.
	 *
	 * @return true when loading the document generated an error.
	 */	
	public boolean isError() {
		return exception != null;
	}

	/**
	 * Check to see if previously validating the document
	 * resulted in an error.
	 *
	 * @return true when valdating the document generated an error.
	 */	
//	public boolean isValidationError() {
//		return validationException != null;
//	}

	/**
	 * Check to see if the document is a remote document.
	 *
	 * @return true when the document is remote.
	 */	
	public boolean isRemote() {
		if ( url != null) {
			return !url.getProtocol().equals( "file");
		} else {
			return false;
		}
	}

	/**
	 * Check to see if the document is set to read only.
	 *
	 * @return true when the document cannot be changed.
	 */	
	public boolean isReadOnly() {
		if ( file != null) {
			return !file.canWrite();
		} else {
			return false;
		}
	}

	/**
	 * Returns the error generated when previously loading 
	 * the document resulted in an error.
	 *
	 * @return the error.
	 */	
	public Exception getError() {
		return exception;
	}

	/**
	 * Returns the error generated when previously validating
	 * the document resulted in an error.
	 *
	 * @return the error.
	 */	
//	public Exception getValidationError() {
//		return validationException;
//	}

	/**
	 * Saves the document to disc and informs the 
	 * listeners that the document has been changed.
	 */	
	public void save() throws IOException {
		if (DEBUG) System.out.println( "ExchangerDocument.save()");
		try {
			saving = true;
			
			URLUtilities.save( url, new ByteArrayInputStream( text.getBytes( getJavaEncoding())), getJavaEncoding());
//);
//			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( out, getJavaEncoding()));
//
//			writer.write( text, 0, text.length());
//			writer.flush();
//			writer.close();
		} finally {
			if ( file != null) {
				modified = file.lastModified();
			}
			
			saving = false;

			fireDocumentUpdated( getRoot(), ExchangerDocumentEvent.SAVED);
		}
	}

	/**
	 * Returns the text for this document.
	 *
	 * @return the text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text in the document model.
	 *
	 * @param text the text.
	 */
	public void setText( String text) throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "ExchangerDocument.setText()");
		XElement root = null;
		
		exception = null;
//		validationException = null;
		
		document = null;
		
		this.text = text;
		
		// check for any updates to the XML Declaration
		updateDeclaration();

		try {
			byte[] bytes = text.getBytes( getJavaEncoding());
			ByteArrayInputStream stream = new ByteArrayInputStream( bytes);
			InputStreamReader reader = new InputStreamReader( stream, getJavaEncoding());
			String systemId = null;

			
			if ( url != null) {
				systemId = URLUtilities.toString( url);
			}
				
			String location = null;
			
			if ( grammar != null) {
				location = grammar.getLocation();
			}
			
			if ( grammar != null && grammar.useExternal() && grammar.getType() == XMLGrammar.TYPE_DTD && location != null) 
			{
				if (stripWhiteSpace)
				{
					document = XMLUtilities.parse( new BufferedReader( reader), bytes.length, systemId, location, stripWhiteSpace);
				}
				else
				{
					document = XMLUtilities.parse( new BufferedReader( reader), bytes.length, systemId, location);
				}
			} else 
			{
				if (stripWhiteSpace)
				{
					document = XMLUtilities.parse( new BufferedReader( reader), bytes.length, systemId,stripWhiteSpace);
				}
				else
				{
					document = XMLUtilities.parse( new BufferedReader( reader), bytes.length, systemId);
				}
			}
			//setEncoding( document.getEncoding());

			valid = false;
							
//			if ( lastDocument != null) {
//				lastDocument.cleanup();
//			}
			lastDocument = document;

			root = (XElement)document.getRootElement();

			// Set this as the document in the root element.
//			root.document( this);
			
			// make sure the text is uptodate 
			// and that the model nodes have the correct positions
			writeText(text);
			
		} catch ( IOException e) {
			exception = e;
//			e.printStackTrace();
			throw e;
		} catch ( SAXParseException e) {
			exception = e;
//			e.printStackTrace();
			throw e;
		} finally {
			fireDocumentUpdated( root, ExchangerDocumentEvent.MODEL_UPDATED);
		}
	}

	/**
	 * Validates the document.
	 */
	public void validate( ErrorHandler handler) throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "ExchangerDocument.validate()");
//		validationException = null;
		
		try {
			String systemId = null;

			if ( url != null) {
				systemId = URLUtilities.toString( url);
			}

			ByteArrayInputStream stream = new ByteArrayInputStream( getText().getBytes( getJavaEncoding()));
			InputStreamReader reader = new InputStreamReader( stream, getJavaEncoding());
//			errorHandler.clear();

			String location = null;
			
			if ( grammar != null) {
				location = grammar.getLocation();
			}
			
			if ( grammar != null && grammar.useExternal() && location != null) {
				XMLUtilities.validate( handler, new BufferedReader( reader), systemId, getJavaEncoding(), grammar.getType(), location);
			} else {
				XMLUtilities.validate( handler, new BufferedReader( reader), systemId);
			}

			valid = true;

		} catch ( IOException e) {
//			validationException = e;
			throw e;
		} catch ( SAXParseException e) {
//			validationException = e;
			throw e;
		} catch ( Exception e) {
			e.printStackTrace();
		} 
//		finally {
//			fireDocumentUpdated( getRoot(), ExchangerDocumentEvent.VALIDATED);
//		}
	}
	
	public InputSource getInputSource()  throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream( getText().getBytes( getJavaEncoding()));
		InputStreamReader reader = new InputStreamReader( stream, getJavaEncoding());
		
		String systemId = "";

		if ( url != null) {
			systemId = URLUtilities.toString( url);
		}

		InputSource source = new InputSource( reader);
		source.setEncoding( getJavaEncoding());
		source.setSystemId( systemId);
		
		return source;
	}

	public URL checkSchemaLocation() throws IOException {
		if (DEBUG) System.out.println( "ExchangerDocument.checkSchemaLocation()");
		if ( !isError()) {
			int type = -1;

			String schemaLocation = null;
			URL baseURL = getURL();
			
			if ( grammar != null && grammar.useExternal() && grammar.getLocation() != null) {
				schemaLocation = grammar.getLocation();
				type = grammar.getType();
			} else {
				DocumentType docType = document.getDocType();

				if ( docType != null) {
					schemaLocation = XMLUtilities.resolve( docType.getPublicID(), docType.getSystemID());
					type = XMLGrammar.TYPE_DTD;
					
					if ( schemaLocation == null) {
						schemaLocation = docType.getSystemID();
					}
					
					// no system id in the doctype, maybe internal DTD???
					if ( schemaLocation == null) {
						return null;
					}
				}
				
				if ( schemaLocation == null) {
					schemaLocation = getRoot().getAttribute( "schemaLocation");

					if ( schemaLocation != null) {
						StringTokenizer tokenizer = new StringTokenizer( schemaLocation);
						if ( tokenizer.countTokens() > 1) {
							String namespace = tokenizer.nextToken(); // discard first token, it is a namespace...
							
							schemaLocation = schemaLocation.substring(namespace.length()).trim();
							//schemaLocation = tokenizer.nextToken();
						} //else {
							//schemaLocation = tokenizer.nextToken();
						//}
					}

					type = XMLGrammar.TYPE_XSD;
				}
				
				if ( schemaLocation == null) {
					schemaLocation = getRoot().getAttribute( "noNamespaceSchemaLocation");
	
					type = XMLGrammar.TYPE_XSD;
				} 
			}
			
			if (DEBUG) System.out.println("schemaLocation: " + schemaLocation);
			
			if ( schemaLocation == null) {
				throw new IOException( "No validation schema location has been defined for this document.");
			}
			
			URL url = null;

			try {
				if ( baseURL != null) {
					url = new URL( baseURL, schemaLocation);
				} else {
					url = new URL( schemaLocation);
				}
			} catch ( Exception e) {
				url = XngrURLUtilities.getURLFromFile(new File( schemaLocation));
			}

			
			if (DEBUG) System.out.println("url: " + url.toString());

			InputStream stream = null;
			
			try {
				stream = url.openStream();
				return url;
			} catch (IOException e) {
				String message = null;
				
				if ( type == XMLGrammar.TYPE_DTD) {
					message = "No valid DTD location has been defined for this document.";
				} else if ( type == XMLGrammar.TYPE_XSD) {
					message = "No valid Schema location has been defined for this document.";
				} else if ( type == XMLGrammar.TYPE_RNG) {
					message = "No valid RelaxNG location has been defined for this document.";
				} else if ( type == XMLGrammar.TYPE_RNC) {
					message = "No valid RelaxNG location has been defined for this document.";
				} else if ( type == XMLGrammar.TYPE_NRL) {
					message = "No valid NRL location has been defined for this document.";
				}
				
				throw new IOException( message);
			} finally {
				if ( stream != null) {
					stream.close();
				}
			}
		}
		
		return null;
	}

	/**
	 * Validates the document.
	 */
	/*public void canonicalize() throws Exception, IOException, SAXParseException {
		if (DEBUG) System.out.println( "ExchangerDocument.canonicalize()");
		
		text = C14N.canonicalize( text, getEncoding());
		setText( text);
	}*/

	public int getInternalGrammarType() {
		int type = -1;

		if ( !isError() && isXML() && document != null) {
			DocumentType docType = document.getDocType();

			if ( docType != null) {
				type = XMLGrammar.TYPE_DTD;
			}
			
			if ( type == -1) {
				
				String schemaLocation = getRoot().getAttribute( "schemaLocation");

				if ( schemaLocation != null) {
					type = XMLGrammar.TYPE_XSD;
				}

				if ( schemaLocation == null) {
					schemaLocation = getRoot().getAttribute( "noNamespaceSchemaLocation");

					if ( schemaLocation != null) {
						type = XMLGrammar.TYPE_XSD;
					}
				} 
			}
		}
		
		return type;
	}

	public String getPublicID() {
		DocumentType docType = document.getDocType();

		if ( docType != null) {
			return docType.getPublicID();
		}
		
		return null;
	}

	public String getSystemID() {
		DocumentType docType = document.getDocType();

		if ( docType != null) {
			String systemID = XMLUtilities.resolve( docType.getPublicID(), docType.getSystemID());
			
			if ( systemID == null) {
				systemID = docType.getSystemID();
			}

			return systemID;
		}
		
		return null;
	}

	public URL getSchemaURL() {
		URL schemaURL = null;

		if ( !isError() && isXML()) {
			String schemaLocation = null;
			
			if ( grammar != null && grammar.useExternal() && grammar.getLocation() != null && grammar.getType() == XMLGrammar.TYPE_XSD) {
				schemaLocation = grammar.getLocation();
			} else {
				schemaLocation = getRoot().getAttribute( "schemaLocation");
				
				if ( schemaLocation != null) {

					StringTokenizer tokenizer = new StringTokenizer( schemaLocation);
					if ( tokenizer.countTokens() > 1) {
						tokenizer.nextToken(); // discard first token, it is a namespace...
						schemaLocation = tokenizer.nextToken();
					} else {
						schemaLocation = tokenizer.nextToken();
					}
				}

				if ( schemaLocation == null) {
					schemaLocation = getRoot().getAttribute( "noNamespaceSchemaLocation");
				} 
			}
			
			URL baseURL = getURL();

			try {
				if ( baseURL != null) {
					schemaURL = new URL( baseURL, schemaLocation);
				} else {
					schemaURL = new URL( schemaLocation);
				}
			} catch ( Exception e) {
				try {
					if ( baseURL != null) {
						schemaURL = new URL( baseURL, schemaLocation);
					} else {
						schemaURL = XngrURLUtilities.getURLFromFile(new File( schemaLocation));
					}
				} catch ( Exception x) {
					// The url could not be constructed, just return null
					schemaURL = null;
				}
			}
		}
		
		return schemaURL;
	}

	public boolean isValid() {
		return valid;
	}

	public int getType() {
		if ( forcedType == UNKNOWN_DOCUMENT) {
			URL url = getURL();
			
			if ( url != null) {
				if( url.toString().toLowerCase().endsWith( "dtd") || url.toString().toLowerCase().endsWith( "mod") || url.toString().toLowerCase().endsWith( "ent")) {
					return DTD_DOCUMENT;
				}
			}
			
			if ( exception instanceof XMLUtilities.NotXMLException) {
				return UNKNOWN_DOCUMENT;
			}
			
			return XML_DOCUMENT;
		} else {
			return forcedType;
		}
	}

	public boolean isXML() {
		return getType() == XML_DOCUMENT;
	}

	public boolean isDTD() {
		return getType() == DTD_DOCUMENT;
	}

	public String getEncoding() {
		if (DEBUG) System.out.println( "ExchangerDocument.getEncoding() ["+encoding+"]");

		if ( encoding != null) {
			return encoding;
		}

		return "UTF-8";
	}
	
	public int getCount( QName name) {
		return ((Counter)elementNames.get( name)).counter;
	}

	public void setEncoding( String encoding) {
		if (DEBUG) System.out.println( "ExchangerDocument.setEncoding( "+encoding+")");
	
		this.encoding = encoding;
	}

	public String getJavaEncoding() {
		String encoding = XMLUtilities.mapXMLEncodingToJava( getEncoding());

		if (DEBUG) System.out.println( "ExchangerDocument.getJavaEncoding() ["+encoding+"]");
		return encoding;
	}

	/**
	 * Loads the document from disc and informs the 
	 * listeners that the document has been changed.
	 */	
	public void load() throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "ExchangerDocument.load()");
		XElement root = null;
		exception = null;

		document = null;
		
		String textURL = new String();

		try {
			XMLUtilities.XMLDeclaration decl = new XMLUtilities.XMLDeclaration();
			text = XMLUtilities.getText( url, decl);
			
			// need the encoding for setText() !
			setEncoding( decl.getEncoding());
			
			setText( text);

//			document = XMLUtilities.parse( url);
//			setEncoding( document.getEncoding());
//
//			valid = false;
//			modified = file.lastModified();
//
//			if ( lastDocument != null) {
//				lastDocument.cleanup();
//			}
//
//			lastDocument = document;
//
//			root = (XElement)document.getRootElement();
//
//			// Set this as the document in the root element.
//			root.document( this);
//			
//			textURL = XMLUtilities.getText( url, getJavaEncoding());
		} catch ( IOException e) {
			exception = e;
			throw e;

		} catch ( SAXParseException e) {
			exception = e;
			
			// there was a SAXParseException, try to get the xml as text
//			XMLUtilities.XMLDeclaration decl = new XMLUtilities.XMLDeclaration();
//			text = XMLUtilities.getText( url, decl);
//			setEncoding( decl.getEncoding());

			throw e;
//		} finally {
//			if ( !isError()) { 
				//writeText();
//				writeText( text);
//				fireDocumentUpdated( root, ExchangerDocumentEvent.CONTENT_UPDATED);
//			}
		}
	}
	
	/**
	 * Loads the document from disc and informs the listeners that 
	 * the document has been changed, does not substitute '&' characters
	 * and is only used for the properties file.
	 */	
	public void loadWithoutSubstitution() throws IOException, SAXParseException {
		if (DEBUG) System.out.println( "ExchangerDocument.loadWithoutSubstitution()");
		XElement root = null;
		exception = null;

		document = null;

		try {
			document = XMLUtilities.parseWithoutSubstitution( url);
			setEncoding( document.getEncoding());

			valid = false;
			modified = file.lastModified();

//			if ( lastDocument != null) {
//				lastDocument.cleanup();
//			}

			lastDocument = document;

			root = (XElement)document.getRootElement();

			// Set this as the document in the root element.
//			root.document( this);

		} catch ( IOException e) {
			exception = e;
			throw e;
		} catch ( SAXParseException e) {
			exception = e;

			// there was an error, try to get the xml as text
			XMLUtilities.XMLDeclaration decl = new XMLUtilities.XMLDeclaration();
			text = XMLUtilities.getText( url, decl);
			setEncoding( decl.getEncoding());

			throw e;
		} finally {
			if ( !isError()) {
//				writeText();
				fireDocumentUpdated( root, ExchangerDocumentEvent.CONTENT_UPDATED);
			}
		}
	}

	/**
	 * Searches for elements and attributes matching the given xpath.
	 *
	 * @return a list of matching (parent)elements or attributes
	 */	
	public Vector search( String xpath) {
		return search( xpath, getDeclaredNamespaces());
	}

	public Vector search( String xpath, Vector namespaces) {
		Vector results = new Vector();

		if ( !isError()) {
			Map namespaceURIs = XDocumentFactory.getInstance().getXPathNamespaceURIs();
			HashMap map = new HashMap();
			Iterator keys = namespaceURIs.keySet().iterator();
			
			while ( keys.hasNext()) {
				Object prefix = keys.next();
				Object namespace = namespaceURIs.get( prefix);
				
				map.put( prefix, namespace);
			}
		
			for ( int i = 0; i < namespaces.size(); i++) {
				Namespace namespace = (Namespace)namespaces.elementAt( i);
				String prefix = namespace.getPrefix();
				
				if ( prefix != null && prefix.trim().length() > 0) {
					map.put( prefix, namespace.getURI());
				}
			}

			XDocumentFactory.getInstance().setXPathNamespaceURIs( map);

			Object object = document.selectObject( xpath);
			
			if ( object instanceof List) {
				List list = (List)object;
				Iterator iterator = list.iterator();
				
				while ( iterator.hasNext()) {
					Node node = (Node)iterator.next();
					results.addElement( node);
				}
			} else {
				results.addElement( object);
			}

			XDocumentFactory.getInstance().setXPathNamespaceURIs( namespaceURIs);
		}

		return results;
	}

	public Vector searchLastWellFormedDocument( String xpath) {
		Vector results = new Vector();

		if ( lastDocument != null) {
			Map namespaceURIs = XDocumentFactory.getInstance().getXPathNamespaceURIs();
			HashMap map = new HashMap();
			Iterator keys = namespaceURIs.keySet().iterator();
			
			while ( keys.hasNext()) {
				Object prefix = keys.next();
				Object namespace = namespaceURIs.get( prefix);
				
				map.put( prefix, namespace);
			}
		
			Vector namespaces = getDeclaredNamespaces();

			for ( int i = 0; i < namespaces.size(); i++) {
				Namespace namespace = (Namespace)namespaces.elementAt( i);
				String prefix = namespace.getPrefix();
				
				if ( prefix != null && prefix.trim().length() > 0) {
					map.put( prefix, namespace.getURI());
				}
			}

			XDocumentFactory.getInstance().setXPathNamespaceURIs( map);

			Object object = lastDocument.selectObject( xpath);
			
			if ( object instanceof List) {
				List list = (List)object;
				Iterator iterator = list.iterator();
				
				while ( iterator.hasNext()) {
					Node node = (Node)iterator.next();
					results.addElement( node);
				}
			} else {
				results.addElement( object);
			}

			XDocumentFactory.getInstance().setXPathNamespaceURIs( namespaceURIs);
		}


		return results;
	}

	/**
	 * The document model has been updated, update the text 
	 * and fire an update event. 
	 */	
	public void update() {
		if (DEBUG) System.out.println( "ExchangerDocument.update()");
		writeText();

		fireDocumentUpdated( getRoot(), ExchangerDocumentEvent.TEXT_UPDATED);
	}
	
	public boolean isSOAP()	{
		XElement root = getLastRoot();
	
		if (root != null) {
			if (root.getName().equals("Envelope")) {
				if ( root.getNamespaceURI().equals("http://schemas.xmlsoap.org/soap/envelope/")) {
					return true;
				}
			}
		}
		
		return false;
	}

	public boolean isWSDL()	{
		XElement root = getLastRoot();
	
		if (root != null) {
			if (root.getName().equals( "definitions")) {
				if ( root.getNamespaceURI().equals( "http://schemas.xmlsoap.org/wsdl/")) {
					return true;
				}
			}
		}
		
		return false;
	}

	public boolean isSVG()	{
		XElement root = getLastRoot();
	
		if ( root != null) {
			if ( root.getName().equals( "svg")) {
				if ( root.getNamespaceURI().equals( "http://www.w3.org/2000/svg")) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isXSD()	{
		XElement root = getLastRoot();
	
		if ( root != null) {
			if ( root.getName().equals( "schema")) {
				if ( root.getNamespaceURI().equals( "http://www.w3.org/2001/XMLSchema")) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isRNG() {
		XElement root = getLastRoot();

		if (root != null) {
			if ( root.getName().equals( "grammar")) {
				if ( root.getNamespaceURI().equals( "http://relaxng.org/ns/structure/1.0")) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isXSL() {
		XElement root = getLastRoot();

		if (root != null) {
			if ( root.getName().equals( "stylesheet") || root.getName().equals( "transform")) {
				if ( root.getNamespaceURI().equals( "http://www.w3.org/1999/XSL/Transform")) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks to find out if this version of the document is consistent 
	 * with the one saved on disk. Tries to find out if the document has 
	 * been changed or deleted by an external process.<p>
	 * When the document has been changed or deleted by an external 
	 * process the correct event is fired to the document listener.
	 */	
	 public void consistent() {
	 	
	 	if ( file != null && url != null && "file".equals( url.getProtocol())) {
			File file = new File( url.getPath());
	
			if ( file.exists()) {
				if ( modified != file.lastModified()) {
					// Make sure the event is always fired on the GUI thread!
	//				SwingUtilities.invokeAndWait( new Runnable() {
	//				    public void run() {
	//						fireDocumentUpdatedExternally();
	//				    }
	//				});
				}
			} else {
				// Make sure the event is always fired on the GUI thread!
	//			SwingUtilities.invokeAndWait( new Runnable() {
	//			    public void run() {
	//					fireDocumentDeletedExternally();
	//			    }
	//			});
			}
	 	}
	 }

	 public boolean isModified() {
	 	if ( !saving && file != null && url != null && "file".equals( url.getProtocol())) {
			File file = new File( url.getPath());
	
			if ( file.exists()) {
				if ( modified != file.lastModified()) {
					modified = file.lastModified();
					return true;
				}
//			} else {
//				return true;
			}
	 	}
	 	
	 	return false;
	 }

	 /**
	 * Adds a document listener to the document. 
	 */	
	public void addListener( ExchangerDocumentListener listener) {
		if ( listeners != null) {
			listeners.add( (Class)listener.getClass(), listener);
		}
	}

	/**
	 * Removes a document listener from the document.
	 */	
	public void removeListener( ExchangerDocumentListener listener) {
		if ( listeners != null) {
			listeners.remove( (Class)listener.getClass(), listener);
		}
	}

	/**
	 * This is not an XDocument method!
	 * This method returns the dom4j document.
	 *
	 * @return the properties for this document.
	 */	
	public XDocument getDocument() {
		return document;
	}
	
	/**
	 * Returns the list of defined element names.
	 *
	 * @return the list of element names.
	 */
	public Vector getElementNames() {
		return new Vector( elementNames.keySet());
	}

	/**
	 * Returns the list of defined attribute names.
	 *
	 * @return the list of attribute names.
	 */
	public Vector getAttributeNames() {
		return new Vector( attributeNames.keySet());
	}

	/**
	 * Returns the list of values defined for the current attribute.
	 *
	 * @return the list of attribute values.
	 */
	public Vector getAttributeValues( QName name) {
		return (Vector)attributeNames.get( name);
	}

	/**
	 * Returns the list of defined prefixes.
	 *
	 * @return the list of prefixes.
	 */
	public Vector getDeclaredNamespaces() {
		return namespaces;
	}
	
	/**
	 * Returns the W3C Document.
	 *
	 * @return the w3c document.
	 */
	public Document getW3CDocument() throws IOException, SAXException {
		Document doc = null;

		try {
			javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setAttribute( "http://xml.org/sax/features/namespaces", Boolean.TRUE);
	
			javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
			ByteArrayInputStream stream = new ByteArrayInputStream( getText().getBytes( getJavaEncoding()));
			
			InputStreamReader isReader = new InputStreamReader( stream, getJavaEncoding());
			InputSource source = new InputSource(isReader);

			if (url != null)
			{
				source.setSystemId( url.toString());
			}

			doc = db.parse( source);
		} catch( ParserConfigurationException e) {
			e.printStackTrace();
		}

//		DOMWriter writer = new DOMWriter();
//		
//		Document doc = writer.write( document);
//
//		DOMReader reader = new DOMReader();
//		XDocument doc2 = (XDocument)reader.read( doc);
//		System.out.println( "doc = "+doc2.asXML());
		
		return doc;
	}

	/**
	 * Sets the W3C Document.
	 *
	 * @param the w3c document.
	 * @deprecated this does not work!
	 */
	public void setW3CDocument( Document document) throws DocumentException {
		DOMReader reader = new DOMReader();
		this.document = (XDocument)reader.read( document);
		
		writeText(); // write the text out of the dom directly

		fireDocumentUpdated( getRoot(), ExchangerDocumentEvent.CONTENT_UPDATED);
	}

	private void setNames( Hashtable eNames, Hashtable aNames, Vector prefixes, XElement element) {
		XElement[] elements = element.getElements();
		
		for ( int i = 0; i < elements.length; i++) {
			setNames( eNames, aNames, prefixes, elements[i]);
		}
		
		XAttribute[] attributes = element.getAttributes();
		
		for ( int i = 0; i < attributes.length; i++) {
			QName name = attributes[i].getQName();
			
			Vector values = (Vector)aNames.get( name);

			if ( values == null) {
				values = new Vector();
				aNames.put( name, values);
			}
			
			if ( !values.contains( attributes[i].getValue())) {
				values.addElement( attributes[i].getValue());
			}
		}

		QName name = element.getQName();
		Counter count = (Counter)eNames.get( name);

		if ( count == null) {
			eNames.put( name, new Counter());
		} else {
			count.counter++;
		}

		List nss = element.declaredNamespaces();
		
		for ( int i = 0; i < nss.size(); i++) {
			Namespace ns = (Namespace)nss.get( i);
			
			if ( !prefixes.contains( ns) && ns.getURI() != null && ns.getURI().trim().length() > 0) { //&& ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
				prefixes.addElement( ns);
			}
		}
	}

	// uses the SimpleParser to create the positions for the tree model
	private void writeText(String text )
	{
		if (DEBUG) System.out.println( "ExchangerDocument.writeText(String text)");
		try 
		{   
		    XElement root = getRoot();
			if ( root != null) {
				elementNames = new Hashtable();
				attributeNames = new Hashtable();
				namespaces = new Vector();
				
				setNames( elementNames, attributeNames, namespaces, getRoot());
			}
			
            // get the stream from the input text
			ByteArrayInputStream stream = new ByteArrayInputStream( text.getBytes( getJavaEncoding()));
			InputStreamReader reader = new InputStreamReader( stream, getJavaEncoding());
								
			// call the simple parser
			SimpleParser simpleparser = new SimpleParser();
			simpleparser.writeText(reader,root);
			
			this.text = text;
		} 
		catch (Exception e) {
			System.out.println("An error occurred in the simple parser: "+e.getMessage());
			e.printStackTrace();
			
			// if the simple kparser has a problem then just call the normal writeText() and continue
			writeText();
		}
	}
	
	// writes the document to text
	private void writeText() {
		if (DEBUG) System.out.println( "ExchangerDocument.writeText()");
		try {
			XElement root = getRoot();
			if ( root != null) {
				elementNames = new Hashtable();
				attributeNames = new Hashtable();
				namespaces = new Vector();
				
				setNames( elementNames, attributeNames, namespaces, getRoot());
			}
			
//			System.out.println( document.asXML());

			StringWriter writer = new StringWriter();
			ExchangerOutputFormat format = new ExchangerOutputFormat( "", false, getEncoding());
			
			if ( hasDeclaration()) {
				if ( getStandalone() != STANDALONE_NONE) {
					format.setStandalone( getStandalone());
					format.setOmitStandalone( false);
				}
				
				format.setVersion( getVersion());
				format.setOmitEncoding( !hasEncoding());
				format.setSuppressDeclaration( false);
			} else {
				format.setSuppressDeclaration( true);
			}
			

//			if ( !getRoot().hasContent()) {
//				format.setExpandEmptyElements( true);
//			}
							
			XMLWriter formatter = new ExchangerXMLWriter( writer, format);
			formatter.write( document);
			formatter.flush();
			
			text = writer.toString();
			
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}

	private Object getNode( XElement root, int pos, boolean current) {
		XElement element = getElement( root, pos, current);

		if ( element != null) {
			XAttribute[] attributes = element.getAttributes();
			
			for ( int i = 0; i < attributes.length; i++) {
				if ( pos >= attributes[i].getAttributeStartPosition() && pos < attributes[i].getAttributeEndPosition()) {
					return attributes[i];
				}
			}
		}
		
		return element;
	}

	private XElement getElement( XElement element, int pos, boolean current) {
		XElement result = null;

		if ( element != null) {
			XElement[] elements = element.getElements();
			
			for ( int i = 0; i < elements.length; i++) {
				if ( pos >= elements[i].getElementStartPosition() && pos < elements[i].getElementEndPosition()) {
					XElement e = getElement( elements[i], pos, current);
					
					if ( e == null) {
						return elements[i];
					} else {
						return e;
					}
				} else if ( !current && pos >= elements[i].getContentEndPosition() && pos < ((XElement)elements[i].getParent()).getContentEndPosition()+1) {
					result = elements[i];
				}
			}
		}
		
		return result;
	}

	/** 
	 * Notifies the listeners about a change in the document.
	 *
	 * Note: This does not happen on the event-dispatching thread!
	 */
	protected void fireDocumentUpdated( final XElement element, final int type) {
		// Make sure the event is always fired on the GUI thread!
//		try {
//			SwingUtilities.invokeAndWait( new Runnable() {
//			    public void run() {
					// Guaranteed to return a non-null array
					Object[] list = listeners.getListenerList();
					
					// Process the listeners last to first, notifying
					// those that are interested in this event
					for ( int i = list.length-2; i >= 0; i -= 2) {
						((ExchangerDocumentListener)list[i+1]).documentUpdated( new ExchangerDocumentEvent( ExchangerDocument.this, element, type));
					}
//				}
//			});
//		} catch ( InvocationTargetException e) {
//			e.printStackTrace();
//		} catch ( InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	/** 
	 * Notifies the listeners about the deletion of this document.
	 */
//	protected void fireDocumentDeleted() {
		// Guaranteed to return a non-null array
//		Object[] list = listeners.getListenerList();
		
		// Process the listeners last to first, notifying
		// those that are interested in this event
//		for ( int i = list.length-2; i >= 0; i -= 2) {
//		    if ( list[i] == ExchangerDocumentListener.class) {
//				((ExchangerDocumentListener)list[i+1]).documentDeleted( new ExchangerDocumentEvent( this, getRoot()));
//		    }
//		}
//	}

	/** 
	 * Removes all the listeners from this document.
	 */
	protected void removeAllListeners() {
		// Guaranteed to return a non-null array
		if(listeners != null) {
			Object[] list = listeners.getListenerList();
			
			for ( int i = list.length-2; i >= 0; i -= 2) {
				listeners.remove( (Class)list[i], (EventListener)list[i+1]);
			}
		}
	}
	
	public void cleanup() {
		if ( document != null) {
			document.cleanup();
		} else if ( lastDocument != null) {
			lastDocument.cleanup();
		}

		finalize();
	}
	
	protected void finalize() {
		removeAllListeners();
		
		listeners 	= null;

		encoding	= null;
		name		= null;
		file		= null;
		url			= null;

		document	= null;
		text		= null;

		grammar 	= null;

		exception			= null;
//		validationException	= null;

		elementNames	= null;
		attributeNames	= null;
		namespaces		= null;

		modified		= 0;
	}
	
	public boolean hasEncoding() { 
		return hasEncoding;
	}

	public boolean hasDeclaration() {
		if (text != null)
		{
			return text.indexOf("<?xml") != -1;
		}
		else
		{
			return false;
		}
		
	}

	/**
	 * Updates the XML declaration values (i.e version, encoding and standalone values)
	 */
	private void updateDeclaration()
	{
		try{	
			int start = text.indexOf("<?xml");
			if (start == -1 || text.trim().startsWith("<?xml-stylesheet"))
			{
				// no XML declaration so set the defaults
				setVersion("1.0");
				setEncoding("UTF-8");
				setStandalone(STANDALONE_NONE);
				return;
			}
		
			int end = text.indexOf("?>");
			
			// get the declaration, as need to search for encoding and standalone, cant use full text for this
			// in case "encoding" of "standalone" occurs in the document anyway
			String decl = text.substring(start,end);
			
			String versionValue = getAttributeValue(decl,"version");
			if (versionValue != null)
			{
				setVersion(versionValue);
			}
			else
			{
				throw new Exception("No XML version found");
			}
			
			String encodingValue = getAttributeValue(decl,"encoding");
			if (encodingValue != null)
			{
				hasEncoding = true;
				setEncoding(encodingValue);
			}
			else
			{
				hasEncoding = false;
				setEncoding("UTF-8");
			}
			
			String standaloneValue = getAttributeValue(decl,"standalone");
			if (standaloneValue != null) 
			{
				setStandalone(standaloneValue);
			}
			else
			{
				setStandalone(STANDALONE_NONE);
			}
		}
		catch (Exception e)
		{
			System.out.println("The following error occurred parsing the XML declaration: "+e.toString());
		}
		
	}
	
	/**
	 * Gets the value of an XML attribute
	 * 
	 * @param text The XML like text
	 * @param attr The attribute name
	 * 
	 * @return Returns the attribue value
	 */
	private String getAttributeValue(String text,String attr)
	{
		// get the attribute start position
		int attrStart = 0;
		if ((attrStart = text.indexOf(attr)) == -1)
		{
			return null;
		}
		
		int attrValueStart = attrStart+attr.length();;
		char next = text.charAt(attrValueStart);
		while ((next != '"') && (next != '\''))
		{
			attrValueStart++;
			next = text.charAt(attrValueStart);
		}
		
		// get the attribute end position
		int attrValueEnd = attrValueStart+1;
		next = text.charAt(attrValueEnd);
		while ((next != '"') && (next != '\''))
		{
			attrValueEnd++;
			next = text.charAt(attrValueEnd);
		}
		
		return text.substring(attrValueStart+1,attrValueEnd);
	}
	
	/**
	 * @return Returns the standalone value
	 */
	public String getStandalone() {
		return standalone;
	}
	/**
	 * @param standalone The standalone value to set.
	 */
	public void setStandalone(String standalone) {
		this.standalone = standalone;
	}
	/**
	 * @return Returns the version.
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version The version to set.
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	private class Counter {
		public int counter = 1;
		
	}
} 
