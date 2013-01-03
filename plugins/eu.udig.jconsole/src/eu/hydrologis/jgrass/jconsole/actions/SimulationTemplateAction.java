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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.TextEditorAction;

import eu.hydrologis.jgrass.jconsole.JConsolePlugin;
import eu.hydrologis.jgrass.jconsole.JavaEditor;
import eu.hydrologis.jgrass.jconsole.JavaEditorMessages;

/**
 * A template insertion action.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class SimulationTemplateAction extends TextEditorAction {

    public static final String TEMPLATE = "icons/template.gif"; //$NON-NLS-1$

    /**
     * Constructs and updates the action.
     */
    public SimulationTemplateAction() {
        super(JavaEditorMessages.getResourceBundle(), "Template.", null); //$NON-NLS-1$

        ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(JConsolePlugin.PLUGIN_ID, TEMPLATE);
        setImageDescriptor(id);
    }

    public void run() {
        JavaEditor editor = (JavaEditor) getTextEditor();

        IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());

        String text = doc.get();

        StringBuilder sb = new StringBuilder(text);
        sb.append("\n");
        sb.append("\n");

        URL templateUrl = Platform.getBundle(JConsolePlugin.PLUGIN_ID).getResource("templates/simtemplate.jgrass");
        String templatePath = null;
        try {
            templatePath = FileLocator.toFileURL(templateUrl).getPath();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(templatePath));
                String line = null;
                while( (line = br.readLine()) != null ) {
                    sb.append(line).append("\n");
                }
            } finally {
                br.close();
            }

            doc.set(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
