/*
 * $Id: ChangeDocumentDialog.java,v 1.9 2004/09/06 10:36:34 tcurley Exp $
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
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.DefaultListModel;



/**
 * The Select Document dialog.
 *
 * @version	$Revision: 1.9 $, $Date: 2004/09/06 10:36:34 $
 * @author Dogs bay
 */
public class ChangeDocumentDialog extends JDialog {
	
	private boolean cancelled	= false;
	private JFrame parent		= null;
	private JPanel main	= null;
	private JList list = null;
	private DefaultListModel model = new DefaultListModel();
	private ArrayList tabPanes = null;
	private int selectedIndex = -1;
	
	private ArrayList images;
    private ArrayList titleStrings;
	
	
	/**
	 * The Select Document dialog.
	 *
	 * @param parent the parent frame.
	 */
	public ChangeDocumentDialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Select Document");
		
		main = new JPanel(new BorderLayout());
		
		// create the list with a dynamic model
		list = new JList(model);
		
		// only allow one selection
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// mouselistener for mouse selection on the list
		MouseListener mouseListener = new MouseAdapter() {
			 public void mouseClicked(MouseEvent e) 
			 {
			     if (e.getClickCount() == 1) 
			     {
					cancelled = false;
					//setVisible(false);
					hide();
			     }
			 }
		};

		list.addMouseListener(mouseListener);
		
		// catch the required keystrokes
		KeyStroke altOne = KeyStroke.getKeyStroke(KeyEvent.VK_1,InputEvent.ALT_MASK, false);
		KeyStroke returnKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0, false);
		KeyStroke altOneUp = KeyStroke.getKeyStroke(KeyEvent.VK_ALT,0, true);
		
		Action nextSelectionAction = new AbstractAction(){	
			public void actionPerformed(ActionEvent e)	
			{		
				int index = list.getSelectedIndex();
				if (index < list.getModel().getSize()-1)
				{
					list.setSelectedIndex(index+1);
					// to move scrollbar if required
					list.ensureIndexIsVisible( index+1);
				}
				else
				{
					// move back to first
					list.setSelectedIndex(0);
					list.ensureIndexIsVisible(0);
				}
			}};
			
		Action selectAction = new AbstractAction(){	
			public void actionPerformed(ActionEvent e)	
			{		
				cancelled = false;
				//setVisible(false);
				hide();
		}};
		
		list.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(altOne, "NEXTSELECTION");
		list.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(returnKey, "SELECT");
		list.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(altOneUp, "SELECT");
		
		list.getActionMap().put("NEXTSELECTION", nextSelectionAction);
		list.getActionMap().put("SELECT", selectAction);
		
		// add the list to a scrollpane
		JScrollPane scroll = new JScrollPane( list);
		scroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
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
	}
	
	
	/**
	 * Initialises and shows the dialog
	 *
	 * @param tab The JTabbedPane
     * @param current The current tab index
     * @param previous The previous tab index
	 */
	public void show( Vector views, ExchangerView current, ExchangerView previous) 
	{
		// intialise the list
		setListContent( views, current, previous);
		
		// if greater than 15 open docs, then show scrollbar
		if ( views.size() > 15)
		{
			list.setVisibleRowCount(15);
		}
		else
		{
			list.setVisibleRowCount( views.size());
		}
		
		pack();
		
		if ( getSize().width < 150) {
			setSize( new Dimension( 150, getSize().height));  
		} 

		setLocationRelativeTo( parent);

		//super.setVisible(true);
		super.show();
	}	
	
	private void setListContent( Vector views, ExchangerView current, ExchangerView previous)
	{
		// blank the previous list
		model.removeAllElements();
		
		// blank the previous list of tabPanes
		tabPanes = new ArrayList();
		
		// list of images
		images = new ArrayList();
		
		// list of doc titles
		titleStrings = new ArrayList();
		
		if ( current != null && current.getDocument() != null)
		{
			// add the current to the list
			addToModel( current);
		}
		
		if ( previous != null && previous.getDocument() != null)
		{
			// add the previous to the list
			addToModel( previous);
		}
		
		for (int i = 0; i < views.size(); i++)
		{
			ExchangerView view = (ExchangerView)views.elementAt(i);

			// check for current and previous, as don't want to add them again
			if ( view != current && view != previous)
			{
				addToModel( view);	
			}
		}
		
		// custom cell renderer, so we can add images and text
		MyCellRenderer renderer = new MyCellRenderer();
		list.setCellRenderer(renderer);
		
		// select the second doc in the list (normally the previous doc)
		if ( views.size() > 1)
		{
			list.setSelectedIndex(1);
		}
		else
		{
			list.setSelectedIndex(0);
		}
	}
	
	private void addToModel( ExchangerView view)
	{
		String title = view.getDocument().getName();
		Icon icon = view.getViewIcon();
		
		images.add(icon);
		titleStrings.add(title);
		
		// add tot he list's model
		model.addElement(title);
		tabPanes.add( view);
	}
	
	public ExchangerView getSelectedView() {
		int index = list.getSelectedIndex();
		if ( index >= 0) {
			return (ExchangerView)tabPanes.get( index);
		}
		
		return null;
	}

	public boolean isCancelled() 
	{
		return cancelled;
	}
	
	class MyCellRenderer extends JLabel implements ListCellRenderer 
	{
		public Component getListCellRendererComponent(JList list,Object value,int selectedIndex,boolean isSelected,      
				boolean cellHasFocus)    
		{
			String s = (String)titleStrings.get(selectedIndex);
			setText(s);
			
			Icon image = (Icon)images.get(selectedIndex); 
			setIcon(image);
			
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			
			setBorder( new EmptyBorder( 0, 2, 1, 15));
			return this;
		}
 }

}
