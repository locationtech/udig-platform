/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2007-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.element.extensible.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.ows.wms.CRSEnvelope;
import org.junit.Test;
import org.locationtech.udig.project.internal.render.impl.ScaleUtils;

public class ScaleUtilsTest {
	private static SortedSet<Double> SCALES = new TreeSet<Double>(Arrays.asList(10.0,100.0,1000.0,10000.0,100000.0,1000000.0));
	@Test
	public void calculateClosestScale() {
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 0.0, .5),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 10.0, .5),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 9.0, .5),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 5.1, .5),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 54, .5),0.0001);
		assertEquals(100.0, ScaleUtils.calculateClosestScale(SCALES, 56, .5),0.0001);
		assertEquals(100.0, ScaleUtils.calculateClosestScale(SCALES, 99.0, .5),0.0001);
		assertEquals(1000000.0, ScaleUtils.calculateClosestScale(SCALES, 10000000.0, .5),0.0001);
		
		assertEquals(1000000.0, ScaleUtils.calculateClosestScale(SCALES, 10000000.0, 0),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 99, 0),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 0, 0),0.0001);
		assertEquals(100.0, ScaleUtils.calculateClosestScale(SCALES, 101, 0),0.0001);
		assertEquals(100.0, ScaleUtils.calculateClosestScale(SCALES, 150, 0),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 0, 1),0.0001);
		assertEquals(100.0, ScaleUtils.calculateClosestScale(SCALES, 11, 1),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 10, 1),0.0001);
		assertEquals(1000000.0, ScaleUtils.calculateClosestScale(SCALES, 10000000.0, 1),0.0001);
		
		assertEquals(100.0, ScaleUtils.calculateClosestScale(SCALES, 51.0, .7),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 30.0, .7),0.0001);
	}
	
	@Test
	public void calculateResolutions(){
	    ReferencedEnvelope envelope = new ReferencedEnvelope(new CRSEnvelope("WGS84(DD)", //$NON-NLS-1$ 
	            143.83482400000003, -43.648056, 148.47914100000003, -39.573891));

	    assertEquals(0.009015193399753197, ScaleUtils.calculateResolutionFromScale(envelope, 1000000.0, 256),0.0001);
	    assertEquals(9.015193399753199E-4, ScaleUtils.calculateResolutionFromScale(envelope, 100000.0, 256),0.0001);
	    assertEquals(4.5075966998765993E-4, ScaleUtils.calculateResolutionFromScale(envelope, 50000.0, 256),0.0001);
	    assertEquals(1.8030386799506394E-4, ScaleUtils.calculateResolutionFromScale(envelope, 20000.0, 256),0.0001);
	    assertEquals(9.015193399753197E-5, ScaleUtils.calculateResolutionFromScale(envelope, 10000.0, 256),0.0001);
	    assertEquals(4.5075966998765985E-5, ScaleUtils.calculateResolutionFromScale(envelope, 5000.0, 256),0.0001);
	    assertEquals(2.2537983499382992E-5, ScaleUtils.calculateResolutionFromScale(envelope, 2500.0, 256),0.0001);
	    assertEquals(9.015193399753199E-6, ScaleUtils.calculateResolutionFromScale(envelope, 1000.0, 256),0.0001);
	}
}
