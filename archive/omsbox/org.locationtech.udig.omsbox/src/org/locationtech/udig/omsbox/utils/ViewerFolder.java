/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.locationtech.udig.omsbox.core.ModuleDescription;

/**
 * A folder for the treeviewer of the modules.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ViewerFolder {
    private final String name;

    private List<ViewerFolder> subFolders = new ArrayList<ViewerFolder>();
    private List<ViewerModule> modules = new ArrayList<ViewerModule>();

    private ViewerFolder parentFolder;

    public ViewerFolder( String name ) {
        this.name = name;
    }

    public void setParentFolder( ViewerFolder parentFolder ) {
        this.parentFolder = parentFolder;
    }

    public ViewerFolder getParentFolder() {
        return parentFolder;
    }

    public String getName() {
        return name;
    }

    public void addSubFolder( ViewerFolder subFolder ) {
        if (!subFolders.contains(subFolder)) {
            subFolder.setParentFolder(this);
            subFolders.add(subFolder);
        }
    }

    public void addModule( ViewerModule module ) {
        if (!modules.contains(module)) {
            module.setParentFolder(this);
            modules.add(module);
        }
    }

    public List<ViewerFolder> getSubFolders() {
        return subFolders;
    }

    public List<ViewerModule> getModules() {
        return modules;
    }

    public static List<ViewerFolder> hashmap2ViewerFolders( HashMap<String, List<ModuleDescription>> availableModules ) {
        List<ViewerFolder> folders = new ArrayList<ViewerFolder>();

        HashMap<String, ViewerFolder> tmpFoldersMap = new HashMap<String, ViewerFolder>();

        Set<Entry<String, List<ModuleDescription>>> entrySet = availableModules.entrySet();
        for( Entry<String, List<ModuleDescription>> entry : entrySet ) {
            String key = entry.getKey();
            List<ModuleDescription> md = entry.getValue();

            String[] keySplit = key.split("/"); //$NON-NLS-1$
            String mainKey = keySplit[0];

            ViewerFolder mainFolder = tmpFoldersMap.get(mainKey);
            if (mainFolder == null) {
                mainFolder = new ViewerFolder(mainKey);
                folders.add(mainFolder);
                tmpFoldersMap.put(mainKey, mainFolder);
            }

            for( int i = 1; i < keySplit.length; i++ ) {
                ViewerFolder tmpFolder = tmpFoldersMap.get(keySplit[i]);
                if (tmpFolder == null) {
                    tmpFolder = new ViewerFolder(keySplit[i]);
                    tmpFoldersMap.put(keySplit[i], tmpFolder);
                }
                mainFolder.addSubFolder(tmpFolder);
                mainFolder = tmpFolder;
            }

            // add the module to the last available
            for( ModuleDescription moduleDescription : md ) {
                mainFolder.addModule(new ViewerModule(moduleDescription));
            }
        }

        return folders;
    }

}
