/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.jconsole.actions;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import eu.udig.jconsole.JavaEditor;

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
