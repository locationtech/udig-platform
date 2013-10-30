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
package org.locationtech.udig.issues.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.util.concurrent.atomic.AtomicBoolean;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.core.enums.Priority;
import org.locationtech.udig.core.enums.Resolution;
import org.locationtech.udig.issues.AbstractIssue;
import org.locationtech.udig.issues.FeatureIssue;
import org.locationtech.udig.issues.IIssue;
import org.locationtech.udig.issues.IIssuesContentProvider;
import org.locationtech.udig.issues.IIssuesList;
import org.locationtech.udig.issues.IIssuesManager;
import org.locationtech.udig.issues.IRemoteIssuesList;
import org.locationtech.udig.issues.IssueConfiguration;
import org.locationtech.udig.issues.IssueConstants;
import org.locationtech.udig.issues.IssuesList;
import org.locationtech.udig.issues.internal.ImageConstants;
import org.locationtech.udig.issues.internal.IssuesActivator;
import org.locationtech.udig.issues.internal.IssuesManager;
import org.locationtech.udig.issues.internal.PreferenceConstants;
import org.locationtech.udig.issues.internal.view.IssueExpansionProvider;
import org.locationtech.udig.issues.internal.view.IssuesContentProvider;
import org.locationtech.udig.issues.internal.view.IssuesLabelProvider;
import org.locationtech.udig.issues.internal.view.IssuesSorter;
import org.locationtech.udig.issues.internal.view.IssuesView;
import org.locationtech.udig.issues.internal.view.StrategizedSorter;
import org.locationtech.udig.issues.listeners.IIssuesListListener;
import org.locationtech.udig.issues.listeners.IssuesListEvent;
import org.locationtech.udig.issues.listeners.IssuesListEventType;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.tests.ui.ViewPart1;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.MapEditorWithPalette;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

public class IssuesViewTest extends AbstractProjectUITestCase {
    private IIssuesList list;
    private TreeViewer viewer;
    private IssuesView view;

    @Before
    public void setUp() throws Exception {
        FeatureIssue.setTesting(true);
        ((IssuesList)IIssuesManager.defaultInstance.getIssuesList()).listeners.clear();
        IIssuesManager.defaultInstance.getIssuesList().clear();
        view=(IssuesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IssueConstants.VIEW_ID);
        view.forTestingShowResolvedssues(false);
        view.forTestingGetResolvedIssues().clear();
        viewer = view.forTestingGetViewer();
        view.forTestingSetTesting();
        view.testingAddListeners();
        
        IPreferenceStore preferenceStore = IssuesActivator.getDefault().getPreferenceStore();
        preferenceStore.setValue(PreferenceConstants.KEY_VIEW_CONTENT_PROVIDER, ""); //$NON-NLS-1$
        preferenceStore.setValue(PreferenceConstants.KEY_VIEW_SORTER, ""); //$NON-NLS-1$
        preferenceStore.setValue(PreferenceConstants.KEY_VIEW_EXPANSION_PROVIDER, ""); //$NON-NLS-1$
        view.initViewerProviders();
        
        list = IIssuesManager.defaultInstance.getIssuesList();
        list.add(new TestIssue(Priority.CRITICAL, Resolution.UNRESOLVED));
        list.add(new TestIssue(Priority.HIGH, Resolution.IN_PROGRESS));
        list.add(new TestIssue(Priority.LOW, Resolution.RESOLVED));
        list.add(new TestIssue(Priority.TRIVIAL, Resolution.UNKNOWN));
        list.add(new TestIssue(Priority.WARNING, null));
        
        // allow viewer to update.
        viewer.refresh(true);

        assertEquals(5, ((Tree)viewer.getControl()).getItemCount());
    }
    
    @After
    public void tearDown() throws Exception {
    	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(view);
        FeatureIssue.setTesting(false);
    }
    
    @Test
    public void testIconsAndText() throws Exception {

        Display.getDefault().syncExec(new Runnable(){
            public void run() {
                // test labels and text

                IssuesLabelProvider p = new IssuesLabelProvider();
                // priority image
                assertNotNull(p.getColumnImage(list.get(0), IssuesView.PRIORITY_COLUMN));
                assertNotNull(p.getColumnImage(list.get(1), IssuesView.PRIORITY_COLUMN));
                assertNotNull(p.getColumnImage(list.get(2), IssuesView.PRIORITY_COLUMN));
                assertNotNull(p.getColumnImage(list.get(3), IssuesView.PRIORITY_COLUMN));
                assertNotNull(p.getColumnImage(list.get(4), IssuesView.PRIORITY_COLUMN));

                assertEquals(p.getColumnImage(list.get(0), IssuesView.PRIORITY_COLUMN), IssuesActivator
                		.getDefault().getImage(ImageConstants.PRIORITY_CRITICAL));
                assertEquals(p.getColumnImage(list.get(1), IssuesView.PRIORITY_COLUMN), IssuesActivator
                		.getDefault().getImage(ImageConstants.PRIORITY_HIGH));
                assertEquals(p.getColumnImage(list.get(2), IssuesView.PRIORITY_COLUMN), IssuesActivator
                		.getDefault().getImage(ImageConstants.PRIORITY_LOW));
                assertEquals(p.getColumnImage(list.get(3), IssuesView.PRIORITY_COLUMN), IssuesActivator
                		.getDefault().getImage(ImageConstants.PRIORITY_TRIVIAL));
                assertEquals(p.getColumnImage(list.get(4), IssuesView.PRIORITY_COLUMN), IssuesActivator
                		.getDefault().getImage(ImageConstants.PRIORITY_WARNING));

                // resolution image
                assertNotNull(p.getColumnImage(list.get(0), IssuesView.RESOLUTION_COLUMN));
                assertNotNull(p.getColumnImage(list.get(1), IssuesView.RESOLUTION_COLUMN));
                assertNotNull(p.getColumnImage(list.get(2), IssuesView.RESOLUTION_COLUMN));
                assertNotNull(p.getColumnImage(list.get(3), IssuesView.RESOLUTION_COLUMN));
                assertNotNull(p.getColumnImage(list.get(4), IssuesView.RESOLUTION_COLUMN));

                assertEquals(p.getColumnImage(list.get(0), IssuesView.RESOLUTION_COLUMN), IssuesActivator
                		.getDefault().getImage(ImageConstants.RESOLUTION_UNRESOLVED));
                assertEquals(p.getColumnImage(list.get(1), IssuesView.RESOLUTION_COLUMN), IssuesActivator
                		.getDefault().getImage(ImageConstants.RESOLUTION_VIEWED));
                assertEquals(p.getColumnImage(list.get(2), IssuesView.RESOLUTION_COLUMN), IssuesActivator
                		.getDefault().getImage(ImageConstants.RESOLUTION_RESOLVED));
                assertEquals(p.getColumnImage(list.get(3), IssuesView.RESOLUTION_COLUMN), IssuesActivator
                		.getDefault().getImage(ImageConstants.RESOLUTION_UNKNOWN));
                assertEquals(p.getColumnImage(list.get(4), IssuesView.RESOLUTION_COLUMN), IssuesActivator
                		.getDefault().getImage(ImageConstants.RESOLUTION_UNRESOLVED));

                // name image
                assertNull(p.getColumnImage(list.get(0), IssuesView.OBJECT_COLUMN));
                assertNull(p.getColumnImage(list.get(1), IssuesView.OBJECT_COLUMN));
                assertNull(p.getColumnImage(list.get(2), IssuesView.OBJECT_COLUMN));
                assertNull(p.getColumnImage(list.get(3), IssuesView.OBJECT_COLUMN));
                assertNull(p.getColumnImage(list.get(4), IssuesView.OBJECT_COLUMN));
                // desc image
                assertNull(p.getColumnImage(list.get(0), IssuesView.DESC_COLUMN));
                assertNull(p.getColumnImage(list.get(1), IssuesView.DESC_COLUMN));
                assertNull(p.getColumnImage(list.get(2), IssuesView.DESC_COLUMN));
                assertNull(p.getColumnImage(list.get(3), IssuesView.DESC_COLUMN));
                assertNull(p.getColumnImage(list.get(4), IssuesView.DESC_COLUMN));

                // priority text
                assertNull(p.getColumnText(list.get(0), IssuesView.PRIORITY_COLUMN));
                assertNull(p.getColumnText(list.get(1), IssuesView.PRIORITY_COLUMN));
                assertNull(p.getColumnText(list.get(2), IssuesView.PRIORITY_COLUMN));
                assertNull(p.getColumnText(list.get(3), IssuesView.PRIORITY_COLUMN));
                assertNull(p.getColumnText(list.get(4), IssuesView.PRIORITY_COLUMN));

                // Resolution text
                assertNull(p.getColumnText(list.get(0), IssuesView.RESOLUTION_COLUMN));
                assertNull(p.getColumnText(list.get(1), IssuesView.RESOLUTION_COLUMN));
                assertNull(p.getColumnText(list.get(2), IssuesView.RESOLUTION_COLUMN));
                assertNull(p.getColumnText(list.get(3), IssuesView.RESOLUTION_COLUMN));
                assertNull(p.getColumnText(list.get(4), IssuesView.RESOLUTION_COLUMN));
                // name text
                assertEquals(
                        "problem" + Priority.CRITICAL, p.getColumnText(list.get(0), IssuesView.OBJECT_COLUMN)); //$NON-NLS-1$
                assertEquals(
                        "problem" + Priority.HIGH, p.getColumnText(list.get(1), IssuesView.OBJECT_COLUMN)); //$NON-NLS-1$
                assertEquals(
                        "problem" + Priority.LOW, p.getColumnText(list.get(2), IssuesView.OBJECT_COLUMN)); //$NON-NLS-1$
                assertEquals(
                        "problem" + Priority.TRIVIAL, p.getColumnText(list.get(3), IssuesView.OBJECT_COLUMN)); //$NON-NLS-1$
                assertEquals(
                        "problem" + Priority.WARNING, p.getColumnText(list.get(4), IssuesView.OBJECT_COLUMN)); //$NON-NLS-1$
                // desc text
                assertEquals(
                        "desc" + Priority.CRITICAL, p.getColumnText(list.get(0), IssuesView.DESC_COLUMN)); //$NON-NLS-1$
                assertEquals(
                        "desc" + Priority.HIGH, p.getColumnText(list.get(1), IssuesView.DESC_COLUMN)); //$NON-NLS-1$
                assertEquals(
                        "desc" + Priority.LOW, p.getColumnText(list.get(2), IssuesView.DESC_COLUMN)); //$NON-NLS-1$
                assertEquals(
                        "desc" + Priority.TRIVIAL, p.getColumnText(list.get(3), IssuesView.DESC_COLUMN)); //$NON-NLS-1$
                assertEquals(
                        "desc" + Priority.WARNING, p.getColumnText(list.get(4), IssuesView.DESC_COLUMN)); //$NON-NLS-1$
            }
        });

    }

    @Test
    public void testSetFocus() throws Exception {
        assertEquals(5, list.size());
        
        for ( int i=0; i<15; i++ )
            view.forTestingGetResolvedIssues().add(new TestIssue(null, Resolution.RESOLVED));
        assertEquals(15, view.forTestingGetResolvedIssues().size());

        view.setFocus();
        assertEquals(4, list.size());
        assertEquals(10, view.forTestingGetResolvedIssues().size());

    }

    @Test
    public void testSorterChange() throws Exception {
        Display.getDefault().syncExec(new Runnable(){
            public void run() {

                // test resolution sorting
                viewer.getTree().getColumn(IssuesView.RESOLUTION_COLUMN).notifyListeners(SWT.Selection, new Event());
                ordering(viewer, 0, 2);
                viewer.getTree().getColumn(IssuesView.RESOLUTION_COLUMN).notifyListeners(SWT.Selection, new Event());
                ordering(viewer, 3, 2);

                // test resolution sorting
                viewer.getTree().getColumn(IssuesView.PRIORITY_COLUMN).notifyListeners(SWT.Selection, new Event());
                ordering(viewer, 0, 1);
                viewer.getTree().getColumn(IssuesView.PRIORITY_COLUMN).notifyListeners(SWT.Selection, new Event());
                ordering(viewer, 4, 3);

// test name sorting
                viewer.getTree().getColumn(IssuesView.OBJECT_COLUMN).notifyListeners(SWT.Selection, new Event());
                ordering(viewer, 0, 1);
                viewer.getTree().getColumn(IssuesView.OBJECT_COLUMN).notifyListeners(SWT.Selection, new Event());
                ordering(viewer, 4, 3);
                
// test desc sorting
                viewer.getTree().getColumn(IssuesView.DESC_COLUMN).notifyListeners(SWT.Selection, new Event());
                ordering(viewer, 0, 1);
                viewer.getTree().getColumn(IssuesView.DESC_COLUMN).notifyListeners(SWT.Selection, new Event());
                ordering(viewer, 4, 3);
                
            }
        });
    }

    @Ignore
    @Test
    public void testFixIssue() throws Exception {
        list.clear();
        
        list.add(new TestIssue(Priority.CRITICAL, Resolution.UNRESOLVED){
            @Override
            public void fixIssue( IViewPart arg0, IEditorPart arg1 ) {
                setResolution(Resolution.RESOLVED);
            }
        });
        
        view.fixIssue(list.get(0));
        
        assertEquals(Resolution.RESOLVED, list.get(0).getResolution());
        
        list.clear();
        
        list.add(new TestIssue(Priority.CRITICAL, Resolution.UNKNOWN){
            @Override
            public void fixIssue( IViewPart arg0, IEditorPart arg1 ) {
                //do nothing
            }
        });
        
        view.fixIssue(list.get(0));
        
        assertEquals(Resolution.UNKNOWN, list.get(0).getResolution());
        
        list.get(0).setResolution(Resolution.RESOLVED);
        view.fixIssue(list.get(0));        
        assertEquals(Resolution.RESOLVED, list.get(0).getResolution());

        list.get(0).setResolution(Resolution.UNRESOLVED);
        view.fixIssue(list.get(0));        
        assertEquals(Resolution.IN_PROGRESS, list.get(0).getResolution());
        
        list.get(0).setResolution(Resolution.IN_PROGRESS);
        view.fixIssue(list.get(0));        
        assertEquals(Resolution.IN_PROGRESS, list.get(0).getResolution());
        
        list.clear();
        
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("test", 4); //$NON-NLS-1$
        IGeoResource resource = MapTests.createGeoResource(features, false);
        final Map map = MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(512,512));
        class FixIssue extends TestIssue{
            public IViewPart view;
            public IEditorPart editor;
            FixIssue(){
            super(Priority.CRITICAL, Resolution.UNRESOLVED);
            }
                @Override
                public String getEditorID() {
                    return MapEditorWithPalette.ID;
              
                }
                
                @Override
                public IEditorInput getEditorInput() {
                    return ApplicationGIS.getInput(map);
                }
                @Override
                public String getViewPartId() {
                    return ViewPart1.ID;
                }
                @Override
                public void fixIssue( IViewPart arg0, IEditorPart arg1 ) {
                    view=arg0;
                    editor=arg1;
                }
            }
        FixIssue fixIssue = new FixIssue();
        list.add(fixIssue );
        
        view.fixIssue(list.get(0));
        while( Display.getCurrent().readAndDispatch());
        assertEquals(Resolution.IN_PROGRESS, list.get(0).getResolution());      
        assertNotNull( fixIssue.editor);
        assertNotNull( fixIssue.view);
    }
    
//    public void testDoubleClick() throws Exception {
//        list.clear();
//        
//        list.add(new TestIssue(Priority.CRITICAL, Resolution.UNRESOLVED){
//            @Override
//            public void fixIssue( IViewPart arg0, IEditorPart arg1 ) {
//                setResolution(Resolution.RESOLVED);
//            }
//        });
//        
//        viewer.getTable().getItem(0).notifyListeners(SWT.MouseDoubleClick, new Event());
//        
//        while (Display.getCurrent().readAndDispatch() );
//        
//        assertEquals(Resolution.RESOLVED, list.get(0).getResolution());
//    }
    
    @Test
    public void testResolutionChange() throws Exception {
        Display.getDefault().syncExec(new Runnable(){
            public void run()  {
                assertEquals(Resolution.RESOLVED, list.get(2).getResolution());
                viewer.editElement(list.get(2),IssuesView.RESOLUTION_COLUMN);
                assertEquals(Resolution.UNKNOWN, list.get(2).getResolution());
                viewer.editElement(list.get(2),IssuesView.RESOLUTION_COLUMN);
                assertEquals(Resolution.UNRESOLVED, list.get(2).getResolution());
                viewer.editElement(list.get(2),IssuesView.RESOLUTION_COLUMN);
                assertEquals(Resolution.IN_PROGRESS, list.get(2).getResolution());
                viewer.editElement(list.get(2),IssuesView.RESOLUTION_COLUMN);
                assertEquals(Resolution.RESOLVED, list.get(2).getResolution());
                view.setFocus();
                assertEquals(4, list.size());
            }
        });
    }

    @Test
    public void testPriorityChange() throws Exception {
        assertEquals(Priority.CRITICAL, list.get(0).getPriority());
        viewer.editElement(list.get(0),IssuesView.PRIORITY_COLUMN);
        assertEquals(Priority.TRIVIAL, list.get(0).getPriority());
        viewer.editElement(list.get(0),IssuesView.PRIORITY_COLUMN);
        assertEquals(Priority.LOW, list.get(0).getPriority());
        viewer.editElement(list.get(0),IssuesView.PRIORITY_COLUMN);
        assertEquals(Priority.WARNING, list.get(0).getPriority());
        viewer.editElement(list.get(0),IssuesView.PRIORITY_COLUMN);
        assertEquals(Priority.HIGH, list.get(0).getPriority());
        viewer.editElement(list.get(0),IssuesView.PRIORITY_COLUMN);
        assertEquals(Priority.CRITICAL, list.get(0).getPriority());
    }
    
    @Test
    public void testShowFixedIssues() throws Exception {
        list.clear();
        for ( int i=0; i<15; i++ )
            list.add(new TestIssue(null, Resolution.RESOLVED));

        assertEquals( 15, ((Tree)viewer.getControl()).getItemCount());
        assertEquals( 15, list.size());
        view.setFocus();
        assertEquals( 0, list.size());
        assertEquals( 10, view.forTestingGetResolvedIssues().size());
        
        view.forTestingShowResolvedssues(true);
        
        assertEquals( 10, ((Tree)viewer.getControl()).getItemCount());
        assertEquals( view.forTestingGetResolvedIssues().get(0), ((Tree)viewer.getControl()).getItem(0).getData());
        

        view.forTestingGetResolvedIssues().clear();
        assertEquals( 0, ((Tree)viewer.getControl()).getItemCount());
        assertEquals( 0, view.forTestingGetResolvedIssues().size());
        
        for ( int i=0; i<4; i++ )
            list.add(new TestIssue(null, Resolution.RESOLVED));        
        for ( int i=0; i<4; i++ )
            list.add(new TestIssue(null, Resolution.UNKNOWN));

        view.setFocus();
        assertEquals( 4, ((Tree)viewer.getControl()).getItemCount());
        
        list.clear();
        assertEquals( 4, ((Tree)viewer.getControl()).getItemCount());
        
        viewer.editElement(view.forTestingGetResolvedIssues().get(0),IssuesView.RESOLUTION_COLUMN);
        assertEquals(Resolution.UNKNOWN, view.forTestingGetResolvedIssues().get(0).getResolution());
        
        view.setFocus();
        assertEquals( 3, ((Tree)viewer.getControl()).getItemCount());
        assertEquals( 1, list.size());
        
        view.forTestingShowResolvedssues(false);
        assertEquals( 1, ((Tree)viewer.getControl()).getItemCount());
        assertEquals( list.get(0), ((Tree)viewer.getControl()).getItem(0).getData());

    }
    
    @Test
    public void testCloseView() throws Exception {
        IIssuesList old = IIssuesManager.defaultInstance.getIssuesList();
        try{
        IIssuesManager.defaultInstance.setIssuesList(IssuesListTestHelper.createInMemoryDatastoreIssuesList(null, null));
        FeatureIssue createFeatureIssue = IssuesListTestHelper.createFeatureIssue("issue"); //$NON-NLS-1$
        IssuesManager.defaultInstance.getIssuesList().add(createFeatureIssue);
        createFeatureIssue.setPriority(Priority.TRIVIAL);
        final AtomicBoolean saved=new AtomicBoolean(false);
        IssuesManager.defaultInstance.getIssuesList().addListener(new IIssuesListListener(){

            public void notifyChange( IssuesListEvent event ) {
                if(event.getType()==IssuesListEventType.SAVE)
                    saved.set(true);
            }
            
        });
        view.getSite().getPage().hideView(view);
        assertTrue(saved.get());
        }finally{
            IIssuesManager.defaultInstance.setIssuesList(old);
        }
        
    }
    
    @Test
    public void testDispose() throws Exception {
        view.disposeListeners();
        viewer.getControl().getMenu().setVisible(true);
        viewer.getControl().getMenu().setVisible(false);
        assertEquals(0, ((IssuesList)list).listeners.size());
        view.restoreListeners();
    }

    @Test
    public void testDelete() throws Exception {
		IIssue issue = list.get(0);
		viewer.setSelection(new StructuredSelection(issue));
		view.forTestingGetDeleteAction().runWithEvent(new Event());
		assertEquals("only 1 item should have been deleted", 4,list.size()); //$NON-NLS-1$
		IIssue[] issues=new IIssue[3];
		issues[0]=list.get(0);
		issues[1]=list.get(1);
		issues[2]=list.get(2);
		StructuredSelection selection = new StructuredSelection(issues);
		viewer.setSelection(selection);
		view.forTestingGetDeleteAction().runWithEvent(new Event());
		assertEquals("All items should have been deleted except 1", 1,list.size()); //$NON-NLS-1$
	}

    @Test
    public void testDeleteResolvedIssues() throws Exception {
    	for (IIssue issue : list) {
			issue.setResolution(Resolution.RESOLVED);
		}
    	view.setFocus();
    	view.forTestingShowResolvedssues(true);
        IIssuesList list=view.forTestingGetResolvedIssues();
    	IIssue issue = list.get(0);
		viewer.setSelection(new StructuredSelection(issue));
		view.forTestingGetDeleteAction().runWithEvent(new Event());
		assertEquals("only 1 item should have been deleted", 4,list.size()); //$NON-NLS-1$
		IIssue[] issues=new IIssue[3];
		issues[0]=list.get(0);
		issues[1]=list.get(1);
		issues[2]=list.get(2);
		StructuredSelection selection = new StructuredSelection(issues);
		viewer.setSelection(selection);
		view.forTestingGetDeleteAction().runWithEvent(new Event());
		assertEquals("All items should have been deleted except 1", 1,list.size()); //$NON-NLS-1$
	}

    @Test
    public void testGroupDelete() throws Exception {
		IIssue issue = list.get(0);
		viewer.setSelection(new StructuredSelection(issue));
		Event event = new Event();
		event.display=Display.getCurrent();
		view.forTestingGetDeleteGroupAction().runWithEvent(event);
		assertEquals("All items should be deleted", 0,list.size()); //$NON-NLS-1$
		
	}

    @Test
    public void testGroupDeleteResolvedIssues() throws Exception {
    	for (IIssue issue : list) {
			issue.setResolution(Resolution.RESOLVED);
		}
    	view.setFocus();
    	view.forTestingShowResolvedssues(true);
        IIssuesList list=view.forTestingGetResolvedIssues();
    	IIssue issue = list.get(0);
		viewer.setSelection(new StructuredSelection(issue));
		Event event = new Event();
		event.display=Display.getCurrent();
		view.forTestingGetDeleteGroupAction().runWithEvent(event);
		assertEquals("all items should have been deleted", 0,list.size()); //$NON-NLS-1$
		
	}
    
    @Test
    public void testButtonEnablement() {
    	viewer.setSelection(new StructuredSelection());
    	assertFalse(view.forTestingGetDeleteAction().isEnabled());
    	assertFalse(view.forTestingGetDeleteGroupAction().isEnabled());
    	assertFalse(view.forTestingGetFixAction().isEnabled());
    	
    	viewer.setSelection(new StructuredSelection(list.get(0)));
    	assertTrue(view.forTestingGetDeleteAction().isEnabled());
    	assertTrue(view.forTestingGetDeleteGroupAction().isEnabled());
    	assertTrue(view.forTestingGetFixAction().isEnabled());
    }
    
    @Test
    public void testRefreshSaveButtonEnablement() throws Exception {
        IIssuesList old = IIssuesManager.defaultInstance.getIssuesList();
        try{
            assertFalse(view.forTestingGetRefreshButton().isEnabled());
            assertFalse(view.forTestingGetSaveButton().isEnabled());
    
            IIssuesManager.defaultInstance.setIssuesList(IssuesListTestHelper.createInMemoryDatastoreIssuesList(null, null));
            IIssuesManager.defaultInstance.getIssuesList().addAll(list);
            IIssuesList list=IIssuesManager.defaultInstance.getIssuesList();
            
            assertTrue(view.forTestingGetRefreshButton().isEnabled());
            assertFalse(view.forTestingGetSaveButton().isEnabled());
            
            list.get(0).setDescription("New Description"); //$NON-NLS-1$

            assertTrue(view.forTestingGetRefreshButton().isEnabled());
            assertTrue(view.forTestingGetSaveButton().isEnabled());
            
            IIssuesManager.defaultInstance.save(new NullProgressMonitor());
            
            assertTrue(view.forTestingGetRefreshButton().isEnabled());
            assertFalse(view.forTestingGetSaveButton().isEnabled());
            
            IIssuesManager.defaultInstance.setIssuesList(new IssuesList());
            assertFalse(view.forTestingGetRefreshButton().isEnabled());
            assertFalse(view.forTestingGetSaveButton().isEnabled());
        }finally{
            IIssuesManager.defaultInstance.setIssuesList(old);
        }
    }
    
    @Test
    public void testRefreshAction() throws Exception {
        IIssuesList old = IIssuesManager.defaultInstance.getIssuesList();
        try{
            IIssuesManager.defaultInstance.setIssuesList(IssuesListTestHelper.createInMemoryDatastoreIssuesList(null, null));
            IRemoteIssuesList list=(IRemoteIssuesList) IIssuesManager.defaultInstance.getIssuesList();
            
            final IssuesListEvent[] change=new IssuesListEvent[1];
            list.addListener(new IIssuesListListener(){

                public void notifyChange( IssuesListEvent event ) {
                    change[0]=event;
                }
                
            });
            
            view.forTestingGetRefreshButton().runWithEvent(new Event());
            
            assertEquals(IssuesListEventType.REFRESH, change[0].getType());
        }finally{
            IIssuesManager.defaultInstance.setIssuesList(old);
        }
    }
    
    @Test
    public void testSaveAction() throws Exception {
        IIssuesList old = IIssuesManager.defaultInstance.getIssuesList();
        try{
            IIssuesManager.defaultInstance.setIssuesList(IssuesListTestHelper.createInMemoryDatastoreIssuesList(null, null));
            IRemoteIssuesList list=(IRemoteIssuesList) IIssuesManager.defaultInstance.getIssuesList();
            list.add(IssuesListTestHelper.createFeatureIssue("id")); //$NON-NLS-1$
            
            list.get(0).setDescription("new Description YEAH!"); //$NON-NLS-1$
            
            final IssuesListEvent[] change=new IssuesListEvent[1];
            list.addListener(new IIssuesListListener(){

                public void notifyChange( IssuesListEvent event ) {
                    change[0]=event;
                }
                
            });
            
            view.forTestingGetSaveButton().runWithEvent(new Event());
            
            assertEquals(IssuesListEventType.SAVE, change[0].getType());
        }finally{
            IIssuesManager.defaultInstance.setIssuesList(old);
        }
    }

    @Test
    public void testSetContentProvider() throws Exception {
        view.setContentProvider(new TestContentProvider.Provider1());
        assertEquals(TestContentProvider.Provider1.CHILD,viewer.getTree().getTopItem().getData());
        
        view.setContentProvider(new TestContentProvider.Provider2());
        assertEquals(TestContentProvider.Provider2.CHILD,viewer.getTree().getTopItem().getData());
        
        IssueConfiguration.get().setContentProvider(new TestContentProvider.Provider1());
        assertEquals(TestContentProvider.Provider1.CHILD,viewer.getTree().getTopItem().getData());
    }

    @Test
    public void testSetLabelProvider() throws Exception {
        view.setLabelProvider(new TestLabelProvider.Provider1());

        assertEquals(TestLabelProvider.Provider1.ROWTEXT,viewer.getTree().getItem(0).getText());
        assertEquals(TestLabelProvider.Provider1.HEADERTEXT, view.forTestingGetProblemObjectColumnHeader().getText());

        view.setLabelProvider(new TestLabelProvider.Provider2());

        assertEquals(TestLabelProvider.Provider2.ROWTEXT,viewer.getTree().getItem(0).getText());
        assertEquals(TestLabelProvider.Provider2.HEADERTEXT, view.forTestingGetProblemObjectColumnHeader().getText());
        

        view.setLabelProvider(new TestLabelProvider.Provider1());

        assertEquals(TestLabelProvider.Provider1.ROWTEXT,viewer.getTree().getItem(0).getText());
        assertEquals(TestLabelProvider.Provider1.HEADERTEXT, view.forTestingGetProblemObjectColumnHeader().getText());
        
    }

    @Ignore
    @Test
    public void testSetExpansionProvider() throws Exception {
        viewer.setSelection(new StructuredSelection());
        view.setContentProvider(new IIssuesContentProvider(){
            String parent= "parent";//$NON-NLS-1$
            private Object input;

            public String getExtensionID() {
                return null;
            }

            public Object[] getChildren( Object parentElement ) {
                if( parentElement instanceof IIssuesList) {
                    return new String[]{parent}; 
                }
                if( parentElement instanceof String )
                    return new Integer[]{2};
                return null;
            }

            public Object getParent( Object element ) {
                if( element instanceof Integer) {
                    return parent; 
                }
                if( element instanceof String )
                    return input;
                return null;
            }

            public boolean hasChildren( Object parentElement ) {
                if( parentElement instanceof IIssuesList)
                    return true;
                if( parentElement instanceof String )
                    return true;
                return false;
            }

            public Object[] getElements( Object inputElement ) {
                return getChildren(inputElement);
            }

            public void dispose() {
            }

            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
                this.input=newInput;
            }
            
        });
        

        assertTrue(viewer.getTree().getTopItem().getExpanded());
        
        view.setExpansionProvider(new TestExpansionProvider.Provider1());
        assertTrue(viewer.getTree().getTopItem().getExpanded());
        
        view.setExpansionProvider(new TestExpansionProvider.Provider2());
        assertFalse(viewer.getTree().getTopItem().getExpanded());
        
        view.setExpansionProvider(new TestExpansionProvider.Provider1());
        assertTrue(viewer.getTree().getTopItem().getExpanded());
    }
    
    @Ignore
    @Test
    public void testInit() throws Exception {
        IPreferenceStore preferenceStore = IssuesActivator.getDefault().getPreferenceStore();
        preferenceStore.setValue(PreferenceConstants.KEY_VIEW_CONTENT_PROVIDER, "org.locationtech.udig.issues.test.TestContentProvider"); //$NON-NLS-1$
        preferenceStore.setValue(PreferenceConstants.KEY_VIEW_SORTER, "org.locationtech.udig.issues.test.TestSorter"); //$NON-NLS-1$
        preferenceStore.setValue(PreferenceConstants.KEY_VIEW_EXPANSION_PROVIDER, "org.locationtech.udig.issues.test.TestContentProvider"); //$NON-NLS-1$
        preferenceStore.setValue(PreferenceConstants.KEY_VIEW_LABEL_PROVIDER, "org.locationtech.udig.issues.test.TestLabelProvider"); //$NON-NLS-1$
        
        view.initViewerProviders();
        
        assertTrue( viewer.getContentProvider() instanceof TestContentProvider.Provider1 );
        assertTrue( ((StrategizedSorter)viewer.getSorter()).getStrategy() instanceof TestSorter.Sorter1 );
        assertTrue( view.getExpansionProvider() instanceof TestExpansionProvider.Provider1 );
        assertTrue( viewer.getLabelProvider() instanceof TestLabelProvider.Provider1 );
        
        preferenceStore.setValue(PreferenceConstants.KEY_VIEW_CONTENT_PROVIDER, "randomNonsense value"); //$NON-NLS-1$
        preferenceStore.setValue(PreferenceConstants.KEY_VIEW_LABEL_PROVIDER, "randomNonsense value"); //$NON-NLS-1$
        preferenceStore.setValue(PreferenceConstants.KEY_VIEW_EXPANSION_PROVIDER, "randomNonsense value"); //$NON-NLS-1$
        preferenceStore.setValue(PreferenceConstants.KEY_VIEW_SORTER, "randomNonsense value"); //$NON-NLS-1$

        view.initViewerProviders();
        
        assertTrue( viewer.getContentProvider() instanceof IssuesContentProvider );
        assertTrue( ((StrategizedSorter)viewer.getSorter()).getStrategy() instanceof IssuesSorter );
        assertTrue( view.getExpansionProvider() instanceof IssueExpansionProvider );
        assertTrue( viewer.getLabelProvider() instanceof IssuesLabelProvider );
    }

    
    
    /**
     * @param view
     * @param criticalPriorityRow
     * @param expected2nd
     */
    void ordering( final TreeViewer viewer, int criticalPriorityRow, int highPriorityRow ) {
        IIssue issue=(IIssue) viewer.getTree().getItem(criticalPriorityRow).getData();
        assertEquals(0, issue.getPriority().ordinal());
        issue=(IIssue) viewer.getTree().getItem(highPriorityRow).getData();
        assertEquals(1, issue.getPriority().ordinal());
    }

    class TestIssue extends AbstractIssue {


        TestIssue( Priority p2, Resolution r ) {
            Priority p=p2;
            setResolution(r);
            if( p==null )
                p=Priority.WARNING;
            setPriority(p);
        }
        public TestIssue() {
        }
        public String getProblemObject() {
            return "problem"+getPriority(); //$NON-NLS-1$
        }

        public String getDescription() {
            return "desc"+getPriority(); //$NON-NLS-1$
        }

        public void fixIssue( IViewPart part, IEditorPart editor ) {
            // nada
        }
        
        @Override
        public String toString() {
            return getResolution()+" "+getPriority(); //$NON-NLS-1$
        }
		public String getExtensionID() {
			return null;
		}
        public ReferencedEnvelope getBounds() {
            return null;
        }
        public void init( IMemento memento, IMemento viewMemento, String issueId, String groupId, ReferencedEnvelope bounds ) {
        }
        public void save( IMemento memento ) {
        }
    }
}
