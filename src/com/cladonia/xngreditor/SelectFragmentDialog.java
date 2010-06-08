/*
 * $Id: SelectFragmentDialog.java,v 1.5 2004/11/03 17:47:46 edankert Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.event.DoubleClickListener;

import com.cladonia.xngreditor.grammar.FragmentProperties;

/**
 * The Select Document dialog.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/11/03 17:47:46 $
 * @author Dogs bay
 */
public class SelectFragmentDialog extends JDialog {
	
	private static final Dimension SIZE = new Dimension( 300, 200);
	
	private boolean cancelled	= false;
	private JFrame parent		= null;
	private JPanel main	= null;
	private JList list = null;
	private DefaultListModel model = null;
	private ArrayList tabPanes = null;
	private int selectedIndex = -1;
	
	private ArrayList images;
    private ArrayList titleStrings;
	
	
	/**
	 * The Select Document dialog.
	 *
	 * @param parent the parent frame.
	 */
	public SelectFragmentDialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Select Fragment");
		
		main = new JPanel( new BorderLayout());
		
		model = new DefaultListModel();

		// create the list with a dynamic model
		list = new JList( model);
		
		// only allow one selection
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer( new FragmentListCellRenderer());
		
		list.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelled = true;
				//setVisible(false);
				hide();
			}
		});
		list.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");

		list.getActionMap().put( "enterAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelled = false;
				//setVisible(false);
				hide();
			}
		});
		list.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false), "enterAction");

		list.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				cancelled = false;
				//setVisible(false);
				hide();
			}
		});
		
		list.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = list.getSelectedIndex();
                if(selected>-1) {
                    list.ensureIndexIsVisible(selected);
                }
            }
		    
		});

		// add the list to a scrollpane
		JScrollPane scroll = new JScrollPane( list);
//		JScrollPane scroll = new JScrollPane( new ScrollableListPanel( list));
		scroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground( list.getBackground());
		
		// add the scrollpane to the main panel
		main.add( scroll,BorderLayout.CENTER);

		// listen for a dialog close
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				//setVisible(false);
				hide();
			}
		});

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
		
//		setSize( SIZE);

//		setLocationRelativeTo( parent);
	}
	
	
	/**
	 * Initialises and shows the dialog
	 *
	 * @param tab The JTabbedPane
     * @param current The current tab index
     * @param previous The previous tab index
	 */
	public void show( Vector fragments) {
		fragments = sort( fragments);

		if ( fragments != null && fragments.size() > 0) {
			model.removeAllElements();
			
			// intialise the list
			for ( int i = 0; i < fragments.size(); i++) {
				model.addElement( fragments.elementAt(i));
			}
			
			list.setSelectedIndex(0);
			
//			if ( fragments.size() > 1) {
				if ( fragments.size() > 15) {
					list.setVisibleRowCount(15);
				} else {
					list.setVisibleRowCount( fragments.size());
				}
				
				pack();
				
				if ( getSize().width > 400) {
					setSize( new Dimension( 400, getSize().height));  
				} else if ( getSize().width < 250) {
					setSize( new Dimension( 250, getSize().height));  
				} 
		
				setLocationRelativeTo( parent);

				//super.setVisible(true);
				super.show();
//			} else {
//				cancelled = false;
//			}
		} else {
			cancelled = true;
		}
	}
	
	private Vector sort( Vector fragments) {
		Vector result = new Vector();
		
		for ( int i = 0; i < fragments.size(); i++) {
			FragmentProperties fragment = (FragmentProperties)fragments.elementAt(i);
			int index = -1;

			for ( int j = 0; j < result.size() && index == -1; j++) {
				// Compare by order
				if ( fragment.getOrder() <= ((FragmentProperties)result.elementAt(j)).getOrder()) {
					index = j;
				}
			}
			
			if ( index != -1) {
				result.insertElementAt( fragment, index);
			} else {
				result.addElement( fragment);
			}
		}
		
		return result;
	}
	
	public FragmentProperties getSelectedFragment() {
		return (FragmentProperties)list.getSelectedValue();
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	class FragmentListCellRenderer extends JPanel implements ListCellRenderer {
		private JLabel fragment 	= null;
		private JLabel shortcut		= null;
		
		public FragmentListCellRenderer() {
			super( new BorderLayout());
			
			setBorder( new EmptyBorder( 1, 2, 1, 5));
			setOpaque( true);
			
			fragment = new JLabel();
			fragment.setOpaque( false);
			
			shortcut = new JLabel();
			shortcut.setOpaque( false);
			
			this.add( fragment, BorderLayout.CENTER);
			this.add( shortcut, BorderLayout.EAST);
		}
		
		public Component getListCellRendererComponent(JList list,Object value,int selectedIndex,boolean isSelected, boolean cellHasFocus) {	
			if ( value instanceof FragmentProperties) {
				FragmentProperties f = (FragmentProperties)value;
				fragment.setText( f.getName());
				setIcon( f.getIcon());
				String key = f.getKey();

				if ( key != null && key.length() > 0) {
					shortcut.setText( "("+key+")");
				} else {
					shortcut.setText("");
				}
			}
			
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				shortcut.setForeground(list.getSelectionForeground());
				fragment.setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				shortcut.setForeground(list.getForeground());
				fragment.setForeground(list.getForeground());
			}

			setEnabled(list.isEnabled());
			
			fragment.setFont( list.getFont().deriveFont( Font.BOLD));
			shortcut.setFont( list.getFont().deriveFont( Font.PLAIN, list.getFont().getSize()-2));

			return this;
		}

		private void setIcon( String location) {
			ImageIcon icon = null;
			
			try {
				icon = XngrImageLoader.get().getImage( new URL( location));

				if ( icon.getIconHeight() != 16 || icon.getIconWidth() != 16) {
					icon = new ImageIcon( icon.getImage().getScaledInstance( 16, 16, Image.SCALE_SMOOTH));
				}
			} catch (Exception e) {
				icon = null;
			}
			
			if ( icon == null) {
				icon = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/DefaultFragmentIcon.gif");
			}
			
			fragment.setIcon( icon);
		}
	}
}
