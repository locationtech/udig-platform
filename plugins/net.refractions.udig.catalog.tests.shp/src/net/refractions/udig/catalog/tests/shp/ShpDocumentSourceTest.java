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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.DocumentFactory;
import net.refractions.udig.catalog.FileDocument;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.URLDocument;
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
@SuppressWarnings({"nls","restriction"})
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
        
//        File propertiesFile = ShpDocumentSource.getPropertiesFile(url);
//        if (propertiesFile.exists()) {
//            propertiesFile.deleteOnExit();
//        }
        
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
        source.clean();
        assertFalse("Properties file not clean: please delete it", source.hasDocuments());
        
        // add a Resource file
        URL bundleURL = TestActivator.getDefault().getBundle()
                .getEntry("data/resource_file.txt"); //$NON-NLS-1$
        URL internalURL = FileLocator.toFileURL(bundleURL);
        File resourceFile = DataUtilities.urlToFile(internalURL);
        DocumentFactory factory = new DocumentFactory();
        IDocument resourceDocument = factory.create(resourceFile);
        source.add(resourceDocument);
        assertTrue("Resource file document not added", source.hasDocuments());
        
        // remove the Resource file
        source.remove(resourceDocument);
        assertFalse("Resource file document not removed", source.hasDocuments());
        
        File propertiesFile = ShpDocumentSource.getPropertiesFile(url);
        assertFalse("properties file was not deleted", propertiesFile.exists());
        
        // add a resource web url
        URL webURL = new URL("http://www.google.com.au"); //$NON-NLS-1$
        resourceDocument = factory.create(webURL);
        source.add(resourceDocument);
        assertTrue("Resource url document not added", source.hasDocuments());
        
        // remove the resource web url
        source.remove(resourceDocument);
        assertFalse("Resource url document not removed", source.hasDocuments());
        
        
        // --------------
        
        // add an attribute for feature file documents
        Class<FileDocument> docType = FileDocument.class;
        source.addAttribute("Comment", docType);
        assertTrue("feature attribute for file documents not added", source.hasDocuments());
        
        // test retrieving a feature file document
        List<IDocument> findDocuments = source.findDocuments("10m_geography_regions_points.6");
        assertFalse("feature file document not retrieved", findDocuments.isEmpty());
        // check that it is a file document
        IDocument document = findDocuments.get(0);
        assertTrue("feature document is not of FileDocument type", document instanceof FileDocument);
        
        // add an attribute for feature url documents
        source.addAttribute("Name_Alt", URLDocument.class);
        List<String> attributes = source.getAttributes();
        assertEquals("feature attribute for url documents not added", 2, attributes.size());
        
        // test retrieving a feature url document
        findDocuments = source.findDocuments("10m_geography_regions_points.8");
        assertFalse("feature url document not retrieved", findDocuments.isEmpty());
        // check that it is a url document
        document = findDocuments.get(0);
        assertTrue("feature document is not of URLDocument type", document instanceof URLDocument);
        
        // remove the attribute for feature url documents
        source.removeAttribute("Name_Alt");
        assertEquals("feature attribute for url documents not removed", 1, source.getAttributes().size());
        
        // remove the attribute
        source.removeAttribute("Comment");
        assertFalse("feature attribute not removed", source.hasDocuments());
    }
    
}
