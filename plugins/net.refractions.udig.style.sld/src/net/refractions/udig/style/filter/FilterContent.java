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
package net.refractions.udig.style.filter;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.StyleContent;
import net.refractions.udig.style.sld.SLDPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;

/**
 * FilterContent is responsible for saving a filter for the current layer;
 * offers the ability to filter out content "as a view" prior to SLD
 * getting a hold of it.
 * @see ShowViewInterceptor
 */
public final class FilterContent extends StyleContent {
    /** style id, used to identify query on a blackboard */
    public static String STYLE_ID = ProjectBlackboardConstants.LAYER__DATA_QUERY;
    
    /**
     * FilterContent constructor.
     */
    public FilterContent() {
        super(STYLE_ID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.project.StyleContent#getStyleClass()
     */
    public Class<?> getStyleClass() {
        return Boolean.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.project.StyleContent#save(org.eclipse.ui.IMemento,
     *      java.lang.Object)
     */
    public void save( IMemento memento, Object value ) {
        Filter filter = null;
        if( value instanceof Filter ){
            filter = (Filter) value;
        }
        else if( value instanceof Query ){
            Query query = (Query) value;
            filter = query.getFilter();
        }
        if( filter != null ){
            String cql = CQL.toCQL( filter );
            memento.putString("cql", cql );
        }
        memento.putString("type", "ViewStle"); //$NON-NLS-1$ //$NON-NLS-2$
        memento.putString("version", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.project.StyleContent#load(org.eclipse.ui.IMemento)
     */
    public Object load( IMemento momento ) {
        String cql = momento.getString("cql");
        if( cql == null || cql.length() == 0 ){
            return Filter.INCLUDE;
        }
        try {
            Filter filter = CQL.toFilter(cql);
            return filter;
        }
        catch( CQLException eek ){
            SLDPlugin.log("Could not restore filter:"+eek, eek);
            return Filter.EXCLUDE; // something is the matter
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.project.StyleContent#load(java.net.URL)
     */
    public Object load( URL url, IProgressMonitor m ) throws IOException {
        return null;
    }
    
    /**
     * This will need to know the "scheme."
     */
    public Object createDefaultStyle( IGeoResource resource, Color colour, 
            IProgressMonitor m ) throws IOException {
        return Filter.INCLUDE;
    }    
}