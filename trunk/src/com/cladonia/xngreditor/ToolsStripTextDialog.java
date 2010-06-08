/*
 * $Id: ToolsStripTextDialog.java,v 1.11 2004/11/04 19:21:49 edankert Exp $ 
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */

package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
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
 * Dialog used by the ToolsStripTextAction class
 *
 * @version	$Revision: 1.11 $, $Date: 2004/11/04 19:21:49 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsStripTextDialog extends XngrDialog {

    private static final Dimension SIZE = new Dimension( 400, 315);
		
	private JPanel main = null;
    public JCheckBox elementsCheckBox;
    public JCheckBox attributesCheckBox;
    public JCheckBox mixedContentCheckBox;
    public JRadioButton toNewDocumentRadio;
    public JRadioButton toCurrentDocumentRadio;
    private JFrame parent;
    private ConfigurationProperties props;
    public XPathFilterPanel xpathPanel; 
        
    /**
     * @param frame
     * @param modal
     */
    public ToolsStripTextDialog(JFrame frame,ConfigurationProperties props) {

        super(frame,true);
	    super.setTitle("XML Tools",
				"Strip Text...",
				"Choose the various options...");
	    
	    this.parent = frame;
	    this.props = props;
	    main = new JPanel();
	    main.setLayout(new BorderLayout());
	    
	    JPanel optionsPanel = this.buildOptionsPanel();
	    JPanel generalPanel = this.buildGeneralPanel();
	    xpathPanel = new XPathFilterPanel(frame,props,XPathFilterPanel.XPATH_OPTIONAL,XPathFilterPanel.DONT_ALLOW_TRAVERSE_CHILDREN);
	    
	    main.add(optionsPanel,BorderLayout.NORTH);
	    main.add(xpathPanel,BorderLayout.CENTER);
	    main.add(generalPanel,BorderLayout.SOUTH);
	    
	    main.setBorder(new EmptyBorder(5,5,5,5));
	    
		
	    setContentPane( main);
	    setDefaultCloseOperation( HIDE_ON_CLOSE);
	    pack();
	    setSize( new Dimension( Math.max( SIZE.width, getSize().width), (getSize().height+15)));
    }
        
    /**
     * @return
     */
    private JPanel buildOptionsPanel() {

        JPanel options = new JPanel();
        options.setLayout(new FormLayout());
                       
        elementsCheckBox = new JCheckBox("Strip Element Values");
        
        elementsCheckBox.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
			    
		        mixedContentCheckBox.setEnabled(elementsCheckBox.isSelected());
			    mixedContentCheckBox.setSelected(false);
			    
			}
			
        });
                
        attributesCheckBox = new JCheckBox("Strip Attribute Values");
        JLabel blankTab = new JLabel("      ");
        mixedContentCheckBox = new JCheckBox("Strip Mixed Content Elements");
        mixedContentCheckBox.setEnabled(false);
        
        //set it initially to strip element text
        elementsCheckBox.setSelected(true);
        
        options.add(elementsCheckBox,FormLayout.FULL_FILL);
        options.add(blankTab,FormLayout.LEFT);
        options.add(mixedContentCheckBox,FormLayout.RIGHT_FILL);
        options.add(attributesCheckBox,FormLayout.FULL_FILL);
        options.setBorder(new CompoundBorder(new TitledBorder("Options"),new EmptyBorder(0,5,5,5)));
        return(options);
    }
    
    /**
     * @return
     */
    private JPanel buildGeneralPanel() {

        JPanel general = new JPanel();
        general.setLayout(new FormLayout());
        
        toNewDocumentRadio = new JRadioButton("To New Document");
        toCurrentDocumentRadio = new JRadioButton("To Current Document");
        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(toNewDocumentRadio);
        radioGroup.add(toCurrentDocumentRadio);
        toCurrentDocumentRadio.setSelected(true);
               
        general.add(toNewDocumentRadio,FormLayout.FULL_FILL);
        general.add(toCurrentDocumentRadio,FormLayout.FULL_FILL);
        
        
        general.setBorder(new CompoundBorder(new TitledBorder("Output"),new EmptyBorder(0,5,5,5)));
        
        return (general);
    }


    public void show(String currentXPath) {
		xpathPanel.init(currentXPath);
        super.show();
    }
    
    protected void okButtonPressed() {
        
        if(this.elementsCheckBox.isSelected()||this.attributesCheckBox.isSelected()) {
            
            //if xpath was selected
            if(xpathPanel.xpathBox.isSelected()) {
                try {
                    String value = (String)xpathPanel.xpathField.getSelectedItem();
                    if(!value.trim().equals("")) {
                        
                        xpathPanel.saveAllProperties();
                        super.okButtonPressed();
                    }
                    else {
                        MessageHandler.showError(parent,"Please Enter the XPath Expression","Tools Strip Text");
                    }
                }catch (NullPointerException e) {
                    MessageHandler.showError(parent,"Please Enter the XPath Expression","Tools Strip Text");
                }
            }
            else {
                super.okButtonPressed();
            }
        }
        else {
            //elements or attributes were not selected
            MessageHandler.showError(parent,"Please select to strip either elements or attributes","Tools Strip Text");
        }
        
    }
        
}
