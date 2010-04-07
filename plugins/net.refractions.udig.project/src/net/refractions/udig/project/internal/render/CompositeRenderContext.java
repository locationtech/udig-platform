/*
 * Created on Dec 21, 2004 TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package net.refractions.udig.project.internal.render;

import java.util.Collection;
import java.util.List;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.render.impl.CompositeContextListener;
import net.refractions.udig.project.render.ICompositeRenderContext;

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
 */
public interface CompositeRenderContext extends RenderContext, ICompositeRenderContext {

    /**
     * @return List <RenderContext>The list of RenderContexts that contain the information about how
     *         each layer should be rendered.
     */
    List<Layer> getLayersInternal();
    /**
     * Removes a listener
     *
     * @param contextListener listener to add
     */
    void removeListener( CompositeContextListener contextListener );
    /**
     * Adds a listener
     *
     * @param contextListener listener to add
     */
    void addListener( CompositeContextListener contextListener );

    /**
     * Clears all the contexts
     */
    void clear();

    /**
     * Adds contexts
     *
     * @param contexts contexts to add
     */
    void addContexts( Collection<? extends RenderContext> contexts );

    /**
     * Removes some contexts
     *
     * @param contexts contexts to remove
     */
    void removeContexts( Collection<? extends RenderContext> contexts );
    
    public CompositeRenderContext copy();
}
