/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.printing.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

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
 * 
 * This was generated using EMF
 * The intended use of this interface to access the properties of the classes
 * generted using EMF.
 * 
 * <!-- end-user-doc -->
 * @see net.refractions.udig.printing.model.ModelFactory
 * @model kind="package"
 * @generated
 */
public interface ModelPackage extends EPackage{
	/**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "model"; //$NON-NLS-1$

	/**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http:///net/refractions/udig/printing/model.ecore"; //$NON-NLS-1$

	/**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "net.refractions.udig.printing.model"; //$NON-NLS-1$

	/**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ModelPackage eINSTANCE = net.refractions.udig.printing.model.impl.ModelPackageImpl.init();

	/**
     * The meta object id for the '{@link net.refractions.udig.printing.model.impl.ElementImpl <em>Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.printing.model.impl.ElementImpl
     * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getElement()
     * @generated
     */
    int ELEMENT = 2;

	/**
     * The feature id for the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELEMENT__LOCATION = 0;

	/**
     * The feature id for the '<em><b>Size</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELEMENT__SIZE = 1;

	/**
     * The feature id for the '<em><b>Paper Size</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELEMENT__PAPER_SIZE = 2;

    /**
     * The number of structural features of the '<em>Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ELEMENT_FEATURE_COUNT = 3;

	/**
     * The meta object id for the '{@link net.refractions.udig.printing.model.impl.BoxImpl <em>Box</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.printing.model.impl.BoxImpl
     * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getBox()
     * @generated
     */
    int BOX = 0;

	/**
     * The feature id for the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BOX__LOCATION = ELEMENT__LOCATION;

	/**
     * The feature id for the '<em><b>Size</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BOX__SIZE = ELEMENT__SIZE;

	/**
     * The feature id for the '<em><b>Paper Size</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BOX__PAPER_SIZE = ELEMENT__PAPER_SIZE;

    /**
     * The feature id for the '<em><b>Source Connections</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BOX__SOURCE_CONNECTIONS = ELEMENT_FEATURE_COUNT + 0;

	/**
     * The feature id for the '<em><b>Target Connections</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BOX__TARGET_CONNECTIONS = ELEMENT_FEATURE_COUNT + 1;

	/**
     * The feature id for the '<em><b>Box Printer</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BOX__BOX_PRINTER = ELEMENT_FEATURE_COUNT + 2;

	/**
     * The feature id for the '<em><b>ID</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BOX__ID = ELEMENT_FEATURE_COUNT + 3;

	/**
     * The number of structural features of the '<em>Box</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BOX_FEATURE_COUNT = ELEMENT_FEATURE_COUNT + 4;

	/**
     * The meta object id for the '{@link net.refractions.udig.printing.model.impl.ConnectionImpl <em>Connection</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.printing.model.impl.ConnectionImpl
     * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getConnection()
     * @generated
     */
    int CONNECTION = 1;

	/**
     * The feature id for the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION__LOCATION = ELEMENT__LOCATION;

	/**
     * The feature id for the '<em><b>Size</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION__SIZE = ELEMENT__SIZE;

	/**
     * The feature id for the '<em><b>Paper Size</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION__PAPER_SIZE = ELEMENT__PAPER_SIZE;

    /**
     * The feature id for the '<em><b>Connected</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION__CONNECTED = ELEMENT_FEATURE_COUNT + 0;

	/**
     * The feature id for the '<em><b>Source</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION__SOURCE = ELEMENT_FEATURE_COUNT + 1;

	/**
     * The feature id for the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION__TARGET = ELEMENT_FEATURE_COUNT + 2;

	/**
     * The number of structural features of the '<em>Connection</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_FEATURE_COUNT = ELEMENT_FEATURE_COUNT + 3;

	/**
     * The meta object id for the '{@link net.refractions.udig.printing.model.impl.PageImpl <em>Page</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.printing.model.impl.PageImpl
     * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getPage()
     * @generated
     */
    int PAGE = 3;

	/**
     * The feature id for the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PAGE__LOCATION = ELEMENT__LOCATION;

	/**
     * The feature id for the '<em><b>Size</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PAGE__SIZE = ELEMENT__SIZE;

	/**
     * The feature id for the '<em><b>Paper Size</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PAGE__PAPER_SIZE = ELEMENT__PAPER_SIZE;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PAGE__NAME = ELEMENT_FEATURE_COUNT + 0;

	/**
     * The feature id for the '<em><b>Project Internal</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PAGE__PROJECT_INTERNAL = ELEMENT_FEATURE_COUNT + 1;

	/**
     * The feature id for the '<em><b>Boxes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PAGE__BOXES = ELEMENT_FEATURE_COUNT + 2;

	/**
     * The number of structural features of the '<em>Page</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PAGE_FEATURE_COUNT = ELEMENT_FEATURE_COUNT + 3;

	/**
     * The meta object id for the '<em>Graphics2 D</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.awt.Graphics2D
     * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getGraphics2D()
     * @generated
     */
    int GRAPHICS2_D = 4;

	/**
     * The meta object id for the '<em>Dimension</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.draw2d.geometry.Dimension
     * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getDimension()
     * @generated
     */
    int DIMENSION = 5;

	/**
     * The meta object id for the '<em>Point</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.draw2d.geometry.Point
     * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getPoint()
     * @generated
     */
    int POINT = 6;


	/**
     * The meta object id for the '<em>IProgress Monitor</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.core.runtime.IProgressMonitor
     * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getIProgressMonitor()
     * @generated
     */
    int IPROGRESS_MONITOR = 7;


	/**
     * The meta object id for the '<em>Box Printer</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.refractions.udig.printing.model.BoxPrinter
     * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getBoxPrinter()
     * @generated
     */
    int BOX_PRINTER = 8;


	/**
     * Returns the meta object for class '{@link net.refractions.udig.printing.model.Box <em>Box</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Box</em>'.
     * @see net.refractions.udig.printing.model.Box
     * @generated
     */
    EClass getBox();

	/**
     * Returns the meta object for the reference list '{@link net.refractions.udig.printing.model.Box#getSourceConnections <em>Source Connections</em>}'.
     * <!-- begin-user-doc -->
	 * 
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Source Connections</em>'.
     * @see net.refractions.udig.printing.model.Box#getSourceConnections()
     * @see #getBox()
     * @generated
     */
    EReference getBox_SourceConnections();

	/**
     * Returns the meta object for the reference list '{@link net.refractions.udig.printing.model.Box#getTargetConnections <em>Target Connections</em>}'.
     * <!-- begin-user-doc -->
	 * 
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Target Connections</em>'.
     * @see net.refractions.udig.printing.model.Box#getTargetConnections()
     * @see #getBox()
     * @generated
     */
    EReference getBox_TargetConnections();

	/**
     * Returns the meta object for the attribute '{@link net.refractions.udig.printing.model.Box#getBoxPrinter <em>Box Printer</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Box Printer</em>'.
     * @see net.refractions.udig.printing.model.Box#getBoxPrinter()
     * @see #getBox()
     * @generated
     */
    EAttribute getBox_BoxPrinter();

	/**
     * Returns the meta object for the attribute '{@link net.refractions.udig.printing.model.Box#getID <em>ID</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>ID</em>'.
     * @see net.refractions.udig.printing.model.Box#getID()
     * @see #getBox()
     * @generated
     */
    EAttribute getBox_ID();

	/**
     * Returns the meta object for class '{@link net.refractions.udig.printing.model.Connection <em>Connection</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Connection</em>'.
     * @see net.refractions.udig.printing.model.Connection
     * @generated
     */
    EClass getConnection();

	/**
     * Returns the meta object for the attribute '{@link net.refractions.udig.printing.model.Connection#isConnected <em>Connected</em>}'.
     * <!-- begin-user-doc -->
	 * 
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Connected</em>'.
     * @see net.refractions.udig.printing.model.Connection#isConnected()
     * @see #getConnection()
     * @generated
     */
    EAttribute getConnection_Connected();

	/**
     * Returns the meta object for the reference '{@link net.refractions.udig.printing.model.Connection#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
	 * 
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Source</em>'.
     * @see net.refractions.udig.printing.model.Connection#getSource()
     * @see #getConnection()
     * @generated
     */
    EReference getConnection_Source();

	/**
     * Returns the meta object for the reference '{@link net.refractions.udig.printing.model.Connection#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
	 * 
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Target</em>'.
     * @see net.refractions.udig.printing.model.Connection#getTarget()
     * @see #getConnection()
     * @generated
     */
    EReference getConnection_Target();

	/**
     * Returns the meta object for class '{@link net.refractions.udig.printing.model.Element <em>Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Element</em>'.
     * @see net.refractions.udig.printing.model.Element
     * @generated
     */
    EClass getElement();

	/**
     * Returns the meta object for the attribute '{@link net.refractions.udig.printing.model.Element#getLocation <em>Location</em>}'.
     * <!-- begin-user-doc -->
	 * 
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Location</em>'.
     * @see net.refractions.udig.printing.model.Element#getLocation()
     * @see #getElement()
     * @generated
     */
    EAttribute getElement_Location();

	/**
     * Returns the meta object for the attribute '{@link net.refractions.udig.printing.model.Element#getSize <em>Size</em>}'.
     * <!-- begin-user-doc -->
	 * 
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Size</em>'.
     * @see net.refractions.udig.printing.model.Element#getSize()
     * @see #getElement()
     * @generated
     */
    EAttribute getElement_Size();

	/**
     * Returns the meta object for the attribute '{@link net.refractions.udig.printing.model.Element#getPaperSize <em>Paper Size</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Paper Size</em>'.
     * @see net.refractions.udig.printing.model.Element#getPaperSize()
     * @see #getElement()
     * @generated
     */
    EAttribute getElement_PaperSize();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.printing.model.Page <em>Page</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Page</em>'.
     * @see net.refractions.udig.printing.model.Page
     * @generated
     */
    EClass getPage();

	/**
     * Returns the meta object for the containment reference list '{@link net.refractions.udig.printing.model.Page#getBoxes <em>Boxes</em>}'.
     * <!-- begin-user-doc -->
	 * 
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Boxes</em>'.
     * @see net.refractions.udig.printing.model.Page#getBoxes()
     * @see #getPage()
     * @generated
     */
    EReference getPage_Boxes();

	/**
     * Returns the meta object for data type '{@link java.awt.Graphics2D <em>Graphics2 D</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Graphics2 D</em>'.
     * @see java.awt.Graphics2D
     * @model instanceClass="java.awt.Graphics2D"
     * @generated
     */
    EDataType getGraphics2D();

	/**
     * Returns the meta object for data type '{@link org.eclipse.draw2d.geometry.Dimension <em>Dimension</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Dimension</em>'.
     * @see org.eclipse.draw2d.geometry.Dimension
     * @model instanceClass="org.eclipse.draw2d.geometry.Dimension"
     * @generated
     */
    EDataType getDimension();

	/**
     * Returns the meta object for data type '{@link org.eclipse.draw2d.geometry.Point <em>Point</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Point</em>'.
     * @see org.eclipse.draw2d.geometry.Point
     * @model instanceClass="org.eclipse.draw2d.geometry.Point"
     * @generated
     */
    EDataType getPoint();

	/**
     * Returns the meta object for data type '{@link org.eclipse.core.runtime.IProgressMonitor <em>IProgress Monitor</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>IProgress Monitor</em>'.
     * @see org.eclipse.core.runtime.IProgressMonitor
     * @model instanceClass="org.eclipse.core.runtime.IProgressMonitor"
     * @generated
     */
    EDataType getIProgressMonitor();

	/**
     * Returns the meta object for data type '{@link net.refractions.udig.printing.model.BoxPrinter <em>Box Printer</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Box Printer</em>'.
     * @see net.refractions.udig.printing.model.BoxPrinter
     * @model instanceClass="net.refractions.udig.printing.model.BoxPrinter"
     * @generated
     */
    EDataType getBoxPrinter();

	/**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
	 * 
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ModelFactory getModelFactory();

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
         * The meta object literal for the '{@link net.refractions.udig.printing.model.impl.BoxImpl <em>Box</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.printing.model.impl.BoxImpl
         * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getBox()
         * @generated
         */
        EClass BOX = eINSTANCE.getBox();

        /**
         * The meta object literal for the '<em><b>Source Connections</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference BOX__SOURCE_CONNECTIONS = eINSTANCE.getBox_SourceConnections();

        /**
         * The meta object literal for the '<em><b>Target Connections</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference BOX__TARGET_CONNECTIONS = eINSTANCE.getBox_TargetConnections();

        /**
         * The meta object literal for the '<em><b>Box Printer</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BOX__BOX_PRINTER = eINSTANCE.getBox_BoxPrinter();

        /**
         * The meta object literal for the '<em><b>ID</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BOX__ID = eINSTANCE.getBox_ID();

        /**
         * The meta object literal for the '{@link net.refractions.udig.printing.model.impl.ConnectionImpl <em>Connection</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.printing.model.impl.ConnectionImpl
         * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getConnection()
         * @generated
         */
        EClass CONNECTION = eINSTANCE.getConnection();

        /**
         * The meta object literal for the '<em><b>Connected</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONNECTION__CONNECTED = eINSTANCE.getConnection_Connected();

        /**
         * The meta object literal for the '<em><b>Source</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONNECTION__SOURCE = eINSTANCE.getConnection_Source();

        /**
         * The meta object literal for the '<em><b>Target</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONNECTION__TARGET = eINSTANCE.getConnection_Target();

        /**
         * The meta object literal for the '{@link net.refractions.udig.printing.model.impl.ElementImpl <em>Element</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.printing.model.impl.ElementImpl
         * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getElement()
         * @generated
         */
        EClass ELEMENT = eINSTANCE.getElement();

        /**
         * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ELEMENT__LOCATION = eINSTANCE.getElement_Location();

        /**
         * The meta object literal for the '<em><b>Size</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ELEMENT__SIZE = eINSTANCE.getElement_Size();

        /**
         * The meta object literal for the '<em><b>Paper Size</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ELEMENT__PAPER_SIZE = eINSTANCE.getElement_PaperSize();

        /**
         * The meta object literal for the '{@link net.refractions.udig.printing.model.impl.PageImpl <em>Page</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.printing.model.impl.PageImpl
         * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getPage()
         * @generated
         */
        EClass PAGE = eINSTANCE.getPage();

        /**
         * The meta object literal for the '<em><b>Boxes</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference PAGE__BOXES = eINSTANCE.getPage_Boxes();

        /**
         * The meta object literal for the '<em>Graphics2 D</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.awt.Graphics2D
         * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getGraphics2D()
         * @generated
         */
        EDataType GRAPHICS2_D = eINSTANCE.getGraphics2D();

        /**
         * The meta object literal for the '<em>Dimension</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.draw2d.geometry.Dimension
         * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getDimension()
         * @generated
         */
        EDataType DIMENSION = eINSTANCE.getDimension();

        /**
         * The meta object literal for the '<em>Point</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.draw2d.geometry.Point
         * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getPoint()
         * @generated
         */
        EDataType POINT = eINSTANCE.getPoint();

        /**
         * The meta object literal for the '<em>IProgress Monitor</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.core.runtime.IProgressMonitor
         * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getIProgressMonitor()
         * @generated
         */
        EDataType IPROGRESS_MONITOR = eINSTANCE.getIProgressMonitor();

        /**
         * The meta object literal for the '<em>Box Printer</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.refractions.udig.printing.model.BoxPrinter
         * @see net.refractions.udig.printing.model.impl.ModelPackageImpl#getBoxPrinter()
         * @generated
         */
        EDataType BOX_PRINTER = eINSTANCE.getBoxPrinter();

    }

} //ModelPackage
