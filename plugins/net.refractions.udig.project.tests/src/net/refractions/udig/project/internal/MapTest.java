/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.internal;

import java.awt.Dimension;

import net.refractions.udig.project.tests.support.MapTests;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Map.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class MapTest {

    private Map map;

    @Before
    public void setUp() throws Exception {
        map=MapTests.createDefaultMap("FTName", 4, true, new Dimension(500,500)); //$NON-NLS-1$
    }

    @Test
    public void testEcoreCopy() throws Exception {
        EcoreUtil.copy(map);
        // just making sure no exceptions occur
    }
}
