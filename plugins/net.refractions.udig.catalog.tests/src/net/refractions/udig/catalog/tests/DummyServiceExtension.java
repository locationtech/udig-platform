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
package net.refractions.udig.catalog.tests;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;

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