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
package net.refractions.udig.catalog.internal.shp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.DocumentFolder;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IDocument.TYPE;
import net.refractions.udig.catalog.IDocumentFolder;
import net.refractions.udig.catalog.IHotlinkSource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

/**
 * This is the shapefile hotlink document source implementation. This implements getters and setters
 * to the document linked attributes and their values.
 * 
 * @author Naz Chan
 */
public class ShpHotlinkSource extends AbstractShpDocumentSource implements IHotlinkSource {

    private static final String defaultLabel = "Feature Documents"; //$NON-NLS-1$
    private static final String namedLabelFormat = defaultLabel + " (%s)"; //$NON-NLS-1$
    
    private ShpServiceImpl service;

    public ShpHotlinkSource(URL url) {
        super(url, defaultLabel);
    }
    
    @Override
    public IDocumentFolder getDocumentsInFolder(String folderName, FeatureId fid) {
        if (folder instanceof DocumentFolder) {
            final DocumentFolder docfolder = (DocumentFolder) folder;
            docfolder.setName(String.format(namedLabelFormat, folderName));    
        }
        return getDocumentsInFolder(fid);
    }
    
    @Override
    public IDocumentFolder getDocumentsInFolder(FeatureId fid) {
        final SimpleFeature feature = getFeature(fid);
        folder.setDocuments(propParser.getFeatureLinks(feature));
        return folder;
    }
    
    @Override
    public List<IDocument> getDocuments(FeatureId fid) {
        final SimpleFeature feature = getFeature(fid);
        folder.setDocuments(propParser.getFeatureLinks(feature));
        return folder.getDocuments();
    }

    @Override
    public IDocument getDocument(FeatureId fid, String attributeName) {
        final SimpleFeature feature = getFeature(fid);
        final String spec = (String) feature.getAttribute(attributeName);
        final TYPE type = propParser.getFeatureLinkInfo(attributeName).getType();
        return docFactory.create(url, spec, type);
    }

    @Override
    public IDocument setFile(FeatureId fid, String attributeName, File file) {
        set(fid, attributeName, propParser.getFileLinkValue(file));
        return null;
    }
    
    @Override
    public IDocument setLink(FeatureId fid, String attributeName, URL url) {
        set(fid, attributeName, propParser.getUrlLinkValue(url));
        return null;
    }

    @Override
    public void remove(FeatureId fid, String attributeName) {
        set(fid, attributeName, null);
    }
    
    private void set(FeatureId fid, String attributeName, Object obj) {
        final SimpleFeature feature = getFeature(fid);
        // feature.setAttribute(attributeName, obj);
    }
    
    private ShpServiceImpl getService() {
        if (service == null) {
            final IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
            final List<IService> services = factory.createService(url);
            service = (ShpServiceImpl) services.get(0);    
        }
        return service;
    }
    
    private SimpleFeature getFeature(FeatureId fid) {
        try {
            final ShapefileDataStore dataStore = getService().getDS(new NullProgressMonitor());
            final FilterFactory2 ff = (FilterFactory2) CommonFactoryFinder.getFilterFactory(null);
            final Filter fidFilter = ff.id(fid);
            final SimpleFeatureCollection featureCollection = dataStore.getFeatureSource().getFeatures(fidFilter);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
