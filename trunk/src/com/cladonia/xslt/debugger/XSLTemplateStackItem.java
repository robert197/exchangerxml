
package com.cladonia.xslt.debugger;

public class XSLTemplateStackItem
{

String _name = null;
String _match = null;
String _priority = null;
String _mode = null;
String _filename = null;
int _lineNum = -1;
int _colNum = -1;  


public XSLTemplateStackItem(String name, String match, String priority, String mode, String filename, int lineNum, int colNum ) 
{
	this._name = name;
	this._match = match;
	this._priority = priority;
	this._mode = mode;
	this._filename = filename;
	this._lineNum = lineNum;
	this._colNum  = colNum;
}





/**
 * @return Returns the _colNum.
 */
public int get_colNum()
{
  return _colNum;
}
/**
 * @param num The _colNum to set.
 */
public void set_colNum(int num)
{
  _colNum = num;
}
/**
 * @return Returns the _filename.
 */
public String get_filename()
{
  return _filename;
}
/**
 * @param _filename The _filename to set.
 */
public void set_filename(String _filename)
{
  this._filename = _filename;
}
/**
 * @return Returns the _lineNum.
 */
public int get_lineNum()
{
  return _lineNum;
}
/**
 * @param num The _lineNum to set.
 */
public void set_lineNum(int num)
{
  _lineNum = num;
}
/**
 * @return Returns the _match.
 */
public String get_match()
{
  return _match;
}
/**
 * @param _match The _match to set.
 */
public void set_match(String _match)
{
  this._match = _match;
}
/**
 * @return Returns the _mode.
 */
public String get_mode()
{
  return _mode;
}
/**
 * @param _mode The _mode to set.
 */
public void set_mode(String _mode)
{
  this._mode = _mode;
}
/**
 * @return Returns the _name.
 */
public String get_name()
{
  return _name;
}
/**
 * @param _name The _name to set.
 */
public void set_name(String _name)
{
  this._name = _name;
}
/**
 * @return Returns the _priority.
 */
public String get_priority()
{
  return _priority;
}
/**
 * @param _priority The _priority to set.
 */
public void set_priority(String _priority)
{
  this._priority = _priority;
}
}