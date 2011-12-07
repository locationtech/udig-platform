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

import net.refractions.udig.core.filter.AdaptingFilter;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectElement;
import net.refractions.udig.project.ui.internal.actions.Rename;

import org.eclipse.emf.common.ui.action.WorkbenchWindowActionDelegate;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Calls implemented operate method on the each element of selection.
 * 
 * @see Rename
 * @author jeichar
 * @since 0.6.0
 */
public abstract class UDIGGenericAction extends WorkbenchWindowActionDelegate {
    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action ) {
        ISelection sel = getSelection();
        if( sel == null || sel.isEmpty() || !(sel instanceof IStructuredSelection)){
            return;
        }
        IStructuredSelection selection = (IStructuredSelection) sel;
        
        /*
         * Optimization for a set of objects in selection of the same nature. The goal: run an
         * operation once over all selected objects.
         */
        ArrayList<Layer> layers = new ArrayList<Layer>(selection.size());
        
        Object firstElem = selection.iterator().next();
        
        Object stateData; 
        if (firstElem instanceof Project) {
            stateData = showErrorMessage(selection.size(), (Project) firstElem);
        } else if (firstElem instanceof IProjectElement) {
            stateData = showErrorMessage(selection.size(), (ProjectElement) firstElem);
        } else if (firstElem instanceof Layer) {
            stateData = showErrorMessage(selection.size(), (Layer) firstElem);
        } else if (firstElem instanceof SimpleFeature) {
            stateData = showErrorMessage(selection.size(), (SimpleFeature) firstElem);
        } else if (firstElem instanceof AdaptingFilter) {
            AdaptingFilter<?> f = (AdaptingFilter<?>) firstElem;
            ILayer layer = (ILayer) f.getAdapter(ILayer.class);
            stateData = showErrorMessage(selection.size(), layer,f);
        } else {
            stateData = null;
        }
        
        for( Iterator<?> iter = selection.iterator(); iter.hasNext(); ) {
            Object element = iter.next();

            if (element instanceof Project) {
                operate((Project) element, stateData);
            } else if (element instanceof IProjectElement) {
                operate((ProjectElement) element, stateData);
            } else if (element instanceof Layer) {
                layers.add((Layer) element);
            } else if (element instanceof SimpleFeature) {
                operate((SimpleFeature) element, stateData);
            }else if (element instanceof AdaptingFilter) {
                AdaptingFilter<?> f = (AdaptingFilter<?>) element;
                ILayer layer = (ILayer) f.getAdapter(ILayer.class);
                operate(layer,f, stateData);
            }
        }

        if (!layers.isEmpty()) {
            operate(layers.toArray(new Layer[layers.size()]), stateData);
        }

        // layers = null;
    }

    /**
     * Called before operation.  Default implementation does nothing
     *
     * @param size number of items to operate on
     * @param firstElement one sample object
     * @return an object to pass to the corresponding operate method
     */
    protected Object showErrorMessage( int size, Project firstElement ) {
        //do nothing
        return null;
    }

    /**
     * Called before operation.  Default implementation does nothing
     *
     * @param size number of items to operate on
     * @param firstElement one sample object
     * @return an object to pass to the corresponding operate method
     */
    protected Object showErrorMessage( int size, Layer firstElement ) {
        //do nothing
        return null;
    }

    /**
     * Called before operation.  Default implementation does nothing
     *
     * @param size number of items to operate on
     * @param firstElement one sample object
     * @return an object to pass to the corresponding operate method
     */
    protected Object showErrorMessage( int size, SimpleFeature firstElement ) {
        //do nothing
        return null;
    }

    /**
     * Called before operation.  Default implementation does nothing
     *
     * @param size number of items to operate on
     * @param firstElement one sample object
     * @return an object to pass to the corresponding operate method
     */
    protected Object showErrorMessage( int size, ProjectElement firstElement ) {
        //do nothing
        return null;
    }

    /**
     * Called before operation.  Default implementation does nothing
     *
     * @param size number of items to operate on
     * @param firstElement one sample object
     */
    protected Object showErrorMessage( int size, ILayer layer, AdaptingFilter firstElement ) {
        //do nothing
        return null;
    }

    /**
     * Operates on a filter and associated layer. Default Implementation does nothing.
     */
    protected void operate( ILayer layer, AdaptingFilter filter, Object context) {
        // do nothing
    }

    /**
     * Operates on a SimpleFeature. Default Implementation does nothing.
     * 
     * @param feature
     */
    protected void operate( SimpleFeature feature, Object context ) {
        // do nothing
    }

    /**
     * Operates on an array of layers.
     * <p>
     * Default Implementation does nothing.
     * 
     * @param layers
     */
    protected void operate( Layer[] layers, Object context ) {
        // do nothing
    }

    /**
     * Operates on a IProjectElement. Default Implementation does nothing.
     * 
     * @param element
     */
    protected void operate( ProjectElement element, Object context ) {
        // do nothing
    }

    /**
     * Operates on a Project. Default Implementation does nothing.
     * 
     * @param project
     */
    protected void operate( Project project, Object context ) {
        // do nothing
    }

}
