package net.refractions.udig.tutorials.catalog.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.URLUtils;

import org.eclipse.core.runtime.IProgressMonitor;

public class CSVService extends IService {
    
    private Map<String, Serializable> params;
    private URL url;
    
    private Throwable msg;
    private File file;
    List<CSVGeoResource> members;
    
    CSVService( Map<String, Serializable> params ){
        this.params = params;
        url = (URL) params.get(CSVServiceExtension.KEY);
    }
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }
    
    @Override
    public CSVServiceInfo getInfo(IProgressMonitor monitor) throws IOException {
    	return (CSVServiceInfo) super.getInfo(monitor);
    }
    protected CSVServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
    	return new CSVServiceInfo( this );
    }
    
    public List<CSVGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        if (members == null) { //lazy creation
            synchronized (this) { //support concurrent access
                if (members == null) {
                    CSVGeoResource dataHandle = new CSVGeoResource( this );
                    members = Collections.singletonList( dataHandle );
                }
            }
        }
        return members;
    }
    public URL getIdentifier() {
        return url;
    }

    public File getFile(){
        if (file == null) { //lazy creation
            synchronized (this) { //support concurrent access
                if (file == null) {
                    try {
                        file = URLUtils.urlToFile( url );                        
                    } catch (Throwable t) {
                        msg = t;
                    }
                    if( !file.exists() ){
                        msg = new FileNotFoundException(url.toString());
                    }
                }
            }
        }
        return file;
    }
    public Throwable getMessage() {
        return msg;
    }
    public Status getStatus() {
        //did an error occur
        if (msg != null)
            return Status.BROKEN;
        
        //has the file been parsed yet
        if (file == null)
            return Status.NOTCONNECTED;
        
        return Status.CONNECTED;
    }
    
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee.isAssignableFrom(File.class) || super.canResolve(adaptee);
    }
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if( adaptee.isAssignableFrom(File.class) ){
            return adaptee.cast( getFile() );
        }
        return super.resolve(adaptee, monitor);
    }
}