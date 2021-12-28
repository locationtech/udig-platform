/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2008 IBM Corporation and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 * Contributors:
 *     Refractions Research - adapt to Feature Model
 *     IBM Corporation - initial API and implementation
 */
/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.locationtech.udig.feature.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyViewer;

/**
 * Viewer representing the feature panel page. On the left side it contains a list of tabs and on
 * the right side it contains the current selected tab.
 * <p>
 *
 * @see TabbedPropertyViewer
 * @author Jody Garnett
 * @author Anthony Hunter
 */
public class FeaturePanelViewer extends StructuredViewer {

    protected FeaturePanelList list;

    protected List<FeaturePanelTabDescriptor> elements;

    protected IWorkbenchPart part;

    /**
     * Constructor for FeaturePanelViewer.
     *
     * @param list the FeaturePanelViewer.
     */
    public FeaturePanelViewer(FeaturePanelList list) {
        this.list = list;
        hookControl(list);
        elements = new ArrayList<>();
    }

    /**
     * Returns the element with the given index from this list viewer. Returns <code>null</code> if
     * the index is out of range.
     *
     * @param index the zero-based index
     * @return the element at the given index, or <code>null</code> if the index is out of range
     */
    public Object getElementAt(int index) {
        if (index >= 0 && index < elements.size()) {
            return elements.get(index);
        }
        return null;
    }

    /**
     * Returns the zero-relative index of the item which is currently selected in the receiver, or
     * -1 if no item is selected.
     *
     * @return the index of the selected item
     */
    public int getSelectionIndex() {
        return list.getSelectionIndex();
    }

    @Override
    protected Widget doFindInputItem(Object element) {
        /* not implemented */
        return null;
    }

    @Override
    protected Widget doFindItem(Object element) {
        /* not implemented */
        return null;
    }

    @Override
    protected void doUpdateItem(Widget item, Object element, boolean fullMap) {
        /* not implemented */
    }

    @Override
    protected List<Object> getSelectionFromWidget() {
        int index = list.getSelectionIndex();
        if (index == FeaturePanelList.NONE) {
            return Collections.EMPTY_LIST;
        }
        List<Object> result = new ArrayList<>(1);
        result.add(getElementAt(index));
        return result;
    }

    @Override
    protected void internalRefresh(Object element) {
        /* not implemented */
    }

    @Override
    public void reveal(Object element) {
        /* not implemented */
    }

    /**
     * We do not consider multiple selections. Only the first element will represent the selection.
     */
    @Override
    protected void setSelectionToWidget(List l, boolean reveal) {
        if (l == null || l.isEmpty()) { // clear selection
            list.deselectAll();
        } else {
            Object object = l.get(0);
            int index = -1;
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i) == object) {
                    index = i;
                }
            }
            Assert.isTrue(index != -1,
                    "Could not set the selected tab in the feature panel viewer");//$NON-NLS-1$
            list.select(index);
        }
    }

    @Override
    public Control getControl() {
        return list;
    }

    @Override
    protected void inputChanged(Object input, Object oldInput) {
        elements.clear();
        Object[] children = getSortedChildren(getRoot());
        list.removeAll();
        for (int i = 0; i < children.length; i++) {
            elements.add((FeaturePanelTabDescriptor) children[i]);
            mapElement(children[i], list);
        }
        list.setElements(children);
    }

    /**
     * Set the input for viewer.
     *
     * @param part the workbench part.
     * @param selection the selection in the workbench part.
     */
    public void setInput(IWorkbenchPart part, ISelection selection) {
        this.part = part;
        setInput(selection);
    }

    /**
     * Get the current workbench part.
     *
     * @return the current workbench part.
     */
    public IWorkbenchPart getWorkbenchPart() {
        return part;
    }

    /**
     * Returns the elements in this list viewer.
     *
     * @return the elements in this list viewer.
     * @since 3.5
     */
    public List<FeaturePanelTabDescriptor> getElements() {
        return elements;
    }
}
