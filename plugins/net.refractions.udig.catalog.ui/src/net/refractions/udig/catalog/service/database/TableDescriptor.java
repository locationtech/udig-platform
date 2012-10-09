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
package net.refractions.udig.catalog.service.database;

import com.vividsolutions.jts.geom.Geometry;

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