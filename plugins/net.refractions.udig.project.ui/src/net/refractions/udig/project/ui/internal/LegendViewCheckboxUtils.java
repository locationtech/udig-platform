package net.refractions.udig.project.ui.internal;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;

/**
 * Contains utility methods to manage the checkboxes of the LegendView tree viewer.
 * 
 * @author nchan
 * @since 1.2.0
 */
public final class LegendViewCheckboxUtils {

    /**
     * Updates the viewer's checkbox display of the layer parameter with respect to its visibility.
     * Method called from the notifications of checkboxContextListener
     * 
     * @param layer
     */
    public static void updateCheckbox( final Layer layer ) {
        
        PlatformGIS.asyncInDisplayThread(new Runnable(){
            public void run() {
                if (!PlatformUI.getWorkbench().isClosing()) {
                    final CheckboxTreeViewer viewer = (CheckboxTreeViewer) LegendView.getViewer();
                    viewer.setChecked(layer, layer.isVisible());
                }
            }
        }, true);
        
    }

    /**
     * Updates the viewer's checkbox display with respect to the current layer's visibility. Method
     * called from the notifications of checkboxContextListener
     */
    public static void updateCheckboxes() {
        
        PlatformGIS.asyncInDisplayThread(new Runnable(){
            public void run() {

                final LegendView view = LegendView.getViewPart();
                final Map map = view.getCurrentMap();
                final CheckboxTreeViewer viewer = (CheckboxTreeViewer) LegendView.getViewer();
                final List<Layer> layers = map.getLayersInternal();

                if (canUpdateCheckboxes(view, map, layers, viewer)) {

                    final List<Layer> checkedLayers = new ArrayList<Layer>();
                    for( Layer layer : layers ) {
                        if (layer.isVisible()) {
                            checkedLayers.add(layer);
                        }
                    }

                    if (viewer != null) {
                        viewer.setCheckedElements(checkedLayers.toArray());
                        final ILayer selectedLayer = map.getEditManager().getSelectedLayer();
                        if (selectedLayer != null) {
                            viewer.setSelection(new StructuredSelection(selectedLayer), true);
                        }
                    }

                }

            }

        }, true);
        
    }

    /**
     * Checks if the current status of workbench and current map allows checkbox update.
     * 
     * @param layers
     * @return true if checkbox update is possible, otherwise false
     */
    private static boolean canUpdateCheckboxes( LegendView view, Map map, List<Layer> layers,
            CheckboxTreeViewer viewer ) {

        if (PlatformUI.getWorkbench().isClosing()) {
            return false;
        }

        final Map currentMap;
        synchronized (view) {
            currentMap = map;
        }

        if (currentMap == null) {
            return false;
        }

        if (!requiresCheckboxUpdate(layers, viewer)) {
            return false;
        }

        return true;

    }

    /**
     * Checks if the current visibility of the layers are not in sync with the viewer's checkbox
     * display.
     * 
     * @param layers
     * @return true if requires update, otherwise false
     */
    private static boolean requiresCheckboxUpdate( List<Layer> layers, CheckboxTreeViewer viewer ) {

        for( Layer layer : layers ) {
            if (!(layer.isVisible() == viewer.getChecked(layer))) {
                return true;
            }
        }

        return false;

    }

}
