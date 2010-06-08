/*
 * $Id: ParameterManagementDialog.java,v 1.10 2004/10/29 15:01:31 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.scenario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.FormConstraints;
import org.bounce.event.DoubleClickListener;

import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.XngrDialogHeader;

/**
 * The parameter properties dialog.
 *
 * @version	$Revision: 1.10 $, $Date: 2004/10/29 15:01:31 $
 * @author Dogsbay
 */
public class ParameterManagementDialog extends XngrDialogHeader {
	private static final Dimension SIZE 					= new Dimension( 300, 250  + XngrDialogHeader.HEADER_HEIGHT);
	private static final FormConstraints LEFT_ALIGN_RIGHT	= new FormConstraints( FormConstraints.LEFT, FormConstraints.RIGHT);

	private JFrame parent = null;

	private ParameterProperties properties	= null;
	private ParameterPropertiesDialog parameterPropertiesDialog	= null;

	private JList list					= null;
	private ParameterListModel model		= null;
	private JButton addParameterButton		= null;
	private JButton deleteParameterButton	= null;
	private JButton changeParameterButton	= null;

	/**
	 * The dialog that displays the preferences for the editor.
	 *
	 * @param frame the parent frame.
	 */
	public ParameterManagementDialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "XSLT Parameters");
		setDialogDescription( "Edit, Add and Delete parameters.");
		
		JPanel parameterPanel = new JPanel( new BorderLayout());
		
		parameterPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Parameters"),
									new EmptyBorder( 0, 5, 5, 5)));

		model = new ParameterListModel();
		list = new JList( model);
		list.setCellRenderer( new ParameterCellRenderer());
		list.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				changeParameterButtonPressed();
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

		JScrollPane scrollPane = new JScrollPane(	list,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize( new Dimension( 100, 50));
		scrollPane.getViewport().setBackground( list.getBackground());
		parameterPanel.add( scrollPane, BorderLayout.CENTER);
		
		addParameterButton = new JButton( "Add");
		addParameterButton.setMnemonic('A');
		addParameterButton.setFont( addParameterButton.getFont().deriveFont( Font.PLAIN));
		addParameterButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				addParameterButtonPressed();
			}
		});
		
		deleteParameterButton = new JButton( "Delete");
		deleteParameterButton.setMnemonic('D');
		deleteParameterButton.setFont( deleteParameterButton.getFont().deriveFont( Font.PLAIN));
		deleteParameterButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				deleteParameterButtonPressed();
			}
		});

		changeParameterButton = new JButton( "Edit");
		changeParameterButton.setMnemonic('E');
		changeParameterButton.setFont( changeParameterButton.getFont().deriveFont( Font.PLAIN));
		changeParameterButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				changeParameterButtonPressed();
			}
		});

		JPanel paramButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		paramButtonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
		paramButtonPanel.add( changeParameterButton);
		paramButtonPanel.add( addParameterButton);
		paramButtonPanel.add( deleteParameterButton);
		
		parameterPanel.add( paramButtonPanel, BorderLayout.SOUTH);
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 2, 2, 5, 2));
		main.add( parameterPanel, BorderLayout.CENTER);
		
		JButton closeButton = new JButton( "Close");
		closeButton.setMnemonic('C');
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				setVisible(false);
			}
		});
	
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
		buttonPanel.add( closeButton);
		
		main.add( buttonPanel, BorderLayout.SOUTH);
		
		setContentPane( main);
		
		pack();
		setSize( SIZE);

		setLocationRelativeTo( parent);
	}
	
	private void addParameterButtonPressed() {
		ParameterPropertiesDialog dialog = getParameterPropertiesDialog();
		ParameterProperties properties = new ParameterProperties();
		
		Vector names = new Vector();
		Vector parameters = model.getParameters();
		
		for ( int i = 0; i < parameters.size(); i++) {
			names.addElement( ((ParameterProperties)parameters.elementAt( i)).getName());
		}

		dialog.show( properties, names);

		if ( !dialog.isCancelled()) {
			model.addParameter( properties);
		}
	}

	private void changeParameterButtonPressed() {
		int index = list.getSelectedIndex();

		if ( index != -1) {
			ParameterProperties properties = model.getParameter( index);
			ParameterPropertiesDialog dialog = getParameterPropertiesDialog();
			Vector names = new Vector();
			Vector parameters = model.getParameters();
			
			for ( int i = 0; i < parameters.size(); i++) {
				String name = ((ParameterProperties)parameters.elementAt( i)).getName();

				if ( !name.equalsIgnoreCase( properties.getName())) {
					names.addElement( name);
				}
			}
	
			dialog.show( properties, names);

			if ( !dialog.isCancelled()) {
				model.updateParameter( properties);
			}
		}
	}

	private void deleteParameterButtonPressed() {
		/*int index = list.getSelectedIndex();

		if ( index != -1) {
			ParameterProperties properties = model.getParameter( index);
			model.removeParameter( properties);
		}*/
		
//	  *******************START new code*********************************
	    
	    //get the selected objects
	    Object[] selectedObjects = list.getSelectedValues();
	    
	    //only continue with the confirm all if the array has 2 or more items
	    if(selectedObjects.length>0) {
	        
	        //create the variable that the user can set if they just want to delete all
	        boolean deleteAll = false;
	        
	        //loop through the selected objects
	        for(int cnt=0;cnt<selectedObjects.length;++cnt) {
	            
	            //if the deleteAll flag is false, then ask the user about each individual object
	            if( deleteAll == false) {
	            
		            //create the message for the user
		            String message = "Are you sure you want to delete:\n ";
		            message += ((ParameterProperties)selectedObjects[cnt]).getName();
		            
		            //ask the question
		            int questionResult = -1;
		            if(selectedObjects.length>1) {
                        questionResult = MessageHandler.showConfirmYesNoAll(parent,message);
                    }
                    else {
                        questionResult = MessageHandler.showConfirm(parent,message);
                    }
		            		            
		            //if the user answered All, don't do anything for now and delete them all later
		            if(questionResult==MessageHandler.CONFIRM_ALL_OPTION) {
		                model.removeParameter((ParameterProperties) selectedObjects[cnt]);
	                	deleteAll=true;
	                } 
	                //user choose to delete this object, remove it from the list
	                else if(questionResult==JOptionPane.YES_OPTION) {
	                    model.removeParameter((ParameterProperties) selectedObjects[cnt]);
					}
		            
	            } else {
                    model.removeParameter((ParameterProperties) selectedObjects[cnt]);
	            }
	        } //end for loop
	    } //end if(selectedObjects.length>1) {
	    
	        
	    //*******************END new code*********************************
	    		
	}
	
	public void setParameters( Vector parameters) {
		model.setParameters( parameters);
	}
	
	private ParameterPropertiesDialog getParameterPropertiesDialog() {
		if ( parameterPropertiesDialog == null) {
			parameterPropertiesDialog = new ParameterPropertiesDialog( parent);
		} 
		
		return parameterPropertiesDialog;
	}
	
	public Vector getParameters() {
		return model.getParameters();
	}
	
//	private class ParameterTableModel extends AbstractTableModel {
//		private Vector parameters = null;
//		private ScenarioProperties scenario = null;
//		
//		public ParameterTableModel() {
//			parameters = new Vector();
//		}
//		
//		public void setScenario( ScenarioProperties props) {
//			this.scenario = props;
//			
//			setParameters( props.getParameters());
//		}
//		
//		public Vector getParameters() {
//			return parameters;
//		}
//
//		private void setParameters( Vector parameters) {
//			parameters = new Vector();
//			for ( int i = 0; i < parameters.size(); i++) {
//				parameters.addElement( new ParameterProperties( (ParameterProperties)parameters.elementAt(i)));
//			}
//			
//			fireTableDataChanged();
//		}
//
//		public void addParameter( ParameterProperties props) {
//			parameters.addElement( props);
//			fireTableRowsInserted( parameters.size()-2, parameters.size()-1);
//		}
//
//		public void updateParameter( ParameterProperties props) {
//			int index = parameters.indexOf( props);
//
//			fireTableRowsUpdated( index, index);
//		}
//
//		public void removeParameter( ParameterProperties props) {
//			int index = parameters.indexOf( props);
//			parameters.removeElement( props);
//
//			fireTableRowsDeleted( index, index);
//		}
//
//		public int getRowCount() {
//			return parameters.size();
//		}
//		
//		public ParameterProperties getParameter( int row) {
//			ParameterProperties result = null;
//			
//			if ( parameters.size() >= row) {
//				result = (ParameterProperties)parameters.elementAt( row);
//			}
//
//			return result;
//		}
//
//		public int getRow( ParameterProperties props) {
//			return parameters.indexOf( props);
//		}
//
//		public String getColumnName( int column) {
//			String name = "";
//
//			if ( column == 0) {
//				name = "Name";
//			} else if ( column == 1) {
//				name = "Value";
//			}
//			
//			return name;
//		}
//
//		public Class getColumnClass( int column) {
//			return String.class;
//		}
//
//		public int getColumnCount() {
//			return 2;
//		}
//
//		public String getName( int row) {
//			return ((ParameterProperties)parameters.elementAt( row)).getName();
//		}
//
//		public Object getValueAt( int row, int column) {
//			Object result = null;
//			
//			if ( column == 0) {
//				result = ((ParameterProperties)parameters.elementAt( row)).getName();
//			} else if ( column == 1) {
//				result = ((ParameterProperties)parameters.elementAt( row)).getValue();
//			}
//			
//			return result;
//		}
//	}

	private class ParameterListModel extends AbstractListModel {
		Vector parameters = null;
		
		public Vector getParameters() {
			return parameters;
		}

		public void addParameter( ParameterProperties props) {
			parameters.addElement( props);

			fireIntervalAdded( this, parameters.size()-1, parameters.size()-1);
		}

		public void updateParameter( ParameterProperties props) {
			int index = parameters.indexOf( props);

			fireContentsChanged( this, index, index);
		}

		public void removeParameter( ParameterProperties props) {
			int index = parameters.indexOf( props);
			parameters.removeElement( props);

			fireIntervalRemoved( this, index, index);
		}

		public void setParameters( Vector params) {
			parameters = new Vector();

			for ( int i = 0; i < params.size(); i++) {
				parameters.addElement( new ParameterProperties( (ParameterProperties)params.elementAt(i)));
			}
			
			fireContentsChanged( this, 0, this.parameters.size()-1);
		}
		
		public ParameterProperties getParameter( int index) {
			ParameterProperties result = null;
			
			if ( parameters.size() >= index) {
				result = (ParameterProperties)parameters.elementAt( index);
			}

			return result;
		}

		public Object getElementAt( int index) {
			if ( parameters != null) {
				return parameters.elementAt( index);
			}
			
			return null;
		}
		
		public int getSize() {
			if ( parameters != null) {
				return parameters.size();
			}
			
			return 0;
		}
	}
} 
