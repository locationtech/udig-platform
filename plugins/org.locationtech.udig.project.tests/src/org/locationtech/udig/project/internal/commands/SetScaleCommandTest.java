package org.locationtech.udig.project.internal.commands;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;
import org.locationtech.udig.project.command.navigation.SetScaleCommand;
import org.locationtech.udig.project.internal.render.ViewportModel;

public class SetScaleCommandTest {

    private static final double SCALE_TO_SET = 1 / 5000;

    private static final double PREVIOUS_SCALE = 1 / 500;

    @Test
    public void ifModelIsSetSetScaleIsCalled() throws Exception {
        SetScaleCommand setScaleCommand = new SetScaleCommand(SCALE_TO_SET);
        ViewportModel mockedViewportModel = EasyMock.createMock(ViewportModel.class);
        IProgressMonitor monitor = EasyMock.createNiceMock(IProgressMonitor.class);
        EasyMock.expect(mockedViewportModel.getScaleDenominator()).andReturn(PREVIOUS_SCALE).once();
        mockedViewportModel.setScale(SCALE_TO_SET);
        EasyMock.expectLastCall().times(1);

        EasyMock.replay(mockedViewportModel);
        setScaleCommand.setViewportModel(mockedViewportModel);
        setScaleCommand.run(monitor);
        EasyMock.verify(mockedViewportModel);
    }

    @Test
    public void rollbackSetsPreviousScaleFromModel() throws Exception {
        SetScaleCommand setScaleCommand = new SetScaleCommand(SCALE_TO_SET);
        ViewportModel mockedViewportModel = EasyMock.createMock(ViewportModel.class);
        IProgressMonitor monitor = EasyMock.createNiceMock(IProgressMonitor.class);
        // get scale from model on first execution
        EasyMock.expect(mockedViewportModel.getScaleDenominator()).andReturn(PREVIOUS_SCALE).once();
        mockedViewportModel.setScale(SCALE_TO_SET);
        EasyMock.expectLastCall().times(1);
        // on roll-back, reset previous scale denominator
        mockedViewportModel.setScale(PREVIOUS_SCALE);
        EasyMock.expectLastCall().times(1);

        EasyMock.replay(mockedViewportModel);
        setScaleCommand.setViewportModel(mockedViewportModel);
        setScaleCommand.run(monitor);
        setScaleCommand.rollback(monitor);
        EasyMock.verify(mockedViewportModel);
    }

    @Test
    public void commandNameNotNull() throws Exception {
        assertNotNull("Command name expected", new SetScaleCommand(SCALE_TO_SET).getName());
    }
}
