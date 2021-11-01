/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2021, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.catalog.ui;

import static org.junit.Assert.*;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.catalog.tests.ui.CatalogTestsUIPlugin;

public class FileConnectionFactoryTest {

    private FileConnectionFactory fileConnectionFactory = null;

    @Before
    public void setUp() throws Exception {
        fileConnectionFactory = new FileConnectionFactory();
    }

    @Test
    public void testFirstExtensionFromMultibleExtensionFileProvider() throws Exception {
        URL url = CatalogTestsUIPlugin.getDefault().getBundle().getEntry("data/");
        url = FileLocator.toFileURL(new URL(url, "datasource.test1"));
        boolean canProcess = fileConnectionFactory.canProcess(url);
        assertTrue(canProcess);
    }

    @Test
    public void testSecondExtensionFromMultibleExtensionFileProvider() throws Exception {
        URL url = CatalogTestsUIPlugin.getDefault().getBundle().getEntry("data/");
        url = FileLocator.toFileURL(new URL(url, "datasource.test2"));
        boolean canProcess = fileConnectionFactory.canProcess(url);
        assertTrue(canProcess);
    }

    @Test
    public void createConnectionURL_nullContext_returnNull() {
        assertNull(fileConnectionFactory.createConnectionURL(null));
    }

    @Test
    public void createConnectionURL_unsupportedType_returnNull() {
        assertNull(fileConnectionFactory.createConnectionURL(1L));
    }
}
