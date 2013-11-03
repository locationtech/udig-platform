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
package org.locationtech.udig.project.ui.internal;

/**
 * Constants for use with eclipse tracing api. Rember only engage tracing if
 * ProjectUIPlugin.getDefault().isDebugging().
 * <p>
 * Sample use:
 * 
 * <pre><code>
 *  static import org.locationtech.udig.project.ui.internal.RENDERING;
 *  
 *  if( ProjectUIPlugin.isDebugging( RENDERING ) ){
 *       System.out.println( &quot;your message here&quot; );
 *  }
 * </code></pre>
 * 
 * </p>
 */
public interface Trace {
    /**
     * Trace ID to print tracing logs during the rendering process
     */
    public static final String RENDER = "org.locationtech.udig.project.ui/debug/render/trace"; //$NON-NLS-1$
    /**
     * Trace ID to print tracing logs during the drag and drop process
     */
    public static final String DND = "org.locationtech.udig.project.ui/debug/dnd/trace"; //$NON-NLS-1$
    public static final String VIEWPORT = "org.locationtech.udig.project.ui/debug/viewport/trace"; //$NON-NLS-1$
}
