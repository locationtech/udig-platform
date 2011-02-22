package net.refractions.udig.catalog.util;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.IResolve.Status;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.catalog.Catalog;
import org.geotools.catalog.CatalogInfo;
import org.geotools.catalog.GeoResource;
import org.geotools.catalog.GeoResourceInfo;
import org.geotools.catalog.Resolve;
import org.geotools.catalog.ResolveChangeEvent;
import org.geotools.catalog.ResolveChangeListener;
import org.geotools.catalog.Service;
import org.geotools.catalog.ServiceInfo;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.ProgressListener;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

/**
 * This class provides support for GeoTools Java 1.4 catalog interfaces.
 * <p>
 * This factory produces Java 5 wrappers around base GeoTools constructs.
 *
 * @author Jody Garnett
 */
public class GeoToolsAdapters {
    static Catalog localWrapper;
    /**
     * Serves up the local catalog as a GeoTools Catalog.
     * <p>
     * This is helpful when creating many of the geotools service
     * instances (who often want to provide event notification).
     *
     * @return Catalog
     */
    static synchronized public Catalog getLocalCatalog(){
        if( localWrapper != null ){
            return localWrapper;
        }
        localWrapper = new Catalog(){

            private ICatalog local = CatalogPlugin.getDefault().getLocalCatalog();

            public void add( Service service ) throws UnsupportedOperationException {
                local.add( GeoToolsAdapters.service( service ) );
            }
            public List find( URI arg0, ProgressListener arg1 ) {
                return null;
            }

            public List findService( URI arg0, ProgressListener arg1 ) {
                return null;
            }

            public CatalogInfo getInfo( ProgressListener arg0 ) throws IOException {
                return null;
            }

            public void remove( Service service ) throws UnsupportedOperationException {

            }

            public void replace( URI arg0, Service arg1 ) throws UnsupportedOperationException {

            }

            public Object resolve( Class adaptee, ProgressListener monitor ) throws IOException {
                return local.resolve(adaptee, progress(monitor));
            }

            public List search( String arg0, Envelope arg1, ProgressListener arg2 ) throws IOException {
                return null;
            }

            public void addListener( ResolveChangeListener listener) throws UnsupportedOperationException {
            }

            public boolean canResolve( Class adaptee ) {
                return local.canResolve( adaptee );
            }

            public void fire( ResolveChangeEvent event ) {
            }

            public URI getIdentifier() {
                try {
                    return local.getIdentifier().toURI();
                } catch (URISyntaxException e) {
                    throw new RuntimeException( e );
                }
            }

            public Throwable getMessage() {
                return local.getMessage();
            }

            public Status getStatus() {
                return status( local.getStatus() );
            }

            public List members( ProgressListener arg0 ) throws IOException {
                return null;
            }

            public Resolve parent( ProgressListener monitor ) throws IOException {
                return null;
            }

            public void removeListener( ResolveChangeListener arg0 ) {
            }

        };
        return localWrapper;
    }
	static public List<IGeoResource> resourceList( List<GeoResource> resourceList ){
		List<IGeoResource> list = new ArrayList<IGeoResource>();
		for( GeoResource handle : resourceList ){
			list.add( resource( handle ));
		}
		return list;
	}
	static public IGeoResource resource( final GeoResource resource ) {
		final URL url;
		try {
			url = resource.getIdentifier().toURL();
		} catch (MalformedURLException e) {
			return null;
		}
		return new IGeoResource(){
			@Override
			public URL getIdentifier() {
				return url;
			}
			@Override
			public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
                if (monitor == null)
                    monitor = new NullProgressMonitor();

                if (adaptee == null) {
                    throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
                }
                if( resource.canResolve( adaptee )){
                    return adaptee.cast( resource.resolve( adaptee, progress( monitor)) );
                }
                return super.resolve(adaptee, monitor);

			}

			public <T> boolean canResolve(Class<T> adaptee) {
				return resource.canResolve( adaptee ) ||
                     super.canResolve(adaptee);
			}
            public IGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
                return info( resource.getInfo( progress( monitor )));
            }
            public IService service( IProgressMonitor monitor ) throws IOException {
                return GeoToolsAdapters.service( (Service) resource.parent( progress(monitor)));
            }
			public Throwable getMessage() {
				return resource.getMessage();
			}

			public Status getStatus() {
				return status( resource.getStatus() );
			}
		};
	}
    static public IGeoResourceInfo info( final GeoResourceInfo info ){
        if( info == null ) return null;
        return new IGeoResourceInfo(){
            @Override
            public ReferencedEnvelope getBounds() {
                return (ReferencedEnvelope) info.getBounds();
            }
            @Override
            public CoordinateReferenceSystem getCRS() {
                return info.getCRS();
            }
            @Override
            public String getDescription() {
                return info.getDescription();
            }
            @Override
            public ImageDescriptor getIcon() {
                // need to pain super.getIcon() into a graphic
                return null;
            }
            @Override
            public String[] getKeywords() {
                return info.getKeywords();
            }
            @Override
            public String getName() {
                return info.getName();
            }
            @Override
            public URI getSchema() {
                return info.getSchema();
            }
            @Override
            public String getTitle() {
                return info.getTitle();
            }
        };
    }
    static public IServiceInfo info( final ServiceInfo info ){
        return new IServiceInfo(){
            public String getAbstract() {
                return info.getAbstract();
            }
            public String getDescription() {
                return info.getDescription();
            }
            public ImageDescriptor getIcon() {
                // we need to paint info.getIcon();
                return null;
            }
            public String[] getKeywords() {
                return info.getKeywords();
            }
            public URL getPublisher() {
                return super.getPublisher();
            }
            public URI getSchema() {
                return info.getSchema();
            }
            public URL getSource() {
                try {
                    return info.getSource().toURL();
                } catch (MalformedURLException e) {
                    return null;
                }
            }
            public String getTitle() {
                return info.getTitle();
            }
        };
    }
	static public IService service( final Service service){
		final URL url;
		try {
			url = service.getIdentifier().toURL();
		} catch (MalformedURLException e) {
			return null;
		}
		return new IService(){
			@SuppressWarnings("unchecked")
			public Map<String, Serializable> getConnectionParams() {
				return service.getConnectionParams();
			}

			@Override
			public List< ? extends IGeoResource> resources( IProgressMonitor monitor )
			        throws IOException {
			    List children = service.members( progress( monitor ));
			    return resourceList( children );
			}

			@Override
			public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
                if (monitor == null)
                    monitor = new NullProgressMonitor();

                if (adaptee == null) {
                    throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
                }
                if( service.canResolve( adaptee )){
                    return adaptee.cast( service.resolve( adaptee, progress( monitor)) );
                }
                return super.resolve(adaptee, monitor);
			}

			public <T> boolean canResolve(Class<T> adaptee) {
                return service.canResolve( adaptee ) ||
                       super.canResolve(adaptee);
			}

            @Override
            public IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
                return info( service.getInfo( progress(monitor)));
            }

			public URL getIdentifier() {
				return url;
			}

			public Throwable getMessage() {
				return service.getMessage();
			}

			public Status getStatus() {
				return status( service.getStatus() );
            }
            public void dispose( IProgressMonitor monitor ) {
            }
		};
	}
	static public Status status(org.geotools.catalog.Resolve.Status status) {
		if( status == null ) return null;
		if( status == org.geotools.catalog.Resolve.Status.BROKEN ) return Status.BROKEN;
		if( status == org.geotools.catalog.Resolve.Status.CONNECTED ) return Status.CONNECTED;
		if( status == org.geotools.catalog.Resolve.Status.NOTCONNECTED) return Status.NOTCONNECTED;
		return Status.NOTCONNECTED;
	}
    static public org.geotools.catalog.Resolve.Status status(Status status) {

        if( status == Status.BROKEN ) return org.geotools.catalog.Resolve.Status.BROKEN;
        if( status == Status.CONNECTED) return org.geotools.catalog.Resolve.Status.CONNECTED;
        if( status == Status.NOTCONNECTED) return org.geotools.catalog.Resolve.Status.NOTCONNECTED;
        return null;
    }
	static public ProgressListener progress(final IProgressMonitor monitor) {
		if( monitor == null ) return null;
		return new ProgressListener(){
			private String description;
			private int progress;

			public void complete() {
				monitor.done();
			}
			public void dispose() {
				description = null;
			}
			public void exceptionOccurred(Throwable arg0) {
			}
			public String getDescription() {
				return description;
			}
			public boolean isCanceled() {
				return monitor.isCanceled();
			}
			public void progress(float amount) {
				int current = (int)(100.0 * amount);
				monitor.worked( current - progress );
				progress = current;
			}

			public void setCanceled(boolean arg0) {
				monitor.setCanceled(true);
			}

			public void setDescription(String text) {
				description = text;
			}

			public void started() {
				monitor.beginTask( description, 100);
			}

			public void warningOccurred(String arg0, String arg1, String arg2) {
			}
		};
	}

    static public IProgressMonitor progress(final ProgressListener monitor) {
        if( monitor == null ) return null;
        return new IProgressMonitor(){
            int total;
            int amount;
            public void beginTask( String name, int totalWork ) {
                amount = 0;
                total = totalWork;
                monitor.setDescription( name );
                monitor.progress( work() );
            }
            float work(){
                return (float) amount / (float) total;
            }
            public void done() {
                amount = total;
                monitor.complete();
                monitor.dispose();
            }
            public void internalWorked( double work ) {
            }
            public boolean isCanceled() {
                return monitor.isCanceled();
            }
            public void setCanceled( boolean cancel ) {
                monitor.setCanceled( cancel );
            }
            public void setTaskName( String name ) {
                monitor.setDescription( name );
            }
            public void subTask( String name ) {
                monitor.setDescription( name );
            }
            public void worked( int work ) {
                amount += total;
            }
        };
    }
}
