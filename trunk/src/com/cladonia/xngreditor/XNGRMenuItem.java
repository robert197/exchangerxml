/*
 * $Id: XNGRMenuItem.java,v 1.2 2004/10/04 16:19:28 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.Font;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Handles MenuItems, allows for emacs accelerator keys
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/04 16:19:28 $
 * @author Dogs bay
 */
public class XNGRMenuItem extends JMenuItem{
	KeyStroke accelerator = null;
	
	public XNGRMenuItem (Action action)
	{
		super(action);
	}
	
	public XNGRMenuItem (String name, char shortcut)
	{
		super(name,shortcut);
	}
	
    public KeyStroke getAccelerator()
    {
        return accelerator;
    }

    public void setAccelerator(KeyStroke keystroke, boolean emacsMode)
    {
    	// start off by blanking the existing accelerator
    	setAccelerator(null);
    	
		KeyStroke keystroke1 = accelerator;
		accelerator = keystroke;
		if (!emacsMode)
		{
			firePropertyChange("accelerator",null, keystroke);
			this.setFont(UIManager.getFont("MenuItem.font"));
		}
		else
		{
			// put any emacs mode keys in Italic
			Font font = this.getFont().deriveFont(Font.ITALIC+this.getFont().getStyle());
			this.setFont(font);
		}
		
		
    }
    
    public void setAccelerator(KeyStroke keystroke)
    {
        KeyStroke keystroke1 = accelerator;
        accelerator = keystroke;
        firePropertyChange("accelerator", keystroke1, accelerator);
    }
    
    
}
