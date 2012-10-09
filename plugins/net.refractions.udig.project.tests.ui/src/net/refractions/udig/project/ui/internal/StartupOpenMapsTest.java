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
package net.refractions.udig.project.ui.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.net.URL;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.tests.ui.CatalogTestsUIPlugin;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.widgets.Display;
import org.junit.Ignore;
import org.junit.Test;

public class StartupOpenMapsTest extends AbstractProjectUITestCase {

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.StartupOpenMaps.earlyStartup()'
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
