/*
 * $Id: XDiffDialog.java,v 1.6 2004/11/01 15:02:50 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.xdiff;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.Insets;


import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JFileChooser;

import org.bounce.FormLayout;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrDialog;



/**
 * The XDiff dialog.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/11/01 15:02:50 $
 * @author Dogs bay
 */
public class XDiffDialog extends XngrDialog {
	
	//private boolean cancelled	= false;
	private JFrame parent		= null;
	private JButton updateButton	= null;
	private JButton cancelButton	= null;
	private JButton inputBaseLocationButton = null;
	private JButton inputModLocationButton = null;
	private JPanel comparePanel	= null;
	private JTextField namespaceField = null;
	private ExchangerDocument document	= null;
	private JTextField baseField = null;
	private JTextField modField = null;
	
	/**
	 * The XDiff dialog.
	 *
	 * @param parent the parent frame.
	 */
	public XDiffDialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "XML Diff and Merge");
		setDialogDescription( "Specify file locations");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
//		removed for xngr-dialog
		/*cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		updateButton = new JButton( "OK");
		updateButton.setMnemonic( 'O');
		updateButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				updateButtonPressed();
			}
		});

		getRootPane().setDefaultButton( updateButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 10, 0, 3, 0));
		buttonPanel.add( updateButton);
		buttonPanel.add( cancelButton);*/
		
		JPanel form = new JPanel( new FormLayout( 10, 2));

		// fill the panel...
		form.add( getComparePanel(), FormLayout.FULL_FILL);

		main.add( form, BorderLayout.CENTER);
		/*main.add( buttonPanel, BorderLayout.SOUTH);

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				hide();
			}
		});*/

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
	}
	
	
	/**
	 *  Called when the cancel button is pushed
	 */
	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}
	
	/**
	 *  Called when the ok button is pushed, validates that the required text fields have a value
	 */
	protected void okButtonPressed() {
		
		if (getBaseFile().equals("") || getModifiedFile().equals(""))
		{
			MessageHandler.showError( "Please enter a file location for both the \nBase file and the Modified file.", "Compare XML Files Error");
			return;
		}
			
		super.okButtonPressed();
		//cancelled = false;
		//hide();
		return;
	}
	
	private JPanel getComparePanel() {
		if ( comparePanel == null) {
			comparePanel = new JPanel( new FormLayout( 10, 10));
			comparePanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Select Files"),
										new EmptyBorder( 0, 5, 5, 5)));
			
			JPanel locationPanel = new JPanel( new BorderLayout());
			
			baseField  = new JTextField();
			inputBaseLocationButton = new JButton( "...");
			inputBaseLocationButton.setMargin( new Insets( 0, 10, 0, 10));
			inputBaseLocationButton.setPreferredSize( new Dimension( inputBaseLocationButton.getPreferredSize().width, baseField.getPreferredSize().height));
			inputBaseLocationButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					inputBaseLocationButtonPressed();
				}
			});

			locationPanel.add( baseField, BorderLayout.CENTER);
			locationPanel.add( inputBaseLocationButton, BorderLayout.EAST);
			
			comparePanel.add(new JLabel( "Base URL:"),FormLayout.LEFT);
			comparePanel.add( locationPanel, FormLayout.RIGHT_FILL);
		
			JPanel locationModPanel = new JPanel( new BorderLayout());
			
			modField  = new JTextField();
			inputModLocationButton = new JButton( "...");
			inputModLocationButton.setMargin( new Insets( 0, 10, 0, 10));
			inputModLocationButton.setPreferredSize( new Dimension( inputModLocationButton.getPreferredSize().width, baseField.getPreferredSize().height));
			inputModLocationButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					inputModLocationButtonPressed();
				}
			});
			
			locationModPanel.add( modField, BorderLayout.CENTER);
			locationModPanel.add( inputModLocationButton, BorderLayout.EAST);
			
			comparePanel.add(new JLabel( "Modified URL:"),FormLayout.LEFT);
			comparePanel.add( locationModPanel, FormLayout.RIGHT_FILL);
				
		}

		return comparePanel;
	}
	
	/**
	 * Displays the dialog
	 *
	 * @param document The ExchangeDocument
	 */
	public void show() 
	{
		JPanel form = new JPanel( new FormLayout(10, 2));
		
		pack();
		setSize( new Dimension( 420, getSize().height));

		setLocationRelativeTo( parent);

		super.show();
	}
	
	/**
	 * When the dialog is cancelled and no selection has been made, 
	 * this method returns true.
	 *
	 * @return true when the dialog has been cancelled.
	 */
	public boolean isCancelled() 
	{
		return cancelled;
	}
	
	/**
	 * Gets the url value from the url field
	 *
	 * @return String The URL
	 */
	public String getBaseFile()
	{
		return baseField.getText();
	}
	
	public String getModifiedFile()
	{
		return modField.getText();
	}
	
	
		
	private void inputBaseLocationButtonPressed() {
		JFileChooser chooser = getInputFileChooser();

		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			URL url = null;

			try {
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			} catch ( MalformedURLException x) {
				x.printStackTrace(); // should never happen
			}

			setText( baseField, url.toString());
		}
	}
	
	//	The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getInputFileChooser() {
		JFileChooser chooser = FileUtilities.getCurrentFileChooser();
		chooser.setFileFilter( chooser.getAcceptAllFileFilter());
		
		File file = URLUtilities.toFile(baseField.getText());
		
		if ( file != null) {
			chooser.setCurrentDirectory( file);
		}

		chooser.rescanCurrentDirectory();
		
		return chooser;
	}
	
	private void setText( JTextField field, String text) {
		field.setText(text);
		field.setCaretPosition(0);
	}
	
	private void inputModLocationButtonPressed() {
		JFileChooser chooser = getModFileChooser();

		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			URL url = null;

			try {
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			} catch ( MalformedURLException x) {
				x.printStackTrace(); // should never happen
			}

			setText( modField, url.toString());
		}
	}
	
	//The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getModFileChooser() {
		JFileChooser chooser = FileUtilities.getCurrentFileChooser();
		chooser.setFileFilter( chooser.getAcceptAllFileFilter());
		
		File file = URLUtilities.toFile(modField.getText());
		
		if ( file != null) {
			chooser.setCurrentDirectory( file);
		}

		chooser.rescanCurrentDirectory();
		
		return chooser;
	}

	
}
