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
package eu.udig.omsbox.view.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.joda.time.DateTime;

import eu.udig.omsbox.OmsBoxPlugin;
import eu.udig.omsbox.core.IProcessListener;
import eu.udig.omsbox.core.JConsoleOutputConsole;
import eu.udig.omsbox.core.OmsScriptExecutor;
import eu.udig.omsbox.utils.OmsBoxConstants;
import eu.udig.omsbox.view.OmsBoxView;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class OmsScriptExecutionAction implements IViewActionDelegate, IProcessListener {

    private IViewPart view;
    private String scriptID;

    public void init( IViewPart view ) {
        this.view = view;
    }

    public void run( IAction action ) {
        if (view instanceof OmsBoxView) {
            // OmsBoxView omsView = (OmsBoxView) view;

            FileDialog fileDialog = new FileDialog(view.getSite().getShell(), SWT.OPEN);
            fileDialog.setFilterPath(OmsBoxPlugin.getDefault().getLastFolderChosen());
            String path = fileDialog.open();

            if (path == null || path.length() < 1) {
                return;
            }

            OmsBoxPlugin.getDefault().setLastFolderChosen(fileDialog.getFilterPath());

            execute(path);

        }
    }

    private void execute( String path ) {
        JConsoleOutputConsole outputConsole = new JConsoleOutputConsole("Script: " + path);
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
            executor.addProcessListener(this);
            String loggerLevelGui = OmsBoxPlugin.getDefault().retrieveSavedLogLevel();
            String ramLevel = String.valueOf(OmsBoxPlugin.getDefault().retrieveSavedHeap());
            Process process = executor.exec(path, internalStream, errorStream, loggerLevelGui, ramLevel);

            File scriptFile = new File(path);
            scriptID = scriptFile.getName() + " " + new DateTime().toString(OmsBoxConstants.dateTimeFormatterYYYYMMDDHHMMSS);
            OmsBoxPlugin.getDefault().addProcess(process, scriptID);

            // cleanup when leaving uDig
            // scriptFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void selectionChanged( IAction action, ISelection selection ) {
    }

    @Override
    public void onProcessStopped() {
        OmsBoxPlugin.getDefault().cleanProcess(scriptID);
    }

    /**
     * Read text from a file in one line.
     * 
     * @param file the file to read.
     * @return the read string.
     * @throws IOException 
     */
    public static String readFile( File file ) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder(200);
            String line = null;
            while( (line = br.readLine()) != null ) {
                sb.append(line);
                sb.append("\n"); //$NON-NLS-1$
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

}
