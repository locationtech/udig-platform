/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.project.internal.render;

import net.refractions.udig.project.internal.render.impl.RenderExecutorComposite;
import net.refractions.udig.project.internal.render.impl.RenderExecutorMultiLayer;

/**
 * A Visitor that visit the tree of RenderExecutors.
 * 
 * @author jeichar
 * @since 0.6.0
 */
public interface ExecutorVisitor {
    /**
     * Called by RenderExecutorImpl objects
     * 
     * @param executor
     */
    void visit( RenderExecutor executor );
    /**
     * Called by RendererExecutorMultiLayer objects.
     * 
     * @param executor
     */
    void visit( RenderExecutorMultiLayer executor );

    /**
     * Called by CompositeRendererImpl objects.
     * 
     * @param executor
     */
    void visit( RenderExecutorComposite executor );
}
