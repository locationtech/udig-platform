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
package org.locationtech.udig.catalog.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

public abstract class AbstractServiceTest extends AbstractResolveTest {
    
    protected abstract IService getResolve();

    @Test
    public void testInfo() throws IOException {
        IServiceInfo info = getInfo(getResolve(), null);
        assertNotNull("Info is required", info); //$NON-NLS-1$
    }

    @Test
    public void testInfoMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        IServiceInfo info = getInfo(getResolve(), monitor);
        assertNotNull("Info is required", info); //$NON-NLS-1$
    }

    @Test(timeout = BLOCK)
    public void testGetParams() {
        Map<String, Serializable> params = getResolve().getConnectionParams();
        assertNotNull("The params cannot be null", params); //$NON-NLS-1$
    }

    @Test(timeout = BLOCK)
    public void testCanResolve() {
        assertTrue("Must resolve List.class", getResolve().canResolve(List.class)); //$NON-NLS-1$
        assertTrue("Must resolve IServiceInfo.class", getResolve().canResolve(IServiceInfo.class)); //$NON-NLS-1$
    }

    @Test
    public void testResolve() throws IOException {
        IServiceInfo info = resolve(getResolve(), IServiceInfo.class, null);
        if (!isLeaf())
            assertTrue("May not have null info", info != null); //$NON-NLS-1$
    }

    @Test
    public void testResolveMonitor() throws IOException {
        FakeProgress monitor = new FakeProgress();
        IServiceInfo info = resolve(getResolve(), IServiceInfo.class, monitor);
        if (!isLeaf())
            assertTrue("May not have null info", info != null); //$NON-NLS-1$
    }

    protected IServiceInfo getInfo(final IService service, final IProgressMonitor monitor) throws IOException {
        final Callable<IServiceInfo> job = new Callable<IServiceInfo>() {
            
            @Override
            public IServiceInfo call() throws Exception {
                return service.getInfo(monitor);
            }
            
        };
        
        return retrieveInNewThread(job);
    }

}
