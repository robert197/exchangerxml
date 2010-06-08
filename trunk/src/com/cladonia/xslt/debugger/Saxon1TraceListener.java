package com.cladonia.xslt.debugger;

import java.util.Stack;
import java.util.HashMap;

import java.util.Properties;
import com.icl.saxon.Bindery;
import com.icl.saxon.Binding;
import com.icl.saxon.Context;
import com.icl.saxon.Controller;
import com.icl.saxon.NodeHandler;
import com.icl.saxon.expr.StaticContext;
import com.icl.saxon.expr.Value;
import com.icl.saxon.om.NodeInfo;
import com.icl.saxon.style.StyleElement;
import com.icl.saxon.trace.TraceListener;
import com.icl.saxon.style.XSLTemplate;
import com.icl.saxon.output.Outputter;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStreamWriter;

public class Saxon1TraceListener implements TraceListener {

  String indent = "";
  Saxon1Debugger _XSLTDebugger = null;
  int _currentStylesheetLineNumber = -1;
  int _currentStylesheetColumnNumber = -1;
  int _currentInputLineNumber = -1;
  int _currentInputColumnNumber = -1;
  int _previousStylesheetLineNumber = -1;
  int _previousInputLineNumber = -1;
  
  boolean _isInputBreakpoint = false;
  boolean _isStylesheetStartTag = true;
  boolean _isInputStartTag = true;
  
  String _inputDisplayName = null;
  String _inputLocalName = null;
  String _inputURI = null;
 
  String _stylesheetDisplayName = null;
  String _stylesheetLocalName = null;
  String _stylesheetURI = null;
 

  String _currentStylesheetFilename = null;
  String _previousStylesheetFilename = null;
  String _currentInputFilename = null;
  String _previousInputFilename = null;
  
  boolean _atStartOfStylesheet = true;
  boolean _atStartOfInput = true;
  
  Stack _styleStack = null;
  Saxon1StyleStackItem _ssiTemplateEnd = null;
  Stack _xslTemplateStack = null;
  Stack _xmlStack = null;
  Stack _outStack = null;
  Stack _mixedStack = null;
 
  Stack _xslTraceStack = null;
  Stack _xmlTraceStack = null;
  Stack _outTraceStack = null;
  Stack _mixedTraceStack = null;
  
  
  HashMap _xslTemplatesProfileMap = null;
  
  HashMap _xslFilesMap = null;

  boolean newdoc = false;
  
  
  public void open()
  {
    _atStartOfStylesheet = true;
    _atStartOfInput = true;
    
    _xslFilesMap = new HashMap();
    
    this._XSLTDebugger.onStart();
    
    
  }

 
  public void close()
  {
    this._XSLTDebugger.onEnd();
  }

 
  public void toplevel(NodeInfo element)
  {
    StyleElement e = (StyleElement) element;
 
    // TODO should we handle global variables/params here
    //if (element.getDisplayName() != "xsl:template")
    //	enter(element, null);
  }

 
  
  public void enterSource(NodeHandler handler, Context context)
  {
    NodeInfo element = context.getContextNodeInfo();
    

    // handle stylesheets turning up as input
    if (_xslFilesMap.get(Breakpoint.normalizeFilename(element
        .getSystemId())) != null)
      return;

    this._currentInputFilename = Breakpoint.normalizeFilename(element
        .getSystemId());
    this._currentInputLineNumber = element.getLineNumber();

    _isInputStartTag = true;
    _inputLocalName = element.getLocalName();
    _inputURI = element.getURI();
    _inputDisplayName = element.getDisplayName();
   
    if (element.getNodeType() == NodeInfo.ELEMENT)
    {
      DefaultStackItem dsi = new DefaultStackItem(_inputDisplayName,
          this._currentInputFilename, this._currentInputLineNumber,
          this._currentInputColumnNumber, XSLTDebugger.STACK_ITEM_XML_START);
      
      _xmlStack.push(dsi);
      _mixedStack.push(dsi);
      
      
      
      if (this._XSLTDebugger.isTracingEnabled())
      {
        _xmlTraceStack.push(dsi);
        _mixedTraceStack.push(dsi);     
        
      }
      
    }
    
   
    if (this._XSLTDebugger.getCommand() == XSLTDebugger.COMMAND_RUN_TO_END)
    {
      // do nothing!!!
    }
    else if (this._XSLTDebugger.isInputBreakpoint(this._currentInputFilename,
        this._currentInputLineNumber))
    {
      // dont stop yet (only stop in stylesheet)
      _isInputBreakpoint = true;
      //stoppedOn(XSLTDebugger.STOPPED_ON_INPUT_BREAKPOINT, this._currentInputFilename,
      //    this._currentInputLineNumber, element, false);
    } 
 
    this._previousInputLineNumber = this._currentInputLineNumber;
    this._previousInputFilename = this._currentInputFilename;
  }

  /**
   * Called after a node of the source tree got processed
   */
  public void leaveSource(NodeHandler handler, Context context)
  {
    NodeInfo element = context.getContextNodeInfo();
    
    this._currentInputFilename = Breakpoint.normalizeFilename(element
        .getSystemId());
    this._currentInputLineNumber = element.getLineNumber();
    
    if (_xslFilesMap.get(this._currentInputFilename) != null)
      return;

    _isInputStartTag = false;
    _inputLocalName = element.getLocalName();
    _inputURI = element.getURI();
    _inputDisplayName = element.getDisplayName();
    
    if (element.getNodeType() == NodeInfo.ELEMENT)
    {
      _xmlStack.pop();
      _mixedStack.pop();
      
      
      if (this._XSLTDebugger.isTracingEnabled())
      {

      DefaultStackItem dsi = new DefaultStackItem(_inputDisplayName,
          this._currentInputFilename, this._currentInputLineNumber,
          this._currentInputColumnNumber, XSLTDebugger.STACK_ITEM_XML_END);
      
      _xmlTraceStack.push(dsi);
      _mixedTraceStack.push(dsi);

      }
    }
    
    _isInputBreakpoint = false;

    /*
     * indent = indent.substring(0, indent.length() - 1);
     * System.err.println(indent + " </Source> <!-- " +
     * Navigator.getPath(context.getContextNodeInfo()) + " -->");
     */
  }

  /**
   * Called when an element of the stylesheet gets processed
   */
  public void enter(NodeInfo element, Context context)
  {
    if (!(element instanceof StyleElement)) { 
      return; 
    }
    
   _isStylesheetStartTag = true;

    StyleElement styleElement = (StyleElement) element;
    
    _stylesheetLocalName = element.getLocalName();
    _stylesheetURI = element.getURI();
    _stylesheetDisplayName = element.getDisplayName();
    
    //System.out.println(localName + "; " + displayName + "; " );
       
    this._currentStylesheetFilename = Breakpoint.normalizeFilename(element
        .getSystemId());
    this._currentStylesheetLineNumber = element.getLineNumber();
    this._currentStylesheetColumnNumber = -1;
    
    _xslFilesMap.put(this._currentStylesheetFilename, this._currentStylesheetFilename);
    
    if (styleElement instanceof XSLTemplate)
    {
      String match = null;
      match = styleElement.getAttributeValue("match");
      String name = null;
      name = styleElement.getAttributeValue("name");
      String priority = null;
      priority = styleElement.getAttributeValue("priority");
      String mode = null;
      mode = styleElement.getAttributeValue("mode");
      
      //System.out.println(name + "; " + match + "; " + priority + "; "
      // + mode );
      
      XSLTemplateStackItem xtsi = new XSLTemplateStackItem(name, match,
          priority, mode, this._currentStylesheetFilename,
          this._currentStylesheetLineNumber,
          this._currentStylesheetColumnNumber);
      _xslTemplateStack.push(xtsi);
      
      String styleElementStr = _stylesheetDisplayName;
      if (name != null) styleElementStr += " name=\"" + name + "\"";
      if (match != null) styleElementStr += " match=\"" + match + "\"";
      if (priority != null)
          styleElementStr += " priority=\"" + priority + "\"";
      if (mode != null) styleElementStr += " mode=\"" + mode + "\"";
      
      DefaultStackItem dsi = new DefaultStackItem(styleElementStr,
          this._currentStylesheetFilename, this._currentStylesheetLineNumber,
          this._currentStylesheetColumnNumber, XSLTDebugger.STACK_ITEM_XSL_START);
      _mixedStack.push(dsi);
      
      if (this._XSLTDebugger.isTracingEnabled())
      {
        _xslTraceStack.push(dsi);
        _mixedTraceStack.push(dsi);
      }
      
      Integer count = (Integer)_xslTemplatesProfileMap.get(styleElementStr);
      
      // update profile for this template
      if (count == null)
      {
        _xslTemplatesProfileMap.put(styleElementStr, new Integer(1));
      }
      else
      {
        _xslTemplatesProfileMap.put(styleElementStr, new Integer(count.intValue() + 1));
      }
      
    } 
    else if (_stylesheetURI.equals(XSLTDebugger.XSL_URI) && _stylesheetLocalName.equals("element"))
    {
      String name = "";
      name = styleElement.getAttributeValue("name");
      //System.out.println("Element: " + name);
      DefaultStackItem dsi = new DefaultStackItem(name,
          this._currentStylesheetFilename, this._currentStylesheetLineNumber,
          this._currentStylesheetColumnNumber, XSLTDebugger.STACK_ITEM_OUT_START);
      _outStack.push(dsi);
      _mixedStack.push(dsi);
      
      if (this._XSLTDebugger.isTracingEnabled())
      {
        _outTraceStack.push(dsi);
      	_mixedTraceStack.push(dsi);
      }
    } 
    else if (_stylesheetURI.equals(XSLTDebugger.XSL_URI))
    {
      DefaultStackItem dsi = new DefaultStackItem(_stylesheetDisplayName,
          this._currentStylesheetFilename, this._currentStylesheetLineNumber,
          this._currentStylesheetColumnNumber, XSLTDebugger.STACK_ITEM_XSL_START);
      //System.out.println("push on mixedstack");
      _mixedStack.push(dsi);
      
      if (this._XSLTDebugger.isTracingEnabled())
      {
        _xslTraceStack.push(dsi);
        _mixedTraceStack.push(dsi);
      } 
      
    } 
    else if (!(_stylesheetURI.equals(XSLTDebugger.XSL_URI)))
    {
      //System.out.println("Element: " + localName);
      DefaultStackItem dsi = new DefaultStackItem(_stylesheetDisplayName,
          this._currentStylesheetFilename, this._currentStylesheetLineNumber,
          this._currentStylesheetColumnNumber, XSLTDebugger.STACK_ITEM_OUT_START);
      _outStack.push(dsi);
      _mixedStack.push(dsi);
      
      if (this._XSLTDebugger.isTracingEnabled())
      {
        _outTraceStack.push(dsi);
        _mixedTraceStack.push(dsi);
      }
    }
    
    
    Saxon1StyleStackItem ssi = new Saxon1StyleStackItem(context,
        (StyleElement) element);
    this._styleStack.push(ssi);
    
    if (this._XSLTDebugger.isOutputRedirected())
    {
	    if (_stylesheetURI.equals(XSLTDebugger.XSL_URI) && _stylesheetLocalName.equals("document"))
	    {
	      handleOutputDocumentOpen(element, context);
	    } 
	    else if (newdoc == true)
	    {
	      // must wait till first instruction after xsl:document to actually change
	      changeOutputDestination(element, context);
	    }
    }
    
    if (handleImmediateStop() == true)
    {
      this._XSLTDebugger.command(XSLTDebugger.COMMAND_STEP);
    }
    
    
    if (this._XSLTDebugger.getCommand() == XSLTDebugger.COMMAND_RUN_TO_END)
    {
      //DO NOTHING
    }
    else if ( (_isInputBreakpoint == true) || this._XSLTDebugger.isStylesheetBreakpoint(_currentStylesheetFilename,
        _currentStylesheetLineNumber))
    {
      
      XSLTStatus status = new XSLTStatus(
          this._currentStylesheetFilename,
          this._currentStylesheetLineNumber,
          -1,
          _stylesheetDisplayName,
          _stylesheetLocalName,
          _stylesheetURI,
          _isStylesheetStartTag,
          this._currentInputFilename,
          this._currentInputLineNumber,
          -1,
          _inputDisplayName,
          _inputLocalName,
          _inputURI,
          _isInputStartTag         
          );      
      
      stoppedOn(XSLTDebugger.STOPPED_ON_STYLESHEET_BREAKPOINT, status);
      
      _isInputBreakpoint = false;

    } 
    else if (this._XSLTDebugger.getCommand() == XSLTDebugger.COMMAND_STEP)
    {
      XSLTStatus status = new XSLTStatus(
          this._currentStylesheetFilename,
          this._currentStylesheetLineNumber,
          -1,
          _stylesheetDisplayName,
          _stylesheetLocalName,
          _stylesheetURI,
          _isStylesheetStartTag,
          this._currentInputFilename,
          this._currentInputLineNumber,
          -1,
          _inputDisplayName,
          _inputLocalName,
          _inputURI,
          _isInputStartTag         
          );
      
      stoppedOn(XSLTDebugger.STOPPED_ON_STYLESHEET_LINECHANGE, status);
      
    }
    
    /*
    else if ((this._XSLTDebugger.getCommand() == XSLTDebugger.COMMAND_STEP)
        && (this._previousStylesheetLineNumber != this._currentStylesheetLineNumber
            || this._previousStylesheetFilename == null || !(this._previousStylesheetFilename
            .equals(this._currentStylesheetFilename))))
    {
      stoppedOn(XSLTDebugger.STOPPED_ON_STYLESHEET_LINECHANGE,
          this._currentStylesheetFilename, this._currentStylesheetLineNumber,
          element, false);
      
    }
    */
    
    this._previousStylesheetLineNumber = this._currentStylesheetLineNumber;
    this._previousStylesheetFilename = this._currentStylesheetFilename;

    
  }

  /**
   * Called after an element of the stylesheet got processed
   */
  public void leave(NodeInfo element, Context context)
  {
    if (!(element instanceof StyleElement)) { 
      return; 
    }
    
    _isStylesheetStartTag = false;
    
    StyleElement styleElement = (StyleElement) element;
    
    String previousStylesheetLocalName = _stylesheetLocalName;
    String previousStylesheetURI = _stylesheetURI;
    
    _stylesheetLocalName = element.getLocalName();
    _stylesheetURI = element.getURI();
    _stylesheetDisplayName = element.getDisplayName();

    this._currentStylesheetFilename = Breakpoint.normalizeFilename(element
        .getSystemId());
    this._currentStylesheetLineNumber = element.getLineNumber();
    this._currentStylesheetColumnNumber = -1;
    
    if (styleElement instanceof XSLTemplate)
    {
      //System.out.println(name + "; " + match + "; " + priority + "; "
      // + mode );
      _xslTemplateStack.pop();
      _mixedStack.pop();

      if (this._XSLTDebugger.isTracingEnabled())
      {
       
	      String match = null;
	      match = styleElement.getAttributeValue("match");
	      String name = null;
	      name = styleElement.getAttributeValue("name");
	      String priority = null;
	      priority = styleElement.getAttributeValue("priority");
	      String mode = null;
	      mode = styleElement.getAttributeValue("mode");
	            
	      String styleElementStr = _stylesheetDisplayName;
	      if (name != null) styleElementStr += " name=\"" + name + "\"";
	      if (match != null) styleElementStr += " match=\"" + match + "\"";
	      if (priority != null)
	          styleElementStr += " priority=\"" + priority + "\"";
	      if (mode != null) styleElementStr += " mode=\"" + mode + "\"";
	      
	      
	      
	      DefaultStackItem dsi = new DefaultStackItem(styleElementStr,
	          this._currentStylesheetFilename, this._currentStylesheetLineNumber,
	          this._currentStylesheetColumnNumber, XSLTDebugger.STACK_ITEM_XSL_END);
	       
	      _xslTraceStack.push(dsi);
	      _mixedTraceStack.push(dsi);
      }
      
      
    } else if (_stylesheetURI.equals(XSLTDebugger.XSL_URI) && _stylesheetLocalName.equals("element"))
    {
      _outStack.pop();
      _mixedStack.pop();

      if (this._XSLTDebugger.isTracingEnabled())
      {
	
	      DefaultStackItem dsi = new DefaultStackItem(_stylesheetDisplayName,
	          this._currentStylesheetFilename, this._currentStylesheetLineNumber,
	          this._currentStylesheetColumnNumber, XSLTDebugger.STACK_ITEM_XML_END);
	      
	      _outTraceStack.push(dsi);
	      _mixedTraceStack.push(dsi);
      }      
      
    } else if (_stylesheetURI.equals(XSLTDebugger.XSL_URI))
    {
      _mixedStack.pop();
      
      if (this._XSLTDebugger.isTracingEnabled())
      {
	
	      DefaultStackItem dsi = new DefaultStackItem(_stylesheetDisplayName,
	          this._currentStylesheetFilename, this._currentStylesheetLineNumber,
	          this._currentStylesheetColumnNumber, XSLTDebugger.STACK_ITEM_XSL_END);
	      
	      _xslTraceStack.push(dsi);
	      _mixedTraceStack.push(dsi);
      }
      
    } else if (!(_stylesheetURI.equals(XSLTDebugger.XSL_URI)))
    {
      _outStack.pop();
      _mixedStack.pop();
      
      if (this._XSLTDebugger.isTracingEnabled())
      {

          DefaultStackItem dsi = new DefaultStackItem(_stylesheetDisplayName,
          this._currentStylesheetFilename, this._currentStylesheetLineNumber,
          this._currentStylesheetColumnNumber, XSLTDebugger.STACK_ITEM_OUT_END);
	      
	      _outTraceStack.push(dsi);
	      _mixedTraceStack.push(dsi);
      }            
    }
    
    
    Saxon1StyleStackItem ssi = (Saxon1StyleStackItem) this._styleStack.peek();

    if (handleImmediateStop() == true)
    {
      //System.out.println("***set command to STEP");
      this._XSLTDebugger.command(XSLTDebugger.COMMAND_STEP);
    }
    if (this._XSLTDebugger.isOutputRedirected())
    {
	    if (_stylesheetURI.equals(XSLTDebugger.XSL_URI) && _stylesheetLocalName.equals("document"))
	    {
	      //String href = element.getAttributeValue("", "href");
	      handleOutputDocumentClose(element, context);
	
	    }
    }
    switch (this._XSLTDebugger.getCommand())
    {
      case XSLTDebugger.COMMAND_STEP_OVER:
         if (this._ssiTemplateEnd != null && this._ssiTemplateEnd == ssi)
        {
          this._XSLTDebugger.command(XSLTDebugger.COMMAND_STEP);
        }
        break;
      case XSLTDebugger.COMMAND_STEP_OUT_OF_TEMPLATE:
        if (this._ssiTemplateEnd != null && this._ssiTemplateEnd == ssi)
        {
         this._XSLTDebugger.command(XSLTDebugger.COMMAND_STEP);
         }
        break;
        
        
      case XSLTDebugger.COMMAND_STEP:  
        
    
        if (previousStylesheetLocalName.equals(_stylesheetLocalName) 
               && previousStylesheetURI.equals(_stylesheetURI) )
		{ 
          // do nothing - empty element so don't report to gui twice
		}
       else
       {
		  XSLTStatus status = new XSLTStatus(
		      this._currentStylesheetFilename,
		      this._currentStylesheetLineNumber,
          -1,
          _stylesheetDisplayName,
          _stylesheetLocalName,
          _stylesheetURI,
          _isStylesheetStartTag,
          this._currentInputFilename,
          this._currentInputLineNumber,
          -1,
          _inputDisplayName,
          _inputLocalName,
          _inputURI,
          _isInputStartTag         
          );      
   
          stoppedOn(XSLTDebugger.STOPPED_ON_STYLESHEET_LINECHANGE, status);
		}
		break;        
        
    }

    this._styleStack.pop();

  }

  
  
  String getModeName(Context context)
  {
    int nameCode = context.getMode().getNameCode();
    if (nameCode == -1)
    {
      return "";
    } else
    {
      return context.getController().getNamePool().getDisplayName(nameCode);
    }
  }

  public void setXSLTDebugger(Saxon1Debugger debugger)
  {
    this._XSLTDebugger = debugger;
  }

  public void setStyleStack(Stack stack)
  {
    this._styleStack = stack;
  }

  public void setXMLStack(Stack stack)
  {
    this._xmlStack = stack;
  }

  public void setOutStack(Stack stack)
  {
    this._outStack = stack;
  }

  public void setMixedStack(Stack stack)
  {
    this._mixedStack = stack;
  }

  public void setXSLTemplateStack(Stack stack)
  {
    this._xslTemplateStack = stack;
  }

  
  public void setXMLTraceStack(Stack stack)
  {
    this._xmlTraceStack = stack;
  }

  public void setOutTraceStack(Stack stack)
  {
    this._outTraceStack = stack;
  }

  public void setMixedTraceStack(Stack stack)
  {
    this._mixedTraceStack = stack;
  }

  public void setXSLTraceStack(Stack stack)
  {
    this._xslTraceStack = stack;
  }
  
  public void setXSLTemplateProfileMap(HashMap xslTemplatesProfileMap)
  {
    this._xslTemplatesProfileMap = xslTemplatesProfileMap;
  }

  public String getVariableValue(String variableName)
  {
    Context ctx = ((Saxon1StyleStackItem) this._styleStack.peek())._context;
    StyleElement se = ((Saxon1StyleStackItem) this._styleStack.peek())._styleElement;
    Controller c = ctx.getController();
    StaticContext sc = ctx.getStaticContext();
    if (sc == null) return null;
    try
    {
      Bindery bry = ctx.getBindery();
      Binding bng = se.getVariableBinding(sc.makeNameCode(variableName, false));
      if (bng != null)
      {
        Value v = bry.getValue(bng, bry.getFrameId());
        if (v != null) return v.asString();
      }
    } catch (Exception ex)
    {
    }
    return null;
  }

  public void changeOutputDestination(NodeInfo element, Context context)
  {
   
    Outputter op = null;
    Controller con = context.getController();
 
    Properties props = context.getController().getOutputProperties();
    StreamResult sr = new StreamResult(new OutputStreamWriter(
        this._XSLTDebugger._outputStream));
    try
    {
      con.changeOutputDestination(props, this._XSLTDebugger._sresult);
    } catch (Exception ex)
    {
      System.out.println("****exception" + ex.getMessage());
    }
    newdoc = false;
  }

  public void handleOutputDocumentClose(NodeInfo element, Context context)
  {
    this._XSLTDebugger.onCloseOutputDocument("");
  }

  public void handleOutputDocumentOpen(NodeInfo element, Context context)
  {
    


    
    newdoc = true;
    //changeOutputDestination(element, context);
    //System.err.println(element.getDisplayName() + " " +
    // element.getAttributeValue("", "href") + " " + element.getSystemId()
    // + " " + element.getLineNumber());
    String href = element.getAttributeValue("", "href");
    String temp = href;
    int n1 = 0;
    int n2 = 0;
    String fn = "";
    //System.err.println(temp + " n1 = " + temp.indexOf("{") + " n2 = " +
    // temp.indexOf("}") );
    try
    {
      Controller c = context.getController();
      StaticContext sc = context.getStaticContext();
      while ((n1 = temp.indexOf("{")) >= 0 && (n2 = temp.indexOf("}")) > n1)
      {
        String variableName = temp.substring(n1 + 2, n2);
        String val = "";
        //System.err.println("variableName = ***" + variableName +
        // "****");
        fn += temp.substring(0, n1);
        //System.err.println("fn = " + fn);
        Bindery bry = context.getBindery();
        Binding bng = ((StyleElement) element).getVariableBinding(sc
            .makeNameCode(variableName, false));
        if (bng != null)
        {
          Value v = bry.getValue(bng, bry.getFrameId());
          if (v != null) val = v.asString();
        }
        //System.err.println("variableName = " + variableName + " val
        // = " + val);
        fn += val;
        //System.err.println("fn = " + fn);
        temp = temp.substring(n2 + 1);
      }
    } catch (Exception ex)
    {
    }
    fn += temp;
    System.err.println("output filename = " + fn);
    this._XSLTDebugger.onOpenOutputDocument(fn);
  }

  public synchronized boolean handleImmediateStop()
  {
     boolean isStop = this._XSLTDebugger.handleImmediateStop();
  
     return isStop;
  }

  public synchronized void stoppedOn(int stopReason, XSLTStatus status)
  {
    
    //this._XSLTDebugger.stoppedOn(stopReason, status._stylesheetFilename, status._stylesheetLineNumber, status._stylesheetDisplayName, status._isStylesheetStartTag);
    this._XSLTDebugger.stoppedOn(stopReason,status);

    //this._XSLTDebugger.stoppedOn(stopReason, status);
    
    switch (this._XSLTDebugger.getCommand())
    {
      case XSLTDebugger.COMMAND_STEP_OVER:
        if ((status._stylesheetURI.equals(XSLTDebugger.XSL_URI) && status._stylesheetLocalName.equals("apply-templates"))
            || (status._stylesheetURI.equals(XSLTDebugger.XSL_URI) && status._stylesheetLocalName.equals("call-template")))
        {
          this._ssiTemplateEnd = (Saxon1StyleStackItem) this._styleStack.peek();
        } 
        else
        {
          this._XSLTDebugger.command(XSLTDebugger.COMMAND_STEP);
        }
        break;
        
      case XSLTDebugger.COMMAND_STEP_OUT_OF_TEMPLATE:
        for (int i = this._styleStack.size() - 1; i >= 0; i--)
        {
          Saxon1StyleStackItem ssi = (Saxon1StyleStackItem) this._styleStack.get(i);
          if (ssi._isTemplateElement == true)
          {
            //if (i == this._styleStack.size() - 1)
           // {
            //  continue;
           // } 
            //else
            //{
              this._ssiTemplateEnd = ssi;
              break;
            //}
          }
        }
        break;
        
      default:
        break;
    }
  }
  
  public void cleanUp() {
    _XSLTDebugger = null;
    
    _inputDisplayName = null;
    _inputLocalName = null;
    _inputURI = null;
   
    _stylesheetDisplayName = null;
    _stylesheetLocalName = null;
    _stylesheetURI = null;
   
    _currentStylesheetFilename = null;
    _previousStylesheetFilename = null;
    _currentInputFilename = null;
    _previousInputFilename = null;
    
    _styleStack = null;
    _ssiTemplateEnd = null;
    _xslTemplateStack = null;
    _xmlStack = null;
    _outStack = null;
    _mixedStack = null;
   
    _xslTraceStack = null;
    _xmlTraceStack = null;
    _outTraceStack = null;
    _mixedTraceStack = null;
    
    _xslTemplatesProfileMap = null;
    
    _xslFilesMap = null;
  }
}