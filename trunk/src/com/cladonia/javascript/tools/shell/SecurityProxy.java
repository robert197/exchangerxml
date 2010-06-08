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

public abstract class SecurityProxy extends SecurityController
{
    protected abstract void callProcessFileSecure(Context cx, Scriptable scope,
                                                  String filename);

}
