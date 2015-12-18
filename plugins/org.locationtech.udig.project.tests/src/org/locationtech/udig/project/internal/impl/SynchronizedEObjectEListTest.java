/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2015, Refractions Research Inc. and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.impl;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.emf.ecore.InternalEObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPackage;

/**
 * @author Frank Gasdorf
 * @author Erdal Karaca
 * 
 * @since 2.0.0
 */
public class SynchronizedEObjectEListTest {
    
    Map mapInstance;
    
    @Before
    public void before() {
     // set up a dummy map to add layers to
        mapInstance = ProjectFactory.eINSTANCE.createMap();
    }

    @Test
    public void testConcurrentIterationResolveingEList() throws Exception {
        final SynchronizedEObjectResolvingEList<Layer> list = new SynchronizedEObjectResolvingEList<Layer>(
                Layer.class, (InternalEObject) mapInstance.getContextModel(),
                ProjectPackage.CONTEXT_MODEL__LAYERS);

        assertSafeIterationConcurrentAccess(list);
    }

    @Test
    public void testConcurrentIterationWithinInverseResolveingEList() throws Exception {
        final SynchronizedEObjectWithInverseResolvingEList<Layer> list = new SynchronizedEObjectWithInverseResolvingEList<Layer>(
                Layer.class, (InternalEObject) mapInstance.getContextModel(),
                ProjectPackage.CONTEXT_MODEL__LAYERS,
                ProjectPackage.LAYER__CONTEXT_MODEL);
        assertSafeIterationConcurrentAccess(list);
    }
    
    private <T extends Collection<Layer> & ISynchronizedEListIteration<Layer>> void assertSafeIterationConcurrentAccess(
            final T list)
            throws InterruptedException {
        
        // add initial layers
        list.add(ProjectFactory.eINSTANCE.createLayer());
        list.add(ProjectFactory.eINSTANCE.createLayer());
        list.add(ProjectFactory.eINSTANCE.createLayer());

        final CountDownLatch iteratorLatch = new CountDownLatch(1);
        final CountDownLatch mainLatch = new CountDownLatch(1);
        final AtomicBoolean succeeded = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(1);
        // start iterator in new thread
        pool.execute(new Runnable() {
    
            @Override
            public void run() {
    
                list.syncedIteration(new IEListVisitor<Layer>() {
                    private int count = 0;
    
                    public void visit(final Layer t) {
                        // if iterator is consumed the first time, wait for the
                        // other thread to modify the list
                        if (count == 0) {
                            mainLatch.countDown();
    
                            try {
                                iteratorLatch.await(1000, TimeUnit.MILLISECONDS);
                            } catch (final InterruptedException e) {
                                Assert.fail(e.getMessage());
                            }
                        } else {
                            // if iterator is consumed more than once than
                            // assume there will be no errors
                            succeeded.set(true);
                        }
    
                        count++;
                    }
                });
            }
        });
    
        // wait for other thread to start iterator
        boolean await = mainLatch.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(await);
    
        // modify the list while the iterator in the other thread is still open
        list.add(ProjectFactory.eINSTANCE.createLayer());
        iteratorLatch.countDown();
        pool.shutdown();
        pool.awaitTermination(1000, TimeUnit.MILLISECONDS);
    
        // iteration should have been finished without any errors
        Assert.assertTrue("Iteration did not succeed as expected", succeeded.get());
    }
}
