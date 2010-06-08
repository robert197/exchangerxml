/*
 * $Id: GotoDialog.java,v 1.5 2004/11/04 19:21:48 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.bounce.FormLayout;

/**
 * The goto dialog for the editor.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/11/04 19:21:48 $
 * @author Dogsbay
 */
public class GotoDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 200, 100);

	private int line 				= -1;
	
	// The components that contain the values
	private JTextField gotoField	= null;

	/**
	 * The dialog that displays the properties for the document.
	 *
	 * @param frame the parent frame.
	 */
	public GotoDialog( JFrame parent) {
		super( parent, true);
		
		setResizable( false);
		setTitle( "Goto", "Go to line");
		setDialogDescription( "Specify a line number.");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 10, 5, 5, 5));

		main.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelButtonPressed();
			}
		});
		main.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");

		JPanel gotoPanel = new JPanel( new FormLayout( 10, 0));
		gotoPanel.setBorder( new EmptyBorder( 0, 5, 0, 5));

		gotoField = new JTextField();
		gotoField.addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					okButtonPressed();
				}
			}
		});

		gotoField.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
				gotoField.requestFocusInWindow();
			}

			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});

		gotoPanel.add( new JLabel("Line:"), FormLayout.LEFT);
		gotoPanel.add( gotoField, FormLayout.RIGHT_FILL);
		//removed for xngr-dialog
/*
		cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		okButton = new JButton( "OK");
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				okButtonPressed();
			}
		});

		getRootPane().setDefaultButton( okButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);
*/
		main.add( gotoPanel, BorderLayout.NORTH);
		//main.add( buttonPanel, BorderLayout.SOUTH);

		setContentPane( main);
		
		/*addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelButtonPressed();
			}
		});
*/
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);

		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));

		setLocationRelativeTo( parent);
	}
	
	public int getLine() {
		return line;
	}
	
	protected void okButtonPressed() {
		String gotoText = gotoField.getText();
		
		try { 
			line = Integer.parseInt( gotoText);
			
			if ( line > 0) {
				super.okButtonPressed();
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}

	/**
	 * Initialises the values in the dialog.
	 */
	public void show() {
		line = -1;

		gotoField.setText("");
		
		super.show();
	}
} 
