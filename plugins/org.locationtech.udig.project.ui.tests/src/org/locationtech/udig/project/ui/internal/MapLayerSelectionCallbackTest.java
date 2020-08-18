package org.locationtech.udig.project.ui.internal;

import java.util.List;

import org.easymock.EasyMock;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Rule;
import org.junit.Test;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.ui.tests.support.DisplayRule;

public class MapLayerSelectionCallbackTest {

    @Rule
    public DisplayRule displayRule = new DisplayRule();

    @SuppressWarnings("rawtypes")
    private final List listMock = EasyMock.createStrictMock(List.class);

    private final EditManager editManagerMock = EasyMock.createMock(EditManager.class);

    private final Layer layerMock = EasyMock.createNiceMock(Layer.class);

    private final Map mapMock = EasyMock.createNiceMock("mapMock", Map.class);

    @Test
    public void doNothingIfMapIsNull() {
        EasyMock.replay(mapMock, editManagerMock, listMock);
        new MapLayerSelectionCallback(null, null).callback(listMock);
        EasyMock.verify(mapMock, editManagerMock);
    }

    @Test
    public void doNothingIfMapEditManagerIsNull() {
        EasyMock.expect(mapMock.getEditManager()).andReturn(null).once();
        EasyMock.replay(mapMock, editManagerMock, listMock);
        new MapLayerSelectionCallback(mapMock, null).callback(listMock);
        EasyMock.verify(mapMock, editManagerMock);
    }

    @Test
    public void doNothingIfCompositeIsNull() {
        EasyMock.expect(mapMock.getEditManager()).andReturn(editManagerMock).once();
        EasyMock.replay(mapMock, editManagerMock, listMock);
        new MapLayerSelectionCallback(mapMock, null).callback(listMock);
        EasyMock.verify(mapMock, editManagerMock, listMock);
    }

    @Test
    public void doNothingIfCompositeIsDisposed() {
        final Display display = displayRule.getDisplay();
        final Composite composite = new Composite(new Shell(display), SWT.NONE);
        composite.dispose();
        EasyMock.expect(mapMock.getEditManager()).andReturn(editManagerMock).once();
        EasyMock.replay(mapMock, editManagerMock, listMock);
        new MapLayerSelectionCallback(mapMock, composite).callback(listMock);
        EasyMock.verify(mapMock, editManagerMock, listMock);
    }

    @Test
    public void doNothingIfCompositeIsNotVisible() {
        final Display display = displayRule.getDisplay();
        final Composite composite = new Composite(new Shell(display), SWT.NONE);
        composite.setVisible(false);
        EasyMock.expect(mapMock.getEditManager()).andReturn(editManagerMock).once();
        EasyMock.replay(mapMock, editManagerMock, listMock);
        new MapLayerSelectionCallback(mapMock, composite).callback(listMock);
        EasyMock.verify(mapMock, editManagerMock, listMock);
    }

    @Test
    public void doNothingIfMapOfLayerDoesNoMatchMap() {
        final Display display = displayRule.getDisplay();
        final Shell shell = new Shell(display);
        shell.setVisible(true);
        final Composite composite = new Composite(shell, SWT.NONE);

        final Map differentMap = EasyMock.createNiceMock(Map.class);
        EasyMock.expect(mapMock.getEditManager()).andReturn(editManagerMock).once();

        EasyMock.expect(listMock.get(0)).andReturn(layerMock).once();

        EasyMock.expect(layerMock.getMap()).andReturn(differentMap);

        EasyMock.replay(mapMock, editManagerMock, listMock, layerMock, differentMap);
        new MapLayerSelectionCallback(mapMock, composite).callback(listMock);
        EasyMock.verify(mapMock, editManagerMock, listMock, layerMock, differentMap);
    }

    @Test
    public void doNothingIfLayerOfEditManagerHasLayerSet() {
        final Display display = displayRule.getDisplay();
        final Shell shell = new Shell(display);
        shell.setVisible(true);
        final Composite composite = new Composite(shell, SWT.NONE);

        EasyMock.expect(mapMock.getEditManager()).andReturn(editManagerMock).times(2);
        EasyMock.expect(listMock.get(0)).andReturn(layerMock).once();
        EasyMock.expect(editManagerMock.getSelectedLayer()).andReturn(layerMock).once();
        EasyMock.expect(layerMock.getMap()).andReturn(mapMock);

        EasyMock.replay(mapMock, editManagerMock, listMock, layerMock);
        new MapLayerSelectionCallback(mapMock, composite).callback(listMock);
        EasyMock.verify(mapMock, editManagerMock, listMock, layerMock);
    }

    @Test
    public void runSelectLayerCommand() {
        final Display display = displayRule.getDisplay();
        final Shell shell = new Shell(display);
        shell.setVisible(true);
        final Composite composite = new Composite(shell, SWT.NONE);

        final Layer anotherLayer = EasyMock.createNiceMock(Layer.class);

        EasyMock.expect(listMock.get(0)).andReturn(layerMock).once();
        EasyMock.expect(mapMock.getEditManager()).andReturn(editManagerMock).times(2);
        EasyMock.expect(mapMock.getEditManagerInternal()).andReturn(editManagerMock).times(1);
        EasyMock.expect(editManagerMock.getSelectedLayer()).andReturn(anotherLayer).times(2);
        EasyMock.expect(layerMock.getMap()).andReturn(mapMock);

        editManagerMock.setSelectedLayer(layerMock);
        EasyMock.expectLastCall().once();

        EasyMock.replay(mapMock, editManagerMock, listMock, layerMock, anotherLayer);
        new MapLayerSelectionCallback(mapMock, composite).callback(listMock);
        EasyMock.verify(mapMock, editManagerMock, listMock, layerMock, anotherLayer);
    }
}
