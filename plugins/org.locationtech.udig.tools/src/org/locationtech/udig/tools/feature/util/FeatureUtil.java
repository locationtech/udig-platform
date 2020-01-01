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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import org.locationtech.udig.tools.internal.i18n.Messages;
import org.locationtech.udig.tools.geometry.internal.util.GeometryUtil;

/**
 * Convenient method to handle Features and its components
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1
 */
public final class FeatureUtil {

	private FeatureUtil() {

		// utility class pattern
	}

	/**
	 * Copies the property values of source in the target if that is possible. 
	 * The copy is possible only if the names and the class are equals.
	 *   
	 * @param source
	 * @param target
	 * @return
	 * @throws IllegalAttributeException
	 * 
	 */
	public static SimpleFeature copyAttributes(final SimpleFeature source, final SimpleFeature target)
		throws IllegalAttributeException {

		List<String> emptyList = Collections.emptyList();
		return copyAttributesOmitting(source, target, emptyList);
	}

	/**
	 * Copies all attributes omitting that attributes present in the omit list.
	 * 
	 * @param source
	 * @param target
	 * @param attributeNameToOmit
	 *            attribute's name to omit. if it is null does not omit any
	 *            attribute
	 * @return the target feature populated with source data
	 * 
	 * @throws IllegalAttributeException 
	 */
	public static SimpleFeature copyAttributesOmitting(	
			final SimpleFeature source,
			SimpleFeature target,
			final List<String> attributeNameToOmit)
		throws IllegalAttributeException {

		assert source != null : "source cannot be null"; //$NON-NLS-1$
		assert target != null : "target cannot be null"; //$NON-NLS-1$

		for (AttributeDescriptor targetAttrDescriptor : target.getFeatureType().getAttributeDescriptors()) {

			String attName = targetAttrDescriptor.getLocalName();
			if (!attributeNameToOmit.contains(attName)) {
				
				target = copyAttributeValue(attName, source, target);
			}
		}
		return target;
	}
	

	/**
	 * Copies all attributes present in the properties list. 
	 * @param source	source feature
	 * @param target	target feature
	 * @param propertiesList list of property names
	 * @return the target with the new values
	 * 
	 * @throws IllegalAttributeException
	 */
	public static SimpleFeature copyAttributesInPropertyList(
			final SimpleFeature source,
			SimpleFeature target, 
			final List<String> propertiesList)
		throws IllegalAttributeException {

		assert source != null : "source cannot be null"; //$NON-NLS-1$
		assert target != null : "target cannot be null"; //$NON-NLS-1$
		assert propertiesList!= null: "attributeToCopy cannot be null"; //$NON-NLS-1$
		
		for (String property : propertiesList) {

			assert source.getAttribute(property) != null : "propery should be in the source feature"; //$NON-NLS-1$
			
			AttributeDescriptor propDescriptor = target.getFeatureType().getDescriptor(property);
			if(propDescriptor != null){
				
				target = copyAttributeValue(property, source, target);
			}
		}
		return target;
	}
	

	/**
	 * Copies the attribute value if it is compatible with the target value. 
	 * They are compatible if they are equals name and type
	 * @param attrName
	 * @param source
	 * @param target
	 * @return the target with the new value in the attrNaame
	 */
	private static SimpleFeature copyAttributeValue(
			final String attrName,
			final SimpleFeature source,
			SimpleFeature target ) {
		
		// if the attributes are compatible (equals name and type) then does the copy
		AttributeDescriptor sourceAttrDescriptor = source.getType().getDescriptor(attrName);
		if(sourceAttrDescriptor != null){
			
			AttributeDescriptor targetAttrDescriptor = target.getType().getDescriptor(attrName);
			if(targetAttrDescriptor != null){
				
				Class<?> sourceClass = sourceAttrDescriptor.getType().getBinding();
				Class<?> targetClass = targetAttrDescriptor.getType().getBinding();
				if(sourceClass.isAssignableFrom(targetClass) ){

					Object value =source.getAttribute(attrName);
					target.setAttribute(attrName, value);
				} 
			}
		}
		return target;
	}

	/**
	 * Returns the a feature builder for a type with the default geometry
	 * attribute. The default geometry type is <code>Geometry<code>
	 * 
	 * @return FeatureTypeBuilder TODO refactor in Prototype Factory class
	 */
	public static SimpleFeatureTypeBuilder createDefaultFeatureType() {

		return createDefaultFeatureType(Messages.GeoToolsUtils_FeatureTypeName);
	}

	/**
	 * Returns the a feature builder for a type with the attributes present in
	 * the prototype. The default geometry type is <code>Geometry<code>
	 * 
	 * @param prototype
	 * @return FeatureTypeBuilder
	 * 
	 */
	public static SimpleFeatureTypeBuilder createDefaultFeatureType(final SimpleFeatureType prototype) {

		assert prototype != null;
		final String newTypeName = prototype.getTypeName() + "2"; //$NON-NLS-1$ 
		return createDefaultFeatureType(prototype, newTypeName);
	}

	/**
	 * Returns the a feature builder for a type with the attributes present in
	 * the prototype. The default geometry type is <code>Geometry<code>
	 * 
	 * @param prototype
	 * @param typeName
	 *            name of new FeatureType
	 * @return FeatureTypeBuilder
	 */
	public static SimpleFeatureTypeBuilder createDefaultFeatureType(final SimpleFeatureType prototype,
																	final String typeName) {

		assert prototype != null;
		assert typeName != null;

		SimpleFeatureTypeBuilder builder;

		builder = new SimpleFeatureTypeBuilder();
		builder.setName(typeName);
		List<AttributeDescriptor> attributes = prototype.getAttributeDescriptors();
		GeometryDescriptor defaultGeometry = prototype.getGeometryDescriptor();

		for (int i = 0; i < attributes.size(); i++) {

			AttributeDescriptor att = attributes.get(i);

			if (att == defaultGeometry) {
				if (att.getType().getBinding() != MultiPolygon.class && att.getType().getBinding() != Polygon.class) {

					Class<?> targetGeomType = Polygon.class;
					final Class<?> sourceGeomClass = defaultGeometry.getType().getBinding();
					if (GeometryCollection.class.isAssignableFrom(sourceGeomClass)) {

						targetGeomType = MultiPolygon.class;
					}
					final String geomTypeName = att.getLocalName();
					CoordinateReferenceSystem crs = defaultGeometry.getCoordinateReferenceSystem();

					AttributeTypeBuilder build = new AttributeTypeBuilder();
					build.setName(geomTypeName);
					build.setBinding(targetGeomType);
					build.setNillable(true);
					build.setCRS(crs);

					GeometryType type = build.buildGeometryType();
					att = build.buildDescriptor(geomTypeName, type);

				}
				builder.add(att);
				builder.setDefaultGeometry(att.getLocalName());
			} else {
				builder.add(att);
			}
		}
		return builder;

	}

	/**
	 * Returns the a feature builder for a type with the default geometry
	 * attribute The default geometry type is
	 * <code>Geometry<code>. The CRS will be WGS84.
	 * 
	 * @return FeatureTypeBuilder
	 */
	public static SimpleFeatureTypeBuilder createDefaultFeatureType(final String typeName) {

		return createDefaultFeatureType(typeName, DefaultGeographicCRS.WGS84, Geometry.class);

	}

	/**
	 * Returns the a feature builder for a type with the default geometry
	 * attribute The default geometry type is <code>Geometry<code>.
	 * 
	 * @param typeName
	 * @param crs
	 * @return FeatureTypeBuilder
	 */
	public static SimpleFeatureTypeBuilder createDefaultFeatureType(final String typeName,
																	final CoordinateReferenceSystem crs,
																	final Class<? extends Geometry> targetClass) {

		assert typeName != null;

		SimpleFeatureTypeBuilder builder;
		builder = new SimpleFeatureTypeBuilder();
		builder.setName(typeName);
		builder.setCRS(crs);
		builder.add(Messages.GeoToolsUtils_Geometry, targetClass);

		return builder;

	}

	/**
	 * Returns the a feature type with the attributes present in the prototype.
	 * 
	 * @param prototype
	 *            feature type used as model
	 * @param typeName
	 *            name of the new feature type
	 * @param crs
	 *            crs of geometry attribute
	 * @return FeatureType a new feature type TODO refactor in Prototype Factory
	 *         class
	 */
	public static SimpleFeatureType createFeatureType(	final SimpleFeatureType prototype,
														final String typeName,
														final CoordinateReferenceSystem crs) throws SchemaException {

		assert prototype != null : "prototype can be null";
		GeometryDescriptor geomAttrType = prototype.getGeometryDescriptor();

		assert geomAttrType != null : "the Geometry Class can be null";
		Class<? extends Geometry> geomClass = (Class<? extends Geometry>) geomAttrType.getType().getBinding();

		return createFeatureType(prototype, typeName, crs, geomClass);
	}

	/**
	 * Returns the a feature type with the attributes type present in the
	 * prototype. The new feature type will be the name crs and geometry class
	 * specified
	 * 
	 * @param prototype
	 *            feature type used as model
	 * @param typeName
	 *            name of the new feature type
	 * @param crs
	 *            crs of geometry attribute
	 * @param geometryClass
	 *            the geometry class
	 * @return FeatureType a new feature type TODO refactor in Prototype Factory
	 *         class
	 */
	public static SimpleFeatureType createFeatureType(	final SimpleFeatureType prototype,
														final String typeName,
														final CoordinateReferenceSystem crs,
														final Class<? extends Geometry> geometryClass)
		throws SchemaException {

		assert prototype != null : "propotype cannot be null"; //$NON-NLS-1$
		assert typeName != null : "typeName cannot be null"; //$NON-NLS-1$
		assert crs != null : "crs cannot be null"; //$NON-NLS-1$
		assert geometryClass != null : "geometryClass be null"; //$NON-NLS-1$

		SimpleFeatureTypeBuilder builder;
		builder = new SimpleFeatureTypeBuilder();
		builder.setName(typeName);

		List<AttributeDescriptor> attributes = prototype.getAttributeDescriptors();
		GeometryDescriptor defaultGeomAttr = prototype.getGeometryDescriptor();

		assert defaultGeomAttr != null : "default geometry was expected"; //$NON-NLS-1$

		// adds all attributes without default geometry
		for (int i = 0; i < attributes.size(); i++) {
			AttributeDescriptor att = attributes.get(i);
			if (att != defaultGeomAttr) {
				builder.add(att);
			}
		}
		// create the default geometry with Geometry Class value
		AttributeTypeBuilder build = new AttributeTypeBuilder();
		build.setName(defaultGeomAttr.getLocalName());
		build.setNillable(true);
		build.setBinding(geometryClass);
		build.setCRS(crs);

		GeometryType type = build.buildGeometryType();

		builder.setDefaultGeometry(build.buildDescriptor(defaultGeomAttr.getLocalName(), type).getLocalName());

		builder.add(defaultGeomAttr.getLocalName(), geometryClass, crs);

		return builder.buildFeatureType();

	}

	/**
	 * Creates a new feature type using the geometry (named "geometry" by
	 * default) class and the collections of attributes types.
	 * 
	 * @param name
	 *            name of the new feature type.
	 * @param crs
	 * @param geomClass
	 * @param attrTypeCollection
	 * @return
	 * @throws SchemaException
	 * 
	 *             TODO refactor in Prototype Factory class
	 */
	public static SimpleFeatureType createFeatureType(	final String name,
														final CoordinateReferenceSystem crs,
														final Class<? extends Geometry> geomClass,
														final List<AttributeDescriptor> attrTypeCollection)
		throws SchemaException {
		SimpleFeatureTypeBuilder builder = createDefaultFeatureType(name, crs, geomClass);

		// adds the rest of attributes
		for (AttributeDescriptor att : attrTypeCollection) {
			builder.add(att);
		}

		return builder.buildFeatureType();
	}

	/**
	 * Creates a new Feature for <code>targetType</code> that holds the common
	 * attributes from <code>sourceFeature</code> and the new geometry.
	 * 
	 * @param sourceFeature
	 *            the original Feature from which to extract matching attributes
	 *            for the new Feature
	 * @param targetType
	 * @param newGeometry
	 * @return a new Feature of type <code>targetType</code> holding the common
	 *         attributes with <code>sourceFeature</code> and
	 *         <code>bufferedGeometry</code> as the feature's default geometry
	 * @throws IllegalAttributeException
	 */
	public static SimpleFeature createFeatureUsing(	final SimpleFeature sourceFeature,
													final SimpleFeatureType targetType,
													final Geometry newGeometry) throws IllegalAttributeException {

		try {
			SimpleFeature newFeature = DataUtilities.template(targetType);
			final GeometryDescriptor targetGeometryType = targetType.getGeometryDescriptor();
			final String geoAttName = targetGeometryType.getLocalName();
			List<String> omitAttr = new ArrayList<String>(1);
			omitAttr.add(geoAttName);

			newFeature = FeatureUtil.copyAttributesOmitting(sourceFeature, newFeature, omitAttr);

			final Class<? extends Geometry> geomClass = (Class<? extends Geometry>) targetGeometryType.getType()
						.getBinding();
			final Geometry adaptedGeometry = GeometryUtil.adapt(newGeometry, geomClass);

			newFeature.setAttribute(geoAttName, adaptedGeometry);

			return newFeature;

		} catch (IllegalAttributeException e) {
			throw new IllegalAttributeException(null, null, e.getMessage());
		}
	}

	/**
	 * Copies all attributes omitting that attributes present in the list.
	 * 
	 * @param source
	 * @param target
	 * @param attributeNameToOmit
	 *            attribute's name to omit. if it is null does not omit any
	 *            attribute
	 * @return the target feature populated with source data
	 * 
	 * @throws IllegalAttributeException
	 * 
	 */
	public static SimpleFeature copyAttributesCheater(	final SimpleFeature source,
														final SimpleFeature target,
														final List<String> attributeNameToOmit)
		throws IllegalAttributeException {

		assert source != null : "illegal argument: source cannot be null";
		assert target != null : "illegal argument: target cannot be null";

		Map<String, Class<?>> sourceTypes = new HashMap<String, Class<?>>();
		for (AttributeDescriptor att : source.getFeatureType().getAttributeDescriptors()) {

			String name = att.getLocalName();
			if (!attributeNameToOmit.contains(name)) {
				sourceTypes.put(name.toUpperCase(), att.getType().getBinding());
			}

		}
		for (AttributeDescriptor att : target.getFeatureType().getAttributeDescriptors()) {

			String name = att.getLocalName();
			if (!attributeNameToOmit.contains(name)) {

				Class<?> sourceBinding = sourceTypes.get(name);
				if (sourceBinding == null) {
					sourceBinding = sourceTypes.get(name.toUpperCase());
				}
				if (sourceBinding != null) {
					Object attribute = source.getAttribute(name);
					if (attribute == null) {
						attribute = source.getAttribute(name.toLowerCase());
					}
					if (attribute == null) {
						attribute = source.getAttribute(name.toUpperCase());
					}
					target.setAttribute(name, attribute);
				}
			}
		}
		return target;
	}

	/**
	 * Create a new feature of Feature Type with the provided geometry. The
	 * geometry will be adapted to geometry class of feature type.
	 * 
	 * @param type
	 * @param geometry
	 * @return a new feature 
	 */
	public static SimpleFeature createFeatureWithGeometry(final SimpleFeatureType type, Geometry geometry) {

		SimpleFeature newFeature;
		try {
			newFeature = DataUtilities.template(type);
			final GeometryDescriptor targetGeometryType = type.getGeometryDescriptor();

			final String attGeomName = targetGeometryType.getLocalName();
			final Class<? extends Geometry> geomClass = (Class<? extends Geometry>) targetGeometryType.getType()
						.getBinding();
			Geometry geoAdapted = GeometryUtil.adapt(geometry, geomClass);
			
			newFeature.setAttribute(attGeomName, geoAdapted);

			return newFeature;

		} catch (IllegalAttributeException e) {
			final String msg = Messages.GeoToolsUtils_FailCreatingFeature;
			throw (RuntimeException) new RuntimeException(msg).initCause(e);
		} finally {
		}
	}

	/**
	 * Computes the sum of features in the feature collection.
	 * 
	 * @param selectedFeatures
	 * @return the count of features in the collection or Integer.MAX_VALUE if
	 *         the feature collection has more than Integer.MAX_VALUE features
	 */
	public static int computeCollectionSize(FeatureCollection<SimpleFeatureType, SimpleFeature> features) {
    
            FeatureIterator<SimpleFeature> iter = features.features();
            int count = 0;
            try {
                while (iter.hasNext()) {
                    iter.next();
                    count++;
                }
            } catch (ArithmeticException e) {
                count = Integer.MAX_VALUE;
            } finally {
                iter.close();
            }
    
            return count;
	}

	/**
	 * Looks up the property name in the {@link FeatureType}
	 * 
	 * @param featureType
	 *            source feature type
	 * @param dissolveProperty
	 *            property name required
	 * @return true if the the feature type has a property with the specified
	 *         name
	 */
	public static boolean hasProperty(final SimpleFeatureType featureType, final List<String> dissolveProperty) {

		for (String property : dissolveProperty) {
			if (!(getPositionProperty(featureType, property) != -1)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Searches position of the property name in the {@link FeatureType}
	 * 
	 * @param featureType
	 *            source feature type
	 * @param propertyName
	 *            property name required
	 * @return a value equal or greater than 0, it will be -1 if the feature
	 *         type have not got the property
	 * 
	 */
	public static int getPositionProperty(final SimpleFeatureType featureType, final String propertyName) {

		assert featureType != null : "featureType cannot be null";
		assert propertyName != null : "propertyName != cannot be null";

		for (int i = 0; i < featureType.getAttributeCount(); i++) {
			String name = featureType.getDescriptor(i).getLocalName();

			if (propertyName.equals(name)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * List of position of properties in the feature type.
	 * 
	 * @param featureType
	 * @param propertyNames
	 * @return the list of properties position
	 */
	public static List<Integer> getPositionProperty(final SimpleFeatureType featureType, final List<String> propertyNames) {

		assert featureType != null : "featureType cannot be null";
		assert propertyNames != null : "propertyName != cannot be null";

		List<Integer> indexList = new LinkedList<Integer>();

		for (String property : propertyNames) {
			indexList.add(getPositionProperty(featureType, property));
		}

		return indexList;
	}

	/**
	 * Look if the feature start with newX i.e: new1 will return true.
	 * 
	 * @param feature
	 * @return True if it starts with new*
	 */
	public static boolean isNewFeature(SimpleFeature feature) {

		boolean isNew = false;

		if (feature.getID().startsWith("new")) {
			isNew = true;
		}
		return isNew;
	}

	/**
	 * Add an attribute to the current FeatureType.
	 * 
	 * @param prototype
	 * @param attributeName
	 * @param clazz
	 * @return
	 * @throws SchemaException
	 */
	public static SimpleFeatureType addAttributeToFeatureType(	SimpleFeatureType prototype,
																String attributeName,
																Class<?> clazz) throws SchemaException {

		assert prototype != null;
		assert attributeName != null;

		SimpleFeatureTypeBuilder builder;
		builder = new SimpleFeatureTypeBuilder();
		builder.setName(prototype.getName());

		// adds the rest of attributes
		for (AttributeDescriptor att : prototype.getAttributeDescriptors()) {
			builder.add(att);
		}

		AttributeTypeBuilder build = new AttributeTypeBuilder();
		build.setName(attributeName);
		build.setNillable(true);
		build.setBinding(clazz);

		AttributeDescriptor att = build.buildDescriptor(attributeName);

		builder.add(att);

		return builder.buildFeatureType();
	}

	/**
	 * Return an array list with the features contained on the collection.
	 * 
	 * 
	 * @param featureCollection
	 * @return
	 */
	public static List<SimpleFeature> getFeatures(final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection) {

            FeatureIterator<SimpleFeature> iter = null;
            List<SimpleFeature> featureList = new ArrayList<SimpleFeature>(featureCollection.size());
            try {
                iter = featureCollection.features();
                while (iter.hasNext()) {
    
                    featureList.add(iter.next());
    
                }
                return featureList;
            } finally {
                if (iter != null) {
                    iter.close();
                }
            }

	}

}
