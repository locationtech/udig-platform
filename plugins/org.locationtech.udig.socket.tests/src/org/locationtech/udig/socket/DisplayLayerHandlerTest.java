package org.locationtech.udig.socket;
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


import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

import org.junit.Test;
import org.locationtech.udig.core.Pair;

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
