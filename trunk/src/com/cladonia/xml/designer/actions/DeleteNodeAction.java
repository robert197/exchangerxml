/*
 * $Id: DeleteNodeAction.java,v 1.2 2004/10/23 15:04:52 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.cladonia.xml.designer.AttributeNode;
import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.designer.DesignerNode;
import com.cladonia.xml.designer.ElementNode;
import com.cladonia.xml.designer.UndoableDesignerEdit;

/**
 * Deletes a node from the Designer panel.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/23 15:04:52 $
 * @author Dogsbay
 */
public class DeleteNodeAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private Designer designer = null;

 	/**
	 * The constructor for the action which allows 
	 * deleting a node from the designer.
	 *
	 * @param designer the designer panel.
	 */
 	public DeleteNodeAction( Designer designer) {
 		super( "Delete");
		
		this.designer = designer;
 	}
 	
	/**
	 * The implementation of the delete node action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
	public void actionPerformed( ActionEvent e) {
		execute();
	}

	/**
	 * The implementation of the delete node action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void execute() {
		JTree tree = designer.getTree();
	 	TreePath path = tree.getSelectionPath();

	 	if ( path != null) {
	 		DesignerNode node = (DesignerNode) path.getLastPathComponent();
	 		
	 		if ( node instanceof ElementNode) {
	 			ElementNode elementNode = (ElementNode)node;
	 			
	 			if ( !elementNode.isVirtual()) {
	 				UndoableDeleteElement undoDeleteElement = new UndoableDeleteElement( elementNode);

	 				ElementNode parentNode = elementNode.getParentElementNode();
					int index = 0;
					
					if ( parentNode != null) {
						index = parentNode.getIndex( elementNode);
					}
					
	 				elementNode.remove();
	 				
	 				if ( parentNode != null) {
//	 					designer.nodeChanged( parentNode);
						if ( index >= parentNode.getChildCount()) {
							index = parentNode.getChildCount() - 1;
						}

						DesignerNode selectedNode = (DesignerNode)parentNode.getChildAt( index);
							
	 					tree.setSelectionPath( new TreePath( selectedNode.getPath()));
	 				}
	
		 			designer.getUndoSupport().postEdit( undoDeleteElement);
	 			}
	 		} else if ( node instanceof AttributeNode) {
		 		AttributeNode attributeNode = (AttributeNode)node;
		 		
		 		if ( !attributeNode.isVirtual()) {
		 			UndoableDeleteAttribute undoDeleteAttribute = new UndoableDeleteAttribute( attributeNode);

		 			attributeNode.remove();
		 			
//	 				designer.nodeChanged( attributeNode.getParentElementNode());
	 				tree.setSelectionPath( new TreePath( attributeNode.getPath()));
			 		designer.getUndoSupport().postEdit( undoDeleteAttribute);
		 		}
		 	}
			
		 	designer.updateNavigator();
			designer.selectionChanged();
	 	}
 	}

	 private class UndoableDeleteAttribute extends UndoableDesignerEdit {
	 	ElementNode parent = null;
	 	AttributeNode node = null;
	 	Object before = null;
	 	Object after = null;
	 	Object childBefore = null;
	 	Object childAfter = null;
	 	
	 	public UndoableDeleteAttribute( AttributeNode node) {
	 		this.node = node;
	 		this.parent = node.getParentElementNode();

	 		before = parent.takeSnapShot();
	 		childBefore = node.takeSnapShot();
	 	}
	 	
	 	public void undo() {
//	 		System.out.println( "UndoableDeleteAttribute.undo()");
	 		super.undo();

	 		after = parent.takeSnapShot();
	 		childAfter = node.takeSnapShot();
	 		node.setSnapShot( childBefore);
	 		parent.setSnapShot( before);

		 	DeleteNodeAction.this.designer.updateNavigator();
		 	designer.setSelectedNode( node);

//		 	System.out.println( "UndoableDeleteAttribute ["+canUndo()+"]["+canRedo()+"]");
	 	}

	 	public void redo() {
//	 		System.out.println( "UndoableDeleteAttribute.redo()");
		 	super.redo();

		 	node.setSnapShot( childAfter);
		 	parent.setSnapShot( after);

		 	DeleteNodeAction.this.designer.updateNavigator();
		 	designer.setSelectedNode( node);
//		 	System.out.println( "UndoableDeleteAttribute ["+canUndo()+"]["+canRedo()+"]");
	 	}
	 	
	 	public String getUndoPresentationName() {
	 		return "Undo Delete Attribute ("+node.getName()+")";
	 	}

	 	public String getRedoPresentationName() {
	 		return "Redo Delete Attribute ("+node.getName()+")";
	 	}
	 }
	 
	 private class UndoableDeleteElement extends UndoableDesignerEdit {
		 ElementNode parent = null;
		 ElementNode node = null;
		 Object before = null;
		 Object after = null;
		 Object childBefore = null;
		 Object childAfter = null;
	 	
		 public UndoableDeleteElement( ElementNode node) {
		 	this.node = node;
		 	this.parent = node.getParentElementNode();
		 	before = parent.takeSnapShot();
		 	childBefore = node.takeSnapShot();
		 }
		 
		 public void undo() {
//		 	System.out.println( "UndoableDeleteElement.undo()");
		 	super.undo();
	
		 	after = parent.takeSnapShot();
		 	childAfter = node.takeSnapShot();
		 	node.setSnapShot( childBefore);
		 	parent.setSnapShot( before);
	
		 	DeleteNodeAction.this.designer.updateNavigator();
		 	designer.setSelectedNode( node);
		 }
	
		 public void redo() {
//		 	System.out.println( "UndoableDeleteElement.redo()");
		 	super.redo();
		 	
		 	node.setSnapShot( childAfter);
		 	parent.setSnapShot( after);
		
		 	DeleteNodeAction.this.designer.updateNavigator();
		 	designer.setSelectedNode( node);
		 }
	 	
	 	public String getUndoPresentationName() {
	 		return "Undo Delete Element ("+node.getName()+")";
	 	}

	 	public String getRedoPresentationName() {
	 		return "Redo Delete Element ("+node.getName()+")";
	 	}
	 }
	 
	 public void cleanup() {
	 	designer = null;
	 }
}
