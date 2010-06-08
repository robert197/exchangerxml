package com.cladonia.xslt.debugger;


public class XSLTStatus {

	String _stylesheetFilename = null;
	int _stylesheetLineNumber = -1; 
	int _stylesheetColumnNumber = -1; 
	String _stylesheetDisplayName = null;
	String _stylesheetLocalName = null;
	String _stylesheetURI = null;
	boolean _isStylesheetStartTag = true;
	
	String _inputFilename = null;
	int _inputLineNumber = -1; 
	int _inputColumnNumber = -1; 
	String _inputDisplayName = null;
	String _inputLocalName = null;
	String _inputURI = null;
	boolean _isInputStartTag = true;

	
	public XSLTStatus( String stylesheetFilename, 
				int stylesheetLineNumber, 
				int stylesheetColumnNumber, 
				String stylesheetDisplayName,
				String stylesheetLocalName,
				String stylesheetURI,
				boolean isStylesheetStartTag,
				String inputFilename,
				int inputLineNumber, 
				int inputColumnNumber, 
				String inputDisplayName,
				String inputLocalName,
				String inputURI,
				boolean isInputStartTag
				) 
	
	
	{
	 _stylesheetFilename = stylesheetFilename;
	 _stylesheetLineNumber = stylesheetLineNumber; 
	 _stylesheetColumnNumber = stylesheetColumnNumber; 
	 _stylesheetDisplayName = stylesheetDisplayName;
	 _stylesheetLocalName = stylesheetLocalName;
	 _stylesheetURI = stylesheetURI;
	 _isStylesheetStartTag = isStylesheetStartTag;
	
	 _inputFilename = inputFilename;
	 _inputLineNumber = inputLineNumber; 
	 _inputColumnNumber = inputColumnNumber; 
	 _inputDisplayName = inputDisplayName;
	 _inputLocalName = inputLocalName;
	 _inputURI = inputURI;
	 _isInputStartTag = isInputStartTag;
	}


	public String getStylesheetFilename() {
		return _stylesheetFilename;
	}

	public int getStylesheetLineNumber() {
		return _stylesheetLineNumber;
	}
	public int getStylesheetColumnNumber() {
		return _stylesheetColumnNumber;
	}
	public String getStylesheetDisplayName() {
		return _stylesheetDisplayName;
	}
	public String getStylesheetLocalName() {
		return _stylesheetLocalName;
	}
	public String getStylesheetURI() {
		return _stylesheetURI;
	}

	public boolean isStylesheetStartTag() {
		return _isStylesheetStartTag;
	}


	public String getInputFilename() {
		return _inputFilename;
	}

	public int getInputLineNumber() {
		return _inputLineNumber;
	}
	public int getInputColumnNumber() {
		return _inputColumnNumber;
	}
	public String getInputDisplayName() {
		return _inputDisplayName;
	}
	public String getInputLocalName() {
		return _inputLocalName;
	}
	public String getInputURI() {
		return _inputURI;
	}

	public boolean isInputStartTag() {
		return _isInputStartTag;
	}





}