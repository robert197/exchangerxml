/*
 * $Id: Constants.java,v 1.1 2004/03/25 18:44:46 edankert Exp $
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
 * The Initial Developer of the Original Code is Cladonia Ltd. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */

package com.cladonia.xml.editor;

/**
 * The contants used for the XML editor.
 *
 * <p>
 * <b>Note:</b> The XML Editor package is based on the JavaEditorKit example as 
 * described in the article <i>'Customizing a Text Editor'</i> by <b>Timothy Prinzing</b>.
 * See: http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/
 * </p>
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:44:46 $
 * @author Dogsbay
 */
public interface Constants {

    public static final long MAXFILESIZE		= 0xffffffffL;
    public static final long MAXLINENUMBER		= 0xffffffffL;

    public static final int ELEMENT_NAME 		= 1;
    public static final int ELEMENT_PREFIX 		= 2;
    public static final int ELEMENT_VALUE 		= 3;

    public static final int ATTRIBUTE_NAME 		= 5;
    public static final int ATTRIBUTE_PREFIX 	= 6;
    public static final int ATTRIBUTE_VALUE 	= 7;

    public static final int NAMESPACE_NAME 		= 10;
    public static final int NAMESPACE_PREFIX 	= 11;
    public static final int NAMESPACE_VALUE 	= 12;

    public static final int ENTITY		 		= 15;
    public static final int COMMENT 			= 16;
    public static final int DECLARATION 		= 17;
    public static final int CDATA 				= 18;

    public static final int SPECIAL				= 20;
    public static final int STRING				= 21;

	public static final int PI_TARGET			= 22;
	public static final int PI_NAME				= 23;
	public static final int PI_VALUE			= 24;

	public static final int STRING_VALUE 		= 30;	// DTD: String Value
	public static final int ENTITY_VALUE 		= 31;	// DTD: Entity Reference

	public static final int ENTITY_DECLARATION	= 32;	// DTD: ENTITY Declaration
	public static final int ENTITY_NAME			= 33;	// DTD: Entity Name
	public static final int ENTITY_TYPE 		= 34;	// DTD: Entity Type

	public static final int ATTLIST_DECLARATION	= 35; 	// DTD: ATTLIST Declaration
	public static final int ATTLIST_NAME		= 36;	// DTD: Attribute Name
	public static final int ATTLIST_TYPE		= 37;	// DTD: Attribute Type
	public static final int ATTLIST_VALUE		= 38;	// DTD: Attribute Enumeration
	public static final int ATTLIST_DEFAULT		= 39;	// DTD: Attribute Default #REQUIRED/#IMPLIED/#FIXED

	public static final int ELEMENT_DECLARATION			= 40;	// DTD: ELEMENT Declaration
	public static final int ELEMENT_DECLARATION_NAME	= 41;	// DTD: Element Name
	public static final int ELEMENT_DECLARATION_CHILD	= ELEMENT_DECLARATION_NAME;
	public static final int ELEMENT_DECLARATION_TYPE	= 42;	// DTD: Element Type (EMPTY, ANY)
	public static final int ELEMENT_DECLARATION_PCDATA	= 43;	// DTD: #PCDATA
	public static final int ELEMENT_DECLARATION_OPERATOR= 44;	// DTD: Element Operator

	public static final int NOTATION_DECLARATION		= 45;	// DTD: NOTATION Declaration
	public static final int NOTATION_DECLARATION_NAME	= 46;	// DTD: Notation Name
	public static final int NOTATION_DECLARATION_TYPE	= 47;	// DTD: Notation Type (PUBLIC, SYSTEM)

	public static final int DOCTYPE_DECLARATION			= 48;	// DTD: DOCTYPE Declaration
	public static final int DOCTYPE_DECLARATION_TYPE	= 49;	// DTD: Doctype Type (PUBLIC, SYSTEM)

	public static final int MAX_TOKENS	= 50;
}
