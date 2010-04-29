/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2007 IBM Corporation and others
 * ------
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * --------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.refractions.udig.feature.panel;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkbenchPart;

/**
 * The default implementation of the content provider for the feature panel page's list of tabs.
 * <p>
 * Backs on to a FeaturePanelRegistery that has been configured for a particular FetureType.
 * 
 * @see TabListContentProvider
 */
public class FeaturePanelListContentProvider implements IStructuredContentProvider {

    protected FeaturePanelRegistry registry;

    protected IWorkbenchPart currentPart;

    /**
     * Constructor for TabListContentProvider.
     * 
     * @param registry the tabbed property registry.
     */
    public FeaturePanelListContentProvider( FeaturePanelRegistry registry ) {
        this.registry = registry;
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement ) {
        Assert.isTrue(inputElement instanceof ISelection);
        List<FeaturePanelTabDescriptor> list = registry.getTabDescriptors(currentPart,
                (ISelection) inputElement);
        return list.toArray();
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        /* not used */
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        FeaturePanelViewer featurePanelViewer = (FeaturePanelViewer) viewer;
        this.currentPart = featurePanelViewer.getWorkbenchPart();
    }
}
