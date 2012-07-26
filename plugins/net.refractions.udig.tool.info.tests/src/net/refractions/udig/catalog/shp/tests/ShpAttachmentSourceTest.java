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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.FileDocument;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.URLDocument;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;
import net.refractions.udig.catalog.shp.ShpAttachmentSource;
import net.refractions.udig.catalog.shp.ShpDocPropertyParser;

import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.filter.identity.FeatureIdImpl;

/**
 * Test class for {@link ShpAttachmentSource}.
 * 
 * @author Naz Chan 
 */
@SuppressWarnings("nls")
public class ShpAttachmentSourceTest extends AbstractShpDocTest {

    private ShpAttachmentSource source;
    private File attachDir;
    
    private static final String FEATURE = "australia.2";
    private static final FeatureIdImpl FEATURE_ID = new FeatureIdImpl(FEATURE);
    
    @Override
    protected void setUpInternal() {
        super.setUpInternal();

        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, null);
        attachDir = parser.getFeatureAttachmentsDir(FEATURE);
        
        final Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, false);
        params.put(ShapefileDataStoreFactory.DBFCHARSET.key, "P_DEFAULT_CHARSET");
        
        final ShpServiceImpl service = new ShpServiceImpl(url, params);
        final ShpGeoResourceImpl geoResource = new ShpGeoResourceImpl(service, "");
        source = new ShpAttachmentSource(geoResource);
        
    }
    
    public void testGetDocuments() {
        assertEquals("Count is not expected.", 0, source.getDocuments(FEATURE_ID).size());
    }
    
    public void testAddRemoveFile() {
        
        source.addFile(FEATURE_ID, file2);
        
        List<IDocument> docs = source.getDocuments(FEATURE_ID);
        assertEquals("Count is not expected.", 1, docs.size());
        IDocument doc = getDoc(docs, file2);
        assertNotNull("Doc was not added.", doc);
        assertTrue("File was not added.", fileExistsInLocalDir(file2));
        
        source.remove(FEATURE_ID, doc);
        
        docs = source.getDocuments(FEATURE_ID);
        assertEquals("Count is not expected.", 0, docs.size());
        doc = getDoc(docs, file2);
        assertNull("Doc was not removed.", doc);
        assertFalse("File was not removed.", fileExistsInLocalDir(file2));
     
        final List<File> files = new ArrayList<File>();
        files.add(file1);
        files.add(file2);
        
        source.addFiles(FEATURE_ID, files);
        docs = source.getDocuments(FEATURE_ID);
        assertEquals("Count is not expected.", 2, docs.size());
        IDocument doc1 = getDoc(docs, file1);
        assertNotNull("Doc was not removed.", doc1);
        assertTrue("File was not removed.", fileExistsInLocalDir(file1));
        IDocument doc2 = getDoc(docs, file2);
        assertNotNull("Doc was not removed.", doc2);
        assertTrue("File was not removed.", fileExistsInLocalDir(file2));
        
        final List<IDocument> docsAdded = new ArrayList<IDocument>();
        docsAdded.add(doc1);
        docsAdded.add(doc2);
        
        source.remove(FEATURE_ID, docsAdded);
        docs = source.getDocuments(FEATURE_ID);
        assertEquals("Count is not expected.", 0, docs.size());
        doc = getDoc(docs, file1);
        assertNull("Doc was not removed.", doc);
        assertFalse("File was not removed.", fileExistsInLocalDir(file1));
        doc = getDoc(docs, file2);
        assertNull("Doc was not removed.", doc);
        assertFalse("File was not removed.", fileExistsInLocalDir(file2));
        
        cleaupAttachDir();
        
    }
    
    public void testUpdateFile() {
        
        source.addFile(FEATURE_ID, file2);
        
        List<IDocument> docs = source.getDocuments(FEATURE_ID);
        assertEquals("Count is not expected.", 1, docs.size());
        IDocument doc = getDoc(docs, file2);
        assertNotNull("Doc was not added.", doc);
        assertTrue("File was not added.", fileExistsInLocalDir(file2));
        
        source.updateFile(FEATURE_ID, (FileDocument) doc, file1);
        
        docs = source.getDocuments(FEATURE_ID);
        assertEquals("Count is not expected.", 1, docs.size());
        doc = getDoc(docs, file1);
        assertNotNull("Doc was not added.", doc);
        assertTrue("File was not added.", fileExistsInLocalDir(file1));
        assertFalse("File was not added.", fileExistsInLocalDir(file2));
        
        source.remove(FEATURE_ID, doc);
        
        docs = source.getDocuments(FEATURE_ID);
        assertEquals("Count is not expected.", 0, docs.size());
        doc = getDoc(docs, file1);
        assertNull("Doc was not removed.", doc);
        assertFalse("File was not removed.", fileExistsInLocalDir(file1));
        
        cleaupAttachDir();
        
    }
    
    public void testAddUpdateRemoveLink() {
        
        source.addLink(FEATURE_ID, url1);
        
        List<IDocument> docs = source.getDocuments(FEATURE_ID);
        assertEquals("Count is not expected.", 1, docs.size());
        IDocument doc = getDoc(docs, url1);
        assertNotNull("Doc was not added.", doc);
        
        source.updateLink(FEATURE_ID, (URLDocument) doc, url2);
        
        docs = source.getDocuments(FEATURE_ID);
        assertEquals("Count is not expected.", 1, docs.size());
        doc = getDoc(docs, url2);
        assertNotNull("Doc was not added.", doc);
        
        source.remove(FEATURE_ID, doc);
        
        docs = source.getDocuments(FEATURE_ID);
        assertEquals("Count is not expected.", 0, docs.size());
        doc = getDoc(docs, url2);
        assertNull("Doc was not removed.", doc);
        
        cleaupAttachDir();
        
    }
    
    private void cleaupAttachDir() {
        if (attachDir.exists()) {
            final File attachParentDir = attachDir.getParentFile();
            for (File file : attachDir.listFiles()) {
                file.delete();
            }
            attachDir.delete();
            attachParentDir.delete();
        }
    }
    
    private IDocument getDoc(List<IDocument> docs, File file) {
        for (IDocument doc : docs) {
            if (doc instanceof FileDocument) {
                final FileDocument fileDoc = (FileDocument) doc;
                if (fileDoc.getFile().getName().equals(file.getName())) {
                    return fileDoc;
                }
            }
        }
        return null;
    }
    
    private IDocument getDoc(List<IDocument> docs, URL url) {
        for (IDocument doc : docs) {
            if (doc instanceof URLDocument) {
                final URLDocument urlDoc = (URLDocument) doc;
                if (urlDoc.getUrl().toString().equals(url.toString())) {
                    return urlDoc;
                }
            }
        }
        return null;
    }
    
    private boolean fileExistsInLocalDir(File file) {
        for (String fileName : attachDir.list()) {
            if (file.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }
    
}
