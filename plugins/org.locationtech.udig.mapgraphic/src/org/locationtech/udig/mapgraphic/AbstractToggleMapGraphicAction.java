/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.mapgraphic.internal.MapGraphicService;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.commands.AddLayerCommand;
import org.locationtech.udig.project.internal.commands.DeleteLayerCommand;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;
import org.locationtech.udig.ui.ProgressManager;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * This is a helper class for MapGraphics that should be toggled on and off. Some good candidates
 * are: Scalebar and legend.
 * <p>
 * This Example is the adding a Toggle action for LegendGraphic.
 *
 * <p> The following xml snippet must be added to the plugin.xml</p>
 * <p>
 * <pre>
 *       &lt;extension
 *        point="org.eclipse.ui.actionSets"&gt;
 *     &lt;actionSet
 *           id="org.locationtech.udig.project.ui.mapGraphic.action"
 *           label="MapGraphics"
 *           visible="true"&gt;
 *        &lt;action
 *              class="org.locationtech.udig.legend.ui.actions.LegendAction"
 *              id="org.locationtech.udig.project.ui.action.addlegend"
 *              label="Legend"
 *              menubarPath="layer/mapGraphic.ext"
 *              style="push"/&gt;
 *     &lt;/actionSet&gt;
 *  &lt;/extension&gt;
 * </pre>
 * </p>
 * <p>The following class must be referred to by the xml snippet above(the class attribute)</p>
 * <p>
 * <pre>
 * public class LegendAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {
 *
 *   protected Class<LegendGraphic> getMapGraphicClass() {
 *       return LegendGraphic.class;
 *   }
 *
 *   protected String getExtensionID() {
 *       return "legend"; //$NON-NLS-1$
 *   }
 *
 *   public void init( IWorkbenchWindow window ) {
 *   }
 *
 * }
 * </pre>
 * </p>
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractToggleMapGraphicAction extends ActionDelegate
        implements
            IWorkbenchWindowActionDelegate {

    public AbstractToggleMapGraphicAction() {
    }

    @Override
    public void run( IAction action ) {
        Map map = ApplicationGISInternal.getActiveMap();
        if (map == ApplicationGIS.NO_MAP)
            return;
        List<ILayer> layers = map.getMapLayers();
        for( ILayer layer : layers ) {
            if (layer.hasResource(getMapGraphicClass())) {
                removeLegend();
                return;
            }
        }
        // map does not contain legend so add it.
        addLegend();
    }

    private void removeLegend() {
        Map map = ApplicationGISInternal.getActiveMap();
        if (map == ApplicationGIS.NO_MAP)
            return;

        List<ILayer> layers = map.getMapLayers();
        List<ILayer> toRemove = new ArrayList<ILayer>();

        for( ILayer layer : layers ) {
            if (layer.hasResource(getMapGraphicClass())) {
                toRemove.add(layer);
            }
        }

        UndoableComposite composite = new UndoableComposite();
        for( ILayer layer : toRemove ) {
            composite.getCommands().add( new DeleteLayerCommand((Layer)layer) );
        }

        map.sendCommandASync(composite);
    }

    private void addLegend() {
        Map map = ApplicationGISInternal.getActiveMap();
        try {
            IGeoResource legendResource = null;

            URL url = new URL(MapGraphicService.SERVICE_URL, "#" + getExtensionID()); //$NON-NLS-1$
            List<IGeoResource> matches = CatalogPlugin.getDefault().getLocalCatalog().find(IGeoResource.class,
            		url, ProgressManager.instance().get());
            if (!matches.isEmpty())
                legendResource = matches.get(0);

            if (legendResource == null) {
                List<IService> results = CatalogPlugin.getDefault().getServiceFactory()
                        .createService(url);
                for( IGeoResource resource : results.get(0).resources(new NullProgressMonitor()) ) {
                    if (resource.getIdentifier().getRef().equals(url.getRef())) {
                        legendResource = resource;
                        break;
                    }
                }
            }
            if (legendResource == null)
                return;
            Layer layer = map.getLayerFactory().createLayer(legendResource);
            map.sendCommandSync(new AddLayerCommand(layer));
        } catch (MalformedURLException e) {
            MapGraphicPlugin.log("", e); //$NON-NLS-1$
        } catch (IOException e) {
            MapGraphicPlugin.log("", e); //$NON-NLS-1$
        }

    }

    protected abstract Class< ? extends MapGraphic> getMapGraphicClass();

    protected abstract String getExtensionID();

    @Override
    public void init( IWorkbenchWindow window ) {
    }

}
