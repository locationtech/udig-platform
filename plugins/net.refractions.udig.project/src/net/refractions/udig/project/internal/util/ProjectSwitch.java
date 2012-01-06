/**
 * <copyright></copyright> $Id$
 */
package net.refractions.udig.project.internal.util;

import java.util.List;

import net.refractions.udig.catalog.IResolveChangeListener;
import net.refractions.udig.core.IBlockingAdaptable;
import net.refractions.udig.project.IAbstractContext;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IProject;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.IStyleBlackboard;
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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc --> The <b>Switch </b> for the model's inheritance hierarchy. It supports the
 * call {@link #doSwitch(EObject) doSwitch(object)}to invoke the <code>caseXXX</code> method for
 * each class of the model, starting with the actual class of the object and proceeding up the
 * inheritance hierarchy until a non-null result is returned, which is the result of the switch.
 * <!-- end-user-doc -->
 * @see net.refractions.udig.project.internal.ProjectPackage
 * @generated
 */
public class ProjectSwitch<T> extends Switch<T> {

    /**
     * The cached model package
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    static ProjectPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ProjectSwitch() {
        if (modelPackage == null) {
            modelPackage = ProjectPackage.eINSTANCE;
        }
    }

    /**
     * Checks whether this is a switch for the given package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @parameter ePackage the package in question.
     * @return whether this is a switch for the given package.
     * @generated
     */
    @Override
    protected boolean isSwitchFor( EPackage ePackage ) {
        return ePackage == modelPackage;
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    @Override
    protected T doSwitch( int classifierID, EObject theEObject ) {
        switch( classifierID ) {
        case ProjectPackage.CONTEXT_MODEL: {
            ContextModel contextModel = (ContextModel) theEObject;
            T result = caseContextModel(contextModel);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case ProjectPackage.EDIT_MANAGER: {
            EditManager editManager = (EditManager) theEObject;
            T result = caseEditManager(editManager);
            if (result == null) result = caseIEditManager(editManager);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case ProjectPackage.LAYER: {
            Layer layer = (Layer) theEObject;
            T result = caseLayer(layer);
            if (result == null) result = caseILayer(layer);
            if (result == null) result = caseIAdaptable(layer);
            if (result == null) result = caseIBlockingAdaptable(layer);
            if (result == null) result = caseIResolveChangeListener(layer);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case ProjectPackage.MAP: {
            Map map = (Map) theEObject;
            T result = caseMap(map);
            if (result == null) result = caseProjectElement(map);
            if (result == null) result = caseIMap(map);
            if (result == null) result = caseIProjectElement(map);
            if (result == null) result = caseIAdaptable(map);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case ProjectPackage.PROJECT: {
            Project project = (Project) theEObject;
            T result = caseProject(project);
            if (result == null) result = caseIProject(project);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case ProjectPackage.PROJECT_ELEMENT: {
            ProjectElement projectElement = (ProjectElement) theEObject;
            T result = caseProjectElement(projectElement);
            if (result == null) result = caseIProjectElement(projectElement);
            if (result == null) result = caseIAdaptable(projectElement);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case ProjectPackage.PROJECT_REGISTRY: {
            ProjectRegistry projectRegistry = (ProjectRegistry) theEObject;
            T result = caseProjectRegistry(projectRegistry);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case ProjectPackage.STYLE_BLACKBOARD: {
            StyleBlackboard styleBlackboard = (StyleBlackboard) theEObject;
            T result = caseStyleBlackboard(styleBlackboard);
            if (result == null) result = caseIStyleBlackboard(styleBlackboard);
            if (result == null) result = caseCloneable(styleBlackboard);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case ProjectPackage.STYLE_ENTRY: {
            StyleEntry styleEntry = (StyleEntry) theEObject;
            T result = caseStyleEntry(styleEntry);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case ProjectPackage.LAYER_FACTORY: {
            LayerFactory layerFactory = (LayerFactory) theEObject;
            T result = caseLayerFactory(layerFactory);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case ProjectPackage.BLACKBOARD: {
            Blackboard blackboard = (Blackboard) theEObject;
            T result = caseBlackboard(blackboard);
            if (result == null) result = caseIBlackboard(blackboard);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case ProjectPackage.BLACKBOARD_ENTRY: {
            BlackboardEntry blackboardEntry = (BlackboardEntry) theEObject;
            T result = caseBlackboardEntry(blackboardEntry);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        default:
            return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Context Model</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Context Model</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseContextModel( ContextModel object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Layer</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Layer</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseLayer( Layer object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Map</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Map</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseMap( Map object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Project</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Project</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseProject( Project object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Element</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseProjectElement( ProjectElement object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Registry</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Registry</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseProjectRegistry( ProjectRegistry object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Cloneable</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Cloneable</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCloneable( Cloneable object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Layer Factory</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Layer Factory</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseLayerFactory( LayerFactory object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IAdaptable</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IAdaptable</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIAdaptable( IAdaptable object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IBlocking Adaptable</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IBlocking Adaptable</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIBlockingAdaptable( IBlockingAdaptable object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Blackboard</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Blackboard</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseBlackboard( Blackboard object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Blackboard Entry</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Blackboard Entry</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseBlackboardEntry( BlackboardEntry object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IResolve Change Listener</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IResolve Change Listener</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIResolveChangeListener( IResolveChangeListener object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IStyle Blackboard</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IStyle Blackboard</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIStyleBlackboard( IStyleBlackboard object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Style Blackboard</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Style Blackboard</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseStyleBlackboard( StyleBlackboard object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Style Entry</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Style Entry</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseStyleEntry( StyleEntry object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Comparable</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Comparable</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseComparable( Comparable object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>IMap</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>IMap</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIMap( IMap object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>ILayer</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>ILayer</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseILayer( ILayer object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IEdit Manager</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IEdit Manager</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIEditManager( IEditManager object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IProject</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IProject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIProject( IProject object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IAbstract Context</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IAbstract Context</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIAbstractContext( IAbstractContext object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IBlackboard</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IBlackboard</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIBlackboard( IBlackboard object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IProject Element</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IProject Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIProjectElement( IProjectElement object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IRender Manager</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IRender Manager</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIRenderManager( IRenderManager object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IViewport Model</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IViewport Model</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIViewportModel( IViewportModel object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Edit Manager</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Edit Manager</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseEditManager( EditManager object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EObject</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch, but this is the last case anyway. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    @Override
    public T defaultCase( EObject object ) {
        return null;
    }

} // ProjectSwitch
