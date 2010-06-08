/*
 * $Id: SignatureException.java,v 1.1 2004/04/06 08:31:14 knesbitt Exp $
 *
 * Copyright (C) 2002-2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.
 * Use is subject to license terms.
 */
package com.cladonia.security.signature;

import com.cladonia.xngreditor.ExchangerException;

/**
 * The custom Signature Exception
 *
 * @version	$Revision: 1.1 $, $Date: 2004/04/06 08:31:14 $
 * @author Dogs bay
 */
public class SignatureException extends ExchangerException{
	
	/**
     * Creates a new SignatureException instance.
     */
    public SignatureException() {
        super();
    }

    /**	
     * Creates a new SignatureException instance.
     * @param msg A detailed error message
     */
    public SignatureException(String msg) {
        super(msg);
    }

    /**
     * Creates a new SignatureException instance.
     * @param msg A detailed error message
     * @param info Additional error information
     */
    public SignatureException(String msg, String info) {
        super(msg,info);
    }

    /**
     * Creates a new SignatureException instance.
     * @param throwable The root cause of the exception. 
     */
    public SignatureException(Throwable throwable) {
        super(throwable);
    }
   

    /**
     * Creates a new SignatureException instance.
     * @param msg A detailed error message
     * @param throwable The root cause of the exception. 
     */
    public SignatureException(String msg, Throwable throwable) {
        super(msg,throwable);
    }
}
