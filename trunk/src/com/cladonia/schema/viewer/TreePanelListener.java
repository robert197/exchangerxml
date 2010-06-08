/*
 * $Id: TreePanelListener.java,v 1.1 2004/03/25 18:39:54 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.awt.event.MouseEvent;
import java.util.EventListener;

/**
 * Allows for listening to Tree Panel mouse and selection events.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:54 $
 * @author Dogsbay
 */
public interface TreePanelListener extends EventListener {
	/** 
	 * Called when a node has been right clicked.
	 *
	 * @param event the mouse event for the right clicked node.
	 * @param node the selected node.
	 */
	public void popupTriggered( MouseEvent event, SchemaNode node);
	
	/** 
	 * Called when a node has been double clicked.
	 *
	 * @param event the mouse event for the right clicked node.
	 * @param node the selected node.
	 */
	public void doubleClicked( MouseEvent event, SchemaNode node);

	/** 
	 * Called when a node has been selected.
	 *
	 * @param node the selected node.
	 */
	public void selectionChanged( SchemaNode node);
} 
