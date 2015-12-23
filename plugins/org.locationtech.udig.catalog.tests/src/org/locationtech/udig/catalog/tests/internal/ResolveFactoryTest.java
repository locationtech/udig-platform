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
package org.locationtech.udig.catalog.tests.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.locationtech.udig.catalog.testsupport.DummyService;
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
