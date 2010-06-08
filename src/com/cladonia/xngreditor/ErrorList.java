/*
 * $Id: ErrorList.java,v 1.1 2004/07/22 15:58:55 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xngreditor;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.apache.commons.collections.ComparatorUtils;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XMLError;

/**
 * This ErrorList is used to keep a list of all the XML errors for a document.
 *
 * @version $Revision: 1.1 $, $Date: 2004/07/22 15:58:55 $
 * @author Dogsbay
 */
public class ErrorList implements Comparator {
	private ExchangerDocument document = null;
	private Vector errors = null;
	private String header = null;
	private String footer = null;

	/**
	 * A list of XML Errors.
	 */
	public ErrorList() {
		errors = new Vector();
	}
	
	public void setDocument( ExchangerDocument doc) {
		document = doc;
	}

	public Vector getErrors() {
		return errors;
	}
	
	public void sortErrorsByLineNumber() {
		
		Collections.sort(errors,this);
		
	}
	
	public Vector getCurrentErrors() {
		Vector result = new Vector();
		
		for ( int i = 0; i < errors.size(); i++) {
			String systemId = ((XMLError)errors.elementAt(i)).getSystemId();

			if ( systemId != null) {
 				String name = document.getName();
 				
 				if ( systemId.endsWith( name)) {
 					result.add( errors.elementAt(i));
 				}
			} else {
				result.add( errors.elementAt(i));
			}
		}

		return result;
	}

	public void addError( XMLError error) {
		errors.addElement( error);
	}
	
	public void addErrorSortedByLineNumber( XMLError error) {
		if(error != null) {
			if(errors != null) {
				boolean greaterThanFound = false;
				int cnt = 0;
				while((greaterThanFound == false) && (cnt < errors.size())) {
					
					Object tempObj = errors.get(cnt);
					if(tempObj instanceof XMLError) {
						XMLError tempError = (XMLError) tempObj;
						if(tempError != null) {
							if(error.getLineNumber() > tempError.getLineNumber()) {
								//add after
							}
							else if(error.getLineNumber() < tempError.getLineNumber()) {
								//add before now
								greaterThanFound = true;
							}
							else {
								
								if(error.getColumnNumber() > tempError.getColumnNumber()) {
									//add after
								}
								else if(error.getColumnNumber() < tempError.getColumnNumber()) {
									greaterThanFound = true;
								}
								else {
									//prob wont happen
								}
							}
						}
						
						if(greaterThanFound == false) {
							cnt++;
						}
					}
				}
				
				if(errors.size() == 0) {
					errors.add(error);
				}
				else if(greaterThanFound == true) {
					//add before cnt
					errors.add(cnt, error);
					
				}
			}				
		}
		//errors.addElement( error);
	}
	
	public void reset() { 
		errors.removeAllElements();
		header = null;
		footer = null;
	}
	
	public void setFooter( String footer) {
		this.footer = footer;
	}

	public String getFooter() {
		return footer;
	}

	public void setHeader( String header) {
		this.header = header;
	}

	public String getHeader() {
		return header;
	}

	@Override
	public int compare(Object o1, Object o2) {
		if(o1 != null) {
			if(o2 != null) {
				if(o1 instanceof XMLError) {
					if(o2 instanceof XMLError) {
						
						int lineNumber1 = ((XMLError)o1).getLineNumber();
						int lineNumber2 = ((XMLError)o2).getLineNumber();
						
						if(lineNumber1 < lineNumber2) {
							return(-1);
						}
						else if(lineNumber1 > lineNumber2) {
							return(1);
						}
						else {
							
							int columnNumber1 = ((XMLError)o1).getColumnNumber();
							int columnNumber2 = ((XMLError)o2).getColumnNumber();
							
							if(columnNumber1 < columnNumber2) {
								return(-1);
							}
							else if(columnNumber1 > columnNumber2) {
								return(1);
							}
							else {
								return(0);
							}
						}
					}
					else {
						return(-1);
					}
				}
				else {
					if(o2 instanceof XMLError) {
						return(1);
					}
					else {
						return(0);
					}
				}
			}
			else {
				//o1 not null, o2 is null
				return(-1);
			}
		}
		else {
			if(o2 != null) {
				return(1);
			}
			else {
				//both are null
				return(0);
			}
		}
		
	}
}