/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.internal.property;

import java.net.URL;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.objectproperty.ObjectPropertyCatalogListener;
import net.refractions.udig.project.BlackboardEvent;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IBlackboardListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.ui.operations.AbstractPropertyValue;
import net.refractions.udig.ui.operations.PropertyValue;

import org.geotools.data.FeatureSource;

/**
 * Returns true if the layer has a FeatureSource as a resource.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.0
 */
public class FeatureSourceResourceProperty extends AbstractPropertyValue<ILayer>
        implements PropertyValue<ILayer> {

    private volatile AtomicBoolean isEvaluating = new AtomicBoolean(false);
    private Set<URL> ids = new CopyOnWriteArraySet<URL>();

    public boolean canCacheResult() {
        return false;
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isTrue( final ILayer object, String value ) {
        isEvaluating.set(true);
        try {
            object.getBlackboard().addListener(new IBlackboardListener(){
                public void blackBoardChanged( BlackboardEvent event ) {
                    if (event.getKey().equals(ProjectBlackboardConstants.LAYER__DATA_QUERY)) {
                        notifyListeners(object);
                    }
                }
                public void blackBoardCleared( IBlackboard source ) {
                    notifyListeners(object);
                }
            });

            final IGeoResource resource = object.findGeoResource(FeatureSource.class);
            if (resource != null && ids.add(resource.getIdentifier())) {
                CatalogPlugin.getDefault().getLocalCatalog().addCatalogListener(
                        new ObjectPropertyCatalogListener(object, resource, isEvaluating, this));
            }
            boolean canResolve = resource.canResolve(FeatureSource.class);
            return canResolve;
        } catch (Exception e) {
            return false;
        } finally {
            isEvaluating.set(false);
        }
    }

}