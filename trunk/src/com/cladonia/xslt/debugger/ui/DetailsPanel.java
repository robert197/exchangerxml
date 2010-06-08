/*
 * $Id: DetailsPanel.java,v 1.4 2004/05/23 14:46:44 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The panel that shows details for the current document.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/05/23 14:46:44 $
 * @author Dogsbay
 */
public class DetailsPanel extends JPanel {
	private static ImageIcon downIcon = null;
	private static ImageIcon upIcon = null;
	
	private JButton closeButton = null;
	private JPanel mainPanel = null;
	private JLabel titleLabel = null;

	/**
	 * The constructor for the properties panel.
	 *
	 * @param properties the configuration properties.
	 */
	public DetailsPanel( String title, ImageIcon icon) {
		this( title, icon, true);
	}
	
	public DetailsPanel( String title, ImageIcon icon, boolean border) {
		super( new BorderLayout());
		
//		this.setBorder( 
//				new CompoundBorder(
//						new MatteBorder( 1, 1, 0, 0, Color.white),
//						new MatteBorder(0, 0, 1, 1, UIManager.getColor("controlDkShadow"))));

		JPanel titelPanel = new JPanel( new BorderLayout());
		titleLabel = new JLabel( title, icon, JLabel.LEFT);
		titelPanel.add( titleLabel, BorderLayout.WEST);
		titelPanel.setBorder( new EmptyBorder( 0, 5, 0, 5));
		if ( border) {
			titelPanel.setBorder( 
				new CompoundBorder( 
					new CompoundBorder(
							new MatteBorder( 1, 1, 0, 0, Color.white),
							new MatteBorder(0, 0, 1, 1, UIManager.getColor("controlDkShadow"))),
					new EmptyBorder( 0, 5, 0, 5)));
		} else {
			titelPanel.setBorder( 
					new CompoundBorder( 
						new CompoundBorder(
								new MatteBorder( 1, 1, 0, 0, UIManager.getColor("controlDkShadow")),
								new MatteBorder( 0, 0, 0, 1, Color.white)),
						new CompoundBorder( 
							new CompoundBorder(
									new MatteBorder( 1, 1, 0, 0, Color.white),
									new MatteBorder(0, 0, 0, 1, UIManager.getColor("controlDkShadow"))),
							new EmptyBorder( 0, 5, 0, 5))));
						;
		}
		
//		closeButton = new JButton();
//		closeButton.setText(null);
//		closeButton.setIcon( getUpIcon());
//		closeButton.setBorder( null);
//		closeButton.setMargin(new Insets(1, 1, 1, 1));
//		closeButton.setOpaque( false);
//		closeButton.addActionListener( new ActionListener(){
//			public void actionPerformed( ActionEvent e) {
//				mainPanel.setVisible( !mainPanel.isVisible());
//				closeButton.setIcon( mainPanel.isVisible() ? getDownIcon() : getUpIcon());
//			}
//		});
//		closeButton.setFocusPainted( false);
//		
//		titelPanel.add( closeButton, BorderLayout.EAST);
		
		mainPanel = new JPanel( new BorderLayout());

		if ( border) {
			this.setBorder( 
					new CompoundBorder(
							new MatteBorder( 1, 1, 0, 0, UIManager.getColor("controlDkShadow")),
							new MatteBorder(0, 0, 1, 1, Color.white)));
		}

		this.add( titelPanel, BorderLayout.NORTH);
		this.add( mainPanel, BorderLayout.CENTER);
	}

	protected void setCenterComponent( JComponent component) {
		mainPanel.add( component, BorderLayout.CENTER);
	}
	
	
	private ImageIcon getUpIcon() { 
		if ( upIcon == null) {
			upIcon = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Up8.gif");
		}
		
		return upIcon;
	}
	
	public void setTitle( String title) {
		titleLabel.setText( title);
	}

	private ImageIcon getDownIcon() { 
		if ( downIcon == null) {
			downIcon = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Down8.gif");
		}
		
		return downIcon;
	}
} 
