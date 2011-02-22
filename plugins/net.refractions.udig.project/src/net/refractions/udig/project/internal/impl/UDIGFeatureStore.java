package net.refractions.udig.project.internal.impl;

import java.io.IOException;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.ProjectPlugin;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.feature.AttributeType;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;

/**
 * A FeatureStore decorator that does not allow the transaction to be set more than once. (Only if
 * the current transaction is "AUTO_COMMIT" can the transaction be set)
 *
 * @author jones
 * @since 1.0.0
 */
public class UDIGFeatureStore implements FeatureStore {

    FeatureStore wrapped;
    ILayer layer;

    /**
     * Create a new FeatureStore decorator that does not allow the transaction to be set more than
     * once. (Only if the current transaction is "AUTO_COMMIT" can the transaction be set)
     *
     * @param store the feature store that will be decorated
     * @param layer TODO
     */
    public UDIGFeatureStore( FeatureStore store, ILayer layer ) {
        wrapped = store;
        this.layer = layer;

    }

    @SuppressWarnings({"deprecation", "unchecked"})
    public Set addFeatures( FeatureReader arg0 ) throws IOException {
        setTransactionInternal();
        return wrapped.addFeatures(arg0);
    }

    public void removeFeatures( Filter arg0 ) throws IOException {
        setTransactionInternal();
        wrapped.removeFeatures(arg0);
    }

    public void modifyFeatures( AttributeType[] arg0, Object[] arg1, Filter arg2 )
            throws IOException {
        setTransactionInternal();
        wrapped.modifyFeatures(arg0, arg1, arg2);
    }

    public void modifyFeatures( AttributeType arg0, Object arg1, Filter arg2 ) throws IOException {
        setTransactionInternal();
        wrapped.modifyFeatures(arg0, arg1, arg2);
    }

    public void setFeatures( FeatureReader arg0 ) throws IOException {
        setTransactionInternal();
        wrapped.setFeatures(arg0);
    }

    public void setTransaction( Transaction arg0 ) {
        throw new IllegalArgumentException(Messages.UDIGFeatureStore_0
                + Messages.UDIGFeatureStore_1);
        // do nothing
    }

    protected void editComplete() {
        wrapped.setTransaction(Transaction.AUTO_COMMIT);
    }

    private void setTransactionInternal() {
        if (!layer.isApplicable(ProjectBlackboardConstants.LAYER__EDIT_APPLICABILITY)) {
            ProjectPlugin.log("Attempted to open a transaction on a non-editable layer (Aborted)", //$NON-NLS-1$
                    new Exception());
        }
        if (wrapped.getTransaction() == Transaction.AUTO_COMMIT) {
            Transaction transaction = ((EditManager) layer.getMap().getEditManager())
                    .getTransaction();
            wrapped.setTransaction(transaction);
        }
    }

    /**
     * Vitalus: Think out how to provide for developers the opportunity to use its own FeatureStore
     * wrapper, not UDIGFeatureStore.
     */
    public void startTransaction() {
        if (wrapped.getTransaction() == Transaction.AUTO_COMMIT) {
            Transaction transaction = ((EditManager) layer.getMap().getEditManager())
                    .getTransaction();
            wrapped.setTransaction(transaction);
        }
    }

    public Transaction getTransaction() {
        return wrapped.getTransaction();
    }

    public DataStore getDataStore() {
        return wrapped.getDataStore();
    }

    public void addFeatureListener( FeatureListener arg0 ) {
        wrapped.addFeatureListener(arg0);
    }

    public void removeFeatureListener( FeatureListener arg0 ) {
        wrapped.removeFeatureListener(arg0);
    }

    public FeatureCollection getFeatures( Query arg0 ) throws IOException {
        return wrapped.getFeatures(arg0);
    }

    public FeatureCollection getFeatures( Filter arg0 ) throws IOException {
        return wrapped.getFeatures(arg0);
    }

    public FeatureCollection getFeatures() throws IOException {
        return wrapped.getFeatures();
    }

    public FeatureType getSchema() {
        return wrapped.getSchema();
    }

    public Envelope getBounds() throws IOException {
        return wrapped.getBounds();
    }

    public Envelope getBounds( Query arg0 ) throws IOException {
        return wrapped.getBounds(arg0);
    }

    public int getCount( Query arg0 ) throws IOException {
        return wrapped.getCount(arg0);
    }

    @SuppressWarnings("unchecked")
    public Set addFeatures( FeatureCollection arg0 ) throws IOException {
        setTransactionInternal();
        return wrapped.addFeatures(arg0);
    }

    public boolean sameSource( FeatureSource source ) {
        return source == wrapped || source == this;
    }
}
