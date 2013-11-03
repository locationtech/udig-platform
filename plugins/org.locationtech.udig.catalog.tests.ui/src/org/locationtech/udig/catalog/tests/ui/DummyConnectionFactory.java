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

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.tests.DummyService;
import org.locationtech.udig.catalog.ui.UDIGConnectionFactory;
import org.locationtech.udig.catalog.ui.UDIGConnectionPage;

public class DummyConnectionFactory extends UDIGConnectionFactory {

	public boolean canProcess(Object context) {
		return createConnectionParameters(context) != null;
	}

	public Map<String, Serializable> createConnectionParameters(Object context) {
		if (context instanceof URL) {
			URL url = (URL)context;
			if (url.toExternalForm().startsWith(DummyService.url.toExternalForm())) {
				HashMap<String, Serializable> map = new HashMap<String, Serializable>();
				map.put("dummy", url); //$NON-NLS-1$
				return map;
			}	
		}
		
		return null;
	}

	public URL createConnectionURL(Object context) {
		Map<String,Serializable> params = createConnectionParameters(context);
		if (params != null) {
			return (URL) params.get("dummy"); //$NON-NLS-1$
		}
		
		return null;
	}
    
    @Override
    public UDIGConnectionPage createConnectionPage(int i) {
        return new DummyConnectionPage();
    }
}
