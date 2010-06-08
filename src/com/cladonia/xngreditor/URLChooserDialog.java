/*
 * $Id: URLChooserDialog.java,v 1.4 2004/11/04 19:21:49 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.bounce.FormLayout;

import com.cladonia.xml.XngrURLUtilities;

/**
 * A dialog that allows for selecting a URL.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/11/04 19:21:49 $ 
 * @author Dogsbay
 */
public class URLChooserDialog extends XngrDialog {
	private static final boolean DEBUG = false;
	private static final Dimension SIZE = new Dimension( 380, 110);
	private JTextField urlField = null;
	private JButton fileSelectionButton = null;
	private File dir = null;
		
	/**
	 * Creates a modal dialog that allows for opening a URL.
	 *
	 * @param parent the parent frame.
	 * @param title the title for the dialog.
	 */
	public URLChooserDialog( JFrame parent, String title, String description) {
		super( parent, true);
		//setModal( true);

		setTitle( title); // Add/Open Remote Document
		setDialogDescription( description);
		
		setResizable( false);
		
		urlField = new JTextField();
		
		JLabel urlLabel = new JLabel( "URL:");

		fileSelectionButton = new JButton( "...");

		fileSelectionButton.setMargin( new Insets( 0, 10, 0, 10));
		fileSelectionButton.setPreferredSize( new Dimension( fileSelectionButton.getPreferredSize().width, urlField.getPreferredSize().height));
		fileSelectionButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				fileSelectionButtonPressed();
			}
		});

		JPanel panel = new JPanel( new BorderLayout( 3, 5));
		panel.add( urlLabel, BorderLayout.WEST);
		panel.add( urlField, BorderLayout.CENTER);
		panel.add( fileSelectionButton, BorderLayout.EAST);
		panel.setBorder( new EmptyBorder( 5, 5, 5, 5));

		panel.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
				urlField.requestFocusInWindow();
			}

			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});

		JPanel urlPanel = new JPanel( new FormLayout( 10, 2));
		urlPanel.add( panel, FormLayout.FULL_FILL);

		JPanel mainPanel = new JPanel( new BorderLayout());
		this.setContentPane( mainPanel); 
		mainPanel.add( urlPanel, BorderLayout.CENTER);
		mainPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		mainPanel.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelled = true;
				//setVisible(false);
				hide();
			}
		});
		mainPanel.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");
		
		//removed for xngr-dialog
		okButton.setText("Open");
		/*okButton = new JButton("Open");
		cancelButton = new JButton("Cancel");
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelled = false;
				setVisible(false);
			}
		});
		
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
				
		getRootPane().setDefaultButton( okButton);
		
		mainPanel.add( buttonPanel, BorderLayout.SOUTH);
		*/
		
		pack();

		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));
	}
	
	private void fileSelectionButtonPressed() {
		JFileChooser chooser = FileUtilities.getFileChooser();
		
		if ( dir != null) {
			chooser.setCurrentDirectory( dir);
		}

		int value = chooser.showOpenDialog( getParent());
		dir = chooser.getCurrentDirectory();

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			try {
				urlField.setText( XngrURLUtilities.getURLFromFile(file).toString());
				urlField.setCaretPosition( 0);
			} catch ( MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set the properties of the document.
	 *
	 * @param name the name of the document
	 * @param url the URL of the document
	 */
	public void setProperties( URL url) {
		if ( url != null) {
			urlField.setText( url.toString());
		} else {
			urlField.setText( "");
		}
	}

	/**
	 * Returns the URL of the document.
	 *
	 * @return the URL of the document
	 */
	public URL getURL() {
		URL result = null;
		
		try {
			result = new URL( urlField.getText());
		} catch ( MalformedURLException e) {
			try {
				result = XngrURLUtilities.getURLFromFile(new File( urlField.getText()));
			} catch ( MalformedURLException x) {
				x.printStackTrace();
			}
		}

		return result;
	}
		
} 
