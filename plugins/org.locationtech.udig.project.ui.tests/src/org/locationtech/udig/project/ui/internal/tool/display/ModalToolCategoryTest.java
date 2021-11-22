/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.tool.display;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.project.ui.tool.IToolManager;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ModalToolCategoryTest {

    @Mock
    private IToolManager manager;

    @Mock
    private IToolBarManager toolBarManager;

    @Mock
    private IConfigurationElement configElement;

    @Mock
    private IContributionItem contributionItem;

    @Test
    public void initContributionItemOnlyOnce() {

        when(configElement.getAttribute("id")).thenReturn("test.modal.tool.category"); //$NON-NLS-1$ //$NON-NLS-2$
        when(configElement.getAttribute("commandId")).thenReturn("test.command.id"); //$NON-NLS-1$ //$NON-NLS-2$
        when(configElement.getAttribute("name")).thenReturn("test.name"); //$NON-NLS-1$ //$NON-NLS-2$
        when(configElement.getAttribute("icon")).thenReturn(null); //$NON-NLS-1$

        when(toolBarManager.find("test.modal.tool.category")).thenReturn(null); //$NON-NLS-1$
        // second call simulates that the Contribution has been added
        when(toolBarManager.find("test.modal.tool.category")).thenReturn(contributionItem); //$NON-NLS-1$

        toolBarManager.update(false);

        ModalToolCategory modalToolCategory = new ModalToolCategory(configElement, manager);

        assertNull("Contribution should not be initialized", modalToolCategory.getContribution()); //$NON-NLS-1$
        modalToolCategory.contribute(toolBarManager);
        ContributionItem firstInstanceContribItem = modalToolCategory.getContribution();

        assertEquals("test.modal.tool.category", firstInstanceContribItem.getId()); //$NON-NLS-1$

        // contribute a second time
        modalToolCategory.contribute(toolBarManager);
        ContributionItem secondInstanceContribItem = modalToolCategory.getContribution();
        assertEquals("test.modal.tool.category", secondInstanceContribItem.getId()); //$NON-NLS-1$

        assertEquals(firstInstanceContribItem, secondInstanceContribItem);

        verify(configElement, times(4)).getAttribute(anyString());
        verify(toolBarManager, times(2)).find(anyString());
    }
}
