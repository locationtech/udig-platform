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
package org.locationtech.udig.catalog.tests.ui;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.tests.ui.workflow.DummyMonitor;
import org.locationtech.udig.catalog.ui.wizard.CatalogImport;
import org.locationtech.udig.catalog.util.CatalogTestUtils;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

public abstract class CatalogImportTest {

	CatalogImport catalogImport;

    @Before
    public void setUp() throws Exception {
        catalogImport = new CatalogImport();
        CatalogTestUtils.assumeNoConnectionException(getContext(), 1000);
    }

    @Test
	public void testNormal() throws Exception{
			URL context = getContext();
			final ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();

			List<IResolve> members = catalog.members(new DummyMonitor());
			if (!members.isEmpty()) {
				//clear the catalog
				for (Iterator<IResolve> itr = members.iterator(); itr.hasNext();) {
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
            UDIGTestUtil.inDisplayThreadWait(10000, new WaitCondition(){

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
			for (Iterator<IResolve> itr = members.iterator(); itr.hasNext();) {
				assertServiceType((IService)itr.next());
			}
	}

	abstract URL getContext() throws Exception;

	abstract void assertServiceType(IService service);
}
