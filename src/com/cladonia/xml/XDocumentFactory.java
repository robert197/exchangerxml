/*
 * $Id: XDocumentFactory.java,v 1.1 2004/03/25 18:41:32 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.QName;

/**
 * Makes sure the XElement is created instead of the 
 * org.dom4j.Element
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:41:32 $
 * @author Dogsbay
 */
public class XDocumentFactory extends DocumentFactory {
    private static transient DocumentFactory singleton = new XDocumentFactory();
	
    /** 
	 * Access to singleton implementation of DocumentFactory which 
	 * is used if no DocumentFactory is specified when building using the 
	 * standard builders.
	 *
	 * @return the default singleon instance
	 */
    public static DocumentFactory getInstance() {
        return singleton;
    }

	// The public constructor for the factory
    public XDocumentFactory() {
        super();
    }
	
    /** 
     * Creates the XDocument. 
     *
     * @return the XDocument.
     */
    public Document createDocument() {
        XDocument answer = new XDocument();
        answer.setDocumentFactory( this);

        return answer;
    }
    
    /** 
     * Creates the XDocument. 
	 *
	 * @param root the root element.
     *
     * @return the XDocument.
     */
    public Document createDocument(Element root) {
        Document answer = createDocument();
        answer.setRootElement( root);

        return answer;
    }

    /** 
     * Creates the XDocumentType. 
     *
     * @param root the root element.
     *
     * @return the XDocument.
     */
    public DocumentType createDocType( String name, String publicId, String systemId) {
        return new XDocumentType( name, publicId, systemId);
    }

    /** 
     * Creates the XElement. 
     *
     * @param qname, the name of the element.
	 *
     * @return the XElement.
     */
    public Element createElement( QName qname) {
        return new XElement( qname);
    }

    /** 
     * Creates the XAttribute. 
     *
     * @param qname, the name of the attribute.
     *
     * @return the XAttribute.
     */
    public Attribute createAttribute( Element owner, QName qname, String value) {
        return new XAttribute( qname, value);
    }
} 
