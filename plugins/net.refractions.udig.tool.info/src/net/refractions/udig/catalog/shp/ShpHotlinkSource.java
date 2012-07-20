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
package net.refractions.udig.catalog.shp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.DocumentFolder;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IDocument.TYPE;
import net.refractions.udig.catalog.IDocumentFolder;
import net.refractions.udig.catalog.IHotlinkSource;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;
import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.provider.FIDFeatureProvider;
import net.refractions.udig.project.internal.commands.edit.SetAttributeCommand;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.tool.info.InfoPlugin;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

/**
 * Provides shapefile hotlink support. This should only be available when
 * we can determine what attribute as storing hotlinks.
 * <p>
 * Provides an implementation of getters and setters to the document linked attributes and their values.
 * 
 * @author Naz Chan
 */
public class ShpHotlinkSource extends AbstractShpDocumentSource implements IHotlinkSource {

    private static final String defaultLabel = "Feature Documents"; //$NON-NLS-1$
    private static final String namedLabelFormat = defaultLabel + " (%s)"; //$NON-NLS-1$
    
    private ShpServiceImpl service;
    
    public ShpHotlinkSource(ShpGeoResourceImpl geoResource) {
        super(geoResource, defaultLabel);
        this.service = geoResource.service();
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
    
    /**
     * Sets the attribute value of the feature.
     * 
     * @param fid
     * @param attributeName
     * @param obj
     */
    private void set(final FeatureId fid, final String attributeName, final Object obj) {
        if (geoResource.canResolve(SimpleFeatureStore.class)) {
            final IMap map = ApplicationGIS.getActiveMap();
            if (map != null) {
                setOnMap(map, fid, attributeName, obj);
            } else {
                setOnGeoResource(fid, attributeName, obj);
            }
        }
    }
    
    /**
     * Sets the attribute value of the feature given that the layer in on the map.
     * 
     * @param map
     * @param fid
     * @param attributeName
     * @param obj
     */
    private void setOnMap(final IMap map, final FeatureId fid, final String attributeName,
            final Object obj) {
        
        IBlockingProvider<ILayer> layerProvider = new IBlockingProvider<ILayer>() {
            @Override
            public ILayer get(IProgressMonitor monitor, Object... params) throws IOException {
                for (ILayer layer : map.getMapLayers()) {
                    if (layer.getGeoResource().getID() == geoResource.getID()) {
                        return layer;
                    }
                }
                return null;
            }
        };
        IBlockingProvider<SimpleFeature> featureProvider = new FIDFeatureProvider(fid.getID(),
                layerProvider);
        
        map.sendCommandASync(new SetAttributeCommand(featureProvider, layerProvider,
                attributeName, obj));
    }
    
    /**
     * Sets the attribute value of the feature directly to the geoResource.
     * 
     * @param fid
     * @param attributeName
     * @param obj
     */
    private void setOnGeoResource(final FeatureId fid, final String attributeName, final Object obj) {

        IRunnableWithProgress runner = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException,
                    InterruptedException {
                try {
                    SimpleFeatureStore featureStore = geoResource.resolve(SimpleFeatureStore.class,
                            new NullProgressMonitor());
                    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
                    Filter filter = ff.id(fid);
                    featureStore.modifyFeatures(attributeName, obj, filter);
                } catch (IOException e) {
                    InfoPlugin.log("Shapefile hotlink" + fid + " not set:" + e, e);
                }
            }
        };
        // we should indicate that modification is not supported?
        // or update the shapefile in the background?
        PlatformGIS.runInProgressDialog("Hotlink", true, runner, true);

    }
    
    /**
     * Gets the feature from the geoResource
     * 
     * @param fid
     * @return feature
     */
    private SimpleFeature getFeature(FeatureId fid) {
        try {
            
            final SimpleFeatureStore featureSource = geoResource.resolve(SimpleFeatureStore.class,
                    new NullProgressMonitor());
            final Filter fidFilter = CommonFactoryFinder.getFilterFactory2().id(fid);
            final SimpleFeatureCollection featureCollection = featureSource.getFeatures(fidFilter);
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
            InfoPlugin.log("Shapefile hotlink" + fid + " unavailable:" + e, e);
        }
        return null;
    }
    
}
