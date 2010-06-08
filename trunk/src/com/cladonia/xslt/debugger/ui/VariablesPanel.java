/*
 * $Id: VariablesPanel.java,v 1.11 2004/09/30 11:29:32 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xslt.debugger.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.cladonia.xngreditor.XngrImageLoader;

/**
 * This VariablesPanel is used to ...
 *
 * @version $Revision: 1.11 $, $Date: 2004/09/30 11:29:32 $
 * @author Dogsbay
 */
public class VariablesPanel extends DetailsPanel {
	private XSLTDebuggerPane debugger = null;
	private static final ImageIcon ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/Variable16.gif");

	private JList globalList = null;
	private VariableListModel globalModel = null;
	private VariableListModel localModel = null;
	private VariableCellRenderer cellRenderer = null;

	/**
	 * Constructs a new Variables Panel
	 */
	public VariablesPanel( XSLTDebuggerPane debugger) {
		super( "Variables", ICON);

		this.debugger = debugger;
		
		globalModel = new VariableListModel();

		globalList = new JList();
		globalList.setModel( globalModel);
		cellRenderer = new VariableCellRenderer( true);
		globalList.setCellRenderer( cellRenderer);
		globalList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = globalList.getSelectedIndex();
                if(selected>-1) {
                    globalList.ensureIndexIsVisible(selected);
                }
            }
		    
		});


		JScrollPane scroller = new JScrollPane(	globalList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize( new Dimension( 100, 100));
		
		scroller.getViewport().setBackground( globalList.getBackground());
		JPanel globalPanel = new JPanel( new BorderLayout());
		globalPanel.add( scroller, BorderLayout.CENTER);
		
		localModel = new VariableListModel();
		
		JList localList = new JList();
		localList.setModel( localModel);
		localList.setCellRenderer( new VariableCellRenderer( false));
		
		scroller = new JScrollPane(	localList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize( new Dimension( 100, 100));
		scroller.getViewport().setBackground( localList.getBackground());
		
		
		JPanel localPanel = new JPanel( new BorderLayout());
		localPanel.add( scroller, BorderLayout.CENTER);
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.setFont( tabs.getFont().deriveFont( Font.PLAIN));
		tabs.add( "Global", globalPanel);
		tabs.add( "Local", localPanel);
		
		setCenterComponent( tabs);
	}
	
	public void setLocalVariables( Vector variables) {
//		System.out.println( "VariablesPanel.setLocalVariables( "+variables+")");
		localModel.setVariables( variables);
	}

	public void setGlobalVariables( Vector variables) {
//		System.out.println( "VariablesPanel.setGlobalVariables( "+variables+")");
		globalModel.setVariables( variables);
	}
	
	public void reset() {
		globalModel.setVariables( null);
		localModel.setVariables( null);
	}
	
	public void updatePreferences() {
//		cellRenderer.updatePreferences();
	}

	private class VariableListModel extends AbstractListModel {
		Vector variables = null;
		
		public void setVariables( Vector variables) {
			this.variables = variables;
			if ( variables != null) {
				fireContentsChanged( this, 0, variables.size()-1);
			} else {
				fireContentsChanged( this, 0, 0);
			}
		}
		
		public Object getElementAt( int i) {
			if ( variables != null) {
				return variables.elementAt(i);
			}
			
			return null;
		}
		
		public int getSize() {
			if ( variables != null) {
				return variables.size();
			}
			
			return 0;
		}
	}

//	private class VariableTree extends QTree {
//		
//		public VariableTree() {
//			super ( new DefaultTreeModel( new DefaultMutableTreeNode()));
//
//			setBorder( new EmptyBorder( 2, 2, 2, 2));
//	
//			setModel( treeModel);
//	
//			ToolTipManager.sharedInstance().registerComponent( this);
//			setShowsRootHandles( false);
//			putClientProperty( "JTree.lineStyle", "None");
//			getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION);
//	
//			VariableCellRenderer renderer = new VariableCellRenderer();
//			setCellRenderer( renderer);
//			setRootVisible( false);
//			setExpandsSelectedPaths( true);
//		}
//		
//		public void setVariables( boolean global, Vector variables) {
//			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
//			DefaultTreeModel model = (DefaultTreeModel)getModel();
//
//			for ( int i = 0; i < variables.size(); i++) {
//				root.add( new VariableNode( (XSLTVariable)variables.elementAt(i), global));
//			}
//
//			model.setRoot( root);
//			model.reload();
//		}
//	}
}
