/**
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * $Id$
 */
package net.refractions.udig.project.element;

import net.refractions.udig.project.internal.ProjectPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see net.refractions.udig.project.element.ElementFactory
 * @model kind="package"
 * @generated
 */
public interface ElementPackage extends EPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "element"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http:///net/refractions/udig/project/element.ecore"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "net.refractions.udig.project.element"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ElementPackage eINSTANCE = net.refractions.udig.project.element.impl.ElementPackageImpl.init();

    /**
     * The meta object id for the '{@link net.refractions.udig.project.element.impl.ProjectElementAdapterImpl <em>Project Element Adapter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.project.element.impl.ProjectElementAdapterImpl
     * @see net.refractions.udig.project.element.impl.ElementPackageImpl#getProjectElementAdapter()
     * @generated
     */
    int PROJECT_ELEMENT_ADAPTER = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT_ELEMENT_ADAPTER__NAME = ProjectPackage.PROJECT_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Project Internal</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL = ProjectPackage.PROJECT_ELEMENT__PROJECT_INTERNAL;

    /**
     * The feature id for the '<em><b>Backing Object</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT_ELEMENT_ADAPTER__BACKING_OBJECT = ProjectPackage.PROJECT_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Project Element Adapter</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT_ELEMENT_ADAPTER_FEATURE_COUNT = ProjectPackage.PROJECT_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '<em>IGeneric Project Element</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.project.element.IGenericProjectElement
     * @see net.refractions.udig.project.element.impl.ElementPackageImpl#getIGenericProjectElement()
     * @generated
     */
    int IGENERIC_PROJECT_ELEMENT = 1;

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.element.ProjectElementAdapter <em>Project Element Adapter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Project Element Adapter</em>'.
     * @see net.refractions.udig.project.element.ProjectElementAdapter
     * @generated
     */
    EClass getProjectElementAdapter();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.element.ProjectElementAdapter#getBackingObject <em>Backing Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Backing Object</em>'.
     * @see net.refractions.udig.project.element.ProjectElementAdapter#getBackingObject()
     * @see #getProjectElementAdapter()
     * @generated
     */
    EAttribute getProjectElementAdapter_BackingObject();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.project.element.IGenericProjectElement <em>IGeneric Project Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>IGeneric Project Element</em>'.
     * @see net.refractions.udig.project.element.IGenericProjectElement
     * @model instanceClass="net.refractions.udig.project.element.IGenericProjectElement"
     * @generated
     */
    EDataType getIGenericProjectElement();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ElementFactory getElementFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link net.refractions.udig.project.element.impl.ProjectElementAdapterImpl <em>Project Element Adapter</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.element.impl.ProjectElementAdapterImpl
         * @see net.refractions.udig.project.element.impl.ElementPackageImpl#getProjectElementAdapter()
         * @generated
         */
        EClass PROJECT_ELEMENT_ADAPTER = eINSTANCE.getProjectElementAdapter();

        /**
         * The meta object literal for the '<em><b>Backing Object</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute PROJECT_ELEMENT_ADAPTER__BACKING_OBJECT = eINSTANCE
                .getProjectElementAdapter_BackingObject();

        /**
         * The meta object literal for the '<em>IGeneric Project Element</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.element.IGenericProjectElement
         * @see net.refractions.udig.project.element.impl.ElementPackageImpl#getIGenericProjectElement()
         * @generated
         */
        EDataType IGENERIC_PROJECT_ELEMENT = eINSTANCE.getIGenericProjectElement();

    }

} //ElementPackage
