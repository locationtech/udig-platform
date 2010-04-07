package net.refractions.udig.catalog.geotools.data;

import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataAccess;
import org.geotools.data.FeatureSource;
import org.geotools.data.ResourceInfo;
import org.opengis.feature.type.Name;

public class FeatureSourceGeoResource extends IGeoResource {

    protected Name name;

    public FeatureSourceGeoResource( DataStoreService service, Name name ) {
        this.service = service;
        this.name = name;
    }
    public DataStoreService service( IProgressMonitor monitor ) throws IOException {
        return (DataStoreService) this.service;
    };
    public FeatureSource< ? , ? > toFeatureSource() throws IOException {
        DataAccess< ? , ? > access = service(null).toDataAccess();
        return access.getFeatureSource(name);
    }
    @Override
    protected IGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        FeatureSource< ? , ? > featureSource = toFeatureSource();
        ResourceInfo gtInfo = featureSource.getInfo();
        return new FeatureSourceGeoResourceInfo( gtInfo );
    }

    @Override
    public URL getIdentifier() {
        return null;
    }

    public Throwable getMessage() {
        return null;
    }

    public Status getStatus() {
        return null;
    }

}
