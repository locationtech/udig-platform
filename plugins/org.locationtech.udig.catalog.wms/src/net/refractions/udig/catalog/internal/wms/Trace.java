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
package org.locationtech.udig.catalog.internal.wms;

/**
 * Constants for use with eclipse tracing api.
 * Rember only engage tracing if WMSPlugin.getDefault().isDebugging().
 * <p>
 * Sample use:<pre><code>
 * static import org.locationtech.udig.project.ui.internal.RENDERING;
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
        "org.locationtech.udig.catalog.wms/debug/request"; //$NON-NLS-1$    
}
