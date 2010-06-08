package com.cladonia.xslt.debugger;

import org.apache.xalan.trace.TraceListenerEx2;

import java.util.Stack;
import java.util.HashMap;

import org.apache.xalan.trace.GenerateEvent;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.trace.EndSelectionEvent;
import org.apache.xalan.trace.TracerEvent;
import org.w3c.dom.Node;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xalan.templates.ElemTemplateElement;
import javax.xml.transform.SourceLocator;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemElement;
import org.apache.xpath.NodeSet;
import org.apache.xalan.lib.NodeInfo;

public class XalanTraceListener implements TraceListenerEx2 {

  XalanDebugger _XSLTDebugger = null;

  String _currentStylesheetFilename = null;
  int _currentStylesheetLineNumber = -1;
  int _currentStylesheetColumnNumber = -1;
  String _previousStylesheetFilename = null;
  int _previousStylesheetLineNumber = -1;
  int _previousStylesheetColumnNumber = -1;
  
  String _currentInputFilename = null;
  int _currentInputLineNumber = -1;
  int _currentInputColumnNumber = -1;
  String _previousInputFilename = null;
  int _previousInputLineNumber = -1;
  int _previousInputColumnNumber = -1;

  
  boolean _isInputBreakpoint = false;
  boolean _isStylesheetStartTag = true;
  boolean _isInputStartTag = true;
  
  String _inputDisplayName = null;
  String _inputLocalName = null;
  String _inputURI = null;
 
  String _stylesheetDisplayName = null;
  String _stylesheetLocalName = null;
  String _stylesheetURI = null;
 
  
  boolean _atStartOfStylesheet = true;
  boolean _atStartOfInput = true;

  Stack _styleStack = null;
  XalanStyleStackItem _ssiTemplateEnd = null;

  Stack _localVarsStack = null;

  Stack _xslTemplateStack = null;
  Stack _xmlStack = null;
  Stack _outStack = null;
  Stack _mixedStack = null;

  Stack _xslTraceStack = null;
  Stack _xmlTraceStack = null;
  Stack _outTraceStack = null;
  Stack _mixedTraceStack = null;

  HashMap _xslTemplatesProfileMap = null;

  boolean newdoc = false;
  
  private static boolean DEBUG = false;
  private static boolean DEBUG1 = false;
  

  public XalanTraceListener()

  {

  }

  public void generated(GenerateEvent ev)
  {
    // ignored
  }

  public void selected(SelectionEvent se)
  {
    

    Node curr = se.m_sourceNode;

    String nodeName = curr.getNodeName();
    
    SourceLocator locator = ((DTMNodeProxy) curr).getDTM().getSourceLocatorFor(
        ((DTMNodeProxy) curr).getDTMNodeNumber());

    String tempFilename = locator.getSystemId();
    
    if (DEBUG1) System.out.println("selected file:" + locator.getSystemId());
    if (DEBUG1) System.out.println("selected line num:" + locator.getLineNumber());
    if (DEBUG1) System.out.println("selected node line num:" + NodeInfo.lineNumber(new NodeSet(curr)));
    if (DEBUG1) System.out.println("selected node line num def:" + NodeInfo.lineNumber(new NodeSet()));

    if (tempFilename == null)
    {
      if (this._previousInputFilename != null)
      {
        this._currentInputFilename = this._previousInputFilename;
      }
    } else
      this._currentInputFilename = tempFilename;

    this._currentInputFilename = Breakpoint
        .normalizeFilename(this._currentInputFilename);
    this._currentInputLineNumber = locator.getLineNumber();

    _isInputStartTag = true;
    _inputLocalName = curr.getLocalName();

    try {
    _inputURI = curr.getNamespaceURI();
    }
    catch (Exception ex)
    {
      _inputURI = null;
    
    }
    _inputDisplayName = "";
    if (curr.getPrefix() != null && curr.getPrefix() != "")
      _inputDisplayName = curr.getPrefix() + ":";
    _inputDisplayName += _inputLocalName;
    
    
    if (this._XSLTDebugger.getCommand() == XSLTDebugger.COMMAND_RUN_TO_END)
    {
      // do nothing!!!
    }
    else if (this._XSLTDebugger.isInputBreakpoint(this._currentInputFilename,
        this._currentInputLineNumber))
    {
      _isInputBreakpoint = true;
    } 

    this._previousInputLineNumber = this._currentInputLineNumber;
    this._previousInputColumnNumber = this._currentInputColumnNumber;
    this._previousInputFilename = this._currentInputFilename;


  }

  public void selectEnd(EndSelectionEvent ese)
      throws javax.xml.transform.TransformerException
  {
 
    Node curr = ese.m_sourceNode;

    SourceLocator locator = ((DTMNodeProxy) curr).getDTM().getSourceLocatorFor(
        ((DTMNodeProxy) curr).getDTMNodeNumber());

    String tempFilename = locator.getSystemId();

    if (tempFilename == null)
    {
      if (this._previousInputFilename != null)
      {
        this._currentInputFilename = this._previousInputFilename;
      }
    } else
      this._currentInputFilename = tempFilename;

    this._currentInputFilename = Breakpoint
        .normalizeFilename(this._currentInputFilename);
    this._currentInputLineNumber = locator.getLineNumber();

    _isInputStartTag = false;
    _inputLocalName = curr.getLocalName();
    try {
      _inputURI = curr.getNamespaceURI();
      }
      catch (Exception ex)
      {
        _inputURI = null;
      
      }    _inputDisplayName = "";
    if (curr.getPrefix() != null && curr.getPrefix() != "")
      _inputDisplayName = curr.getPrefix() + ":";
    _inputDisplayName += _inputLocalName;
   
    _isInputBreakpoint = false;
 
  }

  public void trace(TracerEvent te)
  {
    
 
    ElemTemplateElement element = te.m_styleNode;
    String tempFilename = element.getSystemId();

    Node curr = te.m_sourceNode;
  
     SourceLocator locator = ((DTMNodeProxy) curr).getDTM().getSourceLocatorFor(
        ((DTMNodeProxy) curr).getDTMNodeNumber());

     
     if (DEBUG1) System.out.println("trace file:" + locator.getSystemId());
     if (DEBUG1) System.out.println("trace line num:" + locator.getLineNumber());
     if (DEBUG1) System.out.println("trace node line num:" + NodeInfo.lineNumber(new NodeSet(curr)));
     if (DEBUG1) System.out.println("trace node line num def:" + NodeInfo.lineNumber(new NodeSet()));

    String tempXMLFilename = locator.getSystemId();

    if (tempXMLFilename == null)
    {
      if (this._previousInputFilename != null)
      {
        this._currentInputFilename = this._previousInputFilename;
      }
    } else
      this._currentInputFilename = tempXMLFilename;

    this._currentInputFilename = Breakpoint
        .normalizeFilename(this._currentInputFilename);
    this._currentInputLineNumber = locator.getLineNumber();
    
    
    String xmlNodeName = null;
    if (null != curr.getNodeName())
      xmlNodeName = curr.getNodeName();
    
    _isStylesheetStartTag = true;

    _stylesheetLocalName = element.getLocalName(); 
    try {
      _stylesheetURI = element.getNamespaceURI();
      }
      catch (Exception ex)
      {
        _stylesheetURI = null;
      
      }
     _stylesheetDisplayName = "";
    if (curr.getPrefix() != null && curr.getPrefix() != "")
      _stylesheetDisplayName = curr.getPrefix() + ":";
    _stylesheetDisplayName += _stylesheetLocalName;
    

    if (tempFilename == null)
    {
      if (this._previousStylesheetFilename != null)
      {
        this._currentStylesheetFilename = this._previousStylesheetFilename;
      }
    } else
      this._currentStylesheetFilename = tempFilename;

    this._currentStylesheetFilename = Breakpoint
        .normalizeFilename(this._currentStylesheetFilename);
    this._currentStylesheetLineNumber = element.getLineNumber();

    
    XalanStyleStackItem ssi = new XalanStyleStackItem(
        (ElemTemplateElement) element);
    this._styleStack.push(ssi);
    
    
    if (te.m_styleNode.getNodeName().equals("template")
        || te.m_styleNode.getNodeName().equals("param")
        || te.m_styleNode.getNodeName().equals("variable"))
    {
      this._localVarsStack.push(te.m_styleNode);
    }
    
    int xslToken = te.m_styleNode.getXSLToken();

    switch (xslToken)
    {
      case Constants.ELEMNAME_TEMPLATE:
      {
        // Manage XML Stack here so as to keep it in sync with templates (too many
        // events from xalan for start/end inputs and not always balanced

      
        DefaultStackItem xmldsi = new DefaultStackItem(xmlNodeName,
            this._currentInputFilename, this._currentInputLineNumber,
            this._currentInputColumnNumber, XSLTDebugger.STACK_ITEM_XML_START);
   
        _xmlStack.push(xmldsi);
        _mixedStack.push(xmldsi);

        if (this._XSLTDebugger.isTracingEnabled())
        {
	        _xmlTraceStack.push(xmldsi);
	        _mixedTraceStack.push(xmldsi);
        }        
        
        ElemTemplate et = (ElemTemplate) element;

        String match = null;
        if (null != et.getMatch())
        {
          match = et.getMatch().getPatternString();
        }

         
        String name = null;
        if (null != et.getName())
          name = et.getName().toString();
        
         
        String priority = null;
        //if (null != et.getPriority())
          priority = new Double(et.getPriority()).toString();
        
          
        String mode = null;
        if (null != et.getMode())
          mode = et.getMode().toString();
      
        XSLTemplateStackItem xtsi = new XSLTemplateStackItem(name, match,
            priority, mode, this._currentStylesheetFilename,
            this._currentStylesheetLineNumber,
            this._currentStylesheetColumnNumber);
        
       
        _xslTemplateStack.push(xtsi);

        String styleElementStr = "xsl:template";
        if (name != null) styleElementStr += " name=\"" + name + "\"";
        if (match != null) styleElementStr += " match=\"" + match + "\"";
        if (priority != null)
            styleElementStr += " priority=\"" + priority + "\"";
        if (mode != null) styleElementStr += " mode=\"" + mode + "\"";

        DefaultStackItem xsldsi = new DefaultStackItem(styleElementStr,
            this._currentStylesheetFilename, this._currentStylesheetLineNumber,
            this._currentStylesheetColumnNumber,
            XSLTDebugger.STACK_ITEM_XSL_START);
        
        _mixedStack.push(xsldsi);

        if (this._XSLTDebugger.isTracingEnabled())
        {
	        _xslTraceStack.push(xsldsi);
	        _mixedTraceStack.push(xsldsi);
		}
        

        Integer count = (Integer) _xslTemplatesProfileMap.get(styleElementStr);

        if (count == null)
        {
          _xslTemplatesProfileMap.put(styleElementStr, new Integer(1));
        } else
        {
          _xslTemplatesProfileMap.put(styleElementStr, new Integer(count
              .intValue() + 1));
        }
    
      }
      break;

      case Constants.ELEMNAME_LITERALRESULT :
      {
      
      ElemLiteralResult elr = (ElemLiteralResult) element;

       
        String nodeName = null;
        if (null != elr.getNodeName())
          nodeName = elr.getNodeName();
 
        DefaultStackItem dsi = new DefaultStackItem(nodeName,
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
      break;
 
      case Constants.ELEMNAME_ELEMENT :
      {
      
      ElemElement ee = (ElemElement) element;

       
        String elName = null;
        if (null != ee.getName())
          elName = ee.getName().toString();

        
   
        DefaultStackItem dsi = new DefaultStackItem(elName,
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
      break;
         
      case Constants.ELEMNAME_TEXTLITERALRESULT :
        break;
        
        
      default:
      {
        // Assume everything else is xsl:something_or_other
        
        String name = "xsl:" + te.m_styleNode.getNodeName();
               
        DefaultStackItem dsi = new DefaultStackItem(name,
            this._currentStylesheetFilename, this._currentStylesheetLineNumber,
            this._currentStylesheetColumnNumber,
            XSLTDebugger.STACK_ITEM_XSL_START);
        
        _mixedStack.push(dsi);
        
        if (this._XSLTDebugger.isTracingEnabled())
        {       
        	_xslTraceStack.push(dsi);
       		_mixedTraceStack.push(dsi);
       	}
        
      
        
        
      }
      break;

    }

    if (handleImmediateStop() == true)
    {
      this._XSLTDebugger.command(XSLTDebugger.COMMAND_STEP);
    }

    if (this._XSLTDebugger.getCommand() == XSLTDebugger.COMMAND_RUN_TO_END)
    {
      // do nothing!!!
    }
    else if ((_isInputBreakpoint == true) || this._XSLTDebugger.isStylesheetBreakpoint(_currentStylesheetFilename,
        _currentStylesheetLineNumber))
    {

      XSLTStatus status = new XSLTStatus(
          this._currentStylesheetFilename,
          this._currentStylesheetLineNumber,
          this._currentStylesheetColumnNumber,
          _stylesheetDisplayName,
          _stylesheetLocalName,
          _stylesheetURI,
          _isStylesheetStartTag,
          this._currentInputFilename,
          this._currentInputLineNumber,
          this._currentInputColumnNumber,
          _inputDisplayName,
          _inputLocalName,
          _inputURI,
          _isInputStartTag         
          );      
      
      stoppedOn(XSLTDebugger.STOPPED_ON_STYLESHEET_BREAKPOINT, status);
      
      _isInputBreakpoint = false;

    } else if ((this._XSLTDebugger.getCommand() == XSLTDebugger.COMMAND_STEP))
        
    {
 
      XSLTStatus status = new XSLTStatus(
          this._currentStylesheetFilename,
          this._currentStylesheetLineNumber,
          this._currentStylesheetColumnNumber,
          _stylesheetDisplayName,
          _stylesheetLocalName,
          _stylesheetURI,
          _isStylesheetStartTag,
          this._currentInputFilename,
          this._currentInputLineNumber,
          this._currentInputColumnNumber,
          _inputDisplayName,
          _inputLocalName,
          _inputURI,
          _isInputStartTag         
          );      
      
      stoppedOn(XSLTDebugger.STOPPED_ON_STYLESHEET_LINECHANGE, status);
      

    }

    this._previousStylesheetLineNumber = this._currentStylesheetLineNumber;
    this._previousStylesheetFilename = this._currentStylesheetFilename;

    
  }

  public void traceEnd(TracerEvent te)
  {
   
    ElemTemplateElement element = te.m_styleNode;

    String elname = element.getNodeName();

    XalanStyleStackItem ssi = (XalanStyleStackItem) this._styleStack.peek();

    if (elname != ssi._elname)
    {

       this._styleStack.pop();
      ssi = (XalanStyleStackItem) this._styleStack.peek();

    }
    String tempFilename = element.getSystemId();

    
    
    Node curr = te.m_sourceNode;
    
      SourceLocator locator = ((DTMNodeProxy) curr).getDTM().getSourceLocatorFor(
          ((DTMNodeProxy) curr).getDTMNodeNumber());

      String tempXMLFilename = locator.getSystemId();

      if (tempXMLFilename == null)
      {
        if (this._previousInputFilename != null)
        {
          this._currentInputFilename = this._previousInputFilename;
        }
      } else
        this._currentInputFilename = tempXMLFilename;

      this._currentInputFilename = Breakpoint
          .normalizeFilename(this._currentInputFilename);
      this._currentInputLineNumber = locator.getLineNumber();
      
      
      String xmlNodeName = null;
      if (null != curr.getNodeName())
        xmlNodeName = curr.getNodeName();
      

   

      
      _isStylesheetStartTag = false;

      _stylesheetLocalName = element.getLocalName();  
      try {
        _stylesheetURI = element.getNamespaceURI();
        }
        catch (Exception ex)
        {
          _stylesheetURI = "";
        
        }
      _stylesheetDisplayName = "";
      if (curr.getPrefix() != null && curr.getPrefix() != "")
        _stylesheetDisplayName = curr.getPrefix() + ":";
      _stylesheetDisplayName += _stylesheetLocalName;
      
    
      String previousStylesheetLocalName = _stylesheetLocalName;
      String previousStylesheetURI = _stylesheetURI;

    
    if (tempFilename == null)
    {
      if (this._previousStylesheetFilename != null)
      {
        this._currentStylesheetFilename = this._previousStylesheetFilename;
      }
    } else
      this._currentStylesheetFilename = tempFilename;

    this._currentStylesheetFilename = Breakpoint
        .normalizeFilename(this._currentStylesheetFilename);
    this._currentStylesheetLineNumber = element.getLineNumber();

    
    int xslToken = te.m_styleNode.getXSLToken();

    switch (xslToken)
    {
      case Constants.ELEMNAME_TEMPLATE:
      {     
        // Manage XML Stack here so as to keep it in sync with templates (too many
        // events from xalan for start/end inputs and not always balanced

        
        _xmlStack.pop();
        _mixedStack.pop();
 
         if (this._XSLTDebugger.isTracingEnabled())
        {               
           DefaultStackItem xmldsi = new DefaultStackItem(xmlNodeName,
               this._currentInputFilename, this._currentInputLineNumber,
               this._currentInputColumnNumber, XSLTDebugger.STACK_ITEM_XML_END);       

           _xmlTraceStack.push(xmldsi);
        	_mixedTraceStack.push(xmldsi);
 		}
	       
         
        
      _xslTemplateStack.pop();
      _mixedStack.pop();
     
      
      
        ElemTemplate et = (ElemTemplate) element;

        String match = null;
        if (null != et.getMatch())
        {
          match = et.getMatch().getPatternString();
        }

        
        String name = null;
        if (null != et.getName())
          name = et.getName().toString();
        
        
        String priority = null;
        //if (null != et.getPriority())
          priority = new Double(et.getPriority()).toString();
        
                 
        String mode = null;
        if (null != et.getMode())
          mode = et.getMode().toString();
        
        String styleElementStr = "xsl:template";
        if (name != null) styleElementStr += " name=\"" + name + "\"";
        if (match != null) styleElementStr += " match=\"" + match + "\"";
        if (priority != null)
            styleElementStr += " priority=\"" + priority + "\"";
        if (mode != null) styleElementStr += " mode=\"" + mode + "\"";


         if (this._XSLTDebugger.isTracingEnabled())
        {                       
           DefaultStackItem dsi = new DefaultStackItem(styleElementStr,
               this._currentStylesheetFilename, this._currentStylesheetLineNumber,
               this._currentStylesheetColumnNumber,
               XSLTDebugger.STACK_ITEM_XSL_END);

           _xslTraceStack.push(dsi);
	        _mixedTraceStack.push(dsi);
		}

         
      } 
      break;
  
      case Constants.ELEMNAME_LITERALRESULT :
      {
       
      ElemLiteralResult elr = (ElemLiteralResult) element;
       
        String nodeName = null;
        if (null != elr.getNodeName())
          nodeName = elr.getNodeName();
        
        
        _outStack.pop();
        _mixedStack.pop();
        
         if (this._XSLTDebugger.isTracingEnabled())
        {               
           DefaultStackItem dsi2 = new DefaultStackItem(nodeName,
               this._currentStylesheetFilename, this._currentStylesheetLineNumber,
               this._currentStylesheetColumnNumber, XSLTDebugger.STACK_ITEM_OUT_END);
    
           _outTraceStack.push(dsi2);
        _mixedTraceStack.push(dsi2);
		}
		
  
      }
        break;
 
      case Constants.ELEMNAME_TEXTLITERALRESULT :
        break;
                
        
      default:
      {
        // Assume everything else is xsl:something or other
        
        String name = "xsl:" + te.m_styleNode.getNodeName();
              
        DefaultStackItem dsi = new DefaultStackItem(name,
            this._currentStylesheetFilename, this._currentStylesheetLineNumber,
            this._currentStylesheetColumnNumber,
            XSLTDebugger.STACK_ITEM_XSL_END);
        
        _mixedStack.push(dsi);
        
         if (this._XSLTDebugger.isTracingEnabled())
        {               
        
        _xslTraceStack.push(dsi);
        _mixedTraceStack.push(dsi);
        }
     
      }
        break;
    }
        
     if (handleImmediateStop() == true)
    {
      this._XSLTDebugger.command(XSLTDebugger.COMMAND_STEP);
    }

    if (element.getNodeName().equals("document"))
    {

      //this._XSLTDebugger.onCloseOutputDocument(href);
    }

    //System.out.println("getCommand");

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


    if (te.m_styleNode.getNodeName().equals("template"))
    {
      while (this._localVarsStack.size() - 1 >= 0)
      {
         ElemTemplateElement ete = (ElemTemplateElement) this._localVarsStack
            .get(this._localVarsStack.size() - 1);

        this._localVarsStack.pop();

        if (ete.getNodeName().equals("template"))
        {
           break;
        }
      }

    }

  }

  public void setXSLTDebugger(XalanDebugger debugger)
  {
    this._XSLTDebugger = debugger;
  }

  public void setStyleStack(Stack stack)
  {
    this._styleStack = stack;
  }

  public void setLocalVarsStack(Stack stack)
  {
    this._localVarsStack = stack;
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

  
  
  
  public void setStylesheetFilename(String stylesheetFilename)
  {
    this._currentStylesheetFilename = stylesheetFilename;
  }

  public synchronized boolean handleImmediateStop()
  {

    //System.out.println("style COMMAND_STOP_IMMEDIATELY");

    boolean isStop = this._XSLTDebugger.handleImmediateStop();

 
    //if (isStop == true) System.out.println("style COMMAND_STOP_IMMEDIATELY");

    return isStop;

  }

  public void handleOutputDocumentOpen()
  {

  }

  public void handleOutputDocumentClose()
  {

  }

  public synchronized void stoppedOn(int stopReason, XSLTStatus status)
  {

    //this._XSLTDebugger.stoppedOn(stopReason, status._stylesheetFilename, status._stylesheetLineNumber, status._stylesheetDisplayName, status._isStylesheetStartTag);
    this._XSLTDebugger.stoppedOn(stopReason,status);
    
    switch (this._XSLTDebugger.getCommand())
    {

      case XSLTDebugger.COMMAND_STEP_OVER:
        //if ((nodeName.equals("apply-templates"))
        //    || (nodeName.equals("call-template")))
          
          if ((/*status._stylesheetURI.equals(XSLTDebugger.XSL_URI) &&*/ status._stylesheetLocalName.equals("apply-templates"))
          || (/*status._stylesheetURI.equals(XSLTDebugger.XSL_URI) &&*/ status._stylesheetLocalName.equals("call-template")))
          
        {
          this._ssiTemplateEnd = (XalanStyleStackItem) this._styleStack.peek();
        } else
        {
          this._XSLTDebugger.command(XSLTDebugger.COMMAND_STEP);
        }
        break;

      case XSLTDebugger.COMMAND_STEP_OUT_OF_TEMPLATE:
        for (int i = this._styleStack.size() - 1; i >= 0; i--)
        {
           XalanStyleStackItem ssi = (XalanStyleStackItem) this._styleStack
              .get(i);
          if (ssi._isTemplateElement == true)
          {
            if (i == this._styleStack.size() - 1)
            {
              continue;
            } else
            {
               this._ssiTemplateEnd = ssi;
              break;
            }
          }
        }
        break;

      default:
        break;

    }

  }

}