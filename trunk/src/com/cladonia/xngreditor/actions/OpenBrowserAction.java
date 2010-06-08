/*
 * $Id: OpenBrowserAction.java,v 1.14 2004/09/06 14:47:24 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;

import org.bounce.util.BrowserLauncher;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.FlyweightProcessingInstruction;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XngrURLUtilities;
import com.cladonia.xml.transform.TransformerUtilities;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.URLUtilities;

/**
 * An action that can be used to open the current document in a browser.
 *
 * @version	$Revision: 1.14 $, $Date: 2004/09/06 14:47:24 $
 * @author Dogsbay
 */
public class OpenBrowserAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ExchangerEditor parent = null;
 	
 	private String XML_STYLESHEET = "xml-stylesheet";
	
 	/**
	 * The constructor for the copy action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public OpenBrowserAction( ExchangerEditor parent) {
 		super( "Start Browser");

		putValue( MNEMONIC_KEY, new Integer( 'B'));
		putValue( SHORT_DESCRIPTION, "Start the default internet browser");		
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F9, 0, false));

		
		this.parent = parent;
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		ExchangerDocument document = parent.getDocument();
		
		parent.getView().updateModel();
		
		if ( !document.isError()) {
			XElement root = document.getRoot();
			
			if ( root.getName().equalsIgnoreCase( "html") || document.getName().toLowerCase().endsWith( "htm") || document.getName().toLowerCase().endsWith( "html")) {
				writeOutputAsHTML( document);
			} else { 
			    //need to check if the XML has a stylesheet PI (css/xslt)
			    if(!checkForStylesheetPI(document)) {
			        
			        // normal XML, use the default stylesheet to convert to html
			        try {
			            URL url = document.getURL();
			            File file = new File( System.getProperty( "java.io.tmpdir"));
			            
			            if ( url != null && url.getProtocol().equals( "file")) {
			                file = new File( url.getFile());
			            }
			            
			            if ( !file.isDirectory()) {
			                file = file.getParentFile();
			            }
			            
			            File temp = null;
			            
			            try {
			                temp = File.createTempFile( "temp"+URLUtilities.getFileNameWithoutExtension( document.getName()), ".htm", file);
			            } catch ( IOException x) { 
			                // could not create file, try in temp dir...
			                temp = File.createTempFile( "temp"+URLUtilities.getFileNameWithoutExtension( document.getName()), ".htm", new File( System.getProperty( "java.io.tmpdir")));
			            }
			            
			            temp.deleteOnExit();
			            
			            FileOutputStream stream = new FileOutputStream( temp);
			            
			            TransformerUtilities.transform( document, stream, false);
			            stream.flush();
			            stream.close();
			            
			            url = XngrURLUtilities.getURLFromFile(temp);
			            URL newUrl = new URL( url.getProtocol(), "localhost", url.getFile());
			            
			            BrowserLauncher.openURL( URLUtilities.encodeURL( newUrl.toString()));
			        } catch ( Exception x) {
			            x.printStackTrace();
			        }
			    }
			    else {
			        //document has a processing instruction specifying a stylesheet,
			        //just show as is
			        writeOutputAsHTML(document);
//			      
			    }
			}
		} else { // error
			// always write the document as html
//			if ( document.getName().endsWith( "htm") || document.getName().endsWith( "html")) {
			writeOutputAsHTML( document);
//			}
			
		}
 	}
 	
 	private void writeOutputAsHTML( ExchangerDocument document) {
		URL url = document.getURL();
		File file = new File( System.getProperty( "java.io.tmpdir"));
		
		if ( url != null && url.getProtocol().equals( "file")) {
			file = new File( url.getFile());
		}

		if ( !file.isDirectory()) {
			file = file.getParentFile();
		}
 		
		try {
			File temp = null;

			try {
				temp = File.createTempFile( "temp"+URLUtilities.getFileNameWithoutExtension( document.getName()), ".htm", file);
			} catch ( IOException e) { 
				// could not create file, try in temp dir...
				temp = File.createTempFile( "temp"+URLUtilities.getFileNameWithoutExtension( document.getName()), ".htm", new File( System.getProperty( "java.io.tmpdir")));
			}
	
			temp.deleteOnExit();

			FileOutputStream stream = new FileOutputStream( temp);
			stream.write( document.getText().getBytes( document.getJavaEncoding()));
			stream.flush();
			stream.close();
	
			url = XngrURLUtilities.getURLFromFile(temp);

			URL newUrl = new URL( url.getProtocol(), "localhost", url.getFile());

			BrowserLauncher.openURL( URLUtilities.encodeURL( newUrl.toString()));
		} catch ( IOException e) {
			e.printStackTrace();
		}
 	}
 	
 	/**
 	 * Method to check if the document contains a processing intruction
 	 * which specifies a stylesheet. If it cannot find one declared at the beginning
 	 * it will walk the tree to see can it find one.
 	 * 
 	 * @param document
 	 * @return boolean true or false
 	 */
 	public boolean checkForStylesheetPI(ExchangerDocument document) {
 	   XDocument doc = document.getDocument();
 	   for(int cnt=0;cnt<doc.nodeCount();++cnt) {
 	       Node n = doc.node(cnt);
 	       if(n.getNodeType()==Node.PROCESSING_INSTRUCTION_NODE) {
 	           FlyweightProcessingInstruction pi = (FlyweightProcessingInstruction)n;
 	           if(pi.getTarget().equalsIgnoreCase(XML_STYLESHEET)) {
 	              return(true); 
 	           }
 	       }
 	       
 	   }
 	  return(treeWalk(document.getRoot()));
 	   
 	}
 	
 	/**
 	 * walks the tree to see if it can find a processing instruction 
 	 * relating to a stylesheet
 	 * @param element
 	 * @return boolean true or false
 	 */
 	public boolean treeWalk(XElement element) {
        for ( int i = 0, size = element.nodeCount(); i < size; i++ ) {
            Node node = element.node(i);
            if ( node instanceof Element ) {
                treeWalk( (XElement) node );
            }
            else {
                
                if(node.getNodeType()==Node.PROCESSING_INSTRUCTION_NODE) {
      	           FlyweightProcessingInstruction pi = (FlyweightProcessingInstruction)node;
      	           if(pi.getTarget().equalsIgnoreCase(XML_STYLESHEET)) {
      	              return(true);
      	           }
      	       }
            }
        }
        return(false);
    }
 	
 	 	 
}
