package net.refractions.udig.catalog.internal.mysql.ui;

/**
 * Data structure that contains the information about a Table in a mysql database.
 * probably needs to be changed
 * @author jesse
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public class MySQLTableDescriptor {
    public final String name;
    public final String geometryType;
    public final String geometryColumn;
    public final String srid;

    public MySQLTableDescriptor( String name, String geometryType, String geometryColumn, String srid ) {
        super();
        this.name = name;
        this.geometryType = geometryType;
        this.geometryColumn=geometryColumn;
        this.srid = srid;
    }

}
