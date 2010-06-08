/*
 * $Id: WSDLException.java,v 1.2 2004/04/06 08:12:14 knesbitt Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.
 * Use is subject to license terms.
 */

package com.cladonia.xml.webservice.wsdl;

import com.cladonia.xngreditor.ExchangerException;

/**
 * The custom WSDL Exception
 *
 * @version	$Revision: 1.2 $, $Date: 2004/04/06 08:12:14 $
 * @author Dogs bay
 */

public class WSDLException extends ExchangerException{
	
	/**
     * Creates a new WSDLException instance.
     */
    public WSDLException() {
        super();
    }

    /**	
     * Creates a new WSDLException instance.
     * @param msg A detailed error message
     */
    public WSDLException(String msg) {
        super(msg);
    }

    /**
     * Creates a new WSDLException instance.
     * @param msg A detailed error message
     * @param info Additional error information
     */
    public WSDLException(String msg, String info) {
        super(msg,info);
    }

    /**
     * Creates a new WSDLException instance.
     * @param throwable The root cause of the exception. 
     */
    public WSDLException(Throwable throwable) {
        super(throwable);
    }
   

    /**
     * Creates a new WSDLException instance.
     * @param msg A detailed error message
     * @param throwable The root cause of the exception. 
     */
    public WSDLException(String msg, Throwable throwable) {
        super(msg,throwable);
    }
}
