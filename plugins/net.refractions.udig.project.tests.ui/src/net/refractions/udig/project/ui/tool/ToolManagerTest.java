package net.refractions.udig.project.ui.tool;

import java.awt.Dimension;
import java.io.IOException;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.AdaptingFilter;
import net.refractions.udig.project.ui.internal.ApplicationGISInternal;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.Feature;
import org.geotools.filter.Filter;

public class ToolManagerTest extends AbstractProjectUITestCase {

    private Map map;
    private Layer firstLayer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        map = MapTests.createDefaultMap("test", 10, true, new Dimension(500, 500)); //$NON-NLS-1$
        firstLayer=map.getLayersInternal().get(0);
    }

    public void testCUTPASTEFeatures() throws Exception {
        ApplicationGIS.openMap(map);
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                return ApplicationGIS.getActiveMap() != null && ApplicationGIS.getActiveMap()==map;
            }

        }, true);

        Feature[] features = UDIGTestUtil.createDefaultTestFeatures("new", 1); //$NON-NLS-1$
        IGeoResource resource = MapTests.createGeoResource(features, true);
        Layer layer = map.getLayerFactory().createLayer(resource);
        map.getLayersInternal().add(layer);


        IAction copyAction = ApplicationGIS.getToolManager().getCOPYAction(ApplicationGISInternal.getActiveEditor());
        IAction pasteAction = ApplicationGIS.getToolManager().getPASTEAction(ApplicationGISInternal.getActiveEditor());

        map.getEditManagerInternal().setSelectedLayer(firstLayer);

        firstLayer.setFilter(Filter.NONE);

        StructuredSelection structuredSelection = new StructuredSelection(new AdaptingFilter(firstLayer.getFilter(), firstLayer));
        ApplicationGISInternal.getActiveEditor().getEditorSite().getSelectionProvider().setSelection(structuredSelection) ;
        Event event = new Event();
        event.display=Display.getCurrent();
        copyAction.runWithEvent(event);

        ApplicationGISInternal.getActiveEditor().getEditorSite().getSelectionProvider().setSelection(new StructuredSelection(layer));

        pasteAction.runWithEvent(event);

        final FeatureSource fs = layer.getResource(FeatureSource.class, new NullProgressMonitor());

        UDIGTestUtil.inDisplayThreadWait( 4000, new WaitCondition(){

            public boolean isTrue() {
                try {
                    return fs.getCount(Query.ALL)==11;
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
            }

        }, true);
        assertEquals(11, fs.getCount(Query.ALL));

    }

    public void testDeleteAction() throws Exception {
        Feature[] features = UDIGTestUtil.createDefaultTestFeatures("new", 15); //$NON-NLS-1$
        IGeoResource resource = MapTests.createGeoResource(features, true);
        Layer layer = map.getLayerFactory().createLayer(resource);
        map.getLayersInternal().add(layer);


    }
}
