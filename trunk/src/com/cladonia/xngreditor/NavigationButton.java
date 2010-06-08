/*
 * $Id: NavigationButton.java,v 1.2 2005/05/17 10:55:05 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;


/**
 * Formats a Navigation Button.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/05/17 10:55:05 $
 * @author Dogsbay
 */
public class NavigationButton extends JToggleButton {
	private static final Color DARK_SHADOW		=  UIManager.getColor( "controlDkShadow"); // new Color( 102, 102, 102)
	private static final Color SHADOW 			=  UIManager.getColor( "controlShadow"); // new Color( 153, 153, 153)
	private static final Color CONTROL 			=  UIManager.getColor( "control"); // new Color( 204, 204, 204)
//	private static final Color HIGHLIGHT 		=  UIManager.getColor( "controlHighlight"); // new Color( 255, 255, 255)
	private static final Color LIGHT_HIGHLIGHT 	=  UIManager.getColor( "controlLtHighlight"); // new Color( 255, 255, 255)
//	private static final CompoundBorder BORDER = new CompoundBorder(
//														new MatteBorder( 1, 1, 0, 0, new Color( 102, 102, 102)), 
//														new MatteBorder( 0, 0, 0, 1, Color.white));

	private static final CompoundBorder BORDER = new CompoundBorder( 
													new CompoundBorder( 
														new MatteBorder( 1, 1, 1, 1, DARK_SHADOW), 
														new CompoundBorder( 
															new MatteBorder( 1, 1, 0, 0, CONTROL),
															new MatteBorder( 0, 0, 1, 1, SHADOW))),
													new EmptyBorder( 0, 2, 0, 2));

//	private static final CompoundBorder BORDER = new CompoundBorder( 
//														new MatteBorder( 1, 1, 0, 1, new Color( 102, 102, 102)), 
//														new MatteBorder( 1, 1, 2, 1, new Color( 204, 204, 204)));
//														new MatteBorder( 0, 0, 2, 1, new Color( 153, 153, 153))));
	private static final CompoundBorder EMPTY_BORDER = new CompoundBorder( 
														new MatteBorder( 1, 1, 1, 1, SHADOW),
														new EmptyBorder( 1, 3, 1, 3));
//	private static final EmptyBorder EMPTY_BORDER = new EmptyBorder( 2, 4, 2, 4);
//	private static final CompoundBorder SELECTED_BORDER = new CompoundBorder( 
//														new MatteBorder( 1, 1, 0, 0, Color.white), new MatteBorder( 0, 0, 0, 1, new Color( 102, 102, 102)));
	/*private static final CompoundBorder SELECTED_BORDER = new CompoundBorder( 
															new CompoundBorder( 
																new MatteBorder( 1, 1, 1, 1, DARK_SHADOW), 
																new CompoundBorder( 
																	new MatteBorder( 1, 1, 0, 0, Color.white),
																	new MatteBorder( 0, 0, 1, 1, CONTROL))),
															new EmptyBorder( 0, 2, 0, 2));*/
	
	private static final CompoundBorder SELECTED_BORDER = new CompoundBorder( 
			new CompoundBorder( 
				new MatteBorder( 2, 2, 2, 2, DARK_SHADOW), 
				new CompoundBorder( 
					new MatteBorder( 2, 2, 0, 0, Color.white),
					new MatteBorder( 0, 0, 2, 2, CONTROL))),
			new EmptyBorder( 0, 2, 0, 2));
	
//	private static final BORDER = new CompoundBorder( new CompoundBorder( new LineBorder( getBackground(), 1), new LineBorder( Color.gray, 1)), new EmptyBorder( 0, 2, 0, 2))
	private static final Color SELECTED_BACKGROUND = CONTROL;
	private static final Color BACKGROUND = SHADOW;
	private static final Color SELECTED_FOREGROUND = Color.black;
	private static final Color FOREGROUND = CONTROL;
	
//	private boolean selected = false;
	
	/**
	 * The constructor for the navigation Button.
	 *
	 * @param frame the parent frame.
	 */
	public NavigationButton( String text, ImageIcon icon) {
		super( text, icon);
		
		this.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				if ( isSelected()) {
					setSelectedProperties( isEnabled());
				} else {
					setUnselectedProperties( isEnabled());
				}
			}
		});
	
		setContentAreaFilled( true);
		setUnselectedProperties( isEnabled());
	}
	
	public void setSelected( boolean enabled) {
		if ( enabled) {
			setSelectedProperties( isEnabled());
		} else {
			setUnselectedProperties( isEnabled());
		}
		
		super.setSelected( enabled);
//		this.selected = selected;
	}

//	public boolean isSelected() {
//		return selected;
//	}

	public void setEnabled( boolean enabled) {
		if ( isSelected()) {
			setSelectedProperties( enabled);
		} else {
			setUnselectedProperties( enabled);
		}

		super.setEnabled( enabled);
	}
	
	private void setUnselectedProperties( boolean enabled) {
		if ( enabled) {
			setContentAreaFilled( true);
			setBorder( BORDER);
		} else {
			setContentAreaFilled( false);
			setBorder( EMPTY_BORDER);
		}

		setBackground( BACKGROUND);
		//setForeground( FOREGROUND);
		setForeground( Color.BLACK);
//		System.out.println( "["+getText()+"] Background: "+BACKGROUND);
	}

	private void setSelectedProperties( boolean enabled) {
		if ( enabled) {
			setContentAreaFilled( false);
			setBorder( SELECTED_BORDER);

			//setForeground( SELECTED_FOREGROUND);
			setForeground( Color.BLACK);
			setBackground( SELECTED_BACKGROUND);
//			System.out.println( "["+getText()+"] Selected Background: "+SELECTED_BACKGROUND);
			
			/*
			 * Used to set the background of the button to white
			 * works in all l&fs except jgoodies.
			 *
			setContentAreaFilled( false);
			setOpaque(true);
			setBorder( SELECTED_BORDER);
			setForeground( Color.BLACK);
			setBackground( Color.WHITE);
			*/
			
		} else {
			setContentAreaFilled( false);
			setBorder( EMPTY_BORDER);

			setForeground( FOREGROUND);
			setBackground( BACKGROUND);
		}
	}
}
