
package com.cladonia.xslt.debugger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


public class SAXErrorHandler implements ErrorHandler
{
  DebugController  _debugController ;
  
  public SAXErrorHandler(DebugController  debugController )
  {
    this._debugController = debugController;  }
  
  public void error(SAXParseException ex)
  {
    this._debugController.onException(ex);
  }

  public void warning(SAXParseException ex)
  {
    this._debugController.onException(ex);
  }

  public void fatalError(SAXParseException ex)
  {
    this._debugController.onException(ex);
  }

}
