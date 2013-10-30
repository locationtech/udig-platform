/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveAdapterFactory;
import org.locationtech.udig.catalog.IResolveManager;
import org.locationtech.udig.catalog.tests.DummyService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

/**
 * Test the resolve manager
 * @author Jesse
 * @since 1.1.0
 */
public class ResolveManagerTest {

    IResolveManager resolveManager = CatalogPlugin.getDefault().getResolveManager();
    DummyService service=new DummyService();


    /**
     * Test method for {@link org.locationtech.udig.catalog.internal.ResolveManager#canResolve(org.locationtech.udig.catalog.IResolve, java.lang.Class)}.
     */
    @Test
    public void testCanResolve() {
        assertTrue(resolveManager.canResolve(service, ResolvedTo.class));
    }

    /**
     * Test method for {@link org.locationtech.udig.catalog.internal.ResolveManager#registerResolves(org.locationtech.udig.catalog.IResolveAdapterFactory)}.
     */
    @Test
    public void testRegisterResolves() {
        assertFalse(resolveManager.canResolve(service, Integer.class));
        IResolveAdapterFactory factory = new IResolveAdapterFactory() {

            public <T> T adapt(IResolve resolve, Class<T> adapter, IProgressMonitor monitor)
                    throws IOException {
                if( adapter == Integer.class ){
                    return adapter.cast( new Integer(1) );
                }
                return null;
            }

            public boolean canAdapt(IResolve resolve, Class adapter) {
                return adapter == Integer.class;
            }

            public String toString() {
                return "Generic ResolveAdapterFactory";
            }
        };
        resolveManager.registerResolves(factory);
        assertTrue(resolveManager.canResolve(service, Integer.class));
        resolveManager.unregisterResolves(factory);
        assertFalse(resolveManager.canResolve(service, Integer.class));
    }

    /**
     * Test method for {@link org.locationtech.udig.catalog.internal.ResolveManager#resolve(org.locationtech.udig.catalog.IResolve, java.lang.Class, IProgressMonitor)}.
     */
    @Test
    public void testResolve() throws Exception {
        assertNotNull(resolveManager.resolve(service, ResolvedTo.class, new NullProgressMonitor()));
    }

    /**
     * Test method for {@link org.locationtech.udig.catalog.internal.ResolveManager#unregisterResolves(org.locationtech.udig.catalog.IResolveAdapterFactory, java.lang.Class)}.
     */
    @Test
    public void testUnregisterResolvesIResolveAdapterFactoryClass() {
        IResolveAdapterFactory factory = new IResolveAdapterFactory(){
            
            public <T> T adapt( IResolve resolve, Class<T> adapter, IProgressMonitor monitor ) throws IOException {
                if( adapter == Integer.class ){
                    return adapter.cast( Integer.valueOf(1) );                   
                }
                else if (adapter == Float.class ){
                    return adapter.cast( Float.valueOf(2.0f));
                }
                return null; // unable to handle this one
             }

            public boolean canAdapt( IResolve resolve, Class adapter ) {
                return adapter == Integer.class || adapter == Float.class;
            }
            
            public String toString() {
                return "Generic ResolveAdapterFactory";
            }
        };

        resolveManager.registerResolves(factory);
        
        assertTrue(resolveManager.canResolve(service, Integer.class));
        assertTrue(resolveManager.canResolve(service, Float.class));
        
        resolveManager.unregisterResolves(factory, Integer.class);
        
        assertFalse(resolveManager.canResolve(service, Integer.class));
        assertTrue(resolveManager.canResolve(service, Float.class));
        
    }

}
