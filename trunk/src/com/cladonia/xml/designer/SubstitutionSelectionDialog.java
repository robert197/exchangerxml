/*
 * $Id: SubstitutionSelectionDialog.java,v 1.5 2005/04/29 12:39:57 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import com.cladonia.schema.SchemaElement;
import com.cladonia.xngreditor.XngrDialog;
//import com.cladonia.schema.XMLSchema;

/**
 * A dialog that takes in a list of schema elements and allows 
 * the user to select a element.
 *
 * @version	$Revision: 1.5 $, $Date: 2005/04/29 12:39:57 $ 
 * @author Dogsbay
 */
public class SubstitutionSelectionDialog extends XngrDialog {
	private static final boolean DEBUG = false;
	private static final Dimension SIZE = new Dimension( 230, 300);

	private JList list	= null;
	//private QLabel text = null;

	private ElementListModel model = null;

	/**
	 * Creates a modal dialog that allows the user to select a 
	 * substitution element.
	 *
	 * @param parent the parent frame.
	 */
	public SubstitutionSelectionDialog( JFrame parent) {
		super( parent,true);
		
		//try {
		//	LicenseManager licenseManager = LicenseManager.getInstance();
		//	licenseManager.isValid( com.cladonia.license.KeyGenerator.generate(2), "Exchanger XML Editor");
		//} catch (Exception x) {
		//	System.exit(0);
		//	return;
		//}

		
		setResizable( false);
		
		setTitle( "Select Substitution", "Select Substitution");
		setDialogDescription( "Select a Substitution element.");
		
		list = new JList();
		list.addMouseListener( new MouseAdapter() { 
			public void mouseReleased( MouseEvent e) {
				int index = list.getSelectedIndex();

				if ( index != -1) { // A row is selected...
					if ( e.getClickCount() == 2) {
						cancelled = false;
						hide();
						//setVisible(false);
					}
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
		
		/*text = new QLabel();
		text.setLines( 3);
		text.setText( "Select an element that should be used to Substitute:");
		text.setHorizontalAlignment( QLabel.LEFT);*/
		//removed for xngr-dialog
		
		/*
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelled = false;
				setVisible(false);
			}
		});
		
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER));
		
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
				
		getRootPane().setDefaultButton( okButton);
		
*/		JPanel mainPanel = new JPanel( new BorderLayout());
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
	 * Set the schema.
	 *
	 * @param schema the schema with the root elements.
	 */
	public void setElement( SchemaElement element) {
		Vector substitutes = element.getSubstituteElements();
		
		if ( substitutes != null) {
			model = new ElementListModel( substitutes);
		
			list.setModel( model);
			
			list.setSelectedIndex( 0);
		}
	}

	/**
	 * Returns the URL of the document.
	 *
	 * @return the URL of the document
	 */
	public SchemaElement getSelectedElement() {
		int index = list.getSelectedIndex();
		
		if ( index != -1) {
			return model.getElement( index);
		} else {
			return model.getElement( 0);
		}
	}
	
	class ElementListModel extends AbstractListModel {
		Vector elements = null;
		
		public ElementListModel( Vector list) {
			elements = new Vector();

			for ( int i = 0; i < list.size(); i++) {
				SchemaElement element = (SchemaElement)list.elementAt(i);

				// Find out where to insert the element...
				int index = -1;

				for ( int j = 0; j < elements.size() && index == -1; j++) {
					// Compare alphabeticaly
					if ( element.getName().compareToIgnoreCase( ((SchemaElement)elements.elementAt(j)).getName()) <= 0) {
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
			return ((SchemaElement)elements.elementAt( i)).getName();
		}

		public SchemaElement getElement( int i) {
			return (SchemaElement)elements.elementAt( i);
		}
	}
} 
