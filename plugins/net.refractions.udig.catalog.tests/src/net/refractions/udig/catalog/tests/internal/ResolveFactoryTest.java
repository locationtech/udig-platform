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
package net.refractions.udig.catalog.tests.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.refractions.udig.catalog.tests.DummyService;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

public class ResolveFactoryTest {

    @Test
    public void testResolve() throws Exception {
        DummyService service=new DummyService();
        assertTrue(service.canResolve(ResolvedTo.class));
        assertNotNull(service.resolve(ResolvedTo.class, new NullProgressMonitor()));
    }
}
