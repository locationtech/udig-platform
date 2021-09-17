/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.locationtech.udig.internal.ui.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.ui.operations.OpAction;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.activities.WorkbenchActivityHelper;

/**
 * Provides content for viewers that wish to show Operations.
 */
public class OperationContentProvider implements ITreeContentProvider {

    /**
     * Child cache.  Map from Object->Object[].  Our hasChildren() method is
     * expensive so it's better to cache the results of getChildren().
     */
    private Map childMap = new HashMap();

    /**
     * Create a new instance of the ViewContentProvider.
     */
    public OperationContentProvider() {
        //no-op
    }

    public void dispose() {
        childMap.clear();
    }

    public Object[] getChildren(Object element) {
        Object[] children = (Object[]) childMap.get(element);
        if (children == null) {
            children = createChildren(element);
            childMap.put(element, children);
        }
        return children;
    }

    /**
     * Does the actual work of getChildren.
     *
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    private Object[] createChildren(Object element) {
        if (element instanceof OperationMenuFactory) {
            OperationMenuFactory opMenuFactory = (OperationMenuFactory) element;
            Map<String, OperationCategory> categoriesMap = opMenuFactory.getCategories();

            ArrayList filtered = new ArrayList();
            for (OperationCategory category : categoriesMap.values()) {
                if (!hasChildren(category))
                    continue;

                filtered.add(category);
            }
            filtered.addAll(opMenuFactory.getActions());
            Object[] topLevelElements = filtered.toArray(new Object[filtered
                    .size()]);

            // if there is only one category, return it's children directly
            if (topLevelElements.length == 1) {
                return getChildren(topLevelElements[0]);
            }
            return topLevelElements;
        } else if (element instanceof OperationCategory) {
            List<OpAction> actionList = ((OperationCategory) element).getActions();
            OpAction [] actions = actionList.toArray(new OpAction[actionList.size()]);
            if (actions != null) {
                ArrayList filtered = new ArrayList();
                for (int i = 0; i < actions.length; i++) {
                    OpAction action = actions[i];
                    if (!action.isEnabled() || WorkbenchActivityHelper.filterItem(action))
                        continue;
                    filtered.add(action);
                }
                return filtered.toArray();
            }
        }

        return new Object[0];
    }

    public Object[] getElements(Object element) {
        return getChildren(element);
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(java.lang.Object element) {
        if (element instanceof OperationMenuFactory)
            return true;
        else if (element instanceof OperationCategory) {
            if (getChildren(element).length > 0)
                return true;
        }
        return false;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        childMap.clear();
    }
}
