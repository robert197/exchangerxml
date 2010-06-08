/*
 * $Id: TransformationPanel.java,v 1.4 2004/10/13 18:37:42 edankert Exp $
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
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.bounce.event.DoubleClickListener;
import org.bounce.event.PopupListener;

import com.cladonia.xngreditor.component.GUIUtilities;
import com.cladonia.xngreditor.scenario.*;
import com.cladonia.xslt.debugger.Breakpoint;
import com.cladonia.xslt.debugger.BreakpointList;

/**
 * This transformationPanel, shows the transformation breakpoints and parameters.
 *
 * @version $Revision: 1.4 $, $Date: 2004/10/13 18:37:42 $
 * @author Dogsbay
 */
public class TransformationPanel extends JPanel {
	private JPopupMenu popup = null;

	private XSLTDebuggerPane debugger = null;
	private XSLTTransformation transformation = null;
	
	private JTabbedPane breakpointTabs = null;
	
	private JSplitPane breakpointSplit = null;

	private BreakpointListModel styleBreakpointsModel = null;
	private JList styleBreakpointList = null;
	private BreakpointListModel inputBreakpointsModel = null;
	private JList inputBreakpointList = null;

	private ParameterListModel parametersModel = null;

	/**
	 * Constructs a new Variables Panel
	 */
	public TransformationPanel( XSLTDebuggerPane pane) {
		super( new BorderLayout());

		this.debugger = pane;

		parametersModel = new ParameterListModel();

		JList parameterList = new JList();
		parameterList.setModel( parametersModel);
		parameterList.setCellRenderer( new ParameterCellRenderer());

		JScrollPane scroller = new JScrollPane(	parameterList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize( new Dimension( 100, 100));
		
		scroller.getViewport().setBackground( parameterList.getBackground());
		DetailsPanel paramPanel = new DetailsPanel( "Parameters:", null, false);
		paramPanel.setCenterComponent( scroller);

		styleBreakpointsModel = new BreakpointListModel();

		styleBreakpointList = new JList();
		styleBreakpointList.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				int index[] = styleBreakpointList.getSelectedIndices();
				
				for ( int i = 0; i < index.length; i++)	{
					Breakpoint bp = (Breakpoint)styleBreakpointsModel.getElementAt( index[i]);
					debugger.getXSLTView().selectLine( bp.getFilename(), bp.getLineNumber());
				}
			}
		});
		styleBreakpointList.addMouseListener( new PopupListener() {
			public void popupTriggered( MouseEvent e) {
				showPopupMenu( e);
			}
		});
		
		styleBreakpointList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = styleBreakpointList.getSelectedIndex();
                if(selected>-1) {
                    styleBreakpointList.ensureIndexIsVisible(selected);
                }
            }
		    
		});

		styleBreakpointList.setModel( styleBreakpointsModel);
		styleBreakpointList.setCellRenderer( new BreakpointCellRenderer());

		JScrollPane styleScroller = new JScrollPane(	styleBreakpointList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		styleScroller.setPreferredSize( new Dimension( 100, 100));
		
		styleScroller.getViewport().setBackground( styleBreakpointList.getBackground());
		
		inputBreakpointsModel = new BreakpointListModel();

		inputBreakpointList = new JList();
		inputBreakpointList.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				int index[] = inputBreakpointList.getSelectedIndices();
				
				for ( int i = 0; i < index.length; i++)	{
					Breakpoint bp = (Breakpoint)inputBreakpointsModel.getElementAt( index[i]);
					debugger.getInputView().selectLine( bp.getFilename(), bp.getLineNumber());
				}
			}
		});
		inputBreakpointList.addMouseListener( new PopupListener() {
			public void popupTriggered( MouseEvent e) {
				showPopupMenu( e);
			}
		});
		inputBreakpointList.setModel( inputBreakpointsModel);
		inputBreakpointList.setCellRenderer( new BreakpointCellRenderer());

		JScrollPane inputScroller = new JScrollPane(	inputBreakpointList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		inputScroller.setPreferredSize( new Dimension( 100, 100));
		
		inputScroller.getViewport().setBackground( inputBreakpointList.getBackground());

		breakpointTabs = new JTabbedPane();
		breakpointTabs.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT);
		breakpointTabs.setFont( breakpointTabs.getFont().deriveFont( Font.PLAIN));
		breakpointTabs.addTab( "Style", styleScroller);
		breakpointTabs.addTab( "Input", inputScroller);

		DetailsPanel bpPanel = new DetailsPanel( "Breakpoints:", null);
		bpPanel.setCenterComponent( breakpointTabs);
		
		breakpointSplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT, paramPanel, bpPanel);
//		split.setOneTouchExpandable( true);
		breakpointSplit.setBorder( null);

		breakpointSplit.setResizeWeight( 0.5);

		if ( breakpointSplit.getDividerSize() > 6) {
			breakpointSplit.setDividerSize( 6);
		}

		ui = breakpointSplit.getUI();
		if ( ui instanceof BasicSplitPaneUI) {
			((BasicSplitPaneUI)ui).getDivider().setBorder( null);
		}

		this.add( breakpointSplit, BorderLayout.CENTER);
	}
	
	private void showPopupMenu( MouseEvent e) {
		getPopupMenu().show( (JList)e.getSource(), e.getX(), e.getY());
	}
	
	public void setTransformation( XSLTTransformation transformation) {
//		System.out.println( "setTransformation( "+transformation+")");
		this.transformation = transformation;
	
		if ( transformation != null) {
			parametersModel.setParameters( transformation.getParameters());
			setStyleBreakpoints( transformation.getStyleBreakpoints());
			setInputBreakpoints( transformation.getInputBreakpoints());
		} else {
			parametersModel.setParameters( null);
			styleBreakpointsModel.setBreakpoints( null);
			inputBreakpointsModel.setBreakpoints( null);
		}
	}

	public int getBreakpointsSplitLocation() {
		return breakpointSplit.getDividerLocation();
	}

	public void setBreakpointsSplitLocation( int location) {
		breakpointSplit.setDividerLocation( location);
	}


	public void setInputBreakpoints( BreakpointList breakpoints) {
//		System.out.println( "setInputBreakpoints( "+breakpoints+")");
		inputBreakpointsModel.setBreakpoints( breakpoints);
	}
	
	public void setStyleBreakpoints( BreakpointList breakpoints) {
//		System.out.println( "setStyleBreakpoints( "+breakpoints+")");
		styleBreakpointsModel.setBreakpoints( breakpoints);
	}
	
	private JPopupMenu getPopupMenu() {
		if ( popup == null) {
			popup = new JPopupMenu();
			popup.add( new EnableBreakpointsAction());
			popup.add( new DisableBreakpointsAction());
			popup.addSeparator();
			popup.add( new RemoveBreakpointsAction());

			GUIUtilities.alignMenu( popup);
		}
		
		return popup;
	}

	private class BreakpointListModel extends AbstractListModel {
		BreakpointList breakpointList = null;
		Vector breakpoints = null;
		
		public void setBreakpoints( BreakpointList breakpointList) {
			this.breakpointList = breakpointList;
			this.breakpoints = new Vector();

			if ( breakpointList != null) {
				Hashtable table = new Hashtable();
				Vector bps = breakpointList.getBreakpoints();
			
				for ( int i = 0; i < bps.size(); i++) {
					Breakpoint bp = (Breakpoint)bps.elementAt(i);
					Vector list = (Vector)table.get( bp.getFilename());
					
					if ( list == null) {
						list = new Vector();
						list.addElement( bp);
						table.put( bp.getFilename(),list);
					} else {
						boolean inserted = false;
	
						for ( int j = 0; j < list.size(); j++) {
							Breakpoint b = (Breakpoint)list.elementAt(j);
							
							if ( b.getLineNumber() > bp.getLineNumber()) {
								list.insertElementAt( bp, j);
								inserted = true;
								break;
							}
						}
						
						if ( !inserted) {
							list.addElement( bp);
						}
					}
				}
				
				Collection values = table.values();
				
				if ( values != null) {
					Iterator iterator = values.iterator();
		
					while( iterator.hasNext()) {
						Vector list = (Vector)iterator.next();
						
						for ( int i = 0; i < list.size(); i++) {
							this.breakpoints.addElement( list.elementAt(i));
						}
					}
				}

				fireContentsChanged( this, 0, this.breakpoints.size()-1);
			} else {
				fireContentsChanged( this, 0, 0);
			}			
		}
		
		public void removeBreakpoint( Breakpoint breakpoint) {
			int index = breakpoints.indexOf( breakpoint);
			breakpoints.removeElement( breakpoint);
			breakpointList.removeBreakpoint( breakpoint);

			fireIntervalRemoved( this, index, index);
		}

		public Object getElementAt( int i) {
			if ( breakpoints != null) {
				return breakpoints.elementAt(i);
			}
			
			return null;
		}
		
		public int getSize() {
			if ( breakpoints != null) {
				return breakpoints.size();
			}
			
			return 0;
		}
	}
	
	private class ParameterListModel extends AbstractListModel {
		Vector parameters = null;
		
		public void setParameters( Vector params) {
			this.parameters = params;
			
			if ( parameters != null) {
				fireContentsChanged( this, 0, this.parameters.size()-1);
			} else {
				fireContentsChanged( this, 0, 0);
			}
		}
		
		public Object getElementAt( int i) {
			if ( parameters != null) {
				return parameters.elementAt(i);
			}
			
			return null;
		}
		
		public int getSize() {
			if ( parameters != null) {
				return parameters.size();
			}
			
			return 0;
		}
	}
	
	 private class EnableBreakpointsAction extends AbstractAction {
	 	public EnableBreakpointsAction() {
	 		super( "Enable Breakpoint(s)");
	 	}
	 	
	 	public void actionPerformed( ActionEvent e) {
	 		int index = breakpointTabs.getSelectedIndex();
	 		if ( index == 0) { // Style
	 			Object values[] = styleBreakpointList.getSelectedValues();
	 			
	 			for ( int i = 0; i < values.length; i++) {
	 				((Breakpoint)values[i]).setEnabled( true);
	 			}
	 			
	 			debugger.styleBreakPointsUpdated();
	 		} else if ( index == 1) { // Input
	 			Object values[] = inputBreakpointList.getSelectedValues();
	 			
	 			for ( int i = 0; i < values.length; i++) {
	 				((Breakpoint)values[i]).setEnabled( true);
	 			}

	 			debugger.inputBreakPointsUpdated();
	 		}
	 	}
	 }

	 private class DisableBreakpointsAction extends AbstractAction {
	 	public DisableBreakpointsAction() {
	 		super( "Disable Breakpoint(s)");
	 	}
	 	
	 	public void actionPerformed( ActionEvent e) {
	 		int index = breakpointTabs.getSelectedIndex();
	 		if ( index == 0) { // Style
	 			Object values[] = styleBreakpointList.getSelectedValues();
	 			
	 			for ( int i = 0; i < values.length; i++) {
	 				((Breakpoint)values[i]).setEnabled( false);
	 			}
	 			
	 			debugger.styleBreakPointsUpdated();
	 		} else if ( index == 1) { // Input
	 			Object values[] = inputBreakpointList.getSelectedValues();
	 			
	 			for ( int i = 0; i < values.length; i++) {
	 				((Breakpoint)values[i]).setEnabled( false);
	 			}

	 			debugger.inputBreakPointsUpdated();
	 		}
	 	}
	 }

	 private class RemoveBreakpointsAction extends AbstractAction {
	 	public RemoveBreakpointsAction() {
	 		super( "Remove Breakpoint(s)");
	 	}
	 	
	 	public void actionPerformed( ActionEvent e) {
	 		int index = breakpointTabs.getSelectedIndex();
	 		if ( index == 0) { // Style
	 			Object values[] = styleBreakpointList.getSelectedValues();
	 			
	 			for ( int i = 0; i < values.length; i++) {
	 				styleBreakpointsModel.removeBreakpoint( (Breakpoint)values[i]);
	 			}
	 			
	 			debugger.styleBreakPointsUpdated();
	 		} else if ( index == 1) { // Input
	 			Object values[] = inputBreakpointList.getSelectedValues();
	 			
	 			for ( int i = 0; i < values.length; i++) {
	 				inputBreakpointsModel.removeBreakpoint( (Breakpoint)values[i]);
	 			}

	 			debugger.inputBreakPointsUpdated();
	 		}
	 	}
	 }
}
