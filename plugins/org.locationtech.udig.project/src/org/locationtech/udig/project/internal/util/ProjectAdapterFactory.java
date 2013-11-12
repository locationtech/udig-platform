/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.util;

import org.locationtech.udig.catalog.IResolveChangeListener;
import org.locationtech.udig.core.IBlockingAdaptable;
import org.locationtech.udig.project.IAbstractContext;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.IFolder;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILayerLegendItem;
import org.locationtech.udig.project.ILegendItem;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.internal.AbstractContext;
import org.locationtech.udig.project.internal.Blackboard;
import org.locationtech.udig.project.internal.BlackboardEntry;
import org.locationtech.udig.project.internal.ContextModel;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.Folder;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerFactory;
import org.locationtech.udig.project.internal.LayerLegendItem;
import org.locationtech.udig.project.internal.LegendItem;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectElement;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectRegistry;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.internal.StyleEntry;
import org.locationtech.udig.project.render.IRenderManager;
import org.locationtech.udig.project.render.IViewportModel;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * TODO Purpose of org.locationtech.udig.project.internal.util
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class ProjectAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    static ProjectPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ProjectAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = ProjectPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc
     * --> This implementation returns <code>true</code> if the object is either the model's
     * package or is an instance object of the model. <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject) object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ProjectSwitch<Adapter> modelSwitch = new ProjectSwitch<Adapter>() {
        @Override
        public Adapter caseComparable(Comparable object) {
            return createComparableAdapter();
        }

        @Override
        public Adapter caseIMap(IMap object) {
            return createIMapAdapter();
        }

        @Override
        public Adapter caseILayer(ILayer object) {
            return createILayerAdapter();
        }

        @Override
        public Adapter caseIEditManager(IEditManager object) {
            return createIEditManagerAdapter();
        }

        @Override
        public Adapter caseIProject(IProject object) {
            return createIProjectAdapter();
        }

        @Override
        public Adapter caseIAbstractContext(IAbstractContext object) {
            return createIAbstractContextAdapter();
        }

        @Override
        public Adapter caseIBlackboard(IBlackboard object) {
            return createIBlackboardAdapter();
        }

        @Override
        public Adapter caseIProjectElement(IProjectElement object) {
            return createIProjectElementAdapter();
        }

        @Override
        public Adapter caseIRenderManager(IRenderManager object) {
            return createIRenderManagerAdapter();
        }

        @Override
        public Adapter caseIViewportModel(IViewportModel object) {
            return createIViewportModelAdapter();
        }

        @Override
        public Adapter caseContextModel(ContextModel object) {
            return createContextModelAdapter();
        }

        @Override
        public Adapter caseEditManager(EditManager object) {
            return createEditManagerAdapter();
        }

        @Override
        public Adapter caseLayer(Layer object) {
            return createLayerAdapter();
        }

        @Override
        public Adapter caseMap(Map object) {
            return createMapAdapter();
        }

        @Override
        public Adapter caseProject(Project object) {
            return createProjectAdapter();
        }

        @Override
        public Adapter caseProjectElement(ProjectElement object) {
            return createProjectElementAdapter();
        }

        @Override
        public Adapter caseProjectRegistry(ProjectRegistry object) {
            return createProjectRegistryAdapter();
        }

        @Override
        public Adapter caseStyleBlackboard(StyleBlackboard object) {
            return createStyleBlackboardAdapter();
        }

        @Override
        public Adapter caseStyleEntry(StyleEntry object) {
            return createStyleEntryAdapter();
        }

        @Override
        public Adapter caseCloneable(Cloneable object) {
            return createCloneableAdapter();
        }

        @Override
        public Adapter caseLayerFactory(LayerFactory object) {
            return createLayerFactoryAdapter();
        }

        @Override
        public Adapter caseIAdaptable(IAdaptable object) {
            return createIAdaptableAdapter();
        }

        @Override
        public Adapter caseIBlockingAdaptable(IBlockingAdaptable object) {
            return createIBlockingAdaptableAdapter();
        }

        @Override
        public Adapter caseBlackboard(Blackboard object) {
            return createBlackboardAdapter();
        }

        @Override
        public Adapter caseBlackboardEntry(BlackboardEntry object) {
            return createBlackboardEntryAdapter();
        }

        @Override
        public Adapter caseIResolveChangeListener(IResolveChangeListener object) {
            return createIResolveChangeListenerAdapter();
        }

        @Override
        public Adapter caseIStyleBlackboard(IStyleBlackboard object) {
            return createIStyleBlackboardAdapter();
        }

        @Override
        public Adapter caseInteractionToEBooleanObjectMapEntry(
                java.util.Map.Entry<Interaction, Boolean> object) {
            return createInteractionToEBooleanObjectMapEntryAdapter();
        }

        @Override
        public Adapter caseIFolder(IFolder object) {
            return createIFolderAdapter();
        }

        @Override
        public Adapter caseFolder(Folder object) {
            return createFolderAdapter();
        }

        @Override
        public Adapter caseILayerLegendItem(ILayerLegendItem object) {
            return createILayerLegendItemAdapter();
        }

        @Override
        public Adapter caseLegendItem(LegendItem object) {
            return createLegendItemAdapter();
        }

        @Override
        public Adapter caseILegendItem(ILegendItem object) {
            return createILegendItemAdapter();
        }

        @Override
        public Adapter caseLayerLegendItem(LayerLegendItem object) {
            return createLayerLegendItemAdapter();
        }

        @Override
        public Adapter defaultCase(EObject object) {
            return createEObjectAdapter();
        }
    };

    /**
     * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject) target);
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link org.locationtech.udig.project.ContextModel <em>Context Model</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.ContextModel
     * @generated
     */
    public Adapter createContextModelAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.Layer <em>Layer</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.Layer
     * @generated
     */
    public Adapter createLayerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.Map <em>Map</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.Map
     * @generated
     */
    public Adapter createMapAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.Project <em>Project</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.Project
     * @generated
     */
    public Adapter createProjectAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.ProjectElement <em>Element</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.ProjectElement
     * @generated
     */
    public Adapter createProjectElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.ProjectRegistry <em>Registry</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.ProjectRegistry
     * @generated
     */
    public Adapter createProjectRegistryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link Cloneable <em>Cloneable</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * 
     * @return the new adapter.
     * @see Cloneable
     * @generated
     */
    public Adapter createCloneableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.LayerFactory <em>Layer Factory</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.LayerFactory
     * @generated
     */
    public Adapter createLayerFactoryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.core.runtime.IAdaptable <em>IAdaptable</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.core.runtime.IAdaptable
     * @generated
     */
    public Adapter createIAdaptableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.core.IBlockingAdaptable <em>IBlocking Adaptable</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.core.IBlockingAdaptable
     * @generated
     */
    public Adapter createIBlockingAdaptableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.Blackboard <em>Blackboard</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.Blackboard
     * @generated
     */
    public Adapter createBlackboardAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.BlackboardEntry <em>Blackboard Entry</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.BlackboardEntry
     * @generated
     */
    public Adapter createBlackboardEntryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.catalog.IResolveChangeListener <em>IResolve Change Listener</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.catalog.IResolveChangeListener
     * @generated
     */
    public Adapter createIResolveChangeListenerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.IStyleBlackboard <em>IStyle Blackboard</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.IStyleBlackboard
     * @generated
     */
    public Adapter createIStyleBlackboardAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>Interaction To EBoolean Object Map Entry</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see java.util.Map.Entry
     * @generated
     */
    public Adapter createInteractionToEBooleanObjectMapEntryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.IFolder <em>IFolder</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.IFolder
     * @generated
     */
    public Adapter createIFolderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.Folder <em>Folder</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.Folder
     * @generated
     */
    public Adapter createFolderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.ILayerLegendItem <em>ILayer Legend Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.ILayerLegendItem
     * @generated
     */
    public Adapter createILayerLegendItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.LegendItem <em>Legend Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.LegendItem
     * @generated
     */
    public Adapter createLegendItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.ILegendItem <em>ILegend Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.ILegendItem
     * @generated
     */
    public Adapter createILegendItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.LayerLegendItem <em>Layer Legend Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.LayerLegendItem
     * @generated
     */
    public Adapter createLayerLegendItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link org.locationtech.udig.project.IProjectElement <em>IProject Element</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.ProjectElement
     * @generated
     */
    public Adapter createIProjectElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.render.IRenderManager <em>IRender Manager</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.render.IRenderManager
     * @generated
     */
    public Adapter createIRenderManagerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.render.IViewportModel <em>IViewport Model</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.render.IViewportModel
     * @generated
     */
    public Adapter createIViewportModelAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link org.locationtech.udig.project.StyleBlackboard <em>Style Blackboard</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.StyleBlackboard
     * @generated
     */
    public Adapter createStyleBlackboardAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.StyleEntry <em>Style Entry</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.StyleEntry
     * @generated
     */
    public Adapter createStyleEntryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * 
     * @return the new adapter.
     * @see Comparable
     * @generated
     */
    public Adapter createComparableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.IMap <em>IMap</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.IMap
     * @generated
     */
    public Adapter createIMapAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.EditManager <em>Edit Manager</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.EditManager
     * @generated
     */
    public Adapter createEditManagerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.ILayer <em>ILayer</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.ILayer
     * @generated
     */
    public Adapter createILayerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.IEditManager <em>IEdit Manager</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.IEditManager
     * @generated
     */
    public Adapter createIEditManagerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.IProject <em>IProject</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.IProject
     * @generated
     */
    public Adapter createIProjectAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.IAbstractContext <em>IAbstract Context</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.IAbstractContext
     * @generated
     */
    public Adapter createIAbstractContextAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.IBlackboard <em>IBlackboard</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.IBlackboard
     * @generated
     */
    public Adapter createIBlackboardAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc --> This default
     * implementation returns null. <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} // ProjectAdapterFactory
