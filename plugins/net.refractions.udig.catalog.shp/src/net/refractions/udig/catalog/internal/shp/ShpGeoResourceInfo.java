/**
 *
 */
package net.refractions.udig.catalog.internal.shp;

import java.io.IOException;
import java.net.URI;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.shp.internal.Messages;
import net.refractions.udig.ui.graphics.Glyph;

import org.eclipse.core.runtime.IStatus;
import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

class ShpGeoResourceInfo extends IGeoResourceInfo {
    /**
     *
     */
    private final ShpGeoResourceImpl resource;
    private FeatureType ft = null;
    ShpGeoResourceInfo(ShpGeoResourceImpl shpGeoResourceImpl) throws IOException{
        resource = shpGeoResourceImpl;
        ft = resource.parent.getDS(null).getSchema();

            try {
                FeatureSource source=resource.parent.getDS(null).getFeatureSource();
                Envelope tmpBounds=source.getBounds();
                if( tmpBounds instanceof ReferencedEnvelope)
                	bounds=(ReferencedEnvelope) tmpBounds;
                else
                	bounds=new ReferencedEnvelope(tmpBounds, getCRS());
                if( bounds==null ){
                    bounds=new ReferencedEnvelope(new Envelope(), getCRS());
                    FeatureIterator iter=source.getFeatures().features();
                    try{
                        while(iter.hasNext() ) {
                            Feature element = iter.next();
                            if( bounds.isNull() )
                                bounds.init(element.getBounds());
                            else
                                bounds.expandToInclude(element.getBounds());
                        }
                    }finally{
                        iter.close();
                    }
                }
            } catch (Exception e) {
                CatalogPlugin.getDefault().getLog().log(new org.eclipse.core.runtime.Status(IStatus.WARNING,
                       "net.refractions.udig.catalog", 0, Messages.ShpGeoResourceImpl_error_layer_bounds, e ));   //$NON-NLS-1$
                bounds = new ReferencedEnvelope(new Envelope(), getCRS());
            }

            icon=Glyph.icon(ft);
            keywords = new String[]{
                ".shp","Shapefile", //$NON-NLS-1$ //$NON-NLS-2$
                ft.getTypeName(),
                ft.getNamespace().toString()
            };
    }

    public CoordinateReferenceSystem getCRS() {
        return ft.getDefaultGeometry().getCoordinateSystem();
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
}
