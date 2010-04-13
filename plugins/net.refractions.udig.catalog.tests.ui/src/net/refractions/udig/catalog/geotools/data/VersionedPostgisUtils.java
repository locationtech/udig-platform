package net.refractions.udig.catalog.geotools.data;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.geotools.data.postgis.VersionedPostgisDataStoreFactory;
    
public class VersionedPostgisUtils {

        public static Fixture newFixture(String props) throws IOException {
            PropertyResourceBundle resource;
            resource = new PropertyResourceBundle(
                VersionedPostgisUtils.class.getResourceAsStream(props)
            );

            Fixture f = new Fixture();
            
            f.namespace = resource.getString("namespace");
            f.host = resource.getString("host");
            f.port = Integer.valueOf(resource.getString("port"));
            f.database = resource.getString("database");
            f.user = resource.getString("user");
            f.password = resource.getString("password");    
            f.schema = resource.getString("schema");
            
            if (f.schema == null || "".equals(f.schema.trim()))
                f.schema = "public";
            
            f.wkbEnabled = null;
            f.looseBbox = null;

            Enumeration keys = resource.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement().toString();
                if (key.equalsIgnoreCase("wkbEnabled")) {
                    f.wkbEnabled = new Boolean(resource.getString("wkbEnabled"));
                } else if (key.equalsIgnoreCase("looseBbox")) {
                    f.looseBbox = new Boolean(resource.getString("looseBbox"));
                }
            }
            
            return f;
        }
        
        public static Fixture newFixture() throws IOException {
            return newFixture("fixture.properties");
        }
        
        public static class Fixture {
            public String namespace;
            public String host;
            public String database;
            public Integer port;
            public String user;
            public String password;
            public String schema;
            public Boolean wkbEnabled;
            public Boolean looseBbox;
        }
        
        public static Map getParams(Fixture f) {
            Map params = new HashMap();
            
            params.put(VersionedPostgisDataStoreFactory.DBTYPE.key, "postgis-versioned");
            params.put(VersionedPostgisDataStoreFactory.HOST.key, f.host);
            params.put(VersionedPostgisDataStoreFactory.PORT.key, f.port);
            params.put(VersionedPostgisDataStoreFactory.DATABASE.key, f.database);
            params.put(VersionedPostgisDataStoreFactory.USER.key, f.user);
            params.put(VersionedPostgisDataStoreFactory.PASSWD.key, f.password);
            params.put(VersionedPostgisDataStoreFactory.SCHEMA.key,f.schema);
            if (f.wkbEnabled != null) {
                params.put(VersionedPostgisDataStoreFactory.WKBENABLED.key, f.wkbEnabled);
            }
            if (f.looseBbox != null) {
                params.put(VersionedPostgisDataStoreFactory.LOOSEBBOX.key, f.looseBbox);
            }

            return params;
        }
        
        public static Map getParams(String fixtureFile) throws IOException {
            Fixture f = newFixture(fixtureFile);
            return getParams(f);
        }

}
