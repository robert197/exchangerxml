/*
 * $Id: ContentModelDetailsPanel.java,v 1.2 2004/05/18 16:58:39 edankert Exp $
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

import javax.swing.JLabel;

import org.bounce.LinkButton;
import org.exolab.castor.xml.schema.Schema;

import com.cladonia.schema.SchemaModel;
import com.cladonia.xngreditor.URLUtilities;

/**
 * The Content Model details panel.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/18 16:58:39 $
 * @author Dogsbay
 */
public class ContentModelDetailsPanel extends BaseDetailsPanel {
	private SchemaViewerDetails viewer = null;  
	private Schema schemaDecl = null;  

	private LinkButton schema = null;

	private JLabel id = null;
	private JLabel occurs = null;

	public ContentModelDetailsPanel(  SchemaViewerDetails schemaViewer, String title) {
		super( title);
		
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
		occurs = createValueLabel();
		add( "occurs", occurs);
	}

	public void setPreferredFont( Font font) {
//		super.setPreferredFont( font.deriveFont( Font.BOLD, (float)14));
		
		schema.setFont( font);
		id.setFont( font);
		occurs.setFont( font);
	}

	public void setContentModel( SchemaModel model) {

		schemaDecl = model.getSchema();
		String schemaLocation = URLUtilities.getFileName( schemaDecl.getSchemaLocation());
		
		id.setText( model.getId());

		schema.setText( schemaLocation);

		int max = model.getMaxOccurs();
		int min = model.getMinOccurs();
		occurs.setText( "["+min+".."+ (max != -1 ? ""+max : "*")+"]");
	}
}