/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.omsbox.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import oms3.CLI;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.joda.time.DateTime;

import eu.udig.omsbox.OmsBoxPlugin;
import eu.udig.omsbox.utils.OmsBoxConstants;

/**
 * Executor of OMS scripts.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class OmsScriptExecutor {

    private String classPath;

    private boolean isRunning = false;

    List<IProcessListener> listeners = new ArrayList<IProcessListener>();

    private String javaFile;

    public OmsScriptExecutor() throws Exception {
        /*
         * get java exec
         */
        javaFile = OmsBoxPlugin.getUdigJava();
        if (!javaFile.equals("java")) {
            javaFile = "\"" + javaFile + "\"";
        }
        /*
         * get libraries
         */
        String classpathJars = OmsBoxPlugin.getDefault().getClasspathJars();
        classPath = classpathJars;
    }

    /**
     * Execute an OMS script.
     * 
     * @param script the script file or the script string.
     * @param internalStream
     * @param errorStream
     * @param loggerLevelGui the log level as presented in the GUI, can be OFF|ON. This is not the OMS logger level, which 
     *                              in stead has to be picked from the {@link OmsBoxConstants#LOGLEVELS_MAP}.
     * @param ramLevel the heap size to use in megabytes.
     * @return the process.
     * @throws Exception
     */
    public Process exec( String script, final PrintStream internalStream, final PrintStream errorStream, String loggerLevelGui,
            String ramLevel ) throws Exception {
        if (loggerLevelGui == null)
            loggerLevelGui = OmsBoxConstants.LOGLEVEL_GUI_OFF;

        File scriptFile = new File(script);
        if (!scriptFile.exists()) {
            // if the file doesn't exist, it is a script, let's put it into a file
            scriptFile = File.createTempFile("omsbox_script_", ".oms");
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(scriptFile));
                bw.write(script);
            } finally {
                bw.close();
            }

        } else {
            // it is a script in a file, read it to log it
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            try {
                br = new BufferedReader(new FileReader(scriptFile));
                String line = null;
                while( (line = br.readLine()) != null ) {
                    sb.append(line).append("\n");
                }
            } finally {
                br.close();
            }
            script = sb.toString();
        }

        // tmp folder
        String tempdir = System.getProperty("java.io.tmpdir");
        File omsTmp = new File(tempdir + File.separator + "oms");
        if (!omsTmp.exists())
            omsTmp.mkdirs();

        List<String> arguments = new ArrayList<String>();
        arguments.add(javaFile);

        // ram usage
        String ramExpr = "-Xmx" + ramLevel + "m";
        arguments.add(ramExpr);

        // modules jars
        List<String> modulesJars = OmsModulesManager.getInstance().getModulesJars();
        StringBuilder sb = new StringBuilder();
        for( String moduleJar : modulesJars ) {
            sb.append(File.pathSeparator).append(moduleJar);
        }
        String modulesJarsString = sb.toString().replaceFirst(File.pathSeparator, "");
        String resourcesFlag = "-Doms.sim.resources=\"" + modulesJarsString + "\"";
        arguments.add(resourcesFlag);

        // grass gisbase
        String grassGisbase = OmsBoxPlugin.getDefault().getGisbasePreference();
        if (grassGisbase != null && grassGisbase.length() > 0) {
            arguments.add("-D" + OmsBoxConstants.GRASS_ENVIRONMENT_GISBASE_KEY + "=" + grassGisbase);
        }
        String grassShell = OmsBoxPlugin.getDefault().getShellPreference();
        if (grassShell != null && grassShell.length() > 0) {
            arguments.add("-D" + OmsBoxConstants.GRASS_ENVIRONMENT_SHELL_KEY + "=" + grassShell);
        }

        // all the arguments
        arguments.add("-cp");
        arguments.add(classPath);
        arguments.add(CLI.class.getCanonicalName());
        arguments.add("-r ");
        arguments.add("\"" + scriptFile.getAbsolutePath() + "\"");

        String homeDir = System.getProperty("java.io.tmpdir");
        File homeFile = new File(homeDir);
        StringBuilder runSb = new StringBuilder();
        for( String arg : arguments ) {
            runSb.append(arg).append(" ");
        }

        String[] args;
        if (Platform.getOS().equals(Platform.OS_WIN32)) {
            File tmpRunFile = new File(homeFile, "udig_spatialtoolbox.bat");
            FileUtils.writeStringToFile(tmpRunFile, runSb.toString());
            args = new String[]{"cmd", "/c", tmpRunFile.getAbsolutePath()};
        } else {
            File tmpRunFile = new File(homeFile, "udig_spatialtoolbox.sh");
            FileUtils.writeStringToFile(tmpRunFile, runSb.toString());
            args = new String[]{"sh", tmpRunFile.getAbsolutePath()};
        }

        // {javaFile, ramExpr, resourcesFlag, "-cp", classPath,
        // CLI.class.getCanonicalName(), "-r",
        // scriptFile.getAbsolutePath()};

        ProcessBuilder processBuilder = new ProcessBuilder(args);
        // work in home
        // processBuilder.directory(homeFile);

        // environment
        Map<String, String> environment = processBuilder.environment();
        // environment.put("CLASSPATH", classPath);

        final Process process = processBuilder.start();
        internalStream.println("Process started: " + new DateTime().toString(OmsBoxConstants.dateTimeFormatterYYYYMMDDHHMMSS));
        internalStream.println("");

        // command launched
        if (loggerLevelGui.equals(OmsBoxConstants.LOGLEVEL_GUI_ON)) {
            internalStream.println("------------------------------>8----------------------------");
            internalStream.println("Launching command: ");
            internalStream.println("------------------");
            List<String> command = processBuilder.command();
            for( String arg : command ) {
                internalStream.print(arg);
                internalStream.print(" ");
            }
            internalStream.println("\n");
            internalStream.println("(you can run the above from command line, customizing the content)");
            internalStream.println("----------------------------------->8---------------------------------");
            internalStream.println("");
            // script run
            internalStream.println("Script run: ");
            internalStream.println("-----------");
            internalStream.println(script);
            internalStream.println("");
            internalStream.println("------------------------------>8----------------------------");
            internalStream.println("");
            // environment used
            internalStream.println("Environment used: ");
            internalStream.println("-----------------");

            Set<Entry<String, String>> entrySet = environment.entrySet();
            for( Entry<String, String> entry : entrySet ) {
                internalStream.print(entry.getKey());
                internalStream.print(" =\t");
                internalStream.println(entry.getValue());
            }
            internalStream.println("------------------------------>8----------------------------");
            internalStream.println("");
        }
        internalStream.println("");
        isRunning = true;

        new Thread(){
            public void run() {
                BufferedReader br = null;
                try {
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    String line;
                    while( (line = br.readLine()) != null ) {
                        internalStream.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorStream.println(e.getLocalizedMessage());
                } finally {
                    if (br != null)
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    isRunning = false;
                    updateListeners();
                }
                internalStream.println("");
                internalStream.println("");
                internalStream.println("Process finished: "
                        + new DateTime().toString(OmsBoxConstants.dateTimeFormatterYYYYMMDDHHMMSS));
            };
        }.start();

        new Thread(){
            public void run() {
                BufferedReader br = null;
                try {
                    InputStream is = process.getErrorStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    String line;
                    while( (line = br.readLine()) != null ) {
                        /*
                         * remove of ugly recurring geotools warnings. Not nice, but 
                         * at least users do not get confused. 
                         */
                        if (ConsoleMessageFilter.doRemove(line)) {
                            continue;
                        }
                        errorStream.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorStream.println(e.getLocalizedMessage());
                } finally {
                    if (br != null)
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            };
        }.start();

        return process;
    }
    public boolean isRunning() {
        return isRunning;
    }

    public void addProcessListener( IProcessListener listener ) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeProcessListener( IProcessListener listener ) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    private void updateListeners() {
        for( IProcessListener listener : listeners ) {
            listener.onProcessStopped();
        }
    }
}
