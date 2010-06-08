/*
 * $Id: ExchangerXMLWriter.java,v 1.4 2004/05/28 09:14:00 edankert Exp $
 *
 * Copyright (C) 2002-2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.dtd.ExternalEntityDecl;
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
  * @version $Revision: 1.4 $
  */
public class ExchangerXMLWriter extends XMLWriter {
	private static final boolean DEBUG = false;

	private ExchangerOutputFormat format = null;
	private Element prevElement = null;

	/** The Stack of namespaceStack written so far */
	private NamespaceStack namespaceStack = new NamespaceStack();
	private int indentLevel = 0;
	private boolean escapeEntities = false;

	private static String indentString = null;
	private static boolean attributeOnNewLine = false;

	private static int lineLength = -1;
	private boolean wrapText = false;

	private boolean elementFound = false;

    public ExchangerXMLWriter( OutputStream out, ExchangerOutputFormat format) throws UnsupportedEncodingException {
        super( out, format);

	    namespaceStack.push(Namespace.NO_NAMESPACE);

	    this.format = format;

	    format.setNewlines( true);
	    format.setIndent( "");
    }

    public ExchangerXMLWriter( ExchangerOutputFormat format) throws UnsupportedEncodingException {
        super( format);

		namespaceStack.push(Namespace.NO_NAMESPACE);

	    this.format = format;

	    format.setNewlines( true);
	    format.setIndent( "");
    }

    public ExchangerXMLWriter( Writer writer, ExchangerOutputFormat format) throws UnsupportedEncodingException {
        super( format);
		
		setWriter( new PositionedWriter( this, writer));
		
    	namespaceStack.push(Namespace.NO_NAMESPACE);

        this.format = format;

        format.setNewlines( true);
        format.setIndent( "");
    }
    
    public void setEscapeEntities( boolean escapeEntities) {
    	this.escapeEntities = escapeEntities;
    }
	
    public boolean isEscapeEntities() {
    	return escapeEntities;
    }

    public static void setAttributeOnNewLine( boolean enabled) {
		attributeOnNewLine = enabled;
	}

    /**
     * Sets the string to indent with.
     *
     * @sets the indent string.
     */
    public static void setIndentString( String string) {
    	indentString = string;
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

    /**
     * Get an OutputStreamWriter, use preferred encoding.
     */
    protected Writer createWriter( OutputStream outStream, String encoding) throws UnsupportedEncodingException {
        return new PositionedWriter( this, super.createWriter( outStream, XMLUtilities.mapXMLEncodingToJava( encoding)));
    }

    public static void setMaxLineLength( int length) {
        lineLength = length;
    }

    public static int getMaxLineLength() {
        return lineLength;
    }

    public boolean isWrapText() {
        return wrapText;
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

    public int getIndentLevel() {
        return indentLevel;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) throws SAXException {
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

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
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

    protected boolean isElementFound() throws IOException {
		return elementFound;
    }

    protected void setElementFound() throws IOException {
		if ( !elementFound) {
			writePrintln();
				
			format.setNewlines( false);
		}

	    elementFound = true;
    }

    // Implementation methods
    //-------------------------------------------------------------------------
    protected void writeElement(Element element) throws IOException {
		int prev = lastOutputNodeType;

		setElementFound();

		if ( element instanceof XElement) {
			XElement e = (XElement)element;

			if ( e.isParsed()) {
				format.setNewlines( false);
				format.setIndent( "");

				writeXElement( e);
			} else {
				format.setNewlines( true);
				format.setIndent( indentString);

				writeXElement( e);
				
				XElement p = (XElement)e.getParent();
				
				// The parent has set the formatting so the 
				// parent has to undo the settings as well
				if ( p != null && p.isParsed()) {
					format.setNewlines( false);
					format.setIndent( "");
				}
			}

			prevElement = element;
		}
    }
	
	// this is the same as super.writeElement...
	protected void writeXElement( XElement element) throws IOException {
	    int size = element.nodeCount();
	    String qualifiedName = element.getQualifiedName();

	    writePrintln();
	    indent();

	    element.setElementStartPosition( ((PositionedWriter)writer).getPosition());
//	    System.out.print( "["+((PositionedWriter)writer).getPosition()+"]");

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
	        } else if ( node instanceof Element) {
	            textOnly = false;
	        }
	    }

	    writeAttributes(element);

	    lastOutputNodeType = Node.ELEMENT_NODE;

	    if ( size <= 0 ) {
	        writeEmptyElementClose(qualifiedName);
	    } else {
	        writer.write(">");
			
	        element.setContentStartPosition( ((PositionedWriter)writer).getPosition());

	        if ( textOnly ) {
	            // we have at least one text node so lets assume
	            // that its non-empty
	            writeElementContent(element);
	        } else {
	            XElement[] elements = element.getElements();
	            boolean allNotParsed = true;
	            
	            for ( int i = 0; i < elements.length; i++) {
	            	if ( elements[i].isParsed()) {
	            		allNotParsed = false;
	            		break;
	            	}
	            }

	            // we know it's not null or empty from above
	            ++indentLevel;

	            writeElementContent(element);

	            --indentLevel;
				
				// All Children were not parsed so the end-tag has been newly created
				// ED: This could still be a previous element ending though!
				if ( allNotParsed) {
					format.setNewlines( true);
					format.setIndent( indentString);
				}

	            writePrintln();
	            indent();

		        if ( allNotParsed) { // reset
		        	format.setNewlines( false);
		        	format.setIndent( "");
		        }
	        }

	        element.setContentEndPosition( ((PositionedWriter)writer).getPosition());

	        writer.write("</");
	        writer.write(qualifiedName);
	        writer.write(">");
	    }

	    element.setElementEndPosition( ((PositionedWriter)writer).getPosition());
//		System.out.print( "["+((PositionedWriter)writer).getPosition()+"]");

	    // remove declared namespaceStack from stack
	    while (namespaceStack.size() > previouslyDeclaredNamespaces) {
	        namespaceStack.pop();
	    }

	    lastOutputNodeType = Node.ELEMENT_NODE;
	}

	/** Outputs the content of the given element. If whitespace trimming is
	 * enabled then all adjacent text nodes are appended together before
	 * the whitespace trimming occurs to avoid problems with multiple
	 * text nodes being created due to text content that spans parser buffers
	 * in a SAX parser.
	 */
	protected void writeElementContent( Element element) throws IOException {
	    CDATA lastCDATANode = null;
	    StringBuffer cdataBuffer = null;

	    if (format.isTrimText()) {
	        // concatenate adjacent text nodes together
	        // so that whitespace trimming works properly
	        Text lastTextNode = null;
	        StringBuffer textBuffer = null;
	        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
	            Node node = element.node(i);

	            if ( node instanceof Text ) {
	                if ( lastCDATANode != null ) {
	                    if ( cdataBuffer != null ) {
	                        writeCDATA( cdataBuffer.toString() );
	                        cdataBuffer = null;
	                    } else {
	                        writeCDATA( lastCDATANode.getText() );
	                    }
	                	
	                    lastCDATANode = null;
	                }

	                if ( lastTextNode == null ) {
	                    lastTextNode = (Text) node;
	                }
	                else {
	                    if (textBuffer == null) {
	                        textBuffer = new StringBuffer( lastTextNode.getText() );
	                    }
						textBuffer.append( ((Text) node).getText() );
	                }
	            } else if ( node instanceof CDATA) {
	                if ( lastTextNode != null ) {
	                    if ( textBuffer != null ) {
	                        writeString( textBuffer.toString() );
	                        textBuffer = null;
	                    } else {
	                        writeString( lastTextNode.getText() );
	                    }
	            		
	                    lastTextNode = null;
	                }

		            if ( lastCDATANode == null ) {
		                lastCDATANode = (CDATA) node;
		            } else {
		                if ( cdataBuffer == null) {
		                    cdataBuffer = new StringBuffer( lastCDATANode.getText());
		                }

		            	cdataBuffer.append( ((CDATA)node).getText());
		            }
	            } else {
	                if ( lastCDATANode != null ) {
	                    if ( cdataBuffer != null ) {
	                        writeCDATA( cdataBuffer.toString() );
	                        cdataBuffer = null;
	                    } else {
	                        writeCDATA( lastCDATANode.getText() );
	                    }
	                	
	                    lastCDATANode = null;
	                }

	                if ( lastTextNode != null ) {
	                    if ( textBuffer != null ) {
	                        writeString( textBuffer.toString() );
	                        textBuffer = null;
	                    } else {
	                        writeString( lastTextNode.getText() );
	                    }
						
	                    lastTextNode = null;
	                }

	                writeNode(node);
	            }
	        }

	        if ( lastCDATANode != null ) {
	            if ( cdataBuffer != null ) {
	                writeCDATA( cdataBuffer.toString() );
	                cdataBuffer = null;
	            } else {
	                writeCDATA( lastCDATANode.getText() );
	            }
	        	
	            lastCDATANode = null;
	        }

	        if ( lastTextNode != null ) {
	            if ( textBuffer != null ) {
	                writeString( textBuffer.toString() );
	                textBuffer = null;
	            }
	            else {
	                writeString( lastTextNode.getText() );
	            }

	            lastTextNode = null;
	        }
	    }
	    else {
	        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
	            Node node = element.node(i);
	            
				if ( node instanceof CDATA) {
	                if ( lastCDATANode == null ) {
	                    lastCDATANode = (CDATA) node;
	                } else {
	                    if ( cdataBuffer == null) {
	                        cdataBuffer = new StringBuffer( lastCDATANode.getText());
	                    }

	                	cdataBuffer.append( ((CDATA)node).getText());
	                }
				} else {
		            if ( lastCDATANode != null ) {
		                if ( cdataBuffer != null ) {
		                    writeCDATA( cdataBuffer.toString() );
		                    cdataBuffer = null;
		                } else {
		                    writeCDATA( lastCDATANode.getText() );
		                }
		            	
		                lastCDATANode = null;
		            }

		            writeNode(node);
				}
	        }

		    if ( lastCDATANode != null ) {
		        if ( cdataBuffer != null ) {
		            writeCDATA( cdataBuffer.toString() );
		            cdataBuffer = null;
		        } else {
		            writeCDATA( lastCDATANode.getText() );
		        }
		    	
		        lastCDATANode = null;
		    }
	    }
	}

	protected void writeDocType( DocumentType docType) throws IOException {
	    if (DEBUG) System.out.println( "ExchangerXMLWriter.writeDocType( "+docType+")");

	    if (docType != null) {
			List list = docType.getInternalDeclarations();
			
			if ( list != null) {
				for ( int i = 0; i < list.size(); i++) {
					Object internalDecl = list.get( i);
					
					if ( internalDecl instanceof InternalEntityDecl) {
						InternalEntityDecl decl = (InternalEntityDecl)internalDecl;
//						System.out.println( "<!ENTITY "+decl.getName()+" \""+decl.getValue()+"\">");
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
					} else if ( internalDecl instanceof ExternalEntityDecl) {
						ExternalEntityDecl decl = (ExternalEntityDecl)internalDecl;

//						System.out.println( "<!ENTITY "+decl.getName()+" \""+decl.getPublicID()+"\" \""+decl.getSystemID()+"\">");

//						System.out.println( internalDecl.getClass());
//						System.out.println( internalDecl.toString());
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
		if (DEBUG) System.out.println( "ExchangerXMLWriter.writeEntity( "+name+")");

		if ( !name.equals( "amp")) { // Should never happen.
		    writeEntityRef( entity.getName());
		} else {
			writer.write( "&");
		}
	}

	protected void writeEntityRef( String name) throws IOException {
		if (DEBUG) System.out.println( "ExchangerXMLWriter.writeEntityRef( "+name+")");
//		if ( !name.equals( "amp")) { // Should never happen.
//		    writeEntityRef( entity.getName());
//		} else {
//			writer.write( "&");
//		}
	}

	protected String escapeElementEntities( String text) {
		if (DEBUG) System.out.println( "ExchangerXMLWriter.escapeElementEntities( "+text+")");
		
		if ( escapeEntities) { 
			return super.escapeElementEntities( text);
		} else {
			// No automatic entity escaping!
			return text;
		}
	}	

	protected String escapeAttributeEntities(String text) {
		if (DEBUG) System.out.println( "ExchangerXMLWriter.escapeAttributeEntities( "+text+")");

		if ( escapeEntities) { 
			return super.escapeAttributeEntities( text);
		} else {
			// No automatic entity escaping!
			return text;
		}
	}

	protected void writeAttribute( Attribute attribute) throws IOException {
		StringBuffer buffer = new StringBuffer();
		XAttribute a = (XAttribute)attribute;
		
		buffer.append( attribute.getQualifiedName());
	    buffer.append("=");
		
		String value = attribute.getValue();
		boolean hasQuote = value.indexOf( "\"") != -1;
		
		if ( hasQuote) {
		    buffer.append("'");
		    buffer.append( value);
		    buffer.append("'");
		} else {
			buffer.append("\"");
			buffer.append( value);
			buffer.append("\"");
		}
		
		String attr = buffer.toString();
		
//		System.out.println( "Wrap ["+isWrapText()+", "+getMaxLineLength()+", "+((PositionedWriter)writer).getColumn()+", "+attr.length()+"]");

		if ( getMaxLineLength() != -1 && (((PositionedWriter)writer).getColumn() + attr.length() + 1) >= getMaxLineLength()) {
//			System.out.println( "Wrapping ... ["+attr+"]");
			++indentLevel;

			format.setNewlines( true);
			format.setIndent( indentString);
			
		    writePrintln();
		    indent();

			--indentLevel;

			format.setNewlines( false);
			format.setIndent( "");
		} else {
		    writer.write( " ");
		}

	    a.setAttributeStartPosition( ((PositionedWriter)writer).getPosition());
		
		writer.write( attr);

	    a.setAttributeEndPosition( ((PositionedWriter)writer).getPosition());

	    lastOutputNodeType = Node.ATTRIBUTE_NODE;
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
		StringBuffer buffer = new StringBuffer();

        if ( prefix != null && prefix.length() > 0 ) {
            buffer.append("xmlns:");
            buffer.append( prefix);
            buffer.append("=\"");
        } else {
            buffer.append("xmlns=\"");
        }

        buffer.append( uri);
        buffer.append("\"");

		String namespace = buffer.toString();
		
//		System.out.println( "Namespace Wrap ["+isWrapText()+", "+getMaxLineLength()+", "+((PositionedWriter)writer).getColumn()+", "+namespace.length()+"]");

		if ( getMaxLineLength() != -1 && (((PositionedWriter)writer).getColumn() + namespace.length() + 1) >= getMaxLineLength()) {
//			System.out.println( "Namespace Wrapping ... ["+namespace+"]");
			++indentLevel;

			format.setNewlines( true);
			format.setIndent( indentString);
			
		    writePrintln();
		    indent();

			--indentLevel;

			format.setNewlines( false);
			format.setIndent( "");
		} else {
		    writer.write( " ");
		}

		writer.write( namespace);
	}

	/**
	 * Writes the SAX namepsaces
	 */
//	protected void writeNamespaces() throws IOException {
//		System.out.println( "writeNamespaces");

//	    if ( namespacesMap != null ) {
//	        for ( Iterator iter = namespacesMap.entrySet().iterator(); iter.hasNext(); ) {
//	            Map.Entry entry = (Map.Entry) iter.next();
//	            String prefix = (String) entry.getKey();
//	            String uri = (String) entry.getValue();
//				
//				writeNamespace( prefix, uri);
//	        }
//
//	        namespacesMap = null;
//	    }
//	}

	protected void indent() throws IOException {
	    String indent = format.getIndent();
	    if ( indent != null && indent.length() > 0 ) {
	        for ( int i = 0; i < indentLevel; i++ ) {
	            writer.write(indent);
	        }
	    }
	}

	protected boolean isNamespaceDeclaration( Namespace ns ) {
	    if (ns != null && ns != Namespace.XML_NAMESPACE) {
	        String uri = ns.getURI();
	        if ( uri != null ) {
	            if ( ! namespaceStack.contains( ns ) ) {
	                return true;

	            }
	        }
	    }
	    return false;
	}

	private static class PositionedWriter extends Writer {
		private ExchangerXMLWriter parent = null;
		private int pos = 0;
		private int column = 0;
		private Writer out;
		
		public PositionedWriter( ExchangerXMLWriter parent, Writer out) {
			this.out = out;
			this.parent = parent;
		}

		public int getPosition() {
			return pos;
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
//			System.out.print( "["+((char)c)+"]");
			pos++;
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
