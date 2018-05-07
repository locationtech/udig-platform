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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IDocument.ContentType;
import org.locationtech.udig.catalog.document.IDocument.Type;
import org.locationtech.udig.catalog.document.IDocumentSource.DocumentInfo;
import org.locationtech.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import org.locationtech.udig.document.model.ActionHotlinkDocument;
import org.locationtech.udig.document.model.FileAttachmentDocument;
import org.locationtech.udig.document.model.FileHotlinkDocument;
import org.locationtech.udig.document.model.FileLinkedDocument;
import org.locationtech.udig.document.model.WebHotlinkDocument;
import org.locationtech.udig.document.model.WebLinkedDocument;
import org.locationtech.udig.document.source.ShpDocFactory;

import org.junit.Test;

/**
 * Test case for {@link ShpDocFactory}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class ShpDocFactoryTest {
    
    private static final String LABEL = "label";
    private static final String DESCRIPTION = "description";
    private static final String INFO = "info";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String CONFIG = "config";
    
    @Test
    public void testCreateDocument() {
        
        final ShpDocFactory factory = new ShpDocFactory(null);
        
        DocumentInfo info = new DocumentInfo(LABEL, DESCRIPTION, INFO, ContentType.FILE, false, Type.LINKED);
        IDocument doc = factory.create(info);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof FileLinkedDocument);
        
        info = new DocumentInfo(LABEL, DESCRIPTION, INFO, ContentType.FILE, false, Type.ATTACHMENT);
        doc = factory.create(info);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof FileAttachmentDocument);
        
        info = new DocumentInfo(LABEL, DESCRIPTION, INFO, ContentType.WEB, false, Type.LINKED);
        doc = factory.create(info);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof WebLinkedDocument);
        
    }
    
    @Test
    public void testCreateHotlink() {

        final ShpDocFactory factory = new ShpDocFactory(null);
        
        HotlinkDescriptor descriptor = new HotlinkDescriptor(LABEL, DESCRIPTION, ATTRIBUTE_NAME, ContentType.FILE, CONFIG);
        IDocument doc = factory.create(INFO, Collections.singletonList(descriptor));
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof FileHotlinkDocument);
        
        descriptor = new HotlinkDescriptor(LABEL, DESCRIPTION, ATTRIBUTE_NAME, ContentType.WEB, CONFIG);
        doc = factory.create(INFO, Collections.singletonList(descriptor));
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof WebHotlinkDocument);
        
        descriptor = new HotlinkDescriptor(LABEL, DESCRIPTION, ATTRIBUTE_NAME, ContentType.ACTION, CONFIG);
        doc = factory.create(INFO, Collections.singletonList(descriptor));
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof ActionHotlinkDocument);
        
        descriptor = new HotlinkDescriptor(LABEL, DESCRIPTION, ATTRIBUTE_NAME, ContentType.ACTION, CONFIG);
        HotlinkDescriptor descriptor2 = new HotlinkDescriptor(LABEL, DESCRIPTION, ATTRIBUTE_NAME, ContentType.ACTION, CONFIG);
        HotlinkDescriptor descriptor3 = new HotlinkDescriptor(LABEL, DESCRIPTION, ATTRIBUTE_NAME, ContentType.ACTION, CONFIG);
        final List<HotlinkDescriptor> descriptors = new ArrayList<HotlinkDescriptor>();
        descriptors.add(descriptor);
        descriptors.add(descriptor2);
        descriptors.add(descriptor3);
        doc = factory.create(INFO, descriptors);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc's type is not expected.", doc instanceof ActionHotlinkDocument);

    }
    
}
