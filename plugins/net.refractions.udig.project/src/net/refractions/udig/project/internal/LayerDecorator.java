/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ILayerListener;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.Interaction;
import net.refractions.udig.project.LayerEvent;
import net.refractions.udig.ui.palette.ColourScheme;

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

import com.vividsolutions.jts.geom.Envelope;

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
     * @see net.refractions.udig.project.Layer#addListener(net.refractions.udig.project.LayerListener)
     */
    public synchronized void addListener( final ILayerListener listener ) {
        if (listeners == null) {
            listeners = new CopyOnWriteArraySet<ILayerListener>();
            layer.addListener(watcher);
        }
        listeners.add(listener);
    }

    /*
     * @see net.refractions.udig.project.Layer#removeListener(net.refractions.udig.project.LayerListener)
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
     * @see net.refractions.udig.project.Layer#properties()
     */
    public IBlackboard getProperties() {
        return layer.getProperties();
    }

    /*
     * @see net.refractions.udig.project.Layer#getContextModel()
     */
    public ContextModel getContextModel() {
        return layer.getContextModel();
    }

    /*
     * @see net.refractions.udig.project.Layer#setContextModel(net.refractions.udig.project.ContextModel)
     */
    public void setContextModel( ContextModel value ) {
        layer.setContextModel(value);
    }

    /*
     * @see net.refractions.udig.project.Layer#getFilter()
     */
    public Filter getFilter() {
        return layer.getFilter();
    }

    /*
     * @see net.refractions.udig.project.Layer#setFilter(org.opengis.filter.Filter)
     */
    public void setFilter( Filter value ) {
        layer.setFilter(value);
    }

    /*
     * @see net.refractions.udig.project.Layer#getStyleBlackboard()
     */
    public StyleBlackboard getStyleBlackboard() {
        return layer.getStyleBlackboard();
    }

    /*
     * @see net.refractions.udig.project.Layer#setStyleBlackboard(net.refractions.udig.project.StyleBlackboard)
     */
    public void setStyleBlackboard( StyleBlackboard value ) {
        layer.setStyleBlackboard(value);
    }

    /*
     * @see net.refractions.udig.project.Layer#getZorder()
     */
    public int getZorder() {
        return layer.getZorder();
    }

    /*
     * @see net.refractions.udig.project.Layer#setZorder(int)
     */
    public void setZorder( int value ) {
        layer.setZorder(value);
    }

    /*
     * @see net.refractions.udig.project.Layer#getStatus()
     */
    public int getStatus() {
        return layer.getStatus();
    }

    /*
     * @see net.refractions.udig.project.Layer#setStatus(int)
     */
    public void setStatus( int value ) {
        layer.setStatus(value);
    }

    /*
     * @see net.refractions.udig.project.Layer#isApplicable(java.lang.String)
     */
    public boolean getInteraction( Interaction interaction ) {
        return layer.getInteraction(interaction);
    }

    /*
     * @see net.refractions.udig.project.Layer#setApplicable(java.lang.String, boolean)
     */
    public void setInteraction( Interaction interaction, boolean isApplicable ) {
        layer.setInteraction(interaction, isApplicable);
    }

    /*
     * @see net.refractions.udig.project.Layer#isSelectable()
     */
    public boolean isSelectable() {
        return layer.isSelectable();
    }

    /*
     * @see net.refractions.udig.project.Layer#setSelectable(boolean)
     */
    public void setSelectable( boolean value ) {
        layer.setSelectable(value);
    }

    /*
     * @see net.refractions.udig.project.Layer#getName()
     */
    public String getName() {
        return layer.getName();
    }

    /*
     * @see net.refractions.udig.project.Layer#setName(java.lang.String)
     */
    public void setName( String value ) {
        layer.setName(value);
    }

    /*
     * @see net.refractions.udig.project.Layer#getCatalogRef()
     */
    public CatalogRef getCatalogRef() {
        return layer.getCatalogRef();
    }

    /*
     * @see net.refractions.udig.project.Layer#setCatalogRef(net.refractions.udig.project.LayerRef)
     */
    public void setCatalogRef( CatalogRef value ) {
        layer.setCatalogRef(value);
    }

    /*
     * @see net.refractions.udig.project.Layer#getID()
     */
    public URL getID() {
        return layer.getID();
    }

    /*
     * @see net.refractions.udig.project.Layer#setID(java.net.URL)
     */
    public void setID( URL value ) {
        layer.setID(value);
    }

    /*
     * @see net.refractions.udig.project.Layer#isVisible()
     */
    public boolean isVisible() {
        return layer.isVisible();
    }

    /*
     * @see net.refractions.udig.project.Layer#setVisible(boolean)
     */
    public void setVisible( boolean value ) {
        layer.setVisible(value);
    }

    /*
     * @see net.refractions.udig.project.Layer#getGeoResources()
     */
    public List<IGeoResource> getGeoResources() {
        return layer.getGeoResources();
    }

    /*
     * @see net.refractions.udig.project.Layer#getGlyph()
     */
    public ImageDescriptor getIcon() {
        return layer.getIcon();
    }

    /*
     * @see net.refractions.udig.project.Layer#setGlyph(org.eclipse.jface.resource.ImageDescriptor)
     */
    public void setIcon( ImageDescriptor value ) {
        layer.setIcon(value);
    }

    /*
     * @see net.refractions.udig.project.Layer#getQuery(boolean)
     */
    public Query getQuery( boolean selection ) {
        return layer.getQuery(selection);
    }

    /*
     * @see net.refractions.udig.project.Layer#getSchema()
     */
    public SimpleFeatureType getSchema() {
        return layer.getSchema();
    }

    /*
     * @see net.refractions.udig.project.Layer#getCRS(org.eclipse.core.runtime.IProgressMonitor)
     */
    public CoordinateReferenceSystem getCRS( IProgressMonitor monitor ) {
        return layer.getCRS();
    }

    /*
     * @see net.refractions.udig.project.Layer#getCRS()
     */
    public CoordinateReferenceSystem getCRS() {
        return layer.getCRS();
    }

    /*
     * @see net.refractions.udig.project.Layer#setCRS(org.opengis.referencing.crs.CoordinateReferenceSystem)
     */
    public void setCRS( CoordinateReferenceSystem value ) {
        layer.setCRS(value);
    }

    /*
     * @see net.refractions.udig.project.Layer#refresh(com.vividsolutions.jts.geom.Envelope)
     */
    public void refresh( Envelope bounds ) {
        layer.refresh(bounds);
    }

    /*
     * @see net.refractions.udig.project.Layer#getMathTransform()
     */
    public MathTransform layerToMapTransform() throws IOException {
        return layer.layerToMapTransform();
    }

    /*
     * @see net.refractions.udig.project.Layer#getBounds(org.eclipse.core.runtime.IProgressMonitor,
     *      org.opengis.referencing.crs.CoordinateReferenceSystem)
     */
    public ReferencedEnvelope getBounds( IProgressMonitor monitor, CoordinateReferenceSystem crs ) {
        return layer.getBounds(monitor, crs);
    }

    /*
     * @see net.refractions.udig.project.Layer#createBBoxFilter(com.vividsolutions.jts.geom.Envelope)
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
     * @see net.refractions.udig.project.internal.Layer#getMap()
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
     * @see net.refractions.udig.project.ILayer#getGeoResource()
     */
    public IGeoResource getGeoResource() {
        return layer.getGeoResource();
    }

    /**
     * @see net.refractions.udig.project.internal.Layer#setGeoResource(net.refractions.udig.catalog.IGeoResource)
     * @deprecated
     */
    public void setGeoResource( IGeoResource value ) {
        layer.setGeoResource(value);
    }

    /**
     * @see net.refractions.udig.project.ILayer#getGeoResource(java.lang.Class)
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
     * @see net.refractions.udig.project.ILayer#getGeoResource(java.lang.Class)
     * @deprecated
     */
    public <T> IGeoResource getGeoResource( Class<T> clazz ) {
        return layer.getGeoResource(clazz);
    }

    /**
     * @see net.refractions.udig.project.ILayer#getMap()
     */
    public Map getMap() {
        return layer.getMap();
    }

    @Override
    public void setMap( Map value ) {
        layer.setMap(value);
    }
    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class adapter ) {
        return layer.getAdapter(adapter);
    }

    /**
     * @see net.refractions.udig.core.IBlockingAdaptable#getAdapter(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T getAdapter( Class<T> adapter, IProgressMonitor monitor ) throws IOException {
        return layer.getAdapter(adapter, monitor);
    }

    /**
     * @see net.refractions.udig.core.IBlockingAdaptable#canAdaptTo(java.lang.Class)
     */
    public <T> boolean canAdaptTo( Class<T> adapter ) {
        return layer.canAdaptTo(adapter);
    }

    /**
     * @see net.refractions.udig.project.internal.Layer#getColourScheme()
     */
    public ColourScheme getColourScheme() {
        return layer.getColourScheme();
    }

    /**
     * @see net.refractions.udig.project.internal.Layer#setColourScheme(net.refractions.udig.ui.palette.ColourScheme)
     */
    public void setColourScheme( ColourScheme value ) {
        layer.setColourScheme(value);
    }

    /**
     * @see net.refractions.udig.project.internal.Layer#getDefaultColor()
     */
    public Color getDefaultColor() {
        return layer.getDefaultColor();
    }

    /**
     * @see net.refractions.udig.project.internal.Layer#setDefaultColor(java.awt.Color)
     */
    public void setDefaultColor( Color value ) {
        layer.setDefaultColor(value);
    }

    /**
     * @see net.refractions.udig.project.ILayer#mapToLayerTransform()
     */
    public MathTransform mapToLayerTransform() throws IOException {
        return layer.mapToLayerTransform();
    }

    /**
     * @see net.refractions.udig.project.internal.Layer#setStatusMessage(java.lang.String)
     */
    public void setStatusMessage( String message ) {
        layer.setStatusMessage(message);
    }

    /**
     * @see net.refractions.udig.project.ILayer#getStatusMessage()
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
