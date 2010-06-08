/*
 * $Id: OperationInfo.java,v 1.1 2004/03/30 15:32:54 knesbitt Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.
 * Use is subject to license terms.
 */
package com.cladonia.xml.webservice.wsdl;

/**
 * Encapsulates all the properties of a WSDL operation
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/30 15:32:54 $
 * @author Dogs bay
 */
public class OperationInfo {

	// the SOAP operation type
	private String operationType = "";

	// the URL where the target object is located.
	private String targetURL = "";

	// the namespace URI used for this SOAP operation.
	private String namespaceURI = "";

	// the URI of the target object to invoke for this SOAP operation.
	private String targetObjectURI = "";

	// the name used to when making an invocation.
	private String targetMethodName = "";

	// the input message.
	private String inputMessageText = "";

	// the output message.
	private String outputMessageText = "";

	// the name of input message.
	private String inputMessageName = "";

	// the name of output message.
	private String outputMessageName = "";

	// the action URI value to use when making a invocation.
	private String soapActionURI = "";

	// the encoding type "document" vs. "rpc"
	private String style = "document";

	/**
	 * Constructs an OperationInfo
	 */
	public OperationInfo()
	{
		super();
	}

	/**
	 * Constructs an OperationInfo for a particular style
	 *
	 * @param style Pass "document" or "rpc"
	 */
	public OperationInfo(String style)
	{
		super();

		setStyle(style);
	}


	/**
	 * Sets the Target URL used to make a SOAP invocation for this operation
	 *
	 * @param value The target URL
	 */
	public void setTargetURL(String value)
	{
		targetURL = value;
	}

	/**
	 * Gets the Target URL used to make a SOAP invocation for this operation
	 *
	 * @return The target URL is returned
	 */
	public String getTargetURL()
	{
		return targetURL;
	}

	/**
	 * Sets the namespace URI used for this operation
	 *
	 * @param value The namespace URI to use
	 */
	public void setNamespaceURI(String value)
	{
		namespaceURI = value;
	}

	/**
	 * Gets the namespace URI
	 *
	 * @return The namespace URI
	 */
	public String getNamespaceURI()
	{
		return namespaceURI;
	}

	/**
	 * Sets the Target Object's URI used to make an invocation
	 *
	 * @param value The URI of the target object
	 */
	public void setTargetObjectURI(String value)
	{
		targetObjectURI = value;
	}

	/**
	 * Gets the Target Object's URI
	 *
	 * @return The URI of the target object
	 */
	public String getTargetObjectURI()
	{
		return targetObjectURI;
	}

	/**
	 * Sets the name of the target method to call
	 *
	 * @param value The name of the method to call
	 */
	public void setTargetMethodName(String value)
	{
		targetMethodName = value;
	}

	/**
	 * Gets the name of the target method to call
	 *
	 * @return The name of the method to call
	 */
	public String getTargetMethodName()
	{
		return targetMethodName;
	}

	/**
	 * Sets the value of the target's input SOAP message
	 *
	 * @param value The name of input message
	 */
	public void setInputMessageName(String value)
	{
		inputMessageName = value;
	}

	/**
	 * Gets the value of the target's input SOAP message
	 *
	 * @return  The name of the input message is returned
	 */
	public String getInputMessageName()
	{
		return inputMessageName;
	}

	/**
	 * Sets the value of the target's output message name
	 *
	 * @param value The name of the output message
	 */
	public void setOutputMessageName(String value)
	{
		outputMessageName = value;
	}

	/**
	 * Gets the value of the target method's output message name
	 *
	 * @return The name of the output message is returned
	 */
	public String getOutputMessageName()
	{
		return outputMessageName;
	}

	/**
	 * Sets the value of the target's input message
	 *
	 * @param value The SOAP input message
	 */
	public void setInputMessageText(String value)
	{
		inputMessageText = value;
	}

	/**
	 * Gets the value of the target's input message
	 *
	 * @return The input message is returned
	 */
	public String getInputMessageText()
	{
		return inputMessageText;
	}

	/**
	 * Sets the value of the target method's Output message
	 *
	 * @param value The output message
	 */
	public void setOutputMessageText(String value)
	{
		outputMessageText = value;
	}

	/**
	 * Gets the value of the target method's Output message
	 *
	 * @return The Output message is returned
	 */
	public String getOutputMessageText()
	{
		return outputMessageText;
	}

	/**
	 * Sets the value for the SOAP Action URI used to make a SOAP invocation
	 *
	 * @param value The SOAP Action URI value for the SOAP invocation
	 */
	public void setSoapActionURI(String value)
	{
		soapActionURI = value;
	}

	/**
	 * Gets the value for the SOAP Action URI used to make a SOAP invocation
	 *
	 * @return The SOAP Action URI value for the SOAP invocation is returned.
	 */
	public String getSoapActionURI()
	{
		return soapActionURI;
	}

	/**
	 * Sets the encoding document vs. rpc
	 *
	 * @return value A string value "document" or "rpc" should be used
	 */
	public void setStyle(String value)
	{
		style = value;
	}

	/**
	 * Returns the style "document" or "rpc"
	 *
	 * @return The style type is returned
	 */
	public String getStyle()
	{
		return style;
	}

	/**
	 * Override toString to return a name for the operation
	 *
	 * @return The name of the operation is returned
	 */
	public String toString()
	{
		return getTargetMethodName();
	}


}
