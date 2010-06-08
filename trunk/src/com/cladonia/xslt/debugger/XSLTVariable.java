package com.cladonia.xslt.debugger;


public class XSLTVariable {
	boolean _enabled = false;
	String _name = null;
	String _value= "";
	int _type = XSLT_TYPE_UNKNOWN;

	public static final int XSLT_TYPE_STRING = 1;
	public static final int XSLT_TYPE_BOOLEAN = 2;
	public static final int XSLT_TYPE_NUMBER = 3;
	public static final int XSLT_TYPE_NODESET = 4;
	public static final int XSLT_TYPE_OBJECT = 5;
	public static final int XSLT_TYPE_ANY = 6;
	public static final int XSLT_TYPE_UNKNOWN = -1;
	
	
	public XSLTVariable( String name) {
		this._name = name;
	}

	public XSLTVariable( String name, String value, int type) {
		this._name = name;
		this._value= value;
		this._type= type;
	}

	public String getName() {
		return _name;
	}

	public String getValue() {
		return _value;
	}

	public int getType() {
		return _type;
	}

	public void setValue(String value) {
		_value = value;
	}

	public void setName(String name) {
		_name = name;
	}

	public  void setType(int type) {
		_type = type;
	}

}