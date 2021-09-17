/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.style.filter;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.StyleContent;
import org.locationtech.udig.style.sld.SLDPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;

/**
 * FilterContent is responsible for saving query information used to preprocess the current layer.
 * filter for the current layer.
 * <p>
 * Currently there are three ways to do this!
 * <ul>
 * <li>Filter: if the value is a filter it is combined with the featureSource name and turned into a
 * Query (this is usually supplied by a programmer).</li>
 * <li>Query: if the value is a Query it is used directly. This is usually supplied by a
 * programmer).</li>
 * <li>FilterStyle: Complete filter settings supplied by user using the FilterConfigurator user
 * interface. The number of settings here may grow in the future.</li>
 * </ul>
 * These settings offer the ability to "filter" out content; or using Query pre process the columns
 * or sort information prior to the FeatureSource being used (this is accomplished using
 * ShowViewInterceptor).
 *
 * @see ShowViewInterceptor
 */
public final class FilterContent extends StyleContent {

    /** style id, used to identify query on a blackboard */
    public static String STYLE_ID = ProjectBlackboardConstants.LAYER__STYLE_FILTER;

    /**
     * FilterContent constructor.
     */
    public FilterContent() {
        super(STYLE_ID);
    }

    public Class< ? > getStyleClass() {
        return Boolean.class;
    }

    public void save( IMemento memento, Object value ) {
        Filter filter = null;
        Boolean aoiFilter = null;
        if (value instanceof Filter) {
            filter = (Filter) value;
        } else if (value instanceof Query) {
            Query query = (Query) value;
            filter = query.getFilter();
        } else if (value instanceof FilterStyle) {
            filter = ((FilterStyle) value).getFilter();
            aoiFilter = ((FilterStyle) value).isAoiFilter();
        }
        if (filter != null && filter != Filter.INCLUDE) {
            String cql = CQL.toCQL(filter);
            memento.putString("cql", cql);
        }
        if (aoiFilter != null && aoiFilter == true) {
            memento.putString("aoi", "true");
        }
        memento.putString("type", "FilterContent"); //$NON-NLS-1$ //$NON-NLS-2$
        memento.putString("version", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Retrieves a {@link FilterStyle}, or null if not provided.
     */
    public Object load( IMemento memento ) {
        String type = memento.getString("type");
        String version = memento.getString("version");
        if( "FilterContent".equals(type) && "1.0".equals(version)){
            String cql = memento.getString("cql");
            Filter filter = null;
            if (cql != null && cql.length() != 0) {
                try {
                    filter = CQL.toFilter(cql);
                } catch (CQLException eek) {
                    SLDPlugin.log("Could not restore filter:" + eek, eek);
                }
            }
            String aoi = memento.getString("aoiFilter"); //$NON-NLS-1$
            boolean isAOI = "true".equalsIgnoreCase(aoi);

            if (isAOI) {
                FilterStyle styleFilter = new FilterStyle();
                styleFilter.setAoiFilter(true);
                if (filter != null) {
                    styleFilter.setFilter(filter);
                }
                return styleFilter;
            } else {
                if (filter == null) {
                    return null;
                } else {
                    return new FilterStyle(filter);
                }
            }
        }
        return null; // not a supported memento format
    }

    public Object load( URL url, IProgressMonitor m ) throws IOException {
        return null;
    }

    /**
     * Default FilterStyle for the provided resource.
     * <p>
     * Note some resources may have a good default value here; as an example background layers
     * probably never want to have AOI turned on by default.
     */
    public Object createDefaultStyle( IGeoResource resource, Color colour, IProgressMonitor m )
            throws IOException {
        return new FilterStyle();
    }
}
