/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Simple class that acepts all data.  On perform run it assigns the data to a static variable for tests to access.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class AlwaysAcceptDropAction extends IDropAction {

    /**
     * Last data dropped
     */
    public static Object droppedData;
    /**
     * Last drop destination
     */
    public static Object dropDestination;
    /**
     * All items that were dropped since reset was called.
     */
    public static List<Object> allDrops=Collections.synchronizedList(new ArrayList<Object>());


    @Override
    public boolean accept() {
        return true;
    }

    @Override
    public void perform( IProgressMonitor monitor ) {
        droppedData=getData();
        dropDestination=getDestination();
        allDrops.add(getData());
    }

    public static void reset() {
        dropDestination=null;
        droppedData=null;
        allDrops.clear();
    }

}
