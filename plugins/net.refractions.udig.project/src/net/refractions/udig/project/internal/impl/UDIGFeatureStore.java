package net.refractions.udig.project.internal.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.ProjectPlugin;

import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.ResourceInfo;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * A FeatureStore decorator that does not allow the transaction to be set more than once. (Only if
 * the current transaction is "AUTO_COMMIT" can the transaction be set)
 * 
 * @author jones
 * @since 1.0.0
 */
public class UDIGFeatureStore implements SimpleFeatureStore {

    SimpleFeatureStore wrapped;
    ILayer layer;

    /**
     * Create a new FeatureStore decorator that does not allow the transaction to be set more than
     * once. (Only if the current transaction is "AUTO_COMMIT" can the transaction be set)
     * 
     * @param store the feature store that will be decorated
     * @param layer TODO
     */
    public UDIGFeatureStore( FeatureStore<SimpleFeatureType, SimpleFeature> store, ILayer layer ) {
        wrapped = DataUtilities.simple(store);
        this.layer = layer;

    }

    public Name getName() {
        return wrapped.getName();
    }

    public ResourceInfo getInfo() {
        return wrapped.getInfo();
    }

    public void removeFeatures( Filter filter ) throws IOException {
        setTransactionInternal();
        wrapped.removeFeatures(filter);
    }

    public void modifyFeatures( AttributeDescriptor[] descriptors, Object[] values, Filter filter )
            throws IOException {
        setTransactionInternal();
        wrapped.modifyFeatures(descriptors, values, filter);
    }
    
    public void modifyFeatures( Name[] names, Object[] values, Filter filter ) throws IOException {
        setTransactionInternal();
        wrapped.modifyFeatures(names, values, filter);
    }
    public void modifyFeatures( Name name, Object value, Filter filter ) throws IOException {
        setTransactionInternal();
        wrapped.modifyFeatures(name, value, filter);
    }
    public void modifyFeatures( String name, Object value, Filter filter ) throws IOException {
        setTransactionInternal();
        wrapped.modifyFeatures(name, value, filter);
    } 
    public void modifyFeatures( String names[], Object values[], Filter filter ) throws IOException {
        setTransactionInternal();
        wrapped.modifyFeatures(names, values, filter);
    }
    public void modifyFeatures( AttributeDescriptor arg0, Object arg1, Filter arg2 )
            throws IOException {
        setTransactionInternal();
        if (arg1 instanceof Geometry) {
            Geometry geom = (Geometry) arg1;
            if (!geom.isValid()) {
                WKTWriter writer = new WKTWriter();
                String wkt = writer.write(geom);
                String where = arg2.toString();
                if (arg2 instanceof Id) {
                    Id id = (Id) arg2;
                    where = id.getIDs().toString();
                }
                String msg = "Modify fetures (WHERE " + where + ") failed with invalid geometry:"
                        + wkt;
                ProjectPlugin.log(msg);
                throw new IOException(msg);
            }
        }
        wrapped.modifyFeatures(arg0, arg1, arg2);
    }

    public void setFeatures( FeatureReader<SimpleFeatureType, SimpleFeature> arg0 )
            throws IOException {
        setTransactionInternal();
        wrapped.setFeatures(arg0);
    }

    public void setTransaction( Transaction arg0 ) {
        throw new IllegalArgumentException(Messages.UDIGFeatureStore_0
                + Messages.UDIGFeatureStore_1);
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
        return (DataStore) wrapped.getDataStore();
    }

    public void addFeatureListener( FeatureListener arg0 ) {
        wrapped.addFeatureListener(arg0);
    }

    public void removeFeatureListener( FeatureListener arg0 ) {
        wrapped.removeFeatureListener(arg0);
    }

    public SimpleFeatureCollection getFeatures( Query arg0 )
            throws IOException {
        return wrapped.getFeatures(arg0);
    }

    public SimpleFeatureCollection getFeatures( Filter arg0 )
            throws IOException {
        return wrapped.getFeatures(arg0);
    }

    public SimpleFeatureCollection getFeatures() throws IOException {
        return wrapped.getFeatures();
    }

    public SimpleFeatureType getSchema() {
        return wrapped.getSchema();
    }

    public ReferencedEnvelope getBounds() throws IOException {
        return wrapped.getBounds();
    }

    public ReferencedEnvelope getBounds( Query arg0 ) throws IOException {
        return wrapped.getBounds(arg0);
    }

    public int getCount( Query arg0 ) throws IOException {
        return wrapped.getCount(arg0);
    }

    public List<FeatureId> addFeatures( FeatureCollection<SimpleFeatureType, SimpleFeature> arg0 )
            throws IOException {
        setTransactionInternal();
        return wrapped.addFeatures(arg0);
    }

    public boolean sameSource( FeatureSource<SimpleFeatureType, SimpleFeature> source ) {
        return source == wrapped || source == this;
    }

    public Set getSupportedHints() {
        return Collections.EMPTY_SET;
    }

    public QueryCapabilities getQueryCapabilities() {
        return wrapped.getQueryCapabilities();
    }
}