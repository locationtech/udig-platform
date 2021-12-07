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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.locationtech.udig.project.element.ElementFactory;
import org.locationtech.udig.project.element.ElementPackage;
import org.locationtech.udig.project.element.IGenericProjectElement;
import org.locationtech.udig.project.element.ProjectElementAdapter;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.impl.ProjectPackageImpl;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.impl.RenderPackageImpl;

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
     * @see org.locationtech.udig.project.element.ElementPackage#eNS_URI
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
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     *
     * <p>This method is used to initialize {@link ElementPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
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
        Object registeredElementPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
        ElementPackageImpl theElementPackage = registeredElementPackage instanceof ElementPackageImpl
                ? (ElementPackageImpl) registeredElementPackage
                : new ElementPackageImpl();

        isInited = true;

        // Initialize simple dependencies
        EcorePackage.eINSTANCE.eClass();

        // Obtain or create and register interdependencies
        Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(ProjectPackage.eNS_URI);
        ProjectPackageImpl theProjectPackage = (ProjectPackageImpl) (registeredPackage instanceof ProjectPackageImpl
                ? registeredPackage
                : ProjectPackage.eINSTANCE);
        registeredPackage = EPackage.Registry.INSTANCE.getEPackage(RenderPackage.eNS_URI);
        RenderPackageImpl theRenderPackage = (RenderPackageImpl) (registeredPackage instanceof RenderPackageImpl
                ? registeredPackage
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

        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(ElementPackage.eNS_URI, theElementPackage);
        return theElementPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getProjectElementAdapter() {
        return projectElementAdapterEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getProjectElementAdapter_BackingObject() {
        return (EAttribute) projectElementAdapterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EDataType getIGenericProjectElement() {
        return iGenericProjectElementEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
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

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        projectElementAdapterEClass.getESuperTypes().add(theProjectPackage.getProjectElement());

        // Initialize classes and features; add operations and parameters
        initEClass(projectElementAdapterEClass, ProjectElementAdapter.class,
                "ProjectElementAdapter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getProjectElementAdapter_BackingObject(), this.getIGenericProjectElement(),
                "backingObject", null, 0, 1, ProjectElementAdapter.class, !IS_TRANSIENT, //$NON-NLS-1$
                !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);

        // Initialize data types
        initEDataType(iGenericProjectElementEDataType, IGenericProjectElement.class,
                "IGenericProjectElement", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} //ElementPackageImpl
