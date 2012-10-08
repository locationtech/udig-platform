/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
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
package eu.udig.catalog.csw;

import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.CatalogPlugin;

import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.prefs.PreferencesService;

public class LoadSearchCatalogs implements IStartup {

	@Override
	public void earlyStartup() {
		BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
		ServiceReference<PreferencesService> preferenceServiceReference = bundleContext.getServiceReference(PreferencesService.class);
		String cswCatalogs = (String) preferenceServiceReference.getProperty(PreferenceConstants.CSW_CATALOGS);
		if(cswCatalogs != null) {
			process(cswCatalogs);
		}
	}

	private void process(String cswCatalogs) {
		String[] catalogs = cswCatalogs.split(";");
		for (String string : catalogs) {
			try {
				URL url = new URL(string);
				CatalogPlugin.getDefault().addSearchCatalog(new CswCatalog(url));
			} catch (MalformedURLException e) {
				Activator.log("Error creating CSW catalog from URI: "+string,null);
			}
		}
	}

}
