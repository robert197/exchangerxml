/*
 * $Id: XMLDeclarationDialog.java,v 1.7 2004/11/04 19:21:49 edankert Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

import org.bounce.FormLayout;

import com.cladonia.xml.ExchangerDocument;

/**
 * The XML Declaration dialog.
 *
 * @version	$Revision: 1.7 $, $Date: 2004/11/04 19:21:49 $
 * @author Dogs bay
 */
public class XMLDeclarationDialog extends XngrDialog {
	
	private JFrame parent		= null;
	private JPanel declarationPanel	= null;
	private JComboBox standaloneField = null;
	private JComboBox encodingField = null;
	private ExchangerDocument document	= null;
	private JRadioButton version10Button	= null;
	private JRadioButton version11Button	= null;
	private JTextField versionField = null;
	
	private static final String STANDALONE_NONE = "NONE";
	private static final String STANDALONE_NO = "no";
	private static final String STANDALONE_YES = "yes";
	private static final String[] ENCODINGLIST = {"UTF-8","UTF-16","US-ASCII","ISO-8859-1","ISO-8859-2","ISO-8859-3","ISO-8859-4",
			"ISO-8859-5","ISO-8859-6","ISO-8859-7","ISO-8859-8","ISO-8859-9","ISO-8859-13","ISO-8859-15",
			"ISO-2022-JP","Shift_JIS","EUC-JP","GBK","Big5","ISO-2022-KR","GB2312"};

	
	/**
	 * The XML Declaration dialog.
	 *
	 * @param parent the parent frame.
	 */
	public XMLDeclarationDialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Set XML Declaration");
		setDialogDescription( "Specify Version, Encoding and Standalone properties.");

		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
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
		buttonPanel.add( cancelButton);
		*/
		JPanel form = new JPanel( new FormLayout( 10, 2));

		// fill the panel...
		form.add( getDeclarationPanel(), FormLayout.FULL_FILL);

		main.add( form, BorderLayout.CENTER);
		/*
		main.add( buttonPanel, BorderLayout.SOUTH);

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				hide();
			}
		});
		*/
		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
	}
	
	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}
	
	protected void okButtonPressed() {
		super.okButtonPressed();
	}
	
	private JPanel getDeclarationPanel() {
		if ( declarationPanel == null) {
			declarationPanel = new JPanel( new FormLayout( 10, 10));
			declarationPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "XML Declaration"),
										new EmptyBorder( 0, 5, 5, 5)));
			
			declarationPanel.add( new JLabel( "version:"), FormLayout.LEFT);
			versionField = new JTextField();
			
			version10Button = new JRadioButton( "1.0");
			version10Button.setFont( version10Button.getFont().deriveFont( Font.PLAIN));
			version10Button.setPreferredSize( new Dimension( version10Button.getPreferredSize().width, versionField.getPreferredSize().height));
			
			JPanel radioPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 5, 0));
			
			radioPanel.add( version10Button);

			version11Button = new JRadioButton( "1.1");
			version11Button.setFont( version11Button.getFont().deriveFont( Font.PLAIN));
			version11Button.setPreferredSize( new Dimension( version11Button.getPreferredSize().width, versionField.getPreferredSize().height));

			radioPanel.add( version11Button);

			declarationPanel.add( radioPanel, FormLayout.RIGHT_FILL);
			
			ButtonGroup versionGroup = new ButtonGroup();
			versionGroup.add( version10Button);
			versionGroup.add( version11Button);
			
			encodingField = new JComboBox();
			for (int i=0;i<ENCODINGLIST.length;i++)
				encodingField.addItem(ENCODINGLIST[i]);
			
			encodingField.setPreferredSize( new Dimension( encodingField.getPreferredSize().width, versionField.getPreferredSize().height));
			encodingField.setEditable(true);
			encodingField.setFont( encodingField.getFont().deriveFont( Font.PLAIN));

			declarationPanel.add( new JLabel( "encoding:"), FormLayout.LEFT);
			declarationPanel.add( encodingField, FormLayout.RIGHT);
			
			standaloneField  = new JComboBox();
			standaloneField.addItem(STANDALONE_NONE);
			standaloneField.addItem(STANDALONE_NO);
			standaloneField.addItem(STANDALONE_YES);
			
			standaloneField.setFont( standaloneField.getFont().deriveFont( Font.PLAIN));
			standaloneField.setPreferredSize( new Dimension( standaloneField.getPreferredSize().width, versionField.getPreferredSize().height));
			

			declarationPanel.add( new JLabel( "standalone:"), FormLayout.LEFT);
			declarationPanel.add( standaloneField, FormLayout.RIGHT);
		}

		return declarationPanel;
	}
	
	public void show(ExchangerDocument document) 
	{
		JPanel form = new JPanel( new FormLayout(10, 2));
		
		this.document = document;
		setVersion();
		setCurrentEncoding();
		setStandalone();
		
		pack();
		setSize( new Dimension( 300, getSize().height));

		setLocationRelativeTo( parent);

		super.show();
	}
	
	public boolean isVersion10() 
	{
		return version10Button.isSelected();
	}
	
	public boolean isVersion11() 
	{
		return version11Button.isSelected();
	}
	
	public String getEncoding() 
	{
		//int index = encodingField.getSelectedIndex();
		//return ENCODINGLIST[index];
		return (String)encodingField.getSelectedItem(); 
	}
	
	public String getStandalone()
	{
		int index = standaloneField.getSelectedIndex();
		
		switch (index) 
		{
			case 0:
				return STANDALONE_NONE;
			case 1:
				return STANDALONE_NO;
			case 2:
				return STANDALONE_YES;
		}

		return STANDALONE_NONE;
	}
	
	private void setCurrentEncoding()
	{	
		String currentEncoding = document.getEncoding();
		boolean found = false;
		
		int index;
		for (index=0;index<ENCODINGLIST.length;index++)
		{
			if (ENCODINGLIST[index].equalsIgnoreCase(currentEncoding))
			{
				found = true;
				break;
			}
		}
		
		if (found)
			encodingField.setSelectedIndex(index);
		else
			encodingField.setSelectedItem(currentEncoding);
		
			//encodingField.setSelectedIndex(0);
	}
	
	private void setVersion()
	{
	
		if (document.getVersion().equals("1.0"))
		{	
			version10Button.setSelected(true);
		}
		else if (document.getVersion().equals("1.1"))
		{
			version11Button.setSelected(true);
		}
		else
		{
			// unsupported version number, should never get here, use 1.0 as default
			version10Button.setSelected(true);
		}
	}
	
	private void setStandalone()
	{
		if (document.getStandalone().equals(STANDALONE_NONE))
		{
			standaloneField.setSelectedIndex(0);
		}
		else if (document.getStandalone().equals(STANDALONE_NO))
		{
			standaloneField.setSelectedIndex(1);
		}
		else if (document.getStandalone().equals(STANDALONE_YES))
		{
			standaloneField.setSelectedIndex(2);
		}
		else
		{
			// unknown value for standalone, just use NONE as default
			standaloneField.setSelectedIndex(0);
		}
	}

	
}
