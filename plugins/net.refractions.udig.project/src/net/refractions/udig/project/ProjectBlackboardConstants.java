/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project;

import net.refractions.udig.project.interceptor.ShowViewInterceptor;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.project.internal.impl.UDIGFeatureStore;
import net.refractions.udig.project.internal.render.RendererCreator;

import org.geotools.data.DataStore;
import org.geotools.data.Query;

/**
 * Lists the keys of common items that are put on the Map blackboard and the Layer blackboard
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface ProjectBlackboardConstants {
    
    /**
     * Provides a way to influence the choice of renderers.  The value of a blackboard entry must a string which is the 
     * id of the Renderer as declared in the Extension definition.  For example "BasicFeatureRenderer".
     * <p>If an entry is on the Map's blackboard with this key then that renderer will be preferred over other renderers unless
     * there is also an entry on a layer.  In that case the layer's renderer still has precidence.</p>
     * <p><b>IMPORTANT:</b> don't forget to append the plugin ID to the id entered into the id field.</p>
     * @see #LAYER__PREFERRED_RENDERER
     * @see #MAP__LAST_RESORT_RENDERER
     */
    String MAP__PREFERRED_RENDERER=RendererCreator.PREFERRED_RENDERER_ID;

    /**
     * Provides a way to influence the choice of renderers.  The value of a blackboard entry must a string which is the 
     * id of the Renderer as declared in the Extension definition.  For example "BasicFeatureRenderer".
     * <p>If an entry is on the Map's blackboard with this key then that renderer will 
     * be negatively weighted compared to other renderers.<p>
     * <p><b>IMPORTANT:</b> don't forget to append the plugin ID to the id entered into the id field.</p>
     * @see #LAYER__LAST_RESORT_RENDERER
     * @see #MAP__PREFERRED_RENDERER
     */
    String MAP__LAST_RESORT_RENDERER=RendererCreator.LAST_RESORT_RENDERER_ID;

    /**
     * The key to the map's background color on the blackboard.  The object returned will be null or a Color
     */
    String MAP__BACKGROUND_COLOR = "mapBackgroundColor"; //$NON-NLS-1$
    /**
     * If a filter or a query is on the map blackboard under the key: the {@link #MAP__DATA_QUERY} then the
     * interceptor {@link ShowViewInterceptor} will return the "view" see {@link DataStore#getView(Query)}.   
     * In addition Renderers should attempt to use the query to filter what is displayed.
     * <p>If a filter is on the map blackboard then it will apply to all layers.</p>
     * <p>If a query is on the Map blackboard then it will be applied only those layers who's typename is the same as that in the query.</p>
     * <p>If there is a filter or query on both the Map blackboard and 
     * the layer blackboard then the item on the layer blackboard will take precedence.</p>
     * @see #LAYER__DATA_QUERY
     */
    String MAP__DATA_QUERY = "DATA__QUERY"; //$NON-NLS-1$
    /**
     * Any filter in this entry will be used to filter out features from the rendering.  For example if a fid filter is here that feature 
     * <b>WILL NOT</b> be rendered.
     */
    String MAP__RENDERING_FILTER = "EDIT_FILTER_CACHE"; //$NON-NLS-1$
    /**
     * Any filter in this entry will be used to filter out features from the rendering.  For example if a fid filter is here that feature 
     * <b>WILL NOT</b> be rendered.
     */
    String LAYER__RENDERING_FILTER = MAP__RENDERING_FILTER;

    /**
     * Provides a way to influence the choice of renderers.  The value of a blackboard entry must a string which is the 
     * id of the Renderer as declared in the Extension definition.  For example "BasicFeatureRenderer".
     * <p>If an entry is on a Layer's blackboard with this key then that renderer will be preferred over other renderers.</p>
     * <p><b>IMPORTANT:</b> don't forget to append the plugin ID to the id entered into the id field.</p>
     * @see #MAP__PREFERRED_RENDERER
     * @see #LAYER__LAST_RESORT_RENDERER
     */
    String LAYER__PREFERRED_RENDERER=RendererCreator.PREFERRED_RENDERER_ID;

    /**
     * Provides a way to influence the choice of renderers.  The value of a blackboard entry must a string which is the 
     * id of the Renderer as declared in the Extension definition.  For example "BasicFeatureRenderer".
     * <p>If an entry is on a Layer's blackboard with this key then that renderer will 
     * be negatively weighted compared to other renderers.<p>
     * <p><b>IMPORTANT:</b> don't forget to append the plugin ID to the id entered into the id field.</p>
     * @see #MAP__LAST_RESORT_RENDERER
     * @see #LAYER__PREFERRED_RENDERER
     */
    String LAYER__LAST_RESORT_RENDERER=RendererCreator.LAST_RESORT_RENDERER_ID;
    /**
     * If a filter or a query is on a layer's blackboard under the key: the {@link #LAYER__DATA_QUERY} then the
     * interceptor {@link ShowViewInterceptor} will return the "view" see {@link DataStore#getView(Query)}.   
     * In addition Renderers should attempt to use the query to filter what is displayed.
     * <p>If a filter is in the layer {@link StyleBlackboard} then the filter will only apply to that layer.  </p>
     * <p>If a query is in the Layer {@link StyleBlackboard} then it will be applied to the layer even if the type name is incorrect</p>
     * <p>If there is a filter or query on both the Map blackboard and 
     * the layer {@link StyleBlackboard} then the item on the layer {@link StyleBlackboard} will take precedence.</p>
     * If the query is found on the map
     * @see #MAP__DATA_QUERY
     */
    String LAYER__DATA_QUERY = "net.refractions.udig.project.view"; //$NON-NLS-1$

    /**
     * Key to indicate a layer may be edited.
     * 
     * @see UDIGFeatureStore
     */
    String LAYER__EDIT_APPLICABILITY = "net.refractions.udig.edit"; //$NON-NLS-1$
    
    
    /**
     * @see net.refractions.udig.project.preferences.PreferenceConstants.P_MINIMUM_ZOOM_SCALE
     */
    String LAYER__MINIMUM_ZOOM_SCALE = "net.refractions.udig.project.MINIMUM_ZOOM_SCALE"; //$NON-NLS-1$

    /**
     * NOT USED at the current codebase.
     */
    String LAYER__MAXIMUM_ZOOM_SCALE = "net.refractions.udig.project.MAXIMUM_ZOOM_SCALE"; //$NON-NLS-1$

    /**
     * FALSE boolean value contained in layer's blackboard advises the platform
     * to block removing of features functionality in the target layer.
     */
    String LAYER__FEATURES_REMOVE_APPLICABILITY = "net.refractions.udig.edit.layer.FEATURES_REMOVE_APPLICABILITY"; //$NON-NLS-1$

    /**
     * NOT USED at the current codebase
     */
    String LAYER__FEATURES_ADD_APPLICABILITY = "net.refractions.udig.edit.layer.FEATURES_ADD_APPLICABILITY"; //$NON-NLS-1$

    /**
     * NOT USED at the current codebase
     */
    String LAYER__FEATURES_MODIFY_APPLICABILITY = "net.refractions.udig.edit.layer.FEATURES_MODIFY_APPLICABILITY"; //$NON-NLS-1$
}
