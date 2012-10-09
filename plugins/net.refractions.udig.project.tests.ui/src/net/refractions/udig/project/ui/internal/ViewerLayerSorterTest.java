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
package net.refractions.udig.project.ui.internal;

import static org.junit.Assert.assertEquals;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;

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
