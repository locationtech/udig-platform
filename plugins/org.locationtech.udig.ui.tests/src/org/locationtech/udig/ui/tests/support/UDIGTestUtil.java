/**
 * 
 */
package org.locationtech.udig.ui.tests.support;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.locationtech.udig.ui.WaitCondition;
import org.eclipse.swt.widgets.Display;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

/**
 * Utility class with a number of static convenience methods
 * 
 * @author jeichar
 */
@SuppressWarnings("nls")
public final class UDIGTestUtil {
	/**
	 * The features created have a polygon geometry and have the SimpleFeatureType:
	 * "*geom:Geometry,name:String".
	 * <p>
	 * <ul>
	 * <li> the values of the name attribute are: value+featurenumber</li>
	 * <li>The geometries are polygons and are squares going diagonal down the
	 * screen. They will overlap</li>
	 * </ul>
	 * 
	 * @param numFeatures
	 *            number of features to create
	 * @return array of features as described above.
	 */
	public static SimpleFeature[] createDefaultTestFeatures(String typename,
			int numFeatures) throws SchemaException {
        
		GeometryFactory factory = new GeometryFactory();
		Polygon[] polys = new Polygon[numFeatures];
		String[] attrValues = new String[numFeatures];

		for (int i = 0; i < polys.length; i++) {

			LinearRing ring1 = factory.createLinearRing(new Coordinate[] {
					new Coordinate(25 * i, 25 * i), new Coordinate(25 * i+25, 25 * i),
					new Coordinate(25 * i+25, 25 * i+25), new Coordinate(25 * i, 25 * i+25),
					new Coordinate(25 * i, 25 * i),

			});

			polys[i] = factory.createPolygon(ring1, new LinearRing[] {});
			attrValues[i] = "value" + i; //$NON-NLS-1$
		}
		SimpleFeature[] features = createTestFeatures(typename, polys, attrValues,
				DefaultEngineeringCRS.GENERIC_2D);

		return features;
	}

	/**
	 * Takes array of geometries and array of strings and creates features of
	 * type: "*geom:Geometry,name:String". The number of features returned are
	 * the Max(geom.length, attributeValue.length).
	 * <p>
	 * If crs is null WGS84 will be used.
	 */
	public static SimpleFeature[] createTestFeatures(String typename,
			Geometry[] geom, String[] attributeValue) throws SchemaException {
		return createTestFeatures(typename, geom, attributeValue, null);
	}

	/**
	 * Takes array of geometries and array of strings and creates features of
	 * type: "*geom:Geometry,name:String". The number of features returned are
	 * the Max(geom.length, attributeValue.length).
	 * <p>
	 * If crs is null WGS84 will be used.
	 */
    public static SimpleFeature[] createTestFeatures(String typename,
			Geometry[] geom2, String[] attributeValue2,
			CoordinateReferenceSystem crs) throws SchemaException {
        Geometry[] geom=geom2;
        String[] attributeValue=attributeValue2;
		if (geom.length == 0) {
			geom = new Geometry[1];
		}
		if (attributeValue == null || attributeValue.length == 0) {
			attributeValue = new String[1];
		}
		if (crs == null) {
		    crs = DefaultGeographicCRS.WGS84;
		}
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("test");
		builder.crs(crs).add("geom", Geometry.class);
		builder.setCRS(crs);
		builder.add("name", String.class);
		SimpleFeatureType ft = builder.buildFeatureType();
		int size = Math.max(geom.length, attributeValue.length);
		SimpleFeature[] features = new SimpleFeature[size];
		for (int i = 0; i < size; i++) {
			Geometry geometry;
			if (i >= geom.length)
				geometry = geom[geom.length - 1];
			else
				geometry = geom[i];
			String value;
			if (i >= attributeValue.length)
				value = attributeValue[attributeValue.length - 1];
			else
				value = attributeValue[i];
			features[i] = SimpleFeatureBuilder.build(ft, new Object[] { geometry, value }, typename+ "."+Integer //$NON-NLS-1$
					.toString(i + 1));
		}
		return features;
	}

	/**
	 * The method will throw an exception if the current thread is not in the
	 * display thread.
	 * 
	 * @param maxWaitInMilliSeconds
	 *            The maximum time to wait in Milliseconds
	 * @param condition
	 *            the condition that will cause the method to return early.
     * @param throwExceptionOnTimeout if true an exception will be thrown if a 
     *                                  timeout occurs before condition is true. 
	 * @throws Exception 
	 * @throws Exception
	 */
	public static void inDisplayThreadWait(long maxWaitInMilliSeconds,
			WaitCondition condition, boolean throwExceptionOnTimeout) throws Exception {
		long startTime = System.currentTimeMillis();
        long lastTime=System.currentTimeMillis();
		while (startTime + maxWaitInMilliSeconds > lastTime
				&& !condition.isTrue()) {
            Thread.yield();
			if (!Display.getCurrent().readAndDispatch()){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					//don't worry about it
				}
            }
                
            lastTime=System.currentTimeMillis();
		}
		while(Display.getCurrent().readAndDispatch());
		if (throwExceptionOnTimeout && !condition.isTrue() && startTime + maxWaitInMilliSeconds < lastTime){
            throw new IllegalStateException("condition is false!  Condition that failed is:"+ condition);  //$NON-NLS-1$
            }
	}

}
