/*
 * $Id: EntitySelectionDialog.java,v 1.4 2004/10/27 13:26:16 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.bounce.FormLayout;

import com.cladonia.xml.CommonEntities;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The entity selection dialog.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/10/27 13:26:16 $
 * @author Dogsbay
 */
public class EntitySelectionDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 400, 350);

	// The components that contain the values
	private JTable entityTable					= null;

	private JLabel magnifiedCharacterLabel		= null;

	private JRadioButton entityTextButton		= null;
	private JRadioButton entityNumericButton	= null;
	private JRadioButton characterButton		= null;
	private JLabel entityTextLabel 				= null;
	private JLabel entityNumericLabel 			= null;
	private JLabel characterLabel 				= null;

	/**
	 * The dialog that displays the possible entities.
	 *
	 * @param frame the parent frame.
	 */
	public EntitySelectionDialog( JFrame parent) {
		super( parent, true);
		
		setResizable( false);
		setTitle( "Insert Special Character");
		setDialogDescription( "Select the Character to insert.");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 10, 5, 5, 5));

		main.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelButtonPressed();
			}
		});
		main.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment( SwingConstants.CENTER);
		renderer.setVerticalAlignment( SwingConstants.CENTER);
		
		entityTable = new JTable( new EntityTableModel());
		entityTable.setDefaultRenderer( String.class, renderer);
		entityTable.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS);
		entityTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
		entityTable.setCellSelectionEnabled( true);
		entityTable.setRowHeight( 22);
		entityTable.getColumnModel().getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent e) {
				if ( !e.getValueIsAdjusting()) {
					int row = entityTable.getSelectedRow();
					int col = entityTable.getSelectedColumn();

					selectionChanged( row, col);
				}
			}
		});
		
		entityTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent e) {
				if ( !e.getValueIsAdjusting()) {
					int row = entityTable.getSelectedRow();
					int col = entityTable.getSelectedColumn();

					selectionChanged( row, col);
				}
			}
		});
		
		entityTable.getActionMap().put( "enterAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				okButtonPressed();
			}
		});
		entityTable.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false), "enterAction");

		entityTable.addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					cancelButtonPressed();
				}
			}
		});
		
		entityTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
			    if(event.getClickCount()==2) {
			        //double clicked
			        okButtonPressed();
			    }				
			}
		});

		magnifiedCharacterLabel	= new JLabel();
		magnifiedCharacterLabel.setHorizontalAlignment( SwingConstants.CENTER);
		magnifiedCharacterLabel.setBorder( new LineBorder( UIManager.getColor( "controlDkShadow"), 1));
		magnifiedCharacterLabel.setFont( magnifiedCharacterLabel.getFont().deriveFont( (float)36));
	
		entityTextButton 	= new JRadioButton( "Entity Name");
		entityNumericButton = new JRadioButton( "Character Entity");
		characterButton 	= new JRadioButton( "Character");

		entityTextLabel 	= new JLabel();
		entityNumericLabel 	= new JLabel();
		characterLabel 	= new JLabel();
		characterLabel.setFont( characterLabel.getFont().deriveFont( Font.PLAIN, (float)12));
		entityTextLabel.setFont( entityTextLabel.getFont().deriveFont( Font.PLAIN, (float)12));
		entityNumericLabel.setFont( entityNumericLabel.getFont().deriveFont( Font.PLAIN, (float)12));

	
		ButtonGroup group = new ButtonGroup();
		group.add( entityTextButton);
		group.add( entityNumericButton);
		group.add( characterButton);
		
		entityTextButton.setSelected( true);

//		gotoPanel.add( new JLabel("Line:"), FormLayout.LEFT);
//		gotoPanel.add( gotoField, FormLayout.RIGHT_FILL);

		//removed for xngr-dialog
		super.okButton.setText("Insert");
		/*cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		okButton = new JButton( "Insert");
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				okButtonPressed();
			}
		});

		getRootPane().setDefaultButton( okButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);*/

		JPanel radioButtonPanel = new JPanel( new FormLayout( 5, 0));
		radioButtonPanel.add( entityTextButton, FormLayout.LEFT);
		radioButtonPanel.add( entityTextLabel, FormLayout.RIGHT);
		radioButtonPanel.add( entityNumericButton, FormLayout.LEFT);
		radioButtonPanel.add( entityNumericLabel, FormLayout.RIGHT);
		radioButtonPanel.add( characterButton, FormLayout.LEFT);
		radioButtonPanel.add( characterLabel, FormLayout.RIGHT);
		
		JPanel southPanel = new JPanel( new BorderLayout());
		southPanel.setBorder( new EmptyBorder( 10, 5, 5, 5));
		southPanel.add( radioButtonPanel, BorderLayout.CENTER);
		southPanel.add( magnifiedCharacterLabel, BorderLayout.EAST);
		magnifiedCharacterLabel.setPreferredSize( new Dimension( radioButtonPanel.getPreferredSize().height, radioButtonPanel.getPreferredSize().height));

		JPanel entityPanel = new JPanel( new BorderLayout());
		entityPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Select Character"),
									new EmptyBorder( 0, 5, 5, 5)));

		entityPanel.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
				entityTable.requestFocusInWindow();
			}

			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});

		entityPanel.add( new JScrollPane( entityTable), BorderLayout.CENTER);
		entityPanel.add( southPanel, BorderLayout.SOUTH);
		
		main.add( entityPanel, BorderLayout.CENTER);
		//main.add( buttonPanel, BorderLayout.SOUTH);

		setContentPane( main);
		
		/*addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelButtonPressed();
			}
		});
*/
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);

		setSize( SIZE);

		setLocationRelativeTo( parent);
		
//		updatePreferences();
	}
	
	private void selectionChanged( int row, int col) {
		if ( row != -1 && col != -1) {
			String val = (String)entityTable.getModel().getValueAt( row, col);
			
			if ( val.length() > 0) {
				char character = val.charAt(0);
				String name = "&"+CommonEntities.getEntityName( character)+";";
				String numeric = "&#"+(int)character+";";

				entityTextLabel.setText( "( "+name+" )");
				entityNumericLabel.setText( "( "+numeric+" )");
				characterLabel.setText( "( "+character+" )");
				magnifiedCharacterLabel.setText( ""+character);
				return;
			}
		} 
		
		entityTextLabel.setText( "");
		entityNumericLabel.setText( "");
		characterLabel.setText( "");
		magnifiedCharacterLabel.setText( "");
	}
	
	public void updatePreferences() {
		Font base = TextPreferences.getBaseFont();
		
		entityTable.setFont( base.deriveFont( Font.PLAIN, (float)12));
		characterLabel.setFont( base.deriveFont( Font.PLAIN, (float)12));
		entityTextLabel.setFont( base.deriveFont( Font.PLAIN, (float)12));
		entityNumericLabel.setFont( base.deriveFont( Font.PLAIN, (float)12));
		magnifiedCharacterLabel.setFont( base.deriveFont( (float)36));
	}
	
	public void show() {
		updatePreferences();

		//super.setVisible(true);
		super.show();
	}
	
	public String getSelectedValue() {
		String text = null;
		
		int row = entityTable.getSelectedRow();
		int col = entityTable.getSelectedColumn();

		if ( row != -1 && col != -1) {
			String val = (String)entityTable.getModel().getValueAt( row, col);
			
			if ( val.length() > 0) {
				char character = val.charAt(0);
				String name = "&"+CommonEntities.getEntityName( character)+";";
				String numeric = "&#"+(int)character+";";
				
				if ( entityTextButton.isSelected()) {
					text = name;
				} else if ( entityNumericButton.isSelected()) {
					text = numeric;
				} else {
					text = ""+character;
				}
			}
		}
		
		return text;
	}
	
	protected void okButtonPressed() {
		super.okButtonPressed();
		
		//System.out.println( getSelectedValue());
		
//		String gotoText = gotoField.getText();
//		int line = -1;
//		
//		try { 
//			line = Integer.parseInt( gotoText);
//			
//			if ( line > 0) {
//				editor.gotoLine( line);
//				hide();
//				editor.setFocus();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
//		editor.setFocus();
	}

	private class EntityTableModel extends AbstractTableModel {
		Vector characters = null;
		
		public EntityTableModel() {
			characters = CommonEntities.getOrderedCharacters();
		}
		
		public Class getColumnClass( int col) {
			return String.class;
		}
		
		public String getColumnName( int col) {
			return null;
		}
		
		public int getColumnCount() {
			return 16;
		}

		public int getRowCount() {
			return (characters.size() / getColumnCount()) + 1;
		}
		
		public Object getValueAt( int row, int col) {
			int index = (row * getColumnCount()) + col;
			
			if ( index >= characters.size()) {
				return "";
			} else {
				return characters.elementAt( index).toString();
			}
		}
	} 
	
	public static void main( String[] args) {
		JDialog dialog = new EntitySelectionDialog( null);
		//dialog.setVisible(true);
		dialog.show();
	}
} 
