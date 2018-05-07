/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.tests.shp;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IDocument.ContentType;
import org.locationtech.udig.catalog.document.IDocument.Type;
import org.locationtech.udig.catalog.document.IDocumentSource.DocumentInfo;
import org.locationtech.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import org.locationtech.udig.document.model.AbstractLinkedDocument;

import org.junit.Before;

/**
 * Abstract test class for shape document tests.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public abstract class AbstractShpDocTest {

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
    
    protected IProgressMonitor monitor;
    
    @Before
    public void setUp() throws Exception {
        monitor = new NullProgressMonitor();
        
        final File directory = new File(DIRECTORY);
        
        file = new File(directory, SHAPEFILE);
        url = file.toURI().toURL();

        file1 = new File(directory, FILE1);
        fileDocInfo1 = new DocumentInfo("fileDoc1", "fileDocDesc1", file1.getAbsolutePath(), ContentType.FILE, false, Type.ATTACHMENT);
        file2 = new File(directory, FILE2);
        fileDocInfo2 = new DocumentInfo("fileDoc2", "fileDocDesc2", file2.getAbsolutePath(), ContentType.FILE, true, Type.ATTACHMENT);
        
        webDocInfo1 = new DocumentInfo("webDoc1", "webDocDesc1", WEB1, ContentType.WEB, false, Type.LINKED);
        webDocInfo2 = new DocumentInfo("webDoc2", "webDocDesc2", WEB2, ContentType.WEB, false, Type.LINKED);
        
        descriptor1 = new HotlinkDescriptor("fileLabel", "description", FILE_ATTR, ContentType.FILE, "config");
        descriptor2 = new HotlinkDescriptor("webLabel", "description", LINK_ATTR, ContentType.WEB, "config");
        descriptor3 = new HotlinkDescriptor("actionLabel", "description", STATE_ATTR, ContentType.ACTION, "config");
        
        setUpInternal();

    }

    protected void setUpInternal() {
        // Override in child class
    }
    
    protected IDocument getDoc(List<IDocument> docs, DocumentInfo info) {
        for (IDocument doc : docs) {
            if (doc instanceof AbstractLinkedDocument) {
                final AbstractLinkedDocument basicDoc = (AbstractLinkedDocument) doc;  
                if (basicDoc.getInfo().getLabel().equals(info.getLabel())) {
                    return doc;
                }    
            }
        }
        return null;
    }
}

