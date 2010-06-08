/*
 * $Id: Loader.java,v 1.1.1.1 2004/03/26 12:26:16 edankert Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.util.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.StringTokenizer;

/**
 * A generic application loader.
 *
 * @version	$Revision: 1.1.1.1 $, $Date: 2004/03/26 12:26:16 $
 * @author Dogsbay
 */
public class Loader {

    static final boolean VERBOSE = false;
    
    public static void main( String[] args) throws Exception {
        new Loader( args);
    }
    
    public Loader( String[] args) throws Exception {    
		initProperties();
		
//        String repositories = "xngr-editor.jar"+";"+getLibraryPath()+";"+getExtensionPath();
//        String mainClass = "com.cladonia.xngreditor.Main";

        if (VERBOSE) System.out.println("-------------------- Loading --------------------");

        ExtensionClassLoader classLoader = new ExtensionClassLoader( this.getClass().getClassLoader());

        if ( System.getProperty( "endorsed.libraries") != null) {
	        StringTokenizer st = new StringTokenizer( System.getProperty( "endorsed.libraries"), ",;:");
	        while ( st.hasMoreTokens()) {
	            classLoader.addEndorsedLibrary( new File( st.nextToken()));
	        }
        }

		if ( System.getProperty( "libraries") != null) {
	        StringTokenizer st = new StringTokenizer( System.getProperty( "libraries"), ",;:");
    	    while ( st.hasMoreTokens()) {
        	    classLoader.addLibrary( new File( st.nextToken()));
	        }
		}

		if ( System.getProperty( "extension.dir") != null) {
			classLoader.addExtensionDirectory( new File( System.getProperty( "extension.dir")));
		}

        if ( System.getProperty( "extensions") != null) {
	        StringTokenizer st = new StringTokenizer( System.getProperty( "extensions"), ",;:");
	        while ( st.hasMoreTokens()) {
	            classLoader.addExtension( new File( st.nextToken()));
	        }
        }
        
        if ( System.getProperty( "plugins.lib.dir") != null) {
        	classLoader.addPluginsLibDirectory( new File( System.getProperty( "plugins.lib.dir")));
        }
        
        if ( System.getProperty( "plugins.dir") != null) {
        	classLoader.addPluginsDirectory( new File( System.getProperty( "plugins.dir")));
        }

        Thread.currentThread().setContextClassLoader( classLoader);
        

        if (VERBOSE) System.out.println("-------------------- Executing -----------------");
            
        invokeMain( classLoader, System.getProperty( "main.class"), args);
    }
        
    private void invokeMain( ClassLoader classloader, String classname, String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
    	if(VERBOSE) System.out.println("Trying to loadClass: classname: "+classname);
        Class invokedClass = classloader.loadClass(classname);
                
        Class[] methodParamTypes = new Class[2];
        methodParamTypes[0] = classloader.getClass();
        methodParamTypes[1] = args.getClass();
        
        Method main = invokedClass.getDeclaredMethod( "main", methodParamTypes);
        
        Object[] methodParams = new Object[2];
        methodParams[0] = classloader;
        methodParams[1] = args;
                
        main.invoke(null, methodParams);
    }    
	
    private void initProperties() {
    	Properties properties = null;

		if ( System.getProperty( "loader.properties.file") == null) {
			System.setProperty( "loader.properties.file", "loader.properties");
		}
    	
		File file = new File( System.getProperty( "loader.properties.file"));
		
		if ( file.exists()) {
			try {
				properties = new Properties();
				properties.load( new FileInputStream( file));
			} catch ( Exception e) {
				properties = null;
			}
		}
		else {
			if(VERBOSE) {
				File testFile = new File("testFile.test");
				try {
					testFile.createNewFile();
					System.err.println("Loader::initProperties - testFile for current dir: "+testFile.getAbsolutePath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			if(VERBOSE) System.err.println("Loader::initProperties - cannot load loader.properties file: "+file.toString());
		}
		
		if ( properties != null) {
			setProperty( properties, "libraries");
			setProperty( properties, "extension.dir");
			setProperty( properties, "endorsed.libraries");
			setProperty( properties, "main.class");
			setProperty( properties, "extensions");
			setProperty( properties, "plugins.dir");
			setProperty( properties, "plugins.lib.dir");
		}
    }
	
	private void setProperty( Properties properties, String key) {
		if ( System.getProperty( key) == null) {
			if ( properties.getProperty( key) != null) {
				System.setProperty( key, properties.getProperty( key));
			}
		}
	}
}
