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
package org.locationtech.udig.catalog.tests.ui.dnd;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.wms.WMSServiceImpl;
import org.locationtech.udig.catalog.tests.ui.CatalogTestsUIPlugin;

public class WMSCatalogViewDNDTest extends CatalogViewDNDTest {
	
	@Override
	protected URL getData() throws Exception {
        return new URL(CatalogTestsUIPlugin.WMSTestCapabilitiesURL);
	}
	
	
	@Override
	URL[] getDataMulti() throws Exception {
            return new URL[] { new URL(CatalogTestsUIPlugin.WMSTestCapabilitiesURL), new URL(
                    "https://demo.boundlessgeo.com/geoserver/ows?service=wms&version=1.1.1&request=GetCapabilities") //$NON-NLS-1$
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
