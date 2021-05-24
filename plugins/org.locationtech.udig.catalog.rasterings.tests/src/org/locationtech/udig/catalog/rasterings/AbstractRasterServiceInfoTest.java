/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.rasterings;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IServiceInfo;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test raster services getInfo.getMetric function
 *
 * @author fgdrf
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractRasterServiceInfoTest {

    class AbstractRasterServiceTest extends AbstractRasterService {

        private GridCoverage2DReader testReader;

        volatile int counterReaderRequested = 0;

        protected AbstractRasterServiceTest(GridCoverage2DReader reader)
                throws MalformedURLException {
            super(new URL("http://test.service"), "ajs", null);
            this.testReader = reader;
        }

        @Override
        public Map<String, Serializable> getConnectionParams() {
            return null;
        }

        @Override
        protected IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
            return null;
        }

        @Override
        public List<AbstractRasterGeoResource> resources(IProgressMonitor monitor)
                throws IOException {
            return null;
        }

        @Override
        public synchronized GridCoverage2DReader getReader(IProgressMonitor monitor) {
            counterReaderRequested++;
            return testReader;
        }
    }

    @Mock
    GeneralEnvelope generalEnvelope = Mockito.mock(GeneralEnvelope.class);

    @Mock
    GridCoverage2DReader abstractGridCoverage2DReaderMock = Mockito
            .mock(GridCoverage2DReader.class);

    AbstractRasterServiceInfo rasterServiceInfo;

    ID serviceId;

    private AbstractRasterServiceTest rasterServiceTest;

    @Before
    public void before() throws Exception {
        rasterServiceTest = new AbstractRasterServiceTest(abstractGridCoverage2DReaderMock);
        rasterServiceInfo = new AbstractRasterServiceInfo(rasterServiceTest);
    }

    @Test
    public void completenessOneOutOfTwoWithGeneric2DandValidEnvelope() {
        // when
        when(abstractGridCoverage2DReaderMock.getCoordinateReferenceSystem())
                .thenReturn(DefaultEngineeringCRS.GENERIC_2D);
        when(abstractGridCoverage2DReaderMock.getOriginalEnvelope()).thenReturn(generalEnvelope);
        when(generalEnvelope.isEmpty()).thenReturn(Boolean.FALSE);
        when(generalEnvelope.isNull()).thenReturn(Boolean.FALSE);
        when(generalEnvelope.isInfinite()).thenReturn(Boolean.FALSE);

        assertEquals(0.5, rasterServiceInfo.getMetric(), 0.001);
    }

    @Test
    public void completenessTwoOutOfTwoWithWGS84andValidEnvelope() {
        // when
        when(abstractGridCoverage2DReaderMock.getCoordinateReferenceSystem())
                .thenReturn(DefaultGeographicCRS.WGS84);
        when(abstractGridCoverage2DReaderMock.getOriginalEnvelope()).thenReturn(generalEnvelope);
        when(generalEnvelope.isEmpty()).thenReturn(Boolean.FALSE);
        when(generalEnvelope.isNull()).thenReturn(Boolean.FALSE);
        when(generalEnvelope.isInfinite()).thenReturn(Boolean.FALSE);

        assertEquals(1, rasterServiceInfo.getMetric(), 0.001);
    }

    @Test
    public void completenessZeroOutOfTwoWithGeneric2DandInvalidEnvelope()
            throws MalformedURLException {
        // when
        when(abstractGridCoverage2DReaderMock.getCoordinateReferenceSystem())
                .thenReturn(DefaultEngineeringCRS.GENERIC_2D);
        when(abstractGridCoverage2DReaderMock.getOriginalEnvelope()).thenReturn(generalEnvelope);
        when(generalEnvelope.isEmpty()).thenReturn(Boolean.TRUE);

        assertEquals(0, rasterServiceInfo.getMetric(), 0.001);
    }

    @Test
    public void completenessFromCachedResultOnSecondRequest() {
        when(abstractGridCoverage2DReaderMock.getCoordinateReferenceSystem())
                .thenReturn(DefaultGeographicCRS.WGS84);
        when(abstractGridCoverage2DReaderMock.getOriginalEnvelope()).thenReturn(generalEnvelope);
        when(generalEnvelope.isEmpty()).thenReturn(Boolean.FALSE);
        when(generalEnvelope.isNull()).thenReturn(Boolean.FALSE);
        when(generalEnvelope.isInfinite()).thenReturn(Boolean.FALSE);

        assertEquals(1, rasterServiceInfo.getMetric(), 0.001);

        assertEquals(1, rasterServiceInfo.getMetric(), 0.001);
        assertEquals(1, rasterServiceTest.counterReaderRequested);
    }

}
