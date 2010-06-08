/*
 * $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.bounce.FormLayout;
import org.dom4j.Namespace;

import com.cladonia.xngreditor.component.XPathFilterPanel;
import com.cladonia.xngreditor.properties.ConfigurationProperties;


/**
 * Dialog used by the ToolsAddNodeToNamespaceAction class
 *
 * @version	$Revision: 1.10 $, $Date: 2004/11/04 19:21:49 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsAddNodeToNamespaceDialog extends XngrDialog {

    private static final Dimension SIZE = new Dimension( 400, 315);
    private JFrame parent = null;
    private JPanel main = null;
    private ConfigurationProperties props = null;
    public JRadioButton toNewDocumentRadio;
    private JRadioButton toCurrentDocumentRadio;
    public JTextField nsPrefixTextField;
    public JComboBox nsURICombo;
    private Vector namespaces;
    public XPathFilterPanel xpathPanel;
    

    /**
     * Constructor for the dialog
     * @param frame the parent frame
     * @param props exchangers configuration properties
     */
    public ToolsAddNodeToNamespaceDialog(JFrame frame,ConfigurationProperties props) {

        super( frame, true);
		super.setTitle("XML Tools",
				"Add Nodes to Namespace",
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
     * This method builds the panel for the general info about the new namespace
     * @return the general panel
     */
    private JPanel buildGeneralPanel() {

        
        JPanel main = new JPanel(new BorderLayout());
        
        JPanel newNs = new JPanel(new FormLayout(5,5));
        newNs.setBorder(new CompoundBorder(new TitledBorder("New Namespace"),new EmptyBorder(0,5,5,5)));
        
        JLabel nsPrefixLabel = new JLabel("Prefix");
        nsPrefixTextField = new JTextField();
        JLabel nsURILabel = new JLabel("URI");
        nsURICombo = new JComboBox();
        nsURICombo.setEditable(true);
        nsURICombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                String value = (String)nsURICombo.getSelectedItem();
                String prefix = getPrefixForURI(value);
                if(prefix!=null) {
                    nsPrefixTextField.setText(prefix);
                }
                else {
                    nsPrefixTextField.setText("None");
                }
            }
        });
        
        newNs.add(nsURILabel, FormLayout.LEFT);
        newNs.add(nsURICombo, FormLayout.RIGHT_FILL);
        newNs.add(nsPrefixLabel,FormLayout.LEFT);
        newNs.add(nsPrefixTextField, FormLayout.RIGHT_FILL);
        
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
    
    /**
     * This method shows the dialog and initialises the xpath combobox with
     * previously used xpaths
     */
    public void show(Vector namespaces, String currentXPath) {
        xpathPanel.init(currentXPath);
        this.namespaces = namespaces;
		this.fillNamespaceCombo(namespaces);
		
		
        super.show();
    }
    
    /**
     * This method overrides the the supers okButtonPressed to check if the fields are filled in
     * 
     */
    public void okButtonPressed() {
    	
    	
        try {
            if(this.xpathPanel.xpathField.getSelectedItem().equals("")) {
            	MessageHandler.showError(parent,"Please Enter the XPath","Tools Add Namespace By XPath");
            }
            else {
            	xpathPanel.saveAllProperties();
            	super.okButtonPressed();
            }
        }
        catch (NullPointerException e) {
            MessageHandler.showError(parent,"Please Enter the XPath","Tools Add Namespace By XPath");
        }
    }
    	
    private void fillNamespaceCombo (Vector namespaces) {
        nsURICombo.removeAllItems();
        nsURICombo.addItem("None");
        
        for(int cnt=0;cnt<namespaces.size();++cnt) {
            Namespace ns = (Namespace)namespaces.get(cnt);
            //String toAdd = ns.getPrefix()+":"+ns.getURI();
            nsURICombo.addItem(ns.getURI());
            
        }
        nsURICombo.setSelectedIndex(0);
        
    }
    
    private String getPrefixForURI (String uri) {
        
        for(int cnt=0;cnt<namespaces.size();++cnt) {
            Namespace ns = (Namespace)namespaces.get(cnt);
            if(ns.getURI().equalsIgnoreCase(uri)){
                return(ns.getPrefix());
            }
        }
        return(null);
    }
}
