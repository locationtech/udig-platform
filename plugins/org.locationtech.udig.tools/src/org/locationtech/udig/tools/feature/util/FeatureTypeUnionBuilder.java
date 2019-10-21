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
package org.locationtech.udig.tools.feature.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;

/**
 * Maintains the relations between the types present in the feature types union.
 * 
 * <p>
 * When a new feature type is joined its attribute name could be equals to
 * attribute name of attributes type present in previously feature types. This
 * clash is solved creating a new name with the 2 number.
 * </p>
 * <p>
 * 
 * <pre>
 * Implementation Note:
 * The join is maintained in a map with the following structure
 * &lt;li&gt;union attr name ---&gt; original attr type&lt;/li&gt;
 * &lt;li&gt;feature type name, attr type name ---&gt; join name&lt;/li&gt;
 * 
 * </pre>
 * 
 * </p>
 * 
 * 
 * @author Mauricio Pazos (www.axios.es)
 * 
 */
public final class FeatureTypeUnionBuilder {

	private final String FEATURE_TYPE_NAME;

	private Map<String, AttributeDescriptor> mapUnionAttributes = new LinkedHashMap<String, AttributeDescriptor>();

	private Map<UnionKey, String> mapOriginalUnion = new HashMap<UnionKey, String>();

	private GeometryDescriptor unionGeometryAttr = null;

	private SimpleFeatureType unionFeatureType = null;

	private List<SimpleFeature> unionFeatures = new ArrayList<SimpleFeature>(2);

	private Geometry geometry = null;

	private Class<? extends Geometry> geometryClass = null;

	private String geometryName = null;

	public FeatureTypeUnionBuilder(String featureTypeName) {
		FEATURE_TYPE_NAME = featureTypeName;
	}

	/**
	 * This method joins the feature type with the types maintained by this
	 * object. Geometry attribute is not added into the join geometry. It must
	 * be computed by the client and setted using the setGeometry Method.
	 * 
	 * @see setGeometry
	 * 
	 * @param featureType
	 */
	public FeatureTypeUnionBuilder add(final SimpleFeatureType featureType) {

		// adds the attribute types of this feature type, if there are name
		// collisions
		// the method appends the number "2" at the name to avoid name
		// duplication.
		// The geometry attribute will be omitted.
		for (int i = 0; i < featureType.getAttributeCount(); i++) {

			AttributeDescriptor attributeType = featureType.getDescriptor(i);
			if (!(attributeType instanceof GeometryDescriptor)) {

				String attrUnionName = attributeType.getLocalName();
				if (this.mapUnionAttributes.containsKey(attrUnionName)) {
					StringBuffer duplicatedName = new StringBuffer(
							attrUnionName);
					duplicatedName.append("2");

					attrUnionName = duplicatedName.toString();
				}
				AttributeTypeBuilder builder = new AttributeTypeBuilder();
				builder.setBinding(attributeType.getType().getBinding());
				builder.setNillable(attributeType.isNillable());

				AttributeDescriptor newAttribute = builder
						.buildDescriptor(attrUnionName);

				mapUnionAttributes.put(attrUnionName, newAttribute);
				mapOriginalUnion.put(new UnionKey(featureType.getTypeName(),
						attributeType.getLocalName()), attrUnionName);
			}
		}
		return this;
	}

	public FeatureTypeUnionBuilder setGeometryClass(final String geometryName,
			final Class<? extends Geometry> geomClass,
			final CoordinateReferenceSystem crs) {

		assert geometryName != null : "the geometry name can not be null";
		assert geomClass != null : "the geometry class can not be null";
		assert crs != null : "the CRS can not be null";

		this.geometryName = geometryName;
		this.geometryClass = geomClass;

		GeometryDescriptor geoAttrType;

		AttributeTypeBuilder build = new AttributeTypeBuilder();
		build.setName(this.geometryName);
		build.setBinding(this.geometryClass);
		build.setNillable(true);
		build.setLength(100);
		build.setCRS(crs);

		GeometryType type = build.buildGeometryType();
		geoAttrType = build.buildDescriptor(this.geometryName, type);

		this.unionGeometryAttr = geoAttrType;

		return this;
	}

	/**
	 * The name of geometry
	 * 
	 * @param geometryName
	 */
	public FeatureTypeUnionBuilder setGeometry(Geometry geom) {

		this.geometry = geom;

		return this;
	}

	/**
	 * Adds the feature to this union
	 * 
	 * @param feature
	 * @return this union
	 */
	public FeatureTypeUnionBuilder add(SimpleFeature feature) {

		this.unionFeatures.add(feature);

		return this;
	}

	/**
	 * @return the union Feature Type
	 * @throws SchemaException
	 */
	private SimpleFeatureType createFeatureType() throws SchemaException {

		AttributeDescriptor[] attrType = new AttributeDescriptor[this.mapUnionAttributes
				.size()];

		int i = 0;
		for (Entry<String, AttributeDescriptor> entry : this.mapUnionAttributes
				.entrySet()) {

			attrType[i++] = entry.getValue();
		}
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();

		builder.setName(FEATURE_TYPE_NAME);
		builder.setAbstract(false);
		builder.add(this.unionGeometryAttr);
		builder.addAll(attrType);

		SimpleFeatureType newType = builder.buildFeatureType();
		return newType;
	}

	public SimpleFeatureType getFeatureType() throws SchemaException {

		if (this.unionFeatureType == null) {
			this.unionFeatureType = createFeatureType();
		}
		return this.unionFeatureType;
	}

	/**
	 * A new feature type using the attribute names of all joined feature type.
	 * 
	 * @return a new feature type
	 * @throws IllegalAttributeException
	 * @throws SchemaException
	 */
	public SimpleFeature getFeature() throws IllegalAttributeException,
			SchemaException {

		SimpleFeatureType unionFeatureType = getFeatureType();
		// adds the attributes values
		Object[] attrList = new Object[unionFeatureType.getAttributeCount()];
		for (SimpleFeature feature : this.unionFeatures) {

			SimpleFeatureType featureType = feature.getFeatureType();
			for (int j = 0; j < featureType.getAttributeCount(); j++) {

				// gets the attribute value
				AttributeDescriptor attrType = featureType.getDescriptor(j);
				if (!(attrType instanceof GeometryDescriptor)) {

					Object attrValue = feature.getAttribute(j);

					// gets the position in the union
					String unionAttrName = findAttributeName(featureType
							.getTypeName(), attrType.getLocalName());

					int unionAttrPosition = unionFeatureType
							.indexOf(unionAttrName);

					// set the value in union
					attrList[unionAttrPosition] = attrValue;
				}
			}
		}
		// creates the new feature
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(
				unionFeatureType);
		builder.addAll(attrList);
		// SimpleFeature product = unionFeatureType.create(attrList);
		SimpleFeature product = builder.buildFeature(null);
		product.setDefaultGeometry(this.geometry);

		return product;
	}

	/**
	 * Returns the position of attribute in the union type.
	 * 
	 * @param featureTypeName
	 * @param attrName
	 * 
	 * @return the attribute name in the union type
	 */
	private String findAttributeName(final String featureTypeName,
			final String attrName) {

		UnionKey key = new UnionKey(featureTypeName, attrName);
		String unionAttrName = this.mapOriginalUnion.get(key);

		return unionAttrName;
	}

	protected class UnionKey {

		private String featureTypeName;
		private String attrTypeName;

		UnionKey(final String featureTypeName, final String attrTypeName) {
			this.attrTypeName = attrTypeName;
			this.featureTypeName = featureTypeName;
		}

		public String getFeatureTypeName() {
			return featureTypeName;
		}

		public String getAttrTypeName() {
			return attrTypeName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((attrTypeName == null) ? 0 : attrTypeName.hashCode());
			result = prime
					* result
					+ ((featureTypeName == null) ? 0 : featureTypeName
							.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final UnionKey other = (UnionKey) obj;
			if (attrTypeName == null) {
				if (other.attrTypeName != null)
					return false;
			} else if (!attrTypeName.equals(other.attrTypeName))
				return false;
			if (featureTypeName == null) {
				if (other.featureTypeName != null)
					return false;
			} else if (!featureTypeName.equals(other.featureTypeName))
				return false;
			return true;
		}

	}

}
