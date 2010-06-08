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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.bounce.FormLayout;

import com.cladonia.xngreditor.component.XPathFilterPanel;
import com.cladonia.xngreditor.properties.ConfigurationProperties;


/**
 * Dialog used by the ToolsRenameNodeAction class
 *
 * @version	$Revision: 1.9 $, $Date: 2004/11/04 19:21:49 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsRenameNodeDialog extends XngrDialog {

    private static final Dimension SIZE = new Dimension( 400, 315);
    private JFrame parent = null;
    private JPanel main = null;
    private ConfigurationProperties props = null;
    public JRadioButton toNewDocumentRadio;
    private JRadioButton toCurrentDocumentRadio;
    private JTextField newNameTextField;
    public XPathFilterPanel xpathPanel;

    /**
     * @param frame
     * @param modal
     */
    public ToolsRenameNodeDialog(JFrame frame,ConfigurationProperties props) {

        super( frame, true);
		super.setTitle("XML Tools",
				"Rename Nodes",
				"Enter Query and New Name...");
		
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
        
        JPanel general = new JPanel();
        general.setLayout(new FormLayout(5,5));
        
        JPanel newNs = new JPanel(new FormLayout(5,5));
        newNs.setBorder(new CompoundBorder(new TitledBorder("New Node Name"),new EmptyBorder(0,5,5,5)));
        
        JLabel newNameLabel = new JLabel("New Name:");
        newNameTextField = new JTextField();
        
        newNs.add(newNameLabel,FormLayout.LEFT);
        newNs.add(newNameTextField, FormLayout.RIGHT_FILL);
        
        
        
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
    	
    	
        if(this.xpathPanel.xpathField.getSelectedItem().equals("")) {
    		MessageHandler.showError(parent,"Please Enter the XPath","Tools Rename By XPath");
    	}
        else if(this.newNameTextField.getText().equals("")) {
    		MessageHandler.showError(parent,"Please Enter the New Name","Tools Rename By XPath");
    	}
    	else {
    	    xpathPanel.saveAllProperties();
    		super.okButtonPressed();
    	}
    }
    	
	public String getNewName() {
		return newNameTextField.getText();
	}
}
