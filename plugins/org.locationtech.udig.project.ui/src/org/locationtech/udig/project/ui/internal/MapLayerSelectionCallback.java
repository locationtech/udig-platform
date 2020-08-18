package org.locationtech.udig.project.ui.internal;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.locationtech.udig.project.command.UndoRedoCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.commands.selection.SelectLayerCommand;
import org.locationtech.udig.ui.ProgressManager;

/**
 * Runs SelectlayerCommand after selection change on layer
 *
 */
public final class MapLayerSelectionCallback implements LayerSelectionListener.Callback {
    private Map map;

    private Composite composite;

    public MapLayerSelectionCallback(Map map, Composite composite) {
        this.map = map;
        this.composite = composite;
    }

    public void callback(List<Layer> layers) {
        if (map == null || map.getEditManager() == null) {
            return;
        }

        if (composite == null || composite.isDisposed() || !composite.isVisible()) {
            return; // component.isVisible cannot be called on a disposed component
        }

        Layer layer = layers.get(0);

        if (map == layer.getMap() && map.getEditManager().getSelectedLayer() != layer) {
            SelectLayerCommand selectLayerCommand = new SelectLayerCommand(layer);
            selectLayerCommand.setMap(map);
            try {
                selectLayerCommand.run(ProgressManager.instance().get());
            } catch (Exception e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
            map.sendCommandSync(new UndoRedoCommand(selectLayerCommand));
        }
    }
}
