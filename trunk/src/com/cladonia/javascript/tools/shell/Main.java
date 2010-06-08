/*
 * Created on 24-Mar-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.javascript.tools.shell;

import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.ToolErrorReporter;

import com.cladonia.xngreditor.api.*;


/**
 * @author gmcgoldrick
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Main
{
    public static final ShellContextFactory
        shellContextFactory = new ShellContextFactory();

    private static Exchanger xngr = null;
    /**
     * Proxy class to avoid proliferation of anonymous classes.
     */
    private static class IProxy implements ContextAction
    {
        private static final int PROCESS_FILES = 1;
        private static final int EVAL_INLINE_SCRIPT = 2;

        private int type;
        String[] args;
        String scriptText;

        IProxy(int type)
        {
            this.type = type;
        }

        public Object run(Context cx)
        {
            if (type == PROCESS_FILES) {
                processFiles(cx, args);
            } else if (type == EVAL_INLINE_SCRIPT) {
                evaluateScript(cx, getGlobal(), null, scriptText,
                               "<command>", 1, null);
            } else {
                throw Kit.codeBug();
            }
            return null;
        }
    }


    /**
     * Main entry point.
     *
     * Process arguments as would a normal Java program. Also
     * create a new Context and associate it with the current thread.
     * Then set up the execution environment and begin to
     * execute scripts.
     */
    public static void main(String args[]) {
        try {
            if (Boolean.getBoolean("rhino.use_java_policy_security")) {
                initJavaPolicySecuritySupport();
            }
        } catch (SecurityException ex) {
            ex.printStackTrace(System.err);
        }

        int result = exec(args, null);
        if (result != 0) {
            System.exit(result);
        }
    }

    /**
     *  Execute the given arguments, but don't System.exit at the end.
     */
    public static int exec(String origArgs[], Exchanger exchangerContext)
    {
      //if (exchangerContext == null)
      //  System.out.println("XXXXX null context sent down");
      
      xngr = exchangerContext;
      
        for (int i=0; i < origArgs.length; i++) {
            String arg = origArgs[i];
            if (arg.equals("-sealedlib")) {
                sealedStdLib = true;
                break;
            }
        }

        errorReporter = new ToolErrorReporter(false, global.getErr());
    	  //System.out.println("Call setErrorReporter");
       shellContextFactory.setErrorReporter(errorReporter);
        
  	  //System.out.println("Call processOptions");

        String[] args = processOptions(origArgs);
        if (processStdin)
            fileList.addElement(null);

        IProxy iproxy = new IProxy(IProxy.PROCESS_FILES);
        iproxy.args = args;
        
    	  //System.out.println("Call shellContextFactory.call");

        shellContextFactory.call(iproxy);

        return exitCode;
    }

    static void processFiles(Context cx, String[] args)
    {

if (!global.initialized) {
            global.init(cx);
        }
        // define "arguments" array in the top-level object:
        // need to allocate new array since newArray requires instances
        // of exactly Object[], not ObjectSubclass[]
        Object[] array = new Object[args.length];
        System.arraycopy(args, 0, array, 0, args.length);
        Scriptable argsObj = cx.newArray(global, array);
        global.defineProperty("arguments", argsObj,
                              ScriptableObject.DONTENUM);

     	  //System.out.println("in processFiles: fileList.size()=" + fileList.size());

        for (int i=0; i < fileList.size(); i++) {
       	  //System.out.println("in processFiles: call processSource: " + (String) fileList.elementAt(i));
            processSource(cx, (String) fileList.elementAt(i));
        }

    }

    public static Global getGlobal()
    {
        return global;
    }

    /**
     * Parse arguments.
     */
    public static String[] processOptions(String args[])
    {
   	  //System.out.println("in processOptions: args.length=" + args.length);
        String usageError;
        goodUsage: for (int i = 0;  ; ++i) {
            if (i >= args.length) {
                return new String[0];
            }
            String arg = args[i];
            //System.out.println("in processOptions: arg=" + arg);
                    if (!arg.startsWith("-")) {
                processStdin = false;
                fileList.addElement(arg);
                String[] result = new String[args.length - i - 1];
                System.arraycopy(args, i+1, result, 0, args.length - i - 1);
                return result;
            }
            if (arg.equals("-version")) {
                if (++i == args.length) {
                    usageError = arg;
                    break goodUsage;
                }
                int version;
                try {
                    version = Integer.parseInt(args[i]);
                } catch (NumberFormatException ex) {
                    usageError = args[i];
                    break goodUsage;
                }
                if (!Context.isValidLanguageVersion(version)) {
                    usageError = args[i];
                    break goodUsage;
                }
                shellContextFactory.setLanguageVersion(version);
                continue;
            }
            if (arg.equals("-opt") || arg.equals("-O")) {
                if (++i == args.length) {
                    usageError = arg;
                    break goodUsage;
                }
                int opt;
                try {
                    opt = Integer.parseInt(args[i]);
                } catch (NumberFormatException ex) {
                    usageError = args[i];
                    break goodUsage;
                }
                if (opt == -2) {
                    // Compatibility with Cocoon Rhino fork
                    opt = -1;
                } else if (!Context.isValidOptimizationLevel(opt)) {
                    usageError = args[i];
                    break goodUsage;
                }
                shellContextFactory.setOptimizationLevel(opt);
                continue;
            }
            if (arg.equals("-strict")) {
                shellContextFactory.setStrictMode(true);
                continue;
            }
            if (arg.equals("-e")) {
                processStdin = false;
                if (++i == args.length) {
                    usageError = arg;
                    break goodUsage;
                }
                IProxy iproxy = new IProxy(IProxy.EVAL_INLINE_SCRIPT);
                iproxy.scriptText = args[i];
                shellContextFactory.call(iproxy);
                continue;
            }
            if (arg.equals("-w")) {
                errorReporter.setIsReportingWarnings(true);
                continue;
            }
            if (arg.equals("-f")) {
                processStdin = false;
                if (++i == args.length) {
                    usageError = arg;
                    break goodUsage;
                }
                fileList.addElement(args[i].equals("-") ? null : args[i]);
                continue;
            }
            if (arg.equals("-sealedlib")) {
                // Should already be processed
                if (!sealedStdLib) Kit.codeBug();
                continue;
            }
            usageError = arg;
            break goodUsage;
        }
        // print usage message
        p(ToolErrorReporter.getMessage("msg.shell.usage", usageError));
        System.exit(1);
        return null;
    }

    private static void initJavaPolicySecuritySupport()
    {
        Throwable exObj;
        try {
            Class cl = Class.forName
                ("org.mozilla.javascript.tools.shell.JavaPolicySecurity");
            securityImpl = (SecurityProxy)cl.newInstance();
            SecurityController.initGlobal(securityImpl);
            return;
        } catch (ClassNotFoundException ex) {
            exObj = ex;
        } catch (IllegalAccessException ex) {
            exObj = ex;
        } catch (InstantiationException ex) {
            exObj = ex;
        } catch (LinkageError ex) {
            exObj = ex;
        }
        throw Kit.initCause(new IllegalStateException(
            "Can not load security support: "+exObj), exObj);
    }

    /**
     * Evaluate JavaScript source.
     *
     * @param cx the current context
     * @param filename the name of the file to compile, or null
     *                 for interactive mode.
     */
    public static void processSource(Context cx, String filename)
    {
   	  //System.out.println("in processSource");
        if (filename == null || filename.equals("-")) {
            if (filename == null) {
           	  //System.out.println("getOut().println(cx.getImplementationVersion()");

                // print implementation version
                getOut().println(cx.getImplementationVersion());
            }

            // Use the interpreter for interactive input
            cx.setOptimizationLevel(-1);

            
           //Scriptable scope = cx.initStandardObjects(null);
          // if (xngr == null)
           //  System.out.println("****null xngr context ****");
           
           ScriptableObject.putProperty(getScope(), "exchanger", xngr);

        	  //System.out.println("start reading");

        	/*  
        	if (filename == null)
                global.getErr().print("xxxjs> ");
        	
            global.getErr().flush();
            */

        	  
            BufferedReader in = new BufferedReader
                (new InputStreamReader(global.getIn()));
            int lineno = 1;
 
            /*
            int startline1 = lineno;
  
            String source1 = "";
            
            String newline1 = "";
            try {
        	  //System.out.println("readLine");
                newline1 = in.readLine();
            }
            catch (IOException ioe) {
                global.getErr().println(ioe.toString());
            }
 
            source1 = source1 + newline1 + "\n";
            lineno++;
            if (cx.stringIsCompilableUnit(source1))
			{           
        	  //System.out.println("stringIsCompilableUnit");
           
	            Object result = evaluateScript(cx, global, null, source1,
	                                           "<stdin>", startline1, null);
	            if (result != cx.getUndefinedValue()) {
	                try {
	                    global.getErr().println(cx.toString(result));
	                } catch (RhinoException rex) {
	                    errorReporter.reportException(rex);
	                }
	            }        
            }
            */
            
                  
            
            boolean hitEOF = false;
            while (!hitEOF) {
                int startline = lineno;
                if (filename == null)
                    global.getErr().print("js> ");
                global.getErr().flush();
                String source = "";

                // Collect lines of source to compile.
                while (true) {
                    String newline;
                    try {
                        newline = in.readLine();
                    }
                    catch (IOException ioe) {
                        global.getErr().println(ioe.toString());
                        break;
                    }
                    if (newline == null) {
                        hitEOF = true;
                        break;
                    }
                    source = source + newline + "\n";
                    lineno++;
                    if (cx.stringIsCompilableUnit(source))
                        break;
                }
             
                //source = "print(\"hello world\")";
                Object result = evaluateScript(cx, global, null, source,
                                               "<stdin>", startline, null);
                if (result != cx.getUndefinedValue()) {
                    try {
                        global.getErr().println(cx.toString(result));
                    } catch (RhinoException rex) {
                        errorReporter.reportException(rex);
                    }
                }
                NativeArray h = global.history;
                h.put((int)h.getLength(), h, source);
            }
            global.getErr().println();
               
       
        } else {
            processFile(cx, global, filename);
        }
        System.gc();

    
        }

    public static void processFile(Context cx, Scriptable scope,
                                   String filename)
    {
        if (securityImpl == null) {
            processFileSecure(cx, scope, filename, null);
        } else {
            securityImpl.callProcessFileSecure(cx, scope, filename);
        }
    }

    static void processFileSecure(Context cx, Scriptable scope,
                                  String filename, Object securityDomain)
    {
        Reader in = null;
        // Try filename first as URL
        try {
            URL url = new URL(filename);
            InputStream is = url.openStream();
            in = new BufferedReader(new InputStreamReader(is));
        }  catch (MalformedURLException mfex) {
            // fall through to try it as a file
            in = null;
        } catch (IOException ioex) {
            Context.reportError(ToolErrorReporter.getMessage(
                "msg.couldnt.open.url", filename, ioex.toString()));
            exitCode = EXITCODE_FILE_NOT_FOUND;
            return;
        }

        if (in == null) {
            // Try filename as file
            try {
                in = new PushbackReader(new FileReader(filename));
                int c = in.read();
                // Support the executable script #! syntax:  If
                // the first line begins with a '#', treat the whole
                // line as a comment.
                if (c == '#') {
                    while ((c = in.read()) != -1) {
                        if (c == '\n' || c == '\r')
                            break;
                    }
                    ((PushbackReader) in).unread(c);
                } else {
                    // No '#' line, just reopen the file and forget it
                    // ever happened.  OPT closing and reopening
                    // undoubtedly carries some cost.  Is this faster
                    // or slower than leaving the PushbackReader
                    // around?
                    in.close();
                    in = new FileReader(filename);
                }
                filename = new java.io.File(filename).getCanonicalPath();
            }
            catch (FileNotFoundException ex) {
                Context.reportError(ToolErrorReporter.getMessage(
                    "msg.couldnt.open",
                    filename));
                exitCode = EXITCODE_FILE_NOT_FOUND;
                return;
            } catch (IOException ioe) {
                global.getErr().println(ioe.toString());
            }
        }
        // Here we evalute the entire contents of the file as
        // a script. Text is printed only if the print() function
        // is called.
        evaluateScript(cx, scope, in, null, filename, 1, securityDomain);
    }

    public static Object evaluateScript(Context cx, Scriptable scope,
                                        Reader in, String script,
                                        String sourceName,
                                        int lineno, Object securityDomain)
    {
      
   	  //System.out.println("in evaluateScript");
           
        if (!global.initialized) {
            global.init(cx);
        }
        Object result = cx.getUndefinedValue();
        try {
            if (in != null) {
                try {
                    try {
                        result = cx.evaluateReader(scope, in,
                                                   sourceName, lineno,
                                                   securityDomain);
                    } finally {
                        in.close();
                    }
                } catch (IOException ioe) {
                    global.getErr().println(ioe.toString());
                }
            } else {
           	  //System.out.println("call evaluateString: " + script);

                result = cx.evaluateString(scope, script, sourceName, lineno,
                                           securityDomain);
            }
        } catch (WrappedException we) {
            global.getErr().println(we.getWrappedException().toString());
            we.printStackTrace();
        } catch (EvaluatorException ee) {
            // Already printed message.
            exitCode = EXITCODE_RUNTIME_ERROR;
        } catch (RhinoException rex) {
            errorReporter.reportException(rex);
            exitCode = EXITCODE_RUNTIME_ERROR;
        } catch (VirtualMachineError ex) {
            // Treat StackOverflow and OutOfMemory as runtime errors
            ex.printStackTrace();
            String msg = ToolErrorReporter.getMessage(
                "msg.uncaughtJSException", ex.toString());
            exitCode = EXITCODE_RUNTIME_ERROR;
            Context.reportError(msg);
        }
        return result;
    }

    private static void p(String s) {
        global.getOut().println(s);
    }

    public static ScriptableObject getScope() {
        if (!global.initialized) {
            global.init(Context.getCurrentContext());
        }
        return global;
    }

    public static InputStream getIn() {
        return Global.getInstance(getGlobal()).getIn();
    }

    public static void setIn(InputStream in) {
        Global.getInstance(getGlobal()).setIn(in);
    }

    public static PrintStream getOut() {
        return Global.getInstance(getGlobal()).getOut();
    }

    public static void setOut(PrintStream out) {
        Global.getInstance(getGlobal()).setOut(out);
    }

    public static PrintStream getErr() {
        return Global.getInstance(getGlobal()).getErr();
    }

    public static void setErr(PrintStream err) {
        Global.getInstance(getGlobal()).setErr(err);
    }

    static protected final Global global = new Global();
    static protected ToolErrorReporter errorReporter;
    static protected int exitCode = 0;
    static private final int EXITCODE_RUNTIME_ERROR = 3;
    static private final int EXITCODE_FILE_NOT_FOUND = 4;
    //static private DebugShell debugShell;
    static boolean processStdin = true;
    static boolean sealedStdLib = false;
    static Vector fileList = new Vector(5);
    private static SecurityProxy securityImpl;
}
