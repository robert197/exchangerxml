/*
 * $Id: Statusbar.java,v 1.3 2004/05/28 15:38:21 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.bounce.QButton;
import org.bounce.event.DoubleClickListener;

import com.cladonia.xngreditor.IconFactory;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The status bar for the ExchangerEditor application.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/05/28 15:38:21 $
 * @author Dogsbay
 */
public class Statusbar extends JPanel {
	private static final boolean DEBUG = false;

	private static final ImageIcon STATUS_IDLE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/Transformation16.gif");
	private static final ImageIcon STATUS_RUNNING_ICON1 = STATUS_IDLE_ICON;
	private static final ImageIcon STATUS_RUNNING_ICON2 = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/TransformationRotate16.gif");
	private static final ImageIcon STATUS_PAUSED_ICON1 = STATUS_IDLE_ICON;
	private static final ImageIcon STATUS_PAUSED_ICON2 = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/TransformationSmall16.gif");

	public static final int STATUS_IDLE			= -1;
	public static final int STATUS_RUNNING		= 0;
	public static final int STATUS_PAUSED		= 1;
	
	private int status = STATUS_IDLE;

	public static final String PROCESSOR_XALAN	= "Xalan";
	public static final String PROCESSOR_SAXON1	= "Saxon (XSLT 1.*)";
	public static final String PROCESSOR_SAXON2	= "Saxon (XSLT 2.0)";

	private static final Border BEVEL_BORDER = new CompoundBorder( 
													new EmptyBorder( 0, 2, 0, 0),
													new CompoundBorder( 
//														new BevelBorder( BevelBorder.LOWERED, Color.white, new Color( 204, 204, 204), new Color( 204, 204, 204), new Color( 102, 102, 102)), 
														new BevelBorder( BevelBorder.LOWERED, Color.white, UIManager.getColor( "control"), UIManager.getColor( "control"), UIManager.getColor( "controlDkShadow")), 
														new EmptyBorder( 0, 2, 0, 0)));

	private XSLTDebuggerPane debugger	= null;
	private JLabel processorLabel 		= null;
	private String inputURL 			= null;
	private String styleURL 			= null;
	

	private QButton inputURLButton 		= null;
	private QButton styleURLButton 		= null;

	private JLabel statusLabel	= null;

	/**
	 * The constructor for the about dialog.
	 *
	 * @param frame the parent frame.
	 */
	public Statusbar( XSLTDebuggerPane debugger) {
		super( new BorderLayout());

		this.debugger = debugger;
		
		JPanel flowPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		
		processorLabel = new JLabel( PROCESSOR_SAXON2+"X");
		processorLabel.setForeground( Color.black);
		processorLabel.setFont( processorLabel.getFont().deriveFont( Font.PLAIN));
		processorLabel.setBorder( BEVEL_BORDER);
		processorLabel.setHorizontalAlignment( JLabel.CENTER);
		processorLabel.setPreferredSize( new Dimension( processorLabel.getPreferredSize().width, 20));
		processorLabel.setText("");
		
		styleURLButton = new QButton();
		styleURLButton.setPreferredSize( new Dimension( 150, 20));
		styleURLButton.setHorizontalAlignment( SwingConstants.LEFT);
		styleURLButton.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent event) {
				Statusbar.this.debugger.getXSLTView().select( styleURL);
			}
		});
		styleURLButton.setOpaque( false);
		styleURLButton.setFont( styleURLButton.getFont().deriveFont( Font.PLAIN));
		styleURLButton.setBorder( BEVEL_BORDER);
		styleURLButton.setFocusPainted( false);

		inputURLButton = new QButton();
		inputURLButton.setPreferredSize( new Dimension( 150, 20));
		inputURLButton.setHorizontalAlignment( SwingConstants.LEFT);
		inputURLButton.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent event) {
				Statusbar.this.debugger.getInputView().select( inputURL);
			}
		});
		inputURLButton.setBorder( BEVEL_BORDER);
		inputURLButton.setOpaque( false);
		inputURLButton.setFont( inputURLButton.getFont().deriveFont( Font.PLAIN));
		inputURLButton.setFocusPainted( false);

		statusLabel = new JLabel();
//		statusLabel.setFont( statusLabel.getFont().deriveFont( Font.PLAIN));
		statusLabel.setBorder( null);
		statusLabel.setBackground( getBackground());

		flowPanel.add( styleURLButton);
		flowPanel.add( inputURLButton);
		flowPanel.add( processorLabel);
		
		this.add( statusLabel, BorderLayout.CENTER);
		this.add( flowPanel, BorderLayout.EAST);
		
		Timer timer = new Timer( 200, new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				updateStatusIcon();
			}
		});
		
		timer.start();

		setBorder( new EmptyBorder( 2, 2, 2, 2));
	}

	public void setStatus( int status, String text) {
		if (DEBUG) System.out.println( "Statusbar.setStatus( "+status+", "+text+")");
		
		this.status = status;
		
		updateStatusIcon();

		statusLabel.setText( text);
	}

	public void setStyleURL( String url) {
		if (DEBUG) System.out.println( "Statusbar.setStyleURL( "+url+")");

		if ( url == null) {
			styleURLButton.setText( "");
			styleURLButton.setIcon( null);
		} else {
			styleURLButton.setText( URLUtilities.getFileName( url));
			styleURLButton.setIcon( IconFactory.getIconForExtension( URLUtilities.getExtension( url)));
		}
		
		styleURLButton.setToolTipText( url);
		styleURL = url;
	}

	public void setInputURL( String url) {
		if (DEBUG) System.out.println( "Statusbar.setInputURL( "+url+")");

		if ( url == null) {
			inputURLButton.setText( "");
			inputURLButton.setIcon( null);
		} else {
			inputURLButton.setText( URLUtilities.getFileName( url));
			inputURLButton.setIcon( IconFactory.getIconForExtension( URLUtilities.getExtension( url)));
		}

		inputURLButton.setToolTipText( url);
		inputURL = url;
	}

	public void setProcessor( String processor) {
		if (DEBUG) System.out.println( "Statusbar.setProcessor( "+processor+")");

		processorLabel.setText( processor);
	}
	
	public void updateStatusIcon() {
		Icon icon = statusLabel.getIcon();

		if ( status == STATUS_PAUSED) {
			if ( icon == STATUS_PAUSED_ICON1) {
				statusLabel.setIcon( STATUS_PAUSED_ICON2);
			} else {
				statusLabel.setIcon( STATUS_PAUSED_ICON1);
			}
		} else if ( status == STATUS_RUNNING) {
			if ( icon == STATUS_RUNNING_ICON1) {
				statusLabel.setIcon( STATUS_RUNNING_ICON2);
			} else {
				statusLabel.setIcon( STATUS_RUNNING_ICON1);
			}
			
		} else {
			statusLabel.setIcon( STATUS_IDLE_ICON);
		}
	}

} 
