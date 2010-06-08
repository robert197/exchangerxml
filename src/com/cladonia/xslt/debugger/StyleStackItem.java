
package com.cladonia.xslt.debugger;

import com.icl.saxon.Context;
import com.icl.saxon.style.StyleElement;
import com.icl.saxon.style.XSLTemplate;

public class StyleStackItem 
{

Context _context = null;
StyleElement _styleElement = null;
String _elname = null;
boolean _isTemplateElement = false;

public StyleStackItem(Context ctx, StyleElement se ) 
{
	this._context = ctx;
	this._styleElement = se;
	//this._elname = elname;

	if (se instanceof XSLTemplate)
		this._isTemplateElement = true;

}





/**
 * @return Returns the _context.
 */
public Context get_context()
{
  return _context;
}
/**
 * @param _context The _context to set.
 */
public void set_context(Context _context)
{
  this._context = _context;
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
public StyleElement get_styleElement()
{
  return _styleElement;
}
/**
 * @param element The _styleElement to set.
 */
public void set_styleElement(StyleElement element)
{
  _styleElement = element;
}
}