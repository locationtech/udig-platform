package net.refractions.udig.tutorials.catalog.csv;

import java.net.URL;
import java.util.List;

import junit.framework.TestCase;
import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.tutorials.catalog.csv.CSVService;
import net.refractions.udig.tutorials.catalog.csv.internal.Activator;

import org.eclipse.core.runtime.FileLocator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.csvreader.CsvReader;
import com.vividsolutions.jts.geom.Point;

public class CSVServiceTest extends TestCase {
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
        
        CSVService csvService = (CSVService) service;
        
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
        CsvReader reader = csv.reader();
        reader.readHeaders();
        reader.setCaptureRawRecord(true);
        reader.setTrimWhitespace(true);
        int count=0;
        while( reader.readRecord() ){
            String x = reader.get("x");
            String y = reader.get("y");
            System.out.print( reader.getCurrentRecord() +" point "+x+" x "+y);
            Point point = CSV.getPoint( reader );
            System.out.println( "-->"+ point );
            
            count++;
        }
        reader.close();
        System.out.println( count );
    }
}
