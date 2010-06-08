package com.cladonia.xslt.debugger;

import java.io.File;
import java.net.URL;

import com.cladonia.xngreditor.URLUtilities;

public class Breakpoint {
	boolean enabled = false;
	private String filename = null;
	private int linenumber = -1;
	
	public Breakpoint( String filename, int linenumber, boolean enabled) {
		this.filename = normalizeFilename( filename);
		this.linenumber = linenumber;
		this.enabled = enabled;
	}

	public void setEnabled( boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled()	{
		return this.enabled;
	}
	
	public void toggle()	{
		enabled = !enabled;
	}

	public String getFilename() {
		return filename;
	}

	public int getLineNumber() {
		return linenumber;
	}
	
	public String toString() {
		return "Breakpoint[filename:"+filename+", line:"+linenumber+", enabled:"+enabled+"]";
	}
	
	public boolean sameFile( String filename) {
		String othername = normalizeFilename( filename);
		
		return this.filename.equals( othername);
	}
	
	public static String normalizeFilename( String filename) {
		// normalize the file name.
		URL url = null;
		
		try {
			url = new URL( filename);
		} catch (Exception e) {
			File file = new File( filename);
			
			try { 
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			} catch (Exception x) {
				x.printStackTrace();
			}
		}

		// url should never be null, if null change normalization method!
		return URLUtilities.encodeURL( url.toString());
	}
}