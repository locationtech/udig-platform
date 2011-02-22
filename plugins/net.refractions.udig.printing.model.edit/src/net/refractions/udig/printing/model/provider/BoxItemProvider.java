/**
 * <copyright>
 * </copyright>
 *
 * $Id: BoxItemProvider.java 23333 2006-12-08 19:40:41Z jeichar $
 */
package net.refractions.udig.printing.model.provider;


import java.util.Collection;
import java.util.List;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.ModelPackage;

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
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link net.refractions.udig.printing.model.Box} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class BoxItemProvider
    extends ElementItemProvider
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
    public BoxItemProvider(AdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getPropertyDescriptors(Object object) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addSourceConnectionsPropertyDescriptor(object);
            addTargetConnectionsPropertyDescriptor(object);
            addBoxPrinterPropertyDescriptor(object);
            addIDPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Source Connections feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addSourceConnectionsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_Box_sourceConnections_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_Box_sourceConnections_feature", "_UI_Box_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ModelPackage.eINSTANCE.getBox_SourceConnections(),
                 true,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Target Connections feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addTargetConnectionsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_Box_targetConnections_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_Box_targetConnections_feature", "_UI_Box_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ModelPackage.eINSTANCE.getBox_TargetConnections(),
                 true,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Box Printer feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addBoxPrinterPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_Box_boxPrinter_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_Box_boxPrinter_feature", "_UI_Box_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ModelPackage.eINSTANCE.getBox_BoxPrinter(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the ID feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addIDPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_Box_iD_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_Box_iD_feature", "_UI_Box_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ModelPackage.eINSTANCE.getBox_ID(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This returns Box.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getImage(Object object) {
        return getResourceLocator().getImage("full/obj16/Box"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getText(Object object) {
        String label = ((Box)object).getID();
        return label == null || label.length() == 0 ?
            getString("_UI_Box_type") : //$NON-NLS-1$
            getString("_UI_Box_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached
     * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void notifyChanged(Notification notification) {
        updateChildren(notification);

        switch (notification.getFeatureID(Box.class)) {
            case ModelPackage.BOX__BOX_PRINTER:
            case ModelPackage.BOX__ID:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
        }
        super.notifyChanged(notification);
    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s
     * describing all of the children that can be created under this object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void collectNewChildDescriptors(Collection newChildDescriptors, Object object) {
        super.collectNewChildDescriptors(newChildDescriptors, object);
    }

    /**
     * Return the resource locator for this item provider's resources.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ResourceLocator getResourceLocator() {
        return PageEditPlugin.INSTANCE;
    }

}
