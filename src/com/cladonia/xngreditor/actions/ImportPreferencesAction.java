/*
 * $Id: AddNodeAction.java,v 1.3 2004/07/21 09:09:42 knesbitt Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.designer.AttributeNode;
import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.designer.DesignerListener;
import com.cladonia.xml.designer.DesignerNode;
import com.cladonia.xml.designer.ElementNode;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ImportPreferencesDialog;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.ToolsLowercaseDialog;
import com.cladonia.xngreditor.XngrProgressDialog;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.project.ProjectProperties;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xngreditor.template.TemplateProperties;

/**
 * An action that can be used to copy information 
 * in a XML Document.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/07/21 09:09:42 $
 * @author Dogsbay
 */
 public class ImportPreferencesAction extends AbstractAction {
 	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ImportPreferencesDialog dialog = null;
	private ExchangerEditor parent = null;
	private static final boolean DEBUG = false;
	private ConfigurationProperties oldProperties = null;
	private ExchangerDocument newPropsDocument = null;
	
 	/**
	 * The constructor for the copy action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public ImportPreferencesAction(ExchangerEditor parent, ConfigurationProperties oldProperties, ExchangerDocument newPropsDocument) {
 		super( "Import Preferences");
 		
 		this.parent = parent;
 		this.oldProperties = oldProperties;
 		this.newPropsDocument = newPropsDocument;

		putValue( MNEMONIC_KEY, new Integer( 'P'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Copy16.gif"));
		putValue( SHORT_DESCRIPTION, "Import Preferences");
		
		setEnabled( true);
 	}
 	
	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		this.execute();
 	}
 	
 	public void execute() {
 		if ( dialog == null) {
            dialog = new ImportPreferencesDialog( parent, true);
        }
 		
 		if(this.oldProperties != null) {
 			dialog.show(oldProperties.getTemplateProperties(), oldProperties.getGrammarProperties(), oldProperties.getProjectProperties(), oldProperties.getScenarioProperties());
 		}
 		
        if(!dialog.isCancelled()) {
        	if(parent != null) {
        		parent.setWait( true);
        		parent.setStatus( "Changing Case ...");
        	}
            
            // Run in Thread!!!
            Runnable runner = new Runnable() {
                public void run()  {
                    try {
                    	
                    	//count how many will be imported
                    	int totalToBeImported = 0;
                    	if(dialog.getTemplatesCheckBox().isSelected() == true) {
                    		if((oldProperties.getTemplateProperties() != null) && (oldProperties.getTemplateProperties().size() > 0)) {
                    			totalToBeImported += oldProperties.getTemplateProperties().size();
                    		}
                    	}
                    	
                    	if(dialog.getTypesCheckBox().isSelected() == true) {
							if((oldProperties.getGrammarProperties() != null) && (oldProperties.getGrammarProperties().size() > 0)) {
								totalToBeImported += oldProperties.getGrammarProperties().size();
							}
                    	}
                    	
                    	if(dialog.getSamplesCheckBox().isSelected() == true) {
							if((oldProperties.getProjectProperties() != null) && (oldProperties.getProjectProperties().size() > 0)) {
								totalToBeImported += oldProperties.getProjectProperties().size();
							}
                    	}
                    	
                    	if(dialog.getScenariosCheckBox().isSelected() == true) {
							if((oldProperties.getScenarioProperties() != null) && (oldProperties.getScenarioProperties().size() > 0)) {
								totalToBeImported += oldProperties.getScenarioProperties().size();
							}
                    	}
                    	
                    	

                    	XngrProgressDialog progressDialog  = new XngrProgressDialog(parent, true);
        	 			progressDialog.setTitle("Importing...");
        	 			progressDialog.setVisible(0, totalToBeImported);
                    	
                    	ConfigurationProperties properties = new ConfigurationProperties(newPropsDocument);
                    	if(dialog.getTemplatesCheckBox().isSelected() == true) {
                    		if((oldProperties.getTemplateProperties() != null) && (oldProperties.getTemplateProperties().size() > 0)) {
                    			for(int cnt=0;cnt<oldProperties.getTemplateProperties().size();++cnt) {
                    				progressDialog.incrementMonitor(1);
                    				properties.addTemplateProperties((TemplateProperties) oldProperties.getTemplateProperties().get(cnt));
                    			}
                    			
                    		}
                    	}
                    	
                    	
						if(dialog.getTypesCheckBox().isSelected() == true) {
							if((oldProperties.getGrammarProperties() != null) && (oldProperties.getGrammarProperties().size() > 0)) {
                    			for(int cnt=0;cnt<oldProperties.getGrammarProperties().size();++cnt) {
                    				progressDialog.incrementMonitor(1);
                    				properties.addGrammarProperties((GrammarProperties) oldProperties.getTemplateProperties().get(cnt));
                    			}
                    			
                    		}
                    	}

						if(dialog.getSamplesCheckBox().isSelected() == true) {
							if((oldProperties.getProjectProperties() != null) && (oldProperties.getProjectProperties().size() > 0)) {
								for(int cnt=0;cnt<oldProperties.getProjectProperties().size();++cnt) {
									progressDialog.incrementMonitor(1);
									properties.addProjectProperties((ProjectProperties) oldProperties.getProjectProperties().get(cnt));
								}
							}
						}

						if(dialog.getScenariosCheckBox().isSelected() == true) {
							if((oldProperties.getScenarioProperties() != null) && (oldProperties.getScenarioProperties().size() > 0)) {
								for(int cnt=0;cnt<oldProperties.getScenarioProperties().size();++cnt) {
									progressDialog.incrementMonitor(1);
									properties.addScenarioProperties((ScenarioProperties) oldProperties.getScenarioProperties().get(cnt));
								}
							}
						}
						
						progressDialog.setVisible(false);
                        
                    } catch ( Exception e) {
                        // This should never happen, just report and continue
                        MessageHandler.showError( parent, "Cannot Import Preferences","Import Preferences Error");
                    } finally {
                    	if(parent != null) {
                    		parent.setStatus( "Done");
                    		parent.setWait( false);
                    	}
                    }
                }
            };
            
            // Create and start the thread ...
            Thread thread = new Thread( runner);
            thread.start();
//          }
        }
 	}
 }
