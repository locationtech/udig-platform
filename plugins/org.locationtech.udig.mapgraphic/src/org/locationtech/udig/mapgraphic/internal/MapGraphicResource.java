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
package org.locationtech.udig.mapgraphic.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.core.internal.CorePlugin;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicFactory;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

public class MapGraphicResource extends IGeoResource {

    /** the service which contains all decorator resources * */
    private final MapGraphicService parent;

    /** the map graphic name * */
    private String name;

    /** the map graphic id * */
    private final String id;

    private IConfigurationElement element;

    private MapGraphic mapgraphic;

    MapGraphicResource( MapGraphicService service, IConfigurationElement element ) {
        this.service = service;
        parent = service;
        this.name = element.getAttribute("name"); //$NON-NLS-1$
        this.id = element.getAttribute("id"); //$NON-NLS-1$
        this.element = element;
        mapgraphic = createMapGraphic();
    }

    /**
     *
     */
    private MapGraphic createMapGraphic() {
        return MapGraphicFactory.getInstance().createMapGraphic(id);
    }

    /*
     * @see org.locationtech.udig.catalog.IGeoResource#resolve(java.lang.Class,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null)
            return null;

        // if (adaptee.isAssignableFrom(IService.class)) {
        // return adaptee.cast(parent);
        // }
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class)) {
            return adaptee.cast(createInfo(monitor));
        }
        if (adaptee.isAssignableFrom(MapGraphic.class)) {
            return adaptee.cast(getMapGraphic());
        }
        if (adaptee.isAssignableFrom(MapGraphicFactory.class)) {
            return adaptee.cast(MapGraphicFactory.getInstance());
        }
        if (mapgraphic != null && adaptee.isAssignableFrom(mapgraphic.getClass())) {
            return adaptee.cast(createMapGraphic());
        }

        return super.resolve(adaptee, monitor);
    }

    /**
     * Returns the MapGraphic
     * 
     * @return the mapgraphic
     */
    public MapGraphic getMapGraphic() {
        return mapgraphic;
    }

    @Override
    public MapGraphicResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (MapGraphicResourceInfo) super.getInfo(monitor);
    }
    protected MapGraphicResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        return new MapGraphicResourceInfo(element);
    }
    /*
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {

        return adaptee != null
                && (adaptee.isAssignableFrom(element.getClass())
                        || adaptee.isAssignableFrom(IService.class)
                        || adaptee.isAssignableFrom(IGeoResource.class)
                        || adaptee.isAssignableFrom(MapGraphic.class)
                        || adaptee.isAssignableFrom(MapGraphicFactory.class) || (getMapGraphic() != null && adaptee
                        .isAssignableFrom(getMapGraphic().getClass())))
                || super.canResolve(adaptee);
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        return parent.getStatus();
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return parent.getMessage();
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        try {
            return new URL(null,
                    parent.getIdentifier().toString() + "#" + id, CorePlugin.RELAXED_HANDLER); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    class MapGraphicResourceInfo extends IGeoResourceInfo {

        public MapGraphicResourceInfo( IConfigurationElement element ) {
            String iconPath = element.getAttribute("icon"); //$NON-NLS-1$
            if (iconPath != null && iconPath.length() > 0)
                this.icon = AbstractUIPlugin.imageDescriptorFromPlugin(element
                        .getNamespaceIdentifier(), iconPath);
        }

        /*
         * @see org.locationtech.udig.catalog.IGeoResourceInfo#getName()
         */
        @Override
        public String getName() {
            return MapGraphicResource.this.name;
        }

        @Override
        public CoordinateReferenceSystem getCRS() {
            return DefaultGeographicCRS.WGS84;
        }

        @Override
        public String getTitle() {
            return getName();
        }

        @Override
        public String getDescription() {
            MessageFormat formatter = new MessageFormat(Messages.MapGraphicResource_description);
            return formatter.format(name);
        }

        @Override
        public ReferencedEnvelope getBounds() {
            if (bounds == null) {
                Envelope e = new Envelope();
                e.setToNull();

                bounds = new ReferencedEnvelope(e, DefaultGeographicCRS.WGS84);
            }
            return bounds;
        }
    }

    /**
     * Returns the MapGraphic for this
     * 
     * @return
     */
    public MapGraphic getGraphic() {
        return getMapGraphic();
    }

}
