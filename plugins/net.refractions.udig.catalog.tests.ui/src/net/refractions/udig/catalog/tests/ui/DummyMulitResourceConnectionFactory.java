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
