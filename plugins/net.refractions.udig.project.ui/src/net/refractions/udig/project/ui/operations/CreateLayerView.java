/**
 *
 */
package net.refractions.udig.project.ui.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.interceptor.ShowViewInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.commands.AddLayerCommand;
import net.refractions.udig.ui.operations.IOp;

/**
 * Creates a layer that is a view based on the selection of the currently selected layer.
 *
 * @author Jesse
 */
public class CreateLayerView implements IOp {

    public void op(Display display, Object target, IProgressMonitor monitor)
            throws Exception {
        ILayer layer = (ILayer) target;
        IMap map = layer.getMap();
        Layer view = map.getLayerFactory().createLayer(layer.findGeoResource(FeatureSource.class));
        view.getStyleBlackboard().put(ShowViewInterceptor.KEY, layer.getFilter());
        AddLayerCommand command = new AddLayerCommand(view);
        map.sendCommandASync(command);
    }

}
