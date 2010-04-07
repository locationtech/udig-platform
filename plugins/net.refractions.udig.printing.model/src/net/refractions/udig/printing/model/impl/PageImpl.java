/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.printing.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.BoxPrinter;
import net.refractions.udig.printing.model.ModelPackage;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IProject;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.internal.Map;
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
    protected Project projectInternal;

    /**
     * The cached value of the '{@link #getBoxes() <em>Boxes</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBoxes()
     * @generated
     * @ordered
     */
    protected EList<Box> boxes;

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
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.PAGE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Project getProjectInternal() {
        if (projectInternal != null && projectInternal.eIsProxy()) {
            InternalEObject oldProjectInternal = (InternalEObject) projectInternal;
            projectInternal = (Project) eResolveProxy(oldProjectInternal);
            if (projectInternal != oldProjectInternal) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                            ModelPackage.PAGE__PROJECT_INTERNAL, oldProjectInternal,
                            projectInternal));
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
    public NotificationChain basicSetProjectInternal( Project newProjectInternal,
            NotificationChain msgs ) {
        Project oldProjectInternal = projectInternal;
        projectInternal = newProjectInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ModelPackage.PAGE__PROJECT_INTERNAL, oldProjectInternal, newProjectInternal);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setProjectInternal( Project newProjectInternal ) {
        if (newProjectInternal != projectInternal) {
            NotificationChain msgs = null;
            if (projectInternal != null)
                msgs = ((InternalEObject) projectInternal).eInverseRemove(this,
                        ProjectPackage.PROJECT__ELEMENTS_INTERNAL, Project.class, msgs);
            if (newProjectInternal != null)
                msgs = ((InternalEObject) newProjectInternal).eInverseAdd(this,
                        ProjectPackage.PROJECT__ELEMENTS_INTERNAL, Project.class, msgs);
            msgs = basicSetProjectInternal(newProjectInternal, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ModelPackage.PAGE__PROJECT_INTERNAL, newProjectInternal, newProjectInternal));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List<Box> getBoxes() {
        if (boxes == null) {
            boxes = new EObjectContainmentEList<Box>(Box.class, this, ModelPackage.PAGE__BOXES);
        }
        return boxes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd( InternalEObject otherEnd, int featureID,
            NotificationChain msgs ) {
        switch( featureID ) {
        case ModelPackage.PAGE__PROJECT_INTERNAL:
            if (projectInternal != null)
                msgs = ((InternalEObject) projectInternal).eInverseRemove(this,
                        ProjectPackage.PROJECT__ELEMENTS_INTERNAL, Project.class, msgs);
            return basicSetProjectInternal((Project) otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove( InternalEObject otherEnd, int featureID,
            NotificationChain msgs ) {
        switch( featureID ) {
        case ModelPackage.PAGE__PROJECT_INTERNAL:
            return basicSetProjectInternal(null, msgs);
        case ModelPackage.PAGE__BOXES:
            return ((InternalEList< ? >) getBoxes()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet( int featureID, boolean resolve, boolean coreType ) {
        switch( featureID ) {
        case ModelPackage.PAGE__NAME:
            return getName();
        case ModelPackage.PAGE__PROJECT_INTERNAL:
            if (resolve)
                return getProjectInternal();
            return basicGetProjectInternal();
        case ModelPackage.PAGE__BOXES:
            return getBoxes();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet( int featureID, Object newValue ) {
        switch( featureID ) {
        case ModelPackage.PAGE__NAME:
            setName((String) newValue);
            return;
        case ModelPackage.PAGE__PROJECT_INTERNAL:
            setProjectInternal((Project) newValue);
            return;
        case ModelPackage.PAGE__BOXES:
            getBoxes().clear();
            getBoxes().addAll((Collection< ? extends Box>) newValue);
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
    public void eUnset( int featureID ) {
        switch( featureID ) {
        case ModelPackage.PAGE__NAME:
            setName(NAME_EDEFAULT);
            return;
        case ModelPackage.PAGE__PROJECT_INTERNAL:
            setProjectInternal((Project) null);
            return;
        case ModelPackage.PAGE__BOXES:
            getBoxes().clear();
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
    public boolean eIsSet( int featureID ) {
        switch( featureID ) {
        case ModelPackage.PAGE__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case ModelPackage.PAGE__PROJECT_INTERNAL:
            return projectInternal != null;
        case ModelPackage.PAGE__BOXES:
            return boxes != null && !boxes.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eBaseStructuralFeatureID( int derivedFeatureID, Class< ? > baseClass ) {
        if (baseClass == IProjectElement.class) {
            switch( derivedFeatureID ) {
            default:
                return -1;
            }
        }
        if (baseClass == IAdaptable.class) {
            switch( derivedFeatureID ) {
            default:
                return -1;
            }
        }
        if (baseClass == ProjectElement.class) {
            switch( derivedFeatureID ) {
            case ModelPackage.PAGE__NAME:
                return ProjectPackage.PROJECT_ELEMENT__NAME;
            case ModelPackage.PAGE__PROJECT_INTERNAL:
                return ProjectPackage.PROJECT_ELEMENT__PROJECT_INTERNAL;
            default:
                return -1;
            }
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eDerivedStructuralFeatureID( int baseFeatureID, Class< ? > baseClass ) {
        if (baseClass == IProjectElement.class) {
            switch( baseFeatureID ) {
            default:
                return -1;
            }
        }
        if (baseClass == IAdaptable.class) {
            switch( baseFeatureID ) {
            default:
                return -1;
            }
        }
        if (baseClass == ProjectElement.class) {
            switch( baseFeatureID ) {
            case ProjectPackage.PROJECT_ELEMENT__NAME:
                return ModelPackage.PAGE__NAME;
            case ProjectPackage.PROJECT_ELEMENT__PROJECT_INTERNAL:
                return ModelPackage.PAGE__PROJECT_INTERNAL;
            default:
                return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (name: ");
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
    public void setName( String newName ) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PAGE__NAME, oldName,
                    name));
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
    public Object getAdapter( Class adapter ) {
        for( Iterator i = eAdapters().iterator(); i.hasNext(); ) {
            Object o = i.next();
            if (o.getClass().isAssignableFrom(adapter))
                return o;
        }
        return null;
    }

    public String getFileExtension() {
        return "upage"; //$NON-NLS-1$
    }

    @SuppressWarnings("unchecked")
    public List getElements( Class type ) {

        List lists = new ArrayList();

        List<Box> boxList = getBoxes();
        for( Box box : boxList ) {
            if (type.isAssignableFrom(box.getClass())) {
                lists.add(box);
            }
        }

        for( Box box : boxList ) {
            BoxPrinter boxPrinter = box.getBoxPrinter();
            if (boxPrinter instanceof MapBoxPrinter) {
                MapBoxPrinter mapBP = (MapBoxPrinter) boxPrinter;
                Map map = mapBP.getMap();

                if (map != null && type.isAssignableFrom(map.getClass())) {
                    lists.add(map);
                }
            }
        }

        return lists;
    }

    @SuppressWarnings("unchecked")
    public List getElements() {
        return getBoxes();
    }

    @Override
    public void setSize( Dimension newSize ) {
        Dimension previousSize = getSize();
        if (previousSize != null) {
            int newW = newSize.width;
            int newH = newSize.height;
            int prevW = previousSize.width;
            int prevH = previousSize.height;

            float xScale = (float) prevW / (float) newW;
            float yScale = (float) prevH / (float) newH;

            List<Box> myboxes = getBoxes();
            for( Box box : myboxes ) {
                box.eSetDeliver(false);
                try {
                    Dimension boxSize = box.getSize();
                    int boxH = boxSize.height;
                    int boxW = boxSize.width;

                    float newBoxW = (float) boxW / xScale;
                    float newBoxH = (float) boxH / yScale;

                    box.setSize(new Dimension((int) newBoxW, (int) newBoxH));
                } finally {
                    box.eSetDeliver(true);
                }
            }
        }
        super.setSize(newSize);
    }
} // PageImpl
