package net.refractions.udig.catalog.geotools.data;

import java.util.Collection;
import java.util.GregorianCalendar;

import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;

import org.geotools.data.ResourceInfo;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class FeatureSourceGeoResourceInfo extends IGeoResourceInfo {

	private ResourceInfo info;


	private ReferencedEnvelope getCRSBounds() {
		CoordinateReferenceSystem crs = info.getCRS();
		if(crs == null)
			return new ReferencedEnvelope();
		Extent extent = crs.getDomainOfValidity();
		Collection<? extends GeographicExtent> elem = extent.getGeographicElements();
		double xmin = Double.MAX_VALUE, ymin = Double.MAX_VALUE;
		double xmax = Double.MIN_VALUE, ymax = Double.MIN_VALUE;
		try {
			for(GeographicExtent ext : elem) {
				if(ext instanceof BoundingPolygon) {
					BoundingPolygon bp = (BoundingPolygon)ext;
					Collection<? extends org.opengis.geometry.Geometry> geoms = bp.getPolygons();
					for(org.opengis.geometry.Geometry geom : geoms) {
						Envelope env = geom.getEnvelope();
						if(env.getMinimum(0) < xmin)
							xmin = env.getMinimum(0);
						if(env.getMaximum(0) > xmax)
							xmax = env.getMaximum(0);
						if(env.getMinimum(1) < ymin)
							ymin = env.getMinimum(1);
						if(env.getMaximum(1) > ymax)
							ymax = env.getMaximum(1);
					}
				} else if(ext instanceof GeographicBoundingBox) {
					GeographicBoundingBox gbb = (GeographicBoundingBox)ext;
					ReferencedEnvelope env = new ReferencedEnvelope(DefaultGeographicCRS.WGS84);
					env.expandToInclude(gbb.getWestBoundLongitude(), gbb.getNorthBoundLatitude());
					env.expandToInclude(gbb.getEastBoundLongitude(), gbb.getSouthBoundLatitude());
					env = env.transform(info.getCRS(), true);
					if(env.getMinX() < xmin)
						xmin = env.getMinX();
					if(env.getMaxX() > xmax)
						xmax = env.getMaxX();
					if(env.getMinY() < ymin)
						ymin = env.getMinY();
					if(env.getMaxY() > ymax)
						ymax = env.getMaxY();
				}
			}
			if(xmin == Double.MAX_VALUE || 
					ymin == Double.MAX_VALUE ||
					xmax == Double.MIN_NORMAL ||
					ymax == Double.MAX_VALUE) {
				System.out.println("No sensible extents generated.");
				return new ReferencedEnvelope(info.getCRS());
			}
			return new ReferencedEnvelope(xmin, xmax, ymin, ymax, info.getCRS());
		} catch(Throwable ex) {
			ex.printStackTrace(System.out);
			return new ReferencedEnvelope(info.getCRS());
		}
	}

    public FeatureSourceGeoResourceInfo( ResourceInfo info ) {
        this.info = info;
        
        System.out.println("Getting a bounds: " + new GregorianCalendar().getTimeInMillis());
        this.bounds = getCRSBounds();
        
        this.description = info.getDescription();
        this.keywords = info.getKeywords().toArray(new String[0]);
        this.name = info.getName();
        /* 
         * This is a horrible hack to handle null namespaces in Name
         * If the namespace NPE's, we can just leave schema as it is.
         */
        try {
            this.schema = info.getSchema();
        } catch(NullPointerException ex) {
            ;
        }
        this.title = info.getTitle();
        
        ISharedImages images = CatalogUIPlugin.getDefault().getImages();
        this.icon = images.getImageDescriptor( ISharedImages.FEATURE_OBJ ); // generic!
    }
 
    public ResourceInfo toResourceInfo(){
        return info;
    }
    
    public ResourceInfo getInfo() {
        return info;
    }
}
