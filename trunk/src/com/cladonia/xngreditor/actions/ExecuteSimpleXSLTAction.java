/*
 * $Id: ExecuteSimpleXSLTAction.java,v 1.2 2005/06/01 15:15:34 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import com.cladonia.xml.transform.ScenarioUtilities;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xngreditor.XMLDocumentChooserDialog;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.transform.XSLTProcessorDialog;
/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/06/01 15:15:34 $
 * @author Dogsbay
 */
public class ExecuteSimpleXSLTAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ExchangerEditor parent = null;
	//private ExecuteXSLTDialog dialog = null;
	private XMLDocumentChooserDialog chooserXSL = null;
	private XMLDocumentChooserDialog chooserXML = null;
	private XSLTProcessorDialog processorDialog = null;
	
 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public ExecuteSimpleXSLTAction( ExchangerEditor parent) {
 		super( "Execute Simple XSLT ...");

		this.parent = parent;

		putValue( MNEMONIC_KEY, new Integer( 'S'));
		putValue( SHORT_DESCRIPTION, "Execute a Simple XSLT Transformation");
 	}
 	
 	/**
 	 * The implementation of the execute XSLT action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
		
		ExchangerView view = parent.getView();
		String xslUrl = null;
		String xmlUrl = null;
		
		if ( view != null) {
			view.updateModel();
		}

		ExchangerDocument document = parent.getDocument();
		ExchangerDocument xslDocument = parent.getDocument();

		if ( chooserXML == null) {
		  chooserXML = new XMLDocumentChooserDialog( parent,  "Select XML Input", "Specify XML Input Document", parent, false);
		}

		if ( document != null) {
		  chooserXML.show( document.isXML());
		} else{
		  chooserXML.show( false);
		}
		
		if ( !chooserXML.isCancelled()) {
			try {
				if ( chooserXML.isOpenDocument()) {				  
				  document = chooserXML.getOpenDocument();	
				  xmlUrl = document.getURL().toString();
				}  
				else if ( !chooserXML.isCurrentDocument()) {
					xmlUrl = chooserXML.getInputLocation();

				}

				
			} 
			catch (Exception ex) {}
//			catch ( IOException x) {
//				MessageHandler.showError( "Could not create the Document:\n"+chooserXML.getInputLocation(), "Document Error");
//			} 
//			catch ( SAXParseException x) {
//				MessageHandler.showError( "Could not parse the Document.", x, "Document Error");
//			}
			
			if ( chooserXSL == null) {
			  chooserXSL = new XMLDocumentChooserDialog( parent,  "Select XSL Input", "Specify XSL Stylesheet", parent, false);
			}

			if ( xslDocument != null) {
			  chooserXSL.show( xslDocument.isXSL());
			} else{
			  chooserXSL.show( false);
			}
			
			if ( !chooserXSL.isCancelled()) {
				try {
					if ( chooserXSL.isOpenDocument()) {				  
					  xslDocument = chooserXSL.getOpenDocument();	
					  xslUrl = xslDocument.getURL().toString();
					}  
					else if ( !chooserXSL.isCurrentDocument()) {
						xslUrl =  chooserXSL.getInputLocation();

					}

					
				} 
				catch (Exception ex) {}
//					catch ( IOException x) {
//					MessageHandler.showError( "Could not create the Document:\n"+chooserXSL.getInputLocation(), "Document Error");
//				} 
//				catch ( SAXParseException x) {
//					MessageHandler.showError( "Could not parse the Document.", x, "Document Error");
//				}
				
	
				if ( processorDialog == null) {
				  processorDialog = new XSLTProcessorDialog( parent);
				}
				
				processorDialog.setProcessor(ScenarioProperties.PROCESSOR_DEFAULT);
				processorDialog.show();
				
				if (processorDialog.isCancelled())
					return;
				
				ScenarioProperties scenario = new ScenarioProperties();
				  if (chooserXML.isCurrentDocument())
				    scenario.setInputType(ScenarioProperties.INPUT_CURRENT_DOCUMENT);
				  else
				  {
				    scenario.setInputType(ScenarioProperties.INPUT_FROM_URL);
				    scenario.setInputFile(xmlUrl);
				  }

				  if (chooserXSL.isCurrentDocument())
				    scenario.setXSLType(ScenarioProperties.XSL_CURRENT_DOCUMENT);
				  else
				  {
				    scenario.setXSLType(ScenarioProperties.XSL_FROM_URL);
				    scenario.setXSLURL(xslUrl);
				  }

				  scenario.setOutputType(ScenarioProperties.OUTPUT_TO_NEW_DOCUMENT);

				  
				  scenario.setXSLEnabled(true);
				  
				  //public static final int XSLT_PROCESSOR_DEFAULT		= 0;
				  //public static final int XSLT_PROCESSOR_XALAN		= 1;
				  //public static final int XSLT_PROCESSOR_SAXON_XSLT1	= 2;
				  //public static final int XSLT_PROCESSOR_SAXON_XSLT2	= 3;
			    	
				  scenario.setProcessor(processorDialog.getProcessor());
				
				parent.getExecutePreviousXSLTAction().setScenario( scenario);
				ScenarioUtilities.execute( parent.getDocument(), scenario);
				
				
				
			}
			
			
	
			
		}
		
	}
}
