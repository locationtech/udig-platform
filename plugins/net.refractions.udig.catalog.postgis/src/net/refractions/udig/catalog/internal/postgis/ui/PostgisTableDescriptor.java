package net.refractions.udig.catalog.internal.postgis.ui;

/**
 * Data structure that contains the information about a Table in a postgis database.
 *
 * @author jesse
 * @since 1.1.0
 */
public class PostgisTableDescriptor {
    public final String name;
    public final String geometryType;
    public final String schema;
    public final String geometryColumn;
    public final String srid;

    public PostgisTableDescriptor( String name, String geometryType, String schema, String geometryColumn, String srid ) {
        super();
        this.name = name;
        this.geometryType = geometryType;
        this.schema = schema;
        this.geometryColumn=geometryColumn;
        this.srid = srid;
    }

}
