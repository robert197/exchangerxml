
package com.cladonia.xslt.debugger;




import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.xalan.processor.TransformerFactoryImpl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.XalanProperties;
import org.apache.xalan.trace.TraceManager;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.VariableStack;

import java.util.Stack;
import java.util.Vector;

import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.ElemTemplateElement;

public class XalanDebugger extends XSLTDebugger {

	private static boolean DEBUG = true;

public TransformerFactory  tf = null;
StylesheetRoot sr = null;
TransformerImpl ti = null;
XalanTraceListener _xalanTraceListener = null;

Stack _localVarsStack = new Stack();

public XalanDebugger( BreakpointList stylesheetBreakpoints, BreakpointList inputBreakpoints) 
{
	super( stylesheetBreakpoints, inputBreakpoints);

	
}


  public TransformerFactory getFactory()
  {
      tf = new org.apache.xalan.processor.TransformerFactoryImpl();

	return tf;
  }


public Transformer initializeTransformer(Transformer t)
{
      TransformerFactoryImpl tfi = (TransformerFactoryImpl)tf;

      tfi.setAttribute(XalanProperties.SOURCE_LOCATION, Boolean.TRUE);
      //tfi.setAttribute(TransformerFactoryImpl.FEATURE_SOURCE_LOCATION, 
      //    Boolean.TRUE);

      _xalanTraceListener = new XalanTraceListener();


		_xalanTraceListener.setXSLTDebugger(this);
		_xalanTraceListener.setStyleStack(_styleStack);
		_xalanTraceListener.setLocalVarsStack(_localVarsStack);

		_xalanTraceListener.setXSLTemplateStack(_xslTemplateStack);
		_xalanTraceListener.setXMLStack(_xmlStack);
		_xalanTraceListener.setOutStack(_outStack);
		_xalanTraceListener.setMixedStack(_mixedStack);
		_xalanTraceListener.setXSLTraceStack(_xslTraceStack);
		_xalanTraceListener.setXMLTraceStack(_xmlTraceStack);
		_xalanTraceListener.setOutTraceStack(_outTraceStack);
		_xalanTraceListener.setMixedTraceStack(_mixedTraceStack);

		_xalanTraceListener.setXSLTemplateProfileMap(_xslTemplatesProfileMap);

		
//System.out.println("getStylesheetFilename = " + this.getStylesheetFilename());

		_xalanTraceListener.setStylesheetFilename(this.getStylesheetFilename());

      ti = (TransformerImpl)t;

	sr = ti.getStylesheet();

      TraceManager trMgr = ti.getTraceManager();


      try {
        trMgr.addTraceListener(_xalanTraceListener );
      }
      catch (Exception e) {
      }

	return t;
}


public Vector getLocalVariables()
{


	if (this._localVarsStack == null) 
	{
		//System.out.println("null localVarsStack ");
		return null;
	}

	if (this._localVarsStack .size() == 0) 
	{
		//System.out.println("empty localVarsStack ");
		return null;
	}

	Vector locvars = new Vector();


//System.out.println("**** lOCAL vARIABLES STAck size = " + this._localVarsStack.size());

	
	VariableStack vs = ti.getXPathContext().getVarStack();


	for (int i=this._localVarsStack.size()-1; i>=0; i--)
	{

		ElemTemplateElement ete  = (ElemTemplateElement) this._localVarsStack.get( i);


//System.out.println("ete.getNodeName() = " + ete.getNodeName());

		if (ete.getNodeName().equals("template"))
			break;
		else if (ete.getNodeName().equals("variable") || ete.getNodeName().equals("param"))
            {

			ElemVariable variable = (ElemVariable)ete;
	
			String elname = variable.getName().getLocalName();
			String elvalue = "";


			int index = variable.getIndex();
			int frame = vs.getStackFrame();


//System.out.println("index  = " + index );

			XObject xo = null;

			if (!(variable.getIsTopLevel()))
			{
				try {
					xo = vs.getLocalVariable(index, frame);
				}
				catch (Exception ex) {}

			}


			if (xo != null)
			{
//System.out.println("xo not null    type = " + xo.getType());

				switch (xo.getType())
				{

					case XObject.CLASS_BOOLEAN:
					case XObject.CLASS_NUMBER:
					case XObject.CLASS_STRING:
					case XObject.CLASS_UNKNOWN:
//System.out.println("xo to string " + xo.getType());
						elvalue = xo.toString();
						break;

					case XObject.CLASS_NODESET:
//System.out.println("xo NODESET");

						elvalue =((XNodeSet)xo).xstr().toString();
						break;

					case XObject.CLASS_UNRESOLVEDVARIABLE:
//System.out.println("xo UNRESOLVEDVARIABLE");
						//elvalue = xo.xstr().toString();
					  	elvalue = "UNRESOLVED";
						break;

					default:
//System.out.println("xo other");
						break;

				}
			}


			//System.out.println( elname +  "   =   " +  elvalue);

			locvars.add(new XSLTVariable(elname, elvalue, XSLTVariable.XSLT_TYPE_STRING));

		
		}

	}


	return locvars;
}


public Vector getGlobalVariables()
{

/*
	if (this._styleStack == null) 
	{
		System.out.println("null stack ");
		return;
	}

	if (this._styleStack.size() == 0) 
	{
		System.out.println("empty stack ");
		return;
	}
*/


//System.out.println("**** GLOBAL vARIABLES");


	Vector glvars = new Vector();

	
	if (sr == null)
	  	return glvars;
	    
	Vector variables = sr.getVariablesAndParamsComposed();

	if (variables == null)
	  	return glvars;
	
	VariableStack vs = ti.getXPathContext().getVarStack();

	if (vs == null)
	  	return glvars;

	for ( int i = 0; i < variables.size(); i++) {
			ElemVariable variable = (ElemVariable)variables .elementAt( i);

		if (variable ==  null)
		  continue;
			
		String elname = null;
		if (variable.getName() != null)
		  elname = variable.getName().getLocalName();
		
		String elvalue = "";

		XSLTVariable var = new XSLTVariable(elname); 

		int index = variable.getIndex();
		int frame = vs.getStackFrame();


//System.out.println("index  = " + index );

		XObject xo = null;

		if (variable.getIsTopLevel())
		{
//System.out.println("top level element");

			xo = vs.elementAt(index);

		}

		int type = XObject.CLASS_UNKNOWN;

			if (xo != null)
			{
//System.out.println("xo not null    type = " + xo.getType());
			  type = xo.getType();
			  
				switch (type)
				{

					case XObject.CLASS_BOOLEAN:
					case XObject.CLASS_NUMBER:
					case XObject.CLASS_STRING:
					case XObject.CLASS_UNKNOWN:
						if(DEBUG) System.out.println("xo to string " + xo.getType());
						elvalue = xo.toString();
						break;

					case XObject.CLASS_NODESET:
						if(DEBUG) System.out.println("xo NODESET value: "+((XNodeSet)xo).xstr().toString());

						elvalue =((XNodeSet)xo).xstr().toString();
						break;

					case XObject.CLASS_UNRESOLVEDVARIABLE:
						if(DEBUG) System.out.println("xo UNRESOLVEDVARIABLE");
						if(DEBUG) elvalue = xo.xstr().toString();
					  	elvalue = "UNRESOLVED";

						break;

					default:
						if(DEBUG) System.out.println("xo other");
						break;

				}
			}
			


		//System.out.println( elname +  "   =   " +  elvalue);

	    var.setType(convertXalanType(type));
  	    var.setValue(elvalue);

		
		glvars.add(var);


	}


	return glvars;


}


public String getVariableValue(String variableName) {
	if(DEBUG) System.out.println("XalanDebugger::getVariableValue("+variableName+")");
	
	return null;
}


public static int convertXalanType(int xalanType)
{
  switch (xalanType)
  {
    case XObject.CLASS_BOOLEAN:
      return XSLTVariable.XSLT_TYPE_BOOLEAN;
    
    case XObject.CLASS_NUMBER:
      return XSLTVariable.XSLT_TYPE_NUMBER;
    
    case XObject.CLASS_STRING:
      return XSLTVariable.XSLT_TYPE_STRING;
    
    case XObject.CLASS_NODESET:
      return XSLTVariable.XSLT_TYPE_NODESET;
    
    case XObject.CLASS_UNKNOWN:
      return XSLTVariable.XSLT_TYPE_UNKNOWN;
    
    default:
      return XSLTVariable.XSLT_TYPE_UNKNOWN;
    
  }
}


public void cleanUp() {
	super.cleanUp();
	
	//T Curley 04.12.06
	//fixed a null pointer exception bug with the debugger
	if(ti != null) {
		TraceManager trMgr = ti.getTraceManager();

	    try {
	      trMgr.removeTraceListener(_xalanTraceListener );
	    } catch (Exception e) {}
	}
    
    ti = null;
    sr = null;
    _xalanTraceListener = null;
}
}


