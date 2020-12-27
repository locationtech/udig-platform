package org.locationtech.udig.project.ui.internal;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.ui.tests.support.DisplayRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class MapLayerSelectionCallbackTest {

    @Rule
    public DisplayRule displayRule = new DisplayRule();

    @SuppressWarnings("rawtypes")
    @Mock
    private List listMock;

    @Mock
    private EditManager editManagerMock;

    @Mock
    private Layer layerMock;

    @Mock
    private Map mapMock;

    @Test
    public void doNothingIfMapIsNull() {
        new MapLayerSelectionCallback(null, null).callback(listMock);
    }

    @Test
    public void doNothingIfMapEditManagerIsNull() {
        when(mapMock.getEditManager()).thenReturn(null);
        new MapLayerSelectionCallback(mapMock, null).callback(listMock);
        verify(mapMock, only()).getEditManager();
        verifyNoMoreInteractions(mapMock, editManagerMock, listMock);
    }

    @Test
    public void doNothingIfCompositeIsNull() {
        when(mapMock.getEditManager()).thenReturn(editManagerMock);
        new MapLayerSelectionCallback(mapMock, null).callback(listMock);
        verify(mapMock, only()).getEditManager();
        verifyNoMoreInteractions(mapMock, editManagerMock, listMock);
    }

    @Test
    public void doNothingIfCompositeIsDisposed() {
        final Display display = displayRule.getDisplay();
        final Composite composite = new Composite(new Shell(display), SWT.NONE);
        composite.dispose();
        when(mapMock.getEditManager()).thenReturn(editManagerMock);
        new MapLayerSelectionCallback(mapMock, composite).callback(listMock);

        verify(mapMock, only()).getEditManager();
        verifyNoMoreInteractions(mapMock, editManagerMock, listMock);
    }

    @Test
    public void doNothingIfCompositeIsNotVisible() {
        final Display display = displayRule.getDisplay();
        final Composite composite = new Composite(new Shell(display), SWT.NONE);
        composite.setVisible(false);
        when(mapMock.getEditManager()).thenReturn(editManagerMock);

        new MapLayerSelectionCallback(mapMock, composite).callback(listMock);

        verify(mapMock, only()).getEditManager();
        verifyNoMoreInteractions(editManagerMock, listMock);
    }

    @Test
    public void doNothingIfMapOfLayerDoesNoMatchMap() {
        final Display display = displayRule.getDisplay();
        final Shell shell = new Shell(display);
        shell.setVisible(true);
        final Composite composite = new Composite(shell, SWT.NONE);

        final Map differentMap = mock(Map.class);

        when(mapMock.getEditManager()).thenReturn(editManagerMock);
        when(listMock.get(0)).thenReturn(layerMock);
        when(layerMock.getMap()).thenReturn(differentMap);

        new MapLayerSelectionCallback(mapMock, composite).callback(listMock);

        verify(mapMock, only()).getEditManager();
        verify(layerMock, only()).getMap();
        verify(listMock, only()).get(0);
        verifyNoMoreInteractions(mapMock, editManagerMock, listMock, layerMock, differentMap);
    }

    @Test
    public void doNothingIfLayerOfEditManagerHasLayerSet() {
        final Display display = displayRule.getDisplay();
        final Shell shell = new Shell(display);
        shell.setVisible(true);
        final Composite composite = new Composite(shell, SWT.NONE);

        when(mapMock.getEditManager()).thenReturn(editManagerMock);
        when(listMock.get(0)).thenReturn(layerMock);
        when(editManagerMock.getSelectedLayer()).thenReturn(layerMock);
        when(layerMock.getMap()).thenReturn(mapMock);

        new MapLayerSelectionCallback(mapMock, composite).callback(listMock);

        verify(mapMock, times(2)).getEditManager();
        verify(listMock, only()).get(0);
        verify(editManagerMock, only()).getSelectedLayer();
        verify(layerMock, only()).getMap();
        verifyNoMoreInteractions(mapMock, editManagerMock, listMock, layerMock);
    }

    @Test
    public void runSelectLayerCommand() {
        final Display display = displayRule.getDisplay();
        final Shell shell = new Shell(display);
        shell.setVisible(true);
        final Composite composite = new Composite(shell, SWT.NONE);

        final Layer anotherLayer = mock(Layer.class);

        when(listMock.get(0)).thenReturn(layerMock);
        when(mapMock.getEditManager()).thenReturn(editManagerMock);
        when(mapMock.getEditManagerInternal()).thenReturn(editManagerMock);
        when(editManagerMock.getSelectedLayer()).thenReturn(anotherLayer);
        when(layerMock.getMap()).thenReturn(mapMock);

        new MapLayerSelectionCallback(mapMock, composite).callback(listMock);

        verify(listMock, only()).get(0);
        verify(mapMock, times(2)).getEditManager();
        verify(mapMock, atLeastOnce()).getEditManagerInternal();
        verify(mapMock, atLeastOnce()).sendCommandSync(Mockito.any());
        verify(editManagerMock, atLeastOnce()).setSelectedLayer(layerMock);
        verify(editManagerMock, times(2)).getSelectedLayer();
        verify(layerMock, only()).getMap();

        verifyNoMoreInteractions(mapMock, editManagerMock, listMock, layerMock, anotherLayer);
    }
}
