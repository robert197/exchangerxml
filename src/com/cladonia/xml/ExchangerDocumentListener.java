/*
 * $Id: ExchangerDocumentListener.java,v 1.1 2004/03/25 18:41:32 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml;

import java.util.EventListener;

/**
 * This interface needs to be implemented to be able to listen 
 * to specific Document events.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:41:32 $
 * @author Dogsbay
 */
public interface ExchangerDocumentListener extends EventListener {

 	/**
	 * This method is called when the document has been informed
	 * by an internal process that the document has been updated 
	 * calling the <code>update( XElement element)</code> method.
 	 *
 	 * @param event the document event fired.
 	 */	
 	public void documentUpdated( ExchangerDocumentEvent event);
} 
