/**
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * $Id$
 */
package net.refractions.udig.project.element.impl;

import net.refractions.udig.project.element.ElementFactory;
import net.refractions.udig.project.element.ElementPackage;
import net.refractions.udig.project.element.IGenericProjectElement;
import net.refractions.udig.project.element.ProjectElementAdapter;

import net.refractions.udig.project.internal.ProjectPackage;

import net.refractions.udig.project.internal.impl.ProjectPackageImpl;

import net.refractions.udig.project.internal.render.RenderPackage;

import net.refractions.udig.project.internal.render.impl.RenderPackageImpl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ElementPackageImpl extends EPackageImpl implements ElementPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass projectElementAdapterEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType iGenericProjectElementEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see net.refractions.udig.project.element.ElementPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private ElementPackageImpl() {
        super(eNS_URI, ElementFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this
     * model, and for any others upon which it depends.  Simple
     * dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else.  This method drives
     * initialization for interdependent packages directly, in parallel
     * with this package, itself.
     * <p>Of this package and its interdependencies, all packages which
     * have not yet been registered by their URI values are first created
     * and registered.  The packages are then initialized in two steps:
     * meta-model objects for all of the packages are created before any
     * are initialized, since one package's meta-model objects may refer to
     * those of another.
     * <p>Invocation of this method will not affect any packages that have
     * already been initialized.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static ElementPackage init() {
        if (isInited)
            return (ElementPackage) EPackage.Registry.INSTANCE.getEPackage(ElementPackage.eNS_URI);

        // Obtain or create and register package
        ElementPackageImpl theElementPackage = (ElementPackageImpl) (EPackage.Registry.INSTANCE
                .getEPackage(eNS_URI) instanceof ElementPackageImpl ? EPackage.Registry.INSTANCE
                .getEPackage(eNS_URI) : new ElementPackageImpl());

        isInited = true;

        // Obtain or create and register interdependencies
        ProjectPackageImpl theProjectPackage = (ProjectPackageImpl) (EPackage.Registry.INSTANCE
                .getEPackage(ProjectPackage.eNS_URI) instanceof ProjectPackageImpl
                ? EPackage.Registry.INSTANCE.getEPackage(ProjectPackage.eNS_URI)
                : ProjectPackage.eINSTANCE);
        RenderPackageImpl theRenderPackage = (RenderPackageImpl) (EPackage.Registry.INSTANCE
                .getEPackage(RenderPackage.eNS_URI) instanceof RenderPackageImpl
                ? EPackage.Registry.INSTANCE.getEPackage(RenderPackage.eNS_URI)
                : RenderPackage.eINSTANCE);

        // Create package meta-data objects
        theElementPackage.createPackageContents();
        theProjectPackage.createPackageContents();
        theRenderPackage.createPackageContents();

        // Initialize created meta-data
        theElementPackage.initializePackageContents();
        theProjectPackage.initializePackageContents();
        theRenderPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theElementPackage.freeze();

        return theElementPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getProjectElementAdapter() {
        return projectElementAdapterEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProjectElementAdapter_BackingObject() {
        return (EAttribute) projectElementAdapterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getIGenericProjectElement() {
        return iGenericProjectElementEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ElementFactory getElementFactory() {
        return (ElementFactory) getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated)
            return;
        isCreated = true;

        // Create classes and their features
        projectElementAdapterEClass = createEClass(PROJECT_ELEMENT_ADAPTER);
        createEAttribute(projectElementAdapterEClass, PROJECT_ELEMENT_ADAPTER__BACKING_OBJECT);

        // Create data types
        iGenericProjectElementEDataType = createEDataType(IGENERIC_PROJECT_ELEMENT);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized)
            return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        ProjectPackage theProjectPackage = (ProjectPackage) EPackage.Registry.INSTANCE
                .getEPackage(ProjectPackage.eNS_URI);

        // Add supertypes to classes
        projectElementAdapterEClass.getESuperTypes().add(theProjectPackage.getProjectElement());

        // Initialize classes and features; add operations and parameters
        initEClass(projectElementAdapterEClass, ProjectElementAdapter.class,
                "ProjectElementAdapter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(
                getProjectElementAdapter_BackingObject(),
                this.getIGenericProjectElement(),
                "backingObject", null, 0, 1, ProjectElementAdapter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize data types
        initEDataType(iGenericProjectElementEDataType, IGenericProjectElement.class,
                "IGenericProjectElement", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} //ElementPackageImpl
