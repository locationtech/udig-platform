/**
 * <copyright>
 * </copyright>
 *
 * $Id: ViewportModelItemProvider.java 24145 2007-02-01 18:03:34Z jeichar $
 */
package net.refractions.udig.project.internal.render.provider;

import java.util.Collection;
import java.util.List;

import net.refractions.udig.project.internal.provider.ProjectEditPlugin;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.internal.render.ViewportModel;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a
 * {@link net.refractions.udig.project.internal.render.ViewportModel} object. <!-- begin-user-doc
 * --> <!-- end-user-doc -->
 *
 * @generated
 */
public class ViewportModelItemProvider extends ItemProviderAdapter
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
    public ViewportModelItemProvider( AdapterFactory adapterFactory ) {
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

            addCRSPropertyDescriptor(object);
            addBoundsPropertyDescriptor(object);
            addCenterPropertyDescriptor(object);
            addHeightPropertyDescriptor(object);
            addWidthPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the CRS feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    protected void addCRSPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_cRS_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_cRS_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.eINSTANCE.getViewportModel_CRS(), true,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Bounds feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    protected void addBoundsPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_bounds_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_bounds_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.eINSTANCE.getViewportModel_Bounds(), true,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Center feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated NOT
     */
    protected void addCenterPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(new ItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_center_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_center_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.eINSTANCE.getViewportModel_Center(), false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE));
    }

    /**
     * This adds a property descriptor for the Height feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated NOT
     */
    protected void addHeightPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(new ItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_height_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_height_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.eINSTANCE.getViewportModel_Height(), false,
                        ItemPropertyDescriptor.REAL_VALUE_IMAGE));
    }

    /**
     * This adds a property descriptor for the Width feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated NOT
     */
    protected void addWidthPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(new ItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_width_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_width_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.eINSTANCE.getViewportModel_Width(), false,
                        ItemPropertyDescriptor.REAL_VALUE_IMAGE));
    }

    /**
     * This adds a property descriptor for the Aspect Ratio feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    protected void addAspectRatioPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_aspectRatio_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_aspectRatio_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.eINSTANCE.getViewportModel_AspectRatio(), false,
                        ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Pixel Size feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    protected void addPixelSizePropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_pixelSize_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_pixelSize_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.eINSTANCE.getViewportModel_PixelSize(), false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Render Manager Internal feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @generated
     */
    protected void addRenderManagerInternalPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_renderManagerInternal_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_renderManagerInternal_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.eINSTANCE.getViewportModel_RenderManagerInternal(), true,
                        null, null, null));
    }

    /**
     * This returns ViewportModel.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/ViewportModel"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     *
     * @generated NOT
     */
    public String getText( Object object ) {
        return "ViewportModel";

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

        switch( notification.getFeatureID(ViewportModel.class) ) {
        case RenderPackage.VIEWPORT_MODEL__CRS:
        case RenderPackage.VIEWPORT_MODEL__BOUNDS:
        case RenderPackage.VIEWPORT_MODEL__CENTER:
        case RenderPackage.VIEWPORT_MODEL__HEIGHT:
        case RenderPackage.VIEWPORT_MODEL__WIDTH:
        case RenderPackage.VIEWPORT_MODEL__ASPECT_RATIO:
        case RenderPackage.VIEWPORT_MODEL__PIXEL_SIZE:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(),
                    false, true));
            return;
        }
        super.notifyChanged(notification);
    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s
     * describing all of the children that can be created under this object. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected void collectNewChildDescriptors( Collection newChildDescriptors, Object object ) {
        super.collectNewChildDescriptors(newChildDescriptors, object);
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
