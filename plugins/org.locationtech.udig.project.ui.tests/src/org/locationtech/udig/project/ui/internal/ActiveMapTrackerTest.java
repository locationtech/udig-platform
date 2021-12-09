/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2021, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.project.internal.Map;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ActiveMapTrackerTest {

    @Mock
    private IWorkbenchWindow workbenchWindow;

    @Mock
    private IWorkbenchPage workbenchPage;

    @Mock
    private IEditorReference editorReference;

    @Mock
    private IWorkbenchPartReference partRef;

    @Test
    public void registersPageListenerOnWindowOpen() {
        final ActiveMapTracker instance = new ActiveMapTracker();
        when(workbenchWindow.getPages()).thenReturn(new IWorkbenchPage[] {});

        workbenchWindow.addPageListener(instance);

        instance.windowOpened(workbenchWindow);
        verify(workbenchWindow, atLeastOnce()).addPageListener(instance);
    }

    @Test
    public void removesPageListenerOnWindowClose() {
        final ActiveMapTracker instance = new ActiveMapTracker();
        when(workbenchWindow.getPages()).thenReturn(new IWorkbenchPage[] {});

        workbenchWindow.removePageListener(instance);

        instance.windowClosed(workbenchWindow);
        verify(workbenchWindow, atLeastOnce()).removePageListener(instance);
    }

    @Test
    public void registersOpenPartOnWindowOpen() {
        final ActiveMapTracker instance = new ActiveMapTracker();

        final Map map = mock(Map.class);

        final MapEditorPart mEditor = mock(MapEditorPart.class);

        when(mEditor.getMap()).thenReturn(map);

        when(editorReference.getPart(false)).thenReturn(mEditor);

        when(workbenchWindow.getPages()).thenReturn(new IWorkbenchPage[] { workbenchPage });

        when(workbenchPage.getEditorReferences())
                .thenReturn(new IEditorReference[] { editorReference });

        when(workbenchPage.getViewReferences()).thenReturn(new IViewReference[] {});

        instance.windowOpened(workbenchWindow);

        assertTrue(instance.getOpenMapParts().contains(mEditor));
        assertTrue(instance.getOpenMaps().contains(map));

        verify(mEditor, times(1)).getMap();
        verify(editorReference, times(1)).getPart(false);
        verify(workbenchWindow, times(1)).getPages();
        verify(workbenchPage, times(1)).getEditorReferences();
        verify(workbenchPage, times(1)).getViewReferences();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void openMapsCollectionIsReadOnly() {
        final Map map = mock(Map.class);
        new ActiveMapTracker().getOpenMaps().remove(map);
    }

    @Test
    public void trackMapPartOnlyOnPartOpened() {
        final ActiveMapTracker instance = new ActiveMapTracker();
        final MapPart mapPart = mock(MapPart.class);
        final IViewPart anotherPart = mock(IViewPart.class);

        when(partRef.getPart(false)).thenReturn(mapPart);

        assertEquals(0, instance.getOpenMapParts().size());
        instance.partOpened(partRef);

        assertTrue(instance.getOpenMapParts().contains(mapPart));
        assertEquals(1, instance.getOpenMapParts().size());

        // another View
        when(partRef.getPart(false)).thenReturn(anotherPart);

        instance.partOpened(partRef);

        assertTrue(instance.getOpenMapParts().contains(mapPart));
        assertEquals(1, instance.getOpenMapParts().size());
        assertEquals(mapPart, instance.getMostRecentOpenedPart());

        verify(partRef, times(2)).getPart(false);
    }

    @Test
    public void mostRecentOpenedMapFirst() {
        final ActiveMapTracker instance = new ActiveMapTracker();
        IWorkbenchPartReference partRef = mock(IWorkbenchPartReference.class);
        final MapPart mapPart1 = mock(MapPart.class);
        final MapPart mapPart2 = mock(MapPart.class);

        when(partRef.getPart(false)).thenReturn(mapPart1);

        assertEquals(0, instance.getOpenMapParts().size());
        instance.partOpened(partRef);

        assertTrue(instance.getOpenMapParts().contains(mapPart1));
        assertEquals(mapPart1, instance.getMostRecentOpenedPart());
        assertEquals(1, instance.getOpenMapParts().size());

        // second View
        when(partRef.getPart(false)).thenReturn(mapPart2);

        instance.partOpened(partRef);
        assertTrue(instance.getOpenMapParts().contains(mapPart1));
        assertTrue(instance.getOpenMapParts().contains(mapPart2));

        assertEquals(2, instance.getOpenMapParts().size());
        assertEquals(mapPart2, instance.getMostRecentOpenedPart());
        verify(partRef, times(2)).getPart(false);
    }

    @Test
    public void openingTwoMaps_ActivateFirstOne_FirstOneIsActive() {
        final ActiveMapTracker instance = new ActiveMapTracker();
        IWorkbenchPartReference partRef = mock(IWorkbenchPartReference.class);
        final MapEditorPart mapPart1 = mock(MapEditorPart.class);
        final MapEditorPart mapPart2 = mock(MapEditorPart.class);

        when(partRef.getPart(false)).thenReturn(mapPart1);

        instance.partOpened(partRef);
        instance.partActivated(partRef);

        assertTrue(instance.getOpenMapParts().contains(mapPart1));
        assertEquals(mapPart1, instance.getActiveMapPart());
        assertEquals(1, instance.getOpenMapParts().size());

        when(partRef.getPart(false)).thenReturn(mapPart2);

        instance.partOpened(partRef);
        instance.partActivated(partRef);

        assertTrue(instance.getOpenMapParts().contains(mapPart2));
        assertEquals(mapPart2, instance.getActiveMapPart());
        assertEquals(2, instance.getOpenMapParts().size());

        when(partRef.getPart(false)).thenReturn(mapPart1);
        instance.partActivated(partRef);

        assertEquals(mapPart1, instance.getActiveMapPart());
    }
}
