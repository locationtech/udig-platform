package net.refractions.udig.project.internal.util;

import net.refractions.udig.catalog.IResolveChangeListener;
import net.refractions.udig.core.IBlockingAdaptable;
import net.refractions.udig.project.IAbstractContext;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IProject;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.internal.AbstractContext;
import net.refractions.udig.project.internal.Blackboard;
import net.refractions.udig.project.internal.BlackboardEntry;
import net.refractions.udig.project.internal.ContextModel;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerFactory;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectElement;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectRegistry;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.project.internal.StyleEntry;
import net.refractions.udig.project.render.IRenderManager;
import net.refractions.udig.project.render.IViewportModel;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * TODO Purpose of net.refractions.udig.project.internal.util
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class ProjectAdapterFactory extends AdapterFactoryImpl {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

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
    public boolean isFactoryForType( Object object ) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject) object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch the delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ProjectSwitch modelSwitch = new ProjectSwitch(){
        public Object caseComparable( Comparable object ) {
            return createComparableAdapter();
        }
        public Object caseIMap( IMap object ) {
            return createIMapAdapter();
        }
        public Object caseILayer( ILayer object ) {
            return createILayerAdapter();
        }
        public Object caseIEditManager( IEditManager object ) {
            return createIEditManagerAdapter();
        }
        public Object caseIProject( IProject object ) {
            return createIProjectAdapter();
        }
        public Object caseIAbstractContext( IAbstractContext object ) {
            return createIAbstractContextAdapter();
        }
        public Object caseIBlackboard( IBlackboard object ) {
            return createIBlackboardAdapter();
        }
        public Object caseIProjectElement( IProjectElement object ) {
            return createIProjectElementAdapter();
        }
        public Object caseIRenderManager( IRenderManager object ) {
            return createIRenderManagerAdapter();
        }
        public Object caseIViewportModel( IViewportModel object ) {
            return createIViewportModelAdapter();
        }
        public Object caseAbstractContext( AbstractContext object ) {
            return createAbstractContextAdapter();
        }
        public Object caseContextModel( ContextModel object ) {
            return createContextModelAdapter();
        }
        public Object caseEditManager( EditManager object ) {
            return createEditManagerAdapter();
        }
        public Object caseLayer( Layer object ) {
            return createLayerAdapter();
        }
        public Object caseMap( Map object ) {
            return createMapAdapter();
        }
        public Object caseProject( Project object ) {
            return createProjectAdapter();
        }
        public Object caseProjectElement( ProjectElement object ) {
            return createProjectElementAdapter();
        }
        public Object caseProjectRegistry( ProjectRegistry object ) {
            return createProjectRegistryAdapter();
        }
        public Object caseStyleBlackboard( StyleBlackboard object ) {
            return createStyleBlackboardAdapter();
        }
        public Object caseStyleEntry( StyleEntry object ) {
            return createStyleEntryAdapter();
        }
        public Object caseCloneable( Cloneable object ) {
            return createCloneableAdapter();
        }
        public Object caseLayerFactory( LayerFactory object ) {
            return createLayerFactoryAdapter();
        }
        public Object caseIAdaptable( IAdaptable object ) {
            return createIAdaptableAdapter();
        }
        public Object caseIBlockingAdaptable( IBlockingAdaptable object ) {
            return createIBlockingAdaptableAdapter();
        }
        public Object caseBlackboard( Blackboard object ) {
            return createBlackboardAdapter();
        }
        public Object caseBlackboardEntry( BlackboardEntry object ) {
            return createBlackboardEntryAdapter();
        }
        public Object caseIResolveChangeListener( IResolveChangeListener object ) {
            return createIResolveChangeListenerAdapter();
        }
        public Object defaultCase( EObject object ) {
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
    public Adapter createAdapter( Notifier target ) {
        return (Adapter) modelSwitch.doSwitch((EObject) target);
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link net.refractions.udig.project.ContextModel <em>Context Model</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * 
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.ContextModel
     * @generated
     */
    public Adapter createContextModelAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.Layer <em>Layer</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.Layer
     * @generated
     */
    public Adapter createLayerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.Map <em>Map</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.Map
     * @generated
     */
    public Adapter createMapAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.Project <em>Project</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.Project
     * @generated
     */
    public Adapter createProjectAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.ProjectElement <em>Element</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.ProjectElement
     * @generated
     */
    public Adapter createProjectElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.ProjectRegistry <em>Registry</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.ProjectRegistry
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
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.LayerFactory <em>Layer Factory</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.LayerFactory
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
     * Creates a new adapter for an object of class '{@link net.refractions.udig.core.IBlockingAdaptable <em>IBlocking Adaptable</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.core.IBlockingAdaptable
     * @generated
     */
    public Adapter createIBlockingAdaptableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.PicoBlackboard <em>Pico Blackboard</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.PicoBlackboard
     * @generated
     */
    public Adapter createPicoBlackboardAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.Blackboard <em>Blackboard</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.Blackboard
     * @generated
     */
    public Adapter createBlackboardAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.BlackboardEntry <em>Blackboard Entry</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.BlackboardEntry
     * @generated
     */
    public Adapter createBlackboardEntryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.catalog.IResolveChangeListener <em>IResolve Change Listener</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.catalog.IResolveChangeListener
     * @generated
     */
    public Adapter createIResolveChangeListenerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link net.refractions.udig.project.AbstractContext <em>Abstract Context</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * 
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.AbstractContext
     * @generated
     */
    public Adapter createAbstractContextAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link net.refractions.udig.project.IProjectElement <em>IProject Element</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * 
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.ProjectElement
     * @generated
     */
    public Adapter createIProjectElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.render.IRenderManager <em>IRender Manager</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.render.IRenderManager
     * @generated
     */
    public Adapter createIRenderManagerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.render.IViewportModel <em>IViewport Model</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.render.IViewportModel
     * @generated
     */
    public Adapter createIViewportModelAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link net.refractions.udig.project.StyleBlackboard <em>Style Blackboard</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * 
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.StyleBlackboard
     * @generated
     */
    public Adapter createStyleBlackboardAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.StyleEntry <em>Style Entry</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.StyleEntry
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
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.IMap <em>IMap</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.IMap
     * @generated
     */
    public Adapter createIMapAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.EditManager <em>Edit Manager</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.EditManager
     * @generated
     */
    public Adapter createEditManagerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.ILayer <em>ILayer</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.ILayer
     * @generated
     */
    public Adapter createILayerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.IEditManager <em>IEdit Manager</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.IEditManager
     * @generated
     */
    public Adapter createIEditManagerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.IProject <em>IProject</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.IProject
     * @generated
     */
    public Adapter createIProjectAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.IAbstractContext <em>IAbstract Context</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.IAbstractContext
     * @generated
     */
    public Adapter createIAbstractContextAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.IBlackboard <em>IBlackboard</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.IBlackboard
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
