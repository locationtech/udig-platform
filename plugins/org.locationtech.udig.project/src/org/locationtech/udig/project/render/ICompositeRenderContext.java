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
package org.locationtech.udig.project.render;

import java.util.List;

import org.locationtech.udig.project.ILayer;

/**
 * A toolkit that is provided to composite renderers.
 * <p>
 * In addition to the references available in the Toolkit class, RenderToolkit has the layers and
 * services the renderer is responsible for and the buffered image that the renderer draws to.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Provide access to the objects that an extension can use for its operations.</li>
 * <li>Provide convenience methods for extension developers to use.</li>
 * <li>Provide a consistent interface for extensions which will not easily change in future
 * versions</li>
 * </ul>
 * </p>
 * 
 * @author Jesse
 * @since 0.5
 * @see IRenderContext
 */
public interface ICompositeRenderContext extends IRenderContext {

    /**
     * @return List <IRenderContext>The list of RenderContexts that contain the information about
     *         how each layer should be rendered.
     * @see IRenderContext
     * @see List
     */
    List<IRenderContext> getContexts();

    /**
     * Returns the list of all the layers referenced by the contained Contexts.
     * <p>
     * The method iterates throught the list of contexts and collects all the Layers
     * </p>
     * 
     * @return List <ILayer>The list of Layers
     * @see ILayer
     * @see List
     */
    List<ILayer> getLayers();
    
    public ICompositeRenderContext copy();

}
