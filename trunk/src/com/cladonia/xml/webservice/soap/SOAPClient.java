/*
 * $Id: SOAPClient.java,v 1.3 2004/05/28 10:44:16 knesbitt Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.webservice.soap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;


/**
 * Handles SOAP with Attachements, sends and receives messages.
 * 
 *
 * @version	$Revision: 1.3 $, $Date: 2004/05/28 10:44:16 $
 * @author Dogs bay
 */
public class SOAPClient {
	
	private SOAPMessage message = null;
	
	
	/**
	 * Constructs the SOAPClient with a dom that cotains the SOAP Envelope
	 *
	 * @param doc the dom containing the SOAP Envelope  
	 */
	public SOAPClient(Document doc) throws SOAPException
	{
		try{
			
			// create a SOAPMessage object
			MessageFactory factory = MessageFactory.newInstance();
			message = factory.createMessage();
			
			// get the SOAPPart
			SOAPPart soapPart = message.getSOAPPart();
			
			// add our soap envelope to the SOAPPart
			DOMSource domSource = new DOMSource(doc);  
			soapPart.setContent(domSource);
		}
		catch (Exception e)
		{
			// should log\trace error here
			final String errMsg = "The following error occurred creating the SOAP client: "+
			e.getMessage();
			throw new SOAPException(errMsg,e);
		}
	}
	
	
	/**
	 * Constructs the SOAPClient with a dom that cotains the SOAP Envelope, and file array
	 * containing all the required attachments
	 *
	 * @param doc the dom containing the SOAP Envelope
	 * @param attachments The attachments  
	 */
	public SOAPClient(Document doc,File[] attachments) throws SOAPException
	{
		try{
			
			// create a SOAPMessage object
			MessageFactory factory = MessageFactory.newInstance();
			message = factory.createMessage();
			
			// get the SOAPPart
			SOAPPart soapPart = message.getSOAPPart();
			
			// add our soap envelope to the SOAPPart
			DOMSource domSource = new DOMSource(doc);  
			soapPart.setContent(domSource); 
			
			if (attachments == null)
			{
				// no attachments required
				return;
			}
			
			for (int i=0;i<attachments.length;i++)
			{
				File attachment = attachments[i];
				
				// use the activation datahandler to encapsulate an attachment
				DataHandler dataHandler = new DataHandler(new FileDataSource(attachment));
				
				// create AttachmentPart using the datahandler, this sets the Content-Type header 
				// for us
				AttachmentPart attachmentPart = message.createAttachmentPart(dataHandler);
				
				// set the Content-Id header for this attachment (use the file name)
				attachmentPart.setContentId(attachment.getName());
				
				// add the attachment
				message.addAttachmentPart(attachmentPart);
			}
		}
		catch (Exception e)
		{
			// should log\trace error here
			final String errMsg = "The following error occurred creating the SOAP client: "+
			e.getMessage();
			throw new SOAPException(errMsg,e);
		}
	}
	
	/**
	 * adds the attachements to the SOAP message
	 * 
	 * @param attachments The attachments  
	 */
	public void addAttachments(File[] attachments)
	{
		for (int i=0;i<attachments.length;i++)
		{
			File attachment = attachments[i];
			
			// use the activation datahandler to encapsulate an attachment
			DataHandler dataHandler = new DataHandler(new FileDataSource(attachment));

			// create AttachmentPart using the datahandler, this sets the Content-Type header 
			// for us
			AttachmentPart attachmentPart = message.createAttachmentPart(dataHandler);
			
			// set the Content-Id header for this attachment (use the file name)
			attachmentPart.setContentId(attachment.getName());

			// add the attachment
			message.addAttachmentPart(attachmentPart);
		}
	}

	/**
	 * gets the full message i.e the SOAP Envelope and the attachments with their mime headers 
	 * 
	 * @return the full message (SOAP Envelope and attachments)
	 */
	public String getMimeMessage() throws SOAPException
	{
		try{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			message.writeTo(out);
			return out.toString();	
		}
		catch(Exception e)
		{
			// should log\trace error here
			final String errMsg = "The following error occurred getting the mime message: "+
			e.getMessage();
			throw new SOAPException(errMsg,e);
		}
	}
	
	/**
	 * sends the soap message to the web service 
	 * @param endpoint The destination web service
	 * @return the response from the web service (SOAP Envelope and attachments)
	 */
	public String send2(URL endpoint) throws SOAPException
	{
		try{
			
			// get a soap connection
			SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
			SOAPConnection connection = factory.createConnection();
			
			// make the call
			SOAPMessage response = connection.call(message, endpoint);
			
			// return the full soap message as a String
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.writeTo(out);
			return out.toString();
			
		}
		catch (Exception e)
		{
			// should log\trace error here
			final String errMsg = "The following error occurred sending the SOAP message: "+
			e.getMessage();
			throw new SOAPException(errMsg,e);
		}
		
	}
	
	
	/**
	 * sends the soap message to the web service 
	 * @param endpoint The destination web service
	 * @return the SOAP envelope part of the web service response
	 */
	public Document send(URL endpoint) throws SOAPException
	{
		try{
			
			// get a soap connection
			SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
			SOAPConnection connection = factory.createConnection();
			
			// make the call
			SOAPMessage response = connection.call(message, endpoint);
			
			// return the dom part
			return response.getSOAPPart();
			
		}
		catch(Exception e)
		{
			// should log\trace error here
			final String errMsg = "The following error occurred sending the SOAP message: "+
			e.getMessage();
			throw new SOAPException(errMsg,e);
		}
		
	}
	
	/**
	 * sets the soap action header
	 * @param soapAction the SOAPAction header
	 */
	public void setSOAPAction(String soapAction)
	{
		MimeHeaders headers = message.getMimeHeaders();
		if (soapAction.equals(""))
		{
			// if no soap action specified then put in "" for SOAPAction as Amazon soap
			// service can't handle the case when there is no value specified for 
			// the SOAPAction
			soapAction = "\"\""; 
		}
		
		headers.setHeader("SOAPAction",soapAction);
	}
	
	public static void main(String[] args) throws Exception
	{
		// for testing purposes only
		System.out.println("**** Testing Soap with attachments ****");
		
		javax.xml.parsers.DocumentBuilderFactory dbf = 
			javax.xml.parsers.DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		
		File f = new File("c:\\temp\\soap.xml");
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document doc = db.parse(new java.io.FileInputStream(f));
		DOMSource domSource = new DOMSource(doc); 
		
		File[] attachments =  new File[1];
		File attachment = new File("c:\\temp\\die.JPG");
		attachments[0] = attachment;
		
		
		// use this instead to get a valid soap response from this webservice
		SOAPClient client = new SOAPClient(doc);
		
		// note this webservice fails when you send it an attachment
		client.addAttachments(attachments);
		
		// output the message to console
		//System.out.println("The Soap Message:\n"+client.getMimeMessage());
		
		// set the SOAPAction
		client.setSOAPAction("http://www.Nanonull.com/TimeService/getServerTime");
		
		System.out.println("Sending the soap message...");
		
		// (with tcptrace: localhost:8080 -> www.nanonull.com:80)
		URL url = new URL("http://localhost:8080/TimeService/TimeService.asmx");
		
		// returns just the soap envelope part of the message (i.e no returned attachements will be
		// seen)
		Document responseDoc = client.send(url);
		System.out.println("The response message:\n\n"+serialise(responseDoc));
		
		// Note: can use send2 to get a String of the whole message returned, this would show any
		// returned attachments
		//String response = client.send2(url);
		//System.out.println("The response message:\n"+response);
		
		System.out.println("");
	}
	
	/**
     * Utility function for serialising a DOM into an XML string
     * @param dom The DOM object to serialise
     * @return The serialised XML
     */
    private static String serialise(Document dom) throws Exception 
	{
        return serialise(new DOMSource(dom));
    }
    
    /**
     * Utility function for serialising a DOM into an XML string
     * @param source The JAXM Source object to serialise
     * @return The serialised XML
     */
    private static String serialise(javax.xml.transform.Source source) throws Exception 
	{
        
        javax.xml.transform.TransformerFactory factory =
        	javax.xml.transform.TransformerFactory.newInstance();
        javax.xml.transform.Transformer transformer = factory.newTransformer();

        try {
            // Use TrAX to convert DOM into serialised XML
            java.io.StringWriter writer = new java.io.StringWriter();
            javax.xml.transform.stream.StreamResult result = 
            	new javax.xml.transform.stream.StreamResult(writer);

            // Use the transformer to serialise the XML content
            transformer.transform(source, result);

            // Return the result as an XML string
            return writer.toString();
        }
        catch (javax.xml.transform.TransformerConfigurationException e) {
            // JAXP/TrAX is not configured properly for this system
            throw new Exception(e);
        }
        catch (javax.xml.transform.TransformerException e) {
            // Problem with the DOM?
            throw new Exception(e);
        }
	}
}
