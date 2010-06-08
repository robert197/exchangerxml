/*
 * $Id: ServiceInfo.java,v 1.1 2004/03/30 15:33:19 knesbitt Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.
 * Use is subject to license terms.
 */
package com.cladonia.xml.webservice.wsdl;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Encapsulates all the properties of a WSDL service
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/30 15:33:19 $
 * @author Dogs bay
 */
public class ServiceInfo
{
	// the service name
	String name = "";

	// the list of operations that this service defines.
	List operations = new ArrayList();

	/**
	 * Constructs an instance of ServiceInfo
	 */
	public ServiceInfo()
	{
	}

	/**
	 * Sets the name of the service
	 *
	 * @param value The name of the service
	 */
	public void setName(String value)
	{
		name = value;
	}

	/**
	 * Gets the name of the service
	 *
	 * @return The name of the service is returned
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Add an operation info object to this service definition
	 *
	 * @param operation The operation to add to this service definition
	 */
	public void addOperation(OperationInfo operation)
	{
		operations.add(operation);
	}

	/**
	 * Returs the operations defined by this service
	 *
	 * @return an Iterator that can be used to iterate the operations defined by this service
	 */
	public Iterator getOperations()
	{
		return operations.iterator();
	}

	/**
	 * Override toString to return the name of the service
	 *
	 * @return The name of the service is returned
	 */
	public String toString()
	{
		return getName();
	}
}
