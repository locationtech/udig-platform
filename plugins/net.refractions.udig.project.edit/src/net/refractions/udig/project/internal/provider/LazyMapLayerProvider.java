/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.project.internal.provider;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.project.edit.internal.Messages;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerDecorator;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.impl.SynchronizedEList;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.internal.render.provider.ViewportModelItemProvider;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

/**
 * This is a wrapper around the genrated MapItemProvider allowing us to lazily fetch children
 * using a {@link #createChildFetcher()} Job.
 */
public class LazyMapLayerProvider extends AbstractLazyLoadingItemProvider
        implements
            IEditingDomainItemProvider,
            IStructuredItemContentProvider,
            ITreeItemContentProvider,
            IItemLabelProvider,
            IItemPropertySource {

    /**
     * Placeholder that will be placed in viewers while layers are being loaded.
     * 
     * @author Jesse
     * @since 1.1.0
     */
    public static class LayerLoadingPlaceHolder extends LayerDecorator
            implements
                LoadingPlaceHolder {

        public LayerLoadingPlaceHolder( Layer layer ) {
            super(layer);
        }

        public Image getImage() {
            return getIcon().createImage();
        }

        public String getText() {
            return getName();
        }

        public int getZorder() {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Allows the property descriptor of the viewport model to appear as part of the map (User does
     * not need to know that there is a viewport model)
     * 
     * @author jones
     * @since 0.6.0
     */
    private static class ViewportModelDescriptor implements IItemPropertyDescriptor {

        private ItemPropertyDescriptor element;

        /**
         * Construct <code>ViewportModelDescriptor</code>.
         * 
         * @param element
         */
        public ViewportModelDescriptor( ItemPropertyDescriptor element ) {
            this.element = element;
        }

        ViewportModel getModel( Object obj ) {
            return ((Map) obj).getViewportModelInternal();
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#getPropertyValue(java.lang.Object)
         */
        public Object getPropertyValue( Object object ) {
            return element.getPropertyValue(getModel(object));
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#isPropertySet(java.lang.Object)
         */
        public boolean isPropertySet( Object object ) {
            return element.isPropertySet(getModel(object));
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#canSetProperty(java.lang.Object)
         */
        public boolean canSetProperty( Object object ) {
            return element.canSetProperty(getModel(object));
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#resetPropertyValue(java.lang.Object)
         */
        public void resetPropertyValue( Object object ) {
            element.resetPropertyValue(getModel(object));
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#setPropertyValue(java.lang.Object,
         *      java.lang.Object)
         */
        public void setPropertyValue( Object object, Object value ) {
            element.setPropertyValue(getModel(object), value);
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#getCategory(java.lang.Object)
         */
        public String getCategory( Object object ) {
            return element.getCategory(getModel(object));
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#getDescription(java.lang.Object)
         */
        public String getDescription( Object object ) {
            return element.getDescription(getModel(object));
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#getDisplayName(java.lang.Object)
         */
        public String getDisplayName( Object object ) {
            return element.getDisplayName(getModel(object));
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#getFilterFlags(java.lang.Object)
         */
        public String[] getFilterFlags( Object object ) {
            return element.getFilterFlags(getModel(object));
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#getHelpContextIds(java.lang.Object)
         */
        public Object getHelpContextIds( Object object ) {
            return element.getHelpContextIds(getModel(object));
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#getId(java.lang.Object)
         */
        public String getId( Object object ) {
            return element.getId(getModel(object));
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#getLabelProvider(java.lang.Object)
         */
        public IItemLabelProvider getLabelProvider( Object object ) {
            return element.getLabelProvider(getModel(object));
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#isCompatibleWith(java.lang.Object,
         *      java.lang.Object, org.eclipse.emf.edit.provider.IItemPropertyDescriptor)
         */
        public boolean isCompatibleWith( Object object, Object anotherObject,
                IItemPropertyDescriptor anotherPropertyDescriptor ) {
            return element.isCompatibleWith(getModel(object), anotherObject,
                    anotherPropertyDescriptor);
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#getFeature(java.lang.Object)
         */
        public Object getFeature( Object object ) {
            return element.getFeature(getModel(object));
        }

        /**
         * @see org.eclipse.emf.edit.provider.IItemPropertyDescriptor#getChoiceOfValues(java.lang.Object)
         */
        public Collection getChoiceOfValues( Object object ) {
            return element.getChoiceOfValues(getModel(object));
        }

        public boolean isMany( Object arg0 ) {
            return element.isMany(arg0);
        }

        public boolean isMultiLine( Object arg0 ) {
            return element.isMultiLine(arg0);
        }

        public boolean isSortChoices( Object arg0 ) {
            return element.isSortChoices(arg0);
        }
    }
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$
    ViewportModelItemProvider modelProvider;
    /**
     * This constructs an instance from a factory and a notifier.
     */
    public LazyMapLayerProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
        modelProvider = new ViewportModelItemProvider(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class.  <!--
     * end-user-doc -->
     * 
     * 
     */
    public List getPropertyDescriptors( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);
            addAbstractPropertyDescriptor(object);
            addNamePropertyDescriptor(object);
            addViewportModelDescriptors((Map) object);
        }
        return itemPropertyDescriptors;
    }
    @SuppressWarnings("unchecked")
    private void addViewportModelDescriptors( Map map ) {
        for( Iterator iter = modelProvider.getPropertyDescriptors(map.getViewportModel())
                .iterator(); iter.hasNext(); ) {
            ItemPropertyDescriptor element = (ItemPropertyDescriptor) iter.next();
            itemPropertyDescriptors.add(new ViewportModelDescriptor(element));
        }
    }

    /**
     * This adds a property descriptor for the Abstract feature.  <!--
     * end-user-doc -->
     * 
     * 
     */
    @SuppressWarnings("unchecked")
    protected void addAbstractPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Map_abstract_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Map_abstract_feature", "_UI_Map_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.eINSTANCE.getMap_Abstract(), true,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Name feature.  <!--
     * end-user-doc -->
     * 
     * 
     */
    @SuppressWarnings("unchecked")
    protected void addNamePropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ProjectElement_name_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ProjectElement_name_feature", "_UI_ProjectElement_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.eINSTANCE.getProjectElement_Name(), true,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Nav Command Stack feature. 
     * 
     * 
     * 
     */
    @SuppressWarnings("unchecked")
    protected void addNavCommandStackPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Map_navCommandStack_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Map_navCommandStack_feature", "_UI_Map_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.eINSTANCE.getMap_NavCommandStack(), false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Command Stack feature.  <!--
     * end-user-doc -->
     * 
     * 
     */
    @SuppressWarnings("unchecked")
    protected void addCommandStackPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Map_commandStack_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Map_commandStack_feature", "_UI_Map_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.eINSTANCE.getMap_CommandStack(), false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Color Palette feature.  <!--
     * end-user-doc -->
     * 
     * 
     */
    @SuppressWarnings("unchecked")
    protected void addColorPalettePropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Map_colorPalette_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Map_colorPalette_feature", "_UI_Map_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.eINSTANCE.getMap_ColorPalette(), true,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Render Manager Internal feature. <!-- begin-user-doc
     * --> 
     * 
     * 
     */
    protected void addRenderManagerInternalPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Map_renderManagerInternal_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Map_renderManagerInternal_feature", "_UI_Map_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.eINSTANCE.getMap_RenderManagerInternal(), true, null, null,
                        null));
    }

    /**
     * This adds a property descriptor for the Colour Scheme feature.  <!--
     * end-user-doc -->
     * 
     * 
     */
    protected void addColourSchemePropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Map_colourScheme_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Map_colourScheme_feature", "_UI_Map_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.eINSTANCE.getMap_ColourScheme(), true,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Black Board Internal feature. 
     * 
     * 
     * 
     */
    protected void addBlackBoardInternalPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(
                ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                getResourceLocator(), "blackboard", "blackboard ",
                ProjectPackage.eINSTANCE.getMap_BlackBoardInternal(), true, null, null, null));
    }

    /**
     * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate
     * feature for an {@link org.eclipse.emf.edit.command.AddCommand},
     * {@link org.eclipse.emf.edit.command.RemoveCommand} or
     * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}. <!--
     * begin-user-doc --> Changed so only layers and viewport model appears. 
     * 
     * 
     */
    public Collection getChildrenFeatures( Object object ) {
        if (childrenFeatures == null) {
            super.getChildrenFeatures(object);
            childrenFeatures.add(ProjectPackage.eINSTANCE.getMap_ContextModel());
        }
        return childrenFeatures;
    }

    /**
     *  
     * 
     * 
     */
    protected EStructuralFeature getChildFeature( Object object, Object child ) {
        // Check the type of the specified child object and return the proper feature to use for
        // adding (see {@link AddCommand}) it as a child.

        return super.getChildFeature(object, child);
    }

    /**
     * This returns Map.gif.  
     * 
     * 
     */
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/Map"); //$NON-NLS-1$
    }

    public static final LayerLoadingPlaceHolder LOADING_LAYER;
    static {
        LOADING_LAYER = new LayerLoadingPlaceHolder(ProjectFactory.eINSTANCE.createLayer());
        LOADING_LAYER.setName(Messages.ProjectItemProvider_loading);
        Bundle bundle = ProjectEditPlugin.getPlugin().getBundle();

        IPath path = new Path("icons/full/obj16/Layer.gif"); //$NON-NLS-1$
        ImageDescriptor image = ImageDescriptor.createFromURL(FileLocator.find(bundle, path, null));
        LOADING_LAYER.setIcon(image);
        LOADING_LAYER.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    protected net.refractions.udig.project.internal.provider.LoadingPlaceHolder getLoadingItem() {
        return LOADING_LAYER;
    };

    protected ChildFetcher createChildFetcher() {
        return new ChildFetcher(this){
            protected void notifyChanged() {
                LazyMapLayerProvider.this.notifyChanged(new ENotificationImpl((InternalEObject) parent,
                        Notification.SET, ProjectPackage.MAP__CONTEXT_MODEL, LOADING_LAYER, null));
            }
            @SuppressWarnings("unchecked")
            @Override
            protected IStatus run( IProgressMonitor monitor ) {
                if (parent instanceof Map) {
                    Map map = (Map) parent;
                    boolean found = false;
                    SynchronizedEList adapters = (SynchronizedEList) map.getContextModel()
                            .eAdapters();
                    adapters.lock();
                    try {
                        for( Iterator<Adapter> iter = adapters.iterator(); iter.hasNext(); ) {
                            Adapter next = iter.next();
                            if (next instanceof ContextModelItemProvider
                                    && ((ContextModelItemProvider) next).getAdapterFactory() == getAdapterFactory())
                                found = true;
                        }
                    } finally {
                        adapters.unlock();
                    }
                    if (!found) {
                        ContextModelItemProvider provider = new ContextModelItemProvider(
                                getAdapterFactory());
                        adapters.add(provider);
                    }
                }

                IStatus result = super.run(monitor);
                return result;
            }
        };
    }

    @Override
    protected Collection< ? extends Object> getConcreteChildren( Object object ) {
        if (object instanceof Map) return ((Map) object).getMapLayers();
        throw new IllegalArgumentException("Object must be a Map.  Was: " + object); //$NON-NLS-1$
    }

    @Override
    public Object getChild( Object object, int childIndex ) {
        Map map = (Map) object;
        if (childIndex >= map.getMapLayers().size()) return null;
        return map.getMapLayers().get(childIndex);
    }

    @Override
    public boolean hasChildren( Object object ) {
        return true;
    }

    /**
     * This returns the label text for the adapted class.  <!-- end-user-doc
     * -->
     * 
     * 
     */
    public String getText( Object object ) {
        Map map = ((Map) object);
        String label = map.getName();
        if (label == null) {
            Resource resource = map.eResource();
            if (resource != null) {
                String toString = resource.toString();
                int lastSlash = toString.lastIndexOf(File.pathSeparator);
                if (lastSlash == -1) lastSlash = 0;
                label = toString.substring(lastSlash);
            }
        }
        return label == null || label.length() == 0 ? "Unable to load map" : label;
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached
     * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
     *  
     * 
     * 
     */
    public void notifyChanged( Notification notification ) {
        if (notification.getNewValue() == notification.getOldValue()
                || (notification.getNewValue() != null && notification.getNewValue().equals(
                        notification.getOldValue()))
                || (notification.getOldValue() != null && notification.getOldValue().equals(
                        notification.getNewValue()))) return;

        switch( notification.getFeatureID(Map.class) ) {
        case ProjectPackage.MAP__NAME:
        case ProjectPackage.MAP__ABSTRACT:
            //        case ProjectPackage.MAP__NAV_COMMAND_STACK:
            //        case ProjectPackage.MAP__COMMAND_STACK:
        case ProjectPackage.MAP__COLOR_PALETTE:
        case ProjectPackage.MAP__COLOUR_SCHEME:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(),
                    false, true));
            return;
        case ProjectPackage.MAP__CONTEXT_MODEL:
        case ProjectPackage.MAP__LAYER_FACTORY:
        case ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL:
        case ProjectPackage.MAP__EDIT_MANAGER_INTERNAL:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(),
                    true, false));
            return;
        }
        super.notifyChanged(notification);
    }

    /**
     * Return the resource locator for this item provider's resources.  
     * 
     * 
     */
    public ResourceLocator getResourceLocator() {
        return ProjectEditPlugin.INSTANCE;
    }

}
