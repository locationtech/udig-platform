/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.geotools.data;

import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.ISharedImages;
import org.locationtech.udig.core.jts.ReferencedEnvelopeCache;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.ResourceInfo;

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
         * TODO : This is because of null namespaces in Name
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
        
        this.icon = CatalogUIPlugin.getDefault().getImageDescriptor( ISharedImages.FEATURE_OBJ ); // generic!
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
