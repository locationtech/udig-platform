package net.refractions.udig.catalog.tests.ui.dnd;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wms.WMSServiceImpl;

public class WMSCatalogViewDNDTest extends CatalogViewDNDTest {
	
	@Override
	protected Object getData() throws Exception {
        return new URL("http://www.refractions.net:8080/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities"); //$NON-NLS-1$
	}
	
	
	@Override
	Object getDataMulti() throws Exception {
		return new URL[]{
			new URL("http://www.refractions.net:8082/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities"), //$NON-NLS-1$
            new URL("http://www.refractions.net:8080/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities") //$NON-NLS-1$
		};
	}
	
	@Override
	void makeAssertion(String assertionDescription, ICatalog catalog) throws Exception {
		super.makeAssertion(assertionDescription, catalog);
		
		IService service;
		try {
			service = (IService) catalog.members(null).get(0);
			assertTrue("All services must be WMSServices", service instanceof WMSServiceImpl); //$NON-NLS-1$
		} 
		catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Override
	void makeAssertionMulti(String assertionDescription, ICatalog catalog) {
		super.makeAssertionMulti(assertionDescription, catalog);
		
		try {
			for (Iterator itr = catalog.members(null).iterator(); itr.hasNext();) {
				IService s = (IService)itr.next();
				assertTrue("All services must be WMSServices", s instanceof WMSServiceImpl); //$NON-NLS-1$
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}
