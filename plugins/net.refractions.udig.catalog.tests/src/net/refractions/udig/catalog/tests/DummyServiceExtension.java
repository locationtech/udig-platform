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