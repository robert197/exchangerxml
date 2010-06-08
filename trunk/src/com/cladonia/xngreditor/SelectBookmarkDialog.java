/*
 * $Id: SelectBookmarkDialog.java,v 1.2 2004/11/03 17:47:46 edankert Exp $
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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

import org.bounce.event.DoubleClickListener;

import com.cladonia.xml.editor.Bookmark;

/**
 * The Select Document dialog.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/11/03 17:47:46 $
 * @author Dogs bay
 */
public class SelectBookmarkDialog extends JDialog {
	
	private static final ImageIcon BOOKMARK_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Bookmarks16.gif");
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
	public SelectBookmarkDialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Select Bookmark");
		
		main = new JPanel( new BorderLayout());
		
		model = new DefaultListModel();

		// create the list with a dynamic model
		list = new JList( model);
		
		// only allow one selection
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer( new BookmarkCellRenderer());
		
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

		// add the list to a scrollpane
		JScrollPane scroll = new JScrollPane( list);
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
	public void show( Vector bookmarks) {
		bookmarks = sort( bookmarks);

		if ( bookmarks != null && bookmarks.size() > 0) {
			model.removeAllElements();
			
			// intialise the list
			for ( int i = 0; i < bookmarks.size(); i++) {
				model.addElement( bookmarks.elementAt(i));
			}
			
			list.setSelectedIndex(0);
			
			if ( bookmarks.size() > 1) {
				if ( model.getSize() > 15) {
					list.setVisibleRowCount(15);
				} else {
					list.setVisibleRowCount( model.getSize());
				}
				
				pack();
				
				if ( getSize().width > 400) {
					setSize( new Dimension( 400, getSize().height));  
				} 
		
				setLocationRelativeTo( parent);

				//super.setVisible(true);
				super.show();
			} else {
				cancelled = false;
			}
		} else {
			cancelled = true;
		}
	}
	
	private Vector sort( Vector bookmarks) {
		Vector result = new Vector();
		
		for ( int i = 0; i < bookmarks.size(); i++) {
			Bookmark bm = (Bookmark)bookmarks.elementAt(i);
			int index = -1;

			for ( int j = 0; j < result.size() && index == -1; j++) {
				// Compare alphabeticaly
				if ( bm.getLineNumber() < ((Bookmark)result.elementAt(j)).getLineNumber()) {
					index = j;
				}
			}
			
			if ( index != -1) {
				result.insertElementAt( bm, index);
			} else {
				result.addElement( bm);
			}
		}
		
		return result;
	}
	
	public Bookmark getSelectedBookmark() {
		return (Bookmark)list.getSelectedValue();
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	class BookmarkCellRenderer extends JLabel implements ListCellRenderer {
		public BookmarkCellRenderer() {
			setFont( getFont().deriveFont( Font.PLAIN));
		}

		public Component getListCellRendererComponent(JList list,Object value,int selectedIndex,boolean isSelected,      
				boolean cellHasFocus)  {

			if ( value instanceof Bookmark) {
				Bookmark bm = (Bookmark)value;
				
				setText( "["+(bm.getLineNumber()+1)+"] "+bm.getContent());
				
				setIcon( BOOKMARK_ICON);
			}
			
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setEnabled(list.isEnabled());
			setFont(list.getFont().deriveFont( Font.PLAIN));
			setOpaque(true);

			return this;
		}
 }

}
