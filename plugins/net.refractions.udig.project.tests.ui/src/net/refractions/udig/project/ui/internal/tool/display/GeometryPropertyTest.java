package net.refractions.udig.project.ui.internal.tool.display;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.tests.CatalogTests;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.operations.IOpFilterListener;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class GeometryPropertyTest {

    private ILayer layer;
    private GeometryProperty prop;

    @Before
    public void setUp() throws Exception {
        Map map = MapTests.createDefaultMap("GeomPropertyTest", 2, true, null); //$NON-NLS-1$
        layer=map.getMapLayers().get(0);
        prop=new GeometryProperty();
    }

    @Test
    public void testIsTrue() {
        assertTrue(prop.isTrue(layer, "Polygon")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "LineString")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "line")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "LinearRing")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "Geometry")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "Point")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "MultiPoint")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "MultiPolygon")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "MultiLineString")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "MultiLine")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "geometrycollection")); //$NON-NLS-1$
        assertFalse(prop.isTrue(layer, "java.lang.Integer")); //$NON-NLS-1$
        assertFalse(prop.isTrue(layer, "miss spelling")); //$NON-NLS-1$
    }

    @Ignore
    @Test
    public void testResolveChange() throws Exception {
        final int[] changed=new int[1];
        changed[0]=0;
        IOpFilterListener l = new IOpFilterListener(){

            public void notifyChange( Object c ) {
                changed[0]++;
            }
            
        };
        prop.addListener(l);
        // to test that a listener is only added once
        prop.addListener(l);
        
        URL id = layer.getID();
        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
        IGeoResource resource = localCatalog.getById(IGeoResource.class, new ID(id), new NullProgressMonitor());
        IService parent = resource.service(new NullProgressMonitor());
        localCatalog.replace(parent.getID(), parent);
        
        assertEquals(0, changed[0]);
        
        assertTrue(prop.isTrue(layer, "Polygon")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "Polygon")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "Polygon")); //$NON-NLS-1$
        assertTrue(prop.isTrue(layer, "Polygon")); //$NON-NLS-1$

        localCatalog.replace(parent.getID(), parent);
        
        assertEquals(1, changed[0]);
        
        parent=CatalogTests.createResource(null, "ResolveTo").service(new NullProgressMonitor()); //$NON-NLS-1$

        localCatalog.replace(parent.getID(), parent);
        changed[0]=0;
        assertEquals(0, changed[0]);
    }
}
