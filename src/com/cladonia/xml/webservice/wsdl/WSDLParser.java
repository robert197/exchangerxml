/*
 * $Id: WSDLParser.java,v 1.5 2004/10/18 10:34:38 knesbitt Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.
 * Use is subject to license terms.
 */
package com.cladonia.xml.webservice.wsdl;


import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Enumeration;
import java.io.File;
import java.net.URL;

import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.Port;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOutput;
import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap.SOAPBody;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.exolab.castor.xml.schema.XMLType;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.Particle;
import org.exolab.castor.xml.schema.Structure;

import com.cladonia.xml.webservice.soap.SOAPClient;



/**
 * Parser for WSDL files. Uses Castor to work out schema types and create a sample SOAP
 * message.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/10/18 10:34:38 $
 * @author Dogs bay
 */
public class WSDLParser {

	// WSDL4J Factory instance
	private WSDLFactory wsdlFactory = null;

	// castor schema types
	private Schema wsdlTypes = null;

	// schema target namespace
	private String schemaTargetNamespace = null;

	// dom to hold each soap message as it gets built
	private Document document = null;

	// holds the SOAP Body element for each message
	private Element body = null;


	public WSDLParser() throws WSDLException
	{
		try{
			wsdlFactory = WSDLFactory.newInstance();
		}
		catch (javax.wsdl.WSDLException e)
		{
			throw new WSDLException(e.toString());
		}
	}

   /**
    * Builds a List of ServiceInfo components for each Service defined in a WSDL Document
    *
    * @param wsdlBaseURI A base URI that points to a WSDL file
    * @param dcoument the w3c dom document.
    *
    * @return A List of ServiceInfo objects populated for each service defined
    * in the WSDL file.
    */
	public List getServicesInfo( String wsdlBaseURI, Document document) throws WSDLException
	{
		try{
		// the list of ServiceInfo that will be returned
	    List serviceList = Collections.synchronizedList(new ArrayList());

	    // create the WSDL Reader object
	    WSDLReader reader = wsdlFactory.newWSDLReader();

	    // read the WSDL and get the top-level Definition object
        Definition def = reader.readWSDL( wsdlBaseURI, document);

        // create a castor schema from the types element defined in WSDL
        // this method will return null if there are types defined in the WSDL
        wsdlTypes = createSchemaFromTypes(def);

        // get the services defined in the document
        Map services = def.getServices();

        if(services != null)
        {
           // create a ServiceInfo for each service defined
           Iterator svcIter = services.values().iterator();

           for(int i = 0; svcIter.hasNext(); i++)
           {
              ServiceInfo serviceInfo = new ServiceInfo();

              // populate the new component from the WSDL Definition read
              populateInfo(serviceInfo, (Service)svcIter.next());

              // add the new component to the List to be returned
              serviceList.add(serviceInfo);
           }
        }

        // return the List of services we created
        return serviceList;
        
		}
		catch (WSDLException e)
		{
			// should really log this here
			final String errMsg = "The following error occurred obtaining the service "+
			"information from the WSDL: "+e.getMessage();
			throw e;
		}
		catch (Exception e)
		{
			final String errMsg = "The following error occurred obtaining the service "+
			"information from the WSDL: "+e.getMessage();
			throw new WSDLException(errMsg,e);
		}
	}

   /**
    * Builds a List of ServiceInfo components for each Service defined in a WSDL Document
    *
    * @param wsdlURI A URI that points to a WSDL file
    *
    * @return A List of ServiceInfo objects populated for each service defined
    * in the WSDL file.
    */
	public List getServicesInfo(String wsdlURI) 
	throws WSDLException
{
	
	try{
		
	
	// the list of ServiceInfo that will be returned
    List serviceList = Collections.synchronizedList(new ArrayList());

    // create the WSDL Reader object
    WSDLReader reader = wsdlFactory.newWSDLReader();

    // read the WSDL and get the top-level Definition object
    Definition def = reader.readWSDL(null, wsdlURI);

    // create a castor schema from the types element defined in WSDL
    // this method will return null if there are types defined in the WSDL
    wsdlTypes = createSchemaFromTypes(def);

    // get the services defined in the document
    Map services = def.getServices();

    if(services != null)
    {
       // create a ServiceInfo for each service defined
       Iterator svcIter = services.values().iterator();

       for(int i = 0; svcIter.hasNext(); i++)
       {
          ServiceInfo serviceInfo = new ServiceInfo();

          // populate the new component from the WSDL Definition read
          populateInfo(serviceInfo, (Service)svcIter.next());

          // add the new component to the List to be returned
          serviceList.add(serviceInfo);
       }
    }

    // return the List of services we created
    return serviceList;
    
	}
	catch (WSDLException e)
	{
		// should really log this here
		final String errMsg = "The following error occurred obtaining the service "+
		"information from the WSDL: "+e.getMessage();
		throw e;
	}
	catch (Exception e)
	{
		final String errMsg = "The following error occurred obtaining the service "+
		"information from the WSDL: "+e.getMessage();
		throw new WSDLException(errMsg,e);
	}
}

	/**
	* Populates a ServiceInfo instance from the specified Service definiition
	*
	* @param component The ServiceInfo component to populate
	* @param service The Service to populate from
	*
	* @return The populated ServiceInfo is returned representing the Service parameter
	*/
	private ServiceInfo populateInfo(ServiceInfo component, Service service) 
		throws WSDLException
	{
		try{
		
		// get the qualified service name information
		QName qName = service.getQName();

		// get the service's namespace URI
		String namespace = qName.getNamespaceURI();

		// use the local part of the qualified name for the component's name
		String name = qName.getLocalPart();

		// set the name
		component.setName(name);

		// get the defined ports for this service
		Map ports = service.getPorts();

		// use the Ports to create OperationInfos for all request/response messages defined
		Iterator portIter = ports.values().iterator();

		while(portIter.hasNext())
		{
			// get the next defined port
			Port port = (Port)portIter.next();

			// get the Port's Binding
			Binding binding = port.getBinding();

			// now we will create operations from the Binding information
			List operations = buildOperations(binding,namespace);

			// process objects built from the binding information
			Iterator operIter = operations.iterator();

			while(operIter.hasNext())
			{
				OperationInfo operation = (OperationInfo)operIter.next();

				// find the SOAP target URL
				ExtensibilityElement addrElem =
					findExtensibilityElement(port.getExtensibilityElements(), "address");

				if(addrElem != null && addrElem instanceof SOAPAddress)
				{
					// set the SOAP target URL
					SOAPAddress soapAddr = (SOAPAddress)addrElem;
					operation.setTargetURL(soapAddr.getLocationURI());
				}

				// add the operation info to the component
				component.addOperation(operation);
			}
		}

		return component;
		
		}
		catch (WSDLException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			// should log this here
			throw new WSDLException(e);
		}
	}


  /**
    * Creates Info objects for each Binding Operation defined in a Port Binding
    *
    * @param binding The Binding that defines Binding Operations used to build info objects from
	* @param namespace The namespace obtained from the service part
    *
    * @return A List of built and populated OperationInfos is returned for each Binding Operation
    */
	private List buildOperations(Binding binding,String namespace) throws WSDLException
	{
		try{
		
		// create the array of info objects to be returned
		List operationInfos = new ArrayList();

		// get the list of Binding Operations from the passed binding
		List operations = binding.getBindingOperations();

		if(operations != null && !operations.isEmpty())
		{
			// determine encoding (rpc or document)
			ExtensibilityElement soapBindingElem =
				findExtensibilityElement(binding.getExtensibilityElements(), "binding");

			// set "document" as the default
			String style = "document";

			if(soapBindingElem != null && soapBindingElem instanceof SOAPBinding)
			{
				SOAPBinding soapBinding = (SOAPBinding)soapBindingElem;
				style = soapBinding.getStyle();
			}

			// for each binding operation, create a new OperationInfo
			Iterator opIter = operations.iterator();
			int i = 0;

			while(opIter.hasNext())
			{

				// for each operation we need a new clean dom
				createEmptySoapMessage();

				BindingOperation oper = (BindingOperation)opIter.next();

				// only required to support soap:operation bindings
				ExtensibilityElement operElem =
					findExtensibilityElement(oper.getExtensibilityElements(), "operation");

				if(operElem != null && operElem instanceof SOAPOperation)
				{
					// create a new operation info
					OperationInfo operationInfo = new OperationInfo(style);

					// style maybe overridden in operation
					String operStyle = ((SOAPOperation)operElem).getStyle();
					if ((operStyle != null) && (!operStyle.equals("")))
					{
						operationInfo.setStyle(operStyle);
					}

					// set the namespace URI for the operation.
					operationInfo.setNamespaceURI(namespace);

					// populate it from the Binding Operation
					buildOperation(operationInfo, oper);

					// add to the return list
					operationInfos.add(operationInfo);
				}
			}
		}

		return operationInfos;
		
		}
		catch (WSDLException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			// should log this here
			throw new WSDLException(e);
		}
	}

	/**
	 * Populates an OperationInfo from the specified Binding Operation
	 *
	 * @param operationInfo The component to populate
	 * @param bindingOper A Binding Operation to define the OperationInfo from
	 *
	 * @return The populated OperationInfo object is returned.
	 */
	private OperationInfo buildOperation(OperationInfo operationInfo,
			BindingOperation bindingOper) throws WSDLException
	{
		try{
		
		// get the operation
		Operation oper = bindingOper.getOperation();

		// set the name using the operation name
		operationInfo.setTargetMethodName(oper.getName());

		// set the action URI
		ExtensibilityElement operElem =
			findExtensibilityElement(bindingOper.getExtensibilityElements(), "operation");

		if(operElem != null && operElem instanceof SOAPOperation)
		{
			SOAPOperation soapOperation = (SOAPOperation)operElem;
			operationInfo.setSoapActionURI(soapOperation.getSoapActionURI());
		}

		// get the Binding Input
		BindingInput bindingInput = bindingOper.getBindingInput();

		// get the Binding Output
		BindingOutput bindingOutput = bindingOper.getBindingOutput();

		// TO DO Build the SOAP Header (not really needed for SOAP testing)

		// get the SOAP Body part (of the operation inside the binding)
		ExtensibilityElement bodyElem =
			findExtensibilityElement(bindingInput.getExtensibilityElements(), "body");

		if(bodyElem != null && bodyElem instanceof SOAPBody)
		{
			SOAPBody soapBody = (SOAPBody)bodyElem;

			// the SOAP Body contains the target object's namespace URI (may or may not be present)
			operationInfo.setTargetObjectURI(soapBody.getNamespaceURI());
		}


		// get the Operation's Input definition
		Input inDef = oper.getInput();

		if(inDef != null)
		{
			// build input parameters
			Message inMsg = inDef.getMessage();

			if(inMsg != null)
			{
				// set the name of the operation's input message (good to know for debugging)
				operationInfo.setInputMessageName(inMsg.getQName().getLocalPart());

				// set the body of the operation's input message
				operationInfo.setInputMessageText(buildMessageText(operationInfo, inMsg));
			}
		}

		// finished, return the populated object
		return operationInfo;
		
		}
		catch (WSDLException e)
		{
			// should log\trace this here
			throw e;
		}
		catch (Exception e)
		{
			// should log\trace this here
			throw new WSDLException(e);
		}
	}


	/**
	 * Returns the desired ExtensibilityElement if found in the List
	 *
	 * @param extensibilityElements The list of extensibility elements to search
	 * @param elementType The element type to find
	 *
	 * @return  Returns the first matching element of type found in the list
	 */
	private static ExtensibilityElement findExtensibilityElement(List extensibilityElements, String elementType)
	{
		if(extensibilityElements != null)
		{
			Iterator iter = extensibilityElements.iterator();

			while(iter.hasNext())
			{
				ExtensibilityElement element = (ExtensibilityElement)iter.next();

				if(element.getElementType().getLocalPart().equalsIgnoreCase(elementType))
				{
					// found it
					return element;
				}
			}
		}

		return null;
	}

	/**
	 * Builds the SOAP Body content given a SOAP Message definition (from WSDL)
	 *
	 * @param operationInfo The component to build message text for
	 * @param msg The SOAP Message definition that has parts to defined parameters for
	 *
     * @return The SOAP Envelope as a String
	 */
	private String buildMessageText(OperationInfo operationInfo, Message msg) 
		throws WSDLException
	{
		try{
		
	    // the root element to add all the message content
	    Element rootElem = null;
	    String operationStyle = operationInfo.getStyle();

	    if(operationStyle.equalsIgnoreCase("rpc"))
	    {
	    	// if "rpc" style then add wrapper element with the name of the operation
	    	if ((operationInfo.getTargetObjectURI() != null) &&
	    			(!operationInfo.getTargetObjectURI().equals("")))
			{
	    		// create the element with the object namespace
	    		rootElem =
	    			document.createElementNS(operationInfo.getTargetObjectURI(),"xngr:"+
	    			operationInfo.getTargetMethodName());
				rootElem.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:xngr",
						operationInfo.getTargetObjectURI());
			}
	    	else
	    	{
	    		// create the element with no namespace
	    		rootElem = document.createElementNS(null,operationInfo.getTargetMethodName());
	    	}
	    }
	    else
	    {
	    	// else for "document" style set the root to be the SOAP Body
	    	rootElem = body;
	    }

		// get the message parts
		List msgParts = msg.getOrderedParts(null);

		// process each part
		Iterator iter = msgParts.iterator();

		while(iter.hasNext())
		{
			// get each part
			Part part = (Part)iter.next();

			// add content for each message part
			String partName = part.getName();

			if(partName != null)
			{
				// is it an element or a type ?
				if (part.getElementName() != null)
				{
					// determine if the element is complex or simple
					XMLType xmlType = getXMLType(part);

					if(xmlType != null && xmlType.isComplexType())
					{
						// build the element that will be added to the message
						Element partElem =
							document.createElementNS(null,part.getElementName().getLocalPart());

						// build the complex message structure
						buildComplexPart((ComplexType)xmlType, partElem);

						// add this message part
						rootElem.appendChild(partElem);
					}
					else if(xmlType != null && xmlType.isSimpleType())
					{
						// build the simple element that will be added to the message
						Element partElem = document.createElementNS(null,partName);

						// add some defaultContent
						String defaultContent = xmlType.getName();
						if (defaultContent != null)
						{
							partElem.appendChild(document.createTextNode(defaultContent));
						}

						// add this message part
						rootElem.appendChild(partElem);

					}
				}
				else
				{
					// of type "type"
					XMLType xmlType = getXMLType(part);

					// is it comlex or simple type
					if(xmlType != null && xmlType.isComplexType())
					{
						if(operationStyle.equalsIgnoreCase("rpc"))
						{
							// create an element with the part name (only required for RPC)
							Element partElem = document.createElementNS(null,partName);

							// build the complex message structure
							buildComplexPart((ComplexType)xmlType, partElem);

							// add this message part
							rootElem.appendChild(partElem);
						}
						else
						{
							// build the complex message structure
							buildComplexPart((ComplexType)xmlType, rootElem);
						}
					}
					else if(xmlType != null && xmlType.isSimpleType())
					{
						// build the simple element that will be added to the message
						Element partElem = document.createElementNS(null,partName);

						// add some default content
						String defaultContent = xmlType.getName();
						if (defaultContent != null)
						{
							partElem.appendChild(document.createTextNode(defaultContent));
						}

						// add this message part
						rootElem.appendChild(partElem);
					}
				}
			}
		}

		if(operationStyle.equalsIgnoreCase("rpc"))
	    {
			// append the content to the SOAP Body element
	    	body.appendChild(rootElem);
	    }

		// add the schema targetnamespace if "document" style to the SOAP body
		if(operationStyle.equalsIgnoreCase("document"))
		{
			if (schemaTargetNamespace != null)
			{
				// add the schema targetnameapace to the soap body
				body.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns",
						schemaTargetNamespace);
			}
			else if ((operationInfo.getNamespaceURI() != null) &&
					(!operationInfo.getNamespaceURI().equals("")))
			{
				// if the schema targetnamespace isn't present then add the service namespace
				body.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns",
						operationInfo.getNamespaceURI());
			}
			else
			{
				// no namespaces to add
			}
		}

		// return the serialised dom
		return XMLSupport.prettySerialise(document);
		
		}
		catch (WSDLException e)
		{
			// should log\trace this here
			throw e;
		}
		catch (Exception e)
		{
			// should log\trace this here
			throw new WSDLException(e);
		}
	}

	/**
	 * Gets an XML Type from a SOAP Message Part read from WSDL
	 *
	 * @param part The SOAP Message part
	 *
	 * @return The corresponding XML Type is returned.
	 */
	protected XMLType getXMLType(Part part)
	{
		if(wsdlTypes == null)
		{
			// no defined types, Nothing to do
			return null;
		}

		// find the XML type
		XMLType xmlType = null;

		// first see if there is a defined element
		if(part.getElementName() != null)
		{
			// get the element name
			String elemName = part.getElementName().getLocalPart();

			// find the element declaration
			ElementDecl elemDecl = wsdlTypes.getElementDecl(elemName);

			if(elemDecl != null)
			{
				// from the element declaration get the XML type
				xmlType = elemDecl.getType();
			}
		}
		else if (part.getTypeName() != null)
		{
			// get the type name
			String typeName = part.getTypeName().getLocalPart();

			// get the XML type
			xmlType = wsdlTypes.getType(typeName);
		}

		return xmlType;
	}

	/**
	 * Populate an element using the complex XML type passed in
	 *
	 * @param complexType The complex XML type to build the element for
	 * @param partElem The element to build content for
	 */
	protected void buildComplexPart(ComplexType complexType, Element partElem)
	{
		
		XMLType baseType = complexType.getBaseType();
		if (baseType != null && baseType.isComplexType())
		{
			buildComplexPart((ComplexType)baseType,partElem);
		}
		
		// find the group
		Enumeration particleEnum = complexType.enumerate();
		Group group = null;

		while(particleEnum.hasMoreElements())
		{
			Particle particle = (Particle)particleEnum.nextElement();

			if (particle instanceof Group)
			{
				group = (Group)particle;
				break;
			}
		}

		if (group != null)
		{
			Enumeration groupEnum = group.enumerate();

			while (groupEnum.hasMoreElements())
			{
				Structure item = (Structure)groupEnum.nextElement();

				if (item.getStructureType() == Structure.ELEMENT)
				{
					ElementDecl elementDecl = (ElementDecl)item;

					// build the element that will be added to the message
					Element childElem = document.createElementNS(null,elementDecl.getName());

					XMLType xmlType = elementDecl.getType();

					if(xmlType != null && xmlType.isComplexType())
					{
						// recurse
						buildComplexPart((ComplexType)xmlType, childElem);
					}
					else if(xmlType != null && xmlType.isSimpleType())
					{
						// add some default content as just a place holder
						String defaultContent = xmlType.getName();
						if (defaultContent != null)
						{
							childElem.appendChild(document.createTextNode(defaultContent));
						}

					}

					partElem.appendChild(childElem);

				}
			}
		}
	}

	/**
	 * Creates a castor schema based on the types defined by a WSDL document
	 *
	 * @param wsdlDefinition The WSDL4J instance of a WSDL definition.
	 *
	 * @return  A castor schema is returned if the WSDL definition contains
	 *          a types element.
	 */
	private Schema createSchemaFromTypes(Definition wsdlDefinition)
	{
		// get the schema element from the WSDL definition
		Element schemaElement = null;

		if(wsdlDefinition.getTypes() != null)
		{
			ExtensibilityElement schemaExtElem =
				findExtensibilityElement(wsdlDefinition.getTypes().getExtensibilityElements(),
				"schema");

			if(schemaExtElem != null && schemaExtElem instanceof UnknownExtensibilityElement)
			{
				schemaElement = ((UnknownExtensibilityElement)schemaExtElem).getElement();
			}
		}

		if(schemaElement == null)
		{
			// no schema to read
			return null;
		}

		Map namespaces = wsdlDefinition.getNamespaces();

		if(namespaces != null && !namespaces.isEmpty())
		{
			Iterator nsIter = namespaces.keySet().iterator();

			while(nsIter.hasNext())
			{
				String nsPrefix = (String)nsIter.next();
				String nsURI = (String)namespaces.get(nsPrefix);

				if(nsPrefix != null && nsPrefix.length() > 0)
				{
					// add the namespaces from the definition element to teh schema element
					schemaElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:"+
					nsPrefix, nsURI);
				}
			}
		}

		// convert it into a Castor schema instance
		Schema schema = null;

		try
		{
			schema = XMLSupport.convertElementToSchema(schemaElement);
			schemaTargetNamespace = schema.getTargetNamespace();
		}

		catch(Exception e)
		{
			System.out.println("The following error occurred obtaining the schema from WSDL: " +
					e.getMessage());
		}

		return schema;
	}

	/**
	 * Creates an empty SOAP message.
	 */
	private void createEmptySoapMessage() throws Exception
	{
        // create the dom
		javax.xml.parsers.DocumentBuilderFactory dbf =
	         javax.xml.parsers.DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
	    document = db.newDocument();

	    // create the SOAP Envelope element
	    Element envelope =
	    	document.createElementNS("http://schemas.xmlsoap.org/soap/envelope/",
				"SOAP-ENV:Envelope");

	    // add the SOAP Namespace (1.1 version)
	    envelope.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:SOAP-ENV",
	    		"http://schemas.xmlsoap.org/soap/envelope/");

	    // add the soap encoding namespace
	    envelope.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:SOAP-ENC",
				"http://schemas.xmlsoap.org/soap/encoding/");

	    // add the schema instance namespace
	    envelope.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi",
	    		"http://www.w3.org/2001/XMLSchema-instance");

	    // add the schema namespace
	    envelope.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd",
	    		"http://www.w3.org/2001/XMLSchema");

	    // TO DO add SOAP header (security headers etc)

	    // create the SOAP Body (store in a memeber variale so we can easly access later)
	    body =
	    	document.createElementNS("http://schemas.xmlsoap.org/soap/envelope/","SOAP-ENV:Body");

	    // add the body to the envelope
	    envelope.appendChild(body);

	    // add the envelope to the document
	    document.appendChild(envelope);
	}

	/**
	 * Invoke a SOAP call passing in an operation instance
	 *
	 * @param operation The selected operation
	 *
	 * @return The response SOAP Envelope as a String
	 */
	public static String invokeOperation(OperationInfo operation) throws WSDLException
	{
		try{
			return invokeOperation(operation,null);
		}
		catch (Exception e)
		{
			// should log\trace this here
			throw new WSDLException(e);
		}
	}

	/**
	 * Invoke a SOAP call passing in an operation instance and attachments
	 *
	 * @param operation The selected operation
	 * @param attachements The required attachments
	 *
	 * @return The response SOAP Envelope as a String
	 */
	public static String invokeOperation(OperationInfo operation,File[] attachments)
	throws WSDLException
	{
		try{
		
		Document docRequest = XMLSupport.parse(operation.getInputMessageText());

		// create the saaj based soap client
		SOAPClient client = new SOAPClient(docRequest);

		// add any attachments if required
		if (attachments != null)
		{
			client.addAttachments(attachments);
		}

		// set the SOAPAction
		client.setSOAPAction(operation.getSoapActionURI());

		// get the url
		URL url = new URL(operation.getTargetURL());

		// send the soap message
		Document responseDoc = client.send(url);

       // returns just the soap envelope part of the message (i.e no returned attachements will be
	   // seen)
		return XMLSupport.prettySerialise(responseDoc);
		
		}
		catch (Exception e)
		{
			// should log\trace this here
			throw new WSDLException(e);
		}
	}

	/**
	 * Invoke a SOAP call passing in an operation instance
	 *
	 * @param operation The selected operation
	 *
	 * @return The response SOAP Message as a String (includes any attachments and mime headers)
	 */
	public static String invokeOperation2(OperationInfo operation) throws WSDLException
	{
		try{
			return invokeOperation(operation,null);
		}
		catch (Exception e)
		{
			// should log\trace this here
			throw new WSDLException(e);
		}
	}

	/**
	 * Invoke a SOAP call passing in an operation instance and attachments
	 *
	 * @param operation The selected operation
	 * @param attachements The required attachments
	 *
	 * @return The response SOAP Message as a String (includes any attachments and mime headers)
	 */
	public static String invokeOperation2(OperationInfo operation,File[] attachments) 
		throws WSDLException
	{
		try{
		
		Document document = XMLSupport.parse(operation.getInputMessageText());

		// create the saaj based soap client
		SOAPClient client = new SOAPClient(document);

		// add any attachments if required
		if (attachments != null)
		{
			client.addAttachments(attachments);
		}

		// set the SOAPAction
		client.setSOAPAction(operation.getSoapActionURI());

		// get the url
		URL url = new URL(operation.getTargetURL());

		// send the soap message
		String response = client.send2(url);

       // returns the whole saaj soap message (i.e includes attachments and mime headers)
		return response;
		
		}
		catch (Exception e)
		{
			// should log\trace this here
			throw new WSDLException(e);
		}
	}

	public static void main(String[] args) throws Exception
	{
		// for testing purposes only

		System.out.println("Starting the WSDL Parse..");
		WSDLParser wsdlparser = new WSDLParser();
		List services = wsdlparser.getServicesInfo("http://www.xignite.com/xquotes.asmx?WSDL");

		// process objects built from the binding information
		Iterator servicesIter = services.iterator();
		while (servicesIter.hasNext())
		{
			ServiceInfo service = (ServiceInfo)servicesIter.next();
			System.out.println("Service Name: "+service.getName());
			System.out.println();

			Iterator operationsIter = service.getOperations();
			while(operationsIter.hasNext())
			{
				OperationInfo operation = (OperationInfo)operationsIter.next();
				System.out.println("Operation Name: "+operation.toString());
				System.out.println("getInputMessageName: "+operation.getInputMessageName());
				System.out.println("getInputMessageText: "+operation.getInputMessageText());
				System.out.println("getNamespaceURI: "+operation.getNamespaceURI());
				System.out.println("getSoapActionURI: "+operation.getSoapActionURI());
				System.out.println("getStyle: "+operation.getStyle());
				System.out.println("getTargetMethodName: "+operation.getTargetMethodName());
				System.out.println("getTargetObjectURI(): "+operation.getTargetObjectURI());
				System.out.println("getTargetURL(): "+operation.getTargetURL());
				System.out.println();

			}
		}

		System.out.println();
		// get the first operation and invoke it
		Iterator servicesIter2 = services.iterator();
		if (servicesIter2.hasNext())
		{
			ServiceInfo service = (ServiceInfo)servicesIter2.next();
			Iterator operationsIter = service.getOperations();
			if (operationsIter.hasNext())
			{
				OperationInfo operation = (OperationInfo)operationsIter.next();
				System.out.println("Invoking the following:");
				System.out.println("Service Name: "+service.getName());
				System.out.println("Operation Name: "+operation.toString());
				System.out.println("Target URL: "+operation.getTargetURL());
				System.out.println("SOAPAction: "+operation.getSoapActionURI());
				System.out.println("SOAP request: \n"+operation.getInputMessageText());
				System.out.println();
				System.out.println("SOAP response: \n"+invokeOperation(operation));
			}
		}
	}
}
