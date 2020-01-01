/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands.edit;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;

/**
 * Adds the new feature to the
 * 
 * @author jones
 * @since 1.1.0
 */
public class AddFeatureCommand extends AbstractCommand implements UndoableMapCommand {

    private SimpleFeature feature;
    private ILayer layer;
    private FeatureStore<SimpleFeatureType, SimpleFeature> resource;
    private String fid;

    public AddFeatureCommand( SimpleFeature feature, ILayer layer ) {
        this.feature = feature;
        this.layer = layer;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        resource = layer.getResource(FeatureStore.class, monitor);
        if (resource == null) {
            return;
        }
        FeatureCollection<SimpleFeatureType, SimpleFeature> c = new org.geotools.feature.collection.AdaptorFeatureCollection(
                "addFeatureCollection", resource.getSchema()){
            @Override
            public int size() {
                return 1;
            }
            @Override
            public ReferencedEnvelope getBounds() {
                return ReferencedEnvelope.reference(feature.getBounds());
            }
            @Override
            protected Iterator openIterator() {
                return new Iterator(){
                    SimpleFeature next = feature;
                    public Object next() {
                        SimpleFeature tmp = next;
                        next = null;
                        return tmp;
                    }
                    public boolean hasNext() {
                        return next != null;
                    }
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            @Override
            protected void closeIterator( Iterator close ) {
            }
        };
        List<FeatureId> added = resource.addFeatures(c);
        for( FeatureId featureId : added){
            fid = featureId.getID();
            break;
        }
    }

    public SimpleFeature getNewFeature() throws IOException {
        if (resource != null) {
            FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools
                    .getDefaultHints());
            FeatureCollection<SimpleFeatureType, SimpleFeature> features = resource.getFeatures(
                                                                                        filterFactory.id(
                                                                                                FeatureUtils.stringToId(filterFactory, fid)));
            FeatureIterator<SimpleFeature> iter = features.features();
            try {
                return iter.next();
            } finally {
                iter.close();
            }
        }
        return null;
    }

    public String getName() {
        return Messages.AddFeatureCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools
                .getDefaultHints());
        resource.removeFeatures(filterFactory.id(FeatureUtils.stringToId(filterFactory, fid)));
    }

    /**
     * @return Returns the fid.
     */
    public String getFid() {
        return fid;
    }

}
