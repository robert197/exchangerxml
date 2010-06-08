/*
 * $Id: ElementInformation.java,v 1.2 2004/09/23 10:36:17 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import java.util.Vector;

/**
 * A cross-grammar container for element related information.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/09/23 10:36:17 $
 * @author Dogsbay
 */
public interface ElementInformation {
	public Vector getChildElements();
	public Vector getAttributes();
	public String getName();
	public String getParentName();
	public String getQualifiedName();
	public String getNamespace();
	public String getType();
	public String getPrefix();
	public String getAnnotations();
	public boolean isEmpty();
	public void setPrefix( String prefix);
}