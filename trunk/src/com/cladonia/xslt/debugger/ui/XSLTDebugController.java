/*
 * $Id: XSLTDebugController.java,v 1.19 2005/04/29 12:37:44 gmcgoldrick Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.io.IOException;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.scenario.ParameterProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xslt.debugger.DebugController;
import com.cladonia.xslt.debugger.XSLTDebugger;
import com.cladonia.xslt.debugger.XSLTStatus;

/**
 * The XSLT debug controller.
 *
 * @version	$Revision: 1.19 $, $Date: 2005/04/29 12:37:44 $
 * @author Dogsbay
 */
public class XSLTDebugController extends DebugController {
	private boolean redirect = false;
	private boolean tracing = false;
	private boolean running = false;
	private boolean started = false;
	private boolean cancelled = false;
	private boolean finished = false;

	private XSLTDebuggerPane debugger = null;
	private XSLTTransformation transformation = null;

	public XSLTDebugController( XSLTDebuggerPane debugger, XSLTTransformation transformation, boolean tracing, boolean redirect) {
		super( transformation.getStyleBreakpoints(), transformation.getInputBreakpoints());
			
		this.transformation = transformation;
		this.debugger = debugger;
		
		this.tracing = tracing;
		this.redirect = redirect;

		setInputFilename( transformation.getInputURL());
		setStylesheetFilename( transformation.getStyleURL());
		setOutputStream( debugger.getOutputView().getOutputStream());
		setMessageStream( debugger.getOutputView().getMessageStream());
		
		Vector parameters = transformation.getParameters();

		for ( int i = 0; i < parameters.size(); i++) {
			ParameterProperties para = (ParameterProperties)parameters.elementAt(i);
			setParam( para.getName(), para.getValue());
		}
		
		int processor = transformation.getProcessor();

		if ( processor == ScenarioProperties.PROCESSOR_SAXON_XSLT1) {
			setXSLTProcessor( XSLTDebugController.SAXON1_PROCESSOR);
		} else if ( processor == ScenarioProperties.PROCESSOR_XALAN) {
			setXSLTProcessor( XSLTDebugController.XALAN_PROCESSOR);		
		} else if ( processor == ScenarioProperties.PROCESSOR_SAXON_XSLT2) {
			setXSLTProcessor( XSLTDebugController.SAXON2_PROCESSOR);
		}
	}

	public synchronized void startDebugging() {
		debugger.reset();
		
		setRunning( true);
		initializeDebugger();
		
		setTracingEnabled( tracing);
		setOutputRedirected( redirect);
		
		startDebugger();
	}
	
	public void cleanUp() {
		super.cleanUp();
		
		debugger = null;
		transformation = null;
	}
	
	public boolean isRunning() {
		return running;
	}

	public boolean isStarted() {
		return started;
	}
	
	public synchronized void continueToBreakOrEnd() {
		if ( isRunning()) { //(getState() == STATE_NOT_STARTED) {
			super.continueToBreakOrEnd();
		} else {
			startDebugging();
		}
	}
		
	public synchronized void step() {
		if ( isRunning()) { //(getState() == STATE_NOT_STARTED) {
			super.step();
		} else {
			debugger.reset();

			setRunning( true);
			
			initializeDebugger();
			setTracingEnabled( tracing);
			setOutputRedirected( redirect);

			super.step();
			startDebugger();
		}
	}

	public void onStylesheetChange( int reason, final XSLTStatus status) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						if ( !debugger.isOpenInput()) {
							if ( debugger.getInputView().getPane( status.getInputFilename()) != null) {
								debugger.getInputView().select( status.getInputFilename(), status.getInputLineNumber(), status.getInputColumnNumber(), status.getInputDisplayName(), status.isInputStartTag());
							}
						} else {
							debugger.getInputView().select( status.getInputFilename(), status.getInputLineNumber(), status.getInputColumnNumber(), status.getInputDisplayName(), status.isInputStartTag());
						}

						debugger.getXSLTView().select( status.getStylesheetFilename(), status.getStylesheetLineNumber(), status.getStylesheetColumnNumber(), status.getStylesheetDisplayName(), status.isStylesheetStartTag());
						debugger.update();
					}
				}
			);
	}

	public void onOpenOutputDocument( String outputFilename)	{
//		System.out.println("Controller.onOpenOutputDocument( "+outputFilename+")");
		debugger.getOutputView().select( outputFilename);
	}

	public void onCloseOutputDocument(String outputFilename) {
		debugger.getOutputView().close();
	}
	
	public void onStop( String stylesheetFilename, int stylesheetLineNumber, String inputFilename, int inputLineNumber) {
		setRunning( false);

		updateUI();
	}
	
	public void stop() {
		int state = getState();
		
		setRunning( false);
		cancelled = true;

		if ( state != XSLTDebugger.STATE_NOT_STARTED && state != XSLTDebugger.STATE_STOPPED_DEBUGGING) {
			pauseDebugger();
		} else {
			if ( debugger != null) {
				updateState( XSLTDebugger.STATE_STOPPED_DEBUGGING);
			}
		}
	}

	public void onStart() {}

	public void onEnd()	{
		setRunning( false);

		flushDebuggerOutput();
	}
	
	public void onException(Exception e)	{
		if ( e instanceof SAXException) {
			MessageHandler.showError( debugger.getFrame(), "Could not parse document.", e, "Parse Error");
		} else if ( e instanceof IOException) {
			MessageHandler.showError( debugger.getFrame(), "Could not open the file.", e, "File Error");
		} else if ( e instanceof javax.xml.transform.TransformerException && (e.getMessage().equals("Running an XSLT 1.0 stylesheet with an XSLT 2.0 processor"))) {
			//MessageHandler.showError( debugger.getFrame(), "Could not open the file.", e, "File Error");
		} else if ( e instanceof javax.xml.transform.TransformerException && (e.getMessage().startsWith("Running an XSLT"))) {
			//MessageHandler.showError( debugger.getFrame(), "Could not open the file.", e, "File Error");
		} else {
		  	//System.out.println("exception: " + e.getClass().getName());
			MessageHandler.showError( debugger.getFrame(), "Could not debug the document.", e, "XSLT Debug Error");
		}
	}
	
	
	public void stateChanged( final int state) {
			
		try{
			if ( SwingUtilities.isEventDispatchThread()) {
				if ( debugger != null) {
					updateState( state);
				}
			} else {
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						if ( debugger != null) {
							updateState( state);
						}
					}
				});
			}
		} catch ( Exception e){
			e.printStackTrace();
		}
	}
	
	private void updateState( int state) {
//		System.err.println( "update state = "+state);

		if ( state == XSLTDebugger.STATE_NOT_STARTED || state == XSLTDebugger.STATE_STOPPED_DEBUGGING) {
			if ( isRunning()) {
				debugger.getStatusbar().setStatus( Statusbar.STATUS_PAUSED, "Transformation Paused");

				debugger.getContinueAction().setEnabled( true);
				debugger.getContinueToEndAction().setEnabled( true);
				debugger.getPauseAction().setEnabled( false);
				debugger.getStepIntoAction().setEnabled( true);
				debugger.getStepOutAction().setEnabled( true);
				debugger.getStepOverAction().setEnabled( true);
				debugger.getStopAction().setEnabled( true);
			} else if ( !finished) {
				
				finished = true;
				debugger.getStatusbar().setStatus( Statusbar.STATUS_IDLE, "Transformation Finished");
				
				if ( !cancelled) {
					debugger.debuggingFinished();
				}

				debugger.getContinueAction().setEnabled( true);
				debugger.getContinueToEndAction().setEnabled( true);
				debugger.getPauseAction().setEnabled( false);
				debugger.getStepIntoAction().setEnabled( true);
				debugger.getStepOutAction().setEnabled( false);
				debugger.getStepOverAction().setEnabled( false);
				debugger.getStopAction().setEnabled( false);
			}
		} else {
			debugger.getStatusbar().setStatus( Statusbar.STATUS_RUNNING, "Transformation Running ...");
			
			debugger.getContinueAction().setEnabled( false);
			debugger.getContinueToEndAction().setEnabled( false);
			debugger.getPauseAction().setEnabled( true);
			debugger.getStepIntoAction().setEnabled( false);
			debugger.getStepOutAction().setEnabled( false);
			debugger.getStepOverAction().setEnabled( false);
			debugger.getStopAction().setEnabled( true);
		}

		debugger.update();
	}

	private void setRunning( boolean enabled) {
		running = enabled;

		if ( debugger != null) {
			debugger.getContinueAction().setRunning( running);
			debugger.getContinueToEndAction().setRunning( running);
			debugger.getStepIntoAction().setRunning( running);
			debugger.setRunning( running);
		}

		if ( running) {
			started = true;
		}
	}
		
	public void onStylesheetBreakpoint( final String stylesheetFilename, final int stylesheetLineNumber) {}
	public void onStylesheetLineChange( final String stylesheetFilename, final int stylesheetLineNumber) {}
	public void onInputBreakpoint( final String inputFilename, final int inputLineNumber) {}
	public void onInputLineChange( final String inputFilename, final int inputLineNumber) {}
		
}
