
package com.cladonia.xslt.debugger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import com.icl.saxon.Controller;
import com.icl.saxon.trace.TraceListener;
import com.icl.saxon.output.MessageEmitter;

import java.util.Enumeration;
import java.util.Vector;
import java.io.PrintWriter;

import com.icl.saxon.Context;
import com.icl.saxon.style.StyleElement;


import com.icl.saxon.Bindery;
import com.icl.saxon.Binding;
import com.icl.saxon.expr.StaticContext;
import com.icl.saxon.expr.Value;



public class Saxon1Debugger extends XSLTDebugger
{
	Saxon1TraceListener _saxon1TraceListener = null;
	MessageEmitter me = null;

	public Saxon1Debugger( BreakpointList stylesheetBreakpoints, BreakpointList inputBreakpoints) 
	{
		super( stylesheetBreakpoints, inputBreakpoints);
	}

	public TransformerFactory getFactory()
	{
		return new com.icl.saxon.TransformerFactoryImpl();
	}

	public Transformer initializeTransformer(Transformer t) {
		_saxon1TraceListener  = new Saxon1TraceListener();
		_saxon1TraceListener.setXSLTDebugger(this);
		_saxon1TraceListener.setStyleStack(_styleStack);
		_saxon1TraceListener.setXSLTemplateStack(_xslTemplateStack);
		_saxon1TraceListener.setXMLStack(_xmlStack);
		_saxon1TraceListener.setOutStack(_outStack);
		_saxon1TraceListener.setMixedStack(_mixedStack);
		_saxon1TraceListener.setXSLTraceStack(_xslTraceStack);
		_saxon1TraceListener.setXMLTraceStack(_xmlTraceStack);
		_saxon1TraceListener.setOutTraceStack(_outTraceStack);
		_saxon1TraceListener.setMixedTraceStack(_mixedTraceStack);

		_saxon1TraceListener.setXSLTemplateProfileMap(_xslTemplatesProfileMap);

		((Controller)t).setTraceListener((TraceListener)_saxon1TraceListener);
		
		if (_messageOutputStream != null)
		{
		  me = new MessageEmitter();
		  me.setWriter(new PrintWriter(_messageOutputStream));
		  me.setOutputStream(_messageOutputStream);
		  ((Controller)t).setMessageEmitter(me);
		}
		
		((Controller)t).setLineNumbering(true);

		return t;
	}



public Vector getLocalVariables()
{


	if (this._styleStack == null) 
	{
		//System.out.println("null stack ");
		return null;
	}

	if (this._styleStack.size() == 0) 
	{
		//System.out.println("empty stack ");
		return null;
	}


	Vector locvars = new Vector();

	Saxon1StyleStackItem ssi = (Saxon1StyleStackItem)this._styleStack.peek();

	if (ssi == null)
		return null;


	Enumeration[] en = ssi._styleElement.getVariableNames();


	//System.out.println("Local Variables ");
	while (en[1].hasMoreElements())
	{
		String elname = (String)en[1].nextElement();

		elname = elname.substring(1, elname.length());

		locvars.add(getVariable(ssi, elname));		
	}

	return locvars;

}

public Vector getGlobalVariables()
{

	if (this._styleStack == null) 
	{
		//System.out.println("null stack ");
		return null;
	}

	if (this._styleStack.size() == 0) 
	{
		//System.out.println("empty stack ");
		return null;
	}

	Vector glvars = new Vector();


	Saxon1StyleStackItem ssi = (Saxon1StyleStackItem)this._styleStack.peek();

	if (ssi == null)
		return null;

	Enumeration[] en = ssi._styleElement.getVariableNames();


	//System.out.println("Global  Variables ");
	while (en[0].hasMoreElements())
	{
		String elname = (String)en[0].nextElement();

		elname = elname.substring(1, elname.length());


		glvars.add(getVariable(ssi, elname));
		

	}

	return glvars;
}


public XSLTVariable getVariable(Saxon1StyleStackItem ssi, String variableName)
{
	Context ctx = ssi._context;
	
	int type = XSLTVariable.XSLT_TYPE_UNKNOWN;
	String value = "";
	
	XSLTVariable var = new XSLTVariable(variableName);
	
	
	if (ctx == null)
		return var;

	StyleElement se = ssi._styleElement;

	Controller c = ctx.getController();
	StaticContext sc = ctx.getStaticContext();

	if (sc == null)
		return var;

	try
	{
	  Bindery bry = ctx.getBindery();

	  Binding bng = se.getVariableBinding(sc.makeNameCode(variableName, false));

	  if (bng != null)
	  {
	    	Value v = bry.getValue(bng, bry.getFrameId());

	    	if (v != null)
	    	{
	    	  var.setValue(v.asString());
	    	}
	    	
	    	var.setType(convertSaxon1Type(v.getDataType()));
	  }

	}
	catch (Exception ex) {}

	return var;

	}


public String getVariableValue(String variableName) {
	return this._saxon1TraceListener.getVariableValue(variableName);
}

public static int convertSaxon1Type(int saxonType)
{
  switch (saxonType)
  {
    case Value.BOOLEAN:
      return XSLTVariable.XSLT_TYPE_BOOLEAN;
    
    case Value.NUMBER:
      return XSLTVariable.XSLT_TYPE_NUMBER;
    
    case Value.STRING:
      return XSLTVariable.XSLT_TYPE_STRING;
    
    case Value.NODESET:
      return XSLTVariable.XSLT_TYPE_NODESET;
    
    case Value.OBJECT:
      return XSLTVariable.XSLT_TYPE_OBJECT;
    
    default:
      return XSLTVariable.XSLT_TYPE_UNKNOWN;
    
  }
}

public void cleanUp() {
	super.cleanUp();

	//T Curley 04.12.06
	//fixed a null pointer exception bug with the debugger
	if(t != null) {
		((Controller)t).removeTraceListener( _saxon1TraceListener);
		((Controller)t).setMessageEmitter( null);
		((Controller)t).clearDocumentPool();
	}

	if(me != null) {
		me.setWriter( null);
		me.setOutputStream( null);
	}
	
	me = null;
	t = null;
}
}

