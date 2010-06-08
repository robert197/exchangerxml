/*
 * $Id: XMLErrorReporter.java,v 1.1 2004/03/25 18:41:32 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml;

/**
 * Defines a Reporter for XMLErrors.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:41:32 $
 * @author Dogsbay
 */
public interface XMLErrorReporter {
	public void report( XMLError e);
} 
