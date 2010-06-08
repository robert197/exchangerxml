/*
 * $Id: PluginAction.java,v 1.0 14 Mar 2007 15:12:17 Administrator Exp $
 *
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.plugins;

import javax.swing.AbstractAction;

import com.cladonia.xml.ExchangerDocument;


/**
 * 
 *
 * @version	$Revision: 1.0 $, $Date: 14 Mar 2007 15:12:17 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public abstract class PluginAction extends AbstractAction {

	public PluginAction(String name) {
		super(name);
	}
	
	public abstract void setView( Object view);
	
	public void updateActions(ExchangerDocument document) {
 		
 		if(document != null) {
 			if(document.isXML() == true) {
 				if(document.isError() == false) {
 					//grid.getGridAddAttributeToSelectedAction().setEnabled(true);
 					setEnabled(true);
 				}
 				else {
 					//grid.getGridAddAttributeToSelectedAction().setEnabled(false);
 					setEnabled(false);
 				}
 			}
 			else {
 				//grid.getGridAddAttributeToSelectedAction().setEnabled(false);
 				setEnabled(false);
 			} 			
 		}
 		else {
 			//grid.getGridAddAttributeToSelectedAction().setEnabled(false);
 			setEnabled(false);
 		}
 	}
}
