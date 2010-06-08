/*
 * $Id: ReplaceDialog.java,v 1.11 2005/11/30 15:54:06 tcurley Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import java.util.regex.Matcher;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bounce.FormLayout;

import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The find dialog for the ExchangerEditor editor.
 *
 * @version	$Revision: 1.11 $, $Date: 2005/11/30 15:54:06 $
 * @author Dogsbay
 */
public class ReplaceDialog extends XngrDialogHeader {
	private Dimension size 				= null;
	
	private JButton cancelButton		= null;
	private JButton findNextButton		= null;
	private JButton replaceButton		= null;
	private JButton replaceAllButton	= null;
	
	// The components that contain the values
	private JCheckBox xpathBox			= null;
	private JComboBox xpathField		= null;
	private JComboBox searchField		= null;
	private JComboBox replaceField		= null;
	private JComboBox basicSearchField		= null;
	private JComboBox basicReplaceField		= null;
	
	private JCheckBox matchCaseButton			= null;
	private JCheckBox matchWholeWordButton		= null;
	private JCheckBox basicMatchCaseButton			= null;
	private JCheckBox basicMatchWholeWordButton		= null;
	private JCheckBox wrapSearchButton			= null;
	private JCheckBox regularExpressionButton	= null;
	
	private JRadioButton upButton		= null;
	private JRadioButton downButton		= null;
	private JRadioButton basicUpButton		= null;
	private JRadioButton basicDownButton		= null;
	
	private JTabbedPane tabs			= null;
	
	private JPanel basicPanel			= null;
	private JPanel basicContainer		= null;
	private JPanel advancedPanel		= null;
	private JPanel advancedContainer	= null;
	
	private ConfigurationProperties properties = null;
	
	private ExchangerEditor parent = null;
	
	/**
	 * The dialog that displays the properties for the document.
	 *
	 * @param frame the parent frame.
	 */
	public ReplaceDialog( ExchangerEditor parent, ConfigurationProperties props) {
		super( parent, false);
		
		properties = props;
		this.parent = parent;
		
		setResizable( false);
		
		setTitle( "Replace", "Find and Replace in current Document");
		setDialogDescription( "Specify the Find/Replace criteria.");
		
		JPanel main = new JPanel( new BorderLayout());
		
		main.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelButtonPressed();
			}
		});
		main.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");
		
		cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});
		
		replaceAllButton = new JButton( "Replace All");
		replaceAllButton.setMnemonic( 'A');
		replaceAllButton.setFont( replaceAllButton.getFont().deriveFont( Font.PLAIN));
		replaceAllButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				replaceAllButtonPressed();
			}
		});
		
		replaceButton = new JButton( "Replace");
		replaceButton.setMnemonic( 'R');
		replaceButton.setFont( replaceButton.getFont().deriveFont( Font.PLAIN));
		replaceButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				replaceButtonPressed();
			}
		});
		
		findNextButton = new JButton( "Find Next");
		findNextButton.setMnemonic( 'F');
		findNextButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				findNextButtonPressed();
			}
		});
		getRootPane().setDefaultButton( findNextButton);
		
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 10, 0, 3, 0));
		buttonPanel.add( findNextButton);
		buttonPanel.add( replaceButton);
		buttonPanel.add( replaceAllButton);
		buttonPanel.add( Box.createHorizontalStrut( 5));
		buttonPanel.add( cancelButton);
		
		basicContainer		= new JPanel( new BorderLayout());
		advancedContainer	= new JPanel( new BorderLayout());
		advancedPanel		= createAdvancedPanel();
		basicPanel			= createBasicPanel();
		
		tabs = new JTabbedPane();
		tabs.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
				if ( !isBasic()) {
					searchField.requestFocusInWindow();
					searchField.getEditor().selectAll();
				} else {
					basicSearchField.requestFocusInWindow();
					basicSearchField.getEditor().selectAll();
				}
			}
			
			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});
		
		tabs.addTab( "Basic", basicContainer);
		tabs.addTab( "Advanced", advancedContainer);
		tabs.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent event) {
				int index = tabs.getSelectedIndex();
				
				if (index == 0) {  // basic
					advancedContainer.remove( advancedPanel);
					basicContainer.add( basicPanel, BorderLayout.CENTER);
					
					int difference = advancedPanel.getPreferredSize().height - basicPanel.getPreferredSize().height;
					setSize( new Dimension( size.width, size.height - difference));
					doLayout();
					
					basicSearchField.requestFocusInWindow();
					basicSearchField.getEditor().selectAll();
				} else {
					basicContainer.remove( basicPanel);
					advancedContainer.add( advancedPanel, BorderLayout.CENTER);
					setSize( size);
					doLayout();
					
					searchField.requestFocusInWindow();
					searchField.getEditor().selectAll();
				}
			}
		});
		
		main.add( tabs, BorderLayout.CENTER);
		main.add( buttonPanel, BorderLayout.SOUTH);
		
		setContentPane( main);
		
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelButtonPressed();
			}
		});
		
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);
		
		advancedContainer.add( advancedPanel, BorderLayout.CENTER);
		pack();
		
		setLocationRelativeTo( parent);
		
		size = getSize();
	}
	
	private JPanel createAdvancedPanel() {
		JPanel searchPanel = new JPanel( new FormLayout( 0, 0));
		
		xpathField = new JComboBox();
		xpathField.getEditor().getEditorComponent().addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					findNextButtonPressed();
				}
			}
		});
		
		xpathField.setFont( xpathField.getFont().deriveFont( Font.PLAIN));
		xpathField.setPreferredSize( new Dimension( 100, 23));
		xpathField.setEditable(true);
		
		xpathBox = new JCheckBox( "XPath filter:");
		xpathBox.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				xpathField.setEnabled( xpathBox.isSelected());
			}
		});
		
		xpathField.setEnabled( false);
		
		searchField = new JComboBox();
		searchField.getEditor().getEditorComponent().addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					findNextButtonPressed();
//					replaceButton.setE
					
				}
			}
		});
		
		searchField.setFont( searchField.getFont().deriveFont( Font.PLAIN));
		searchField.setPreferredSize( new Dimension( 100, 23));
		searchField.setEditable(true);
		JLabel label = new JLabel("Find what:");
		label.setBorder( new EmptyBorder( 0, xpathBox.getBorder().getBorderInsets( xpathBox).left, 0 , 0));
		searchPanel.add( label, FormLayout.LEFT);
		searchPanel.add( searchField, FormLayout.RIGHT_FILL);
		
		replaceField = new JComboBox();
		replaceField.getEditor().getEditorComponent().addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					findNextButtonPressed();
				}
			}
		});
		
		replaceField.setFont( replaceField.getFont().deriveFont( Font.PLAIN));
		replaceField.setPreferredSize( new Dimension( 100, 23));
		replaceField.setEditable(true);
		
		label = new JLabel("Replace with:");
		label.setBorder( new EmptyBorder( 0, xpathBox.getBorder().getBorderInsets( xpathBox).left, 0 , 0));
		
		searchPanel.add( label, FormLayout.LEFT);
		searchPanel.add( replaceField, FormLayout.RIGHT_FILL);
		
		searchPanel.add( getSeparator(), FormLayout.FULL_FILL);
		
		searchPanel.add( xpathBox, FormLayout.LEFT);
		searchPanel.add( xpathField, FormLayout.RIGHT_FILL);
		
		searchPanel.setBorder( new EmptyBorder( 0, 0, 10, 0));
		
		matchCaseButton = new JCheckBox( "Case Sensitive");
		matchCaseButton.setMnemonic( 'C');
		matchCaseButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( basicMatchCaseButton != null) {
					basicMatchCaseButton.setSelected( matchCaseButton.isSelected());
				}
			}
		});
		
		wrapSearchButton = new JCheckBox( "Wrap Search");
		wrapSearchButton.setMnemonic( 'p');
		
		matchWholeWordButton = new JCheckBox( "Whole Word");
		matchWholeWordButton.setMnemonic( 'W');
		matchWholeWordButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( basicMatchWholeWordButton != null) {
					basicMatchWholeWordButton.setSelected( matchWholeWordButton.isSelected());
				}
			}
		});
		
		regularExpressionButton = new JCheckBox( "Regular Expression");
		regularExpressionButton.setMnemonic( 'x');
		
		JPanel matchCasePanel = new JPanel( new FormLayout());
		matchCasePanel.setBorder( new EmptyBorder( 0, 5, 5, 25));
		matchCasePanel.add( matchCaseButton, FormLayout.FULL);
		matchCasePanel.add( matchWholeWordButton, FormLayout.FULL);
		matchCasePanel.add( regularExpressionButton, FormLayout.FULL);
		matchCasePanel.add( wrapSearchButton, FormLayout.FULL);
		
		upButton = new JRadioButton( "Backward");
		upButton.setMnemonic( 'B');
		upButton.setFont( upButton.getFont().deriveFont( Font.PLAIN));
		upButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( basicUpButton != null) {
					basicUpButton.setSelected( upButton.isSelected());
				}
			}
		});
		
		downButton = new JRadioButton( "Forward");
		downButton.setFont( upButton.getFont());
		downButton.setMnemonic( 'o');
		downButton.setSelected( true);
		downButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( basicDownButton != null) {
					basicDownButton.setSelected( downButton.isSelected());
				}
			}
		});
		
		ButtonGroup group = new ButtonGroup();
		group.add( upButton);
		group.add( downButton);
		
		JPanel directionPanel = new JPanel( new FormLayout( 5, 0));
		directionPanel.setBorder( new EmptyBorder( 0, 10, 5, 0));
		
		label = new JLabel( "Direction:");
		label.setBorder( new EmptyBorder( 0, wrapSearchButton.getBorder().getBorderInsets( wrapSearchButton).left, 0 , 0));
		directionPanel.add( label, FormLayout.LEFT);
		directionPanel.add( downButton, FormLayout.RIGHT);
		directionPanel.add( new JLabel(), FormLayout.LEFT);
		directionPanel.add( upButton, FormLayout.RIGHT);
		directionPanel.add( wrapSearchButton, FormLayout.FULL);
		
		JPanel gridPanel = new JPanel( new GridLayout());
		gridPanel.setBorder( new TitledBorder( "Options"));
		gridPanel.add( matchCasePanel);
		gridPanel.add( directionPanel);
		
		JPanel panel = new JPanel( new BorderLayout());
		panel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		panel.add( searchPanel, BorderLayout.NORTH);
		panel.add( gridPanel, BorderLayout.CENTER);
		
		return panel;
	}
	
	private JPanel createBasicPanel() {
		JPanel searchPanel = new JPanel( new FormLayout( 0, 0));
		
		basicSearchField = new JComboBox();
		basicSearchField.getEditor().getEditorComponent().addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					findNextButtonPressed();
					
				}
			}
		});
		
		basicSearchField.setFont( basicSearchField.getFont().deriveFont( Font.PLAIN));
		basicSearchField.setPreferredSize( new Dimension( 100, 23));
		basicSearchField.setEditable(true);
		
		JLabel label = new JLabel("Find what:");
		label.setBorder( new EmptyBorder( 0, xpathBox.getBorder().getBorderInsets( xpathBox).left, 0 , 0));
		label.setPreferredSize( new Dimension( xpathBox.getPreferredSize().width, label.getPreferredSize().height));
		searchPanel.add( label, FormLayout.LEFT);
		searchPanel.add( basicSearchField, FormLayout.RIGHT_FILL);
		
		basicReplaceField = new JComboBox();
		basicReplaceField.getEditor().getEditorComponent().addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					findNextButtonPressed();
				}
			}
		});
		
		basicReplaceField.setFont( basicReplaceField.getFont().deriveFont( Font.PLAIN));
		basicReplaceField.setPreferredSize( new Dimension( 100, 23));
		basicReplaceField.setEditable(true);
		
		label = new JLabel("Replace with:");
		label.setBorder( new EmptyBorder( 0, xpathBox.getBorder().getBorderInsets( xpathBox).left, 0 , 0));
		
		searchPanel.add( label, FormLayout.LEFT);
		searchPanel.add( basicReplaceField, FormLayout.RIGHT_FILL);
		
		searchPanel.setBorder( new EmptyBorder( 0, 0, 10, 0));
		
		basicMatchCaseButton = new JCheckBox( "Case Sensitive");
		basicMatchCaseButton.setMnemonic( 'C');
		basicMatchCaseButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( matchCaseButton != null) {
					matchCaseButton.setSelected( basicMatchCaseButton.isSelected());
				}
			}
		});
		
		basicMatchWholeWordButton = new JCheckBox( "Whole Word");
		basicMatchWholeWordButton.setMnemonic( 'W');
		basicMatchWholeWordButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( matchWholeWordButton != null) {
					matchWholeWordButton.setSelected( basicMatchWholeWordButton.isSelected());
				}
			}
		});
		
		JPanel matchCasePanel = new JPanel( new FormLayout());
		matchCasePanel.setBorder( new EmptyBorder( 0, 5, 5, 25));
		matchCasePanel.add( basicMatchCaseButton, FormLayout.FULL);
		matchCasePanel.add( basicMatchWholeWordButton, FormLayout.FULL);
		
		basicUpButton = new JRadioButton( "Backward");
		basicUpButton.setMnemonic( 'B');
		basicUpButton.setFont( basicUpButton.getFont().deriveFont( Font.PLAIN));
		basicUpButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( upButton != null) {
					upButton.setSelected( basicUpButton.isSelected());
				}
			}
		});
		
		basicDownButton = new JRadioButton( "Forward");
		basicDownButton.setFont( basicUpButton.getFont());
		basicDownButton.setMnemonic( 'o');
		basicDownButton.setSelected( true);
		basicDownButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( downButton != null) {
					downButton.setSelected( basicDownButton.isSelected());
				}
			}
		});
		
		ButtonGroup group = new ButtonGroup();
		group.add( basicUpButton);
		group.add( basicDownButton);
		
		JPanel directionPanel = new JPanel( new FormLayout( 5, 0));
		directionPanel.setBorder( new EmptyBorder( 0, 10, 5, 0));
		
		label = new JLabel( "Direction:");
		directionPanel.add( label, FormLayout.LEFT);
		directionPanel.add( basicDownButton, FormLayout.RIGHT);
		directionPanel.add( new JLabel(), FormLayout.LEFT);
		directionPanel.add( basicUpButton, FormLayout.RIGHT);
		
		JPanel gridPanel = new JPanel( new GridLayout());
		gridPanel.setBorder( new TitledBorder( "Options"));
		gridPanel.add( matchCasePanel);
		gridPanel.add( directionPanel);
		
		JPanel panel = new JPanel( new BorderLayout());
		panel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		panel.add( searchPanel, BorderLayout.NORTH);
		panel.add( gridPanel, BorderLayout.CENTER);
		
		return panel;
	}
	
	private boolean isBasic() {
		return tabs.getSelectedIndex() == 0;
	}
	
	/**
	 * Returns the search text... 
	 *
	 * @return the search text.
	 */
	public String getSearch() {
		String result = null;
		Object item = null;
		
		if ( isBasic()) {
			item = basicSearchField.getEditor().getItem();
		} else {
			item = searchField.getEditor().getItem();
		}
		
		if ( item != null) {
			result = item.toString();
		}
		
		return result;
	}
	
	/**
	 * Returns the xpath text... 
	 *
	 * @return the xpath text.
	 */
	public String getXPath() {
		String result = null;
		
		if ( !isBasic() && xpathBox.isEnabled() && xpathBox.isSelected()) {
			Object item = xpathField.getEditor().getItem();
			
			if ( item != null) {
				result = item.toString();
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the search text... 
	 *
	 * @return the search text.
	 */
	public String getReplace() {
		String result = null;
		
		Object item = null;
		
		if ( isBasic()) {
			item = basicReplaceField.getEditor().getItem();
		} else {
			item = replaceField.getEditor().getItem();
		}
		
		if ( item != null) {
			result = item.toString();
		}
		
		return result;
	}
	
	/**
	 * Wether the search should be case sensitive. 
	 *
	 * @return true when the search should be case sensitive.
	 */
	public boolean isCaseSensitive() {
		return matchCaseButton.isSelected();
	}
	
	public boolean isXPath() {
		return !isBasic() && xpathBox.isEnabled() && xpathBox.isSelected() && getXPath() != null && getXPath().trim().length() > 0;
	}
	
	/**
	 * Wether the search should be case sensitive. 
	 *
	 * @return true when the search should be case sensitive.
	 */
	public boolean isMatchWholeWord() {
		return matchWholeWordButton.isSelected();
	}
	
	/**
	 * Wether the search should be wrapped. 
	 *
	 * @return true when the search should be wrapped.
	 */
	public boolean isWrapSearch() {
		return isBasic() || wrapSearchButton.isSelected();
	}
	
	/**
	 * Wether the search should be case sensitive. 
	 *
	 * @return true when the search should be case sensitive.
	 */
	public boolean isRegularExpression() {
		return !isBasic() && regularExpressionButton.isSelected();
	}
	
	/**
	 * Wether the search should be from top to bottom. 
	 *
	 * @return true when the search direction should be from top to bottom.
	 */
	public boolean isSearchDirectionDown() {
		return downButton.isSelected();
	}
	
	private Matcher matcher = null;
	
	private void findNextButtonPressed() {
		Editor editor = parent.getView().getEditor();
		
		matcher = editor.search( getXPath(), getSearch(), isRegularExpression(), isCaseSensitive(), isMatchWholeWord(), isSearchDirectionDown(), isWrapSearch());
		
		if ( matcher != null) {
			replaceButton.setEnabled( true);
		} else {
			replaceButton.setEnabled( false);
		}
		
		properties.addReplace( getReplace());
		properties.addSearch( getSearch());
		
		if ( !isBasic()) {
			properties.addXPath( getXPath());
		}
		
		updateComboBoxes();
	}
	
	private void replaceButtonPressed() {
		Editor editor = parent.getView().getEditor();
		String replace = getReplace();
		
		editor.replace( matcher, getReplace(), isRegularExpression(), isSearchDirectionDown());
		matcher = editor.search( getXPath(), getSearch(), isRegularExpression(), isCaseSensitive(), isMatchWholeWord(), isSearchDirectionDown(), isWrapSearch());
		
		if ( matcher != null) {
			replaceButton.setEnabled( true);
		} else {
			replaceButton.setEnabled( false);
		}
		
		properties.addReplace( getReplace());
		properties.addSearch( getSearch());
		
		if ( !isBasic()) {
			properties.addXPath( getXPath());
		}
		
		updateComboBoxes();
	}
	
	private void replaceAllButtonPressed() {
		
		
		Editor editor = parent.getView().getEditor();
		int caret = editor.getCursorPosition();
		
		boolean down = isSearchDirectionDown();
		
		if ( isWrapSearch()) {
			editor.setCursorPosition( 0);
			down = true;
		}
		
		replaceButton.setEnabled( false);
		
		String replace = getReplace();
		
		
		//matcher = editor.search( getXPath(), getSearch(), isRegularExpression(), isCaseSensitive(), isMatchWholeWord(), down, false);
		
		/*while ( matcher != null) {
		 editor.replace( matcher, getReplace(), isRegularExpression(), down);
		 
		 matcher = editor.search( getXPath(), getSearch(), isRegularExpression(), isCaseSensitive(), isMatchWholeWord(), down, false);
		 
		 
		 }*/
		editor.replaceAll(getSearch(), getReplace(), isRegularExpression(), isCaseSensitive(), isMatchWholeWord());
		
		
		properties.addReplace( getReplace());
		properties.addSearch( getSearch());
		
		if ( !isBasic()) {
			properties.addXPath( getXPath());
		}
		
		updateComboBoxes();
		
		editor.setCursorPosition( caret);
		
		
	}
	
	protected void cancelButtonPressed() {
		//setVisible(false);
		hide();
		
		properties.setBasicReplace( isBasic());
		
		properties.setMatchCase( isCaseSensitive());
		properties.setMatchWholeWord( isMatchWholeWord());
		properties.setDirectionDown( isSearchDirectionDown());
		
		properties.setXPath( xpathBox.isSelected());
		properties.setWrapSearch( wrapSearchButton.isSelected());
		properties.setRegularExpression( regularExpressionButton.isSelected());
		
		parent.getView().getEditor().setFocus();
	}
	
	private void updateComboBoxes() {
		if ( searchField.getItemCount() > 0) {
			searchField.removeAllItems();
		}
		
		if ( basicSearchField.getItemCount() > 0) {
			basicSearchField.removeAllItems();
		}
		
		if ( xpathField.getItemCount() > 0) {
			xpathField.removeAllItems();
		}
		
		Vector searches = properties.getSearches();
		Vector xpaths = properties.getXPaths();
		
		for ( int i = 0; i < searches.size(); i++) {
			searchField.addItem( searches.elementAt(i));
		}
		
		for ( int i = 0; i < searches.size(); i++) {
			basicSearchField.addItem( searches.elementAt(i));
		}
		
		for ( int i = 0; i < xpaths.size(); i++) {
			xpathField.addItem( xpaths.elementAt(i));
		}
		
		if ( searchField.getItemCount() > 0) {
			searchField.setSelectedIndex( 0);
		}
		
		if ( basicSearchField.getItemCount() > 0) {
			basicSearchField.setSelectedIndex( 0);
		}
		
		if ( xpathField.getItemCount() > 0) {
			xpathField.setSelectedIndex( 0);
		}
		
		searchField.requestFocusInWindow();
		
		if ( replaceField.getItemCount() > 0) {
			replaceField.removeAllItems();
		}
		
		if ( basicReplaceField.getItemCount() > 0) {
			basicReplaceField.removeAllItems();
		}
		
		Vector replaces = properties.getReplaces();
		
		for ( int i = 0; i < replaces.size(); i++) {
			replaceField.addItem( replaces.elementAt(i));
		}
		
		for ( int i = 0; i < replaces.size(); i++) {
			basicReplaceField.addItem( replaces.elementAt(i));
		}
		
		if ( replaceField.getItemCount() > 0) {
			replaceField.setSelectedIndex( 0);
		}
		
		if ( basicReplaceField.getItemCount() > 0) {
			basicReplaceField.setSelectedIndex( 0);
		}
	}
	
	/**
	 * Initialises the values in the dialog.
	 */
	public void init( String text, boolean error) {
		updateComboBoxes();
		
		if ( properties.isBasicReplace()) {
			tabs.setSelectedIndex(0);
			advancedContainer.remove( advancedPanel);
			basicContainer.add( basicPanel, BorderLayout.CENTER);
			
			int difference = advancedPanel.getPreferredSize().height - basicPanel.getPreferredSize().height;
			setSize( new Dimension( size.width, size.height - difference));
			doLayout();
		} else {
			tabs.setSelectedIndex(1);
			basicContainer.remove( basicPanel);
			advancedContainer.add( advancedPanel, BorderLayout.CENTER);
			setSize( size);
			doLayout();
		}
		
		matchCaseButton.setSelected( properties.isMatchCase());
		regularExpressionButton.setSelected( properties.isRegularExpression());
		matchWholeWordButton.setSelected( properties.isMatchWholeWord());
		wrapSearchButton.setSelected( properties.isWrapSearch());
		
		if ( properties.isDirectionDown()) {
			downButton.setSelected( true);
		} else {
			upButton.setSelected( true);
		}
		
		replaceField.setFont( properties.getTextPreferences().getFont().deriveFont( Font.PLAIN));
		searchField.setFont( properties.getTextPreferences().getFont().deriveFont( Font.PLAIN));
		basicReplaceField.setFont( properties.getTextPreferences().getFont().deriveFont( Font.PLAIN));
		basicSearchField.setFont( properties.getTextPreferences().getFont().deriveFont( Font.PLAIN));
		xpathBox.setSelected( properties.isXPath());
		xpathField.setFont( properties.getTextPreferences().getFont().deriveFont( Font.PLAIN));
		
		if ( text != null && text.trim().length() > 0) {
			searchField.getEditor().setItem( text);
		}
		
		if ( text != null && text.trim().length() > 0) {
			basicSearchField.getEditor().setItem( text);
		}
		
		if ( isBasic()) {
			basicSearchField.requestFocusInWindow();
			basicSearchField.getEditor().selectAll();
		} else {
			searchField.requestFocusInWindow();
			searchField.getEditor().selectAll();
		}
		
		replaceButton.setEnabled( false);
		
		xpathBox.setEnabled( !error);
		xpathField.setEnabled( !error && xpathBox.isSelected());
	}
	
	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));
		
		return separator;
	}
} 
