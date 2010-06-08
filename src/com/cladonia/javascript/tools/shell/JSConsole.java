/*
 * Created on 24-Mar-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.javascript.tools.shell;

import com.cladonia.xngreditor.ExchangerEditor;

import java.awt.BorderLayout;
import java.awt.event.*;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author gmcgoldrick
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
  public class JSConsole extends JPanel {
 
	private ExchangerEditor parent = null;
    private ConsoleTextArea consoleTextArea;
    private String [] origArgs= new String [] {};
    private Thread thread = null;

	public JSConsole( ExchangerEditor _parent) {
		super( new BorderLayout());
		
		this.parent = _parent;
    
		consoleTextArea = new ConsoleTextArea(null);
        JScrollPane scroller = new JScrollPane(consoleTextArea);
        //setContentPane(scroller);
        add(scroller);
        consoleTextArea.setRows(24);
        consoleTextArea.setColumns(80);
        consoleTextArea.setVisible( true);
        //consoleTextArea.setText( "Console>");

        
        addFocusListener(new FocusListener() {

          public void focusGained(FocusEvent e) {

//System.out.println( "JSConsole FocusListener.focusGained()");
         }

          public void focusLost(FocusEvent e) {

//System.out.println( "JSConsole FocusListener.focusLost()");
          }
      });

        
        Main.setIn(consoleTextArea.getIn());
        Main.setOut(consoleTextArea.getOut());
        Main.setErr(consoleTextArea.getErr());
        
        //Main.main(origArgs);

        
 		Runnable runner = new Runnable() {
			public void run()  {
				try{
			       Main.exec(origArgs, parent.getExchangerContext());				  
				}
				catch (Exception ex) {}
				
			}
 		};
 		
 		thread = new Thread( runner);
 		thread.start();

        
	}
	
	public void startScripting()
	{
	  consoleTextArea.startScripting();

	  
	}
}
