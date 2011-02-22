package net.refractions.udig.tutorials.catalog.csv;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;

import org.eclipse.core.runtime.IProgressMonitor;

public class CSVGeoResource extends IGeoResource {

    private URL url;
    private CSVService service;
    private CSVGeoResourceInfo info;
    private CSV csv;

    public CSVGeoResource( CSVService service ) {
        File file = service.getFile();
        this.service = service;
        try {
            url = new URL( service.getIdentifier()+"#"+file.getName() );
        } catch (MalformedURLException e) {
        }
    }

    @Override
    public URL getIdentifier() {
        return url;
    }

    @Override
    public CSVGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null) { //lazy creation
            synchronized (this) { //support concurrent access
                if (info == null) {
                    info = new CSVGeoResourceInfo( this, monitor );
                }
            }
        }
        return info;
    }

    @Override
    public CSVService service( IProgressMonitor monitor ) throws IOException {
        return service;
    }

    public Throwable getMessage() {
        return service.getMessage();
    }

    public Status getStatus() {
        return service.getStatus();
    }

    /**
     * CSV API used to access the file.
     * @param monitor
     * @return CSV
     * @throws IOException
     */
    public CSV getCSV( IProgressMonitor monitor ) throws IOException {
        if (csv == null) { // lazy creation
            synchronized (this) { //support concurrent access
                if (csv == null) {
                    csv = new CSV( service.getFile() );
                }
            }
        }
        return csv;
    }

    @Override
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee.isAssignableFrom(CSV.class) || super.canResolve(adaptee);
    }
    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if( adaptee.isAssignableFrom(CSV.class) ){
            return adaptee.cast( getCSV( monitor) );
        }
        return super.resolve(adaptee, monitor);
    }
}
