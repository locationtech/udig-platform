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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocument.ContentType;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;
import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import net.refractions.udig.catalog.internal.document.AttachmentFileDocument;
import net.refractions.udig.catalog.internal.document.FileDocument;
import net.refractions.udig.catalog.internal.document.HotlinkActionDocument;
import net.refractions.udig.catalog.internal.document.HotlinkFileDocument;
import net.refractions.udig.catalog.internal.document.HotlinkWebDocument;
import net.refractions.udig.catalog.internal.document.WebDocument;
import net.refractions.udig.catalog.shp.ShpDocFactory;

/**
 * Test case for {@link ShpDocFactory}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class ShpDocFactoryTest extends TestCase {
    
    private static final String LABEL = "label";
    private static final String DESCRIPTION = "description";
    private static final String INFO = "info";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String CONFIG = "config";
    
    public void testCreate() {
        
        final ShpDocFactory factory = new ShpDocFactory(null);
        
        DocumentInfo info = new DocumentInfo(LABEL, DESCRIPTION, INFO, ContentType.FILE, false);
        IDocument doc = factory.create(info);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof FileDocument);
        
        info = new DocumentInfo(LABEL, DESCRIPTION, INFO, ContentType.WEB, false);
        doc = factory.create(info);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof WebDocument);
        
    }
    
    public void testCreateWithAttachOption() {
        
        final ShpDocFactory factory = new ShpDocFactory(null);
        
        DocumentInfo info = new DocumentInfo(LABEL, DESCRIPTION, INFO, ContentType.FILE, false);
        IDocument doc = factory.create(info, false);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof FileDocument);
        
        info = new DocumentInfo(LABEL, DESCRIPTION, INFO, ContentType.FILE, false);
        doc = factory.create(info, true);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof AttachmentFileDocument);
        
        info = new DocumentInfo(LABEL, DESCRIPTION, INFO, ContentType.WEB, false);
        doc = factory.create(info, false);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof WebDocument);
        
        info = new DocumentInfo(LABEL, DESCRIPTION, INFO, ContentType.WEB, false);
        doc = factory.create(info, true);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof WebDocument);
        
    }
    
    public void testCreateHotlink() {

        final ShpDocFactory factory = new ShpDocFactory(null);
        
        HotlinkDescriptor descriptor = new HotlinkDescriptor(LABEL, DESCRIPTION, ATTRIBUTE_NAME, ContentType.FILE, CONFIG);
        IDocument doc = factory.create(INFO, Collections.singletonList(descriptor));
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof HotlinkFileDocument);
        
        descriptor = new HotlinkDescriptor(LABEL, DESCRIPTION, ATTRIBUTE_NAME, ContentType.WEB, CONFIG);
        doc = factory.create(INFO, Collections.singletonList(descriptor));
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof HotlinkWebDocument);
        
        descriptor = new HotlinkDescriptor(LABEL, DESCRIPTION, ATTRIBUTE_NAME, ContentType.ACTION, CONFIG);
        doc = factory.create(INFO, Collections.singletonList(descriptor));
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof HotlinkActionDocument);
        
        descriptor = new HotlinkDescriptor(LABEL, DESCRIPTION, ATTRIBUTE_NAME, ContentType.ACTION, CONFIG);
        HotlinkDescriptor descriptor2 = new HotlinkDescriptor(LABEL, DESCRIPTION, ATTRIBUTE_NAME, ContentType.ACTION, CONFIG);
        HotlinkDescriptor descriptor3 = new HotlinkDescriptor(LABEL, DESCRIPTION, ATTRIBUTE_NAME, ContentType.ACTION, CONFIG);
        final List<HotlinkDescriptor> descriptors = new ArrayList<HotlinkDescriptor>();
        descriptors.add(descriptor);
        descriptors.add(descriptor2);
        descriptors.add(descriptor3);
        doc = factory.create(INFO, descriptors);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof HotlinkActionDocument);

    }
    
}
