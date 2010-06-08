/*
 * $Id: AboutDialog.java,v 1.2 2004/11/04 19:21:48 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.bounce.FormLayout;
import org.bounce.LinkButton;
import org.bounce.QPanel;
import org.bounce.util.BrowserLauncher;

import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The dialog that shows the eXchaNGeR identity.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/11/04 19:21:48 $
 * @author Dogsbay
 */
public class AboutDialog extends JDialog {
	private static final Dimension SIZE = new Dimension( 300, 200);
	private LinkButton referenceField  = null;

	/**
	 * The constructor for the about dialog.
	 *
	 * @param frame the parent frame.
	 */
	public AboutDialog( JFrame frame) {
		super( frame, false);
		
		setResizable( false);
		
		setTitle( "About Schema Viewer");
		
		QPanel formpanel = new QPanel( new FormLayout( 5, 3));
		formpanel.setBorder( 
					new CompoundBorder( 
						new LineBorder( Color.darkGray),
						new EmptyBorder( 5, 5, 5, 5)));

		ImageIcon icon = XngrImageLoader.get().getImage( "com/cladonia/schema/viewer/icons/SchemaIcon.gif");
		SchemaViewerIdentity identity = new SchemaViewerIdentity();
		
		JPanel contentPane = new JPanel( new BorderLayout());
		
		JLabel titleField = new JLabel( identity.getTitle());
		Font prevFont = titleField.getFont();
		titleField.setFont( new Font( prevFont.getFontName(), Font.BOLD, prevFont.getSize() + 8));

		JLabel descriptionField = new JLabel( identity.getDescription());
		JLabel versionField = new JLabel( "Version: "+identity.getVersion());

		referenceField = new LinkButton( identity.getReference());
		referenceField.setHorizontalAlignment( SwingConstants.CENTER);
		referenceField.setForeground( Color.blue);
		referenceField.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				try {
					BrowserLauncher.openURL( referenceField.getText());
				} catch( Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		JLabel copyrightField = new JLabel( identity.getCopyright());
		copyrightField.setHorizontalAlignment( SwingConstants.CENTER);

		JLabel iconField = new JLabel( icon);
		iconField.setBorder( new EmptyBorder( 0, 10, 0, 10));
		
		Box descriptionPanel = Box.createVerticalBox();
//		descriptionPanel.setOpaque( false);
		descriptionPanel.add( titleField);
		descriptionPanel.add( descriptionField);
		descriptionPanel.add( versionField);
		
		JPanel productPanel = new JPanel( new BorderLayout());
	
		productPanel.setOpaque( false);
		productPanel.add( descriptionPanel, BorderLayout.CENTER);
		productPanel.add( iconField, BorderLayout.EAST);
		productPanel.setBorder( new EmptyBorder( 5, 5, 10, 5));

		JButton okButton = new JButton( "OK");
		
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				okButtonPressed();
			}
		});
		
		contentPane.setBorder( new EmptyBorder( 2, 2, 2, 2));
		
		copyrightField.setBorder( new EmptyBorder( 10, 0, 0, 0));
		
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( okButton);

		formpanel.add( productPanel, FormLayout.FULL_FILL);
		formpanel.add( referenceField, FormLayout.FULL_FILL);
		formpanel.add( copyrightField, FormLayout.FULL_FILL);

		contentPane.add( formpanel, BorderLayout.CENTER);
		contentPane.add( buttonPanel, BorderLayout.SOUTH);
		
		setContentPane( contentPane);
		
		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));

		getRootPane().setDefaultButton( okButton);
		setDefaultCloseOperation( HIDE_ON_CLOSE);
	}
	// Ok button handler
	private void okButtonPressed() {
		//setVisible( false);
		hide();
	}
} 
