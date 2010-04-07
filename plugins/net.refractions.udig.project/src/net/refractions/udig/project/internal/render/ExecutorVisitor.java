/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
