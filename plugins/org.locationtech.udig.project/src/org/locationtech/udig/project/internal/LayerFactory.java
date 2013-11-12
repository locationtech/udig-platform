/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal;

import java.io.IOException;
import java.util.List;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;

import org.eclipse.emf.ecore.EObject;

/**
 * TODO Purpose of org.locationtech.udig.project.internal
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface LayerFactory extends EObject {
    /**
     * Creates a list of {@linkplain Layer}objects from the provided selection.
     * 
     * @param selection A selection of CatalogEntries obtained from a {@linkplain CatalogTreeViewer}
     *        object.
     * @return a list of {@linkplain Layer}objects from the provided selection.
     * @throws IOException
     */
    public abstract List<Layer> getLayers(List selection) throws IOException;

    /**
     * Creates a list of layers from a service. Each GeoResource in the service will have a
     * corresponding layer created for it. Becareful this can result in a large number of layers.
     * 
     * @param service a service that will be used to created layers
     * @return a list of layers
     * @throws IOException
     */
    public abstract List<Layer> getLayers(IService service) throws IOException;

    /**
     * Creates a layer from a service and a resource. The layer is represented the data in resource.
     * May return null if it cannot resolve the service.
     * 
     * @param service
     * @param resource
     * @return
     * @throws IOException
     */
    public abstract Layer createLayer(IGeoResource resource) throws IOException;

    /**
     * @return
     * @model opposite="layerFactory" many="false"
     */
    public Map getMap();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.LayerFactory#getMap <em>Map</em>}' container reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Map</em>' container reference.
     * @see #getMap()
     * @generated
     */
    void setMap(Map value);

}
