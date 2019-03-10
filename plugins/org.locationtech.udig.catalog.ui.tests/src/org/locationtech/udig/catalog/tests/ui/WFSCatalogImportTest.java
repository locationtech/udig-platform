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
package org.locationtech.udig.catalog.tests.ui;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.wfs.WFSServiceImpl;

// @Ignore("because of access restriction for WFSServiceImpl")
public class WFSCatalogImportTest extends CatalogImportTest {

	@Override
	URL getContext() throws Exception {
		return new URL(CatalogTestsUIPlugin.WFSTestCapabilitiesURL);
	}

	@Override
	void assertServiceType(IService service) {
		assertTrue(service instanceof WFSServiceImpl);
	}

}
