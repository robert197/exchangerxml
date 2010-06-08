/*
 * $Id: AnyAttributeDetailsPanel.java,v 1.3 2005/08/25 10:48:21 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.bounce.FormLayout;
import org.bounce.LinkButton;
import org.exolab.castor.xml.schema.Schema;

import com.cladonia.schema.AnySchemaAttribute;
import com.cladonia.xngreditor.URLUtilities;

/**
 * The (wildcard) Attribute details panel.
 *
 * @version	$Revision: 1.3 $, $Date: 2005/08/25 10:48:21 $
 * @author Dogsbay
 */
public class AnyAttributeDetailsPanel extends BaseDetailsPanel {

	private SchemaViewerDetails viewer = null;  
	private Schema schemaDecl = null;  

	private LinkButton schema = null;

	private JLabel id				= null;
	private JLabel processContents	= null;
	private JList namespaces		= null;

	public AnyAttributeDetailsPanel( SchemaViewerDetails schemaViewer) {
		super( "Any Attribute");
		
		this.viewer = schemaViewer;
		
		schema = new LinkButton();
		schema.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				// show the schema dialog!
				SchemaViewerDetails.getSchemaDetailsDialog().show( schemaDecl);
			}
		});
		add( "schema", schema);

		addSeparator();

		id = createValueLabel();
		add( "id", id);
		processContents = createValueLabel();
		add( "processContents", processContents);

		addSeparator();

		add( createTitleLabel( "namespaces"), FormLayout.FULL);

		namespaces = new JList();
		namespaces.setVisibleRowCount( 3);
		namespaces.setOpaque( false);
		JScrollPane scrollPane = new JScrollPane(	namespaces,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setOpaque( false);		
		scrollPane.getViewport().setOpaque( false);
		add( scrollPane, FormLayout.FULL_FILL);
	}
	
	public void setPreferredFont( Font font) {
		super.setPreferredFont( font);
		
		schema.setFont( font);
		id.setFont( font);
		processContents.setFont( font);
		namespaces.setFont( font);
	}

	public void setWildcard( AnySchemaAttribute wildcard) {
		schemaDecl = wildcard.getSchema();
		String schemaLocation = URLUtilities.getFileName( schemaDecl.getSchemaLocation());

		schema.setText( schemaLocation);

		// Castor does not have a getId for the wildcard particle.
		id.setText( wildcard.getId());

		processContents.setText( toString( wildcard.getProcessContents()));

		namespaces.setModel( new NamespacesListModel( wildcard.getNamespaces()));
	}

	class NamespacesListModel extends AbstractListModel {
		Vector namespaces = null;
		
		public NamespacesListModel( Enumeration enumeration) {
			namespaces = new Vector();

			while( enumeration.hasMoreElements()) {
				namespaces.addElement( enumeration.nextElement());
			}
		}
		
		public int getSize() {
			if ( namespaces != null) {
				return namespaces.size();
			}
			
			return 0;
		}

		public Object getElementAt( int i) {
			return (String)namespaces.elementAt( i);
		}
	}
}