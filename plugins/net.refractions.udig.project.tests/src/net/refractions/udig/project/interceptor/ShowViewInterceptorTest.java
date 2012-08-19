/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.project.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URI;

import net.refractions.udig.catalog.tests.CatalogTests;
import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.interceptor.CacheInterceptor.ViewStyleContent;
import net.refractions.udig.project.internal.interceptor.ShowViewInterceptor;
import net.refractions.udig.project.tests.support.MapTests;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.XMLMemento;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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
    private  FeatureSource<SimpleFeatureType, SimpleFeature> featureSource;
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
        featureSource = layer.getResource(FeatureSource.class,
                new NullProgressMonitor());
        f = (SimpleFeature) featureSource.getFeatures().iterator().next();
        FilterFactory filterFactory = CommonFactoryFinder
                .getFilterFactory(GeoTools.getDefaultHints());
        filter = filterFactory.id(FeatureUtils.stringToId(filterFactory, f
                .getID()));
    }

    /**
     * Test method for
     * {@link net.refractions.udig.project.interceptor.ShowViewInterceptor#run(net.refractions.udig.project.ILayer, org.geotools.data.FeatureSource)}.
     */
    @Ignore
    @Test
    public void testFilterOnLayerStyleBlackboard() throws Exception {
        layer.getStyleBlackboard().put(ShowViewInterceptor.KEY, filter);

        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = assertFilter(layer, 1);
        assertEquals(f, features.iterator().next());
        assertFilter(layer2, 3);

        layer.getStyleBlackboard().clear();

        assertFilter(layer, 5);
        assertFilter(layer2, 3);
    }

    /**
     * Test method for
     * {@link net.refractions.udig.project.interceptor.ShowViewInterceptor#run(net.refractions.udig.project.ILayer, org.geotools.data.FeatureSource)}.
     */
    @Ignore
    @Test
    public void testFilterOnLayerBlackboard() throws Exception {
        layer.getBlackboard().put(ShowViewInterceptor.KEY, filter);

        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = assertFilter(layer, 1);
        assertEquals(f, features.iterator().next());
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
     * {@link net.refractions.udig.project.interceptor.ShowViewInterceptor#run(net.refractions.udig.project.ILayer, org.geotools.data.FeatureSource)}.
     */
    @Ignore
    @Test
    public void testQueryOnLayerStyleBlackboard() throws Exception {
        layer.getStyleBlackboard().put(ShowViewInterceptor.KEY,
                new DefaultQuery(f.getFeatureType().getTypeName(), filter));

        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = assertFilter(layer, 1);
        assertEquals(f, features.iterator().next());
        assertFilter(layer2, 3);
        
        layer.getStyleBlackboard().clear();

        assertFilter(layer, 5);
        assertFilter(layer2, 3);
    }

    /**
     * Test method for
     * {@link net.refractions.udig.project.interceptor.ShowViewInterceptor#run(net.refractions.udig.project.ILayer, org.geotools.data.FeatureSource)}.
     */
    @Ignore
    @Test
    public void testQueryOnLayerBlackboard() throws Exception {
        layer.getBlackboard().put(ShowViewInterceptor.KEY,
                new DefaultQuery(f.getFeatureType().getTypeName(), filter));

        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = assertFilter(layer, 1);
        assertEquals(f, features.iterator().next());
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
        DefaultQuery start = new DefaultQuery();
        content.save(memento, start);
        Query loaded = (Query) content.load(memento);
        assertEquals(start, loaded);
    }

    @Test
    public void testStyleContentAllNoneFilters() throws Exception {
        ViewStyleContent content = new ViewStyleContent();
        XMLMemento memento = XMLMemento.createWriteRoot("root");
        DefaultQuery start = new DefaultQuery("Feature", Filter.EXCLUDE);
        content.save(memento, start);
        Query loaded = (Query) content.load(memento);
        assertEquals(start, loaded);

        start = new DefaultQuery("Feature", Filter.INCLUDE);
        content.save(memento, start);
        loaded = (Query) content.load(memento);
        assertEquals(start, loaded);
    }

    @Ignore
    @Test
    public void testStyleContentFullQuery() throws Exception {
        XMLMemento memento = XMLMemento.createWriteRoot("root");
        DefaultQuery start = new DefaultQuery("type", new URI(
                "http://localhost"), filter, 27, new String[] { "att" },
                "handle");
        ViewStyleContent content = new ViewStyleContent();
        content.save(memento, start);
        Query loaded = (Query) content.load(memento);
        assertEquals(start, loaded);
    }

}
