/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.property;

import java.net.URL;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.geotools.data.FeatureStore;
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
 * returns true if the layer has a FeatureStore as a resource.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class FeatureStoreResourceProperty extends AbstractPropertyValue<ILayer>
        implements PropertyValue<ILayer> {

    private volatile AtomicBoolean isEvaluating = new AtomicBoolean(false);

    private Set<URL> ids = new CopyOnWriteArraySet<>();

    @Override
    public boolean canCacheResult() {
        return false;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean isTrue(final ILayer object, String value) {
        isEvaluating.set(true);
        try {

            object.getBlackboard().addListener(new IBlackboardListener() {
                @Override
                public void blackBoardChanged(BlackboardEvent event) {
                    if (event.getKey().equals(ProjectBlackboardConstants.LAYER__DATA_QUERY)) {
                        notifyListeners(object);
                    }
                }

                @Override
                public void blackBoardCleared(IBlackboard source) {
                    notifyListeners(object);
                }

            });

            final IGeoResource resource = object.findGeoResource(FeatureStore.class);
            if (resource != null && ids.add(resource.getIdentifier())) {
                CatalogPlugin.getDefault().getLocalCatalog().addCatalogListener(
                        new ObjectPropertyCatalogListener(object, resource, isEvaluating, this));
            }

            // TODO codereview: This resolves http://jira.codehaus.org/browse/UDIG-1686
            // final FeatureStore<?,?> store = object.getResource(FeatureStore.class,
            // ProgressManager.instance().get());
            // return store != null;
            boolean canResolve = resource.canResolve(FeatureStore.class);
            return canResolve;
        } catch (Exception e) {
            return false;
        } finally {
            isEvaluating.set(false);
        }
    }

}
