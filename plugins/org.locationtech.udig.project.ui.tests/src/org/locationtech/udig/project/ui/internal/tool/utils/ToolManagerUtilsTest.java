/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2022, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.project.ui.internal.tool.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.internal.tool.util.ToolManagerUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ToolManagerUtilsTest {

    @Mock
    IWorkbenchPart part;

    @Mock
    IWorkbenchPartSite site;

    @Mock
    ISelectionProvider selectionProvider;

    @Mock
    IStructuredSelection selection;

    @Mock
    Map map;

    @Test
    public void getTargetMapFromSelection() {
        when(part.getSite()).thenReturn(site);
        when(site.getSelectionProvider()).thenReturn(selectionProvider);
        when(selectionProvider.getSelection()).thenReturn(selection);
        when(selection.isEmpty()).thenReturn(Boolean.FALSE);
        when(selection.getFirstElement()).thenReturn(map);

        Map targetMap = ToolManagerUtils.getTargetMap(part);

        assertNotNull(targetMap);
        assertEquals(map, targetMap);
    }

    @Test
    public void getTargetMapFromCreateNewMapCommand() throws Exception {
        when(part.getSite()).thenReturn(site);
        when(site.getSelectionProvider()).thenReturn(selectionProvider);
        when(selectionProvider.getSelection()).thenReturn(selection);
        when(selection.isEmpty()).thenReturn(Boolean.TRUE);
        assertNotNull(ToolManagerUtils.getTargetMap(part));
    }

    @Test
    public void nullObjectOnNullSelection() {
        when(part.getSite()).thenReturn(site);
        when(site.getSelectionProvider()).thenReturn(selectionProvider);
        when(selectionProvider.getSelection()).thenReturn(null);
        assertNull(ToolManagerUtils.getFirstSelectedElement(part));
    }

    @Test
    public void nullObjectOnEmptySelection() {
        when(part.getSite()).thenReturn(site);
        when(site.getSelectionProvider()).thenReturn(selectionProvider);
        when(selectionProvider.getSelection()).thenReturn(selection);
        when(selection.isEmpty()).thenReturn(true);
        assertNull(ToolManagerUtils.getFirstSelectedElement(part));
    }

    @Test
    public void nullObjectOnNonStructuredSelection() {
        when(part.getSite()).thenReturn(site);
        when(site.getSelectionProvider()).thenReturn(selectionProvider);
        ISelection nonStructuredSelection = Mockito.mock(ISelection.class);
        when(nonStructuredSelection.isEmpty()).thenReturn(false);
        when(selectionProvider.getSelection()).thenReturn(nonStructuredSelection);
        assertNull(ToolManagerUtils.getFirstSelectedElement(part));
    }

}
