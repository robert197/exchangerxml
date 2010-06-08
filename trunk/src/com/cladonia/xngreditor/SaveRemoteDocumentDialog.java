/*
 * $Id: SaveRemoteDocumentDialog.java,v 1.7 2004/11/04 19:21:48 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.bounce.FormLayout;

import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * A dialog that allows for loading a remote document.
 *
 * @version	$Revision: 1.7 $, $Date: 2004/11/04 19:21:48 $ 
 * @author Dogsbay
 */
public class SaveRemoteDocumentDialog extends XngrDialog {
	private static final boolean DEBUG = false;
	private static final Dimension SIZE = new Dimension( 380, 110);

	private static final String PROTOCOL_WEBDAV_HTTP = "webdav (http)"; 
	private static final String PROTOCOL_WEBDAV_HTTPS = "webdav (https)"; 
	private static final String PROTOCOL_FTP = "ftp"; 

	private static final String[] SAVE_PROTOCOLS = { PROTOCOL_WEBDAV_HTTP, PROTOCOL_WEBDAV_HTTPS, PROTOCOL_FTP};

	private JComboBox urlField = null;
	private JComboBox protocolField = null;
	private JTextField hostField = null;
	private JTextField portField = null;
	private JTextField usernameField = null;
	private JPasswordField passwordField = null;
	private JTextField fileField = null;

	private ConfigurationProperties properties = null;
	
	/**
	 * Creates a modal dialog that allows for specifying a remote document.
	 *
	 * @param parent the parent frame.
	 * @param title the title for the dialog.
	 */
	public SaveRemoteDocumentDialog( ConfigurationProperties properties, JFrame parent) {
		super( parent,true);
		
		this.properties = properties;

		//setModal( true);
		
		setTitle( "Save Remote Document");
		setDialogDescription( "Specify Remote Document details.");
		
		setResizable( false);
		
		urlField = new JComboBox();
		urlField.setEditable( true);
		urlField.setPreferredSize( new Dimension( 100, 21));
		urlField.setFont( urlField.getFont().deriveFont( Font.PLAIN));
		urlField.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				updateDetails();
			}
		});
		urlField.addFocusListener( new FocusListener(){
			public void focusGained( FocusEvent e) {}
			public void focusLost( FocusEvent e) {
				updateDetails();
			}
		});
//		((JTextField)urlField.getEditor().getEditorComponent()).addCaretListener( new CaretListener() {
//			public void caretUpdate( CaretEvent e) {
//				updateDetails();
//			}
//		});
		
		JLabel urlLabel = new JLabel( "URL:");
//		JLabel nameLabel = new JLabel( "Name:");

		JPanel panel = new JPanel( new FormLayout( 3, 5));
		panel.add( urlLabel, FormLayout.LEFT);
		panel.add( urlField, FormLayout.RIGHT_FILL);
		panel.add( getSeparator(), FormLayout.FULL_FILL);
		panel.setBorder( new EmptyBorder( 5, 5, 5, 5));

		JPanel detailsPanel = new JPanel( new FormLayout( 3, 5));
		detailsPanel.setBorder( new CompoundBorder( 
				new TitledBorder( "Details"),
				new EmptyBorder( 0, 5, 5, 5)));

		protocolField = new JComboBox( SAVE_PROTOCOLS);
		
		protocolField.setPreferredSize( new Dimension( protocolField.getPreferredSize().width, 21));
		protocolField.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				updateURL();
			}
		});

		detailsPanel.add( new JLabel( "Protocol:"), FormLayout.LEFT);
		detailsPanel.add( protocolField, FormLayout.RIGHT);
		
		hostField = new JTextField();
		hostField.addFocusListener( new FocusListener(){
			public void focusGained( FocusEvent e) {}
			public void focusLost( FocusEvent e) {
				updateURL();
				hostField.setCaretPosition( 0);
			}
		});
		hostField.addCaretListener( new CaretListener(){
			public void caretUpdate( CaretEvent e) {
				updateURL();
			}
		});
		
		detailsPanel.add( new JLabel( "Host:"), FormLayout.LEFT);
		detailsPanel.add( hostField, FormLayout.RIGHT_FILL);

		portField = new JTextField();
		portField.setPreferredSize( new Dimension( 70, portField.getPreferredSize().height));
		portField.addFocusListener( new FocusListener(){
			public void focusGained( FocusEvent e) {}
			public void focusLost( FocusEvent e) {
				updateURL();
				portField.setCaretPosition( 0);
			}
		});
		portField.addCaretListener( new CaretListener(){
			public void caretUpdate( CaretEvent e) {
				updateURL();
			}
		});
		
		portField.setHorizontalAlignment( JTextField.RIGHT);
		
		detailsPanel.add( new JLabel( "Port:"), FormLayout.LEFT);
		detailsPanel.add( portField, FormLayout.RIGHT);

		detailsPanel.add( getSeparator(), FormLayout.FULL);

		usernameField = new JTextField();
		
		detailsPanel.add( new JLabel( "Username:"), FormLayout.LEFT);
		detailsPanel.add( usernameField, FormLayout.RIGHT_FILL);

		passwordField = new JPasswordField();
		
		detailsPanel.add( new JLabel( "Password:"), FormLayout.LEFT);
		detailsPanel.add( passwordField, FormLayout.RIGHT_FILL);
		
		detailsPanel.add( getSeparator(), FormLayout.FULL);
		
		fileField = new JTextField();
		
		detailsPanel.add( new JLabel( "File:"), FormLayout.LEFT);
		detailsPanel.add( fileField, FormLayout.RIGHT_FILL);
		fileField.addCaretListener( new CaretListener(){
			public void caretUpdate( CaretEvent e) {
				updateURL();
			}
		});
		fileField.addFocusListener( new FocusListener(){
			public void focusGained( FocusEvent e) {}
			public void focusLost( FocusEvent e) {
				updateURL();
				fileField.setCaretPosition( 0);
			}
		});

		panel.add( detailsPanel, FormLayout.FULL_FILL);

		
		//removed for xngr-dialog
		super.okButton.setText("Save");
		/*okButton = new JButton( "Save");

		cancelButton = new JButton( "Cancel");
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
		
		mainPanel.add( buttonPanel, BorderLayout.SOUTH);*/
		JPanel mainPanel = new JPanel( new BorderLayout());
		this.setContentPane( mainPanel); 
		mainPanel.add( panel, BorderLayout.CENTER);
		mainPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		mainPanel.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelled = true;
				//setVisible(false);
				hide();
			}
		});
		mainPanel.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");
		
		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));
		
		setLocationRelativeTo( parent);
	}
	
	/**
	 * Set the properties of the document.
	 *
	 * @param name the name of the document
	 * @param url the URL of the document
	 */
	public void show( String url) {
		urlField.removeAllItems();
		Vector urls = properties.getLastOpenedURLs();

		for ( int i = 0; i < urls.size(); i++) {
//			System.out.println( (URL)urls.elementAt(i));
			urlField.addItem( URLUtilities.toString( (URL)urls.elementAt(i)));
		}
		
		if ( urls.size() > 0) { 
			urlField.setSelectedIndex( -1);
		} else {
			((JTextField)urlField.getEditor().getEditorComponent()).setText( url);
		}

		super.show();
	}
	
	public void okButtonPressed() {
		URL url = getURL();
		
		if ( url == null) {
			MessageHandler.showError( "Please specify a valid URL.", "Remote Error");
		} else {
			super.okButtonPressed();
		}
	}

	/**
	 * Returns the URL of the document.
	 *
	 * @return the URL of the document
	 */
	public URL getURL() {
		String protocol = null;
		Object item = protocolField.getSelectedItem();
		
		if ( item == PROTOCOL_WEBDAV_HTTPS) {
			protocol = "https";
		} else if ( item == PROTOCOL_FTP) {
			protocol = "ftp";
		} else {
			protocol = "http";
		}
		
		String host = hostField.getText();
		String username = usernameField.getText();
		String password = new String( passwordField.getPassword());
		
		String port = portField.getText();
		int portNumber = 0;
		
		if ( port != null && port.trim().length() > 0) {
			try {
				portNumber = Integer.parseInt( port);
			} catch ( NumberFormatException e) {
				portNumber = 0;
			}
		}

		String file = fileField.getText();
		if ( file != null && !file.startsWith( "/") && file.trim().length() > 0) {
			file = "/"+file;
		}
		
		if ( StringUtilities.isEmpty( file) || StringUtilities.isEmpty( host)) {
			return null;
		}

		return URLUtilities.createURL( protocol, username, password, host, portNumber, file, null, null);
	}
	
	private boolean updating = false;
	
	private void updateDetails() {
		Vector urls = properties.getLastOpenedURLs();
		String string = null;
		int index = urlField.getSelectedIndex();
		
		if ( index != -1) {
			string = ((URL)urls.elementAt( index)).toString();
		} else {
			string = ((JTextField)urlField.getEditor().getEditorComponent()).getText();
		}
		
		if ( string != null && string.trim().length() > 0) { 
			try {
				URL url = new URL( string);
				
				String protocol = url.getProtocol();
				
				if ( protocol.equals( "http")) {
					protocolField.setSelectedItem( PROTOCOL_WEBDAV_HTTP);
				} else if ( protocol.equals( "https")) {
					protocolField.setSelectedItem( PROTOCOL_WEBDAV_HTTPS);
				} else if ( protocol.equals( "ftp")) {
					protocolField.setSelectedItem( PROTOCOL_FTP);
				}

				hostField.setText( url.getHost());
				
				if ( url.getPort() > 0) {
					portField.setText( ""+url.getPort());
				} else {
					portField.setText( "");
				}
				
				String user = URLUtilities.getUsername( url);
				String password = URLUtilities.getPassword( url);
				
				if ( user != null && password != null) {
					usernameField.setText( user);
					passwordField.setText( password);
				} else {
					usernameField.setText( "");
					passwordField.setText( "");
				}

				fileField.setText( url.getFile());
			} catch ( MalformedURLException e) {
				protocolField.setSelectedItem( PROTOCOL_WEBDAV_HTTP);
				hostField.setText( "");
				portField.setText( "");
				usernameField.setText( "");
				passwordField.setText( "");
				fileField.setText( "");
			}
		} else {
			protocolField.setSelectedItem( PROTOCOL_WEBDAV_HTTP);
			hostField.setText( "");
			portField.setText( "");
			usernameField.setText( "");
			passwordField.setText( "");
			fileField.setText( "");
		}
	}
	
	private void updateURL() {
		String url = null;
		Object item = protocolField.getSelectedItem();

		if ( item == PROTOCOL_WEBDAV_HTTPS) {
			url = "https://";
		} else if ( item == PROTOCOL_FTP) {
			url = "ftp://";
		} else {
			url = "http://";
		}

		url = url+hostField.getText();
		
		String port = portField.getText();
		
		if ( port != null && port.trim().length() > 0) {
			int portNumber = -1;

			try {
				portNumber = Integer.parseInt( port);
			} catch ( NumberFormatException e) {
				portNumber = -1;
			}
			
			if ( portNumber > 0) {
				url = url+":"+portNumber;
			}
		}

		String file = fileField.getText();
		if ( file != null && !file.startsWith( "/") && file.trim().length() > 0) {
			url = url + "/";
		}
		
		url = url + file;
		
		((JTextField)urlField.getEditor().getEditorComponent()).setText( url);
	}
	
	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));

		return separator;
	}
} 
