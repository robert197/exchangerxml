/*
 * $Id: BaseNode.java,v 1.1 2004/03/25 18:54:53 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The base node class.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:54:53 $
 * @author Dogsbay
 */
public abstract class BaseNode extends DefaultMutableTreeNode implements Comparable {
	public abstract String getName();
	public abstract Icon getIcon();
	public abstract String getDescription();
	public abstract Icon getSelectedIcon();
	public abstract Icon getExpandedIcon();
	public abstract Icon getExpandedSelectedIcon();

	/**
	 * Returns true if the node supplied contains a text or something
	 * that can be used to sort this node.
	 *
	 * @param object the other node.
	 *
	 * @return a positive value if this object is greater than the object supplied.
	 */
	public int compareTo( Object object) {
		int result = getName().compareToIgnoreCase( ((BaseNode)object).getName());
		
		return result;
	}

	/** 
	 * Adds the node to the parent at a sorted location.
	 *
	 * @param the node to be added.
	 */
//	public int add( BaseNode node) {
//		int index = 0;
//		
//		for ( index = 0; index < getChildCount(); index++) {
//			if ( ((BaseNode)node).compareTo( getChildAt( index)) <= 0) {
//				insert( node, index);
//				return index;
//			}
//		}
//		
//		super.add( node);
//		
//		return index;
//	}
} 
