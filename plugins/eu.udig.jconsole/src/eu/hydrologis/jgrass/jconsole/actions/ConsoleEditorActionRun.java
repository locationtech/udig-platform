/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
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

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import eu.hydrologis.jgrass.jconsole.JavaEditor;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class ConsoleEditorActionRun extends TextEditorAction {

    public static final String ID = "eu.hydrologis.jgrass.console.editor.consoleruncommand"; //$NON-NLS-1$

    // Attributes
    /** */
    private String m_szMsgFmtText = null;

    // Construction
    public ConsoleEditorActionRun( ResourceBundle bundle, String prefix, ITextEditor editor ) {

        super(bundle, prefix, editor);
        m_szMsgFmtText = getText();

        setId(ID);
    } // ConsoleEditorActionRun
    // Operations

    /*
     * @see org.eclipse.jface.action.Action#run()
     */

    public void run() {

        Display.getDefault().asyncExec(new Runnable(){

            public void run() {

                JavaEditor editor = (JavaEditor) getTextEditor();
                IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
                editor.getOutputConsole().clearConsole();

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

                System.out.println(text);
                // Object o = CLI.createSim(text, false, "OFF");
                // CLI.invoke(o, "run");
            }
        });
    } // run

    /** @see org.eclipse.ui.texteditor.TextEditorAction#update() */

    public void update() {

        ITextEditor editor;
        if (null != m_szMsgFmtText && null != (editor = getTextEditor())) {

            Object[] argv = {editor.getTitle()};
            setText(MessageFormat.format(m_szMsgFmtText, argv));
        }
    } // update
} // ConsoleEditorActionRun
