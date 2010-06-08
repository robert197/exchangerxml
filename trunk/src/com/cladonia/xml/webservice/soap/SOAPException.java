/*
 * $Id: SOAPException.java,v 1.1 2004/04/06 08:14:03 knesbitt Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.
 * Use is subject to license terms.
 */

package com.cladonia.xml.webservice.soap;

import com.cladonia.xngreditor.ExchangerException;

/**
 * The custom SOAP Exception
 *
 * @version	$Revision: 1.1 $, $Date: 2004/04/06 08:14:03 $
 * @author Dogs bay
 */
public class SOAPException extends ExchangerException{
	/**
     * Creates a new SOAPException instance.
     */
    public SOAPException() {
        super();
    }

    /**	
     * Creates a new SOAPException instance.
     * @param msg A detailed error message
     */
    public SOAPException(String msg) {
        super(msg);
    }

    /**
     * Creates a new SOAPException instance.
     * @param msg A detailed error message
     * @param info Additional error information
     */
    public SOAPException(String msg, String info) {
        super(msg,info);
    }

    /**
     * Creates a new SOAPException instance.
     * @param throwable The root cause of the exception. 
     */
    public SOAPException(Throwable throwable) {
        super(throwable);
    }
   

    /**
     * Creates a new SOAPException instance.
     * @param msg A detailed error message
     * @param throwable The root cause of the exception. 
     */
    public SOAPException(String msg, Throwable throwable) {
        super(msg,throwable);
    }
}
