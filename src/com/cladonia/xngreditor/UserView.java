/*
 * $Id: UserView.java,v 1.2 2005/06/30 09:09:17 tcurley Exp $
 *
 * Copyright (C) 2002 - 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextField;




/**
 *
 *
 * @version	$Revision: 1.2 $, $Date: 2005/06/30 09:09:17 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class UserView {

    private UserViewPanel panel = null;
    private String identifier = null;
    private NavigationButton button = null;
    private ImageIcon icon = null;
    private ExchangerEditor parent = null;
    
    public UserView(JPanel panel, String identifier, ExchangerEditor parent) {
        
        this.panel = new UserViewPanel(panel);
        this.identifier = identifier;
        this.parent = parent;
        this.icon = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ExchangerEditorSmallIcon.gif");
        this.button = new NavigationButton(identifier, icon);
        this.button.setPreferredSize(new Dimension(this.button.getPreferredSize().width, 29));
        
        button.addItemListener(new UserViewItemListener());
        JTextField field = new JTextField();
        field.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

           
                
            }
            
        });
    }
    
    public UserView() {

    }
    
    
    /**
     * @return Returns the identifier.
     */
    public String getIdentifier() {

        return identifier;
    }
    /**
     * @param identifier The identifier to set.
     */
    public void setIdentifier(String identifier) {

        this.identifier = identifier;
    }
    /**
     * @return Returns the panel.
     */
    public UserViewPanel getPanel() {

        return panel;
    }
    /**
     * @param panel The panel to set.
     */
    public void setPanel(UserViewPanel panel) {

        this.panel = panel;
    }

    
    /**
     * @return Returns the button.
     */
    public NavigationButton getButton() {

        return button;
    }
    /**
     * @param button The button to set.
     */
    public void setButton(NavigationButton button) {

        this.button = button;
    }
    
    private class UserViewItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
			    ExchangerView view = parent.getView();
				ViewPanel current = view.getCurrentView();
				if (view != null) {
					try {
						parent.switchToUserView(UserView.this);
					} catch (Exception e) {
					    e.printStackTrace();
						MessageHandler.showMessage(	"Please ensure the document is well-formed\nbefore switching to the \"Grid\".");
						current.setFocus();
					}
				}
			}
		}
	}
    
    public class UserViewPanel extends ViewPanel {
            
        public UserViewPanel(JPanel newPanel) {
            super(new BorderLayout());
            this.add(newPanel, BorderLayout.CENTER);
        }
        
	    /* (non-Javadoc)
	     * @see com.cladonia.xngreditor.ViewPanel#setFocus()
	     */
	    public void setFocus() {
	
	        // TODO
	        this.revalidate();
	        this.repaint();
	        
	    }
	
	    /* (non-Javadoc)
	     * @see com.cladonia.xngreditor.ViewPanel#updatePreferences()
	     */
	    public void updatePreferences() {
	
	        // TODO
	        
	    }
	
	    /* (non-Javadoc)
	     * @see com.cladonia.xngreditor.ViewPanel#setProperties()
	     */
	    public void setProperties() {
	
	        // TODO
	        
	    }
	    	    
    }
}
