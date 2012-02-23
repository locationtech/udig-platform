/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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

package net.refractions.udig.style.filter;

import java.io.IOException;

import net.refractions.udig.aoi.IAOIService;
import net.refractions.udig.ui.PlatformGIS;

import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.Filters;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Data structure for holding the filter that is used for the style of a layer.
 * <p>
 * This is stored in the style blackboard; and we have provided additional constructors
 * to help "upconvert" simple fitlers or queries to a LayerStyleFilter.
 * 
 * @see FilterContent
 * 
 * @author paul.pfeiffer
 * @version 1.3.1
 *
 */
public class FilterStyle {
    
    public FilterStyle(){
        // just the defaults please
    }
    public FilterStyle(Object value){
        if( value == null ){
            // ignore me use defaults
        }
        else if( value instanceof FilterStyle){
            FilterStyle copy = (FilterStyle) value;
            this.aoiFilter = copy.aoiFilter;
            this.filter = copy.filter;
        }
        else if (value instanceof Filter){
            this.filter = (Filter) value;
        }
        else if (value instanceof Query){
            Query query = (Query) value;
            this.filter = query.getFilter();
        }
    }
    public FilterStyle( FilterStyle copy ){
        this.aoiFilter = copy.aoiFilter;
        this.filter = copy.filter;
    }
    public FilterStyle( Filter filter ){
        this.filter = filter;
    }
    /** AOI toggle; if this is true please look up the AOI settings and use them */
    private boolean aoiFilter;
    
    /** User aupplied filter (optional) */
    private Filter filter;
    
    /**
     * User supplied filter (optional).
     * 
     * @return User supplied filter, may be <code>null</code>
     */
    public Filter getFilter() {
        return filter;
    }
    
    /**
     * User supplied filter (optional).
     * @param value User supplied filter (optional) may use null
     */
    public void setFilter(Filter value) {
        filter = value;
    }
    /**
     * AOI filter requested.
     * @return true of AOI filter is requested.
     */
    public boolean isAoiFilter() {
        return aoiFilter;
    }
    /**
     * AOI filter request.
     * 
     * @param aoiFilter true to request AOI filter be applied
     */
    public void setAoiFilter( boolean aoiFilter ) {
        this.aoiFilter = aoiFilter;
    }
    
    /**
     * Helper method used to combine user requested filter and any global settings such as AOI.
     * <p>
     * Please note that since this is intended for drawing the default will be Filter.INCLUDE (so that
     * we show everything unless otherwise instructed).
     * <p>
     * This method takes into account {@link #isAoiFilter()} and {@link #getFilter()}
     */
    public Filter toFilter( FeatureType schema){
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        Filter filter = getFilter(); // start with user supplied filter!
        if( filter == Filter.INCLUDE ){
            filter = null; // please ignore the user they want it all
        }
        if( isAoiFilter() ){
            try {
                IAOIService aoiService = PlatformGIS.getAOIService();
                CoordinateReferenceSystem crs = aoiService.getExtent().getCoordinateReferenceSystem();
                Geometry geometry = aoiService.getGeometry();
                CoordinateReferenceSystem dataCRS = schema.getCoordinateReferenceSystem();
                if (!crs.equals(dataCRS)) {
                    MathTransform transform = CRS.findMathTransform(crs, dataCRS);
                    geometry = JTS.transform(geometry, transform);
                }
                String the_geom = schema.getGeometryDescriptor().getName().getLocalPart();
                Filter spatialFilter = ff.intersects( ff.property(the_geom), ff.literal( geometry ) );            
    //            if( filter != null ){
    //                return ff.and( filter, spatialFilter );
    //            }
    //            else {
    //                return spatialFilter;
    //            }
                filter = Filters.and(ff,  filter, spatialFilter);
                
                return filter;
            }
            catch (MismatchedDimensionException e) {
                // could not filter by AOI
            } catch (TransformException e) {
                // could not filter by AOI
            } catch (FactoryException e) {
                // could not filter by AOI
            }
        }
        if( filter != null ){
            return filter;
        }
        else {
            return Filter.INCLUDE; // draw everything
        }
    }
    
}
