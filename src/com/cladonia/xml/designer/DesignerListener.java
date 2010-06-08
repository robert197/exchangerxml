/*
 * $Id: DesignerListener.java,v 1.1 2004/03/25 18:42:44 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer;

import java.util.EventListener;

/**
 * Allows for listening to Tree Panel mouse and selection events.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:42:44 $
 * @author Dogsbay
 */
public interface DesignerListener extends EventListener {
	/** 
	 * Called when a node has been selected.
	 *
	 * @param node the selected node.
	 */
	public void selectionChanged( DesignerNode node);
} 
