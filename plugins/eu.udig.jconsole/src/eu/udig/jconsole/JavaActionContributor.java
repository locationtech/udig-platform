/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package eu.udig.jconsole;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

/**
 * Contributes interesting Java actions to the desktop's Edit menu and the toolbar.
 */
public class JavaActionContributor extends TextEditorActionContributor {

    protected RetargetTextEditorAction fContentAssistProposal;
    protected RetargetTextEditorAction fContentAssistTip;
    // protected TextEditorAction fToggleStartStop;
    // protected TextEditorAction templateAction;

    /**
     * Default constructor.
     */
    public JavaActionContributor() {
        super();
        fContentAssistProposal = new RetargetTextEditorAction(JavaEditorMessages.getResourceBundle(), "ContentAssistProposal."); //$NON-NLS-1$
        fContentAssistProposal.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        fContentAssistTip = new RetargetTextEditorAction(JavaEditorMessages.getResourceBundle(), "ContentAssistTip."); //$NON-NLS-1$
        fContentAssistTip.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION);
        // fToggleStartStop = new StartStopAction();
        // templateAction = new AddCommonImportsAction();
    }

    /*
     * @see IEditorActionBarContributor#init(IActionBars)
     */
    public void init( IActionBars bars ) {
        super.init(bars);

        IMenuManager menuManager = bars.getMenuManager();
        IMenuManager editMenu = menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
        if (editMenu != null) {
            editMenu.removeAll();
            // editMenu.add(new Separator());
            // editMenu.add(fContentAssistProposal);
            // editMenu.add(fContentAssistTip);
        }

        // IToolBarManager toolBarManager = bars.getToolBarManager();
        // if (toolBarManager != null) {
        // toolBarManager.add(new Separator());
        // toolBarManager.add(fToggleStartStop);
        // toolBarManager.add(templateAction);
        // }
    }

    private void doSetActiveEditor( IEditorPart part ) {
        super.setActiveEditor(part);

        ITextEditor editor = null;
        if (part instanceof ITextEditor)
            editor = (ITextEditor) part;

        fContentAssistProposal.setAction(getAction(editor, ITextEditorActionConstants.CONTENT_ASSIST));
        fContentAssistTip.setAction(getAction(editor, ITextEditorActionConstants.CONTENT_ASSIST_CONTEXT_INFORMATION));

        // fToggleStartStop.setEditor(editor);
        // fToggleStartStop.update();
        // templateAction.setEditor(editor);
        // templateAction.update();
    }

    /*
     * @see IEditorActionBarContributor#setActiveEditor(IEditorPart)
     */
    public void setActiveEditor( IEditorPart part ) {
        super.setActiveEditor(part);
        doSetActiveEditor(part);
    }

    /*
     * @see IEditorActionBarContributor#dispose()
     */
    public void dispose() {
        doSetActiveEditor(null);
        super.dispose();
    }
}
