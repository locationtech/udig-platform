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
package org.locationtech.udig.project.internal;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;

import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.tests.support.MapTests;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

public class ViewportModelImplTest {

	private static final double ACCURACY = 0.000001;

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.ViewportModelImpl.getScaleDenominator()'
	 */
	@Ignore
	@Test
	public void testGetScaleDenominatorWGS84BoundsSmallerThanWorld() throws Exception {
		Map map = MapTests.createDefaultMap("test",4,true, new Dimension( 500,500 )); //$NON-NLS-1$
		ViewportModel viewportModel = map.getViewportModelInternal();
		viewportModel.setCRS(DefaultGeographicCRS.WGS84);
		viewportModel.setBounds(-20,20,-20,20);
		assertEquals(1.0d, viewportModel.getBounds().getWidth()/viewportModel.getHeight(), ACCURACY);
		
		assertEquals(calculateScale(viewportModel.getBounds(), viewportModel.getCRS(), 500,500),
				viewportModel.getScaleDenominator());
		
	}
	
	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.ViewportModelImpl.getScaleDenominator()'
	 */
    @Test
	public void testGetScaleDenominatorAlbersBoundsSmallerThanWorld() throws Exception {
		Map map = MapTests.createDefaultMap("test",4,true, new Dimension( 500,500 )); //$NON-NLS-1$
		ViewportModel viewportModel = map.getViewportModelInternal();
		viewportModel.setCRS(CRS.decode("EPSG:3005")); //$NON-NLS-1$
		viewportModel.setBounds(-140,-120,40,60);
		assertEquals(1.0d, viewportModel.getBounds().getWidth()/viewportModel.getHeight(), ACCURACY);
		
//		assertEquals(calculateScale(viewportModel.getBounds(), viewportModel.getCRS(), 500,500),
//				viewportModel.getScaleDenominator());
	}
	

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.ViewportModelImpl.getScaleDenominator()'
	 */
    @Test
	public void testGetScaleDenominatorWGS84BoundsLongerThanWorldIn1Direction() throws Exception {
		Map map = MapTests.createDefaultMap("test",4,true, new Dimension( 500,500 )); //$NON-NLS-1$
		ViewportModel viewportModel = map.getViewportModelInternal();
		viewportModel.setCRS(DefaultGeographicCRS.WGS84);
		viewportModel.setBounds(-100,100,-100,100);
		assertEquals(1.0d, viewportModel.getBounds().getWidth()/viewportModel.getHeight(), ACCURACY);
		
		double trueScale = calculateScale(new Envelope(-180,-80, -90,10), viewportModel.getCRS(), 250,250);
		double viewportResults = viewportModel.getScaleDenominator();
		double d = Math.abs(trueScale-viewportResults);
	}
    
	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.ViewportModelImpl.getScaleDenominator()'
	 */
    @Test
	public void testGetScaleDenominatorWGS84BoundsLargerThanWorld() throws Exception {
		Map map = MapTests.createDefaultMap("test",4,true, new Dimension( 500,500 )); //$NON-NLS-1$
		ViewportModel viewportModel = map.getViewportModelInternal();
		viewportModel.setCRS(DefaultGeographicCRS.WGS84);
		viewportModel.setBounds(-180,320,-90,410);
		assertEquals(1.0d, viewportModel.getBounds().getWidth()/viewportModel.getHeight(), ACCURACY);
		
		double trueScale = calculateScale(new Envelope(-180,-80, -90,10), viewportModel.getCRS(), 100,100);
		double viewportResults = viewportModel.getScaleDenominator();
		double d = Math.abs(trueScale-viewportResults);
//		assertTrue(d+" should be less than "+ACCURACY_SCALE, d< ACCURACY_SCALE );		 //$NON-NLS-1$
	}
	
	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.ViewportModelImpl.getScaleDenominator()'
	 */
    @Test
	public void testGetScaleDenominatorWGS84BoundsLargerThanWorldOffScreenData() throws Exception {
		Map map = MapTests.createDefaultMap("test",4,true, new Dimension( 500,500 )); //$NON-NLS-1$
		ViewportModel viewportModel = map.getViewportModelInternal();
		viewportModel.setCRS(DefaultGeographicCRS.WGS84);
		viewportModel.setBounds(500,500,1000,1000);
//		assertEquals(1.0d, viewportModel.getBounds().getWidth()/viewportModel.getHeight(), ACCURACY);
		
		double trueScale = calculateScale(new Envelope(-180,-80, -90,10), viewportModel.getCRS(), 100,100);
		double viewportResults = viewportModel.getScaleDenominator();
		double d = Math.abs(trueScale-viewportResults);
//		assertTrue(d+" should be less than "+ACCURACY_SCALE, d< ACCURACY_SCALE );		 //$NON-NLS-1$
	}

    @Test
	public void testMetersPerPixel() throws Exception {
		
	}
	
	private static double calculateScale(Envelope envelope, CoordinateReferenceSystem coordinateReferenceSystem,int imageWidth,int imageHeight) throws Exception 
	{
        ReferencedEnvelope bounds=new ReferencedEnvelope(envelope, coordinateReferenceSystem);
        bounds.transform(DefaultGeographicCRS.WGS84, true);
        
		double diagonalGroundDistance = DefaultGeographicCRS.WGS84.distance(new double[] {bounds
                .getMinX(), bounds.getMinY()}, new double[] {bounds
                .getMaxX(), bounds.getMaxY()}).doubleValue(); 
		     // pythagorus theorm
		double diagonalPixelDistancePixels = Math.sqrt( imageWidth*imageWidth+imageHeight*imageHeight);
		double diagonalPixelDistanceMeters = diagonalPixelDistancePixels / 72 * 2.54 / 100; // 2.54 = cm/inch, 100= cm/m
		
		return diagonalGroundDistance/diagonalPixelDistanceMeters; // remember, this is the denominator, not the actual scale;
	}

}
