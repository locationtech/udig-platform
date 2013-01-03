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
package eu.hydrologis.jgrass.jconsole.actions;

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

import eu.hydrologis.jgrass.jconsole.JConsoleOutputConsole;
import eu.hydrologis.jgrass.jconsole.JConsolePlugin;
import eu.hydrologis.jgrass.jconsole.JavaEditor;
import eu.hydrologis.jgrass.jconsole.JavaEditorMessages;
import eu.hydrologis.jgrass.jconsole.jgrasstools.IProcessListener;
import eu.hydrologis.jgrass.jconsole.jgrasstools.JGrassToolsExecutor;

/**
 * A toolbar action which toggles start and stop of the module.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class StartStopAction extends TextEditorAction implements IProcessListener {

    public static final String START = "icons/start.gif"; //$NON-NLS-1$
    public static final String STOP = "icons/stop.gif"; //$NON-NLS-1$

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
            editor.setExecutor(null);
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

            PrintStream internalStream = outputConsole.internal;
            // PrintStream outputStream = outputConsole.out;
            PrintStream errorStream = outputConsole.err;
            // open console
            IConsoleManager manager = org.eclipse.ui.console.ConsolePlugin.getDefault().getConsoleManager();
            manager.addConsoles(new IConsole[]{outputConsole});
            manager.showConsoleView(outputConsole);

            try {
                JGrassToolsExecutor executor = new JGrassToolsExecutor();
                executor.addProcessListener(this);
                String loggerLevel = JConsolePlugin.getDefault().getLoggerLevel();
                String ramLevel = JConsolePlugin.getDefault().getRam();
                process = executor.exec(text, internalStream, errorStream, loggerLevel, ramLevel);
                editor.setExecutor(executor);
                editor.setProcess(process);
                setButtonOnStop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        ITextEditor textEditor = getTextEditor();
        if (textEditor instanceof JavaEditor) {
            JavaEditor editor = (JavaEditor) textEditor;
            JGrassToolsExecutor executor = editor.getExecutor();
            if (executor != null && executor.isRunning()) {
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
        editor.setExecutor(null);
    }

}
