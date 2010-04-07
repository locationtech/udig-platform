/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.impl;

import java.net.URI;
import java.util.Set;

import javax.swing.Icon;

import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceInterceptor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Intercepts the IGeoResource Info and wraps it with a decorator that modifies getCRS and getBounds if the CRS has been set on the layer.
 * <p>
 * This is for the use case where the CRS on the IGeoResource is wrong 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class GeoResourceInfoInterceptor implements IResourceInterceptor<IGeoResourceInfo> {

    /**
     * @deprecated here as a work around for a bug in the CRS Generation not having a valid area
     */
    static final ReferencedEnvelope UNKNOWN_BOUNDS = new ReferencedEnvelope(-179.99, 179.99, -79.99, 79.99, DefaultGeographicCRS.WGS84);

    public IGeoResourceInfo run( ILayer layer, IGeoResourceInfo resource, Class requestedType ) {
        if (layer instanceof LayerImpl) {
            LayerImpl impl = (LayerImpl) layer;
            return new Wrapper(impl, resource );
        }
        return resource;
    }
    
    private static class Wrapper extends IGeoResourceInfo{
        private final IGeoResourceInfo info;
        private LayerImpl layer;

        public Wrapper( LayerImpl impl, final IGeoResourceInfo info ) {
            super();
            this.info = info;
            this.layer=impl;
        }

        public ReferencedEnvelope getBounds() {
            
            ReferencedEnvelope tmp = info.getBounds();
            if (tmp == null ){
                
                ReferencedEnvelope referencedEnvelope = MapImpl.toReferencedEnvelope(getCRS().getDomainOfValidity(), getCRS());
                if( referencedEnvelope!=null )
                    return referencedEnvelope;
                
                tmp = UNKNOWN_BOUNDS;
            }

            if( tmp.isNull() ){
                return new ReferencedEnvelope(getCRS());
            }
            return tmp;
        }

        public CoordinateReferenceSystem getCRS() {
            if( layer.cRS!=null )
                return layer.cRS;
            CoordinateReferenceSystem crs = info.getCRS();
            if (crs == null) {
                return DefaultEngineeringCRS.GENERIC_2D;
            }
            return crs;
        }

        public String getDescription() {
            return info.getDescription();
        }

        @Override
        public ImageDescriptor getImageDescriptor() {
            return info.getImageDescriptor();
        }
        
        public Icon getIcon() {
            return info.getIcon();
        }

        public Set<String> getKeywords() {
            return info.getKeywords();
        }

        public String getName() {
            return info.getName();
        }

        public URI getSchema() {
            return info.getSchema();
        }

        public String getTitle() {
            return info.getTitle();
        }

        public String toString() {
            return info.toString();
        }        

        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((info == null) ? 0 : info.hashCode());
            return result;
        }

        @Override
        public boolean equals( Object obj ) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Wrapper other = (Wrapper) obj;
            if (info == null) {
                if (other.info != null)
                    return false;
            } else if (!info.equals(other.info))
                return false;
            return true;
        }
        
        
        
    }

}
