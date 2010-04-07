/**
 * <copyright></copyright> $Id$
 */
package net.refractions.udig.project.internal.impl;

import java.util.Collection;
import java.util.List;

import net.refractions.udig.project.internal.ContextModel;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPackage;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;

/**
 * ContextModel responsible for holding on to layers for an IMap.
 * <p>
 * This class has several deprecated methods for working with the layers list
 * but they have all been moved to Map.
 * </p>
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
@SuppressWarnings("deprecation")
public class ContextModelImpl extends EObjectImpl implements ContextModel {

    private LayersList2 layers=new LayersList2(Layer.class, this,
            ProjectPackage.CONTEXT_MODEL__LAYERS, ProjectPackage.LAYER__CONTEXT_MODEL);

    @SuppressWarnings("unchecked")
    protected ContextModelImpl() {
        super();
    }

    
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return ProjectPackage.eINSTANCE.getContextModel();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated not
     */
    @SuppressWarnings("unchecked")
    public List<Layer> getLayers() {
        return layers;
        
    }

    /**
     * Typesafe Layer access as a workaround for EMF generation bug.
     * 
     * @return
     * @deprecated
     */
    public List<Layer> layers() {
        return getLayers();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Map getMap() {
        if (eContainerFeatureID != ProjectPackage.CONTEXT_MODEL__MAP)
            return null;
        return (Map) eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setMap( Map newMap ) {
        if (newMap != eContainer
                || (eContainerFeatureID != ProjectPackage.CONTEXT_MODEL__MAP && newMap != null)) {
            if (EcoreUtil.isAncestor(this, (EObject) newMap))
                throw new IllegalArgumentException(
                        "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMap != null)
                msgs = ((InternalEObject) newMap).eInverseAdd(this,
                        ProjectPackage.MAP__CONTEXT_MODEL, Map.class, msgs);
            msgs = eBasicSetContainer((InternalEObject) newMap, ProjectPackage.CONTEXT_MODEL__MAP,
                    msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.CONTEXT_MODEL__MAP, newMap, newMap));
    }

    public void addDeepAdapter( Adapter adapter ) {
        getMap().addDeepAdapter(adapter);
    }
    
    public void removeDeepAdapter( Adapter adapter ) {
        getMap().removeDeepAdapter(adapter);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void lowerLayer( Layer layer ) {
        getMap().lowerLayer(layer);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void raiseLayer( Layer layer ) {
        getMap().raiseLayer(layer);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public NotificationChain eInverseAdd( InternalEObject otherEnd, int featureID, Class baseClass,
            NotificationChain msgs ) {
        if (featureID >= 0) {
            switch( eDerivedStructuralFeatureID(featureID, baseClass) ) {
            case ProjectPackage.CONTEXT_MODEL__LAYERS:
                return ((InternalEList) getLayers()).basicAdd(otherEnd, msgs);
            case ProjectPackage.CONTEXT_MODEL__MAP:
                if (eContainer != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return eBasicSetContainer(otherEnd, ProjectPackage.CONTEXT_MODEL__MAP, msgs);
            default:
                return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public NotificationChain eInverseRemove( InternalEObject otherEnd, int featureID,
            Class baseClass, NotificationChain msgs ) {
        if (featureID >= 0) {
            switch( eDerivedStructuralFeatureID(featureID, baseClass) ) {
            case ProjectPackage.CONTEXT_MODEL__LAYERS:
                return ((InternalEList) getLayers()).basicRemove(otherEnd, msgs);
            case ProjectPackage.CONTEXT_MODEL__MAP:
                return eBasicSetContainer(null, ProjectPackage.CONTEXT_MODEL__MAP, msgs);
            default:
                return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eBasicRemoveFromContainer( NotificationChain msgs ) {
        if (eContainerFeatureID >= 0) {
            switch( eContainerFeatureID ) {
            case ProjectPackage.CONTEXT_MODEL__MAP:
                return eContainer.eInverseRemove(this, ProjectPackage.MAP__CONTEXT_MODEL,
                        Map.class, msgs);
            default:
                return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null,
                msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Object eGet( EStructuralFeature eFeature, boolean resolve ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.CONTEXT_MODEL__LAYERS:
            return getLayers();
        case ProjectPackage.CONTEXT_MODEL__MAP:
            return getMap();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public void eSet( EStructuralFeature eFeature, Object newValue ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.CONTEXT_MODEL__LAYERS:
            getLayers().clear();
            getLayers().addAll((Collection) newValue);
            return;
        case ProjectPackage.CONTEXT_MODEL__MAP:
            setMap((Map) newValue);
            return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void eUnset( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.CONTEXT_MODEL__LAYERS:
            getLayers().clear();
            return;
        case ProjectPackage.CONTEXT_MODEL__MAP:
            setMap((Map) null);
            return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.CONTEXT_MODEL__LAYERS:
            return getLayers() != null && !getLayers().isEmpty();
        case ProjectPackage.CONTEXT_MODEL__MAP:
            return getMap() != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String toString() {
        if (eIsProxy())
            return super.toString();

        return "Layers"+getLayers().toString(); //$NON-NLS-1$
    }

    /**
     * Turns off emf notification
     * 
     * @param notify true if notifications should be used.
     */
    public void setNotification( boolean notify ) {
        if (notify)
            eFlags = eFlags | (EDELIVER);
        else
            eFlags = eFlags & (~EDELIVER);
    }

    public void select( Envelope boundingBox ) {
        getMap().select(boundingBox);
    }

    public void select( Envelope boundingBox, boolean and ) {
        getMap().select(boundingBox,and);
    }

    public void select( Filter filter ) {
        getMap().select(filter);
    }

    public void select( Filter filter, boolean and ) {
        getMap().select(filter,and);
    }

    private volatile EList eAdapters;
    
    @Override
    public EList eAdapters() {
        if( eAdapters==null ){
            synchronized (this) {
                if( eAdapters==null ){
                    eAdapters=new SynchronizedEList(super.eAdapters()) ;
                }
            }
        }
        return eAdapters;
    }

}
