/*
 * $Id: XPathList.java,v 1.1 2004/07/22 15:57:58 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xngreditor;

import java.util.Vector;

/**
 * This XPathList is used to keep a list of all the XPath results for a document.
 *
 * @version $Revision: 1.1 $, $Date: 2004/07/22 15:57:58 $
 * @author Dogsbay
 */
public class XPathList {
	private Vector results = null;

	/**
	 * A list of XML Errors.
	 */
	public XPathList() {
		results = new Vector();
	}
	
	public Vector getResults() {
		return results;
	}
	
	public void setResults( Vector results) {
		this.results = results;
	}
	
	public void reset() { 
		results.removeAllElements();
	}
}	
