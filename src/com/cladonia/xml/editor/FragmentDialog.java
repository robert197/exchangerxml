/*
 * $Id: FragmentDialog.java,v 1.3 2004/11/04 19:21:49 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.bounce.FormLayout;

import com.cladonia.xngreditor.XngrDialog;

/**
 * The namespace properties dialog.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/11/04 19:21:49 $
 * @author Dogsbay
 */
public class FragmentDialog extends XngrDialog {
	private static final Dimension SIZE 	= new Dimension( 300, 400);

	private JFrame parent	= null;
	private Hashtable fragments	= null;
	private JPanel centerPanel	= null;
	private JTextField defaultFocusField	= null;

	/**
	 * The dialog that displays the preferences for the editor.
	 *
	 * @param frame the parent frame.
	 */
	public FragmentDialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Fragment Details");
		setDialogDescription( "Specify the Fragment Details");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		// GENERAL
		centerPanel = new JPanel( new BorderLayout());
		centerPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
	
		main.add( centerPanel, BorderLayout.CENTER);
		setContentPane( main);
		
		main.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
				defaultFocusField.requestFocusInWindow();
			}

			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});
		
	}
	
	public void show( Vector keys) {
		centerPanel.removeAll();

		fragments = new Hashtable();
		JPanel panel = new JPanel( new FormLayout( 2, 2));
		JTextField initialComponent = null;
		
		for ( int i = 0; i < keys.size(); i++) {
			String name = (String)keys.elementAt(i);
			
			JTextField field = new JTextField();
			fragments.put( name, field);
			
			panel.add( new JLabel( name+":"), FormLayout.LEFT);
			panel.add( field, FormLayout.RIGHT_FILL);
			
			if ( i == 0) {
				defaultFocusField = field;
			}
		}
		
		centerPanel.add( panel, BorderLayout.CENTER);

		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));
		setLocationRelativeTo( parent);
		
		super.show();
	}
	
	public String getValue( String key) {
		return ((JTextField)fragments.get( key)).getText();
	}
}