/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.catalog.tests.ui.dnd;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wms.WMSServiceImpl;

public class WMSCatalogViewDNDTest extends CatalogViewDNDTest {
	
	@Override
	protected Object getData() throws Exception {
        return new URL("http://demo.opengeo.org/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities"); //$NON-NLS-1$
	}
	
	
	@Override
	Object getDataMulti() throws Exception {
		return new URL[]{
			new URL("http://demo.opengeo.org/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities"), //$NON-NLS-1$
            new URL("http://atlas.gc.ca/cgi-bin/atlaswms_en?VERSION=1.1.1&Request=GetCapabilities&Service=WMS") //$NON-NLS-1$
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
			for (Iterator<IResolve> itr = catalog.members(null).iterator(); itr.hasNext();) {
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
