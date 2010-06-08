/*
 * @(#)MetalTreeUI.java	1.19 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.cladonia.xml.designer;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalTreeUI;
import javax.swing.tree.AbstractLayoutCache;

public class DesignerTreeUI extends MetalTreeUI {
	public DesignerTreeUI() {
		super();
		System.out.println( "DesignerTreeUI()");
	}

    // Boilerplate
    public static ComponentUI createUI(JComponent x) {
	    return new DesignerTreeUI();
    }

    /**
     * Creates the object responsible for managing what is expanded, as
     * well as the size of nodes.
     */
    protected AbstractLayoutCache createLayoutCache() {
		return new DesignerHeightLayoutCache();
    }
}
