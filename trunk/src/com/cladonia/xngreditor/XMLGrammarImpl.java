/*
 * $Id: XMLGrammarImpl.java,v 1.1 2004/03/25 18:52:46 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import com.cladonia.xml.XMLGrammar;

/**
 * The default implementation of XML Grammar.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:52:46 $
 * @author Dogsbay
 */
public class XMLGrammarImpl implements XMLGrammar {
	private String location = null;
 	private int type = TYPE_XSD;
	private boolean external = false;

 	public String getLocation() {
		return location;
 	}

 	public void setLocation( String location) {
//		System.out.println( "XMLGrammarImpl.setLocation( "+location+")");
		if ( location == null || location.trim().length() == 0) {
	 		this.location = null;
		} else {
			this.location = location;
		}
 	}

 	public int getType() {
		return type;
 	}
	
 	public void setType( int type) {
 		this.type = type;
 	}

 	public void setExternal( boolean external) {
 		this.external = external;
 	}

 	public boolean useExternal() {
		return external;
 	}
	
	public String toString() {
		return "XMLGrammarImpl[location="+location+"ext="+external+",type="+type+"]";
	}
} 
