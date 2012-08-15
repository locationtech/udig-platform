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
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;
import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import net.refractions.udig.catalog.internal.document.AbstractBasicDocument;

import junit.framework.TestCase;

/**
 * Abstract test class for shape document tests.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public abstract class AbstractShpDocTest extends TestCase {

    protected File file;
    protected URL url;
    
    protected File file1;
    protected File file2;
    
    protected DocumentInfo fileDocInfo1;
    protected DocumentInfo fileDocInfo2;
    
    protected DocumentInfo webDocInfo1;
    protected DocumentInfo webDocInfo2;
    
    protected HotlinkDescriptor descriptor1;
    protected HotlinkDescriptor descriptor2;
    protected HotlinkDescriptor descriptor3;
    
    protected static final String DIRECTORY = "internal";
    protected static final String SHAPEFILE = "australia.shp";
    protected static final String FILE1 = "readme.txt";
    protected static final String FILE2 = "australia.png";
    protected static final String WEB1 = "http://en.wikipedia.org/wiki/Australia";
    protected static final String WEB2 = "http://en.wikipedia.org/wiki/History_of_Australia";
        
    protected static final String FILE_ATTR = "FILE";
    protected static final String LINK_ATTR = "LINK";
    protected static final String STATE_ATTR = "STATE";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final File directory = new File(DIRECTORY);
        
        file = new File(directory, SHAPEFILE);
        url = file.toURI().toURL();

        file1 = new File(directory, FILE1);
        fileDocInfo1 = new DocumentInfo("fileDoc1", "fileDocDesc1", file1.getAbsolutePath(), Type.FILE, false);
        file2 = new File(directory, FILE2);
        fileDocInfo2 = new DocumentInfo("fileDoc2", "fileDocDesc2", file2.getAbsolutePath(), Type.FILE, true);
        
        webDocInfo1 = new DocumentInfo("webDoc1", "webDocDesc1", WEB1, Type.WEB, false);
        webDocInfo2 = new DocumentInfo("webDoc2", "webDocDesc2", WEB2, Type.WEB, false);
        
        descriptor1 = new HotlinkDescriptor("label", "description", FILE_ATTR, Type.FILE, "config");
        descriptor2 = new HotlinkDescriptor("label", "description", LINK_ATTR, Type.WEB, "config");
        descriptor3 = new HotlinkDescriptor("label", "description", STATE_ATTR, Type.ACTION, "config");
        
        setUpInternal();

    }

    protected void setUpInternal() {
        // Override in child class
    }
    
    protected IDocument getDoc(List<IDocument> docs, DocumentInfo info) {
        for (IDocument doc : docs) {
            if (doc instanceof AbstractBasicDocument) {
                final AbstractBasicDocument basicDoc = (AbstractBasicDocument) doc;  
                if (basicDoc.getInfo().getLabel().equals(info.getLabel())) {
                    return doc;
                }    
            }
        }
        return null;
    }
}

