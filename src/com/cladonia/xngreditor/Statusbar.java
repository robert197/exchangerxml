/*
 * $Id: Statusbar.java,v 1.7 2004/07/21 09:10:24 knesbitt Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;




/**
 * The status bar for the ExchangerEditor application.
 *
 * @version	$Revision: 1.7 $, $Date: 2004/07/21 09:10:24 $
 * @author Dogsbay
 */
public class Statusbar extends JPanel {
	private static final boolean DEBUG = false;

	public static final String VALIDATOR_TYPE_SCHEMA	= "XSD";
	public static final String VALIDATOR_TYPE_DTD 		= "DTD";

	public static final String VALIDATION_LOCATION_INTERNAL = "INT";
	public static final String VALIDATION_LOCATION_EXTERNAL = "EXT";

	public static final String DOCUMENT_STATUS_VALID 		= "VAL";
	public static final String DOCUMENT_STATUS_ERROR 		= "ERR";
	public static final String DOCUMENT_STATUS_WELLFORMED 	= "WF";
	public static final String DOCUMENT_STATUS_UNKNOWN 		= "UNK";

	private static final ImageIcon ERROR_STATUS_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ErrorStatusIcon.gif");
	private static final ImageIcon UNKNOWN_STATUS_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/UnknownStatusIcon.gif");
	private static final ImageIcon VALID_STATUS_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ValidStatusIcon.gif");
	private static final ImageIcon WELLFORMED_STATUS_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/WellformedStatusIcon.gif");
	
	private static final String CTRLX = "Ctrl-X";

	private static final Border BEVEL_BORDER = new CompoundBorder( 
													new EmptyBorder( 0, 2, 0, 0),
													new CompoundBorder( 
//														new BevelBorder( BevelBorder.LOWERED, Color.white, new Color( 204, 204, 204), new Color( 204, 204, 204), new Color( 102, 102, 102)), 
														new BevelBorder( BevelBorder.LOWERED, Color.white, UIManager.getColor( "control"), UIManager.getColor( "control"), UIManager.getColor( "controlDkShadow")), 
														new EmptyBorder( 0, 2, 0, 0)));

	private JLabel statusLabel 		= null;
	private JLabel positionLabel	= null;
	private JLabel locationLabel	= null;
	private JLabel validatorLabel	= null;
	private JLabel typeLabel		= null;
	private JTextField modeField		= null;
	private JTextField statusField	= null;
	
	private InputMap modeMap = new InputMap();
	private InputMap currentMap = null;
	private ExchangerEditor parent = null;
	

	/**
	 * The constructor for the about dialog.
	 *
	 * @param frame the parent frame.
	 */
	public Statusbar(ExchangerEditor _parent) {
		super( new BorderLayout());

		this.parent = _parent;
		JPanel flowPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		
		statusLabel = new JLabel();
		statusLabel.setForeground( Color.black);
		statusLabel.setFont( statusLabel.getFont().deriveFont( Font.PLAIN));
		statusLabel.setBorder( BEVEL_BORDER);
		statusLabel.setHorizontalAlignment( SwingConstants.CENTER);
		statusLabel.setVerticalAlignment( SwingConstants.CENTER);
		statusLabel.setPreferredSize( new Dimension( 24, 16));
		
		positionLabel = new JLabel( "Ln 1 Col 1");
		positionLabel.setFont( positionLabel.getFont().deriveFont( Font.PLAIN));
		positionLabel.setForeground( Color.black);
		positionLabel.setPreferredSize( new Dimension( 120, 16));
		positionLabel.setHorizontalAlignment( SwingConstants.CENTER);
		positionLabel.setBorder( BEVEL_BORDER);
		positionLabel.setText("");

		locationLabel = new JLabel( "WWW");
		locationLabel.setFont( locationLabel.getFont().deriveFont( Font.PLAIN));
		locationLabel.setForeground( Color.black);
		locationLabel.setBorder( BEVEL_BORDER);
		locationLabel.setHorizontalAlignment( JLabel.CENTER);
		locationLabel.setPreferredSize( new Dimension( locationLabel.getPreferredSize().width, 16));
		locationLabel.setText("");

		validatorLabel = new JLabel();
		validatorLabel.setFont( validatorLabel.getFont().deriveFont( Font.PLAIN));
		validatorLabel.setForeground( Color.black);
		validatorLabel.setPreferredSize( locationLabel.getPreferredSize());
		validatorLabel.setHorizontalAlignment( JLabel.CENTER);
		validatorLabel.setBorder( BEVEL_BORDER);
		validatorLabel.setText("");

		typeLabel = new JLabel();
		typeLabel.setFont( typeLabel.getFont().deriveFont( Font.PLAIN));
		typeLabel.setForeground( Color.black);
		typeLabel.setPreferredSize( new Dimension( 200, 16));
		typeLabel.setBorder( BEVEL_BORDER);
		typeLabel.setText("");
		
		modeField = new JTextField(" Ctrl-X ");
		modeField.setFocusable(false);
		modeField.setEditable( false);
		modeField.setFont( modeField.getFont().deriveFont( Font.PLAIN));
		modeField.setForeground( Color.black);
		modeField.setBorder( BEVEL_BORDER);
		modeField.setBackground( getBackground());
		modeField.setPreferredSize( new Dimension( modeField.getPreferredSize().width, 16));
		modeField.setText("");
		
		
		// if Ctrl-X caught then return back to current editor
		KeyStroke ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK, false);
		Action modeAction = new AbstractAction(){	
			public void actionPerformed(ActionEvent e)	
			{	
				if ( parent.getView() != null) {
					parent.getView().getEditor().setFocus();
				}
				else
				{
					parent.setIntialFocus();
				}
			}};
			
		modeMap.put(ctrlX, "MODE");
		modeField.getActionMap().put("MODE", modeAction);
		
		// rememeber the current map
		currentMap = modeField.getInputMap();
		
		FocusListener fs = new FocusListener(){
			public void focusGained(FocusEvent e) {
					modeField.setText(CTRLX);
					modeField.setInputMap(JComponent.WHEN_FOCUSED,modeMap);
			    }

		    public void focusLost(FocusEvent e) {
		    		modeField.setText("");
		    		modeField.setInputMap(JComponent.WHEN_FOCUSED,currentMap);
		    }
		};
		
		modeField.addFocusListener(fs);
		
		
		statusField = new JTextField();
		statusField.setFont( statusField.getFont().deriveFont( Font.PLAIN));
		statusField.setBorder( null);
		statusField.setEditable( false);
		statusField.setBackground( getBackground());

		flowPanel.add( modeField);
		flowPanel.add( typeLabel);
		flowPanel.add( validatorLabel);
		flowPanel.add( locationLabel);
		flowPanel.add( statusLabel);
		flowPanel.add( positionLabel);
		
		this.add( statusField, BorderLayout.CENTER);
		this.add( flowPanel, BorderLayout.EAST);

		setBorder( new EmptyBorder( 2, 2, 0, 0));
	}

//	public void setError( boolean enabled) {
//		if ( enabled) {
//			errorLabel.setText( "ERROR");
//		} else {
//			errorLabel.setText( "");
//		}
//	}
	
	/**
	 *  adds an action to the emacs editing mode input\action map
	 *
 	 * @param actionName The action name
 	 * @param stroke The KeyStroke to invoke the action 
 	 * @param action The action 
	 */
	public void addToModeMap(String actionName,KeyStroke stroke,Action action)
	{
		modeMap.put(stroke,actionName);	
		modeField.getActionMap().put(actionName,action);
	}
	
	public JTextField getModeField()
	{
		return modeField;
	}
	
	public void setModeFocusable(boolean condition)
	{
		if (condition)
		{
			modeField.setFocusable(true);
		}
		else
		{
			modeField.setFocusable(false);
		}
	}

	public void setStatus( String status) {
		if (DEBUG) System.out.println( "Statusbar.setStatus( "+status+")");
		statusField.setText( status);
		statusField.setCaretPosition(0);
	}

	public void setDocumentStatus( String status) {
		if (DEBUG) System.out.println( "Statusbar.setDocumentStatus( "+status+")");
		if ( status == DOCUMENT_STATUS_VALID) {
			statusLabel.setIcon( VALID_STATUS_ICON);
		} else if ( status == DOCUMENT_STATUS_ERROR) {
			statusLabel.setIcon( ERROR_STATUS_ICON);
		} else if ( status == DOCUMENT_STATUS_WELLFORMED) {
			statusLabel.setIcon( WELLFORMED_STATUS_ICON);
		} else if ( status == DOCUMENT_STATUS_UNKNOWN){
			statusLabel.setIcon( UNKNOWN_STATUS_ICON);
		} else {
			statusLabel.setIcon( null);
		}
	}

	public void setValidator( String validator) {
		if (DEBUG) System.out.println( "Statusbar.setValidator( "+validator+")");
		validatorLabel.setText( validator);
	}

	public void setLocation( String location) {
		if (DEBUG) System.out.println( "Statusbar.setLocation( "+location+")");
		locationLabel.setText( location);
	}

	public void setType( String type) {
		if (DEBUG) System.out.println( "Statusbar.setType( "+type+")");
		typeLabel.setText( type);
	}

	public void setPosition( int line, int col) {
		positionLabel.setText( "Ln "+line+" Col "+col);
	}

	public void clearPosition() {
		positionLabel.setText( "");
	}
} 

