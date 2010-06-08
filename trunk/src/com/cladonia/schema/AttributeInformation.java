/*
 * $Id: AttributeInformation.java,v 1.2 2004/09/23 10:26:20 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import java.util.Vector;

/**
 * A cross-grammar container for attribute related information.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/09/23 10:26:20 $
 * @author Dogsbay
 */
public interface AttributeInformation {
	public String getName();
	public String getPrefix();
	public String getNamespace();
	public String getQualifiedName();
	public String getUniversalName();
	public Vector getValues();

	public void setPrefix( String prefix);
	public boolean isRequired();
} 
