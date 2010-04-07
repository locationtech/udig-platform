/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
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
