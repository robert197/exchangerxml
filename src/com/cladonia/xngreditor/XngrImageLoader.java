/*
 * $Id: ImageLoader.java,v 1.3 2005/08/11 08:10:48 edankert Exp $
 *
 * Copyright (c) 2002 - 2005, Edwin Dankert
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, 
 *	 this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright 
 * 	 notice, this list of conditions and the following disclaimer in the 
 *	 documentation and/or other materials provided with the distribution. 
 * * Neither the name of 'Edwin Dankert' nor the names of its contributors 
 *	 may  be used to endorse or promote products derived from this software 
 *	 without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR 
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cladonia.xngreditor;

import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

import com.cladonia.util.loader.ExtensionClassLoader;

/**
 * Loads Images from file and stores the images in a list for future reference.
 *
 * @version	$Revision: 1.3 $, $Date: 2005/08/11 08:10:48 $
 * @author Dogsbay
 */
public class XngrImageLoader {
	private static final boolean DEBUG = false;
	private static XngrImageLoader loader = null;
	private ClassLoader classLoader = null;
	
	private HashMap images = null;
	
	/**
	 * Constructs the ImageLoader object. 
	 */
	public XngrImageLoader() {
		this.classLoader = ExchangerEditor.getStaticExtensionClassLoader();
		images = new HashMap();
		
		if(DEBUG) System.out.println("XngrImageLoader::XngrImageLoader - classLoader: "+classLoader);
	}

	/**
	 * Returns the single reference to the ImageLoader.
	 *
	 * @return the ImageLoader singleton.
	 */
	public static XngrImageLoader get() {
		if(DEBUG) System.out.println("***********Getting the new imageLoader**********");
		if ( loader == null) {
			loader = new XngrImageLoader();
			return(loader);
		}

		return loader;
	}

	/**
	 * Gets an image for the string supplied. If the image cannot be found 
	 * in the list of images already loaded, the image is loaded from the 
	 * class path.
	 * 
	 * @param name the name of the image.
	 *
	 * @return the image.
	 */
	public ImageIcon getImage( String name) {
		if (DEBUG) System.out.println("ImageLoader.getImage("+name+")");
		ImageIcon icon = (ImageIcon)images.get(name);
		
		if (icon == null) {
			if (DEBUG) System.out.println("classLoader.findResource: "+((ExtensionClassLoader)classLoader).findResource(name));
			icon = new ImageIcon(classLoader.getResource(name));
			images.put(name, icon);
		}
		
		return icon;
	}

	/**
	 * Gets an image for the url supplied. The image is only loaded from 
	 * file if the image cannot be found in the list of images already loaded.
	 * 
	 * @param url the url to the image.
	 *
	 * @return the image.
	 */
	public ImageIcon getImage(URL url) {
		if (DEBUG) System.out.println( "ImageLoader.getImage( "+url+")");
		ImageIcon icon = (ImageIcon)images.get(url.toString());
		
		if (icon == null) {
			icon = new ImageIcon(url);
			images.put(url.toString(), icon);
		}
		
		return icon;
	}
} 
