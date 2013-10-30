/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.core;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * Used by a Command Handler to adapt the provided selection to the provided type.
 * <p>
 * Examples:
 * <ul>
 * <li>List the property pages for the current Map, when a layer (which can adapt to a Map) is selected</li>
 * </ul>
 * </p>
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public class SelectionProviderForwarder implements ISelectionProvider {
    protected ISelectionProvider provider;

    protected Class<?> forwardType;

    public SelectionProviderForwarder(ISelectionProvider provider, Class<?> forwardType) {
        this.provider = provider;
        this.forwardType = forwardType;
    }

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        provider.addSelectionChangedListener(listener);
    }

    @Override
    public ISelection getSelection() {
        ISelection selection = provider.getSelection();
        if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
            IStructuredSelection sel = (IStructuredSelection) selection;
            Object element = sel.getFirstElement();

            if (forwardType.isInstance(element)) {
                return selection;
            }
            // check IAdaptable incase ILayer or another selection wants to play
            if (element instanceof IAdaptable) {
                IAdaptable adaptable = (IAdaptable) element;

                Object value = adaptable.getAdapter(forwardType);
                if (value != null) {
                    return new StructuredSelection(value);
                }
            }
        }
        return StructuredSelection.EMPTY; // no dice!
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        provider.addSelectionChangedListener(listener);
    }

    @Override
    public void setSelection(ISelection selection) {
        provider.setSelection(selection);
    }
}