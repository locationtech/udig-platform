/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.locationtech.udig.project.internal.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

/**
 * This is the original lazy map item provided gerneated from EMF with the super class replaced.
 * <p>
 * This is being preserved for the use of LayersView; but it is our intention to replace it with
 * LazyMapItemProvider (configured with a ChildFetcher for listing layers).
 */
public class MapItemProviderDecorator 
        implements
            IEditingDomainItemProvider,
            IStructuredItemContentProvider,
            ITreeItemContentProvider,
            IItemLabelProvider,
            IItemPropertySource {

    /**
     * The original generated MapItemProvider we are wrapping.
     */
    MapItemProvider delegate;

    @Override
    public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
        return delegate.getPropertyDescriptors(object);
    }

    @Override
    public IItemPropertyDescriptor getPropertyDescriptor(Object object, Object propertyID) {
        return delegate.getPropertyDescriptor(object, propertyID);
    }

    @Override
    public Object getEditableValue(Object object) {
        return delegate.getEditableValue(object);
    }

    @Override
    public String getText(Object object) {
        return delegate.getText(object);
    }

    @Override
    public Object getImage(Object object) {
        return delegate.getImage(object);
    }

    @Override
    public boolean hasChildren(Object object) {
        return delegate.hasChildren(object);
    }

    @Override
    public Collection<?> getElements(Object object) {
        return delegate.getElements(object);
    }

    @Override
    public Collection<?> getChildren(Object object) {
        return delegate.getChildren(object);
    }

    @Override
    public Object getParent(Object object) {
        return delegate.getParent(object);
    }

    @Override
    public Collection<?> getNewChildDescriptors(Object object, EditingDomain editingDomain, Object sibling) {
        return delegate.getNewChildDescriptors(object, editingDomain, sibling);
    }

    @Override
    public Command createCommand(Object object, EditingDomain editingDomain, Class<? extends Command> commandClass,
            CommandParameter commandParameter) {
        return delegate.createCommand(object, editingDomain, commandClass, commandParameter);
    }

}
