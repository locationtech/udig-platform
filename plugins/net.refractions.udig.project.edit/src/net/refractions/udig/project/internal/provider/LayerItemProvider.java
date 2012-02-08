/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.project.internal.provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.impl.LayerImpl;
import net.refractions.udig.project.preferences.PreferenceConstants;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.geotools.util.Range;

/**
 * This is the item provider adapter for a {@link net.refractions.udig.project.internal.Layer} object.
 * <!-- begin-user-doc --> The handling of this class is very important:
 * <ul>
 * <li>No method can block
 * <li>if you have to catch an IOException you are doing it wrong
 * <li>Responsible for providing an ImageDescriptor (not an Image)
 * </ul>
 * <!-- end-user-doc -->
 * @generated
 */
public class LayerItemProvider extends ItemProviderAdapter
        implements
            IEditingDomainItemProvider,
            IStructuredItemContentProvider,
            ITreeItemContentProvider,
            IItemLabelProvider,
            IItemPropertySource {

    /** Properties Key used to cache generated name in layer.getProperties() */
    public static final String GENERATED_NAME = "generated title"; //$NON-NLS-1$

    /** Propeties Key used to cache generated icon in layer.getProperties() */
    public static final String GENERATED_ICON = "generated icon"; //$NON-NLS-1$

    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    public LayerItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public List getPropertyDescriptors( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addZorderPropertyDescriptor(object);
            addNamePropertyDescriptor(object);
            addVisiblePropertyDescriptor(object);
            addGlyphPropertyDescriptor(object);
            addFilterPropertyDescriptor(object);
            addStatusPropertyDescriptor(object);
            addCRSPropertyDescriptor(object);
            // FIXME: Add new layer properties such as applicable
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Zorder feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     */
    @SuppressWarnings("unchecked")
    protected void addZorderPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_zorder_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_zorder_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.eINSTANCE.getLayer_Zorder(), true,
                        ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Name feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    protected void addNamePropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_name_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_name_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__NAME, true, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the ID feature. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     */
    protected void addIDPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_iD_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_iD_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__ID, true, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Visible feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    protected void addVisiblePropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_visible_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_visible_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__VISIBLE, true, false, false,
                        ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Geo Resource feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    protected void addGeoResourcePropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_geoResource_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_geoResource_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__GEO_RESOURCE, true, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Glyph feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    protected void addGlyphPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_glyph_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_glyph_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__GLYPH, true, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Geo Resources feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    protected void addGeoResourcesPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_geoResources_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_geoResources_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__GEO_RESOURCES, false, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Catalog Ref feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    protected void addCatalogRefPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_catalogRef_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_catalogRef_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__CATALOG_REF, true, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Filter feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    protected void addFilterPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_filter_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_filter_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__FILTER, false, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Status feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    protected void addStatusPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_status_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_status_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__STATUS, true, false, false,
                        ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
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
                        getString("_UI_Layer_cRS_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_cRS_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__CRS, true, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Properties feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    protected void addPropertiesPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_properties_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_properties_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__PROPERTIES, false, false, false, null, null,
                        null));
    }

    /**
     * This adds a property descriptor for the Colour Scheme feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    protected void addColourSchemePropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_colourScheme_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_colourScheme_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__COLOUR_SCHEME, true, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Default Color feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    protected void addDefaultColorPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_defaultColor_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_defaultColor_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__DEFAULT_COLOR, true, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Feature Changes feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    protected void addFeatureChangesPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_featureChanges_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_featureChanges_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__FEATURE_CHANGES, true, false, false,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Min Scale Denominator feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addMinScaleDenominatorPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_minScaleDenominator_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_minScaleDenominator_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__MIN_SCALE_DENOMINATOR, true, false, false,
                        ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Max Scale Denominator feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addMaxScaleDenominatorPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_maxScaleDenominator_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_maxScaleDenominator_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__MAX_SCALE_DENOMINATOR, true, false, false,
                        ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Shown feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addShownPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Layer_shown_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Layer_shown_feature", "_UI_Layer_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.Literals.LAYER__SHOWN, true, false, false,
                        ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
    }

    /**
     * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate
     * feature for an {@link org.eclipse.emf.edit.command.AddCommand},
     * {@link org.eclipse.emf.edit.command.RemoveCommand} or
     * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Collection getChildrenFeatures( Object object ) {
        if (childrenFeatures == null) {
            super.getChildrenFeatures(object);
        }
        return childrenFeatures;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EStructuralFeature getChildFeature( Object object, Object child ) {
        // Check the type of the specified child object and return the proper feature to use for
        // adding (see {@link AddCommand}) it as a child.

        return super.getChildFeature(object, child);
    }

    private Map<Layer, Data> cache = new HashMap<Layer, Data>();
    private static class Data {
        ImageDescriptor desc;
        Image image;
        public Data( ImageDescriptor desc, Image image2 ) {
            super();
            this.desc = desc;
            image = image2;
        }
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((desc == null) ? 0 : desc.hashCode());
            return result;
        }
        @Override
        public boolean equals( Object obj ) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            final Data other = (Data) obj;
            if (desc == null) {
                if (other.desc != null) return false;
            } else if (!desc.equals(other.desc)) return false;
            return true;
        }

    }
    /**
     * This returns Layer.gif. <!-- begin-user-doc --> Returns layers glyph property, requesting WMS
     * Legend Graphic will be handled by a decorator. <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Object getImage( Object object ) {
        Layer layer = (Layer) object;

        // Check for generated image - generated by our decorators
        // (lightweight decorators only seem to work with ImageDecorator)
        //
        // Decorator should try out any icon given to it by a third party and make us a image
        // (We don't want the ImageDescriptor to block on an external WMS)
        //
        ImageDescriptor image = layer.getGlyph();

        if (image == null) {
            image = (ImageDescriptor) layer.getProperties().get(GENERATED_ICON);
        }

        int outOfScaleModifier = SWT.IMAGE_DISABLE;

        if (image == null) {
            Object object2 = ProjectEditPlugin.INSTANCE.getImage("full/obj16/Layer");
            if (object2 instanceof ImageDescriptor) {
                image = (ImageDescriptor) object2;
            } else {
                if (object2 instanceof Image) {
                    image = ImageDescriptor.createFromImage((Image) object2);
                } else {
                    return object2;
                }
            }
        }

        if (outOfScale(layer)) {
            image = ImageDescriptor.createWithFlags(image, outOfScaleModifier);
        }

        return image; //$NON-NLS-1$
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

    @Override
    public void dispose() {
        Collection<Data> values = cache.values();
        for( Data data : values ) {
            try {
                if (data.image != null && !data.image.isDisposed()) data.image.dispose();
            } catch (Throwable e) {
                ProjectEditPlugin.log("Error disposing LayerItemProvider", e); //$NON-NLS-1$
            }
        }
        cache.clear();

        super.dispose();
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> Makes use of layer
     * label, will use name of GeoResource URL by default. A decorator can request the GeoResoruce's
     * title in thread. <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public String getText( Object object ) {
        Layer layer = (Layer) object;
        String label = layer.getName();

        if (label != null && label.length() != 0) return label;

        String title = (String) layer.getProperties().get("generated title"); //$NON-NLS-1$
        if (title != null) return title;

        // Okay have a default
        //
        // return "Untitled";
        return "Layer";
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached
     * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void notifyChanged( Notification notification ) {
        updateChildren(notification);

        switch( notification.getFeatureID(Layer.class) ) {
        case ProjectPackage.LAYER__NAME:
        case ProjectPackage.LAYER__ID:
        case ProjectPackage.LAYER__GEO_RESOURCE:
        case ProjectPackage.LAYER__PROPERTIES:
        case ProjectPackage.LAYER__STATUS:
        case ProjectPackage.LAYER__GLYPH:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(),
                    false, true));
            return;
        case ProjectPackage.LAYER__ZORDER:
            fireNotifyChanged(new ViewerNotification(notification,
                    ((ILayer) notification.getNotifier()).getMap(), true, false));
            return;
        case ProjectPackage.LAYER__STYLE_BLACKBOARD:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(),
                    true, false));
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

    public Color getBackground( Object element ) {
        if (element instanceof LayerImpl) {
            ScopedPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
            String highlightPref = store.getString(PreferenceConstants.P_HIGHLIGHT);
            if (highlightPref.equals(PreferenceConstants.P_HIGHLIGHT_NONE)) {
                return null;
            }
            LayerImpl layer = (LayerImpl) element;
            java.awt.Color awtColor = layer.getDefaultColor();
            if (awtColor == null) return null;
            if (highlightPref.equals(PreferenceConstants.P_HIGHLIGHT_FOREGROUND)) {
                return null; //not used yet (flip between black and white?)
            } else if (highlightPref.equals(PreferenceConstants.P_HIGHLIGHT_BACKGROUND)) {
                return new Color(PlatformUI.getWorkbench().getDisplay(), awtColor.getRed(),
                        awtColor.getGreen(), awtColor.getBlue());
            }
        }
        return null;
    }

    public Color getForeground( Object element ) {
        if (element instanceof LayerImpl) {

            LayerImpl layer = (LayerImpl) element;

            Color systemColor = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
            if (outOfScale(layer)) {
                return systemColor;
            }
            IMap map = layer.getMap();
            if (map == null) return null;
            boolean mylarOnAndAffectingLayer = mylarOnAndAffectingLayer(layer, map);

            ScopedPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
            String highlightPref = store.getString(PreferenceConstants.P_HIGHLIGHT);

            if (highlightPref.equals(PreferenceConstants.P_HIGHLIGHT_NONE)) {
                if (mylarOnAndAffectingLayer) return systemColor;
                return null;
            }

            float mylarEffect = 1.0f;
            java.awt.Color awtColor = layer.getDefaultColor();
            if (awtColor == null) return null;
            if (highlightPref.equals(PreferenceConstants.P_HIGHLIGHT_FOREGROUND)) {
                return new Color(PlatformUI.getWorkbench().getDisplay(), (int) mylarEffect
                        * awtColor.getRed(), (int) mylarEffect * awtColor.getGreen(),
                        (int) mylarEffect * awtColor.getBlue());
            } else if (highlightPref.equals(PreferenceConstants.P_HIGHLIGHT_BACKGROUND)) {
                if (awtColor.getRed() + awtColor.getGreen() + awtColor.getBlue() > 512) {
                    if (mylarOnAndAffectingLayer) {
                        return systemColor;
                    } else {
                        return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
                    }
                } else {
                    return new Color(PlatformUI.getWorkbench().getDisplay(),
                            (int) mylarEffect * 255, (int) mylarEffect * 255,
                            (int) mylarEffect * 255);
                }
            }
        }
        return null;
    }

    private boolean outOfScale( Layer layer ) {
        try {
            Set<Range> scales = layer.getScaleRange();
            if (scales.isEmpty()) {
                return false;
            }
            for( Range range : scales ) {
                if (range.contains(layer.getMap().getViewportModel().getScaleDenominator())) {
                    return false;
                }
            }
            return true;
        } catch (Throwable t) {
            ProjectPlugin.log("Could not aquire scale range", t);
            return false;
        }
    }

    /**
     * Returns true if mylar is "on" for the given layer.  "on" means that
     * the layer should be greyed out.
     * 
     * <p>
     * In the case of composite layers (wms for example); all layers associated
     * with the context should be "on"; which is why function thing needs to know about 
     * the renderers.
     * </p>
     *
     * @param layer
     * @param map
     * 
     * @return if mylar is turned on and it is affecting (greying out) the current layer
     */
    private boolean mylarOnAndAffectingLayer( LayerImpl layer, IMap map ) {

        //look at blackboard for mylar flag
        Object mylar = map.getBlackboard().get("MYLAR"); //$NON-NLS-1$
        boolean mylarIsOn = mylar != null && ((Boolean) mylar).booleanValue();

        if (mylarIsOn) {
            ILayer selectedLayer = map.getEditManager().getSelectedLayer();
            if (map.getRenderManager() == null) {
                return false;
            }
            return !map.getRenderManager().areLayersRelatedByContext(layer, selectedLayer);
        }
        return mylarIsOn;
    }

}
