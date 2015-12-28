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

import static org.junit.Assert.assertTrue;

import java.awt.Dimension;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.core.testsupport.FeatureCreationTestUtil;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.testsupport.AbstractProjectTestCase;
import org.locationtech.udig.project.testsupport.MapTests;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * Confirms that the transaction of a feature store keeps its state correctly (UDIG-1051).
 *  
 * @author chorner
 * @since 1.1.0
 */
public class UDIGFeatureStoreTransactionTest extends AbstractProjectTestCase {

    @Ignore
    @Test
    public void testTransactionUse() throws Exception {
        IGeoResource resource1 = MapTests.createGeoResource(FeatureCreationTestUtil.createDefaultTestFeatures(
                "type1", 4), false); //$NON-NLS-1$
        Map map = MapTests.createNonDynamicMapAndRenderer(resource1, new Dimension(512,512));
        ILayer layer = map.getLayersInternal().get(0);
        EditManager manager = (EditManager) layer.getMap().getEditManager();
        //wrapper/decorator
        FeatureStore<SimpleFeatureType, SimpleFeature> store = layer.getResource(FeatureStore.class, new NullProgressMonitor());
        //we are in read mode, so the wrapper doesn't delegate to the transaction
        assertTrue(store.getTransaction() == Transaction.AUTO_COMMIT);
        //read, bounds doesn't start a transaction
        store.getBounds();
        assertTrue(store.getTransaction() == Transaction.AUTO_COMMIT);
        //start editing
        store.removeFeatures(Filter.EXCLUDE);
        assertTrue(store.getTransaction() != Transaction.AUTO_COMMIT);
        //read keeps transaction open
        store.getFeatures();
        assertTrue(store.getTransaction() != Transaction.AUTO_COMMIT);
        //rollback stops
        manager.rollbackTransaction();
        assertTrue(store.getTransaction() == Transaction.AUTO_COMMIT);
        //remove starts again
        store.removeFeatures(Filter.INCLUDE);
        assertTrue(store.getTransaction() != Transaction.AUTO_COMMIT);
        //done
        manager.commitTransaction();
        assertTrue(store.getTransaction() == Transaction.AUTO_COMMIT);
    }
    
}
