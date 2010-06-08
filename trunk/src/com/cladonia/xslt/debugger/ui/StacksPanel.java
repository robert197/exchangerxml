/*
 * $Id: StacksPanel.java,v 1.14 2004/09/30 11:29:32 edankert Exp $
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

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.event.DoubleClickListener;

import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.DefaultStackItem;
import com.cladonia.xslt.debugger.XSLTemplateStackItem;

/**
 * This StacksPanel, shows the style, input and output stacks.
 *
 * @version $Revision: 1.14 $, $Date: 2004/09/30 11:29:32 $
 * @author Dogsbay
 */
public class StacksPanel extends DetailsPanel {
	private static final ImageIcon ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/Stack16.gif");

	private XSLTDebuggerPane debugger = null;

	private JList styleList = null;
	private JList inputList = null;
	private JList outputList = null;
	private JList mixedList = null;

	private StackListModel styleModel = null;
	private StackListModel inputModel = null;
	private StackListModel outputModel = null;
	private StackListModel mixedModel = null;

	/**
	 * Constructs a new Variables Panel
	 */
	public StacksPanel( XSLTDebuggerPane debugger) {
		super( "Stacks",ICON);

		this.debugger = debugger;

		styleModel = new StackListModel();
		
		styleList = new JList( styleModel);
		styleList.setCellRenderer( new StackItemCellRenderer());
		styleList.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				StacksPanel.this.doubleClicked( e);
			}
		});
		
		styleList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = styleList.getSelectedIndex();
                if(selected>-1) {
                    styleList.ensureIndexIsVisible(selected);
                }
            }
		    
		});

		JScrollPane scroller = new JScrollPane(	styleList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize( new Dimension( 100, 100));
		
		scroller.getViewport().setBackground( styleList.getBackground());
		JPanel stylePanel = new JPanel( new BorderLayout());
		stylePanel.add( scroller, BorderLayout.CENTER);
		
		inputModel = new StackListModel();
		
		inputList = new JList( inputModel);
		inputList.setCellRenderer( new StackItemCellRenderer());
		inputList.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				StacksPanel.this.doubleClicked( e);
			}
		});

		scroller = new JScrollPane(	inputList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize( new Dimension( 100, 100));
		
		scroller.getViewport().setBackground( inputList.getBackground());
		JPanel inputPanel = new JPanel( new BorderLayout());
		inputPanel.add( scroller, BorderLayout.CENTER);
		
		outputModel = new StackListModel();

		outputList = new JList( outputModel);
		outputList.setCellRenderer( new StackItemCellRenderer());
		outputList.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				StacksPanel.this.doubleClicked( e);
			}
		});
		
		scroller = new JScrollPane(	outputList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize( new Dimension( 100, 100));
		
		scroller.getViewport().setBackground( outputList.getBackground());
		JPanel outputPanel = new JPanel( new BorderLayout());
		outputPanel.add( scroller, BorderLayout.CENTER);

		mixedModel = new StackListModel();

		mixedList = new JList( mixedModel);
		mixedList.setCellRenderer( new StackItemCellRenderer());
		mixedList.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				StacksPanel.this.doubleClicked( e);
			}
		});

		scroller = new JScrollPane(	mixedList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize( new Dimension( 100, 100));
		
		scroller.getViewport().setBackground( mixedList.getBackground());
		JPanel mixedPanel = new JPanel( new BorderLayout());
		mixedPanel.add( scroller, BorderLayout.CENTER);

		JTabbedPane tabs = new JTabbedPane();
		tabs.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.setFont( tabs.getFont().deriveFont( Font.PLAIN));
		tabs.add( "Template", stylePanel);
		tabs.add( "Input", inputPanel);
		tabs.add( "Output", outputPanel);
		tabs.add( "Mixed", mixedPanel);
		
		setCenterComponent( tabs);
	}
	
	public void setStyleStack( Vector stack) {
//		System.out.println( "StacksPanel.setStyleStack( "+stack+")");
		styleModel.setStack( stack);
		
		int size = styleModel.getSize();
		if ( size > 0) {
			styleList.setSelectedIndex( size-1);
			styleList.ensureIndexIsVisible( size-1);
		}
	}

	public void setInputStack( Vector stack) {
//		System.out.println( "StacksPanel.setInputStack( "+stack+")");
		inputModel.setStack( stack);

		int size = inputModel.getSize();
		
		if ( size > 0) {
			inputList.setSelectedIndex( size-1);
			inputList.ensureIndexIsVisible( size-1);
		}
	}

	public void setOutputStack( Vector stack) {
//		System.out.println( "StacksPanel.setOutputStack( "+stack+")");
		outputModel.setStack( stack);

		int size = outputModel.getSize();

		if ( size > 0) {
			outputList.setSelectedIndex( size-1);
			outputList.ensureIndexIsVisible( size-1);
		}
	}
	
	public void setMixedStack( Vector stack) {
//		System.out.println( "StacksPanel.setMixedStack( "+stack+")");
		mixedModel.setStack( stack);

		int size = mixedModel.getSize();

		if ( size > 0) {
			mixedList.setSelectedIndex( size-1);
			mixedList.ensureIndexIsVisible( size-1);
		}
	}
	
	public void doubleClicked( MouseEvent e) {
		JList list = (JList)e.getSource();
		int row = list.getSelectedIndex();
		StackListModel model = (StackListModel)list.getModel();
		
		if ( row != -1) {
			Object item = model.getElementAt( row);
			
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
					System.out.println( "ERROR: Unknown Stack Item Type: "+type);
				}
			}
		}
	}
	
	public void reset() {
		styleModel.setStack( null);
		inputModel.setStack( null);
		outputModel.setStack( null);
		mixedModel.setStack( null);
	}

	private class StackListModel extends AbstractListModel {
		Vector stack = null;
		
		public void setStack( Vector stack) {
			this.stack = stack;
			if ( stack != null) {
				fireContentsChanged( this, 0, stack.size()-1);
			} else {
				fireContentsChanged( this, 0, 0);
			}
		}
		
		public Object getElementAt( int i) {
			if ( stack != null && stack.size() > i) {
				return stack.elementAt(i);
			}
			
			return null;
		}
		
		public int getSize() {
			if ( stack != null) {
				return stack.size();
			}
			
			return 0;
		}
	}
}
