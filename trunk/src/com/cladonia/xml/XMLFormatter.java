/*
 * $Id: XMLFormatter.java,v 1.3 2004/05/28 09:14:00 edankert Exp $
 *
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.mozilla.org/MPL/ 
 *
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is eXchaNGeR browser code. (org.xngr.browser.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd.. Portions created 
 * by the Initial Developer are Copyright (C) 2003 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */
package com.cladonia.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.StringTokenizer;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.Text;
import org.dom4j.dtd.InternalEntityDecl;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.NamespaceStack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**<p><code>XMLWriter</code> takes a DOM4J tree and formats it to a
  * stream as XML.
  * It can also take SAX events too so can be used by SAX clients as this object
  * implements the {@link ContentHandler} and {@link LexicalHandler} interfaces.
  * as well. This formatter performs typical document
  * formatting.  The XML declaration and processing instructions are
  * always on their own lines. An {@link OutputFormat} object can be
  * used to define how whitespace is handled when printing and allows various
  * configuration options, such as to allow suppression of the XML declaration,
  * the encoding declaration or whether empty documents are collapsed.</p>
  *
  * <p> There are <code>write(...)</code> methods to print any of the
  * standard DOM4J classes, including <code>Document</code> and
  * <code>Element</code>, to either a <code>Writer</code> or an
  * <code>OutputStream</code>.  Warning: using your own
  * <code>Writer</code> may cause the writer's preferred character
  * encoding to be ignored.  If you use encodings other than UTF8, we
  * recommend using the method that takes an OutputStream instead.
  * </p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author Joseph Bowbeer
  * @version $Revision: 1.3 $
  */
public class XMLFormatter extends XMLWriter {

    private static final boolean DEBUG = false;

    /** The Stack of namespaceStack written so far */
    private NamespaceStack namespaceStack = new NamespaceStack();
    private int indentLevel = 0;
    private ExchangerOutputFormat format = null;
    private int lineLength = -1;
    private boolean wrapText = false;

    boolean preserveMixedContent = false;
    boolean defaultTrimText = false;

//    public XMLFormatter( Writer writer, OutputFormat format) throws UnsupportedEncodingException {
//        super( new LineWriter( writer), format);
//		this.format = format;
//	    namespaceStack.push(Namespace.NO_NAMESPACE);
//    }

    public XMLFormatter( OutputStream out, ExchangerOutputFormat format) throws UnsupportedEncodingException {
        super( out, format);

	    this.format = format;
	    namespaceStack.push(Namespace.NO_NAMESPACE);
    }

    public XMLFormatter( ExchangerOutputFormat format) throws UnsupportedEncodingException {
        super( format);

	    this.format = format;
	    namespaceStack.push(Namespace.NO_NAMESPACE);
    }

    /**
     * Get an OutputStreamWriter, use preferred encoding.
     */
    protected Writer createWriter( OutputStream outStream, String encoding) throws UnsupportedEncodingException {
        return new LineWriter( super.createWriter( outStream, XMLUtilities.mapXMLEncodingToJava( encoding)));
    }

    public void setMaxLineLength( int length) {
        this.lineLength = length;
    }

    public int getMaxLineLength() {
        return this.lineLength;
    }

    /** Set the initial indentation level.  This can be used to output
      * a document (or, more likely, an element) starting at a given
      * indent level, so it's not always flush against the left margin.
      * Default: 0
      *
      * @param indentLevel the number of indents to start with
      */
    public void setIndentLevel(int indentLevel) {
        this.indentLevel = indentLevel;
    }

    public void setPreserveMixedContent( boolean preserve) {
        if (DEBUG) System.out.println( "XMLFormatter.setPreserveMixedContent( "+preserve+")");
        preserveMixedContent = preserve;
    }

    public boolean isPreserveMixedContent() {
        return preserveMixedContent;
    }

    public void setTrimText( boolean trim) {
        if (DEBUG) System.out.println( "XMLFormatter.setTrimText( "+trim+")");
        defaultTrimText = trim;
		format.setTrimText( trim);
    }

    public boolean isTrimText() {
        return defaultTrimText;
    }

    /**
     * <p>
     * This will write the declaration to the given Writer.
     *   Assumes XML version 1.0 since we don't directly know.
     * </p>
     */
    protected void writeDeclaration() throws IOException {
        String encoding = format.getEncoding();
        String version = format.getVersion();
        String standalone = format.getStandalone();

        // Only print of declaration is not suppressed
        if ( !format.isSuppressDeclaration()) {
            // Assume 1.0 version
            writer.write("<?xml version=\"" + version + "\"");

            if ( !format.isOmitEncoding()) {
                writer.write(" encoding=\"" + encoding + "\"");
            }

            if ( !format.isOmitStandalone()) {
                writer.write(" standalone=\"" + standalone + "\"");
            }

            writer.write("?>");

            println();
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) throws SAXException {
        if (DEBUG) System.out.println( "XMLFormatter.startElement( "+namespaceURI+", "+localName+", "+qName+", "+attributes+")");
        try {
            writePrintln();
            indent();
            writer.write("<");
            writer.write(qName);
            writeNamespaces();
            writeAttributes( attributes );
            writer.write(">");
            ++indentLevel;
            lastOutputNodeType = Node.ELEMENT_NODE;

            super.startElement( namespaceURI, localName, qName, attributes );
        }
        catch (IOException e) {
            handleException(e);
        }
    }

    public void endElement( String namespaceURI, String localName, String qName) throws SAXException {
        if (DEBUG) System.out.println( "XMLFormatter.endElement( "+namespaceURI+", "+localName+", "+qName+")");
        try {
            --indentLevel;
            if ( lastOutputNodeType == Node.ELEMENT_NODE ) {
                writePrintln();
                indent();
            }

            // XXXX: need to determine this using a stack and checking for
            // content / children
            boolean hadContent = true;
            if ( hadContent ) {
                writeClose(qName);
            }
            else {
                writeEmptyElementClose(qName);
            }
            lastOutputNodeType = Node.ELEMENT_NODE;

            super.endElement( namespaceURI, localName, qName );
        }
        catch (IOException e) {
            handleException(e);
        }
    }

    protected void writeDocType( DocumentType docType) throws IOException {
        if (DEBUG) System.out.println( "XMLFormatter.writeDocType( "+docType+")");

        if (docType != null) {
    		List list = docType.getInternalDeclarations();
    		
    		if ( list != null) {
    			for ( int i = 0; i < list.size(); i++) {
    				Object internalDecl = list.get( i);

    				if ( internalDecl instanceof InternalEntityDecl) {
    					InternalEntityDecl decl = (InternalEntityDecl)internalDecl;
    					String value = decl.getValue();
    					StringBuffer result = new StringBuffer( "&");
    
    					int index = value.indexOf( "&amp;");
    
    					if ( index != -1 && value.length() > 5) {
    						if ( index > 0) {
    							result.append( value.substring( 0, index+1));
    						}
    
    						result.append( value.substring( index+5, value.length()));
    
    						decl.setValue( result.toString());
    					}
    				} 
    			}
    		}

            docType.write( writer);

            writer.write( format.getLineSeparator());
            writer.write( format.getLineSeparator());
        }
    }

    protected void writeEntity( Entity entity) throws IOException {
    	String name = entity.getName();
    	if (DEBUG) System.out.println( "XMLFormatter.writeEntity( "+name+")");

    	if ( !name.equals( "amp")) { // Should never happen.
    	    writeEntityRef( entity.getName());
    	} else {
    		writer.write( "&");
    	}
    }

    protected void writeEntityRef( String name) throws IOException {
    	if (DEBUG) System.out.println( "XMLFormatter.writeEntityRef( "+name+")");
//		if ( !name.equals( "amp")) { // Should never happen.
//		    writeEntityRef( entity.getName());
//		} else {
//			writer.write( "&");
//		}
    }

    protected String escapeElementEntities( String text) {
    	if (DEBUG) System.out.println( "XMLFormatter.escapeElementEntities( "+text+")");
    	// No automatic entity escaping!
    	return text;
    }	

    protected String escapeAttributeEntities(String text) {
    	if (DEBUG) System.out.println( "XMLFormatter.escapeAttributeEntities( "+text+")");
    	// No automatic entity escaping!
    	return text;
    }


    // Implementation methods
    //-------------------------------------------------------------------------
    protected void writeElement(Element element) throws IOException {
		if (DEBUG) System.out.println( "XMLFormatter.writeElement( "+element+")");
//		if ( indentMixed) {
//			super.writeElement( element);
//		} else {
        int size = element.nodeCount();
        String qualifiedName = element.getQualifiedName();
		
		boolean hasElement = false;
		boolean hasText = false;

		// first test to see if this element has mixed content, 
		// if whitespace is significant ...
        for ( int i = 0; i < size; i++ ) {
            Node node = element.node(i);

			if ( node instanceof Element) {
                hasElement = true;
            } else if ( node instanceof Text) {
				String text = node.getText();
				
				if ( text != null && text.trim().length() > 0) {
	                hasText = true;
				}
            }
        }
		
	    Attribute space = element.attribute( "space");
		boolean preserveSpace = false;
		
		if ( space != null) {
			String prefix = space.getNamespacePrefix();
			String value = space.getValue();
//			System.out.println( "prefix = "+prefix+" value = "+value);
			if ( prefix != null && "xml".equals( prefix) && "preserve".equals( value)) {
				preserveSpace = true;
			}
		}
		
		writePrintln();
		indent();

        writer.write( "<");
        writer.write( qualifiedName);

        int previouslyDeclaredNamespaces = namespaceStack.size();

        Namespace ns = element.getNamespace();

        if ( isNamespaceDeclaration( ns ) ) {
            namespaceStack.push( ns);
            writeNamespace( ns);
        }

        // Print out additional namespace declarations
        for ( int i = 0; i < size; i++ ) {
            Node node = element.node(i);

            if ( node instanceof Namespace ) {
                Namespace additional = (Namespace)node;

                if ( isNamespaceDeclaration( additional)) {
                    namespaceStack.push( additional);
                    writeNamespace( additional);
                }
            } 
        }

        writeAttributes(element);

        lastOutputNodeType = Node.ELEMENT_NODE;

        if ( size <= 0 ) {
            writeEmptyElementClose(qualifiedName);
        } else {
            writer.write(">");
			
            if ( !hasElement && !preserveSpace) { // text only
                // we have at least one text node so lets assume
                // that its non-empty
//				System.out.println( "writeElementContent (Text) ...");
				boolean previousWrapText = wrapText;
                wrapText = true;
                writeElementContent(element);
	            wrapText = previousWrapText;
            } else if ( preserveMixedContent && hasElement && hasText) { // preserve space
                // Mixed content
//                System.out.println( "writeMixedElementContent ...");
                Node lastNode = writeMixedElementContent(element);
				
            } else if ( preserveSpace) { // preserve space
                // Mixed content
//                System.out.println( "writePreserveElementContent ...");
                Node lastNode = writePreservedElementContent(element);

            } else { // hasElement && !hasText
//	            System.out.println( "writeElementContent (Element) ...");

	            boolean previousWrapText = wrapText;
	            wrapText = true;
	            ++indentLevel;
	            writeElementContent(element);
	            --indentLevel;
	            wrapText = previousWrapText;

	            writePrintln();
	            indent();
            }

            writer.write("</");
            writer.write(qualifiedName);
            writer.write(">");
//			writePrintln();
        }

        // remove declared namespaceStack from stack
        while (namespaceStack.size() > previouslyDeclaredNamespaces) {
            namespaceStack.pop();
        }

        lastOutputNodeType = Node.ELEMENT_NODE;
//		}
    }


    protected void writeMixedElement( Element element) throws IOException {
        if (DEBUG) System.out.println( "XMLFormatter.writeMixedElement( "+element+")");

        int size = element.nodeCount();
        String qualifiedName = element.getQualifiedName();
		
        boolean previousTrim = format.isTrimText();
        boolean previousWrapText = wrapText;

        if ( isPreserveMixedContent()) {
	        format.setTrimText( false);
	        wrapText = false;
        } else {
	        wrapText = true;
        }

        writer.write("<");
        writer.write(qualifiedName);

        int previouslyDeclaredNamespaces = namespaceStack.size();
        Namespace ns = element.getNamespace();

        if (isNamespaceDeclaration( ns ) ) {
            namespaceStack.push(ns);
            writeNamespace(ns);
        }

        // Print out additional namespace declarations
        boolean textOnly = true;
        for ( int i = 0; i < size; i++ ) {
            Node node = element.node(i);
            if ( node instanceof Namespace ) {
                Namespace additional = (Namespace) node;
                if (isNamespaceDeclaration( additional ) ) {
                    namespaceStack.push(additional);
                    writeNamespace(additional);
                }
            }
            else if ( node instanceof Element) {
                textOnly = false;
            }
        }

        writeAttributes(element);

        lastOutputNodeType = Node.ELEMENT_NODE;

        if ( size <= 0 ) {
            writeEmptyElementClose(qualifiedName);
//	        writePrintln();
        }
        else {
            writer.write(">");
            writeMixedElementContent(element);
            writer.write("</");
            writer.write(qualifiedName);
            writer.write(">");
        }

        // remove declared namespaceStack from stack
        while (namespaceStack.size() > previouslyDeclaredNamespaces) {
            namespaceStack.pop();
        }

        lastOutputNodeType = Node.ELEMENT_NODE;

        format.setTrimText( previousTrim);
		wrapText = previousWrapText;
    }

    protected void writePreservedElement( Element element) throws IOException {
        if (DEBUG) System.out.println( "XMLFormatter.writePreservedElement( "+element+")");
        int size = element.nodeCount();
        String qualifiedName = element.getQualifiedName();

        boolean previousTrim = format.isTrimText();
		boolean previousWrapText = wrapText;

        format.setTrimText( false);
        wrapText = false;

        boolean hasElement = false;
        boolean hasText = false;

        // first test to see if this element has mixed content, 
        // if whitespace is significant ...
        for ( int i = 0; i < size; i++ ) {
            Node node = element.node(i);

        	if ( node instanceof Element) {
                hasElement = true;
            } else if ( node instanceof Text) {
        		String text = node.getText();
        		
        		if ( text != null && text.trim().length() > 0) {
                    hasText = true;
        		}
            }
        }
        
        Attribute space = element.attribute( "space");
        boolean defaultSpace = false;
        
        if ( space != null) {
        	String prefix = space.getNamespacePrefix();
        	String value = space.getValue();
//	      	System.out.println( "prefix = "+prefix+" value = "+value);
        	if ( prefix != null && "xml".equals( prefix) && "default".equals( value)) {
        		defaultSpace = true;
        	}
        }

        writer.write( "<");
        writer.write( qualifiedName);

        int previouslyDeclaredNamespaces = namespaceStack.size();
        Namespace ns = element.getNamespace();

        if (isNamespaceDeclaration( ns ) ) {
            namespaceStack.push(ns);
            writeNamespace(ns);
        }

        // Print out additional namespace declarations
        for ( int i = 0; i < size; i++ ) {
            Node node = element.node(i);
            if ( node instanceof Namespace ) {
                Namespace additional = (Namespace) node;
                if (isNamespaceDeclaration( additional ) ) {
                    namespaceStack.push(additional);
                    writeNamespace(additional);
                }
            }
        }

        writeAttributes(element);

        lastOutputNodeType = Node.ELEMENT_NODE;

        if ( size <= 0 ) {
            writeEmptyElementClose(qualifiedName);
        } else {
            writer.write(">");

			if ( preserveMixedContent && hasElement && hasText) { // mixed content
//                System.out.println( "writeMixedElementContent ...");

                Node lastNode = writeMixedElementContent( element);
            } else if ( !defaultSpace) { // preserve space
//                System.out.println( "writePreservedElementContent ...");

                Node lastNode = writePreservedElementContent( element);
            } else {
//                System.out.println( "writeElementContent ...");

                format.setTrimText( isTrimText());
                boolean prevWrapText = wrapText;
                wrapText = true;
                writeElementContent( element);
	            wrapText = prevWrapText;
	            format.setTrimText( false);
            }

            writer.write("</");
            writer.write(qualifiedName);
            writer.write(">");
        }

        // remove declared namespaceStack from stack
        while (namespaceStack.size() > previouslyDeclaredNamespaces) {
            namespaceStack.pop();
        }

        lastOutputNodeType = Node.ELEMENT_NODE;

        format.setTrimText( previousTrim);
		wrapText = previousWrapText;
    }

    /** Outputs the content of the given element. If whitespace trimming is
     * enabled then all adjacent text nodes are appended together before
     * the whitespace trimming occurs to avoid problems with multiple
     * text nodes being created due to text content that spans parser buffers
     * in a SAX parser.
     */
    protected Node writeMixedElementContent( Element element) throws IOException {
        if (DEBUG) System.out.println( "XMLFormatter.writeMixedElementContent( "+element+")");
        Node previousNode = null;

		boolean previousTrim = format.isTrimText();
		boolean previousWrapText = wrapText;

		if ( isPreserveMixedContent()) {
			format.setTrimText( false);
			wrapText = false;
		} else {
			wrapText = true;
		}

        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
            Node node = element.node( i);
            Node nextNode = null;
			
			if ( i+1 < size) {
				nextNode = element.node( i+1);
			}

			if ( node instanceof Text) {
				writeString( node.getText());
			} else {
                writeMixedNode( node);
			}
			
			previousNode = node;
        }

	    format.setTrimText( previousTrim);
	    wrapText = previousWrapText;

	    return previousNode;
    }
    protected void writeString( String text) throws IOException {
        if ( text != null && text.length() > 0 ) {
			if ( format.isTrimText()) {
                boolean first = true;
                StringTokenizer tokenizer = new StringTokenizer( text);
				
                while ( tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
					
                    if ( wrapText && lineLength != -1 && ((((LineWriter)writer).getColumn() + token.length()) > lineLength)) {
						indentLevel++;
						writePrintln();
						indent();
	                    indentLevel--;
						
						if ( first) {
							first = false;
						}
                    } else if ( first) {
                        first = false;

                        if ( lastOutputNodeType == Node.TEXT_NODE ) {
                            writer.write(" ");
                        } else if ( format.isPadText() && lastOutputNodeType == Node.ELEMENT_NODE) {
	                        writer.write(" ");
                        }
                    } else {
                        writer.write(" ");
                    }

                    writer.write(token);
                    lastOutputNodeType = Node.TEXT_NODE;
                }
            } else {
                lastOutputNodeType = Node.TEXT_NODE;
                writer.write(text);
            }
        }
    }

    protected Node writePreservedElementContent(Element element) throws IOException {
        if (DEBUG) System.out.println( "XMLFormatter.writePreservedElementContent( "+element+")");
        Node previousNode = null;

    	boolean previousTrim = format.isTrimText();
    	boolean previousWrapText = wrapText;
    	format.setTrimText( false);
    	wrapText = false;

        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
            Node node = element.node( i);
            Node nextNode = null;
    		
    		if ( i+1 < size) {
    			nextNode = element.node( i+1);
    		}

    		if ( node instanceof Text) {
    			String text = node.getText();
    			
    			// previous node was an empty element-node <element/>
    			writeString( text);
    		} else {
                writePreservedNode( node);
    		}
    		
    		previousNode = node;
        }

        format.setTrimText( previousTrim);
        wrapText = previousWrapText;

        return previousNode;
    }

    protected void writeMixedNode(Node node) throws IOException {
        if (DEBUG) System.out.println( "XMLFormatter.writeMixedNode( "+node+")");
        int nodeType = node.getNodeType();
        switch (nodeType) {
            case Node.ELEMENT_NODE:
                writeMixedElement((Element) node);
                break;
            case Node.ATTRIBUTE_NODE:
                writeAttribute((Attribute) node);
                break;
            case Node.TEXT_NODE:
                writeString(node.getText());
                //write((Text) node);
                break;
            case Node.CDATA_SECTION_NODE:
                writeCDATA(node.getText());
                break;
            case Node.ENTITY_REFERENCE_NODE:
                writeEntity((Entity) node);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                writeProcessingInstruction((ProcessingInstruction) node);
                break;
            case Node.COMMENT_NODE:
                writeComment(node.getText());
                break;
            case Node.DOCUMENT_NODE:
                write((Document) node);
                break;
            case Node.DOCUMENT_TYPE_NODE:
                writeDocType((DocumentType) node);
                break;
            case Node.NAMESPACE_NODE:
                // Will be output with attributes
                //write((Namespace) node);
                break;
            default:
                throw new IOException( "Invalid node type: " + node );
        }
    }
	
    protected void writePreservedNode(Node node) throws IOException {
        if (DEBUG) System.out.println( "XMLFormatter.writeMixedNode( "+node+")");
        int nodeType = node.getNodeType();
        switch (nodeType) {
            case Node.ELEMENT_NODE:
                writePreservedElement((Element) node);
                break;
            case Node.ATTRIBUTE_NODE:
                writeAttribute((Attribute) node);
                break;
            case Node.TEXT_NODE:
                writeString(node.getText());
                //write((Text) node);
                break;
            case Node.CDATA_SECTION_NODE:
                writeCDATA(node.getText());
                break;
            case Node.ENTITY_REFERENCE_NODE:
                writeEntity((Entity) node);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                writeProcessingInstruction((ProcessingInstruction) node);
                break;
            case Node.COMMENT_NODE:
                writeComment(node.getText());
                break;
            case Node.DOCUMENT_NODE:
                write((Document) node);
                break;
            case Node.DOCUMENT_TYPE_NODE:
                writeDocType((DocumentType) node);
                break;
            case Node.NAMESPACE_NODE:
                // Will be output with attributes
                //write((Namespace) node);
                break;
            default:
                throw new IOException( "Invalid node type: " + node );
        }
    }

    /** Writes the attributes of the given element
      *
      */
//    protected void writeAttributes( Element element ) throws IOException {
//	    if (DEBUG) System.out.println( "XMLFormatter.writeAttributes( "+element+")");
//	    if (indentMixed) {
//	    	super.writeAttributes( element);
//	    } else {

	        // I do not yet handle the case where the same prefix maps to
	        // two different URIs. For attributes on the same element
	        // this is illegal; but as yet we don't throw an exception
	        // if someone tries to do this
//	        for ( int i = 0, size = element.attributeCount(); i < size; i++ ) {
//	            Attribute attribute = element.attribute(i);
//	            Namespace ns = attribute.getNamespace();
//	            if (ns != null && ns != Namespace.NO_NAMESPACE && ns != Namespace.XML_NAMESPACE) {
//	                String prefix = ns.getPrefix();
//	                String uri = namespaceStack.getURI(prefix);
//	                if (!ns.getURI().equals(uri)) { // output a new namespace declaration
//	                    writeNamespace(ns);
//	                    namespaceStack.push(ns);
//	                }
//	            }
//
//	            writer.write(" ");
//	            writer.write(attribute.getQualifiedName());
//	            writer.write("=\"");
//	            writeEscapeAttributeEntities(attribute.getValue());
//	            writer.write("\"");
//	        }
////	    }
//    }
	
    protected void writeAttribute( Attribute attribute) throws IOException {
    	XAttribute a = (XAttribute)attribute;
    	
//    	if ( attributeOnNewLine) {
//    		++indentLevel;
//
//    		format.setNewlines( true);
//    		format.setIndent( indentString);
//    		
//    	    writePrintln();
//    	    indent();
//    	} else {
	    writer.write(" ");
//    	}

        writer.write(attribute.getQualifiedName());
        writer.write("=");
    	
    	String value = attribute.getValue();
    	
    	if ( format.isTrimText() && value != null && value.trim().length() > 0) {
    	    StringTokenizer tokenizer = new StringTokenizer( value);
			StringBuffer buffer = new StringBuffer();
			boolean first = true;
    		
    	    while ( tokenizer.hasMoreTokens()) {
    	        String token = tokenizer.nextToken();
				
				if ( !first) {
					buffer.append(" ");
				}

				buffer.append(token);
				first = false;
    	    }
			
			value = buffer.toString();
    	}

    	boolean hasQuote = value.indexOf( "\"") != -1;

    	if ( hasQuote) {
    	    writer.write("'");
    	    writeEscapeAttributeEntities( value);
    	    writer.write("'");
    	} else {
    		writer.write("\"");
    		writeEscapeAttributeEntities( value);
    		writer.write("\"");
    	}

        lastOutputNodeType = Node.ATTRIBUTE_NODE;

    	// reset...
//    	if ( attributeOnNewLine) {
//    		--indentLevel;
//
//    		format.setNewlines( false);
//    		format.setIndent( "");
//    	}
    }

    protected void writeAttributes( Element element ) throws IOException {

        // I do not yet handle the case where the same prefix maps to
        // two different URIs. For attributes on the same element
        // this is illegal; but as yet we don't throw an exception
        // if someone tries to do this
        for ( int i = 0, size = element.attributeCount(); i < size; i++ ) {
            Attribute attribute = element.attribute(i);
            Namespace ns = attribute.getNamespace();
            if (ns != null && ns != Namespace.NO_NAMESPACE && ns != Namespace.XML_NAMESPACE) {
                String prefix = ns.getPrefix();
                String uri = namespaceStack.getURI(prefix);
                if (!ns.getURI().equals(uri)) { // output a new namespace declaration
                    writeNamespace(ns);
                    namespaceStack.push(ns);
                }
            }
    		
    		writeAttribute( attribute);
        }
    }

    protected void writeNamespace(Namespace namespace) throws IOException {
        if ( namespace != null ) {
    		writeNamespace( namespace.getPrefix(), namespace.getURI());
        }
    }

    protected void writeNamespace( String prefix, String uri) throws IOException {
//        if ( attributeOnNewLine) {
//        	++indentLevel;
//
//        	format.setNewlines( true);
//        	format.setIndent( indentString);
//        	
//            writePrintln();
//            indent();
//        } else {
		writer.write(" ");
//        }

        if ( prefix != null && prefix.length() > 0 ) {
            writer.write("xmlns:");
            writer.write( prefix);
            writer.write("=\"");
        } else {
            writer.write("xmlns=\"");
        }

        writer.write( uri);
        writer.write("\"");

    	// reset...
//    	if ( attributeOnNewLine) {
//    		--indentLevel;
//
//    		format.setNewlines( false);
//    		format.setIndent( "");
//    	}
    }

    protected void indent() throws IOException {
	    if (DEBUG) System.out.println( "XMLFormatter.indent()");
//		if (indentMixed) {
//			super.indent();
//		} else {
	        String indent = getOutputFormat().getIndent();
	        if ( indent != null && indent.length() > 0 ) {
	            for ( int i = 0; i < indentLevel; i++ ) {
	                writer.write(indent);
	            }
	        }
//		}
    }

    protected boolean isNamespaceDeclaration( Namespace ns ) {
	    if (DEBUG) System.out.println( "XMLFormatter.isNamespaceDeclaration( "+ns+")");
//        if (indentMixed) {
//        	return super.isNamespaceDeclaration( ns);
//        } else if (ns != null && ns != Namespace.XML_NAMESPACE) {
            String uri = ns.getURI();
            if ( uri != null ) {
                if ( ! namespaceStack.contains( ns ) ) {
                    return true;

                }
            }
//        }
        return false;

    }
	
	private String trimStart( String string) {
		int len = string.length();
		char[] val = string.toCharArray();    /* avoid getfield opcode */
		int index = 0;

		while ((index < len) && (val[ index] <= ' ')) {
		    index++;
		}

		return (index > 0) ? string.substring( index, len) : string;
	}

	private String trimEnd( String string) {
		int len = string.length();
		char[] val = string.toCharArray();    /* avoid getfield opcode */

		while ((len > 0) && (val[len - 1] <= ' ')) {
		    len--;
		}

		return ( (len < string.length())) ? string.substring( 0, len) : string;
	}

	private String trimSpaces( String string) {
		StringBuffer buffer = new StringBuffer();
		int len = string.length();
		char[] val = string.toCharArray();    /* avoid getfield opcode */

		// trim spaces till first normal character.
		for ( int i = 0; i < val.length; i++) {
			if ( (val[ i] < ' ')) {
				buffer.append( val[ i]);
			} else if ( val[ i] > ' ') {
				break;
			}
		}
		
		buffer.append( string.trim());
		
		// trim spaces till last normal character.
		for ( int i = val.length; i > 0; i--) {
			if ( (val[ i-1] < ' ')) {
				buffer.append( val[ i-1]);
			} else if ( val[ i-1] > ' ') {
				break;
			}
		}

		return buffer.toString();
	}
	
	private class LineWriter extends Writer {
		private int column = 0;
		private Writer out;
		
		public LineWriter( Writer out) {
			this.out = out;
		}

		public int getColumn() {
			return column;
		}

		public void close() throws IOException {
			out.close();
		}
		
		public void flush() throws IOException {
			out.flush();
		}

		public void write( char[] cbuf, int off, int len) throws IOException {
			for ( int i = off; i < (off + len); i++) {
				write( cbuf[i]);
			}
		}

		public void write( int c) throws IOException {
			column++;

			if ( (char)c == '\n') {
				column = 0;
			}

			out.write( c);

//			if ( Character.isWhitespace( (char)c)) {
//				if ( (char)c == '\n') {
//					column = 0;
//
//					out.write( c);
//				} else if ( wrapText && lineLength != -1 && format.isNewlines() && column >= lineLength) {
//					column = 0;
//
//					writePrintln();
//
//					indentLevel++;
//					indent();
//					indentLevel--;
//				} else {
//					out.write( c);
//				}
//			} else {
//				out.write( c);
//			}
		}
		
		public void write( String s, int off, int len) throws IOException {
			write( s.toCharArray(), off, len);
		}

		public void write( char[] buf) throws IOException {
			write( buf, 0, buf.length);
		}

		public void write( String s) throws IOException {
			write( s.toCharArray(), 0, s.length());
		}
	}
}
