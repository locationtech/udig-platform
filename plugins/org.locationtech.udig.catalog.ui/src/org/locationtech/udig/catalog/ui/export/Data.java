/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.export;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;

import org.geotools.data.Query;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.ui.ProgressManager;

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
    private Charset charset;
    
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
        this( resource, new Query());
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
    
    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
    
}
