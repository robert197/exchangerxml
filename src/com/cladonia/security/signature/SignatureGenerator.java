/*
 * $Id: SignatureGenerator.java,v 1.3 2004/06/03 17:31:46 knesbitt Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */

package com.cladonia.security.signature;

import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;

import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.content.x509.XMLX509SubjectName;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPathContainer;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.cladonia.xngreditor.properties.ConfigurationProperties;


/**
 * An implementation of an XML Signature generator. 
 *
 * @version	$Revision: 1.3 $, $Date: 2004/06/03 17:31:46 $
 * @author Dogs bay
 */
public class SignatureGenerator {
	
	static 
	{
		  // apache api needs to be initialized before being used
	      org.apache.xml.security.Init.init();
	}
	
	// all the allowed c14N algorithms
	public static final String TRANSFORM_C14N_OMIT_COMMENTS = 
		"http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
	public static final String TRANSFORM_C14N_WITH_COMMENTS  =  
		"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
	public static final String TRANSFORM_C14N_EXCL_OMIT_COMMENTS= 
		"http://www.w3.org/2001/10/xml-exc-c14n#";
	public static final String TRANSFORM_C14N_EXCL_WITH_COMMENTS =
		"http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
	public static final String NO_C14N = "No_c14n";
	
	private PrivateKey pkey = null;
	private X509Certificate cert = null;
	private Document doc = null;
	private String uri = null;
	private String xpath = null;
	private String id = null;
	
	// holds the C14n algorithm
	private String c14nAlg = TRANSFORM_C14N_OMIT_COMMENTS;
	
	/**
 	 * Constructs a SignaturGenerator from a private key, certificate and dom document.
 	 * Use this constructor to create an enveloped signature.
 	 *
 	 * @param pkey the private key to use for signing
 	 * @param cert the X509 Certificate containing the matching public key
 	 * @param doc  the dom containing the XML to be signed  
 	 */
	public SignatureGenerator(PrivateKey pkey, X509Certificate cert,Document doc)
	{
		this.pkey = pkey;
		this.cert = cert;
		this.doc = doc;
	}
	
	// call this constructor for a detached signature
	/**
 	 * Constructs a SignaturGenerator from a private key, certificate and a uri (file or http).    
 	 * Use this constructor for a detached signature
 	 *
 	 * @param pkey the private key to use for signing
 	 * @param cert the X509 Certificate containing the matching public key
 	 * @param uri a String representing the uri of the resource to be signed  
 	 */
	public SignatureGenerator(PrivateKey pkey, X509Certificate cert,String uri)
	{
		this.pkey = pkey;
		this.cert = cert;
		this.uri = uri;
		this.doc = null;
	}
	
	/**
	 * Sets the Canonicalization algorithm. Note: The SignedInfo block requires a 
	 * CanonicalizationMethod. Whatever is set using this method will be used for both the 
	 * SignedInfo and the Reference, except in the case where "NO_C14N" is set where then the
	 * normal (without comeents) C14N will be used for the SignedInfo and no C14N transform
	 * for the Reference.
	 *
	 * @param algorithm the canonicalization algorithm 
	 */	
	public void setC14nAlgorithm(String algorithm) {
        this.c14nAlg = algorithm;
    }
	
	/**
	 * Sets the xpath expression if an xpath transform is required.
	 * Note: Can onlt be used on XML content or an exception is thrown.
	 *
	 * @param xpath the xpath expression 
	 */	
	public void setXpath(String xpath)
	{
		this.xpath = xpath;
	}
	
	/**
	 * Sets the Id.
	 * This can only be used for enveloped signatures. Where a Reference URI value of "" means
	 * sign the whole docuemnt, a URI of "#someid" means there is an element that contains an 
	 * attribute (of type ID) called one of the following id\ID\Id that contains a value of 
	 * "someid". In other words it is a way of selecting a particular element (and all it's 
	 * attributes and child elements) in the document containing the signature.
	 *
	 * @param xpath the xpath expression 
	 */	
	public void setId(String id)
	{
		this.id = id;
	}
	
	/**
	 * Signs the resource.
	 * Uses rsa-sha1 for the signature algorithm. Adds any required transforms. Adds the KeyInfo
	 * section which contains the certificate and the public key, which allows for the 
	 * signature to be verified without having to a find a public key by some application
	 * specific mechanism.
	 * 
	 * @return Document a new dom that contains the signature in the case of a detached signature,
	 * and a signature plus the XML content (the same dom that was passed in the constructor) 
	 * in the case of an enveloped signature
	 */	
	public Document sign(ConfigurationProperties props) throws SignatureException
	{
		try{
			
			String sigInfoC14NAlg = null;
			if (!c14nAlg.equals(NO_C14N))
			{
				// set the SignedInfo canonacalization to be the same as the reference
				sigInfoC14NAlg = c14nAlg;
			}
			else
			{
				// if no canoncalization has been specified then just set the SignedInfo cano to be 
				// the normal (without comments) cano
				sigInfoC14NAlg = TRANSFORM_C14N_OMIT_COMMENTS;
			}
			
			XMLSignature sig = null;
			if (this.doc != null)
			{
				// we require an enveloped signature
				
				// construct the signature object
				sig = new XMLSignature(doc,"",XMLSignature.ALGO_ID_SIGNATURE_RSA,sigInfoC14NAlg);
				
				// get the root
				Element root = doc.getDocumentElement();
				
				// append the signature
				root.appendChild(sig.getElement());
				
				// create the transforms object for the Document/Reference
				Transforms transforms = new Transforms(doc);
				
				// strip away the signature element if the signature is inside the same document
				transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
				
				// check for xpath transform
				if (xpath != null)
				{
					// get the prefix mappings
					Map mappings = null;
					
					if (props!= null)
					{
						mappings = props.getPrefixNamespaceMappings();
					}
					
					XPathContainer xc = new XPathContainer(doc);
					
					if (mappings != null && mappings.size() > 0)
		 			{
		 				Iterator iterator = mappings.keySet().iterator();
		 				//String prefix = (String)iterator.next();
		 				//String namespace = (String)mappings.get(prefix)
		 				
		 				//ele = XMLUtils.createDSctx(w3cDoc,prefix,namespace);
		 				
		 				while (iterator.hasNext())
		 				{
		 					String prefix = (String)iterator.next();
		 					String namespace = (String)mappings.get(prefix);
		 					//ele.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:"+prefix,namespace);
		 					xc.setXPathNamespaceContext(prefix,namespace);
		 				}
		 			}
					
					xc.setXPath(xpath);
					transforms.addTransform(Transforms.TRANSFORM_XPATH, xc.getElementPlusReturns());
				}
				
				if (!c14nAlg.equals(NO_C14N))
				{
					// declare the canonicalization algorithm
					transforms.addTransform(c14nAlg);
				}
				
				// Add the above Document/Reference
				if (id != null)
					sig.addDocument("#"+id, transforms, Constants.ALGO_ID_DIGEST_SHA1);  
				else
					sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);      
			}
			else
			{	
				// we require a detached signature
				
				// get a new dom to hold the signature
				doc = getDom();
				
				// construct the signature object
				sig = new XMLSignature(doc,uri,XMLSignature.ALGO_ID_SIGNATURE_RSA,sigInfoC14NAlg);
				
				// append the signature
				doc.appendChild(sig.getElement());
				
				// check for transforms
				Transforms transforms = null;
				
				if ((xpath != null) || (!c14nAlg.equals(NO_C14N)))
				{
					// create the transforms object for the Document/Reference
					transforms = new Transforms(doc);
					
					// check for xpath transform
					if (xpath != null)
					{
						// get the prefix mappings
						Map mappings = null;
						
						if (props!= null)
						{
							mappings = props.getPrefixNamespaceMappings();
						}
						
						XPathContainer xc = new XPathContainer(doc);
						
						if (mappings != null && mappings.size() > 0)
			 			{
			 				Iterator iterator = mappings.keySet().iterator();
			 				//String prefix = (String)iterator.next();
			 				//String namespace = (String)mappings.get(prefix)
			 				
			 				//ele = XMLUtils.createDSctx(w3cDoc,prefix,namespace);
			 				
			 				while (iterator.hasNext())
			 				{
			 					String prefix = (String)iterator.next();
			 					String namespace = (String)mappings.get(prefix);
			 					//ele.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:"+prefix,namespace);
			 					xc.setXPathNamespaceContext(prefix,namespace);
			 				}
			 			}
						
						xc.setXPath(xpath);
						transforms.addTransform(Transforms.TRANSFORM_XPATH, xc.getElementPlusReturns());
					}
					
					if (!c14nAlg.equals(NO_C14N))
					{
						// declare which canonicaliztion algorithm
						transforms.addTransform(c14nAlg);
					}
					
					// Add the above Document/Reference
					sig.addDocument(uri, transforms, Constants.ALGO_ID_DIGEST_SHA1);
				}
				else
				{
					sig.addDocument(uri);
				}
			}
			
			// create the x509 data
			X509Data x509data = new X509Data(doc);
			
			// add the subject name
			x509data.add(new XMLX509SubjectName(doc, cert));
			
			// add the cert
			x509data.add(new XMLX509Certificate(doc, cert));
			
			// add the x509 data to the keyinfo
			sig.getKeyInfo().add(x509data);
			
			// add the public key to the keyinfo
			sig.addKeyInfo(cert.getPublicKey());
			
			// sign the document
			sig.sign(pkey);
			
			return doc;
			
		}
		catch(Exception e)
		{
			// should log\trace error here
			final String errMsg = "The following error occurred signing the document: "+
			e.getMessage();
			throw new SignatureException(errMsg,e);
		}
	}
	
	// utility method to create a new dom (could make this into a dom cache)
	private Document getDom() throws Exception
	{
		// create a new dom to hold the signature	
		javax.xml.parsers.DocumentBuilderFactory dbf =
	         javax.xml.parsers.DocumentBuilderFactory.newInstance();
		
		// XML Signature needs to be namespace aware
	    dbf.setNamespaceAware(true);

	    javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
	    org.w3c.dom.Document docNew = db.newDocument();
	    
	    return docNew;
	}
	
	// for testing purposes only
	public static void main(String args[]) throws Exception
	{
		// use this if you want to configure logging, normally would put this in a static block, 
		// but this is just for testing (see jre\lib\logging.properties)
	    org.apache.commons.logging.Log log = 
	        org.apache.commons.logging.LogFactory.getLog(
	                        SignatureGenerator.class.getName());
	    
	    //System.out.println("Using the logger: "+log.getClass().getName());  

	    //log.debug("Debug is on");
	    //log.warn("Warning is on");
	    //log.error("Error is on");
	    log.info("**** Testing Signature Generator *****");
	    	
		//All the parameters for the keystore
		String keystoreType = "JKS";
	    String keystoreFile = "data/keystore.jks";
	    String keystorePass = "xmlexchanger";
	    String privateKeyAlias = "exchanger";
	    String privateKeyPass = "xmlexchanger";
	    String certificateAlias = "exchanger";

	    // set the keystore and private key properties
	    KeyBuilder.setParams(keystoreType,keystoreFile,keystorePass,privateKeyAlias,privateKeyPass,
	    		certificateAlias);
	    
	    // get the private key for signing.
	    PrivateKey privateKey = KeyBuilder.getPrivateKey();

	    // get the cert
	    X509Certificate cert = KeyBuilder.getCertificate();
	    
	    
	    // ************* create a sample to be signed ******************
	    javax.xml.parsers.DocumentBuilderFactory dbf =
	         javax.xml.parsers.DocumentBuilderFactory.newInstance();

	    //XML Signature needs to be namespace aware
	    dbf.setNamespaceAware(true);
	    
	    javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
	    org.w3c.dom.Document document = db.newDocument();
	    
	    //Build a sample document. It will look something like:
	    //<!-- Comment before -->
	    //<cladonia:Exchanger xmlns:cladonia="http://www.exchangerxml.com">
	    //</cladonia:Exchanger>
	    
	    document.appendChild(document.createComment(" Comment before "));
	    Element root = document.createElementNS("http://www.exchangerxml.com","cladonia:Exchanger"); 
	    root.setAttributeNS(null, "attr1", "test1");
	    root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:foo", "http://www.exchangerxml.com/#foo");
	    root.setAttributeNS("http://example.org/#foo", "foo:attr1", "foo's test");
	    root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cladonia", "http://www.exchangerxml.com");
	    document.appendChild(root);
	    Element firstchild = document.createElementNS("http://www.exchangerxml.com","cladonia:Editor"); 
	    firstchild.appendChild(document.createTextNode("simple text\n"));
	    firstchild.setAttributeNS(null,"Id","CladoniaId");
	    root.appendChild(firstchild);
	    //******************** End of sample to be signed*************************
	    
	    // *************** Signature 1
	    // create SignatureGenerator using private key, cert and the dom (i.e an enveloped signature)
		SignatureGenerator gen = new SignatureGenerator(privateKey,cert,document);

		// set the c14n algorithm (Exclusive)
		gen.setC14nAlgorithm(SignatureGenerator.TRANSFORM_C14N_EXCL_WITH_COMMENTS);
		
		// set the xpath transform
		gen.setXpath("//cladonia:Editor");
		
		// set the id
		gen.setId("CladoniaId");

		// sign the document
		document = gen.sign(null);
	   
		// output the enveloped signature
		FileOutputStream fos = new FileOutputStream("c:\\temp\\sigout.xml");
		XMLUtils.outputDOMc14nWithComments(document, fos);
		fos.close();
		
		System.out.println("Created Signature 1 - an enveloped signature");
		
		
		// ************** Signature 2
		// now sign the previous output as an example of a detached signature
		SignatureGenerator gen2 = new SignatureGenerator(privateKey,cert,"file:///c:/temp/sigout.xml");
		
		// set the c14n algorithm
		gen2.setC14nAlgorithm(SignatureGenerator.TRANSFORM_C14N_WITH_COMMENTS);

		// sign the document
		Document document2 = gen2.sign(null);
		
		// output the detached signature
		FileOutputStream fos2 = new FileOutputStream("c:\\temp\\sigout2.xml");
		XMLUtils.outputDOMc14nWithComments(document2, fos2);
		fos2.close();
		
		System.out.println("Created Signature 2 - a detached signature");
		System.out.println("");
	}	
}
