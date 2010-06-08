/*
 * $Id: XPathFilterPanel.java,v 1.2 2004/10/26 07:59:32 tcurley Exp $ 
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */

package com.cladonia.xngreditor.component;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.bounce.FormLayout;

import com.cladonia.xngreditor.PrefixNamespaceMappingPanel;
import com.cladonia.xngreditor.properties.ConfigurationProperties;


/**
 * Panel used for the XPath predicate and mapping panel in dialogs 
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/26 07:59:32 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class XPathFilterPanel extends JPanel {
    
    public static boolean ALLOW_TRAVERSE_CHILDREN = true;
    public static boolean DONT_ALLOW_TRAVERSE_CHILDREN = false;
    public static boolean XPATH_MANDATORY = false;
    public static boolean XPATH_OPTIONAL = true;
    
    public PrefixNamespaceMappingPanel mappingPanel;
    private JLabel preNamespaceMappingLabel;
    private ConfigurationProperties props;
    public JCheckBox traverseChildrenBox;
    public JCheckBox xpathBox;

    public JComboBox xpathField;
    private JLabel xpathLabel;

    
    /**
     * Basic contructor
     * @param parent The Parent frame
     * @param props xngr's configuration properties
     */
    public XPathFilterPanel(JFrame parent,ConfigurationProperties props) {
        
        this(parent,props,XPATH_MANDATORY,DONT_ALLOW_TRAVERSE_CHILDREN);
    }
    
    /**
     * Second constructor allow the choice of a checkbox beside the xpath label
     * i.e. Use XPath functionality
     * @param parent The parent frame
     * @param props xngr's configuration properties
     * @param xpathOption is the xpath optional or mandatory for the dialog
     */
    public XPathFilterPanel(JFrame parent,ConfigurationProperties props, boolean xpathOption) {
        
        this(parent,props,xpathOption,DONT_ALLOW_TRAVERSE_CHILDREN);
    }
    
    /**
     * Third constructor allow the choice of a checkbox beside the xpath label
     * i.e. Use XPath functionality
     * And the choice to show a traverse children checkbox
     * @param parent The parent frame
     * @param props xngr's configuration properties
     * @param xpathOption is the xpath optional or mandatory for the dialog
     * @param traverseChildren to show the traverse children checkbox or not
     */
    public XPathFilterPanel(JFrame parent,ConfigurationProperties props,boolean optional,boolean traverseChildren) {

        super(new BorderLayout());
        this.props = props;
        JPanel xpathPanel = new JPanel( new FormLayout( 5, 5));
		xpathPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "XPath"),
									new EmptyBorder( 0, 5, 5, 5)));
		
		preNamespaceMappingLabel = new JLabel( "Prefix Namespace Mapping:");
		mappingPanel = new PrefixNamespaceMappingPanel(parent,props,3);
		
		// the xpath prediate combo, comtains previous predicates used
		xpathField = new JComboBox();
		xpathField.setFont( xpathField.getFont().deriveFont( Font.PLAIN));
		xpathField.setEditable(true);

		//the traverse through children option
		traverseChildrenBox = new JCheckBox("Process Sub Elements");
		
		
		
		//used if xpath is an option in the dialog box
		xpathBox = new JCheckBox("Use XPath Filter:");
		xpathBox.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				xpathField.setEnabled( xpathBox.isSelected());
				xpathPanelSetEnabled(xpathBox.isSelected());
			}
		});
		
		//used if xpath is mandatory in the dialog box
		xpathLabel = new JLabel("XPath:");
		
		if(optional == XPATH_OPTIONAL) {
		    xpathField.setEnabled( false);
			xpathPanelSetEnabled(false);
		    xpathPanel.add(xpathBox, FormLayout.LEFT);
		}
		else if(optional == XPATH_MANDATORY) {
		    xpathField.setEnabled( true);
			xpathPanelSetEnabled(true);
		    xpathPanel.add(xpathLabel, FormLayout.LEFT);
		}
		
		xpathPanel.add(xpathField, FormLayout.RIGHT_FILL);
		if(traverseChildren) {
		    xpathPanel.add(traverseChildrenBox, FormLayout.LEFT);
		}
		xpathPanel.add( new JLabel( " "), FormLayout.FULL_FILL);
		xpathPanel.add(preNamespaceMappingLabel , FormLayout.FULL_FILL);
		xpathPanel.add(mappingPanel, FormLayout.FULL_FILL);
		this.add(xpathPanel,BorderLayout.CENTER);
		
    }
    
    /**
     * This method returns the user-entered xpath predicate from the combobox
     * @return The user-entered xpath predicate
     */
    public String getXpathPredicate()
	{
		return (String)xpathField.getSelectedItem(); 
	}   
    
    /**
     * The init method is called on showing this panel
     * It initialises the combobox with all predicates from the xpath history
     * and adds the currently selected on in the editor
     * @param currentXPath The current xpath selected in the editor
     */
    public void init(String currentXPath) {
        mappingPanel.init();
        setPredicateHistory(currentXPath);
	}

    /**
     * Saves all properties, including:
     * 	The namespace mapping panel and the xpath predicates
     */
    public void saveAllProperties() {
        props.addXPathPredicate(getXpathPredicate());
        mappingPanel.save();
    }
    
    /**
     * Adds the currently selected xpath in the editor to the xpath combo box
     * @param currentXPath the currently selected xpath in the editor
     */
    private void setPredicateHistory(String currentXPath)
	{
        Vector predicateHistory = props.getXPathPredicates();
        xpathField.removeAllItems();
		xpathField.addItem(currentXPath);
		for (int i=0;i<predicateHistory.size();i++)
		{
			xpathField.addItem((String)predicateHistory.get(i));
		}
		xpathField.setSelectedIndex(0);
	}
    
    /**
     * Enables and disables the various options in the xpath panel
     * @param value the value of the xpath checkbox
     */
    private void xpathPanelSetEnabled(boolean value) {
        if(value==true) {
            xpathField.setEnabled(true);
            preNamespaceMappingLabel.setEnabled(true);
            mappingPanel.setEnabled(true);
            traverseChildrenBox.setEnabled(true);
        }
        else {
            xpathField.setEnabled(false);
            preNamespaceMappingLabel.setEnabled(false);
            mappingPanel.setEnabled(false);
            traverseChildrenBox.setEnabled(false);
            
        }
    }
    
}
