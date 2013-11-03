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
package org.locationtech.udig.project.internal;

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
    /** Trace rendering such as RenderExecutor and RenderManager and RenderImpl */
    public static final String RENDER = "org.locationtech.udig.project/debug/render/trace"; //$NON-NLS-1$
    /** Trace the execution of commands */
    public static final String COMMANDS = "org.locationtech.udig.project/debug/commands/trace"; //$NON-NLS-1$
    public static final String MODEL = "org.locationtech.udig.project/debug/model/trace"; //$NON-NLS-1$
}
