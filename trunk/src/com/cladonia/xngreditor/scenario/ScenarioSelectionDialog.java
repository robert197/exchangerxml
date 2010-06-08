/*
 * $Id: ScenarioSelectionDialog.java,v 1.4 2004/10/13 18:30:53 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.scenario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.event.DoubleClickListener;

import com.cladonia.xngreditor.XngrDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The scenario properties selection dialog.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/10/13 18:30:53 $
 * @author Dogsbay
 */
public class ScenarioSelectionDialog extends XngrDialog {
	private static final boolean DEBUG = false;
	private static final Dimension SIZE = new Dimension( 300, 300);

	private ConfigurationProperties properties = null;

	private Vector scenarios = null;

	private JList list	= null;

	private ScenarioListModel model = null;

	private JCheckBox showAllCheck = null;
	
	/**
	 * The dialog that displays the list of scenario properties.
	 *
	 * @param frame the parent frame.
	 * @param props the configuration properties.
	 */
	public ScenarioSelectionDialog( JFrame parent, ConfigurationProperties props, String confirm, boolean toggleAll, boolean multipleSelection) {
		super( parent, true);
		
		this.properties = props;
		
		setResizable( false);
		
		setTitle( "Select Scenario");
		setDialogDescription( "Select a Scenario to \""+confirm+"\".");
		
		list = new JList();
		list.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				int index = list.getSelectedIndex();

				if ( index != -1) { // A row is selected...
					okButtonPressed();
				}
			}
		});
		
		list.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = list.getSelectedIndex();
                if(selected>-1) {
                    list.ensureIndexIsVisible(selected);
                }
            }
		    
		});
		
		if ( multipleSelection) {
			list.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		} else {
			list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
		}
			

		//removed for xngr-dialog
		super.okButton.setText(confirm);
		/*okButton = new JButton( confirm);
		cancelButton = new JButton("Cancel");
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelled = false;
				hide();
			}
		});
		
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelled = true;
				hide();
			}
		});

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				hide();
			}
		});

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
				
		getRootPane().setDefaultButton( okButton);*/
		
		JPanel mainPanel = new JPanel( new BorderLayout());

		JPanel listPanel = new JPanel( new BorderLayout());
		listPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Select Scenario"),
									new EmptyBorder( 0, 5, 5, 5)));

		JScrollPane scrollPane = new JScrollPane(	list,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		listPanel.add( scrollPane, BorderLayout.CENTER);
		
		if ( toggleAll) {
			JPanel showAllPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
			showAllCheck = new JCheckBox("Show All Scenarios");
			showAllCheck.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					showAll( showAllCheck.isSelected());
				}
			});
			
			showAllPanel.add( showAllCheck);
			
			listPanel.add( showAllPanel, BorderLayout.SOUTH);
		}

		mainPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		mainPanel.add( listPanel, BorderLayout.CENTER);
		//mainPanel.add( buttonPanel, BorderLayout.SOUTH);

		this.setContentPane( mainPanel); 

		setSize( SIZE);
		
		setLocationRelativeTo( parent);
	}
	
	/**
	 * Set the properties.
	 *
	 * @param properties the list of grammar properties.
	 */
	public void show( Vector properties, ScenarioProperties defaultScenario) {
		this.scenarios = properties;
		
		if ( scenarios != null && scenarios.size() > 0) {
			if ( !showAllCheck.isSelected()) {
				showAll( false);
			} else {
				showAllCheck.setSelected( false);
			}
			
			showAllCheck.setEnabled( true);
		} else {
			if ( showAllCheck.isSelected()) {
				showAll( true);
			} else {
				showAllCheck.setSelected( true);
			}
			
			showAllCheck.setEnabled( false);
		}
		
		if ( defaultScenario != null) {
			setSelectedScenario( defaultScenario);
		}
	
		super.show();
	}

	public void show() {
		if ( showAllCheck == null || showAllCheck.isSelected()) {
			showAll( true);
		} else {
			showAllCheck.setSelected( true);
		}
		
		if ( showAllCheck != null) {
			showAllCheck.setEnabled( false);
		}

		super.show();
	}

	/**
	 * Set the properties.
	 *
	 * @param properties the list of grammar properties.
	 */
	private void showAll( boolean enabled) {
		
		if ( enabled) {
			model = new ScenarioListModel( properties.getScenarioProperties());
		} else {
			model = new ScenarioListModel( scenarios);
		}

		list.setModel( model);
		
		list.setSelectedIndex( 0);
	}

	/**
	 * Set the properties.
	 *
	 * @param properties the list of grammar properties.
	 */
	public void setSelectedScenario( ScenarioProperties scenario) {
		for ( int i = 0; i < model.getSize(); i++) {
			ScenarioProperties prop = model.getScenario( i);
			
			if ( prop == scenario) {
				list.setSelectedIndex( i);
				return;
			}
		}
	}

	/**
	 * Returns the properties for the grammar type.
	 *
	 * @return the grammar type properties.
	 */
	public ScenarioProperties getSelectedScenario() {
		int index = list.getSelectedIndex();
		
		if ( index != -1 && model.getSize() > index) {
			return model.getScenario( index);
		} else {
			return model.getScenario( 0);
		}
	}
	
	/**
	 * Returns the properties for the grammar type.
	 *
	 * @return the grammar type properties.
	 */
	public Vector getSelectedScenarios() {
		int indices[] = list.getSelectedIndices();
		Vector scenarios = new Vector();
		
		for ( int i = 0; i < indices.length; i++) {
			if ( indices[i] < model.getSize()) {
				scenarios.addElement( model.getScenario( indices[i]));
			}
		}
		
		return scenarios;
	}

	class ScenarioListModel extends AbstractListModel {
		Vector elements = null;
		ScenarioProperties defaultScenario = null;
		
		public ScenarioListModel( Vector list) {
			elements = new Vector();

			for ( int i = 0; i < list.size(); i++) {
				ScenarioProperties element = (ScenarioProperties)list.elementAt(i);

				// Find out where to insert the element...
				int index = -1;

				for ( int j = 0; j < elements.size() && index == -1; j++) {
					// Compare alphabeticaly
					if ( element.getName().compareToIgnoreCase( ((ScenarioProperties)elements.elementAt(j)).getName()) <= 0) {
						index = j;
					}
				}
				
				if ( index != -1) {
					elements.insertElementAt( element, index);
				} else {
					elements.addElement( element);
				}
			}
		}
		
		public int getSize() {
			if ( elements != null) {
				return elements.size();
			}
			
			return 0;
		}

//		public void setDefault( ScenarioProperties scenario) {
//			defaultScenario = scenario; 
//		}
//
//		public boolean isDefault( ScenarioProperties scenario) {
//			boolean result = false;
//			
//			if ( defaultScenario != null && scenario != null) {
//				result = defaultScenario.getId().equals( scenario.getId()); 
//			}
//
//			return result; 
//		}

		public Object getElementAt( int i) {
			return ((ScenarioProperties)elements.elementAt( i)).getName();
		}

		public ScenarioProperties getScenario( int i) {
			return (ScenarioProperties)elements.elementAt( i);
		}
	}
} 
