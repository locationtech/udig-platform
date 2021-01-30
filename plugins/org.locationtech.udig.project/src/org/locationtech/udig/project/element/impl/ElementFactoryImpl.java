/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 * $Id$
 */
package org.locationtech.udig.project.element.impl;

import java.io.IOException;
import java.util.List;

import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.element.ElementFactory;
import org.locationtech.udig.project.element.ElementPackage;
import org.locationtech.udig.project.element.IGenericProjectElement;
import org.locationtech.udig.project.element.ProjectElementAdapter;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.memento.UdigMemento;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ElementFactoryImpl extends EFactoryImpl implements ElementFactory {
    private static final String EXTENSION_POINT_ID_KEY = "@ElementFactoryImpl.ExtensionPointId.key@"; //$NON-NLS-1$

    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ElementFactory init() {
        try {
            ElementFactory theElementFactory = (ElementFactory) EPackage.Registry.INSTANCE
                    .getEFactory(ElementPackage.eNS_URI);
            if (theElementFactory != null) {
                return theElementFactory;
            }
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new ElementFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ElementFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
        case ElementPackage.PROJECT_ELEMENT_ADAPTER:
            return createProjectElementAdapter();
        default:
            throw new IllegalArgumentException(
                    "The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
        case ElementPackage.IGENERIC_PROJECT_ELEMENT:
            return createIGenericProjectElementFromString(eDataType, initialValue);
        default:
            throw new IllegalArgumentException(
                    "The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
        case ElementPackage.IGENERIC_PROJECT_ELEMENT:
            return convertIGenericProjectElementToString(eDataType, instanceValue);
        default:
            throw new IllegalArgumentException(
                    "The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ProjectElementAdapter createProjectElementAdapter() {
        ProjectElementAdapterImpl projectElementAdapter = new ProjectElementAdapterImpl();
        return projectElementAdapter;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NO MORE
     */
    public IGenericProjectElement createIGenericProjectElementFromString(EDataType eDataType,
            String initialValue) {
        try {
            UdigMemento memento = UdigMemento.readString(initialValue);
            IGenericProjectElement backingObject = createGenericProjectElement(
                    IGenericProjectElement.class, memento.getString(EXTENSION_POINT_ID_KEY));
            backingObject.init(memento);
            return backingObject;
        } catch (IOException e) {
            ProjectPlugin.log("Error parsing memento data for IGenericProject Element", e); //$NON-NLS-1$
            return null;
        }
    }

    /**
     * Uses the IMemento pattern to obtain persistence data from the object
     * @generated NO MORE
     */
    public String convertIGenericProjectElementToString(EDataType eDataType, Object instanceValue) {
        IGenericProjectElement elem = (IGenericProjectElement) instanceValue;
        UdigMemento memento = new UdigMemento();
        elem.save(memento);
        memento.putString(EXTENSION_POINT_ID_KEY, elem.getExtensionId());
        return memento.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ElementPackage getElementPackage() {
        return (ElementPackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static ElementPackage getPackage() {
        return ElementPackage.eINSTANCE;
    }

    @Override
    public ProjectElementAdapter createProjectElementAdapter(IProject project,
            Class<? extends IGenericProjectElement> typeToCreate, String extensionId) {
        ProjectElementAdapter adapter = createProjectElementAdapter();

        IGenericProjectElement genericProjectElement = createGenericProjectElement(typeToCreate,
                extensionId);
        adapter.setBackingObject(genericProjectElement);

        ((Project) project).getElementsInternal().add(adapter);
        return adapter;
    }

    @Override
    public ProjectElementAdapter createProjectElementAdapter(IProject project, String elemName,
            Class<? extends IGenericProjectElement> typeToCreate, String extensionId) {
        ProjectElementAdapter adapter = createProjectElementAdapter();
        adapter.setName(elemName);

        IGenericProjectElement genericProjectElement = createGenericProjectElement(typeToCreate,
                extensionId);
        adapter.setBackingObject(genericProjectElement);

        ((Project) project).getElementsInternal().add(adapter);
        return adapter;
    }

    private <T extends IGenericProjectElement> T createGenericProjectElement(Class<T> typeToCreate,
            String extensionId) {
        List<IConfigurationElement> list = ExtensionPointList
                .getExtensionPointList(ProjectElementAdapter.EXT_ID);
        for (IConfigurationElement configurationElement : list) {
            String id = configurationElement.getAttribute("id"); //$NON-NLS-1$
            if (id != null && id.equals(extensionId)) {
                try {
                    Object obj = configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
                    if (typeToCreate.isAssignableFrom(obj.getClass())) {
                        ((IGenericProjectElement) obj).setExtensionId(extensionId);
                        return typeToCreate.cast(obj);
                    } else {
                        throw new IllegalArgumentException("The " + extensionId //$NON-NLS-1$
                                + " created an object of type: " + obj.getClass() //$NON-NLS-1$
                                + " which is not compatible with " + typeToCreate); //$NON-NLS-1$
                    }
                } catch (CoreException e) {
                    throw new RuntimeException("Error creating extension", e); //$NON-NLS-1$
                }
            }
        }

        throw new IllegalArgumentException(extensionId + " was not a valid extension id"); //$NON-NLS-1$
    }

} //ElementFactoryImpl
