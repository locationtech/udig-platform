/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.impl;

import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.ResourceInfo;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.LayerEvent;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;

/**
 * A SimpleFeatureStore decorator that does not allow the transaction to be set more than once.
 * <p>
 * Only if the current transaction is "AUTO_COMMIT" can the transaction be set.
 * </p>
 *
 * @author Jody Garnett
 * @since 1.2.1
 * @see UDIGFeatureStore
 */
public class UDIGSimpleFeatureStore implements SimpleFeatureStore, UDIGStore {
    SimpleFeatureStore wrapped;

    ILayer layer;

    /**
     * Create a new FeatureStore decorator that does not allow the transaction to be set more than
     * once. (Only if the current transaction is "AUTO_COMMIT" can the transaction be set)
     *
     * @param store the feature store that will be decorated
     * @param layer layer providing context
     */
    public UDIGSimpleFeatureStore(SimpleFeatureStore featureStore, ILayer layer) {
        wrapped = featureStore;
        this.layer = layer;
    }

    public UDIGSimpleFeatureStore(FeatureStore<?, ?> featureStore, ILayer layer) {
        wrapped = DataUtilities.simple(featureStore);
        this.layer = layer;
    }

    @Override
    public Name getName() {
        return wrapped.getName();
    }

    @Override
    public ResourceInfo getInfo() {
        return wrapped.getInfo();
    }

    @Override
    public void removeFeatures(Filter filter) throws IOException {
        setTransactionInternal();
        wrapped.removeFeatures(filter);

        fireLayerEditEvent(FeatureEvent.Type.REMOVED, null, filter);
    }

    @Deprecated
    public void modifyFeatures(AttributeDescriptor[] descriptors, Object[] values, Filter filter)
            throws IOException {
        setTransactionInternal();
        Name[] names = new Name[descriptors.length];
        for (int i = 0; i < names.length; i++)
            names[i] = descriptors[i].getName();
        wrapped.modifyFeatures(names, values, filter);
        fireLayerEditEvent(FeatureEvent.Type.CHANGED, null, filter);
    }

    @Override
    public void modifyFeatures(Name[] names, Object[] values, Filter filter) throws IOException {
        setTransactionInternal();
        wrapped.modifyFeatures(names, values, filter);
        fireLayerEditEvent(FeatureEvent.Type.CHANGED, null, filter);
    }

    @Override
    public void modifyFeatures(Name name, Object value, Filter filter) throws IOException {
        setTransactionInternal();
        wrapped.modifyFeatures(name, value, filter);
        fireLayerEditEvent(FeatureEvent.Type.CHANGED, null, filter);
    }

    @Override
    public void modifyFeatures(String name, Object value, Filter filter) throws IOException {
        setTransactionInternal();
        wrapped.modifyFeatures(name, value, filter);
        fireLayerEditEvent(FeatureEvent.Type.CHANGED, null, filter);
    }

    @Override
    public void modifyFeatures(String names[], Object values[], Filter filter) throws IOException {
        setTransactionInternal();
        wrapped.modifyFeatures(names, values, filter);
        fireLayerEditEvent(FeatureEvent.Type.CHANGED, null, filter);
    }

    @Deprecated
    public void modifyFeatures(AttributeDescriptor attribute, Object value, Filter selectFilter)
            throws IOException {
        setTransactionInternal();
        if (value instanceof Geometry) {
            Geometry geom = (Geometry) value;
            if (!geom.isValid()) {
                WKTWriter writer = new WKTWriter();
                String wkt = writer.write(geom);
                String where = selectFilter.toString();
                if (selectFilter instanceof Id) {
                    Id id = (Id) selectFilter;
                    where = id.getIDs().toString();
                }
                String msg = "Modify fetures (WHERE " + where + ") failed with invalid geometry:"
                        + wkt;
                ProjectPlugin.log(msg);
                throw new IOException(msg);
            }
        }
        wrapped.modifyFeatures(attribute.getName(), value, selectFilter);
        fireLayerEditEvent(FeatureEvent.Type.CHANGED, null, selectFilter);
    }

    /**
     * Used to force the layer to send out an LayerEditEvent (and refresh!); we are faking the
     * correct FeatureEventType we expected from the wrapped GeoTools datastore. This is defensive
     * programming as we are not trusting the implementations to provide good events.
     *
     * @param type
     * @param bounds
     * @param filter
     */
    public void fireLayerEditEvent(FeatureEvent.Type type, ReferencedEnvelope bounds,
            Filter filter) {
        // issue edit event for TableView and any other interested parties
        if (type == null) {
            type = FeatureEvent.Type.CHANGED;
        }
        FeatureEvent featureEvent = new FeatureEvent(this, type, bounds, filter);
        ((LayerImpl) layer).fireLayerChange(
                new LayerEvent(layer, LayerEvent.EventType.EDIT_EVENT, null, featureEvent));
        layer.refresh(bounds);
    }

    @Override
    public void setFeatures(FeatureReader<SimpleFeatureType, SimpleFeature> features)
            throws IOException {
        setTransactionInternal();
        wrapped.setFeatures(features);
        fireLayerEditEvent(FeatureEvent.Type.CHANGED, null, Filter.INCLUDE);
    }

    @Override
    public void setTransaction(Transaction transaction) {
        throw new IllegalArgumentException(
                Messages.UDIGFeatureStore_0 + Messages.UDIGFeatureStore_1);

    }

    /**
     * Called when commitRollbackCompleted to restore Transaction.AUTO_COMMIT
     */
    @Override
    public void editComplete() {
        wrapped.setTransaction(Transaction.AUTO_COMMIT);
    }

    /**
     * Called when any method that may modify feature content is used.
     * <p>
     * This method is responsible for setting the transaction prior to use.
     * </p>
     */
    private void setTransactionInternal() {
        if (!layer.getInteraction(Interaction.EDIT)) {
            String message = "Attempted to open a transaction on a non-editable layer (Aborted)";
            IllegalStateException illegalStateException = new IllegalStateException(message);
            ProjectPlugin.log(message, illegalStateException);
            throw illegalStateException;
        }
        // grab the current map transaction
        EditManager editManager = (EditManager) layer.getMap().getEditManager();
        Transaction transaction = editManager.getTransaction();

        if (wrapped.getTransaction() == null
                || wrapped.getTransaction() == Transaction.AUTO_COMMIT) {
            // change over from autocommit to transactional
            wrapped.setTransaction(transaction);
        } else if (wrapped.getTransaction() != transaction) {
            // a transaction is already present? huh ...
            String msg = "Layer transaction already set " + wrapped.getTransaction(); //$NON-NLS-1$
            IllegalStateException illegalStateException = new IllegalStateException(msg);
            ProjectPlugin.log(msg, illegalStateException);
            throw illegalStateException;
        }
    }

    /**
     * Used to start a transaction.
     * <p>
     * Q: (Vitalus) Think out how to provide for developers the opportunity to use its own
     * FeatureStore wrapper, not UDIGFeatureStore.
     * </p>
     * <p>
     * A: (Jody) They can use the id; and grab the actual IResource from the catalog; and get there
     * own that way.
     * </p>
     */
    @Override
    public void startTransaction() {
        if (wrapped.getTransaction() == Transaction.AUTO_COMMIT) {
            Transaction transaction = ((EditManager) layer.getMap().getEditManager())
                    .getTransaction();
            wrapped.setTransaction(transaction);
        }
    }

    @Override
    public Transaction getTransaction() {
        // may need to check that this is not auto commit?
        return wrapped.getTransaction();
    }

    @Override
    public DataStore getDataStore() {
        return (DataStore) wrapped.getDataStore();
    }

    @Override
    public void addFeatureListener(FeatureListener listener) {
        wrapped.addFeatureListener(listener);
    }

    @Override
    public void removeFeatureListener(FeatureListener listener) {
        wrapped.removeFeatureListener(listener);
    }

    @Override
    public SimpleFeatureCollection getFeatures(Query query) throws IOException {
        return wrapped.getFeatures(query);
    }

    @Override
    public SimpleFeatureCollection getFeatures(Filter filter) throws IOException {
        return wrapped.getFeatures(filter);
    }

    @Override
    public SimpleFeatureCollection getFeatures() throws IOException {
        return wrapped.getFeatures();
    }

    @Override
    public SimpleFeatureType getSchema() {
        return wrapped.getSchema();
    }

    @Override
    public ReferencedEnvelope getBounds() throws IOException {
        return wrapped.getBounds();
    }

    @Override
    public ReferencedEnvelope getBounds(Query query) throws IOException {
        return wrapped.getBounds(query);
    }

    @Override
    public int getCount(Query query) throws IOException {
        return wrapped.getCount(query);
    }

    @Override
    public List<FeatureId> addFeatures(FeatureCollection<SimpleFeatureType, SimpleFeature> features)
            throws IOException {
        setTransactionInternal();
        List<FeatureId> ids = wrapped.addFeatures(features);

        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        Id filter = ff.id(new HashSet<>(ids));

        fireLayerEditEvent(FeatureEvent.Type.CHANGED, null, Filter.INCLUDE);

        return ids;
    }

    @Override
    public boolean sameSource(Object source) {
        return source == wrapped || source == this;
    }

    @Override
    public SimpleFeatureStore wrapped() {
        return wrapped;
    }

    @Override
    public Set<Key> getSupportedHints() {
        return wrapped.getSupportedHints();
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return wrapped.getQueryCapabilities();
    }
}
