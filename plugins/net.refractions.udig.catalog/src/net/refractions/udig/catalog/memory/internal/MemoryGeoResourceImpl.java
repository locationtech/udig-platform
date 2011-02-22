package net.refractions.udig.catalog.memory.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ITransientResolve;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeocentricCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public class MemoryGeoResourceImpl extends IGeoResource implements ITransientResolve {

	/** parent service * */
	private MemoryServiceImpl parent;

	/** feature type name * */
	String type;

	/** info object * */
	private volatile ScratchResourceInfo info;

    private volatile Status status;
    private volatile Throwable message;

	public MemoryGeoResourceImpl(String type, MemoryServiceImpl parent) {
		this.type = type;
		this.parent = parent;
	}

	@Override
	public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor)
			throws IOException {
		if (adaptee == null)
			return null;
        if (adaptee.isAssignableFrom(ITransientResolve.class)) {
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(IService.class))
			return adaptee.cast(parent);
		if (adaptee.isAssignableFrom(IGeoResource.class))
			return adaptee.cast(this);
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class))
			return adaptee.cast(getInfo(monitor));
		if (adaptee.isAssignableFrom(FeatureStore.class))
			return adaptee.cast(parent.getDS().getFeatureSource(type));
        if (adaptee.isAssignableFrom(FeatureSource.class))
            return adaptee.cast(parent.getDS().getFeatureSource(type));
        if (adaptee.isAssignableFrom(FeatureType.class))
            return adaptee.cast(parent.getDS().getSchema(type));

		return super.resolve(adaptee, monitor);
	}

    public IService service( IProgressMonitor monitor ) throws IOException {
        return parent;
    }
	public <T> boolean canResolve(Class<T> adaptee) {
		if (adaptee == null)
			return false;

		return adaptee.isAssignableFrom(IGeoResourceInfo.class)
				|| adaptee.isAssignableFrom(FeatureStore.class)
				|| adaptee.isAssignableFrom(FeatureSource.class)
				|| adaptee.isAssignableFrom(IService.class)
                || adaptee.isAssignableFrom(ITransientResolve.class)||
                super.canResolve(adaptee);
	}

	public Status getStatus() {
        if( status == null )
            return parent.getStatus();
        return status;
	}

	public Throwable getMessage() {
        if( message==null )
            return parent.getMessage();
        return message;
	}

	public URL getIdentifier() {
		try {
			return new URL(parent.getIdentifier().toString() + "#" + type); //$NON-NLS-1$
		} catch (MalformedURLException e) {
			return parent.getIdentifier();
		}
	}

	@Override
	public IGeoResourceInfo getInfo(IProgressMonitor monitor)
			throws IOException {
		if (info == null) {
			parent.rLock.lock();
            try{
				if (info == null) {
					info = new ScratchResourceInfo();
				}
            }finally{
                parent.rLock.unlock();
            }
            }
		return info;

	}

	class ScratchResourceInfo extends IGeoResourceInfo {
		FeatureType ft = null;
        FeatureSource source;

		ScratchResourceInfo() throws IOException {
			try {
				source = parent.getDS().getFeatureSource(type);
				ft = source.getSchema();
			} catch (Exception e) {
                status=Status.BROKEN;
                message=new Exception("Error obtaining the feature type: "+type).initCause(e); //$NON-NLS-1$
				bounds = new ReferencedEnvelope(new Envelope(), getCRS());
			}

			keywords = new String[] { type, ft.getNamespace().toString() };
		}

		public CoordinateReferenceSystem getCRS() {
			GeometryAttributeType defaultGeometry = ft.getDefaultGeometry();
            if( defaultGeometry!=null )
                return defaultGeometry.getCoordinateSystem();
            return null;
		}

		public String getName() {
			return ft.getTypeName();
		}

		public URI getSchema() {
			return ft.getNamespace();
		}

		public String getTitle() {
			return ft.getTypeName();
		}

        @Override
        public ReferencedEnvelope getBounds() {
            Envelope bounds;
            try {
                bounds = source.getBounds();
                if( bounds == null )
                    return new ReferencedEnvelope(new Envelope(), DefaultGeographicCRS.WGS84);
                if( bounds instanceof ReferencedEnvelope)
                    return (ReferencedEnvelope) bounds;
                return new ReferencedEnvelope(bounds, getCRS());
            } catch (IOException e) {
                return new ReferencedEnvelope(new Envelope(), DefaultGeographicCRS.WGS84);
            }
        }

	}
}
