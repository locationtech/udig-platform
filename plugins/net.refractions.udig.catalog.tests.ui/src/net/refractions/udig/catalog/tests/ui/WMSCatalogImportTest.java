package net.refractions.udig.catalog.tests.ui;

import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wms.WMSServiceImpl;

public class WMSCatalogImportTest extends CatalogImportTest {

	@Override
	Object getContext() throws MalformedURLException {
		return new URL("http://www.refractions.net:8080/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities"); //$NON-NLS-1$
	}
	
	@Override
	void assertServiceType(IService service) {
		assertTrue(service instanceof WMSServiceImpl);
	}
}

