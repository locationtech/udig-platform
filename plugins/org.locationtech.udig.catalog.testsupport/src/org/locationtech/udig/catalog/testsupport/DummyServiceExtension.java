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
package org.locationtech.udig.catalog.testsupport;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceExtension;

public class DummyServiceExtension implements ServiceExtension {

	public IService createService(URL id, Map<String, Serializable> params) {
		if (params.containsKey("dummy")) { //$NON-NLS-1$
			return new DummyService(params);
		}
		
		return null;
	}

	public Map<String, Serializable> createParams(URL url) {
		if (url.toExternalForm().startsWith(DummyService.url.toExternalForm())) {
			HashMap<String, Serializable> map = new  HashMap<String, Serializable>();
			map.put("dummy", url); //$NON-NLS-1$
			return map;
		}
		
		return null;
	}
	
}
