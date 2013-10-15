/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.project.internal.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.refractions.udig.project.IProject;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectPackage;

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
 * This is the item provider adapter for a {@link net.refractions.udig.project.internal.Project}
 * object. <!-- begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated NOT
 */
public class ProjectItemProvider extends AbstractLazyLoadingItemProvider
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
    public static final String copyright = 
            "uDig - User Friendly Desktop Internet GIS client\n"
          + "http://udig.refractions.net\n"
          + "(C) 2004-2012, Refractions Research Inc.\n"
          + "\n\n"
          + "All rights reserved. This program and the accompanying materials\n"
          + "are made available under the terms of the Eclipse Public License v1.0\n"
          + "(http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD\n"
          + "License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).\n";
    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    public ProjectItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    public List getPropertyDescriptors( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addNamePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Name feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    protected void addNamePropertyDescriptor( Object object ) {
        itemPropertyDescriptors
                .add(createItemPropertyDescriptor(
                        ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
                        getResourceLocator(),
                        getString("_UI_Project_name_feature"), //$NON-NLS-1$
                        getString(
                                "_UI_PropertyDescriptor_description", "_UI_Project_name_feature", "_UI_Project_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ProjectPackage.eINSTANCE.getProject_Name(), true,
                        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
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
            childrenFeatures.add(ProjectPackage.eINSTANCE.getProject_ElementsInternal());
        }
        return childrenFeatures;
    }

    //    @Override
    //    protected Collection< ? extends Object> getConcreteChildren( Object object ) {
    //        if( object instanceof IProject )
    //            return ((IProject)object).getElements();
    //        return super.getConcreteChildren(object);
    //    }
    /**
     * This returns Project.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/Project"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated NOT
     */
    public String getText( Object object ) {
        String label = ((Project) object).getName();
        return label == null || label.length() == 0 ? "Project" : //$NON-NLS-1$
                label;
    }

    /**
     * @see org.eclipse.emf.edit.provider.ITreeItemContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object object ) {
        return true;
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

        switch( notification.getFeatureID(Project.class) ) {
        case ProjectPackage.PROJECT__NAME:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(),
                    false, true));
            return;
        case ProjectPackage.PROJECT__ELEMENTS_INTERNAL:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(),
                    true, false));
            return;
        }
        super.notifyChanged(notification);
    }

    public ResourceLocator getResourceLocator() {
        return ProjectEditPlugin.INSTANCE;
    }

    @Override
    protected Collection< ? extends Object> getConcreteChildren( Object object ) {
        if (object instanceof IProject) {
            List<IProjectElement> elements = new ArrayList<IProjectElement>(
                    ((IProject) object).getElements());
            return elements;
        }

        return super.getConcreteChildren(object);
    }

}
