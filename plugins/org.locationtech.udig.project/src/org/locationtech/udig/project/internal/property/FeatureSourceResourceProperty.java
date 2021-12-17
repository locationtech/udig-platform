/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.property;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.geotools.data.FeatureSource;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.objectproperty.ObjectPropertyCatalogListener;
import org.locationtech.udig.project.BlackboardEvent;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.IBlackboardListener;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.ui.operations.AbstractPropertyValue;
import org.locationtech.udig.ui.operations.PropertyValue;

/**
 * Returns true if the layer has a FeatureSource as a resource.
 *
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.0
 */
public class FeatureSourceResourceProperty extends AbstractPropertyValue<ILayer>
        implements PropertyValue<ILayer> {

    private AtomicBoolean isEvaluating = new AtomicBoolean(false);
    private Set<URI> ids = new CopyOnWriteArraySet<>();

    @Override
    public boolean canCacheResult() {
        return false;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean isTrue( final ILayer object, String value ) {
        isEvaluating.set(true);
        try {
            object.getBlackboard().addListener(new IBlackboardListener(){
                @Override
                public void blackBoardChanged( BlackboardEvent event ) {
                    if (ProjectBlackboardConstants.LAYER__DATA_QUERY.equals(event.getKey())) {
                        notifyListeners(object);
                    }
                }
                @Override
                public void blackBoardCleared( IBlackboard source ) {
                    notifyListeners(object);
                }
            });

            final IGeoResource resource = object.findGeoResource(FeatureSource.class);
            if (resource == null) {
                return false;
            }
            if (ids.add(resource.getIdentifier().toURI())) {
                CatalogPlugin.getDefault().getLocalCatalog().addCatalogListener(
                        new ObjectPropertyCatalogListener(object, resource, isEvaluating, this));
            }
            return resource.canResolve(FeatureSource.class);
        } catch (Exception e) {
            return false;
        } finally {
            isEvaluating.set(false);
        }
    }

}
