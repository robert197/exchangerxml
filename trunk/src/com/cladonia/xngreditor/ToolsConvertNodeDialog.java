/*
 * Id: 
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.bounce.FormLayout;

import com.cladonia.xngreditor.component.XPathFilterPanel;
import com.cladonia.xngreditor.properties.ConfigurationProperties;


/**
 * This dialog is used by the ToolsConvertNodeAction class,
 * To convert a node to another type, e.g. Element to attribute
 *
 * @version	$Revision: 1.9 $, $Date: 2004/11/04 19:21:49 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsConvertNodeDialog extends XngrDialog {

    private static final Dimension SIZE = new Dimension( 400, 315);
    private JFrame parent = null;
    private JPanel main = null;
    private ConfigurationProperties props = null;
    public JRadioButton toNewDocumentRadio;
    private JRadioButton toCurrentDocumentRadio;
    public JComboBox nodeTypeCombo;
    public XPathFilterPanel xpathPanel;
       

    /**
     * @param frame
     * @param modal
     */
    public ToolsConvertNodeDialog(JFrame frame,ConfigurationProperties props) {

        super( frame, true);
		super.setTitle("XML Tools",
				"Convert Nodes",
				"Enter Query and Convert To Details...");
		
		this.parent = frame;
		this.props = props;
				
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		xpathPanel = new XPathFilterPanel(frame,props,XPathFilterPanel.XPATH_MANDATORY);
		main.add(xpathPanel, BorderLayout.NORTH);
		main.add(this.buildGeneralPanel(),BorderLayout.CENTER);
			
	    setContentPane( main);
	    pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), (getSize().height+15)));
	    
    }
    
    /**
     * @return
     */
    private JPanel buildGeneralPanel() {

        
        JPanel main = new JPanel(new BorderLayout());
        
        JPanel newNs = new JPanel(new FormLayout(5,5));
        newNs.setBorder(new CompoundBorder(new TitledBorder("Convert To"),new EmptyBorder(0,5,5,5)));

        JLabel nodeTypeLabel = new JLabel("Node Type:");
        nodeTypeCombo = new JComboBox();
                
        nodeTypeCombo.removeAllItems();
        nodeTypeCombo.addItem("Element Node");
        nodeTypeCombo.addItem("Attribute Node");
        nodeTypeCombo.addItem("Text Node");
        nodeTypeCombo.addItem("CDATA Section Node");
        nodeTypeCombo.addItem("Processing Instruction Node");
        nodeTypeCombo.addItem("Comment Node");
        
        newNs.add(nodeTypeLabel,FormLayout.LEFT);
        newNs.add(nodeTypeCombo, FormLayout.RIGHT_FILL);
        
        JPanel general = new JPanel();
        general.setLayout(new FormLayout(5,5));
        
        toNewDocumentRadio = new JRadioButton("To New Document");
        toCurrentDocumentRadio = new JRadioButton("To Current Document");
        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(toNewDocumentRadio);
        radioGroup.add(toCurrentDocumentRadio);
        toCurrentDocumentRadio.setSelected(true);
        
        general.add(toNewDocumentRadio,FormLayout.FULL_FILL);
        general.add(toCurrentDocumentRadio,FormLayout.FULL_FILL);
        
        
        general.setBorder(new CompoundBorder(new TitledBorder("Output"),new EmptyBorder(0,5,5,5)));
        
        main.add(newNs,BorderLayout.NORTH);
        main.add(general,BorderLayout.CENTER);
                
        return (main);
    }
    
    


    public void show(String currentXPath) {
        xpathPanel.init(currentXPath);
        super.show();
    }
    
    public void okButtonPressed() {
    	
    	
        try {
            if(this.xpathPanel.xpathField.getSelectedItem().equals("")) {
            	MessageHandler.showError(parent,"Please Enter the XPath","Tools Convert Node By XPath");
            }
            else {
            	xpathPanel.saveAllProperties();
            	super.okButtonPressed();
            }
        }
        catch (NullPointerException e) {
            MessageHandler.showError(parent,"Please Enter the XPath","Tools Convert Node By XPath");
        }
    }
    
    
}
