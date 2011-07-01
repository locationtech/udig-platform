/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.omsbox.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import oms3.Access;
import oms3.ComponentAccess;
import oms3.annotations.Description;
import oms3.annotations.Label;
import oms3.annotations.Range;
import oms3.annotations.Status;
import oms3.annotations.UI;
import oms3.annotations.Unit;
import oms3.util.Components;
import eu.udig.omsbox.OmsBoxPlugin;
import eu.udig.omsbox.utils.OmsBoxConstants;
import eu.udig.omsbox.utils.OmsBoxUtils;

/**
 * Singleton in which the modules discovery and load/unload occurrs.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class OmsModulesManager {

    private static OmsModulesManager modulesManager;

    private List<String> loadedJarsList = new ArrayList<String>();
    private HashMap<String, List<ModuleDescription>> modulesMap;
    private List<ModuleDescription> gridReaders = new ArrayList<ModuleDescription>();
    private List<ModuleDescription> rasterReaders = new ArrayList<ModuleDescription>();
    private List<ModuleDescription> rasterWriters = new ArrayList<ModuleDescription>();
    private List<ModuleDescription> featureReaders = new ArrayList<ModuleDescription>();
    private List<ModuleDescription> featureWriters = new ArrayList<ModuleDescription>();
    private List<ModuleDescription> hashMapReaders = new ArrayList<ModuleDescription>();
    private List<ModuleDescription> hashMapWriters = new ArrayList<ModuleDescription>();
    private List<ModuleDescription> listReaders = new ArrayList<ModuleDescription>();
    private List<ModuleDescription> listWriters = new ArrayList<ModuleDescription>();

    private URLClassLoader jarClassloader;

    private OmsModulesManager() {
        // add jars from preferences
        String[] retrieveSavedJars = OmsBoxPlugin.getDefault().retrieveSavedJars();
        for( String jar : retrieveSavedJars ) {
            addJar(jar);
        }
    }

    public static OmsModulesManager getInstance() {
        if (modulesManager == null) {
            modulesManager = new OmsModulesManager();
        }
        return modulesManager;
    }

    /**
     * Add a jar to the jars list.
     * 
     * @param newJar the path to the new jar to add.
     */
    public void addJar( String newJar ) {
        if (!loadedJarsList.contains(newJar)) {
            loadedJarsList.add(newJar);
        }
    }

    /**
     * Remove a jar from the jars list.
     * 
     * @param removeJar the jar to remove.
     */
    public void removeJar( String removeJar ) {
        if (loadedJarsList.contains(removeJar)) {
            loadedJarsList.remove(removeJar);
        }
    }

    /**
     * Remove all jars from the cache list.
     */
    public void clearJars() {
        loadedJarsList.clear();
    }

    /**
     * Browses the loaded jars searching for executable modules.
     * 
     * @param rescan whther to scan the jars again. Isn't considered the first time.
     * @return the list of modules that are executable.
     * @throws IOException
     */
    public HashMap<String, List<ModuleDescription>> browseModules( boolean rescan ) {
        try {
            if (modulesMap == null) {
                modulesMap = new HashMap<String, List<ModuleDescription>>();
            } else {
                if (rescan) {
                    modulesMap.clear();
                    gridReaders.clear();
                    rasterReaders.clear();
                    rasterWriters.clear();
                    featureReaders.clear();
                    featureWriters.clear();
                    hashMapReaders.clear();
                    hashMapWriters.clear();
                    listWriters.clear();
                    listReaders.clear();
                } else {
                    if (modulesMap.size() > 0) {
                        return modulesMap;
                    }
                }
            }

            scanForModules();

        } catch (Exception e) {
            e.printStackTrace();
            modulesMap = new HashMap<String, List<ModuleDescription>>();
        }

        Set<Entry<String, List<ModuleDescription>>> entrySet = modulesMap.entrySet();
        for( Entry<String, List<ModuleDescription>> entry : entrySet ) {
            Collections.sort(entry.getValue(), new ModuleDescription.ModuleDescriptionNameComparator());
        }

        return modulesMap;
    }

    public List<ModuleDescription> getFeatureReaders() {
        return cloneList(featureReaders);
    }

    public List<ModuleDescription> getFeatureWriters() {
        return cloneList(featureWriters);
    }

    public List<ModuleDescription> getGridReaders() {
        return cloneList(gridReaders);
    }

    public List<ModuleDescription> getRasterReaders() {
        return cloneList(rasterReaders);
    }

    public List<ModuleDescription> getRasterWriters() {
        return cloneList(rasterWriters);
    }

    public List<ModuleDescription> getHashMapReaders() {
        return cloneList(hashMapReaders);
    }

    public List<ModuleDescription> getHashMapWriters() {
        return cloneList(hashMapWriters);
    }

    public List<ModuleDescription> getListReaders() {
        return cloneList(listReaders);
    }

    public List<ModuleDescription> getListWriters() {
        return cloneList(listWriters);
    }

    public List<ModuleDescription> cloneList( List<ModuleDescription> list ) {
        List<ModuleDescription> copy = new ArrayList<ModuleDescription>();
        for( ModuleDescription moduleDescription : list ) {
            copy.add(moduleDescription.makeCopy());
        }
        return copy;
    }

    private void scanForModules() throws Exception {

        if (loadedJarsList.size() == 0) {
            return;
        }
        List<URL> urlList = new ArrayList<URL>();
        for( int i = 0; i < loadedJarsList.size(); i++ ) {
            String jarPath = loadedJarsList.get(i);
            File jarFile = new File(jarPath);
            if (!jarFile.exists()) {
                continue;
            }
            urlList.add(jarFile.toURI().toURL());
        }

        URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);

        jarClassloader = new URLClassLoader(urls, this.getClass().getClassLoader());

        List<Class< ? >> allComponents = new ArrayList<Class< ? >>();
        try {
            allComponents = Components.getComponentClasses(jarClassloader, urls);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        // clean up html docs in config area, it will be redone
        OmsBoxUtils.cleanModuleDocumentation();

        for( Class< ? > moduleClass : allComponents ) {
            try {
                UI uiHints = moduleClass.getAnnotation(UI.class);
                if (uiHints != null) {
                    String uiHintStr = uiHints.value();
                    if (uiHintStr.contains(OmsBoxConstants.HIDE_UI_HINT)) {
                        continue;
                    }
                }

                Label category = moduleClass.getAnnotation(Label.class);
                String categoryStr = OmsBoxConstants.CATEGORY_OTHERS;
                if (category != null && categoryStr.trim().length() > 1) {
                    categoryStr = category.value();
                }
                Description description = moduleClass.getAnnotation(Description.class);
                String descrStr = null;
                if (description != null) {
                    descrStr = description.value();
                }
                Status status = moduleClass.getAnnotation(Status.class);

                ModuleDescription module = new ModuleDescription(moduleClass, categoryStr, descrStr, status);

                Object newInstance = null;
                try {
                    newInstance = moduleClass.newInstance();

                    // generate the html docs
                    String className = module.getClassName();
                    OmsBoxUtils.generateModuleDocumentation(className);
                } catch (Throwable e) {
                    // ignore module
                    continue;
                }
                ComponentAccess cA = new ComponentAccess(newInstance);

                Collection<Access> inputs = cA.inputs();
                for( Access access : inputs ) {
                    addInput(access, module);
                }

                Collection<Access> outputs = cA.outputs();
                for( Access access : outputs ) {
                    addOutput(access, module);
                }

                if (categoryStr.equals(OmsBoxConstants.GRIDGEOMETRYREADER)) {
                    gridReaders.add(module);
                } else if (categoryStr.equals(OmsBoxConstants.RASTERREADER)) {
                    rasterReaders.add(module);
                } else if (categoryStr.equals(OmsBoxConstants.RASTERWRITER)) {
                    rasterWriters.add(module);
                } else if (categoryStr.equals(OmsBoxConstants.VECTORREADER)) {
                    featureReaders.add(module);
                } else if (categoryStr.equals(OmsBoxConstants.VECTORWRITER)) {
                    featureWriters.add(module);
                } else if (categoryStr.equals(OmsBoxConstants.GENERICREADER)) {
                    // ignore for now
                } else if (categoryStr.equals(OmsBoxConstants.GENERICWRITER)) {
                    // ignore for now
                } else if (categoryStr.equals(OmsBoxConstants.HASHMAP_READER)) {
                    hashMapReaders.add(module);
                } else if (categoryStr.equals(OmsBoxConstants.HASHMAP_WRITER)) {
                    hashMapWriters.add(module);
                } else if (categoryStr.equals(OmsBoxConstants.LIST_READER)) {
                    listReaders.add(module);
                } else if (categoryStr.equals(OmsBoxConstants.LIST_WRITER)) {
                    listWriters.add(module);
                } else {
                    List<ModuleDescription> modulesList4Category = modulesMap.get(categoryStr);
                    if (modulesList4Category == null) {
                        modulesList4Category = new ArrayList<ModuleDescription>();
                        modulesMap.put(categoryStr, modulesList4Category);
                    }
                    modulesList4Category.add(module);
                }

            } catch (NoClassDefFoundError e) {
                if (moduleClass != null)
                    System.out.println("ERROR IN: " + moduleClass.getCanonicalName());
                e.printStackTrace();
            }
        }
    }
    private void addInput( Access access, ModuleDescription module ) throws Exception {
        Field field = access.getField();
        Description descriptionAnn = field.getAnnotation(Description.class);
        String descriptionStr = "No description available";
        if (descriptionAnn != null) {
            descriptionStr = descriptionAnn.value();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(descriptionStr);

        Unit unitAnn = field.getAnnotation(Unit.class);
        if (unitAnn != null) {
            sb.append(" [");
            sb.append(unitAnn.value());
            sb.append("]");
        }
        Range rangeAnn = field.getAnnotation(Range.class);
        if (rangeAnn != null) {
            sb.append(" [");
            sb.append(rangeAnn.min());
            sb.append(" ,");
            sb.append(rangeAnn.max());
            sb.append("]");
        }
        descriptionStr = sb.toString();

        String fieldName = field.getName();
        Class< ? > fieldClass = field.getType();
        Object fieldValue = access.getFieldValue();

        String defaultValue = ""; //$NON-NLS-1$
        if (fieldValue != null) {
            defaultValue = fieldValue.toString();
        }

        UI uiHintAnn = field.getAnnotation(UI.class);
        String uiHint = null;
        if (uiHintAnn != null) {
            uiHint = uiHintAnn.value();
        }

        module.addInput(fieldName, fieldClass.getCanonicalName(), descriptionStr, defaultValue, uiHint);
    }

    private void addOutput( Access access, ModuleDescription module ) throws Exception {
        Field field = access.getField();
        Description descriptionAnn = field.getAnnotation(Description.class);
        String descriptionStr = "No description available";
        if (descriptionAnn != null) {
            descriptionStr = descriptionAnn.value();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(descriptionStr);

        Unit unitAnn = field.getAnnotation(Unit.class);
        if (unitAnn != null) {
            sb.append(" [");
            sb.append(unitAnn.value());
            sb.append("]");
        }
        Range rangeAnn = field.getAnnotation(Range.class);
        if (rangeAnn != null) {
            sb.append(" [");
            sb.append(rangeAnn.min());
            sb.append(" ,");
            sb.append(rangeAnn.max());
            sb.append("]");
        }
        descriptionStr = sb.toString();

        String fieldName = field.getName();
        Class< ? > fieldClass = field.getType();
        Object fieldValue = access.getFieldValue();

        String defaultValue = ""; //$NON-NLS-1$
        if (fieldValue != null) {
            defaultValue = fieldValue.toString();
        }

        UI uiHintAnn = field.getAnnotation(UI.class);
        String uiHint = null;
        if (uiHintAnn != null) {
            uiHint = uiHintAnn.value();
        }

        module.addOutput(fieldName, fieldClass.getCanonicalName(), descriptionStr, defaultValue, uiHint);
    }

    /**
     * Get a class from the loaded modules.
     * 
     * @param className full class name.
     * @return the class for the given name.
     * @throws ClassNotFoundException
     */
    public Class< ? > getModulesClass( String className ) throws ClassNotFoundException {
        Class< ? > moduleClass = Class.forName(className, true, jarClassloader);
        return moduleClass;
    }

    public InputStream getResourceAsStream( String fullName ) throws IOException {
        URL resource = jarClassloader.getResource(fullName);
        InputStream resourceAsStream = resource.openStream();
        // InputStream resourceAsStream = jarClassloader.getResourceAsStream(fullName);
        return resourceAsStream;
    }

}
