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

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.FileDocument;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.URLDocument;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;
import net.refractions.udig.catalog.shp.ShpHotlinkSource;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

/**
 * Test class for {@link ShpHotlinkSource}.
 * 
 * @author Naz Chan 
 */
@SuppressWarnings("nls")
public class ShpHotlinkSourceTest extends AbstractShpDocTest {

    private ShpGeoResourceImpl geoResource;
    private ShpHotlinkSource source;
    private SimpleFeature feature;
    
    private static final String FEATURE = "australia.1";
    
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
        
        source.setFile(feature, FILE_ATTR, file1);
        source.setLink(feature, LINK_ATTR, url1);
        
    }
    
    private SimpleFeature getFeature(IGeoResource geoResource, Filter filter) {
        try {
            if (geoResource.canResolve(SimpleFeatureStore.class)) {
                final SimpleFeatureStore featureSource = geoResource.resolve(SimpleFeatureStore.class,
                        new NullProgressMonitor());
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
    
    public void testGetDocument() {
        
        assertEquals("Count is not expected.", 2, source.getDocuments(feature).size());
        
        IDocument doc = source.getDocument(feature, FILE_ATTR);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc is not an instance of FileDoc.", (doc instanceof FileDocument));
        FileDocument fileDoc = (FileDocument) doc;
        assertEquals("File is not expected.", file1.getAbsolutePath(), fileDoc.getFile().getAbsolutePath());
        
        doc = source.getDocument(feature, LINK_ATTR);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc is not an instance of UrlDoc.", (doc instanceof URLDocument));
        URLDocument urlDoc = (URLDocument) doc;
        assertEquals("File is not expected.", url1.toString(), urlDoc.getUrl().toString());
        
    }
    
    public void testGetDocuments() {
        assertEquals("Count is not expected.", 2, source.getDocuments(feature).size());
    }
    
    public void testSetClearFile() throws InterruptedException {

        assertEquals("Count is not expected.", 2, source.getDocuments(feature).size());

        source.setFile(feature, FILE_ATTR, file2);
        
        IDocument doc = source.getDocument(feature, FILE_ATTR);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc is not an instance of FileDoc.", (doc instanceof FileDocument));
        FileDocument fileDoc = (FileDocument) doc;
        assertEquals("File is not expected.", file2.getAbsolutePath(), fileDoc.getFile()
                .getAbsolutePath());

        source.clear(feature, FILE_ATTR);
        
        doc = source.getDocument(feature, FILE_ATTR);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc is not an instance of FileDoc.", (doc instanceof FileDocument));
        fileDoc = (FileDocument) doc;
        assertNull("File is not expected.", fileDoc.getFile());
        
        source.setFile(feature, FILE_ATTR, file1);
        
        doc = source.getDocument(feature, FILE_ATTR);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc is not an instance of FileDoc.", (doc instanceof FileDocument));
        fileDoc = (FileDocument) doc;
        assertEquals("File is not expected.", file1.getAbsolutePath(), fileDoc.getFile()
                .getAbsolutePath());
        
    }
    
    public void testSetClearLink() throws InterruptedException {

        assertEquals("Count is not expected.", 2, source.getDocuments(feature).size());

        source.setLink(feature, LINK_ATTR, url2);
        
        IDocument doc = source.getDocument(feature, LINK_ATTR);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc is not an instance of FileDoc.", (doc instanceof URLDocument));
        URLDocument urlDoc = (URLDocument) doc;
        assertEquals("File is not expected.", url2.toString(), urlDoc.getUrl().toString());

        source.clear(feature, LINK_ATTR);
        
        doc = source.getDocument(feature, LINK_ATTR);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc is not an instance of FileDoc.", (doc instanceof URLDocument));
        urlDoc = (URLDocument) doc;
        assertNull("File is not expected.", urlDoc.getUrl());
        
        source.setLink(feature, LINK_ATTR, url1);
        
        doc = source.getDocument(feature, LINK_ATTR);
        assertNotNull("Doc is null.", doc);
        assertTrue("Doc is not an instance of FileDoc.", (doc instanceof URLDocument));
        urlDoc = (URLDocument) doc;
        assertEquals("File is not expected.", url1.toString(), urlDoc.getUrl().toString());
        
    }
    
}
