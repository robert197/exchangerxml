/*
 * $Id: C14NDialog.java,v 1.6 2004/10/27 13:26:16 edankert Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
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

import java.util.Vector;

import org.bounce.FormLayout;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.PrefixNamespaceMappingPanel;

/**
 * The C14N dialog.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/10/27 13:26:16 $
 * @author Dogs bay
 */
public class C14NDialog extends XngrDialog {
	
	private JFrame parent		= null;
	private JPanel c14nPanel	= null;
	private JPanel xpathPanel	= null;
	private JPanel outputPanel = null;
	private ExchangerDocument document	= null;
	private JRadioButton exclButton	= null;
	private JRadioButton exclWithCommentsButton	= null;
	private JRadioButton normalButton	= null;
	private JRadioButton normalWithCommentsButton	= null;
	private JRadioButton outputToNewDocumentButton = null;
	private JRadioButton outputToSameDocumentButton = null;
	private JComboBox xpathField = null;
	private Vector predicateHistory = null;
	private ConfigurationProperties props = null;
	private PrefixNamespaceMappingPanel mappingPanel = null;

	// all the allowed c14N algorithms
	private static final String TRANSFORM_C14N_OMIT_COMMENTS = 
		"http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
	private static final String TRANSFORM_C14N_WITH_COMMENTS  =  
		"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
	private static final String TRANSFORM_C14N_EXCL_OMIT_COMMENTS= 
		"http://www.w3.org/2001/10/xml-exc-c14n#";
	private static final String TRANSFORM_C14N_EXCL_WITH_COMMENTS =
		"http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
	
	
	/**
	 * The C14N dialog.
	 *
	 * @param parent the parent frame.
	 */
	public C14NDialog( JFrame parent,ConfigurationProperties props) 
	{
		super( parent, true);
		
		this.parent = parent;
		this.props = props;
		
		setResizable( false);
		setTitle( "Canonicalize XML Document");
		setDialogDescription( "Specify Canonicalization settings");
		
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
		form.add(getC14NPanel(), FormLayout.FULL_FILL);
		form.add(getXpathPanel(),FormLayout.FULL_FILL);
		form.add(getOutputPanel(),FormLayout.FULL_FILL);

		main.add( form, BorderLayout.CENTER);
		//main.add( buttonPanel, BorderLayout.SOUTH);

		/*addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				mappingPanel.save();
				hide();
			}
		});
*/
		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
	}
	
	protected void cancelButtonPressed() {
		mappingPanel.save();
		super.cancelButtonPressed();
	}
	
	protected void okButtonPressed() {
		
		String xpathPred = (String)xpathField.getSelectedItem();
		if ((xpathPred != null) && (!xpathPred.equals("")))
		{
			props.addXPathPredicate(xpathPred);
		}
		
		mappingPanel.save();
		super.okButtonPressed();
	}
	
	private JPanel getC14NPanel() {
		if ( c14nPanel == null) {
			c14nPanel = new JPanel( new FormLayout( 10, 5));
			c14nPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Canonicalization"),
										new EmptyBorder( 0, 5, 5, 5)));
			
			c14nPanel.add( new JLabel( "C14N Method:"), FormLayout.LEFT);
			JTextField methodField = new JTextField();
			
			exclButton = new JRadioButton( "Exclusive");
			exclButton.setFont( exclButton.getFont().deriveFont( Font.PLAIN));
			exclButton.setPreferredSize( new Dimension( exclButton.getPreferredSize().width, methodField.getPreferredSize().height));
			c14nPanel.add(exclButton, FormLayout.RIGHT);

			exclWithCommentsButton = new JRadioButton( "Exclusive With Comments");
			exclWithCommentsButton.setFont( exclWithCommentsButton.getFont().deriveFont( Font.PLAIN));
			exclWithCommentsButton.setPreferredSize( new Dimension( exclWithCommentsButton.getPreferredSize().width, methodField.getPreferredSize().height));
			c14nPanel.add(exclWithCommentsButton, FormLayout.RIGHT);
			
			normalButton = new JRadioButton("Inclusive");
			normalButton.setFont( normalButton.getFont().deriveFont( Font.PLAIN));
			normalButton.setPreferredSize( new Dimension( normalButton.getPreferredSize().width, methodField.getPreferredSize().height));
			c14nPanel.add(normalButton, FormLayout.RIGHT);
			
			normalWithCommentsButton = new JRadioButton("Inclusive With Comments");
			normalWithCommentsButton.setFont( normalWithCommentsButton.getFont().deriveFont( Font.PLAIN));
			normalWithCommentsButton.setPreferredSize( new Dimension( normalWithCommentsButton.getPreferredSize().width, methodField.getPreferredSize().height));
			c14nPanel.add(normalWithCommentsButton, FormLayout.RIGHT);

			ButtonGroup c14nGroup = new ButtonGroup();
			c14nGroup.add(exclButton);
			c14nGroup.add(exclWithCommentsButton);
			c14nGroup.add(normalButton);
			c14nGroup.add(normalWithCommentsButton);
			exclButton.setSelected(true);
				
		}

		return c14nPanel;
	}
	
	private JPanel getXpathPanel() {
		if (xpathPanel == null) {
			xpathPanel = new JPanel( new FormLayout( 10, 5));
			xpathPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Xpath"),
										new EmptyBorder( 0, 5, 5, 5)));
			
			JTextField methodField = new JTextField();
			
			xpathPanel.add(new JLabel( "Predicate:"), FormLayout.LEFT);
			
			// the xpath prediate combo, comtains previous predicates used
			xpathField = new JComboBox();
			//xpathField.addItem("TO DO");
			xpathField.setFont( xpathField.getFont().deriveFont( Font.PLAIN));
			xpathField.setPreferredSize( new Dimension( xpathField.getPreferredSize().width, methodField.getPreferredSize().height));
			xpathField.setEditable(true);
			xpathPanel.add(xpathField, FormLayout.RIGHT_FILL);
			
			xpathPanel.add( new JLabel( " "), FormLayout.FULL_FILL);
			
			xpathPanel.add(new JLabel( "Prefix Namespace Mapping:"), FormLayout.FULL_FILL);
			
			mappingPanel = new PrefixNamespaceMappingPanel(parent,props,3);
			
			xpathPanel.add(mappingPanel, FormLayout.FULL_FILL);
		}

		return xpathPanel;
	}
	
	private JPanel getOutputPanel() {
		if ( outputPanel == null) {
			outputPanel = new JPanel( new FormLayout( 10, 2));
			outputPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Output"),
										new EmptyBorder( 0, 5, 5, 5)));
			
			JTextField outputLocationField = new JTextField();
			
			outputToNewDocumentButton	= new JRadioButton( "To New Document");
			outputToNewDocumentButton.setPreferredSize( new Dimension( outputToNewDocumentButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			outputPanel.add( outputToNewDocumentButton, FormLayout.FULL);


			outputToSameDocumentButton	= new JRadioButton( "To Current Document");
			outputToSameDocumentButton.setPreferredSize( new Dimension( outputToSameDocumentButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			outputPanel.add( outputToSameDocumentButton, FormLayout.FULL);
			
			ButtonGroup outputGroup = new ButtonGroup();
			outputGroup.add( outputToSameDocumentButton);
			outputGroup.add( outputToNewDocumentButton);

			outputToSameDocumentButton.setSelected( true);
		}
		
		return outputPanel;
	}

	
	public void show(ExchangerDocument document) 
	{
		JPanel form = new JPanel( new FormLayout(10, 2));
		
		this.document = document;
		
		// get the predicate history
		predicateHistory = props.getXPathPredicates();

		// set the combo
		setPredicateHistory();
		
		// set the prefix namespace mapping
		mappingPanel.init();
		
		pack();
		setSize( new Dimension( 400, getSize().height));

		setLocationRelativeTo( parent);

		super.show();
	}
	
	
	/**
	 * returns the xpath predicate expression
	 *
	 * @return String The xpath predicate
	 */
	public String getXpathPredicate()
	{
		return (String)xpathField.getSelectedItem(); 
	}
	
	public String getC14N()
	{
		if (exclButton.isSelected())
		{
			return TRANSFORM_C14N_EXCL_OMIT_COMMENTS;
		}
		else if (exclWithCommentsButton.isSelected())
		{
			return TRANSFORM_C14N_EXCL_WITH_COMMENTS;
		}
		else if (normalButton.isSelected())
		{
			return TRANSFORM_C14N_OMIT_COMMENTS;
		}
		else
		{
			return TRANSFORM_C14N_WITH_COMMENTS;
		}
	}
	
	private void setPredicateHistory()
	{
		xpathField.removeAllItems();
		
		for (int i=0;i<predicateHistory.size();i++)
		{
			xpathField.addItem((String)predicateHistory.get(i));
		}
		
		xpathField.setSelectedIndex(-1);
	}
	
	public boolean isOutputToNewDocument() {
		return outputToNewDocumentButton.isSelected();
	}

	public boolean isOutputToSameDocument() {
		return outputToSameDocumentButton.isSelected();
	}

}
