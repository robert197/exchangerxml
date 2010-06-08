/*
 * $Id: PrefixNamespaceMappingPanel.java,v 1.16 2004/11/04 11:15:05 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.FormConstraints;
import org.bounce.FormLayout;
import org.bounce.event.DoubleClickListener;

import com.cladonia.xml.XDocumentFactory;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.FontType;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * This PrefixNamespaceMappingPanel is used to ...
 *
 * @version $Revision: 1.16 $, $Date: 2004/11/04 11:15:05 $
 * @author Dogsbay
 */
public class PrefixNamespaceMappingPanel extends JPanel {
	private JFrame parent = null;
	private ConfigurationProperties properties = null;
	
	private MappingsDialog mappingsDialog = null;

	private JList prefixNamespaceMappingList				= null;
	private DefaultListModel prefixNamespaceMappingsModel	= null;
//	private Vector removedPrefixNamespaceMappings			= null;
//	private Vector addedPrefixNamespaceMappings				= null;
	
	public PrefixNamespaceMappingPanel( JFrame parent, ConfigurationProperties properties, int visibleRows) {
		super( new BorderLayout());
		
		this.parent = parent;
		this.properties = properties;
	
		JPanel mappingListPanel = new JPanel( new BorderLayout());
	
		prefixNamespaceMappingList = new JList();
		prefixNamespaceMappingList.setCellRenderer( new MappingCellRenderer());
		prefixNamespaceMappingList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		prefixNamespaceMappingList.setVisibleRowCount( visibleRows);
		prefixNamespaceMappingsModel = new DefaultListModel();
		prefixNamespaceMappingList.setModel( prefixNamespaceMappingsModel);
		prefixNamespaceMappingList.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				editButtonPressed();
			}
		});
		prefixNamespaceMappingList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = prefixNamespaceMappingList.getSelectedIndex();
                if(selected>-1) {
                    prefixNamespaceMappingList.ensureIndexIsVisible(selected);
                }
            }
		    
		});
	
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 2, 0, 0, 0));
	
		JButton addButton = new JButton("Add");
		addButton.setFont( addButton.getFont().deriveFont( Font.PLAIN));
		addButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				addButtonPressed();
			}
		});
		buttonPanel.add( addButton);
	
		JButton editButton = new JButton("Edit");
		editButton.setFont( editButton.getFont().deriveFont( Font.PLAIN));
		editButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				editButtonPressed();
			}
		});
		buttonPanel.add( editButton);

		JButton deleteButton = new JButton( "Delete");
		deleteButton.setFont( deleteButton.getFont().deriveFont( Font.PLAIN));
		deleteButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				deleteButtonPressed();
			}
		});
		buttonPanel.add( deleteButton);
	
		JScrollPane scroller = new JScrollPane( prefixNamespaceMappingList);
		scroller.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mappingListPanel.add( scroller, BorderLayout.CENTER);
		mappingListPanel.add( buttonPanel, BorderLayout.SOUTH);
	
		this.add( mappingListPanel, BorderLayout.CENTER);
	}
	
	private MappingsDialog getMappingsDialog() {
		if ( mappingsDialog == null) {
			mappingsDialog = new MappingsDialog( parent);
		}

		return mappingsDialog;
	}
	
	private void deleteButtonPressed() {
		/*int index = prefixNamespaceMappingList.getSelectedIndex();
		
		if ( index != -1) {
			prefixNamespaceMappingsModel.removeElementAt( index);

		}*/
	    
	    //*******************START new code*********************************
	    
	    //get the selected objects
	    Object[] selectedObjects = prefixNamespaceMappingList.getSelectedValues();
	    
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
		            message += "xmlns:"+getPrefix( (String)selectedObjects[cnt]);
		            message += "=\""+getURI( (String)selectedObjects[cnt])+"\"";
		            
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
	                    prefixNamespaceMappingsModel.removeElement( selectedObjects[cnt]);
	                	deleteAll=true;
	                } 
	                //user choose to delete this object, remove it from the list
	                else if(questionResult==JOptionPane.YES_OPTION) {
	                    prefixNamespaceMappingsModel.removeElement( selectedObjects[cnt]);
					}
		            
	            } else {
                    prefixNamespaceMappingsModel.removeElement( selectedObjects[cnt]);
	            }
	        } //end for loop
	    } //end if(selectedObjects.length>1) {
	    
	    	    
	    
	    //*******************END new code*********************************
	}
	
	
	private Vector getPropertiesPrefixes() {
		Vector result = new Vector();
		Map mappings = properties.getPrefixNamespaceMappings();
		Iterator keys = mappings.keySet().iterator();
		
		while ( keys.hasNext()) {
			result.addElement( keys.next());
		}
		
		return result;
	}
	
	private Vector getModelPrefixes() {
		Vector result = new Vector();
		
		for ( int i = 0; i < prefixNamespaceMappingsModel.getSize(); i++) {
			result.addElement( getPrefix( (String)prefixNamespaceMappingsModel.elementAt(i)));
		}
		
		return result;
	}

	private void addButtonPressed() {
		MappingsDialog dialog = getMappingsDialog();
		dialog.show( getModelPrefixes());
		
		if ( !dialog.isCancelled()) {
			prefixNamespaceMappingsModel.addElement( dialog.getPrefix()+":"+dialog.getURI());

		}
	}

	private void editButtonPressed() {
		int index = prefixNamespaceMappingList.getSelectedIndex();
		
		if ( index != -1) {
			String mapping = (String)prefixNamespaceMappingsModel.elementAt( index);
			String prevPrefix = getPrefix( mapping);
			String prevURI = getURI( mapping);
	
			MappingsDialog dialog = getMappingsDialog();
			dialog.show( getModelPrefixes(), prevPrefix, prevURI);
			
			if ( !dialog.isCancelled()) {
				prefixNamespaceMappingsModel.setElementAt( dialog.getPrefix()+":"+dialog.getURI(), index);

			}
		}
	}

	public void init() {
		prefixNamespaceMappingsModel.removeAllElements();
		
		Vector prefixes = getPropertiesPrefixes();
		Map mappings = properties.getPrefixNamespaceMappings();
		Vector orderedPrefixes = new Vector();

		for ( int i = 0; i < prefixes.size(); i++) {
			String prefix = (String)prefixes.elementAt(i);
			int index = -1;

			for ( int j = 0; j < orderedPrefixes.size(); j++) {
				int comp = prefix.compareToIgnoreCase( (String)orderedPrefixes.elementAt(j));
	
				// Compare alphabeticaly
				if ( comp < 0) {
					index = j;
					break;
				}
			}
		
			if ( index != -1) {
				orderedPrefixes.insertElementAt( prefix, index);
			} else {
				orderedPrefixes.addElement( prefix);
			}
		}
		
		for ( int i = 0; i < orderedPrefixes.size(); i++) {
			String prefix = (String)orderedPrefixes.elementAt(i);
			prefixNamespaceMappingsModel.addElement( prefix+":"+mappings.get( prefix));
		}
	}
	
	public void save() {
		// remove previous mappings...
		Vector prefixes = getPropertiesPrefixes();
		Map mappings = properties.getPrefixNamespaceMappings();
		
		for ( int i = 0; i < prefixes.size(); i++) {
			String prefix = (String)prefixes.elementAt(i);
			properties.removePrefixNamespaceMapping( prefix, (String)mappings.get( prefix));
		}

		// add current mappings...
		for ( int i = 0; i < prefixNamespaceMappingsModel.getSize(); i++) {
			String mapping = (String)prefixNamespaceMappingsModel.elementAt( i);
			String prefix = getPrefix( mapping);
			String uri = getURI( mapping);
			properties.addPrefixNamespaceMapping( prefix, uri);
		}
		
		XDocumentFactory.getInstance().setXPathNamespaceURIs( properties.getPrefixNamespaceMappings());
	}
	
	private String getPrefix( String mapping) {
		int index = mapping.indexOf( ':');
		
		return mapping.substring( 0, index);
	}
	
	private String getURI( String mapping) {
		int index = mapping.indexOf( ':');
		
		return mapping.substring( index+1, mapping.length());
	}

	private static final Dimension MAPPINGS_DIALOG_SIZE 	= new Dimension( 250, 120);
	private static final FormConstraints LEFT_ALIGN_RIGHT	= new FormConstraints( FormConstraints.LEFT, FormConstraints.RIGHT);

	private class MappingsDialog extends XngrDialog {
		private Vector prefixes		= null;
		private String prefix		= null;

		private boolean cancelled	= false;
		
		private JButton cancelButton	= null;
		private JButton okButton		= null;
		
		// The components that contain the values
		private JTextField prefixField		= null;
		private JTextField uriField		= null;

		/**
		 * The dialog that displays the preferences for the editor.
		 *
		 * @param frame the parent frame.
		 */
		public MappingsDialog( JFrame parent) {
			super( parent, true);
			
			setResizable( false);
			setTitle( "Prefix Namespace Mapping");
			setDialogDescription( "Specify a Namespace URI and Prefix");
			
			JPanel main = new JPanel( new BorderLayout());
			main.setBorder( new EmptyBorder( 5, 5, 5, 5));
			
			// GENERAL
			JPanel generalPanel = new JPanel( new FormLayout( 10, 2));
			generalPanel.setBorder( new EmptyBorder( 5, 5, 15, 5));
		
			// name
			prefixField = new JTextField();
			prefixField.addAncestorListener( new AncestorListener() {
				public void ancestorAdded( AncestorEvent e) {
				    prefixField.requestFocusInWindow();
				}

				public void ancestorMoved( AncestorEvent e) {}
				public void ancestorRemoved( AncestorEvent e) {}
			});

			JLabel nameLabel = new JLabel("Prefix:");
			generalPanel.add( nameLabel, FormLayout.LEFT);
			generalPanel.add( prefixField, FormLayout.RIGHT_FILL);

			// name
			uriField = new JTextField();

			JLabel valueLabel = new JLabel("URI:");
			generalPanel.add( valueLabel, FormLayout.LEFT);
			generalPanel.add( uriField, FormLayout.RIGHT_FILL);

			/*cancelButton = new JButton( "Cancel");
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
			buttonPanel.add( cancelButton);
*/
			main.add( generalPanel, BorderLayout.CENTER);
			/*main.add( buttonPanel, BorderLayout.SOUTH);

			addWindowListener( new WindowAdapter() {
				public void windowClosing( WindowEvent e) {
					cancelled = true;
					hide();
				}
			});
*/
			setContentPane( main);
			
			setDefaultCloseOperation( HIDE_ON_CLOSE);
			
			pack();
			setSize( new Dimension( MAPPINGS_DIALOG_SIZE.width, getSize().height));

			setLocationRelativeTo( parent);
		}
		
		protected void okButtonPressed() {
			String prefix = prefixField.getText();
			String uri = uriField.getText();
			
			if ( checkPrefix( prefix) && checkURI( uri)) {
				super.okButtonPressed();
			}
		}

		public void show( Vector prefixes, String prefix, String uri) {
			this.prefixes = prefixes;
			this.prefix = prefix;
			
			setText( prefixField, prefix);
			setText( uriField, uri);
			
			super.show();
		}

		public void show( Vector prefixes) {
			show( prefixes, "", "");
		}

		protected void cancelButtonPressed() {
			super.cancelButtonPressed();
		}
		
		public String getPrefix() {
			return prefixField.getText();
		}

		public String getURI() {
			return uriField.getText();
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

		private boolean checkPrefix( String prefix) {
			if ( isEmpty( prefix)) {
				MessageHandler.showMessage( "Please specify a Prefix for this Mapping.");
				return false;
			}
			
			for ( int i = 0; i < prefixes.size(); i++) {
				if ( ((String)prefixes.elementAt(i)).equals( prefix) && !prefix.equals( this.prefix)) {
					MessageHandler.showMessage( "Prefix \""+prefix+"\" exists already.\n"+
												"Please specify a different prefix.");
					return false;
				}
			}
			
			return true;
		}

		private boolean checkURI( String uri) {
			if ( isEmpty( uri)) {
				MessageHandler.showMessage( "Please specify a URI for this Mapping.");
				return false;
			}
			
			return true;
		}
	} 
	
	private static final EmptyBorder noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public class MappingCellRenderer extends JPanel implements ListCellRenderer {
		private JLabel colon = null;
		private JLabel equals = null;
		private JLabel xmlns = null;
		private JLabel prefix = null;
		private JLabel uri = null;
		private JLabel startApos = null;
		private JLabel endApos = null;

		private boolean selected = false;
		private boolean showValue = false;
		
		/**
		 * The constructor for the renderer, sets the font type etc...
		 */
		public MappingCellRenderer() {
			super( new FlowLayout( FlowLayout.LEFT, 0, 0));
			
//			icon = new JLabel();

			xmlns = new JLabel();
			xmlns.setBorder( new EmptyBorder( 0, 0, 0, 0));
			xmlns.setOpaque( false);
			xmlns.setFont( xmlns.getFont().deriveFont( Font.BOLD));
			xmlns.setForeground( Color.black);
			xmlns.setText( "xmlns");
			
			colon = new JLabel();
			colon.setBorder( new EmptyBorder( 0, 0, 0, 0));
			colon.setOpaque( false);
			colon.setFont( colon.getFont().deriveFont( Font.PLAIN));
			colon.setText( ":");

			prefix = new JLabel();
			prefix.setBorder( new EmptyBorder( 0, 0, 0, 0));
			prefix.setOpaque( false);
			prefix.setFont( prefix.getFont().deriveFont( Font.BOLD));
			prefix.setForeground( Color.black);

			equals = new JLabel();
			equals.setBorder( new EmptyBorder( 0, 0, 0, 0));
			equals.setOpaque( false);
			equals.setFont( equals.getFont().deriveFont( Font.BOLD));
			equals.setForeground( Color.black);
			equals.setText( "=");

			startApos = new JLabel();
			startApos.setBorder( new EmptyBorder( 0, 0, 0, 0));
			startApos.setOpaque( false);
			startApos.setFont( startApos.getFont().deriveFont( Font.PLAIN));
			startApos.setText( "\"");

			uri = new JLabel();
			uri.setBorder( new EmptyBorder( 0, 0, 0, 0));
			uri.setOpaque( false);
			uri.setFont( uri.getFont().deriveFont( Font.PLAIN));

			endApos = new JLabel();
			endApos.setBorder( new EmptyBorder( 0, 0, 0, 0));
			endApos.setOpaque( false);
			endApos.setFont( endApos.getFont().deriveFont( Font.PLAIN));
			endApos.setText( "\"");

			//			this.add( icon);
			this.add( xmlns);
			this.add( colon);
			this.add( prefix);
			this.add( equals);
			this.add( startApos);
			this.add( uri);
			this.add( endApos);
			
			updatePreferences();
		}
		
		public void updatePreferences() {
			FontType type = TextPreferences.getFontType( TextPreferences.NAMESPACE_PREFIX);
			prefix.setFont( type.getFont());
			prefix.setForeground( type.getColor());

			type = TextPreferences.getFontType( TextPreferences.SPECIAL);
			
			colon.setFont( type.getFont());
			colon.setForeground( type.getColor());
			
			startApos.setFont( type.getFont());
			startApos.setForeground( type.getColor());

			endApos.setFont( type.getFont());
			endApos.setForeground( type.getColor());
			
			equals.setFont( type.getFont());
			equals.setForeground( type.getColor());

			type = TextPreferences.getFontType( TextPreferences.NAMESPACE_NAME);
			xmlns.setFont( type.getFont());
			xmlns.setForeground( type.getColor());

			type = TextPreferences.getFontType( TextPreferences.NAMESPACE_VALUE);
			uri.setFont( type.getFont());
			uri.setForeground( type.getColor());
		}

		public void setForegroundColor( Color foreground) {
			prefix.setForeground( foreground);
			colon.setForeground( foreground);
			startApos.setForeground( foreground);
			endApos.setForeground( foreground);
			equals.setForeground( foreground);
			xmlns.setForeground( foreground);
			uri.setForeground( foreground);
		}

		public Component getListCellRendererComponent( JList list, Object node, int index, boolean selected, boolean focus) {
			if ( node instanceof String) {
				setPrefix( getPrefix( (String)node));
				uri.setText( getURI( (String)node));

				this.setToolTipText( "xmlns:"+getPrefix( (String)node)+"=\""+getURI( (String)node)+"\"");
			} else {
				prefix.setText( "");
				uri.setText( "");
			}

			if ( selected) {
				setForegroundColor( list.getSelectionForeground());
				setBackground( list.getSelectionBackground());
			} else {
				updatePreferences();
				setBackground( list.getBackground());
			}

			setEnabled( list.isEnabled());
			updatePreferences();
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

			return this;
		}
		
		private void setPrefix( String text) {
			if ( text != null && text.length() > 0) {
				colon.setVisible( true);
				prefix.setVisible( true);
				prefix.setText( text);
			} else {
				prefix.setVisible( false);
				colon.setVisible( false);
			}
		}
	} 
}
