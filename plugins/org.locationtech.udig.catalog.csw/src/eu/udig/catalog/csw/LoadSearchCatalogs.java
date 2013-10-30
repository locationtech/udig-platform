/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
