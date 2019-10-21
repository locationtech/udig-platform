/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.locationtech.udig.catalog.IGeoResource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.data.Query;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Envelope;

/**
 * Layer interface (part of a map).
 * 
 * @author Jesse
 * @since 1.0.0
 */
public interface ILayer extends ILegendItem, Comparable<ILayer> {

    /**
     * Indicates the crs that will be used if the layer does not declare a crs. This crs is wgs84
     * crs with the name: Messages.LayerImpl_unknown (unkown in english)
     */
    public static final CoordinateReferenceSystem UNKNOWN_CRS = DefaultEngineeringCRS.GENERIC_2D; 


    /** <code>UNCONFIGURED</code> associated GeoResource is unconfigured, or is unavailable */
    public static final int UNCONFIGURED = -2;

    /** <code>MISSING</code> cannot locate a GeoResource for this layer */
    public static final int MISSING = -1;

    /** <code>DONE</code> rendering process completed normally */
    public static final int DONE = 0;

    /**
     * <code>WAIT</code> layer is waiting for information.
     */
    public static final int WAIT = 1;

    /** <code>ERROR</code> render process was unable to complete normally */
    public static final int ERROR = 2;

    /** <code>WARNING</code> render process has produced warning(s) in the log. */
    public static final int WARNING = 4;

    /**
     * <code>WORKING</code> rendering process is underway.
     * <p>
     * Note the default Label decorator makes use of a WORKING_HINT layer property. The rendering
     * process will clear this HINT when starting work, subsequence calls to setState( WORKING )
     * will cycle the to update the clock.
     * </p>
     */
    public static final int WORKING = 5;

    /** Listen to changes on this layer.  Each listener can only be added once*/
    public void addListener( ILayerListener listener );

    /** remove LayerListener */
    public void removeListener( ILayerListener listener );

    /**
     * Resource the user associates with this layer. This is not the same as getGeoResource(Class
     * clazz).
     * <p>
     * This is usually the first GeoResource among "friends"
     * 
     * @return Resource the user associates with this layer.
     */
    public IGeoResource getGeoResource();

    /**
     * Returns the first found geoResource that can resolve to clazz.
     * 
     * @param clazz the clazz that the returned georesource can resolve to.
     * @return the first found geoResource that can resolve to clazz.
     * @deprecated Please use {@link #findGeoResource(Class)}
     */
    public <T> IGeoResource getGeoResource( Class<T> clazz );

    /**
     * Locate the first IGeoResource that canResolve the provided resource type.
     * <p>
     * Example implementation:
     * 
     * <pre><code>
     * for( IGeoResource resource : getGeoResources() ) {
     *     if (resource.canResolve(FeatureSource.class)) {
     *         return resource;
     *     }
     * }
     * return null;
     * </code></pre>
     * 
     * </p>
     * 
     * @param clazz class of the resource that the IGeoResource claims it can adapt to.
     * @return true if a IGeoResource exists that canResolve to resourceType.
     */
    <T> IGeoResource findGeoResource( Class<T> clazz);
    
    /**
     * StyleBlackboard used for collaboration with the rendering process.
     * <p>
     * This Blackboard is persisted, any modications made to the blackboard will result refresh of
     * the effected layers.
     * <p>
     * 
     * @return Blackboard used for renderer collaboration.
     */
    public IStyleBlackboard getStyleBlackboard();

    /**
     * Check if a IGeoResource exists that canResolve to the provided resource type.
     * <p>
     * Example:
     * 
     * <pre><code>
     * if (layer.hasResource(FeatureSource.class)) {
     *     return layer.getResource(FeatureSource.class, monitor);
     * }
     * </code></pre>
     * 
     * </p>
     * The blocking {@linkplain #getResource(Class, IProgressMonitor)} method allow the object to be
     * obtained.
     * 
     * @param resourceType  the type of resource calleer is interested in.
     * @return true if a IGeoResource exists that canResolve to resourceType.
     */
    public <T> boolean hasResource( Class<T> resourceType );
    /**
     * @see #hasResource(Class)
     *
     * @deprecated use {@link #hasResource(Class)}
     */
    public <T> boolean isType( Class<T> resourceType );

    /**
     * Returns a <i>real</i> resource that one of the GeoResources can resolved to.
     * <p>
     * Note: examples of resources are: FeatureSource, WebMapServer, etc... GeoResources are handles
     * for real resources.
     * </p>
     * <p>
     * Example implementation:
     * 
     * <pre><code>
     * for( IGeoResource resource : getGeoResources() ) {
     *     if (resource.canResolve(FeatureSource.class)) {
     *         return resource;
     *     }
     * }
     * return null;
     * </code></pre>
     * 
     * </p>
     * 
     * <p>
     * <b>IMPORTANT:</b> unlike using {@link IGeoResource#resolve(Class, IProgressMonitor)} {@link #getResource(Class, IProgressMonitor)} 
     * returns the same <em>instance</em> of the resource.  The {@link IGeoResource#resolve(Class, IProgressMonitor)} method returns a new instance
     * each time and therefore should <b>NOT</b> be used as a replacement for {@link #getResource(Class, IProgressMonitor)}.
     * </p>
     * 
     * @param resourceType
     * @return true if a IGeoResource exists that canResolve to resourceType.
     */
    public <E> E getResource( Class<E> resourceType, IProgressMonitor monitor ) throws IOException;

    /**
     * Filter indicating the selected features.
     * <p>
     * In order for this value to be useful the layer should be selectable, often a single fid
     * filter during user edit opperations.
     * </p>
     * <p>
     * Note: Filter.EXCLUDE indicates no selected Features. (All features are filtered out)
     * </p>
     * <p>
     * A tool may wish to record the previous Filter, before replacing (or adding to) this value.
     * </p>
     * Will never return null.
     * 
     * @return Filter indicating the selected features. Filter.EXCLUDE indicates no selected Features.
     */
    Filter getFilter();

    /**
     * Returns the ZOrder of the Layer. The Z-Order dictates the order in which the Layer is rendered
     * Low z-orders are rendered first and higher z-orders are rendered on top.
     * 
     * @return the ZOrder of the Layer.
     */
    public int getZorder();

    /**
     * Indication of Layer status.
     * <p>
     * This is used to provide feedback for a Layers rendering status.
     * </p>
     * Future versions will return an enum.
     * 
     * @return
     * @uml.property name="status"
     */
    public int getStatus();

    /**
     * A message to provide the user with additional feed back about the current rendering status.
     * <p>
     * This is used to provide feedback for a Layers rendering status.
     * </p>
     * 
     * @return message to provide the user with additional feed back about the current rendering
     *         status.
     */
    public String getStatusMessage();

    /**
     * Check layer interaction applicability.
     * <p>
     * Note: some layers may not ever be applicable for certaint toolsets. Sometimes this is can be
     * determined quickly from a layer property like "selectable" for the selection toolset. Other
     * toolsets may need to perform a more detailed examination.
     * </p>
     * 
     * @see isSelectable
     * @param interaction
     * @return
     */
    public boolean getInteraction( Interaction interaction );

    /**
     * Gets the name from the associated metadata.
     * 
     * @return the name from the associated metadata
     */
    public String getName();

    /**
     * Gets the unique id, unique within a context model
     * 
     * @return the id of the layerRef
     */
    public URL getID();

    /**
     * Returns whether this layer is currently visible
     * 
     * @return whether this layer is currently visible
     */
    public boolean isVisible();

    /**
     * Access to resources that hold data for this layer.
     * 
     * @return IGeoResources that can used to obtain layer data
     */
    public List<IGeoResource> getGeoResources();

    /**
     * ImageDescriptor for this Layer.
     * <p>
     * Note we need to do the decorator exention on Layer to reflect status.
     * 
     * @return Custom glyph - or null if none available.
     */
    public ImageDescriptor getIcon();

    /**
     * Query used to retrieve the contents of the layer.
     * <p>
     * The selected flag is used with respect to {@link getFilter()}:
     * <ul>
     * <li><b>true </b>: Query for the layer's selected features
     * <li><b>false </b>: Query for layers contents
     * </ul>
     * </p>
     * <p>
     * This is a helper method to save client code the trouble of hunting down
     * the filter associated with this layer, including any overrides provided
     * by the style blackboard.
     * </p>
     * 
     * @param layer The layer the Query is associated with.
     * @param selection true will return a query for the selected features.
     * @return If selection if false then the features that are not selected are returned, otherwise
     *         a query that selects all the selected features is returned.
     */
    public Query getQuery( boolean selection );

    /**
     * Retrieve a schema description of this Layer.
     * <p>
     * This schema can be used to determine the available attributes for use in Style Rule
     * construction. That is we will need to construct an "answer" for this query even if we just
     * have a WMS layer.
     * </p>
     * <p>
     * This is similar to the following check:
     * 
     * <pre><code>
     *  
     *   data = getResource();
     *   &lt;b&gt;return&lt;/b&gt; data != null ? data.getSchema() : null;
     *   
     * </code></pre>
     * 
     * </p>
     * 
     * @return Schema information if available, otherwise null.
     */
    public SimpleFeatureType getSchema();

    /**
     * Gets the CRS for the layer. NOTE: THIS METHOD MAY BLOCK!!!
     * 
     * @param monitor may be null.
     * @return the CoordinateReferenceSystem of the layer or if the CRS cannot be determined. the
     *         current map's CRS will be returned, or if this fails the CRS will be WGS84.
     * 
     *  @deprecated use getCRS()
     */
    CoordinateReferenceSystem getCRS( IProgressMonitor monitor );

    /**
     * Gets the CRS for the layer. NOTE: THIS METHOD MAY BLOCK!!!
     * 
     * @return the CoordinateReferenceSystem of the layer or if the CRS cannot be determined. the
     *         current map's CRS will be returned, or if this fails the CRS will be WGS84.
     */
    CoordinateReferenceSystem getCRS( );

    /**
     * Triggers the layer to rerender if it is currently displayed.
     * 
     * @param bounds The area to render or the entire viewport if null.
     */
    void refresh( Envelope bounds );

    /**
     * Returns the Mathtransform from this layers CRS to the map's CRS (modelled as part of the
     * viewport model).
     * <p>
     * getMap().getViewportModel() will return the ViewportModel.
     * </p>
     * 
     * @see org.locationtech.udig.project.render.IViewportModel
     */
    MathTransform layerToMapTransform() throws IOException;

    /**
     * Returns the Mathtransform from the map's CRS (modeled as part of the viewport model) to this
     * layers CRS .
     * <p>
     * getMap().getViewportModel() will return the ViewportModel.
     * </p>
     * 
     * @see org.locationtech.udig.project.render.IViewportModel
     */
    MathTransform mapToLayerTransform() throws IOException;

    /**
     * Temporary layer properties, used for lightweight collaboration.
     * <p>
     * Note these values are not persisted, this can act as a blackboard for plugin collabaration.
     * These properties are not saved and are reset when a map is opened.
     * </p>
     * If you need long term collaboration we can set up a persistent blackboard in the same manner
     * as StyleBlackbord.
     * </p>
     * <p>
     * Note: Please don't use this to work around limitations of our object model, instead send
     * email and we can set up a long term solution.
     * </p>
     * 
     * @return Blackboard used for lightweight collaboration.
     * @deprecated
     */
    IBlackboard getProperties();

    /**
     * Temporary layer properties, used for lightweight collaboration.
     * <p>
     * Note these values are not persisted, this can act as a blackboard for plugin collabaration.
     * These properties are not saved and are reset when a map is opened.
     * </p>
     * If you need long term collaboration we can set up a persistent blackboard in the same manner
     * as {@link IMap#getBlackboard()}.
     * </p>
     * <p>
     * Note: Please don't use this to work around limitations of our object model, instead send
     * email and we can set up a long term solution.
     * </p>
     * 
     * @return Blackboard used for lightweight collaboration.
     * 
     */
    IBlackboard getBlackboard();

    /**
     * Returns the bounds of the layer as best estimated. The bounds will be reprojected into the
     * crs provided. If the crs parameter is null then the native envelope will be returned. If the
     * native projection is not known or if a transformation is not possible then the native
     * envelope will be returned.. This method may be blocking.
     * 
     * @param monitor
     * @param crs the desired CRS for the returned envelope.
     * @return the envelope of the layer. If the native crs is not known or if a transformation is
     *         not possible then the untransformed envelope will be returned.
     */
    ReferencedEnvelope getBounds( IProgressMonitor monitor, CoordinateReferenceSystem crs );

    /**
     * Creates A geometry filter for the layer.
     * <p>
     * getMap().getViewportModel() will return the ViewportModel.
     * </p>
     * 
     * @see org.locationtech.udig.project.render.IViewportModel
     * @param boundingBox in the same crs as the viewport model.
     * @return a Geometry filter in the correct CRS or null if an exception occurs.
     */
    public Filter createBBoxFilter( Envelope boundingBox, IProgressMonitor monitor );

    /**
     * The Map that "owns" or "contains" the current layer.
     * 
     * @return the containing map.
     */
    public IMap getMap();

    /**
     * Sets the current status of the Layer.
     * 
     * @param status the status. Will be an enum in the future but current EMF implementation
     *        prevents this.
     * @uml.property name="status"
     */
    public void setStatus( int status );

    /**
     * Sets the current status message
     * 
     * @param message the status message
     */
    public void setStatusMessage( String string );

}
