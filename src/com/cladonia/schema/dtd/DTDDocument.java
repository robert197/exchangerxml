/* 
 * (C) Copyright 2002-2003, Andy Clark.  All rights reserved.
 *
 * This file is distributed under an Apache style license. Please
 * refer to the LICENSE file for specific details.
 */

package com.cladonia.schema.dtd;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.parsers.BasicParserConfiguration;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.dom4j.Namespace;

import com.cladonia.schema.AttributeInformation;
import com.cladonia.schema.ElementInformation;
import com.cladonia.schema.SchemaDocument;

/**
 * An XNI-based parser configuration that can be used to parse DTD
 * documents to generate an XML representation of the DTD. This
 * configuration can be used directly in order to parse DTD documents
 * or can be used in conjunction with any XNI based tools, such as 
 * the Xerces2 implementation.
 * <p>
 * For complete usage information, refer to the documentation.
 *
 * @author Andy Clark
 *
 * @version $Id: DTDDocument.java,v 1.3 2005/08/25 10:48:21 gmcgoldrick Exp $
 */
public class DTDDocument extends BasicParserConfiguration implements XMLParserConfiguration, XMLDTDHandler, XMLDTDContentModelHandler, SchemaDocument {

    /** Locale property identifier. */
    protected static final String LOCALE = Constants.XERCES_PROPERTY_PREFIX + "locale";
	
	protected Hashtable elements = new Hashtable();

    protected XMLDocumentHandler documentHandler;
    protected XMLErrorHandler errorHandler;
    protected XMLEntityResolver entityResolver;

    // DTD sources
    protected XMLDTDSource dtdSource;
    protected XMLDTDContentModelSource fDTDContentModelSource;

    // components
    protected SymbolTable fSymbolTable = new SymbolTable();
	protected XMLDTDScannerImpl scanner = new XMLDTDScannerImpl();

    protected XMLEntityManager fEntityManager = new XMLEntityManager();
    protected XMLErrorReporter fErrorReporter = new XMLErrorReporter();

    /** Attributes. */
    private final XMLAttributes fAttributes = new XMLAttributesImpl();
	
	private URL url = null;
        
    /** Default constructor. */
    public DTDDocument( URL url) throws IOException {

		this.url = url;

		// add default features
        String[] featureNames = {
            "http://xml.org/sax/features/validation",
            "http://xml.org/sax/features/namespaces",
            "http://apache.org/xml/features/validation/warn-on-duplicate-attdef",
            "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef",
            "http://apache.org/xml/features/warn-on-duplicate-entitydef",
        };

		addRecognizedFeatures(featureNames);
        
		for (int i = 0; i < featureNames.length; i++) {
            setFeature( featureNames[i], false);
        }

        // add default properties
        String[] propertyNames = {
            "http://apache.org/xml/properties/internal/symbol-table",
            "http://apache.org/xml/properties/internal/entity-manager",
            "http://apache.org/xml/properties/internal/error-reporter",
        };
        Object[] propertyValues = {
            fSymbolTable,
            fEntityManager,
            fErrorReporter,
        };
        
		addRecognizedProperties(propertyNames);
        
		for (int i = 0; i < propertyNames.length; i++) {
            Object propertyValue = propertyValues[i];
            if (propertyValue != null) {
                String propertyName = propertyNames[i];
                setProperty( propertyName, propertyValue);
            }
        }
		
        fErrorReporter.setDocumentLocator(fEntityManager.getEntityScanner());

        if ( fErrorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN) == null) {
            XMLMessageFormatter xmft = new XMLMessageFormatter();
            fErrorReporter.putMessageFormatter(XMLMessageFormatter.XML_DOMAIN, xmft);
            fErrorReporter.putMessageFormatter(XMLMessageFormatter.XMLNS_DOMAIN, xmft);
        }

        // set handlers
        scanner.setDTDHandler( this);
        scanner.setDTDContentModelHandler( this);
		
//		System.out.println( "Document setup!");
		
		parse( url);
    } // <init>()

	public Vector getElements() {
		Enumeration enumeration = elements.elements();
		Vector elems = new Vector();
		
		while ( enumeration.hasMoreElements()) {
			elems.addElement( enumeration.nextElement());
		}

		return elems;
	}

	public Vector getGlobalElements() {
		return getElements();
	}

	public URL getURL() {
		return url;
	}
	
	public int getType() {
		return TYPE_DTD;
	}

//
// XMLParserConfiguration methods
//
    /** Sets the error handler. */
    public void setErrorHandler( XMLErrorHandler handler) {
		fErrorReporter.setProperty( "http://apache.org/xml/properties/internal/error-handler", handler);
        errorHandler = handler;
    }

	public Vector getAnyElements() {
		return null;
	}

	/** Returns the error handler. */
    public XMLErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /** Sets the entity resolver. */
    public void setEntityResolver(XMLEntityResolver resolver) {
        entityResolver = resolver;
    }

    /** Returns the entity resolver. */
    public XMLEntityResolver getEntityResolver() {
        return entityResolver;
    }

    /** Sets the document handler. */
    public void setDocumentHandler(XMLDocumentHandler handler) {
        documentHandler = handler;
    }

    /** Returns the document handler. */
    public XMLDocumentHandler getDocumentHandler() {
        return documentHandler;
    }

    /** Sets the DTD handler. */
    public void setDTDHandler(XMLDTDHandler handler) {}

    /** Returns the DTD handler. */
    public XMLDTDHandler getDTDHandler() { return null; }

    /** Sets the DTD content model handler. */
    public void setDTDContentModelHandler(XMLDTDContentModelHandler handler) {}

    /** Returns the DTD content model handler. */
    public XMLDTDContentModelHandler getDTDContentModelHandler() { return null; }

    /** Sets the locale. */
    public void setLocale(Locale locale) {
        try {
            setProperty(LOCALE, locale);
        }
        catch (Exception e) {
            // ignore
        }
    }

    /** Returns the locale. */
    public Locale getLocale() {
        Locale locale = null;
        try {
            locale = (Locale)getProperty(LOCALE);
            fErrorReporter.setLocale(locale);
        }
        catch (Exception e) {
            // ignore
        }
        return locale;
    }

	public void updatePrefixes( Vector declarations) {
		Vector allElements = getElements();
		
		for ( int i = 0; i < allElements.size(); i++) {
			ElementInformation model = (ElementInformation)allElements.elementAt(i);
			Vector attributes = model.getAttributes();
//			Vector children = model.getChildElements();
			
//			if ( children != null) {
//				for ( int j = 0; j < children.size(); j++) {
//					ElementInformation child = (ElementInformation)children.elementAt(j);
//					
//					for ( int k = 0; k < declarations.size(); k++) {
//						Namespace ns = (Namespace)declarations.elementAt(k);
//
//						if ( ns.getURI().equals( child.getNamespace())) {// && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
//							child.setPrefix( ns.getPrefix());
//							break;
//						}
//					}
//				}
//			}
			
			if ( attributes != null) {
				for ( int j = 0; j < attributes.size(); j++) {
					AttributeInformation attribute = (AttributeInformation)attributes.elementAt(j);
					
					for ( int k = 0; k < declarations.size(); k++) {
						Namespace ns = (Namespace)declarations.elementAt(k);

						if ( ns.getURI().equals( attribute.getNamespace()) ) { // && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
							attribute.setPrefix( ns.getPrefix());
							break;
						}
					}
				}
			}
			
			for ( int j = 0; j < declarations.size(); j++) {
				Namespace ns = (Namespace)declarations.elementAt(j);

				if ( ns.getURI().equals( model.getNamespace()) ) { // && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
					model.setPrefix( ns.getPrefix());
					break;
				}
			}
		}
	}

	public void parse( URL url) throws XNIException, IOException {
//		System.out.println( "parse: "+url);
		parse( new XMLInputSource( null, url.toString(), null));
    }

    /** Parses the DTD file specified by the given input source. */
    public void parse( XMLInputSource source) throws XNIException, IOException {
//      System.out.println( "parse( "+source);

        scanner.reset(this);
        fEntityManager.reset(this);
        scanner.setInputSource(source);

        try {
            scanner.scanDTDExternalSubset(true);
        } catch (EOFException e) {
            // ignore
            // NOTE: This is to work around a problem in the Xerces
            //       DTD scanner implementation when used standalone. -Ac
        }
		
		processReferences();
		processNamespaces();

//	    System.out.println( "DTDDocument created!");
    }
	
	// Processes all element references 
	private void processReferences() {
//		System.out.println( "processReferences()");
		Enumeration enumeration = elements.elements();
		
		while ( enumeration.hasMoreElements()) {
			DTDElement element = (DTDElement)enumeration.nextElement();
			
			Vector references = element.getReferences();
			for ( int i = 0; i < references.size(); i++) {
				String reference = (String)references.elementAt(i);
				DTDElement child = (DTDElement)elements.get( reference);
				
				if ( child != null) {
					element.addChild( child);
				}
			}
		}
	}

	// Processes all element references 
	private void processNamespaces() {
//		System.out.println( "processNamespaces()");
		Enumeration enumeration = elements.elements();
		
		while ( enumeration.hasMoreElements()) {
			DTDElement element = (DTDElement)enumeration.nextElement();
			
			element.processNamespaces();
		}
	}

//
// XMLDTDHandler methods
//

    /** Sets the DTD source. */
    public void setDTDSource(XMLDTDSource source) {
        dtdSource = source;
    }

    /** Returns the DTD source. */
    public XMLDTDSource getDTDSource() {
        return dtdSource;
    }

    /** Start DTD. */
    public void startDTD( XMLLocator locator, Augmentations augs) throws XNIException {
        if (documentHandler != null) {
            String encoding = "UTF-8";
            NamespaceContext nscontext = new NamespaceSupport();
            try {
                // NOTE: Hack to allow the default filter to work with
                //       old and new versions of the XNI document handler
                //       interface. -Ac
                Class cls = documentHandler.getClass();
                Class[] types = {
                    XMLLocator.class, String.class,
                    NamespaceContext.class, Augmentations.class
                };
                Method method = cls.getMethod("startDocument", types);
                Object[] params = {
                    locator, encoding, 
                    nscontext, augs
                };
                method.invoke(documentHandler, params);
            }
            catch (XNIException e) {
                throw e;
            }
            catch (Exception e) {
                try {
                    // NOTE: Hack to allow the default filter to work with
                    //       old and new versions of the XNI document handler
                    //       interface. -Ac
                    Class cls = documentHandler.getClass();
                    Class[] types = {
                        XMLLocator.class, String.class, Augmentations.class
                    };
                    Method method = cls.getMethod("startDocument", types);
                    Object[] params = {
                        locator, encoding, augs
                    };
                    method.invoke(documentHandler, params);
                }
                catch (XNIException ex) {
                    throw ex;
                }
                catch (Exception ex) {
                    // NOTE: Should never reach here!
                    throw new XNIException(ex);
                }
            }
        }
    }

    /** Element declaration. */
    public void elementDecl(String ename, String model, Augmentations augs) throws XNIException {
		DTDElement element = (DTDElement)elements.get( ename);
		
		if ( element != null) {
			element.setReferences( model);
		} else {
			element = new DTDElement( ename, model);
			elements.put( ename, element);
		}
    }

    /** Attribute declaration. */
    public void attributeDecl(String ename, String aname, String atype, String[] enumeration, String dtype, XMLString dvalue, XMLString nondvalue, Augmentations augs) throws XNIException {
        DTDElement element = (DTDElement)elements.get( ename);
        
        if ( element == null) {
        	element = new DTDElement( ename);
        	elements.put( ename, element);
        }
		
		
		String defaultValue = null;
		
		if ( dvalue != null) {
			defaultValue = dvalue.toString();
		}
		
		DTDAttribute attribute = new DTDAttribute( aname, atype, enumeration, dtype, defaultValue);
		element.addAttribute( attribute);
    }

    public void startExternalSubset(Augmentations augs) throws XNIException {} 
    public void startExternalSubset(XMLResourceIdentifier id, Augmentations augs) throws XNIException {}
    public void endExternalSubset( Augmentations augs) throws XNIException {}
    public void endDTD( Augmentations augs) throws XNIException {}
    public void comment( XMLString text, Augmentations augs) throws XNIException {}
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {}
    public void startParameterEntity(String name, XMLResourceIdentifier id, String encoding, Augmentations augs) throws XNIException {}
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {}
    public void endParameterEntity(String name, Augmentations augs) throws XNIException {}
    public void startAttlist(String ename, Augmentations augs) throws XNIException {}
    public void endAttlist(Augmentations augs) throws XNIException {}
    public void internalEntityDecl(String name, XMLString value, XMLString nonvalue, Augmentations augs) throws XNIException {}
    public void externalEntityDecl(String name, XMLResourceIdentifier id, Augmentations augs) throws XNIException {}
    public void unparsedEntityDecl(String name, XMLResourceIdentifier id, String notation, Augmentations augs) throws XNIException {}
    public void notationDecl(String name, XMLResourceIdentifier id, Augmentations augs) throws XNIException {}
    public void startConditional(short type, Augmentations augs) throws XNIException {}
    public void ignoredCharacters(XMLString text, Augmentations augs) throws XNIException {}
    public void endConditional(Augmentations augs) throws XNIException {}

//
// XMLDTDContentModelHandler methods
//

    /** Sets the DTD content model source. */
    public void setDTDContentModelSource(XMLDTDContentModelSource source) {
        fDTDContentModelSource = source;
    }

    /** Returns the DTD content model source. */
    public XMLDTDContentModelSource getDTDContentModelSource() {
        return fDTDContentModelSource;
    }

    public void startContentModel(String ename, Augmentations augs) throws XNIException {}
    public void endContentModel(Augmentations augs) throws XNIException {}
    public void any(Augmentations augs) throws XNIException {}
    public void empty(Augmentations augs) throws XNIException {}
    public void startGroup(Augmentations augs) throws XNIException {}
    public void pcdata(Augmentations augs) throws XNIException {}
    public void element(String name, Augmentations augs) throws XNIException {}
    public void separator(short type, Augmentations augs) throws XNIException {}
    public void occurrence(short type, Augmentations augs) throws XNIException {}
    public void endGroup(Augmentations augs) throws XNIException {}
	
	private String print( Augmentations augs) {
		StringBuffer buffer = new StringBuffer();

		if ( augs != null) {
			Enumeration keys = augs.keys();
			
			while ( keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
				String value = (String)augs.getItem( key);
				
				buffer.append( "[");
				buffer.append( key);
				buffer.append( ":");
				buffer.append( value);
				buffer.append( "]");
			}
		}
		
		return buffer.toString();
	}
} 
