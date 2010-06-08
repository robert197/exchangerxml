/*
 * $Id: ElementSelectionPanel.java,v 1.3 2005/08/29 08:27:39 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.cladonia.schema.SchemaElement;
import com.cladonia.xngreditor.ExchangerEditor;

/**
 * A dialog that can be used to set the referencing elements.
 *
 * @version	$Revision: 1.3 $, $Date: 2005/08/29 08:27:39 $ 
 * @author Dogsbay
 */
public class ElementSelectionPanel extends JPanel {
	private static final boolean DEBUG = false;
	private static final Dimension SIZE = new Dimension( 200, 300);

	private JList list	= null;

	private ElementListModel model = null;

	private JButton showButton = null;

	private ExchangerEditor parent = null;
	
	/**
	 * Creates a panel that allows the user to select an 
	 * element.
	 */
	public ElementSelectionPanel( ExchangerEditor parent, int rows) {
		super( new BorderLayout());
		
		this.parent = parent;
		
		list = new JList();
		list.addMouseListener( new MouseAdapter() { 
			public void mouseReleased( MouseEvent e) {
				int index = list.getSelectedIndex();

				if ( index != -1) { // A row is selected...
					if ( e.getClickCount() == 2) {
						elementSelected();
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
		
		list.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = list.getSelectedIndex();
                if(selected>-1) {
                    list.ensureIndexIsVisible(selected);
                }
            }
		    
		});
		list.setVisibleRowCount( rows);
		list.setFixedCellHeight( 18);
		
//		JLabel text = new JLabel();
//		text.setText( title);
//		text.setHorizontalAlignment( QLabel.LEFT);
//		text.setOpaque( false);
		
		showButton = new JButton("Show");

		showButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				try{
					elementSelected();
				}catch(Exception e1) {
					
				}
			}
		});
		
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( showButton);
		buttonPanel.setOpaque( false);
		
		JScrollPane scrollPane = new JScrollPane(	list,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

//		this.add( text, BorderLayout.NORTH);
		this.add( scrollPane, BorderLayout.CENTER);
		this.add( buttonPanel, BorderLayout.SOUTH);

		this.setOpaque( false);
		this.setBorder( new EmptyBorder( 2, 2, 2, 2));
	}
	
	public void setPreferredFont( Font font) {
		list.setFont( font);
	}
	
	private void elementSelected() {
		parent.getView().getSchemaViewer().setRoot( getSelectedElement());
				
	} 

	/**
	 * Set the selected element.
	 *
	 * @param element the selected element.
	 */
	public void setElements( Vector elements) {
		model = new ElementListModel( elements);
		
		list.setModel( model);
		
		if ( model.getSize() > 0) {
			list.setSelectedIndex( 0);
			list.ensureIndexIsVisible( 0);
		}
	}

	/**
	 * Returns the URL of the document.
	 *
	 * @return the URL of the document
	 */
	public SchemaElement getSelectedElement() {
		int index = list.getSelectedIndex();
		//if(model.getSize() > 0) {
			if ( index != -1) {
				return model.getElement( index);
			} else {
				return model.getElement( 0);
			}
		//}
		//else {
		//	return(null);
		//}
	}
	
	class ElementListModel extends AbstractListModel {
		Vector elements = null;
		
		public ElementListModel( Vector list) {
			elements = new Vector();
			
			if ( list != null) {
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
