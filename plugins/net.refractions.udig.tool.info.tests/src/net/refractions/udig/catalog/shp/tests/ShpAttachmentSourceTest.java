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
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;
import net.refractions.udig.document.source.ShpAttachmentSource;
import net.refractions.udig.document.source.ShpDocPropertyParser;

import org.geotools.filter.identity.FeatureIdImpl;

/**
 * Test class for {@link ShpAttachmentSource}.
 * 
 * @author Naz Chan 
 */
@SuppressWarnings("nls")
public class ShpAttachmentSourceTest extends ShpHotlinkSourceTest {

    private ShpAttachmentSource attachSource;
    private File attachDir;
    
    protected static final FeatureIdImpl FEATURE_ID = new FeatureIdImpl(FEATURE);
    
    @Override
    protected void setUpInternal() {
        super.setUpInternal();

        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url);
        parser.setFeatureDocumentInfos(feature, new ArrayList<DocumentInfo>());
        parser.writeProperties();
        
        attachDir = parser.getFeatureAttachDir(FEATURE);
        attachSource = new ShpAttachmentSource(geoResource);
        
        cleaupAttachDir();
        
    }
    
    public void testGetDocuments() {
        
        List<IDocument> docs = attachSource.getDocuments(feature);
        assertEquals("Count is not expected.", 3, docs.size());
        
    }
    
    public void testAddRemove() {
        
        List<IDocument> docs = attachSource.getDocuments(feature);
        
        attachSource.add(feature, fileDocInfo1);
        assertEquals("Count is not expected.", 4, docs.size());
        assertTrue("File was not added.", fileExistsInLocalDir(file1));
        
        attachSource.remove(feature, getDoc(docs, fileDocInfo1));
        assertEquals("Count is not expected.", 3, docs.size());
        assertFalse("File was not removed.", fileExistsInLocalDir(file1));
        
        List<DocumentInfo> inInfos = new ArrayList<DocumentInfo>();
        inInfos.add(fileDocInfo2);
        inInfos.add(webDocInfo2);
        attachSource.add(feature, inInfos);
        assertEquals("Count is not expected.", 5, docs.size());
        assertTrue("File was not added.", fileExistsInLocalDir(file2));
        
        List<IDocument> inDocs = new ArrayList<IDocument>();
        inDocs.add(getDoc(docs, fileDocInfo2));
        inDocs.add(getDoc(docs, webDocInfo2));
        attachSource.remove(feature, inDocs);
        assertEquals("Count is not expected.", 3, docs.size());
        assertFalse("File was not removed.", fileExistsInLocalDir(file2));
        
        cleaupAttachDir();
        
    }
    
    public void testUpdate() {
        
        List<IDocument> docs = attachSource.getDocuments(feature);
        assertEquals("Count is not expected.", 3, docs.size());
        
        IDocument doc = attachSource.add(feature, fileDocInfo1);
        assertNotNull("Doc does not exists.", doc);
        assertTrue("File was not added.", fileExistsInLocalDir(file1));
        
        attachSource.update(feature, doc, fileDocInfo2);
        
        doc = getDoc(docs, fileDocInfo1);
        assertNull("Doc exists.", doc);
        assertFalse("File was not removed.", fileExistsInLocalDir(file1));
        
        doc = getDoc(docs, fileDocInfo2);
        assertNotNull("Doc does not exists.", doc);
        assertTrue("File was not added.", fileExistsInLocalDir(file2));
        
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
    
    private boolean fileExistsInLocalDir(File file) {
        for (String fileName : attachDir.list()) {
            if (file.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }
    
}
