/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.udig.omsbox.view.actions;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;

import eu.udig.omsbox.OmsBoxPlugin;
import eu.udig.omsbox.view.OmsBoxView;

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
                .find("eu.udig.omsbox.view.actions.ProcessingRegionToggleAction");
        return ((ActionContributionItem) item).getAction();
    }

}