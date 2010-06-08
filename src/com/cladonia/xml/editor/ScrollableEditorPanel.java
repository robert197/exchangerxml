/*
 * $Id: ScrollableEditorPanel.java,v 1.1 2004/08/19 17:41:40 edankert Exp $
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
package com.cladonia.xml.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

public class ScrollableEditorPanel extends JPanel implements Scrollable {
	private XmlEditorPane editor = null;
	
	public ScrollableEditorPanel( XmlEditorPane editor) {
		super( new BorderLayout());
		
		this.editor = editor;
		
		add( editor, BorderLayout.CENTER);
	}

	public Dimension getPreferredScrollableViewportSize() {
	    return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return editor.getScrollableUnitIncrement( visibleRect, orientation, direction);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return editor.getScrollableBlockIncrement( visibleRect, orientation, direction);
	}  

	public boolean getScrollableTracksViewportWidth() {
	
		if ( editor.isWrapped()) {
			return true;
		} else if ( getParent() instanceof JViewport) {
		    return (((JViewport)getParent()).getWidth() > getPreferredSize().width);
		}

		return false;
	}

	public boolean getScrollableTracksViewportHeight() {
		if ( getParent() instanceof JViewport) {
		    return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
		}
		return false;
	}
	
	public void cleanup() {
		removeAll();
		
		editor = null;
	}
}
