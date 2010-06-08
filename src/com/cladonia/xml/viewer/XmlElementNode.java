/*
 * $Id: XmlElementNode.java,v 1.6 2004/09/09 15:53:48 knesbitt Exp $
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
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */
package com.cladonia.xml.viewer;

import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.Text;

import com.cladonia.xml.XElement;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The node for the XML tree, containing an XML element.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/09/09 15:53:48 $
 * @author Dogsbay
 */
public class XmlElementNode extends DefaultMutableTreeNode {
	private static final int MAX_LINE_LENGTH = 80;
	private XElement element = null;
	private static Viewer _viewer = null;
	private Line[] lines = null;
	private static final String XNGR_DUMMY_ROOT = "xngr_dummy_element";
	
	private boolean isEndTag = false;

	/**
	 * Constructs the node for the XML element.
	 *
	 * @param element the XML element.
	 */	
	public XmlElementNode( Viewer viewer, XElement element) {
		this( viewer, element, false);
	}
	
	/**
	 * Constructs the the XML element node.
	 *
	 * @param element the XML element.
	 */	
	public XmlElementNode( Viewer viewer, XElement element, boolean end) {
		this.element = element;
		_viewer = viewer;
		
		isEndTag = end;
		
		if ( !isEndTag()) {
			if (!isMixed2(element) || !_viewer.showInline()) {
				for ( int i = 0; i < element.nodeCount(); i++) {
					Node node = element.node( i);
					
					if ( node instanceof XElement) {
						add( new XmlElementNode( _viewer, (XElement)node));
					} 
					else if ( (node instanceof Comment) && _viewer.showComments()) {
						add( new XmlCommentNode( _viewer, (Comment)node));
					}
					else if ( (node instanceof ProcessingInstruction) && _viewer.showPI()) {
						add( new XmlProcessingInstructionNode( _viewer, (ProcessingInstruction)node));
					}
					else if ( (node instanceof CDATA) && _viewer.showValues()) {
						add( new XmlCDATANode( _viewer, (CDATA)node));
					}
					else if ( (node instanceof Text) && _viewer.showValues()) {
						if (!isWhiteSpace(node))
						{
							if (!hasTextOnly((XElement)node.getParent()))
							{
								add( new XmlTextNode( _viewer, (Text)node));
							}
						}
					}
				}
				
				
				// check to see if we need to add end element
				if (!hasTextOnly(element) && element.hasContent() 
					&& !element.getName().equals(XNGR_DUMMY_ROOT))
				{
					add( new XmlElementNode( _viewer, element, true));
				}
			} 
		}
	
		format();
	}
	


	/**
	 * Constructs the the XML element node.
	 *
	 * @param the XML comment element.
	 */	
	public XmlElementNode( Viewer viewer) {
		_viewer = viewer;
	}

	public boolean isEndTag() {
		return isEndTag;
	}
	
	public void update() {
		for ( int i = 0; i < getChildCount(); i++) {
			XmlElementNode node = (XmlElementNode)getChildAt( i);
			node.update();
		}
		
		format();
	}
	
	/**
	 * Returns the formatted lines for this element.
	 *
	 * @return the formatted Lines.
	 */	
	public Line[] getLines() {
		return lines;
	}
	
	/**
	 * Returns the viewer.
	 *
	 * @return the viewer.
	 */	
	public Viewer getViewer() {
		return _viewer;
	}

	/**
	 * Constructs the node for the XML element.
	 *
	 * @param element the XML element.
	 */	
	public XElement getElement() {
		return element;
	}
	
	private void format() {
		Vector lines = new Vector();
		Line current = new Line();
		lines.add( current);
		
		if ( isEndTag()) {
			current = parseEndTag( lines, current, element);
		} else {
			current = parseElement( lines, current, element);
		}

		this.lines = new Line[lines.size()];
		
		for ( int i = 0; i < lines.size(); i++) {
			this.lines[i] = (Line)lines.elementAt(i);
		}
	}

	protected Line parseElement( Vector lines, Line current, XElement elem) {

		if (isMixed2(element) && _viewer.showInline())
		{
			current = parseMixedElement2( lines, current, elem);

			// decide if we need to display the end tag
			if (requiresEndTag(elem))
			{
				current = parseEndTag( lines, current, elem);
			}
		} 
		else 
		{
			current = parseStartTag( lines, current, elem);

			if ( _viewer.showValues()) 
			{
				if (hasTextOnly(elem))
				{
					current = parseContent( lines, current, getContent(elem));
					current = parseEndTag( lines, current, elem);
				}
			}
		}
		
		return current;
	}
	
	private String getContent(XElement elem)
	{
		Iterator iterator = elem.nodeIterator();
		StringBuffer buf = new StringBuffer();
		
		while (iterator.hasNext())
		{
			Node node = (Node)iterator.next();
			if (node instanceof Text)
			{
				buf.append(node.asXML());
			}
		}
	
		return buf.toString().trim();
	}
	
//	 Elements parsed here can be both mixed and normal but then contained in a mixed element...
	protected Line parseMixedElement2( Vector lines, Line current, XElement elem) {
		
		current = parseStartTag( lines, current, elem);

		if (isMixed2( elem)) 
		{
			
			int previousText = 0;
			
			for ( int i = 0; i < elem.nodeCount(); i++) 
			{
				Node node = elem.node( i);
				
				if ( node instanceof XElement) 
				{
					previousText = 0;
					
					current = parseMixedElement2( lines, current, (XElement)node);
					XElement ele = (XElement)node;
					if (isMixed2(ele))
					{
						if (requiresEndTag(ele))
						{
							current = parseEndTag( lines, current, (XElement)node);
						}
					}
				} 
				else if ( (node instanceof Text) || (node instanceof Entity)) 
				{
					if (_viewer.showValues() && !isWhiteSpace(node))
					{
						String text = node.getText().trim();
						
						if (!"".equals(text))
						{
							if (previousText == 1)
							{
								text = " "+text;
							}
							previousText = 1;
						}
		
						current = parseContent( lines, current, text);
					}
				} 
				else if ( (node instanceof Comment) && _viewer.showComments()) 
				{
					previousText = 0;
					current = parseComment( lines, current, (Comment)node);
				}
				else if ( (node instanceof CDATA) && _viewer.showValues()) 
				{
					previousText = 0;
					current = parseCDATA( lines, current, (CDATA)node);
				}
				else if ( (node instanceof ProcessingInstruction) && _viewer.showPI()) 
				{
					previousText = 0;
					current = parseProcessingInstruction( lines, current, (ProcessingInstruction)node);
				}
			}
		} 
		else 
		{
			List elements = (List)elem.elements();

			if ( elements != null && elements.size() > 0) {
				Iterator iterator = elements.iterator();
				
				while ( iterator.hasNext()) {
					current = parseMixedElement2( lines, current, (XElement)iterator.next());
				}
				current = parseEndTag( lines, current, elem);
			} 
			else if ( elem.hasContent()) {
				if (_viewer.showValues())
				{
					current = parseContent( lines, current, elem.getText());
					if (requiresEndTag(elem))
					{
						current = parseEndTag( lines, current, elem);
					}
				}
			}
		}
		
		
		return current;
	}
	
	
	// Elements parsed here can be both mixed and normal but then contained in a mixed element...
	protected Line parseMixedElement( Vector lines, Line current, XElement elem) {

		current = parseStartTag( lines, current, elem);

		if ( _viewer.showValues()) {
			if ( isMixed( elem)) {
				for ( int i = 0; i < elem.nodeCount(); i++) {
					Node node = elem.node( i);
					
					if ( node instanceof XElement) {
						current = parseMixedElement( lines, current, (XElement)node);
					} else if ( (node instanceof Text) || (node instanceof Entity)) {
						String text = node.getText();
						current = parseContent( lines, current, text);
					} else if ( (node instanceof Comment) && _viewer.showComments()) {
						current = parseComment( lines, current, (Comment)node);
					}
				}
			} else {
				List elements = (List)elem.elements();

				if ( elements != null && elements.size() > 0) {
					Iterator iterator = elements.iterator();
					
					while ( iterator.hasNext()) {
						current = parseMixedElement( lines, current, (XElement)iterator.next());
					}

					current = parseEndTag( lines, current, elem);
				} else if ( elem.hasContent()) {
					current = parseContent( lines, current, elem.getText());
					current = parseEndTag( lines, current, elem);
				}
			}
		}
		
		return current;
	}

	protected Line parseComment( Vector lines, Line current, Comment comment) {
		StyledElement styledElement = new StyledElement();
		styledElement.addString( COMMENT_START);
		
		current.addStyledElement( styledElement);
		current = parseCommentContent( lines, current, comment.getText());
		
		styledElement = new StyledElement();
		styledElement.addString( COMMENT_END);
		current.addStyledElement( styledElement);
		
		return current;
	}
	
	protected Line parseProcessingInstruction( Vector lines, Line current, ProcessingInstruction pi) {
		StyledElement styledElement = new StyledElement();
		styledElement.addString(PI_START);
		
		current.addStyledElement( styledElement);
		current = parseProcessingInstructionContent( lines, current, pi);
		
		styledElement = new StyledElement();
		styledElement.addString(PI_END);
		current.addStyledElement( styledElement);
		
		return current;
	}
	
	protected Line parseCDATA( Vector lines, Line current, CDATA cdata) {
		StyledElement styledElement = new StyledElement();
		styledElement.addString(new CDATA_SECTION(cdata.asXML()));
		current.addStyledElement( styledElement);
		return current;
	}
	
	protected Line parseTextNode( Vector lines, Line current, Text textNode) 
	{
		current = parseContent(lines,current,textNode.asXML().trim());
		return current;
	}

	// Create a styled version of the start-tag.
	protected Line parseStartTag( Vector lines, Line current, XElement elem) {
		StyledElement styledElement = new StyledElement();
		styledElement.addString( OPEN_BRACKET);
		
		if ( _viewer.showNamespaces()) {
			String prefix = elem.getNamespacePrefix();
		
			if ( prefix != null && prefix.length() > 0) {
				styledElement.addString( new ElementPrefix( prefix));
				styledElement.addString( ELEMENT_COLON);
			}
		}

		styledElement.addString( new ElementName( elem.getName()));
		current.addStyledElement( styledElement);

		if ( _viewer.showNamespaces()) {
			Namespace ns = elem.getNamespace();

			if ( ns != null) {
				XElement parent = (XElement)elem.getParent();
			
				if ( parent != null) {
					Namespace prev = parent.getNamespaceForPrefix( ns.getPrefix());

					if ( prev == null || !ns.equals( prev)) {
						StyledElement sns = formatNamespace( ns);
						
						if ( sns != null) {
							if ( current.length()+sns.length()+1 > MAX_LINE_LENGTH) {
								current = new Line();
								lines.add( current);
								current.addStyledString( TAB);
							} else {
								current.addStyledString( SPACE);
							}
						
							current.addStyledElement( sns);
						}
					} 
				} else {
					StyledElement sns = formatNamespace( ns);
					
					if ( sns != null) {
						if ( current.length()+sns.length()+1 > MAX_LINE_LENGTH) {
							current = new Line();
							lines.add( current);
							current.addStyledString( TAB);
						} else {
							current.addStyledString( SPACE);
						}
					
						current.addStyledElement( sns);
					}
				}
			}

			List namespaces = elem.additionalNamespaces();

			if ( namespaces != null && namespaces.size() > 0) {
				Iterator iterator = namespaces.iterator();
			
				for ( int i = 0; i < namespaces.size(); i++) {
					StyledElement sns = formatNamespace( (Namespace)iterator.next());
					
					if ( sns != null) {
						if ( current.length()+sns.length()+1 > MAX_LINE_LENGTH) {
							current = new Line();
							lines.add( current);
							current.addStyledString( TAB);
						} else {
							current.addStyledString( SPACE);
						}
					
						current.addStyledElement( sns);
					}
				}
			}
		}

		if ( _viewer.showAttributes()) {
			List attributes = elem.attributes();

			if ( attributes != null && attributes.size() > 0) {
				Iterator iterator = attributes.iterator();
			
				for ( int i = 0; i < attributes.size(); i++) {
					StyledElement sa = formatAttribute( (Attribute)iterator.next());
					
					if ( current.length()+sa.length()+1 > MAX_LINE_LENGTH) {
						current = new Line();
						lines.add( current);
						current.addStyledString( TAB);
					} else {
						current.addStyledString( SPACE);
					}
				
					current.addStyledElement( sa);
				}
			}
		}

		if ( !elem.hasContent()) {
			current.addStyledString( SLASH);
		} 
		else if ( hasTextOnly(elem) && !_viewer.showValues()) {
			current.addStyledString( SLASH);
		} 
		else if (!requiresEndTag(elem) && _viewer.showInline())
		{
			current.addStyledString( SLASH);
		}
		
//		else if (_viewer.showInline())
//		{
//			if (!_viewer.showValues())
//			{
//				if ( hasTextOrCDATAOnly(elem))
//				{
//					current.addStyledString( SLASH);
//				}
//				else if (hasComment(elem) && !_viewer.showComments() && !hasProcessingInstruction(elem) 
//						&& !hasEntity(elem))
//				{
//					current.addStyledString( SLASH);
//				}
//				else if (hasProcessingInstruction(elem) && !_viewer.showPI() && !hasComment(elem) 
//						&& !hasEntity(elem))
//				{
//					current.addStyledString( SLASH);
//				}
//				else if (hasComment(elem) && hasProcessingInstruction(elem) && !_viewer.showComments() 
//						&& !hasProcessingInstruction(elem) && !hasEntity(elem))
//				{
//					current.addStyledString( SLASH);
//				}
//			}
//			else
//			{
//				if (!hasTextOrCDATA(elem))
//				{
//					if (hasComment(elem) && !_viewer.showComments() && !hasProcessingInstruction(elem) 
//							&& !hasEntity(elem))
//					{
//						current.addStyledString( SLASH);
//					}
//					else if (hasProcessingInstruction(elem) && !_viewer.showPI() && !hasComment(elem) 
//							&& !hasEntity(elem))
//					{
//						current.addStyledString( SLASH);
//					}
//					else if (hasComment(elem) && hasProcessingInstruction(elem) && !_viewer.showComments() 
//							&& !hasProcessingInstruction(elem) && !hasEntity(elem))
//					{
//						current.addStyledString( SLASH);
//					}
//				}
//			}
//		}
		
		current.addStyledString( CLOSE_BRACKET);
		
		return current;
	}
	
	private boolean requiresEndTag(XElement elem)
	{
		if (elem.hasChildElements())
		{
			return true;
		}
		
		if (!_viewer.showValues())
		{
			if ( hasTextOrCDATAOnly(elem))
			{
				return false;
			}
			else if (hasComment(elem) && !_viewer.showComments() && !hasProcessingInstruction(elem) 
					&& !hasEntity(elem))
			{
				return false;
			}
			else if (hasProcessingInstruction(elem) && !_viewer.showPI() && !hasComment(elem) 
					&& !hasEntity(elem))
			{
				return false;
			}
			else if (hasComment(elem) && hasProcessingInstruction(elem) && !_viewer.showComments() 
					&& !_viewer.showPI() && !hasEntity(elem))
			{
				return false;
			}
		}
		else
		{
			if (!hasTextOrCDATA(elem))
			{
				if (hasComment(elem) && !_viewer.showComments() && !hasProcessingInstruction(elem) 
						&& !hasEntity(elem))
				{
					return false;
				}
				else if (hasProcessingInstruction(elem) && !_viewer.showPI() && !hasComment(elem) 
						&& !hasEntity(elem))
				{
					return false;
				}
				else if (hasComment(elem) && hasProcessingInstruction(elem) && !_viewer.showComments() 
						&& !_viewer.showPI() && !hasEntity(elem))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	// Create a styled version of the element content.
	protected Line parseContent( Vector lines, Line current, String text) {

		if ( (current.length()+1 >= MAX_LINE_LENGTH) && (text.length() > 0)) {
			current = new Line();
			lines.add( current);
			current.addStyledString( TAB);
		}

		if ( text.length() > 0) {
			boolean parsed = false;
			
			while ( !parsed) {
				int length = MAX_LINE_LENGTH - (current.length()+1);
				
				if ( length > text.length()) {
					int index = 0;

					if ( text.indexOf( "\n") != -1) {
						index = text.indexOf( "\n");
					} else if ( text.indexOf( "\r") != -1) {
						index = text.indexOf( "\r");
					} else {
						index = text.length();
					}
					
					if ( index != 0) {
						String string = text.substring( 0, index);
						current.addStyledString( new ElementValue( string));
					}

					if ( index == text.length()) {
						parsed = true;
					} else {
						text = text.substring( index + 1, text.length());
					}
				} else {
					int index = 0;
					String sub = text.substring( 0, length);

					if ( sub.indexOf( "\n") != -1) {
						index = sub.indexOf( "\n");
					} else if ( sub.indexOf( "\r") != -1) {
						index = sub.indexOf( "\r");
					} else if ( sub.lastIndexOf( " ") != -1) {
						index = sub.lastIndexOf( " ");
					} 
					
					if ( index != 0) {
						String string = sub.substring( 0, index);
						current.addStyledString( new ElementValue( string));

						text = text.substring( index + 1, text.length());
					} else { // Text is too long without any whitespaces...
						int nlindex = text.indexOf( "\n");
						int rindex = text.indexOf( "\r");
						int spindex = sub.indexOf( " ");
						
						if ( nlindex == -1) {
							nlindex = Integer.MAX_VALUE;
						}
						if ( rindex == -1) {
							rindex = Integer.MAX_VALUE;
						}
						if ( spindex == -1) {
							spindex = Integer.MAX_VALUE;
						}
						
						index = Math.min( nlindex, rindex);
						index = Math.min( index, spindex);
						index = Math.min( index, text.length());

						String string = text.substring( 0, index);
						current.addStyledString( new ElementValue( string));

						if ( index == text.length()) {
							parsed = true;
						} else {
							text = text.substring( index + 1, text.length());
						}
					}
				}

				if ( !parsed) {
					current = new Line();
					lines.add( current);
					current.addStyledString( TAB);
				}
			}
		}
		
		return current;
	}
	
	protected Line parseProcessingInstructionContent(Vector lines, Line current, ProcessingInstruction pi)
	{
		String target = pi.getTarget();
		
		if ( (current.length()+1 >= MAX_LINE_LENGTH) && (target.length() > 0)) {
			current = new Line();
			lines.add( current);
			current.addStyledString( TAB);
		}
		
		if (target.length() > 0) 
		{
			// get the PI Target
			current.addStyledString( new PI_Target(target));
		}
		
		String text = pi.getText();
		if (text.length() > 0)
		{
			// get the PI text
			current.addStyledString( SPACE);
			current.addStyledString( new PI_Text(text));
		}
		
		return current;
	}
	
	protected Line parseCommentContent( Vector lines, Line current, String text) {

		if ( (current.length()+1 >= MAX_LINE_LENGTH) && (text.length() > 0)) {
			current = new Line();
			lines.add( current);
			current.addStyledString( TAB);
		}

		if ( text.length() > 0) {
			boolean parsed = false;
			
			while ( !parsed) {
				int length = MAX_LINE_LENGTH - (current.length()+1);
				
				if ( length > text.length()) {
					int index = 0;

					if ( text.indexOf( "\n") != -1) {
						index = text.indexOf( "\n");
					} else if ( text.indexOf( "\r") != -1) {
						index = text.indexOf( "\r");
					} else {
						index = text.length();
					}
					
					if ( index != 0) {
						String string = text.substring( 0, index);
						current.addStyledString( new CommentText( string));
					}

					if ( index == text.length()) {
						parsed = true;
					} else {
						text = text.substring( index + 1, text.length());
					}
				} else {
					int index = 0;
					String sub = text.substring( 0, length);

					if ( sub.indexOf( "\n") != -1) {
						index = sub.indexOf( "\n");
					} else if ( sub.indexOf( "\r") != -1) {
						index = sub.indexOf( "\r");
					} else if ( sub.lastIndexOf( " ") != -1) {
						index = sub.lastIndexOf( " ");
					} 
					
					if ( index != 0) {
						String string = sub.substring( 0, index);
						current.addStyledString( new CommentText( string));

						text = text.substring( index + 1, text.length());
					} else { // Text is too long without any whitespaces...
						int nlindex = text.indexOf( "\n");
						int rindex = text.indexOf( "\r");
						int spindex = sub.indexOf( " ");
						
						if ( nlindex == -1) {
							nlindex = Integer.MAX_VALUE;
						}
						if ( rindex == -1) {
							rindex = Integer.MAX_VALUE;
						}
						if ( spindex == -1) {
							spindex = Integer.MAX_VALUE;
						}
						
						index = Math.min( nlindex, rindex);
						index = Math.min( index, spindex);
						index = Math.min( index, text.length());

						String string = text.substring( 0, index);
						current.addStyledString( new CommentText( string));

						if ( index == text.length()) {
							parsed = true;
						} else {
							text = text.substring( index + 1, text.length());
						}
					}
				}

				if ( !parsed) {
					current = new Line();
					lines.add( current);
					current.addStyledString( TAB);
				}
			}
		}
		
		return current;
	}

	// Create a styled version of the end-tag.
	protected Line parseEndTag( Vector lines, Line current, Element elem) {
		
		StyledElement styledEnd = new StyledElement();
		styledEnd.addString( OPEN_BRACKET);
		styledEnd.addString( SLASH);
		
		if ( _viewer.showNamespaces()) {
			String prefix = elem.getNamespacePrefix();
		
			if ( prefix != null && prefix.length() > 0) {
				styledEnd.addString( new ElementPrefix( prefix));
				styledEnd.addString( ELEMENT_COLON);
			}
		}

		styledEnd.addString( new ElementName( elem.getName()));
		styledEnd.addString( CLOSE_BRACKET);
		current.addStyledElement( styledEnd);
		
		return current;
	}

	private StyledElement formatAttribute( Attribute a) {
		StyledElement styledAttribute = new StyledElement();
		
		String name = a.getName();
		String value = a.getValue();

		if ( _viewer.showNamespaces()) {
			String prefix = a.getNamespacePrefix();

			if ( prefix != null && prefix.length() > 0) {
				styledAttribute.addString( new AttributePrefix( prefix));
				styledAttribute.addString( ATTRIBUTE_COLON);
			}
		}

		styledAttribute.addString( new AttributeName( name));
		styledAttribute.addString( ATTRIBUTE_ASIGN);

		styledAttribute.addString( new AttributeValue( value));
			
		return styledAttribute;
	}
		
	private StyledElement formatNamespace( Namespace n) {
		StyledElement styledNamespace = null;

		String prefix = n.getPrefix();
		String value = n.getText();
		
		if ( value != null && value.length() > 0) {
			styledNamespace = new StyledElement();
			styledNamespace.addString( NAMESPACE_NAME);

			if ( prefix != null && prefix.length() > 0) {
				styledNamespace.addString( NAMESPACE_COLON);
				styledNamespace.addString( new NamespacePrefix( prefix));
			}

			styledNamespace.addString( NAMESPACE_ASIGN);
			styledNamespace.addString( new NamespaceURI( value));
		}

		return styledNamespace;
	}

	public class StyledElement {
		private Vector strings = null;
		
		public StyledElement() {
			strings = new Vector();
		}
	
		public void addString( StyledString string) {
			strings.addElement( string);
		}
		
		public int length() {
			int result = 0;
			
			for ( int i = 0; i < strings.size(); i++) {
				result += ((StyledString)strings.elementAt(i)).getText().length();
			}
			
			return result;
		}
		
		public Vector getStrings() {
			return strings;
		}
	}

	public class Line {
		private Vector strings = null;
		
		public Line() {
			strings = new Vector();
		}
		
		public void addStyledString( StyledString string) {
			strings.add( string);
		}
		
		public void addStyledElement( StyledElement element) {
			Vector strings = element.getStrings();

			for ( int i =0; i < strings.size(); i++) {
				addStyledString( (StyledString)strings.elementAt(i));
			}
		}

		public StyledString[] getStyledStrings() {
			StyledString[] ss = new StyledString[ strings.size()];
			
			for ( int i = 0; i < strings.size(); i++) {
				ss[i] = (StyledString)strings.elementAt(i);
			}

			return ss;
		}
		
		public int length() {
			int result = 0;
			
			for ( int i = 0; i < strings.size(); i++) {
				result += ((StyledString)strings.elementAt(i)).getText().length();
			}
			
			return result;
		}

		public String getText() {
			String result = "";
			
			for ( int i = 0; i < strings.size(); i++) {
				result += ((StyledString)strings.elementAt(i)).getText();
			}
			
			return result;
		}
	}
	
	// solves a problem in the Element that hasMixedContent returns true when the content 
	// has comment information.
	private static boolean isMixed( XElement element) {
		if ( element.hasMixedContent()) {
			boolean elementFound = false;
			boolean textFound = false;

			for ( int i = 0; i < element.nodeCount(); i++) {
				Node node = element.node( i);
				
				if ( node instanceof XElement) {
					elementFound = true;
				} else if ( (node instanceof Text)|| (node instanceof CDATA) || (node instanceof Entity)) {
					if ( !isWhiteSpace( node)) {
						textFound = true;
					}
				}
				
				if ( textFound && elementFound) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks to see if the element has a combination of text and one of the other type nodes
	 *
	 * @param element the XML element.
	 */	
	private static boolean isMixed2( XElement element) {
		if ( element.hasMixedContent()) {
			boolean elementFound = false;
			boolean otherFound = false;
			boolean textFound = false;

			for ( int i = 0; i < element.nodeCount(); i++) {
				Node node = element.node( i);
				
				if ( (node instanceof XElement)|| (node instanceof CDATA) || (node instanceof Entity) || 
						(node instanceof Comment) || (node instanceof ProcessingInstruction))
				{
					otherFound = true;
				}
				else if ( (node instanceof Text)|| (node instanceof CDATA) || (node instanceof Entity) || 
							(node instanceof Comment) || (node instanceof ProcessingInstruction))
				{
					if ( !isWhiteSpace( node)) {
						textFound = true;
					}
				}
				
				if ( textFound && otherFound) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// has text only, ie no other type node and not only whitespace
	private static boolean hasTextOnly(XElement element) 
	{		
		boolean textFound = false;
			
		for ( int i = 0; i < element.nodeCount(); i++) 
		{
			Node node = element.node( i);
			
			if ((node instanceof XElement)|| (node instanceof CDATA) || (node instanceof Entity) || 
					(node instanceof Comment) || (node instanceof ProcessingInstruction))
			{
				return false;
			}
			else if (node instanceof Text)
			{
				if ( !isWhiteSpace( node)) {
					textFound = true;
				}
			}
		}

		return textFound;
	}
	
	//	 has text and\or cdata only, ie no other type node and not only whitespace
	private static boolean hasTextOrCDATAOnly(XElement element) 
	{		
		boolean textCDATAFound = false;
			
		for ( int i = 0; i < element.nodeCount(); i++) 
		{
			Node node = element.node( i);
			
			if ((node instanceof XElement)|| (node instanceof Entity) || 
					(node instanceof Comment) || (node instanceof ProcessingInstruction))
			{
				return false;
			}
			else if (node instanceof CDATA)
			{
				textCDATAFound = true;
			}
			else if (node instanceof Text)
			{
				if ( !isWhiteSpace( node)) {
					textCDATAFound = true;
				}
			}
		}

		return textCDATAFound;
	}
	
//	 has text and\or cdata 
	private static boolean hasTextOrCDATA(XElement element) 
	{		
		boolean textCDATAFound = false;
			
		for ( int i = 0; i < element.nodeCount(); i++) 
		{
			Node node = element.node( i);
			
			if (node instanceof CDATA)
			{
				textCDATAFound = true;
			}
			else if (node instanceof Text)
			{
				if ( !isWhiteSpace( node)) {
					textCDATAFound = true;
				}
			}
		}

		return textCDATAFound;
	}
	
	
	// returns true if the element contains a comment, 
	private static boolean hasComment( XElement element) 
	{
		for ( int i = 0; i < element.nodeCount(); i++) 
		{
			Node node = element.node( i);
			
			if  (node instanceof Comment) 
			{
				return true;
			}
		}
		
		return false;
	}
	
	//returns true if the element contains a PI
	private static boolean hasProcessingInstruction( XElement element) 
	{
		for ( int i = 0; i < element.nodeCount(); i++) 
		{
			Node node = element.node( i);
			
			if  (node instanceof ProcessingInstruction) 
			{
				return true;
			}
		}
		
		return false;
	}
	
	//	returns true if the element contains a PI
	private static boolean hasEntity( XElement element) 
	{
		for ( int i = 0; i < element.nodeCount(); i++) 
		{
			Node node = element.node( i);
			
			if  (node instanceof Entity) 
			{
				return true;
			}
		}
		
		return false;
	}

	private static boolean isWhiteSpace( Node node) {
		return node.getText().trim().length() == 0;
	}

	private static final StyledString COMMENT_START = new ControlString( "<!--");
	private static final StyledString COMMENT_END = new ControlString( "-->");
	private static final StyledString PI_START = new ControlString( "<?");
	private static final StyledString PI_END = new ControlString( "?>");
	private static final StyledString SPACE = new ControlString( " ");
	private static final StyledString TAB = new ControlString( "  ");
	private static final StyledString SLASH = new ControlString( "/");
	private static final StyledString ATTRIBUTE_ASIGN = new ControlString( "=");
	private static final StyledString ATTRIBUTE_COLON = new ControlString( ":");
	private static final StyledString NAMESPACE_ASIGN = new ControlString( "=");
	private static final StyledString NAMESPACE_COLON = new ControlString( ":");
	private static final StyledString NAMESPACE_NAME = new NamespaceName();
	private static final StyledString ELEMENT_COLON = new ControlString( ":");

	private static final StyledString OPEN_BRACKET = new ControlString( "<");
	private static final StyledString CLOSE_BRACKET = new ControlString( ">");

	public class CommentText extends StyledString {
		public CommentText( String text) {
			super( text);
		}
		
		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.COMMENT).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.COMMENT).getFont();
		}
	}
	
	public class PI_Target extends StyledString {
		public PI_Target( String text) {
			super( text);
		}
		
		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.PI_TARGET).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.PI_TARGET).getFont();
		}
	}
	
	public class PI_Text extends StyledString {
		public PI_Text( String text) {
			super( text);
		}
		
		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.PI_NAME).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.PI_NAME).getFont();
		}
	}
	
	public class CDATA_SECTION extends StyledString {
		public CDATA_SECTION( String text) {
			super( text);
		}
		
		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.CDATA).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.CDATA).getFont();
		}
	}

	public class ElementValue extends StyledString {
		public ElementValue( String text) {
			super( text);
		}

		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.ELEMENT_VALUE).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ELEMENT_VALUE).getFont();
		}
	}

	public class AttributeValue extends StyledString {
		public AttributeValue( String text) {
			super( "\""+text+"\"");
		}

		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.ATTRIBUTE_VALUE).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ATTRIBUTE_VALUE).getFont();
		}
	}

	public class AttributePrefix extends StyledString {
		public AttributePrefix( String text) {
			super( text);
		}

		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.ATTRIBUTE_PREFIX).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ATTRIBUTE_PREFIX).getFont();
		}
	}

	public class AttributeName extends StyledString {
		public AttributeName( String text) {
			super( text);
		}

		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.ATTRIBUTE_NAME).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ATTRIBUTE_NAME).getFont();
		}
	}

	public class NamespaceURI extends StyledString {
		public NamespaceURI( String text) {
			super( "\""+text+"\"");
		}

		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.NAMESPACE_VALUE).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.NAMESPACE_VALUE).getFont();
		}
	}

	public static class NamespaceName extends StyledString {
		public NamespaceName() {
			super( "xmlns");
		}

		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.NAMESPACE_NAME).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.NAMESPACE_NAME).getFont();
		}
	}

	public static class ControlString extends StyledString {
		public ControlString( String text) {
			super( text);
		}

		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.SPECIAL).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.SPECIAL).getFont();
		}
	}

	public class NamespacePrefix extends StyledString {
		public NamespacePrefix( String text) {
			super( text);
		}

		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.NAMESPACE_PREFIX).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.NAMESPACE_PREFIX).getFont();
		}
	}

	public class ElementName extends StyledString {
		public ElementName( String text) {
			super( text);
		}

		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.ELEMENT_NAME).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ELEMENT_NAME).getFont();
		}
	}

	public class ElementPrefix extends StyledString {
		public ElementPrefix( String text) {
			super( text);
		}

		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.ELEMENT_PREFIX).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ELEMENT_PREFIX).getFont();
		}
	}
	
	public void cleanup() {
		Enumeration list = children();
		
		while ( list.hasMoreElements()) {
			((XmlElementNode)list.nextElement()).cleanup();
		}
		
		finalize();
	}
	
	protected void finalize() {
		element = null;
		_viewer = null;
		lines = null;
	}
	
	public String toString() {
		return getClass().getName() + "@" + Integer.toHexString(hashCode());
	}
} 
