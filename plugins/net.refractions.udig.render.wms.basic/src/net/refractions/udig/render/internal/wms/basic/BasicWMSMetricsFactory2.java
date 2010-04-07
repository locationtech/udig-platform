/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.render.internal.wms.basic;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;

import org.geotools.data.ows.Layer;
import org.geotools.data.wms.WebMapServer;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * Creates Metrics for the BasicWMSRenderer
 * 
 * @author Richard Gould
 */
public class BasicWMSMetricsFactory2 implements IRenderMetricsFactory {

    Map<String, CoordinateReferenceSystem> crsCache=new HashMap<String, CoordinateReferenceSystem>();
    Map<Pair,MathTransform> transformCache=new HashMap<Pair, MathTransform>();
    Map<Layer, Set<CoordinateReferenceSystem>> legalCRSCache=new HashMap<Layer, Set<CoordinateReferenceSystem>>();
    
    public boolean canRender( IRenderContext toolkit ) {
        if( !toolkit.getLayer().hasResource(WebMapServer.class) ) {
            return false; // not a wms
        }
        CoordinateReferenceSystem crs = toolkit.getViewportModel().getCRS();
        if( crs == null ) {
            return true; // we will assume our default            
        }        
        org.geotools.data.ows.Layer layer;
        try {
            layer = toolkit.getLayer().getResource(org.geotools.data.ows.Layer.class, null);
        } catch (IOException e) {
            return false;
        }

        Set<CoordinateReferenceSystem> crss = legalCRSCache.get(layer);
        if( crss!=null && crss.contains(crs))
            return true;
        if( searchForCRSMatch(crs, layer) ){
            if( crss==null ){
                crss=new HashSet<CoordinateReferenceSystem>();
                legalCRSCache.put(layer, crss);
            }
            crss.add(crs);
            
            return true;
        }
        return false;
    }

    private boolean searchForCRSMatch( CoordinateReferenceSystem crs, org.geotools.data.ows.Layer layer ) {
        Set srs =  layer.getSrs();
        for( Iterator i=srs.iterator(); i.hasNext();) {
            try {
                String epsgCode = (String) i.next();
                CoordinateReferenceSystem rs = getCRS(epsgCode);
                
                if (rs.equals(crs)) {
                    return true;
                }
                
                MathTransform transform = getMathTransform(crs, rs);
                
                if (transform != null) {
                    return true;
                }
            }
            catch( Throwable t ) {
                // could not create a object representation of this code 
            }
        }
		return false; // we cannot handle crs
    }

    private synchronized MathTransform getMathTransform( CoordinateReferenceSystem from, CoordinateReferenceSystem to) throws FactoryException {
        Pair pair = new Pair(from,to);
        MathTransform result=this.transformCache.get(pair);
        if( result==null ){
            result=CRS.findMathTransform(from, to, true);
            transformCache.put(pair, result);
        }
        return result;
    }

    private synchronized CoordinateReferenceSystem getCRS( String epsgCode ) throws NoSuchAuthorityCodeException {
        CoordinateReferenceSystem result = crsCache.get(epsgCode);
        if( result==null  ){
            try {
                result=CRS.decode( epsgCode );
            } catch (FactoryException e) {
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
            crsCache.put(epsgCode, result);
        }
        return result;
    }

    /**
     * @see net.refractions.udig.project.render.RenderMetricsFactory#createMetrics(net.refractions.udig.project.render.RenderContext)
     */
    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        return new BasicWMSMetrics2(context, this);
    }
    /**
     * @see net.refractions.udig.project.render.RenderMetrics#getRendererType()
     */
    public Class getRendererType() {
        return BasicWMSRenderer2.class;
    }

    private class Pair{
        final CoordinateReferenceSystem from,to;

        public Pair( CoordinateReferenceSystem from, CoordinateReferenceSystem to ) {
            this.from = from;
            this.to = to;
        }

        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((from == null) ? 0 : from.hashCode());
            result = PRIME * result + ((to == null) ? 0 : to.hashCode());
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
            final Pair other = (Pair) obj;
            if (from == null) {
                if (other.from != null)
                    return false;
            } else if (!from.equals(other.from))
                return false;
            if (to == null) {
                if (other.to != null)
                    return false;
            } else if (!to.equals(other.to))
                return false;
            return true;
        }
    }
}
