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
package org.locationtech.udig.project.internal.impl;

import static org.junit.Assert.assertNotNull;

import java.awt.Dimension;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.AbstractProjectTestCase;
import org.locationtech.udig.project.tests.support.MapTests;

import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class LayerResourceTest extends AbstractProjectTestCase {
   
    private Map map;
    private Layer layer;
    private IGeoResource resource;

    @Before
    public void setUp() throws Exception {
        resource=MapTests.createGeoResource("test", 3, true); //$NON-NLS-1$
        map = MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(1024, 800));
        layer=map.getLayersInternal().get(0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFrameworkTransactionProtection() throws Exception{
        FeatureSource<SimpleFeatureType, SimpleFeature> source =layer.getResource(FeatureSource.class, null);
        assertNotNull(source);
        FeatureStore<SimpleFeatureType, SimpleFeature> store=(FeatureStore<SimpleFeatureType, SimpleFeature>) source;
        store.setTransaction(Transaction.AUTO_COMMIT);
    }
}
