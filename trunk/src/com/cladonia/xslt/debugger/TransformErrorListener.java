package com.cladonia.xslt.debugger;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;


public class TransformErrorListener implements ErrorListener
{
  DebugController  _debugController ;
  
  public TransformErrorListener (DebugController  debugController )
  {
    this._debugController = debugController;
  }
    
  public void error(TransformerException ex)
    throws TransformerException
  {
    this._debugController.onException(ex);
  }

  public void warning(TransformerException ex)
    throws TransformerException
  {
    this._debugController.onException(ex);
  }

  public void fatalError(TransformerException ex)
    throws TransformerException
  {
    this._debugController.onException(ex);
  }

}
