/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.merge.internal.view;

import java.util.Collections;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.locationtech.udig.project.ILayer;

import org.geotools.data.DataUtilities;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import org.locationtech.udig.tools.geometry.internal.util.GeometryUtil;

/**
 * A builder for a {@link Feature} that allows to select which attributes from a
 * set of source features the new Feature is going to be composed of.
 * <p>
 * The merge feature is initialized with the values of the first feature found
 * in the provided {@link FeatureCollection}. If a precomputed merged geometry
 * is provided, the default geometry in the merged feature will hold that value,
 * otherwise it'll hold the geometry from the first Feature.
 * </p>
 * <p>
 * Sample usage:
 * 
 * <pre>
 * &lt;code&gt;
 * FeatureCollection features = &lt;get desired source features&gt;...
 * Geometry mergedGeometry = &lt;get merged geometry from features&gt;...
 * MergeFeatureBuilder builder = new MergeFeatureBuilder(features, union);
 * 
 * //set the first attribute of the merge feature to be the first attribute
 * //of the third source feature
 * int featureIndex = 2;
 * int attributeIndex = 0;
 * builder.copyAttributeToMerge(featureIndex, attributeIndex);
 * .... repeat as desired
 * //set the 3th attribute of the merge feature to null
 * builder.clearMergeAttribute(2);
 * 
 * // check if the geometry of the merge feature is the 
 * // unioned one provided at the constructor
 * boolean isGeomUnion = builder.mergeGeomIsUnion();
 * 
 * // set all the attributes of the merge feature to be the ones
 * // of the last source feature
 * int fcount = builder.getFeatureCount();
 * int attCount = builder.getAttributeCount();
 * int fIndex = fount - 1;
 * for(int attIndex = 0; attIndex &lt; attCount; attIndex++){
 *  builder.setMergeAttribute(fIndex, attIndex);
 * }
 * 
 * //get the resulting Feature
 * try{
 *  Feature mergedFeature = builder.buildMergeFeature();
 * }catch(IllegalAttributeException e){
 *  //got an invalid attribute (may be a non nillable one got null?)
 *  LOGGER.log(Level.WARNING, &quot;Failed to create merge feature&quot;, e);
 * }
 * &lt;/code&gt;
 * </pre>
 * 
 * </p>
 * <p>
 * Among being able to set the merge geometry attributes from the indexes of the
 * source features and their attributes, this class also allows client code to
 * register a {@link ChangeListener} to be notified every time an attribute for
 * the merge feature is changed.
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @author Marco Foi (www.mcfoi.it)
 * @since 1.1.0
 */
class MergeFeatureBuilder {

	private EventListenerList			listeners		= new EventListenerList();
	private SimpleFeatureType			featureType;
	
	/** maintains a list of features. A feature cannot be two times in the list*/
	private List<SimpleFeature>			sourceFeatures = Collections.synchronizedList( new LinkedList<SimpleFeature>() );
	
	/** the attributes of the merge feature (in other words the feature to build)*/
	private Object[]					mergedFeature;

	/** Geometry if it have a null value the build executes the union operation in the source features*/
	private int							defaultGeometryIndex;
	
	/** the layer where the features will be merge (or working layer)*/
	private ILayer						layer;
	

	
	/**
	 * Creates a MergeFeatureBuilder that works over the provided 
	 * features and their geometries.
	 * @param layer The layer which contains the features.
	 */
	public MergeFeatureBuilder(ILayer layer) {
		
		assert layer != null;

		this.layer = layer;
		this.featureType = layer.getSchema();

		GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();
		if( geometryDescriptor == null){
			throw new IllegalStateException( "The layer schema does not contain a geometry descriptor"); //$NON-NLS-1$
		}
		this.mergedFeature = new Object[featureType.getAttributeCount()];
		this.defaultGeometryIndex = featureType.indexOf(geometryDescriptor.getName());		
	}
	
	/**
	 * Checks all features have got the same feature type
	 * @param sourceFeatures
	 * @return
	 */	
	private boolean compatibleFeatureType(List<SimpleFeature> sourceFeatures) {

		this.featureType = sourceFeatures.get(0).getFeatureType();

		for (int i = 0; i < sourceFeatures.size(); i++) {
			SimpleFeature next = sourceFeatures.get(i);
			if (featureType != next.getFeatureType()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Adds the feature list to the existent source features
	 * @param featureList
	 */
	public void addSourceFeature(List<SimpleFeature> featureList) {
		
		for (SimpleFeature feature : featureList) {
			addSourceFeature(feature);
		}
		
	}
	
	/**
	 * Adds the feature as last element. If the feature is present in the list it won't be added.
	 * 
	 * @param feature
	 * @return return the position of the added feature.  -1 will be returned because the feature had added previously.
	 */
	public int addSourceFeature(SimpleFeature feature) {
		
		if(this.sourceFeatures.contains(feature)){
			return -1;
		}
		if(this.sourceFeatures.isEmpty() ){
			// The porperty values of the first feature are used as default values for the merge feature
			setDefaultMergeValues(feature);
		}
		
		assert canMerge(feature): "this precondition should be evaluated before call this method"; //$NON-NLS-1$
		
		this.sourceFeatures.add(feature);

		assert compatibleFeatureType(sourceFeatures):"Features in the collection must conform to a common schema"; //$NON-NLS-1$

		return this.sourceFeatures.size()-1;
	}


	/**
	 * Uses the feature's values to set the merge feature. The geometry value is not set by
	 * this method.
	 * 
	 * @param feature the feature where the values will get to set the merge feature properties
	 */
	private void setDefaultMergeValues(SimpleFeature feature) {
		

		this.mergedFeature = feature.getAttributes().toArray();
		
		this.mergedFeature[getDefaultGeometryIndex()] = null;
	}


	/**
	 * Event listener interface for implementors to listen to merge attribute
	 * changes
	 * 
	 * @author Mauricio Pazos (www.axios.es)
	 * @author Aritz Davila (www.axios.es)
	 * @since 1.1.0
	 * @see MergeFeatureBuilder#addChangeListener(MergeFeatureBuilder.ChangeListener)
	 * @see MergeFeatureBuilder#removeChangeListener(MergeFeatureBuilder.ChangeListener)
	 */
	public static interface ChangeListener extends EventListener {
		/**
		 * Called when an attribute value for the merged feature changes
		 * 
		 * @param builder
		 *            the {@link MergeFeatureBuilder} where the change occurred
		 * @param attributeIndex
		 *            the index of the attribute changed
		 * @param oldValue
		 *            the previous value of the attribute changed
		 */
		public void attributeChanged(MergeFeatureBuilder builder, int attributeIndex, Object oldValue);
	}

	/**
	 * Adds a listener for changes in the target feature attribute values
	 * 
	 * @see #copyAttributeToMerge(int, int)
	 * @see #clearMergeAttribute(int)
	 */
	public void addChangeListener(ChangeListener listener) {

		assert listener != null;

		listeners.add(ChangeListener.class, listener);
	}

	/**
	 * Removes a listener for changes in the target feature attribute values
	 */
	public void removeChangeListener(ChangeListener listener) {

		listeners.remove(ChangeListener.class, listener);
	}

	/**
	 * Builds the merged {@link Feature} from the build attributes
	 * 
	 * @return a new {@link Feature} of the same {@link FeatureType} than the
	 *         provided source features, built with the attribute values
	 *         established through this builders methods
	 * @throws IllegalStateException
	 *             if it is not possible to create the Feature with the current
	 *             list of attribute values
	 */
	public SimpleFeature buildMergedFeature() throws IllegalStateException {

		SimpleFeature feature;
		try {
			
			// sets the features attributes
			SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
			builder.addAll(mergedFeature);
			feature = builder.buildFeature(null); 

			// build the geometry
			Geometry geometry = buildMergeGeometry();
			feature.setDefaultGeometry(geometry);
			
		} catch (IllegalAttributeException e) {
			throw new IllegalStateException("Can't create merged feature: " + e.getMessage(), e); //$NON-NLS-1$
		}
		return feature;
	}

	/**
	 * Returns a printable geometry for the Union Geometry.
	 * 
	 * @return the original geometry passed as constructor argument to be used
	 *         as the merge geometry
	 */
	public String getPrittyMergeGeometry() {

		String printableGeom;
		if(isGeometriesUnion()){
		
			printableGeom = "Union"; //$NON-NLS-1$
		
		} else {
			
			printableGeom = mergedFeature[getDefaultGeometryIndex()].toString();
		}
		
		return printableGeom;
	}

	/**
	 * @return the attribute index of the default geometry
	 */
	public int getDefaultGeometryIndex() {
		assert defaultGeometryIndex >= 0;
		return defaultGeometryIndex;
	}

	/**
	 * Checks if any geometry was set in the merge feature. In other case the
	 * union of source geometries will be exectured. 
	 * 
	 * @return True if the merge resultant is the union of source geometries
	 */
	public boolean isGeometriesUnion() {

		int defaultGeometryIndex = getDefaultGeometryIndex();
		boolean geomAttIsMerged = mergedFeature[defaultGeometryIndex] == null;
		
		return geomAttIsMerged;
	}

	/**
	 * @param attIndex
	 *            the index of an attribute in the builder's {@link FeatureType}
	 * @return the name of the attribute at index <code>attIndex</code>
	 */
	public String getAttributeName(int attIndex) {

		assert attIndex < getAttributeCount();

		AttributeDescriptor attributeType = featureType.getDescriptor(attIndex);

		return attributeType.getLocalName();
	}

	/**
	 * @param attributeIndex
	 *            the index of the merge feature attribute to return
	 * @return the current attribute value for the merge feature at index
	 *         <code>attributeIndex</code>
	 */
	public Object getMergeAttribute(int attributeIndex) {

		assert attributeIndex < getAttributeCount();
		Object attribute = mergedFeature[attributeIndex];

		return attribute;
	}

	/**
	 * Copies the value of the attribute at index {@code attritbuteIndex} in the
	 * merged feature to be the value of the {@code attritbuteIndex} attribute
	 * in the {@code featureIndex} source feature, and fires a change event to
	 * be caught by the registered {@link ChangeListener}
	 * 
	 * @param srcFeatureIndex position of source feature
	 * @param attributeIndex  index of the attribute at source feature {@code featureIndex} index
	 * @throws IllegalArgumentException
	 * @see {@link #addChangeListener(MergeFeatureBuilder.ChangeListener)}
	 */
	public void copyAttributeToMerge(int srcFeatureIndex, int attributeIndex) throws IllegalArgumentException {

		assert srcFeatureIndex < getFeatureCount();
		assert attributeIndex < getAttributeCount();

		Object value = getAttribute(srcFeatureIndex, attributeIndex);
		setMergeValue(attributeIndex, value);
	}

	/**
	 * Clears the value of the merge feature's attribute at index {@code
	 * attributeIndex}, and fires a change event to be caught by the registered
	 * {@link ChangeListener}
	 * <p>
	 * If {@code attributeIndex == #getDefaultGeometryIndex()}, sets the
	 * geometry value of the merged feature to {@link #getPrittyMergeGeometry()}
	 * </p>
	 * 
	 * @param attributeIndex
	 *            the attribute index of the target feature to be cleared
	 * @see {@link #addChangeListener(MergeFeatureBuilder.ChangeListener)}
	 */
	public void clearMergeAttribute(int attributeIndex) {

		assert attributeIndex < getAttributeCount();

		Object value = null;
		if (attributeIndex == getDefaultGeometryIndex()) {
			value = null;
		}
		setMergeValue(attributeIndex, value);
	}

	/**
	 * Returns the attribute value at index <code>attributeIndex</code>of the
	 * source feature at index <code>featureIndex</code>
	 * 
	 * @param featureIndex
	 *            the index of the feature to retrieve the attribute from
	 * @param attributeIndex
	 *            the index of the attribute to retrieve from the feature at
	 *            index <code>featureIndex</code>
	 * @return the attribute value at index <code>attributeIndex</code>of the
	 *         source feature at index <code>featureIndex</code>
	 */
	public Object getAttribute(int featureIndex, int attributeIndex) {

		assert featureIndex < getFeatureCount();
		assert attributeIndex < getAttributeCount();

		SimpleFeature feature = getFeature(featureIndex);
		Object attribute = feature.getAttribute(attributeIndex);
		return attribute;
	}

	/**
	 * @return the number of source features available to select target feature
	 *         attributes from
	 */
	public int getFeatureCount() {

		return sourceFeatures.size();
	}

	/**
	 * @return the number of attributes defined in the shared
	 *         {@link FeatureType} for the source features and the one to be
	 *         created
	 */
	public int getAttributeCount() {

		return featureType.getAttributeCount();
	}

	/**
	 * Returns the feature ID of the source feature at index
	 * <code>featureIndex</code>
	 * 
	 * @param featureIndex
	 * @return
	 */
	public String getID(int featureIndex) {

		assert featureIndex < getFeatureCount();

		SimpleFeature feature = getFeature(featureIndex);
		return feature.getID();
	}

	/**
	 * Sets the attribute value at index <code>attributeIndex</code> of the
	 * target feature to be <code>value</code> and fires a change event to be
	 * caught by the registered {@link ChangeListener}
	 * 
	 * @param attributeIndex
	 * @param value
	 */
	private void setMergeValue(int attributeIndex, Object value) {

		Object oldValue = getMergeAttribute(attributeIndex);
		mergedFeature[attributeIndex] = value;
		fireChangedEvent(attributeIndex, oldValue);
	}

	private void fireChangedEvent(final int attributeIndex, Object oldValue) {

		for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
			listener.attributeChanged(this, attributeIndex, oldValue);
		}
	}

	public SimpleFeature getFeature(int featureIndex) {

		SimpleFeature feature = sourceFeatures.get(featureIndex);

		return feature;
	}

	/**
	 * Get the layer.
	 * 
	 * @return The layer where the features are.
	 */
	public ILayer getLayer() {

		return this.layer;
	}


	
	public List<SimpleFeature> getSourceFeatures(){
		
		List<SimpleFeature> clone = new LinkedList<SimpleFeature>(this.sourceFeatures);
		return clone;
	}

	public void removeFromSourceFeatures(SimpleFeature selectedFeature) {
		
		sourceFeatures.remove(selectedFeature);
	}
	public synchronized void removeFromSourceFeatures(List<SimpleFeature> featureList) {
		
		sourceFeatures.removeAll(featureList);
	}
	/**
	 * Used by MergeView to remove all features before adding new collection (while in Operation Mode)
	 */
        public synchronized void removeFromSourceFeaturesAll() {
            
            sourceFeatures.clear();
        }

	/**
	 * Checks if the feature's geometry fulfill the conditions to be added in the list of features to merge.
	 * 
	 * <lu>
	 * <li>should be compatible with the layer geometry.</li>
	 * <li>Multipolygon, MultiLineString, MultiPoint can be merge always.
	 * <li>Polygon, LineString, Point can be merge if and only if they intersect.<li>
	 * </lu>
	 * 
	 * @param newFeature
	 * @return true if the feature can be merge.
	 */
	public boolean canMerge(SimpleFeature newFeature) {

		if(this.sourceFeatures.isEmpty()){
			return true; // this is the first feature, so there is nothing to check.
		}
		Geometry defaultGeometry = (Geometry) newFeature.getDefaultGeometry();
		assert defaultGeometry != null:"the feature " + newFeature.getID() + " has not geometry!";  //$NON-NLS-1$//$NON-NLS-2$


		// "Multi" geometries could be merge always.
		Class<? extends Object> geomClass = (defaultGeometry != null)? defaultGeometry.getClass(): Object.class;
		
		Class<? extends Geometry> layerGemetryClass = (Class<? extends Geometry>) layer.getSchema().getGeometryDescriptor().getType().getBinding();
		if(!layerGemetryClass.isAssignableFrom( geomClass) ){
			return false; 
		}
		if(GeometryCollection.class.isAssignableFrom(geomClass)){
			return true;
		}
		assert (defaultGeometry instanceof Polygon) || (defaultGeometry instanceof LineString) || (defaultGeometry instanceof Point);
		
		// Simple geometries should intersects.
		for (SimpleFeature sourceFeature : this.sourceFeatures) {
			Geometry sourceGeometry = (Geometry) sourceFeature.getDefaultGeometry();
			
			if(defaultGeometry.intersects(sourceGeometry) )
				return true;
		}
		return false;
	}


	/**
	 * Builds the merge geometry. If any geometry was set in the merge feature, this
	 * method will build a new one execturing the union of source geometries.
	 *  
	 * @return a Geometry for the merge feature.
	 */
	private Geometry buildMergeGeometry() {
		
		Geometry geom =  (Geometry) this.mergedFeature[ getDefaultGeometryIndex() ];
		if(geom != null){
			// a geometry was set
			return geom;
		} else {
			// make the union 
			SimpleFeatureType type = layer.getSchema();
			final Class<?> expectedGeometryType = type.getGeometryDescriptor()
					.getType().getBinding();
			
			Geometry union;
			
			synchronized(sourceFeatures){
				union = GeometryUtil.geometryUnion(DataUtilities.collection(sourceFeatures));
			}
			union = GeometryUtil.adapt(union,(Class<? extends Geometry>) expectedGeometryType);
			
			return union;
		}
	}

}
