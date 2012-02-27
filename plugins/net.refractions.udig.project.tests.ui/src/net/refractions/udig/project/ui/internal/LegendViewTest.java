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
package net.refractions.udig.project.ui.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IRepository;
import net.refractions.udig.project.Interaction;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.AddLayersCommand;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.ui.PlatformUI;

/**
 * Test class for LegendView 
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
@SuppressWarnings("nls")
public class LegendViewTest extends AbstractProjectUITestCase {

    private static final String GRID_URL = "mapgraphic:/localhost/mapgraphic#grid"; //$NON-NLS-1$
    private static final ID GRID_ID = new ID(GRID_URL, null);
    
    private LegendView view;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        view  = (LegendView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(LegendView.ID);
    }
    
    public void testHandlers() throws Exception {

        assertNotNull("View should not be null.", view);

        assertNotNull("Send to back should not be null.", view.getBackAction());
        assertNotNull("Send to front should not be null.", view.getFrontAction());
        
        LegendViewGridHandler gridHandler = view.getGridHandler();
        assertNotNull("Grid handler should not be null.", gridHandler);
        assertNotNull("Grid toggle action should not be null.", gridHandler.getGridAction());
        
        LegendViewFiltersHandler filtersHandler = view.getFiltersHandler();
        assertNotNull("Filter handler should not be null.", filtersHandler);
        assertNotNull("Filter map graphic action should not be null.", filtersHandler.getToggleBgAction());
        assertNotNull("Filter background layer action should not be null.", filtersHandler.getToggleMgAction());
        
        assertNotNull("Filter map graphic filter should not be null.", filtersHandler.getMgLayerFilter());
        assertNotNull("Filter background layer filter should not be null.", filtersHandler.getBgLayerFilter());
        assertEquals(2, filtersHandler.getFilters().length);
        
        final CheckboxTreeViewer viewer = (CheckboxTreeViewer) LegendView.getViewer();
        assertEquals(2, viewer.getFilters().length);
        
        
        Map map = ProjectFactory.eINSTANCE.createMap(ProjectPlugin.getPlugin().getProjectRegistry()
                .getDefaultProject(), "LegendView Map", new ArrayList<Layer>());
        ApplicationGIS.openMap(map, true);
        
        /*
        //TODO - Update to acquire map graphic resource
        map.sendCommandSync(new AddLayersCommand(Collections.singletonList(LegendViewUtils.getGridMapGraphic())));
        
        List<Layer> layersList = map.getLayersInternal();
        Layer gridLayer = layersList.get(0);
        assertNotNull("Grid layer must not be null.", gridLayer);
        
        gridHandler.testToggleGrid(true);
        assertTrue("Grid layer must be visible.", gridLayer.isVisible());
        gridHandler.testToggleGrid(false);
        assertFalse("Grid layer must not be visible.", gridLayer.isVisible());
        */
        
        final Layer layer = ProjectFactory.eINSTANCE.createLayer();
        map.sendCommandSync(new AddLayersCommand(Collections.singletonList(layer)));
        
        filtersHandler.setBgLayerFilter(false);
        viewer.refresh();
        assertTrue("Layers must be visible.", filtersHandler.isBgInViewer(viewer, map, layer));
        
        filtersHandler.setBgLayerFilter(true);
        viewer.refresh();
        assertTrue("Layers must be visible.", filtersHandler.isBgInViewer(viewer, map, layer));
        
        layer.setInteraction(Interaction.BACKGROUND, true);
        
        filtersHandler.setBgLayerFilter(false);
        viewer.refresh();
        assertFalse("Layers must not be visible.", filtersHandler.isBgInViewer(viewer, map, layer));
        
        filtersHandler.setBgLayerFilter(true);
        viewer.refresh();
        assertTrue("Layers must be visible.", filtersHandler.isBgInViewer(viewer, map, layer));
        
        layer.setInteraction(Interaction.BACKGROUND, false);
        
        filtersHandler.setBgLayerFilter(false);
        viewer.refresh();
        assertTrue("Layers must not be visible.", filtersHandler.isBgInViewer(viewer, map, layer));
        
        filtersHandler.setBgLayerFilter(true);
        viewer.refresh();
        assertTrue("Layers must be visible.", filtersHandler.isBgInViewer(viewer, map, layer));
        
        //mapgraphic:/localhost/mapgraphic
        
    }

    private IGeoResource getGridMapGraphic() throws MalformedURLException, IOException {
        final IRepository local = CatalogPlugin.getDefault().getLocal();
        IGeoResource gridResource = null;// = local.getById(IGeoResource.class, GRID_ID, new NullProgressMonitor());
        URL mgUrl = new URL("http://localhost/mapgraphic");
        if (gridResource == null) {
            local.acquire(mgUrl, new NullProgressMonitor());
            gridResource = local.getById(IGeoResource.class, GRID_ID, new NullProgressMonitor());
        }
        return gridResource;
    }
    
}
