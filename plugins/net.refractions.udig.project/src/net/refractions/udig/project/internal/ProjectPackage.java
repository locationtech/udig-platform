/**
 * <copyright></copyright> $Id$
 */
package net.refractions.udig.project.internal;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * TODO Purpose of net.refractions.udig.project.internal
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @model kind="package"
 * @generated
 */
public interface ProjectPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "internal"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http:///net/refractions/udig/project/internal.ecore"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "net.refractions.udig.project.internal"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    ProjectPackage eINSTANCE = net.refractions.udig.project.internal.impl.ProjectPackageImpl.init();

    /**
     * The meta object id for the '{@link Comparable <em>Comparable</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see Comparable
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getComparable()
     * @generated
     */
    int COMPARABLE = 0;

    /**
     * The number of structural features of the the '<em>Comparable</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int COMPARABLE_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.IMap <em>IMap</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.IMap
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIMap()
     * @generated
     */
    int IMAP = 1;

    /**
     * The number of structural features of the '<em>IMap</em>' class.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IMAP_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.ILayer <em>ILayer</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.ILayer
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getILayer()
     * @generated
     */
    int ILAYER = 2;

    /**
     * The number of structural features of the '<em>ILayer</em>' class.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ILAYER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.IEditManager <em>IEdit Manager</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.IEditManager
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIEditManager()
     * @generated
     */
    int IEDIT_MANAGER = 3;

    /**
     * The number of structural features of the the '<em>IEdit Manager</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IEDIT_MANAGER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.IProject <em>IProject</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.IProject
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIProject()
     * @generated
     */
    int IPROJECT = 4;

    /**
     * The number of structural features of the the '<em>IProject</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IPROJECT_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.IAbstractContext <em>IAbstract Context</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.IAbstractContext
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIAbstractContext()
     * @generated
     */
    int IABSTRACT_CONTEXT = 5;

    /**
     * The number of structural features of the the '<em>IAbstract Context</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IABSTRACT_CONTEXT_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.IBlackboard <em>IBlackboard</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.IBlackboard
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIBlackboard()
     * @generated
     */
    int IBLACKBOARD = 6;

    /**
     * The number of structural features of the the '<em>IBlackboard</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IBLACKBOARD_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.IProjectElement <em>IProject Element</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.IProjectElement
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIProjectElement()
     * @generated
     */
    int IPROJECT_ELEMENT = 7;

    /**
     * The number of structural features of the the '<em>IProject Element</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IPROJECT_ELEMENT_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.render.IRenderManager <em>IRender Manager</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.render.IRenderManager
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIRenderManager()
     * @generated
     */
    int IRENDER_MANAGER = 8;

    /**
     * The number of structural features of the the '<em>IRender Manager</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IRENDER_MANAGER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.render.IViewportModel <em>IViewport Model</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.render.IViewportModel
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIViewportModel()
     * @generated
     */
    int IVIEWPORT_MODEL = 9;

    /**
     * The number of structural features of the the '<em>IViewport Model</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IVIEWPORT_MODEL_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link Cloneable <em>Cloneable</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see Cloneable
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getCloneable()
     * @generated
     */
    int CLONEABLE = 19;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.impl.ContextModelImpl <em>Context Model</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.internal.impl.ContextModelImpl
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getContextModel()
     * @generated
     */
    int CONTEXT_MODEL = 10;

    /**
     * The feature id for the '<em><b>Layers</b></em>' containment reference list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int CONTEXT_MODEL__LAYERS = 0;

    /**
     * The feature id for the '<em><b>Map</b></em>' container reference.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTEXT_MODEL__MAP = 1;

    /**
     * The number of structural features of the the '<em>Context Model</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int CONTEXT_MODEL_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.impl.EditManagerImpl <em>Edit Manager</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.internal.impl.EditManagerImpl
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getEditManager()
     * @generated
     */
    int EDIT_MANAGER = 11;

    /**
     * The feature id for the '<em><b>Edit Feature</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EDIT_MANAGER__EDIT_FEATURE = IEDIT_MANAGER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Map Internal</b></em>' container reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int EDIT_MANAGER__MAP_INTERNAL = IEDIT_MANAGER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Edit Layer Internal</b></em>' reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int EDIT_MANAGER__EDIT_LAYER_INTERNAL = IEDIT_MANAGER_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Transaction Type</b></em>' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EDIT_MANAGER__TRANSACTION_TYPE = IEDIT_MANAGER_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Edit Layer Locked</b></em>' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int EDIT_MANAGER__EDIT_LAYER_LOCKED = IEDIT_MANAGER_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Selected Layer</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EDIT_MANAGER__SELECTED_LAYER = IEDIT_MANAGER_FEATURE_COUNT + 5;

    /**
     * The number of structural features of the the '<em>Edit Manager</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int EDIT_MANAGER_FEATURE_COUNT = IEDIT_MANAGER_FEATURE_COUNT + 6;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.impl.LayerImpl <em>Layer</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.internal.impl.LayerImpl
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getLayer()
     * @generated
     */
    int LAYER = 12;

    /**
     * The feature id for the '<em><b>Context Model</b></em>' container reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int LAYER__CONTEXT_MODEL = ILAYER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Filter</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__FILTER = ILAYER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Style Blackboard</b></em>' containment reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int LAYER__STYLE_BLACKBOARD = ILAYER_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Zorder</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__ZORDER = ILAYER_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Status</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__STATUS = ILAYER_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Selectable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__SELECTABLE = ILAYER_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__NAME = ILAYER_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Catalog Ref</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__CATALOG_REF = ILAYER_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>ID</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__ID = ILAYER_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Visible</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__VISIBLE = ILAYER_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Geo Resource</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__GEO_RESOURCE = ILAYER_FEATURE_COUNT + 10;

    /**
     * The feature id for the '<em><b>Geo Resources</b></em>' attribute list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int LAYER__GEO_RESOURCES = ILAYER_FEATURE_COUNT + 11;

    /**
     * The feature id for the '<em><b>Glyph</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__GLYPH = ILAYER_FEATURE_COUNT + 12;

    /**
     * The feature id for the '<em><b>CRS</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__CRS = ILAYER_FEATURE_COUNT + 13;

    /**
     * The feature id for the '<em><b>Properties</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__PROPERTIES = ILAYER_FEATURE_COUNT + 14;

    /**
     * The feature id for the '<em><b>Colour Scheme</b></em>' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__COLOUR_SCHEME = ILAYER_FEATURE_COUNT + 15;

    /**
     * The feature id for the '<em><b>Default Color</b></em>' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__DEFAULT_COLOR = ILAYER_FEATURE_COUNT + 16;

    /**
     * The feature id for the '<em><b>SimpleFeature Changes</b></em>' attribute list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int LAYER__FEATURE_CHANGES = ILAYER_FEATURE_COUNT + 17;

    /**
     * The feature id for the '<em><b>Min Scale Denominator</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__MIN_SCALE_DENOMINATOR = ILAYER_FEATURE_COUNT + 18;

    /**
     * The feature id for the '<em><b>Max Scale Denominator</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER__MAX_SCALE_DENOMINATOR = ILAYER_FEATURE_COUNT + 19;

    /**
     * The number of structural features of the '<em>Layer</em>' class.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER_FEATURE_COUNT = ILAYER_FEATURE_COUNT + 20;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.ProjectElement <em>Element</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.internal.ProjectElement
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getProjectElement()
     * @generated
     */
    int PROJECT_ELEMENT = 15;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT_ELEMENT__NAME = IPROJECT_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Project Internal</b></em>' reference.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT_ELEMENT__PROJECT_INTERNAL = IPROJECT_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Element</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int PROJECT_ELEMENT_FEATURE_COUNT = IPROJECT_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.impl.MapImpl <em>Map</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.internal.impl.MapImpl
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getMap()
     * @generated
     */
    int MAP = 13;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int MAP__NAME = PROJECT_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Project Internal</b></em>' reference.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAP__PROJECT_INTERNAL = PROJECT_ELEMENT__PROJECT_INTERNAL;

    /**
     * The feature id for the '<em><b>Context Model</b></em>' containment reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int MAP__CONTEXT_MODEL = PROJECT_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Abstract</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAP__ABSTRACT = PROJECT_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Nav Command Stack</b></em>' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int MAP__NAV_COMMAND_STACK = PROJECT_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Command Stack</b></em>' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAP__COMMAND_STACK = PROJECT_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Layer Factory</b></em>' containment reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int MAP__LAYER_FACTORY = PROJECT_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Viewport Model Internal</b></em>' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAP__VIEWPORT_MODEL_INTERNAL = PROJECT_ELEMENT_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Color Palette</b></em>' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAP__COLOR_PALETTE = PROJECT_ELEMENT_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Edit Manager Internal</b></em>' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAP__EDIT_MANAGER_INTERNAL = PROJECT_ELEMENT_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Render Manager Internal</b></em>' reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int MAP__RENDER_MANAGER_INTERNAL = PROJECT_ELEMENT_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Colour Scheme</b></em>' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAP__COLOUR_SCHEME = PROJECT_ELEMENT_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Black Board Internal</b></em>' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAP__BLACK_BOARD_INTERNAL = PROJECT_ELEMENT_FEATURE_COUNT + 10;

    /**
     * The number of structural features of the '<em>Map</em>' class.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAP_FEATURE_COUNT = PROJECT_ELEMENT_FEATURE_COUNT + 11;

    /**
     * Event for batched events.
     * <p>
     * <ul>
     * <li>When a batch event occurs the Notification object will be implement the Iterable
     * <Notification>interface.</li>
     * <li>By definition of this event all Notification objects will have occurred effectively
     * simultaneously.</li>
     * <li>All the notifiers of the notification objects will be Layer objects.</li>
     * <li>The Event object received will not be one of the objects in the iterations</li>
     * </ul>
     * </p>
     */
    int MAP__BATCH_EVENT = MAP_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.impl.ProjectImpl <em>Project</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.internal.impl.ProjectImpl
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getProject()
     * @generated
     */
    int PROJECT = 14;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT__NAME = IPROJECT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Elements Internal</b></em>' reference list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int PROJECT__ELEMENTS_INTERNAL = IPROJECT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Project</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int PROJECT_FEATURE_COUNT = IPROJECT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.impl.ProjectRegistryImpl <em>Registry</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.internal.impl.ProjectRegistryImpl
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getProjectRegistry()
     * @generated
     */
    int PROJECT_REGISTRY = 16;

    /**
     * The feature id for the '<em><b>Current Project</b></em>' reference.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT_REGISTRY__CURRENT_PROJECT = 0;

    /**
     * The feature id for the '<em><b>Projects</b></em>' reference list.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT_REGISTRY__PROJECTS = 1;

    /**
     * The number of structural features of the the '<em>Registry</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int PROJECT_REGISTRY_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.IStyleBlackboard <em>IStyle Blackboard</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.project.IStyleBlackboard
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIStyleBlackboard()
     * @generated
     */
    int ISTYLE_BLACKBOARD = 26;

    /**
     * The number of structural features of the '<em>IStyle Blackboard</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ISTYLE_BLACKBOARD_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.impl.StyleBlackboardImpl <em>Style Blackboard</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.internal.impl.StyleBlackboardImpl
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getStyleBlackboard()
     * @generated
     */
    int STYLE_BLACKBOARD = 17;

    /**
     * The feature id for the '<em><b>Content</b></em>' containment reference list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int STYLE_BLACKBOARD__CONTENT = ISTYLE_BLACKBOARD_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Style Blackboard</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int STYLE_BLACKBOARD_FEATURE_COUNT = ISTYLE_BLACKBOARD_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.impl.StyleEntryImpl <em>Style Entry</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.internal.impl.StyleEntryImpl
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getStyleEntry()
     * @generated
     */
    int STYLE_ENTRY = 18;

    /**
     * The feature id for the '<em><b>ID</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int STYLE_ENTRY__ID = 0;

    /**
     * The feature id for the '<em><b>Memento</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int STYLE_ENTRY__MEMENTO = 1;

    /**
     * The feature id for the '<em><b>Style</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int STYLE_ENTRY__STYLE = 2;

    /**
     * The feature id for the '<em><b>Style Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STYLE_ENTRY__STYLE_CLASS = 3;

    /**
     * The number of structural features of the the '<em>Style Entry</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int STYLE_ENTRY_FEATURE_COUNT = 4;

    /**
     * The number of structural features of the the '<em>Cloneable</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int CLONEABLE_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.impl.LayerFactoryImpl <em>Layer Factory</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.internal.impl.LayerFactoryImpl
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getLayerFactory()
     * @generated
     */
    int LAYER_FACTORY = 20;

    /**
     * The feature id for the '<em><b>Map</b></em>' container reference.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LAYER_FACTORY__MAP = 0;

    /**
     * The number of structural features of the the '<em>Layer Factory</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int LAYER_FACTORY_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.eclipse.core.runtime.IAdaptable <em>IAdaptable</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.core.runtime.IAdaptable
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIAdaptable()
     * @generated
     */
    int IADAPTABLE = 21;

    /**
     * The number of structural features of the the '<em>IAdaptable</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IADAPTABLE_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.core.IBlockingAdaptable <em>IBlocking Adaptable</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.core.IBlockingAdaptable
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIBlockingAdaptable()
     * @generated
     */
    int IBLOCKING_ADAPTABLE = 22;

    /**
     * The number of structural features of the the '<em>IBlocking Adaptable</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IBLOCKING_ADAPTABLE_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.impl.BlackboardImpl <em>Blackboard</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.internal.impl.BlackboardImpl
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getBlackboard()
     * @generated
     */
    int BLACKBOARD = 23;

    /**
     * The feature id for the '<em><b>Entries</b></em>' containment reference list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int BLACKBOARD__ENTRIES = IBLACKBOARD_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Blackboard</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int BLACKBOARD_FEATURE_COUNT = IBLACKBOARD_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.impl.BlackboardEntryImpl <em>Blackboard Entry</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.internal.impl.BlackboardEntryImpl
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getBlackboardEntry()
     * @generated
     */
    int BLACKBOARD_ENTRY = 24;

    /**
     * The feature id for the '<em><b>Key</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int BLACKBOARD_ENTRY__KEY = 0;

    /**
     * The feature id for the '<em><b>Memento</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int BLACKBOARD_ENTRY__MEMENTO = 1;

    /**
     * The feature id for the '<em><b>Object Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BLACKBOARD_ENTRY__OBJECT_CLASS = 2;

    /**
     * The feature id for the '<em><b>Object</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int BLACKBOARD_ENTRY__OBJECT = 3;

    /**
     * The number of structural features of the the '<em>Blackboard Entry</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int BLACKBOARD_ENTRY_FEATURE_COUNT = 4;

    /**
     * The meta object id for the '{@link net.refractions.udig.catalog.IResolveChangeListener <em>IResolve Change Listener</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see net.refractions.udig.catalog.IResolveChangeListener
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIResolveChangeListener()
     * @generated
     */
    int IRESOLVE_CHANGE_LISTENER = 25;

    /**
     * The number of structural features of the '<em>IResolve Change Listener</em>' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IRESOLVE_CHANGE_LISTENER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '<em>Coordinate</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see com.vividsolutions.jts.geom.Coordinate
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getCoordinate()
     * @generated
     */
    int COORDINATE = 27;

    /**
     * The meta object id for the '<em>Map Display</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see net.refractions.udig.project.render.displayAdapter.IMapDisplay
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getMapDisplay()
     * @generated
     */
    int MAP_DISPLAY = 28;

    /**
     * The meta object id for the '<em>Feature Results</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.geotools.data.FeatureResults
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getFeatureResults()
     * @generated
     */
    int FEATURE_RESULTS = 29;

    /**
     * The meta object id for the '<em>List</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see java.util.List
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getList()
     * @generated
     */
    int LIST = 30;

    /**
     * The meta object id for the '<em>Affine Transform</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.awt.geom.AffineTransform
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getAffineTransform()
     * @generated
     */
    int AFFINE_TRANSFORM = 31;

    /**
     * The meta object id for the '<em>Nav Command Stack</em>' data type.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see net.refractions.udig.project.command.NavCommandStack
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getNavCommandStack()
     * @generated
     */
    int NAV_COMMAND_STACK = 32;

    /**
     * The meta object id for the '<em>IGeo Resource</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.catalog.IGeoResource
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIGeoResource()
     * @generated
     */
    int IGEO_RESOURCE = 33;

    /**
     * The meta object id for the '<em>Filter</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see org.opengis.filter.Filter
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getFilter()
     * @generated
     */
    int FILTER = 34;

    /**
     * The meta object id for the '<em>Coordinate Reference System</em>' data type. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.opengis.referencing.crs.CoordinateReferenceSystem
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getCoordinateReferenceSystem()
     * @generated
     */
    int COORDINATE_REFERENCE_SYSTEM = 35;

    /**
     * The meta object id for the '<em>Command Stack</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.project.command.CommandStack
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getCommandStack()
     * @generated
     */
    int COMMAND_STACK = 36;

    /**
     * The meta object id for the '<em>Feature</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see org.geotools.feature.SimpleFeature
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getFeature()
     * @generated
     */
    int FEATURE = 37;

    /**
     * The meta object id for the '<em>Point</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see java.awt.Point
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getPoint()
     * @generated
     */
    int POINT = 38;

    /**
     * The meta object id for the '<em>Adapter</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see org.eclipse.emf.common.notify.Adapter
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getAdapter()
     * @generated
     */
    int ADAPTER = 39;

    /**
     * The meta object id for the '<em>IProgress Monitor</em>' data type.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see org.eclipse.core.runtime.IProgressMonitor
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIProgressMonitor()
     * @generated
     */
    int IPROGRESS_MONITOR = 40;

    /**
     * The meta object id for the '<em>Query</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see org.geotools.data.Query
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getQuery()
     * @generated
     */
    int QUERY = 41;

    /**
     * The meta object id for the '<em>URL</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see java.net.URL
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getURL()
     * @generated
     */
    int URL = 42;

    /**
     * The meta object id for the '<em>Image Descriptor</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jface.resource.ImageDescriptor
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getImageDescriptor()
     * @generated
     */
    int IMAGE_DESCRIPTOR = 43;

    /**
     * The meta object id for the '<em>Edit Command</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.project.command.EditCommand
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getEditCommand()
     * @generated
     */
    int EDIT_COMMAND = 44;

    /**
     * The meta object id for the '<em>Nav Command</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see net.refractions.udig.project.command.NavCommand
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getNavCommand()
     * @generated
     */
    int NAV_COMMAND = 45;

    /**
     * The meta object id for the '<em>Envelope</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see com.vividsolutions.jts.geom.Envelope
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getEnvelope()
     * @generated
     */
    int ENVELOPE = 46;

    /**
     * The meta object id for the '<em>Edit Manager Control Command</em>' data type. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getEditManagerControlCommand()
     * @generated
     */
    int EDIT_MANAGER_CONTROL_COMMAND = 47;

    /**
     * The meta object id for the '<em>Command</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see net.refractions.udig.project.command.MapCommand
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getCommand()
     * @generated
     */
    int COMMAND = 48;

    /**
     * The meta object id for the '<em>URI</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see org.eclipse.emf.common.util.URI
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getURI()
     * @generated
     */
    int URI = 49;

    /**
     * The meta object id for the '<em>Catalog Ref</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see net.refractions.udig.project.internal.CatalogRef
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getCatalogRef()
     * @generated
     */
    int CATALOG_REF = 50;

    /**
     * The meta object id for the '<em>Colour Palette</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.ui.palette.ColourPalette
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getColourPalette()
     * @generated
     */
    int COLOUR_PALETTE = 51;

    /**
     * The meta object id for the '<em>Colour Scheme</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.ui.palette.ColourScheme
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getColourScheme()
     * @generated
     */
    int COLOUR_SCHEME = 52;

    /**
     * The meta object id for the '<em>Mutable Pico Container</em>' data type. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.picocontainer.MutablePicoContainer
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getMutablePicoContainer()
     * @generated
     */
    int MUTABLE_PICO_CONTAINER = 53;

    /**
     * The meta object id for the '<em>Referenced Envelope</em>' data type.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see org.geotools.geometry.jts.ReferencedEnvelope
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getReferencedEnvelope()
     * @generated
     */
    int REFERENCED_ENVELOPE = 54;

    /**
     * The meta object id for the '<em>Feature Event</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.geotools.data.FeatureEvent
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getFeatureEvent()
     * @generated
     */
    int FEATURE_EVENT = 55;

    /**
     * The meta object id for the '<em>Simple Feature</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.opengis.feature.simple.SimpleFeature
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getSimpleFeature()
     * @generated
     */
    int SIMPLE_FEATURE = 56;

    /**
     * The meta object id for the '<em>Illegal Argument Exception</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.IllegalArgumentException
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIllegalArgumentException()
     * @generated
     */
    int ILLEGAL_ARGUMENT_EXCEPTION = 57;

    /**
     * The meta object id for the '<em>IO Exception</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.io.IOException
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIOException()
     * @generated
     */
    int IO_EXCEPTION = 58;

    /**
     * The meta object id for the '<em>Color</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.awt.Color
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getColor()
     * @generated
     */
    int COLOR = 59;

    /**
     * The meta object id for the '<em>Brewer Palette</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.geotools.brewer.color.BrewerPalette
     * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getBrewerPalette()
     * @generated
     */
    int BREWER_PALETTE = 60;

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see Comparable
     * @model instanceClass="Comparable"
     * @generated
     */
    EClass getComparable();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.IMap
     * @model instanceClass="net.refractions.udig.project.IMap"
     * @generated
     */
    EClass getIMap();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.ILayer
     * @model instanceClass="net.refractions.udig.project.ILayer"
     * @generated
     */
    EClass getILayer();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.IEditManager
     * @model instanceClass="net.refractions.udig.project.IEditManager"
     * @generated
     */
    EClass getIEditManager();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.IProject
     * @model instanceClass="net.refractions.udig.project.IProject"
     * @generated
     */
    EClass getIProject();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.IAbstractContext
     * @model instanceClass="net.refractions.udig.project.IAbstractContext"
     * @generated
     */
    EClass getIAbstractContext();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.IBlackboard
     * @model instanceClass="net.refractions.udig.project.IBlackboard"
     * @generated
     */
    EClass getIBlackboard();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.IProjectElement
     * @model instanceClass="net.refractions.udig.project.IProjectElement"
     * @generated
     */
    EClass getIProjectElement();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.render.IRenderManager
     * @model instanceClass="net.refractions.udig.project.render.IRenderManager"
     * @generated
     */
    EClass getIRenderManager();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.render.IViewportModel
     * @model instanceClass="net.refractions.udig.project.render.IViewportModel"
     * @generated
     */
    EClass getIViewportModel();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.ContextModel <em>Context Model</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Context Model</em>'.
     * @see net.refractions.udig.project.internal.ContextModel
     * @generated
     */
    EClass getContextModel();

    /**
     * Returns the meta object for the containment reference list '{@link net.refractions.udig.project.internal.ContextModel#getLayers <em>Layers</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Layers</em>'.
     * @see net.refractions.udig.project.internal.ContextModel#getLayers()
     * @see #getContextModel()
     * @generated
     */
    EReference getContextModel_Layers();

    /**
     * Returns the meta object for the container reference '{@link net.refractions.udig.project.internal.ContextModel#getMap <em>Map</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Map</em>'.
     * @see net.refractions.udig.project.internal.ContextModel#getMap()
     * @see #getContextModel()
     * @generated
     */
    EReference getContextModel_Map();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.EditManager <em>Edit Manager</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Edit Manager</em>'.
     * @see net.refractions.udig.project.internal.EditManager
     * @generated
     */
    EClass getEditManager();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.EditManager#getEditFeature <em>Edit Feature</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Edit Feature</em>'.
     * @see net.refractions.udig.project.internal.EditManager#getEditFeature()
     * @see #getEditManager()
     * @generated
     */
    EAttribute getEditManager_EditFeature();

    /**
     * Returns the meta object for the container reference '{@link net.refractions.udig.project.internal.EditManager#getMapInternal <em>Map Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Map Internal</em>'.
     * @see net.refractions.udig.project.internal.EditManager#getMapInternal()
     * @see #getEditManager()
     * @generated
     */
    EReference getEditManager_MapInternal();

    /**
     * Returns the meta object for the reference '{@link net.refractions.udig.project.internal.EditManager#getEditLayerInternal <em>Edit Layer Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Edit Layer Internal</em>'.
     * @see net.refractions.udig.project.internal.EditManager#getEditLayerInternal()
     * @see #getEditManager()
     * @generated
     */
    EReference getEditManager_EditLayerInternal();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.EditManager#getTransactionType <em>Transaction Type</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Transaction Type</em>'.
     * @see net.refractions.udig.project.internal.EditManager#getTransactionType()
     * @see #getEditManager()
     * @generated
     */
    EAttribute getEditManager_TransactionType();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.EditManager#isEditLayerLocked <em>Edit Layer Locked</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Edit Layer Locked</em>'.
     * @see net.refractions.udig.project.internal.EditManager#isEditLayerLocked()
     * @see #getEditManager()
     * @generated
     */
    EAttribute getEditManager_EditLayerLocked();

    /**
     * Returns the meta object for the reference '{@link net.refractions.udig.project.internal.EditManager#getSelectedLayer <em>Selected Layer</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Selected Layer</em>'.
     * @see net.refractions.udig.project.internal.EditManager#getSelectedLayer()
     * @see #getEditManager()
     * @generated
     */
    EReference getEditManager_SelectedLayer();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.Layer <em>Layer</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Layer</em>'.
     * @see net.refractions.udig.project.internal.Layer
     * @generated
     */
    EClass getLayer();

    /**
     * Returns the meta object for the container reference '{@link net.refractions.udig.project.internal.Layer#getContextModel <em>Context Model</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Context Model</em>'.
     * @see net.refractions.udig.project.internal.Layer#getContextModel()
     * @see #getLayer()
     * @generated
     */
    EReference getLayer_ContextModel();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getFilter <em>Filter</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Filter</em>'.
     * @see net.refractions.udig.project.internal.Layer#getFilter()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_Filter();

    /**
     * Returns the meta object for the containment reference '{@link net.refractions.udig.project.internal.Layer#getStyleBlackboard <em>Style Blackboard</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Style Blackboard</em>'.
     * @see net.refractions.udig.project.internal.Layer#getStyleBlackboard()
     * @see #getLayer()
     * @generated
     */
    EReference getLayer_StyleBlackboard();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getZorder <em>Zorder</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Zorder</em>'.
     * @see net.refractions.udig.project.internal.Layer#getZorder()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_Zorder();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getStatus <em>Status</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Status</em>'.
     * @see net.refractions.udig.project.internal.Layer#getStatus()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_Status();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#isSelectable <em>Selectable</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Selectable</em>'.
     * @see net.refractions.udig.project.internal.Layer#isSelectable()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_Selectable();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getName <em>Name</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see net.refractions.udig.project.internal.Layer#getName()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_Name();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getCatalogRef <em>Catalog Ref</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Catalog Ref</em>'.
     * @see net.refractions.udig.project.internal.Layer#getCatalogRef()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_CatalogRef();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getID <em>ID</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>ID</em>'.
     * @see net.refractions.udig.project.internal.Layer#getID()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_ID();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#isVisible <em>Visible</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Visible</em>'.
     * @see net.refractions.udig.project.internal.Layer#isVisible()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_Visible();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getGeoResource <em>Geo Resource</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Geo Resource</em>'.
     * @see net.refractions.udig.project.internal.Layer#getGeoResource()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_GeoResource();

    /**
     * Returns the meta object for the attribute list '{@link net.refractions.udig.project.internal.Layer#getGeoResources <em>Geo Resources</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Geo Resources</em>'.
     * @see net.refractions.udig.project.internal.Layer#getGeoResources()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_GeoResources();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getGlyph <em>Glyph</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Glyph</em>'.
     * @see net.refractions.udig.project.internal.Layer#getGlyph()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_Glyph();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getCRS <em>CRS</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>CRS</em>'.
     * @see net.refractions.udig.project.internal.Layer#getCRS()
     * @see #getLayer()
     * @generated
     */
    @SuppressWarnings("deprecation")
    EAttribute getLayer_CRS();

    /**
     * Returns the meta object for the reference '{@link net.refractions.udig.project.internal.Layer#getProperties <em>Properties</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Properties</em>'.
     * @see net.refractions.udig.project.internal.Layer#getProperties()
     * @see #getLayer()
     * @generated
     */
    EReference getLayer_Properties();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getColourScheme <em>Colour Scheme</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Colour Scheme</em>'.
     * @see net.refractions.udig.project.internal.Layer#getColourScheme()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_ColourScheme();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getDefaultColor <em>Default Color</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Default Color</em>'.
     * @see net.refractions.udig.project.internal.Layer#getDefaultColor()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_DefaultColor();

    /**
     * Returns the meta object for the attribute list '{@link net.refractions.udig.project.internal.Layer#getFeatureChanges <em>Feature Changes</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Feature Changes</em>'.
     * @see net.refractions.udig.project.internal.Layer#getFeatureChanges()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_FeatureChanges();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getMinScaleDenominator <em>Min Scale Denominator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Min Scale Denominator</em>'.
     * @see net.refractions.udig.project.internal.Layer#getMinScaleDenominator()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_MinScaleDenominator();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Layer#getMaxScaleDenominator <em>Max Scale Denominator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Max Scale Denominator</em>'.
     * @see net.refractions.udig.project.internal.Layer#getMaxScaleDenominator()
     * @see #getLayer()
     * @generated
     */
    EAttribute getLayer_MaxScaleDenominator();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.Map <em>Map</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Map</em>'.
     * @see net.refractions.udig.project.internal.Map
     * @generated
     */
    EClass getMap();

    /**
     * Returns the meta object for the containment reference '{@link net.refractions.udig.project.internal.Map#getContextModel <em>Context Model</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Context Model</em>'.
     * @see net.refractions.udig.project.internal.Map#getContextModel()
     * @see #getMap()
     * @generated
     */
    EReference getMap_ContextModel();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Map#getAbstract <em>Abstract</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Abstract</em>'.
     * @see net.refractions.udig.project.internal.Map#getAbstract()
     * @see #getMap()
     * @generated
     */
    EAttribute getMap_Abstract();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Map#getNavCommandStack <em>Nav Command Stack</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Nav Command Stack</em>'.
     * @see net.refractions.udig.project.internal.Map#getNavCommandStack()
     * @see #getMap()
     * @generated
     */
    EAttribute getMap_NavCommandStack();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Map#getCommandStack <em>Command Stack</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Command Stack</em>'.
     * @see net.refractions.udig.project.internal.Map#getCommandStack()
     * @see #getMap()
     * @generated
     */
    EAttribute getMap_CommandStack();

    /**
     * Returns the meta object for the containment reference '{@link net.refractions.udig.project.internal.Map#getLayerFactory <em>Layer Factory</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Layer Factory</em>'.
     * @see net.refractions.udig.project.internal.Map#getLayerFactory()
     * @see #getMap()
     * @generated
     */
    EReference getMap_LayerFactory();

    /**
     * Returns the meta object for the containment reference '{@link net.refractions.udig.project.internal.Map#getViewportModelInternal <em>Viewport Model Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Viewport Model Internal</em>'.
     * @see net.refractions.udig.project.internal.Map#getViewportModelInternal()
     * @see #getMap()
     * @generated
     */
    EReference getMap_ViewportModelInternal();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Map#getColorPalette <em>Color Palette</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Color Palette</em>'.
     * @see net.refractions.udig.project.internal.Map#getColorPalette()
     * @see #getMap()
     * @generated
     */
    EAttribute getMap_ColorPalette();

    /**
     * Returns the meta object for the containment reference '{@link net.refractions.udig.project.internal.Map#getEditManagerInternal <em>Edit Manager Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Edit Manager Internal</em>'.
     * @see net.refractions.udig.project.internal.Map#getEditManagerInternal()
     * @see #getMap()
     * @generated
     */
    EReference getMap_EditManagerInternal();

    /**
     * Returns the meta object for the reference '{@link net.refractions.udig.project.internal.Map#getRenderManagerInternal <em>Render Manager Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Render Manager Internal</em>'.
     * @see net.refractions.udig.project.internal.Map#getRenderManagerInternal()
     * @see #getMap()
     * @generated
     */
    EReference getMap_RenderManagerInternal();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Map#getColourScheme <em>Colour Scheme</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Colour Scheme</em>'.
     * @see net.refractions.udig.project.internal.Map#getColourScheme()
     * @see #getMap()
     * @generated
     */
    EAttribute getMap_ColourScheme();

    /**
     * Returns the meta object for the containment reference '{@link net.refractions.udig.project.internal.Map#getBlackBoardInternal <em>Black Board Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Black Board Internal</em>'.
     * @see net.refractions.udig.project.internal.Map#getBlackBoardInternal()
     * @see #getMap()
     * @generated
     */
    EReference getMap_BlackBoardInternal();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.Project <em>Project</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Project</em>'.
     * @see net.refractions.udig.project.internal.Project
     * @generated
     */
    EClass getProject();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.Project#getName <em>Name</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see net.refractions.udig.project.internal.Project#getName()
     * @see #getProject()
     * @generated
     */
    EAttribute getProject_Name();

    /**
     * Returns the meta object for the reference list '{@link net.refractions.udig.project.internal.Project#getElementsInternal <em>Elements Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Elements Internal</em>'.
     * @see net.refractions.udig.project.internal.Project#getElementsInternal()
     * @see #getProject()
     * @generated
     */
    EReference getProject_ElementsInternal();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.ProjectElement <em>Element</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Element</em>'.
     * @see net.refractions.udig.project.internal.ProjectElement
     * @generated
     */
    EClass getProjectElement();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.ProjectElement#getName <em>Name</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see net.refractions.udig.project.internal.ProjectElement#getName()
     * @see #getProjectElement()
     * @generated
     */
    EAttribute getProjectElement_Name();

    /**
     * Returns the meta object for the reference '{@link net.refractions.udig.project.internal.ProjectElement#getProjectInternal <em>Project Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Project Internal</em>'.
     * @see net.refractions.udig.project.internal.ProjectElement#getProjectInternal()
     * @see #getProjectElement()
     * @generated
     */
    EReference getProjectElement_ProjectInternal();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.ProjectRegistry <em>Registry</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Registry</em>'.
     * @see net.refractions.udig.project.internal.ProjectRegistry
     * @generated
     */
    EClass getProjectRegistry();

    /**
     * Returns the meta object for the reference '{@link net.refractions.udig.project.internal.ProjectRegistry#getCurrentProject <em>Current Project</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Current Project</em>'.
     * @see net.refractions.udig.project.internal.ProjectRegistry#getCurrentProject()
     * @see #getProjectRegistry()
     * @generated
     */
    EReference getProjectRegistry_CurrentProject();

    /**
     * Returns the meta object for the reference list '{@link net.refractions.udig.project.internal.ProjectRegistry#getProjects <em>Projects</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Projects</em>'.
     * @see net.refractions.udig.project.internal.ProjectRegistry#getProjects()
     * @see #getProjectRegistry()
     * @generated
     */
    EReference getProjectRegistry_Projects();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.StyleBlackboard <em>Style Blackboard</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Style Blackboard</em>'.
     * @see net.refractions.udig.project.internal.StyleBlackboard
     * @generated
     */
    EClass getStyleBlackboard();

    /**
     * Returns the meta object for the containment reference list '{@link net.refractions.udig.project.internal.StyleBlackboard#getContent <em>Content</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Content</em>'.
     * @see net.refractions.udig.project.internal.StyleBlackboard#getContent()
     * @see #getStyleBlackboard()
     * @generated
     */
    EReference getStyleBlackboard_Content();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.StyleEntry <em>Style Entry</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Style Entry</em>'.
     * @see net.refractions.udig.project.internal.StyleEntry
     * @generated
     */
    EClass getStyleEntry();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.StyleEntry#getID <em>ID</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>ID</em>'.
     * @see net.refractions.udig.project.internal.StyleEntry#getID()
     * @see #getStyleEntry()
     * @generated
     */
    EAttribute getStyleEntry_ID();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.StyleEntry#getMemento <em>Memento</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Memento</em>'.
     * @see net.refractions.udig.project.internal.StyleEntry#getMemento()
     * @see #getStyleEntry()
     * @generated
     */
    EAttribute getStyleEntry_Memento();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.StyleEntry#getStyle <em>Style</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Style</em>'.
     * @see net.refractions.udig.project.internal.StyleEntry#getStyle()
     * @see #getStyleEntry()
     * @generated
     */
    EAttribute getStyleEntry_Style();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.StyleEntry#getStyleClass <em>Style Class</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Style Class</em>'.
     * @see net.refractions.udig.project.internal.StyleEntry#getStyleClass()
     * @see #getStyleEntry()
     * @generated
     */
    EAttribute getStyleEntry_StyleClass();

    /**
     * Returns the meta object for class '{@link Cloneable <em>Cloneable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Cloneable</em>'.
     * @see Cloneable
     * @model instanceClass="Cloneable"
     * @generated
     */
    EClass getCloneable();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.LayerFactory <em>Layer Factory</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Layer Factory</em>'.
     * @see net.refractions.udig.project.internal.LayerFactory
     * @generated
     */
    EClass getLayerFactory();

    /**
     * Returns the meta object for the container reference '{@link net.refractions.udig.project.internal.LayerFactory#getMap <em>Map</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Map</em>'.
     * @see net.refractions.udig.project.internal.LayerFactory#getMap()
     * @see #getLayerFactory()
     * @generated
     */
    EReference getLayerFactory_Map();

    /**
     * Returns the meta object for class '{@link org.eclipse.core.runtime.IAdaptable <em>IAdaptable</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>IAdaptable</em>'.
     * @see org.eclipse.core.runtime.IAdaptable
     * @model instanceClass="org.eclipse.core.runtime.IAdaptable"
     * @generated
     */
    EClass getIAdaptable();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.core.IBlockingAdaptable <em>IBlocking Adaptable</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>IBlocking Adaptable</em>'.
     * @see net.refractions.udig.core.IBlockingAdaptable
     * @model instanceClass="net.refractions.udig.core.IBlockingAdaptable"
     * @generated
     */
    EClass getIBlockingAdaptable();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.Blackboard <em>Blackboard</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Blackboard</em>'.
     * @see net.refractions.udig.project.internal.Blackboard
     * @generated
     */
    EClass getBlackboard();

    /**
     * Returns the meta object for the containment reference list '{@link net.refractions.udig.project.internal.Blackboard#getEntries <em>Entries</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Entries</em>'.
     * @see net.refractions.udig.project.internal.Blackboard#getEntries()
     * @see #getBlackboard()
     * @generated
     */
    EReference getBlackboard_Entries();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.BlackboardEntry <em>Blackboard Entry</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Blackboard Entry</em>'.
     * @see net.refractions.udig.project.internal.BlackboardEntry
     * @generated
     */
    EClass getBlackboardEntry();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.BlackboardEntry#getKey <em>Key</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Key</em>'.
     * @see net.refractions.udig.project.internal.BlackboardEntry#getKey()
     * @see #getBlackboardEntry()
     * @generated
     */
    EAttribute getBlackboardEntry_Key();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.BlackboardEntry#getMemento <em>Memento</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Memento</em>'.
     * @see net.refractions.udig.project.internal.BlackboardEntry#getMemento()
     * @see #getBlackboardEntry()
     * @generated
     */
    EAttribute getBlackboardEntry_Memento();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.BlackboardEntry#getObjectClass <em>Object Class</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Object Class</em>'.
     * @see net.refractions.udig.project.internal.BlackboardEntry#getObjectClass()
     * @see #getBlackboardEntry()
     * @generated
     */
    EAttribute getBlackboardEntry_ObjectClass();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.BlackboardEntry#getObject <em>Object</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Object</em>'.
     * @see net.refractions.udig.project.internal.BlackboardEntry#getObject()
     * @see #getBlackboardEntry()
     * @generated
     */
    EAttribute getBlackboardEntry_Object();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.catalog.IResolveChangeListener <em>IResolve Change Listener</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>IResolve Change Listener</em>'.
     * @see net.refractions.udig.catalog.IResolveChangeListener
     * @model instanceClass="net.refractions.udig.catalog.IResolveChangeListener"
     * @generated
     */
    EClass getIResolveChangeListener();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.IStyleBlackboard <em>IStyle Blackboard</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>IStyle Blackboard</em>'.
     * @see net.refractions.udig.project.IStyleBlackboard
     * @model instanceClass="net.refractions.udig.project.IStyleBlackboard"
     * @generated
     */
    EClass getIStyleBlackboard();

    /**
     * Returns the meta object for data type '{@link com.vividsolutions.jts.geom.Coordinate <em>Coordinate</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Coordinate</em>'.
     * @see com.vividsolutions.jts.geom.Coordinate
     * @model instanceClass="com.vividsolutions.jts.geom.Coordinate"
     * @generated
     */
    EDataType getCoordinate();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.project.render.displayAdapter.IMapDisplay <em>Map Display</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Map Display</em>'.
     * @see net.refractions.udig.project.render.displayAdapter.IMapDisplay
     * @model instanceClass="net.refractions.udig.project.render.displayAdapter.IMapDisplay"
     * @generated
     */
    EDataType getMapDisplay();

    /**
     * Returns the meta object for data type '{@link org.geotools.data.FeatureResults <em>Feature Results</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Feature Results</em>'.
     * @see org.geotools.data.FeatureResults
     * @model instanceClass="org.geotools.data.FeatureResults"
     * @generated
     */
    EDataType getFeatureResults();

    /**
     * Returns the meta object for data type '{@link java.util.List <em>List</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for data type '<em>List</em>'.
     * @see java.util.List
     * @model instanceClass="java.util.List"
     * @generated
     */
    EDataType getList();

    /**
     * Returns the meta object for data type '{@link java.awt.geom.AffineTransform <em>Affine Transform</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Affine Transform</em>'.
     * @see java.awt.geom.AffineTransform
     * @model instanceClass="java.awt.geom.AffineTransform"
     * @generated
     */
    EDataType getAffineTransform();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.project.command.NavCommandStack <em>Nav Command Stack</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Nav Command Stack</em>'.
     * @see net.refractions.udig.project.command.NavCommandStack
     * @model instanceClass="net.refractions.udig.project.command.NavCommandStack"
     * @generated
     */
    EDataType getNavCommandStack();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.catalog.IGeoResource <em>IGeo Resource</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>IGeo Resource</em>'.
     * @see net.refractions.udig.catalog.IGeoResource
     * @model instanceClass="net.refractions.udig.catalog.IGeoResource"
     * @generated
     */
    EDataType getIGeoResource();

    /**
     * Returns the meta object for data type '{@link org.opengis.filter.Filter <em>Filter</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Filter</em>'.
     * @see org.opengis.filter.Filter
     * @model instanceClass="org.opengis.filter.Filter"
     * @generated
     */
    EDataType getFilter();

    /**
     * Returns the meta object for data type '{@link org.opengis.referencing.crs.CoordinateReferenceSystem <em>Coordinate Reference System</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Coordinate Reference System</em>'.
     * @see org.opengis.referencing.crs.CoordinateReferenceSystem
     * @model instanceClass="org.opengis.referencing.crs.CoordinateReferenceSystem"
     * @generated
     */
    EDataType getCoordinateReferenceSystem();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.project.command.CommandStack <em>Command Stack</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Command Stack</em>'.
     * @see net.refractions.udig.project.command.CommandStack
     * @model instanceClass="net.refractions.udig.project.command.CommandStack"
     * @generated
     */
    EDataType getCommandStack();

    /**
     * Returns the meta object for data type '{@link org.geotools.feature.SimpleFeature <em>Feature</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Feature</em>'.
     * @see org.geotools.feature.SimpleFeature
     * @model instanceClass="org.geotools.feature.SimpleFeature"
     * @generated
     */
    EDataType getFeature();

    /**
     * Returns the meta object for data type '{@link java.awt.Point <em>Point</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for data type '<em>Point</em>'.
     * @see java.awt.Point
     * @model instanceClass="java.awt.Point"
     * @generated
     */
    EDataType getPoint();

    /**
     * Returns the meta object for data type '{@link org.eclipse.emf.common.notify.Adapter <em>Adapter</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Adapter</em>'.
     * @see org.eclipse.emf.common.notify.Adapter
     * @model instanceClass="org.eclipse.emf.common.notify.Adapter"
     * @generated
     */
    EDataType getAdapter();

    /**
     * Returns the meta object for data type '{@link org.eclipse.core.runtime.IProgressMonitor <em>IProgress Monitor</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>IProgress Monitor</em>'.
     * @see org.eclipse.core.runtime.IProgressMonitor
     * @model instanceClass="org.eclipse.core.runtime.IProgressMonitor"
     * @generated
     */
    EDataType getIProgressMonitor();

    /**
     * Returns the meta object for data type '{@link org.geotools.data.Query <em>Query</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Query</em>'.
     * @see org.geotools.data.Query
     * @model instanceClass="org.geotools.data.Query"
     * @generated
     */
    EDataType getQuery();

    /**
     * Returns the meta object for data type '{@link java.net.URL <em>URL</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for data type '<em>URL</em>'.
     * @see java.net.URL
     * @model instanceClass="java.net.URL"
     * @generated
     */
    EDataType getURL();

    /**
     * Returns the meta object for data type '{@link org.eclipse.jface.resource.ImageDescriptor <em>Image Descriptor</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Image Descriptor</em>'.
     * @see org.eclipse.jface.resource.ImageDescriptor
     * @model instanceClass="org.eclipse.jface.resource.ImageDescriptor"
     * @generated
     */
    EDataType getImageDescriptor();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.project.command.EditCommand <em>Edit Command</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Edit Command</em>'.
     * @see net.refractions.udig.project.command.EditCommand
     * @model instanceClass="net.refractions.udig.project.command.EditCommand"
     * @generated
     */
    EDataType getEditCommand();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.project.command.NavCommand <em>Nav Command</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Nav Command</em>'.
     * @see net.refractions.udig.project.command.NavCommand
     * @model instanceClass="net.refractions.udig.project.command.NavCommand"
     * @generated
     */
    EDataType getNavCommand();

    /**
     * Returns the meta object for data type '{@link com.vividsolutions.jts.geom.Envelope <em>Envelope</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Envelope</em>'.
     * @see com.vividsolutions.jts.geom.Envelope
     * @model instanceClass="com.vividsolutions.jts.geom.Envelope"
     * @generated
     */
    EDataType getEnvelope();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.project.command.EditManagerControlCommand <em>Edit Manager Control Command</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Edit Manager Control Command</em>'.
     * @see net.refractions.udig.project.command.EditManagerControlCommand
     * @model instanceClass="net.refractions.udig.project.command.EditManagerControlCommand"
     * @generated
     */
    EDataType getEditManagerControlCommand();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.project.command.MapCommand <em>Command</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Command</em>'.
     * @see net.refractions.udig.project.command.MapCommand
     * @model instanceClass="net.refractions.udig.project.command.MapCommand"
     * @generated
     */
    EDataType getCommand();

    /**
     * Returns the meta object for data type '{@link org.eclipse.emf.common.util.URI <em>URI</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>URI</em>'.
     * @see org.eclipse.emf.common.util.URI
     * @model instanceClass="org.eclipse.emf.common.util.URI"
     * @generated
     */
    EDataType getURI();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.project.internal.CatalogRef <em>Catalog Ref</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Catalog Ref</em>'.
     * @see net.refractions.udig.project.internal.CatalogRef
     * @model instanceClass="net.refractions.udig.project.internal.CatalogRef"
     * @generated
     */
    EDataType getCatalogRef();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.ui.palette.ColourPalette <em>Colour Palette</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Colour Palette</em>'.
     * @see net.refractions.udig.ui.palette.ColourPalette
     * @model instanceClass="net.refractions.udig.ui.palette.ColourPalette"
     * @generated
     */
    EDataType getColourPalette();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.ui.palette.ColourScheme <em>Colour Scheme</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Colour Scheme</em>'.
     * @see net.refractions.udig.ui.palette.ColourScheme
     * @model instanceClass="net.refractions.udig.ui.palette.ColourScheme"
     * @generated
     */
    EDataType getColourScheme();

    /**
     * Returns the meta object for data type '{@link org.picocontainer.MutablePicoContainer <em>Mutable Pico Container</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Mutable Pico Container</em>'.
     * @see org.picocontainer.MutablePicoContainer
     * @model instanceClass="org.picocontainer.MutablePicoContainer"
     * @generated
     */
    EDataType getMutablePicoContainer();

    /**
     * Returns the meta object for data type '{@link org.geotools.geometry.jts.ReferencedEnvelope <em>Referenced Envelope</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Referenced Envelope</em>'.
     * @see org.geotools.geometry.jts.ReferencedEnvelope
     * @model instanceClass="org.geotools.geometry.jts.ReferencedEnvelope"
     * @generated
     */
    EDataType getReferencedEnvelope();

    /**
     * Returns the meta object for data type '{@link org.geotools.data.FeatureEvent <em>Feature Event</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Feature Event</em>'.
     * @see org.geotools.data.FeatureEvent
     * @model instanceClass="org.geotools.data.FeatureEvent"
     * @generated
     */
    EDataType getFeatureEvent();

    /**
     * Returns the meta object for data type '{@link org.opengis.feature.simple.SimpleFeature <em>Simple Feature</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Simple Feature</em>'.
     * @see org.opengis.feature.simple.SimpleFeature
     * @model instanceClass="org.opengis.feature.simple.SimpleFeature"
     * @generated
     */
    EDataType getSimpleFeature();

    /**
     * Returns the meta object for data type '{@link java.lang.IllegalArgumentException <em>Illegal Argument Exception</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Illegal Argument Exception</em>'.
     * @see java.lang.IllegalArgumentException
     * @model instanceClass="java.lang.IllegalArgumentException"
     * @generated
     */
    EDataType getIllegalArgumentException();

    /**
     * Returns the meta object for data type '{@link java.io.IOException <em>IO Exception</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>IO Exception</em>'.
     * @see java.io.IOException
     * @model instanceClass="java.io.IOException"
     * @generated
     */
    EDataType getIOException();

    /**
     * Returns the meta object for data type '{@link java.awt.Color <em>Color</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Color</em>'.
     * @see java.awt.Color
     * @model instanceClass="java.awt.Color"
     * @generated
     */
    EDataType getColor();

    /**
     * Returns the meta object for data type '{@link org.geotools.brewer.color.BrewerPalette <em>Brewer Palette</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Brewer Palette</em>'.
     * @see org.geotools.brewer.color.BrewerPalette
     * @model instanceClass="org.geotools.brewer.color.BrewerPalette"
     * @generated
     */
    EDataType getBrewerPalette();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ProjectFactory getProjectFactory();

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
         * The meta object literal for the '{@link Comparable <em>Comparable</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see Comparable
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getComparable()
         * @generated
         */
        EClass COMPARABLE = eINSTANCE.getComparable();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.IMap <em>IMap</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.IMap
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIMap()
         * @generated
         */
        EClass IMAP = eINSTANCE.getIMap();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.ILayer <em>ILayer</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.ILayer
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getILayer()
         * @generated
         */
        EClass ILAYER = eINSTANCE.getILayer();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.IEditManager <em>IEdit Manager</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.IEditManager
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIEditManager()
         * @generated
         */
        EClass IEDIT_MANAGER = eINSTANCE.getIEditManager();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.IProject <em>IProject</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.IProject
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIProject()
         * @generated
         */
        EClass IPROJECT = eINSTANCE.getIProject();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.IAbstractContext <em>IAbstract Context</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.IAbstractContext
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIAbstractContext()
         * @generated
         */
        EClass IABSTRACT_CONTEXT = eINSTANCE.getIAbstractContext();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.IBlackboard <em>IBlackboard</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.IBlackboard
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIBlackboard()
         * @generated
         */
        EClass IBLACKBOARD = eINSTANCE.getIBlackboard();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.IProjectElement <em>IProject Element</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.IProjectElement
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIProjectElement()
         * @generated
         */
        EClass IPROJECT_ELEMENT = eINSTANCE.getIProjectElement();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.render.IRenderManager <em>IRender Manager</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.render.IRenderManager
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIRenderManager()
         * @generated
         */
        EClass IRENDER_MANAGER = eINSTANCE.getIRenderManager();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.render.IViewportModel <em>IViewport Model</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.render.IViewportModel
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIViewportModel()
         * @generated
         */
        EClass IVIEWPORT_MODEL = eINSTANCE.getIViewportModel();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.internal.impl.ContextModelImpl <em>Context Model</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.impl.ContextModelImpl
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getContextModel()
         * @generated
         */
        EClass CONTEXT_MODEL = eINSTANCE.getContextModel();

        /**
         * The meta object literal for the '<em><b>Layers</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONTEXT_MODEL__LAYERS = eINSTANCE.getContextModel_Layers();

        /**
         * The meta object literal for the '<em><b>Map</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONTEXT_MODEL__MAP = eINSTANCE.getContextModel_Map();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.internal.impl.EditManagerImpl <em>Edit Manager</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.impl.EditManagerImpl
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getEditManager()
         * @generated
         */
        EClass EDIT_MANAGER = eINSTANCE.getEditManager();

        /**
         * The meta object literal for the '<em><b>Edit Feature</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute EDIT_MANAGER__EDIT_FEATURE = eINSTANCE.getEditManager_EditFeature();

        /**
         * The meta object literal for the '<em><b>Map Internal</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EDIT_MANAGER__MAP_INTERNAL = eINSTANCE.getEditManager_MapInternal();

        /**
         * The meta object literal for the '<em><b>Edit Layer Internal</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EDIT_MANAGER__EDIT_LAYER_INTERNAL = eINSTANCE.getEditManager_EditLayerInternal();

        /**
         * The meta object literal for the '<em><b>Transaction Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute EDIT_MANAGER__TRANSACTION_TYPE = eINSTANCE.getEditManager_TransactionType();

        /**
         * The meta object literal for the '<em><b>Edit Layer Locked</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute EDIT_MANAGER__EDIT_LAYER_LOCKED = eINSTANCE.getEditManager_EditLayerLocked();

        /**
         * The meta object literal for the '<em><b>Selected Layer</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EDIT_MANAGER__SELECTED_LAYER = eINSTANCE.getEditManager_SelectedLayer();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.internal.impl.LayerImpl <em>Layer</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.impl.LayerImpl
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getLayer()
         * @generated
         */
        EClass LAYER = eINSTANCE.getLayer();

        /**
         * The meta object literal for the '<em><b>Context Model</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference LAYER__CONTEXT_MODEL = eINSTANCE.getLayer_ContextModel();

        /**
         * The meta object literal for the '<em><b>Filter</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__FILTER = eINSTANCE.getLayer_Filter();

        /**
         * The meta object literal for the '<em><b>Style Blackboard</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference LAYER__STYLE_BLACKBOARD = eINSTANCE.getLayer_StyleBlackboard();

        /**
         * The meta object literal for the '<em><b>Zorder</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__ZORDER = eINSTANCE.getLayer_Zorder();

        /**
         * The meta object literal for the '<em><b>Status</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__STATUS = eINSTANCE.getLayer_Status();

        /**
         * The meta object literal for the '<em><b>Selectable</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__SELECTABLE = eINSTANCE.getLayer_Selectable();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__NAME = eINSTANCE.getLayer_Name();

        /**
         * The meta object literal for the '<em><b>Catalog Ref</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__CATALOG_REF = eINSTANCE.getLayer_CatalogRef();

        /**
         * The meta object literal for the '<em><b>ID</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__ID = eINSTANCE.getLayer_ID();

        /**
         * The meta object literal for the '<em><b>Visible</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__VISIBLE = eINSTANCE.getLayer_Visible();

        /**
         * The meta object literal for the '<em><b>Geo Resource</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__GEO_RESOURCE = eINSTANCE.getLayer_GeoResource();

        /**
         * The meta object literal for the '<em><b>Geo Resources</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__GEO_RESOURCES = eINSTANCE.getLayer_GeoResources();

        /**
         * The meta object literal for the '<em><b>Glyph</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__GLYPH = eINSTANCE.getLayer_Glyph();

        /**
         * The meta object literal for the '<em><b>CRS</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__CRS = eINSTANCE.getLayer_CRS();

        /**
         * The meta object literal for the '<em><b>Properties</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference LAYER__PROPERTIES = eINSTANCE.getLayer_Properties();

        /**
         * The meta object literal for the '<em><b>Colour Scheme</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__COLOUR_SCHEME = eINSTANCE.getLayer_ColourScheme();

        /**
         * The meta object literal for the '<em><b>Default Color</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__DEFAULT_COLOR = eINSTANCE.getLayer_DefaultColor();

        /**
         * The meta object literal for the '<em><b>Feature Changes</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__FEATURE_CHANGES = eINSTANCE.getLayer_FeatureChanges();

        /**
         * The meta object literal for the '<em><b>Min Scale Denominator</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__MIN_SCALE_DENOMINATOR = eINSTANCE.getLayer_MinScaleDenominator();

        /**
         * The meta object literal for the '<em><b>Max Scale Denominator</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LAYER__MAX_SCALE_DENOMINATOR = eINSTANCE.getLayer_MaxScaleDenominator();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.internal.impl.MapImpl <em>Map</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.impl.MapImpl
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getMap()
         * @generated
         */
        EClass MAP = eINSTANCE.getMap();

        /**
         * The meta object literal for the '<em><b>Context Model</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MAP__CONTEXT_MODEL = eINSTANCE.getMap_ContextModel();

        /**
         * The meta object literal for the '<em><b>Abstract</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MAP__ABSTRACT = eINSTANCE.getMap_Abstract();

        /**
         * The meta object literal for the '<em><b>Nav Command Stack</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MAP__NAV_COMMAND_STACK = eINSTANCE.getMap_NavCommandStack();

        /**
         * The meta object literal for the '<em><b>Command Stack</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MAP__COMMAND_STACK = eINSTANCE.getMap_CommandStack();

        /**
         * The meta object literal for the '<em><b>Layer Factory</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MAP__LAYER_FACTORY = eINSTANCE.getMap_LayerFactory();

        /**
         * The meta object literal for the '<em><b>Viewport Model Internal</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MAP__VIEWPORT_MODEL_INTERNAL = eINSTANCE.getMap_ViewportModelInternal();

        /**
         * The meta object literal for the '<em><b>Color Palette</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MAP__COLOR_PALETTE = eINSTANCE.getMap_ColorPalette();

        /**
         * The meta object literal for the '<em><b>Edit Manager Internal</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MAP__EDIT_MANAGER_INTERNAL = eINSTANCE.getMap_EditManagerInternal();

        /**
         * The meta object literal for the '<em><b>Render Manager Internal</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MAP__RENDER_MANAGER_INTERNAL = eINSTANCE.getMap_RenderManagerInternal();

        /**
         * The meta object literal for the '<em><b>Colour Scheme</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MAP__COLOUR_SCHEME = eINSTANCE.getMap_ColourScheme();

        /**
         * The meta object literal for the '<em><b>Black Board Internal</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MAP__BLACK_BOARD_INTERNAL = eINSTANCE.getMap_BlackBoardInternal();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.internal.impl.ProjectImpl <em>Project</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.impl.ProjectImpl
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getProject()
         * @generated
         */
        EClass PROJECT = eINSTANCE.getProject();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute PROJECT__NAME = eINSTANCE.getProject_Name();

        /**
         * The meta object literal for the '<em><b>Elements Internal</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference PROJECT__ELEMENTS_INTERNAL = eINSTANCE.getProject_ElementsInternal();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.internal.ProjectElement <em>Element</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.ProjectElement
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getProjectElement()
         * @generated
         */
        EClass PROJECT_ELEMENT = eINSTANCE.getProjectElement();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute PROJECT_ELEMENT__NAME = eINSTANCE.getProjectElement_Name();

        /**
         * The meta object literal for the '<em><b>Project Internal</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference PROJECT_ELEMENT__PROJECT_INTERNAL = eINSTANCE
                .getProjectElement_ProjectInternal();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.internal.impl.ProjectRegistryImpl <em>Registry</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.impl.ProjectRegistryImpl
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getProjectRegistry()
         * @generated
         */
        EClass PROJECT_REGISTRY = eINSTANCE.getProjectRegistry();

        /**
         * The meta object literal for the '<em><b>Current Project</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference PROJECT_REGISTRY__CURRENT_PROJECT = eINSTANCE
                .getProjectRegistry_CurrentProject();

        /**
         * The meta object literal for the '<em><b>Projects</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference PROJECT_REGISTRY__PROJECTS = eINSTANCE.getProjectRegistry_Projects();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.internal.impl.StyleBlackboardImpl <em>Style Blackboard</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.impl.StyleBlackboardImpl
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getStyleBlackboard()
         * @generated
         */
        EClass STYLE_BLACKBOARD = eINSTANCE.getStyleBlackboard();

        /**
         * The meta object literal for the '<em><b>Content</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STYLE_BLACKBOARD__CONTENT = eINSTANCE.getStyleBlackboard_Content();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.internal.impl.StyleEntryImpl <em>Style Entry</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.impl.StyleEntryImpl
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getStyleEntry()
         * @generated
         */
        EClass STYLE_ENTRY = eINSTANCE.getStyleEntry();

        /**
         * The meta object literal for the '<em><b>ID</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STYLE_ENTRY__ID = eINSTANCE.getStyleEntry_ID();

        /**
         * The meta object literal for the '<em><b>Memento</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STYLE_ENTRY__MEMENTO = eINSTANCE.getStyleEntry_Memento();

        /**
         * The meta object literal for the '<em><b>Style</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STYLE_ENTRY__STYLE = eINSTANCE.getStyleEntry_Style();

        /**
         * The meta object literal for the '<em><b>Style Class</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STYLE_ENTRY__STYLE_CLASS = eINSTANCE.getStyleEntry_StyleClass();

        /**
         * The meta object literal for the '{@link Cloneable <em>Cloneable</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see Cloneable
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getCloneable()
         * @generated
         */
        EClass CLONEABLE = eINSTANCE.getCloneable();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.internal.impl.LayerFactoryImpl <em>Layer Factory</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.impl.LayerFactoryImpl
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getLayerFactory()
         * @generated
         */
        EClass LAYER_FACTORY = eINSTANCE.getLayerFactory();

        /**
         * The meta object literal for the '<em><b>Map</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference LAYER_FACTORY__MAP = eINSTANCE.getLayerFactory_Map();

        /**
         * The meta object literal for the '{@link org.eclipse.core.runtime.IAdaptable <em>IAdaptable</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.core.runtime.IAdaptable
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIAdaptable()
         * @generated
         */
        EClass IADAPTABLE = eINSTANCE.getIAdaptable();

        /**
         * The meta object literal for the '{@link net.refractions.udig.core.IBlockingAdaptable <em>IBlocking Adaptable</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.core.IBlockingAdaptable
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIBlockingAdaptable()
         * @generated
         */
        EClass IBLOCKING_ADAPTABLE = eINSTANCE.getIBlockingAdaptable();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.internal.impl.BlackboardImpl <em>Blackboard</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.impl.BlackboardImpl
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getBlackboard()
         * @generated
         */
        EClass BLACKBOARD = eINSTANCE.getBlackboard();

        /**
         * The meta object literal for the '<em><b>Entries</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference BLACKBOARD__ENTRIES = eINSTANCE.getBlackboard_Entries();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.internal.impl.BlackboardEntryImpl <em>Blackboard Entry</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.impl.BlackboardEntryImpl
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getBlackboardEntry()
         * @generated
         */
        EClass BLACKBOARD_ENTRY = eINSTANCE.getBlackboardEntry();

        /**
         * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BLACKBOARD_ENTRY__KEY = eINSTANCE.getBlackboardEntry_Key();

        /**
         * The meta object literal for the '<em><b>Memento</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BLACKBOARD_ENTRY__MEMENTO = eINSTANCE.getBlackboardEntry_Memento();

        /**
         * The meta object literal for the '<em><b>Object Class</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BLACKBOARD_ENTRY__OBJECT_CLASS = eINSTANCE.getBlackboardEntry_ObjectClass();

        /**
         * The meta object literal for the '<em><b>Object</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BLACKBOARD_ENTRY__OBJECT = eINSTANCE.getBlackboardEntry_Object();

        /**
         * The meta object literal for the '{@link net.refractions.udig.catalog.IResolveChangeListener <em>IResolve Change Listener</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.catalog.IResolveChangeListener
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIResolveChangeListener()
         * @generated
         */
        EClass IRESOLVE_CHANGE_LISTENER = eINSTANCE.getIResolveChangeListener();

        /**
         * The meta object literal for the '{@link net.refractions.udig.project.IStyleBlackboard <em>IStyle Blackboard</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.IStyleBlackboard
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIStyleBlackboard()
         * @generated
         */
        EClass ISTYLE_BLACKBOARD = eINSTANCE.getIStyleBlackboard();

        /**
         * The meta object literal for the '<em>Coordinate</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.vividsolutions.jts.geom.Coordinate
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getCoordinate()
         * @generated
         */
        EDataType COORDINATE = eINSTANCE.getCoordinate();

        /**
         * The meta object literal for the '<em>Map Display</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.render.displayAdapter.IMapDisplay
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getMapDisplay()
         * @generated
         */
        EDataType MAP_DISPLAY = eINSTANCE.getMapDisplay();

        /**
         * The meta object literal for the '<em>Feature Results</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.geotools.data.FeatureResults
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getFeatureResults()
         * @generated
         */
        EDataType FEATURE_RESULTS = eINSTANCE.getFeatureResults();

        /**
         * The meta object literal for the '<em>List</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.util.List
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getList()
         * @generated
         */
        EDataType LIST = eINSTANCE.getList();

        /**
         * The meta object literal for the '<em>Affine Transform</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.awt.geom.AffineTransform
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getAffineTransform()
         * @generated
         */
        EDataType AFFINE_TRANSFORM = eINSTANCE.getAffineTransform();

        /**
         * The meta object literal for the '<em>Nav Command Stack</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.command.NavCommandStack
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getNavCommandStack()
         * @generated
         */
        EDataType NAV_COMMAND_STACK = eINSTANCE.getNavCommandStack();

        /**
         * The meta object literal for the '<em>IGeo Resource</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.catalog.IGeoResource
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIGeoResource()
         * @generated
         */
        EDataType IGEO_RESOURCE = eINSTANCE.getIGeoResource();

        /**
         * The meta object literal for the '<em>Filter</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.opengis.filter.Filter
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getFilter()
         * @generated
         */
        EDataType FILTER = eINSTANCE.getFilter();

        /**
         * The meta object literal for the '<em>Coordinate Reference System</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.opengis.referencing.crs.CoordinateReferenceSystem
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getCoordinateReferenceSystem()
         * @generated
         */
        EDataType COORDINATE_REFERENCE_SYSTEM = eINSTANCE.getCoordinateReferenceSystem();

        /**
         * The meta object literal for the '<em>Command Stack</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.command.CommandStack
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getCommandStack()
         * @generated
         */
        EDataType COMMAND_STACK = eINSTANCE.getCommandStack();

        /**
         * The meta object literal for the '<em>Feature</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.geotools.feature.SimpleFeature
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getFeature()
         * @generated
         */
        EDataType FEATURE = eINSTANCE.getFeature();

        /**
         * The meta object literal for the '<em>Point</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.awt.Point
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getPoint()
         * @generated
         */
        EDataType POINT = eINSTANCE.getPoint();

        /**
         * The meta object literal for the '<em>Adapter</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.emf.common.notify.Adapter
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getAdapter()
         * @generated
         */
        EDataType ADAPTER = eINSTANCE.getAdapter();

        /**
         * The meta object literal for the '<em>IProgress Monitor</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.core.runtime.IProgressMonitor
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIProgressMonitor()
         * @generated
         */
        EDataType IPROGRESS_MONITOR = eINSTANCE.getIProgressMonitor();

        /**
         * The meta object literal for the '<em>Query</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.geotools.data.Query
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getQuery()
         * @generated
         */
        EDataType QUERY = eINSTANCE.getQuery();

        /**
         * The meta object literal for the '<em>URL</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.net.URL
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getURL()
         * @generated
         */
        EDataType URL = eINSTANCE.getURL();

        /**
         * The meta object literal for the '<em>Image Descriptor</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jface.resource.ImageDescriptor
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getImageDescriptor()
         * @generated
         */
        EDataType IMAGE_DESCRIPTOR = eINSTANCE.getImageDescriptor();

        /**
         * The meta object literal for the '<em>Edit Command</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.command.EditCommand
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getEditCommand()
         * @generated
         */
        EDataType EDIT_COMMAND = eINSTANCE.getEditCommand();

        /**
         * The meta object literal for the '<em>Nav Command</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.command.NavCommand
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getNavCommand()
         * @generated
         */
        EDataType NAV_COMMAND = eINSTANCE.getNavCommand();

        /**
         * The meta object literal for the '<em>Envelope</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.vividsolutions.jts.geom.Envelope
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getEnvelope()
         * @generated
         */
        EDataType ENVELOPE = eINSTANCE.getEnvelope();

        /**
         * The meta object literal for the '<em>Edit Manager Control Command</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.command.EditManagerControlCommand
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getEditManagerControlCommand()
         * @generated
         */
        EDataType EDIT_MANAGER_CONTROL_COMMAND = eINSTANCE.getEditManagerControlCommand();

        /**
         * The meta object literal for the '<em>Command</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.command.MapCommand
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getCommand()
         * @generated
         */
        EDataType COMMAND = eINSTANCE.getCommand();

        /**
         * The meta object literal for the '<em>URI</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.emf.common.util.URI
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getURI()
         * @generated
         */
        EDataType URI = eINSTANCE.getURI();

        /**
         * The meta object literal for the '<em>Catalog Ref</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.project.internal.CatalogRef
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getCatalogRef()
         * @generated
         */
        EDataType CATALOG_REF = eINSTANCE.getCatalogRef();

        /**
         * The meta object literal for the '<em>Colour Palette</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.ui.palette.ColourPalette
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getColourPalette()
         * @generated
         */
        EDataType COLOUR_PALETTE = eINSTANCE.getColourPalette();

        /**
         * The meta object literal for the '<em>Colour Scheme</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.ui.palette.ColourScheme
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getColourScheme()
         * @generated
         */
        EDataType COLOUR_SCHEME = eINSTANCE.getColourScheme();

        /**
         * The meta object literal for the '<em>Mutable Pico Container</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.picocontainer.MutablePicoContainer
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getMutablePicoContainer()
         * @generated
         */
        EDataType MUTABLE_PICO_CONTAINER = eINSTANCE.getMutablePicoContainer();

        /**
         * The meta object literal for the '<em>Referenced Envelope</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.geotools.geometry.jts.ReferencedEnvelope
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getReferencedEnvelope()
         * @generated
         */
        EDataType REFERENCED_ENVELOPE = eINSTANCE.getReferencedEnvelope();

        /**
         * The meta object literal for the '<em>Feature Event</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.geotools.data.FeatureEvent
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getFeatureEvent()
         * @generated
         */
        EDataType FEATURE_EVENT = eINSTANCE.getFeatureEvent();

        /**
         * The meta object literal for the '<em>Simple Feature</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.opengis.feature.simple.SimpleFeature
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getSimpleFeature()
         * @generated
         */
        EDataType SIMPLE_FEATURE = eINSTANCE.getSimpleFeature();

        /**
         * The meta object literal for the '<em>Illegal Argument Exception</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.IllegalArgumentException
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIllegalArgumentException()
         * @generated
         */
        EDataType ILLEGAL_ARGUMENT_EXCEPTION = eINSTANCE.getIllegalArgumentException();

        /**
         * The meta object literal for the '<em>IO Exception</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.io.IOException
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getIOException()
         * @generated
         */
        EDataType IO_EXCEPTION = eINSTANCE.getIOException();

        /**
         * The meta object literal for the '<em>Color</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.awt.Color
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getColor()
         * @generated
         */
        EDataType COLOR = eINSTANCE.getColor();

        /**
         * The meta object literal for the '<em>Brewer Palette</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.geotools.brewer.color.BrewerPalette
         * @see net.refractions.udig.project.internal.impl.ProjectPackageImpl#getBrewerPalette()
         * @generated
         */
        EDataType BREWER_PALETTE = eINSTANCE.getBrewerPalette();

    }

} // ProjectPackage
