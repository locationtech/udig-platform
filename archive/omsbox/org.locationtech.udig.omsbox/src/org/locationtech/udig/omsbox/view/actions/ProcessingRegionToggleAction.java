/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.view.actions;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;

import org.locationtech.udig.omsbox.OmsBoxPlugin;
import org.locationtech.udig.omsbox.view.OmsBoxView;

public class ProcessingRegionToggleAction implements IViewActionDelegate {

    private IViewPart view;

    private boolean isEnabled = false;

    public void init( IViewPart view ) {
        this.view = view;
    }

    public void run( IAction action ) {
        // if (view instanceof OmsBoxView) {
        // OmsBoxView dbView = (OmsBoxView) view;
        //
        // Shell shell = dbView.getSite().getShell();
        //
        // }

        isEnabled = !isEnabled;

        if (isEnabled) {
            // enable processing region use
            OmsBoxPlugin.getDefault().setDoIgnoreProcessingRegion(false);
        } else {
            // disable processing region use
            OmsBoxPlugin.getDefault().setDoIgnoreProcessingRegion(true);
        }

        getAction().setChecked(isEnabled);

    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }

    public IAction getAction() {
        IViewSite site = (IViewSite) this.view.getSite();
        IContributionItem item = site.getActionBars().getToolBarManager()
                .find("org.locationtech.udig.omsbox.view.actions.ProcessingRegionToggleAction");
        return ((ActionContributionItem) item).getAction();
    }

}
