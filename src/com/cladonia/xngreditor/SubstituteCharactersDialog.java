/*
 * $Id: SubstituteCharactersDialog.java,v 1.4 2004/11/04 19:21:49 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.bounce.FormLayout;

import com.cladonia.xml.editor.EditorProperties;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The dialog that shows the settings for character substitution.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/11/04 19:21:49 $
 * @author Dogsbay
 */
public class SubstituteCharactersDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 250, 300);
	
	private EditorProperties properties = null;

	// The components that contain the values
	private JCheckBox convertLtButton			= null;
	private JCheckBox convertGtButton			= null;
	private JCheckBox convertAmpButton			= null;
	private JCheckBox convertQuotButton			= null;
	private JCheckBox convertAposButton			= null;

	private JCheckBox escapeCharactersButton		= null;
	private JFormattedTextField characterEntityRangeField	= null;
	private JLabel characterEntityRangeLabel		= null;
	private JCheckBox useNamedEntitiesButton		= null;

	/**
	 * The dialog that displays the possible entities.
	 *
	 * @param frame the parent frame.
	 */
	public SubstituteCharactersDialog( JFrame parent, ConfigurationProperties props) {
		super( parent, true);
		
		properties = props.getEditorProperties();
		
		setResizable( false);
		setTitle( "Convert Characters", "Convert Characters to Entities");
		setDialogDescription( "Specify conversion settings");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 10, 5, 5, 5));

		main.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelButtonPressed();
			}
		});
		main.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");
		
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
		buttonPanel.setBorder( new EmptyBorder( 10, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);*/

		convertLtButton 	= new JCheckBox( "Convert < to &lt;");
		convertGtButton 	= new JCheckBox( "Convert > to &gt;");
		convertAmpButton 	= new JCheckBox( "Convert & to &amp;");
		convertQuotButton 	= new JCheckBox( "Convert \" to &quot;");
		convertAposButton 	= new JCheckBox( "Convert ' to &apos;");

		JPanel centerPanel = new JPanel( new FormLayout( 5, 0));
		centerPanel.add( convertLtButton, FormLayout.FULL);
		centerPanel.add( convertGtButton, FormLayout.FULL);
		centerPanel.add( convertAmpButton, FormLayout.FULL);
		centerPanel.add( convertQuotButton, FormLayout.FULL);
		centerPanel.add( convertAposButton, FormLayout.FULL);
		
		escapeCharactersButton = new JCheckBox( "Escape Unicode Characters");
		escapeCharactersButton.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				characterEntityRangeField.setEnabled( escapeCharactersButton.isSelected());
				characterEntityRangeLabel.setEnabled( escapeCharactersButton.isSelected());
			}
		});

		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setMinimumIntegerDigits( 0);

		characterEntityRangeField = new JFormattedTextField( format);
		characterEntityRangeField.setPreferredSize( new Dimension( 50, characterEntityRangeField.getPreferredSize().height));
		characterEntityRangeField.setHorizontalAlignment( JFormattedTextField.RIGHT);
		
		characterEntityRangeLabel = new JLabel( "Start at Entry:");
		
		characterEntityRangeField.setEnabled( false);
		characterEntityRangeLabel.setEnabled( false);

		JPanel characterEntityRangePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 5, 0));
		characterEntityRangePanel.add( characterEntityRangeLabel);
		characterEntityRangePanel.add( characterEntityRangeField);
		characterEntityRangePanel.setBorder( new EmptyBorder( 0, 17, 0, 0));
				
		centerPanel.add( getSeparator(), FormLayout.FULL_FILL);
		centerPanel.add( escapeCharactersButton, FormLayout.FULL);
		centerPanel.add( characterEntityRangePanel, FormLayout.FULL);
		centerPanel.add( getSeparator(), FormLayout.FULL_FILL);
		
		useNamedEntitiesButton = new JCheckBox( "Convert to named entities.");
		centerPanel.add( useNamedEntitiesButton, FormLayout.FULL);
		
		centerPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Conversion Settings:"),
									new EmptyBorder( 5, 10, 10, 10)));

		main.add( centerPanel, BorderLayout.CENTER);
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
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), this.getSize().height));

		setLocationRelativeTo( parent);
	}
	
	public void show() {
		convertLtButton.setSelected( properties.isConvertLtToEntity());
		convertGtButton.setSelected( properties.isConvertGtToEntity());
		convertAmpButton.setSelected( properties.isConvertAmpToEntity());
		convertQuotButton.setSelected( properties.isConvertQuotToEntity());
		convertAposButton.setSelected( properties.isConvertAposToEntity());
	
		escapeCharactersButton.setSelected( properties.isEscapeUnicodeCharacters());
		characterEntityRangeField.setValue( new Integer( properties.getUnicodeStartEntry()));
		useNamedEntitiesButton.setSelected( properties.isConvertCharacterToNamedEntity());
 
 		super.show();
	}
	
	protected void okButtonPressed() {
		properties.setConvertLtToEntity( convertLtButton.isSelected());
		properties.setConvertGtToEntity( convertGtButton.isSelected());
		properties.setConvertAmpToEntity( convertAmpButton.isSelected());
		properties.setConvertQuotToEntity( convertQuotButton.isSelected());
		properties.setConvertAposToEntity( convertAposButton.isSelected());

		properties.setEscapeUnicodeCharacters( escapeCharactersButton.isSelected());

		int value = -1;

		try {
			characterEntityRangeField.commitEdit();
			value = ((Long)characterEntityRangeField.getValue()).intValue();
		} catch( Exception e) { 
			e.printStackTrace();
		}

		properties.setUnicodeStartEntry( value);
		properties.setConvertCharacterToNamedEntity( useNamedEntitiesButton.isSelected());
 
 		super.okButtonPressed();
	}

	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}

	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));

		return separator;
	}
} 
