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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.internal.document.DocumentFactory;
import net.refractions.udig.catalog.internal.document.FileDocument;
import net.refractions.udig.catalog.internal.document.URLDocument;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;
import net.refractions.udig.catalog.shp.ShpDocPropertyParser;
import net.refractions.udig.catalog.shp.ShpDocumentSource;

import org.geotools.data.shapefile.ShapefileDataStoreFactory;

/**
 * Test class for {@link ShpDocumentSource}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class ShpDocumentSourceTest extends AbstractShpDocTest {

    private ShpDocumentSource source;
    
    @Override
    protected void setUpInternal() {
        super.setUpInternal();
        
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
