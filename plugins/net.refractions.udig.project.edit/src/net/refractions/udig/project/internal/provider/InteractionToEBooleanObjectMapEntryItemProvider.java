/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
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
 * 
 */
package net.refractions.udig.project.internal.provider;

import java.util.List;
import java.util.Map;

import net.refractions.udig.project.internal.ProjectPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link java.util.Map.Entry} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class InteractionToEBooleanObjectMapEntryItemProvider extends ItemProviderAdapter
        implements
            IEditingDomainItemProvider,
            IStructuredItemContentProvider,
            ITreeItemContentProvider,
            IItemLabelProvider,
            IItemPropertySource {
    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public InteractionToEBooleanObjectMapEntryItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public List<IItemPropertyDescriptor> getPropertyDescriptors( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addKeyPropertyDescriptor(object);
            addValuePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Key feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addKeyPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_InteractionToEBooleanObjectMapEntry_key_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_InteractionToEBooleanObjectMapEntry_key_feature", "_UI_InteractionToEBooleanObjectMapEntry_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.INTERACTION_TO_EBOOLEAN_OBJECT_MAP_ENTRY__KEY,
                        true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Value feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addValuePropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_InteractionToEBooleanObjectMapEntry_value_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_InteractionToEBooleanObjectMapEntry_value_feature", "_UI_InteractionToEBooleanObjectMapEntry_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.INTERACTION_TO_EBOOLEAN_OBJECT_MAP_ENTRY__VALUE,
                        true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This returns InteractionToEBooleanObjectMapEntry.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return overlayImage(object,
                getResourceLocator().getImage("full/obj16/InteractionToEBooleanObjectMapEntry")); //$NON-NLS-1$
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected boolean shouldComposeCreationImage() {
        return true;
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getText( Object object ) {
        Map.Entry< ? , ? > interactionToEBooleanObjectMapEntry = (Map.Entry< ? , ? >) object;
        return "" + interactionToEBooleanObjectMapEntry.getKey() + " -> " + interactionToEBooleanObjectMapEntry.getValue(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached
     * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void notifyChanged( Notification notification ) {
        updateChildren(notification);

        switch( notification.getFeatureID(Map.Entry.class) ) {
        case ProjectPackage.INTERACTION_TO_EBOOLEAN_OBJECT_MAP_ENTRY__KEY:
        case ProjectPackage.INTERACTION_TO_EBOOLEAN_OBJECT_MAP_ENTRY__VALUE:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(),
                    false, true));
            return;
        }
        super.notifyChanged(notification);
    }

    /**
     * Return the resource locator for this item provider's resources.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return ProjectEditPlugin.INSTANCE;
    }

}
