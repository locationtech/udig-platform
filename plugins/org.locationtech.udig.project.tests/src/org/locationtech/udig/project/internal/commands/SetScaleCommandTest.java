/* uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2021, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.project.internal.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.project.command.navigation.SetScaleCommand;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SetScaleCommandTest {

    private static final double SCALE_TO_SET = 0.5;

    private static final double PREVIOUS_SCALE = 0.3;

    @Mock
    private ViewportModel mockedViewportModel;

    @Mock
    private IProgressMonitor monitor;

    @Captor
    private ArgumentCaptor<Double> scaleDenominatorCaptor;

    @Test
    public void ifModelIsSetSetScaleIsCalled() throws Exception {
        when(mockedViewportModel.getScaleDenominator()).thenReturn(PREVIOUS_SCALE);

        SetScaleCommand setScaleCommand = new SetScaleCommand(SCALE_TO_SET);
        setScaleCommand.setViewportModel(mockedViewportModel);
        setScaleCommand.run(monitor);

        verify(mockedViewportModel).setScale(SCALE_TO_SET);
    }

    @Test
    public void rollbackSetsPreviousScaleFromModel() throws Exception {
        when(mockedViewportModel.getScaleDenominator()).thenReturn(PREVIOUS_SCALE);

        SetScaleCommand setScaleCommand = new SetScaleCommand(SCALE_TO_SET);
        setScaleCommand.setViewportModel(mockedViewportModel);

        setScaleCommand.run(monitor);
        setScaleCommand.rollback(monitor);

        verify(mockedViewportModel, times(2)).setScale(scaleDenominatorCaptor.capture());

        assertEquals(SCALE_TO_SET, scaleDenominatorCaptor.getAllValues().get(0), 0);
        assertEquals(PREVIOUS_SCALE, scaleDenominatorCaptor.getAllValues().get(1), 0);
    }

    @Test
    public void commandNameNotNull() throws Exception {
        assertNotNull("Command name expected", new SetScaleCommand(SCALE_TO_SET).getName());
    }
}
