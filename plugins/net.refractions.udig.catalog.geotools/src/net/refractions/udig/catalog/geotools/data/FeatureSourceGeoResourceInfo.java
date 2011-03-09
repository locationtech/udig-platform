package net.refractions.udig.catalog.geotools.data;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;
import net.refractions.udig.core.jts.ReferencedEnvelopeCache;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.ResourceInfo;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Description of a FeatureSource.
 * <p>
 * This implementation is 
 * @author jody
 * @since 1.2.0
 */
public class FeatureSourceGeoResourceInfo extends IGeoResourceInfo {

	private ResourceInfo info;

    public FeatureSourceGeoResourceInfo( ResourceInfo info ) {
        this.info = info;
        
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
        if( title != null ){
            this.title = title.replace('_', ' ');
        }
        
        this.icon = CatalogUIPlugin.getImageDescriptor( ISharedImages.FEATURE_OBJ ); // generic!
    }
    /**
     * Can be called to refresh the bounds from the internal info object.
     * 
     * @param monitor
     */
    public void refresh( IProgressMonitor monitor ){
        if ( monitor == null ) monitor = new NullProgressMonitor();
        try {
            monitor.beginTask("refresh bounds", 100 );
            bounds = info.getBounds();
        }
        finally {
            monitor.done();
        }
    }
    
    public ResourceInfo toResourceInfo(){
        return info;
    }
    
    public ResourceInfo getInfo() {
        return info;
    }
}
