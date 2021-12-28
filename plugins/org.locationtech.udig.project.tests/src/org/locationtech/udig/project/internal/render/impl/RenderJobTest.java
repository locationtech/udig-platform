/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2021, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.render.IRenderContext;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RenderJobTest {

    @Mock
    RenderExecutor executorMock;

    @Mock
    ReferencedEnvelope envelopeMock;

    @Mock
    IProgressMonitor monitorMock;

    @Mock
    IRenderContext renderContextMock;

    RenderJob renderJob;

    AtomicBoolean scheduleRequested = new AtomicBoolean(false);

    @Before
    public void setup() {
        renderJob = new RenderJob(executorMock) {
            @Override
            public boolean shouldSchedule() {
                scheduleRequested.set(true);
                return false;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRequestFailsWithoutCRS() {
        when(envelopeMock.getCoordinateReferenceSystem()).thenReturn(null);

        renderJob.addRequest(envelopeMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRequestFailsWithEnvelopeIsEmpty() {
        when(envelopeMock.getCoordinateReferenceSystem()).thenReturn(DefaultGeographicCRS.WGS84);
        when(envelopeMock.isEmpty()).thenReturn(Boolean.TRUE);

        renderJob.addRequest(envelopeMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRequestFailsWithEnvelopeIsNull() {
        when(envelopeMock.getCoordinateReferenceSystem()).thenReturn(DefaultGeographicCRS.WGS84);
        when(envelopeMock.isNull()).thenReturn(Boolean.TRUE);

        renderJob.addRequest(envelopeMock);
    }

    @Test
    public void addRequestSchedulesOnValidEnvelope() {
        when(envelopeMock.getCoordinateReferenceSystem()).thenReturn(DefaultGeographicCRS.WGS84);
        when(envelopeMock.isEmpty()).thenReturn(Boolean.FALSE);
        when(envelopeMock.isNull()).thenReturn(Boolean.FALSE);

        renderJob.addRequest(envelopeMock);
        assertTrue("expected scheduled RenderingRequest", scheduleRequested.get()); //$NON-NLS-1$
    }

    @Test
    public void addRequestSchedulesAndUsesImageBoundsIfEnvelopeIsEmpty() {
        when(executorMock.getContext()).thenReturn(renderContextMock);
        when(renderContextMock.getImageBounds())
                .thenReturn(new ReferencedEnvelope(DefaultGeographicCRS.WGS84));

        renderJob.addRequest(null);
        assertTrue("expected scheduled RenderingRequest", scheduleRequested.get()); //$NON-NLS-1$
    }
}
