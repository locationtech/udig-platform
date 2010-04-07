/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.printing.model.impl;

import java.awt.Graphics2D;
import net.refractions.udig.printing.model.*;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.BoxPrinter;
import net.refractions.udig.printing.model.Connection;
import net.refractions.udig.printing.model.Element;
import net.refractions.udig.printing.model.ModelFactory;
import net.refractions.udig.printing.model.ModelPackage;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.model.PrintingModelPlugin;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.ui.XMLMemento;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * @generated
 */
public class ModelFactoryImpl extends EFactoryImpl implements ModelFactory {
	/**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ModelFactory init() {
        try {
            ModelFactory theModelFactory = (ModelFactory)EPackage.Registry.INSTANCE.getEFactory("http:///net/refractions/udig/printing/model.ecore"); 
            if (theModelFactory != null) {
                return theModelFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new ModelFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ModelFactoryImpl() {
        super();
    }

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case ModelPackage.BOX: return createBox();
            case ModelPackage.CONNECTION: return createConnection();
            case ModelPackage.ELEMENT: return createElement();
            case ModelPackage.PAGE: return createPage();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
            case ModelPackage.GRAPHICS2_D:
                return createGraphics2DFromString(eDataType, initialValue);
            case ModelPackage.DIMENSION:
                return createDimensionFromString(eDataType, initialValue);
            case ModelPackage.POINT:
                return createPointFromString(eDataType, initialValue);
            case ModelPackage.IPROGRESS_MONITOR:
                return createIProgressMonitorFromString(eDataType, initialValue);
            case ModelPackage.BOX_PRINTER:
                return createBoxPrinterFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
            case ModelPackage.GRAPHICS2_D:
                return convertGraphics2DToString(eDataType, instanceValue);
            case ModelPackage.DIMENSION:
                return convertDimensionToString(eDataType, instanceValue);
            case ModelPackage.POINT:
                return convertPointToString(eDataType, instanceValue);
            case ModelPackage.IPROGRESS_MONITOR:
                return convertIProgressMonitorToString(eDataType, instanceValue);
            case ModelPackage.BOX_PRINTER:
                return convertBoxPrinterToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

	/**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Box createBox() {
        BoxImpl box = new BoxImpl();
        return box;
    }

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Connection createConnection() {
        ConnectionImpl connection = new ConnectionImpl();
        return connection;
    }

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Element createElement() {
        ElementImpl element = new ElementImpl();
        return element;
    }

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Page createPage() {
        PageImpl page = new PageImpl();
        return page;
    }

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Graphics2D createGraphics2DFromString(EDataType eDataType, String initialValue) {
        return (Graphics2D)super.createFromString(eDataType, initialValue);
    }

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertGraphics2DToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Dimension createDimensionFromString( EDataType eDataType, String initialValue ) {
        String[] parts = initialValue.split(","); //$NON-NLS-1$
        Dimension dim;
        try {
            dim = new Dimension(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (Exception e) {
            PrintingModelPlugin.log("", e); //$NON-NLS-1$
            dim = new Dimension(0, 0);
        }
        return dim;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public String convertDimensionToString( EDataType eDataType, Object instanceValue ) {
        Dimension dim = (Dimension) instanceValue;
        return dim.width + "," + dim.height; //$NON-NLS-1$
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Point createPointFromString( EDataType eDataType, String initialValue ) {
        String[] parts = initialValue.split(","); //$NON-NLS-1$
        Point point;
        try {
            point = new Point(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
        } catch (Exception e) {
            PrintingModelPlugin.log("", e); //$NON-NLS-1$
            point = new Point(0, 0);
        }
        return point;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public String convertPointToString( EDataType eDataType, Object instanceValue ) {
        Point point = (Point) instanceValue;
        return point.x + "," + point.y; //$NON-NLS-1$
    }

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public IProgressMonitor createIProgressMonitorFromString(EDataType eDataType, String initialValue) {
        return (IProgressMonitor)super.createFromString(eDataType, initialValue);
    }

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertIProgressMonitorToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public BoxPrinter createBoxPrinterFromString(EDataType eDataType, String initialValue) {
        String [] split=initialValue.split("_\\|\\|\\|_"); //$NON-NLS-1$
        String extensionID=split[0].trim();
        String className=split[1].trim();
        String mementoString=split[2].trim();
        
        if( mementoString.length()==0 ){
            return null;
        }
        
        try{
            XMLMemento memento = XMLMemento.createReadRoot(new StringReader(mementoString));
            IExtension extension = Platform.getExtensionRegistry().getExtension(PrintingModelPlugin.BOX_PRINTER_EXTENSION_ID, extensionID);
            if( extension==null ){
                PrintingModelPlugin.log("The method "+className+"#getExtensionID() returned: "+extensionID+ //$NON-NLS-1$ //$NON-NLS-2$
                        " this is not the correct extension id.  Check the extension id.  \nHint: Read the " + //$NON-NLS-1$
                        "javadocs for the method", null); //$NON-NLS-1$
                return null;
            }
            IConfigurationElement[] elements = extension.getConfigurationElements();
            IConfigurationElement current=null;
            for( int i = 0; current==null && i < elements.length; i++ ) {
                current=elements[i];
                if( current.getName().equals("editActionGroup") ){ //$NON-NLS-1$
                    current=null;
                    continue;
                }
                String attribute = current.getAttribute("class"); //$NON-NLS-1$
                if( attribute==null ){
                    PrintingModelPlugin.log("The attribute of the extension: "+current.getNamespaceIdentifier()+"."+current.getName(), null );  //$NON-NLS-1$//$NON-NLS-2$
                    current = null;
                    continue;
                }
                if( !attribute.equals(className) )
                    current=null;
            }
            if( current==null )
                return null;
            BoxPrinter printer=(BoxPrinter) current.createExecutableExtension("class"); //$NON-NLS-1$
            printer.load(memento);
            return printer;
        }catch (Exception e) {
        	PrintingModelPlugin.log("Error while creating a box printer from string.", e); //$NON-NLS-1$
            return null;
        }
    }

    private final String SPLIT="_|||_"; //$NON-NLS-1$
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public String convertBoxPrinterToString(EDataType eDataType, Object instanceValue) {
        BoxPrinter printer=(BoxPrinter) instanceValue;
        XMLMemento memento = XMLMemento.createWriteRoot("boxPrinter"); //$NON-NLS-1$
        printer.save(memento);

        StringWriter writer = new StringWriter();
        writer.getBuffer().append(printer.getExtensionPointID());
        writer.getBuffer().append(SPLIT);
        writer.getBuffer().append(printer.getClass().getName());
        writer.getBuffer().append(SPLIT);
        try {
            memento.save(writer);
        } catch (IOException e) {
            return "<boxPrinter></boxPrinter>"; //$NON-NLS-1$
        }

        return writer.getBuffer().toString();
    }

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ModelPackage getModelPackage() {
        return (ModelPackage)getEPackage();
    }

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static ModelPackage getPackage() {
        return ModelPackage.eINSTANCE;
    }

} // ModelFactoryImpl
