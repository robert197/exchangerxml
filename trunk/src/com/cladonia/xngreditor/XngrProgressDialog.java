/*
 * $Id: XngrProgressDialog.java,v 1.1 2005/08/31 09:18:42 tcurley Exp $
 *
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import org.bounce.QPanel;


/**
 * 
 *
 * @version	$Revision: 1.1 $, $Date: 2005/08/31 09:18:42 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class XngrProgressDialog extends XngrDialogHeader {

	private static final Dimension SIZE = new Dimension( 400, 150);
	public JProgressBar monitor = null;
	public JLabel label;
	private JButton cancelButton;
	/**
	 * Initialise the class XngrProgressDialog.java
	 * @param frame
	 * @param modal
	 */
	public XngrProgressDialog(JFrame parent, boolean modal) {

		super(parent, modal);
		
		monitor = new JProgressBar();
		label = new JLabel("Searching");
		
		JPanel main = new JPanel(new BorderLayout());
		JPanel progressPanel = new JPanel(new BorderLayout());
		
		progressPanel.add(label, BorderLayout.NORTH);
		progressPanel.add(monitor, BorderLayout.CENTER);
		
		progressPanel.setBorder(new EmptyBorder(5,5,5,5));
		
		main.add(progressPanel, BorderLayout.CENTER);
		main.add(buildFooter(), BorderLayout.SOUTH);
		
		setContentPane( main);
		
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);

		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), Math.max( SIZE.height, getSize().height)));

		setLocationRelativeTo( parent);
	}
	
	private QPanel buildFooter() {
	    QPanel footer = new QPanel();
	    footer.setLayout(new FlowLayout());
	    footer.setOpaque(true);
	    
	    //add buttons
	    	    
	    cancelButton = new JButton("Cancel");
	    cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonPressed();
			}});
	    

	    getRootPane().setDefaultButton(cancelButton);
	    
	    
	    footer.add(cancelButton);
	    
	    return(footer);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean b) {
	
		if(b == true) {
			
			
		}
		else {
			monitor.setIndeterminate(false);
		}
		super.setVisible(b);
	}
	
	public void setVisible(int min, int max) {
		remakeMonitor(min, max);
		
		super.setVisible(true);
	}
	
	public void incrementMonitor(int value) {
		if(value > -1) {
			this.monitor.setValue(this.monitor.getValue()+value);
		}
	}

	public void remakeMonitor(int min, int max) {
		this.monitor = new JProgressBar(min, max);
		this.monitor.setStringPainted(true);
		this.monitor.setValue(0);
	}
	
	public boolean isCancelled() {
		return(super.cancelled);
	}
}
