/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.catalog.tests.shp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.DocumentFactory;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.internal.shp.ShpDocumentSource;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;

import org.eclipse.core.runtime.FileLocator;
import org.geotools.data.DataUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author paul.pfeiffer
 *
 */
public class ShpDocumentSourceTest {

    private static URL url = null;
    private static File file;
    private static ShpServiceImpl service;
    
    @BeforeClass
    public static void testSetup() throws Exception {
        URL bundleURL = TestActivator.getDefault().getBundle()
                .getEntry("data/10m_geography_regions_points.shp"); //$NON-NLS-1$

        url = FileLocator.toFileURL(bundleURL);
        file = DataUtilities.urlToFile(url);
        
        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
        
        //create the service
        List<IService> services = factory.createService( url );
        service = (ShpServiceImpl) services.get(0);
    }
    
    @AfterClass
    public static void testCleanup() throws Exception {
        // assume we clean up any temp files here
        
        File propertiesFile = ShpDocumentSource.getPropertiesFile(url);
        if (propertiesFile.exists()) {
//            propertiesFile.deleteOnExit();
        }
        
        // clean up service
        if( service != null ){
            service.dispose(null);
        }

    }
    
    @Test
    public void test() {
        assertNotNull("Shapefile resource not found", url);
        assertNotNull("Connection", service );
    }

    @Test
    public void testShpDocumentSource() throws IOException {
        ShpDocumentSource source = null;
        source = new ShpDocumentSource(url);
        assertNotNull("Unable to create document source", source);
        assertFalse("Properties file not clean: please delete it", source.hasDocuments());
        
        // add a global file
        URL bundleURL = TestActivator.getDefault().getBundle()
                .getEntry("data/global_file.txt"); //$NON-NLS-1$
        URL internalURL = FileLocator.toFileURL(bundleURL);
        File globalFile = DataUtilities.urlToFile(internalURL);
        DocumentFactory factory = new DocumentFactory();
        IDocument globalDocument = factory.create(globalFile);
        source.add(globalDocument);
        assertTrue("Global file document not added", source.hasDocuments());
        
        // remove the global file
        source.remove(globalDocument);
        assertFalse("Global file document not removed", source.hasDocuments());
        
        File propertiesFile = ShpDocumentSource.getPropertiesFile(url);
        assertFalse("properties file was not deleted", propertiesFile.exists());
        
        // add a global web url
        URL webURL = new URL("http://www.google.com.au"); //$NON-NLS-1$
        globalDocument = factory.create(webURL);
        source.add(globalDocument);
        assertTrue("Global url document not added", source.hasDocuments());
        
        // remove the global web url
        source.remove(globalDocument);
        assertFalse("Global url document not removed", source.hasDocuments());
        
        // add an attribute for feature documents
        source.addAttribute("Comment");
        assertTrue("feature attribute not added", source.hasDocuments());
        
        // remove the attribute
//        source.removeAttribute("Comment");
//        assertFalse("feature attribute not removed", source.hasDocuments());
    }
    
}
