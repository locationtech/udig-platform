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
package org.locationtech.udig.project.ui.internal;

import static org.junit.Assert.assertEquals;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.MapTests;

import org.junit.Test;

public class ViewerLayerSorterTest {

    @Test
    public void testCompareViewerObjectObject() throws Throwable {
        Map map = MapTests.createDefaultMap("typename", 2, true, null); //$NON-NLS-1$
        map.getLayersInternal().add(map.getLayerFactory().createLayer(MapTests.createGeoResource("type2", 3, false))); //$NON-NLS-1$
        ViewerLayerSorter sorter=new ViewerLayerSorter();
        assertEquals( -1, sorter.compare(null, map.getLayersInternal().get(1), map.getLayersInternal().get(0)));
        assertEquals( 0, sorter.compare(null, map.getLayersInternal().get(1), map.getLayersInternal().get(1)));
        assertEquals( 1, sorter.compare(null, map.getLayersInternal().get(0), map.getLayersInternal().get(1)));
    }

}
