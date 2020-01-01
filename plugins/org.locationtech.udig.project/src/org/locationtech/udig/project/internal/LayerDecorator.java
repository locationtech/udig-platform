/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolveChangeEvent;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILayerListener;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.LayerEvent;
import org.locationtech.udig.ui.palette.ColourScheme;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.data.FeatureEvent;
import org.geotools.data.Query;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.Range;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Envelope;

/**
 * Wraps a layer and delegates all the method calls to that layer. See the gang of four decorator
 * pattern.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class LayerDecorator implements Layer, InternalEObject {

    final protected Layer               layer;

    final private InternalEObject       interalObject;

    /**
     * Listeners to this layer.
     * <p>
     * Note this will need to be hooked into the usual EMF adapater mechasim.
     * </p>
     */
    CopyOnWriteArraySet<ILayerListener> listeners = new CopyOnWriteArraySet<ILayerListener>();

    ILayerListener watcher   = new ILayerListener(){
        public void refresh( LayerEvent event ) {
            LayerEvent myEvent = new LayerEvent(
                  LayerDecorator.this, event.getType(), event.getOldValue(), event.getNewValue());
            fireLayerChange(myEvent);
       }
  };

    /**
     * Construct <code>LayerDecorator</code>.
     * 
     * @param layer
     */
    public LayerDecorator( Layer layer ) {
        this.layer = layer;
        this.interalObject = (InternalEObject) layer;
    }

    /*
     * @see org.locationtech.udig.project.Layer#addListener(org.locationtech.udig.project.LayerListener)
     */
    public synchronized void addListener( final ILayerListener listener ) {
        if (listeners == null) {
            listeners = new CopyOnWriteArraySet<ILayerListener>();
            layer.addListener(watcher);
        }
        listeners.add(listener);
    }

    /*
     * @see org.locationtech.udig.project.Layer#removeListener(org.locationtech.udig.project.LayerListener)
     */
    public synchronized void removeListener( final ILayerListener listener ) {
        listeners.remove(listener);
        if (listeners.size() == 0) {
            layer.removeListener(watcher);
            listeners = null;
        }
    }

    protected synchronized void fireLayerChange( LayerEvent event ) {
        if (listeners.size() == 0) {
            return; // nobody is listenting
        }
        for( ILayerListener listener : listeners ) {
            try {
                listener.refresh(event);
            } catch (Throwable t) {
                ProjectPlugin.log("Error in listener:"+listener, t); //$NON-NLS-1$
            }
        }
    }

    /*
     * @see org.locationtech.udig.project.Layer#properties()
     */
    public IBlackboard getProperties() {
        return layer.getProperties();
    }

    /*
     * @see org.locationtech.udig.project.Layer#getContextModel()
     */
    public ContextModel getContextModel() {
        return layer.getContextModel();
    }

    /*
     * @see org.locationtech.udig.project.Layer#setContextModel(org.locationtech.udig.project.ContextModel)
     */
    public void setContextModel( ContextModel value ) {
        layer.setContextModel(value);
    }

    /*
     * @see org.locationtech.udig.project.Layer#getFilter()
     */
    public Filter getFilter() {
        return layer.getFilter();
    }

    /*
     * @see org.locationtech.udig.project.Layer#setFilter(org.opengis.filter.Filter)
     */
    public void setFilter( Filter value ) {
        layer.setFilter(value);
    }

    /*
     * @see org.locationtech.udig.project.Layer#getStyleBlackboard()
     */
    public StyleBlackboard getStyleBlackboard() {
        return layer.getStyleBlackboard();
    }

    /*
     * @see org.locationtech.udig.project.Layer#setStyleBlackboard(org.locationtech.udig.project.StyleBlackboard)
     */
    public void setStyleBlackboard( StyleBlackboard value ) {
        layer.setStyleBlackboard(value);
    }

    /*
     * @see org.locationtech.udig.project.Layer#getZorder()
     */
    public int getZorder() {
        return layer.getZorder();
    }

    /*
     * @see org.locationtech.udig.project.Layer#setZorder(int)
     */
    public void setZorder( int value ) {
        layer.setZorder(value);
    }

    /*
     * @see org.locationtech.udig.project.Layer#getStatus()
     */
    public int getStatus() {
        return layer.getStatus();
    }

    /*
     * @see org.locationtech.udig.project.Layer#setStatus(int)
     */
    public void setStatus( int value ) {
        layer.setStatus(value);
    }

    /*
     * @see org.locationtech.udig.project.Layer#isApplicable(java.lang.String)
     */
    public boolean getInteraction( Interaction interaction ) {
        return layer.getInteraction(interaction);
    }

    /*
     * @see org.locationtech.udig.project.Layer#setApplicable(java.lang.String, boolean)
     */
    public void setInteraction( Interaction interaction, boolean isApplicable ) {
        layer.setInteraction(interaction, isApplicable);
    }

    /*
     * @see org.locationtech.udig.project.Layer#isSelectable()
     */
    public boolean isSelectable() {
        return layer.isSelectable();
    }

    /*
     * @see org.locationtech.udig.project.Layer#setSelectable(boolean)
     */
    public void setSelectable( boolean value ) {
        layer.setSelectable(value);
    }

    /*
     * @see org.locationtech.udig.project.Layer#getName()
     */
    public String getName() {
        return layer.getName();
    }

    /*
     * @see org.locationtech.udig.project.Layer#setName(java.lang.String)
     */
    public void setName( String value ) {
        layer.setName(value);
    }

    /*
     * @see org.locationtech.udig.project.Layer#getCatalogRef()
     */
    public CatalogRef getCatalogRef() {
        return layer.getCatalogRef();
    }

    /*
     * @see org.locationtech.udig.project.Layer#setCatalogRef(org.locationtech.udig.project.LayerRef)
     */
    public void setCatalogRef( CatalogRef value ) {
        layer.setCatalogRef(value);
    }

    /*
     * @see org.locationtech.udig.project.Layer#getID()
     */
    public URL getID() {
        return layer.getID();
    }

    /*
     * @see org.locationtech.udig.project.Layer#setID(java.net.URL)
     */
    public void setID( URL value ) {
        layer.setID(value);
    }

    /*
     * @see org.locationtech.udig.project.Layer#isVisible()
     */
    public boolean isVisible() {
        return layer.isVisible();
    }

    /*
     * @see org.locationtech.udig.project.Layer#setVisible(boolean)
     */
    public void setVisible( boolean value ) {
        layer.setVisible(value);
    }

    /*
     * @see org.locationtech.udig.project.Layer#getGeoResources()
     */
    public List<IGeoResource> getGeoResources() {
        return layer.getGeoResources();
    }

    /*
     * @see org.locationtech.udig.project.Layer#getGlyph()
     */
    public ImageDescriptor getIcon() {
        return layer.getIcon();
    }

    /*
     * @see org.locationtech.udig.project.Layer#setGlyph(org.eclipse.jface.resource.ImageDescriptor)
     */
    public void setIcon( ImageDescriptor value ) {
        layer.setIcon(value);
    }

    /*
     * @see org.locationtech.udig.project.Layer#getQuery(boolean)
     */
    public Query getQuery( boolean selection ) {
        return layer.getQuery(selection);
    }

    /*
     * @see org.locationtech.udig.project.Layer#getSchema()
     */
    public SimpleFeatureType getSchema() {
        return layer.getSchema();
    }

    /*
     * @see org.locationtech.udig.project.Layer#getCRS(org.eclipse.core.runtime.IProgressMonitor)
     */
    public CoordinateReferenceSystem getCRS( IProgressMonitor monitor ) {
        return layer.getCRS();
    }

    /*
     * @see org.locationtech.udig.project.Layer#getCRS()
     */
    public CoordinateReferenceSystem getCRS() {
        return layer.getCRS();
    }

    /*
     * @see org.locationtech.udig.project.Layer#setCRS(org.opengis.referencing.crs.CoordinateReferenceSystem)
     */
    public void setCRS( CoordinateReferenceSystem value ) {
        layer.setCRS(value);
    }

    /*
     * @see org.locationtech.udig.project.Layer#refresh(org.locationtech.jts.geom.Envelope)
     */
    public void refresh( Envelope bounds ) {
        layer.refresh(bounds);
    }

    /*
     * @see org.locationtech.udig.project.Layer#getMathTransform()
     */
    public MathTransform layerToMapTransform() throws IOException {
        return layer.layerToMapTransform();
    }

    /*
     * @see org.locationtech.udig.project.Layer#getBounds(org.eclipse.core.runtime.IProgressMonitor,
     *      org.opengis.referencing.crs.CoordinateReferenceSystem)
     */
    public ReferencedEnvelope getBounds( IProgressMonitor monitor, CoordinateReferenceSystem crs ) {
        return layer.getBounds(monitor, crs);
    }

    /*
     * @see org.locationtech.udig.project.Layer#createBBoxFilter(org.locationtech.jts.geom.Envelope)
     */
    public Filter createBBoxFilter( Envelope boundingBox, IProgressMonitor monitor ) {
        return layer.createBBoxFilter(boundingBox, monitor);
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eClass()
     */
    public EClass eClass() {
        return layer.eClass();
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eResource()
     */
    public Resource eResource() {
        return layer.eResource();
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eContainer()
     */
    public EObject eContainer() {
        return layer.eContainer();
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eContainingFeature()
     */
    public EStructuralFeature eContainingFeature() {
        return layer.eContainingFeature();
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eContainmentFeature()
     */
    public EReference eContainmentFeature() {
        return layer.eContainmentFeature();
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eContents()
     */
    public EList eContents() {
        return layer.eContents();
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eAllContents()
     */
    public TreeIterator eAllContents() {
        return layer.eAllContents();
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eIsProxy()
     */
    public boolean eIsProxy() {
        return layer.eIsProxy();
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eCrossReferences()
     */
    public EList eCrossReferences() {
        return layer.eCrossReferences();
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eGet(org.eclipse.emf.ecore.EStructuralFeature)
     */
    public Object eGet( EStructuralFeature feature ) {
        return layer.eGet(feature);
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eGet(org.eclipse.emf.ecore.EStructuralFeature, boolean)
     */
    public Object eGet( EStructuralFeature feature, boolean resolve ) {
        return layer.eGet(feature, resolve);
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eSet(org.eclipse.emf.ecore.EStructuralFeature,
     *      java.lang.Object)
     */
    public void eSet( EStructuralFeature feature, Object newValue ) {
        layer.eSet(feature, newValue);
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)
     */
    public boolean eIsSet( EStructuralFeature feature ) {
        return layer.eIsSet(feature);
    }

    /*
     * @see org.eclipse.emf.ecore.EObject#eUnset(org.eclipse.emf.ecore.EStructuralFeature)
     */
    public void eUnset( EStructuralFeature feature ) {
        layer.eUnset(feature);
    }

    /*
     * @see org.eclipse.emf.common.notify.Notifier#eAdapters()
     */
    public EList eAdapters() {
        return layer.eAdapters();
    }

    /*
     * @see org.eclipse.emf.common.notify.Notifier#eDeliver()
     */
    public boolean eDeliver() {
        return layer.eDeliver();
    }

    /*
     * @see org.eclipse.emf.common.notify.Notifier#eSetDeliver(boolean)
     */
    public void eSetDeliver( boolean deliver ) {
        layer.eSetDeliver(deliver);
    }

    /*
     * @see org.eclipse.emf.common.notify.Notifier#eNotify(org.eclipse.emf.common.notify.Notification)
     */
    public void eNotify( Notification notification ) {
        layer.eNotify(notification);
    }

    /*
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( ILayer other ) {
        return layer.compareTo(other);
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#getMap()
     */
    public Map getMapInternal() {
        return layer.getMapInternal();
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eNotificationRequired()
     */
    public boolean eNotificationRequired() {
        return interalObject.eNotificationRequired();
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eURIFragmentSegment(org.eclipse.emf.ecore.EStructuralFeature,
     *      org.eclipse.emf.ecore.EObject)
     */
    public String eURIFragmentSegment( EStructuralFeature eFeature, EObject eObject ) {
        return interalObject.eURIFragmentSegment(eFeature, eObject);
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eObjectForURIFragmentSegment(java.lang.String)
     */
    public EObject eObjectForURIFragmentSegment( String uriFragmentSegment ) {
        return interalObject.eObjectForURIFragmentSegment(uriFragmentSegment);
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eSetClass(org.eclipse.emf.ecore.EClass)
     */
    public void eSetClass( EClass eClass ) {
        interalObject.eSetClass(eClass);
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eSetting(org.eclipse.emf.ecore.EStructuralFeature)
     */
    public Setting eSetting( EStructuralFeature feature ) {
        return interalObject.eSetting(feature);
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eBaseStructuralFeatureID(int, java.lang.Class)
     */
//    public int eBaseStructuralFeatureID( int derivedFeatureID, Class baseClass ) {
//        return interalObject.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
//    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eContainerFeatureID()
     */
    public int eContainerFeatureID() {
        return interalObject.eContainerFeatureID();
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eDerivedStructuralFeatureID(int, java.lang.Class)
     */
//    public int eDerivedStructuralFeatureID( int baseFeatureID, Class baseClass ) {
//        return interalObject.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
//    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eSetResource(org.eclipse.emf.ecore.resource.Resource.Internal,
     *      org.eclipse.emf.common.notify.NotificationChain)
     */
    public NotificationChain eSetResource( Internal resource, NotificationChain notifications ) {
        return interalObject.eSetResource(resource, notifications);
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eInverseAdd(org.eclipse.emf.ecore.InternalEObject,
     *      int, java.lang.Class, org.eclipse.emf.common.notify.NotificationChain)
     */
//    public NotificationChain eInverseAdd( InternalEObject otherEnd, int featureID, Class baseClass,
//            NotificationChain notifications ) {
//        return interalObject.eInverseAdd(otherEnd, featureID, baseClass, notifications);
//    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eInverseRemove(org.eclipse.emf.ecore.InternalEObject,
     *      int, java.lang.Class, org.eclipse.emf.common.notify.NotificationChain)
     */
//    public NotificationChain eInverseRemove( InternalEObject otherEnd, int featureID,
//            Class baseClass, NotificationChain notifications ) {
//        return interalObject.eInverseRemove(otherEnd, featureID, baseClass, notifications);
//    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eBasicSetContainer(org.eclipse.emf.ecore.InternalEObject,
     *      int, org.eclipse.emf.common.notify.NotificationChain)
     */
    public NotificationChain eBasicSetContainer( InternalEObject newContainer,
            int newContainerFeatureID, NotificationChain notifications ) {
        return interalObject.eBasicSetContainer(newContainer, newContainerFeatureID, notifications);
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eBasicRemoveFromContainer(org.eclipse.emf.common.notify.NotificationChain)
     */
    public NotificationChain eBasicRemoveFromContainer( NotificationChain notifications ) {
        return interalObject.eBasicRemoveFromContainer(notifications);
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eProxyURI()
     */
    public URI eProxyURI() {
        return interalObject.eProxyURI();
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eSetProxyURI(org.eclipse.emf.common.util.URI)
     */
    public void eSetProxyURI( URI uri ) {
        interalObject.eSetProxyURI(uri);
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eResolveProxy(org.eclipse.emf.ecore.InternalEObject)
     */
    public EObject eResolveProxy( InternalEObject proxy ) {
        return interalObject.eResolveProxy(proxy);
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eInternalResource()
     */
    public Internal eInternalResource() {
        return interalObject.eInternalResource();
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eStore()
     */
    public EStore eStore() {
        return interalObject.eStore();
    }

    /**
     * @see org.eclipse.emf.ecore.InternalEObject#eSetStore(org.eclipse.emf.ecore.InternalEObject.EStore)
     */
    public void eSetStore( EStore store ) {
        interalObject.eSetStore(store);
    }

    /**
     * @see org.locationtech.udig.project.ILayer#getGeoResource()
     */
    public IGeoResource getGeoResource() {
        return layer.getGeoResource();
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#setGeoResource(org.locationtech.udig.catalog.IGeoResource)
     * @deprecated
     */
    public void setGeoResource( IGeoResource value ) {
        layer.setGeoResource(value);
    }

    /**
     * @see org.locationtech.udig.project.ILayer#getGeoResource(java.lang.Class)
     */
    public <E> E getResource( Class<E> resourceType, IProgressMonitor monitor ) throws IOException {
        return layer.getResource(resourceType, monitor);
    }

    /**
     * The decorated layer
     * 
     * @return the decorated layer
     */
    public Layer getWrappedLayer() {
        return layer;
    }

    /**
     * @see org.locationtech.udig.project.ILayer#getGeoResource(java.lang.Class)
     * @deprecated
     */
    public <T> IGeoResource getGeoResource( Class<T> clazz ) {
        return layer.getGeoResource(clazz);
    }

    /**
     * @see org.locationtech.udig.project.ILayer#getMap()
     */
    public IMap getMap() {
        return layer.getMap();
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class adapter ) {
        return layer.getAdapter(adapter);
    }

    /**
     * @see org.locationtech.udig.core.IBlockingAdaptable#getAdapter(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T getAdapter( Class<T> adapter, IProgressMonitor monitor ) throws IOException {
        return layer.getAdapter(adapter, monitor);
    }

    /**
     * @see org.locationtech.udig.core.IBlockingAdaptable#canAdaptTo(java.lang.Class)
     */
    public <T> boolean canAdaptTo( Class<T> adapter ) {
        return layer.canAdaptTo(adapter);
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#getColourScheme()
     */
    public ColourScheme getColourScheme() {
        return layer.getColourScheme();
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#setColourScheme(org.locationtech.udig.ui.palette.ColourScheme)
     */
    public void setColourScheme( ColourScheme value ) {
        layer.setColourScheme(value);
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#getDefaultColor()
     */
    public Color getDefaultColor() {
        return layer.getDefaultColor();
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#setDefaultColor(java.awt.Color)
     */
    public void setDefaultColor( Color value ) {
        layer.setDefaultColor(value);
    }

    /**
     * @see org.locationtech.udig.project.ILayer#mapToLayerTransform()
     */
    public MathTransform mapToLayerTransform() throws IOException {
        return layer.mapToLayerTransform();
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#setStatusMessage(java.lang.String)
     */
    public void setStatusMessage( String message ) {
        layer.setStatusMessage(message);
    }

    /**
     * @see org.locationtech.udig.project.ILayer#getStatusMessage()
     */
    public String getStatusMessage() {
        return layer.getStatusMessage();
    }

    public void changed( IResolveChangeEvent event ) {
        // do nothing. We don't want to give the layer the same event twice.
    }

    public List<FeatureEvent> getFeatureChanges() {
        return layer.getFeatureChanges();
    }

    public double getMinScaleDenominator() {
        return layer.getMinScaleDenominator();
    }

    public double getMaxScaleDenominator() {
        return layer.getMaxScaleDenominator();
    }

    public void setMinScaleDenominator( double value ) {
        layer.setMinScaleDenominator(value);
    }

    public void setMaxScaleDenominator( double value ) {
        layer.setMaxScaleDenominator(value);
    }

    /**
     * @deprecated
     */
    public <T> boolean isType( Class<T> resourceType ) {
        return layer.hasResource(resourceType);
    }
    public <T> boolean hasResource( Class<T> resourceType ) {
        return layer.hasResource(resourceType);
    }

    public Internal eDirectResource() {
        return interalObject.eDirectResource();
    }

    public Object eGet( EStructuralFeature arg0, boolean arg1, boolean arg2 ) {
        return interalObject.eGet(arg0, arg1, arg2);
    }

    public Object eGet( int arg0, boolean arg1, boolean arg2 ) {
        return interalObject.eGet(arg0, arg1, arg2);
    }

    public InternalEObject eInternalContainer() {
        return interalObject.eInternalContainer();
    }

    public boolean eIsSet( int arg0 ) {
        return interalObject.eIsSet(arg0);
    }

    public void eSet( int arg0, Object arg1 ) {
        interalObject.eSet(arg0, arg1);
    }

    public void eUnset( int arg0 ) {
        interalObject.eUnset(arg0);
    }

    public <T> IGeoResource findGeoResource( Class<T> clazz ) {
        return layer.findGeoResource(clazz);
    }

    public IBlackboard getBlackboard() {
        return layer.getBlackboard();
    }

    public void setBounds( ReferencedEnvelope bounds ) {
        layer.setBounds(bounds);
    }

    public Set<Range> getScaleRange() {
        return layer.getScaleRange();
    }

    public Object eInvoke( EOperation operation, EList< ? > arguments )
            throws InvocationTargetException {
        return layer.eInvoke(operation, arguments);
    }

    public int eBaseStructuralFeatureID( int derivedFeatureID, Class< ? > baseClass ) {
        return this.interalObject.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    public int eDerivedStructuralFeatureID( int baseFeatureID, Class< ? > baseClass ) {
        return interalObject.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }

    public NotificationChain eInverseAdd( InternalEObject otherEnd, int featureID,
            Class< ? > baseClass, NotificationChain notifications ) {
        return interalObject.eInverseAdd(otherEnd, featureID, baseClass, notifications);
    }

    public NotificationChain eInverseRemove( InternalEObject otherEnd, int featureID,
            Class< ? > baseClass, NotificationChain notifications ) {
        return interalObject.eInverseRemove(otherEnd, featureID, baseClass, notifications);
    }

    public Object eInvoke( int operationID, EList< ? > arguments ) throws InvocationTargetException {
        return interalObject.eInvoke(operationID, arguments);
    }

    public int eDerivedOperationID( int baseOperationID, Class< ? > baseClass ) {   
        return interalObject.eDerivedOperationID(baseOperationID, baseClass);
    }

    @Override
    public java.util.Map<Interaction, Boolean> getInteractionMap() {
        return layer.getInteractionMap();
    }

    @Override
    public boolean isShown() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setShown( boolean value ) {
        // TODO Auto-generated method stub
        
    }

}
