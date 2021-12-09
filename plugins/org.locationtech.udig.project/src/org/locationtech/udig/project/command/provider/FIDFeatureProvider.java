/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command.provider;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureIterator;
import org.geotools.util.factory.GeoTools;
import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;

public class FIDFeatureProvider implements IBlockingProvider<SimpleFeature> {

    private IBlockingProvider<ILayer> layerProvider;

    private String fid;

    private SimpleFeature feature;

    public FIDFeatureProvider(String fid2, IBlockingProvider<ILayer> layer2) {
        this.layerProvider = layer2;
        if (fid2 == null) {
            throw new NullPointerException("Fid must not be null"); //$NON-NLS-1$
        }
        this.fid = fid2;
    }

    @Override
    public synchronized SimpleFeature get(IProgressMonitor monitor, Object... params) {
        if (feature == null) {
            FilterFactory filterFactory = CommonFactoryFinder
                    .getFilterFactory(GeoTools.getDefaultHints());
            Id fidFilter = filterFactory.id(FeatureUtils.stringToId(filterFactory, fid));

            if (monitor == null) {
                monitor = new NullProgressMonitor();
            }
            try {
                monitor.beginTask("Get Feature", 100);

                ILayer layer = layerProvider.get(SubMonitor.convert(monitor, 25));
                FeatureSource<SimpleFeatureType, SimpleFeature> source = layer
                        .getResource(FeatureSource.class, SubMonitor.convert(monitor, 25));

                FeatureIterator<SimpleFeature> iter = source.getFeatures(fidFilter).features();
                monitor.worked(25);
                try {
                    if (iter.hasNext()) {
                        feature = iter.next();
                    } else {
                        // feature not available
                    }
                    monitor.worked(25);
                } finally {
                    iter.close();
                }
            } catch (IOException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            } finally {
                monitor.done();
            }
        }
        return feature;
    }

}
