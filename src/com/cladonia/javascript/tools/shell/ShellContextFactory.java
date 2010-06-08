/*
 * Created on 24-Mar-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.javascript.tools.shell;


/**
 * @author gmcgoldrick
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
import org.mozilla.javascript.*;

public class ShellContextFactory extends ContextFactory
{
    private boolean strictMode;
    private int languageVersion;
    private int optimizationLevel;
    private ErrorReporter errorReporter;

    protected boolean hasFeature(Context cx, int featureIndex)
    {
        switch (featureIndex) {
          case Context.FEATURE_STRICT_VARS:
          case Context.FEATURE_STRICT_EVAL:
            return strictMode;
        }
        return super.hasFeature(cx, featureIndex);
    }

    protected void onContextCreated(Context cx)
    {
        cx.setLanguageVersion(languageVersion);
        cx.setOptimizationLevel(optimizationLevel);
        if (errorReporter != null) {
            cx.setErrorReporter(errorReporter);
        }
        super.onContextCreated(cx);
    }

    public void setStrictMode(boolean flag)
    {
        checkNotSealed();
        this.strictMode = flag;
    }

    public void setLanguageVersion(int version)
    {
        Context.checkLanguageVersion(version);
        checkNotSealed();
        this.languageVersion = version;
    }

    public void setOptimizationLevel(int optimizationLevel)
    {
        Context.checkOptimizationLevel(optimizationLevel);
        checkNotSealed();
        this.optimizationLevel = optimizationLevel;
    }

    public void setErrorReporter(ErrorReporter errorReporter)
    {
        if (errorReporter == null) throw new IllegalArgumentException();
        this.errorReporter = errorReporter;
    }

}
