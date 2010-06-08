/*
 * $Id: ChangeManager.java,v 1.9 2005/06/08 13:38:32 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.util.EventListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.AbstractUndoableEdit;

import com.cladonia.xml.designer.UndoableDesignerEdit;
//import com.cladonia.xml.grid.UndoableGridEditAbstact;
import com.cladonia.xngreditor.plugins.UndoablePluginEditAbstact;

/**
 * Handles undo events and actions.
 *
 * @version	$Revision: 1.9 $, $Date: 2005/06/08 13:38:32 $
 * @author Dogsbay
 */
public class ChangeManager extends UndoManager {
	private static final boolean DEBUG = false;
	
	private EventListenerList listeners = null;

	private UndoableEdit syncMark = null;
	private UndoableEdit saveMark = null;
	private UndoableEdit parseMark = null;
	private boolean validated = false;
	private UndoableEdit validateMark = null;

	private CompoundEdit compoundEdit = null;

	/**
	 * The constructor for the undo handler.
	 */
	public ChangeManager() {
		listeners = new EventListenerList();
	}
	
	// Mark the Synchronisation between model and text...
	public void markSync() {
		if (DEBUG) System.out.println( "ChangeManager.markSync()");

		syncMark = editToBeUndone();
		fireStateChanged( new ChangeEvent( this));
	}

	public void markSave() {
		if (DEBUG) System.out.println( "ChangeManager.markSave()");

		saveMark = editToBeUndone();
		fireStateChanged( new ChangeEvent( this));
	}

	public void markParse() {
		if (DEBUG) System.out.println( "ChangeManager.markParse()");

		parseMark = editToBeUndone();
		fireStateChanged( new ChangeEvent( this));
	}

	public void markValid() {
		if (DEBUG) System.out.println( "ChangeManager.markParse()");

		validateMark = editToBeUndone();
		
		validated = true;
		fireStateChanged( new ChangeEvent( this));
	}

	public void startCompound( boolean significant) {
		if (DEBUG) System.out.println( "ChangeManager.startCompound( "+significant+")");

		if ( significant) {
			compoundEdit = new CompoundEdit();
		} else {
			compoundEdit = new InsignificantCompoundEdit();
		}
	}

	public void endCompound() {
		if (DEBUG) System.out.println( "ChangeManager.endCompound()");

		compoundEdit.end();

		if ( (editToBeUndone() instanceof UndoableDesignerEdit) && compoundEdit.isSignificant()) {
			super.discardAllEdits();
		}
		else if ( (editToBeUndone() instanceof UndoablePluginEditAbstact) && compoundEdit.isSignificant()) {
			super.discardAllEdits();
		}
		
		super.addEdit( compoundEdit);
	
		compoundEdit = null;
		
		fireStateChanged( new ChangeEvent( this));
	}

	public void discardCompound() {
		if (DEBUG) System.out.println( "ChangeManager.discardCompound()");
		compoundEdit = null;
	}

	public boolean isCompound() {
		if (DEBUG) System.out.println( "ChangeManager.isCompound()");
		return compoundEdit != null;
	}

	public synchronized boolean addEdit( UndoableEdit edit) {
		if (DEBUG) System.out.println( "ChangeManager.addEdit( "+edit+")");
		boolean result = false;
		validated = false;
		
		if ( compoundEdit == null) {
			if (DEBUG) System.out.println( "super.addEdit( "+edit+")");

			if ( edit instanceof UndoableDesignerEdit && !(editToBeUndone() instanceof UndoableDesignerEdit)) {
				super.discardAllEdits();
			} else if ( !(edit instanceof UndoableDesignerEdit) && editToBeUndone() instanceof UndoableDesignerEdit) {
				super.discardAllEdits();
			}
			
			if ( edit instanceof UndoablePluginEditAbstact && !(editToBeUndone() instanceof UndoablePluginEditAbstact)) {
				super.discardAllEdits();
			} else if ( !(edit instanceof UndoablePluginEditAbstact) && editToBeUndone() instanceof UndoablePluginEditAbstact) {
				super.discardAllEdits();
			}
			
			result = super.addEdit( edit);
		
			fireStateChanged( new ChangeEvent( this));
		} else {
			result = compoundEdit.addEdit( edit);
		}
		
		return result;
	}
	
	/**
	 * Adds a dummy undoable edit to make sure the change manager knows 
	 * the content has changed but this cannot be undone.
	 * 
	 * @return boolean ...
	 */
	public synchronized boolean addNotUndoableEdit() {
		return addEdit( new NotUndoableEdit());
	}

	public synchronized void redo() {
		if (DEBUG) System.out.println( "ChangeManager.redo()");
		super.redo();
		validated = false;
		
		fireStateChanged( new ChangeEvent( this));
	}

	public void discardAllEdits() {
		if (DEBUG) System.out.println( "ChangeManager.discardAllEdits()");
		super.discardAllEdits();
		
		syncMark = null;
		saveMark = null;
		parseMark = null;
		validateMark = null;
		validated = false;
	
		compoundEdit = null;
		
		fireStateChanged( new ChangeEvent( this));
	}

	/**
	 * Undo the last change.
	 */
	public synchronized void undo() {
		if (DEBUG) System.out.println( "ChangeManager.undo()");
		super.undo();
		validated = false;
	
		fireStateChanged( new ChangeEvent( this));
	}
	
	/**
	 * Check to see if the last undo item was a significant text change.
	 *
	 * @return true when the text has been changed.
	 */
	public boolean isTextChanged() {
		boolean result = false;
		UndoableEdit undo = editToBeUndone();
		
		// see if the last undoable edit was a significant text edit???
//		if ( undo != syncMark && undo != parseMark && undo != saveMark && !(undo instanceof UndoableDesignerEdit)) {
		if ( undo != parseMark && !(undo instanceof UndoableDesignerEdit) && !(undo instanceof UndoablePluginEditAbstact)) {
			result = true;
		}

		if (DEBUG) System.out.println( "ChangeManager.isTextChanged() ["+result+"]");
		
		return result;
	}

	/**
	 * Check to see if the last undo item was a significant model change.
	 *
	 * @return true when the model has been changed.
	 */
	public boolean isModelChanged() {
		boolean result = false;
		UndoableEdit undo = editToBeUndone();
		
		// see if the last undoable edit was a significant text edit???
		if ( undo != syncMark && (undo instanceof UndoableDesignerEdit)&& !(undo instanceof UndoablePluginEditAbstact)) {
			result = true;
		} else if ( syncMark instanceof UndoableDesignerEdit && undo == null) {
			result = true;
		}
		
		if (DEBUG) System.out.println( "ChangeManager.isModelChanged() ["+result+"]");

		return result;
	}

	/**
	 * Check to see if the last undo item was a significant change.
	 *
	 * @return true when there has been a change.
	 */
	public boolean isChanged() {
		boolean result = false;
		UndoableEdit undo = editToBeUndone();
		
		// see if the last undoable edit was a significant text edit???
		if ( undo != saveMark) {
			result = true;
		}
		
		if (DEBUG) System.out.println( "ChangeManager.isChanged() ["+result+"]");

		return result;
	}

//	/**
//	 * Check to see if the current info has been parsed.
//	 *
//	 * @return true when the current info is parsed.
//	 */
//	public boolean isParsed() {
//		boolean result = false;
//		UndoableEdit undo = editToBeUndone();
//		
//		// see if the last undoable edit was a significant text edit???
//		if ( undo != null && (undo == parseMark || undo instanceof UndoableDesignerEdit)) {
//			result = true;
//		} else if ( undo == null && parseMark == null) {
//			result = true;
//		}
//		
//		if (DEBUG) System.out.println( "ChangeManager.isParsed() ["+result+"]");
//
//		return result;
//	}

	public boolean isValidated() {
		boolean result = false;
		UndoableEdit undo = editToBeUndone();
		
		// see if the last undoable edit was a significant text edit???
		if ( undo != null && undo == validateMark) {
			result = true;
		} else if ( undo == null && validated) {
			result = true;
		}
		
		if (DEBUG) System.out.println( "ChangeManager.isValid() ["+result+"]");

		return result;
	}

	public boolean isDesignerRedo() {
		return (editToBeRedone() instanceof UndoableDesignerEdit);
	}
	
	public boolean isPluginRedo() {
		return (editToBeRedone() instanceof UndoablePluginEditAbstact);
	}

	public boolean isDesignerUndo() {
		return (editToBeUndone() instanceof UndoableDesignerEdit);
	}
	
	public boolean isGridRedo() {
		return (editToBeRedone() instanceof UndoablePluginEditAbstact);
	}

	/*public boolean isGridUndo() {
		return (editToBeUndone() instanceof UndoablePluginEditAbstact);
	}*/
	public boolean isPluginUndo() {
		return (editToBeUndone() instanceof UndoablePluginEditAbstact);
	}

	/** 
	 * Adds a change listener to the list of listeners.
	 *
	 * @param the change listener.
	 */
	public void addChangeListener( ChangeListener listener) {
		listeners.add( (Class)listener.getClass(), listener);
	}

	/** 
	 * Removes a change listener from the list of listeners.
	 *
	 * @param the change listener.
	 */
	public void removeChangeListener( ChangeListener listener) {
		if ( listeners != null) {
			listeners.remove( (Class)listener.getClass(), listener);
		}
	}

	/** 
	 * Notifies the listeners about a popup trigger on a node.
	 *
	 * @param the mouse event.
	 * @param the node.
	 */
	protected void fireStateChanged( ChangeEvent event) {
		// Guaranteed to return a non-null array
		Object[] list = listeners.getListenerList();
		
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = list.length-2; i >= 0; i -= 2) {
			((ChangeListener)list[i+1]).stateChanged( event);
		}
	}
	
	protected void removeAllListeners() {
		// Guaranteed to return a non-null array
		Object[] list = listeners.getListenerList();
		
		for ( int i = list.length-2; i >= 0; i -= 2) {
			listeners.remove( (Class)list[i], (EventListener)list[i+1]);
		}
	}

	public void cleanup() {
		finalize();
	}
	
	protected void finalize() {
		removeAllListeners();

		listeners = null;

		syncMark = null;
		saveMark = null;
		parseMark = null;
		validateMark = null;

		compoundEdit = null;
	}

	public class InsignificantCompoundEdit extends CompoundEdit {
		public boolean isSignificant() {
			return false;
		}
	}

	// This edit cannot be undone but can be
	public class NotUndoableEdit extends AbstractUndoableEdit {
		public boolean canRedo() {
			return false;
		}

		public boolean canUndo() {
			return false;
		}

		public boolean isSignificant() {
			return true;
		}
	}
}