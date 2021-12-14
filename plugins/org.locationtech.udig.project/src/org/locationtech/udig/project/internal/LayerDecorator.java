/**
 * uDig - User Friendly Desktop Internet GIS client
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
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.data.FeatureEvent;
import org.geotools.data.Query;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.Range;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolveChangeEvent;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILayerListener;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.LayerEvent;
import org.locationtech.udig.ui.palette.ColourScheme;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * Wraps a layer and delegates all the method calls to that layer. See the gang of four decorator
 * pattern.
 *
 * @author Jesse
 * @since 1.0.0
 */
public class LayerDecorator implements Layer, InternalEObject {

    final protected Layer layer;

    final private InternalEObject interalObject;

    /**
     * Listeners to this layer.
     * <p>
     * Note this will need to be hooked into the usual EMF adapater mechasim.
     * </p>
     */
    CopyOnWriteArraySet<ILayerListener> listeners = new CopyOnWriteArraySet<>();

    ILayerListener watcher = new ILayerListener() {
        @Override
        public void refresh(LayerEvent event) {
            LayerEvent myEvent = new LayerEvent(LayerDecorator.this, event.getType(),
                    event.getOldValue(), event.getNewValue());
            fireLayerChange(myEvent);
        }
    };

    /**
     * Construct <code>LayerDecorator</code>.
     *
     * @param layer
     */
    public LayerDecorator(Layer layer) {
        this.layer = layer;
        this.interalObject = (InternalEObject) layer;
    }

    @Override
    public synchronized void addListener(final ILayerListener listener) {
        if (listeners == null) {
            listeners = new CopyOnWriteArraySet<>();
            layer.addListener(watcher);
        }
        listeners.add(listener);
    }

    @Override
    public synchronized void removeListener(final ILayerListener listener) {
        listeners.remove(listener);
        if (listeners.size() == 0) {
            layer.removeListener(watcher);
            listeners = null;
        }
    }

    protected synchronized void fireLayerChange(LayerEvent event) {
        if (listeners.size() == 0) {
            return; // nobody is listening
        }
        for (ILayerListener listener : listeners) {
            try {
                listener.refresh(event);
            } catch (Throwable t) {
                ProjectPlugin.log("Error in listener:" + listener, t); //$NON-NLS-1$
            }
        }
    }

    @Override
    public IBlackboard getProperties() {
        return layer.getProperties();
    }

    @Override
    public ContextModel getContextModel() {
        return layer.getContextModel();
    }

    @Override
    public void setContextModel(ContextModel value) {
        layer.setContextModel(value);
    }

    @Override
    public Filter getFilter() {
        return layer.getFilter();
    }

    @Override
    public void setFilter(Filter value) {
        layer.setFilter(value);
    }

    @Override
    public StyleBlackboard getStyleBlackboard() {
        return layer.getStyleBlackboard();
    }

    @Override
    public void setStyleBlackboard(StyleBlackboard value) {
        layer.setStyleBlackboard(value);
    }

    @Override
    public int getZorder() {
        return layer.getZorder();
    }

    @Override
    public void setZorder(int value) {
        layer.setZorder(value);
    }

    @Override
    public int getStatus() {
        return layer.getStatus();
    }

    @Override
    public void setStatus(int value) {
        layer.setStatus(value);
    }

    @Override
    public boolean getInteraction(Interaction interaction) {
        return layer.getInteraction(interaction);
    }

    @Override
    public void setInteraction(Interaction interaction, boolean isApplicable) {
        layer.setInteraction(interaction, isApplicable);
    }

    @Override
    public String getName() {
        return layer.getName();
    }

    @Override
    public void setName(String value) {
        layer.setName(value);
    }

    @Override
    public CatalogRef getCatalogRef() {
        return layer.getCatalogRef();
    }

    @Override
    public void setCatalogRef(CatalogRef value) {
        layer.setCatalogRef(value);
    }

    @Override
    public URL getID() {
        return layer.getID();
    }

    @Override
    public void setID(URL value) {
        layer.setID(value);
    }

    @Override
    public boolean isVisible() {
        return layer.isVisible();
    }

    @Override
    public void setVisible(boolean value) {
        layer.setVisible(value);
    }

    @Override
    public List<IGeoResource> getGeoResources() {
        return layer.getGeoResources();
    }

    @Override
    public ImageDescriptor getIcon() {
        return layer.getIcon();
    }

    @Override
    public void setIcon(ImageDescriptor value) {
        layer.setIcon(value);
    }

    @Override
    public Query getQuery(boolean selection) {
        return layer.getQuery(selection);
    }

    @Override
    public SimpleFeatureType getSchema() {
        return layer.getSchema();
    }

    @Override
    public CoordinateReferenceSystem getCRS() {
        return layer.getCRS();
    }

    @Override
    public void setCRS(CoordinateReferenceSystem value) {
        layer.setCRS(value);
    }

    @Override
    public void refresh(Envelope bounds) {
        layer.refresh(bounds);
    }

    @Override
    public MathTransform layerToMapTransform() throws IOException {
        return layer.layerToMapTransform();
    }

    @Override
    public ReferencedEnvelope getBounds(IProgressMonitor monitor, CoordinateReferenceSystem crs) {
        return layer.getBounds(monitor, crs);
    }

    @Override
    public Filter createBBoxFilter(Envelope boundingBox, IProgressMonitor monitor) {
        return layer.createBBoxFilter(boundingBox, monitor);
    }

    @Override
    public EClass eClass() {
        return layer.eClass();
    }

    @Override
    public Resource eResource() {
        return layer.eResource();
    }

    @Override
    public EObject eContainer() {
        return layer.eContainer();
    }

    @Override
    public EStructuralFeature eContainingFeature() {
        return layer.eContainingFeature();
    }

    @Override
    public EReference eContainmentFeature() {
        return layer.eContainmentFeature();
    }

    @Override
    public EList eContents() {
        return layer.eContents();
    }

    @Override
    public TreeIterator eAllContents() {
        return layer.eAllContents();
    }

    @Override
    public boolean eIsProxy() {
        return layer.eIsProxy();
    }

    @Override
    public EList eCrossReferences() {
        return layer.eCrossReferences();
    }

    @Override
    public Object eGet(EStructuralFeature feature) {
        return layer.eGet(feature);
    }

    @Override
    public Object eGet(EStructuralFeature feature, boolean resolve) {
        return layer.eGet(feature, resolve);
    }

    @Override
    public void eSet(EStructuralFeature feature, Object newValue) {
        layer.eSet(feature, newValue);
    }

    @Override
    public boolean eIsSet(EStructuralFeature feature) {
        return layer.eIsSet(feature);
    }

    @Override
    public void eUnset(EStructuralFeature feature) {
        layer.eUnset(feature);
    }

    @Override
    public EList eAdapters() {
        return layer.eAdapters();
    }

    @Override
    public boolean eDeliver() {
        return layer.eDeliver();
    }

    @Override
    public void eSetDeliver(boolean deliver) {
        layer.eSetDeliver(deliver);
    }

    @Override
    public void eNotify(Notification notification) {
        layer.eNotify(notification);
    }

    @Override
    public int compareTo(ILayer other) {
        return layer.compareTo(other);
    }

    @Override
    public Map getMapInternal() {
        return layer.getMapInternal();
    }

    @Override
    public boolean eNotificationRequired() {
        return interalObject.eNotificationRequired();
    }

    @Override
    public String eURIFragmentSegment(EStructuralFeature eFeature, EObject eObject) {
        return interalObject.eURIFragmentSegment(eFeature, eObject);
    }

    @Override
    public EObject eObjectForURIFragmentSegment(String uriFragmentSegment) {
        return interalObject.eObjectForURIFragmentSegment(uriFragmentSegment);
    }

    @Override
    public void eSetClass(EClass eClass) {
        interalObject.eSetClass(eClass);
    }

    @Override
    public Setting eSetting(EStructuralFeature feature) {
        return interalObject.eSetting(feature);
    }

    @Override
    public int eContainerFeatureID() {
        return interalObject.eContainerFeatureID();
    }

    @Override
    public NotificationChain eSetResource(Internal resource, NotificationChain notifications) {
        return interalObject.eSetResource(resource, notifications);
    }

    @Override
    public NotificationChain eBasicSetContainer(InternalEObject newContainer,
            int newContainerFeatureID, NotificationChain notifications) {
        return interalObject.eBasicSetContainer(newContainer, newContainerFeatureID, notifications);
    }

    @Override
    public NotificationChain eBasicRemoveFromContainer(NotificationChain notifications) {
        return interalObject.eBasicRemoveFromContainer(notifications);
    }

    @Override
    public URI eProxyURI() {
        return interalObject.eProxyURI();
    }

    @Override
    public void eSetProxyURI(URI uri) {
        interalObject.eSetProxyURI(uri);
    }

    @Override
    public EObject eResolveProxy(InternalEObject proxy) {
        return interalObject.eResolveProxy(proxy);
    }

    @Override
    public Internal eInternalResource() {
        return interalObject.eInternalResource();
    }

    @Override
    public EStore eStore() {
        return interalObject.eStore();
    }

    @Override
    public void eSetStore(EStore store) {
        interalObject.eSetStore(store);
    }

    @Override
    public IGeoResource getGeoResource() {
        return layer.getGeoResource();
    }

    @Deprecated
    @Override
    public void setGeoResource(IGeoResource value) {
        layer.setGeoResource(value);
    }

    @Override
    public <E> E getResource(Class<E> resourceType, IProgressMonitor monitor) throws IOException {
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

    @Override
    public IMap getMap() {
        return layer.getMap();
    }

    @Override
    public Object getAdapter(Class adapter) {
        return layer.getAdapter(adapter);
    }

    @Override
    public <T> T getAdapter(Class<T> adapter, IProgressMonitor monitor) throws IOException {
        return layer.getAdapter(adapter, monitor);
    }

    @Override
    public <T> boolean canAdaptTo(Class<T> adapter) {
        return layer.canAdaptTo(adapter);
    }

    @Override
    public ColourScheme getColourScheme() {
        return layer.getColourScheme();
    }

    @Override
    public void setColourScheme(ColourScheme value) {
        layer.setColourScheme(value);
    }

    @Override
    public Color getDefaultColor() {
        return layer.getDefaultColor();
    }

    @Override
    public void setDefaultColor(Color value) {
        layer.setDefaultColor(value);
    }

    @Override
    public MathTransform mapToLayerTransform() throws IOException {
        return layer.mapToLayerTransform();
    }

    @Override
    public void setStatusMessage(String message) {
        layer.setStatusMessage(message);
    }

    @Override
    public String getStatusMessage() {
        return layer.getStatusMessage();
    }

    @Override
    public void changed(IResolveChangeEvent event) {
        // do nothing. We don't want to give the layer the same event twice.
    }

    @Override
    public List<FeatureEvent> getFeatureChanges() {
        return layer.getFeatureChanges();
    }

    @Override
    public double getMinScaleDenominator() {
        return layer.getMinScaleDenominator();
    }

    @Override
    public double getMaxScaleDenominator() {
        return layer.getMaxScaleDenominator();
    }

    @Override
    public void setMinScaleDenominator(double value) {
        layer.setMinScaleDenominator(value);
    }

    @Override
    public void setMaxScaleDenominator(double value) {
        layer.setMaxScaleDenominator(value);
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public <T> boolean isType(Class<T> resourceType) {
        return layer.hasResource(resourceType);
    }

    @Override
    public <T> boolean hasResource(Class<T> resourceType) {
        return layer.hasResource(resourceType);
    }

    @Override
    public Internal eDirectResource() {
        return interalObject.eDirectResource();
    }

    @Override
    public Object eGet(EStructuralFeature arg0, boolean arg1, boolean arg2) {
        return interalObject.eGet(arg0, arg1, arg2);
    }

    @Override
    public Object eGet(int arg0, boolean arg1, boolean arg2) {
        return interalObject.eGet(arg0, arg1, arg2);
    }

    @Override
    public InternalEObject eInternalContainer() {
        return interalObject.eInternalContainer();
    }

    @Override
    public boolean eIsSet(int arg0) {
        return interalObject.eIsSet(arg0);
    }

    @Override
    public void eSet(int arg0, Object arg1) {
        interalObject.eSet(arg0, arg1);
    }

    @Override
    public void eUnset(int arg0) {
        interalObject.eUnset(arg0);
    }

    @Override
    public <T> IGeoResource findGeoResource(Class<T> clazz) {
        return layer.findGeoResource(clazz);
    }

    @Override
    public IBlackboard getBlackboard() {
        return layer.getBlackboard();
    }

    @Override
    public void setBounds(ReferencedEnvelope bounds) {
        layer.setBounds(bounds);
    }

    @Override
    public Set<Range> getScaleRange() {
        return layer.getScaleRange();
    }

    @Override
    public Object eInvoke(EOperation operation, EList<?> arguments)
            throws InvocationTargetException {
        return layer.eInvoke(operation, arguments);
    }

    @Override
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
        return this.interalObject.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    @Override
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
        return interalObject.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }

    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID,
            Class<?> baseClass, NotificationChain notifications) {
        return interalObject.eInverseAdd(otherEnd, featureID, baseClass, notifications);
    }

    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID,
            Class<?> baseClass, NotificationChain notifications) {
        return interalObject.eInverseRemove(otherEnd, featureID, baseClass, notifications);
    }

    @Override
    public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
        return interalObject.eInvoke(operationID, arguments);
    }

    @Override
    public int eDerivedOperationID(int baseOperationID, Class<?> baseClass) {
        return interalObject.eDerivedOperationID(baseOperationID, baseClass);
    }

    @Override
    public java.util.Map<Interaction, Boolean> getInteractionMap() {
        return layer.getInteractionMap();
    }

    @Override
    public boolean isShown() {
        return false;
    }

    @Override
    public void setShown(boolean value) {

    }

}
