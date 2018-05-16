/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.export;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class FeatureWrapperTest {

	private SimpleFeatureType type1;
	private SimpleFeatureType type2;
	private Point point;
	private LineString line;
	private String name;
	private int id;
	private SimpleFeature original;
	private Point newPoint;
	private FeatureWrapper wrapped;

	@Before
	public void setUp() throws Exception {
		type1=DataUtilities.createType("type1", "geom1:LineString,name:String,id:int,geom2:Point");  //$NON-NLS-1$//$NON-NLS-2$
		type2=DataUtilities.createType("type2", "geom2:Point,name:String"); //$NON-NLS-1$ //$NON-NLS-2$
		
		GeometryFactory fac=new GeometryFactory();
		point=fac.createPoint(new Coordinate(10,10));
		
		line=fac.createLineString(new Coordinate[]{
			new Coordinate(20,20), new Coordinate(30,30)	
		});
		
		name="Name"; //$NON-NLS-1$
		id=1;
			
		original=SimpleFeatureBuilder.build( type1, new Object[]{
				line,name,id,point
		}, null);
		newPoint=fac.createPoint(new Coordinate(0,0));
		wrapped=new FeatureWrapper(original,type2, new Geometry[]{newPoint}, new String[]{"geom2"}); //$NON-NLS-1$
	}
	
	@Ignore
	public void testGetAttributeInt() {
		assertEquals(newPoint, wrapped.getAttribute(0));
		assertEquals(name, wrapped.getAttribute(1));
	}

	@Ignore
	public void testGetAttributeString() {
		assertEquals(newPoint, wrapped.getAttribute("geom2")); //$NON-NLS-1$
		assertEquals(name, wrapped.getAttribute("name")); //$NON-NLS-1$
	}

	@Ignore
	public void testGetAttributes() {
		List<Object> atts=wrapped.getAttributes();
		
		assertEquals(newPoint, atts.get(0));
		assertEquals(name, atts.get(1));
		
		atts=wrapped.getAttributes();
		
		assertEquals(newPoint, atts.get(0));
		assertEquals(name, atts.get(1));

		atts=wrapped.getAttributes();
		
		assertEquals(newPoint, atts.get(0));
		assertEquals(name, atts.get(1));
}

	@Ignore
	public void testGetDefaultGeometry() {
		assertEquals(newPoint, wrapped.getDefaultGeometry());
	}

	@Ignore
	public void testGetFeatureType() {
		assertEquals(type2, wrapped.getFeatureType());
	}

	@Ignore
	public void testGetNumberOfAttributes() {
		assertEquals(2, wrapped.getAttributeCount());
	}

}
