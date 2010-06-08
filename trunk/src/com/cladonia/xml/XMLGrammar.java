/*
 * $Id: XMLGrammar.java,v 1.1 2004/03/25 18:41:32 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml;

/**
 * This interface can be implemented to provide grammar information to the document.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:41:32 $
 * @author Dogsbay
 */
public interface XMLGrammar {
	public static final int TYPE_DTD = 0;
	public static final int TYPE_XSD = 1;
	public static final int TYPE_RNG = 2;
	public static final int TYPE_RNC = 3;
	public static final int TYPE_NRL = 4;

 	public String getLocation();
 	public int getType();
 	public boolean useExternal();
} 
