/*
 * $Id: PluginViewButtonGroup.java,v 1.0 19 Apr 2007 09:38:50 Administrator Exp $
 *
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.plugins;

import javax.swing.ButtonGroup;


/**
 * 
 *
 * @version	$Revision: 1.0 $, $Date: 19 Apr 2007 09:38:50 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class PluginViewButtonGroup extends ButtonGroup {

	private String name = null;
	
	public PluginViewButtonGroup(String name) {
		super();
		this.setName(name);
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {

		return name;
	}
}
