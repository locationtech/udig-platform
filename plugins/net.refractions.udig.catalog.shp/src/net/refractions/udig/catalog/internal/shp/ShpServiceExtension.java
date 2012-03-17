/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.catalog.internal.shp;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.AbstractDataStoreServiceExtension;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.shp.internal.Messages;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;

/**
 * Service Extension implementation for Shapefiles.
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class ShpServiceExtension extends AbstractDataStoreServiceExtension implements ServiceExtension {

    // this is for backwards compatibility with 1.1.x.  The parameter key was 
    // changed in geotools since 2.2
    private static final String OLD_URLP_KEY = "shapefile url"; //$NON-NLS-1$
    private static ShapefileDataStoreFactory shpDSFactory;

    public static ShapefileDataStoreFactory getSHPDSFactory(){
        if(shpDSFactory == null)
            shpDSFactory = new ShapefileDataStoreFactory();
        return shpDSFactory;
    }
    
    public IService createService( URL id, Map<String,Serializable> params ) {
        if( params.containsKey(OLD_URLP_KEY)){
            params.put(ShapefileDataStoreFactory.URLP.key, params.get(OLD_URLP_KEY));
            params.remove(OLD_URLP_KEY);
        }
        if(params.containsKey(ShapefileDataStoreFactory.URLP.key) ){
            // shapefile ...

            URL url = null;
            if(params.get(ShapefileDataStoreFactory.URLP.key) == null) {
                return null;
            } else if (params.get(ShapefileDataStoreFactory.URLP.key) instanceof URL){
            
                url = (URL)params.get(ShapefileDataStoreFactory.URLP.key);
            }else{
                try {
                    url = (URL)ShapefileDataStoreFactory.URLP.parse(params.get(ShapefileDataStoreFactory.URLP.key).toString());
                    params.put(ShapefileDataStoreFactory.URLP.key,url);
                } catch (Throwable e1) {
                    // log this?
                    e1.printStackTrace();
                    return null;
                }
            }
            String file=url.getFile();
            file=file.toLowerCase();
            if (!(file.endsWith(".shp") || file.endsWith(".shx") || file.endsWith(".qix") || file
                    .endsWith(".dbf"))) {
                return null;
            }
            
            if( getSHPDSFactory().canProcess(params)){
                if (id == null) {
                    return new ShpServiceImpl(url,params);
                }
                else {
                    return new ShpServiceImpl(id,params);
                }
            }
        }
        return null;
    }

    public Map<String,Serializable> createParams( URL url ) {
        if ( !isSupportedExtension(url) ){
           return null;
        }
        URL cleanedShapeURL = toShpURL( url );
        
        if( cleanedShapeURL==null ){
            return null; // file did not exist or was not valid
        }
        
        if(getSHPDSFactory().canProcess(cleanedShapeURL)){
            // shape file
            File file = URLUtils.urlToFile( cleanedShapeURL );
            if( !file.exists() ){
                return null; // file does not exist?
            }
            HashMap<String,Serializable> params = new HashMap<String,Serializable>();
            params.put(ShapefileDataStoreFactory.URLP.key,cleanedShapeURL); 
            return params;
        }
        return null;
    }

    private boolean isSupportedExtension( URL url ) {
        String file=url.getFile();
        file=file.toLowerCase();

        return (file.endsWith(".shp") || file.endsWith(".shx") ||file.endsWith(".qix")  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                || file.endsWith(".dbf")); //$NON-NLS-1$
    }
    
    /**
     * Has a go at cleaning the provided URL and ensuring it is a file
     */
    private URL toShpURL( URL url) {
        File file = URLUtils.urlToFile( url );
        URL shpURL = URLUtils.fileToURL(file);
        if( shpURL != null ){
            return shpURL; // clean!
        }
        // previous approach tries to duplicate the functionality of URLUtils above; using it as a fallback
        String authority = url.getAuthority();
        String path = url.getPath();
        
        if (authority != null && authority.length() != 0) {
            path = "//"+authority+path; //$NON-NLS-1$
        }
        if( !path.toLowerCase().endsWith(".shp") ){ //$NON-NLS-1$
            path = path.substring(0, path.lastIndexOf('.') )+".shp"; //$NON-NLS-1$
        }
        file = new File(path);
        shpURL = URLUtils.fileToURL(file);
        return shpURL; // clean!
    }

    @Override
    protected String doOtherChecks( Map<String, Serializable> params ) {
        ShapefileDataStoreFactory factory = getSHPDSFactory();
        if( !factory.canProcess(params) ) {
            // this is tough we don't have a good error message out of the geotools factory canProcess method
            // So we will try (and fail!) to connect ...
            DataStore datastore = null;
            try {
                datastore = factory.createDataStore(params);
            }
            catch (Throwable t){
                return t.getLocalizedMessage(); // We cannot connect because of this ...
            }
            finally {
                if( datastore != null){
                    datastore.dispose();
                }
            }
        }
        return null; // apparently we can connect
    }

    @Override
    protected DataStoreFactorySpi getDataStoreFactory() {
        return getSHPDSFactory();
    }

    public String reasonForFailure( URL url ) {
        if (!isSupportedExtension(url) )
            return Messages.ShpServiceExtension_badExtension;
        if ( toShpURL(url) == null )
            return Messages.ShpServiceExtension_cantCreateURL;
        return reasonForFailure(createParams(url));
    }

}
