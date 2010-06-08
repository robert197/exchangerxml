/*
 * $Id: XngrDialog.java,v 1.11 2004/10/08 13:50:32 tcurley Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;
import javax.swing.*;

import java.awt.*;
import org.bounce.QPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

 /**

 * @version	$Revision: 1.11 $, $Date: 2004/10/08 13:50:32 $
 * @author Thomas Curley <tcurley@cladonia.com>
 *
 * @version 0.9 - 03/09/2004
 * @version 0.8 - 11/08/2004
 * @version 0.7 - 03/08/2004
 * @version 0.6 - 20/07/2004
 * @version 0.5 - 14/07/2004
 * @version 0.4 - 13/07/2004
 * @version 0.3 - 12/07/2004
 * @version 0.2 - 05/07/2004
 * 
 * Reusable extension to JDialog
 * which includes a header to display a detailed description,
 * and a footer which contains an okButton and cancelButton.
 * It also handles the windowClose event
 * 
 * 0.2 - added recursive JPanel parsing - not fully tested beyond one level
 * 0.3 - when window closed by x button, window returns null, (same as cancel)
 * 0.4 - changed look of dialog to include qpanel gradient fill
 * 0.5 - developing extension to jdialog to all XngrDialog to be extended
 * 0.6 - developing header and removed all static classes
 * 	   - designed to be extended only
 * 0.7 - added some general enhancements
 * 0.8 - minor bug and look and feel fixes 
 * 0.9 - XngrDialog now extends XngrDialogHeader to allow more flexibility in other dialogs
 * 
 */
public class XngrDialog extends XngrDialogHeader {
	
	private JPanel main = null;
	private boolean useMnemonics = true;
       
    protected JButton okButton = null;
    protected JButton cancelButton = null;
    protected char okMnemonic = 'O';
    protected char cancelMnemonic = 'C';
        
    private JFrame frame = null;
    
    public static final boolean NO_MNEMONICS = false;
	
    
	/**
	 * Constructor for XngrDialog
	 * @param frame The parent of this dialog
	 * @param modal Is this dialog modal
	 */
	public XngrDialog(JFrame frame,boolean modal) {
		
		super(frame,modal);
		this.frame = frame;
		buildDialog();
				
	}
	
	/**
	 * Second contructor which allows you to specify whether the buttons will have mnemonics
	 * attached.
	 * 
	 * @param frame The parent frame
	 * @param modal Boolean as to whether the dialog is modal or not
	 * @param usingMnemonics Boolean as to whether to use default mnemonics for the ok and cancel button
	 */
	public XngrDialog(JFrame frame, boolean modal, boolean usingMnemonics) {
	    super(frame,modal);
		this.frame = frame;
		this.useMnemonics = usingMnemonics;
		buildDialog();
	}
	
	/**
	 * builds the dialog for the constructors
	 *
	 */
	public void buildDialog() {
	   
	   
		main = new JPanel();
		main.setLayout(new BorderLayout());
		QPanel footer = new QPanel();
		footer = this.buildFooter();
		main.add(footer,BorderLayout.SOUTH);
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                cancelButtonPressed();
            }
        });
		
		getRootPane().setDefaultButton(okButton);
		setDefaultCloseOperation( HIDE_ON_CLOSE);
	}
	
	/**
	 * Overrides this method to add the extension class to its center
	 * @see javax.swing.RootPaneContainer#setContentPane(java.awt.Container)
	 */
	public void setContentPane(Container contentPane){
	    main.add(contentPane,BorderLayout.CENTER);
	    if(this.useMnemonics) {
	        //set the button mnemonics
	        this.setMnemonics(this.getCharForMnemonic(okButton),'C');
	    }
	    super.setContentPane(main);
	  
	}
	
	
	/** overrides this method to allow the cancelled boolean be set to false
	 * @see java.awt.Component#show()
	 */
	public void show() {
	    
	    setLocationRelativeTo(frame);
	    cancelled = false;
	    super.show();
	}	
	
	/* (non-Javadoc)
	 * @see java.awt.Dialog#setVisible(boolean)
	 */	
	public void setVisible(boolean b) {
	
		if(b == true) {
			show();
		}
		else {
			hide();
		}
		
	}
	
	/**
	 * Builds the footer panel which contains the okButton and cancelButton
	 * and events to handle each of them being pressed
	 * @return The QPanel
	 */
	private QPanel buildFooter() {
	    QPanel footer = new QPanel();
	    footer.setLayout(new FlowLayout());
	    footer.setOpaque(true);
	    
	    //add buttons
	    okButton = new JButton("OK");
	    okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    okButtonPressed();
			}});
	    
	    getRootPane().setDefaultButton(okButton);
	    
	    cancelButton = new JButton("Cancel");
	    cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonPressed();
			}});
	    
	    
	    footer.add(okButton);
	    footer.add(cancelButton);
	    
	    return(footer);
	}
	
	
	/**
	 * The method which is called when the ok button is pressed
	 */
	protected void okButtonPressed() {
	    cancelled = false;
	    hide();
	}
	
	
	/**
	 * The method which is called when the cancel button is pressed
	 */
	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}
	
	
		
    /**
	 * When the dialog is cancelled and no selection has been made, 
	 * this method returns true.
	 *
	 * @return true when the dialog has been cancelled.
	 */
    public boolean isCancelled() {

        return cancelled;
    }
    /**
     * @param cancelled The cancelled to set.
     */
    public void setCancelled(boolean cancelled) {

        this.cancelled = cancelled;
    }
    
    /**
     * Gets the appropriate char for the mnemonic by searching through the button text
     * and finding the first char that isn't already used
     * 
     * @param button The button to set the mnemonic for
     * @return the mnemonic character
     */
    public char getCharForMnemonic(JButton button) {
        String title = button.getText();
        for(int cnt=0;cnt<title.length();++cnt) {
            //get the first char that isn't already used by the cancel button
            char workChar = title.charAt(cnt);
            if(workChar!=this.cancelMnemonic) {
                //set the ok mnemonic to this
                this.okMnemonic = workChar;
                return(workChar);
            }
                        
        }
        return('O');        
    }
    
    /**
     * Sets the mnemonic of the ok and cancel button
	 * also checks if the sub class has already assigned a mnemonic to the buttons
	 * and if so it skips them.
     * 
     * @param ok The new char for the okButton
     * @param cancel The new char for the cancelButton
     */
    public void setMnemonics(char ok,char cancel) {
        //System.out.println(okButton.getMnemonic());
        if(okButton.getMnemonic()==0) {
            okButton.setMnemonic(ok);
        }
        if(cancelButton.getMnemonic()==0) {
	        cancelButton.setMnemonic(cancel);
	    }
	    //turn off the automatic mnemonics
	    this.useMnemonics = false;
	
    }
    
    public void setSize(Dimension size) {
        super.setSize(size);
    }
    
   
}
