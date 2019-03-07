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
package org.locationtech.udig.tutorials.catalog.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.tutorials.catalog.csv.internal.Activator;

import org.eclipse.core.runtime.FileLocator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Test;
import org.osgi.framework.Bundle;

import au.com.bytecode.opencsv.CSVReader;

import com.vividsolutions.jts.geom.Point;

@SuppressWarnings("nls")
public class CSVServiceTest {
    
    @Test
    public void testCreateService() throws Exception {
        Activator instance = Activator.getDefault();
        assertNotNull("Run as a JUnit Plug-in Test", instance );
        
        Bundle bundle = instance.getBundle();
        URL url = bundle.getEntry("cities.csv");
        System.out.println("Bundle URL"+ url );
        
        URL fileUrl = FileLocator.toFileURL( url );
        System.out.println("Bundle URL"+ fileUrl );
        
        //get the service factory 
        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
        
        //create the service
        
        List<IService> services = factory.createService( fileUrl );
        
        //ensure the service was created
        assertNotNull(services);
        assertEquals(1, services.size() );
        
        //ensure the right type of service was created
        IService service = services.get(0);
        assertNotNull( service );
        
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        catalog.add( service ); // we can now find this service!
        
        List<IResolve> found = catalog.search("csv",null, null );
        assertEquals( 2, found.size() );
        
        //get all the resources from the service
        List<? extends IGeoResource> resources = service.resources(null);
        assertNotNull(resources);
        assertEquals(resources.size(),1);
        
        CSV csv = null;
        for (IGeoResource resource : resources) {
            IGeoResourceInfo info = resource.getInfo(null);
            
            String description = info.getDescription();
            assertNotNull( description );
            System.out.println("Description:"+description);
            
            ReferencedEnvelope bounds = info.getBounds();
            assertTrue( !bounds.isNull() );
            System.out.println("Bounds:"+bounds);
            
           if( resource.canResolve(CSV.class)){
               csv = resource.resolve(CSV.class, null );
           }
        }
        CSVReader reader = csv.reader();
        String row[];
        int count=0;
        int lon = csv.getHeader("x");
        int lat = csv.getHeader("y");
        while ((row = reader.readNext()) != null) {
            String x = row[lon];
            String y = row[lat];
            System.out.print( "row "+count+": point "+x+" x "+y);
            Point point = csv.getPoint( row );
            System.out.println( "-->"+ point );
            
            count++;
        }
        reader.close();
        System.out.println( count );
    }
}
