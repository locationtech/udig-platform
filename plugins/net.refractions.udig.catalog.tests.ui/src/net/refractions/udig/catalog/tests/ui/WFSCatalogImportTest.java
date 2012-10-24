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
package net.refractions.udig.catalog.tests.ui;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wfs.WFSServiceImpl;

public class WFSCatalogImportTest extends CatalogImportTest {

	@Override
	Object getContext() throws Exception {
		return new URL("http://www2.dmsolutions.ca/cgi-bin/mswfs_gmap?version=1.0.0&request=getcapabilities&service=wfs"); //$NON-NLS-1$
	}
	
	@Override
	void assertServiceType(IService service) {
		assertTrue(service instanceof WFSServiceImpl);
	}
	
}
