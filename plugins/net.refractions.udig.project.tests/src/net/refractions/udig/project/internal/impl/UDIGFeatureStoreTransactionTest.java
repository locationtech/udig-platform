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
package net.refractions.udig.project.internal.impl;

import static org.junit.Assert.assertTrue;

import java.awt.Dimension;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.junit.Ignore;
import org.junit.Test;
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
        IGeoResource resource1 = MapTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures(
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
