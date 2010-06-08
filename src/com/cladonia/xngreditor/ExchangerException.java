/*
 * $Id: ExchangerException.java,v 1.2 2004/04/14 16:54:06 edankert Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

/**
 * Implements a custom Exception class for the Editor.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/04/14 16:54:06 $
 * @author Dogs bay
 */
public class ExchangerException extends Exception {

    /**
     * A holder for the original exception
     */
    private Throwable originalException;

    /**
     * String containing additional error information.
     */
    private String info;

    /**
     * Creates a new ExchangerException instance.
     */
    public ExchangerException() {
        super();
    }

    /**	
     * Creates a new ExchangerException instance.
     * @param msg A detailed error message
     */
    public ExchangerException(String msg) {
        super(msg);
    }

    /**
     * Creates a new ExchangerException instance.
     * @param msg A detailed error message
     * @param info Additional error information
     */
    public ExchangerException(String msg, String info) {
        super(msg);
        this.info = info;
    }

    /**
     * Creates a new ExchangerException instance.
     * @param throwable The root cause of the exception. 
     */
    public ExchangerException(Throwable throwable) {
        super("Original  Message - " + throwable.getMessage());
        originalException = throwable;
    }

    /**
     * Creates a new ExchangerException instance.
     * @param msg A detailed error message
     * @param throwable The root cause of the exception. 
     */
    public ExchangerException(String msg, Throwable throwable) {
        super(msg);
        originalException = throwable;
    }

    /**
     * Get the underlying exception that caused this Exception.
     * @return The Throwable that caused this exception
     */
    public Throwable getOriginalException() {
        return originalException;
    }

    /**
     * Returns additional information provided by this exception (or null).
     * @return Additional error information.
     */
    public String getAdditionalInfo() {
        return info;
    }
    
    public Throwable getCause() { 
    	Throwable cause = originalException;
    	
    	while ( cause != null) {
    		Throwable exception = cause.getCause();

    		if ( exception != null) {
    			cause = exception;
    		} else {
    			return cause;
    		}
    	}
    	
    	return null;
    }

    /**
     * Prints a stack trace for this exception and its underlying exception
     * (if defined).
     */
    public void printStackTrace() {
        super.printStackTrace();
        if (originalException != null) originalException.printStackTrace();
    }

}
