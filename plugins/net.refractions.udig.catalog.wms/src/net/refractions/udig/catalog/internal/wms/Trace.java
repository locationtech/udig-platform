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
package net.refractions.udig.catalog.internal.wms;

/**
 * Constants for use with eclipse tracing api.
 * Rember only engage tracing if WMSPlugin.getDefault().isDebugging().
 * <p>
 * Sample use:<pre><code>
 * static import net.refractions.udig.project.ui.internal.RENDERING;
 * 
 * if( WmsPlugin.isDebugging( RENDERING ) ){
 *      System.out.println( "your message here" );
 * }
 * </code></pre>
 * </p>
 */
public interface Trace {
    /** You may set this to "true" in your .options file */
    public static final String REQUEST =
        "net.refractions.udig.catalog.wms/debug/request"; //$NON-NLS-1$    
}