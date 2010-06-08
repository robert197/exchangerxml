/*
 * $Id: DTDElement.java,v 1.2 2004/09/23 10:32:10 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.dtd;

import java.util.StringTokenizer;
import java.util.Vector;

import com.cladonia.schema.ElementInformation;

/**
 * A container for a SchemaElement and possible attributes/child elements
 *
 * @version	$Revision: 1.2 $, $Date: 2004/09/23 10:32:10 $
 * @author Dogsbay
 */
public class DTDElement implements ElementInformation {
	private Vector processedNamespaces	= null;

	private String name			= null;
	private String prefix		= null;
	private String namespace	= null;

	private DTDElement parent	= null;
	private Vector children		= null;
	private Vector attributes	= null;
	private Vector references	= null;
	private String type			= null;
	
	public DTDElement( String name) {
		this( name, null);
	}

	public DTDElement( String qname, String refs) {
		int index = qname.indexOf( ':');
		
		if ( index != -1) {
			prefix = qname.substring( 0, index);
			name = qname.substring( index+1, qname.length());
		} else {
			prefix = "";
			name = qname;
		}
		
		children = new Vector();
		attributes = new Vector();
		references = new Vector();
		
		processedNamespaces = new Vector();
		
		if ( refs != null) {
			setReferences( refs);
		}
	}
	
	public boolean addChild( DTDElement element) {
		if ( element != null) {
			for ( int i = 0; i < children.size(); i++) {
				DTDElement child = (DTDElement)children.elementAt(i);
				
				if ( child.getQualifiedName().equals( element.getQualifiedName())) {
					return false;
				}
			}
			
			children.addElement( element);
			return true;
		} else {
			return false;
		}
	}
	
	public void setParent( DTDElement element) {
		this.parent = element;
	}

	public void addChildren( Vector children) {
		if ( children != null) {
			for ( int i = 0; i < children.size(); i++) {
				DTDElement child = (DTDElement)children.elementAt(i);
				addChild( child);
			}
		}
	}

	public boolean isEmpty() {
		if ( references.size() == 1) {
			return "EMPTY".equals( references.elementAt(0));
		}
		
		return false;
	}

	public Vector getChildElements() {
		return new Vector( children);
	}

	public boolean addAttribute( DTDAttribute attribute) {
		if ( attribute != null) {
			for ( int i = 0; i < attributes.size(); i++) {
				DTDAttribute attrib = (DTDAttribute)attributes.elementAt(i);
				
				if ( attrib.getUniversalName().equals( attribute.getUniversalName())) {
					return false;
				}
			}

			attributes.addElement( attribute);
			return true;
		} else {
			return false;
		}
	}

	private void addAttributes( Vector attributes) {
		if ( attributes != null) {
			for ( int i = 0; i < attributes.size(); i++) {
				DTDAttribute attribute = (DTDAttribute)attributes.elementAt(i);
				addAttribute( attribute);
			}
		}
	}

	public Vector getAttributes() {
		return new Vector( attributes);
	}
	
	public Vector getReferences() {
		return references;
	}

	public String getUniversalName() {
		StringBuffer uname = new StringBuffer();
		if ( namespace != null && namespace.trim().length() > 0) {
			uname.append( "{");
			uname.append( namespace);
			uname.append( "}");
		}
		
		uname.append( name);
		
		return uname.toString();
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix( String prefix) {
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace( String namespace) {
		this.namespace = namespace;
	}

	public String getQualifiedName() {
		String qname = "";
		
		if ( prefix != null && prefix.trim().length() > 0) {
			qname = prefix+":";
		}
		
		return qname+getName();
	}
	
	public String getType() {
		return type;
	}

	public String getParentName() {
		if ( parent != null) {
			return parent.getName();
		}
		
		return null;
	}

	public void setReferences( String refs) {
		type = refs;
		StringTokenizer tokenizer = new StringTokenizer( refs, " \t\n\r\f*(),|?+");

		while ( tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
//			System.out.println("Token = "+token);
			references.addElement( token);
		}
	}
	
	public void addAnnotation( String annotation) {
	}

	public String getAnnotations() {
		return null;
	}

	public void processNamespaces() {
//		System.out.println( "["+this+"] processNamespaces()");
		Vector attributes = getAttributes();
		
		for ( int i = 0; i < attributes.size(); i++) {
			DTDAttribute attribute = (DTDAttribute)attributes.elementAt(i);

			if ( attribute.isNamespace()) {
				String prefix = attribute.getNamespacePrefix();
				String uri = attribute.getNamespaceURI();
				
				processNamespaceInternal( prefix, uri);
			}
		}
	}

	protected void processNamespaceInternal( String prefix, String uri) {
//		System.out.println( "["+this+"] processNamespaceInternal( "+prefix+", "+uri+")");

		if ( prefix == null) { // default namespace
			this.namespace = uri;

			Vector elements = getChildElements();

			for ( int i = 0; i < elements.size(); i++) {
				DTDElement element = (DTDElement)elements.elementAt(i);
				element.processNamespace( prefix, uri);
			}
		} else {
			if ( prefix.equals( this.prefix)) {
				namespace = uri;
			}
			
			Vector attributes = getAttributes();
			
			for ( int i = 0; i < attributes.size(); i++) {
				DTDAttribute attribute = (DTDAttribute)attributes.elementAt(i);
	
				if ( prefix.equals( attribute.getPrefix())) {
					attribute.setNamespace( uri);
				}
			}

			Vector elements = getChildElements();

			for ( int i = 0; i < elements.size(); i++) {
				DTDElement element = (DTDElement)elements.elementAt(i);
				element.processNamespace( prefix, uri);
			}
		}
	}

	protected void processNamespace( String prefix, String uri) {
		if ( !isProcessed( prefix, uri)) {
			addProcessed( prefix, uri);

			if ( namespace == null && !isNamespaceDefined( prefix)) { // avoid recursive loops
				processNamespaceInternal( prefix, uri);
			}
		}
	}
	
	private boolean isNamespaceDefined( String prefix) {
		Vector attributes = getAttributes();
		
		for ( int i = 0; i < attributes.size(); i++) {
			DTDAttribute attribute = (DTDAttribute)attributes.elementAt(i);

			if ( attribute.isNamespace()) {
				String pre = attribute.getNamespacePrefix();
				
				if ( pre == null && prefix == null) {
					return true;
				} else if ( pre != null && prefix != null && pre.equals( prefix)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void addProcessed( String uri, String prefix) {
		if ( uri == null) {
			uri = "";
		}
		
		if ( prefix == null) {
			prefix = "";
		}

		String[] namespace = new String[2];
		namespace[0] = uri;
		namespace[1] = prefix;
		
		processedNamespaces.addElement( namespace);
	}

	private boolean isProcessed( String uri, String prefix) {
		if ( uri == null) {
			uri = "";
		}
		
		if ( prefix == null) {
			prefix = "";
		}

		for ( int i = 0; i < processedNamespaces.size(); i++) {
			String[] namespace = (String[])processedNamespaces.elementAt(i);
			
			if ( namespace[0].equals( uri) && namespace[1].equals( prefix)) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isSame( ElementInformation element) {
		if ( element instanceof DTDElement) {
			return element == this;
		}
		
		return false;
	}
} 
