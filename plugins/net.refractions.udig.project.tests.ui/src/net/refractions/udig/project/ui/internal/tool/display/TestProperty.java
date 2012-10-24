/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal.tool.display;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import net.refractions.udig.ui.operations.IOpFilterListener;
import net.refractions.udig.ui.operations.PropertyValue;

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
