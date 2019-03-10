/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.tool.display;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import org.locationtech.udig.ui.operations.IOpFilterListener;
import org.locationtech.udig.ui.operations.PropertyValue;

@Ignore
public class TestProperty implements PropertyValue {

    public static final List<IOpFilterListener> listeners=new ArrayList<IOpFilterListener>();
    public static boolean returnVal=true;
    public static Object lastObj;

    public void addListener( IOpFilterListener listener ) {
        listeners.add(listener);
    }

    public boolean canCacheResult() {
        return false;
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isTrue( Object object, String value ) {
        lastObj=object;
        return returnVal;
    }

    public void removeListener( IOpFilterListener listener ) {
        listeners.remove(listener);
    }

}
