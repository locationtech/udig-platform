/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.printing.model.impl;

import java.text.MessageFormat;

import net.refractions.udig.printing.model.Element;
import net.refractions.udig.printing.model.ModelPackage;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.model.internal.Messages;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.refractions.udig.printing.model.impl.ElementImpl#getLocation <em>Location</em>}</li>
 *   <li>{@link net.refractions.udig.printing.model.impl.ElementImpl#getSize <em>Size</em>}</li>
 *   <li>{@link net.refractions.udig.printing.model.impl.ElementImpl#getPaperSize <em>Paper Size</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ElementImpl extends EObjectImpl implements Element {
	/**
     * The default value of the '{@link #getLocation() <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLocation()
     * @generated
     * @ordered
     */
    protected static final Point LOCATION_EDEFAULT = null;

	/**
     * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLocation()
     * @generated
     * @ordered
     */
    protected Point location = LOCATION_EDEFAULT;

	/**
     * The default value of the '{@link #getSize() <em>Size</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSize()
     * @generated
     * @ordered
     */
    protected static final Dimension SIZE_EDEFAULT = null;

	/**
     * The cached value of the '{@link #getSize() <em>Size</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSize()
     * @generated
     * @ordered
     */
    protected Dimension size = SIZE_EDEFAULT;

	/**
     * The default value of the '{@link #getPaperSize() <em>Paper Size</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPaperSize()
     * @generated
     * @ordered
     */
    protected static final Dimension PAPER_SIZE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPaperSize() <em>Paper Size</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPaperSize()
     * @generated
     * @ordered
     */
    protected Dimension paperSize = PAPER_SIZE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ElementImpl() {
        super();
    }

	/**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.ELEMENT;
    }

	/**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Point getLocation() {
        return location;
    }

	/**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLocation(Point newLocation) {
        Point oldLocation = location;
        location = newLocation;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ELEMENT__LOCATION, oldLocation, location));
    }

	/**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Dimension getSize() {
        return size;
    }

	/**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSize(Dimension newSize) {
        Dimension oldSize = size;
        size = newSize;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ELEMENT__SIZE, oldSize, size));
    }

	/**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Dimension getPaperSize() {
        return paperSize;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPaperSize(Dimension newPaperSize) {
        Dimension oldPaperSize = paperSize;
        paperSize = newPaperSize;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ELEMENT__PAPER_SIZE, oldPaperSize, paperSize));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelPackage.ELEMENT__LOCATION:
                return getLocation();
            case ModelPackage.ELEMENT__SIZE:
                return getSize();
            case ModelPackage.ELEMENT__PAPER_SIZE:
                return getPaperSize();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ModelPackage.ELEMENT__LOCATION:
                setLocation((Point)newValue);
                return;
            case ModelPackage.ELEMENT__SIZE:
                setSize((Dimension)newValue);
                return;
            case ModelPackage.ELEMENT__PAPER_SIZE:
                setPaperSize((Dimension)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case ModelPackage.ELEMENT__LOCATION:
                setLocation(LOCATION_EDEFAULT);
                return;
            case ModelPackage.ELEMENT__SIZE:
                setSize(SIZE_EDEFAULT);
                return;
            case ModelPackage.ELEMENT__PAPER_SIZE:
                setPaperSize(PAPER_SIZE_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case ModelPackage.ELEMENT__LOCATION:
                return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
            case ModelPackage.ELEMENT__SIZE:
                return SIZE_EDEFAULT == null ? size != null : !SIZE_EDEFAULT.equals(size);
            case ModelPackage.ELEMENT__PAPER_SIZE:
                return PAPER_SIZE_EDEFAULT == null ? paperSize != null : !PAPER_SIZE_EDEFAULT.equals(paperSize);
        }
        return super.eIsSet(featureID);
    }

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated NOT
	 */
    public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
        result.append(MessageFormat.format(Messages.ElementImpl_0+Messages.ElementImpl_toString, new Object[] { location, size } ));  
		return result.toString();
	}

    public Page getPage() {
        return (Page) eContainer;
    }

} //ElementImpl
