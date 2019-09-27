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
package org.locationtech.udig.ui;

import static org.junit.Assert.assertSame;
import org.locationtech.udig.internal.ui.UDigByteAndLocalTransfer;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.spatial.BBOX;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class TransferTest {
    
    @Test
    public void testFeatureTransfering() throws Exception {
        Display display=Display.getCurrent();
        Clipboard cp=new Clipboard(display);
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("test", 1); //$NON-NLS-1$
        cp.setContents(features,new Transfer[]{UDigByteAndLocalTransfer.getInstance()});
        assertSame(features[0], cp.getContents(UDigByteAndLocalTransfer.getInstance()));

//        cp.setContents(features,new Transfer[]{FeatureTextTransfer.getInstance()});
//        SimpleFeature contents = (SimpleFeature) cp.getContents(FeatureTextTransfer.getInstance());
//        Geometry defaultGeometry = features[0].getDefaultGeometry();
//        assertTrue(defaultGeometry.equalsExact(contents.getDefaultGeometry()));
    }
    
    @Test
    public void testGeometryTransfering() throws Exception {
        Display display=Display.getCurrent();
        Clipboard cp=new Clipboard(display);
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("test", 1); //$NON-NLS-1$
        cp.setContents(new Object[]{features[0].getDefaultGeometry()},new Transfer[]{UDigByteAndLocalTransfer.getInstance()});
        assertSame(features[0].getDefaultGeometry(), cp.getContents(UDigByteAndLocalTransfer.getInstance()));

//        cp.setContents(new Object[]{features[0].getDefaultGeometry()},new Transfer[]{GeometryTextTransfer.getInstance()});
//        Geometry contents = (Geometry) cp.getContents(GeometryTextTransfer.getInstance());
//        Geometry defaultGeometry = features[0].getDefaultGeometry();
//        assertTrue(defaultGeometry.equalsExact(contents));
    }

    @Test
    public void testSelectionTransfering() throws Exception{
        Display display=Display.getCurrent();
        Clipboard cp=new Clipboard(display);
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("test", 1); //$NON-NLS-1$
        FilterFactory factory= CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        SimpleFeatureType featureType = features[0].getFeatureType();
        BoundingBox bounds = features[0].getBounds();
        CoordinateReferenceSystem crs= featureType.getCoordinateReferenceSystem();
		BBOX filter = factory.bbox(featureType.getGeometryDescriptor().getName().getLocalPart(), bounds.getMinX(),
        		bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), CRS.lookupIdentifier(crs, false));
        cp.setContents(new Object[]{filter},new Transfer[]{UDigByteAndLocalTransfer.getInstance()});
        assertSame(filter, cp.getContents(UDigByteAndLocalTransfer.getInstance()));

//        cp.setContents(new Object[]{filter},new Transfer[]{FilterTextTransfer.getInstance()});
//        Filter contents = (Filter) cp.getContents(FilterTextTransfer.getInstance());
//        // There is some sort of bug in the filter parser or the Filter.equals that make the two not be equal
//        // so this is a work around because my code is correct.
//        assertTrue(filter.contains(features[0]));
//        assertTrue(contents.contains(features[0]));
    }
}
