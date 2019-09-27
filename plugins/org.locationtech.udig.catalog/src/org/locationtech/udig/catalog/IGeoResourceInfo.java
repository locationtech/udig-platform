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
package org.locationtech.udig.catalog;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

/**
 * Represents a bean style metadata ancestor for metadata about a geoResource.
 * <p>
 * The methods within this class must be non-blocking. This class, and sub-classes represent cached
 * versions of the metadata about a particular service.
 * </p>
 * <p>
 * Much of this interface is based on Dublin Core and the RDF application profile.
 * </p>
 * <p>
 * Any changes to this content will be communicate by an event by the assocaited GeoResource.
 * </p>
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class IGeoResourceInfo {

    protected String title, description, name;
    protected String[] keywords;
    protected URI schema;
    protected ImageDescriptor icon;
    protected ReferencedEnvelope bounds;
    private Icon awtIcon;

    protected IGeoResourceInfo() {
        // for over-riding
    }
    
    public IGeoResourceInfo( String title, String name, String description, URI schema,
            Envelope bounds, CoordinateReferenceSystem crs, String[] keywords, ImageDescriptor icon ) {
        this.title = title;
        this.description = description;
        this.name = name;
        int i = 0;
        if( keywords!=null ){
            i=keywords.length;
        }
        String[] k=new String[i];
        if( keywords!=null )
            System.arraycopy(keywords, 0, k, 0, k.length);
        this.keywords = k;
        this.schema = schema;
        this.icon = icon;
        this.bounds = new ReferencedEnvelope(bounds, crs);
    }

    /**
     * Returns the resource's title
     * 
     * @return Readable title (in current local)
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the keywords assocaited with this resource
     * <p>
     * Known Mappings:
     * <ul>
     * <li> Maps to Dublin Core's Subject element
     * </ul>
     * </p>
     * 
     * @return Keywords for use with search, or <code>null</code> unavailable.
     */
    public Set<String> getKeywords() { // aka Subject
        if( keywords == null ){
            return Collections.emptySet();
        }
        List<String> asList = Arrays.asList(keywords);
        Set<String> set = new HashSet<String>(asList);
        return set;
    }

    /**
     * Returns the resource's description.
     * <p>
     * Known Mappings:
     * <ul>
     * <li>WFS GetCapabilities description
     * <li>WMS GetCapabilities description
     * </ul>
     * </p>
     * 
     * @return description of resource, or <code>null</code> if unavailable
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the xml schema namespace for this resource type.
     * <p>
     * Known Mappings:
     * <ul>
     * <li>Dublin Code Format element
     * </ul>
     * </p>
     * 
     * @return namespace, used with getName() to identify resource
     */
    public URI getSchema() { // aka namespace
        return schema;
    }

    /**
     * Returns the name of the data ... such as the typeName or LayerName.
     * <p>
     * Known Mappings:
     * <ul>
     * <li>WFS typeName
     * <li>Database table name
     * <li>WMS layer name
     * </ul>
     * </p>
     * 
     * @return name of the data, used with getSchema() to identify resource
     */
    public String getName() { // aka layer/type name
        return name;
    }

    /**
     * Base symbology (with out decorators) representing this resource.
     * <p>
     * The ImageDescriptor returned should conform the the Eclipse User Interface Guidelines (16x16
     * image with a 16x15 glyph centered).
     * </p>
     * <p>
     * This plug-in provides default based on resource type:
     * 
     * <pre><code>
     *  &lt;b&gt;return&lt;/b&gt; ISharedImages.getImagesDescriptor( IGeoResoruce );
     * </code></pre>
     * 
     * <ul>
     * <p>
     * Any LabelProvider should use the default image, a label decorator should be used to pick up
     * these images in a separate thread. This allows resources like WMS to make blocking request of
     * an external service.
     * </p>
     * 
     * @return ImageDescriptor symbolizing this resource
     */
    public Icon getIcon() {
        if( awtIcon!=null ){
            return awtIcon;
        }
        if( icon==null ){
            return null;
        }
        
        Icon awtIcon = AWTSWTImageUtils.imageDescriptor2awtIcon(icon);
        return awtIcon;
    }

    /**
     * Default implementation calls getIcon and converts the icon to an ImageDescriptor.
     *
     * @return the icon as an image descriptor
     */
    public ImageDescriptor getImageDescriptor() {
        if( icon!=null ){
            return icon;
        }
        
        Icon icon2 = getIcon();
        if( icon2 == null ){
            return null;
        }
        
        return AWTSWTImageUtils.awtIcon2ImageDescriptor(icon2);
    }

    /**
     * Returns the BBox of the resource if one exists, The null envelope otherwise.
     * <p>
     * The bounds are returned in (ie should be reprojected to) Lat Long:
     * <ul>
     * <li>DefaultGeographicCRS.WGS84
     * <li>EPSG:4369 (LatLong NAD83)
     * <li>ESPG 4326 (another LatLong)
     * </ul>
     * </p>
     * <p>
     * Known Mappings:
     * <ul>
     * <li>1st part of the Dublin Core Coverage</li>
     * </ul>
     * 
     * @return Lat Long bounding box of the resource
     */
    public ReferencedEnvelope getBounds() { // part of Coverage
        return bounds;
    }

    /**
     * Returns the CRS of the resource if one exists, null otherwise.
     * <p>
     * Known Mappings:
     * <ul>
     * <li>2nd part of the Dublin Core Coverage
     * </ul>
     * </p>
     * 
     * @return CRS of the resource, or <code>null</code> if unknown.
     */
    public CoordinateReferenceSystem getCRS() { // part of Coverage
        if (getBounds() == null) {
            return null;
        }
        return getBounds().getCoordinateReferenceSystem();
    }

}
