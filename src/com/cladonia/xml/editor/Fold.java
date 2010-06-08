/*
 * $Id: Fold.java,v 1.3 2004/09/23 10:41:27 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xml.editor;

import java.util.Vector;

import javax.swing.text.Element;

/**
 * This Fold is used to ...
 *
 * @version $Revision: 1.3 $, $Date: 2004/09/23 10:41:27 $
 * @author Dogsbay
 */
public class Fold {
	private Vector children = null;
	private int fixedStart = -1;
	private int fixedEnd = -1;
	private Element start;
	private Element end;
	private boolean collapseAll = false;
	
	private int range = -1;
	
	public Fold( Element start, Element end) {
		this( start, end, false);
	}
	
	public Fold( Element start, Element end, boolean collapseAll) {
		this.collapseAll = collapseAll;
		this.start = start;
		this.end = end;

		range = getEnd() - getStart();
		
		children = new Vector();
	}

	public int getStart() {
		if ( fixedStart == -1) {
			return getRealStart();
		}

		return fixedStart;
	}

	public int getEnd() { 
		if ( range == -1) {
			return getRealEnd();
		}

		return getStart()+range;
	}
	
	private int getRealStart() {
		fixedStart = start.getParentElement().getElementIndex( start.getStartOffset());
		return fixedStart;
	}

	private int getRealEnd() {
//		if ( end != null) {
			return end.getParentElement().getElementIndex( end.getStartOffset());
//		} else {
//			return start.getParentElement().getElementCount();
//		}
	}

	public boolean contains( int index) {
		if ( index > getStart() && index < getEnd()) {
			return true;
		}
		
		return false;
	}
	
	public void add( Fold fold) {
		for ( int i = 0; i < children.size(); i++) {
			if ( ((Fold)children.elementAt(i)).contains( fold.getStart())) {
				((Fold)children.elementAt(i)).add( fold);
				return;
			}
		}
		
		children.add( fold);
	}
	
	public void remove( int start, int end) {
		Vector temp = new Vector( children);
		for ( int i = 0; i < temp.size(); i++) {
			Fold f = (Fold)temp.elementAt(i);

			if ( f.contains( start) || f.contains( end)) {
				f.remove( start, end);
				children.removeElement( f);
				
				Vector childs = f.getChildren();
				for ( int j = 0; j < childs.size(); j++) {
					children.addElement( childs.elementAt(j));
				}
				
				f.shallowCleanup();
			}
		}
	}
	
	public Vector getChildren() {
		return children;
	}

	public boolean isCollapseAll() {
		return collapseAll;
	}
	
	public void checkFolds() {
		Vector folds = new Vector( children);
		for ( int i = 0; i < folds.size(); i++) {
			Fold fold = (Fold)folds.elementAt( i);
			
			if ( !fold.isValid()) {
				fold.checkFolds();
				children.removeElement( fold);
				
				Vector fs = fold.getChildren();
				for ( int j = 0; j < fs.size(); j++) {
					children.addElement( fs.elementAt(j));
				}
			}
		}
	}
	
	public boolean isValid() {
		return (range == (getRealEnd() - getRealStart()));
	}

	public void shallowCleanup() {
		children.removeAllElements();
		start = null;
		end = null;
	}

	public void cleanup() {
		for ( int i = 0; i < children.size(); i++) {
			Fold child = (Fold)children.elementAt(i);
			child.cleanup();
		}
		
		shallowCleanup();
	}
}
