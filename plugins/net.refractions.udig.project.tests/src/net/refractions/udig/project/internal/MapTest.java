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
package net.refractions.udig.project.internal;

import java.awt.Dimension;

import junit.framework.TestCase;
import net.refractions.udig.project.tests.support.MapTests;

import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Test Map.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class MapTest extends TestCase {

    private Map map;


    protected void setUp() throws Exception {
        super.setUp();
        map=MapTests.createDefaultMap("FTName", 4, true, new Dimension(500,500)); //$NON-NLS-1$
    }

    
    public void testEcoreCopy() throws Exception {
        EcoreUtil.copy(map);
        // just making sure no exceptions occur
    }
}
