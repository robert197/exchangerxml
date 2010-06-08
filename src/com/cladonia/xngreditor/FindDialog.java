/*
 * $Id: FindDialog.java,v 1.8 2005/08/23 14:28:46 tcurley Exp $
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
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bounce.FormLayout;

import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The find dialog for the ExchangerEditor editor.
 *
 * @version	$Revision: 1.8 $, $Date: 2005/08/23 14:28:46 $
 * @author Dogsbay
 */
public class FindDialog extends XngrDialog {
	private Dimension size = null;

	// The components that contain the values
	private JCheckBox xpathBox			= null;
	private JComboBox xpathField		= null;
	private JComboBox basicSearchField	= null;
	private JComboBox searchField		= null;

	private JCheckBox matchCaseButton			= null;
	private JCheckBox matchWholeWordButton		= null;
	private JCheckBox basicMatchCaseButton		= null;
	private JCheckBox basicMatchWholeWordButton	= null;
	private JCheckBox regularExpressionButton	= null;
	private JCheckBox wrapSearchButton			= null;
	private JTabbedPane tabs					= null;
	private JRadioButton upButton				= null;
	private JRadioButton downButton				= null;
	private JRadioButton basicUpButton			= null;
	private JRadioButton basicDownButton		= null;
	
	private JPanel basicPanel			= null;
	private JPanel basicContainer		= null;
	private JPanel advancedPanel		= null;
	private JPanel advancedContainer	= null;
	
	private ConfigurationProperties properties = null;

	private ExchangerEditor xngr = null;

	
	/**
	 * The dialog that displays the properties for the document.
	 *
	 * @param frame the parent frame.
	 */
	public FindDialog( JFrame parent, ConfigurationProperties props, ExchangerEditor xngr) {
		super( parent, true);
		
		if( xngr!= null) {
			this.xngr = xngr;
		}
		properties = props;
		
		setResizable( false);
	    setTitle( "Find", "Find in current Document");
		setDialogDescription( "Specify the Search criteria.");

		JPanel main = new JPanel( new BorderLayout());

		super.okButton.setText("Find");
		super.okButton.setMnemonic('F');

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

		setContentPane( main);
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);

		advancedContainer.add( advancedPanel, BorderLayout.CENTER);
		pack();
		
		setLocationRelativeTo( parent);

		size = getSize();
	}
	
	public boolean isBasic() {
		return tabs.getSelectedIndex() == 0;
	}
	
	private JPanel createAdvancedPanel() {
		JPanel searchPanel = new JPanel( new FormLayout( 0, 0));
		
		xpathField = new JComboBox();
		xpathField.getEditor().getEditorComponent().addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					okButtonPressed();
				}
			}
		});

		xpathField.setFont( xpathField.getFont().deriveFont( Font.PLAIN));
		xpathField.setPreferredSize( new Dimension( 100, 23));
		xpathField.setEditable(true);

		searchField = new JComboBox();
		searchField.getEditor().getEditorComponent().addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					okButtonPressed();
				}
			}
		});

		searchField.setFont( searchField.getFont().deriveFont( Font.PLAIN));
		searchField.setPreferredSize( new Dimension( 100, 23));
		searchField.setEditable(true);

		searchPanel.setBorder( new EmptyBorder( 0, 0, 10, 0));
		xpathBox = new JCheckBox("XPath:");
		xpathBox.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				if(xngr != null) {
					xngr.setWait( true);
		            xngr.setStatus( "Updating Document Model ...");
//					 Run in Thread!!!
		            Runnable runner = new Runnable() {
		                public void run()  {
		                    try {
		                    	
		                    	xngr.getView().updateModel();
		                    	
		                    	boolean error = xngr.getDocument().isError();
		                    	
		                    	xpathBox.setEnabled( !error);
		                		xpathField.setEnabled( !error && xpathBox.isSelected());
		                    	
		                    	
		                    	
		                    	
		                    } catch ( Exception e) {
		                        // This should never happen, just report and continue
		                        MessageHandler.showError( xngr, "Error - Cannot update document", "Find Error");
		                    } finally {
		                        xngr.setStatus( "Done");
		                        xngr.setWait( false);
		                    }
		                }
		            };
		            //Create and start the thread ...
		            Thread thread = new Thread( runner);
		            thread.start();
				}
				
			}
		});
		
		xpathField.setEnabled( false);

		JLabel label = new JLabel("Find:");
		label.setBorder( new EmptyBorder( 0, xpathBox.getBorder().getBorderInsets( xpathBox).left, 0 , 0));

		searchPanel.add( label, FormLayout.LEFT);
		searchPanel.add( searchField, FormLayout.RIGHT_FILL);

		searchPanel.add( getSeparator(), FormLayout.FULL_FILL);

		searchPanel.add( xpathBox, FormLayout.LEFT);
		searchPanel.add( xpathField, FormLayout.RIGHT_FILL);

		matchCaseButton = new JCheckBox( "Case Sensitive");
		matchCaseButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( basicMatchCaseButton != null) {
					basicMatchCaseButton.setSelected( matchCaseButton.isSelected());
				}
			}
		});
		matchCaseButton.setMnemonic( 'C');

		matchWholeWordButton = new JCheckBox( "Whole Word");
		matchWholeWordButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( basicMatchWholeWordButton != null) {
					basicMatchWholeWordButton.setSelected( matchWholeWordButton.isSelected());
				}
			}
		});
		matchWholeWordButton.setMnemonic( 'W');

		wrapSearchButton = new JCheckBox( "Wrap Search");
		wrapSearchButton.setMnemonic( 'p');

		regularExpressionButton = new JCheckBox( "Regular Expression");
		regularExpressionButton.setMnemonic( 'x');

		JPanel optionsPanel = new JPanel( new FormLayout());
		optionsPanel.setBorder( new EmptyBorder( 0, 5, 5, 25));
		optionsPanel.add( matchCaseButton, FormLayout.FULL);
		optionsPanel.add( matchWholeWordButton, FormLayout.FULL);
		optionsPanel.add( regularExpressionButton, FormLayout.FULL);
//		optionsPanel.add( wrapSearchButton, FormLayout.FULL);

		upButton = new JRadioButton( "Backward");
		upButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( basicUpButton != null) {
					basicUpButton.setSelected( upButton.isSelected());
				}
			}
		});
		upButton.setMnemonic( 'B');
		upButton.setFont( upButton.getFont().deriveFont( Font.PLAIN));
		downButton = new JRadioButton( "Forward");
		downButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( basicDownButton != null) {
					basicDownButton.setSelected( downButton.isSelected());
				}
			}
		});
		downButton.setFont( upButton.getFont());
		downButton.setMnemonic( 'o');
		downButton.setSelected( true);

		ButtonGroup group = new ButtonGroup();
		group.add( upButton);
		group.add( downButton);
		
		JPanel directionPanel = new JPanel( new FormLayout( 5, 0));
	
		label = new JLabel( "Direction:");
		label.setBorder( new EmptyBorder( 0, wrapSearchButton.getBorder().getBorderInsets( wrapSearchButton).left, 0 , 0));
		directionPanel.add( label, FormLayout.LEFT);
		directionPanel.add( downButton, FormLayout.RIGHT);
		directionPanel.setBorder( new EmptyBorder( 0, 10, 5, 0));
		directionPanel.add( new JLabel(), FormLayout.LEFT);
		directionPanel.add( upButton, FormLayout.RIGHT);
		directionPanel.add( wrapSearchButton, FormLayout.FULL);

//		directionPanel.add( label, FormLayout.FULL_FILL);
//		directionPanel.add( downButton, FormLayout.FULL);
//		directionPanel.setBorder( new EmptyBorder( 0, 10, 5, 0));
//		directionPanel.add( upButton, FormLayout.FULL);
		
		JPanel gridPanel = new JPanel( new GridLayout());
		gridPanel.setBorder( new TitledBorder( "Options"));
		gridPanel.add( optionsPanel);
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
					okButtonPressed();
				}
			}
		});

		basicSearchField.setFont( basicSearchField.getFont().deriveFont( Font.PLAIN));
		basicSearchField.setPreferredSize( new Dimension( 100, 23));
		basicSearchField.setEditable(true);

		searchPanel.setBorder( new EmptyBorder( 0, 0, 10, 0));

		JLabel label = new JLabel("Find:");
		label.setBorder( new EmptyBorder( 0, xpathBox.getBorder().getBorderInsets( xpathBox).left, 0 , 0));
		label.setPreferredSize( new Dimension( xpathBox.getPreferredSize().width, label.getPreferredSize().height));

		searchPanel.add( label, FormLayout.LEFT);
		searchPanel.add( basicSearchField, FormLayout.RIGHT_FILL);

		basicMatchCaseButton = new JCheckBox( "Case Sensitive");
		basicMatchCaseButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( matchCaseButton != null) {
					matchCaseButton.setSelected( basicMatchCaseButton.isSelected());
				}
			}
		});
		basicMatchCaseButton.setMnemonic( 'C');

		basicMatchWholeWordButton = new JCheckBox( "Whole Word");
		basicMatchWholeWordButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( matchWholeWordButton != null) {
					matchWholeWordButton.setSelected( basicMatchWholeWordButton.isSelected());
				}
			}
		});
		basicMatchWholeWordButton.setMnemonic( 'W');

		JPanel optionsPanel = new JPanel( new FormLayout());
		optionsPanel.setBorder( new EmptyBorder( 0, 5, 5, 25));
		optionsPanel.add( basicMatchCaseButton, FormLayout.FULL);
		optionsPanel.add( basicMatchWholeWordButton, FormLayout.FULL);

		basicUpButton = new JRadioButton( "Backward");
		basicUpButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( upButton != null) {
					upButton.setSelected( basicUpButton.isSelected());
				}
			}
		});

		basicUpButton.setMnemonic( 'B');
		basicUpButton.setFont( basicUpButton.getFont().deriveFont( Font.PLAIN));
		basicDownButton = new JRadioButton( "Forward");
		basicDownButton.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( downButton != null) {
					downButton.setSelected( basicDownButton.isSelected());
				}
			}
		});
		basicDownButton.setFont( basicUpButton.getFont());
		basicDownButton.setMnemonic( 'o');
		basicDownButton.setSelected( true);

		ButtonGroup group = new ButtonGroup();
		group.add( basicUpButton);
		group.add( basicDownButton);
		
		JPanel directionPanel = new JPanel( new FormLayout( 5, 0));
	
		label = new JLabel( "Direction:");
		directionPanel.add( label, FormLayout.LEFT);
		directionPanel.add( basicDownButton, FormLayout.RIGHT);
		directionPanel.setBorder( new EmptyBorder( 0, 10, 5, 0));
		directionPanel.add( new JLabel(), FormLayout.LEFT);
		directionPanel.add( basicUpButton, FormLayout.RIGHT);
		
		JPanel gridPanel = new JPanel( new GridLayout());
		gridPanel.setBorder( new TitledBorder( "Options"));
		gridPanel.add( optionsPanel);
		gridPanel.add( directionPanel);

		JPanel panel = new JPanel( new BorderLayout());
		panel.setBorder( new EmptyBorder( 5, 5, 5, 5));

		panel.add( searchPanel, BorderLayout.NORTH);
		panel.add( gridPanel, BorderLayout.CENTER);
		
		return panel;
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
	 * @return the search text.
	 */
	public String getXPath() {
		String result = null;

		if ( xpathBox.isEnabled() && xpathBox.isSelected()) {
			Object item = xpathField.getEditor().getItem();
	
			if ( item != null) {
				result = item.toString();
			}
		}
		
		return result;
	}

	public void show( String value) {
		
		this.xpathBox.setEnabled(true);
		if ( value != null && value.trim().length() > 0) {
			searchField.getEditor().setItem( value);
			basicSearchField.getEditor().setItem( value);
		}
		super.show();
	}
	
	/**
	 * Wether the search should be case sensitive. 
	 *
	 * @return true when the search should be case sensitive.
	 */
	public boolean isCaseSensitive() {
		return matchCaseButton.isSelected();
	}
	
	/**
	 * Wether the search should be case sensitive. 
	 *
	 * @return true when the search should be case sensitive.
	 */
	public boolean isXPath() {
		return xpathBox.isEnabled() && xpathBox.isSelected() && getXPath() != null && getXPath().trim().length() > 0;
	}

	/**
	 * Wether the search should match the whole word. 
	 *
	 * @return true when the search should match the whole word.
	 */
	public boolean isMatchWholeWord() {
		return matchWholeWordButton.isSelected();
	}

	/**
	 * Wether the search should wrap. 
	 *
	 * @return true when the search should wrap.
	 */
	public boolean isWrapSearch() {
		return wrapSearchButton.isSelected();
	}

	/**
	 * Wether the search should be from top to bottom. 
	 *
	 * @return true when the search direction should be from top to bottom.
	 */
	public boolean isSearchDirectionDown() {
		return downButton.isSelected();
	}

	/**
	 * Wether the search should be from top to bottom. 
	 *
	 * @return true when the search direction should be from top to bottom.
	 */
	public boolean isRegularExpression() {
		return regularExpressionButton.isSelected();
	}

	protected void okButtonPressed() {
		super.okButtonPressed(); 
	}

	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}

	/**
	 * Initialises the values in the dialog.
	 */
	public void init( boolean error) {
		
		if ( properties.isBasicSearch()) {
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

		matchCaseButton.setSelected( properties.isMatchCase());
		matchWholeWordButton.setSelected( properties.isMatchWholeWord());
		regularExpressionButton.setSelected( properties.isRegularExpression());
		wrapSearchButton.setSelected( properties.isWrapSearch());

		xpathBox.setSelected( properties.isXPath());
		
		if ( properties.isDirectionDown()) {
			downButton.setSelected( true);
		} else {
			upButton.setSelected( true);
		}
		
		searchField.setFont( properties.getTextPreferences().getFont().deriveFont( Font.PLAIN));
		basicSearchField.setFont( properties.getTextPreferences().getFont().deriveFont( Font.PLAIN));
		xpathField.setFont( properties.getTextPreferences().getFont().deriveFont( Font.PLAIN));

		if ( isBasic()) {
			basicSearchField.requestFocusInWindow();
			basicSearchField.getEditor().selectAll();
		} else {
			searchField.requestFocusInWindow();
			searchField.getEditor().selectAll();
		}

		xpathBox.setEnabled( !error);
		xpathField.setEnabled( !error && xpathBox.isSelected());
	}

	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));

		return separator;
	}
} 
