package net.refractions.udig.mapgraphic.internal.ui;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.ui.UDIGConnectionFactory;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;

public class MapGraphicConnectionFactory extends UDIGConnectionFactory {

	public boolean canProcess(Object context) {
		return false;
	}

	public Map<String, Serializable> createConnectionParameters(Object context) {
		return null;
	}

	public URL createConnectionURL(Object context) {
		return MapGraphicService.SERVICE_URL;
	}

}
