/**
 * 
 */
package net.refractions.udig.project.command.provider;

import java.io.IOException;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.ILayer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;

public class FIDFeatureProvider implements IBlockingProvider<SimpleFeature> {

    private IBlockingProvider<ILayer> layer;
    private String fid;
    private SimpleFeature feature;

    public FIDFeatureProvider( String fid2, IBlockingProvider<ILayer> layer2 ) {
        this.layer = layer2;
        this.fid = fid2;
    }

    public synchronized SimpleFeature get( IProgressMonitor monitor, Object... params  ) {
        if (feature == null) {
            FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
			Id fidFilter = filterFactory
                    .id(FeatureUtils.stringToId(filterFactory, fid));
            try {
                FeatureSource<SimpleFeatureType, SimpleFeature> source = layer.get(monitor).getResource(FeatureSource.class, monitor);
                FeatureIterator<SimpleFeature> iter = source.getFeatures(fidFilter).features();
                try {
                    feature=iter.next();
                } finally {
                    iter.close();
                }

            } catch (IOException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }
        return feature;
    }

}