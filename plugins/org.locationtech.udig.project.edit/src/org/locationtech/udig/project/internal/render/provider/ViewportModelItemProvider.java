/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.locationtech.udig.project.internal.render.provider;

import java.util.Collection;
import java.util.List;

import org.locationtech.udig.project.internal.provider.ProjectEditPlugin;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.ViewportModel;

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
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * This is the item provider adapter for a {@link org.locationtech.udig.project.internal.render.ViewportModel} object.
 * <!-- begin-user-doc
 * --> <!-- end-user-doc -->
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
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
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
     * This adds a property descriptor for the CRS feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
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
                        RenderPackage.Literals.VIEWPORT_MODEL__CRS, true, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Bounds feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
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
                        RenderPackage.Literals.VIEWPORT_MODEL__BOUNDS, true, false, false,
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
     * This adds a property descriptor for the Aspect Ratio feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
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
                        RenderPackage.Literals.VIEWPORT_MODEL__ASPECT_RATIO, false, false, false,
                        ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Pixel Size feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
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
                        RenderPackage.Literals.VIEWPORT_MODEL__PIXEL_SIZE, false, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Render Manager Internal feature.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
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
                        RenderPackage.Literals.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL, true,
                        false, false, null, null, null));
    }

    /**
     * This adds a property descriptor for the Preferred Scale Denominators feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addPreferredScaleDenominatorsPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_preferredScaleDenominators_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_preferredScaleDenominators_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.Literals.VIEWPORT_MODEL__PREFERRED_SCALE_DENOMINATORS, true,
                        false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Available Timesteps feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addAvailableTimestepsPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_availableTimesteps_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_availableTimesteps_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.Literals.VIEWPORT_MODEL__AVAILABLE_TIMESTEPS, true, false,
                        false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Current Timestep feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addCurrentTimestepPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_currentTimestep_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_currentTimestep_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.Literals.VIEWPORT_MODEL__CURRENT_TIMESTEP, true, false,
                        false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Available Elevation feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addAvailableElevationPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_availableElevation_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_availableElevation_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.Literals.VIEWPORT_MODEL__AVAILABLE_ELEVATION, true, false,
                        false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Current Elevation feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addCurrentElevationPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ViewportModel_currentElevation_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ViewportModel_currentElevation_feature", "_UI_ViewportModel_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        RenderPackage.Literals.VIEWPORT_MODEL__CURRENT_ELEVATION, true, false,
                        false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This returns ViewportModel.gif.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return overlayImage(object, getResourceLocator().getImage("full/obj16/ViewportModel")); //$NON-NLS-1$
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
     * @generated
     */
    @Override
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
        case RenderPackage.VIEWPORT_MODEL__PREFERRED_SCALE_DENOMINATORS:
        case RenderPackage.VIEWPORT_MODEL__AVAILABLE_TIMESTEPS:
        case RenderPackage.VIEWPORT_MODEL__CURRENT_TIMESTEP:
        case RenderPackage.VIEWPORT_MODEL__AVAILABLE_ELEVATION:
        case RenderPackage.VIEWPORT_MODEL__CURRENT_ELEVATION:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(),
                    false, true));
            return;
        }
        super.notifyChanged(notification);
    }

    /**
     * Return the resource locator for this item provider's resources.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return ProjectEditPlugin.INSTANCE;
    }

}
