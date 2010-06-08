/*
 * $Id: GUIUtilities.java,v 1.1 2004/10/13 18:32:33 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xngreditor.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.cladonia.xngreditor.IconFactory;

/**
 * This GUIUtilities is used to ...
 *
 * @version $Revision: 1.1 $, $Date: 2004/10/13 18:32:33 $
 * @author Dogsbay
 */
public class GUIUtilities {

	/**
	 * Aligns the sub items in menu.
	 * 
	 * @param menu
	 */
	public static void alignMenu( JMenu menu) {
		//alignMenu( menu.getMenuComponents());
	}

	/**
	 * Aligns the sub items in a popup menu.
	 * 
	 * @param menu
	 */
	public static void alignMenu( JPopupMenu menu) {
		//alignMenu( menu.getComponents());
	}

	private static void alignMenu( Component[] items) {
		Dimension iconSize = new Dimension( 0, 0);
		Dimension checkSize = null;
		Insets margin = null;
		
		if ( UIManager.getLookAndFeel().getName().toLowerCase().indexOf( "windows") == -1) {
			
			// calculate the correct size...
			for ( int i = 0; i < items.length; i++) {
				if ( items[i] instanceof JMenuItem) {
					if ( items[i] instanceof JCheckBoxMenuItem || items[i] instanceof JRadioButtonMenuItem && checkSize == null) {
						Icon checkIcon = UIManager.getIcon( "CheckBoxMenuItem.checkIcon");
						margin = ((JMenuItem)items[i]).getMargin(); 
	
						if ( checkIcon != null) {
							checkSize = new Dimension( checkIcon.getIconWidth(), checkIcon.getIconHeight());
						}
					}
	
					Icon icon = ((JMenuItem)items[i]).getIcon();
	
					if ( icon != null) {
						if ( iconSize == null) {
							iconSize = new Dimension( icon.getIconWidth(), icon.getIconHeight());
						} else {
							if ( iconSize.width < icon.getIconWidth()) {
								iconSize.width = icon.getIconWidth();
							}
		
							if ( iconSize.height < icon.getIconHeight()) {
								iconSize.height = icon.getIconHeight();
							}
						}
					}
				} else if ( items[i] instanceof JMenu) {
					Icon icon = ((JMenu)items[i]).getIcon();
	
					if ( icon != null) {
						if ( iconSize == null) {
							iconSize = new Dimension( icon.getIconWidth(), icon.getIconHeight());
						} else {
							if ( iconSize.width < icon.getIconWidth()) {
								iconSize.width = icon.getIconWidth();
							}
		
							if ( iconSize.height < icon.getIconHeight()) {
								iconSize.height = icon.getIconHeight();
							}
						}
					}
				}
			}
			
			EmptyBorder border = null;
			
			if ( checkSize != null && margin != null) {
				border = new EmptyBorder( margin.top, margin.left + checkSize.width, margin.bottom, margin.right);
			}
	
			// Set the correct values...
			for ( int i = 0; i < items.length; i++) {
				if ( items[i] instanceof JMenuItem) {
					if ( items[i] instanceof JCheckBoxMenuItem || items[i] instanceof JRadioButtonMenuItem) {
						JMenuItem item = (JMenuItem)items[i];
	
						Icon icon = item.getIcon();
						
						if ( icon != null && icon.getIconWidth() < iconSize.width) {
							item.setIconTextGap( item.getIconTextGap() + ((iconSize.width - icon.getIconWidth())/2));
						} else if ( icon == null) {
							item.setIcon( IconFactory.getEmptyIcon( iconSize.width, iconSize.height));
						}
					} else {
						JMenuItem item = (JMenuItem)items[i];
	
						Icon icon = item.getIcon();
						
						if ( border != null) {
							item.setBorder( border);
						}

						if ( icon != null && icon.getIconWidth() < iconSize.width) {
							Insets insets = item.getMargin();
							item.setBorder( new EmptyBorder( insets.top, insets.left+ (iconSize.width - icon.getIconWidth()), insets.bottom, insets.right));
						} else if ( icon == null) {
							item.setIcon( IconFactory.getEmptyIcon( iconSize.width, iconSize.height));
						}
					}
				} else if ( items[i] instanceof JMenu) {
					JMenu item = (JMenu)items[i];
	
					Icon icon = item.getIcon();
					
					if ( border != null) {
						item.setBorder( border);
					}

					if ( icon != null && icon.getIconWidth() < iconSize.width) {
						Insets insets = item.getMargin();
						item.setBorder( new EmptyBorder( insets.top, insets.left+ (iconSize.width - icon.getIconWidth()), insets.bottom, insets.right));
					} else if ( icon == null) {
						item.setIcon( IconFactory.getEmptyIcon( iconSize.width, iconSize.height));
					}
				}
			}
		} else { // Windows!
			// calculate the correct size...
			for ( int i = 0; i < items.length; i++) {
				if ( items[i] instanceof JMenuItem) {
					Icon icon = ((JMenuItem)items[i]).getIcon();
	
					if ( icon != null) {
						if ( iconSize == null) {
							iconSize = new Dimension( icon.getIconWidth(), icon.getIconHeight());
						} else {
							if ( iconSize.width < icon.getIconWidth()) {
								iconSize.width = icon.getIconWidth();
							}
		
							if ( iconSize.height < icon.getIconHeight()) {
								iconSize.height = icon.getIconHeight();
							}
						}
					}
				} else if ( items[i] instanceof JMenu) {
					Icon icon = ((JMenu)items[i]).getIcon();
	
					if ( icon != null) {
						if ( iconSize == null) {
							iconSize = new Dimension( icon.getIconWidth(), icon.getIconHeight());
						} else {
							if ( iconSize.width < icon.getIconWidth()) {
								iconSize.width = icon.getIconWidth();
							}
		
							if ( iconSize.height < icon.getIconHeight()) {
								iconSize.height = icon.getIconHeight();
							}
						}
					}
				}
			}
			
			// Set the correct values...
			for ( int i = 0; i < items.length; i++) {
				if ( items[i] instanceof JMenuItem) {
					if ( items[i] instanceof JCheckBoxMenuItem || items[i] instanceof JRadioButtonMenuItem) {
						JMenuItem item = (JMenuItem)items[i];
	
						if ( item instanceof JRadioButtonMenuItem) {
							item.setBorder( new EmptyBorder( 2, 5, 2, 2));
						}
	
						Icon icon = item.getIcon();
						
						if ( icon != null && icon.getIconWidth() < iconSize.width) {
							item.setIconTextGap( item.getIconTextGap() + ((iconSize.width - icon.getIconWidth())/2));
						} else if ( icon == null) {
							item.setIcon( IconFactory.getEmptyIcon( iconSize.width, iconSize.height));
						}
					} else {
						JMenuItem item = (JMenuItem)items[i];
	
						Icon icon = item.getIcon();
						
						if ( icon != null && icon.getIconWidth() < iconSize.width) {
							Insets insets = item.getMargin();
							item.setBorder( new EmptyBorder( insets.top, insets.left+ (iconSize.width - icon.getIconWidth()), insets.bottom, insets.right));
						} else if ( icon == null) {
							item.setIcon( IconFactory.getEmptyIcon( iconSize.width, iconSize.height));
						}
					}
				} else if ( items[i] instanceof JMenu) {
					JMenu item = (JMenu)items[i];
	
					Icon icon = item.getIcon();
					
					if ( icon != null && icon.getIconWidth() < iconSize.width) {
						Insets insets = item.getMargin();
						item.setBorder( new EmptyBorder( insets.top, insets.left+ (iconSize.width - icon.getIconWidth()), insets.bottom, insets.right));
					} else if ( icon == null) {
						item.setIcon( IconFactory.getEmptyIcon( iconSize.width, iconSize.height));
					}
				}
			}
		}
	}
}
