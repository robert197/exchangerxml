
package com.cladonia.xslt.debugger;

public class DefaultStackItem
{

String _name = null;
String _filename = null;
int _lineNum = -1;
int _colNum = -1;  
int _type = -1;

public DefaultStackItem(String name, String filename, int lineNum, int colNum ) 
{
	this._name = name;
	this._filename = filename;
	this._lineNum = lineNum;
	this._colNum  = colNum;
}



public DefaultStackItem(String name, String filename, int lineNum, int colNum, int type ) 
{
	this._name = name;
	this._filename = filename;
	this._lineNum = lineNum;
	this._colNum  = colNum;
	this._type = type;
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
 * @return Returns the _type.
 */
public int get_type()
{
  return _type;
}
/**
 * @param _type The _type to set.
 */
public void set_type(int _type)
{
  this._type = _type;
}
}