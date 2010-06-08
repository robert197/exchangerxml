/*
 * $Id: ProjectNode.java,v 1.2 2005/09/05 09:08:30 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import org.bounce.image.ImageUtilities;

import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The default node for a project.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/09/05 09:08:30 $
 * @author Dogsbay
 */
public class ProjectNode extends FolderNode {
	private static final ImageIcon ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/project/icons/ProjectIcon.gif");
	private static final ImageIcon EXPANDED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/project/icons/SelectedProjectIcon.gif");
	
	/**
	 * The constructor for the project node.
	 *
	 * @param properties the project properties.
	 */
	public ProjectNode( DefaultTreeModel model, ProjectProperties properties, boolean isStartup) {
		super( model, properties, isStartup);
	}
	
	/**
	 * The icon for this node.
	 *
	 * @return the icon for the element.
	 */
	public Icon getSelectedIcon() {
		return ImageUtilities.createDarkerImage( ICON);
	}
	
	/**
	 * Returns the icon that is shown when the node is expanded and selected.
	 *
	 * @return the selected expanded icon.
	 */
	public Icon getExpandedSelectedIcon() {
		return ImageUtilities.createDarkerImage( EXPANDED_ICON);
	}


	/**
	 * Returns the icon that is shown when the node is expanded.
	 *
	 * @return the expanded icon.
	 */
	public Icon getExpandedIcon() {
		return EXPANDED_ICON;
	}

	/**
	 * The icon for this node.
	 *
	 * @return the icon for the element.
	 */
	public Icon getIcon() {
		return ICON;
	}
} 
