/*
 * $Id: FindInProjectsDialog.java,v 1.2 2005/08/31 09:18:15 tcurley Exp $
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.bounce.FormLayout;

import com.cladonia.xngreditor.project.Project;
import com.cladonia.xngreditor.project.ProjectProperties;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The find dialog for the ExchangerEditor editor.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/08/31 09:18:15 $
 * @author Dogsbay
 */
public class FindInProjectsDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 350, 150);
	// The components that contain the values
	private JComboBox searchField		= null;

	private JCheckBox matchCaseButton			= null;
	private JCheckBox regularExpressionButton	= null;
	private JCheckBox matchWholeWordButton			= null;
	
	private ConfigurationProperties properties = null;
	private JComboBox projectsCombo;
	private ExchangerEditor parent;
	private Vector projects;

	/**
	 * The dialog that displays the properties for the document.
	 *
	 * @param frame the parent frame.
	 */
	public FindInProjectsDialog( ExchangerEditor parent, ConfigurationProperties props) {
		super( parent, true);
		this.parent = parent;
		properties = props;
		
		setResizable( false);
		setTitle( "Find in Projects");
		setDialogDescription( "Specify the Search criteria.");

		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));

		main.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelled = true;
				//setVisible(false);
				hide();
			}
		});
		main.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");

		JPanel searchPanel = new JPanel( new FormLayout( 10, 2));
		searchPanel.setBorder( new EmptyBorder( 0, 0, 5, 0));
		searchPanel.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
				searchField.requestFocusInWindow();
				searchField.getEditor().selectAll();
			}

			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});
	
		
		projectsCombo = new JComboBox();
		projectsCombo.setFont( projectsCombo.getFont().deriveFont( Font.PLAIN));
		projectsCombo.setPreferredSize( new Dimension( 100, 23));
		projectsCombo.setEditable(false);
		
		searchPanel.add( new JLabel("Project:"), FormLayout.LEFT);
		searchPanel.add( projectsCombo, FormLayout.RIGHT_FILL);
		
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

		
		searchPanel.add( new JLabel("Find:"), FormLayout.LEFT);
		searchPanel.add( searchField, FormLayout.RIGHT_FILL);

		matchCaseButton = new JCheckBox( "Case Sensitive");
		matchCaseButton.setMnemonic( 'C');
//		matchCaseButton.setFont( matchCaseButton.getFont().deriveFont( Font.PLAIN));

		matchWholeWordButton = new JCheckBox( "Whole Word");
		matchWholeWordButton.setMnemonic( 'W');
//		matchWholeWordButton.setFont( matchWholeWordButton.getFont().deriveFont( Font.PLAIN));

		regularExpressionButton = new JCheckBox( "Regular Expression");
		regularExpressionButton.setMnemonic( 'x');
//		regularExpressionButton.setFont( regularExpressionButton.getFont().deriveFont( Font.PLAIN));

		JPanel matchCasePanel = new JPanel( new FormLayout());
		matchCasePanel.setBorder( new TitledBorder( "Options"));
//		matchCasePanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
		matchCasePanel.add( matchCaseButton, FormLayout.FULL);
		matchCasePanel.add( matchWholeWordButton, FormLayout.FULL);
		matchCasePanel.add( regularExpressionButton, FormLayout.FULL);

		//removed for xngr-dialog
		super.okButton.setText("Find");
		super.okButton.setMnemonic('F');
/*
		cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		okButton = new JButton( "Find");
		okButton.setMnemonic( 'F');
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				findButtonPressed();
			}
		});
		getRootPane().setDefaultButton( okButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 10, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);
*/
		main.add( searchPanel, BorderLayout.NORTH);
		main.add( matchCasePanel, BorderLayout.CENTER);
		//main.add( buttonPanel, BorderLayout.SOUTH);

		setContentPane( main);
		
		/*addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});
*/
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);

		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));

		setLocationRelativeTo( parent);
	}
	
	
	/**
	 * Returns the search text... 
	 *
	 * @return the search text.
	 */
	public String getSearch() {
		String result = null;

		Object item = searchField.getEditor().getItem();

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
	
	public boolean isMatchWholeWord() {
		return matchWholeWordButton.isSelected();
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
	public void init(Project project) {
		if ( searchField.getItemCount() > 0) {
			searchField.removeAllItems();
		}
		
		Vector searches = properties.getSearches();
		
		for ( int i = 0; i < searches.size(); i++) {
			searchField.addItem( searches.elementAt(i));
		}

		if ( searchField.getItemCount() > 0) {
			searchField.setSelectedIndex( 0);
		}

		matchCaseButton.setSelected( properties.isMatchCase());
		matchWholeWordButton.setSelected( properties.isMatchWholeWord());
		regularExpressionButton.setSelected( properties.isRegularExpression());
		
		projectsCombo.removeAllItems();
		projects = properties.getProjectProperties();
		for(int cnt=0;cnt<projects.size();++cnt) {
			ProjectProperties projProp = (ProjectProperties) projects.get(cnt);
			projectsCombo.addItem(projProp.getName());
		}
		
		if(project != null) {
			if(project.getSelectedProject() != null) {
				projectsCombo.setSelectedItem(project.getSelectedProject().getName());
			}
		}
		
		searchField.requestFocusInWindow();
		searchField.getEditor().selectAll();
	}
	
	public String getProject() {
		String tempProject = (String) projectsCombo.getSelectedItem();
		if((tempProject != null) && (tempProject.length() > 0)) {
			
			for(int cnt=0;cnt<projects.size();++cnt) {
				ProjectProperties projProp = (ProjectProperties) projects.get(cnt);
				if(tempProject.equalsIgnoreCase(projProp.getName())) {
					
					return(tempProject);
				}
			}
			return(null);
		}
		else {
			return(null);
		}
	}
} 
