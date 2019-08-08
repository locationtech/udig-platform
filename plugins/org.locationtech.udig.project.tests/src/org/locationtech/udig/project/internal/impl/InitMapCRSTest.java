package org.locationtech.udig.project.internal.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class InitMapCRSTest {

    @Test
    public void defaultCRSisSetAndValid() throws Exception {
        CoordinateReferenceSystem pseudoMercatorCRS = CRS.decode("EPSG:3857");
        assertTrue(InitMapCRS.isSetValidDefaultCRS(pseudoMercatorCRS, DefaultEngineeringCRS.GENERIC_2D, 3857));
    }

    @Test
    public void defaultCRSisSetAndValid2() throws Exception {
        assertFalse(InitMapCRS.isSetValidDefaultCRS(DefaultEngineeringCRS.GENERIC_2D, DefaultEngineeringCRS.GENERIC_2D, 999222));
    }

    @Test
    public void allowsToGrabCRSFromLayerDefaultCRSisNotSet() throws Exception {
        CoordinateReferenceSystem epsg4326 = CRS.decode("EPSG:4326");
        assertFalse(InitMapCRS.isSetValidDefaultCRS(epsg4326, DefaultEngineeringCRS.GENERIC_2D, -1));
    }
}
