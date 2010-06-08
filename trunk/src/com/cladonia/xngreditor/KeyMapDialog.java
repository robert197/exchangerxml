/*
 * $Id: KeyMapDialog.java,v 1.6 2004/10/27 13:26:16 edankert Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;

import org.bounce.FormLayout;

import com.cladonia.xngreditor.properties.KeyMap;
import com.cladonia.xngreditor.properties.Keystroke;




/**
 * The Key Map dialog.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/10/27 13:26:16 $
 * @author Dogs bay
 */
public class KeyMapDialog extends XngrDialog {
	
	private JFrame parent		= null;
	private JTextField nameField 	= null;
	private JPanel keymapPanel 		  = null;
	private JTextField commandField   = null;
	private JTextField sequenceField  = null;
	private JTextField newField  	  = null;
	private String actionName		  = null;
	private String description        = null;
	private Hashtable keyMaps         = null;
	private String keySequence		  = null;
	private final String DEBUGGER 	= "Debugger:";
	

	/**
	 * The Key Map dialog.
	 *
	 * @param parent the parent frame.
	 */
	public KeyMapDialog( JFrame parent) {
		super( parent, true, XngrDialog.NO_MNEMONICS);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Edit Key Sequence");
		setDialogDescription( "Specify the Key Sequence");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		//removed for xngr-dialog
		//okButton.setMnemonic();
		/*
		cancelButton = new JButton( "Cancel");
		//cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		okButton = new JButton( "OK");
		//okButton.setMnemonic( 'O');
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				updateButtonPressed();
			}
		});

		getRootPane().setDefaultButton( okButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 10, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);
		*/
		JPanel form = new JPanel( new FormLayout( 10, 2));

		// fill the panel...
		form.add( getKeyMapPanel(), FormLayout.FULL_FILL);

		main.add( form, BorderLayout.CENTER);
		//main.add( buttonPanel, BorderLayout.SOUTH);

		/*addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				hide();
			}
		});*/

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
	}
	
	
	/**
	 *  Called when the cancel button is pushed
	 */
	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}
	
	/**
	 *  Called when the ok button is pushed
	 */
	protected void okButtonPressed() 
	{
		keySequence = newField.getText();
		
		if (keySequence.equals("") || keySequence == null)
		{
			JOptionPane.showMessageDialog(this,"You have not entered a new Key Sequence","Error",JOptionPane.ERROR_MESSAGE);
			newField.setText("");
			return;
		}
		
		if (keySequence.endsWith("+"))
		{
			JOptionPane.showMessageDialog(this,"You have not entered a valid Key Sequence","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
	
		int plusSign =  keySequence.lastIndexOf("+");
		
		String mask = null;
		String value = null;
		if (plusSign != -1)
		{
			mask = keySequence.substring(0,plusSign);
			value = keySequence.substring(plusSign+1,keySequence.length());
		}
		else
		{
			value = keySequence;
		}
		
		Keystroke keystroke = new Keystroke(mask,value);
		
		String actionClash = checkForClash(keystroke);
		
		if (actionClash != null && !actionClash.equals(""))
		{
			int result = JOptionPane.showConfirmDialog(this,"This key sequence is already assigned \nto the "+
					"command \""+actionClash+"\"\n\nDo you want to override this assignment?",
					"Duplicate Key Assignment",
					JOptionPane.YES_NO_OPTION);
			
			if ( result == JOptionPane.NO_OPTION) 
			{
				return;
			} 
			else
			{
				// override existing keymap with a blank keystroke
				KeyMap keymapClash = (KeyMap)keyMaps.get(actionClash);
				String descriptionClash = keymapClash.getDescription();
				Keystroke blankKeystroke = new Keystroke(null,null);
				KeyMap keymapClashBlank = new KeyMap(actionClash,descriptionClash,blankKeystroke);
				
				// create the new keymap
				KeyMap keymap = new KeyMap(actionName,description,keystroke);
				
				//add both to the cache
				keyMaps.put(actionClash,keymapClashBlank);
				keyMaps.put(actionName,keymap);
			}
		}
		else
		{
			KeyMap keymap = new KeyMap(actionName,description,keystroke);
			
			//	update the cache
			keyMaps.put(actionName,keymap);
		}
		
		super.okButtonPressed();
	}
	
	
	/**
	 * Returns the new key sequence
	 *
	 * @return String The key sequence
	 */
	public String getKeySequence()
	{
		return keySequence;
	}
	
	private String checkForClash(Keystroke keystroke)
	{
		Enumeration actions = keyMaps.keys();
		while (actions.hasMoreElements())
		{
			String actionName = (String)actions.nextElement();
			KeyMap km = (KeyMap)keyMaps.get(actionName);
			
			// get keystroke
			Vector strokes = km.getKeystrokes();
			if (strokes.size() == 1)
			{
				Keystroke stroke = (Keystroke)strokes.get(0);
				if (stroke.equals(keystroke))
				{
					if (!actionName.startsWith(DEBUGGER) && !this.actionName.startsWith(DEBUGGER))
					{
						// we have a clash, both editor keys
						return actionName;
					}
					if (actionName.startsWith(DEBUGGER) && this.actionName.startsWith(DEBUGGER))
					{
						// we have a clash, bothe debugger keys
						return actionName;
					}
					else
					{
						// not a clas do nothing
					}
					
					
				}
			}
		}
		
		return null;
	}

	private JPanel getKeyMapPanel() {
		if ( keymapPanel == null) {
			keymapPanel = new JPanel( new FormLayout( 5, 10));
			keymapPanel.setBorder(new EmptyBorder( 5, 5, 5, 5));
			
			commandField  = new JTextField();
			commandField.setEditable(false);
			commandField.setBackground(keymapPanel.getBackground());
			JLabel commandLabel = new JLabel( "Command Name:");
			keymapPanel.add( commandLabel, FormLayout.LEFT);
			keymapPanel.add( commandField, FormLayout.RIGHT_FILL);
			
			sequenceField  = new JTextField();
			sequenceField.setEditable(false);
			sequenceField.setBackground(keymapPanel.getBackground());
			JLabel sequenceLabel = new JLabel( "Current Key Sequence:");
			keymapPanel.add( sequenceLabel, FormLayout.LEFT);
			keymapPanel.add( sequenceField, FormLayout.RIGHT_FILL);
			
			newField  = new JTextField();
			JLabel newLabel = new JLabel( "New Key Sequence:");
			keymapPanel.add( newLabel, FormLayout.LEFT);
			keymapPanel.add( newField, FormLayout.RIGHT_FILL);
	
			newField.addKeyListener(new KeyListener(){
				
				 /** Handle the key typed event from the text field. */
			    public void keyTyped(KeyEvent e) {
			        //System.out.println("KEY TYPED: "+e.toString());
			    }

			    /** Handle the key pressed event from the text field. */
			    public void keyPressed(KeyEvent e) {
			    	
			    	int modifiers = e.getModifiersEx();
			        final String modString = KeyEvent.getModifiersExText(modifiers);
			    	
			    	int keyCode = e.getKeyCode();
		            final String keyText = KeyEvent.getKeyText(keyCode);
		            
		            if (keyText.equalsIgnoreCase("DELETE") || keyText.equalsIgnoreCase("BACKSPACE"))
		            {
		            	newField.setText("");
		            	newField.setHorizontalAlignment(JTextField.LEFT);
		            	return;
		            }
		                          
			        if (modString.indexOf(keyText) != -1)
			        {
			        	SwingUtilities.invokeLater(new Runnable() 
			        	{ 
			    			public void run() 
			    			{ 
			    				newField.setText(modString+"+");
			    			} 
			        	}); 
			        }
			        else if (modString != null && !modString.equals(""))
			        {
			        	SwingUtilities.invokeLater(new Runnable() 
			        	{ 
			    			public void run() 
			    			{ 
			    				newField.setText(modString+"+"+keyText);
			    			} 
			        	}); 
			        	
			        }
			        else
			        {
			        	SwingUtilities.invokeLater(new Runnable() 
			        	{ 
			    			public void run() 
			    			{ 
			    				newField.setText(keyText);
			    			} 
			        	}); 
			        }
			    }

			    /** Handle the key released event from the text field. */
			    public void keyReleased(KeyEvent e) {
			    	
			    	int modifiers = e.getModifiersEx();
			        final String modString = KeyEvent.getModifiersExText(modifiers);
			    	
			    	int keyCode = e.getKeyCode();
		            final String keyText = KeyEvent.getKeyText(keyCode);
		            
		            String current = newField.getText();
		            
		            if (!current.endsWith("+"))
		            {
		            	return;
		            }
		            
		            if (current.equalsIgnoreCase(keyText+"+"))
		            {
		            	SwingUtilities.invokeLater(new Runnable() 
			        	{ 
			    			public void run() 
			    			{ 
			    				newField.setText("");
			    			} 
			        	}); 
		            	
		            }
		            else if (current.indexOf(keyText) != -1)
		            {
		            	if (modString != null && !modString.equals(""))
						{
		            		SwingUtilities.invokeLater(new Runnable() 
    			        	{ 
    			    			public void run() 
    			    			{ 
    			    				newField.setText(modString+"+");
    			    			} 
    			        	}); 
						}
		            }
		            else
		            {
		            	// leave the current text
		            }
			    }
			});
		}

		return keymapPanel;
	}
	
	/**
	 * Displays the dialog
	 *
	 * @param configName The configuration name
	 * @param actionName The action name
	 * @param description The action description
	 * @param sequence The current key sequence
	 */
	public void show(String configName, String actionName, String description,String sequence,
			Hashtable keyMaps) 
	{	
		this.actionName = actionName;
		this.description = description;
		this.keyMaps = keyMaps;
		
		// set the values that the dialog needs to display
		setCurrentValues(actionName, sequence);
		
		pack();
		setSize( new Dimension( 360, getSize().height));

		setLocationRelativeTo( parent);

		super.show();
	}
	
	/**
	 * Sets the correct when the dialog is displayed
	 *
	 * @param actionName The action name
	 * @param sequence The current key sequence
	 */
	private void setCurrentValues(String actionName,String sequence)
	{
		newField.setText("");
		
		commandField.setText(actionName);
		sequenceField.setText(sequence);
		
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() 
			{ 
				newField.requestFocus();
				newField.setHorizontalAlignment(JTextField.LEFT);
			} 
		} ); 
	}
}
