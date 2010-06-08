/*
 * $Id: TagDialog.java,v 1.6 2004/11/04 19:21:49 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.FormLayout;
import org.bounce.event.DoubleClickListener;

import com.cladonia.schema.ElementInformation;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The tag dialog for the editor.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/11/04 19:21:49 $
 * @author Dogsbay
 */
public class TagDialog extends XngrDialog implements Editor.CurrentElementsListener {
	private static final Dimension SIZE = new Dimension( 350, 160);
	private static final ImageIcon ELEMENT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/ElementIcon.gif");

	private Editor editor = null;
	private Vector names = null;

	// The components that contain the values
	private JTextField tagNameField	= null;
	private JList tagNamesList		= null;

	/**
	 * The dialog that allows the user provide 
	 * the name for the tag.
	 *
	 * @param editor the XML editor.
	 */
	public TagDialog( JFrame parent) {
		super( parent, true);
		
		setResizable( false);
		setTitle( "Specify Tag");
		setDialogDescription( "Specify an Element Name");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 10, 5, 5, 5));

		main.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelButtonPressed();
			}
		});
		main.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");

		JPanel tagPanel = new JPanel( new FormLayout( 5, 0));
		tagPanel.setBorder( new EmptyBorder( 0, 5, 0, 5));
	
		tagPanel.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
//				System.out.println( "AncestorListener.ancestorAdded() ["+TagDialog.this.isVisible()+"]");
				tagNameField.requestFocus();
				tagNameField.requestFocusInWindow();
			}

			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});

		tagNameField = new JTextField();
		tagNameField.getDocument().addDocumentListener( new DocumentListener() {
			public void changedUpdate( DocumentEvent e) {
				updateList();
			}

			public void insertUpdate( DocumentEvent e) {
				updateList();
			}

			public void removeUpdate( DocumentEvent e) {
				updateList();
			}
		});

		tagNameField.addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					okButtonPressed();
				} else if ( e.getKeyCode() == KeyEvent.VK_DOWN) {
					if ( tagNamesList.getModel().getSize() > 0) {
						if ( tagNamesList.getSelectedIndex() == -1) {
							tagNamesList.setSelectedIndex( 0);
						}

						tagNamesList.requestFocusInWindow();
					}
				}
			}
		});
		
		tagNameField.setFont( tagNameField.getFont().deriveFont( Font.PLAIN));
		tagNameField.setPreferredSize( new Dimension( 100, 23));

		tagNamesList = new JList();
		tagNamesList.setVisibleRowCount( 5);
		tagNamesList.setCellRenderer( new ElementNameListCellRenderer());
		tagNamesList.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				int index = tagNamesList.getSelectedIndex();

				if ( index != -1) { // A row is selected...
					okButtonPressed();
				}
			}
		});
		
		tagNamesList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = tagNamesList.getSelectedIndex();
                if(selected>-1) {
                    tagNamesList.ensureIndexIsVisible(selected);
                }
            }
		    
		});


		tagNamesList.addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					okButtonPressed();
				} else if ( e.getKeyCode() == KeyEvent.VK_UP) {
					if ( tagNamesList.getModel().getSize() > 0) {
						if ( tagNamesList.getSelectedIndex() == 0) {
							tagNameField.requestFocusInWindow();
							tagNamesList.clearSelection();
						}
					}
				} 
			}
		});

		tagPanel.add( new JLabel("Name:"), FormLayout.LEFT);
		tagPanel.add( tagNameField, FormLayout.RIGHT_FILL);
		tagPanel.add( new JLabel(""), FormLayout.LEFT);
		tagPanel.add( new JScrollPane( tagNamesList), FormLayout.RIGHT_FILL);
		
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
		buttonPanel.setBorder( new EmptyBorder( 20, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);*/

		main.add( tagPanel, BorderLayout.NORTH);
		//main.add( buttonPanel, BorderLayout.SOUTH);

		setContentPane( main);
		
		/*addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelButtonPressed();
			}
		});*/

		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);

		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));

		setLocationRelativeTo( parent);
	}
	
	protected void okButtonPressed() {
		int index = tagNamesList.getSelectedIndex();
	
		if ( index != -1) { // A row is selected...
			tagNameField.setText( (String)tagNamesList.getSelectedValue());
		}

		super.okButtonPressed();
	}

	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}

	/**
	 * Returns the selected tag... 
	 *
	 * @return the selected tag.
	 */
	public String getTag() {
		return tagNameField.getText();
	}

	private void updateList() {
//		Vector unorderedNames = names;
		Vector orderedNames = new Vector();
		String prefix = tagNameField.getText();
		
		if ( prefix == null) {
			prefix = "";
		}
		
		
		if ( names != null) {
			for ( int i = 0; i < names.size(); i++) {
				String name = (String)names.elementAt(i);

				if ( name.startsWith( prefix)) {
					int index = -1;
						
					for ( int j = 0; j < orderedNames.size() && index == -1; j++) {
						// Compare alphabeticaly
						if ( name.compareToIgnoreCase( (String)orderedNames.elementAt(j)) <= 0) {
							index = j;
						}
					}
				
					if ( index != -1) {
						orderedNames.insertElementAt( name, index);
					} else {
						orderedNames.addElement( name);
					}
				}
			}
		}

		tagNamesList.setListData( orderedNames);
	}
	
	/**
	 * Initialises the values in the dialog.
	 */
	public void init( Editor editor) {
		this.editor = editor;

		tagNameField.setFont( TextPreferences.getBaseFont().deriveFont( Font.PLAIN));
		tagNamesList.setFont( TextPreferences.getBaseFont().deriveFont( Font.PLAIN));

		tagNameField.setCaretPosition( 0);
		tagNameField.setText( "");

		updateNames();
	}
	
	private void updateNames() {
		if ( editor.getAllElements() == null || editor.getAllElements().size() == 0) {
			Vector unorderedNames = editor.getElementNames();
			Vector orderedNames = new Vector();
			
			if ( unorderedNames != null) {
				for ( int i = 0; i < unorderedNames.size(); i++) {
					String name = (String)unorderedNames.elementAt(i);
					int index = -1;
						
					for ( int j = 0; j < orderedNames.size() && index == -1; j++) {
						// Compare alphabeticaly
						if ( name.compareToIgnoreCase( (String)orderedNames.elementAt(j)) <= 0) {
							index = j;
						}
					}
				
					if ( index != -1) {
						orderedNames.insertElementAt( name, index);
					} else {
						orderedNames.addElement( name);
					}
				}
			}
	
			setNames( orderedNames);
		} else {
			editor.getCurrentElements( this);
		}
	}
	
	public void setCurrentElements( Vector lists) {
		Vector names = new Vector();
		
		if ( lists != null) {
			for ( int i = 0; i < lists.size(); i++) {
				Vector list = orderElements( (Vector)lists.elementAt(i));
				
				for ( int j = 0; j < list.size(); j++) {
					String name = ((ElementInformation)list.elementAt(j)).getQualifiedName();

					if ( !names.contains( name)) {
						names.addElement( name);
					}
				}
			}
		}
		
		setNames( names);
	}
	
	private void setNames( final Vector names) {
		this.names = names;
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				tagNamesList.setListData( names);
			}
		});
	}
	
	private Vector orderElements( Vector list) {
		Vector elements = new Vector();
		
		if ( list != null) {
			for ( int i = 0; i < list.size(); i++) {
				ElementInformation element = (ElementInformation)list.elementAt(i);
				String name = element.getQualifiedName();
				int index = -1;
				boolean add = true;

				for ( int j = 0; j < elements.size() && index == -1; j++) {
					// Compare alphabeticaly
					int comp = name.compareToIgnoreCase( ((ElementInformation)elements.elementAt(j)).getQualifiedName());

					if ( comp < 0) {
						index = j;
						break;
					} else if ( comp == 0) {
						add = false;
						break;
					}
				}
			
				if ( add) {
					if ( index != -1) {
						elements.insertElementAt( element, index);
					} else {
						elements.addElement( element);
					}
				}
			}
		}
		
		return elements;
	}

	class ElementNameListCellRenderer extends JPanel implements ListCellRenderer {
		protected EmptyBorder noFocusBorder;

		private JLabel icon = null;
		private JLabel prefix = null;
		private JLabel colon = null;
		private JLabel name = null;

		public ElementNameListCellRenderer() {
			super( new FlowLayout( FlowLayout.LEFT, 0, 0));
			
			if (noFocusBorder == null) {
			    noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			}
			
			setBorder( noFocusBorder);

			icon = new JLabel();
			icon.setIcon( ELEMENT_ICON);
	
			prefix = new JLabel();
			prefix.setFont( prefix.getFont().deriveFont( Font.PLAIN + Font.ITALIC));
	
			colon = new JLabel(":");
			colon.setFont( colon.getFont().deriveFont( Font.PLAIN));
			icon.setPreferredSize( new Dimension( 16, colon.getPreferredSize().height));
	
			name = new JLabel();
			name.setFont( name.getFont().deriveFont( Font.BOLD));
			
			add( icon);
			add( prefix);
			add( colon);
			add( name);
		}
		
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean selected, boolean focus) {
		
			setForeground( Color.black);

			setPrefix( value.toString());
			setText( value.toString());

			if (selected) {
				setBackground(list.getSelectionBackground());
			} else {
				setBackground(list.getBackground());
			}
		
			setEnabled( list.isEnabled());
			setPreferredFont( list.getFont());
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

			return this;
		}
		
		public void setForeground( Color color) {
		
			if (icon != null) {
				icon.setForeground( color);
				prefix.setForeground( color);
				colon.setForeground( color);
				name.setForeground( color);
				super.setForeground( color);
			}
		}

		public void setBackground( Color color) {
			if (icon != null) {
				icon.setBackground( color);
				prefix.setBackground( color);
				colon.setBackground( color);
				name.setBackground( color);
				super.setBackground( color);
			}
		}

		public void setPreferredFont( Font font) {
			prefix.setFont( font.deriveFont( Font.PLAIN + Font.ITALIC));
			colon.setFont( font.deriveFont( Font.PLAIN));
			name.setFont( font.deriveFont( Font.BOLD));
			icon.setPreferredSize( new Dimension( 16, colon.getPreferredSize().height));
		}
		
		private void setPrefix( String qname) {
			int index = qname.indexOf( ':');
			String value = null;
			
			if ( index != -1) {
				value = qname.substring( 0, index);
			}

			if ( value != null && value.trim().length() > 0) {
				prefix.setText( value);
				prefix.setVisible( true);
				colon.setVisible( true);
			} else {
				prefix.setVisible( false);
				colon.setVisible( false);
			}
		}

		private void setText( String qname) {
			String value = null;
			int index = qname.indexOf( ':');
			
			if ( index != -1) {
				value = qname.substring( index+1, qname.length());
			} else {
				value = qname;
			}

			name.setText( value);
		}
	}
} 
