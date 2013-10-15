/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
