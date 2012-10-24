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
package net.refractions.udig.socket;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.core.Pair;

import org.junit.Test;

public class DisplayLayerHandlerTest {

	@Test
	public void testCreateShpParams() throws Exception {
		DisplayLayerHandler handler = new DisplayLayerHandler();
		String resourceId = "file://home/user/data/myshp.shp#myshp";
		String shpId ="file://home/user/data/myshp.shp";
		StringBuilder builder = new StringBuilder("geoResourceId;"+resourceId );
		builder.append("\nurl;java.net.URL;"+shpId );
		String message = builder.toString();
		Pair<URL, Map<String, Serializable>> results = handler.createParams(toReader(message));
		assertEquals(resourceId, results.getLeft().toExternalForm());
		assertEquals(shpId, ((URL) results.getRight().get("url")).toExternalForm());
	}

	private BufferedReader toReader(String message) {
		return new BufferedReader(new StringReader(message));
	}

}
