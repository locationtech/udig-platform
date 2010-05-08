package net.refractions.udig.catalog.geotools.data;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.WeakHashMap;

import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;
import net.refractions.udig.core.jts.ReferencedEnvelopeCache;

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

    public FeatureSourceGeoResourceInfo( ResourceInfo info ) {
        this.info = info;
        
        System.out.println("Getting a bounds: " + new GregorianCalendar().getTimeInMillis());
        this.bounds = ReferencedEnvelopeCache.getReferencedEnvelope(info.getCRS());
        
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
