/*
 * $Id: RNGInclude.java,v 1.1 2004/03/25 18:39:07 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.rng;

import java.util.Vector;

/**
 * A container for a SchemaElement and possible attributes/child elements
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:07 $
 * @author Dogsbay
 */
public class RNGInclude {
	private String href = null;
	protected Vector definitions	= null;
	protected Vector elements		= null;
	protected Vector references		= null;
	
	public RNGInclude( String href) {
		this.href = href;
		
		definitions = new Vector();
	}
	
	public String getHref() {
		return href;
	}
	
	public boolean addDefinition( RNGDefinition definition) {
		if ( definition != null) {
			for ( int i = 0; i < definitions.size(); i++) {
				RNGDefinition def = (RNGDefinition)definitions.elementAt(i);
				
				if ( def.getName().equals( definition.getName())) {
					def.combine( definition);
					return false;
				}
			}
			
			definitions.addElement( definition);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean include( RNGGrammar grammar) {
		Vector newDefs = new Vector();

		if ( grammar.getURI().equals( href)) {
			references = grammar.getReferences();
			elements = grammar.getElements();
			Vector oldDefs = definitions;
			Vector grammarDefs = grammar.getDefinitions();

			for ( int i = 0; i < grammarDefs.size(); i++) {
				RNGDefinition def = (RNGDefinition)grammarDefs.elementAt(i);
				boolean skip = false;
				
				for ( int j = 0; j < oldDefs.size(); j++) {
					RNGDefinition oldDef = (RNGDefinition)oldDefs.elementAt(j);
					if ( oldDef.getName().equals( def.getName())) {
						skip = true;
						break;
					}
				}
				
				if ( !skip) {
					boolean added = false;
					
					for ( int j = 0; j < newDefs.size(); j++) {
						RNGDefinition newDef = (RNGDefinition)newDefs.elementAt(j);
						
						if ( newDef.getName().equals( def.getName())) {
							newDef.combine( def);
							added = true;
						}
					}
					
					if ( !added) {
						newDefs.addElement( def);
					}
				}
			}
			definitions = newDefs;
			return true;
		}

		return false;
	}

	public Vector getDefinitions() {
		return definitions;
	}

	public Vector getElements() {
		return elements;
	}

	public Vector getReferences() {
		return references;
	}
} 
