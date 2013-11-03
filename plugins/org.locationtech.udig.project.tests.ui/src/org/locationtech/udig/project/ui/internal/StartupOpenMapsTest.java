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
package org.locationtech.udig.project.ui.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.net.URL;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.tests.ui.CatalogTestsUIPlugin;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.widgets.Display;
import org.junit.Ignore;
import org.junit.Test;

public class StartupOpenMapsTest extends AbstractProjectUITestCase {

    /*
     * Test method for 'org.locationtech.udig.project.ui.internal.StartupOpenMaps.earlyStartup()'
     */
    @Ignore
    @Test
    public void testEarlyStartup() throws Exception{
        assertNotSame("There should be no maps open", ApplicationGIS.NO_MAP, ApplicationGIS.getActiveMap()); //$NON-NLS-1$
        StartupOpenMaps startup=new StartupOpenMaps();
        URL url = CatalogTestsUIPlugin.getDefault().getBundle()
        .getEntry("data/streams.shp");   //$NON-NLS-1$
    
        startup.testingSetArgs(new String[]{FileLocator.toFileURL(url).toString()});
        startup.earlyStartup();
        while( Display.getCurrent().readAndDispatch() );
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

			public boolean isTrue()  {
				return ApplicationGIS.getActiveMap()!=ApplicationGIS.NO_MAP;
			}
        	
        }, true);
        assertNotNull(ApplicationGIS.getActiveMap());
        
    }

}
