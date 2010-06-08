/*
 * $Id: DesignerNode.java,v 1.1 2004/03/25 18:42:44 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The base node class for the DOM Editor tree.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:42:44 $
 * @author Dogsbay
 */
public abstract class DesignerNode extends DefaultMutableTreeNode {
	boolean enabled = true;
	
	public abstract String getName();
	public abstract ImageIcon getIcon();
	public abstract String getDescription();
	public abstract ImageIcon getSelectedIcon();

	public abstract Object takeSnapShot();
	public abstract void setSnapShot( Object object);

	public abstract String getValue();
	public abstract void setValue( String value);

	public abstract boolean isVirtual();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled( boolean enabled) {
		this.enabled = enabled;
	}
} 
