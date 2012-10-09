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
package net.refractions.udig.catalog.tests.ui;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.tests.DummyMultiResourceService;
import net.refractions.udig.catalog.tests.DummyMultiResourceServiceExtension;

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
