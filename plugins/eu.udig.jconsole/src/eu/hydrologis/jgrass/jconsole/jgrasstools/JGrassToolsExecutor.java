package eu.hydrologis.jgrass.jconsole.jgrasstools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.hortonmachine.oms.ScriptLauncher;
import org.joda.time.DateTime;

import eu.hydrologis.jgrass.jconsole.JConsolePlugin;

@SuppressWarnings("nls")
public class JGrassToolsExecutor {

    private String classPath;

    private boolean isRunning = false;

    List<IProcessListener> listeners = new ArrayList<IProcessListener>();

    private String javaFile;

    public JGrassToolsExecutor() throws Exception {
        /*
         * get java exec
         */
        // search for the java exec to use
        String[] javaOptions = {"jre/bin/java.exe", "jre/bin/java"};
        URL url = Platform.getInstallLocation().getURL();
        String path = url.getPath();
        File installLocation = new File(path);
        javaFile = null;
        for( String java : javaOptions ) {
            File tmpJavaFile = new File(installLocation, java);
            if (tmpJavaFile.exists()) {
                javaFile = tmpJavaFile.getAbsolutePath();
                break;
            }
        }
        if (javaFile == null) {
            // take the one in the path
            javaFile = "java";
        }

        /*
         * get compiler
         */
        URL compilerUrl = Platform.getBundle(JConsolePlugin.PLUGIN_ID).getResource("compiler");
        String compilerPath = FileLocator.toFileURL(compilerUrl).getPath();
        File compilerFile = new File(compilerPath);

        if (!compilerFile.exists() || !compilerFile.isDirectory()) {
            throw new IllegalArgumentException();
        }

        File toolsFile = new File(compilerFile, "tools.jar");
        if (toolsFile.exists()) {
            classPath = toolsFile.getAbsolutePath();
        } else {
            throw new IllegalArgumentException();
        }

        /*
         * get libraries
         */
        URL libsUrl = Platform.getBundle(JConsolePlugin.PLUGIN_ID).getResource("libs");
        String libsPath = FileLocator.toFileURL(libsUrl).getPath();
        File libsFile = new File(libsPath);

        if (!libsFile.exists() || !libsFile.isDirectory()) {
            throw new IllegalArgumentException();
        }

        File[] files = libsFile.listFiles(new FilenameFilter(){
            public boolean accept( File dir, String name ) {
                return name.endsWith(".jar");
            }
        });

        for( int i = 0; i < files.length; i++ ) {
            classPath = classPath + File.pathSeparator + files[i].getAbsolutePath();
        }
    }

    public Process exec( String script, final PrintStream internalStream, final PrintStream errorStream, String logMode,
            String ramLevel ) throws Exception {
        if (logMode == null)
            logMode = "OFF";
        File scriptFile = File.createTempFile("jgrasstools", "script.jgrass");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(scriptFile));
            bw.write(script);
        } finally {
            bw.close();
        }

        // tmp folder
        String tempdir = System.getProperty("java.io.tmpdir");
        File omsTmp = new File(tempdir + File.separator + "oms");
        if (!omsTmp.exists())
            omsTmp.mkdirs();

        // ram usage
        String ramExpr = "-Xmx" + ramLevel + "m";

        // all the arguments
        String[] args = {javaFile, ramExpr, "-cp", classPath, ScriptLauncher.class.getCanonicalName(),
                scriptFile.getAbsolutePath(), "--mode", logMode, "--work", omsTmp.getAbsolutePath()};

        final Process process = new ProcessBuilder(args).start();
        internalStream.println("Process started: " + new DateTime().toString(JGTConstants.dateTimeFormatterYYYYMMDDHHMMSS));
        internalStream.println("");
        if (!logMode.equals("OFF")) {
            internalStream.println("Command launched: " + Arrays.toString(args));
        }
        internalStream.println("");
        isRunning = true;

        new Thread(){
            public void run() {
                try {
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while( (line = br.readLine()) != null ) {
                        internalStream.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorStream.println(e.getLocalizedMessage());
                } finally {
                    isRunning = false;
                    updateListeners();
                }
                internalStream.println("");
                internalStream.println("");
                internalStream.println("Process finished: "
                        + new DateTime().toString(JGTConstants.dateTimeFormatterYYYYMMDDHHMMSS));
            };
        }.start();

        new Thread(){
            public void run() {
                try {
                    InputStream is = process.getErrorStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while( (line = br.readLine()) != null ) {
                        errorStream.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorStream.println(e.getLocalizedMessage());
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
