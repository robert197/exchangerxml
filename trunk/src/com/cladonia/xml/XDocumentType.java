/*
 * $Id: XDocumentType.java,v 1.1 2004/03/25 18:41:32 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.dom4j.dtd.AttributeDecl;
import org.dom4j.dtd.ExternalEntityDecl;
import org.dom4j.dtd.InternalEntityDecl;
import org.dom4j.tree.DefaultDocumentType;

/**
 * The default implementation of the XDocument interface.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:41:32 $
 * @author Dogsbay
 */
public class XDocumentType extends DefaultDocumentType {

    public XDocumentType() { 
		super();
    }

    public XDocumentType( String name, String systemId) { 
        super( name, systemId);
    }

    public XDocumentType( String name, String publicId, String systemId) { 
        super( name, publicId, systemId);
//	    System.out.println( "XDocumentType( "+name+", "+publicId+", "+systemId+")");
    }

    public void write( Writer writer) throws IOException {
//		System.out.println( "XDocumentType.write()");
        writer.write( "<!DOCTYPE " );
        writer.write( getElementName() );
        
        boolean hasPublicID = false;
        String publicID = getPublicID();
        
        if ( publicID != null && publicID.length() > 0 ) {
            writer.write( " PUBLIC \"" );
            writer.write( publicID );
            writer.write( "\"" );
            hasPublicID = true;
        }
        
        String systemID = getSystemID();
        if ( systemID != null && systemID.length() > 0 ) {
            if (!hasPublicID) {
                writer.write(" SYSTEM");
            }
            writer.write( " \"" );
            writer.write( systemID );
            writer.write( "\"" );
        }
        List list = getInternalDeclarations();
        if ( list != null && list.size() > 0 ) {
            writer.write( " [" );
            for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
                Object decl = iter.next();
                writer.write( "\n  " );

				if ( decl instanceof ExternalEntityDecl) {
					write( writer, (ExternalEntityDecl)decl);
				} else if ( decl instanceof AttributeDecl) {
	                write( writer, (AttributeDecl)decl);
				} else if ( decl instanceof InternalEntityDecl) {
				    write( writer, (InternalEntityDecl)decl);
				} else {
					writer.write( decl.toString());
				}
            }
            writer.write( "\n]" );
        }
        writer.write(">");
    }
	
    private void write( Writer writer, InternalEntityDecl decl) throws IOException {
        String name = decl.getName();
        String value = decl.getValue();

        if ( name.startsWith( "%")) {
        	name = "% "+name.substring( 1);
        }

        StringBuffer buffer = new StringBuffer( "<!ENTITY " );
		buffer.append( name);
		buffer.append( " \"");
		buffer.append( value);
		buffer.append( "\">");
		
		writer.write( buffer.toString());
    }
	
	private void write( Writer writer, ExternalEntityDecl decl) throws IOException {
		String name = decl.getName();
		String publicID = decl.getPublicID();
		String systemID = decl.getSystemID();
		
		if ( name.startsWith( "%")) {
			name = "% "+name.substring( 1);
		}
		
		StringBuffer buffer = new StringBuffer( "<!ENTITY " );
		buffer.append( name);
		if ( publicID != null) {
		    buffer.append( " PUBLIC \"");
		    buffer.append( publicID);
		    buffer.append( "\" ");

		    if ( systemID != null) {
		        buffer.append( " \"");
		        buffer.append( systemID);
		        buffer.append( "\" ");
		    }
		} else if ( systemID != null) {
		    buffer.append( " SYSTEM \"");
		    buffer.append( systemID);
		    buffer.append( "\" ");
		}
		buffer.append( ">");
		writer.write( buffer.toString());
	}

	private void write( Writer writer, AttributeDecl decl) throws IOException {
		String elementName	= decl.getElementName();
		String attributeName = decl.getAttributeName();
		String type			= decl.getType();
		String valueDefault	= decl.getValueDefault();
		String value		= decl.getValue();
	
		StringBuffer buffer = new StringBuffer( "<!ATTLIST ");
		buffer.append( elementName );
		buffer.append( " " );
		buffer.append( attributeName );
		buffer.append( " " );
		buffer.append( type );
		buffer.append( " " );
		if ( valueDefault != null) {
		    buffer.append(valueDefault);
		    if ( valueDefault.equals("#FIXED")) {
		        buffer.append(" \"");
		        buffer.append(value);
		        buffer.append("\"");
		    }
		} 
		else {
		    buffer.append("\"");
		    buffer.append(value);
		    buffer.append("\"");
		}
		buffer.append(">");

		writer.write( buffer.toString());
	}
} 
