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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IDocumentSource.DocumentInfo;
import org.locationtech.udig.catalog.internal.shp.ShpGeoResourceImpl;
import org.locationtech.udig.catalog.internal.shp.ShpServiceImpl;
import org.locationtech.udig.document.source.ShpAttachmentSource;
import org.locationtech.udig.document.source.ShpDocPropertyParser;

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
 * Test class for {@link ShpAttachmentSource}.
 * 
 * @author Naz Chan 
 */
@SuppressWarnings("nls")
public class ShpAttachmentSourceTest extends AbstractShpDocTest {

    private ShpAttachmentSource attachSource;
    protected ShpGeoResourceImpl geoResource;
    protected SimpleFeature feature;
    private File attachDir;
    
    protected static final String FEATURE = "australia.1";
    protected static final FeatureIdImpl FEATURE_ID = new FeatureIdImpl(FEATURE);
    
    @Override
    protected void setUpInternal() {
        super.setUpInternal();

        final Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(ShapefileDataStoreFactory.URLP.key, url);
        params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, false);

        final ShpServiceImpl service = new ShpServiceImpl(url, params);
        geoResource = new ShpGeoResourceImpl(service, "");
        final Filter filter = CommonFactoryFinder.getFilterFactory2()
                .id(new FeatureIdImpl(FEATURE));
        feature = getFeature(geoResource, filter);
        
        final ShpDocPropertyParser parser = new ShpDocPropertyParser(url);
        parser.setFeatureDocumentInfos(feature, new ArrayList<DocumentInfo>());
        parser.writeProperties();
        
        attachDir = parser.getFeatureAttachDir(FEATURE);
        attachSource = new ShpAttachmentSource(geoResource);
        
        cleaupAttachDir();
        
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
    
    public void testGetDocuments() {
        
        List<IDocument> docs = attachSource.getDocuments(feature, monitor);
        assertEquals("Count is not expected.", 0, docs.size());
        
    }
    
    @Test
    public void testAddRemove() {
        
        List<IDocument> docs = attachSource.getDocuments(feature, monitor);
        
        attachSource.add(feature, fileDocInfo1, monitor);
        assertEquals("Count is not expected.", 1, docs.size());
        assertTrue("File was not added.", fileExistsInLocalDir(file1));
        
        attachSource.remove(feature, getDoc(docs, fileDocInfo1), monitor);
        assertEquals("Count is not expected.", 0, docs.size());
        assertFalse("File was not removed.", fileExistsInLocalDir(file1));
        
        List<DocumentInfo> inInfos = new ArrayList<DocumentInfo>();
        inInfos.add(fileDocInfo2);
        inInfos.add(webDocInfo2);
        attachSource.add(feature, inInfos, monitor);
        assertEquals("Count is not expected.", 2, docs.size());
        assertTrue("File was not added.", fileExistsInLocalDir(file2));
        
        List<IDocument> inDocs = new ArrayList<IDocument>();
        inDocs.add(getDoc(docs, fileDocInfo2));
        inDocs.add(getDoc(docs, webDocInfo2));
        attachSource.remove(feature, inDocs, monitor);
        assertEquals("Count is not expected.", 0, docs.size());
        assertFalse("File was not removed.", fileExistsInLocalDir(file2));
        
        cleaupAttachDir();
        
    }
    
    @Test
    public void testUpdate() {
        
        List<IDocument> docs = attachSource.getDocuments(feature, monitor);
        assertEquals("Count is not expected.", 0, docs.size());
        
        IDocument doc = attachSource.add(feature, fileDocInfo1, monitor);
        assertNotNull("Doc does not exists.", doc);
        assertTrue("File was not added.", fileExistsInLocalDir(file1));
        
        attachSource.update(feature, doc, fileDocInfo2, monitor);
        
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
