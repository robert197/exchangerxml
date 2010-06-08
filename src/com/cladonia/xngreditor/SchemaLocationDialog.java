/*
 * $Id: SchemaLocationDialog.java,v 1.4 2004/10/27 13:26:16 edankert Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.ArrayList;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
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
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;

import org.bounce.FormLayout;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XDocument;
import com.cladonia.xngreditor.MessageHandler;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Attribute;






/**
 * The Schema Location dialog.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/10/27 13:26:16 $
 * @author Dogs bay
 */
public class SchemaLocationDialog extends XngrDialog {
	
	private JFrame parent		= null;
	private JButton inputLocationButton = null;
	private JPanel schemaPanel	= null;
	private JTextField namespaceField = null;
	private ExchangerDocument document	= null;
	private JLabel namespaceLabel = null;
	private JRadioButton schemaButton  = null;
	private JRadioButton schemaNoNSButton  = null;
	private JTextField urlField = null;
	
	private static final String SCHEMAINSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String SCHEMALOCATION = "schemaLocation";
	private static final String NOSCHEMALOCATION = "noNamespaceSchemaLocation";
	
	/**
	 * The Schema Location dialog.
	 *
	 * @param parent the parent frame.
	 */
	public SchemaLocationDialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Set Schema Location");
		setDialogDescription( "Specify a Schema Location");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		//removed for xngr-dialog
		/*cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		okButton = new JButton( "OK");
		okButton.setMnemonic( 'O');
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				updateButtonPressed();
			}
		});

		getRootPane().setDefaultButton( okButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 10, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);*/
		
		JPanel form = new JPanel( new FormLayout( 10, 2));

		// fill the panel...
		form.add( getSchemaPanel(), FormLayout.FULL_FILL);

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
		
		// add ok validation here
		if (schemaButton.isSelected())
		{
			// need both namspace and URL
			if (((namespaceField.getText().equals("")) || (namespaceField.getText() == null)) && 
				((urlField.getText().equals("")) || (urlField.getText() == null)))
			{
				MessageHandler.showError( "Please enter a value for both the Namespace \n and the URL.", "Set Schema Location Error");
				return;
			}
			
			if ((namespaceField.getText().equals("")) || (namespaceField.getText() == null))
			{
				MessageHandler.showError( "Please enter a value for the Namespace.", "Set Schema Location Error");
				return;
			}
			
			if ((urlField.getText().equals("")) || (urlField.getText() == null))
			{
				MessageHandler.showError( "Please enter a value for the URL.", "Set Schema Location Error");
				return;
			}
			
			super.okButtonPressed();
			return;
			
		}
		else
		{
			if ((urlField.getText().equals("")) || (urlField.getText() == null))
			{
				MessageHandler.showError( "Please enter a value for the URL.", "Set Schema Location Error");
				return;
			}
			
			super.okButtonPressed();
			return;
		}
	}
	
	private JPanel getSchemaPanel() {
		if ( schemaPanel == null) {
			schemaPanel = new JPanel( new FormLayout( 10, 10));
			schemaPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Schema Location"),
										new EmptyBorder( 0, 5, 5, 5)));
			namespaceField = new JTextField();
			
			schemaButton = new JRadioButton( "schemaLocation");
			schemaButton.setPreferredSize( new Dimension( schemaButton.getPreferredSize().width, namespaceField.getPreferredSize().height));
			//schemaButton.setFont( schemaButton.getFont().deriveFont( Font.PLAIN));
			schemaButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent e) {
					if (schemaButton.isSelected())
					{
						namespaceField.setEnabled(true);
						namespaceLabel.setEnabled(true);
					}
					else
					{
						namespaceField.setEnabled(false);
						namespaceLabel.setEnabled(false);
					}
				}
			});
			
			schemaNoNSButton = new JRadioButton("noNamespaceSchemaLocation");
			schemaNoNSButton.setPreferredSize( new Dimension( schemaNoNSButton.getPreferredSize().width, namespaceField.getPreferredSize().height));
			//schemaNoNSButton.setFont( schemaNoNSButton.getFont().deriveFont( Font.PLAIN));
			
			JPanel schemaTypePanel = new JPanel( new FlowLayout(FlowLayout.LEFT));
			schemaTypePanel.add(schemaButton);
			schemaTypePanel.add(schemaNoNSButton);
			
			schemaPanel.add( schemaTypePanel, FormLayout.FULL_FILL);
			
			ButtonGroup typeGroup = new ButtonGroup();
			typeGroup.add(schemaButton);
			typeGroup.add(schemaNoNSButton);
			
			namespaceLabel = new JLabel("Namespace:");  
			schemaPanel.add(namespaceLabel, FormLayout.LEFT);
			schemaPanel.add(namespaceField, FormLayout.RIGHT_FILL);
			
			
			JPanel locationPanel = new JPanel( new BorderLayout());
			
			urlField  = new JTextField();
			inputLocationButton = new JButton( "...");
			inputLocationButton.setMargin( new Insets( 0, 10, 0, 10));
			inputLocationButton.setPreferredSize( new Dimension( inputLocationButton.getPreferredSize().width, urlField.getPreferredSize().height));
			inputLocationButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					inputLocationButtonPressed();
				}
			});

			locationPanel.add( urlField, BorderLayout.CENTER);
			locationPanel.add( inputLocationButton, BorderLayout.EAST);
			
			schemaPanel.add(new JLabel( "URL:"),FormLayout.LEFT);
			schemaPanel.add( locationPanel, FormLayout.RIGHT_FILL);
		}

		return schemaPanel;
	}
	
	/**
	 * Displays the dialog
	 *
	 * @param document The ExchangeDocument
	 */
	public void show(ExchangerDocument document) 
	{
		JPanel form = new JPanel( new FormLayout(10, 2));
		
		this.document = document;
		
		// set the values that the dialog needs to display
		setCurrentValues();
		
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
	 * Gets the namespace value from the namespace field
	 *
	 * @return String The namespace
	 */
	public String getNamespace()
	{
		return namespaceField.getText(); 
	}
	
	/**
	 * Gets the url value from the url field
	 *
	 * @return String The URL
	 */
	public String getSchemaURL()
	{
		return urlField.getText();
	}
	
	/**
	 * returns whether or not the namespace is required
	 *
	 * @return boolean Whether or not the namespace is required
	 */
	public boolean isNamespaceRequired()
	{
		return schemaButton.isSelected();
	}
		
	/**
	 * Sets the original values displayed by the dialog, plus what should be enabled\disabled
	 */
	private void setCurrentValues()
	{
		XDocument xdoc = document.getDocument();
		Element ele = xdoc.getRootElement();
		
		Attribute attr = ele.attribute(SCHEMALOCATION);
		
		if (attr == null)
		{
			// check for no namespace schema
			Attribute attr2 = ele.attribute(NOSCHEMALOCATION);
			if (attr2 == null)
			{
				// set the defaults
				schemaButton.setSelected(true);
				Namespace ns = ele.getNamespace();
				if (ns != Namespace.NO_NAMESPACE)
				{
					namespaceField.setText(ns.getURI());
				}
				else
				{
					namespaceField.setText("");
				}
				
				urlField.setText("");			
			}
			else
			{
				// we have a "no namespace schema"
				schemaNoNSButton.setSelected(true);
				namespaceField.setText("");
				namespaceField.setEnabled(false);
				namespaceLabel.setEnabled(false);
				urlField.setText(attr2.getValue());
			}
		}
		else
		{	
			// we hava a namepace schema
			
			schemaButton.setSelected(true);
			
			// we have namespace schema, set the namespace and the URL
			String attrValue = attr.getValue();
			
			// break up all the namespace and url pairs
			ArrayList stringValues = new ArrayList();
			
			StringTokenizer st = new StringTokenizer(attrValue);
		     while (st.hasMoreTokens()) 
		     {
		     	stringValues.add(st.nextToken());
		     }
		     
		     String namespaceValue = (String)stringValues.get(0);
		     if (namespaceValue != null)
		     {
		     	namespaceField.setText(namespaceValue);
		     }
		     else
		     {
		     	namespaceField.setText("");
		     }
		     
		     String urlValue = (String)stringValues.get(1);
		     if (urlValue!= null)
		     {
		     	urlField.setText(urlValue);
		     }
		     else
		     {
		     	urlField.setText("");
		     }
		}
	}
		
	private void inputLocationButtonPressed() {
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

			setText( urlField, url.toString());
		}
	}
	
	//	The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getInputFileChooser() {
		JFileChooser chooser = FileUtilities.getCurrentFileChooser();
		chooser.setFileFilter( chooser.getAcceptAllFileFilter());
		
		File file = URLUtilities.toFile(urlField.getText());
		
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

	
}
