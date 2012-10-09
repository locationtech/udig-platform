/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.project.tests.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.ui.ResourceSelectionPage;
import net.refractions.udig.catalog.tests.DummyMultiResourceService;
import net.refractions.udig.catalog.tests.DummyService;
import net.refractions.udig.catalog.tests.ui.workflow.DialogDriver;
import net.refractions.udig.catalog.tests.ui.workflow.DummyMonitor;
import net.refractions.udig.catalog.ui.workflow.ResourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.ApplicationGISInternal;
import net.refractions.udig.project.ui.internal.LayersView;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.wizard.MapImport;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MapImportTest extends AbstractProjectUITestCase {
	
	MapImport mapImport;
	
	@Before
	public void setUp() throws Exception {
		mapImport = new MapImport();
		mapImport.getDialog().setBlockOnOpen(false);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			.getActivePage().showView(LayersView.ID);
	}

    @After
    public void tearDown() throws Exception {
//        Project p=(Project) ApplicationGIS.getActiveProject();
        List<Project> projects = ProjectPlugin.getPlugin().getProjectRegistry().getProjects();
        for( Project p : projects ) {
            p.getElementsInternal().clear();
        }
    }
    
    @Ignore
    @Test
    public void testNormal() throws Exception {
        Object context = getContext();
        
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        
        List members = catalog.members(new DummyMonitor());
        if (!members.isEmpty()) {
            //clear the catalog
            for (Iterator itr = members.iterator(); itr.hasNext();) {
                IService service = (IService)itr.next();
                catalog.remove(service);
            }
        }
        members = catalog.members(new DummyMonitor());
        assertTrue(members.isEmpty());

        runMapImport(context);
        
        IMap map = ApplicationGIS.getActiveMap();
        assertNotNull(map);
        
        List<ILayer> layers = map.getMapLayers();
        assertFalse(layers.isEmpty());
        
        for (ILayer layer : layers) {
            assertGeoResourceType(layer, DummyService.class);
        }

        mapImport=new MapImport();
        runMapImport(context);
        
        assertEquals(2, map.getMapLayers().size());
        assertEquals(map, ApplicationGIS.getActiveMap());
        IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
        assertEquals(1, editors.length);
        
    }

    private void runMapImport( Object context ) throws Exception {        

        mapImport.run(new DummyMonitor(),context);

        WaitCondition object = new WaitCondition(){

			public boolean isTrue() {
                IMap map = ApplicationGIS.getActiveMap();
                if (map == ApplicationGIS.NO_MAP)
                    return false;
                
                List<ILayer> layers = map.getMapLayers();
                if (layers.isEmpty())
                	return false;
                return true;
			}
        	
        };
      UDIGTestUtil.inDisplayThreadWait(5000, object, true);
//      UDIGTestUtil.inDisplayThreadWait(5000000, object, true);

        
        if (mapImport.getDialog().getShell().isVisible()) {
            ResourceSelectionPage page = (ResourceSelectionPage) mapImport.getDialog()
                    .getWorkflowWizard().getPage(Messages.MapImport_selectLayers);
            // set a selection on the viewer
            page.getViewer().setChecked(page.getViewer().getTree().getItem(0).getItem(0).getData(),
                    true);
            page.syncWithUI();
            UDIGTestUtil.inDisplayThreadWait(5000, object, true);
        }
    }

    @Ignore
    @Test
    public void testMultiResource() throws Exception {
        Object context = DummyMultiResourceService.url;
        
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        
        List members = catalog.members(new DummyMonitor());
        if (!members.isEmpty()) {
            //clear the catalog
            for (Iterator itr = members.iterator(); itr.hasNext();) {
                IService service = (IService)itr.next();
                catalog.remove(service);
            }
        }
        members = catalog.members(new DummyMonitor());
        assertTrue(members.isEmpty());
        

        final WorkflowWizard workflowWizard = mapImport.getDialog().getWorkflowWizard();
		workflowWizard.getWorkflow()
            .setContext(context);
        mapImport.run(new DummyMonitor(),context);
        
        UDIGTestUtil.inDisplayThreadWait(3000, new WaitCondition(){

			public boolean isTrue()  {
				State state = workflowWizard.getWorkflow().getCurrentState();
				if ( state instanceof ResourceSelectionState )
					return true;
				return false;
			}
        	
        }, true);
        assertTrue(workflowWizard.getWorkflow().getCurrentState() instanceof ResourceSelectionState);
        
        //check the resource page to ensure that it isn't ignored
        ResourceSelectionState currentState = (ResourceSelectionState) workflowWizard.getWorkflow().getCurrentState();

        assertTrue(currentState.getResources()==null || currentState.getResources().isEmpty());
        
        //Set the resources on the state and press finish
        IService service=currentState.getServices().iterator().next();
        Map<IGeoResource, IService> resources=new HashMap<IGeoResource, IService>();
        
        for (IResolve resolve : service.resources(new NullProgressMonitor())) {
			resources.put((IGeoResource) resolve, service);
		}
        
        
        currentState.setResources(resources);
        
        net.refractions.udig.project.internal.Map activeMap = ApplicationGISInternal.getActiveMap();
        if( activeMap!=ApplicationGIS.NO_MAP )
            activeMap.getLayersInternal().clear();

        
        DialogDriver.pushButton(mapImport.getDialog(), IDialogConstants.FINISH_ID);
       
        UDIGTestUtil.inDisplayThreadWait(4000, new WaitCondition(){

			public boolean isTrue()  {
			    IMap map = ApplicationGIS.getActiveMap();
			    if( map==ApplicationGIS.NO_MAP )
			    	return false;
			    
			    return true;
			}
        	
        }, true);
        
        IMap map = ApplicationGIS.getActiveMap();
        assertNotSame(ApplicationGIS.NO_MAP, map);
        
        List<ILayer> layers = map.getMapLayers();
        assertEquals(2, layers.size());
        
        for (ILayer layer : layers) {
            assertGeoResourceType(layer, DummyMultiResourceService.class);
        }
    }
    
	Object getContext() throws Exception {
		URL url = DummyService.url;
		
		return url;
	}
	
	void assertGeoResourceType(ILayer layer, Class<? extends Object> type) throws Exception {
		assertTrue(layer.getGeoResources().get(0).parent(null).canResolve(type));
	}
}