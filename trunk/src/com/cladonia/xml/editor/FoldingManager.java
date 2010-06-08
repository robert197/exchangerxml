/*
 * $Id: FoldingManager.java,v 1.1 2004/09/30 11:33:53 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xml.editor;

import javax.swing.text.BadLocationException;

/**
 * This FoldingParent is used to ...
 *
 * @version $Revision: 1.1 $, $Date: 2004/09/30 11:33:53 $
 * @author Dogsbay
 */
public interface FoldingManager {

	public boolean isMultipleLineTagStart( int line) throws BadLocationException;
	public void revalidate();
	public void repaint();
	public void setFocus();
	public void updateMargins();
}
