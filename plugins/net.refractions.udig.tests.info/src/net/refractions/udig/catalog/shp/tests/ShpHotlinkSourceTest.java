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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;
import net.refractions.udig.document.model.ActionHotlinkDocument;
import net.refractions.udig.document.model.FileHotlinkDocument;
import net.refractions.udig.document.model.WebHotlinkDocument;
import net.refractions.udig.document.source.BasicHotlinkDescriptorParser;
import net.refractions.udig.document.source.ShpHotlinkSource;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

/**
 * Test class for {@link ShpHotlinkSource}.
 * 
 * @author Naz Chan
 */
@SuppressWarnings("nls")
public class ShpHotlinkSourceTest extends AbstractShpDocTest {

    protected ShpGeoResourceImpl geoResource;
    protected ShpHotlinkSource source;
    protected SimpleFeature feature;
    protected static final String FEATURE = "australia.1";
    
    @Override
    protected void setUpInternal() {
        super.setUpInternal();

        final Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(ShapefileDataStoreFactory.URLP.key, url);
        params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, false);

        final ShpServiceImpl service = new ShpServiceImpl(url, params);
        geoResource = new ShpGeoResourceImpl(service, "");
        source = new ShpHotlinkSource(geoResource);
        final Filter filter = CommonFactoryFinder.getFilterFactory2()
                .id(new FeatureIdImpl(FEATURE));
        feature = getFeature(geoResource, filter);

        final BasicHotlinkDescriptorParser parser = new BasicHotlinkDescriptorParser(geoResource);
        final List<HotlinkDescriptor> inInfos = new ArrayList<HotlinkDescriptor>();
        inInfos.add(descriptor1);
        inInfos.add(descriptor2);
        inInfos.add(descriptor3);
        parser.setDescriptors(inInfos);
        
    }

    private SimpleFeature getFeature(IGeoResource geoResource, Filter filter) {
        try {
            if (geoResource.canResolve(SimpleFeatureStore.class)) {
                final SimpleFeatureStore featureSource = geoResource.resolve(
                        SimpleFeatureStore.class, new NullProgressMonitor());
                final SimpleFeatureCollection featureCollection = featureSource.getFeatures(filter);
                final SimpleFeatureIterator featureIterator = featureCollection.features();
                try {
                    if (featureIterator.hasNext()) {
                        return featureIterator.next();
                    }
                } finally {
                    if (featureIterator != null) {
                        featureIterator.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testGetDescriptors() {
        final List<HotlinkDescriptor> descriptors = source.getHotlinkDescriptors(feature, monitor);
        assertEquals("Descriptor count is not expected.", 3, descriptors.size());
    }

    @Test
    public void testGetDocuments() {

        final List<IDocument> docs = source.getDocuments(feature, monitor);
        assertEquals("Count is not expected.", 3, docs.size());

        IDocument doc = source.getDocument(feature, FILE_ATTR, monitor);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc is not an instance of HotlinkFileDoc.",
                (doc instanceof FileHotlinkDocument));

        doc = source.getDocument(feature, LINK_ATTR, monitor);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc is not an instance of HotlinkWebDoc.", (doc instanceof WebHotlinkDocument));

        doc = source.getDocument(feature, STATE_ATTR, monitor);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc is not an instance of HotlinkWebDoc.",
                (doc instanceof ActionHotlinkDocument));

    }

    @Test
    public void testSetAndClearFile() {

        source.setFile(feature, FILE_ATTR, file1, monitor);

        IDocument doc = source.getDocument(feature, FILE_ATTR, monitor);
        File docFile = (File) doc.getContent();
        assertNotNull("Doc is null.", doc);
        assertEquals("File is not expected.", file1.getAbsolutePath(), docFile.getAbsolutePath());

        source.clear(feature, FILE_ATTR, monitor);

        doc = source.getDocument(feature, FILE_ATTR, monitor);
        docFile = (File) doc.getContent();
        assertNotNull("Doc is null.", doc);
        assertNull("File is not null.", docFile);

    }

    @Test
    public void testSetAndClearWeb() {

        URL url = null;
        try {
            url = new URL(WEB1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        source.setLink(feature, LINK_ATTR, url, monitor);

        IDocument doc = source.getDocument(feature, LINK_ATTR, monitor);
        URL docUrl = (URL) doc.getContent();
        assertNotNull("Doc is null.", doc);
        assertEquals("File is not expected.", url.toString(), docUrl.toString());

        source.clear(feature, LINK_ATTR, monitor);

        doc = source.getDocument(feature, LINK_ATTR, monitor);
        docUrl = (URL) doc.getContent();
        assertNotNull("Doc is null.", doc);
        assertNull("File is not null.", docUrl);

    }

    @Test
    public void testSetAndClearAction() {

        final String action = "ACTION";

        source.setAction(feature, STATE_ATTR, action, monitor);

        IDocument doc = source.getDocument(feature, STATE_ATTR, monitor);
        String docAction = (String) doc.getContent();
        assertNotNull("Doc is null.", doc);
        assertEquals("File is not expected.", action, docAction);

        source.clear(feature, STATE_ATTR, monitor);

        doc = source.getDocument(feature, STATE_ATTR, monitor);
        docAction = (String) doc.getContent();
        assertNotNull("Doc is null.", doc);
        assertNull("File is not null.", docAction);

    }

}
