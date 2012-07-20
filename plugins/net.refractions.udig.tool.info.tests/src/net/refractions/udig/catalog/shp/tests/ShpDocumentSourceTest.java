/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.shp.tests;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.shapefile.ShapefileDataStoreFactory;

import net.refractions.udig.catalog.DocumentFactory;
import net.refractions.udig.catalog.FileDocument;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IDocumentFolder;
import net.refractions.udig.catalog.URLDocument;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.catalog.internal.shp.ShpPlugin;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;
import net.refractions.udig.catalog.shp.ShpDocPropertyParser;
import net.refractions.udig.catalog.shp.ShpDocumentSource;

import junit.framework.TestCase;

/**
 * Test class for {@link ShpDocumentSource}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class ShpDocumentSourceTest extends TestCase {

    private ShpDocumentSource source;
    
    private File file;
    private URL url;
    
    private File file1;
    private File file2;
    
    private URL url1;
    private URL url2;
    
    private static final String DIRECTORY = "internal\\";
    private static final String SHAPEFILE = "countries.shp";
    private static final String FILE1 = "attachment1.txt";
    private static final String FILE2 = "attachment2.txt";
    private static final String WEB1 = "http://www.google.com";
    private static final String WEB2 = "http://www.yahoo.com";
    
    @Override
    protected void setUp() throws Exception {
        
        file = new File(DIRECTORY + SHAPEFILE);
        url = file.toURI().toURL();
        
        file1 = new File(DIRECTORY + FILE1);
        file2 = new File(DIRECTORY + FILE2);
        
        url1 = new URL(WEB1);
        url2 = new URL(WEB2);
        
        final List<IDocument> docs = new ArrayList<IDocument>();
        docs.add(new FileDocument(file1));
        docs.add(new URLDocument(url1));
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        parser.setShapeAttachments(docs);
        
        final Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, false);
        params.put(ShapefileDataStoreFactory.DBFCHARSET.key, "P_DEFAULT_CHARSET");
        
        final ShpServiceImpl service = new ShpServiceImpl(url, params);
        final ShpGeoResourceImpl geoResource = new ShpGeoResourceImpl(service, "");
        source = new ShpDocumentSource(geoResource);
        
    }
    
    public void testGetDocuments() {
        
        assertEquals("Count is not expected.", 2, source.getDocuments().size());
        
        final IDocumentFolder folder = source.getDocumentsInFolder();
        assertEquals("Count is not expected.", 2, folder.getDocuments().size());
        
    }
    
    public void testAddRemoveFile() throws MalformedURLException {
        
        assertEquals("Count is not expected.", 2, source.getDocuments().size());
        
        source.addFile(file2);
        assertEquals("Count is not expected.", 3, source.getDocuments().size());

        final List<IDocument> docs = new ArrayList<IDocument>();
        docs.add(new FileDocument(file1));
        docs.add(new FileDocument(file2));
        source.remove(docs);
        assertEquals("Count is not expected.", 1, source.getDocuments().size());
        
        source.addFile(file1);
        assertEquals("Count is not expected.", 2, source.getDocuments().size());
        
        final IDocument doc = source.addFile(file1);
        assertNull("Doc is not null", doc);
        assertEquals("Count is not expected.", 2, source.getDocuments().size());
        
        source.remove(new FileDocument(file1));
        assertEquals("Count is not expected.", 1, source.getDocuments().size());
        
    }
    
    public void testUpdateFile() {
        
        assertEquals("Count is not expected.", 2, source.getDocuments().size());
        
        final FileDocument fileDoc = new FileDocument(file1);
        assertFalse("Update is successfull.", source.updateFile(fileDoc, file1));
        assertTrue("Update is successfull.", source.updateFile(fileDoc, file2));
        
    }
    
    public void testAddRemoveLink() {
        
        assertEquals("Count is not expected.", 2, source.getDocuments().size());
        
        IDocument doc = source.addLink(url1);
        assertNull("Doc is not null", doc);
        assertEquals("Count is not expected.", 2, source.getDocuments().size());
        
        doc = source.addLink(url2);
        assertNotNull("Doc is not null", doc);
        assertEquals("Count is not expected.", 3, source.getDocuments().size());
        
        source.remove(new URLDocument(url2));
        assertEquals("Count is not expected.", 2, source.getDocuments().size());
        
        source.remove(new URLDocument(url1));
        assertEquals("Count is not expected.", 1, source.getDocuments().size());
        
    }
    
    public void testUpdateUrl() {
        
        assertEquals("Count is not expected.", 2, source.getDocuments().size());
        
        final URLDocument urlDoc = new URLDocument(url1);
        assertFalse("Update is successfull.", source.updateLink(urlDoc, url1));
        assertTrue("Update is successfull.", source.updateLink(urlDoc, url2));
        
    }
    
}
