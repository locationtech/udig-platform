package net.refractions.udig.tools.edit.behaviour;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.tests.CatalogTests;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import net.refractions.udig.tools.edit.support.TestHandler;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class WithinLegalLayerBoundsBehaviourTest extends AbstractProjectUITestCase {

    private TestHandler handler;
    private Map map;
    protected void setUp() throws Exception {
        super.setUp();
        handler=new TestHandler();
        map=(Map) handler.getContext().getMap();
        GeometryFactory fac=new GeometryFactory();
        Point geom=fac.createPoint(new Coordinate(-564121,-1632497));
        SimpleFeature[] feature = UDIGTestUtil.createTestFeatures("test", new Point[]{geom}, new String[]{"name"}, CRS.decode("EPSG:2065")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Layer layer = map.getLayerFactory().createLayer(CatalogTests.createGeoResource(feature, true));
        map.getLayersInternal().add( layer );
        map.getViewportModelInternal().setCRS(DefaultGeographicCRS.WGS84);
        map.getViewportModelInternal().zoomToBox(layer.getBounds(null, map.getViewportModel().getCRS()));
        
        ApplicationGIS.openMap(map);
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                return ApplicationGIS.getActiveMap()!=null;
            }
            
        }, false);
        map.getEditManagerInternal().setSelectedLayer(layer);
    }

    public void testLegal() throws Exception {
        handler.setCurrentState(EditState.ILLEGAL);
        WithinLegalLayerBoundsBehaviour behav=new WithinLegalLayerBoundsBehaviour();
        MapMouseEvent e=new MapMouseEvent(map.getRenderManager().getMapDisplay(), 0,0, 0,0,0);
        
        
        assertNull( behav.isEnabled(handler, e, EventType.MOVED) );
    }
    public void testIllegal() throws Exception {
        map.getViewportModelInternal().setBounds(-300, -250, -180, -140);
        
        System.out.println(map.getViewportModel().getBounds());
        
        WithinLegalLayerBoundsBehaviour behav=new WithinLegalLayerBoundsBehaviour();
        MapMouseEvent e=new MapMouseEvent(map.getRenderManager().getMapDisplay(), 0,0, 0,0,0);
        behav.isEnabled(handler, e, EventType.MOVED);
        
        assertNotNull( behav.isEnabled(handler, e, EventType.MOVED));
    }

}
