/**
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * $Id$
 */
package net.refractions.udig.project.element.provider;

import java.util.List;

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.project.element.ElementPackage;
import net.refractions.udig.project.element.IGenericProjectElement;
import net.refractions.udig.project.element.ProjectElementAdapter;
import net.refractions.udig.project.internal.provider.ProjectEditPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
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
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * This is the item provider adapter for a {@link net.refractions.udig.project.element.ProjectElementAdapter} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ProjectElementAdapterItemProvider extends ItemProviderAdapter
        implements
            IEditingDomainItemProvider,
            IStructuredItemContentProvider,
            ITreeItemContentProvider,
            IItemLabelProvider,
            IItemPropertySource,
            IFontProvider,
            IColorProvider{
    private static final String LABEL_PROVIDER_ATT = "labelProvider";
    private static final String LABEL_ATT = "label";
    private static final String ICON_ATT = "icon";
	/**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ProjectElementAdapterItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getPropertyDescriptors( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addNamePropertyDescriptor(object);
            addBackingObjectPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Name feature.
     *
     * @generated NO MORE
     */
    protected void addNamePropertyDescriptor( Object object ) {
        //  TODO uncomment when all emf has been regenerated
        //
        //        itemPropertyDescriptors
        //                .add(createItemPropertyDescriptor(
        //                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
        //                        getResourceLocator(),
        //                        getString("_UI_ProjectElement_name_feature"), //$NON-NLS-1$
        //                        getString(
        //                                "_UI_PropertyDescriptor_description", "_UI_ProjectElement_name_feature", "_UI_ProjectElement_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        //                        ProjectPackage.Literals.PROJECT_ELEMENT__NAME, true, false, false,
        //                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Backing Object feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addBackingObjectPropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_ProjectElementAdapter_backingObject_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_ProjectElementAdapter_backingObject_feature", "_UI_ProjectElementAdapter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ElementPackage.Literals.PROJECT_ELEMENT_ADAPTER__BACKING_OBJECT, true,
                        false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

	private IConfigurationElement findExtension(String extensionId) {
		List<IConfigurationElement> list = ExtensionPointList
				.getExtensionPointList(ProjectElementAdapter.EXT_ID);
		for (IConfigurationElement configurationElement : list) {
			String id = configurationElement.getAttribute("id"); //$NON-NLS-1$
			if (id != null && id.equals(extensionId)) {
				return configurationElement;
			}
		}
		return null;
	}

	/**
	 * If the extension's LabelProvider is non-null and implements the
	 * IColorProvider interface the foreground color from the provider is
	 * returned otherwise null is returned
	 */
	public Color getForeground(Object object) {
		ProjectElementAdapter projectElementAdapter = ((ProjectElementAdapter) object);
		IGenericProjectElement backingObject = projectElementAdapter
				.getBackingObject();
        if( backingObject ==null ){
            return null;
        }
		String extensionId = backingObject.getExtensionId();
		IConfigurationElement extension = findExtension(extensionId);
		String labelProviderAtt = extension.getAttribute(LABEL_PROVIDER_ATT);
		Color color = null;
		if (labelProviderAtt != null) {
			try {
				IBaseLabelProvider baseProvider = (IBaseLabelProvider) extension
						.createExecutableExtension(LABEL_PROVIDER_ATT);
				if (baseProvider instanceof IColorProvider) {
					IColorProvider labelProvider = (IColorProvider) baseProvider;
					color = labelProvider.getForeground(backingObject);
				}
			} catch (CoreException e) {
				// not good log this
				ProjectEditPlugin.log(
						"Unable to load the LabelProvider for Element: "
								+ extensionId, e);
			}
		}
		return color;
	}

	/**
	 * If the extension's LabelProvider is non-null and implements the
	 * IColorProvider interface the background color from the provider is
	 * returned otherwise null is returned
	 */
	public Color getBackground(Object object) {
		ProjectElementAdapter projectElementAdapter = ((ProjectElementAdapter) object);
		IGenericProjectElement backingObject = projectElementAdapter
				.getBackingObject();
        if( backingObject ==null ){
            return null;
        }
		String extensionId = backingObject.getExtensionId();
		IConfigurationElement extension = findExtension(extensionId);
		String labelProviderAtt = extension.getAttribute(LABEL_PROVIDER_ATT);
		Color color = null;
		if (labelProviderAtt != null) {
			try {
				IBaseLabelProvider baseProvider = (IBaseLabelProvider) extension
						.createExecutableExtension(LABEL_PROVIDER_ATT);
				if (baseProvider instanceof IColorProvider) {
					IColorProvider labelProvider = (IColorProvider) baseProvider;
					color = labelProvider.getBackground(backingObject);
				}
			} catch (CoreException e) {
				// not good log this
				ProjectEditPlugin.log(
						"Unable to load the LabelProvider for Element: "
								+ extensionId, e);
			}
		}
		return color;
	}

	/**
	 * If the extension's LabelProvider is non-null and implements the
	 * IFontProvider interface the font from the provider is returned otherwise
	 * null is returned
	 */
	public Font getFont(Object object) {
		ProjectElementAdapter projectElementAdapter = ((ProjectElementAdapter) object);
		IGenericProjectElement backingObject = projectElementAdapter
				.getBackingObject();
        if( backingObject ==null ){
            return null;
        }
		String extensionId = backingObject.getExtensionId();
		IConfigurationElement extension = findExtension(extensionId);
		String labelProviderAtt = extension.getAttribute(LABEL_PROVIDER_ATT);
		Font font = null;
		if (labelProviderAtt != null) {
			try {
				IBaseLabelProvider baseProvider = (IBaseLabelProvider) extension
						.createExecutableExtension(LABEL_PROVIDER_ATT);
				if (baseProvider instanceof IFontProvider) {
					IFontProvider labelProvider = (IFontProvider) baseProvider;
					font = labelProvider.getFont(backingObject);
				}
			} catch (CoreException e) {
				// not good log this
				ProjectEditPlugin.log(
						"Unable to load the LabelProvider for Element: "
								+ extensionId, e);
			}
		}
		return font;
	}

	/**
	 * If the object is a {@link ProjectElementAdapter} it returns the image
	 * returned by the extension's labelProvider or the icon defined in the
	 * extension (if it is defined). If the element is not a
	 * ProjectElementAdapter the a default image is returned
	 *
	 * @generated NOT
	 */
	public Object getImage(Object object) {
		ProjectElementAdapter projectElementAdapter = ((ProjectElementAdapter) object);
		IGenericProjectElement backingObject = projectElementAdapter
				.getBackingObject();
	      if( backingObject ==null ){
	            return null;
	        }
		String extensionId = backingObject.getExtensionId();
		IConfigurationElement extension = findExtension(extensionId);
		String labelProviderAtt = extension.getAttribute(LABEL_PROVIDER_ATT);
		Object image = null;
		if (labelProviderAtt != null) {
			try {
				IBaseLabelProvider baseProvider = (IBaseLabelProvider) extension
						.createExecutableExtension(LABEL_PROVIDER_ATT);
				if (baseProvider instanceof ILabelProvider) {
					ILabelProvider labelProvider = (ILabelProvider) baseProvider;
					image = labelProvider.getImage(backingObject);
				}
			} catch (CoreException e) {
				// not good log this
				ProjectEditPlugin.log(
						"Unable to load the LabelProvider for Element: "
								+ extensionId, e);
			}
		}
		String iconPath = extension.getAttribute(ICON_ATT);
		if (image == null && iconPath != null) {
			image = AbstractUIPlugin.imageDescriptorFromPlugin(extension
					.getNamespaceIdentifier(), iconPath);
		}
		return image;
	}

	/**
	 * If the object is a {@link ProjectElementAdapter} it returns the text
	 * returned by the extension's labelProvider or the label defined in the
	 * extension (if it is defined). If the element is not a
	 * ProjectElementAdapter the a default text is returned
	 *
	 * @generated NOT
	 */
	public String getText(Object object) {
		ProjectElementAdapter projectElementAdapter = ((ProjectElementAdapter) object);
		IGenericProjectElement backingObject = projectElementAdapter
				.getBackingObject();
		if( backingObject ==null ){
		    return projectElementAdapter.getName()+"- No backing object";
		}
		String extensionId = backingObject.getExtensionId();
		IConfigurationElement extension = findExtension(extensionId);
		String labelProviderAtt = extension.getAttribute(LABEL_PROVIDER_ATT);
		String text = null;
		if (labelProviderAtt != null) {
			try {
				IBaseLabelProvider baseProvider = (IBaseLabelProvider) extension
						.createExecutableExtension(LABEL_PROVIDER_ATT);
				if (baseProvider instanceof ILabelProvider) {
					ILabelProvider labelProvider = (ILabelProvider) baseProvider;
					text = labelProvider.getText(backingObject);
				}
			} catch (CoreException e) {
				// not good log this
				ProjectEditPlugin.log(
						"Unable to load the LabelProvider for Element: "
								+ extensionId, e);
			}
		}
		if( text==null ){
			text = extension.getAttribute(LABEL_ATT);
		}
		return text;
	}

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached
     * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void notifyChanged( Notification notification ) {
        updateChildren(notification);

        switch( notification.getFeatureID(ProjectElementAdapter.class) ) {
        case ElementPackage.PROJECT_ELEMENT_ADAPTER__NAME:
        case ElementPackage.PROJECT_ELEMENT_ADAPTER__BACKING_OBJECT:
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
    public ResourceLocator getResourceLocator() {
        return ProjectEditPlugin.INSTANCE;
    }

}
