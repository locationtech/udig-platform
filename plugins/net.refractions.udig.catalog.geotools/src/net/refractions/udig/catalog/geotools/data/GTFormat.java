package net.refractions.udig.catalog.geotools.data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;

import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.DataAccessFactory.Param;
import org.geotools.data.property.PropertyDataStoreFactory;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.jdbc.JDBCDataStoreFactory;

/**
 * Captures knowledge of GeoTools formats; each format is able to recognize a factory/connection
 * parameter pair and answer some questions.
 * 
 * @since 1.2.0
 */
enum GTFormat {
        /*JDBCNG {
        @Override
        public boolean accepts( DataAccessFactory factory ) {
            return factory instanceof JDBCDataStoreFactory;
        }

        @Override
        public String getTitle( DataAccessFactory factory, Map<String, ? > params ) {
            return JDBC.getTitle(factory, params);
        }

        @Override
        public ID toID( DataAccessFactory factory, Map<String, ? > params ) {
            return JDBC.toID(factory, params);
        }
    },*/
    JDBC {
        @Override
        public boolean accepts( DataAccessFactory factory ) {
            if (factory instanceof JDBCDataStoreFactory) {
                return false;
            }
            for( Param param : factory.getParametersInfo() ) {
                if ("dbtype".equalsIgnoreCase(param.key)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getTitle( DataAccessFactory factory, Map<String, ? > params ) {
            Params keys = new Params( factory );
            String dbtype = keys.lookup( String.class,"dbtype",params );            
            String the_host = keys.lookup( String.class,"host",params );
            Integer intPort = keys.lookup( Integer.class,"port",params );
            String the_database = keys.lookup( String.class,"database",params);
            String the_username = keys.lookup( String.class,"user",params);
            String the_password = keys.lookup( String.class,"passwd",params);

            String port = intPort == null ? "" : ":"+intPort;
            
            String the_spec = the_host+port+"/"+the_database; //$NON-NLS-1$  //$NON-NLS-2$
            
            ID id = toID( dbtype, the_username, the_password, the_host, intPort, the_database);           
            return id.labelServer();
        }

        public ID toID( String dbtype, String the_username, String the_password, String the_host,
                Object intPort, String the_database ) {
            if( intPort == null ){
                intPort = "";
            }
            String the_spec = "jdbc://" + the_username //$NON-NLS-1$
                    + ":" + the_password + "@" + the_host //$NON-NLS-1$ //$NON-NLS-2$
                    + ":" + intPort + "/" + the_database; //$NON-NLS-1$  //$NON-NLS-2$
            return new ID(the_spec, dbtype );
        }
        
        @Override
        public ID toID( DataAccessFactory factory, Map<String, ? > params ) {
            Params keys = new Params( factory );
            String dbtype = keys.lookup( String.class,"dbtype",params );            
            String the_host = keys.lookup( String.class,"host",params );
            Integer intPort = keys.lookup( Integer.class,"port",params );
            String the_database = keys.lookup( String.class,"database",params);
            String the_username = keys.lookup( String.class,"user",params);
            String the_password = keys.lookup( String.class,"passwd",params);

            ID id = toID( dbtype, the_username, the_password, the_host, intPort, the_database);
            return id;
        }

        @Override
        public ImageDescriptor getIcon() {
            return CatalogUIPlugin.getImageDescriptor( ISharedImages.DATABASE_OBJ ); // generic!
        }
    },
    FILE {
        @Override
        public boolean accepts( DataAccessFactory factory ) {
            if (factory instanceof FileDataStoreFactorySpi) {
                return true;
            }
            return false;
        }

        @Override
        public String getTitle( DataAccessFactory factory, Map<String, ? > params ) {
            Params keys = new Params( factory );
            File file = keys.lookup(File.class,params );
            if (file != null) {
                ID id = new ID(file, factory.getDisplayName());
                return id.labelServer();
            }
            URL url = keys.lookup(URL.class,params );          
            if (url != null) {
                ID id = new ID(url, factory.getDisplayName());
                return id.labelServer();
            }
            return null;
        }

        @Override
        public ID toID( DataAccessFactory factory, Map<String, ? > params ) {
            Params keys = new Params( factory );
            File file = keys.lookup(File.class,params );
            if (file != null) {
                ID id = new ID(file, factory.getDisplayName());
                return id;
            }
            URL url = keys.lookup(URL.class,params );                      
            if (url != null) {
                ID id = new ID(url, factory.getDisplayName());
                return id;
            }
            return OTHER.toID(factory, params);
        }
        @Override
        public ImageDescriptor getIcon() {
            return CatalogUIPlugin.getImageDescriptor( ISharedImages.FEATURE_FILE_OBJ ); // generic!
        }
    },
    WFS {
        @Override
        public boolean accepts( DataAccessFactory factory ) {
            return factory instanceof WFSDataStoreFactory;
        }

        @Override
        public String getTitle( DataAccessFactory factory, Map<String, ? > params ) {
            Params keys = new Params( factory );
            URL url = keys.lookup(URL.class,params );                      
            if (url != null) {
                ID id = new ID(url, factory.getDisplayName());
                return id.labelServer();
            }
            return null;
        }

        @Override
        public ID toID( DataAccessFactory factory, Map<String, ? > params ) {
            return null;
        }
        @Override
        public ImageDescriptor getIcon() {
            return CatalogUIPlugin.getImageDescriptor( ISharedImages.WFS_OBJ ); // generic!
        }
    },
    PROPERTY {
        @Override
        public boolean accepts( DataAccessFactory factory ) {
            return factory instanceof PropertyDataStoreFactory;
        }
        @Override
        public String getTitle( DataAccessFactory factory, Map<String, ? > params ) {
            Params keys = new Params( factory );
            File file = keys.lookup(File.class,params );                       
            if (file != null) {
                ID id = new ID(file, factory.getDisplayName());
                return id.labelServer();
            }
            return null;
        }
        @Override
        public ID toID( DataAccessFactory factory, Map<String, ? > params ) {
            Params keys = new Params( factory );
            File file = keys.lookup(File.class,params );                       
            if (file != null) {
                ID id = new ID(file, factory.getDisplayName());
                return id;
            }
            return OTHER.toID(factory, params);
        }
        @Override
        public ImageDescriptor getIcon() {
            return CatalogUIPlugin.getImageDescriptor( ISharedImages.FEATURE_FILE_OBJ ); // generic!
        }
    },
    OTHER {
        @Override
        public boolean accepts( DataAccessFactory factory ) {
            return true;
        }

        @Override
        public String getTitle( DataAccessFactory factory, Map<String, ? > params ) {
            Params keys = new Params( factory );
            File file = keys.lookup(File.class,params );                                   
            if (file != null) {
                ID id = new ID(file, factory.getDisplayName());
                return id.labelServer();
            }
            URL url = keys.lookup(URL.class,params );                                   
            if (url != null) {
                ID id = new ID(url, factory.getDisplayName());
                return id.labelServer();
            }
            StringBuffer buf = new StringBuffer();
            buf.append("unknown:/");
            for( Object value : params.values() ){
                if( value == null ) continue;
                buf.append("/");
                buf.append( value );
            }
            return buf.toString();
        }

        @Override
        public ID toID( DataAccessFactory factory, Map<String, ? > params ) {
            Params keys = new Params( factory );
            File file = keys.lookup(File.class,params ); 
            if (file != null) {
                ID id = new ID(file, factory.getDisplayName());
                return id;
            }
            URL url = keys.lookup(URL.class,params ); 
            if (url != null) {
                ID id = new ID(url, factory.getDisplayName());
                return id;
            }
            StringBuffer buf = new StringBuffer();
            buf.append("unknown:/");
            for( Object value : params.values() ){
                if( value == null ) continue;
                buf.append("/");
                buf.append( value );
            }
            return new ID( buf.toString(), "unknown" );
        }
        @Override
        public ImageDescriptor getIcon() {
            return CatalogUIPlugin.getImageDescriptor( ISharedImages.DATASTORE_OBJ ); // generic!
        }
    };

    /**
     * Recognize a factory/param pair.
     * 
     * @param factory
     * @return true if this format recognizes this factory/param pair.
     */
    abstract boolean accepts( DataAccessFactory factory );
    public abstract ID toID( DataAccessFactory factory, Map<String, ? > params );
    public String getTitle( DataAccessFactory factory, Map<String, ? > params ) {
        ID id = toID(factory, params);
        return id.labelServer();
    }

    public static GTFormat format( DataAccessFactory factory ) {
        for( GTFormat format : values() ) {
            if (format.accepts(factory)) {
                return format;
            }
        }
        return OTHER;
    }

    public abstract ImageDescriptor getIcon();

}
