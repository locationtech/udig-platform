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
package net.refractions.udig.catalog.tests.ui;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wms.WMSServiceImpl;
import net.refractions.udig.catalog.tests.ui.workflow.Assertion;
import net.refractions.udig.catalog.tests.ui.workflow.DialogDriver;
import net.refractions.udig.catalog.tests.ui.workflow.DummyMonitor;
import net.refractions.udig.catalog.ui.ConnectionErrorPage;
import net.refractions.udig.catalog.ui.wizard.CatalogImport;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.junit.Before;
import org.junit.Test;

public class CatalogImportTest {
	
	CatalogImport catalogImport;
	
	@Before
	public void setUp() throws Exception {
		catalogImport = new CatalogImport();
	}
	
    @Test
	public void testNormal() throws Exception{
			Object context = getContext();
			
			final ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
			
			List members = catalog.members(new DummyMonitor());
			if (!members.isEmpty()) {
				//clear the catalog
				for (Iterator itr = members.iterator(); itr.hasNext();) {
					IService service = (IService)itr.next();
					catalog.remove(service);
				}
			}
			members = catalog.members(new DummyMonitor());
			assertTrue(members.isEmpty());
			
			catalogImport.getDialog().getWorkflowWizard().getWorkflow()
				.setContext(context);
			catalogImport.run(new DummyMonitor(),context);
			
            //sleep for 10 seconds, if dialog still active by then kill it
            UDIGTestUtil.inDisplayThreadWait(2000000, new WaitCondition(){

                public boolean isTrue() {
                    try {
                        return !catalog.members(new DummyMonitor()).isEmpty();
                    } catch (IOException e) {
                        return false;
                    }
                }
                
            }, true);
            
			members = catalog.members(new DummyMonitor());
			assertTrue(!members.isEmpty());
			for (Iterator itr = members.iterator(); itr.hasNext();) {
				assertServiceType((IService)itr.next());
			}
	}

    @Test
	public void testConnectionError() throws MalformedURLException {
			//create a bad context object, lets say a wfs that doesn't exist
			URL context = new URL("http://foo.blah.hehehe/geoserver/wfs"); //$NON-NLS-1$
			
			
			Assertion a1 = new Assertion() {
				@Override
				public void run() {
					fail = !(catalogImport.getDialog().getCurrentPage() instanceof ConnectionErrorPage);
				}
			};
			//sleep for 10 seconds, if dialog still active by then kill it
			DialogDriver driver = new DialogDriver(
				catalogImport.getDialog(), 
				new Object[]{
					a1, IDialogConstants.CANCEL_ID
				}
			);
			
			driver.schedule();
			catalogImport.getDialog().getWorkflowWizard().getWorkflow()
				.setContext(context);
			catalogImport.run(new DummyMonitor(),context);
			driver.cancel();
	}
	
	Object getContext() throws Exception {
		return new URL("http://demo.opengeo.org/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities"); //$NON-NLS-1$
	}
	
	void assertServiceType(IService service) {
		assertTrue(service instanceof WMSServiceImpl);
	}
}
