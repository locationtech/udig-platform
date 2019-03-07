package org.locationtech.udig.catalog.ui;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
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

}
