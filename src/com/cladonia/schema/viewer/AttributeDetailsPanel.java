/*
 * $Id: AttributeDetailsPanel.java,v 1.2 2004/05/18 16:58:39 edankert Exp $
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
import javax.swing.JTextField;

import org.bounce.LinkButton;
import org.exolab.castor.xml.schema.Schema;

import com.cladonia.schema.SchemaAttribute;
import com.cladonia.xngreditor.URLUtilities;

/**
 * The Attribute details panel.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/18 16:58:39 $
 * @author Dogsbay
 */
public class AttributeDetailsPanel extends BaseDetailsPanel {

	private SchemaViewerDetails viewer = null;  
	private Schema schemaDecl = null;  

	private LinkButton schema = null;
	private JTextField namespace = null;
	private JLabel id = null;
	private JLabel defaultValue = null; // default
	private JLabel fixed = null;
	private JLabel form = null;
	private JLabel use = null;
	private JLabel type = null;

	public AttributeDetailsPanel( SchemaViewerDetails schemaViewer) {
		super( "Attribute");
		
		this.viewer = schemaViewer;
		
		schema = new LinkButton();
		schema.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				// show the schema dialog!
				SchemaViewerDetails.getSchemaDetailsDialog().show( schemaDecl);
			}
		});
		add( "schema", schema);

		namespace = createValueTextField();
		add( "xmlns", namespace);

		addSeparator();

		id = createValueLabel();
		add( "id", namespace);

		type = createValueLabel();
		add( "type", type);

		use = createValueLabel();
		add( "use", use);
		
		addSeparator();

		defaultValue = createValueLabel();
		add( "default", defaultValue);
		fixed = createValueLabel();
		add( "fixed", fixed);
		form = createValueLabel();
		add( "form", form);
	}


	public void setPreferredFont( Font font) {
		super.setPreferredFont( font);
		
		schema.setFont( font);
		namespace.setFont( font);
		id.setFont( font);
		defaultValue.setFont( font);
		fixed.setFont( font);
		form.setFont( font);
		use.setFont( font);
		type.setFont( font);
	}

	public void setAttribute( SchemaAttribute attribute) {
		schemaDecl = attribute.getSchema();
		String schemaLocation = URLUtilities.getFileName( schemaDecl.getSchemaLocation());

		schema.setText( schemaLocation);
		setName( attribute.getName());
		namespace.setText( attribute.getNamespace());
		namespace.setCaretPosition( 0);
		id.setText( attribute.getId() != null ? "(id: "+attribute.getId()+")" : "");
		defaultValue.setText( attribute.getDefault());
		fixed.setText( attribute.getFixed());
		form.setText( attribute.getForm());
		use.setText( attribute.getUse());
//		getBuiltInBaseType()
		type.setText( attribute.getType().getName());
	}
}