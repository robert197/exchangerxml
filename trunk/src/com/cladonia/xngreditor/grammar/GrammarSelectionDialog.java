/*
 * $Id: GrammarSelectionDialog.java,v 1.3 2004/10/23 15:14:04 edankert Exp $
 *
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.mozilla.org/MPL/ 
 *
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is eXchaNGeR browser code. (org.xngr.browser.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd.. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */
package com.cladonia.xngreditor.grammar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.cladonia.xngreditor.IconFactory;
import com.cladonia.xngreditor.XngrDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The grammar properties selection dialog.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/10/23 15:14:04 $
 * @author Dogsbay
 */
public class GrammarSelectionDialog extends XngrDialog {
	private static final boolean DEBUG = false;
	private static final Dimension SIZE = new Dimension( 300, 300);

	private ConfigurationProperties properties = null;

	private JList list	= null;
	//private QLabel text = null;

	private GrammarListModel model = null;

	/**
	 * The dialog that displays the list of grammar properties.
	 *
	 * @param frame the parent frame.
	 * @param props the configuration properties.
	 */
	public GrammarSelectionDialog( JFrame parent, ConfigurationProperties props) {
		super( parent, true);
		
		this.properties = props;
		
		setResizable( false);
		
		setTitle( "Select XML Type", "XML Types");
		setDialogDescription( "Select a Type from the list:");
		
		list = new JList();
		list.setCellRenderer( new GrammarListCellRenderer());
		list.addMouseListener( new MouseAdapter() { 
			public void mouseReleased( MouseEvent e) {
				int index = list.getSelectedIndex();

				if ( index != -1) { // A row is selected...
					if ( e.getClickCount() == 2) {
						cancelled = false;
						setVisible(false);
					}
				}
			}
		});
		
		//removed for xngr-dialog

		/*text = new QLabel();
		text.setLines( 3);
		text.setText( "Select a XML Type for this Document from the list of types:");
		text.setHorizontalAlignment( QLabel.LEFT);
		
		okButton = new JButton("OK");
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
				*/
		getRootPane().setDefaultButton( okButton);
		
		JPanel mainPanel = new JPanel( new BorderLayout());
		//mainPanel.add( text, BorderLayout.NORTH);
		JScrollPane scrollPane = new JScrollPane(	list,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		mainPanel.add( scrollPane, BorderLayout.CENTER);
		mainPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		//mainPanel.add( buttonPanel, BorderLayout.SOUTH);

		this.setContentPane( mainPanel); 

		pack();
		//setSize( new Dimension( SIZE.width, getSize().height));

		setLocationRelativeTo( parent);
	}
	
	/**
	 * Set the properties.
	 *
	 * @param properties the list of grammar properties.
	 */
	public void setProperties( Vector properties) {
		model = new GrammarListModel( properties);
		list.setModel( model);
		
		list.setSelectedIndex( 0);
	}

	/**
	 * Set the properties.
	 *
	 * @param properties the list of grammar properties.
	 */
	public void setSelectedGrammar( GrammarProperties grammar) {
		for ( int i = 0; i < model.getSize(); i++) {
			GrammarProperties prop = model.getType( i);
			
			if ( prop == grammar) {
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
	public GrammarProperties getSelectedType() {
		int index = list.getSelectedIndex();
		
		if ( index != -1 && model.getSize() > index) {
			return model.getType( index);
		} else {
			return model.getType( 0);
		}
	}
	
	class GrammarListModel extends AbstractListModel {
		Vector elements = null;
		
		public GrammarListModel( Vector list) {
			elements = new Vector();

			for ( int i = 0; i < list.size(); i++) {
				GrammarProperties element = (GrammarProperties)list.elementAt(i);

				// Find out where to insert the element...
				int index = -1;

				for ( int j = 0; j < elements.size() && index == -1; j++) {
					// Compare alphabeticaly
					if ( element.getExtensions().compareToIgnoreCase( ((GrammarProperties)elements.elementAt(j)).getExtensions()) <= 0) {
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

		public Object getElementAt( int i) {
			return (GrammarProperties)elements.elementAt( i);
		}

		public GrammarProperties getType( int i) {
			return (GrammarProperties)elements.elementAt( i);
		}
	}
	
	class GrammarListCellRenderer extends JLabel implements ListCellRenderer {
		protected EmptyBorder noFocusBorder;

		public GrammarListCellRenderer() {
			if ( noFocusBorder == null) {
			    noFocusBorder = new EmptyBorder( 1, 1, 1, 1);
			}
			
			setOpaque(true);
			setBorder( noFocusBorder);
		}
		
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean selected, boolean focus) {
		
			if ( value instanceof GrammarProperties) {
				GrammarProperties type = (GrammarProperties)value;

				setText( type.getDescription());
				setIcon( IconFactory.getIconForType( type));
			}

			if (selected) {
				setForeground ( list.getSelectionForeground());
				setBackground ( list.getSelectionBackground());
			} else {
				setForeground( list.getForeground());
				setBackground( list.getBackground());
			}
		
			setEnabled( list.isEnabled());
			setFont( list.getFont());
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

			return this;
		}
	}
} 
