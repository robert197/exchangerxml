/*
 * $Id: FormatAction.java,v 1.6 2004/06/01 10:17:46 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.ExchangerOutputFormat;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xml.editor.EditorProperties;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * Formats the editors text.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/06/01 10:17:46 $
 * @author Dogsbay
 */
 public class FormatAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private Editor editor = null;
	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;

 	/**
	 * The constructor for the action which formats the 
	 * editors text.
	 *
	 * @param editor the editor pane.
	 */
 	public FormatAction( ExchangerEditor parent, ConfigurationProperties properties) {
		super( "Format");
		
		this.parent = parent;
		this.properties = properties;
		
		putValue( MNEMONIC_KEY, new Integer( 'o'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F4, 0, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/Format16.gif"));
		putValue( SHORT_DESCRIPTION, "Format");
		
		super.setEnabled( false);
 	}
 	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		if ( view instanceof Editor) {
			editor = (Editor)view;
		} else {
			editor = null;
		}
		
		setDocument( parent.getDocument());
	}

	public void setDocument( ExchangerDocument doc) {
		if ( doc != null && doc.isXML()) {
			setEnabled( editor != null);
		} else {
			setEnabled( false);
		}
	}
	
	
//	public void setEnabled( boolean enabled) {
//		if ( editor != null) {
//			super.setEnabled( enabled);
//		}
//	}

	/**
	 * The implementation of the format action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "FormatAction.actionPerformed( "+event+")");
		
		parent.setWait( true);
		parent.setStatus( "Formatting ...");

		// Run in Thread!!!
		Runnable runner = new Runnable() {
			public void run()  {
				try {
					ExchangerDocument document = parent.getDocument();
					
					parent.getView().updateModel();

					String text = document.getText();
					
					String encoding = document.getEncoding();
					URL url = document.getURL();
					String systemId = null;
					
					if ( url != null) {
						systemId = url.toString();
					}
					
					ExchangerOutputFormat format = new ExchangerOutputFormat();
					format.setEncoding( encoding);
					
					if ( document.hasDeclaration()) {
						if ( document.getStandalone() != ExchangerDocument.STANDALONE_NONE) {
							format.setStandalone( document.getStandalone());
							format.setOmitStandalone( false);
						}
						
						format.setVersion( document.getVersion());
						format.setOmitEncoding( !document.hasEncoding());
						format.setSuppressDeclaration( false);
					} else {
						format.setSuppressDeclaration( true);
					}

					final String result = format( text, encoding, systemId, format);

			 		SwingUtilities.invokeLater( new Runnable(){
						public void run() {
							editor.setText( result);
							parent.getView().updateModel();
						}
					});
				} catch ( Exception e) {
					MessageHandler.showError( parent, "Error Formatting the document.\nPlease make sure the document is well-formed.", "Format Error");
					e.printStackTrace();
				} finally {
					parent.setStatus( "Done");
					parent.setWait( false);
					editor.setFocus();
				}
			}
		};
				
		// Create and start the thread ...
		Thread thread = new Thread( runner);
		thread.start();
 	}
 	
 	public String format( String text, String encoding, String systemId) throws IOException, SAXParseException {
		ExchangerOutputFormat format = new ExchangerOutputFormat();
		return format( text, encoding, systemId, format);
 	}

 	public String format( String text, String encoding, String systemId, ExchangerOutputFormat format) throws IOException, SAXParseException {
 		EditorProperties properties = this.properties.getEditorProperties();
 		
		String indent = editor.getTabString();
		boolean newLines = true;
		boolean padText = false;
		boolean preserveMixed = false;
		boolean trim = false;
		int lineLength = -1;
		
		switch ( properties.getFormatType()) {
			case EditorProperties.FORMAT_CUSTOM:
				if ( !properties.isCustomIndent()) {
					indent = "";
				}
				
				newLines = properties.isCustomNewline();
				padText = properties.isCustomPadText();

				if ( properties.isWrapText()) {
					lineLength = properties.getWrappingColumn();
				}

				trim = properties.isCustomStrip();
				preserveMixed = properties.isCustomPreserveMixedContent();
				break;

			case EditorProperties.FORMAT_COMPACT:
				if ( properties.isWrapText()) {
					lineLength = properties.getWrappingColumn();
				}

				indent = "";
				newLines = false;
				padText = false;
				trim = true;
				preserveMixed = false;
				break;
			case EditorProperties.FORMAT_STANDARD:
				if ( properties.isWrapText()) {
					lineLength = properties.getWrappingColumn();
				}

				newLines = true;
				padText = false;

				trim = true;
				preserveMixed = true;
				break;
		}

		return XMLUtilities.format( text, systemId, encoding, indent, newLines, padText, lineLength, trim, preserveMixed, format);
 	}
}
