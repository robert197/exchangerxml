/*
 * $Id: XMLError.java,v 1.2 2005/04/12 15:45:17 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml;

import java.io.IOException;
import java.net.URLDecoder;

import org.xml.sax.SAXParseException;

/**
 * Defines an XMLError.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/04/12 15:45:17 $
 * @author Dogsbay
 */
public class XMLError {
	public static final int WARNING = 0;
	public static final int ERROR = 1;
	public static final int FATAL = 2;

	private Exception exception = null;
	private int type = WARNING;
	private String message = null;
	private int line = -1;
	private int column = -1;
	private String systemId = null;

	public XMLError( IOException e) {
		this.exception = e;
		this.type = ERROR;
		this.message = e.getMessage();
	}

	public XMLError( SAXParseException e, int type) {
		this.exception = e;
		this.type = type;

		this.message = e.getMessage();
		this.line = e.getLineNumber();
		this.column = e.getColumnNumber();
		
		try {
			this.systemId = URLDecoder.decode( e.getSystemId(), "UTF-8");
		} catch (Exception e2) {
			this.systemId = null;
		}
	}
	
	public int getType() {
		return type;
	}
	
	public int getLineNumber() {
		return line;
	}
	
	public String getSystemId() {
		return systemId;
	}

	public int getColumnNumber() {
		return column;
	}

	public String getMessage() {
		return message;
	}

	public Exception getException() {
		return exception;
	}
	
	public String toString() {
	    return("Ln "+getLineNumber()+" Col "+getColumnNumber()+" - "+getMessage());
	}
} 
