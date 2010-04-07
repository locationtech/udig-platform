package net.refractions.udig.project.ui.internal;

import java.net.URL;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.tests.ui.CatalogTestsUIPlugin;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.widgets.Display;

public class StartupOpenMapsTest extends AbstractProjectUITestCase {

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.StartupOpenMaps.earlyStartup()'
     */
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
