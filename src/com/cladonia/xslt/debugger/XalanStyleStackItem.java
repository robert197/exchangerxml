
package com.cladonia.xslt.debugger;

import org.apache.xalan.templates.ElemTemplateElement;


public class XalanStyleStackItem 
{

//Context _context = null;
ElemTemplateElement _styleElement = null;
String _elname = null;
boolean _isTemplateElement = false;

public XalanStyleStackItem(/*Context ctx,*/ ElemTemplateElement se ) 
{
	//this._context = ctx;
	this._styleElement = se;
	this._elname = se.getNodeName();

	if (se instanceof ElemTemplateElement)
		this._isTemplateElement = true;

}





/**
 * @return Returns the _elname.
 */
public String get_elname()
{
  return _elname;
}
/**
 * @param _elname The _elname to set.
 */
public void set_elname(String _elname)
{
  this._elname = _elname;
}
/**
 * @return Returns the _isTemplateElement.
 */
public boolean is_isTemplateElement()
{
  return _isTemplateElement;
}
/**
 * @param templateElement The _isTemplateElement to set.
 */
public void set_isTemplateElement(boolean templateElement)
{
  _isTemplateElement = templateElement;
}
/**
 * @return Returns the _styleElement.
 */
public ElemTemplateElement get_styleElement()
{
  return _styleElement;
}
/**
 * @param element The _styleElement to set.
 */
public void set_styleElement(ElemTemplateElement element)
{
  _styleElement = element;
}
}