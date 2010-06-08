/*
 * $Id: TraceNode.java,v 1.1 2004/05/11 13:19:06 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The base node class for the DOM Editor tree.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/05/11 13:19:06 $
 * @author Dogsbay
 */
public class TraceNode extends DefaultMutableTreeNode {
	Object stackItem = null;
	
	public TraceNode( Object stackItem) {
		this.stackItem = stackItem;
	}
	
	public Object getStackItem() {
		return stackItem;
	}
} 
