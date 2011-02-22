package net.refractions.udig.project.internal.impl;

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
import org.geotools.filter.Filter;

/**
 * Confirms that the transaction of a feature store keeps its state correctly (UDIG-1051).
 *
 * @author chorner
 * @since 1.1.0
 */
public class UDIGFeatureStoreTransactionTest extends AbstractProjectTestCase {

    public void testTransactionUse() throws Exception {
        IGeoResource resource1 = MapTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures(
                "type1", 4), false); //$NON-NLS-1$
        Map map = MapTests.createNonDynamicMapAndRenderer(resource1, new Dimension(512,512));
        ILayer layer = map.getLayersInternal().get(0);
        EditManager manager = (EditManager) layer.getMap().getEditManager();
        //wrapper/decorator
        FeatureStore store = layer.getResource(FeatureStore.class, new NullProgressMonitor());
        //we are in read mode, so the wrapper doesn't delegate to the transaction
        assertTrue(store.getTransaction() == Transaction.AUTO_COMMIT);
        //read, bounds doesn't start a transaction
        store.getBounds();
        assertTrue(store.getTransaction() == Transaction.AUTO_COMMIT);
        //start editing
        store.removeFeatures(Filter.ALL);
        assertTrue(store.getTransaction() != Transaction.AUTO_COMMIT);
        //read keeps transaction open
        store.getFeatures();
        assertTrue(store.getTransaction() != Transaction.AUTO_COMMIT);
        //rollback stops
        manager.rollbackTransaction();
        assertTrue(store.getTransaction() == Transaction.AUTO_COMMIT);
        //remove starts again
        store.removeFeatures(Filter.NONE);
        assertTrue(store.getTransaction() != Transaction.AUTO_COMMIT);
        //done
        manager.commitTransaction();
        assertTrue(store.getTransaction() == Transaction.AUTO_COMMIT);
    }

}
