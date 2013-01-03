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
package eu.udig.jconsole.actions;

import java.io.File;
import java.io.PrintStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.joda.time.DateTime;

import eu.udig.jconsole.JConsolePlugin;
import eu.udig.jconsole.JavaEditor;
import eu.udig.jconsole.JavaEditorMessages;
import eu.udig.omsbox.OmsBoxPlugin;
import eu.udig.omsbox.core.IProcessListener;
import eu.udig.omsbox.core.JConsoleOutputConsole;
import eu.udig.omsbox.core.OmsScriptExecutor;
import eu.udig.omsbox.utils.OmsBoxConstants;

/**
 * A toolbar action which toggles start and stop of the module.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class StartStopAction extends TextEditorAction implements IProcessListener {

    public static final String START = "icons/start.gif"; //$NON-NLS-1$
    public static final String STOP = "icons/stop.gif"; //$NON-NLS-1$
    private String scriptID;

    /**
     * Constructs and updates the action.
     */
    public StartStopAction() {
        super(JavaEditorMessages.getResourceBundle(), "StartStop.", null); //$NON-NLS-1$
        setButtonOnStart();
        update();
    }

    public void run() {
        JavaEditor editor = (JavaEditor) getTextEditor();

        Process process = editor.getProcess();
        if (process != null) {
            process.destroy();
            process = null;
            editor.setProcess(null);
            // editor.setExecutor(null);
            setButtonOnStart();
        } else {
            IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
            JConsoleOutputConsole outputConsole = editor.getOutputConsole();
            outputConsole.clearConsole();

            String text = null;
            ISelection selection = editor.getSelectionProvider().getSelection();
            if (selection instanceof ITextSelection) {
                ITextSelection textSelection = (ITextSelection) selection;
                if (!textSelection.isEmpty()) {
                    text = textSelection.getText();
                }
            }
            if (text == null || 0 >= text.length()) {
                text = doc.get();
            }

            execute(text);
        }
    }

    private void execute( String text ) {
        String trimmed = text.trim();
        int limit = 15;
        if (trimmed.length() <= limit) {
            limit = trimmed.length() - 1;
        }
        String title = trimmed.substring(0, limit);
        JConsoleOutputConsole outputConsole = new JConsoleOutputConsole("Script: " + title);
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
            Process process = executor.exec(text, internalStream, errorStream, loggerLevelGui, ramLevel);

            scriptID = "geoscript_" + new DateTime().toString(OmsBoxConstants.dateTimeFormatterYYYYMMDDHHMMSS);
            OmsBoxPlugin.getDefault().addProcess(process, scriptID);

            // cleanup when leaving uDig
            // scriptFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        ITextEditor textEditor = getTextEditor();
        if (textEditor instanceof JavaEditor) {
            JavaEditor editor = (JavaEditor) textEditor;

            if (OmsBoxPlugin.getDefault().getRunningProcessesMap().get(scriptID) != null) {
                setButtonOnStop();
            } else {
                setButtonOnStart();
            }
            JConsoleOutputConsole outputConsole = editor.getOutputConsole();
            if (outputConsole != null) {
                IConsoleManager manager = org.eclipse.ui.console.ConsolePlugin.getDefault().getConsoleManager();
                manager.showConsoleView(outputConsole);
            }
        }
    }

    private void setButtonOnStart() {
        ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(JConsolePlugin.PLUGIN_ID, START);
        setImageDescriptor(id);
        setChecked(false);
        setEnabled(true);
    }

    private void setButtonOnStop() {
        ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(JConsolePlugin.PLUGIN_ID, STOP);
        setImageDescriptor(id);
        setChecked(true);
        setEnabled(true);
    }

    @Override
    public void onProcessStopped() {
        setButtonOnStart();
        JavaEditor editor = (JavaEditor) getTextEditor();
        editor.setProcess(null);
        // editor.setExecutor(null);
    }

}
