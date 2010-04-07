/**
 * <copyright></copyright> $Id$
 */
package net.refractions.udig.project.internal.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.AdaptableFeature;
import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.Envelope;

/**
 * The default implementation of the EditManager interface.
 * 
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class EditManagerImpl extends EObjectImpl implements EditManager {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getEditFeature() <em>Edit SimpleFeature</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getEditFeature()
     * @generated
     * @ordered
     */
    protected static final SimpleFeature EDIT_FEATURE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEditFeature() <em>Edit SimpleFeature</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getEditFeature()
     * @generated
     * @ordered
     */
    protected SimpleFeature editFeature = EDIT_FEATURE_EDEFAULT;

    /**
     * The cached value of the '{@link #getEditLayerInternal() <em>Edit Layer Internal</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getEditLayerInternal()
     * @generated
     * @ordered
     */
    protected Layer editLayerInternal = null;

    /**
     * The default value of the '{@link #getTransactionType() <em>Transaction Type</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getTransactionType()
     * @generated
     * @ordered
     */
    protected static final Class TRANSACTION_TYPE_EDEFAULT = null;

    /**
     * The default value of the '{@link #isEditLayerLocked() <em>Edit Layer Locked</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #isEditLayerLocked()
     * @generated
     * @ordered
     */
    protected static final boolean EDIT_LAYER_LOCKED_EDEFAULT = false;

    final UDIGTransaction transaction = new UDIGTransaction();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    protected EditManagerImpl() {
        super();
        eAdapters().add( eventCreator );
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return ProjectPackage.eINSTANCE.getEditManager();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Map getMapInternal() {
        if (eContainerFeatureID != ProjectPackage.EDIT_MANAGER__MAP_INTERNAL)
            return null;
        return (Map) eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setMapInternal( Map newMapInternal ) {
        if (newMapInternal != eContainer
                || (eContainerFeatureID != ProjectPackage.EDIT_MANAGER__MAP_INTERNAL && newMapInternal != null)) {
            if (EcoreUtil.isAncestor(this, (EObject) newMapInternal))
                throw new IllegalArgumentException(
                        "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMapInternal != null)
                msgs = ((InternalEObject) newMapInternal).eInverseAdd(this,
                        ProjectPackage.MAP__EDIT_MANAGER_INTERNAL, Map.class, msgs);
            msgs = eBasicSetContainer((InternalEObject) newMapInternal,
                    ProjectPackage.EDIT_MANAGER__MAP_INTERNAL, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.EDIT_MANAGER__MAP_INTERNAL, newMapInternal, newMapInternal));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public SimpleFeature getEditFeature() {
        return editFeature;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Layer getEditLayerInternal() {
        return editLayerInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Class getTransactionType() {
        // TODO: implement this method to return the 'Transaction Type' attribute
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * @see net.refractions.udig.project.internal.EditManager#getTransaction()
     * @uml.property name="transaction"
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void setEditFeature( SimpleFeature feature, Layer layer ) {
        if (layer == null && isEditLayerLocked()) {
            layer = editLayerInternal;
        }

        if (isEditLayerLocked() && (layer != editLayerInternal && editLayerInternal != null))
            throw new IllegalArgumentException(
                    "Edit Layer is locked so argument 'layer' should be not be changed."); //$NON-NLS-1$

        SimpleFeature value = getAdaptableFeature(feature, layer);
        SimpleFeature oldFeature = editFeature;
        if (editFeature != null) {
            editFeature = null;
        }
        if (value != null) {
            editFeature = value;
            this.editLayerInternal = layer;
        } else if (layer != null && !isEditLayerLocked()) {
            this.editLayerInternal = layer;
        }

        if (eNotificationRequired())
            if (oldFeature == value)
                return;
        eNotify(new ENotificationImpl(this, Notification.SET,
                ProjectPackage.EDIT_MANAGER__EDIT_FEATURE, oldFeature, value));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void startTransaction() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    private SimpleFeature getAdaptableFeature( SimpleFeature feature, Layer layer ) {
        if (feature == null || feature instanceof IAdaptable)
            return feature;

        return (SimpleFeature) new AdaptableFeature(feature, layer);
    }

    /**
     * Disables edit events so that the renderers and others listening to edit events won't be triggered until after the 
     * commit or rollback.
     * @param dialogMessage 
     */
    private void startCommitRollback(String dialogMessage){
    	List<Layer> layers = getMapInternal().getLayersInternal();
    	
    	for (Layer layer : layers) {
			layer.eSetDeliver(false);
		}
    	
    	RenderManager rm = getMapInternal().getRenderManagerInternal();
    	if( rm !=null ){
    		rm.disableRendering();
    	}
    	
    	showCommitDialog(dialogMessage);
    	
    }

	private void showCommitDialog(final String message) {
		// the commit flag controls whether or not the commit (progress) dialog stays open.
		committing = true;
    	final Display display = Display.getDefault();
    	final IRunnableWithProgress progressRunnable = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException,
					InterruptedException {
				monitor.beginTask(message, IProgressMonitor.UNKNOWN);
				while( committing ){
					if( !display.readAndDispatch() ){
						Thread.sleep(200);
					}
				}
			}
		};
		
		
		display.asyncExec(new Runnable() {
			public void run() {
				BusyIndicator.showWhile(display, new Runnable() {
					public void run() {
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(display.getActiveShell());

						try {
							dialog.run(false, false, progressRunnable);
						} catch (InvocationTargetException e) {
							// won't happen
							ProjectPlugin.log("", e);
						} catch (InterruptedException e) {
							// won't happen
							ProjectPlugin.log("", e);
						}
						
					}
				});

			}
		});
	}

	/**
	 * As long as committing is true then the Commit Dialog will stay open.  It should be set to true
	 * in the {@link #showCommitDialog()} and to false in {@link #commitRollbackComplete()}
	 */
    private volatile boolean committing = false; 

    /**
     * Enables rendering again and 
     * informs the feature store that editing has completed and the transaction should be reset.
     *
     * @see UDIGFeatureStore#editComplete()
     * @throws IOException
     */
    private void commitRollbackComplete() throws IOException {
        List<Layer> layers = getMapInternal().getLayersInternal();
		
		for (Layer layer1 : layers) {
			layer1.eSetDeliver(true);
		}
		
		RenderManager rm = getMapInternal().getRenderManagerInternal();
		if( rm !=null ){
			rm.enableRendering();
		}
		
        for( Layer layer : getMapInternal().getLayersInternal() ) {
        	FeatureStore<SimpleFeatureType, SimpleFeature> resource = layer.getResource(FeatureStore.class, ProgressManager.instance().get());
            if (resource != null && resource instanceof UDIGFeatureStore) {
                ((UDIGFeatureStore) resource).editComplete();
            }
        }
        // the next line closes the CommitDialog
        committing = false;
    }
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void commitTransaction() throws IOException {
        // if (!isEditing() ) return; // check for edit events? May not arrive so lets do what the
        // user tells us

        fireEvent(new EditManagerEvent(this, EditManagerEvent.PRE_COMMIT, null, null) );

        startCommitRollback("Comitting changes all layers in map");
        
        try {
            transaction.commitInternal();
            for( Layer layer : getMapInternal().getLayersInternal() ) {
                if (layer.getFeatureChanges().size() != 0) 
                    layer.getFeatureChanges().clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
            FeatureIterator<SimpleFeature> reader = null;
            try {
                FeatureSource<SimpleFeatureType, SimpleFeature> source = editLayerInternal.getResource(FeatureSource.class, null);
                FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
				FeatureCollection<SimpleFeatureType, SimpleFeature>  results = source.getFeatures(filterFactory
                        .id(FeatureUtils.stringToId(filterFactory, editFeature.getID())));
                reader = results.features();
                setEditFeature(reader.next(), editLayerInternal);
            } catch (Exception e2) {
                setEditFeature(null, null);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
            ProjectPlugin.getPlugin().log(e);
        } finally {
        	commitRollbackComplete();
        }
        
        fireEvent(new EditManagerEvent(this, EditManagerEvent.POST_COMMIT, null, null) );

    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @throws IOException
     * @generated NOT
     */
    public void rollbackTransaction() throws IOException {
        fireEvent(new EditManagerEvent(this, EditManagerEvent.PRE_ROLLBACK, null, null) );
        startCommitRollback("Rolling back to Previous checkpoint");
        
        HashMap<List<FeatureEvent>, FeatureEvent> modified=new HashMap<List<FeatureEvent>, FeatureEvent>();
        try {
        	synchronized (this) {
	            
	            for( Layer layer : getMapInternal().getLayersInternal() ) {
	                if (layer.getFeatureChanges().size() != 0) {
	                    List<FeatureEvent> changes = layer.getFeatureChanges();
	                    // create an event that notifies listeners that the area has changed again.
	                    // calculate bounds of all the Changes to date.
	                    // The reason for this is that otherwise I would have to make the entire
	                    // viewport re-render on a rollback. This little hack is to
	                    // get around that.
	                    Envelope envelope = new Envelope();
	                    for( FeatureEvent event : changes ) {
	                        envelope.expandToInclude(event.getBounds());
	                    }
	                    FeatureSource<SimpleFeatureType, SimpleFeature> source = layer.getResource(FeatureSource.class, null);
	                    FeatureEvent event = new FeatureEvent(source, FeatureEvent.FEATURES_CHANGED,
	                            envelope);
	
	                    modified.put(changes, event);
	                }
	            }
        	}
            if (selectedLayer != null)
                selectedLayer.setFilter(Filter.EXCLUDE);
            transaction.rollbackInternal();
            
        } catch (IOException e) {
            throw e;
        } finally {
            commitRollbackComplete();

            triggerLayerEvents(modified);
            
            setEditFeature(null, null);
            
            fireEvent(new EditManagerEvent(this, EditManagerEvent.POST_ROLLBACK, null, null) );
        }
    }

    private void triggerLayerEvents( HashMap<List<FeatureEvent>, FeatureEvent> modified ) {
        for( java.util.Map.Entry<List<FeatureEvent>, FeatureEvent> entry : modified.entrySet() ) {
            entry.getKey().add(entry.getValue());
            // now that the area has re-rendered (and whatever else) clear all the events so
            // the layer is considered clean.
            entry.getKey().clear();

        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseAdd( InternalEObject otherEnd, int featureID, Class baseClass,
            NotificationChain msgs ) {
        if (featureID >= 0) {
            switch( eDerivedStructuralFeatureID(featureID, baseClass) ) {
            case ProjectPackage.EDIT_MANAGER__MAP_INTERNAL:
                if (eContainer != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return eBasicSetContainer(otherEnd, ProjectPackage.EDIT_MANAGER__MAP_INTERNAL, msgs);
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
    public NotificationChain eInverseRemove( InternalEObject otherEnd, int featureID,
            Class baseClass, NotificationChain msgs ) {
        if (featureID >= 0) {
            switch( eDerivedStructuralFeatureID(featureID, baseClass) ) {
            case ProjectPackage.EDIT_MANAGER__MAP_INTERNAL:
                return eBasicSetContainer(null, ProjectPackage.EDIT_MANAGER__MAP_INTERNAL, msgs);
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
            case ProjectPackage.EDIT_MANAGER__MAP_INTERNAL:
                return eContainer.eInverseRemove(this, ProjectPackage.MAP__EDIT_MANAGER_INTERNAL,
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
        case ProjectPackage.EDIT_MANAGER__EDIT_FEATURE:
            return getEditFeature();
        case ProjectPackage.EDIT_MANAGER__MAP_INTERNAL:
            return getMapInternal();
        case ProjectPackage.EDIT_MANAGER__EDIT_LAYER_INTERNAL:
            return getEditLayerInternal();
        case ProjectPackage.EDIT_MANAGER__TRANSACTION_TYPE:
            return getTransactionType();
        case ProjectPackage.EDIT_MANAGER__EDIT_LAYER_LOCKED:
            return isEditLayerLocked() ? Boolean.TRUE : Boolean.FALSE;
        case ProjectPackage.EDIT_MANAGER__SELECTED_LAYER:
            if (resolve)
                return getSelectedLayer();
            return basicGetSelectedLayer();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void eSet( EStructuralFeature eFeature, Object newValue ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.EDIT_MANAGER__MAP_INTERNAL:
            setMapInternal((Map) newValue);
            return;
        case ProjectPackage.EDIT_MANAGER__EDIT_LAYER_LOCKED:
            setEditLayerLocked(((Boolean) newValue).booleanValue());
            return;
        case ProjectPackage.EDIT_MANAGER__SELECTED_LAYER:
            setSelectedLayer((Layer) newValue);
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
        case ProjectPackage.EDIT_MANAGER__MAP_INTERNAL:
            setMapInternal((Map) null);
            return;
        case ProjectPackage.EDIT_MANAGER__EDIT_LAYER_LOCKED:
            setEditLayerLocked(EDIT_LAYER_LOCKED_EDEFAULT);
            return;
        case ProjectPackage.EDIT_MANAGER__SELECTED_LAYER:
            setSelectedLayer((Layer) null);
            return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.EDIT_MANAGER__EDIT_FEATURE:
            return EDIT_FEATURE_EDEFAULT == null ? editFeature != null : !EDIT_FEATURE_EDEFAULT
                    .equals(editFeature);
        case ProjectPackage.EDIT_MANAGER__MAP_INTERNAL:
            return getMapInternal() != null;
        case ProjectPackage.EDIT_MANAGER__EDIT_LAYER_INTERNAL:
            return editLayerInternal != null;
        case ProjectPackage.EDIT_MANAGER__TRANSACTION_TYPE:
            return TRANSACTION_TYPE_EDEFAULT == null
                    ? getTransactionType() != null
                    : !TRANSACTION_TYPE_EDEFAULT.equals(getTransactionType());
        case ProjectPackage.EDIT_MANAGER__EDIT_LAYER_LOCKED:
            return editLayerLocked != EDIT_LAYER_LOCKED_EDEFAULT;
        case ProjectPackage.EDIT_MANAGER__SELECTED_LAYER:
            return selectedLayer != null;
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

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (editFeature: "); //$NON-NLS-1$
        result.append(editFeature);
        result.append(", editLayerLocked: "); //$NON-NLS-1$
        result.append(editLayerLocked);
        result.append(')');
        return result.toString();
    }

    /**
     * @throws IOException
     */
    public void addFeature( final SimpleFeature feature, Layer layer ) throws IOException {

        SimpleFeature adaptableFeature = feature;
        if (!(feature instanceof IAdaptable)) {
            adaptableFeature = new AdaptableFeature(feature, layer);

        }
        final SimpleFeature finalFeature = adaptableFeature;
        // setEditFeature(feature, layer);
        FeatureStore<SimpleFeatureType, SimpleFeature> store = layer.getResource(FeatureStore.class, null);
        FeatureCollection<SimpleFeatureType, SimpleFeature> c=new org.geotools.feature.collection.AdaptorFeatureCollection("copyCollection", store.getSchema()){

            @Override
            public int size() {
                return 1;
            }

            @Override
            protected Iterator openIterator() {
                return new Iterator(){

                    boolean more = true;

                    public boolean hasNext() {
                        return more;
                    }

                    public Object next() {
                        more = false;
                        return finalFeature;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                };
            }

            @Override
            protected void closeIterator( Iterator close ) {
            }
            
        };
        store.addFeatures(c);
    }

    /**
     * @see net.refractions.udig.project.internal.EditManager#refreshEditFeature()
     */
    public void refreshEditFeature() {
        try {
            FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
            FeatureIterator<SimpleFeature> editFeatureReader = getEditLayerInternal().getResource(
                    FeatureStore.class, null).getFeatures(
                    filterFactory.id(FeatureUtils.stringToId(filterFactory, getEditFeature().getID())))
                    .features();
            setEditFeature(editFeatureReader.next(), getEditLayerInternal());
        } catch (Exception e) {
            ProjectPlugin.log(null, e);
            setEditFeature(null, getEditLayerInternal());
        }

    }

    /**
     * Returns the currently selected Layer
     * 
     * @return the currently selected Layer
     */
    public Layer getSelectedLayer() {
        if( selectedLayer !=null && !getMap().getMapLayers().contains(selectedLayer)){
        		selectedLayer=null;
        }
        if (selectedLayer == null) {
            List<Layer> layers = getMapInternal().getLayersInternal();
            if (layers.size() != 0)
                setSelectedLayer(layers.get(layers.size() - 1));
        }
        
        return selectedLayer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Layer basicGetSelectedLayer() {
        return selectedLayer;
    }

    /**
     * Sets the currently selected Layer
     * 
     * @uml.property name="selectedLayer"
     */
    public void setSelectedLayer( Layer selectedLayer ) {
        if (!getMapInternal().getLayersInternal().contains(selectedLayer) || selectedLayer==this.selectedLayer)
            return;
        setSelectedLayerGen(selectedLayer);
    }

    /**
     * @generated
     */
    public void setSelectedLayerGen( Layer newSelectedLayer ) {
        Layer oldSelectedLayer = selectedLayer;
        selectedLayer = newSelectedLayer;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.EDIT_MANAGER__SELECTED_LAYER, oldSelectedLayer, selectedLayer));
    }

    /**
     * @see net.refractions.udig.project.IEditManager#getMap()
     */
    public IMap getMap() {
        return getMapInternal();
    }

    /**
     * @see net.refractions.udig.project.IEditManager#getEditLayer()
     */
    public ILayer getEditLayer() {
        return getEditLayerInternal();
    }

    public boolean isEditing() {
        for( Layer layer : getMapInternal().getLayersInternal() ) {
            if (layer.getFeatureChanges().size() != 0) {
                return true;
            }
        }
        return false;
    }

    // Support for normal java style events.
    Adapter eventCreator = new AdapterImpl(){
        public void notifyChanged( Notification msg ) {
            switch( msg.getFeatureID(EditManager.class) ) {
            case ProjectPackage.EDIT_MANAGER__EDIT_FEATURE:
                fireEvent(new EditManagerEvent(EditManagerImpl.this, EditManagerEvent.EDIT_FEATURE,
                        msg.getNewValue(), msg.getOldValue()));
                break;
            case ProjectPackage.EDIT_MANAGER__EDIT_LAYER_INTERNAL:
                fireEvent(new EditManagerEvent(EditManagerImpl.this, EditManagerEvent.EDIT_LAYER,
                        msg.getNewValue(), msg.getOldValue()));
                break;
            case ProjectPackage.EDIT_MANAGER__SELECTED_LAYER:
                fireEvent(new EditManagerEvent(EditManagerImpl.this, EditManagerEvent.SELECTED_LAYER,
                        msg.getNewValue(), msg.getOldValue()));
                break;
            default:
                break;
            }
        }
    };

    Set<IEditManagerListener> listeners = new CopyOnWriteArraySet<IEditManagerListener>();

    private boolean editLayerLocked = false;
    Layer selectedLayer;

    public void addListener( IEditManagerListener listener ) {
        listeners.add(listener);
    }
    
    public boolean containsListener( IEditManagerListener listener){
    	return listeners.contains(listener);
    }

    public void removeListener( IEditManagerListener listener ) {
        listeners.remove(listener);
    }

    void fireEvent( EditManagerEvent event ) {
        listeners.remove(null);
        for( IEditManagerListener object: listeners ) {
            try{
                if( object!=null )
                    object.changed(event);
            }catch(Throwable e){
                ProjectPlugin.log("Error while notifying listener of event: "+event.getType(), e); //$NON-NLS-1$
            }
        }
    }

    /**
     * @return Returns the editLayerLocked.
     * @uml.property name="editLayerLocked"
     */
    public boolean isEditLayerLocked() {
        return editLayerLocked;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setEditLayerLocked( boolean newEditLayerLocked ) {
        boolean oldEditLayerLocked = editLayerLocked;
        editLayerLocked = newEditLayerLocked;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.EDIT_MANAGER__EDIT_LAYER_LOCKED, oldEditLayerLocked,
                    editLayerLocked));
    }

} // LayerManagerImpl
