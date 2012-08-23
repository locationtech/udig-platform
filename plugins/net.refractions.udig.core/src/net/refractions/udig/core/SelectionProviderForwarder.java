/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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