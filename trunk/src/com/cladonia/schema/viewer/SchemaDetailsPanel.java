/*
 * $Id: SchemaDetailsPanel.java,v 1.2 2004/05/18 16:58:39 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bounce.FormLayout;
import org.exolab.castor.xml.schema.Schema;

import com.cladonia.xngreditor.URLUtilities;

/**
 * The Schema details panel.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/18 16:58:39 $
 * @author Dogsbay
 */
public class SchemaDetailsPanel extends BaseDetailsPanel {
	private JTextField location = null;

	private JLabel id = null;
	private JLabel attributeFormDefault = null;
	private JLabel blockDefault = null;
	private JLabel elementFormDefault = null;
	private JLabel finalDefault = null;
	private JLabel version = null;
//	private JLabel language = null;
	private JTextField targetNamespace = null;

	public SchemaDetailsPanel() {
		super( "Schema");
		
		location = createValueTextField();
		JPanel panel = new JPanel( new FormLayout( 5, 3));
		panel.add( createTitleLabel( "Location"), FormLayout.LEFT); 
		panel.add( location, FormLayout.RIGHT_FILL);
		panel.setOpaque( false);
		
		add( panel, FormLayout.FULL_FILL);

//		namespace = createValueTextField();
//		add( "Namespace", namespace);
		addSeparator();

		id = createValueLabel();
		add( "id", id);
		version = createValueLabel();
		add( "version", version);

		addSeparator();

		targetNamespace = createValueTextField();
		add( "targetNamespace", targetNamespace);

		addSeparator();

		attributeFormDefault = createValueLabel();
		add( "attributeFormDefault", attributeFormDefault);
		elementFormDefault = createValueLabel();
		add( "elementFormDefault", elementFormDefault);
		blockDefault = createValueLabel();
		add( "blockDefault", blockDefault);
		finalDefault = createValueLabel();
		add( "finalDefault", finalDefault);
	}

	public void setPreferredFont( Font font) {
		super.setPreferredFont( font);
		
		id.setFont( font);
		attributeFormDefault.setFont( font);
		blockDefault.setFont( font);
		elementFormDefault.setFont( font);
		finalDefault.setFont( font);
		version.setFont( font);
		targetNamespace.setFont( font);
	}

	public void setSchema( Schema schema) {
		if ( schema != null) {
			String schemaName = URLUtilities.getFileName( schema.getSchemaLocation());

			setName( schemaName);
			location.setText( schema.getSchemaLocation());
			location.setCaretPosition(0);
//			namespace.setText( schema.getSchemaNamespace());

			id.setText( schema.getId());
			attributeFormDefault.setText( toString( schema.getAttributeFormDefault(), "unqualified"));
			blockDefault.setText( toString( schema.getBlockDefault()));
			elementFormDefault.setText( toString( schema.getElementFormDefault(), "unqualified"));
			finalDefault.setText( toString( schema.getFinalDefault()));
			targetNamespace.setText( schema.getTargetNamespace());
			targetNamespace.setCaretPosition(0);
			version.setText( schema.getVersion());
		}
//		language.setText( schema.getLanguage());
	}
}