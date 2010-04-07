package net.refractions.udig.catalog.tests.ui;

import java.net.URL;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;

import org.eclipse.core.runtime.FileLocator;

public class ShapefileCatalogImportTest extends CatalogImportTest {

	@Override
	Object getContext() throws Exception {
		URL url = CatalogTestsUIPlugin.getDefault().getBundle()
			.getEntry("data/streams.shp"); //$NON-NLS-1$
		return FileLocator.toFileURL(url);
	}
	
	@Override
	void assertServiceType(IService service) {
		assertTrue(service instanceof ShpServiceImpl);
	}
}
