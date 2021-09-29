/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.locationtech.udig.catalog.ui.search.SearchView;

public class AOIViewActionDelegate extends Action implements IViewActionDelegate {

    private SearchView view;

    @Override
    public void run(IAction action) {
        filterTable(action.isChecked());
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {

    }

    @Override
    public void init(IViewPart view) {
        if (view != null && view instanceof SearchView) {
            this.view = (SearchView) view;
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        if (targetPart != null && targetPart instanceof SearchView) {
            view = (SearchView) targetPart;
        }
    }

    private void filterTable(boolean filter) {
        if (view != null) {
            view.setAOIFilter(filter);
        }
    }

}
