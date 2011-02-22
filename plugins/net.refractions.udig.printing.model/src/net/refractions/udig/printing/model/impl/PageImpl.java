/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.printing.model.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.ModelPackage;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.project.IProject;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectElement;
import net.refractions.udig.project.internal.ProjectPackage;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Page</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.refractions.udig.printing.model.impl.PageImpl#getName <em>Name</em>}</li>
 *   <li>{@link net.refractions.udig.printing.model.impl.PageImpl#getProjectInternal <em>Project Internal</em>}</li>
 *   <li>{@link net.refractions.udig.printing.model.impl.PageImpl#getBoxes <em>Boxes</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PageImpl extends ElementImpl implements Page {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
    protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
    protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getProjectInternal() <em>Project Internal</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProjectInternal()
	 * @generated
	 * @ordered
	 */
	protected Project projectInternal = null;

	/**
	 * The cached value of the '{@link #getBoxes() <em>Boxes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getBoxes()
	 * @generated
	 * @ordered
	 */
    protected EList boxes = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public PageImpl() {
        super();
    }

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected EClass eStaticClass() {
		return ModelPackage.eINSTANCE.getPage();
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public Project getProjectInternal() {
		if (projectInternal != null && projectInternal.eIsProxy()) {
			Project oldProjectInternal = projectInternal;
			projectInternal = (Project)eResolveProxy((InternalEObject)projectInternal);
			if (projectInternal != oldProjectInternal) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.PAGE__PROJECT_INTERNAL, oldProjectInternal, projectInternal));
			}
		}
		return projectInternal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Project basicGetProjectInternal() {
		return projectInternal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProjectInternal(Project newProjectInternal, NotificationChain msgs) {
		Project oldProjectInternal = projectInternal;
		projectInternal = newProjectInternal;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ModelPackage.PAGE__PROJECT_INTERNAL, oldProjectInternal, newProjectInternal);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setProjectInternal(Project newProjectInternal) {
		if (newProjectInternal != projectInternal) {
			NotificationChain msgs = null;
			if (projectInternal != null)
				msgs = ((InternalEObject)projectInternal).eInverseRemove(this, ProjectPackage.PROJECT__ELEMENTS_INTERNAL, Project.class, msgs);
			if (newProjectInternal != null)
				msgs = ((InternalEObject)newProjectInternal).eInverseAdd(this, ProjectPackage.PROJECT__ELEMENTS_INTERNAL, Project.class, msgs);
			msgs = basicSetProjectInternal(newProjectInternal, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PAGE__PROJECT_INTERNAL, newProjectInternal, newProjectInternal));
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public List getBoxes() {
		if (boxes == null) {
			boxes = new EObjectContainmentEList(Box.class, this, ModelPackage.PAGE__BOXES);
		}
		return boxes;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModelPackage.PAGE__PROJECT_INTERNAL:
					if (projectInternal != null)
						msgs = ((InternalEObject)projectInternal).eInverseRemove(this, ProjectPackage.PROJECT__ELEMENTS_INTERNAL, Project.class, msgs);
					return basicSetProjectInternal((Project)otherEnd, msgs);
				default:
					return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
			}
		}
		if (eContainer != null)
			msgs = eBasicRemoveFromContainer(msgs);
		return eBasicSetContainer(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModelPackage.PAGE__PROJECT_INTERNAL:
					return basicSetProjectInternal(null, msgs);
				case ModelPackage.PAGE__BOXES:
					return ((InternalEList)getBoxes()).basicRemove(otherEnd, msgs);
				default:
					return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
			}
		}
		return eBasicSetContainer(null, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModelPackage.PAGE__LOCATION:
				return getLocation();
			case ModelPackage.PAGE__SIZE:
				return getSize();
			case ModelPackage.PAGE__NAME:
				return getName();
			case ModelPackage.PAGE__PROJECT_INTERNAL:
				if (resolve) return getProjectInternal();
				return basicGetProjectInternal();
			case ModelPackage.PAGE__BOXES:
				return getBoxes();
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
			case ModelPackage.PAGE__LOCATION:
				setLocation((Point)newValue);
				return;
			case ModelPackage.PAGE__SIZE:
				setSize((Dimension)newValue);
				return;
			case ModelPackage.PAGE__NAME:
				setName((String)newValue);
				return;
			case ModelPackage.PAGE__PROJECT_INTERNAL:
				setProjectInternal((Project)newValue);
				return;
			case ModelPackage.PAGE__BOXES:
				getBoxes().clear();
				getBoxes().addAll((Collection)newValue);
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
			case ModelPackage.PAGE__LOCATION:
				setLocation(LOCATION_EDEFAULT);
				return;
			case ModelPackage.PAGE__SIZE:
				setSize(SIZE_EDEFAULT);
				return;
			case ModelPackage.PAGE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ModelPackage.PAGE__PROJECT_INTERNAL:
				setProjectInternal((Project)null);
				return;
			case ModelPackage.PAGE__BOXES:
				getBoxes().clear();
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
			case ModelPackage.PAGE__LOCATION:
				return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
			case ModelPackage.PAGE__SIZE:
				return SIZE_EDEFAULT == null ? size != null : !SIZE_EDEFAULT.equals(size);
			case ModelPackage.PAGE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ModelPackage.PAGE__PROJECT_INTERNAL:
				return projectInternal != null;
			case ModelPackage.PAGE__BOXES:
				return boxes != null && !boxes.isEmpty();
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass) {
		if (baseClass == IProjectElement.class) {
			switch (derivedFeatureID) {
				default: return -1;
			}
		}
		if (baseClass == IAdaptable.class) {
			switch (derivedFeatureID) {
				default: return -1;
			}
		}
		if (baseClass == ProjectElement.class) {
			switch (derivedFeatureID) {
				case ModelPackage.PAGE__NAME: return ProjectPackage.PROJECT_ELEMENT__NAME;
				case ModelPackage.PAGE__PROJECT_INTERNAL: return ProjectPackage.PROJECT_ELEMENT__PROJECT_INTERNAL;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass) {
		if (baseClass == IProjectElement.class) {
			switch (baseFeatureID) {
				default: return -1;
			}
		}
		if (baseClass == IAdaptable.class) {
			switch (baseFeatureID) {
				default: return -1;
			}
		}
		if (baseClass == ProjectElement.class) {
			switch (baseFeatureID) {
				case ProjectPackage.PROJECT_ELEMENT__NAME: return ModelPackage.PAGE__NAME;
				case ProjectPackage.PROJECT_ELEMENT__PROJECT_INTERNAL: return ModelPackage.PAGE__PROJECT_INTERNAL;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: "); //$NON-NLS-1$
		result.append(name);
		result.append(')');
		return result.toString();
	}

    /**
     * TODO summary sentence for getName ...
     *
     * @see org.eclipse.ui.IEditorInput#getName()
     * @return
     * @generated
     */
    public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PAGE__NAME, oldName, name));
	}

    /**
     * @see net.refractions.udig.project.IProjectElement#getProject()
     */
    public IProject getProject() {
        return getProjectInternal();
    }

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		for (Iterator i = eAdapters().iterator(); i.hasNext();) {
			Object o = i.next();
			if (o.getClass().isAssignableFrom(adapter))
				return o;
		}
		return null;
	}

    public String getFileExtension() {
        return "upage"; //$NON-NLS-1$
    }

} //PageImpl
