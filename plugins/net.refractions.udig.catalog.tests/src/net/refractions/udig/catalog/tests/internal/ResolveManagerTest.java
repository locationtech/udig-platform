/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.tests.internal;

import java.io.IOException;

import junit.framework.TestCase;
import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.IResolveManager;
import net.refractions.udig.catalog.tests.DummyService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Test the resolve manager
 * @author Jesse
 * @since 1.1.0
 */
public class ResolveManagerTest extends TestCase {

    IResolveManager resolveManager = CatalogPlugin.getDefault().getResolveManager();
    DummyService service=new DummyService();

    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for {@link net.refractions.udig.catalog.internal.ResolveManager#canResolve(net.refractions.udig.catalog.IResolve, java.lang.Class)}.
     */
    public void testCanResolve() {
        assertTrue(resolveManager.canResolve(service, ResolvedTo.class));
    }

    /**
     * Test method for {@link net.refractions.udig.catalog.internal.ResolveManager#registerResolves(net.refractions.udig.catalog.IResolveAdapterFactory)}.
     */
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
     * Test method for {@link net.refractions.udig.catalog.internal.ResolveManager#resolve(net.refractions.udig.catalog.IResolve, java.lang.Class, IProgressMonitor)}.
     */
    public void testResolve() throws Exception {
        assertNotNull(resolveManager.resolve(service, ResolvedTo.class, new NullProgressMonitor()));
    }

    /**
     * Test method for {@link net.refractions.udig.catalog.internal.ResolveManager#unregisterResolves(net.refractions.udig.catalog.IResolveAdapterFactory, java.lang.Class)}.
     */
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
