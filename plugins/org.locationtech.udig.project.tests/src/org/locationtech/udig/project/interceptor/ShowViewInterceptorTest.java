/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URI;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.XMLMemento;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.util.factory.GeoTools;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.udig.catalog.tests.CatalogTests;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.interceptor.CacheInterceptor.ViewStyleContent;
import org.locationtech.udig.project.internal.interceptor.ShowViewInterceptor;
import org.locationtech.udig.project.tests.support.MapTests;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;

/**
 * Tests {@link ShowViewInterceptor}
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ShowViewInterceptorTest {

    private Map map;
    private  SimpleFeatureSource featureSource;
    private Id filter;
    private ILayer layer;
    private SimpleFeature f;
    private Layer layer2;

    @Before
    public void setUp() throws Exception {
        map = MapTests.createDefaultMap("type1", 5, true, null); //$NON-NLS-1$
        layer = map.getMapLayers().get(0);
        layer2 = map.getLayerFactory().createLayer(
                CatalogTests.createGeoResource("type2", 3, true)); //$NON-NLS-1$
        map.getLayersInternal().add(layer2);
        featureSource = layer.getResource(SimpleFeatureSource.class,new NullProgressMonitor());
        f = (SimpleFeature) featureSource.getFeatures().features().next();
        FilterFactory filterFactory = CommonFactoryFinder
                .getFilterFactory(GeoTools.getDefaultHints());
        filter = filterFactory.id(FeatureUtils.stringToId(filterFactory, f
                .getID()));
    }

    /**
     * Test method for
     * {@link org.locationtech.udig.project.interceptor.ShowViewInterceptor#run(org.locationtech.udig.project.ILayer, org.geotools.data.FeatureSource)}.
     */
    @Ignore
    @Test
    public void testFilterOnLayerStyleBlackboard() throws Exception {
        layer.getStyleBlackboard().put(ShowViewInterceptor.KEY, filter);

        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = assertFilter(layer, 1);
        assertEquals(f, features.features().next());
        assertFilter(layer2, 3);

        layer.getStyleBlackboard().clear();

        assertFilter(layer, 5);
        assertFilter(layer2, 3);
    }

    /**
     * Test method for
     * {@link org.locationtech.udig.project.interceptor.ShowViewInterceptor#run(org.locationtech.udig.project.ILayer, org.geotools.data.FeatureSource)}.
     */
    @Ignore
    @Test
    public void testFilterOnLayerBlackboard() throws Exception {
        layer.getBlackboard().put(ShowViewInterceptor.KEY, filter);

        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = assertFilter(layer, 1);
        assertEquals(f, features.features().next());
        assertFilter(layer2, 3);

        layer.getStyleBlackboard().clear();
        layer.getBlackboard().clear();

        assertFilter(layer, 5);
        assertFilter(layer2, 3);
    }

    private FeatureCollection<SimpleFeatureType, SimpleFeature> assertFilter(ILayer layer2, int expectedFeatures)
            throws IOException {
        FeatureSource<SimpleFeatureType, SimpleFeature> fs = layer2.getResource(FeatureSource.class,
                new NullProgressMonitor());
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = fs.getFeatures();
        assertEquals(expectedFeatures, features.size());
        return features;
    }

    /**
     * Test method for
     * {@link org.locationtech.udig.project.interceptor.ShowViewInterceptor#run(org.locationtech.udig.project.ILayer, org.geotools.data.FeatureSource)}.
     */
    @Ignore
    @Test
    public void testQueryOnLayerStyleBlackboard() throws Exception {
        layer.getStyleBlackboard().put(ShowViewInterceptor.KEY,
                new Query(f.getFeatureType().getTypeName(), filter));

        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = assertFilter(layer, 1);
        assertEquals(f, features.features().next());
        assertFilter(layer2, 3);
        
        layer.getStyleBlackboard().clear();

        assertFilter(layer, 5);
        assertFilter(layer2, 3);
    }

    /**
     * Test method for
     * {@link org.locationtech.udig.project.interceptor.ShowViewInterceptor#run(org.locationtech.udig.project.ILayer, org.geotools.data.FeatureSource)}.
     */
    @Ignore
    @Test
    public void testQueryOnLayerBlackboard() throws Exception {
        layer.getBlackboard().put(ShowViewInterceptor.KEY,
                new Query(f.getFeatureType().getTypeName(), filter));

        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = assertFilter(layer, 1);
        assertEquals(f, features.features().next());
        assertFilter(layer2, 3);

        layer.getStyleBlackboard().clear();
        layer.getBlackboard().clear();

        assertFilter(layer, 5);
        assertFilter(layer2, 3);
    }

    @Test
    public void testGetFeatureStore() throws Exception {
        layer.getStyleBlackboard().put(ShowViewInterceptor.KEY, filter);
        assertNull(layer.getResource(FeatureStore.class,
                new NullProgressMonitor()));
    }

    @Test
    public void testStyleContentTestNulls() throws Exception {
        ViewStyleContent content = new ViewStyleContent();
        XMLMemento memento = XMLMemento.createWriteRoot("root");
        Query start = new Query();
        content.save(memento, start);
        Query loaded = (Query) content.load(memento);
        assertEquals(start, loaded);
    }

    @Test
    public void testStyleContentAllNoneFilters() throws Exception {
        ViewStyleContent content = new ViewStyleContent();
        XMLMemento memento = XMLMemento.createWriteRoot("root");
        Query start = new Query("Feature", Filter.EXCLUDE);
        content.save(memento, start);
        Query loaded = (Query) content.load(memento);
        assertEquals(start, loaded);

        start = new Query("Feature", Filter.INCLUDE);
        content.save(memento, start);
        loaded = (Query) content.load(memento);
        assertEquals(start, loaded);
    }

    @Ignore
    @Test
    public void testStyleContentFullQuery() throws Exception {
        XMLMemento memento = XMLMemento.createWriteRoot("root");
        Query start = new Query("type", new URI(
                "http://localhost"), filter, 27, new String[] { "att" },
                "handle");
        ViewStyleContent content = new ViewStyleContent();
        content.save(memento, start);
        Query loaded = (Query) content.load(memento);
        assertEquals(start, loaded);
    }

}
