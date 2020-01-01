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
package org.locationtech.udig.catalog.service.database;

import org.locationtech.jts.geom.Geometry;

/**
 * Data structure that contains the information about a Table in a database.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class TableDescriptor {
    /**
     * Name of the table
     */
    public final String name;
    /**
     * The type of geometry contained in the geometry column
     */
    public final Class<? extends Geometry> geometryType;
    /**
     * if postgis the schema the table is in.  Other databases have other terminology
     */
    public final String schema;
    /**
     * The column containing the geometry
     */
    public final String geometryColumn;
    /**
     * The srid of the geometry column
     */
    public final String srid;
    /**
     * true if there is a misconfiguration so that this looks like a table with geo data but it is not for some reason.
     */
    public final boolean broken;
    
    public TableDescriptor( String name, Class<? extends Geometry> geometryType, String schema, String geometryColumn, String srid, boolean broken ) {
        super();
        this.name = name;
        this.geometryType = geometryType;
        this.schema = schema;
        this.geometryColumn=geometryColumn;
        this.srid = srid;
        this.broken = broken;
    }
    
}
