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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.core.internal.CorePlugin;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.geotools.data.DataAccessFactory.Param;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * This class abstracts out all of the service and database specific code for a
 * Geotools Database-based DatastoreIService extension.
 * 
 * @author jeichar
 */
public abstract class DatabaseServiceDialect {
    // The parameter information required for creating a Geotools Datastore.
	// Teradata was used as the template
	/**
	 * The key of the parameter that (at least in Teradata) identifies the schema
	 * that the table resides in.
	 */
	public final Param schemaParam;

	/**
	 * The key of the parameter that identifies the database (within the
	 * database, this is concept is inherited from Teradata)
	 */
	public final Param databaseParam;

	/**
	 * The key that identifies the host server of the database
	 */
	public final Param hostParam;

	/**
	 * The key that identifies the server port for connecting to the database
	 */
	public final Param portParam;

	/**
	 * The key that identifies connecting user's username
	 */
	public final Param usernameParam;

	/**
	 * The key that identifies connecting user's password
	 */
	public final Param passwordParam;

	/**
	 * The key that indicates the type of Datastore to create.  For example TeradataDataStoreFactory#DBTYPE
	 */
    public final Param typeParam;
    
    public final String dbType;
    
    /**
     * The prefix/host to put in a url that identifies this type of database.
     * 
     * For example the Teradata one is: "jdbc.Teradata"
     */
    public final String urlPrefix;

    public final DatabaseWizardLocalization localization;

    
    public DatabaseServiceDialect(Param schemaParam, Param databaseParam,
            Param hostParam, Param portParam, Param usernameParam,
            Param passwordParam, Param typeParam, String dbType, String urlPrefix, DatabaseWizardLocalization localization) {
        this.schemaParam = schemaParam;
        this.databaseParam = databaseParam;
        this.hostParam = hostParam;
        this.portParam = portParam;
        this.usernameParam = usernameParam;
        this.passwordParam = passwordParam;
        this.typeParam = typeParam;
        this.dbType = dbType != null ? dbType : (String) typeParam.sample;
        this.urlPrefix = urlPrefix;
        this.localization = localization;
    }

	public Collection<URL> constructResourceIDs(TableDescriptor[] descriptors, Map<String, Serializable> params) {
        try {
            URL url = toURL(params);
            String serviceURL = url.toExternalForm();
            List<URL> urls = new ArrayList<URL>();
            for( int i = 0; i < descriptors.length; i++ ) {
                TableDescriptor descriptor = descriptors[i];
                urls.add(new URL(url, serviceURL+"#"+descriptor.name)); //$NON-NLS-1$
            }
            return urls;
        } catch (MalformedURLException e) {
            // really shouldn't happen
            log("Can't make URL", e); //$NON-NLS-1$
            return Collections.emptySet();
        }
	}
	
    public URL toURL( Map<String, Serializable> params ) throws MalformedURLException {
        String the_host = (String) params.get(hostParam.key);
        Integer intPort = (Integer) params.get(portParam.key);
        String the_database = (String) params.get(databaseParam.key);
        String the_username = (String) params.get(usernameParam.key);

        URL toURL = toURL(the_username, the_host, intPort, the_database);
        return toURL;
    }

    public URL toURL( String the_username, String the_host,
            Integer intPort, String the_database ) throws MalformedURLException {
        String the_spec = urlPrefix+"://" + the_username //$NON-NLS-1$
                + "@" + the_host //$NON-NLS-1$ //$NON-NLS-2$
                + ":" + intPort + "/" + the_database; //$NON-NLS-1$  //$NON-NLS-2$
        return toURL(the_spec);
    }

    public URL toURL( String the_spec ) throws MalformedURLException {
        return new URL(null, the_spec, CorePlugin.RELAXED_HANDLER);
    }

    public abstract IDialogSettings getDialogSetting();

    public abstract void log( String message, Throwable e );

    /**
     * Creates a {@link DatabaseConnectionRunnable}.
     *
     * @param host the url of the host to connect to
     * @param port the port on which to connect
     * @param username the username for connections
     * @param password the password for connection
     * @param database In Teradata there are databases within a database.  This is a common construct but often
     *                 named differently.  Please try to make the mapping.  
     * @return {@link DatabaseConnectionRunnable}
     */
    public abstract DatabaseConnectionRunnable createDatabaseConnectionRunnable( String host, int port,
            String username, String password );

    /**
     * The TabSelectionTab is always created, this method allows the specific implementation to add
     * custom tabs. By default this method returns an empty HashMap
     *
     * @param tabFolder the parent folder of the tabs
     */
    public Map<Control, Tab> createOptionConnectionPageTabs( TabFolder tabFolder, DataConnectionPage containingPage ){
        return new HashMap<Control, Tab>();
    }

    /**
     * Creates a {@link LookUpSchemaRunnable}.
     *
     * @param host the url of the host to connect to
     * @param port the port on which to connect
     * @param username the username for connections
     * @param password the password for connection
     * @param database In Teradata there are databases within a database.  This is a common construct but often
     *                 named differently.  Please try to make the mapping.  
     * @return {@link LookUpSchemaRunnable}
     */
    public abstract LookUpSchemaRunnable createLookupSchemaRunnable( String host, int port, String username,
            String password, String database );
    
    /**
     * Convert a geometry string to the class it represents.  The case is unimportant in the default version  
     * <p>
     * Default names are:
     * <ul>
     * <li>GEOMETRY</li>
     * <li>GEOMETRY</li>
     * <li>GEOMETRYCOLLECTION</li>
     * <li>POINT</li>
     * <li>MULTIPOINT</li>
     * <li>POLYGON</li>
     * <li>MULTIPOLYGON</li>
     * <li>LINESTRING</li>
     * <li>MULTILINESTRING</li>  
     * </ul>
     * 
     * @param geomName The name of the geometry read from the database
     * @return the vividsolutions class
     */
    public Class<? extends Geometry> toGeomClass(String geomName) {
    	if(geomName.equalsIgnoreCase("GEOMETRYCOLLECTION") || geomName.equalsIgnoreCase("GEOMETRY")) return Geometry.class;
    	if(geomName.equalsIgnoreCase("POINT")) return Point.class;
    	if(geomName.equalsIgnoreCase("MULTIPOINT")) return MultiPoint.class;
    	if(geomName.equalsIgnoreCase("POLYGON")) return Polygon.class;
    	if(geomName.equalsIgnoreCase("MULTIPOLYGON")) return MultiPolygon.class;
    	if(geomName.equalsIgnoreCase("LINESTRING")) return LineString.class;
    	if(geomName.equalsIgnoreCase("MULTILINESTRING")) return MultiLineString.class;
    	return Geometry.class;
    }
    
    /**
     * Returns a control for configuring extra parameters.  if it returns null the component will not appear in the wizard
     * by default this method calls hostPageExtraParams and constructs an editable table control
     */
    protected ExtraParamsControl createHostPageExtraParamControl() {
    	List<ExtraParams> params = hostPageExtraParams();
    	if(params == null || params.isEmpty()) {
    		return null;
    	} else {
    		return new TableBasedExtraParamsControl(params);
    	}
    }
    
    /**
     * Return The extra params to add to the Host page for extra configuration
     * 
     * This is called by {@link #createHostPageExtraParamControl(Control)}.  Either this method or 
     * that can be overridden to add parameters to  
     *  
     * @return The extra params to add to the Host page for extra configuration
     */
    protected List<ExtraParams> hostPageExtraParams() {
    	return Collections.emptyList();
    }
    
}
