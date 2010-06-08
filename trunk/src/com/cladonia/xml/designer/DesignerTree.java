/*
 * $Id: DesignerTree.java,v 1.1 2004/03/25 18:42:44 edankert Exp $
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
 * The Original Code is eXchaNGeR Skeleton code. (org.xngr.skeleton.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd.. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */
package com.cladonia.xml.designer;

import org.bounce.QTree;

/**
 * The explorer of documents in the system.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:42:44 $
 * @author Dogsbay
 */
public class DesignerTree extends QTree {

	public DesignerTree() {
		super();
		setRowHeight( 0);
	}
	
	public void setRowHeight( int height) {
//		System.out.println( "setRowHeight( "+height+")");
		super.setRowHeight( 0);
	}

	/**
	 * Sets the look and feel to the XML Tree UI look and feel.
	 * Override this method if you want to install a different UI.
	 */
//	public void updateUI() {
//	    setUI( DesignerTreeUI.createUI( this));
//	}
} 
