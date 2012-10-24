/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.catalog.ui.export;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.ui.ProgressManager;

import org.geotools.data.DefaultQuery;
import org.geotools.data.Query;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A data object that is the values in the tree items in the {@link ExportResourceSelectionPage} viewer.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class Data {
    private CoordinateReferenceSystem crs;
    final private IGeoResource resource;
    private Collection<IGeoResource> exportedResources = new HashSet<IGeoResource>();
    private Query query;
    private String name;
    private boolean checked;
    
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    /**
     * new instance
     * @param resource the resource to export
     * @param query the query to restrict the export, may be null
     */
    public Data(final IGeoResource resource, Query query ) {
        super();
        try {
            crs=resource.getInfo(ProgressManager.instance().get()).getCRS();
            this.resource = resource;
            checked = true;
        } catch (IOException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
        this.query = query;
    }
    public Data(final IGeoResource resource) {
        this( resource, new DefaultQuery());
    }

    public CoordinateReferenceSystem getCRS() {
        return crs;
    }
    public IGeoResource getResource() {
        return resource;
    }

    public void setCRS( CoordinateReferenceSystem newCRS ) {
        crs=newCRS;
    }

    /**
     * Adds a collection of resources to the set of exported resources.
     *
     * @param exportedResource the newly exported resources 
     */
    public void addNewResource( IGeoResource resource ) {
        exportedResources.add(resource);
    }
    public void addNewResources( Collection<IGeoResource> resources ){
        exportedResources.addAll( resources );
    }

    /**
     * @return Returns the exportedResources.
     */
    public Collection<IGeoResource> getExportedResources() {
        return exportedResources;
    }
    
    public Query getQuery() {
        return query;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked( boolean checked ) {
        this.checked = checked;
    }
    
    
    
}
