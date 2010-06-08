/*
 * $Id: CreateRequiredAction.java,v 1.2 2004/10/23 15:04:52 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer.actions;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.designer.DesignerNode;
import com.cladonia.xml.designer.ElementNode;
import com.cladonia.xml.designer.UndoableDesignerEdit;

/**
 * Adds a node to the Designer panel.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/23 15:04:52 $
 * @author Dogsbay
 */
 public class CreateRequiredAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private Designer designer = null;

 	/**
	 * The constructor for the action which allows 
	 * for adding nodes to the Designer.
	 *
	 * @param designer the designer.
	 */
 	public CreateRequiredAction( Designer designer) {
 		super( "Create Required");
		
		this.designer = designer;
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
				
	 			if ( !elementNode.isVirtual()) {
		 			UndoableFillRequired undoFillRequired = new UndoableFillRequired( elementNode);

					elementNode.createRequired();
					designer.getUndoSupport().postEdit( undoFillRequired);

	 				tree.setSelectionPath( new TreePath( elementNode.getPath()));
	 			}

			 	designer.updateNavigator();
		 		designer.selectionChanged();
	 		}
	 	}
 	}
	
	private class UndoableFillRequired extends UndoableDesignerEdit {
		ElementNode node = null;
		Object before = null;
		Object after = null;
		Vector childrenBefore = null;
		Vector childrenAfter = null;
		
		public UndoableFillRequired( ElementNode node) {
			this.node = node;
			before = node.takeSnapShot();
			
			childrenBefore = new Vector();
			
			Enumeration nodes = node.children();
			while ( nodes.hasMoreElements()) {
				DesignerNode n = (DesignerNode)nodes.nextElement();
				
				childrenBefore.addElement( new Object[]  { n, n.takeSnapShot()});
			}
		}
		
		public void undo() {
//			System.out.println( "UndoableFillRequired.undo()");
			super.undo();

			after = node.takeSnapShot();
			childrenAfter = new Vector();
			
			Enumeration nodes = node.children();
			while ( nodes.hasMoreElements()) {
				DesignerNode n = (DesignerNode)nodes.nextElement();
				
				childrenAfter.addElement( new Object[]  { n, n.takeSnapShot()});
			}

			node.setSnapShot( before);
			
			for ( int i = 0; i < childrenBefore.size(); i++) {
				Object[] child = (Object[])childrenBefore.elementAt( i);
				
				((DesignerNode)child[0]).setSnapShot( child[1]);
			}
			
			CreateRequiredAction.this.designer.updateNavigator();
			designer.setSelectedNode( node);
		}

		public void redo() {
//			System.out.println( "UndoableFillRequired.redo()");
			super.redo();
			
			node.setSnapShot( after);

			for ( int i = 0; i < childrenAfter.size(); i++) {
				Object[] child = (Object[])childrenAfter.elementAt( i);
				
				((DesignerNode)child[0]).setSnapShot( child[1]);
			}

			CreateRequiredAction.this.designer.updateNavigator();
			designer.setSelectedNode( node);
		}
		
		public String getUndoPresentationName() {
			return "Undo Fill Required ("+node.getName()+")";
		}

		public String getRedoPresentationName() {
			return "Redo Fill Required ("+node.getName()+")";
		}
	}

	 public void cleanup() {
	 	designer = null;
	 }
}
