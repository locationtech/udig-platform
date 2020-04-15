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
package org.locationtech.udig.project.ui.internal.adapters;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.AdaptableFeature;
import org.locationtech.udig.project.ui.internal.properties.FeaturePropertySource;
import org.locationtech.udig.project.ui.internal.properties.GeomPropertySource;
import org.locationtech.udig.project.ui.internal.properties.IGeoResourcePropertySource;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Adapts Geotools objects to Eclipse objects, such as Adaptable objects, property sources, etc...
 * 
 * @author jeichar
 * @since 0.3
 */
public class FeatureAdapterFactory implements IAdapterFactory {

    static final Class[] adapters = new Class[]{IPropertySource2.class, IPropertySource.class,
            ITreeContentProvider.class, ILabelProvider.class, IContentProvider.class,
            IStructuredContentProvider.class, ITableLabelProvider.class, ITableColorProvider.class,
            FeatureTableProvider.class, IAdaptable.class};

    static final Class[] adaptableClasses = new Class[]{SimpleFeature.class, Coordinate.class,
            Coordinate[].class, Geometry.class, IGeoResource.class};

    /**
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
     */
    public Object getAdapter( Object adaptableObject, Class adapterType ) {
        if (!canAdaptTo(adapterType) || !canAdapt(adaptableObject))
            return null;
        if (IPropertySource.class.isAssignableFrom(adapterType)) {
            if (SimpleFeature.class.isAssignableFrom(adaptableObject.getClass()))
                return createFeaturePropertySource(adaptableObject);

            if (Geometry.class.isAssignableFrom(adaptableObject.getClass()))
                return createGeometryPropertySource(adaptableObject);

            if (IGeoResource.class.isAssignableFrom(adaptableObject.getClass()))
                return createServicePropertySource(adaptableObject);
        }
        if (ITableColorProvider.class.isAssignableFrom(adapterType)
                || ITableLabelProvider.class.isAssignableFrom(adapterType)
                || FeatureTableProvider.class.isAssignableFrom(adapterType))
            return new FeatureTableProvider();
        if (IContentProvider.class.isAssignableFrom(adapterType)
                || ILabelProvider.class.isAssignableFrom(adapterType))
            return new FeatureViewerProvider();

        if (IAdaptable.class.isAssignableFrom(adapterType)) {
            if (SimpleFeature.class.isAssignableFrom(adaptableObject.getClass()))
                return new AdaptableFeature((SimpleFeature) adaptableObject);
        }
        return null;
    }

    /**
     * Returns true if the adaptableObject can be adapted to one of the supported adapters.
     * 
     * @param adaptableObject the object to adapt
     * @return true if the adaptableObject can be adapted to one of the supported adapters.
     */
    public boolean canAdapt( Object adaptableObject ) {
        for( int i = 0; i < adaptableClasses.length; i++ ) {
            if (adaptableClasses[i].isAssignableFrom(adaptableObject.getClass()))
                return true;
        }
        return false;
    }

    /**
     * Returns true if this factory can adapt to the adapterType class.
     * <p>
     * A convenience method that searches the array from getAdapterList()
     * </p>
     * 
     * @param adapterType the class to see if the factory can create an adapter for.
     * @return true if an adapter can be made for the type.
     */
    public boolean canAdaptTo( Class adapterType ) {
        for( int i = 0; i < adapters.length; i++ ) {
            if (adapterType.isAssignableFrom(adapters[i]))
                return true;
        }
        return false;
    }

    /**
     * Creates a PropertySource for Features
     * 
     * @param adaptableObject
     * @return a PropertySource for Features
     */
    private IPropertySource2 createFeaturePropertySource( Object adaptableObject ) {
        return new FeaturePropertySource((SimpleFeature) adaptableObject);
    }

    /**
     * Creates a PropertySource for Features
     * 
     * @param adaptableObject
     * @return a PropertySource for Features
     */
    private IPropertySource2 createGeometryPropertySource( Object adaptableObject ) {
        return new GeomPropertySource((Geometry) adaptableObject);
    }

    /**
     * Creates a PropertySource for Features
     * 
     * @param adaptableObject
     * @return a PropertySource for Features
     */
    private IPropertySource2 createServicePropertySource( Object adaptableObject ) {
        return new IGeoResourcePropertySource((IGeoResource) adaptableObject);
    }

    /**
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    public Class[] getAdapterList() {
        Class[] c=new Class[adapters.length];
        System.arraycopy(adapters, 0, c, 0, c.length);
        return c;
    }

}
