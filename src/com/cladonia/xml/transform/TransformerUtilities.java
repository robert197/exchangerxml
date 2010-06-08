/**
 * $Id: TransformerUtilities.java,v 1.4 2004/10/18 16:08:08 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.transform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Vector;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.logger.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xngreditor.URLUtilities;

/**
 * Utilities for transforming XML documents.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/10/18 16:08:08 $
 * @author Dogsbay
 */
public class TransformerUtilities {

	public static SystemLogger logger = new SystemLogger();
	public static TransformationErrorListener listener = new TransformationErrorListener();
	/**
	 * Transforms the ExchangerDocument using the default xsl file 
	 *
	 * @param document the XML document.
	 * @param output the output stream which will contain the result.
	 */
	public static void transform( ExchangerDocument document, OutputStream output, boolean pis) throws TransformerException, SAXException, IOException { //, SAXException, IOException	{
		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setErrorListener( listener);
		Source input = createSource( document);
		Source stylesheet = null;
		
		if ( pis) {
			stylesheet = factory.getAssociatedStylesheet( input, null, null, null);
		} 
		
		if ( stylesheet == null){
			stylesheet = new StreamSource( factory.getClass().getResourceAsStream( "com/cladonia/xml/transform/resources/default.xsl"));
		}
		
		transform( input, stylesheet, null, new StreamResult( output));
	}

	private static void transform( Source document, Source stylesheet, Vector parameters, Result result) throws TransformerException {
	
		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setErrorListener( listener);
		
		if ( stylesheet == null) {
			stylesheet = factory.getAssociatedStylesheet( document, null, null, null);
		}
		
		if ( stylesheet == null) { // no associated stylesheet in the document, use internal stylesheet!
			stylesheet = new StreamSource( factory.getClass().getResourceAsStream( "com/cladonia/xml/transform/resources/default.xsl"));
		}
		
		Transformer transformer = factory.newTransformer( stylesheet);
		
		if ( parameters != null) {
			for ( int i = 0; i < parameters.size(); i++) {
				String[] parameter = (String[])parameters.elementAt(i);
				transformer.setParameter( parameter[0],	parameter[1]);
			}
		}
		
		transformer.transform( document, result);
	}
	
	public static String getPIStylesheetLocation( String inputSystemId) throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setErrorListener( listener);
		
		Source source = factory.getAssociatedStylesheet( new SAXSource( new InputSource(  URLUtilities.encodeURL( inputSystemId))), null, null, null);
		
		if ( source != null) {
			return source.getSystemId();
		} else {
			return null;
		}
	}

	private static SAXSource createSource( ExchangerDocument document) throws UnsupportedEncodingException, SAXException {
		ByteArrayInputStream stream = new ByteArrayInputStream( document.getText().getBytes( document.getJavaEncoding()));

		URL url = document.getURL();
		String systemId = null;

		if ( url != null) {
			systemId = url.toString();
		} 

		return createSource( stream, systemId);
	}

	private static SAXSource createSource( InputStream stream, String systemId) throws UnsupportedEncodingException, SAXException {
		InputSource input = new InputSource( stream);
		SAXSource source = new SAXSource( input);

		input.setSystemId( systemId);
		source.setSystemId( systemId);

		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setFeature( "http://xml.org/sax/features/validation", false);
		reader.setEntityResolver( XMLUtilities.getCatalogResolver());
		source.setXMLReader( reader);
		
		return source;
	}

	private static class TransformationErrorListener implements ErrorListener {
		public void warning( TransformerException e) {
			System.err.println( "WARNING: "+e.getMessageAndLocation());
		}

		public void error( TransformerException e) {
			System.err.println( "ERROR: "+e.getMessageAndLocation());
		}

		public void fatalError( TransformerException e) {
			System.err.println( "FATAL ERROR: "+e.getMessageAndLocation());
		}
	}

	private static class SystemLogger implements Logger{
	    /** Typecode for debugging messages. */
	    public static final int LEVEL_DEBUG = 0;

	    /** Typecode for informational messages. */
	    public static final int LEVEL_INFO = 1;

	    /** Typecode for warning messages. */
	    public static final int LEVEL_WARN = 2;

	    /** Typecode for error messages. */
	    public static final int LEVEL_ERROR = 3;

	    /** Typecode for fatal error messages. */
	    public static final int LEVEL_FATAL = 4;

	    /** Typecode for disabled log levels. */
	    public static final int LEVEL_DISABLED = 5;

	    private final int m_logLevel;

	    /**
	     * Creates a new ConsoleLogger with the priority set to DEBUG.
	     */
	    public SystemLogger() {
	        this( LEVEL_WARN);
	    }

	    /**
	     * Creates a new ConsoleLogger.
	     * @param logLevel log level typecode
	     */
	    public SystemLogger( final int logLevel ) {
	        m_logLevel = logLevel;
	    }

	    /**
	     * Logs a debugging message.
	     *
	     * @param message a <code>String</code> value
	     */
	    public void debug( final String message ) {
	        debug( message, null );
	    }

	    /**
	     * Logs a debugging message and an exception.
	     *
	     * @param message a <code>String</code> value
	     * @param throwable a <code>Throwable</code> value
	     */
	    public void debug( final String message, final Throwable throwable ) {
	        if( m_logLevel <= LEVEL_DEBUG ) {
	            System.err.print( "[DEBUG] " );
	            System.err.println( message );

	            if( null != throwable ) {
	                throwable.printStackTrace( System.out );
	            }
	        }
	    }

	    /**
	     * Returns <code>true</code> if debug-level logging is enabled, false otherwise.
	     *
	     * @return <code>true</code> if debug-level logging
	     */
	    public boolean isDebugEnabled() {
	        return m_logLevel <= LEVEL_DEBUG;
	    }

	    /**
	     * Logs an informational message.
	     *
	     * @param message a <code>String</code> value
	     */
	    public void info( final String message ) {
	        info( message, null );
	    }

	    /**
	     * Logs an informational message and an exception.
	     *
	     * @param message a <code>String</code> value
	     * @param throwable a <code>Throwable</code> value
	     */
	    public void info( final String message, final Throwable throwable ) {
	        if( m_logLevel <= LEVEL_INFO ) {
	            System.err.print( "[INFO] " );
	            System.err.println( message );

	            if( null != throwable ) {
	                throwable.printStackTrace( System.out );
	            }
	        }
	    }

	    /**
	     * Returns <code>true</code> if info-level logging is enabled, false otherwise.
	     *
	     * @return <code>true</code> if info-level logging is enabled
	     */
	    public boolean isInfoEnabled() {
	        return m_logLevel <= LEVEL_INFO;
	    }

	    /**
	     * Logs a warning message.
	     *
	     * @param message a <code>String</code> value
	     */
	    public void warn( final String message ) {
	        warn( message, null );
	    }

	    /**
	     * Logs a warning message and an exception.
	     *
	     * @param message a <code>String</code> value
	     * @param throwable a <code>Throwable</code> value
	     */
	    public void warn( final String message, final Throwable throwable ) {
	        if( m_logLevel <= LEVEL_WARN ) {
	            System.err.print( "[WARNING] " );
	            System.err.println( message );

	            if( null != throwable ) {
	                throwable.printStackTrace( System.out );
	            }
	        }
	    }

	    /**
	     * Returns <code>true</code> if warn-level logging is enabled, false otherwise.
	     *
	     * @return <code>true</code> if warn-level logging is enabled
	     */
	    public boolean isWarnEnabled() {
	        return m_logLevel <= LEVEL_WARN;
	    }

	    /**
	     * Logs an error message.
	     *
	     * @param message a <code>String</code> value
	     */
	    public void error( final String message ) {
	        error( message, null );
	    }

	    /**
	     * Logs an error message and an exception.
	     *
	     * @param message a <code>String</code> value
	     * @param throwable a <code>Throwable</code> value
	     */
	    public void error( final String message, final Throwable throwable ) {
	        if( m_logLevel <= LEVEL_ERROR ) {
	            System.err.print( "[ERROR] " );
	            System.err.println( message );

	            if( null != throwable ) {
	                throwable.printStackTrace( System.out );
	            }
	        }
	    }

	    /**
	     * Returns <code>true</code> if error-level logging is enabled, false otherwise.
	     *
	     * @return <code>true</code> if error-level logging is enabled
	     */
	    public boolean isErrorEnabled() {
	        return m_logLevel <= LEVEL_ERROR;
	    }

	    /**
	     * Logs a fatal error message.
	     *
	     * @param message a <code>String</code> value
	     */
	    public void fatalError( final String message ) {
	        fatalError( message, null );
	    }

	    /**
	     * Logs a fatal error message and an exception.
	     *
	     * @param message a <code>String</code> value
	     * @param throwable a <code>Throwable</code> value
	     */
	    public void fatalError( final String message, final Throwable throwable ) {
	        if( m_logLevel <= LEVEL_FATAL ) {
	            System.err.print( "[FATAL ERROR] " );
	            System.err.println( message );

	            if( null != throwable ) {
	                throwable.printStackTrace( System.out );
	            }
	        }
	    }

	    public boolean isFatalErrorEnabled() {
	        return m_logLevel <= LEVEL_FATAL;
	    }

	    public Logger getChildLogger( final String name ) {
	        return this;
	    }
	}
} 
