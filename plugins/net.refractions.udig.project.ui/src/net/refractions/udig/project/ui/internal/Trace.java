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
package net.refractions.udig.project.ui.internal;

/**
 * Constants for use with eclipse tracing api. Rember only engage tracing if
 * ProjectUIPlugin.getDefault().isDebugging().
 * <p>
 * Sample use:
 * 
 * <pre><code>
 *  static import net.refractions.udig.project.ui.internal.RENDERING;
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
    public static final String RENDER = "net.refractions.udig.project.ui/debug/render/trace"; //$NON-NLS-1$
    /**
     * Trace ID to print tracing logs during the drag and drop process
     */
    public static final String DND = "net.refractions.udig.project.ui/debug/dnd/trace"; //$NON-NLS-1$
    public static final String VIEWPORT = "net.refractions.udig.project.ui/debug/viewport/trace"; //$NON-NLS-1$
}
