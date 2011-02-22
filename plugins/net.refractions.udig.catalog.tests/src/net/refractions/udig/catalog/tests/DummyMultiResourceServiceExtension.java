package net.refractions.udig.catalog.tests;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;

public class DummyMultiResourceServiceExtension implements ServiceExtension {

    public static final String ID = "multidummy"; //$NON-NLS-1$

    public DummyMultiResourceServiceExtension() {
        super();
    }

    public IService createService(URL id, Map<String, Serializable> params) {
        if (params.containsKey(ID)) {
            return new DummyMultiResourceService(params);
        }

        return null;
    }

    public Map<String, Serializable> createParams(URL url) {
        if (url.toExternalForm().startsWith(DummyMultiResourceService.url.toExternalForm())) {
            HashMap<String, Serializable> map = new  HashMap<String, Serializable>();
            map.put(ID, url);
            return map;
        }

        return null;
    }

}
