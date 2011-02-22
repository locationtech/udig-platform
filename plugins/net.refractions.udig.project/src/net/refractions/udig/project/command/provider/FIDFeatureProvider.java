/**
 *
 */
package net.refractions.udig.project.command.provider;

import java.io.IOException;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.ILayer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.FidFilter;
import org.geotools.filter.FilterFactoryFinder;

public class FIDFeatureProvider implements IBlockingProvider<Feature> {

    private IBlockingProvider<ILayer> layer;
    private String fid;
    private Feature feature;

    public FIDFeatureProvider( String fid2, IBlockingProvider<ILayer> layer2 ) {
        this.layer = layer2;
        this.fid = fid2;
    }

    public synchronized Feature get( IProgressMonitor monitor, Object... params ) {
        if (feature == null) {
            FidFilter fidFilter = FilterFactoryFinder.createFilterFactory()
                    .createFidFilter(fid);
            try {
                FeatureSource source = layer.get(monitor).getResource(FeatureSource.class, monitor);
                FeatureIterator iter = source.getFeatures(fidFilter).features();
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
