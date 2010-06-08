
package com.cladonia.xslt.debugger;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Stack;

public abstract class DebugController {

	public static String SAXON1_PROCESSOR	= "saxon1";
	public static String SAXON2_PROCESSOR	= "saxon2";
	public static String XALAN_PROCESSOR	= "xalan";

	String _XSLTProcessor = "saxon1";

	BreakpointList _stylesheetBreakpoints = null;
	BreakpointList _inputBreakpoints = null;

	XSLTDebugger _debugger = null;
	
	String _inputFilename = null;
	String _stylesheetFilename = null;
	String _outputFilename = null;
	OutputStream _outputStream = null;
	OutputStream _messageStream = null;
	
	Hashtable _params = null;
	
	boolean _isAutomated = false;
	int _automatedInterval = 3000;
	
	int _temp = 0;
	
	/*
	static final int COMMAND_DO_NOTHING = 0;
	static final int COMMAND_STEP = 1;
	static final int COMMAND_STEP_OVER = 2;
	static final int COMMAND_STEP_INTO_TEMPLATE = 3;
	static final int COMMAND_STEP_TO_END_OF_TEMPLATE = 4;
	static final int COMMAND_CONTINUE_TO_BREAK_OR_END = 5;
	static final int COMMAND_STOP_IMMEDIATELY = 6;
	static final int COMMAND_FINISH = 7;
	
	public static final int STATE_NOT_STARTED = 1;
	public static final int STATE_DEBUGGING = 2;
	public static final int STATE_STOPPED_DEBUGGING = 3;
*/
	public DebugController() {
		this._stylesheetBreakpoints = new BreakpointList();
		this._inputBreakpoints = new BreakpointList();
	}

	public DebugController( BreakpointList stylesheetBreakpoints, BreakpointList inputBreakpoints) {
		this._stylesheetBreakpoints = stylesheetBreakpoints;
		this._inputBreakpoints = inputBreakpoints;
	}

	public synchronized  void initializeDebugger() {
		this._debugger = this.createDebugger(this._XSLTProcessor);
	
		this._debugger.setProcessor(this._XSLTProcessor);
		this._debugger.setDebugController(this);
	
		this._debugger.setInputFilename(this._inputFilename);
		this._debugger.setStylesheetFilename(this._stylesheetFilename);
		this._debugger.setOutputFilename(this._outputFilename);
	
		this._debugger.setOutputStream(this._outputStream);
		this._debugger.setMessageOutputStream(this._messageStream);
	
		this._debugger.setParams(this._params);
	}

	public void setAutomated( boolean isAutomated, int automatedInterval) {
		this._isAutomated = isAutomated;
		_automatedInterval = automatedInterval;
	}

	public boolean isAutomated() {
		return this._isAutomated;
	}

	public synchronized void startDebugger() {
	
		if (!this._debugger.isStarted()) {

			Thread debugThread = new Thread(this._debugger);
			debugThread.start();


		}
	
	}

	public XSLTDebugger createDebugger( String processor) {
	 
		if (processor.equalsIgnoreCase( SAXON1_PROCESSOR)) {
			_debugger = new Saxon1Debugger( _stylesheetBreakpoints, _inputBreakpoints);
		}
		else if (processor.equalsIgnoreCase( SAXON2_PROCESSOR)) {
			_debugger = new Saxon2Debugger( _stylesheetBreakpoints, _inputBreakpoints);
		}
		else if (processor.equalsIgnoreCase( XALAN_PROCESSOR)) {
			_debugger = new XalanDebugger( _stylesheetBreakpoints, _inputBreakpoints);
		}
	
		return _debugger;
	}


	public abstract void onStylesheetChange(int stopReason, XSLTStatus status);

	public abstract void onStylesheetBreakpoint( String stylesheetFilename, int stylesheetLineNumber);

	public abstract void onStylesheetLineChange( String stylesheetFilename, int stylesheetLineNumber);

	public abstract void onInputBreakpoint( String inputFilename, int inputLineNumber);

	public abstract void onInputLineChange( String inputFilename, int inputLineNumber);

	public abstract void onOpenOutputDocument( String outputFilename);

	public abstract void onCloseOutputDocument(String outputFilename);

	public abstract void onStop(String stylesheetFilename, int stylesheetLineNumber, String inputFilename, int inputLineNumber);

	public abstract void onStart();

	public abstract void onEnd();

	public abstract void onException(Exception ex);

	public void updateUI()
	{
		flushDebuggerOutput();
	
		pauseDebugger();
	
	}

	public void setInputFilename(String inputFilename)
	{
		this._inputFilename= inputFilename;
	}
	
	public void setStylesheetFilename(String stylesheetFilename)
	{
		this._stylesheetFilename = stylesheetFilename;
	}
	
	public void setOutputFilename(String outputFilename)
	{
		this._outputFilename= outputFilename;
	}
	
	public void setOutputStream(OutputStream os)
	{
		this._outputStream = os;
	}

	public void setMessageStream(OutputStream os)
	{
		this._messageStream = os;
	}

	public void flushDebuggerOutput()
	{
		this._debugger.flushDebuggerOutput();
	}
	
	public synchronized void pauseDebugger()
	{
try
{

		stopImmediately( );
}
catch (InterruptedException ie) {}
catch (Exception ex) {}

	}
	
	public void step() 
	{

		this._debugger.command( XSLTDebugger.COMMAND_STEP);
	}
	
	public void stepOver() 
	{
		this._debugger.command( XSLTDebugger.COMMAND_STEP_OVER);
	}
	
	public void stepIntoTemplate() 
	{
		this._debugger.command( XSLTDebugger.COMMAND_STEP_INTO_TEMPLATE);
	}
	
	public void stepToTemplateEnd() 
	{
		this._debugger.command( XSLTDebugger.COMMAND_STEP_TO_END_OF_TEMPLATE);
	}
	
	public synchronized void continueToBreakOrEnd() 
	{
		this._debugger.command( XSLTDebugger.COMMAND_CONTINUE_TO_BREAK_OR_END);
	}
	
	public synchronized void runToEnd() 
	{
		this._debugger.command( XSLTDebugger.COMMAND_RUN_TO_END);
	}

	public synchronized  void stopImmediately()  throws InterruptedException

	{
		this._debugger.stopImmediately();
	}

	public void cleanUp()
	{
		this._debugger.cleanUp();
	
	}
	public void setTracingEnabled(boolean isTracingEnabled) {
		this._debugger.setTracingEnabled(isTracingEnabled);
	}

	public boolean isTracingEnabled() {
		return this._debugger.isTracingEnabled();
	}
	
	public void setOutputRedirected(boolean isOutputRedirected) {
		this._debugger.setOutputRedirected(isOutputRedirected);
	}
	public boolean isOutputRedirected() {
		return this._debugger.isOutputRedirected();
	}

	
	public String getVariableValue(String variableName)
	{
		return this._debugger.getVariableValue(variableName);
	}

	public Vector getGlobalVariables()
	{
		return _debugger.getGlobalVariables();
	}

	public HashMap getXSLProfile() {
		return _debugger.getXSLProfile();
	}

	public Vector getStylesheetStack() {
		return _debugger.getStylesheetStack();
	}

	public Vector getInputStack() {
		return _debugger.getInputStack();
	}

	public Vector getOutputStack() {
		return _debugger.getOutputStack();
	}

	public Vector getMixedStack() {
		return _debugger.getMixedStack();
	}

	public Vector getStylesheetTrace() {
		return _debugger.getStylesheetTrace();
	}

	public Vector getInputTrace() {
		return _debugger.getInputTrace();
	}

	public Vector getOutputTrace() {
		return _debugger.getOutputTrace();
	}

	public Vector getMixedTrace() {
		return _debugger.getMixedTrace();
	}

	//	public TreeMap getGlobalVariablesTreeMap()
//	{
//		return null;
//	}

	public Vector getLocalVariables()
	{
		return _debugger.getLocalVariables();
	}

//	public TreeMap getLocalVariablesTreeMap()
//	{
//		return null;
//	}

	
	public Stack getXMLStack()
	{
		return _debugger._xmlStack;
	}

	public Stack getXSLStack()
	{
		return _debugger._xslTemplateStack;
	}

	public Stack getOutStack()
	{
		return _debugger._outStack;
	}
//	public Stack getMixedStack()
//	{
//		return _debugger._mixedStack;
//	}



	public void setParam(String name, String value)	{
		this.setParam(name, value, false);
	}
	
	public void setParam(String name, String value, boolean isXPath) {
	
		if (this._params == null)
			this._params = new Hashtable();
	
		this._params.put(name, value);
	
	}
	
	public String getParamValue(String name) {
		String value = null;
	
		return value;
	}
	
	public boolean isXPathParam(String name) {
	
		return false;
	}
	
	public synchronized int getState() {
		return _debugger.getCurrentState();
	}
	
	public abstract void stateChanged( int state);
	
	public void setXSLTProcessor(String processor) {
		this._XSLTProcessor = processor;
	}


	public TreeMap getGlobalVariablesTreeMap()
	{
		return null;
//		return this._debugger.getGlobalVariablesTreeMap();
	}


	public TreeMap getLocalVariablesTreeMap()
	{
		return null;
//		return this._debugger.getLocalVariablesTreeMap();
	}


	public void setVariableValueMaxLength(int maxLength)
	{

	}


}