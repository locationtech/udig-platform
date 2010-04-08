package net.refractions.udig.catalog.geotools.data;

import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;

import org.geotools.data.ResourceInfo;

public class FeatureSourceGeoResourceInfo extends IGeoResourceInfo {

    private ResourceInfo info;

    public FeatureSourceGeoResourceInfo( ResourceInfo info ) {
        this.info = info;
        
        this.bounds = info.getBounds();
        this.description = info.getDescription();
        this.keywords = info.getKeywords().toArray(new String[0]);
        this.name = info.getName();
        this.schema = info.getSchema();
        this.title = info.getTitle();
        
        ISharedImages images = CatalogUIPlugin.getDefault().getImages();
        this.icon = images.getImageDescriptor( ISharedImages.FEATURE_OBJ ); // generic!
    }
 
    public ResourceInfo toResourceInfo(){
        return info;
    }
    
}
