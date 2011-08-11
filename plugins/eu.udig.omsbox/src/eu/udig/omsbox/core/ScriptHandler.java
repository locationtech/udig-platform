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
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerFactory;
import net.refractions.udig.project.internal.commands.AddLayerCommand;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.gce.grassraster.JGrassConstants;

import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
import eu.udig.catalog.jgrass.core.JGrassService;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;
import eu.udig.omsbox.OmsBoxPlugin;
import eu.udig.omsbox.utils.OmsBoxConstants;
import eu.udig.omsbox.utils.OmsBoxUtils;
import eu.udig.omsbox.view.widgets.ModuleGui;
import eu.udig.omsbox.view.widgets.ModuleGuiElement;

/**
 * Handler for script generation from gui and script execution.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class ScriptHandler {
    private static final String QUOTE = "'";

    private AtomicInteger counter = new AtomicInteger();

    /**
     * Map of variables bound to their {@link ModuleDescription}.
     */
    private HashMap<ModuleDescription, String> variableNamesMap = new HashMap<ModuleDescription, String>();

    private ModuleDescription mainModuleDescription;

    /**
     * Generates the script from the supplied gui.
     * 
     * @param moduleGui the gui object for which to generate the script.
     * @return the oms3 script.
     * @throws Exception
     */
    public String genereateScript( ModuleGui moduleGui ) throws Exception {
        variableNamesMap.clear();

        mainModuleDescription = moduleGui.getModuleDescription();
        // input
        List<ModuleGuiElement> modulesInputGuiList = moduleGui.getModulesInputGuiList();
        // output
        List<ModuleGuiElement> modulesOutputGuiList = moduleGui.getModulesOuputGuiList();

        StringBuilder sb = new StringBuilder();
        for( ModuleGuiElement mgElem : modulesInputGuiList ) {
            String res = mgElem.validateContent();
            if (res != null) {
                sb.append(res);
            }
        }
        for( ModuleGuiElement mgElem : modulesOutputGuiList ) {
            String res = mgElem.validateContent();
            if (res != null) {
                sb.append(res);
            }
        }

        if (sb.length() > 0) {
            Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
            MessageDialog.openWarning(shell, "WARNING", "The following problems were reported\n" + sb.toString());
            return null;
        }

        String loggerLevelGui = OmsBoxPlugin.getDefault().retrieveSavedLogLevel();
        String loggerLevelOms = OmsBoxConstants.LOGLEVELS_MAP.get(loggerLevelGui);
        StringBuilder scriptSb = new StringBuilder();
        scriptSb.append("def simulation = new oms3.SimBuilder(logging:'" + loggerLevelOms + "').sim(name:");
        scriptSb.append(QUOTE);
        scriptSb.append(mainModuleDescription.getName());
        scriptSb.append(QUOTE);
        scriptSb.append(") {\n\n");
        scriptSb.append("model {\n\n");

        /*
         * first get all the components
         */
        StringBuilder componentsSb = new StringBuilder();
        componentsSb.append("components  {\n");
        componentsSb.append(module2ComponenDescription(mainModuleDescription));
        for( ModuleGuiElement inElement : modulesInputGuiList ) {
            if (!inElement.hasData()) {
                continue;
            }
            FieldData fieldData = inElement.getFieldData();
            if (fieldData.otherModule != null) {
                String componentDescription = module2ComponenDescription(fieldData.otherModule);
                componentsSb.append(componentDescription);
            }
        }
        for( ModuleGuiElement outElement : modulesOutputGuiList ) {
            if (!outElement.hasData()) {
                continue;
            }
            FieldData fieldData = outElement.getFieldData();
            if (fieldData.otherModule != null) {
                String componentDescription = module2ComponenDescription(fieldData.otherModule);
                componentsSb.append(componentDescription);
            }
        }
        componentsSb.append("}\n\n");
        scriptSb.append(componentsSb.toString());

        /*
         * then gather all the input component's fields
         */
        StringBuilder parametersSb = new StringBuilder();
        parametersSb.append("parameter  {\n");
        for( ModuleGuiElement inElement : modulesInputGuiList ) {
            if (!inElement.hasData()) {
                continue;
            }
            FieldData fieldData = inElement.getFieldData();
            field2ParameterDescription(fieldData, mainModuleDescription, parametersSb);
        }
        for( ModuleGuiElement outElement : modulesOutputGuiList ) {
            if (!outElement.hasData()) {
                continue;
            }
            FieldData fieldData = outElement.getFieldData();
            field2ParameterDescription(fieldData, mainModuleDescription, parametersSb);
        }
        parametersSb.append("}\n\n");
        scriptSb.append(parametersSb.toString());

        /*
         * and finally create all the connections
         */
        StringBuilder connectionsSb = new StringBuilder();
        connectionsSb.append("connect  {\n");
        for( ModuleGuiElement inElement : modulesInputGuiList ) {
            if (!inElement.hasData()) {
                continue;
            }
            FieldData fieldData = inElement.getFieldData();
            connectInputModules(mainModuleDescription, fieldData, connectionsSb);
        }
        for( ModuleGuiElement outElement : modulesOutputGuiList ) {
            if (!outElement.hasData()) {
                continue;
            }
            FieldData fieldData = outElement.getFieldData();
            connectOutputModules(mainModuleDescription, fieldData, connectionsSb);
        }
        connectionsSb.append("}\n\n");
        scriptSb.append(connectionsSb.toString());

        scriptSb.append("}\n");
        scriptSb.append("}\n");
        scriptSb.append("result = simulation.run();\n\n");

        dumpSimpleOutputs(scriptSb, mainModuleDescription);

        return scriptSb.toString();
    }

    /**
     * Runs a script.
     * 
     * @param scriptId the unique id that will be used to identify the process of the run script.
     * @param script the script.
     */
    public void runModule( final String scriptId, String script ) {
        JConsoleOutputConsole outputConsole = new JConsoleOutputConsole(scriptId);
        outputConsole.clearConsole();

        PrintStream internalStream = outputConsole.internal;
        // PrintStream outputStream = outputConsole.out;
        PrintStream errorStream = outputConsole.err;
        // open console
        IConsoleManager manager = org.eclipse.ui.console.ConsolePlugin.getDefault().getConsoleManager();
        manager.addConsoles(new IConsole[]{outputConsole});
        manager.showConsoleView(outputConsole);

        try {
            OmsScriptExecutor executor = new OmsScriptExecutor();
            executor.addProcessListener(new IProcessListener(){
                public void onProcessStopped() {
                    OmsBoxPlugin.getDefault().cleanProcess(scriptId);
                    loadOutputMaps();
                }

            });
            String loggerLevelGui = OmsBoxPlugin.getDefault().retrieveSavedLogLevel();
            String ramLevel = String.valueOf(OmsBoxPlugin.getDefault().retrieveSavedHeap());
            Process process = executor.exec(script, internalStream, errorStream, loggerLevelGui, ramLevel);

            OmsBoxPlugin.getDefault().addProcess(process, scriptId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOutputMaps() {
        List<FieldData> outputsList = mainModuleDescription.getOutputsList();
        List<FieldData> InputsList = mainModuleDescription.getInputsList();
        for( FieldData fieldData : outputsList ) {
            if (fieldData.fieldType.equals(GridCoverage2D.class.getCanonicalName())
                    || fieldData.fieldType.equals(SimpleFeatureCollection.class.getCanonicalName())) {
                if (fieldData.otherModule != null) {
                    List<FieldData> inputList2 = fieldData.otherModule.getInputsList();
                    for( FieldData fieldData2 : inputList2 ) {
                        String filePath = fieldData2.fieldValue;
                        File file = new File(filePath);
                        try {
                            if (file.exists())
                                loadFileInMap(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        for( FieldData fieldData : InputsList ) {
            if (fieldData.guiHints == null)
                continue;
            if (fieldData.fieldType.equals(String.class.getCanonicalName())
                    && fieldData.guiHints.contains(OmsBoxConstants.FILEOUT_UI_HINT) && fieldData.fieldValue != null
                    && fieldData.fieldValue.length() > 0) {
                String filePath = fieldData.fieldValue;
                File file = new File(filePath);
                try {
                    if (file.exists())
                        loadFileInMap(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void loadFileInMap( File file ) throws Exception {
        URL fileUrl = file.toURI().toURL();
        CatalogPlugin cp = CatalogPlugin.getDefault();
        IServiceFactory sf = cp.getServiceFactory();
        List<IService> services = sf.createService(fileUrl);
        List<IGeoResource> resources = new ArrayList<IGeoResource>();
        for( IService service : services ) {
            List< ? extends IGeoResource> geoResource = service.resources(new NullProgressMonitor());
            if (geoResource != null) {
                if (service instanceof JGrassService) {
                    for( IGeoResource iGeoResource : geoResource ) {
                        URL identifier = iGeoResource.getIdentifier();
                        File newFile = URLUtils.urlToFile(identifier);

                        if (file.getName().equals(newFile.getName())) {
                            File parentFile = file.getParentFile();
                            File newParentFile = newFile.getParentFile();
                            if (parentFile != null && newParentFile != null
                                    && parentFile.getName().equals(newParentFile.getName())) {
                                parentFile = parentFile.getParentFile();
                                newParentFile = newParentFile.getParentFile();
                                if (parentFile != null && newParentFile != null) {
                                    if (parentFile.getName().equals(newParentFile.getName())) {
                                        resources.add(iGeoResource);
                                    }
                                } else {
                                    resources.add(iGeoResource);
                                }
                            }
                        }
                    }
                } else {
                    for( IGeoResource iGeoResource : geoResource ) {
                        resources.add(iGeoResource);
                    }
                }
            }
        }
        if (resources.size() == 0) {
            return;
        }

        IMap map = ApplicationGIS.getActiveMap();

        LayerFactory layerFactory = map.getLayerFactory();
        for( IGeoResource resource : resources ) {
            if (resource instanceof JGrassMapGeoResource) {
                JGrassMapGeoResource grassMGR = (JGrassMapGeoResource) resource;
                File locationFile = grassMGR.getLocationFile();
                File mapsetFile = grassMGR.getMapsetFile();
                File mapFile = grassMGR.getMapFile();
                IGeoResource addedMapToCatalog = JGrassCatalogUtilities.addMapToCatalog(locationFile.getAbsolutePath(),
                        mapsetFile.getName(), mapFile.getName(), JGrassConstants.GRASSBINARYRASTERMAP);
                int index = map.getMapLayers().size();
                ApplicationGIS.addLayersToMap(map, Arrays.asList(addedMapToCatalog), index);
            } else {
                Layer layer = layerFactory.createLayer(resource);
                AddLayerCommand cmd = new AddLayerCommand(layer);
                map.sendCommandASync(cmd);
            }
        }
    }

    /**
     * Adds to the script a tail part to dump the outputs that are not connected to any module.
     * 
     * <p>These outputs are for example single double values or
     * arrays and matrixes of numbers.
     * 
     * @param scriptSb the script {@link StringBuilder} to which to add the dump commands. 
     * @param mainModuleDescription the main {@link ModuleDescription module}.
     */
    private void dumpSimpleOutputs( StringBuilder scriptSb, ModuleDescription mainModuleDescription ) {
        // make print whatever is simple output
        String mainVarName = variableNamesMap.get(mainModuleDescription);
        List<FieldData> outputsList = mainModuleDescription.getOutputsList();
        for( FieldData fieldData : outputsList ) {
            if (fieldData.isSimpleType()) {
                scriptSb.append("println \"");
                scriptSb.append(fieldData.fieldDescription);
                scriptSb.append(" = \" + result.");
                scriptSb.append(mainVarName);
                scriptSb.append(".");
                scriptSb.append(fieldData.fieldName);
                scriptSb.append("\n");
            }
        }

        // in case make print double[] and double[][] outputs
        scriptSb.append("println \" \"\n\n");
        for( FieldData fieldData : outputsList ) {
            if (fieldData.isSimpleArrayType()) {
                if (fieldData.fieldType.equals(double[][].class.getCanonicalName())
                        || fieldData.fieldType.equals(float[][].class.getCanonicalName())
                        || fieldData.fieldType.equals(int[][].class.getCanonicalName())) {

                    String typeStr = null;
                    if (fieldData.fieldType.equals(double[][].class.getCanonicalName())) {
                        typeStr = "double[][]";
                    } else if (fieldData.fieldType.equals(float[][].class.getCanonicalName())) {
                        typeStr = "float[][]";
                    } else if (fieldData.fieldType.equals(int[][].class.getCanonicalName())) {
                        typeStr = "int[][]";
                    }

                    scriptSb.append("println \"");
                    scriptSb.append(fieldData.fieldDescription);
                    scriptSb.append("\"\n");
                    scriptSb.append("println \"-----------------------------------\"\n");
                    scriptSb.append(typeStr);
                    scriptSb.append(" matrix = result.");
                    scriptSb.append(mainVarName);
                    scriptSb.append(".");
                    scriptSb.append(fieldData.fieldName);
                    scriptSb.append("\n");

                    scriptSb.append("for( int i = 0; i < matrix.length; i++ ) {\n");
                    scriptSb.append("for( int j = 0; j < matrix[0].length; j++ ) {\n");
                    scriptSb.append("print matrix[i][j] + \" \";\n");
                    scriptSb.append("}\n");
                    scriptSb.append("println \" \";\n");
                    scriptSb.append("}\n");
                    scriptSb.append("\n");
                } else if (fieldData.fieldType.equals(double[].class.getCanonicalName())
                        || fieldData.fieldType.equals(float[].class.getCanonicalName())
                        || fieldData.fieldType.equals(int[].class.getCanonicalName())) {

                    String typeStr = null;
                    if (fieldData.fieldType.equals(double[].class.getCanonicalName())) {
                        typeStr = "double[]";
                    } else if (fieldData.fieldType.equals(float[].class.getCanonicalName())) {
                        typeStr = "float[]";
                    } else if (fieldData.fieldType.equals(int[].class.getCanonicalName())) {
                        typeStr = "int[]";
                    }
                    scriptSb.append("println \"");
                    scriptSb.append(fieldData.fieldDescription);
                    scriptSb.append("\"\n");
                    scriptSb.append("println \"-----------------------------------\"\n");
                    scriptSb.append(typeStr);
                    scriptSb.append(" array = result.");
                    scriptSb.append(mainVarName);
                    scriptSb.append(".");
                    scriptSb.append(fieldData.fieldName);
                    scriptSb.append("\n");

                    scriptSb.append("for( int i = 0; i < array.length; i++ ) {\n");
                    scriptSb.append("println array[i] + \" \";\n");
                    scriptSb.append("}\n");
                    scriptSb.append("\n");
                }
                scriptSb.append("println \" \"\n\n");
            }
        }

    }

    /**
     * Converts a module to its oms3 script description as needed in the modules part. 
     * 
     * @param moduleDescription the main {@link ModuleDescription module}. 
     * @return the string describing the module in oms3 syntax. 
     */
    private String module2ComponenDescription( ModuleDescription moduleDescription ) {
        StringBuilder sb = new StringBuilder();
        String varName = moduleDescription.getScriptName() + counter.getAndIncrement();

        variableNamesMap.put(moduleDescription, varName);
        sb.append("\t");
        sb.append(QUOTE);
        sb.append(varName);
        sb.append(QUOTE);
        sb.append("\t\t");
        sb.append(QUOTE);
        sb.append(moduleDescription.getScriptName());
        sb.append(QUOTE);
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Converts module fields to their oms3 script description as needed in the parameters part. 
     * 
     * @param field
     * @param mainModuleDescription
     * @param sb
     */
    private void field2ParameterDescription( FieldData field, ModuleDescription mainModuleDescription, StringBuilder sb ) {
        if (field.otherModule == null && field.fieldValue != null && field.fieldValue.length() > 0) {
            String TMPQUOTE = QUOTE;
            if (OmsBoxUtils.isFieldExceptional(field)) {
                TMPQUOTE = "";
            }
            if (field.guiHints != null && field.guiHints.contains(OmsBoxConstants.MULTILINE_UI_HINT)) {
                TMPQUOTE = "\"\"\"";
            }
            sb.append("\t");
            sb.append(QUOTE);
            sb.append(variableNamesMap.get(mainModuleDescription));
            sb.append(".");
            sb.append(field.fieldName);
            sb.append(QUOTE);
            sb.append("\t\t");
            String fieldValue = field.fieldValue;
            sb.append(TMPQUOTE);
            sb.append(fieldValue);
            sb.append(TMPQUOTE);
            sb.append("\n");
        } else if (field.otherModule != null) {
            ModuleDescription otherModule = field.otherModule;
            List<FieldData> inputsList = otherModule.getInputsList();
            for( FieldData fieldData : inputsList ) {
                if (fieldData.isSimpleType()) {
                    field2ParameterDescription(fieldData, otherModule, sb);
                } else if (OmsBoxUtils.isFieldExceptional(fieldData)) {
                    field2ParameterDescription(fieldData, otherModule, sb);
                }
            }
            List<FieldData> outputList = otherModule.getOutputsList();
            for( FieldData fieldData : outputList ) {
                if (fieldData.isSimpleType())
                    field2ParameterDescription(fieldData, otherModule, sb);
            }
        }
    }

    /**
     * Connects input modules in OMS3 script syntax. 
     * 
     * @param mainModule
     * @param inData
     * @param sb
     */
    private void connectInputModules( ModuleDescription mainModule, FieldData inData, StringBuilder sb ) {
        if (inData.otherModule == null) {
            return;
        }
        ModuleDescription otherModule = inData.otherModule;
        sb.append("\t");
        sb.append(QUOTE);
        sb.append(variableNamesMap.get(otherModule));
        sb.append(".");
        sb.append(inData.otherFieldName);
        sb.append(QUOTE);
        sb.append("\t\t");
        sb.append(QUOTE);
        sb.append(variableNamesMap.get(mainModule));
        sb.append(".");
        sb.append(inData.fieldName);
        sb.append(QUOTE);
        sb.append("\n");
    }

    /**
     * Connects output modules in OMS3 script syntax.
     * 
     * @param mainModule
     * @param outData
     * @param sb
     */
    private void connectOutputModules( ModuleDescription mainModule, FieldData outData, StringBuilder sb ) {
        if (outData.otherModule == null) {
            return;
        }
        ModuleDescription otherModule = outData.otherModule;
        sb.append("\t");
        sb.append(QUOTE);
        sb.append(variableNamesMap.get(mainModule));
        sb.append(".");
        sb.append(outData.fieldName);
        sb.append(QUOTE);
        sb.append("\t\t");
        sb.append(QUOTE);
        sb.append(variableNamesMap.get(otherModule));
        sb.append(".");
        sb.append(outData.otherFieldName);
        sb.append(QUOTE);
        sb.append("\n");
    }
}
