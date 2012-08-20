/*
 *    Parkinfo
 *    http://qpws/parkinfo
 *
 *    (C) 2011, Department of Environment Resource Management
 *
 *    This code is provided for department use.
 */
package net.refractions.udig.catalog.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.geotools.data.DataStoreService;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStore;
import org.geotools.data.shapefile.ng.ShapefileDataStore;
import org.junit.Ignore;
import org.junit.Test;

/**
 * TODO Purpose of 
 * <p>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * @author leviputna
 * @since 0.0.4
 */
public class ShapeCatalogTest {
    
    @Test
    public void testCreateShapeNG() throws IOException {
        File file = new File("data/point.shp");
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        map.put( "url", file.toURL() );
        map.put( "fstype", "shape-ng" );
        DataStore dataStore = DataStoreFinder.getDataStore(map);
        
        boolean test = false;
        if(dataStore instanceof ShapefileDataStore)
            test  = true;
        
        assertTrue( "Check is ShapefileDataStore", test );
        assertEquals(dataStore.getClass().getPackage(), ShapefileDataStore.class.getPackage());
    }
    
    @Test
    public void testCreateShape() throws IOException {
        File file = new File("data/point.shp");
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        map.put( "url", file.toURL() );
        map.put( "fstype", "shape" );
        DataStore dataStore = DataStoreFinder.getDataStore(map);
        
        String packageName = dataStore.getClass().getPackage().getName();
        System.out.println(packageName);
        
        boolean test = false;
        if(dataStore instanceof IndexedShapefileDataStore)
            test  = true;
        
        assertTrue( "Check is IndexedShapefileDataStore", test );
        assertEquals(dataStore.getClass().getPackage(), IndexedShapefileDataStore.class.getPackage());
    }
    
    @Test
    public void testCatalogPluginShape() throws IOException {
        File file = new File("data/point.shp");
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        map.put( "url", file.toURL() );
        map.put( "fstype", "shape" );
        
        IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        
        //List<IService> test = serviceFactory.acquire(map);
        List<IService> test = serviceFactory.createService(map);
        boolean found = false;
        for ( IService victim : test ){
            if( victim instanceof ShpServiceImpl){
                found = true;
                break;
            }
        }
        assertTrue( "ShpServiceImpl", found );
    }
    
    @Ignore
    @Test
    public void testCatalogPluginShapeNG() throws IOException {
        File file = new File("data/point.shp");
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        map.put( "url", file.toURL() );
        map.put( "fstype", "shape-ng" );
        
        IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        
        //List<IService> test = serviceFactory.acquire(map);
        List<IService> test = serviceFactory.createService(map);
        
        boolean found = false;
        for ( IService victim : test ){
            if( victim instanceof ShpServiceImpl){
                found = true;
                break;
            }
        }
        assertTrue( "ShpServiceImpl", !found );
        
        found = false;
        for ( IService victim : test ){
            if( victim instanceof DataStoreService){
                found = true;
                break;
            }
        }
        assertTrue( "DataStoreService", found );
    }
    

}
