/**
 * 
 */
package org.locationtech.udig.catalog.internal.shp;

import java.net.URI;
import java.net.URISyntaxException;

import org.locationtech.udig.catalog.IServiceInfo;

class ShpServiceInfo extends IServiceInfo {

	/**
	 * 
	 */
	private final ShpServiceImpl service;

	ShpServiceInfo(ShpServiceImpl shpServiceImpl) {
		super();
		service = shpServiceImpl;
		keywords = new String[] { ".shp", "Shapefile", //$NON-NLS-1$ //$NON-NLS-2$
				service.ds.getTypeNames()[0] };

		try {
			schema = new URI("shp://www.opengis.net/gml"); //$NON-NLS-1$
		} catch (URISyntaxException e) {
			ShpPlugin.log(null, e);
			schema = null;
		}
		title = service.getID().toString();
		title = title.replace("%20"," ");        		  //$NON-NLS-1$//$NON-NLS-2$
	}

	public String getDescription() {
		return service.getIdentifier().toString();
	}

	public String getTitle() {
	    return title;
	}
}
