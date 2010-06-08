/*
 * $Id: SignatureVerifier.java,v 1.2 2004/04/06 08:30:48 knesbitt Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */

package com.cladonia.security.signature;


import java.io.File;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.cladonia.xml.XngrURLUtilities;


/**
 * An implementation of an XML Signature verifier. 
 *
 * @version	$Revision: 1.2 $, $Date: 2004/04/06 08:30:48 $
 * @author Dogs bay
 */
public class SignatureVerifier {
	
	static 
	{
		  // apache api needs to be initialized before being used
	      org.apache.xml.security.Init.init();
	}
	
	private Document doc = null;
	private String baseUri = null;
	
	/**
 	 * Constructs a SignaturVerifier using a dom and a base uri (can pass in the URL to the file
 	 * containg the signature instead)
 	 *
 	 * @param doc a dom containing the signature
 	 * @param baseUri the baseUri (this is required, I don't like it though), see the uri spec.
 	 */
	public SignatureVerifier(Document doc,String baseUri)
	{
		this.doc = doc;
		this.baseUri = baseUri;
	}
	
	
	/**
	 * Verifys the signature.
	 * Uses Xpath to find the signature in the dom. Uses the certificate from the KeyInfo section
	 * to verify, if no certiicate is present then looks for the public key itself.
	 * 
	 * @return boolean true if the signature verifys, otherwise returns false
	 */	
	public boolean verify() throws SignatureException
	{
		try{
		
		// using xpath to locate the signature
		Element nscontext = XMLUtils.createDSctx(doc, "ds",Constants.SignatureSpecNS);
		Element sigElement = (Element) XPathAPI.selectSingleNode(doc,"//ds:Signature[1]", nscontext);
		
		// create the main apache object using the signature element and base uri
		XMLSignature signature = new XMLSignature(sigElement,baseUri);
		
		// look for the KeyInfo section
		KeyInfo ki = signature.getKeyInfo();
		if (ki != null)
		{
			X509Certificate cert = signature.getKeyInfo().getX509Certificate();
			if (cert != null)
			{
				return signature.checkSignatureValue(cert);
			}
			else
			{
				PublicKey publickey = signature.getKeyInfo().getPublicKey();
				if (publickey != null)
				{
					return signature.checkSignatureValue(publickey);
				}
				else
				{
					throw new Exception("Could not find the public key in the signature");
				}
			}
		}
		else
		{
			throw new Exception("Could not find any KeyInfo in the signature");
		}
		
		}
		catch(Exception e)
		{
			// should log\trace error here
			final String errMsg = "The following error occurred verifying the signature: "+
			e.getMessage();
			throw new SignatureException(errMsg,e);
		}
	}
	
	// for testing purposes only
	public static void main(String[] args) throws Exception
	{
		org.apache.commons.logging.Log log = 
	        org.apache.commons.logging.LogFactory.getLog(
	                        SignatureGenerator.class.getName());
		
		log.info("**** Testing Signature Verification *****");
		
		javax.xml.parsers.DocumentBuilderFactory dbf =
	         javax.xml.parsers.DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);
		
		File f = new File("c:\\temp\\sigout.xml");
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document doc = db.parse(new java.io.FileInputStream(f));
		
		SignatureVerifier verifier = new SignatureVerifier(doc, XngrURLUtilities.getURLFromFile(f).toString());
		if (verifier.verify())
			System.out.println("Signature 1 - verification passed");
		else
			System.out.println("Signature 1 - verification failed");
		
		
		File f2 = new File("c:\\temp\\sigout2.xml");
		org.w3c.dom.Document doc2 = db.parse(new java.io.FileInputStream(f2));
		
		// Note the apache api requires that you pass in a base uri, the location where the 
		// signature file is fine, if you do not know this (i.e in the case where the signature
		// file has not been saved), then just pass in "file:", as it works!!
		SignatureVerifier verifier2 = new SignatureVerifier(doc2,"file:");
		if (verifier2.verify())
			System.out.println("Signature 2 - verification passed");
		else
			System.out.println("Signature 2 - verification failed");
		
		
		File f3 = new File("c:\\temp\\vordelsig.xml");
		org.w3c.dom.Document doc3 = db.parse(new java.io.FileInputStream(f3));
		SignatureVerifier verifier3 = new SignatureVerifier(doc3, XngrURLUtilities.getURLFromFile(f3).toString());
		if (verifier3.verify())
			System.out.println("Vordel Signature - verification passed");
		else
			System.out.println("Vordel Signature - verification failed");
		
		File f4 = new File("c:\\temp\\license.xml");
		org.w3c.dom.Document doc4 = db.parse(new java.io.FileInputStream(f4));
		SignatureVerifier verifier4 = new SignatureVerifier(doc4, XngrURLUtilities.getURLFromFile(f4).toString());
		if (verifier4.verify())
			System.out.println("License Signature - verification passed");
		else
			System.out.println("License Signature - verification failed");
		
	}
}
