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

import java.io.IOException;
import java.net.URI;

import junit.framework.TestCase;
import net.refractions.udig.catalog.tests.CatalogTests;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.interceptor.ShowViewInterceptor.ViewStyleContent;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.XMLMemento;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.FidFilter;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactoryFinder;

/**
 * Tests {@link ShowViewInterceptor}
 *
 * @author Jesse
 * @since 1.1.0
 */
public class ShowViewInterceptorTest extends TestCase {

    private Map map;
    private FeatureSource featureSource;
    private FidFilter filter;
    private ILayer layer;
    private Feature f;
    private Layer layer2;

    protected void setUp() throws Exception {
        super.setUp();
        map = MapTests.createDefaultMap("type1", 5, true, null); //$NON-NLS-1$
        layer = map.getMapLayers().get(0);
        layer2 = map.getLayerFactory().createLayer(
                CatalogTests.createGeoResource("type2", 3, true)); //$NON-NLS-1$
        map.getLayersInternal().add(layer2);
        featureSource = layer.getResource(FeatureSource.class,
                new NullProgressMonitor());
        f = (Feature) featureSource.getFeatures().iterator().next();
        filter = FilterFactoryFinder.createFilterFactory().createFidFilter(
                f.getID());
    }

    /**
     * Test method for
     * {@link net.refractions.udig.project.interceptor.ShowViewInterceptor#run(net.refractions.udig.project.ILayer, org.geotools.data.FeatureSource)}.
     */
    public void testFilterOnLayerStyleBlackboard() throws Exception {
        layer.getStyleBlackboard().put(ShowViewInterceptor.KEY, filter);

        FeatureCollection features = assertFilter(layer, 1);
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
    public void testFilterOnLayerBlackboard() throws Exception {
        layer.getBlackboard().put(ShowViewInterceptor.KEY, filter);

        FeatureCollection features = assertFilter(layer, 1);
        assertEquals(f, features.iterator().next());
        assertFilter(layer2, 3);

        layer.getStyleBlackboard().clear();
        layer.getBlackboard().clear();

        assertFilter(layer, 5);
        assertFilter(layer2, 3);
    }

    private FeatureCollection assertFilter(ILayer layer2, int expectedFeatures)
            throws IOException {
        FeatureSource fs = layer2.getResource(FeatureSource.class,
                new NullProgressMonitor());
        FeatureCollection features = fs.getFeatures();
        assertEquals(expectedFeatures, features.size());
        return features;
    }

    /**
     * Test method for
     * {@link net.refractions.udig.project.interceptor.ShowViewInterceptor#run(net.refractions.udig.project.ILayer, org.geotools.data.FeatureSource)}.
     */
    public void testQueryOnLayerStyleBlackboard() throws Exception {
        layer.getStyleBlackboard().put(ShowViewInterceptor.KEY,
                new DefaultQuery(f.getFeatureType().getTypeName(), filter));

        FeatureCollection features = assertFilter(layer, 1);
        assertEquals(f, features.iterator().next());
        assertFilter(layer2, 3);

        layer.getStyleBlackboard().clear();
        layer.getBlackboard().clear();

        assertFilter(layer, 5);
        assertFilter(layer2, 3);
    }

    /**
     * Test method for
     * {@link net.refractions.udig.project.interceptor.ShowViewInterceptor#run(net.refractions.udig.project.ILayer, org.geotools.data.FeatureSource)}.
     */
    public void testQueryOnLayerBlackboard() throws Exception {
        layer.getBlackboard().put(ShowViewInterceptor.KEY,
                new DefaultQuery(f.getFeatureType().getTypeName(), filter));

        FeatureCollection features = assertFilter(layer, 1);
        assertEquals(f, features.iterator().next());
        assertFilter(layer2, 3);

        layer.getStyleBlackboard().clear();

        assertFilter(layer, 5);
        assertFilter(layer2, 3);
    }

    public void testGetFeatureStore() throws Exception {
        layer.getStyleBlackboard().put(ShowViewInterceptor.KEY, filter);
        assertNull(layer.getResource(FeatureStore.class,
                new NullProgressMonitor()));
    }

    public void testStyleContentTestNulls() throws Exception {
        ViewStyleContent content = new ViewStyleContent();
        XMLMemento memento = XMLMemento.createWriteRoot("root");
        DefaultQuery start = new DefaultQuery();
        content.save(memento, start);
        Query loaded = (Query) content.load(memento);
        assertEquals(start, loaded);
    }

    public void testStyleContentAllNoneFilters() throws Exception {
        ViewStyleContent content = new ViewStyleContent();
        XMLMemento memento = XMLMemento.createWriteRoot("root");
        DefaultQuery start = new DefaultQuery("Feature", Filter.ALL);
        content.save(memento, start);
        Query loaded = (Query) content.load(memento);
        assertEquals(start, loaded);

        start = new DefaultQuery("Feature", Filter.NONE);
        content.save(memento, start);
        loaded = (Query) content.load(memento);
        assertEquals(start, loaded);
    }

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
