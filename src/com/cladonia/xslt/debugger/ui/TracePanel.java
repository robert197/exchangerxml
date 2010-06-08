/*
 * $Id: TracePanel.java,v 1.12 2004/09/30 11:29:32 edankert Exp $
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
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.bounce.QTree;
import org.bounce.event.DoubleClickListener;

import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.DefaultStackItem;
import com.cladonia.xslt.debugger.XSLTemplateStackItem;

/**
 * This TracePanel, shows the style, input and output traces.
 *
 * @version $Revision: 1.12 $, $Date: 2004/09/30 11:29:32 $
 * @author Dogsbay
 */
public class TracePanel extends DetailsPanel {
	private static final ImageIcon ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/Trace16.gif");

	private XSLTDebuggerPane debugger = null;

//	private TemplateTraceTableModel styleModel = null;
	private TraceTree styleTree = null;
	private TraceTree inputTree = null;
	private TraceTree outputTree = null;
	private TraceTree mixedTree = null;

	/**
	 * Constructs a new Trace Panel
	 */
	public TracePanel( XSLTDebuggerPane debugger) {
		super( "Trace", ICON);

		this.debugger = debugger;

//		styleModel = new TemplateTraceTableModel();
		styleTree = new TraceTree();
		styleTree.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				TracePanel.this.doubleClicked( e);
			}
		});
		
		JScrollPane scroller = new JScrollPane(	styleTree,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize( new Dimension( 100, 100));
		
		scroller.getViewport().setBackground( styleTree.getBackground());

		JPanel stylePanel = new JPanel( new BorderLayout());
		stylePanel.add( scroller, BorderLayout.CENTER);
		
		inputTree = new TraceTree();
		inputTree.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				TracePanel.this.doubleClicked( e);
			}
		});
		scroller = new JScrollPane(	inputTree,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize( new Dimension( 100, 100));
		
		scroller.getViewport().setBackground( inputTree.getBackground());
		JPanel inputPanel = new JPanel( new BorderLayout());
		inputPanel.add( scroller, BorderLayout.CENTER);
		
		outputTree = new TraceTree();
		outputTree.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				TracePanel.this.doubleClicked( e);
			}
		});

		scroller = new JScrollPane(	outputTree,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize( new Dimension( 100, 100));
		
		scroller.getViewport().setBackground( outputTree.getBackground());
		JPanel outputPanel = new JPanel( new BorderLayout());
		outputPanel.add( scroller, BorderLayout.CENTER);

		mixedTree = new TraceTree();
		mixedTree.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				TracePanel.this.doubleClicked( e);
			}
		});

		scroller = new JScrollPane(	mixedTree,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize( new Dimension( 100, 100));
		
		scroller.getViewport().setBackground( mixedTree.getBackground());
		JPanel mixedPanel = new JPanel( new BorderLayout());
		mixedPanel.add( scroller, BorderLayout.CENTER);

		JTabbedPane tabs = new JTabbedPane();
		tabs.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.setFont( tabs.getFont().deriveFont( Font.PLAIN));
		tabs.add( "Style", stylePanel);
		tabs.add( "Input", inputPanel);
		tabs.add( "Output", outputPanel);
		tabs.add( "Mixed", mixedPanel);
		
		setCenterComponent( tabs);
	}
	
	public void setStyleTrace( Vector trace) {
//		System.out.println( "TracesPanel.setStyleTrace( "+trace+")");
		styleTree.setTrace( trace);
	}

	public void setInputTrace( Vector trace) {
//		System.out.println( "TracesPanel.setInputTrace( "+trace+")");
		inputTree.setTrace( trace);
	}

	public void setOutputTrace( Vector trace) {
//		System.out.println( "TracesPanel.setOutputTrace( "+trace+")");
		outputTree.setTrace( trace);
	}
	
	public void setMixedTrace( Vector trace) {
//		System.out.println( "TracesPanel.setMixedTrace( "+trace+")");
		mixedTree.setTrace( trace);
	}

	public void doubleClicked( MouseEvent e) {
		QTree tree = (QTree)e.getSource();
		TraceNode node = (TraceNode)tree.getSelectedNode();
		
		if ( node != null) {
			Object item = node.getStackItem();
			
			if ( item instanceof XSLTemplateStackItem) {
				String document = ((XSLTemplateStackItem)item).get_filename();
				int line = ((XSLTemplateStackItem)item).get_lineNum();
				int column = ((XSLTemplateStackItem)item).get_colNum();
				String name = ((XSLTemplateStackItem)item).get_name();

				debugger.getXSLTView().select( document, line, column, name, true);
			} else if ( item instanceof DefaultStackItem) {
				String document = ((DefaultStackItem)item).get_filename();
				int line = ((DefaultStackItem)item).get_lineNum();
				int column = ((DefaultStackItem)item).get_colNum();
				int type = ((DefaultStackItem)item).get_type();
				String name = ((DefaultStackItem)item).get_name();

				if ( type == 1) {
					debugger.getXSLTView().select( document, line, column, name, true);
				} else if ( type == 3) {
					debugger.getInputView().select( document, line, column, name, true);
				} else if ( type == 5) {
					debugger.getXSLTView().select( document, line, column, name, true);
				} else {
					System.out.println( "ERROR: Unknown Trace Item Type: "+type);
				}
			}
		}
	}

	public void reset() {
		styleTree.setTrace( null);
		inputTree.setTrace( null);
		outputTree.setTrace( null);
		mixedTree.setTrace( null);
	}

//	private class TemplateTraceTableModel extends AbstractTableModel {
//		private Vector trace = null;
//		
//		public TemplateTraceTableModel() {
//			trace = new Vector();
//		}
//		
//		private void setTrace( Vector trace) {
//			this.trace = trace;
//			
//			fireTableDataChanged();
//		}
//
//		public int getRowCount() {
//			if ( trace != null)	{
//				return trace.size();
//			} else {
//				return 0;
//			}
//		}
//		
////		public XSLTVariable getVariable( int row) {
////			XSLTVariable result = null;
////			
////			if ( variables.size() >= row) {
////				result = (XSLTVariable)variables.elementAt( row);
////			}
////
////			return result;
////		}
//
//		public String getColumnName( int column) {
//			String name = "";
//
//			if ( column == 0) {
//				name = "Name";
//			} else if ( column == 1) {
//				name = "Match";
//			} else if ( column == 2) {
//				name = "Priority";
//			} else if ( column == 3) {
//				name = "Mode";
//			}
//			
//			return name;
//		}
//
//		public Class getColumnClass( int column) {
//			return String.class;
//		}
//
//		public int getColumnCount() {
//			return 4;
//		}
//
////		public String getName( int row) {
////			return ((XSLTVariable)variables.elementAt( row)).getName();
////		}
//
//		public Object getValueAt( int row, int column) {
//			Object result = null;
//			
//			if ( column == 0) {
//				result = ((XSLTemplateStackItem)trace.elementAt( row)).get_name();
//			} else if ( column == 1) {
//				result = ((XSLTemplateStackItem)trace.elementAt( row)).get_match();
//			} else if ( column == 2) {
//				result = ((XSLTemplateStackItem)trace.elementAt( row)).get_priority();
//			} else if ( column == 3) {
//				result = ((XSLTemplateStackItem)trace.elementAt( row)).get_mode();
//			}
//			
//			return result;
//		}
//	}

//	private class DefaultTraceTableModel extends AbstractTableModel {
//		private Vector trace = null;
//		
//		public DefaultTraceTableModel() {
//			trace = new Vector();
//		}
//		
//		private void setTrace( Vector trace) {
//			this.trace = trace;
//			
//			fireTableDataChanged();
//		}
//
//		public int getRowCount() {
//			if ( trace != null)	{
//				return trace.size();
//			} else {
//				return 0;
//			}
//		}
//		
////		public XSLTVariable getVariable( int row) {
////			XSLTVariable result = null;
////			
////			if ( variables.size() >= row) {
////				result = (XSLTVariable)variables.elementAt( row);
////			}
////
////			return result;
////		}
//
//		public String getColumnName( int column) {
//			String name = "";
//
//			if ( column == 0) {
//				name = "Name";
//			} else if ( column == 1) {
//				name = "Type";
//			}
//			
//			return name;
//		}
//
//		public Class getColumnClass( int column) {
//			return String.class;
//		}
//
//		public int getColumnCount() {
//			return 2;
//		}
//
////		public String getName( int row) {
////			return ((XSLTVariable)variables.elementAt( row)).getName();
////		}
//
//		public Object getValueAt( int row, int column) {
//			Object result = null;
//			
//			if ( column == 0) {
//				result = ((DefaultStackItem)trace.elementAt( row)).get_name();
//			} else if ( column == 1) {
//				result = ""+((DefaultStackItem)trace.elementAt( row)).get_type();
//			}
//			
//			return result;
//		}
//	}

	private class TraceTree extends QTree {
		private TraceNode lastNode = null;

		public TraceTree() {
			super ( new DefaultTreeModel( new DefaultMutableTreeNode()));
	
			setBorder( new EmptyBorder( 2, 2, 2, 2));
	
			setModel( treeModel);
	
			ToolTipManager.sharedInstance().registerComponent( this);
			setShowsRootHandles( true);
			putClientProperty( "JTree.lineStyle", "None");
			getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION);
	
			StackItemCellRenderer renderer = new StackItemCellRenderer();
			setCellRenderer( renderer);
			setRootVisible( false);
			setExpandsSelectedPaths( true);
			setToggleClickCount( 3);
		}
		
		public void setTrace( Vector trace) {
			lastNode = null;

			DefaultTreeModel model = (DefaultTreeModel)getModel();
			TraceNode root = null;

			if ( trace != null && trace.size() > 0) {
				root = new TraceNode( null);
				
//				for ( int i = 1; i < trace.size(); i++) {
				addNodes( root, trace, 0);
//				}
			} else {
				root = new TraceNode( null);
			}
	
			model.setRoot( root);
			model.reload();
			
			expand( 4);
			
			if ( lastNode != null) {
				setSelectedNode( lastNode);
				scrollPathToVisible( new TreePath( lastNode.getPath()));
			}
		}
		
		private int addNodes( TraceNode parent, Vector trace, int index) {
			for ( int i = index; i < trace.size(); i++) {
				Object item = trace.elementAt(i);
				int type = -1;
				
				if ( item instanceof DefaultStackItem) {
					type = ((DefaultStackItem)item).get_type();
				}
				
				if ( type == 2 || type == 4 || type == 6) {
//					System.out.println( "End-tag: "+((DefaultStackItem)item).get_name());
					// end-tags.
					return i;
				} else {
//					System.out.println( "Start-tag: "+((DefaultStackItem)item).get_name());

					TraceNode node = new TraceNode( item);
					parent.add( node);

					lastNode = node;

					i = addNodes( node, trace, i+1);
				}
			}
			
			return trace.size();
		}
	}
}
