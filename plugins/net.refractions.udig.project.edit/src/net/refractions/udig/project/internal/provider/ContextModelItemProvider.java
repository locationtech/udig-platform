/**
 * <copyright>
 * </copyright>
 *
 * $Id: ContextModelItemProvider.java 24145 2007-02-01 18:03:34Z jeichar $
 */
package net.refractions.udig.project.internal.provider;

import java.util.Collection;
import java.util.List;

import net.refractions.udig.project.internal.ContextModel;
import net.refractions.udig.project.internal.ProjectPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a
 * {@link net.refractions.udig.project.internal.ContextModel} object. <!-- begin-user-doc --> <!--
 * end-user-doc -->
 *
 * @generated
 */
public class ContextModelItemProvider extends ItemProviderAdapter
        implements
            IEditingDomainItemProvider,
            IStructuredItemContentProvider,
            ITreeItemContentProvider,
            IItemLabelProvider,
            IItemPropertySource {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    public ContextModelItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated NOT
     */
    public List getPropertyDescriptors( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

        }
        return itemPropertyDescriptors;
    }


    /**
     * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate
     * feature for an {@link org.eclipse.emf.edit.command.AddCommand},
     * {@link org.eclipse.emf.edit.command.RemoveCommand} or
     * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public Collection getChildrenFeatures( Object object ) {
        if (childrenFeatures == null) {
            super.getChildrenFeatures(object);
            childrenFeatures.add(ProjectPackage.eINSTANCE.getContextModel_Layers());
        }
        return childrenFeatures;
    }

    /**
     * This returns ContextModel.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/ContextModel"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     *
     * @generated NOT
     */
    public String getText( Object object ) {
        return "Context Model";
    }

    @Override
    protected void updateChildren( Notification notification ) {
        super.updateChildren(notification);
        switch( notification.getEventType() ) {
        case Notification.ADD:
        case Notification.ADD_MANY:
        case Notification.REMOVE:
        case Notification.REMOVE_MANY:
        case Notification.MOVE:

            Object notifier=notification.getNotifier();
            if( notifier instanceof ContextModel ){
                if( notification.getFeatureID(ContextModel.class)!=ProjectPackage.CONTEXT_MODEL__LAYERS)
                    return ;

                // we need to tell the map item provider that the layers have changed.
                ContextModel model=(ContextModel) notifier;
                EList adapters = model.getMap().eAdapters();
                for( Object object : adapters ) {
                    if( object instanceof MapItemProvider ){
                        MapItemProvider mapItemProvider = ((MapItemProvider)object);
//                        mapItemProvider.updateChildList(notification);
                        mapItemProvider.getChildFetcher().notifyChanged();
                        break;
                    }
                }
            }else{
                ProjectEditPlugin.log("notifier is not a contextModel as expect.  It is a "+notifier.getClass().getSimpleName(), null); //$NON-NLS-1$
            }
            break;

        default:
            break;
        }
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached
     * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public void notifyChanged( Notification notification ) {
        updateChildren(notification);

        switch( notification.getFeatureID(ContextModel.class) ) {
        case ProjectPackage.CONTEXT_MODEL__LAYERS:
            fireNotifyChanged(new ViewerNotification(notification, ((ContextModel)notification.getNotifier()).getMap(),
                    true, false));
            return;
        }
        super.notifyChanged(notification);
    }

    /**
     * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    public ResourceLocator getResourceLocator() {
        return ProjectEditPlugin.INSTANCE;
    }

}
