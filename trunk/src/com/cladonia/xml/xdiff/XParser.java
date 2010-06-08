/**
  * XDiff -- A part of Niagara Project
  * Author:	Yuan Wang
  *
  * Copyright (c)	Computer Sciences Department,
  *			University of Wisconsin -- Madison
  * All Rights Reserved._
  *
  * Permission to use, copy, modify and distribute this software and
  * its documentation is hereby granted, provided that both the copyright
  * notice and this permission notice appear in all copies of the software,
  * derivative works or modified versions, and any portions thereof, and
  * that both notices appear in supporting documentation._
  *
  * THE AUTHOR AND THE COMPUTER SCIENCES DEPARTMENT OF THE UNIVERSITY OF
  * WISCONSIN - MADISON ALLOW FREE USE OF THIS SOFTWARE IN ITS "AS IS"
  * CONDITION, AND THEY DISCLAIM ANY LIABILITY OF ANY KIND FOR ANY DAMAGES
  * WHATSOEVER RESULTING FROM THE USE OF THIS SOFTWARE._
  *
  * This software was developed with support by DARPA through Rome Research
  * Laboratory Contract No.F30602-97-2-0247.
  *
  * Please report bugs or send your comments to yuanwang@cs.wisc.edu
  */
package com.cladonia.xml.xdiff;

import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ext.LexicalHandler;

import com.cladonia.xml.XMLUtilities;

/**
  * <code>XParser</code> parses an input XML document and constructs an
  * <code>XTree</code>
  */
class XParser extends DefaultHandler implements LexicalHandler
{
	private static final String	_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";
	private static boolean	_setValidation = false;
	private static boolean	_setNameSpaces = true;
	private static boolean	_setSchemaSupport = true;
	private static boolean	_setSchemaFullSupport = false;
 	private static boolean  _setNameSpacePrefixes = true;

	private static int	_STACK_SIZE = 100;

	private XMLReader	_parser;
	private XTree		_xtree;
	private int		_idStack[], _lsidStack[]; // id and left sibling
	private long		_valueStack[];
	private int		_stackTop, _currentNodeID;
	private boolean		_readElement;
	private StringBuffer	_elementBuffer;

	/**
	  * Constructor.
	  */
	public XParser() throws Exception
	{
		XHash.initialize();
		try
		{
			_parser = (XMLReader)Class.forName(_PARSER_NAME).newInstance();
			_parser.setFeature("http://xml.org/sax/features/validation", _setValidation);
			_parser.setFeature("http://xml.org/sax/features/namespaces", _setNameSpaces);
			_parser.setFeature("http://apache.org/xml/features/validation/schema", _setSchemaSupport);
			_parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", _setSchemaFullSupport);
 			_parser.setFeature("http://xml.org/sax/features/namespace-prefixes", _setNameSpacePrefixes);

			_parser.setContentHandler(this);
			_parser.setErrorHandler(this);
			_parser.setProperty("http://xml.org/sax/properties/lexical-handler", this);
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			throw new Exception(e);
			//System.exit(1);
		}

		_idStack = new int[_STACK_SIZE];
		_lsidStack = new int[_STACK_SIZE];
		_valueStack = new long[_STACK_SIZE];
		_stackTop = 0;
		_currentNodeID = XTree.NULL_NODE;
		_elementBuffer = new StringBuffer();
	}

	/**
	  * Parse an XML document
	  * @param	uri	input XML document
	  * @return	the created XTree
	  */
	public XTree parse(String uri) throws Exception
	{
		_xtree = new XTree();
		_idStack[_stackTop] = XTree.NULL_NODE;
		_lsidStack[_stackTop] = XTree.NULL_NODE;

		try
		{
			URL url = new URL(uri);
			XMLUtilities.XMLReader reader = XMLUtilities.replaceAmp(url);
			InputSource source = new InputSource(reader);
			_parser.parse(source);
			//_parser.parse(uri);
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			throw new Exception(e);
			//System.exit(1);
		}

		return _xtree;
	}

	// Document handler methods

	public void startElement(String uri, String local, String raw,
				 Attributes attrs)
	{
		// if text is mixed with elements
		if (_elementBuffer.length() > 0)
		{
			String	text = _elementBuffer.toString().trim();
			if (text.length() > 0)
			{
				long	value = XHash.hash(text);
				int	tid = _xtree.addText(_idStack[_stackTop], _lsidStack[_stackTop], text, value);
				_lsidStack[_stackTop] = tid;
				_currentNodeID = tid;
				_valueStack[_stackTop] += value;
			}
		}

		//int	eid = _xtree.addElement(_idStack[_stackTop],
		//				_lsidStack[_stackTop], local);
		
		String localURI = "";
		if (uri != null && !uri.equals(""))
		{
			localURI = local+uri;
		}
		else
		{
			localURI = local;
		}
		
		int	eid = _xtree.addElement(_idStack[_stackTop],
						_lsidStack[_stackTop], raw, localURI );
		
		
		// Update last sibling info.
		_lsidStack[_stackTop] = eid;

		// Push
		_stackTop++;
		_idStack[_stackTop] = eid;
		_currentNodeID = eid;
		_lsidStack[_stackTop] = XTree.NULL_NODE;
		//_valueStack[_stackTop] = XHash.hash(local);
		//_valueStack[_stackTop] = XHash.hash(raw);
		_valueStack[_stackTop] = XHash.hash(uri+local);
		
		// check for root
//		if (_stackTop == 1)
//		{
//			_xtree.setRootHash(XHash.hash(uri+local));
//		}
		

		// Take care of attributes
		if ((attrs != null) && (attrs.getLength() > 0))
		{
			for (int i = 0; i < attrs.getLength(); i++)
			{
				String	name = attrs.getQName(i);
				String	value = attrs.getValue(i);
				//long	namehash = XHash.hash(name);
				String attrNameURI = "";
				if (attrs.getURI(i) != null && !attrs.getURI(i).equals(""))
				{
					attrNameURI = attrs.getLocalName(i)+attrs.getURI(i);
				}
				else
				{
					attrNameURI = attrs.getLocalName(i);
				}
				
				long	namehash = XHash.hash(attrNameURI);
				long	valuehash = XHash.hash(value);
				long	attrhash = namehash * namehash +
						   valuehash * valuehash;
				int	aid = _xtree.addAttribute(eid, _lsidStack[_stackTop], name, value, namehash, attrhash, attrNameURI);

				_lsidStack[_stackTop] = aid;
				_currentNodeID = aid + 1;
				_valueStack[_stackTop] += attrhash * attrhash;
			}
		}

		_readElement = true;
		_elementBuffer = new StringBuffer();
	}

	public void characters(char ch[], int start, int length)
	{
		_elementBuffer.append(ch, start, length);
	}

	public void endElement(String uri, String local, String raw)
	{
		if (_readElement)
		{
			if (_elementBuffer.length() > 0)
			{
				String	text = _elementBuffer.toString();
				long	value = XHash.hash(text);
				_currentNodeID =
					_xtree.addText(_idStack[_stackTop],
						       _lsidStack[_stackTop],
						       text, value);
				_valueStack[_stackTop] += value;
			}
			else	// an empty element
			{
				_currentNodeID =
					_xtree.addText(_idStack[_stackTop],
						       _lsidStack[_stackTop],
						       "", 0);
			}
			_readElement = false;
		}
		else
		{
			if (_elementBuffer.length() > 0)
			{
				String	text = _elementBuffer.toString().trim();
				// More text nodes before end of the element.
				if (text.length() > 0)
				{
					long	value = XHash.hash(text);
					_currentNodeID =
					  _xtree.addText(_idStack[_stackTop],
							 _lsidStack[_stackTop],
							 text, value);
					_valueStack[_stackTop] += value;
				}
			}
		}

		_elementBuffer = new StringBuffer();
		_xtree.addHashValue(_idStack[_stackTop],_valueStack[_stackTop]);
		_valueStack[_stackTop-1] += _valueStack[_stackTop] * _valueStack[_stackTop];
		_lsidStack[_stackTop-1] = _idStack[_stackTop];

		// Pop
		_stackTop--;
	}

	// End of document handler methods

	// Lexical handler methods.

	public void startCDATA()
	{
		// The text node id should be the one next to the current
		// node id.
		int	textid = _currentNodeID + 1;
		String	text = _elementBuffer.toString();
		_xtree.addCDATA(textid, text.length());
	}

	public void endCDATA()
	{
		int	textid = _currentNodeID + 1;
		String	text = _elementBuffer.toString();
		_xtree.addCDATA(textid, text.length());
	}

	// Following functions are not implemented.
	public void comment(char[] ch, int start, int length)
	{
	}

	public void startDTD(String name, String publicId, String systemId)
	{
	}

	public void endDTD()
	{
	}

	public void startEntity(String name)
	{
	}

	public void endEntity(String name)
	{
	}

	// End of lexical handler methods.
}
