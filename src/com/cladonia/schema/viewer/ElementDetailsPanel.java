/*
 * $Id: ElementDetailsPanel.java,v 1.3 2004/09/23 10:35:40 edankert Exp $
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

import com.cladonia.schema.SchemaElement;
import com.cladonia.xngreditor.URLUtilities;

/**
 * The Element details panel.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/09/23 10:35:40 $
 * @author Dogsbay
 */
public class ElementDetailsPanel extends BaseDetailsPanel {
	private SchemaViewerDetails viewer = null;  
	private Schema schemaDecl = null;  

//	private JLabel id = null;
	private LinkButton schema = null;
	private JTextField namespace = null;
	private JLabel id = null;
	private JLabel abstractValue = null; //abstract
	private JLabel block = null;
	private JLabel defaultValue = null; // default
	private JLabel finalValue = null; // final
	private JLabel fixed = null;
	private JLabel form = null;
	private JLabel occurs = null;
	private JLabel nillable = null;
	private JLabel type = null;

	public ElementDetailsPanel( SchemaViewerDetails schemaViewer) {
		super( "Element");
		
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

//		name = createValueLabel();
//		name.setFont( name.getFont().deriveFont( Font.BOLD, (float)14));
//
//		id = createValueLabel();
//		
//		JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0));
//		panel.add( name);
//		panel.add( id);
//		panel.setOpaque( false);
		
//		add( panel, FormLayout.FULL_FILL);

		id = createValueLabel();
		add( "id", id);

		type = createValueLabel();
		add( "type", type);

		occurs = createValueLabel();
		add( "occurs", occurs);

		abstractValue = createValueLabel();
		add( "abstract", abstractValue);

		addSeparator();

		block = createValueLabel();
		add( "block", block);
		defaultValue = createValueLabel();
		add( "default", defaultValue);
		finalValue = createValueLabel();
		add( "final", finalValue);

		fixed = createValueLabel();
		add( "fixed", fixed);
		form = createValueLabel();
		add( "form", form);

		nillable = createValueLabel();
		add( "nillable", nillable);
	}


	public void setPreferredFont( Font font) {
		super.setPreferredFont( font.deriveFont( Font.BOLD, (float)14));
		schema.setFont( font);
		namespace.setFont( font);
		id.setFont( font);
		abstractValue.setFont( font); //abstract
		block.setFont( font);
		defaultValue.setFont( font);
		finalValue.setFont( font);
		fixed.setFont( font);
		form.setFont( font);
		occurs.setFont( font);
		nillable.setFont( font);
		type.setFont( font);
	}

	public void setElement( SchemaElement element) {
		schemaDecl = element.getSchema();
		String schemaLocation = URLUtilities.getFileName( schemaDecl.getSchemaLocation());
		
		setName( element.getName());
		schema.setText( schemaLocation);
		namespace.setText( element.getNamespace());
		namespace.setCaretPosition( 0);
		id.setText( element.getId() != null ? element.getId() : "");
		block.setText( element.getBlock());
		defaultValue.setText( element.getDefault());
		finalValue.setText( element.getFinal());
		fixed.setText( element.getFixed());
		form.setText( element.getForm());

		int max = element.getMaxOccurs();
		int min = element.getMinOccurs();
		occurs.setText( "["+min+".."+ (max != -1 ? ""+max : "*")+"]");
		nillable.setText( ""+element.isNillable());

		String name = element.getType();
		if ( name != null) {
			type.setText( name);
		} else {
			type.setText( "");
		}
		
		abstractValue.setText( ""+element.isAbstract());
	}
}