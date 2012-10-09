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
package net.refractions.udig.project.internal;

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
    /** Trace rendering such as RenderExecutor and RenderManager and RenderImpl */
    public static final String RENDER = "net.refractions.udig.project/debug/render/trace"; //$NON-NLS-1$
    /** Trace the execution of commands */
    public static final String COMMANDS = "net.refractions.udig.project/debug/commands/trace"; //$NON-NLS-1$
    public static final String MODEL = "net.refractions.udig.project/debug/model/trace"; //$NON-NLS-1$
}
