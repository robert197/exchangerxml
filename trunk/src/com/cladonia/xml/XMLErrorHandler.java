/*
 * $Id: XMLErrorHandler.java,v 1.1 2004/03/25 18:41:32 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml;

import java.util.Vector;

import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * The XMLErrorHandler implemetation, can have a list of errors.
 * to specific Document events.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:41:32 $
 * @author Dogsbay
 */
public class XMLErrorHandler implements ErrorHandler, org.apache.xerces.xni.parser.XMLErrorHandler {
	private Vector errors = null;
	private XMLErrorReporter reporter = null;
	private int maximum = -1;
	
	public XMLErrorHandler() {
		this( null, -1);
	}
	
	public XMLErrorHandler( XMLErrorReporter reporter) {
		this( reporter, -1);
	}

	public XMLErrorHandler( XMLErrorReporter reporter, int maximum) {
		errors = new Vector();
		this.reporter = reporter;
		this.maximum = maximum;
	}

	public void warning( SAXParseException e) throws SAXParseException {
		System.out.println( "warning( "+e.getMessage()+")");
		XMLError error = new XMLError( e, XMLError.WARNING);
		errors.addElement( error);
		report( error);

		if ( errors.size() == maximum) {
			throw e;
		}
	}

	public void warning( String domain, String key, XMLParseException e) throws XNIException {
    	System.out.println( "warning( "+domain+", "+key+", "+e.getMessage()+")");
    	try {
    		warning( createSAXParseException( e));
    	} catch ( SAXParseException x) {
    		throw new XNIException( e);
    	}
	}

	public void error( SAXParseException e) throws SAXParseException {
		XMLError error = new XMLError( e, XMLError.ERROR);
		errors.addElement( error);
		report( error);
		
		if ( errors.size() == maximum) {
			throw e;
		}
	}

	public void error( String domain, String key, XMLParseException e) throws XNIException {
		try {
			error( createSAXParseException( e));
		} catch ( SAXParseException x) {
			throw new XNIException( e);
		}
	}

	public void fatalError( SAXParseException e) throws SAXParseException {
		XMLError error = new XMLError( e, XMLError.FATAL);
		errors.addElement( error);
		report( error);

		if ( errors.size() == maximum) {
			throw e;
		}
	}
	
	public void fatalError( String domain, String key, XMLParseException e) throws XNIException {
		try {
			fatalError( createSAXParseException( e));
		} catch ( SAXParseException x) {
			throw new XNIException( e);
		}
	}

	public boolean hasErrors() {
		return errors.size() > 0;
	}
	
	public void clear() {
		errors.removeAllElements();
	}
	
	public Vector getErrors() {
		return errors;
	}
	
	public void setReporter( XMLErrorReporter reporter) {
		this.reporter = reporter;
	}
	
	public XMLErrorReporter getReporter() {
		return reporter;
	}

	private void setMaximum( int maximum) {
		this.maximum = maximum;
	}

	private int getMaximum() {
		return maximum;
	}

	private void report( XMLError error) {
		if ( reporter != null) {
			System.err.println( "ERROR on line "+error.getLineNumber()+", column "+error.getColumnNumber()+"\n"+error.getMessage());
			reporter.report( error);
		} else {
			System.err.println( "ERROR on line "+error.getLineNumber()+", column "+error.getColumnNumber()+"\n"+error.getMessage());
		}
	}
	
	private static SAXParseException createSAXParseException( XMLParseException exception) {
	    return new SAXParseException(exception.getMessage(),
	                                 exception.getPublicId(),
	                                 exception.getExpandedSystemId(),
	                                 exception.getLineNumber(),
	                                 exception.getColumnNumber(),
	                                 exception.getException());
	}	 
} 
