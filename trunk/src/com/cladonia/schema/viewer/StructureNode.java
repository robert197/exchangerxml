/*
 * $Id: StructureNode.java,v 1.1 2004/03/25 18:39:54 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.util.Enumeration;

import com.cladonia.schema.SchemaComponent;

/**
 * The default node for a schema element.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:54 $
 * @author Dogsbay
 */
public abstract class StructureNode extends SchemaNode {
	private SchemaComponent component = null;
	private StructureNode parentNode = null;

	/**
	 * The constructor for the structure node.
	 *
	 * @param parent the schema structure node parent.
	 * @param component the schema component.
	 */
	public StructureNode( StructureNode parent, SchemaComponent component) {
		this.component = component;
		this.parentNode = parent;
	}
	
	/**
	 * The schema component represented by this node.
	 *
	 * @return the schema component.
	 */
	public SchemaComponent getComponent() {
		return component;
	}

	/**
	 * Gets the a node for the structure, up/down the tree.
	 *
	 * @param element the Schema structure.
	 *
	 * @return the node, or null if not found.
	 */	
	public StructureNode getNode( SchemaComponent component) {
		Enumeration e = children();

		if ( component == this.component) {
			return this;
		}

		while ( e.hasMoreElements()) {
			SchemaNode node = (SchemaNode)e.nextElement();
			
			if ( node instanceof StructureNode) {
				StructureNode result = ((StructureNode)node).getNode( component);
			
				if ( result != null) {
					return result;
				}
			}
		}
		
		return null;
	}

	/**
	 * Gets the ancestor node for the schema structure.
	 *
	 * @param structure the Schema structure.
	 *
	 * @return the node, or null if not found.
	 */	
	public StructureNode getAncestor( SchemaComponent component) {

		if ( component == this.component) {
			return this;
		} else if ( parentNode != null) {
			return parentNode.getAncestor( component);
		} else {
			return null;
		}
	}
} 
