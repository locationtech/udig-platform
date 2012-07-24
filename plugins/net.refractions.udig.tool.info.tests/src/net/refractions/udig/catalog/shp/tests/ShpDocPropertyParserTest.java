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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.DocumentFactory;
import net.refractions.udig.catalog.FileDocument;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.LinkInfo;
import net.refractions.udig.catalog.URLDocument;
import net.refractions.udig.catalog.shp.ShpDocPropertyParser;

/**
 * Test class for {@link ShpDocPropertyParser}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class ShpDocPropertyParserTest extends AbstractShpDocTest {
    
    public void testHasProperties() throws MalformedURLException {
        
        ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        assertTrue("Property file does not exist.", parser.hasProperties());
        
        final URL url = new File(new File( DIRECTORY ), "dummy.shp").toURI().toURL();
        parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        assertFalse("Property file exist.", parser.hasProperties());
        
    }
    
    public void testGetLinkValue() throws MalformedURLException {
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        
        final FileDocument fileDoc = new FileDocument( new File(new File(DIRECTORY), FILE1));
        assertEquals("Link is not expected.", FILE1, parser.getLinkValue(fileDoc));
        
        final URLDocument urlDoc = new URLDocument(new URL(WEB1));
        assertEquals("Link is not expected.", WEB1, parser.getLinkValue(urlDoc));
        
    }
    
    public void testGetFileLinkValue() {
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        final File file = new File( new File(DIRECTORY ), FILE1);
        assertEquals("Link is not expected.", FILE1, parser.getFileLinkValue(file));
        
    }
    
    public void testGetUrlLinkValue() throws MalformedURLException {
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        final URL url = new URL(WEB1);
        assertEquals("Link is not expected.", WEB1, parser.getUrlLinkValue(url));
        
    }
    
    public void testGetFeatureLinkInfo() {
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        
        final LinkInfo info1 = parser.getFeatureLinkInfo(FILE_ATTR);
        assertEquals("Label is not expected.", FILE_ATTR_LBL, info1.getLabel());
        assertEquals("Info is not expected.", FILE_ATTR, info1.getInfo());
        assertEquals("Type is not expected.", FILE_ATTR_TYPE, info1.getType());
        
        final LinkInfo info2 = parser.getFeatureLinkInfo(LINK_ATTR);
        assertEquals("Label is not expected.", LINK_ATTR_LBL, info2.getLabel());
        assertEquals("Info is not expected.", LINK_ATTR, info2.getInfo());
        assertEquals("Type is not expected.", LINK_ATTR_TYPE, info2.getType());
        
    }
    
    public void testSetGetShapeAttachments() throws MalformedURLException {
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        
        final List<IDocument> oldDocs = new ArrayList<IDocument>();
        oldDocs.add(new FileDocument(new File( new File(DIRECTORY ), FILE1)));
        oldDocs.add(new URLDocument(new URL(WEB1)));
        
        parser.setShapeAttachments(oldDocs);
        
        final List<IDocument> newDocs = parser.getShapeAttachments();
        assertNotNull("Doc list is null.", newDocs);
        assertEquals("Doc count is not expected.", 2, newDocs.size());

        int fileCnt = 0;
        int urlCnt = 0;
        
        for (IDocument doc : newDocs) {
            if (doc instanceof FileDocument) {
                fileCnt++;
                final FileDocument fileDoc = (FileDocument) doc;
                final File oldFile =  new File( new File(DIRECTORY), FILE1);
                final File newFile =  fileDoc.getFile();
                assertEquals("File directory is not expected", oldFile.getAbsolutePath(),
                        newFile.getAbsolutePath());
            } else if (doc instanceof URLDocument) {
                urlCnt++;
                final URLDocument urlDoc = (URLDocument) doc;
                final URL oldUrl = new URL(WEB1);
                final URL newUrl = urlDoc.getUrl();
                assertEquals("URL is not expected", oldUrl.toString(),
                        newUrl.toString());
            }
        }
        
        assertEquals("File doc count is not expected.", 1, fileCnt);
        assertEquals("URL doc count is not expected.", 1, urlCnt);
        
    }
    
    public void testSetGetFeatureAttachments() throws MalformedURLException {
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        
        final List<IDocument> oldDocs = new ArrayList<IDocument>();
        oldDocs.add(new FileDocument(new File(new File(DIRECTORY ), FILE1)));
        oldDocs.add(new URLDocument(new URL(WEB1)));
        
        final String featureId = "dummy.1";
        
        parser.setFeatureAttachments(featureId, oldDocs);
        
        final List<IDocument> newDocs = parser.getFeatureAttachments(featureId);
        assertNotNull("Doc list is null.", newDocs);
        assertEquals("Doc count is not expected.", 2, newDocs.size());

        int fileCnt = 0;
        int urlCnt = 0;
        
        for (IDocument doc : newDocs) {
            if (doc instanceof FileDocument) {
                fileCnt++;
                final FileDocument fileDoc = (FileDocument) doc;
                final File oldFile =  new File(new File(DIRECTORY ), FILE1);
                final File newFile =  fileDoc.getFile();
                assertEquals("File directory is not expected", oldFile.getAbsolutePath(),
                        newFile.getAbsolutePath());
            } else if (doc instanceof URLDocument) {
                urlCnt++;
                final URLDocument urlDoc = (URLDocument) doc;
                final URL oldUrl = new URL(WEB1);
                final URL newUrl = urlDoc.getUrl();
                assertEquals("URL is not expected", oldUrl.toString(),
                        newUrl.toString());
            }
        }
        
        assertEquals("File doc count is not expected.", 1, fileCnt);
        assertEquals("URL doc count is not expected.", 1, urlCnt);
        
    }
    
}
