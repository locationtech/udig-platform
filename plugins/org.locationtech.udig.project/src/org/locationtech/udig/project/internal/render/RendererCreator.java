/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;

import org.eclipse.emf.common.notify.Notification;

/**
 * Creates Renderers and RenderContexts.
 * 
 * <p>  The default implementation uses a renderer creator to create renderers. 
 * The default implementation chooses a renderer based on the available GeoResource and style.
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 */
public interface RendererCreator{
    
    /**
     * Provides a way to influence the choice of renderers.  The value of a blackboard entry must a string which is the 
     * id of the Renderer as declared in the Extension definition.  For example "BasicFeatureRenderer".
     * <ul>
     * <li>If an entry is on a Layer's blackboard with this key then that renderer will be preferred for that layer over other renderers.</li>
     * <li>If an entry is on the Map's blackboard with this key then that renderer will be preferred over other renderers unless
     * there is also an entry on a layer.  In that case the layer's renderer still has precidence.</li>
     * </ul>
     * <p><b>IMPORTANT:</b> don't forget to append the plugin ID to the id entered into the id field.</p>
     */
    public static final String PREFERRED_RENDERER_ID = "PREFERRED_RENDERER_ID"; //$NON-NLS-1$
    
    /**
     * Provides a way to influence the choice of renderers.  The value of a blackboard entry must a string which is the 
     * id of the Renderer as declared in the Extension definition.  For example "BasicFeatureRenderer".
     * <ul>
     * <li>If an entry is on a Layer's blackboard with this key then that renderer will be negatively weighted for that layer compared other renderers.</li>
     * <li>If an entry is on the Map's blackboard with this key then that renderer will be negatively weighted compared to other renderers.</li>
     * </ul>
     * <p><b>IMPORTANT:</b> don't forget to append the plugin ID to the id entered into the id field.</p>
     */
    public static final String LAST_RESORT_RENDERER_ID = "LAST_RESORT_RENDERER_ID"; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Toolkit</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Toolkit</em>' attribute isn't clear, there really should be
     * more of a description here...
     * </p>
     * 
     * @return the value of the '<em>Toolkit</em>' attribute.
     */
    RenderContext getContext();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.RendererCreator#getContext <em>Context</em>}'
     * reference. 
     * 
     * @param value the new value of the '<em>Context</em>' reference.
     * @see #getContext()
     */
    void setContext( RenderContext value );

    /**
     * Creates a Renderer which can render the provided layer
     * 
     * @param context A context object with a layer to be rendered. The layer will be used to
     *        determine which renderer will be created
     * @return a Renderer which can render the provided layer null if the layer type is not known
     *         (for example a decorator with an unknown renderer)
     */
    public Renderer getRenderer( RenderContext context );

    /**
     * @return List <RenderContext>all the RenderContexts required for rendering all the layers in
     *         the Map.
     */
    public RenderContext getRenderContext( Layer layer );

    /**
     * Causes the RendererCreator to be updated as a result of a Map event.
     */
    void changed( Notification msg );
    /**
     * Rebuilds the configurations.
     */
    public void reset();
    /**
     * Locates the selection layer for layer or returns null;
     * 
     * @return the selection layer for layer or returns null;
     */
    public SelectionLayer findSelectionLayer( ILayer targetLayer );

    /**
     * @return The list of layers that the RendererCreator is responsible for creating renderers
     */
    SortedSet<Layer> getLayers();

    /**
     * Returns the current configuration of Contexts objects.
     * 
     * @return the current configuration of Contexts objects
     */
    Collection<RenderContext> getConfiguration();
    
    /**
     * Returns The name and description of all the renderers that are capable of rendering the provided layer.
     *
     * @return The name and description of all the renderers that are capable of rendering the provided layer.
     */
    public Map<String, String> getAvailableRenderersInfo(Layer layer);

    /**
     * Returns the set of {@link IRenderMetrics} that can render the provided layer.
     *
     * @return the set of {@link IRenderMetrics} that can render the provided layer.
     */
    public Collection<AbstractRenderMetrics> getAvailableRendererMetrics(Layer layer);
    
    /**
     * Returns the ids of all the renderers that are capable of rendering the provided layer.
     *
     * @return the the ids of all the renderers that are capable of rendering the provided layer.
     */
    public Collection<String> getAvailableRendererIds(Layer layer);
}
