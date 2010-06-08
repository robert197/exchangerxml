package com.cladonia.xml;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class XngrURLUtilities {

	/**
	 * Initially created to hold the file.toURL method
	 * which causes deprecated warnings. Will be able to 
	 * fix it here once in the future.
	 * @param file
	 * @return
	 */
	public static URL getURLFromFile(File file) throws MalformedURLException {
		if(file != null) {
			return(file.toURL());
		}
		else {
			return(null);
		}
	}
}
