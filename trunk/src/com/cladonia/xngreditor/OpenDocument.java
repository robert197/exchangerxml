/*
 * Created on 14-Mar-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.xngreditor;

import com.cladonia.xml.ExchangerDocument;

/**
 * @author gmcgoldrick
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OpenDocument{
  String name = null;
  ExchangerDocument doc = null;
  
  public OpenDocument(String name, ExchangerDocument doc)
  {
    this.name = name;
    this.doc = doc;
  }
  
  public String toString()
  {
    return name;
  }

  public ExchangerDocument getDocument()
  {
    return doc;
  }

}