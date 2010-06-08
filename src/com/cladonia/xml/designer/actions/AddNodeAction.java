/*
 * $Id: AddNodeAction.java,v 1.2 2004/10/23 15:04:52 edankert Exp $
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

import com.cladonia.schema.SchemaElement;
import com.cladonia.xml.designer.AttributeNode;
import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.designer.DesignerNode;
import com.cladonia.xml.designer.ElementNode;
import com.cladonia.xml.designer.SubstitutionSelectionDialog;
import com.cladonia.xml.designer.UndoableDesignerEdit;
import com.cladonia.xngreditor.ExchangerEditor;

/**
 * Adds a node to the Designer panel.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/23 15:04:52 $
 * @author Dogsbay
 */
 public class AddNodeAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private Designer designer = null;
	private SubstitutionSelectionDialog substitutionDialog = null;
	private ExchangerEditor parent = null;

 	/**
	 * The constructor for the action which allows 
	 * for adding nodes to the Designer.
	 *
	 * @param designer the designer.
	 */
 	public AddNodeAction( ExchangerEditor parent, Designer designer) {
 		super( "Add");
		
		this.parent = parent;
		this.designer = designer;
 	}

	private SubstitutionSelectionDialog getSubstitutionDialog() {
		if ( substitutionDialog == null) {
			substitutionDialog = new SubstitutionSelectionDialog( parent);
		}
		
		return substitutionDialog;
	}
	  	
	/**
	 * The implementation of the add Node action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		execute();
 	}
	
	public void execute() {
		JTree tree = designer.getTree();
	 	TreePath path = tree.getSelectionPath();

	 	if ( path != null) {
	 		DesignerNode node = (DesignerNode) path.getLastPathComponent();
	 		
	 		if ( node instanceof ElementNode) {
	 			ElementNode elementNode = (ElementNode)node;
				
	 			if ( elementNode.isVirtual()) {
		 			UndoableAddElement undoAddElement = new UndoableAddElement( elementNode);

		 			if ( elementNode.isAbstract()) {
						SubstitutionSelectionDialog subsDialog = getSubstitutionDialog();
						subsDialog.setElement( elementNode.getType());
						//subsDialog.setVisible( true);
						subsDialog.show();
						
						if ( !subsDialog.isCancelled()) {
							SchemaElement type = subsDialog.getSelectedElement();
							
							elementNode.substitute( type, designer.isAutoCreateRequired());
							designer.getUndoSupport().postEdit( undoAddElement);
						} 
		 			} else {
//						String uri = elementNode.getType().getNamespace();
//						System.out.println( "uri:"+uri);
//						if ( elementNode.isNamespaceDeclared()) {
//							System.out.println( " [declared]");
//						}
						
		 				elementNode.add( designer.isAutoCreateRequired());
						designer.getUndoSupport().postEdit( undoAddElement);
		 			}

	 				tree.setSelectionPath( new TreePath( elementNode.getPath()));
	 			} 
	 		}
		 	if ( node instanceof AttributeNode) {
		 		AttributeNode attributeNode = (AttributeNode)node;
		 		
		 		if ( attributeNode.isVirtual()) {
		 			UndoableAddAttribute undoAddAttribute = new UndoableAddAttribute( attributeNode);

		 			attributeNode.add();

		 			designer.getUndoSupport().postEdit( undoAddAttribute);

		 			tree.setSelectionPath( new TreePath( attributeNode.getPath()));
		 		}
		 	}
			
		 	designer.updateNavigator();
		 	designer.selectionChanged();
			designer.selectText();
	 	}
 	}
	
 	private class UndoableAddAttribute extends UndoableDesignerEdit {
 		ElementNode parent = null;
 		AttributeNode node = null;
 		Object before = null;
 		Object after = null;
 		Object childBefore = null;
 		Object childAfter = null;
 		
 		public UndoableAddAttribute( AttributeNode node) {
			this.node = node;
 			this.parent = node.getParentElementNode();
 			before = parent.takeSnapShot();
	 		childBefore = node.takeSnapShot();
 		}
 		
 		public void undo() {
// 			System.out.println( "UndoableAddAttribute.undo()");

 			super.undo();

 			after = parent.takeSnapShot();
 			childAfter = node.takeSnapShot();
 			node.setSnapShot( childBefore);
 			parent.setSnapShot( before);

 			AddNodeAction.this.designer.updateNavigator();
	 		designer.setSelectedNode( node);
 		
//			System.out.println( "UndoableAddAttribute ["+canUndo()+"]["+canRedo()+"]");
 		}

 		public void redo() {
// 			System.out.println( "UndoableAddAttribute.redo()");

 			super.redo();
 			
 			node.setSnapShot( childAfter);
 			parent.setSnapShot( after);
			
 			AddNodeAction.this.designer.updateNavigator();
			designer.setSelectedNode( node);

//	 		System.out.println( "UndoableAddAttribute ["+canUndo()+"]["+canRedo()+"]");
 		}
 		
 		public String getUndoPresentationName() {
 			return "Undo Add Attribute ("+node.getName()+")";
 		}

 		public String getRedoPresentationName() {
 			return "Redo Add Attribute ("+node.getName()+")";
 		}
 	}
	
	private class UndoableAddElement extends UndoableDesignerEdit {
		ElementNode parent = null;
		ElementNode node = null;
		Object before = null;
		Object after = null;
		Object childBefore = null;
		Object childAfter = null;
		
		public UndoableAddElement( ElementNode node) {
			this.node = node;
			this.parent = node.getParentElementNode();
			before = parent.takeSnapShot();
			childBefore = node.takeSnapShot();
		}
		
		public void undo() {
//			System.out.println( "UndoableAddElement.undo()");
			super.undo();

			after = parent.takeSnapShot();
			childAfter = node.takeSnapShot();
			node.setSnapShot( childBefore);
			parent.setSnapShot( before);

 			AddNodeAction.this.designer.updateNavigator();
			designer.setSelectedNode( node);
		}

		public void redo() {
//			System.out.println( "UndoableAddElement.redo()");
			super.redo();
			
			node.setSnapShot( childAfter);
			parent.setSnapShot( after);
	
 			AddNodeAction.this.designer.updateNavigator();
			designer.setSelectedNode( node);
		}
		
		public String getUndoPresentationName() {
			return "Undo Add Element ("+node.getName()+")";
		}

		public String getRedoPresentationName() {
			return "Redo Add Element ("+node.getName()+")";
		}
	}
	
	public void cleanup() {
		designer = null;
		substitutionDialog = null;
		parent = null;
	}
}
