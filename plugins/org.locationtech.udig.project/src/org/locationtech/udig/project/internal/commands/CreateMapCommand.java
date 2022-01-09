/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerFactory;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.Trace;
import org.locationtech.udig.project.preferences.PreferenceConstants;

public class CreateMapCommand extends AbstractCommand implements UndoableMapCommand {

    /** name of the new map * */
    String name;

    /** resources / layers * */
    List<IGeoResource> resources;

    /** owning project * */
    Project owner;

    /** created map * */
    Map map;

    public CreateMapCommand(String name, List<IGeoResource> resources, IProject owner) {
        this.name = name;
        this.resources = resources;
        this.owner = (Project) owner;
    }

    @Override
    public void run(IProgressMonitor monitor) throws Exception {
        if (owner == null) {
            // default to current project
            owner = ProjectPlugin.getPlugin().getProjectRegistry().getCurrentProject();
        }
        if (name == null) {
            if (!resources.isEmpty()) {
                IGeoResource resource = resources.get(0);
                String title = resource.getTitle();
                if (title == null || title.trim().length() == 0) {
                    IGeoResourceInfo info = resource.getInfo(monitor);
                    if (info != null) {
                        title = info.getTitle();
                    }
                }
                if (title != null && !title.contains("(")) {
                    name = title;
                }
                if (name == null) {
                    name = resource.getID().toBaseFile();
                }
            }

            if (name == null) {
                name = Messages.CreateMapCommand_defaultname;
            }

            int i = 1;
            String newName = name;
            while (nameTaken(newName)) {
                i++;
                newName = name + " " + i; //$NON-NLS-1$
            }
            name = newName;
        }
        // create the map
        map = ProjectFactory.eINSTANCE.createMap(owner, name, new ArrayList());

        IPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
        RGB background = PreferenceConverter.getColor(store, PreferenceConstants.P_BACKGROUND);
        map.getBlackboard().put(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR,
                new Color(background.red, background.green, background.blue));

        LayerFactory layerFactory = map.getLayerFactory();
        List<Layer> toAdd = new ArrayList<>(resources.size());
        for (IGeoResource resource : resources) {
            Layer layer = layerFactory.createLayer(resource);
            toAdd.add(layer);
        }

        map.getLayersInternal().addAll(toAdd);

        trace(toAdd);

    }

    private void trace(List<Layer> toAdd) {
        if (ProjectPlugin.isDebugging(Trace.COMMANDS)) {
            List<String> ids = new ArrayList<>();
            for (Layer layer : toAdd) {
                ids.add(layer.getID().toString());
            }
            ProjectPlugin.trace(getClass(),
                    "Created Map: " + map.getName() + " and added Layers: " + ids, null); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private boolean nameTaken(String newName) {
        for (IProjectElement element : owner.getElements()) {
            if (newName.equals(element.getName()))
                return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return Messages.CreateMapCommand_commandname;
    }

    public IMap getCreatedMap() {
        return map;
    }

    @Override
    public void rollback(IProgressMonitor monitor) throws Exception {
        owner.getElementsInternal().remove(map);
        map.eResource().unload();
    }

}
