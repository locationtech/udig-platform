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

import junit.framework.TestCase;
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
public class ShpDocPropertyParserTest extends TestCase {

    private File file;
    private URL url;
    
    private static final String DIRECTORY = "internal\\";
    private static final String SHAPEFILE = "countries.shp";
    private static final String FILE = "attachment1.txt";
    private static final String WEB = "http://www.google.com";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        file = new File(DIRECTORY + SHAPEFILE);
        url = file.toURI().toURL();
    }
    
    public void testHasProperties() throws MalformedURLException {
        
        ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        assertTrue("Property file does not exist.", parser.hasProperties());
        
        final URL url = new File(DIRECTORY + "dummy.shp").toURI().toURL();
        parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        assertFalse("Property file exist.", parser.hasProperties());
        
    }
    
    public void testGetLinkValue() throws MalformedURLException {
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        
        final FileDocument fileDoc = new FileDocument(new File(DIRECTORY + FILE));
        assertEquals("Link is not expected.", FILE, parser.getLinkValue(fileDoc));
        
        final URLDocument urlDoc = new URLDocument(new URL(WEB));
        assertEquals("Link is not expected.", WEB, parser.getLinkValue(urlDoc));
        
    }
    
    public void testGetFileLinkValue() {
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        final File file = new File(DIRECTORY + FILE);
        assertEquals("Link is not expected.", FILE, parser.getFileLinkValue(file));
        
    }
    
    public void testGetUrlLinkValue() throws MalformedURLException {
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        final URL url = new URL(WEB);
        assertEquals("Link is not expected.", WEB, parser.getUrlLinkValue(url));
        
    }
    
    public void testGetFeatureLinkInfo() {
        
        //Country|CNTRY_NAME|FILE||Name|LONG_NAME|WEB
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        
        final LinkInfo info1 = parser.getFeatureLinkInfo("CNTRY_NAME");
        assertEquals("Label is not expected.", "Country", info1.getLabel());
        assertEquals("Info is not expected.", "CNTRY_NAME", info1.getInfo());
        assertEquals("Type is not expected.", IDocument.TYPE.FILE, info1.getType());
        
        final LinkInfo info2 = parser.getFeatureLinkInfo("LONG_NAME");
        assertEquals("Label is not expected.", "Name", info2.getLabel());
        assertEquals("Info is not expected.", "LONG_NAME", info2.getInfo());
        assertEquals("Type is not expected.", IDocument.TYPE.WEB, info2.getType());
        
    }
    
    public void testSetGetShapeAttachments() throws MalformedURLException {
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url, new DocumentFactory(null));
        
        final List<IDocument> oldDocs = new ArrayList<IDocument>();
        oldDocs.add(new FileDocument(new File(DIRECTORY + FILE)));
        oldDocs.add(new URLDocument(new URL(WEB)));
        
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
                final File oldFile =  new File(DIRECTORY + FILE);
                final File newFile =  fileDoc.getFile();
                assertEquals("File directory is not expected", oldFile.getAbsolutePath(),
                        newFile.getAbsolutePath());
            } else if (doc instanceof URLDocument) {
                urlCnt++;
                final URLDocument urlDoc = (URLDocument) doc;
                final URL oldUrl = new URL(WEB);
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
        oldDocs.add(new FileDocument(new File(DIRECTORY + FILE)));
        oldDocs.add(new URLDocument(new URL(WEB)));
        
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
                final File oldFile =  new File(DIRECTORY + FILE);
                final File newFile =  fileDoc.getFile();
                assertEquals("File directory is not expected", oldFile.getAbsolutePath(),
                        newFile.getAbsolutePath());
            } else if (doc instanceof URLDocument) {
                urlCnt++;
                final URLDocument urlDoc = (URLDocument) doc;
                final URL oldUrl = new URL(WEB);
                final URL newUrl = urlDoc.getUrl();
                assertEquals("URL is not expected", oldUrl.toString(),
                        newUrl.toString());
            }
        }
        
        assertEquals("File doc count is not expected.", 1, fileCnt);
        assertEquals("URL doc count is not expected.", 1, urlCnt);
        
    }
    
}
