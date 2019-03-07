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

import org.locationtech.udig.catalog.tests.DummyMultiResourceService;
import org.locationtech.udig.catalog.tests.DummyMultiResourceServiceExtension;

public class DummyMulitResourceConnectionFactory extends DummyConnectionFactory {

    public DummyMulitResourceConnectionFactory() {
        super();
    }

    @Override
    public Map<String, Serializable> createConnectionParameters( Object context ) {
        if (context instanceof URL) {
            URL url = (URL)context;
            if (url.toExternalForm().startsWith(DummyMultiResourceService.url.toExternalForm())) {
                HashMap<String, Serializable> map = new HashMap<String, Serializable>();
                map.put(DummyMultiResourceServiceExtension.ID, url);
                return map;
            }   
        }
        
        return null;
    }

}
