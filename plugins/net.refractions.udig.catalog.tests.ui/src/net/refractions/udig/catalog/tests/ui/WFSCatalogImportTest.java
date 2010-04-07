package net.refractions.udig.catalog.tests.ui;

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
