/*
 * $Id: XMLDocumentChooserDialog.java,v 1.9 2005/04/29 12:38:58 gmcgoldrick Exp $
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import javax.swing.JComboBox;

import org.bounce.FormLayout;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;

/**
 * The grammar properties dialog.
 *
 * @version	$Revision: 1.9 $, $Date: 2005/04/29 12:38:58 $
 * @author Dogsbay
 */
public class XMLDocumentChooserDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 400, 300);

	private Vector names			= null;
	private ExchangerDocument document	= null;
	
	private JPanel inputPanel = null;

	private JFrame parent			= null;

	// The components that contain the values
	private JTextField inputLocationField	= null;
	private JButton inputLocationButton		= null;
	private JRadioButton inputCurrentButton		= null;
	private JRadioButton inputFromOpenDocumentButton		= null;
	private JRadioButton inputFromURLButton		= null;
	private JComboBox inputFromOpenDocumentBox = null;
	private ExchangerEditor editor = null;
	
	private boolean disableCurrentDocument = false;
	
	private boolean savedFilesOnly = true;
	/**
	 * The XSLT execution dialog.
	 *
	 * @param frame the parent frame.
	 */
	
	public XMLDocumentChooserDialog( JFrame parent, String title, String description, ExchangerEditor editor, boolean savedFilesOnly) {
	  this(parent, title, description, editor, savedFilesOnly, false);
	}
	
	public XMLDocumentChooserDialog( JFrame parent, String title, String description, ExchangerEditor editor, boolean savedFilesOnly, boolean disableCurrentDocument) {
		super( parent, true);
		
		this.parent = parent;
		this.editor = editor;
		this.savedFilesOnly = savedFilesOnly;
		this.disableCurrentDocument = disableCurrentDocument;
		
		setResizable( false);
		setTitle( title);
		setDialogDescription( description);
		
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

		okButton = new JButton( "Open");
		okButton.setMnemonic( 'O');
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				executeButtonPressed();
			}
		});

		getRootPane().setDefaultButton( okButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 10, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);*/
		
		JPanel form = new JPanel( new FormLayout( 10, 2));

		// fill all three panels...
		form.add( getInputPanel(), FormLayout.FULL_FILL);

		main.add( form, BorderLayout.CENTER);
		/*main.add( buttonPanel, BorderLayout.SOUTH);

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				hide();
			}
		});*/

		setContentPane( main);
		
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);

		pack();
		setSize( new Dimension( 400, getSize().height));
		
		setLocationRelativeTo( parent);
	  
	}
	  
	  

	private JPanel getInputPanel() {
		if ( inputPanel == null) {
			inputPanel = new JPanel( new FormLayout( 10, 2));
			inputPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
//			inputPanel.setBorder( new CompoundBorder( 
//										new TitledBorder( "Input"),
//										new EmptyBorder( 0, 5, 5, 5)));

			inputLocationField = new JTextField();

			inputLocationButton = new JButton( "...");
			inputLocationButton.setMargin( new Insets( 0, 10, 0, 10));
			inputLocationButton.setPreferredSize( new Dimension( inputLocationButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));
			inputLocationButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					inputLocationButtonPressed();
				}
			});
			
			inputLocationField.setEnabled( false);
			inputLocationButton.setEnabled( false);

			JPanel locationPanel = new JPanel( new BorderLayout());

			inputFromURLButton	= new JRadioButton( "From URL:");
			inputFromURLButton.setPreferredSize( new Dimension( inputFromURLButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));
			inputFromURLButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					inputLocationField.setEnabled( inputFromURLButton.isSelected());
					inputLocationButton.setEnabled( inputFromURLButton.isSelected());
				}
			});

			locationPanel.add( inputLocationField, BorderLayout.CENTER);
			locationPanel.add( inputLocationButton, BorderLayout.EAST);

			inputCurrentButton	= new JRadioButton( "Current Document");
			inputCurrentButton.setPreferredSize( new Dimension( inputCurrentButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));

			
			inputFromOpenDocumentButton	= new JRadioButton( "Open Document");
			inputFromOpenDocumentButton.setPreferredSize( new Dimension( inputFromOpenDocumentButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));

			inputFromOpenDocumentBox = new JComboBox();
			//inputFromOpenDocumentBox.setFont( inputFromOpenDocumentBox.getFont().deriveFont( Font.PLAIN));
			inputFromOpenDocumentBox.setPreferredSize( new Dimension( inputFromOpenDocumentBox.getPreferredSize().width, inputLocationField.getPreferredSize().height));
			inputFromOpenDocumentBox.setEditable(true);
			

			
			
			ButtonGroup group = new ButtonGroup();
			if (!disableCurrentDocument)
			  group.add( inputCurrentButton);
			group.add( inputFromOpenDocumentButton);
			group.add( inputFromURLButton);
			
			if (!disableCurrentDocument)
			  inputPanel.add( inputCurrentButton, FormLayout.FULL);
			inputPanel.add( inputFromOpenDocumentButton, FormLayout.LEFT);
			inputPanel.add( inputFromOpenDocumentBox, FormLayout.RIGHT_FILL);				
			inputPanel.add( inputFromURLButton, FormLayout.LEFT);
			inputPanel.add( locationPanel, FormLayout.RIGHT_FILL);
		}

		return inputPanel;
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

			setText( inputLocationField, url.toString());
		}
	}

	protected void okButtonPressed() {
		super.okButtonPressed();
	}
	
	public String getInputLocation() {
		return inputLocationField.getText();
	}
	
	public boolean isCurrentDocument() {
		return inputCurrentButton.isSelected();
	}

	public boolean isOpenDocument() {
		return inputFromOpenDocumentButton.isSelected();
	}

	public ExchangerDocument getOpenDocument()
	{
	  return ((OpenDocument)inputFromOpenDocumentBox.getSelectedItem()).getDocument();
	  
	}
	
	public void show( boolean currentAllowed) {
	  
	  boolean existsOpenDocuments = setOpenDocuments(); 
//		System.out.println( "XMLDocumentChooserDialog.show( "+currentAllowed+")");
	  
	  
		if ( currentAllowed && !disableCurrentDocument) {
			inputCurrentButton.setEnabled( true);
			inputCurrentButton.setSelected( true);
			inputFromURLButton.setSelected( false);
			inputFromOpenDocumentButton.setSelected( false);
			
			if (existsOpenDocuments)
				inputFromOpenDocumentButton.setEnabled( true);
			else
				inputFromOpenDocumentButton.setEnabled( false);
			  
			  
		} else if (existsOpenDocuments) {
			inputCurrentButton.setEnabled( false);
			inputFromOpenDocumentButton.setEnabled( true);
			inputFromOpenDocumentButton.setSelected( true);
			inputFromURLButton.setSelected( false);	  
		} else {
			inputCurrentButton.setEnabled( false);
			inputFromOpenDocumentButton.setEnabled( false);
			inputFromURLButton.setSelected( true);	  
		}
		
		//inputLocationField.setText( "");
		
		super.show();
	}
	
	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}

	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));

		return separator;
	}

	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getInputFileChooser() {
		JFileChooser chooser = FileUtilities.getCurrentFileChooser();
		chooser.setFileFilter( chooser.getAcceptAllFileFilter());
		
		File file = URLUtilities.toFile( inputLocationField.getText());

		if ( file != null) {
			chooser.setCurrentDirectory( file);
		}

		chooser.rescanCurrentDirectory();
		
		return chooser;
	}

	private void setText( JTextField field, String text) {
		field.setText( text);
		field.setCaretPosition( 0);
	}
	
	protected boolean isEmpty( String string) {
		if ( string != null && string.trim().length() > 0) {
			return false;
		}
		
		return true;
	}
	
	private boolean setOpenDocuments()
	{
	  boolean exists = false;
	  
	  inputFromOpenDocumentBox.removeAllItems();
		
	  if (editor == null)
	    	return false;
	  
		Vector views = ((ExchangerEditor)editor).getViews();

		int result = 0;

		for ( int i = 0; i < views.size(); i++) {
			ExchangerView view = (ExchangerView) views.elementAt(i);
			//URL url = view.getDocument().getURL();
			//if (url != null)
			//{
			  //xslFromOpenDocumentField.addItem(url.toString());
			  //xslFromOpenDocumentField.addItem(view.getDocument().getName());
			//}

			URL url = view.getDocument().getURL();
			if (savedFilesOnly == false || (savedFilesOnly==true && url != null))
			{
			  exists = true;
			  inputFromOpenDocumentBox.addItem(new OpenDocument(view.getDocument().getName(),view.getDocument())) ;
			}
		}
		
		inputFromOpenDocumentBox.setSelectedIndex(-1);
		
		return exists;

	}	
	
}
/*
class OpenDocument{
  String name = null;
  ExchangerDocument doc = null;
  
  OpenDocument(String name, ExchangerDocument doc)
  {
    this.name = name;
    this.doc = doc;
  }
  
  public String toString()
  {
    return name;
  }

  public ExchangerDocument getDocument()
  {
    return doc;
  }

}
 */
