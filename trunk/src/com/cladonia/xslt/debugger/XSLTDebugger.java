package com.cladonia.xslt.debugger;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import java.util.Enumeration;

import java.util.HashMap;
import java.util.Vector;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

//import com.icl.saxon.TransformerFactoryImpl;
//import org.apache.xalan.processor.TransformerFactoryImpl;
public abstract class XSLTDebugger implements Runnable {
	BreakpointList _stylesheetBreakpoints = null;
	BreakpointList _inputBreakpoints = null;
	Stack _styleStack = new Stack();
	
	Stack _xslTemplateStack = new Stack();
	Stack _xmlStack = new Stack();	
	Stack _outStack = new Stack();
	Stack _mixedStack = new Stack();

	Stack _xslTraceStack = new Stack();
	Stack _xmlTraceStack = new Stack();	
	Stack _outTraceStack = new Stack();
	Stack _mixedTraceStack = new Stack();

	HashMap _xslTemplatesProfileMap = new HashMap();
	
	TreeMap _localTreeMap = new TreeMap();
	TreeMap _globalTreeMap = new TreeMap();
	Saxon1TraceListener _saxon1TraceListener = null;
	String _XSLTProcessor = "saxon1";
	String _inputFilename = null;
	String _stylesheetFilename = null;
	String _outputFilename = null;
	OutputStream _outputStream = null;
	OutputStream _messageOutputStream = null;
	StreamResult _sresult = null;
	DebugController _debugController = null;
	Hashtable _params;
	boolean _isStarted = false;
	
	boolean _isTracingEnabled = true;
	boolean _isOutputRedirected = true;

	
	Transformer t = null;
	
	public static final int COMMAND_DO_NOTHING = 0;
	public static final int COMMAND_STEP = 1;
	public static final int COMMAND_STEP_INTO_TEMPLATE = 1;
	
	public static final int COMMAND_STEP_OVER = 2;
	public static final int COMMAND_STEP_TO_END_OF_TEMPLATE = 4;
	public static final int COMMAND_STEP_OUT_OF_TEMPLATE = 4;
	public static final int COMMAND_CONTINUE_TO_BREAK_OR_END = 5;
	public static final int COMMAND_STOP_IMMEDIATELY = 6;
	public static final int COMMAND_FINISH = 7;
	public static final int COMMAND_RUN_TO_END = 8;

	int _command = COMMAND_DO_NOTHING;
	
	public static final int STATE_NOT_STARTED = 1;
	public static final int STATE_DEBUGGING = 2;
	public static final int STATE_STOPPED_DEBUGGING = 3;
	public static final int STOPPED_ON_STYLESHEET_BREAKPOINT = 0;
	public static final int STOPPED_ON_STYLESHEET_LINECHANGE = 1;
	public static final int STOPPED_ON_INPUT_BREAKPOINT = 2;
	public static final int STOPPED_ON_INPUT_LINECHANGE = 3;
	int _currentState = STATE_NOT_STARTED;
	
	public static final int STACK_ITEM_XSL_START = 1;
	public static final int STACK_ITEM_XSL_END = 2;
	public static final int STACK_ITEM_XML_START = 3;
	public static final int STACK_ITEM_XML_END = 4;
	public static final int STACK_ITEM_OUT_START = 5;
	public static final int STACK_ITEM_OUT_END = 6;
	
	static String XSL_URI = "http://www.w3.org/1999/XSL/Transform";
	
	public XSLTDebugger(BreakpointList stylesheetBreakpoints,
			BreakpointList inputBreakpoints) {
		this._stylesheetBreakpoints = stylesheetBreakpoints;
		this._inputBreakpoints = inputBreakpoints;
	}
	public abstract TransformerFactory getFactory();
	public/* synchronized */void run() {
		try {
		  
		  	_isStarted = true;
		  	
			TransformerFactory tf = null;
			tf = getFactory();

			t = tf.newTransformer(new StreamSource(
					_stylesheetFilename));

			tf.setErrorListener(new TransformErrorListener(_debugController));
			
			t = this.initializeTransformer(t);

			
			if (_params != null)
			{
			  for (Enumeration paramNames = _params.keys(); paramNames.hasMoreElements();)
			  {
			    String paramName = (String)paramNames.nextElement();
			    String paramValue = (String)_params.get(paramName);
			    t.setParameter(paramName, paramValue);
			  }
			  
			}
			
			if (this._outputStream != null)
				_sresult = new StreamResult(
						new OutputStreamWriter(this._outputStream));
			else {
				File output = new File(this._outputFilename);
				_sresult = new StreamResult(_outputFilename);
			}
			setCurrentState(STATE_DEBUGGING);

			t.transform(new StreamSource(_inputFilename), _sresult);
		} catch (Exception ex) {

		    this._debugController.onException(ex);
		}
		
		onEnd();
	}
	public abstract Transformer initializeTransformer(Transformer t);
	public void setProcessor(String processor) {
		this._XSLTProcessor = processor;
	}
	public void setInputFilename(String inputFilename) {
		this._inputFilename = inputFilename;
	}
	public void setStylesheetFilename(String stylesheetFilename) {
		this._stylesheetFilename = stylesheetFilename;
	}
	public String getStylesheetFilename() {
		return this._stylesheetFilename;
	}
	public void setOutputFilename(String outputFilename) {
		this._outputFilename = outputFilename;
	}
	public void setOutputStream(OutputStream os) {
		this._outputStream = os;
	}
	public OutputStream getOutputStream() {
		return this._outputStream;
	}

	public void setMessageOutputStream(OutputStream os) {
		this._messageOutputStream = os;
	}
	public OutputStream getMessageOutputStream() {
		return this._messageOutputStream;
	}

	public void setTracingEnabled(boolean isTracingEnabled) {
		this._isTracingEnabled = isTracingEnabled;
	}

	public boolean isTracingEnabled() {
		return this._isTracingEnabled;
	}

	
	public void setOutputRedirected(boolean isOutputRedirected) {
		this._isOutputRedirected = isOutputRedirected;
	}
	public boolean isOutputRedirected() {
		return this._isOutputRedirected;
	}

	

	public void setParams(Hashtable params) {
		// TODO should make copy
		this._params = params;
	}
	public void setDebugController(DebugController debugController) {
		this._debugController = debugController;
	}
	public synchronized boolean isStarted() {
		return this._isStarted;
	}
	private void debugStop() {
		//System.out.println("XSLTDebugger.debugStop()");
	}

	public void onOpenOutputDocument(String outputFilename) {
		flushDebuggerOutput();
		this._debugController.onOpenOutputDocument(outputFilename);
	}
	public void onCloseOutputDocument(String outputFilename) {
		flushDebuggerOutput();
		this._debugController.onCloseOutputDocument(outputFilename);
	}
	public void onStart() {
		this._debugController.onStart();
	}
	public void onEnd() {
		setCurrentState(STATE_STOPPED_DEBUGGING);
		this._command = COMMAND_DO_NOTHING;
		//notifyAll();
		this._debugController.onEnd();
	}
	public boolean isStylesheetBreakpoint(String stylesheetFilename,
			int lineNumber) {
		return _stylesheetBreakpoints.isBreakpoint(stylesheetFilename,
				lineNumber);
	}
	public boolean isInputBreakpoint(String inputFilename, int lineNumber) {
		return _inputBreakpoints.isBreakpoint(inputFilename, lineNumber);
	}
	public synchronized void stoppedOn(int stopReason, String filename,
			int linenumber, String elementName, boolean isEndTag) {
		flushDebuggerOutput();
		setCurrentState(STATE_STOPPED_DEBUGGING);
		notifyAll();
		//System.out.println("debugger Stack size = " + this._styleStack.size());
		//getLocalVariables();
		//getGlobalVariables();
		

		//debugStacks();
		//debugTraces();
		//debugProfile();

		switch (stopReason) {
			case STOPPED_ON_STYLESHEET_BREAKPOINT :
				this._debugController.onStylesheetBreakpoint(filename,
						linenumber);
				break;
			case STOPPED_ON_STYLESHEET_LINECHANGE :
				this._debugController.onStylesheetLineChange(filename,
						linenumber);
				break;
			case STOPPED_ON_INPUT_BREAKPOINT :
				this._debugController.onInputBreakpoint(filename, linenumber);
				break;
			case STOPPED_ON_INPUT_LINECHANGE :
				this._debugController.onInputLineChange(filename, linenumber);
				break;
		}
		while (this._currentState != STATE_DEBUGGING) {
			try {
				wait();
			} catch (Exception ex) {
			}
		}
	}
	
	
	public synchronized void stoppedOn(int stopReason, XSLTStatus status) {
	flushDebuggerOutput();
	setCurrentState(STATE_STOPPED_DEBUGGING);
	notifyAll();
	
	//System.out.println("debugger Stack size = " + this._styleStack.size());
	//getLocalVariables();
	//getGlobalVariables();
	

	//debugStacks();
	//debugTraces();
	//debugProfile();

	
			this._debugController.onStylesheetChange(stopReason, status);
					

	while (this._currentState != STATE_DEBUGGING) {
		try {
			wait();
		} catch (Exception ex) {
		}
	}
}

	
	public void cleanUp()
	{

	  if (this._xslTemplateStack != null)
	    this._xslTemplateStack.clear();
	  
	  if (this._xmlStack != null)
	    this._xmlStack.clear();
	  
	  if (this._outStack != null)
	    this._outStack.clear();

	  if (this._mixedStack != null)
	    this._mixedStack.clear();

	  if (this._xslTraceStack != null)
	    this._xslTraceStack.clear();

	  if (this._xmlTraceStack != null)
	    this._xmlTraceStack.clear();

	  if (this._outTraceStack != null)
	    this._outTraceStack.clear();

	  if (this._mixedTraceStack != null)
	    this._mixedTraceStack.clear();

	  if (this._xslTemplatesProfileMap != null)
	    this._xslTemplatesProfileMap.clear();

	  
	}

	
	public void debugStacks()
	{
		System.out.println("\n\n\n");
	    for (int i = this._xslTemplateStack.size() - 1; i >= 0; i--)
	    {
	      //System.out.println("i=" + i);
	      XSLTemplateStackItem xtsi = (XSLTemplateStackItem) this._xslTemplateStack
	              .get(i);
	      
	      System.out.println("_xslTemplateStack " + i + " match=" + xtsi._match + " name=" + xtsi._name + " node=" + xtsi._mode + " priotity=" + xtsi._priority +  " " + xtsi._filename);
	    }
	    System.out.println("\n");   
	    for (int i = this._xmlStack.size() - 1; i >= 0; i--)
	    {
	      //System.out.println("i=" + i);
	      DefaultStackItem dsi = (DefaultStackItem) this._xmlStack
	              .get(i);
	      
	      System.out.println("_xmlStack " + i + "  " + dsi._name + " " + dsi._filename);
	    }
	       
	    System.out.println("\n");   
	       
	    for (int i = this._outStack.size() - 1; i >= 0; i--)
	    {
	      //System.out.println("i=" + i);
	      DefaultStackItem dsi = (DefaultStackItem) this._outStack
	              .get(i);
	      
	      System.out.println("_outStack " + i + "  " + dsi._name + " " + dsi._filename);
	    }
	    System.out.println("\n");   
	       
	    for (int i = this._mixedStack.size() - 1; i >= 0; i--)
	    {
	      //System.out.println("i=" + i);
	      DefaultStackItem dsi = (DefaultStackItem) this._mixedStack
	              .get(i);
	      
	      System.out.println("_mixedStack " + i + "  " + dsi._name + " " + dsi._filename);
	    }

	    
	}
	
//	Stack _styleStack = new Stack();
	
	public HashMap getXSLProfile() {
		return _xslTemplatesProfileMap;
	}

	public Vector getStylesheetStack() {
		return _xslTemplateStack;
	}

	public Vector getInputStack() {
		return _xmlStack;
	}

	public Vector getOutputStack() {
		return _outStack;
	}

	public Vector getMixedStack() {
		return _mixedStack;
	}

	public Vector getStylesheetTrace() {
		return _xslTraceStack;
	}

	public Vector getInputTrace() {
		return _xmlTraceStack;
	}

	public Vector getOutputTrace() {
		return _outTraceStack;
	}

	public Vector getMixedTrace() {
		return _mixedTraceStack;
	}
	
	public void debugProfile()
	{
	    
	    System.out.println("\n\n\n");   

	    
	    Set entries = this._xslTemplatesProfileMap.entrySet();
	    Iterator iter = entries.iterator();
	    while (iter.hasNext())
	    {
	      Map.Entry entry = (Map.Entry)iter.next();
	      String templateDetails = (String)entry.getKey();
	      Integer count = (Integer)entry.getValue();
	      
	      System.out.println("_xslTemplatesProfileMap " + templateDetails + "  " + count);

	    }
	    System.out.println("\n");   
	}
	
	public void debugTraces()
	{
	    
	    System.out.println("\n\n\n");   

	    for (int i = this._xslTraceStack.size() - 1; i >= 0; i--)
	    {
	      //System.out.println("i=" + i);
	      DefaultStackItem dsi = (DefaultStackItem) this._xslTraceStack
          .get(i);	      
	      System.out.println("_xslTraceStack " + i + "  " + dsi._name + " " + dsi._filename);
	    }
	    System.out.println("\n");   
	    for (int i = this._xmlTraceStack.size() - 1; i >= 0; i--)
	    {
	      //System.out.println("i=" + i);
	      DefaultStackItem dsi = (DefaultStackItem) this._xmlTraceStack
	              .get(i);
	      
	      System.out.println("_xmlTraceStack " + i + "  " + dsi._name + " " + dsi._filename);
	    }
	       
	    System.out.println("\n");   
	       
	    for (int i = this._outTraceStack.size() - 1; i >= 0; i--)
	    {
	      //System.out.println("i=" + i);
	      DefaultStackItem dsi = (DefaultStackItem) this._outTraceStack
	              .get(i);
	      
	      System.out.println("_outTraceStack " + i + "  " + dsi._name + " " + dsi._filename);
	    }
	    System.out.println("\n");   
	       
	    for (int i = this._mixedTraceStack.size() - 1; i >= 0; i--)
	    {
	      //System.out.println("i=" + i);
	      DefaultStackItem dsi = (DefaultStackItem) this._mixedTraceStack
	              .get(i);
	      
	      System.out.println("_mixedTraceStack " + i + "  " + dsi._name + " " + dsi._filename);
	    }
	       
	  
	  
	}
	
	
	
	
	public abstract Vector getGlobalVariables();
	public abstract Vector getLocalVariables();
	/*
	 * public TreeMap getGlobalVariablesTreeMap() {
	 * 
	 * getGlobalVariables();
	 * 
	 * return _globalTreeMap;
	 *  }
	 * 
	 * 
	 * public TreeMap getLocalVariablesTreeMap() {
	 * 
	 * getLocalVariables();
	 * 
	 * return _localTreeMap;
	 * 
	 * 
	 *  }
	 *  
	 */
	public void flushDebuggerOutput() {
		try {
			Writer w = _sresult.getWriter();
			if (w != null) {
				w.flush();
			}
		} catch (Exception ex) {
		}
	}
	public synchronized boolean handleImmediateStop() {
		//notifyAll();
		//System.out.println("debugger handleImmediateStop");
		if (getCommand() == COMMAND_STOP_IMMEDIATELY) {
			//System.out.println("debugger handleImmediateStop setCurrentState STATE_STOPPED_DEBUGGING");
			// DON'T TELL GUI
			//setCurrentState( STATE_STOPPED_DEBUGGING);
			this._currentState = STATE_STOPPED_DEBUGGING;
			//System.out.println("debugger handleImmediateStop notifyAll");
			notifyAll();
			//System.out.println("debugger handleImmediateStop RETURN TRUE");
			Thread.yield();
			return true;
		}
		Thread.yield();
		return false;
	}
	
	public synchronized void stopImmediately() throws InterruptedException {
		//System.out.println("debugger stopImmediately");
		this._command = COMMAND_STOP_IMMEDIATELY;
		notifyAll();
		//System.out.println("stopImmediately this._command = COMMAND_STOP_IMMEDIATELY    state = " + this._currentState);
		try {
			while (this._currentState != STATE_STOPPED_DEBUGGING) {
				wait();
			}
		} catch (Exception ex) {
		}
		//System.out.println("stopImmediately this._currentState != STATE_STOPPED_DEBUGGING");
	}
	public synchronized void command(int cmd) {
		//System.out.println("debugger COMMAND " + cmd);
		switch (cmd) {
		  
			/* Now handled as STEP
			 * case COMMAND_STEP_INTO_TEMPLATE :
				setCurrentState(STATE_DEBUGGING);
				this._command = COMMAND_STEP;
				notifyAll();
				break;*/
				
			case COMMAND_STEP :
			case COMMAND_STEP_OVER :
			case COMMAND_STEP_OUT_OF_TEMPLATE :
			case COMMAND_CONTINUE_TO_BREAK_OR_END :
			case COMMAND_RUN_TO_END :
				setCurrentState(STATE_DEBUGGING);
				this._command = cmd;
				notifyAll();
				break;
			case COMMAND_STOP_IMMEDIATELY :
				//System.out.println("debugger COMMAND_STOP_IMMEDIATELY");
				this._command = cmd;
				notifyAll();
				try {
					while (this._currentState != STATE_STOPPED_DEBUGGING) {
						wait();
					}
				} catch (Exception ex) {
				}
				break;
			default :
				break;
		}
	}
	public int getCommand() {
		return this._command;
	}
	public synchronized void setCurrentState(int currentState) {
		this._currentState = currentState;
		//notifyAll();
		this._debugController.stateChanged(_currentState);
	}
	public synchronized int getCurrentState() {
		return _currentState;
	}
	public abstract String getVariableValue(String variableName);
}
