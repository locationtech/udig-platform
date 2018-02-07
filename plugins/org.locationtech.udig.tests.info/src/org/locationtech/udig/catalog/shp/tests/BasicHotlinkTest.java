/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.shp.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceExtension;
import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IDocument.ContentType;
import org.locationtech.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import org.locationtech.udig.catalog.internal.shp.ShpServiceExtension;
import org.locationtech.udig.document.source.BasicHotlinkDescriptorParser;
import org.locationtech.udig.document.source.BasicHotlinkSource;
import org.locationtech.udig.tool.info.tests.Activator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory2;
import org.osgi.framework.Bundle;

@SuppressWarnings("nls")
public class BasicHotlinkTest {

    private static IService service;
    private static IGeoResource resource;
    private static BasicHotlinkDescriptorParser parser;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        assertNotNull("Please run as Plugin Test", Activator.getDefault());

        Bundle bundle = Activator.getDefault().getBundle();
        File directory = FileLocator.getBundleFile(bundle);
        assertTrue("Test Directory", directory.isDirectory());

        File shapefile = new File(new File(directory, "internal"), "australia.shp");
        assertTrue("Sample File", shapefile.isFile());

        ServiceExtension factory = new ShpServiceExtension();
        Map<String, Serializable> params = factory.createParams(shapefile.toURI().toURL());
        service = factory.createService(null, params);

        List<IGeoResource> members = (List<IGeoResource>) service
                .resources(new NullProgressMonitor());
        resource = members.get(0);

        final List<HotlinkDescriptor> descriptors = new ArrayList<HotlinkDescriptor>();
        descriptors.add(new HotlinkDescriptor("File", "File Description", "FILE", ContentType.FILE, ""));
        descriptors.add(new HotlinkDescriptor("Web", "Web Description", "LINK", ContentType.WEB, ""));

        parser = new BasicHotlinkDescriptorParser(resource);
        parser.setDescriptors(descriptors);

    }

    @Test
    public void testBasicResolveAdaptorFactory() throws Exception {
        
        // IResolveAdapterFactory adaptorFactory = new BasicHotlinkResolveFactory();
        // assertTrue(adaptorFactory.canAdapt(resource, IHotlinkSource.class));
        //
        // IHotlinkSource hotlink = (IHotlinkSource) adaptorFactory.adapt(resource,
        // IHotlinkSource.class, new NullProgressMonitor());

        final BasicHotlinkSource hotlink = new BasicHotlinkSource(resource);
        assertNotNull(hotlink);

        List<HotlinkDescriptor> list = hotlink.getHotlinkDescriptors(null, new NullProgressMonitor());
        assertEquals("descriptors found", 2, list.size());

        SimpleFeatureStore featureStore = resource.resolve(SimpleFeatureStore.class,
                new NullProgressMonitor());
        SimpleFeatureType schema = featureStore.getSchema();
        for (HotlinkDescriptor hotlinkDescriptor : list) {
            AttributeDescriptor attributeDescriptor = schema.getDescriptor(hotlinkDescriptor
                    .getAttributeName());
            assertNotNull("confirm attribute name matches", attributeDescriptor);
            assertTrue("Confirm String",
                    String.class.isAssignableFrom(attributeDescriptor.getType().getBinding()));

            assertNotNull(hotlinkDescriptor.getType() != null);
        }
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        SimpleFeatureCollection features = featureStore.getFeatures(ff.equals(ff.property("STATE"),
                ff.literal("Tasmania")));
        SimpleFeatureIterator iterator = features.features();
        assertTrue("Tasmania found", iterator.hasNext());
        SimpleFeature tasmania = iterator.next();

        List<IDocument> documents = hotlink.getDocuments(tasmania, new NullProgressMonitor());
        assertEquals("image document found", 2, documents.size());

        // IDocument imageDocument = documents.get(0);
        // assertEquals(ContentType.FILE, imageDocument.getContentType());

        // IDocument webLink = documents.get(1);
        // assertEquals(ContentType.WEB, webLink.getContentType());

    }

}
