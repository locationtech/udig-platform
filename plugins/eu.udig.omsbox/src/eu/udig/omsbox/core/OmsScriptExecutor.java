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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import oms3.CLI;

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

    private static final String[] JAVA_EXES = {"jre/bin/java.exe", "jre/bin/java"};

    private String classPath;

    private boolean isRunning = false;

    List<IProcessListener> listeners = new ArrayList<IProcessListener>();

    private String javaFile;

    public OmsScriptExecutor() throws Exception {
        /*
         * get java exec
         */
        // search for the java exec to use
        // try one. The udig jre, which has all we need
        URL url = Platform.getInstallLocation().getURL();
        String path = url.getPath();
        File installLocation = new File(path);
        javaFile = null;
        for( String java : JAVA_EXES ) {
            File tmpJavaFile = new File(installLocation, java);
            if (tmpJavaFile.exists()) {
                javaFile = tmpJavaFile.getAbsolutePath();
                break;
            }
        }
        // else try the java home
        if (javaFile == null) {
            String jreDirectory = System.getProperty("java.home");
            for( String java : JAVA_EXES ) {
                File tmpJavaFile = new File(jreDirectory, java);
                if (tmpJavaFile.exists()) {
                    javaFile = tmpJavaFile.getAbsolutePath();
                    break;
                }
            }
        }
        // else hope for one in the path
        if (javaFile == null) {
            javaFile = "java";
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
        String[] modulesJars = OmsBoxPlugin.getDefault().retrieveSavedJars();
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
        arguments.add("-r");
        arguments.add(scriptFile.getAbsolutePath());

        String[] args = arguments.toArray(new String[0]);
        // {javaFile, ramExpr, resourcesFlag, "-cp", classPath,
        // CLI.class.getCanonicalName(), "-r",
        // scriptFile.getAbsolutePath()};

        ProcessBuilder processBuilder = new ProcessBuilder(args);
        // work in home
        String homeDir = System.getProperty("java.home");
        processBuilder.directory(new File(homeDir));

        final Process process = processBuilder.start();
        internalStream.println("Process started: " + new DateTime().toString(OmsBoxConstants.dateTimeFormatterYYYYMMDDHHMMSS));
        internalStream.println("");

        // command launched
        if (loggerLevelGui.equals(OmsBoxConstants.LOGLEVEL_GUI_ON)) {
            internalStream.println("------------------------------>8----------------------------");
            internalStream.println("Launching command: ");
            internalStream.println("------------------");
            List<String> command = processBuilder.command();
            int i = 0;
            for( String arg : command ) {
                if (i++ != 0) {
                    if (!arg.startsWith("-")) {
                        internalStream.print("\t\t");
                    } else {
                        internalStream.print("\t");
                    }
                }
                internalStream.print(arg);
                internalStream.print("\n");
            }
            internalStream.println("");
            internalStream.println("(Put it all on a single line to execute it from command line)");
            internalStream.println("------------------------------>8----------------------------");
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
            Map<String, String> environment = processBuilder.environment();
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
