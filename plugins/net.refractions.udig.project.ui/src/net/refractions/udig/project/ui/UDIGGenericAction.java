/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui;

import java.util.ArrayList;
import java.util.Iterator;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectElement;
import net.refractions.udig.project.internal.commands.edit.DeleteManyFeaturesCommand;
import net.refractions.udig.project.ui.internal.AdaptingFilter;
import net.refractions.udig.project.ui.internal.actions.Rename;

import org.eclipse.emf.common.ui.action.WorkbenchWindowActionDelegate;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.geotools.feature.Feature;

/**
 * Calls implemented operate method on the each element of selection
 *
 * @see Rename
 * @author jeichar
 * @since 0.6.0
 */
public abstract class UDIGGenericAction extends WorkbenchWindowActionDelegate {

    private IStructuredSelection selection;

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action ) {

        /*
         * TODO: Optimization for a set of objects in selection of the same nature. The goal: run an
         * operation once over all selected objects.
         */
        ArrayList<Layer> layers = new ArrayList<Layer>(selection.size());

        for( Iterator iter = selection.iterator(); iter.hasNext(); ) {
            Object element = iter.next();

            if (element instanceof Project) {
                operate((Project) element);
            } else if (element instanceof IProjectElement) {
                operate((ProjectElement) element);
            } else if (element instanceof Layer) {
                layers.add((Layer) element);
            } else if (element instanceof Feature) {
                operate((Feature) element);
            }
            if (element instanceof AdaptingFilter) {
                AdaptingFilter f = (AdaptingFilter) element;
                ILayer layer = (ILayer) f.getAdapter(ILayer.class);
                layer.getMap().sendCommandASync(new DeleteManyFeaturesCommand(layer, f));
            }

        }

        if (!layers.isEmpty()) {
            operate(layers.toArray(new Layer[layers.size()]));
        }

        // layers = null;
    }

    /**
     * Operates on a Feature. Default Implementation does nothing.
     *
     * @param feature
     */
    protected void operate( Feature feature ) {
        // do nothing
    }

    /**
     * Operates on a layer. Default Implementation does nothing.
     *
     * @param layer
     * @deprecated
     */
    protected void operate( Layer layer ) {
        // do nothing
    }

    /**
     * Operates on an array of layers.
     * <p>
     * Default Implementation does nothing.
     *
     * @param layers
     */
    protected void operate( Layer[] layers ) {
        // do nothing
    }

    /**
     * Operates on a IProjectElement. Default Implementation does nothing.
     *
     * @param element
     */
    protected void operate( ProjectElement element ) {
        // do nothing
    }

    /**
     * Operates on a Project. Default Implementation does nothing.
     *
     * @param project
     */
    protected void operate( Project project ) {
        // do nothing
    }

    /**
     * @see org.eclipse.emf.common.ui.action.WorkbenchWindowActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection ) {
        if (selection instanceof IStructuredSelection)
            this.selection = (IStructuredSelection) selection;
    }

}
