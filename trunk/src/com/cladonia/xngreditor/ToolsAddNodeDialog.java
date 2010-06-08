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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.bounce.FormLayout;
import org.dom4j.Namespace;

import com.cladonia.xngreditor.component.XPathFilterPanel;
import com.cladonia.xngreditor.properties.ConfigurationProperties;


/**
 * Dialog used by the ToolsAddNodeAction class
 *
 * @version	$Revision: 1.12 $, $Date: 2004/11/04 19:21:49 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsAddNodeDialog extends XngrDialog {

    private static final Dimension SIZE = new Dimension( 400, 315);
    private JFrame parent = null;
    private JPanel main = null;
    private ConfigurationProperties props = null;
    public JRadioButton toNewDocumentRadio;
    private JRadioButton toCurrentDocumentRadio;
    public JTextArea valueTextField;
    public JComboBox nodeTypeCombo;
    public JTextField nameTextField;
    public JComboBox namespaceCombo;
    public JLabel prefixLabel;
    public JTextField prefixTextField;
    private Vector namespaces;
    public XPathFilterPanel xpathPanel;

    /**
     * @param frame
     * @param modal
     */
    public ToolsAddNodeDialog(JFrame frame,ConfigurationProperties props) {

        super( frame, true);
		super.setTitle("XML Tools",
				"Add Nodes",
				"Enter Query and New Node Details...");
		
		this.parent = frame;
		this.props = props;
				
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		xpathPanel = new XPathFilterPanel(frame,props,XPathFilterPanel.XPATH_MANDATORY);
		main.add(xpathPanel, BorderLayout.NORTH);
		main.add(this.buildGeneralPanel(),BorderLayout.CENTER);
		main.add(this.buildOutputPanel(),BorderLayout.SOUTH);
		
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
        newNs.setBorder(new CompoundBorder(new TitledBorder("New Node Details"),new EmptyBorder(0,5,5,5)));

        JLabel nodeTypeLabel = new JLabel("Node Type:");
        nodeTypeCombo = new JComboBox();
                
        nodeTypeCombo.removeAllItems();
        nodeTypeCombo.addItem("Element Node");
        nodeTypeCombo.addItem("Attribute Node");
        nodeTypeCombo.addItem("Text Node");
        nodeTypeCombo.addItem("CDATA Section Node");
        nodeTypeCombo.addItem("Processing Instruction Node");
        nodeTypeCombo.addItem("Comment Node");
                
        
        
        
        JPanel namespacePanel = new JPanel(new FormLayout(5,5));
        namespacePanel.setBorder(new CompoundBorder(new TitledBorder("Namespace Details"),new EmptyBorder(0,5,5,5)));
        
        final JLabel namespaceLabel = new JLabel("URI:");
        namespaceCombo = new JComboBox();
        namespaceCombo.setEditable(true);
                
        namespaceCombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                String value = (String)namespaceCombo.getSelectedItem();
                String prefix = getPrefixForURI(value);
                if(prefix!=null) {
                    prefixTextField.setText(prefix);
                }
                else {
                    prefixTextField.setText("None");
                }
            }
        });
        
        prefixLabel = new JLabel("Prefix:");
        prefixTextField = new JTextField();
        
        namespacePanel.add(namespaceLabel,FormLayout.LEFT);
        namespacePanel.add(namespaceCombo,FormLayout.RIGHT_FILL);
        namespacePanel.add(prefixLabel,FormLayout.LEFT);
        namespacePanel.add(prefixTextField,FormLayout.RIGHT_FILL);
                
        final JLabel nameLabel = new JLabel("Name:");
        nameTextField = new JTextField();
        
        JLabel valueLabel = new JLabel("Value:");
        valueTextField = new JTextArea(2,25);
        JScrollPane scroll = new JScrollPane(valueTextField);
        
        newNs.add(nodeTypeLabel,FormLayout.LEFT);
        newNs.add(nodeTypeCombo, FormLayout.RIGHT_FILL);
                
        newNs.add(nameLabel,FormLayout.LEFT);
        newNs.add(nameTextField, FormLayout.RIGHT_FILL);
        
        newNs.add(valueLabel,FormLayout.LEFT);
        newNs.add(scroll, FormLayout.RIGHT_FILL);
                
        nodeTypeCombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                String value = (String)nodeTypeCombo.getSelectedItem();
                
                if(value.equalsIgnoreCase("Element Node")) {
                    namespaceLabel.setEnabled(true);
                    namespaceCombo.setEnabled(true);
                    nameLabel.setEnabled(true);
                    nameTextField.setEnabled(true);
                    nameTextField.setText("");
                    nameLabel.setText("Name:");
                    prefixLabel.setEnabled(true);
                    prefixTextField.setEnabled(true);
                    
                    
                }
                else if(value.equalsIgnoreCase("Attribute Node")) {
                    namespaceLabel.setEnabled(true);
                    namespaceCombo.setEnabled(true);
                    nameLabel.setEnabled(true);
                    nameTextField.setEnabled(true);
                    nameTextField.setText("");
                    nameLabel.setText("Name:");
                    prefixLabel.setEnabled(true);
                    prefixTextField.setEnabled(true);
                    
                }
                else if(value.equalsIgnoreCase("Text Node")) {
                    namespaceLabel.setEnabled(false);
                    namespaceCombo.setEnabled(false);
                    nameLabel.setEnabled(false);
                    nameTextField.setEnabled(false);
                    nameTextField.setText("None");
                    nameLabel.setText("Name:");
                    prefixLabel.setEnabled(false);
                    prefixTextField.setEnabled(false);
                    
                }
                else if(value.equalsIgnoreCase("CDATA Section Node")) {
                    namespaceLabel.setEnabled(false);
                    namespaceCombo.setEnabled(false);
                    nameLabel.setEnabled(false);
                    nameTextField.setEnabled(false);
                    nameTextField.setText("None");
                    nameLabel.setText("Name:");
                    prefixLabel.setEnabled(false);
                    prefixTextField.setEnabled(false);
                    
                }
                else if(value.equalsIgnoreCase("Processing Instruction Node")) {
                    namespaceLabel.setEnabled(false);
                    namespaceCombo.setEnabled(false);
                    nameLabel.setEnabled(true);
                    nameTextField.setEnabled(true);
                    nameTextField.setText("");
                    nameLabel.setText("Target:");
                    prefixLabel.setEnabled(false);
                    prefixTextField.setEnabled(false);
                    
                }
                else if(value.equalsIgnoreCase("Comment Node")) {
                    namespaceLabel.setEnabled(false);
                    namespaceCombo.setEnabled(false);
                    nameLabel.setEnabled(false);
                    nameTextField.setEnabled(false);
                    nameTextField.setText("None");
                    nameLabel.setText("Name:");
                    prefixLabel.setEnabled(false);
                    prefixTextField.setEnabled(false);
                    
                }
            }
            
        });
        
        
        main.add(newNs,BorderLayout.NORTH);
        main.add(namespacePanel,BorderLayout.CENTER);
        //main.add(general,BorderLayout.SOUTH);
                
        return (main);
    }
    
    public JPanel buildOutputPanel() {
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
        
        return(general);
    }


    public void show(Vector namespaces, String currentXPath) {
        xpathPanel.init(currentXPath);
		this.namespaces = namespaces;
		this.fillNamespaceCombo(namespaces);
		
        super.show();
    }
    
    public void okButtonPressed() {
    	
    	
        try {
            if(this.xpathPanel.xpathField.getSelectedItem().equals("")) {
            	MessageHandler.showError(parent,"Please Enter the XPath","Tools Add Node By XPath");
            }
            else  if(this.nameTextField.getText().equals("")) {
            	MessageHandler.showError(parent,"Please Enter The New Node Name","Tools Add Node By XPath");
            }
            /*else  if(this.valueTextField.getText().equals("")) {
            	MessageHandler.showError(parent,"Please Enter The New Node Value","Tools Add Node By XPath");
            }*/
            else {
            	//props.addXPath(this.getXpathPredicate());
            	xpathPanel.saveAllProperties();
            	super.okButtonPressed();
            }
        }
        catch (NullPointerException e) {
            MessageHandler.showError(parent,"Please Enter the XPath","Tools Add Node By XPath");
        }
    }
    
    private void fillNamespaceCombo (Vector namespaces) {
        namespaceCombo.removeAllItems();
        namespaceCombo.addItem("None");
        
        for(int cnt=0;cnt<namespaces.size();++cnt) {
            Namespace ns = (Namespace)namespaces.get(cnt);
            //String toAdd = ns.getPrefix()+":"+ns.getURI();
            namespaceCombo.addItem(ns.getURI());
            
        }
        namespaceCombo.setSelectedIndex(0);
        
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
