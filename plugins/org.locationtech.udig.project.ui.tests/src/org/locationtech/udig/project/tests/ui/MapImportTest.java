/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.tests.ui;

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

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.ui.ResourceSelectionPage;
import org.locationtech.udig.catalog.tests.DummyMultiResourceService;
import org.locationtech.udig.catalog.tests.DummyService;
import org.locationtech.udig.catalog.tests.ui.workflow.DialogDriver;
import org.locationtech.udig.catalog.tests.ui.workflow.DummyMonitor;
import org.locationtech.udig.catalog.ui.workflow.ResourceSelectionState;
import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;
import org.locationtech.udig.project.ui.internal.LayersView;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.wizard.MapImport;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

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
        
        org.locationtech.udig.project.internal.Map activeMap = ApplicationGISInternal.getActiveMap();
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
