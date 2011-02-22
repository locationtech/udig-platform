/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.printing.model.impl;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.Connection;
import net.refractions.udig.printing.model.ModelPackage;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Connection</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.refractions.udig.printing.model.impl.ConnectionImpl#isConnected <em>Connected</em>}</li>
 *   <li>{@link net.refractions.udig.printing.model.impl.ConnectionImpl#getSource <em>Source</em>}</li>
 *   <li>{@link net.refractions.udig.printing.model.impl.ConnectionImpl#getTarget <em>Target</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConnectionImpl extends ElementImpl implements Connection {
	/**
	 * The default value of the '{@link #isConnected() <em>Connected</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #isConnected()
	 * @generated
	 * @ordered
	 */
    protected static final boolean CONNECTED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isConnected() <em>Connected</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #isConnected()
	 * @generated
	 * @ordered
	 */
    protected boolean connected = CONNECTED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSource() <em>Source</em>}' reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getSource()
	 * @generated
	 * @ordered
	 */
    protected Box source = null;

	/**
	 * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getTarget()
	 * @generated
	 * @ordered
	 */
    protected Box target = null;

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected ConnectionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected EClass eStaticClass() {
		return ModelPackage.eINSTANCE.getConnection();
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public boolean isConnected() {
		return connected;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setConnected(boolean newConnected) {
		boolean oldConnected = connected;
		connected = newConnected;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.CONNECTION__CONNECTED, oldConnected, connected));
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public Box getSource() {
		if (source != null && source.eIsProxy()) {
			Box oldSource = source;
			source = (Box)eResolveProxy((InternalEObject)source);
			if (source != oldSource) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.CONNECTION__SOURCE, oldSource, source));
			}
		}
		return source;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public Box basicGetSource() {
		return source;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setSource(Box newSource) {
		Box oldSource = source;
		source = newSource;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.CONNECTION__SOURCE, oldSource, source));
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public Box getTarget() {
		if (target != null && target.eIsProxy()) {
			Box oldTarget = target;
			target = (Box)eResolveProxy((InternalEObject)target);
			if (target != oldTarget) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.CONNECTION__TARGET, oldTarget, target));
			}
		}
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public Box basicGetTarget() {
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setTarget(Box newTarget) {
		Box oldTarget = target;
		target = newTarget;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.CONNECTION__TARGET, oldTarget, target));
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModelPackage.CONNECTION__LOCATION:
				return getLocation();
			case ModelPackage.CONNECTION__SIZE:
				return getSize();
			case ModelPackage.CONNECTION__CONNECTED:
				return isConnected() ? Boolean.TRUE : Boolean.FALSE;
			case ModelPackage.CONNECTION__SOURCE:
				if (resolve) return getSource();
				return basicGetSource();
			case ModelPackage.CONNECTION__TARGET:
				if (resolve) return getTarget();
				return basicGetTarget();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModelPackage.CONNECTION__LOCATION:
				setLocation((Point)newValue);
				return;
			case ModelPackage.CONNECTION__SIZE:
				setSize((Dimension)newValue);
				return;
			case ModelPackage.CONNECTION__CONNECTED:
				setConnected(((Boolean)newValue).booleanValue());
				return;
			case ModelPackage.CONNECTION__SOURCE:
				setSource((Box)newValue);
				return;
			case ModelPackage.CONNECTION__TARGET:
				setTarget((Box)newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModelPackage.CONNECTION__LOCATION:
				setLocation(LOCATION_EDEFAULT);
				return;
			case ModelPackage.CONNECTION__SIZE:
				setSize(SIZE_EDEFAULT);
				return;
			case ModelPackage.CONNECTION__CONNECTED:
				setConnected(CONNECTED_EDEFAULT);
				return;
			case ModelPackage.CONNECTION__SOURCE:
				setSource((Box)null);
				return;
			case ModelPackage.CONNECTION__TARGET:
				setTarget((Box)null);
				return;
		}
		eDynamicUnset(eFeature);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModelPackage.CONNECTION__LOCATION:
				return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
			case ModelPackage.CONNECTION__SIZE:
				return SIZE_EDEFAULT == null ? size != null : !SIZE_EDEFAULT.equals(size);
			case ModelPackage.CONNECTION__CONNECTED:
				return connected != CONNECTED_EDEFAULT;
			case ModelPackage.CONNECTION__SOURCE:
				return source != null;
			case ModelPackage.CONNECTION__TARGET:
				return target != null;
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (connected: "); //$NON-NLS-1$
		result.append(connected);
		result.append(')');
		return result.toString();
	}

	public void disconnect() {
		if (isConnected()) {
			source.remove(this);
			target.remove(this);
			setConnected(false);
		}
	}
	public void reconnect() {
		if (!isConnected()) {
			source.add(this);
			target.add(this);
			setConnected(true);
		}
	}
	public void reconnect(Box src, Box trg) {
		if (src == null) {
			throw new IllegalArgumentException("Source cannot be null"); //$NON-NLS-1$
		}

		if (trg == null) {
			throw new IllegalArgumentException("Target cannot be null"); //$NON-NLS-1$
		}

		if (trg == src) {
			throw new IllegalArgumentException("Target and source cannot be the same"); //$NON-NLS-1$
		}

		disconnect();
		setSource(src);
		setTarget(trg);
		reconnect();
	}
} //ConnectionImpl
