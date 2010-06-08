/*
 * $Id: ScrollableListPanel.java,v 1.1 2004/07/15 15:27:35 edankert Exp $
 *
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.mozilla.org/MPL/ 
 *
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is eXchaNGeR browser code. (org.xngr.browser.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */
package com.cladonia.xngreditor.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.Scrollable;

public class ScrollableListPanel extends JPanel implements Scrollable {
	private JList list = null;
	
	public ScrollableListPanel( JList list) {
		super( new BorderLayout());
		
		this.list = list;
		
		add( list, BorderLayout.CENTER);
	}

	public Dimension getPreferredScrollableViewportSize() {
	    return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return list.getScrollableUnitIncrement( visibleRect, orientation, direction);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return list.getScrollableBlockIncrement( visibleRect, orientation, direction);
	}  

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	public void cleanup() {
		removeAll();
		
		list = null;
	}
}
