/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import java.io.IOException;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;

import org.eclipse.emf.ecore.EObject;

/**
 * TODO Purpose of net.refractions.udig.project.internal
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface LayerFactory extends EObject {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * Creates a list of {@linkplain Layer}objects from the provided selection.
     * 
     * @param selection A selection of CatalogEntries obtained from a {@linkplain CatalogTreeViewer}
     *        object.
     * @return a list of {@linkplain Layer}objects from the provided selection.
     * @throws IOException
     */
    public abstract List<Layer> getLayers( List selection ) throws IOException;

    /**
     * Creates a list of layers from a service. Each GeoResource in the service will have a
     * corresponding layer created for it. Becareful this can result in a large number of layers.
     * 
     * @param service a service that will be used to created layers
     * @return a list of layers
     * @throws IOException
     */
    public abstract List<Layer> getLayers( IService service ) throws IOException;

    /**
     * Creates a layer from a service and a resource. The layer is represented the data in resource.
     * May return null if it cannot resolve the service.
     * 
     * @param service
     * @param resource
     * @return
     * @throws IOException
     */
    public abstract Layer createLayer( IGeoResource resource ) throws IOException;
    /**
     * @return
     * @model opposite="layerFactory" many="false"
     */
    public Map getMap();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.LayerFactory#getMap <em>Map</em>}' container reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Map</em>' container reference.
     * @see #getMap()
     * @generated
     */
    void setMap( Map value );

}
