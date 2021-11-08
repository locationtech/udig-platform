/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.interceptor;

import java.awt.Color;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.gml.GMLFilterDocument;
import org.geotools.gml.GMLFilterGeometry;
import org.geotools.xml.filter.FilterFilter;
import org.geotools.xml.filter.FilterTransformer;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.internal.ui.FilterTextTransfer.SimpleFilterHandler;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IResourceInterceptor;
import org.locationtech.udig.project.StyleContent;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * If a cache flag is set in the layer style blackboard this interceptor will return a
 * CacheFeatureSource wrapper.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class CacheInterceptor
        implements IResourceInterceptor<FeatureSource<SimpleFeatureType, SimpleFeature>> {

    /**
     * The key that is checked to see if a filter is on the Map Blackboard or the Layer Properties.
     */
    public static final String KEY = "org.locationtech.udig.style.cache"; //$NON-NLS-1$

    @Override
    public FeatureSource<SimpleFeatureType, SimpleFeature> run(ILayer layer,
            FeatureSource<SimpleFeatureType, SimpleFeature> resource,
            Class<? super FeatureSource<SimpleFeatureType, SimpleFeature>> requestedType) {

        Object prop = layer.getStyleBlackboard().get(KEY);
        if (prop == null) {
            prop = layer.getBlackboard().get(KEY);
        }
        if (prop instanceof Boolean && Boolean.TRUE.equals(prop)) {
            try {
                CachingFeatureSource cachingFeatureSource = (CachingFeatureSource) layer
                        .getBlackboard().get("cache"); //$NON-NLS-1$
                if (cachingFeatureSource != null) {
                    return cachingFeatureSource;
                }
                cachingFeatureSource = new CachingFeatureSource(resource);
                layer.getBlackboard().put("cache", cachingFeatureSource); //$NON-NLS-1$
                return cachingFeatureSource;
            } catch (IOException e) {
                if (ProjectPlugin.getPlugin().isDebugging()) {
                    ProjectPlugin.getPlugin()
                            .log("Unable to cache " + resource + ":" + e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
                    e.printStackTrace();
                }
            }
        }
        return resource; // no wrapper needed
    }

    private static Query createQuery(Filter filter, CoordinateReferenceSystem crs,
            CoordinateReferenceSystem reproject, String handle, Integer maxFeature, URI namespace,
            String[] propertyNames, String typeName) {
        Query query = new Query();
        if (namespace != null) {
            query = new Query(typeName, namespace, filter, maxFeature, propertyNames, handle);
        }
        if (crs != null) {
            query.setCoordinateSystem(crs);
        }
        if (reproject != null) {
            query.setCoordinateSystemReproject(reproject);
        }
        if (filter != null) {
            query.setFilter(filter);
        }
        if (handle != null) {
            query.setHandle(handle);
        }
        if (maxFeature != null) {
            query.setMaxFeatures(maxFeature);
        }
        if (propertyNames != null) {
            query.setPropertyNames(propertyNames);
        }
        if (typeName != null) {
            query.setTypeName(typeName);
        }
        return query;
    }

    /**
     * Persists Query and Filters saved on the style blackboard.
     *
     * @author Jesse
     */
    public static class ViewStyleContent extends StyleContent {

        private static final String CRS = "CRS"; //$NON-NLS-1$

        private static final String REPOJECT = "REPOJECT"; //$NON-NLS-1$

        private static final String HANDLE = "HANDLE"; //$NON-NLS-1$

        private static final String MAX_FEATURES = "MAX_FEATURES"; //$NON-NLS-1$

        private static final String NAMESPACE = "NAMESPACE"; //$NON-NLS-1$

        private static final String TYPENAME = "TYPENAME"; //$NON-NLS-1$

        private static final String PROPERTY_NAMES = "PROPERTY_NAMES"; //$NON-NLS-1$

        public ViewStyleContent() {
            super(KEY);
        }

        @Override
        public Object createDefaultStyle(IGeoResource resource, Color colour,
                IProgressMonitor monitor) throws IOException {
            return null;
        }

        @Override
        public Class<? extends Object> getStyleClass() {
            return Query.class;
        }

        @Override
        public Object load(IMemento memento) {
            Filter filter;
            String textData = memento.getTextData();
            if (textData == null || textData.trim().length() == 0) {
                filter = Filter.INCLUDE;
            } else {
                filter = readFilter(decode(textData));
            }
            CoordinateReferenceSystem crs = readCRS(memento.getString(CRS));
            CoordinateReferenceSystem reproject = readCRS(memento.getString(REPOJECT));
            String handle = decode(memento.getString(HANDLE));
            Integer maxFeature = memento.getInteger(MAX_FEATURES);
            URI namespace;
            try {
                String uriString = decode(memento.getString(NAMESPACE));
                if (uriString != null) {
                    namespace = new URI(uriString);
                } else {
                    namespace = null;
                }
            } catch (URISyntaxException e) {
                namespace = null;
            }
            String propNameString = decode(memento.getString(PROPERTY_NAMES));
            String[] propertyNames;
            if (propNameString != null) {
                propertyNames = propNameString.split(","); //$NON-NLS-1$
            } else {
                propertyNames = Query.ALL_NAMES;
            }
            String typeName = decode(memento.getString(TYPENAME));

            Query query = createQuery(filter, crs, reproject, handle, maxFeature, namespace,
                    propertyNames, typeName);
            return query;
        }

        private CoordinateReferenceSystem readCRS(String string) {
            try {
                return org.geotools.referencing.CRS.parseWKT(decode(string));
            } catch (Exception e) {
                return null;
            }
        }

        private Filter readFilter(String textData) {
            if ("all".equals(textData)) { //$NON-NLS-1$
                return Filter.EXCLUDE;
            }
            InputSource input = new InputSource(new StringReader(textData));
            SimpleFilterHandler simpleFilterHandler = new SimpleFilterHandler();
            FilterFilter filterFilter = new FilterFilter(simpleFilterHandler, null);
            GMLFilterGeometry filterGeometry = new GMLFilterGeometry(filterFilter);
            GMLFilterDocument filterDocument = new GMLFilterDocument(filterGeometry);

            try {

                // parse XML

                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                XMLReader reader = parser.getXMLReader();

                reader.setContentHandler(filterDocument);
                reader.parse(input);

            } catch (Exception e) {
                return Filter.INCLUDE;
            }

            return simpleFilterHandler.getFilter();
        }

        @Override
        public Object load(URL url, IProgressMonitor monitor) throws IOException {
            return null;
        }

        @Override
        public void save(IMemento memento, Object value) {
            Query viewRestriction;
            if (value instanceof Filter) {
                Filter filter = (Filter) value;
                viewRestriction = new Query("Feature", filter); //$NON-NLS-1$
            } else {
                viewRestriction = (Query) value;
            }
            Filter filter;
            CoordinateReferenceSystem crs;
            CoordinateReferenceSystem reproject;
            String handle;
            int maxFeature;
            URI namespace;
            String[] propertyNames;
            String typeName;
            filter = viewRestriction.getFilter();
            crs = viewRestriction.getCoordinateSystem();
            reproject = viewRestriction.getCoordinateSystemReproject();
            handle = viewRestriction.getHandle();
            maxFeature = viewRestriction.getMaxFeatures();
            namespace = viewRestriction.getNamespace();
            propertyNames = viewRestriction.getPropertyNames();
            typeName = viewRestriction.getTypeName();

            StringBuilder propertyNamesString = new StringBuilder();
            if (propertyNames != null) {
                for (String string : propertyNames) {
                    propertyNamesString.append(string);
                    propertyNamesString.append(',');
                }
            }

            if (filter != null) {
                FilterTransformer transformer = new FilterTransformer();

                try {
                    if (filter == Filter.EXCLUDE) {
                        memento.putTextData("all"); //$NON-NLS-1$
                    } else {
                        memento.putTextData(encode(transformer.transform(filter)));
                    }
                } catch (TransformerException e) {
                    throw new RuntimeException(
                            "Unable to convert filter to string I couldn't save the view query"); //$NON-NLS-1$
                }
            }
            if (crs != null) {
                memento.putString(CRS, encode(crs.toWKT()));
            }
            if (reproject != null) {
                memento.putString(REPOJECT, encode(reproject.toWKT()));
            }
            if (handle != null) {
                memento.putString(HANDLE, encode(handle));
            }
            if (maxFeature != Integer.MAX_VALUE) {
                memento.putInteger(MAX_FEATURES, Integer.valueOf(maxFeature));
            }
            if (namespace != null) {
                memento.putString(NAMESPACE, encode(namespace.toString()));
            }
            if (typeName != null) {
                memento.putString(TYPENAME, encode(typeName));
            }
            if (propertyNames != null) {
                memento.putString(PROPERTY_NAMES, encode(propertyNamesString.toString()));
            }

        }

        private String encode(String toEncode) {
            if (toEncode == null) {
                return null;
            }
            try {
                return URLEncoder.encode(toEncode, "UTF-8"); //$NON-NLS-1$
            } catch (UnsupportedEncodingException e) {
                return toEncode;
            }
        }

        private String decode(String toDecode) {
            if (toDecode == null) {
                return null;
            }
            try {
                return URLDecoder.decode(toDecode, "UTF-8"); //$NON-NLS-1$
            } catch (UnsupportedEncodingException e) {
                return toDecode;
            }

        }

    }

}
