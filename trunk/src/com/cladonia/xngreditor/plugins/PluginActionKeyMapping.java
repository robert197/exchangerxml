/*
 * $Id: PluginActionKeyMapping.java,v 1.0 15 Mar 2007 17:10:30 Administrator Exp $
 *
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.plugins;


/**
 * 
 *
 * @version	$Revision: 1.0 $, $Date: 15 Mar 2007 17:10:30 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class PluginActionKeyMapping {

	private String keystroke_action_name = null;
	private String keystroke_action_description = null;
	private String keystroke_mask = null;
	private String keystroke_value = null;
	
	private PluginAction action = null;
	
	/**
	 * @param keystroke_action_name the keystroke_action_name to set
	 */
	public void setKeystroke_action_name(String keystroke_action_name) {

		this.keystroke_action_name = keystroke_action_name;
	}
	/**
	 * @return the keystroke_action_name
	 */
	public String getKeystroke_action_name() {

		return keystroke_action_name;
	}
	/**
	 * @param keystroke_action_description the keystroke_action_description to set
	 */
	public void setKeystroke_action_description(String keystroke_action_description) {

		this.keystroke_action_description = keystroke_action_description;
	}
	/**
	 * @return the keystroke_action_description
	 */
	public String getKeystroke_action_description() {

		return keystroke_action_description;
	}
	/**
	 * @param keystroke_mask the keystroke_mask to set
	 */
	public void setKeystroke_mask(String keystroke_mask) {

		this.keystroke_mask = keystroke_mask;
	}
	/**
	 * @return the keystroke_mask
	 */
	public String getKeystroke_mask() {

		return keystroke_mask;
	}
	/**
	 * @param keystroke_value the keystroke_value to set
	 */
	public void setKeystroke_value(String keystroke_value) {

		this.keystroke_value = keystroke_value;
	}
	/**
	 * @return the keystroke_value
	 */
	public String getKeystroke_value() {

		return keystroke_value;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(PluginAction action) {

		this.action = action;
	}
	/**
	 * @return the action
	 */
	public PluginAction getAction() {

		return action;
	}
	
}
