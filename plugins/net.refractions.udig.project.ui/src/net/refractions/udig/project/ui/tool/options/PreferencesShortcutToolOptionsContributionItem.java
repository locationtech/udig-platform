/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.tool.options;

import net.refractions.udig.project.ui.internal.tool.display.ToolProxy;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * Adds a button to the status bar that allows quick access to the active modal tool preference
 * page, if no preference page is defined it will open the parent tools preference page.
 * 
 * @see tool.exsd
 * @author leviputna
 * @since 1.2.0
 */
public class PreferencesShortcutToolOptionsContributionItem extends ContributionItem {
    final IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
            IHandlerService.class);
    final ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(
            ICommandService.class);

    final String defultPreferencePage = "net.refractions.udig.tools.edit.preferences";

    private ToolBar toolBar;
    private ToolItem button;
    private ToolProxy activeTool;
    private Listener listener = new Listener(){
        @Override
        public void handleEvent( Event event ) {
            try {
                if (activeTool == null) {
                    return; // we must be disposed or something
                }
                String page;
                if (activeTool.getPreferencePageId() == null) {
                    page = defultPreferencePage;
                } else {
                    page = activeTool.getPreferencePageId();
                }
                final ParameterizedCommand command = commandService
                        .deserialize("org.eclipse.ui.window.preferences(preferencePageId=" + page
                                + ")");

                handlerService.executeCommand(command, null);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    };

    @Override
    public void fill( Composite parent ) {
        toolBar = new ToolBar(parent, SWT.NONE);
        button = new ToolItem(toolBar, SWT.PUSH);
        button.addListener(SWT.Selection, listener);
        toolBar.pack();
        //new Label(parent, SWT.SEPARATOR);
        refreshButton();
    }
    private void refreshButton() {
        if( activeTool != null && button != null ){
            button.setImage(activeTool.getImage());
            button.setToolTipText(activeTool.getName() + " Preferences");
        }
    }
    /**
     * Update the ItemContribution to reflect the current ModalTool.
     * 
     * @param modalToolProxy
     */
    public void update( ToolProxy modalToolProxy ) {
        // remember the active tool for the preference page lookup
        activeTool = modalToolProxy;
        refreshButton();
    }

    public boolean isDisposed() {
        return toolBar.isDisposed();
    }

    @Override
    public void dispose() {
        super.dispose();

        toolBar.dispose();
        button.dispose();
        activeTool = null;
    }

}
