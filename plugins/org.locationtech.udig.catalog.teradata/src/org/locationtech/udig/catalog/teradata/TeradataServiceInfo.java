/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */

package org.locationtech.udig.catalog.teradata;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.catalog.IServiceInfo;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The service info for the TeradataService
 * 
 * @author jesse
 * @since 1.1.0
 */
public class TeradataServiceInfo extends IServiceInfo {

	private TeradataService service;

	public TeradataServiceInfo(TeradataService service) throws IOException {
		this.service = service;
		List<String> tmpKeywords = new ArrayList<String>();
		tmpKeywords.add("Teradata"); //$NON-NLS-1$

		List<TeradataGeoResource> resources = service
				.resources(new NullProgressMonitor());
		for (TeradataGeoResource teradataGeoResource2 : resources) {
			tmpKeywords.add(teradataGeoResource2.typename);
		}
		keywords = tmpKeywords.toArray(new String[0]);

		try {
			schema = new URI("jdbc://teradata/gml"); //$NON-NLS-1$
		} catch (URISyntaxException e) {
			Activator.log(null, e);
		}

		icon = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				"icons/obj16/teradata_16.gif"); //$NON-NLS-1$
	}

	public String getDescription() {
		return service.getIdentifier().toString();
	}

	public URI getSource() {
		try {
			return service.getIdentifier().toURI();
		} catch (URISyntaxException e) {
			// This would be bad
			throw (RuntimeException) new RuntimeException().initCause(e);
		}
	}

	public String getTitle() {
		return service.getTitle();
	}

}
