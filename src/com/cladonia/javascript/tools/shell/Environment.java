/*
 * Created on 24-Mar-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.javascript.tools.shell;


import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.ScriptableObject;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Environment, intended to be instantiated at global scope, provides
 * a natural way to access System properties from JavaScript.
 *
 * @author Patrick C. Beard
 */
public class Environment extends ScriptableObject {
    private Environment thePrototypeInstance = null;

    public static void defineClass(ScriptableObject scope) {
        try {
            ScriptableObject.defineClass(scope, Environment.class);
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    public String getClassName() {
        return "Environment";
    }

    public Environment() {
        if (thePrototypeInstance == null)
            thePrototypeInstance = this;
    }

    public Environment(ScriptableObject scope) {
        setParentScope(scope);
        Object ctor = ScriptRuntime.getTopLevelProp(scope, "Environment");
        if (ctor != null && ctor instanceof Scriptable) {
            Scriptable s = (Scriptable) ctor;
            setPrototype((Scriptable) s.get("prototype", s));
        }
    }

    public boolean has(String name, Scriptable start) {
        if (this == thePrototypeInstance)
            return super.has(name, start);

        return (System.getProperty(name) != null);
    }

    public Object get(String name, Scriptable start) {
        if (this == thePrototypeInstance)
            return super.get(name, start);

        String result = System.getProperty(name);
        if (result != null)
            return ScriptRuntime.toObject(getParentScope(), result);
        else
            return Scriptable.NOT_FOUND;
    }

    public void put(String name, Scriptable start, Object value) {
        if (this == thePrototypeInstance)
            super.put(name, start, value);
        else
            System.getProperties().put(name, ScriptRuntime.toString(value));
    }

    private Object[] collectIds() {
        Properties props = System.getProperties();
        Enumeration names = props.propertyNames();
        Vector keys = new Vector();
        while (names.hasMoreElements())
            keys.addElement(names.nextElement());
        Object[] ids = new Object[keys.size()];
        keys.copyInto(ids);
        return ids;
    }

    public Object[] getIds() {
        if (this == thePrototypeInstance)
            return super.getIds();
        return collectIds();
    }

    public Object[] getAllIds() {
        if (this == thePrototypeInstance)
            return super.getAllIds();
        return collectIds();
    }
}
