/**
 * <copyright></copyright> $Id$
 */
package net.refractions.udig.project.internal;

import java.util.List;

import org.eclipse.emf.ecore.EFactory;

/**
 * TODO Purpose of net.refractions.udig.project.internal
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public interface ProjectFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    ProjectFactory eINSTANCE = net.refractions.udig.project.internal.impl.ProjectFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Context Model</em>'.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return a new object of class '<em>Context Model</em>'.
     * @generated
     */
    ContextModel createContextModel();

    /**
     * Returns a new object of class '<em>Layer</em>'. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @return a new object of class '<em>Layer</em>'.
     * @generated
     */
    Layer createLayer();

    /**
     * Returns a new object of class '<em>Map</em>'. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @return a new object of class '<em>Map</em>'.
     * @generated
     */
    Map createMap();

    /**
     * Returns a new object of class '<em>Map</em>'.
     * 
     * @return a new object of class '<em>Map</em>'.
     */
    Map createMap( Project owner, String name, List layers );

    /**
     * Returns a new object of class '<em>Project</em>'.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return a new object of class '<em>Project</em>'.
     * @generated
     */
    Project createProject();

    /**
     * Returns a new object of class '<em>Registry</em>'.
     * <!-- begin-user-doc --> 
     * <b>This creates a new instance.  {@link ProjectPlugin.Implementation#getProjectRegistry()} should
     * be used instead
     * </b>
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Registry</em>'.
     * @generated
     */
    ProjectRegistry createProjectRegistry();

    /**
     * Returns a new object of class '<em>Style Blackboard</em>'.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return a new object of class '<em>Style Blackboard</em>'.
     * @generated
     */
    StyleBlackboard createStyleBlackboard();

    /**
     * Returns a new object of class '<em>Style Entry</em>'.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return a new object of class '<em>Style Entry</em>'.
     * @generated
     */
    StyleEntry createStyleEntry();

    /**
     * Returns a new object of class '<em>Layer Factory</em>'.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return a new object of class '<em>Layer Factory</em>'.
     * @generated
     */
    LayerFactory createLayerFactory();

    /**
     * Returns a new object of class '<em>Blackboard</em>'.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return a new object of class '<em>Blackboard</em>'.
     * @generated
     */
    Blackboard createBlackboard();

    /**
     * Returns a new object of class '<em>Blackboard Entry</em>'.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return a new object of class '<em>Blackboard Entry</em>'.
     * @generated
     */
    BlackboardEntry createBlackboardEntry();

    /**
     * Returns a new object of class '<em>Folder</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Folder</em>'.
     * @generated
     */
    Folder createFolder();

    /**
     * Returns a new object of class '<em>Legend Item</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Legend Item</em>'.
     * @generated
     */
    LegendItem createLegendItem();

    /**
     * Returns a new object of class '<em>Edit Manager</em>'.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return a new object of class '<em>Edit Manager</em>'.
     * @generated
     */
    EditManager createEditManager();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    ProjectPackage getProjectPackage();

} // ProjectFactory
